// src/hooks.server.ts

import { PUBLIC_API_BASE_URL, PUBLIC_GRAPHQL_URL } from '$env/static/public';
import { clearSessionCookies, setSessionCookies } from '$lib/auth/session';
import { SessionCookieNames } from '$lib/constants/session';
import { authService } from '$lib/services/auth.service';
import type { AuthTokenResponse } from '$lib/types/auth.types';
import type { Handle, HandleFetch } from '@sveltejs/kit';

export const handle: Handle = async ({ event, resolve }) => {
	const userCookie = event.cookies.get(SessionCookieNames.USER_INFO);

	if (userCookie) {
		try {
			event.locals.user = JSON.parse(userCookie);
		} catch {
			event.locals.user = null;
		}
	} else {
		event.locals.user = null;
	}

	return resolve(event);
};

export const handleFetch: HandleFetch = async ({ event, request, fetch }) => {
	const isBackendRequest = request.url.startsWith(PUBLIC_API_BASE_URL) || request.url.startsWith(PUBLIC_GRAPHQL_URL);
	if (!isBackendRequest) {
		return fetch(request);
	}

	let accessToken = event.cookies.get(SessionCookieNames.ACCESS_TOKEN);

	if (accessToken) {
		request.headers.set('Authorization', `Bearer ${accessToken}`);
	}
	console.log('Request URL:', request.url);
	console.log('Access Token:', accessToken);

	const clonedForRetry = request.clone();
	let response = await fetch(request);

	const isAuthEndpoint = request.url.includes('/api/auth/');

	let isUnauthorized = response.status === 401 || response.status === 403;

	// Check 2: Lỗi GraphQL (HTTP 200 nhưng body chứa UNAUTHORIZED)
	if (!isUnauthorized && request.url.startsWith(PUBLIC_GRAPHQL_URL)) {
		const clonedResp = response.clone();
		try {
			const data = await clonedResp.json();
			if (data.errors && data.errors.some((err: any) => err.extensions?.classification === 'UNAUTHORIZED')) {
				isUnauthorized = true;
			}
		} catch {
			// Ignore JSON parse error
		}
	}

	if (isUnauthorized && !isAuthEndpoint) {
		const refreshToken = event.cookies.get(SessionCookieNames.REFRESH_TOKEN);
		if (refreshToken) {
			const refreshResponse = await authService.refreshToken(fetch, refreshToken);
			console.log('Refresh response:', refreshResponse);
			if (refreshResponse.ok) {
				const tokens = refreshResponse.data as AuthTokenResponse;
				console.log('Refreshed tokens:', tokens);

				// FIX 1: Bắt buộc truyền event.locals.user để không bị mất cookie USER_INFO
				setSessionCookies(event.cookies, tokens, event.locals.user ?? undefined);

				// FIX 2: Tạo một instance Headers mới và ghi đè Authorization + Cookie TRƯỚC KHI tạo Request mới
				const newHeaders = new Headers(clonedForRetry.headers);
				newHeaders.set('Authorization', `Bearer ${tokens.accessToken}`);

				// Đồng bộ luôn Cookie header trong context Node/Undici fetch
				const updatedCookies = event.cookies
					.getAll()
					.map(({ name, value }) => `${name}=${encodeURIComponent(value)}`) // Thêm encodeURIComponent
					.join('; ');

				newHeaders.set('cookie', updatedCookies);

				const retryRequest = new Request(clonedForRetry.url, {
					method: clonedForRetry.method,
					headers: newHeaders,
					body: clonedForRetry.body,
					// @ts-expect-error - duplex config cho Node stream body
					duplex: clonedForRetry.body ? 'half' : undefined,
					redirect: clonedForRetry.redirect
				});

				response = await fetch(retryRequest);
			} else {
				clearSessionCookies(event.cookies);
			}
		}
	}

	return response;
};

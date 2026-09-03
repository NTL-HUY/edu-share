// src/lib/server/auth/session.ts
import type { Cookies } from '@sveltejs/kit';
import type { AuthTokenResponse } from '$lib/types/auth.types';
import type { UserBaseProjection } from '$lib/types/user';
import { SessionCookieNames } from '$lib/constants/session';
import { dev } from '$app/environment';


const baseCookieOpts = {
  path: '/',
  httpOnly: true,
  secure: !dev,
  sameSite: dev ? ('lax' as const) : ('strict' as const)
};

const calculateMaxAge = (expiresIn: number) => {
  return Math.max(0, Math.floor((expiresIn - Date.now()) / 1000));
};

export function setSessionCookies(cookies: Cookies, tokens: AuthTokenResponse, user?: UserBaseProjection) {
  const { accessToken, refreshToken, accessTokenExpiresIn, refreshTokenExpiresIn } = tokens;

  // accessTokenExpiresIn là epoch timestamp (ms) - thời điểm hết hạn tuyệt đối,
  // không phải duration, nên phải tự tính maxAge = (thời điểm hết hạn - hiện tại)
  const accessTokenMaxAge = calculateMaxAge(accessTokenExpiresIn);
  const refreshTokenMaxAge = calculateMaxAge(refreshTokenExpiresIn);

  cookies.set(SessionCookieNames.ACCESS_TOKEN, accessToken, {
    ...baseCookieOpts,
    maxAge: accessTokenMaxAge
  });

  cookies.set(SessionCookieNames.REFRESH_TOKEN, refreshToken, {
    ...baseCookieOpts,
    maxAge: refreshTokenMaxAge
  });

  if (user) {
    cookies.set(SessionCookieNames.USER_INFO, JSON.stringify(user), {
      ...baseCookieOpts,
      maxAge: refreshTokenMaxAge
    });
  }
}

export function clearSessionCookies(cookies: Cookies) {
  cookies.delete(SessionCookieNames.ACCESS_TOKEN, { path: '/' });
  cookies.delete(SessionCookieNames.REFRESH_TOKEN, { path: '/' });
  cookies.delete(SessionCookieNames.USER_INFO, { path: '/' });
}
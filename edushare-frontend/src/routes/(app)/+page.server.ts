import { createSdk } from '$lib/graphql/client';
import { ClientError } from 'graphql-request';
import type { PageServerLoad } from './$types';
import { SessionCookieNames } from '$lib/constants/session';

export const load: PageServerLoad = async ({ fetch, cookies }) => {
	const sdk = createSdk(fetch);
	const currentUser = cookies.get(SessionCookieNames.USER_INFO);
	console.log("app. current User" ,currentUser);
	try {
		const data = await sdk.GetFeed({ input: { limit: 10 } });
		return { feed: data.getFeed, error: null, currentUser };
	} catch (err) {
		if (err instanceof ClientError) {
			const gqlError = err.response.errors?.[0];
			const code = gqlError?.extensions?.code as string | undefined;

			console.error('GraphQL error:', code, gqlError?.message);
			return { feed: null, error: gqlError?.message ?? 'Có lỗi xảy ra' };
		}

		console.error('Network/unknown error:', err);
		return { feed: null, error: 'Không thể kết nối máy chủ' };
	}
};

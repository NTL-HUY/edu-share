import type { FeedSearchResult } from '$lib/generated/types';
import { createSdk } from '$lib/graphql/client';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async (event) => {
	const q = event.url.searchParams.get('q') || '';
	const type = event.url.searchParams.get('type') || null;
	const categoryId = event.url.searchParams.get('category') || null;
	const level = event.url.searchParams.get('level') || null;
	const sort = event.url.searchParams.get('sort') || 'knowledgeId,desc';
	const page = Number(event.url.searchParams.get('page')) || 0;

	const sdk = createSdk(event.fetch);

	try {
		const data = await sdk.searchFeed({
			input: {
				keyword: q || null,
				type: type as any,
				categoryId,
				level: level as any,
				page,
				size: 10,
				sort
			}
		});

		return {
			feed: data.searchFeed as FeedSearchResult,
			error: null
		};
	} catch (err) {
		console.error('Search feed error:', err);
		// 1. Nếu là lỗi từ GraphQL Server (GraphQLError / ClientError)
		// SDK (như graphql-request hay urql) thường nhét mảng `response.errors` vào `err`
		if (err?.response?.errors && err.response.errors.length > 0) {
			const firstError = err.response.errors[0];
			return {
				feed: null,
				error: {
					message: firstError.message,
					code: firstError.extensions?.code || 'BAD_REQUEST',
				}
			};
		}

		// 2. Lỗi mạng hoặc Server sập hoàn toàn
		return {
			feed: null,
			error: {
				message: 'Không thể kết nối đến máy chủ, vui lòng thử lại sau.',
				code: 'INTERNAL_SERVER_ERROR'
			}
		};
	}
};

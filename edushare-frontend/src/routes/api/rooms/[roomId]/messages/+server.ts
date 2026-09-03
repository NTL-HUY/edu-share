import { createSdk } from '$lib/graphql/client';
import { apiError, apiSuccess } from '$lib/utils/apiResponse';
import { json, type RequestHandler } from '@sveltejs/kit';

export const GET: RequestHandler = async (event) => {
	const roomId = event.params.roomId;
	const beforeId = event.url.searchParams.get('beforeId');
	const limit = Number(event.url.searchParams.get('limit') ?? 20);

	const sdk = createSdk(event.fetch);

	try {
		const data = await sdk.Messages({
			roomId: event.params.roomId ?? "",
			request: { beforeId, limit }
		});
		return apiSuccess(data.messages);
	} catch (err) {
		console.error('load room messages error:', err);
		return apiError('Không thể tải tin nhắn');
	}
};
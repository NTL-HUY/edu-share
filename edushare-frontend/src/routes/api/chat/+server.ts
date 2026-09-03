import { chatService } from '$lib/services/chat.service';
import { apiError, apiSuccess } from '$lib/utils/apiResponse';
import type { RequestHandler } from '@sveltejs/kit';

export const POST: RequestHandler = async ({ request, fetch }) => {
	let body: { query?: string | number };

	try {
		body = await request.json();
	} catch {
		return apiError('Dữ liệu JSON không hợp lệ');
	}

	if (!body || !body.query) {
		return apiError('Thiếu trường query trong dữ liệu gửi lên');
	}

	const result = await chatService.chatbot(fetch, body);

	if (!result.ok) {
		return apiError(
			result.message,
			result.fieldErrors,
			result.status
		);
	}

	return apiSuccess(result.data, 'Thao tác thành công');
};
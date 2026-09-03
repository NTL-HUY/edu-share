import { interactionService } from '$lib/services/interaction.service';
import { json, type RequestHandler } from '@sveltejs/kit';

export const POST: RequestHandler = async ({ params, request, fetch }) => {
	// 1. Lấy URL Params (feedId)
	const feedId = params.id;
	if (!feedId) {
		return json({ message: 'Thiếu ID bài viết' }, { status: 400 });
	}

	// 1. Parse body từ client gửi lên
	let body: { value?: string | number };
	try {
		body = await request.json();
	} catch {
		return json({ message: 'Dữ liệu không hợp lệ' }, { status: 400 });
	}
	const numericValue = Number(body.value);
	let result;
	if (numericValue === 0) {
		result = await interactionService.unvote(fetch, feedId);
	} else if (numericValue === 1 || numericValue === -1) {
		result = await interactionService.vote(fetch, feedId, { value: numericValue });
	} else {
		return json({ message: 'Giá trị vote không hợp lệ (chỉ chấp nhận 1, -1 hoặc 0)' }, { status: 400 });
	}

	// 3. Trả về kết quả cho Client dựa trên ApiResult
	if (!result.ok) {
		return json(
			{
				message: result.message,
				fieldErrors: result.fieldErrors
			},
			{ status: result.status }
		);
	}

	return json({ success: true, message: 'Thao tác thành công' });
};

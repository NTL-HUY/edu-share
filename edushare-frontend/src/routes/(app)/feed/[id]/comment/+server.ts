import { interactionService } from '$lib/services/interaction.service';
import type { CreateCommentRequest } from '$lib/types/interaction';
import { json, type RequestHandler } from '@sveltejs/kit';

export const PUT: RequestHandler = async ({ params, request, fetch }) => {
	// 1. Lấy URL Params (feedId)
	const feedId = params.id;
	if (!feedId) {
		return json({ message: 'Thiếu ID bài viết' }, { status: 400 });
	}

	// 1. Parse body từ client gửi lên
	let body: CreateCommentRequest;
	try {
		body = await request.json();
	} catch {
		return json({ message: 'Dữ liệu không hợp lệ' }, { status: 400 });
	}

   if (!body.content || !body.content.trim()) {
		return json({ message: 'Nội dung bình luận không được để trống' }, { status: 400 });
	}
   
	let result;
	result = await interactionService.createComment(fetch, feedId, body);

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

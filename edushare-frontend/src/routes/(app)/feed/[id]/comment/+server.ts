import { interactionService } from '$lib/services/interaction.service';
import type { CreateCommentRequest } from '$lib/types/interaction';
import { apiError, apiSuccess } from '$lib/utils/apiResponse';
import { type RequestHandler } from '@sveltejs/kit';

export const PUT: RequestHandler = async ({ params, request, fetch }) => {
   const feedId = params.id;
   if (!feedId) {
      return apiError('Thiếu ID bài viết');
   }

   let body: CreateCommentRequest;
   try {
      body = await request.json();
   } catch {
      return apiError('Dữ liệu không hợp lệ');
   }

   if (!body.content || !body.content.trim()) {
      return apiError('Nội dung bình luận không được để trống');
   }

   const result = await interactionService.createComment(fetch, feedId, body);

   if (!result.ok) {
      return apiError(result.message, result.fieldErrors, result.status);
   }

   return apiSuccess(result.data, 'Đã đăng bình luận!');
};



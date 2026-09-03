import { interactionService } from "$lib/services/interaction.service";
import { apiError, apiSuccess } from "$lib/utils/apiResponse";
import type { RequestHandler } from "@sveltejs/kit";

export const DELETE: RequestHandler = async ({ params, fetch }) => {
   const username = params.username;

   if (!username) {
      return apiError('Thiếu thông tin username');
   }

   const result = await interactionService.unfollow(fetch, username);

   if (!result.ok) {
      return apiError(
         result.message || 'Không thể bỏ theo dõi người dùng này',
         result.fieldErrors,
         result.status
      );
   }

   return apiSuccess(null, 'Bỏ theo dõi thành công');
};
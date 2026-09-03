import { interactionService } from "$lib/services/interaction.service";
import { apiError, apiSuccess } from "$lib/utils/apiResponse";
import type { RequestHandler } from "@sveltejs/kit";

export const POST: RequestHandler = async ({ params, fetch }) => {
   const username = params.username;

   if (!username) {
      return apiError('Thiếu thông tin username');
   }

   const result = await interactionService.follow(fetch, username);
   console.log("api/follow", result);
   
   if (!result.ok) {
      return apiError(
         result.message || 'Không thể theo dõi người dùng này',
         result.fieldErrors,
         result.status
      );
   }

   return apiSuccess(null, 'Theo dõi thành công');
};
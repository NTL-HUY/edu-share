import { clearSessionCookies } from '$lib/auth/session';
import { SessionCookieNames } from '$lib/constants/session';
import { authService } from '$lib/services/auth.service';
import { chatService } from '$lib/services/chat.service';
import { apiError, apiSuccess } from '$lib/utils/apiResponse';
import type { RequestHandler } from '@sveltejs/kit';

export const POST: RequestHandler = async ({ fetch, cookies }) => {
	const refreshToken = cookies.get(SessionCookieNames.REFRESH_TOKEN) || '';
   
	if (refreshToken) {
		const result = await authService.logout(fetch, refreshToken);
      console.log(result);
      
		clearSessionCookies(cookies);

		if (!result.ok) {
			return apiError(
				result.message || 'Lỗi khi đăng xuất phía Server',
				result.fieldErrors,
				result.status
			);
		}
	} else {
		clearSessionCookies(cookies);
	}

	return apiSuccess(null, 'Đăng xuất thành công');
};
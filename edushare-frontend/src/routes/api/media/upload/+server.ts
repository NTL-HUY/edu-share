import { mediaService } from '$lib/services/media.service';
import { apiError, apiSuccess } from '$lib/utils/apiResponse';
import type { RequestHandler } from '@sveltejs/kit';

export const POST: RequestHandler = async ({ request, fetch }) => {
	let formData: FormData;

	try {
		formData = await request.formData();
	} catch {
		return apiError('Dữ liệu form không hợp lệ');
	}

	const file = formData.get('file');
	const folder = (formData.get('folder') as 'AVATAR' | 'LESSON_THUMBNAIL' | 'FEED') || 'FEED';

	if (!file || !(file instanceof File)) {
		return apiError('Vui lòng chọn file hợp lệ');
	}

	const result = await mediaService.uploadImage(fetch, file, folder);

	if (!result.ok) {
		console.error('Upload thumbnail lỗi:', result);
		return apiError(result.message, result.fieldErrors, result.status);
	}

	return apiSuccess(result.data, 'Upload ảnh thành công!');
};

import { API_ENDPOINTS } from "$lib/configs/api";
import { apiRequest, apiUploadRequest, type ApiResult } from "./base.service";

export interface MediaUploadResponse {
   url: string; // Hoặc field tương ứng từ Server trả về
}

export const mediaService = {
   uploadImage: (
      fetchFn: typeof fetch,
      file: File,
      folder: 'AVATAR' | 'LESSON_THUMBNAIL' | 'FEED' = 'FEED'
   ): Promise<ApiResult<MediaUploadResponse>> => {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('folder', folder);

      return apiUploadRequest<MediaUploadResponse>(
         fetchFn,
         API_ENDPOINTS.MEDIA.UPLOAD_IMAGE, 
         formData
      );
   }
};
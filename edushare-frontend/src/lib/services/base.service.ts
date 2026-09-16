import { API_ENDPOINTS } from "$lib/configs/api";

export type ApiResult<T> =
  | { ok: true; data: T }
  | { ok: false; status: number; message: string; fieldErrors?: Record<string, string> };

const DEFAULT_MESSAGES: Record<number, string> = {
  401: 'Sai thông tin đăng nhập',
  403: 'Bạn không có quyền thực hiện thao tác này',
  429: 'Bạn đã thử quá nhiều lần, vui lòng đợi vài phút'
};

export async function apiRequest<T>(
  fetchFn: typeof fetch,
  endpoint: string,
  options: { method: string; body?: unknown }
): Promise<ApiResult<T>> {
  let res: Response;
  try {
    res = await fetchFn(endpoint, {
      method: options.method,
      headers: { 'Content-Type': 'application/json' },
      body: options.body ? JSON.stringify(options.body) : undefined
    });
  } catch (error) {
    console.error(`Không kết nối được backend (${endpoint}):`, error);
    return { ok: false, status: 503, message: 'Hệ thống đang bảo trì, vui lòng thử lại sau' };
  }

  if (!res.ok) {
    const errorBody = await res.json().catch(() => null);

    if (res.status >= 500) {
      console.error(`Backend trả lỗi (${endpoint}):`, res.status, errorBody);
    }

    return {
      ok: false,
      status: res.status,
      message: errorBody?.message ?? DEFAULT_MESSAGES[res.status] ?? 'Có lỗi xảy ra, vui lòng thử lại sau',
      fieldErrors: errorBody?.errors ?? errorBody?.fieldErrors
    };
  }

  const data: T = await res.json();
  return { ok: true, data };
}


export async function apiUploadRequest<T>(
   fetchFn: typeof fetch,
   endpoint: string,
   formData: FormData
): Promise<ApiResult<T>> {
   let res: Response;
   try {
      res = await fetchFn(endpoint, {
         method: 'POST',
         body: formData
      });
   } catch (error) {
      console.error(`Không kết nối được backend (${endpoint}):`, error);
      return { ok: false, status: 503, message: 'Hệ thống đang bảo trì, vui lòng thử lại sau' };
   }

   if (!res.ok) {
      const errorBody = await res.json().catch(() => null);
      return {
         ok: false,
         status: res.status,
         message: errorBody?.message ?? DEFAULT_MESSAGES[res.status] ?? 'Có lỗi xảy ra, vui lòng thử lại sau',
         fieldErrors: errorBody?.errors ?? errorBody?.fieldErrors
      };
   }

   const data: T = await res.json();
   return { ok: true, data };
}

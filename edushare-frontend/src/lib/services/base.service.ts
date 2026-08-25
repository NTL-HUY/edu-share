import { API_ENDPOINTS } from "$lib/configs/api";

export type ApiResult<T> =
  | { ok: true; data: T }
  | { ok: false; status: number; message: string; fieldErrors?: Record<string, string> };

// Message mặc định khi server KHÔNG trả message (network lỗi, 500, hoặc body rỗng)
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
// interface GraphQLResponse<T> {
//   data?: T;
//   errors?: { message: string; extensions?: Record<string, unknown> }[];
// }



// export async function graphqlRequest<T, V extends Record<string, unknown> = Record<string, unknown>>(
//   fetchFn: typeof fetch,
//   query: string,
//   variables?: V
// ): Promise<T> {
//   const res = await fetchFn(API_ENDPOINTS.GRAPHQL.ENDPOINT, {
//     method: 'POST',
//     headers: { 'Content-Type': 'application/json' },
//     body: JSON.stringify({ query, variables })
//   });

//   if (!res.ok) {
//     throw new Error(`GraphQL request failed: ${res.status} ${res.statusText}`);
//   }

//   const json: GraphQLResponse<T> = await res.json();

//   if (json.errors?.length) {
//     throw new Error(json.errors.map((e) => e.message).join('; '));
//   }

//   if (!json.data) {
//     throw new Error('GraphQL response missing data');
//   }

//   return json.data;
// }
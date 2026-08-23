import { API_ENDPOINTS } from '$lib/configs/api';
import type { LoginRequest, RegisterRequest } from '$lib/schemas/auth.schema';
import type { AuthTokenResponse } from '$lib/types/auth.types';
import { apiRequest } from './base.service';

export const authService = {
  login: (fetchFn: typeof fetch, payload: LoginRequest) =>
    apiRequest<AuthTokenResponse>(fetchFn, API_ENDPOINTS.AUTH.LOGIN, {
      method: 'POST',
      body: payload
    }),

  register: (fetchFn: typeof fetch, payload: RegisterRequest) =>
    apiRequest<AuthTokenResponse>(fetchFn, API_ENDPOINTS.AUTH.REGISTER, {
      method: 'POST',
      body: payload
    }),

  me: (fetchFn: typeof fetch) => apiRequest(fetchFn, API_ENDPOINTS.USER.ME, { method: 'GET' }),
  refreshToken: (fetchFn: typeof fetch, refreshToken: string) =>
    apiRequest<AuthTokenResponse>(fetchFn, API_ENDPOINTS.AUTH.REFRESH_TOKEN, {
      method: 'POST',
      body: { token: refreshToken }
    }),

   logout: (fetchFn: typeof fetch) => apiRequest(fetchFn, API_ENDPOINTS.AUTH.LOGOUT, { method: 'POST' })
     
};

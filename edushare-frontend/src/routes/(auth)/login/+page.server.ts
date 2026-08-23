// src/routes/login/+page.server.js
import { fail, redirect, type Actions } from '@sveltejs/kit';
import { loginSchema } from '$lib/schemas/auth.schema';
import { authService } from '$lib/services/auth.service';
import { setSessionCookies } from '$lib/auth/session';
import type { UserBaseProjection } from '$lib/types/user';


export const actions : Actions = {
  default: async ({ request, cookies, fetch }) => {
    const formData = Object.fromEntries(await request.formData());

    // === 1. Validate phía client-facing (Zod) trước khi gọi Spring ===
    const parsed = loginSchema.safeParse(formData);
    if (!parsed.success) {
      return fail(400, {
        error: parsed.error.issues[0].message,
        usernameOrEmail: formData.usernameOrEmail
      });
    }

    // === 2. Gọi Spring qua authService ===
    const result = await authService.login(fetch, parsed.data);

    // === 3. Lỗi nghiệp vụ / server — message + fieldErrors lấy thẳng từ Spring ===
    if (!result.ok) {
      return fail(result.status, {
        error: result.message,
        fieldErrors: result.fieldErrors,
        usernameOrEmail: formData.usernameOrEmail
      });
    }

    setSessionCookies(cookies, result.data);

    const res = await authService.me(fetch);
    if (res.ok) {
      setSessionCookies(cookies, result.data, res.data as UserBaseProjection);
    }

    throw redirect(303, '/');
  }
};
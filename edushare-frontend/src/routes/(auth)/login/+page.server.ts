// src/routes/login/+page.server.js
import { fail, redirect, type Actions } from '@sveltejs/kit';
import { loginSchema } from '$lib/schemas/auth.schema';
import { authService } from '$lib/services/auth.service';
import { setSessionCookies } from '$lib/auth/session';
import type { UserBaseProjection } from '$lib/types/user';


export const actions : Actions = {
  default: async ({ request, cookies, fetch }) => {
    const formData = Object.fromEntries(await request.formData());

    const parsed = loginSchema.safeParse(formData);
    if (!parsed.success) {
      return fail(400, {
        error: parsed.error.issues[0].message,
        usernameOrEmail: formData.usernameOrEmail
      });
    }

    const result = await authService.login(fetch, parsed.data);

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
import { fail, redirect, type Actions } from '@sveltejs/kit';
import { registerSchema } from '$lib/schemas/auth.schema';
import { authService } from '$lib/services/auth.service';
import { setSessionCookies } from '$lib/auth/session';
import type { UserBaseProjection } from '$lib/types/user';

export const actions: Actions = {
  default: async ({ request, cookies, fetch }) => {
    const formData = Object.fromEntries(await request.formData());

    const parsed = registerSchema.safeParse(formData);
    if (!parsed.success) {
      return fail(400, {
        error: parsed.error.issues[0].message,
        username: formData.username,
        email: formData.email,
        fullName: formData.fullName
      });
    }

    const result = await authService.register(fetch, parsed.data);

    if (!result.ok) {
      return fail(result.status, {
        error: result.message,
        fieldErrors: result.fieldErrors,
        username: formData.username,
        email: formData.email,
        fullName: formData.fullName
      });
    }

    const loginResult = await authService.login(fetch, {
      usernameOrEmail: parsed.data.username,
      password: parsed.data.password
    });

    if (!loginResult.ok) {
      throw redirect(303, '/login');
    }

    setSessionCookies(cookies, loginResult.data);

    const meResult = await authService.me(fetch);
    if (meResult.ok) {
      setSessionCookies(cookies, loginResult.data, meResult.data as UserBaseProjection);
    }

    throw redirect(303, '/');
  }
};

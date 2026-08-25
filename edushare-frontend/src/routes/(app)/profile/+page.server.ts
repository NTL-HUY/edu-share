// src/routes/(profile-route)/+layout.server.ts
import { PUBLIC_API_BASE_URL } from '$env/static/public';
import { error, fail, type Actions } from '@sveltejs/kit';
import type { PageServerLoad } from '../$types';
import { API_ENDPOINTS } from '$lib/configs/api';

export const load: PageServerLoad = async ({ fetch, locals }) => {
	if (!locals.user) {
		// chưa đăng nhập -> tuỳ bạn redirect hay trả null
		return { profile: null };
	}

	const res = await fetch(API_ENDPOINTS.USER.ME_PROFILE);

	if (!res.ok) {
		throw error(res.status, 'Không tải được profile');
	}

	const profile = await res.json();

	return { profile };
};


export const actions: Actions = {
  updateProfile: async ({ request, fetch }) => {
    const form = await request.formData();

    const payload = {
      fullName: form.get('fullName'),
      studentId: form.get('studentId'),
      cpa: Number(form.get('cpa')),
      bio: form.get('bio'),
      avatarUrl: form.get('avatarUrl'),
      faculty:form.get('faculty'),
      university:form.get('university'),
    };

    const res = await fetch(API_ENDPOINTS.USER.UPDATE_PROFILE, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (!res.ok) {
      return fail(res.status, { message: 'Cập nhật thất bại' });
    }

    const updated = await res.json();
    console.log(updated)
    return { success: true, profile: updated };
  },

  uploadAvatar: async ({ request, fetch }) => {
    const form = await request.formData();

    const res = await fetch(API_ENDPOINTS.MEDIA.UPLOAD_IMAGE, {
      method: 'POST',
      body: form // multipart/form-data được giữ nguyên, browser tự set boundary
    });

    if (!res.ok) {
      return fail(res.status, { message: 'Upload thất bại' });
    }

    const result = await res.json();
    return { success: true, avatarUrl: result.url };
  }
};
import type { LayoutServerLoad } from './$types';

export const load: LayoutServerLoad = async ({ locals }) => {
	console.log('layout.server load, locals.user =', locals.user);
	return {
		user: locals.user
	};
};

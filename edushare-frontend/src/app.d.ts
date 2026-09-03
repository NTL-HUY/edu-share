// See https://svelte.dev/docs/kit/types#app.d.ts

import type { UserBaseProjection } from "$lib/types/user";

// for information about these interfaces
declare global {
	namespace App {
		// interface Error {}
		// interface Locals {}
		// interface PageData {}
		// interface PageState {}
		// interface Platform {}
		interface Locals {
			user: UserBaseProjection | null;
		}
	}
}

export {};

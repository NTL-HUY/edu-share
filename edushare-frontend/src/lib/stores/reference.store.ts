import { writable } from 'svelte/store';
import { getClientSdk } from '$lib/graphql/client';
import type { Category, EnumOption } from '$lib/generated/types';
import { toast } from 'svelte-sonner';

export const lessonLevels = writable<EnumOption[]>([]);
export const categories = writable<Category[]>([]);

export async function loadReferenceData() {
	try {
		const sdk = getClientSdk();
		const response = await sdk.GetReferenceData();

		if (response.lessonLevels) {
			lessonLevels.set(response.lessonLevels);
		}
		if (response.categories) {
			categories.set(response.categories);
		}
	} catch (error) {
		toast.error("Lỗi khi tải Reference Data");
		console.error('Lỗi khi tải Reference Data:', error);
	}
}

<script lang="ts">
	import { X, Camera, Loader2 } from 'lucide-svelte';
	import { enhance } from '$app/forms';
	import { headerState, type UserProfile } from './profile.svelte.ts';
	import { toast } from 'svelte-sonner';
	import { invalidateAll } from '$app/navigation';

	interface Props {
		isOpen: boolean;
		onClose: () => void;
	}

	let { isOpen = $bindable(false), onClose }: Props = $props();

	let formData = $state({ ...headerState.currentUser });
	let isUploading = $state(false);
	let isSubmitting = $state(false);
	let fileInput = $state<HTMLInputElement>();

	$effect(() => {
		if (isOpen) {
			formData = { ...headerState.currentUser };
		}
	});

	async function handleAvatarChange(event: Event) {
		const target = event.target as HTMLInputElement;
		const file = target.files?.[0];
		if (!file) return;

		isUploading = true;

		const uploadData = new FormData();
		uploadData.append('file', file);
		uploadData.append('folder', 'AVATAR'); 

		try {
			const res = await fetch('/api/media/upload', {
				method: 'POST',
				body: uploadData
			});

			const result = await res.json();

			if (res.ok && result.data?.url) {
				formData.avatarUrl = result.data.url;
				toast.success('Tải ảnh đại diện lên thành công!');
			} else {
				toast.error(result.message || 'Upload thất bại');
			}
		} catch (err) {
			console.error(err);
			toast.error('Có lỗi xảy ra khi upload ảnh');
		} finally {
			isUploading = false;
			if (target) target.value = '';
		}
	}
</script>

{#if isOpen}
	<div
		class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 p-4 backdrop-blur-sm"
		role="dialog"
		aria-modal="true">
		<div class="w-full max-w-lg space-y-4 rounded-xl border border-slate-200 bg-white p-6 shadow-xl">
			<div class="flex items-center justify-between border-b border-slate-100 pb-3">
				<h2 class="text-base font-bold text-slate-800">Cập nhật thông tin Sinh viên</h2>
				<button onclick={onClose} class="rounded-lg p-1 text-slate-400 hover:bg-slate-100 hover:text-slate-600">
					<X class="h-4 w-4" />
				</button>
			</div>

			<div class="flex items-center gap-4 rounded-lg border border-slate-100 bg-slate-50 p-3">
				<div class="relative h-16 w-16 flex-shrink-0">
					<img
						src={formData.avatarUrl || 'https://api.dicebear.com/7.x/avataaars/svg?seed=default'}
						alt="Avatar Preview"
						class="h-16 w-16 rounded-full border border-slate-200 bg-white object-cover" />
					{#if isUploading}
						<div class="absolute inset-0 flex items-center justify-center rounded-full bg-slate-900/40 text-white">
							<Loader2 class="h-5 w-5 animate-spin" />
						</div>
					{/if}
				</div>

				<div class="flex-1 space-y-1.5">
					<span class="block font-medium text-slate-700">Ảnh đại diện</span>

					<!-- Input ẩn bắt sự kiện change -->
					<input bind:this={fileInput} type="file" accept="image/*" class="hidden" onchange={handleAvatarChange} />

					<button
						type="button"
						disabled={isUploading}
						onclick={() => fileInput?.click()}
						class="inline-flex items-center gap-1.5 rounded-md border border-slate-300 bg-white px-3 py-1.5 font-medium text-slate-700 hover:bg-slate-50 disabled:opacity-50">
						<Camera class="h-3.5 w-3.5 text-slate-500" />
						{isUploading ? 'Đang tải lên...' : 'Tải ảnh mới'}
					</button>
				</div>
			</div>
			<!-- FORM 2: THÔNG TIN PROFILE -->
			<form
				method="POST"
				action="?/updateProfile"
				class="space-y-4 text-xs"
				use:enhance={() => {
					isSubmitting = true;
					return async ({ result }) => {
						isSubmitting = false;
						if (result.type === 'success' && result.data?.profile) {
							const updatedProfile = result.data.profile as UserProfile;

							headerState.updateUser(updatedProfile);
							formData = { ...headerState.currentUser! };

							await invalidateAll();

							toast.success('Cập nhật thành công');
							onClose();
						} else if (result.type === 'failure') {
							alert(result.data?.message ?? 'Cập nhật thất bại');
						}
					};
				}}>
				<input type="hidden" name="avatarUrl" value={formData.avatarUrl} />

				<div>
					<label for="fullName" class="block font-medium text-slate-700">Họ và tên</label>
					<input
						id="fullName"
						name="fullName"
						type="text"
						bind:value={formData.fullName}
						required
						placeholder="Nhập họ và tên..."
						class="mt-1 w-full rounded-md border border-slate-300 p-2 focus:border-orange-500 focus:outline-none" />
				</div>
				<!-- Trường Đại học & Khoa/Ngành -->
				<div class="grid grid-cols-2 gap-3">
					<div>
						<label for="university" class="block font-medium text-slate-700">Trường Đại học</label>
						<input
							id="university"
							name="university"
							type="text"
							bind:value={formData.university}
							placeholder="Ví dụ: Đại học Bách Khoa"
							class="mt-1 w-full rounded-md border border-slate-300 p-2 focus:border-orange-500 focus:outline-none" />
					</div>
					<div>
						<label for="faculty" class="block font-medium text-slate-700">Khoa / Ngành học</label>
						<input
							id="faculty"
							name="faculty"
							type="text"
							bind:value={formData.faculty}
							placeholder="Ví dụ: Công nghệ thông tin"
							class="mt-1 w-full rounded-md border border-slate-300 p-2 focus:border-orange-500 focus:outline-none" />
					</div>
				</div>
				<div class="grid grid-cols-2 gap-3">
					<div>
						<label for="studentId" class="block font-medium text-slate-700">Mã số sinh viên (MSSV)</label>
						<input
							id="studentId"
							name="studentId"
							type="text"
							bind:value={formData.studentId}
							class="mt-1 w-full rounded-md border border-slate-300 p-2 focus:border-orange-500 focus:outline-none" />
					</div>
					<div>
						<label for="cpa" class="block font-medium text-slate-700">CPA</label>
						<input
							id="cpa"
							name="cpa"
							type="number"
							step="0.01"
							bind:value={formData.cpa}
							class="mt-1 w-full rounded-md border border-slate-300 p-2 focus:border-orange-500 focus:outline-none" />
					</div>
				</div>

				<div>
					<label for="bio" class="block font-medium text-slate-700">Bio / Giới thiệu</label>
					<textarea
						id="bio"
						name="bio"
						rows="3"
						bind:value={formData.bio}
						class="mt-1 w-full rounded-md border border-slate-300 p-2 focus:border-orange-500 focus:outline-none">
					</textarea>
				</div>

				<div class="flex justify-end gap-2 pt-2">
					<button
						type="button"
						onclick={onClose}
						class="rounded-md border border-slate-300 px-4 py-2 font-medium text-slate-600 hover:bg-slate-50">
						Hủy
					</button>
					<button
						type="submit"
						disabled={isUploading || isSubmitting}
						class="rounded-md bg-orange-600 px-4 py-2 font-semibold text-white hover:bg-orange-700 disabled:opacity-50">
						{isSubmitting ? 'Đang lưu...' : 'Lưu thay đổi'}
					</button>
				</div>
			</form>
		</div>
	</div>
{/if}

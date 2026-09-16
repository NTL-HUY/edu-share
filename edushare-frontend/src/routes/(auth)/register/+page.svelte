<script lang="ts">
	import { enhance } from '$app/forms';
	import { GraduationCap } from 'lucide-svelte';
	import type { ActionData } from './$types';

	let { form }: { form: ActionData } = $props();
	let submitting = $state(false);
</script>

<div class="flex min-h-screen flex-col items-center justify-center bg-[#f1f2f3] px-4 font-sans text-[#0c0d0e]">
	<a href="/" class="mb-6 flex items-center gap-2 transition-opacity hover:opacity-90">
		<GraduationCap class="h-8 w-8 text-sky-600" />
		<span class="text-2xl tracking-tight text-gray-900">
			Edu
			<strong class="font-bold text-sky-600">Share</strong>
		</span>
	</a>

	<div class="w-full max-w-[316px] rounded-lg border border-[#e3e6e8] bg-white p-6 shadow-md">
		<form
			method="POST"
			class="flex flex-col gap-4"
			use:enhance={() => {
				submitting = true;
				return async ({ update }) => {
					await update();
					submitting = false;
				};
			}}>
			{#if form?.error}
				<div class="rounded border border-red-200 bg-red-50 p-2.5 text-xs text-red-600">
					{form.error}
				</div>
			{/if}

			<div class="flex flex-col gap-1">
				<label for="fullName" class="text-[13px] font-semibold text-[#0c0d0e]">Họ và tên</label>
				<input
					type="text"
					id="fullName"
					name="fullName"
					value={form?.fullName ?? ''}
					required
					class="w-full rounded-md border border-[#babfc4] px-2.5 py-1.5 text-sm transition outline-none focus:border-[#0a84ff] focus:ring-4 focus:ring-[#0a84ff]/15" />
			</div>

			<div class="flex flex-col gap-1">
				<label for="username" class="text-[13px] font-semibold text-[#0c0d0e]">Tên tài khoản</label>
				<input
					type="text"
					id="username"
					name="username"
					value={form?.username ?? ''}
					required
					class="w-full rounded-md border border-[#babfc4] px-2.5 py-1.5 text-sm transition outline-none focus:border-[#0a84ff] focus:ring-4 focus:ring-[#0a84ff]/15" />
				{#if form?.fieldErrors?.username}
					<span class="text-xs text-red-600">{form.fieldErrors.username}</span>
				{/if}
			</div>

			<div class="flex flex-col gap-1">
				<label for="email" class="text-[13px] font-semibold text-[#0c0d0e]">Email</label>
				<input
					type="email"
					id="email"
					name="email"
					value={form?.email ?? ''}
					required
					class="w-full rounded-md border border-[#babfc4] px-2.5 py-1.5 text-sm transition outline-none focus:border-[#0a84ff] focus:ring-4 focus:ring-[#0a84ff]/15" />
				{#if form?.fieldErrors?.email}
					<span class="text-xs text-red-600">{form.fieldErrors.email}</span>
				{/if}
			</div>

			<div class="flex flex-col gap-1">
				<label for="password" class="text-[13px] font-semibold text-[#0c0d0e]">Mật khẩu</label>
				<input
					type="password"
					id="password"
					name="password"
					required
					class="w-full rounded-md border border-[#babfc4] px-2.5 py-1.5 text-sm transition outline-none focus:border-[#0a84ff] focus:ring-4 focus:ring-[#0a84ff]/15" />
				{#if form?.fieldErrors?.password}
					<span class="text-xs text-red-600">{form.fieldErrors.password}</span>
				{/if}
			</div>

			<button
				type="submit"
				disabled={submitting}
				class="mt-1 w-full rounded-md bg-[#0a95ff] py-2 text-xs font-semibold text-white shadow-inner transition hover:bg-[#0074cc] active:bg-[#0063bf] disabled:opacity-50">
				{submitting ? 'Đang đăng ký...' : 'Đăng ký'}
			</button>
		</form>
	</div>

	<div class="mt-8 text-xs text-[#232629]">
		Đã có tài khoản?
		<a href="/login" class="text-[#0074cc] hover:text-[#0a84ff]">Đăng nhập</a>
	</div>
</div>

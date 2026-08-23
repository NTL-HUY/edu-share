<script lang="ts">
	import { enhance } from '$app/forms';
	import type { ActionData } from './$types';

	let { form }: { form: ActionData } = $props();
	let submitting = $state(false);
</script>

<div class="flex min-h-screen flex-col items-center justify-center bg-[#f1f2f3] px-4 font-sans text-[#0c0d0e]">
	<div class="mb-6 flex items-center gap-1.5 text-2xl font-bold tracking-tight">
		<svg class="h-8 w-8 text-[#f48225]" viewBox="0 0 32 32" fill="currentColor">
			<path d="M26 24H6v-8H3v11h23v-11h-3z" />
			<path d="M9 20h11v-3H9v3zm1-7.5l10.5 2.2.6-2.9-10.5-2.2-.6 2.9zm3-7.1l9.3 6 1.6-2.5-9.3-6-1.6 2.5zm6-5.8l6.7 8.8 2.4-1.8-6.7-8.8-2.4 1.8zM8 24h13v-3H8v3z" />
		</svg>
		<span class="text-2xl font-semibold tracking-tight">Stack <span class="font-bold">Overflow</span></span>
	</div>

	<!-- Form Card -->
	<div class="w-full max-w-[316px] rounded-lg bg-white p-6 shadow-md border border-[#e3e6e8]">
		<form
			method="POST"
			class="flex flex-col gap-4"
			use:enhance={() => {
				submitting = true;
				return async ({ update }) => {
					await update();
					submitting = false;
				};
			}}
		>
			{#if form?.error}
				<div class="rounded border border-red-200 bg-red-50 p-2.5 text-xs text-red-600">
					{form.error}
				</div>
			{/if}

			<!-- Field: Email / Username -->
			<div class="flex flex-col gap-1">
				<label for="usernameOrEmail" class="text-[13px] font-semibold text-[#0c0d0e]">
					Email
				</label>
				<input
					type="text"
					id="usernameOrEmail"
					name="usernameOrEmail"
					value={form?.usernameOrEmail ?? ''}
					required
					class="w-full rounded-md border border-[#babfc4] px-2.5 py-1.5 text-sm outline-none transition focus:border-[#0a84ff] focus:ring-4 focus:ring-[#0a84ff]/15"
				/>
				{#if form?.fieldErrors?.usernameOrEmail}
					<span class="text-xs text-red-600">{form.fieldErrors.usernameOrEmail}</span>
				{/if}
			</div>

			<!-- Field: Password -->
			<div class="flex flex-col gap-1">
				<div class="flex items-center justify-between">
					<label for="password" class="text-[13px] font-semibold text-[#0c0d0e]">
						Password
					</label>
					<a href="/forgot-password" class="text-xs text-[#0074cc] hover:text-[#0a84ff]">
						Forgot password?
					</a>
				</div>
				<input
					type="password"
					id="password"
					name="password"
					required
					class="w-full rounded-md border border-[#babfc4] px-2.5 py-1.5 text-sm outline-none transition focus:border-[#0a84ff] focus:ring-4 focus:ring-[#0a84ff]/15"
				/>
				{#if form?.fieldErrors?.password}
					<span class="text-xs text-red-600">{form.fieldErrors.password}</span>
				{/if}
			</div>

			<!-- Submit Button -->
			<button
				type="submit"
				disabled={submitting}
				class="mt-1 w-full rounded-md bg-[#0a95ff] py-2 text-xs font-semibold text-white shadow-inner transition hover:bg-[#0074cc] active:bg-[#0063bf] disabled:opacity-50"
			>
				{submitting ? 'Log in...' : 'Log in'}
			</button>
		</form>
	</div>

	<!-- Footer Link -->
	<div class="mt-8 text-xs text-[#232629]">
		Don’t have an account?
		<a href="/register" class="text-[#0074cc] hover:text-[#0a84ff]">Sign up</a>
	</div>
</div>
<script lang="ts">
	import BannerHero from '$lib/components/community/BannerHero.svelte';
	import type { ChatRoom } from '$lib/generated/types';
	import { getClientSdk } from '$lib/graphql/client';
	import { ClientError } from 'graphql-request';
	import { Users, Loader2 } from 'lucide-svelte';
	import { onMount } from 'svelte';



	let rooms = $state<ChatRoom[]>([]);
	let isLoading = $state(true);
	let errorMessage = $state<string | null>(null);

	async function loadRooms() {
		isLoading = true;
		errorMessage = null;
		const sdk = getClientSdk();

		try {
			const res = await sdk.Rooms();
			rooms = res.rooms || [];
		} catch (error) {
			console.error('Lỗi load phòng:', error);
			if (error instanceof ClientError) {
				errorMessage = error.response?.errors?.[0]?.message || 'Không thể tải danh sách phòng';
			} else {
				errorMessage = 'Đã có lỗi xảy ra';
			}
		} finally {
			isLoading = false;
		}
	}

	onMount(() => {
		loadRooms();
	});
</script>

<div class="min-h-screen text-xs text-slate-800">
	<div class="mx-auto space-y-8">
		<!-- Banner Hero -->
		<BannerHero />

		<!-- Section chọn phòng -->
		<section class="space-y-4">
			<div>
				<h2 class="text-xl font-bold tracking-tight text-slate-900">Chọn phòng để bắt đầu</h2>
				<p class="mt-1 text-xs text-slate-500">
					Mỗi phòng có một mục tiêu rõ ràng. Hãy chọn phòng phù hợp với điều bạn cần.
				</p>
			</div>

			{#if isLoading}
				<div class="flex items-center justify-center p-8 text-slate-500">
					<Loader2 class="mr-2 h-4 w-4 animate-spin" />
					<span>Đang tải...</span>
				</div>

			{:else if errorMessage}
				<div class="rounded-lg border border-red-200 bg-red-50 p-4 text-red-600">
					<span>{errorMessage}</span>
					<button onclick={loadRooms} class="ml-2 font-semibold underline">Thử lại</button>
				</div>

			{:else if rooms.length === 0}
				<div class="p-4 text-slate-500">Chưa có phòng chat nào.</div>

			{:else}
				<div class="grid grid-cols-1 gap-4 md:grid-cols-2">
					{#each rooms as room (room.id)}
						<a
							href={`/community/room/${room.id}`}
							class="flex flex-col justify-between rounded-xl border border-slate-200 bg-white p-5 hover:border-slate-400 hover:shadow-sm"
						>
							<div class="space-y-2">
								<h3 class="text-base font-bold text-slate-900">
									{room.name}
								</h3>
								<p class="text-xs text-slate-600">
									{room.description}
								</p>
							</div>

							<div class="mt-4 flex items-center justify-between border-t border-slate-100 pt-3 text-xs text-slate-500">
								{#if room.unreadCount > 0}
									<span class="font-semibold text-rose-600">
										{room.unreadCount} tin mới
									</span>
								{/if}
							</div>
						</a>
					{/each}
				</div>
			{/if}
		</section>
	</div>
</div>
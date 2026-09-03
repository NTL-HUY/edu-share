<script lang="ts">
	import '$lib/styles/filter-bar.css';
	import '$lib/styles/top-bar-header.css';
	import '$lib/styles/filter-toolbar.css';
	import '$lib/styles/base-feed.css';
	import type { PageData } from './$types';
	import FeedItem from '$lib/components/feed/FeedItem.svelte';
	import type { UserBaseProjection } from '$lib/types/user';

	let { data, user  }: { data: PageData, user: UserBaseProjection } = $props();

	let items = $state(data.feed?.items ?? []);
	let cursor = $state(data.feed?.nextCursor ?? null);
	let hasMore = $state(data.feed?.hasMore ?? false);
	let loading = $state(false);
	let currentUser = $derived(data.currentUser);
	let loadMore = async () => {
		if (!hasMore || loading) return;
		loading = true;
		try {
			const response = await fetch(`/?cursor=${cursor}&limit=2`);
			const result = await response.json();

			if (result?.items) {
				items = [...items, ...result.items];
				cursor = result.nextCursor;
				hasMore = result.hasMore;
			}
		} catch (error) {
			console.error('Error loading more feed items:', error);
		} finally {
			loading = false;
		}
	};

	let divEndFeed: HTMLDivElement | null = null;

	$effect(() => {
		if (!divEndFeed) return;

		const observer = new IntersectionObserver(
			(entries) => {
				if (entries[0].isIntersecting) {
					loadMore();
				}
			},
			{ rootMargin: '200px' }
		);
		observer.observe(divEndFeed);
		return () => observer.disconnect();
	});
</script>

<div class="mx-auto max-w-5xl text-slate-800">
	<!-- Controls  Filter Bar -->
	<div class="filter-bar">
		<div>
			<h1 class="page-header-title">Bảng tin cá nhân</h1>
			<p class="page-header-desc">Cập nhật mới nhất từ nền tảng và người dùng bạn quan tâm</p>
		</div>

		<!-- create question -->
		{#if user}
			<a
				href="/feed/create"
				class="rounded-lg bg-blue-600 px-4 py-2 text-xs font-semibold text-white shadow-sm transition-all duration-200 hover:bg-blue-700 hover:shadow active:scale-95">
				+ Tạo câu hỏi / kiến thức
			</a>
		{/if}
	</div>

	<!-- Questions List -->
	<div class="divide-y divide-slate-200 border-t border-slate-200">
		{#each items as f}
			<FeedItem feedItem={f} />
		{/each}
	</div>

	<div bind:this={divEndFeed} class="h-4"></div>

	{#if loading}
		<p class="py-4 text-center text-xs text-slate-400">Đang tải...</p>
	{:else if !hasMore && items.length > 0}
		<p class="py-4 text-center text-xs text-slate-400">Đã hết dữ liệu</p>
	{/if}
</div>

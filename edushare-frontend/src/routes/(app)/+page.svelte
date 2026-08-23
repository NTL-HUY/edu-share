<script lang="ts">
	import { SlidersHorizontal, ChevronDown, BookOpen, Clock, HelpCircle } from 'lucide-svelte';
	import './css/filter-bar.css';
	import './css/top-bar-header.css';
	import './css/filter-toolbar.css';
	import { isLessonFeedMeta, isQuestionFeedMeta } from '$lib/utils/checkTypeName';
	import type { PageData } from './$types';
	import { formatTimeAgo } from '$lib/utils/time';
	import FeedItem from '$lib/components/feed/FeedItem.svelte';
	import { createSdk } from '$lib/graphql/client';
	const tabs = ['Tất cả', 'Bài học', 'Hỏi Đáp'];
	let activeTab = $state('Tất cả');
	let { data }: { data: PageData } = $props();

	function selectTab(tab: string) {
		activeTab = tab;
	}

	let items = $state(data.feed?.items ?? []);
	let cursor = $state(data.feed?.nextCursor ?? null);
	let hasMore = $state(data.feed?.hasMore ?? false);
	let loading = $state(false);

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
	<!-- Controls & Filter Bar -->
	<div class="filter-bar">
		<div>
			<h1 class="text-xl font-bold text-gray-900">Bảng tin cá nhân</h1>
			<p class="text-xs text-gray-500">Cập nhật mới nhất từ nền tảng và người dùng bạn quan tâm</p>
		</div>

		<div class="controls-group">
			<div class="tabs-container">
				{#each tabs as tab}
					<button onclick={() => (activeTab = tab)} class="tab-btn {activeTab === tab ? 'active' : ''}">
						{tab}
					</button>
				{/each}
			</div>
		</div>
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

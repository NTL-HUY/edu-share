<script lang="ts">
	import { goto } from '$app/navigation';
	import { page } from '$app/state';
	import FeedItem from '$lib/components/feed/FeedItem.svelte';
	import type { FeedSearchInput } from '$lib/generated/types';
	import type { PageData } from '../$types';

	import '$lib/styles/base-feed.css';
	import { categories, lessonLevels } from '$lib/stores/reference.store';
	let { data }: { data: PageData } = $props();

	let query = $derived(page.url.searchParams.get('q') || '');
	let currentType = $derived(page.url.searchParams.get('type') || '');
	let currentCategory = $derived(page.url.searchParams.get('category') || '');
	let currentLevel = $derived(page.url.searchParams.get('level') || '');
	let currentSort = $derived(page.url.searchParams.get('sort') || 'knowledgeId,desc');
	let currentPage = $derived(Number(page.url.searchParams.get('page')) || 0);

	let searchInput: FeedSearchInput = $derived({
		keyword: query || null,
		type: currentType ? (currentType as any) : null,
		categoryId: currentCategory || null,
		level: currentLevel ? (currentLevel as any) : null,
		page: currentPage,
		size: 10,
		sort: currentSort
	});

	function updateFilter(key: string, value: string | null) {
		const url = new URL(page.url);
		if (value) {
			url.searchParams.set(key, value);
		} else {
			url.searchParams.delete(key);
		}
		if (key !== 'page') {
			url.searchParams.set('page', '0');
		}
		goto(url.toString(), { keepFocus: true, noScroll: true });
	}

	function clearFilters() {
		const url = new URL(page.url);
		const q = url.searchParams.get('q');
		url.search = '';
		if (q) url.searchParams.set('q', q);
		goto(url.toString());
	}
</script>

<!-- TOP HEADER -->
<div class="flex items-center justify-between border-b border-gray-200 pb-3">
	<div>
		<h1 class="text-xl font-bold text-gray-900">Kết quả tìm kiếm</h1>
		<p class="mt-0.5 text-xs text-gray-500">
			{query ? `${data.feed?.totalCount || 0} Kết quả cho "${query}"` : 'Tất cả kết quả'}
		</p>
	</div>
</div>

<!-- TOP FILTER BAR -->
<div class="flex flex-wrap items-center justify-between gap-2 border-b border-gray-100 py-3 text-xs">
	<div class="flex flex-wrap items-center gap-2">
		<div class="filter-tabs-group">
			<button
				class="filter-tab-btn {currentType === '' ? 'filter-tab-btn-active' : ''}"
				onclick={() => updateFilter('type', null)}>
				Tất cả
			</button>

			<button
				class="filter-tab-btn {currentType === 'LESSON' ? 'filter-tab-btn-active' : ''}"
				onclick={() => updateFilter('type', 'LESSON')}>
				Bài học
			</button>

			<button
				class="filter-tab-btn {currentType === 'QUESTION' ? 'filter-tab-btn-active' : ''}"
				onclick={() => updateFilter('type', 'QUESTION')}>
				Hỏi đáp
			</button>
		</div>

		<!-- Filter 2: Category Dropdown -->
		<div class="relative">
			<select
				class="flex items-center gap-1.5 rounded-md border border-gray-300 bg-white px-3 py-1.5 font-medium text-gray-700 hover:bg-gray-50"
				value={currentCategory}
				onchange={(e) => updateFilter('category', e.currentTarget.value || null)}>
				<option value="">Tất cả danh mục</option>
				{#each $categories as category}
					<option value={category.id}>{category.name}</option>
				{/each}
			</select>
		</div>

		<!-- Filter 3: Level Dropdown -->
		<div class="relative">
			<select
				class="flex items-center gap-1.5 rounded-md border border-gray-200 bg-white px-3 py-1.5 text-gray-600 hover:bg-gray-50"
				value={currentLevel}
				onchange={(e) => updateFilter('level', e.currentTarget.value || null)}>
				<option value="">Tất cả trình độ</option>
				{#each $lessonLevels as lessonLevel}
					<option value={lessonLevel.code}>{lessonLevel.displayName}</option>
				{/each}
			</select>
		</div>

		<!-- Clear filters -->
		<button class="ml-1 font-medium text-gray-400 hover:text-red-600" onclick={clearFilters}>Xóa bộ lọc</button>
	</div>

	<!-- Sort Dropdown -->
	<div class="flex items-center gap-1.5 text-gray-500">
		<span>Sắp xếp:</span>
		<select
			class="cursor-pointer bg-transparent font-medium text-gray-800 focus:outline-none"
			value={currentSort}
			onchange={(e) => updateFilter('sort', e.currentTarget.value)}>
			<option value="knowledgeId,desc">Mới nhất</option>
			<option value="knowledgeId,asc">Cũ nhất</option>
		</select>
	</div>
</div>
<!-- MAIN LIST -->
<main class="divide-y divide-gray-200 border-b border-gray-200">
	{#each data.feed?.items as f}
		<FeedItem feedItem={f} />
	{/each}
</main>

<!-- PAGINATION -->
<div class="mt-6 flex items-center justify-center gap-1 text-xs">
	<button
		disabled={currentPage === 0}
		class="rounded border border-gray-300 bg-white px-2.5 py-1 text-gray-600 hover:bg-gray-50 disabled:opacity-50"
		onclick={() => updateFilter('page', String(currentPage - 1))}>
		Trước
	</button>

	<span class="px-2 font-semibold">Trang {currentPage + 1}</span>

	<button
		class="rounded border border-gray-300 bg-white px-2.5 py-1 text-gray-600 hover:bg-gray-50"
		onclick={() => updateFilter('page', String(currentPage + 1))}>
		Sau
	</button>
</div>

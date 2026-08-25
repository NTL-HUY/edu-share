<script lang="ts">
	import { Search } from 'lucide-svelte';
	import KnowledgeItemRow from './KnowledgeItemRow.svelte';
	import { KnowledgeListState } from './knowledgeList.svelte';

	// let { activeSubTab }: { activeSubTab: 'lessons' | 'questions' } = $props();

	const list = new KnowledgeListState();
	let searchQuery = $state('');

	const filteredItems = $derived(
		list.items.filter((i) => i.title.toLowerCase().includes(searchQuery.toLowerCase()))
	);

	// load lại từ đầu khi đổi tab
	$effect(() => {
		// activeSubTab; 
		list.load(true);
	});

	async function toggleVisibility(id: string) {
		const item = list.items.find((i) => i.id === id);
		if (!item) return;
		list.updateItem(id, { isPublic: !item.isPublic }); // optimistic
		// gọi mutation thật ở đây, rollback nếu lỗi
	}

	async function deleteItem(id: string) {
		if (!confirm('Xoá mục này?')) return;
		list.removeItem(id); // optimistic
		// gọi mutation deleteKnowledge(id) thật ở đây
	}
</script>

<div class="flex items-center justify-between gap-4">
	<h2 class="text-lg font-normal text-slate-800">
		Có tổng cộng {list.totalElements} bài đăng
	</h2>
	<div class="relative w-64">
		<Search class="absolute top-1/2 left-2.5 h-3.5 w-3.5 -translate-y-1/2 text-slate-400" />
		<input
			type="text"
			bind:value={searchQuery}
			placeholder="Lọc tiêu đề..."
			class="w-full rounded-md border border-slate-300 py-1.5 pr-3 pl-8 text-xs focus:border-orange-500 focus:outline-none" />
	</div>
</div>

<div class="divide-y divide-slate-100 rounded-md border border-slate-200 bg-white">
	{#each filteredItems as item (item.id)}
		<KnowledgeItemRow {item} onToggleVisibility={toggleVisibility} onDelete={deleteItem} />
	{:else}
		{#if !list.loading}
			<div class="p-6 text-center text-sm text-slate-400">Không có dữ liệu</div>
		{/if}
	{/each}
</div>

{#if list.error}
	<p class="text-center text-sm text-rose-500">{list.error}</p>
{/if}

{#if list.hasMore}
	<div class="flex justify-center py-3">
		<button
			onclick={list.loadMore}
			disabled={list.loading}
			class="rounded-md border border-slate-300 px-4 py-1.5 text-xs text-slate-600 hover:bg-slate-50 disabled:opacity-50">
			{list.loading ? 'Đang tải...' : 'Xem thêm'}
		</button>
	</div>
{/if}
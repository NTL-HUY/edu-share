<script lang="ts">
	import { Search } from 'lucide-svelte';
	import KnowledgeItemRow from './KnowledgeItemRow.svelte';
	import { KnowledgeListState } from './knowledgeList.svelte.ts';

	let { profile  }: { profile: any } = $props();
	const list = $derived(new KnowledgeListState(profile));
	let searchQuery = $state('');

	const filteredItems = $derived(list.items.filter((i) => i.title.toLowerCase().includes(searchQuery.toLowerCase())));

	$effect(() => {
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
	<div class="">
		
	</div>
</div>

<div class="divide-y divide-slate-100 rounded-md border border-slate-200 bg-white">
	{#each filteredItems as item (item.id)}
		<KnowledgeItemRow {item} onToggleVisibility={toggleVisibility} onDelete={deleteItem} isReadOnly={!profile.isMe} />
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

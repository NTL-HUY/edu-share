<script lang="ts">
	import type { GetMyKnowledgeListQuery } from '$lib/generated/types';
	import { BookOpen, Globe, HelpCircle, Lock, Pencil, Trash2 } from 'lucide-svelte';

	let {
		item,
		onToggleVisibility,
		onDelete,
		isReadOnly
	}: {
		item: GetMyKnowledgeListQuery['myKnowledgeList']['content'][number];
		onToggleVisibility: (id: string) => void;
		onDelete: (id: string) => void;
      isReadOnly: boolean
	} = $props();

   let readOnlyMode = $derived(isReadOnly);
	const isLesson = $derived(item.type === 'LESSON');
</script>

<div class="group flex items-center justify-between gap-4 p-4 transition hover:bg-slate-50/70">
	<div class="flex min-w-0 items-center gap-4">
		<!-- vote score -->
		<div
			class="flex min-w-12 flex-col items-center justify-center rounded-md border border-slate-200 bg-slate-50 px-2 py-1.5">
			<span class="text-sm font-bold text-slate-700">{item.voteScore}</span>
			<span class="text-[9px] tracking-wide text-slate-400 uppercase">votes</span>
		</div>

		<div class="min-w-0 space-y-1.5">
			<div class="flex items-center gap-2">
				<!-- type badge -->
				{#if isLesson}
					<span
						class="inline-flex items-center gap-1 rounded-full bg-orange-50 px-2 py-0.5 text-[10px] font-medium text-orange-600">
						<BookOpen class="h-3 w-3" />
						Bài học
					</span>
				{:else}
					<span
						class="inline-flex items-center gap-1 rounded-full bg-sky-50 px-2 py-0.5 text-[10px] font-medium text-sky-600">
						<HelpCircle class="h-3 w-3" />
						Câu hỏi
					</span>
				{/if}

				{#if 'isResolved' in item && item.isResolved}
					<span class="text-[10px] font-medium text-emerald-600">Đã giải quyết</span>
				{/if}
			</div>

			<a href="/feed/{item.id}" class="block truncate text-sm font-medium text-slate-800 hover:text-orange-600">
				{item.title}
			</a>

			<div class="flex items-center gap-3 text-xs text-slate-400">
				<span>{item.viewsCount} lượt xem</span>
				<span>{item.commentCount} bình luận</span>
			</div>
		</div>
	</div>

	{#if !readOnlyMode}
		<div class="flex items-center gap-1 opacity-0 transition group-hover:opacity-100">
			<button
				onclick={() => onToggleVisibility(item.id)}
				title={item.isPublic ? 'Đang công khai' : 'Đang riêng tư'}
				class="rounded-md p-1.5 text-slate-400 hover:bg-slate-100 hover:text-slate-600">
				{#if item.isPublic}
					<Globe class="h-4 w-4 text-emerald-600" />
				{:else}
					<Lock class="h-4 w-4" />
				{/if}
			</button>
			<a href="/feed/{item.id}/edit" class="rounded-md p-1.5 text-slate-400 hover:bg-slate-100 hover:text-blue-600">
				<Pencil class="h-4 w-4" />
			</a>
			<button
				onclick={() => onDelete(item.id)}
				class="rounded-md p-1.5 text-slate-400 hover:bg-rose-50 hover:text-rose-600">
				<Trash2 class="h-4 w-4" />
			</button>
		</div>
	{/if}
</div>

<script lang="ts">
	import {
		Activity,
		BookOpen,
		HelpCircle,
		MessageSquare,
		ThumbsUp,
		UserCheck,
		UserPlus,
		Sparkles
	} from 'lucide-svelte';
	import { sidebarState } from './activitySidebar.svelte.ts';
</script>

<div class="w-full shrink-0 space-y-1 text-xs sm:w-45 border-r border-slate-200 pr-1 ">


	<button
		onclick={() => sidebarState.setSubTab('knowledge')}
		class="flex w-full items-center justify-between rounded-md px-3 py-2 font-medium transition {sidebarState.activeSubTab ===
		'knowledge'
			? 'bg-slate-100 font-bold text-slate-900'
			: 'text-slate-600 hover:bg-slate-50'}">
		<span class="flex items-center gap-2"><BookOpen class="h-3.5 w-3.5" /> Bài đăng</span>
		<span class="rounded bg-slate-200 px-1.5 py-0.5 text-[10px]">{sidebarState.counts.lessons}</span>
	</button>

	<!-- <button
		onclick={() => sidebarState.setSubTab('comments')}
		class="flex w-full items-center justify-between rounded-md px-3 py-2 font-medium transition {sidebarState.activeSubTab ===
		'comments'
			? 'bg-slate-100 font-bold text-slate-900'
			: 'text-slate-600 hover:bg-slate-50'}">
		<span class="flex items-center gap-2"><MessageSquare class="h-3.5 w-3.5" /> Bình luận</span>
		<span class="rounded bg-slate-200 px-1.5 py-0.5 text-[10px]">{sidebarState.counts.comments}</span>
	</button> -->

	<!-- <button
		onclick={() => sidebarState.setSubTab('votes')}
		class="flex w-full items-center justify-between rounded-md px-3 py-2 font-medium transition {sidebarState.activeSubTab ===
		'votes'
			? 'bg-slate-100 font-bold text-slate-900'
			: 'text-slate-600 hover:bg-slate-50'}">
		<span class="flex items-center gap-2"><ThumbsUp class="h-3.5 w-3.5" /> Lịch sử Vote</span>
		<span class="rounded bg-slate-200 px-1.5 py-0.5 text-[10px]">{sidebarState.counts.votes}</span>
	</button> -->

	<button
		onclick={() => sidebarState.setSubTab('followers')}
		class="flex w-full items-center justify-between rounded-md px-3 py-2 font-medium transition {sidebarState.activeSubTab ===
		'followers'
			? 'bg-slate-100 font-bold text-slate-900'
			: 'text-slate-600 hover:bg-slate-50'}">
		<span class="flex items-center gap-2"><UserCheck class="h-3.5 w-3.5" /> Followers</span>
		<span class="rounded bg-slate-200 px-1.5 py-0.5 text-[10px]">{sidebarState.counts.followers}</span>
	</button>

	<button
		onclick={() => sidebarState.setSubTab('following')}
		class="flex w-full items-center justify-between rounded-md px-3 py-2 font-medium transition {sidebarState.activeSubTab ===
		'following'
			? 'bg-slate-100 font-bold text-slate-900'
			: 'text-slate-600 hover:bg-slate-50'}">
		<span class="flex items-center gap-2"><UserPlus class="h-3.5 w-3.5" /> Following</span>
		<span class="rounded bg-slate-200 px-1.5 py-0.5 text-[10px]">{sidebarState.counts.following}</span>
	</button>

	<!-- <button
		onclick={() => sidebarState.setSubTab('ai_chunks')}
		class="flex w-full items-center justify-between rounded-md px-3 py-2 font-medium transition {sidebarState.activeSubTab ===
		'ai_chunks'
			? 'bg-slate-100 font-bold text-slate-900'
			: 'text-slate-600 hover:bg-slate-50'}">
		<span class="flex items-center gap-2 text-purple-600"><Sparkles class="h-3.5 w-3.5" /> Vector Chunks</span>
		<span class="rounded bg-purple-100 px-1.5 py-0.5 text-[10px] text-purple-700">{sidebarState.counts.ai_chunks}</span>
	</button> -->
</div>

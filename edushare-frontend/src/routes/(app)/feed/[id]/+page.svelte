<!-- src/routes/feed/[id]/+page.svelte -->
<script lang="ts">
	import { Eye, Clock, HelpCircle, BookOpen } from 'lucide-svelte';
	import { isLesson, isQuestion } from '$lib/utils/checkTypeName';
	import { formatTimeAgo } from '$lib/utils/time';
	import MarkdownContent from '$lib/components/feed/MarkdownContent.svelte';
	import { page } from '$app/state';
	import toast from 'svelte-french-toast';
	import type { FeedPageData } from './+page.server';

	// Sub-components
	import VoteSidebar from './_components/VoteSidebar.svelte';
	import CommentItem from './_components/CommentItem.svelte';
	import { createCommentState } from './_components/commentState.svelte';

	let { data }: { data: FeedPageData } = $props();

	let currentUser = $derived(page.data?.user);
	let feedItem = $derived(data.knowledge);
	let comments = $derived(feedItem?.comments);

	// Khoi tao state comment
	const commentState = createCommentState(feedItem?.id ?? '');

	$effect(() => {
		if (data.serverError) toast.error(data.serverError);
	});
</script>

<div class="mx-auto w-full">
	<article class="mx-auto w-full">
		<!-- Title & Meta -->
		<div class="mb-5 border-b border-gray-200 pb-4">
			<div class="mb-2.5 flex items-center gap-2 text-xs">
				{#if isLesson(feedItem)}
					<span class="badge badge-lesson"><BookOpen class="h-3 w-3" /> {feedItem?.type}</span>
					<span class="badge badge-level">INTERMEDIATE</span>
				{:else if isQuestion(feedItem)}
					<span class="badge badge-question"><HelpCircle class="h-3 w-3" /> {feedItem?.type}</span>
					<span class="badge {feedItem?.isResolved ? 'badge-resolved' : 'badge-unresolved'}">
						{feedItem?.isResolved ? 'Đã có lời giải' : 'Chưa có lời giải'}
					</span>
				{/if}
				<span class="text-gray-500">{feedItem?.category?.name}</span>
			</div>

			<h1 class="text-2xl leading-snug font-bold text-gray-900">{feedItem?.title}</h1>

			<div class="mt-4 flex items-center justify-between text-xs text-gray-500">
				<div class="flex items-center gap-2">
					<img
						src="https://ui-avatars.com/api/?name={feedItem?.owner.username}&background=0D8ABC&color=fff"
						alt="{feedItem?.owner.username}'s avatar"
						class="h-7 w-7 rounded-full" />
					<span class="font-semibold text-gray-700">{feedItem?.owner.username}</span>
					<span>• Đăng ngày {formatTimeAgo(feedItem?.createdAt)}</span>
				</div>
				<div class="flex items-center gap-4 text-gray-500">
					<span class="flex items-center gap-1"><Eye size={14} /> {feedItem?.viewsCount} lượt xem</span>
					{#if isLesson(feedItem)}
						<span class="flex items-center gap-1"><Clock size={14} /> {feedItem?.estimateTimeInMinutes ?? 0} phút đọc</span>
					{/if}
				</div>
			</div>
		</div>

		<!-- Main Body -->
		<div class="flex items-start gap-5 border-b border-gray-200 pb-5">
			<VoteSidebar feedId={feedItem?.id} initialScore={feedItem?.voteScore} initialVote={feedItem?.currentUserVote} />

			<div class="min-w-0 flex-1 overflow-hidden">
				{#if isLesson(feedItem)}
					<MarkdownContent content={feedItem?.contentMarkdown ?? feedItem?.abstractText} />
				{:else if isQuestion(feedItem)}
					<div class="text-sm leading-relaxed break-words whitespace-pre-wrap text-gray-800">
						{feedItem?.content ?? feedItem?.abstractText}
					</div>
				{/if}
			</div>
		</div>
	</article>

	<!-- Section Bình luận -->
	<section class="mt-6">
		<div class="flex items-center justify-between border-b border-gray-200 pb-3">
			<h3 class="text-base font-bold text-gray-900">
				Bình luận &amp; Thảo luận
				<span class="ml-1 text-xs font-normal text-gray-500">({comments?.totalElements ?? 0} bình luận)</span>
			</h3>
		</div>

		<!-- Form Bình luận -->
		<div class="mt-4 flex gap-3">
			<img
				src={currentUser?.avatarUrl ?? `https://ui-avatars.com/api/?name=${currentUser?.username ?? 'User'}&background=6366F1&color=fff`}
				alt="Avatar"
				class="h-8 w-8 shrink-0 rounded-full" />

			<div class="flex-1 space-y-2">
				{#if commentState.replyingTo}
					<div class="flex items-center justify-between rounded-md bg-orange-50 px-3 py-1.5 text-xs text-orange-700">
						<span>Đang trả lời <strong class="font-semibold">@{commentState.replyingTo.userName}</strong></span>
						<button type="button" onclick={() => (commentState.replyingTo = null)} class="font-bold text-orange-500 hover:text-orange-800">✕ Hủy</button>
					</div>
				{/if}

				<textarea
					bind:value={commentState.content}
					rows="3"
					placeholder="Viết bình luận hoặc đặt câu hỏi..."
					class="w-full rounded-lg border border-gray-300 p-3 text-xs transition focus:border-orange-500 focus:outline-none"></textarea>

				<div class="flex justify-end">
					<button
						type="button"
						disabled={commentState.isSubmitting || !commentState.content.trim()}
						onclick={commentState.sendComment}
						class="rounded-md bg-orange-600 px-4 py-2 text-xs font-medium text-white shadow-sm transition hover:bg-orange-700 disabled:opacity-50">
						{commentState.isSubmitting ? 'Đang gửi...' : 'Gửi bình luận'}
					</button>
				</div>
			</div>
		</div>

		<!-- Danh sách Bình luận -->
		<div class="mt-4 space-y-4 divide-y divide-gray-100">
			{#if comments?.content && comments.content.length > 0}
				{#each comments.content as comment (comment.id)}
					<CommentItem
						{comment}
						ownerUsername={feedItem?.owner.username}
						onReply={(target) => (commentState.replyingTo = target)} />
				{/each}
			{:else}
				<p class="pt-6 text-center text-xs italic text-gray-400">Chưa có bình luận nào. Hãy là người đầu tiên thảo luận!</p>
			{/if}
		</div>
	</section>
</div>
<!-- src/routes/feed/[id]/_components/CommentItem.svelte -->
<script lang="ts">
	import { formatTimeAgo } from '$lib/utils/time';
	type Props = {
		comment: any;
		ownerUsername?: string | null;
		onReply: (comment: { id: number; userName: string }) => void;
	};
	let { comment, ownerUsername, onReply }: Props = $props();
		
</script>

<div class={comment.rootCommentId ? 'ml-8 pt-3' : 'pt-4'}>
	<div class={comment.rootCommentId ? 'rounded-r-lg border-l-2 border-orange-300 bg-gray-50/60 p-3' : 'space-y-2'}>
		<div class="flex items-center justify-between">
			<div class="flex items-center gap-2">
				<img
					src={comment.userAvatarUrl ?? `https://ui-avatars.com/api/?name=${comment.userName}&background=0D8ABC&color=fff`}
					alt="{comment.userName}'s avatar"
					class="h-6 w-6 rounded-full" />
				<span class="text-xs font-semibold text-gray-800">{comment.userName}</span>

				{#if comment.userName === ownerUsername}
					<span class="rounded bg-orange-100 px-1.5 py-0.5 text-[10px] font-bold text-orange-700">Tác giả</span>
				{/if}

				{#if comment.replyToUserName}
					<span class="text-[11px] text-gray-400">
						trả lời <span class="font-medium text-orange-600">@{comment.replyToUserName}</span>
					</span>
				{/if}

				<span class="text-[11px] text-gray-400">• {formatTimeAgo(comment.createdAt)}</span>
			</div>

			<button
				type="button"
				onclick={() => onReply({ id: comment.id, userName: comment.userName })}
				class="flex items-center gap-1 text-[11px] font-medium text-gray-500 hover:text-orange-600">
				Trả lời
			</button>
		</div>

		<p class="mt-1 text-xs leading-relaxed break-words whitespace-pre-wrap text-gray-700">
			{comment.content}
		</p>

		{#if comment.replyCount > 0 && !comment.rootCommentId}
			<div class="pt-1">
				<button type="button" class="text-[11px] font-semibold text-orange-600 hover:underline">
					Xem {comment.replyCount} câu trả lời
				</button>
			</div>
		{/if}
	</div>
</div>

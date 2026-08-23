<script lang="ts">
	import type { FeedItem } from "$lib/generated/types";
	import { isLessonFeedMeta, isQuestionFeedMeta } from "$lib/utils/checkTypeName";
	import { formatTimeAgo } from "$lib/utils/time";
	import { BookOpen, Clock1, HelpCircle } from "lucide-svelte";
	import './feed-item.css';

	let { feedItem }: { feedItem: FeedItem } = $props();
   console.log('feedItem in FeedItem.svelte:', feedItem);
</script>

<div class="feed-item">
	<!-- Metrics Stats (Left Column) -->
	<div class="metrics">
		<span class="votes">{feedItem?.voteScore ?? 0} votes</span>
		<span>{feedItem?.commentCount ?? 0} answers</span>
		<span>{feedItem?.viewsCount ?? 0} views</span>
	</div>

	<!-- Content Details (Right Column) -->
	<div class="content">
		<!-- Header: Badge & Meta -->
		<div class="feed-meta-header">
			<div class="badge-group">
				{#if isLessonFeedMeta(feedItem?.typeMeta)}
					<span class="badge badge-lesson">
						<BookOpen class="h-3 w-3" />
						LESSON
					</span>
					<span class="badge badge-level">INTERMEDIATE</span>
				{:else if isQuestionFeedMeta(feedItem?.typeMeta)}
					<span class="badge badge-question">
						<HelpCircle class="h-3 w-3" />
						QUESTION
					</span>
					<span class="badge badge-unresolved">Chờ lời giải</span>
				{/if}
				{#if feedItem?.categoryId}
					<span class="text-slate-400">•</span>
					<span class="category-info">
						Danh mục: <strong>{feedItem.categoryName}</strong>
					</span>
				{/if}
			</div>
			{#if isLessonFeedMeta(feedItem.typeMeta)}
				<span class="read-time">
					<Clock1 class="h-3.5 w-3.5" />
					{feedItem.typeMeta?.estimateTimeInMinutes ?? 0} min read
				</span>
			{/if}
		</div>

		<h2 class="title">
			<a href={`/feed/${feedItem.knowledgeId}`}>{feedItem.title}</a>
		</h2>

		<p class="description">
			{#if isLessonFeedMeta(feedItem.typeMeta)}
				{feedItem.abstractText}
			{:else if isQuestionFeedMeta(feedItem.typeMeta)}
				{feedItem.typeMeta?.content ?? feedItem.abstractText}
			{/if}
		</p>

		<!-- Tags & Author Info Footer -->
		<div class="footer">
			<!-- Tags -->
			<div class="tags-list">
				<!-- {#each feedItem.tags as tag}
						<a href={`/tags/${tag}`} class="tag-item">
							{tag}
						</a>
					{/each} -->
			</div>

			<!-- Author info -->
			<div class="author-info">
				<img src={`${feedItem.ownerAvatarUrl ?? `https://ui-avatars.com/api/?name=${feedItem.ownerName}`}`} alt={feedItem.ownerName} class="avatar" />
				<a href={`/users/${feedItem.ownerName}`} class="author-name">{feedItem.ownerName}</a>
				<!-- <span class="reputation">{feedItem.ownerReputation}</span> -->
				<span>Dang:{formatTimeAgo(feedItem.sourceCreatedAt)}</span>
			</div>
		</div>
	</div>
</div>

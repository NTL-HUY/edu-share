import { getClientSdk } from '$lib/graphql/client';
import { toast } from 'svelte-sonner';

type ReplyState = {
	items: any[];
	isLoading: boolean;
	isExpanded: boolean;
	isLoaded: boolean;
};

export function createCommentState(
	feedId: string,
	initComments: any[] = [],
	initNextCursor: string | null = null,
	initHasMore: boolean = false
) {
	let content = $state('');
	let replyingTo = $state<{ id: number; userName: string } | null>(null);
	let isSubmitting = $state(false);

	let commentsList = $state<any[]>(initComments);
	let nextCursor = $state<string | null>(initNextCursor);
	let hasMore = $state(initHasMore);
	let isLoadingMore = $state(false);

	let repliesMap = $state<Record<number, ReplyState>>({});

	function getReplyState(rootCommentId: number): ReplyState {
		return repliesMap[rootCommentId] ?? { items: [], isLoading: false, isExpanded: false, isLoaded: false };
	}

	async function toggleReplies(rootCommentId: number) {
		const current = getReplyState(rootCommentId);

		// Đã mở -> đóng lại
		if (current.isExpanded) {
			repliesMap = { ...repliesMap, [rootCommentId]: { ...current, isExpanded: false } };
			return;
		}

		// Mở ra, load nếu chưa từng load
		if (!current.isLoaded) {
			repliesMap = { ...repliesMap, [rootCommentId]: { ...current, isExpanded: true, isLoading: true } };

			try {
				const sdk = getClientSdk();
				const data = await sdk.listCommentReplies({ knowledgeId: feedId, rootCommentId });

				repliesMap = {
					...repliesMap,
					[rootCommentId]: {
						items: data.listCommentReplies,
						isLoading: false,
						isExpanded: true,
						isLoaded: true
					}
				};
			} catch (err: any) {
				const graphQLError = err?.response?.errors?.[0]?.message;
				const errorMessage = graphQLError || err?.message || 'Không thể tải câu trả lời';
				console.error('Lỗi khi tải replies:', err);
				toast.error(errorMessage);
				repliesMap = { ...repliesMap, [rootCommentId]: { ...current, isLoading: false, isExpanded: false } };
			}
		} else {
			repliesMap = { ...repliesMap, [rootCommentId]: { ...current, isExpanded: true } };
		}
	}

	async function loadMore() {
		if (!hasMore || isLoadingMore) return;

		isLoadingMore = true;

		try {
			const sdk = getClientSdk();
			const data = await sdk.ListRootComments({
				knowledgeId: feedId,
				input: { cursor: nextCursor, limit: 2 }
			});

			const result = data.listRootComments;

			commentsList = [...commentsList, ...result.items];
			nextCursor = result.nextCursor;
			hasMore = result.hasMore;
		} catch (err: any) {
			const graphQLError = err?.response?.errors?.[0]?.message;
			const errorMessage = graphQLError || err?.message || 'Không thể tải thêm bình luận';
			console.error('Lỗi khi tải bình luận:', err);
			toast.error(errorMessage);
		} finally {
			isLoadingMore = false;
		}
	}

	async function sendComment() {
		if (!content.trim()) {
			toast.error('Vui lòng nhập nội dung bình luận!');
			return;
		}

		isSubmitting = true;
		try {
			const res = await fetch(`/feed/${feedId}/comment`, {
				method: 'PUT',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({
					content: content.trim(),
					replyToCommentId: replyingTo?.id ?? null
				})
			});

			const responseData = await res.json();
			if (!res.ok) throw new Error(responseData.message || 'Lỗi khi gửi bình luận');
			if (replyingTo) {
				const rootId = responseData.data.rootCommentId ?? replyingTo.id;
				const current = getReplyState(rootId);
				repliesMap = {
					...repliesMap,
					[rootId]: { ...current, items: [...current.items, responseData.data], isExpanded: true }
				};
			} else {
				commentsList = [responseData.data, ...commentsList];
			}

			toast.success('Đã đăng bình luận!');
			content = '';
			replyingTo = null;
		} catch (err: any) {
			toast.error(err.message || 'Không thể gửi bình luận');
		} finally {
			isSubmitting = false;
		}
	}

	return {
		get content() {
			return content;
		},
		set content(val: string) {
			content = val;
		},
		get replyingTo() {
			return replyingTo;
		},
		set replyingTo(val) {
			replyingTo = val;
		},
		get isSubmitting() {
			return isSubmitting;
		},
		sendComment,

		get commentsList() {
			return commentsList;
		},
		get hasMore() {
			return hasMore;
		},
		get isLoadingMore() {
			return isLoadingMore;
		},
		loadMore,

		getReplyState,
		toggleReplies
	};
}

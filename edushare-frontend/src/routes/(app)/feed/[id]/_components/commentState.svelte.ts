// src/routes/feed/[id]/_components/commentState.svelte.ts
import { invalidateAll } from '$app/navigation';
import toast from 'svelte-french-toast';

export function createCommentState(feedId: string) {
	let content = $state('');
	let replyingTo = $state<{ id: number; userName: string } | null>(null);
	let isSubmitting = $state(false);

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

			toast.success('Đã đăng bình luận!');
			content = '';
			replyingTo = null;
			await invalidateAll();
		} catch (err: any) {
			toast.error(err.message || 'Không thể gửi bình luận');
		} finally {
			isSubmitting = false;
		}
	}

	return {
		get content() { return content; },
		set content(val: string) { content = val; },
		get replyingTo() { return replyingTo; },
		set replyingTo(val) { replyingTo = val; },
		get isSubmitting() { return isSubmitting; },
		sendComment
	};
}
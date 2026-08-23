<!-- src/routes/feed/[id]/_components/VoteSidebar.svelte -->
<script lang="ts">
	import { ChevronUp, ChevronDown } from 'lucide-svelte';
	import toast from 'svelte-french-toast';

	let { feedId, initialScore = 0, initialVote = 0 }: { feedId: string; initialScore?: number; initialVote?: number } = $props();

	let currentVoteScore = $state(initialScore);
	let currentUserVote = $state(initialVote);

	$effect(() => {
		currentVoteScore = initialScore;
		currentUserVote = initialVote;
	});

	async function handleVote(targetVote: number) {
		if (!feedId) return;

		const previousScore = currentVoteScore;
		const previousUserVote = currentUserVote;
		const newVote = currentUserVote === targetVote ? 0 : targetVote;
		const scoreDelta = newVote - currentUserVote;

		currentUserVote = newVote;
		currentVoteScore += scoreDelta;

		try {
			const res = await fetch(`/feed/${feedId}/vote`, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ value: String(newVote) })
			});

			const responseData = await res.json();
			if (!res.ok) throw new Error(responseData.message || 'Lỗi khi gửi vote');

			if (newVote === 1) toast.success('Đã upvote!');
			else if (newVote === -1) toast.success('Đã downvote!');
			else toast.success('Đã hủy vote!');
		} catch (err: any) {
			currentVoteScore = previousScore;
			currentUserVote = previousUserVote;
			toast.error(err.message || 'Không thể gửi vote');
		}
	}
</script>

<div class="flex shrink-0 flex-col items-center gap-1 text-gray-500">
	<button
		type="button"
		onclick={() => handleVote(1)}
		class="flex h-8 w-8 items-center justify-center rounded-full border transition {currentUserVote === 1 ? 'border-orange-500 bg-orange-100 font-bold text-orange-600' : 'border-gray-200 hover:border-orange-500 hover:bg-orange-50 hover:text-orange-600'}"
		title="Upvote">
		<ChevronUp size={16} />
	</button>

	<span class="my-0.5 text-sm font-bold {currentUserVote !== 0 ? 'text-orange-600' : 'text-gray-900'}">
		{currentVoteScore}
	</span>

	<button
		type="button"
		onclick={() => handleVote(-1)}
		class="flex h-8 w-8 items-center justify-center rounded-full border transition {currentUserVote === -1 ? 'border-blue-500 bg-blue-100 font-bold text-blue-600' : 'border-gray-200 hover:border-blue-500 hover:bg-blue-50 hover:text-blue-600'}"
		title="Downvote">
		<ChevronDown size={16} />
	</button>
</div>
<!-- src/routes/(app)/feed/[id]/edit/+page.svelte -->
<script lang="ts">
	import { page } from '$app/stores';
	import PostForm from '../../_components/PostForm.svelte';
	import { CreatePostState } from '../../create/createPost.svelte';

	const form = new CreatePostState();

	$effect(() => {
		const id = $page.params.id;
		if (id) form.loadForEdit(id);
	});
</script>

{#if form.isLoadingDetail}
	<div class="py-20 text-center text-sm text-slate-400">Đang tải dữ liệu...</div>
{:else}
	<PostForm {form} />
{/if}
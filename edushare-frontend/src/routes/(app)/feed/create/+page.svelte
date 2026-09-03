<!-- src/routes/(app)/feed/create/+page.svelte -->
<script lang="ts">
	import PostForm from '../_components/PostForm.svelte';
	import { CreatePostState } from './createPost.svelte';

	const form = new CreatePostState();
</script>

<PostForm {form} />
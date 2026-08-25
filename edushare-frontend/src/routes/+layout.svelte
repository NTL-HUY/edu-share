<script lang="ts">
	import './layout.css';
	import favicon from '$lib/assets/favicon.svg';
	import { onMount } from 'svelte';
	import { loadReferenceData } from '$lib/stores/reference.store';


   onMount(() => {
      loadReferenceData();
   })
	let { children } = $props();
</script>

<svelte:head><link rel="icon" href={favicon} /></svelte:head>

{@render children()}
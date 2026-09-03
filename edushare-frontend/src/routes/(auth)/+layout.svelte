<script lang="ts">
	import favicon from '$lib/assets/favicon.svg';
	import Header from '$lib/components/common/Header.svelte';

	let { children } = $props();
</script>

<svelte:head><link rel="icon" href={favicon} /></svelte:head>
<!-- Header cố định ở trên -->
<div class="flex flex-col">
   <Header />
   <div class="flex flex-1">
      <main class="flex-1">
         {@render children()}
      </main>
   </div>
</div>

<script lang="ts">
	import Header from '$lib/components/common/Header.svelte';
	import Sidebar from '$lib/components/common/Sidebar.svelte';
	import Footer from '$lib/components/common/Footer.svelte';
   import { Toaster } from 'svelte-french-toast';
   import '../../app.css';
	let { children } = $props();
</script>

<Toaster position="top-right"/>

<div class="flex min-h-screen flex-col">
	<Header />
	<div class="flex flex-1">
		<Sidebar />
		<main class="flex-1 p-6 ">
			{@render children()}
		</main>
	</div>
	<Footer />
</div>

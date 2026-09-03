<script lang="ts">
	import Header from '$lib/components/common/Header.svelte';
	import Sidebar from '$lib/components/common/Sidebar.svelte';
	import Footer from '$lib/components/common/Footer.svelte';
   import { Toaster } from 'svelte-sonner';
   import '$lib/styles/app.css';
	import Chatbot from '$lib/components/chatbot/Chatbot.svelte';
	import { stomp } from '$lib/configs/stomp.svelte.js';
	import { onDestroy } from 'svelte';
	let { data, children } = $props();

	$effect(() => {
		if (data.user) {
			stomp.activate(); 
		} else {
			stomp.deactivate();
		}
	});

	onDestroy(() => {
		stomp.deactivate(); 
	});

</script>

<Toaster position="top-right"/>

<div class="flex min-h-screen flex-col">
	<Header />
	<div class="flex flex-1">
		<Sidebar user={data.user}/>
		<main class="flex-1 p-6 ">
			{@render children()}
		</main>
	</div>
	<!-- <Footer /> -->
</div>


<!-- <Chatbot /> -->
<script lang="ts">
	import type { ModelResponse } from '$lib/types/chatbot.typespes';
	import type { ApiResponse } from '$lib/utils/apiResponse';
	import { MessageCircle, X, Send, Bot, User, Loader2 } from 'lucide-svelte';

	type Message = {
		sender: 'user' | 'bot';
		text: string;
		sources?: { title?: string; url?: string }[];
	};

	let isOpen = $state(false);
	let inputQuery = $state('');
	let messages = $state<Message[]>([
		{ sender: 'bot', text: 'Xin chào! Tôi là AI trợ lý EDU SHARE. Bạn cần giải đáp thắc mắc gì hôm nay?' }
	]);
	let loading = $state(false);
	let chatContainer: HTMLDivElement | null = $state(null);

	// Tự động scroll xuống cuối khi có tin nhắn mới
	$effect(() => {
		if (messages.length && chatContainer) {
			chatContainer.scrollTop = chatContainer.scrollHeight;
		}
	});

	async function handleSend() {
		const query = inputQuery.trim();
		if (!query || loading) return;

		// Append user message
		messages = [...messages, { sender: 'user', text: query }];
		inputQuery = '';
		loading = true;

		try {
			// Trỏ tới endpoint FastAPI của bạn
			const response = await fetch('/api/chat', {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ query })
			});
			const result: ApiResponse<ModelResponse> = await response.json();

			if (!result.success) throw new Error('Có lỗi xảy ra từ máy chủ');

			const data = result.data;
			messages = [
				...messages,
				{
					sender: 'bot',
					text: data?.answer || 'Không tìm thấy phản hồi.',
					sources: data?.sources
				}
			];
		} catch (err) {
			messages = [...messages, { sender: 'bot', text: 'Rất tiếc, đã có lỗi kết nối tới Server AI.' }];
		} finally {
			loading = false;
		}
	}

	function handleKeyDown(e: KeyboardEvent) {
		if (e.key === 'Enter' && !e.shiftKey) {
			e.preventDefault();
			handleSend();
		}
	}
</script>

<!-- Floating Toggle Button -->
<div class="fixed right-6 bottom-20 z-50">
	{#if !isOpen}
		<button
			onclick={() => (isOpen = true)}
			class="flex h-14 w-14 items-center justify-center rounded-full bg-blue-600 text-white shadow-lg transition-all duration-200 hover:scale-105 hover:bg-blue-700 active:scale-95"
			aria-label="Mở khung chat trợ lý AI">
			<MessageCircle class="h-6 w-6" />
		</button>
	{/if}
</div>

<!-- Chat Window Popup -->
{#if isOpen}
	<div
		class="fixed right-6 bottom-6 z-50 flex h-[520px] w-96 flex-col overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-2xl transition-all">
		<!-- Header -->
		<div class="flex items-center justify-between border-b border-slate-100 bg-blue-600 px-4 py-3 text-white">
			<div class="flex items-center gap-2.5">
				<div class="flex h-8 w-8 items-center justify-center rounded-full bg-white/20">
					<Bot class="h-5 w-5 text-white" />
				</div>
				<div>
					<h3 class="text-sm leading-tight font-semibold">Trợ lý EDU SHARE</h3>
					<span class="flex items-center gap-1 text-[11px] text-blue-100">
						<span class="h-1.5 w-1.5 rounded-full bg-green-400"></span>
						 Trực tuyến
					</span>
				</div>
			</div>
			<button
				onclick={() => (isOpen = false)}
				class="rounded-lg p-1 text-white/80 transition-colors hover:bg-white/10 hover:text-white">
				<X class="h-5 w-5" />
			</button>
		</div>

		<!-- Messages Body -->
		<div bind:this={chatContainer} class="flex-1 space-y-3 overflow-y-auto bg-slate-50/50 p-4">
			{#each messages as msg}
				<div class="flex gap-2.5 {msg.sender === 'user' ? 'justify-end' : 'justify-start'}">
					{#if msg.sender === 'bot'}
						<div class="mt-1 flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-blue-100 text-blue-600">
							<Bot class="h-4 w-4" />
						</div>
					{/if}

					<div
						class="max-w-[80%] rounded-2xl px-3.5 py-2.5 text-sm shadow-sm {msg.sender === 'user'
							? 'rounded-br-xs bg-blue-600 text-white'
							: 'rounded-bl-xs border border-slate-200 bg-white text-slate-800'}">
						<p class="leading-relaxed whitespace-pre-wrap">{msg.text}</p>

						{#if msg.sources && msg.sources.length > 0}
							<div class="mt-2 border-t border-slate-100 pt-1.5 text-[11px] text-slate-400">
								<span class="font-medium">Nguồn tham khảo:</span>
								<ul class="mt-0.5 list-disc pl-4">
									{#each msg.sources as src}
										<li>{src.title || 'Dữ liệu nội bộ'}</li>
									{/each}
								</ul>
							</div>
						{/if}
					</div>

					{#if msg.sender === 'user'}
						<div
							class="mt-1 flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-slate-200 text-slate-600">
							<User class="h-4 w-4" />
						</div>
					{/if}
				</div>
			{/each}

			{#if loading}
				<div class="flex items-center gap-2.5">
					<div class="flex h-7 w-7 items-center justify-center rounded-full bg-blue-100 text-blue-600">
						<Bot class="h-4 w-4" />
					</div>
					<div class="rounded-2xl rounded-bl-xs border border-slate-200 bg-white px-4 py-2 text-slate-400">
						<Loader2 class="h-4 w-4 animate-spin text-blue-600" />
					</div>
				</div>
			{/if}
		</div>

		<!-- Input Footer -->
		<div class="border-t border-slate-200 bg-white p-3">
			<form
				onsubmit={(e) => {
					e.preventDefault();
					handleSend();
				}}
				class="flex items-center gap-2">
				<input
					type="text"
					bind:value={inputQuery}
					onkeydown={handleKeyDown}
					placeholder="Hỏi trợ lý AI..."
					disabled={loading}
					class="flex-1 rounded-lg border border-slate-200 px-3 py-2 text-sm transition-colors outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 disabled:bg-slate-50" />
				<button
					type="submit"
					disabled={!inputQuery.trim() || loading}
					class="flex h-9 w-9 items-center justify-center rounded-lg bg-blue-600 text-white transition-all hover:bg-blue-700 disabled:opacity-50">
					<Send class="h-4 w-4" />
				</button>
			</form>
		</div>
	</div>
{/if}

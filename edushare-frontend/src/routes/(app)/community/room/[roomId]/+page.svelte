<script lang="ts">
	import { page } from '$app/state';
	import { stomp } from '$lib/configs/stomp.svelte.js';
	import type { ChatMessage } from '$lib/generated/types.js';
	import { RoomMessageState } from '$lib/hooks/community/roomMessageState.svelte.js';
	import type { UserBaseProjection } from '$lib/types/user';
	import { formatTime } from '$lib/utils/time';
	import { ChevronLeft, Bell, Info, Send, CornerDownRight, Loader2, Reply, X } from 'lucide-svelte';
	import { onDestroy, onMount } from 'svelte';
	let { data } = $props();
	let roomId = $derived(page.params.roomId ?? '');
	let currentUser = $derived(data.user);
	let roomState = new RoomMessageState(roomId);
	let displayMessages = $derived([...roomState.items].reverse());
	let roomDest = `/topic/room-${roomId}`;
	const ackDest = '/user/queue/ack';

	function onRoomMessage(data: any) {
		console.log('nhận broadcast:', data.body);
		const message = JSON.parse(data.body);
		console.log('userId type:', typeof message.userId, message.userId);
		roomState.addIncomingMessage(message);
	}

	function onAck(data: any) {
		const ack = JSON.parse(data.body);
		roomState.handleAck(ack);
	}

	let messageContent = $state('');

	onMount(() => {
		roomState.loadMessages();

		stomp.subscribe(ackDest, onAck);
		stomp.subscribe(roomDest, onRoomMessage);
	});

	onDestroy(() => {
		stomp.unsubscribe(ackDest);
		stomp.unsubscribe(roomDest);
	});

	function handleSendMessage() {
		if (!messageContent.trim()) return;
		roomState.sendMessageViaStomp(stomp, messageContent, currentUser);
		messageContent = '';
	}

	function handleKeyDown(e: KeyboardEvent) {
		if (e.key === 'Enter' && !e.shiftKey) {
			e.preventDefault();
			handleSendMessage();
		}
	}

   let scrollContainer = $state<HTMLElement | null>(null);
   $effect(() => {
      if (displayMessages.length > 0 && scrollContainer) {
         requestAnimationFrame(() => {
            scrollContainer!.scrollTop = scrollContainer!.scrollHeight;
         });
      }
   });
</script>

<div class="-m-6 flex h-[calc(100vh-4rem)] flex-col bg-slate-50/50 text-xs text-slate-800">
	<!-- HEADER PHÒNG CHAT -->
	<header class="flex h-14 shrink-0 items-center justify-between border-b border-slate-200 bg-white px-4 shadow-sm">
		<div class="flex items-center gap-3">
			<a
				href="/community"
				class="flex h-8 w-8 items-center justify-center rounded-lg border border-slate-200 text-slate-500 hover:bg-slate-100 hover:text-slate-900"
				title="Quay lại">
				<ChevronLeft class="h-4 w-4" />
			</a>

			<div>
				<div class="flex items-center gap-2">
					<h1 class="text-sm font-bold text-slate-900">Phòng chung</h1>
					<span class="rounded bg-slate-100 px-1.5 py-0.5 text-[10px] text-slate-500">#{roomId}</span>
				</div>
				<p class="text-[11px] text-slate-500">Trò chuyện chung, làm quen và động viên nhau học TOEIC mỗi ngày.</p>
			</div>
		</div>

		<div class="flex items-center gap-1">
			<button
				class="flex h-8 w-8 items-center justify-center rounded-lg text-slate-500 hover:bg-slate-100 hover:text-slate-900">
				<Bell class="h-4 w-4" />
			</button>
			<button
				class="flex h-8 w-8 items-center justify-center rounded-lg text-slate-500 hover:bg-slate-100 hover:text-slate-900">
				<Info class="h-4 w-4" />
			</button>
		</div>
	</header>

	<div bind:this={scrollContainer} class="flex-1 space-y-4 overflow-y-auto p-4 ">
		{#if roomState.hasMore}
			<div class="flex justify-center pb-2">
				<button
					onclick={() => roomState.loadMore()}
					disabled={roomState.loading}
					class="rounded-md border border-slate-200 bg-white px-3 py-1 text-[11px] text-slate-600 shadow-sm hover:bg-slate-50 disabled:opacity-50">
					{#if roomState.loading}
						<Loader2 class="mr-1 inline h-3 w-3 animate-spin" /> Đang tải...
					{:else}
						Tải tin nhắn cũ hơn
					{/if}
				</button>
			</div>
		{/if}

		{#if roomState.loading && displayMessages.length === 0}
			<div class="flex h-full items-center justify-center text-slate-400">
				<Loader2 class="mr-2 h-4 w-4 animate-spin" /> Đang tải tin nhắn...
			</div>
		{:else if roomState.error}
			<div class="rounded-lg border border-red-200 bg-red-50 p-3 text-center text-red-600">
				{roomState.error}
			</div>
		{:else if displayMessages.length === 0}
			<div class="flex h-full items-center justify-center text-slate-400">Chưa có tin nhắn nào trong phòng này.</div>
		{:else}
			{#each displayMessages as msg (msg.id)}
				{@const isMe = String(msg.userId) === String(currentUser?.id)}
				<div class={`group flex items-start gap-3 ${isMe ? 'flex-row-reverse' : ''}`}>
					{#if msg.userAvatarUrl}
						<img src={msg.userAvatarUrl} alt={msg.userName} class="h-8 w-8 shrink-0 rounded-full object-cover" />
					{:else}
						<div
							class="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-slate-500 text-[11px] font-bold text-white">
							{msg.userName?.charAt(0).toUpperCase() ?? '?'}
						</div>
					{/if}

					<div class={`max-w-[80%] space-y-1 ${isMe ? 'flex flex-col items-end' : ''}`}>
						<!-- Tên người gửi & Thời gian -->
						<div class="flex items-center gap-2">
							<span class="font-bold text-slate-900">{msg.userName}</span>
							<span class="text-[10px] text-slate-400">{formatTime(msg.createdAt)}</span>
						</div>

						<!-- Nội dung tin nhắn -->
						<div class="space-y-1">
							{#if msg.replyToMessageId}
								<div
									class="flex items-center gap-1.5 rounded-lg border border-slate-200 bg-slate-100/70 px-2.5 py-1 text-[11px] text-slate-600">
									<CornerDownRight class="h-3 w-3 shrink-0 text-slate-400" />
									<span class="font-semibold text-slate-800">{msg.replyToUserName || 'Người dùng'}:</span>
									<span class="truncate">{msg.replyToContentPreview}</span>
								</div>
							{/if}

							<div class={`relative flex items-center gap-2 ${isMe ? 'flex-row-reverse' : ''}`}>
								<div
									class={`inline-block rounded-2xl border px-3.5 py-2 leading-relaxed shadow-sm transition-opacity ${
										isMe ? 'border-emerald-600 bg-emerald-600 text-white' : 'border-slate-200 bg-white text-slate-800'
									} ${msg.status === 'SENDING' ? 'opacity-60' : ''} ${msg.status === 'PENDING' ? 'opacity-80' : ''} ${
										msg.status === 'FAILED' ? 'border-red-400 bg-red-50 text-red-600' : ''
									}`}>
									{msg.content}
								</div>

								{#if isMe}
									{#if msg.status === 'SENDING'}
										<span class="text-[10px] font-medium text-slate-400 italic select-none">Đang gửi...</span>
									{:else if msg.status === 'PENDING'}
										<span class="text-[10px] font-medium text-amber-500 italic select-none">Đang xử lý...</span>
									{:else if msg.status === 'FAILED'}
										<div class="flex items-center gap-1">
											<span class="text-[10px] font-medium text-red-500 select-none">Gửi thất bại</span>
											<button
												// onclick={() => roomState.retrySendMessage?.(stomp, msg)}
												class="text-[10px] font-semibold text-red-600 underline hover:text-red-800"
												title="Gửi lại tin nhắn">
												Thử lại
											</button>
										</div>
									{/if}
								{/if}

								<!-- Action Bar (Hiện khi hover vào group) -->
								<div
									class="flex items-center gap-0.5 rounded-full border border-slate-200 bg-white p-1 opacity-0 shadow-sm transition-opacity duration-150 group-hover:opacity-100">
									<button
										onclick={() => roomState.setReplyTarget(msg)}
										class="flex h-6 w-6 items-center justify-center rounded-full text-slate-500 hover:bg-slate-100 hover:text-slate-800"
										title="Trả lời">
										<Reply class="h-3 w-3" />
									</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			{/each}
		{/if}
	</div>

	<!-- Ô KHUNG NHẬP TIN NHẮN -->
	<footer class="shrink-0 border-t border-slate-200 bg-white p-3">
		{#if roomState.replyingMessage}
			<div class="flex items-center justify-between rounded-lg border border-slate-200 bg-slate-50 px-3 py-1.5 text-xs">
				<div class="flex items-center gap-2 overflow-hidden">
					<CornerDownRight class="h-3.5 w-3.5 shrink-0 text-emerald-600" />
					<div class="truncate">
						<span class="font-semibold text-slate-700">Đang trả lời {roomState.replyingMessage.userName}:</span>
						<span class="ml-1 truncate text-slate-500">{roomState.replyingMessage.content}</span>
					</div>
				</div>
				<button
					onclick={() => roomState.cancelReply()}
					class="rounded p-0.5 text-slate-400 hover:bg-slate-200 hover:text-slate-700">
					<X class="h-3.5 w-3.5" />
				</button>
			</div>
		{/if}
		<div
			class="relative flex items-center rounded-xl border border-slate-200 bg-slate-50 px-3 py-1.5 focus-within:border-emerald-500 focus-within:bg-white focus-within:ring-1 focus-within:ring-emerald-500">
			<textarea
				bind:value={messageContent}
				onkeydown={handleKeyDown}
				rows="1"
				placeholder="Nhập tin nhắn — Enter để gửi, Shift+Enter xuống dòng"
				class="max-h-24 min-h-[36px] w-full resize-none bg-transparent py-2 text-xs text-slate-800 placeholder-slate-400 focus:outline-none">
			</textarea>

			<div class="flex items-center gap-2 pl-2">
				<span class="text-[10px] text-slate-400">
					{messageContent.length}/2000
				</span>
				<button
					onclick={handleSendMessage}
					disabled={!messageContent.trim()}
					class="flex h-8 w-8 items-center justify-center rounded-lg bg-emerald-600 text-white transition-all hover:bg-emerald-700 disabled:opacity-40 disabled:hover:bg-emerald-600">
					<Send class="h-3.5 w-3.5" />
				</button>
			</div>
		</div>
	</footer>
</div>

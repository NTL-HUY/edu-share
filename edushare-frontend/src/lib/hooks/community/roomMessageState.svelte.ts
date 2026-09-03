import type { ChatMessage, CursorPagingResponse } from '$lib/generated/types';
import { getClientSdk } from '$lib/graphql/client';
import type { ChatAckMessage, ChatMessageUI } from '$lib/types/chat.types';
import type { UserBaseProjection } from '$lib/types/user';
import type { ApiResponse } from '$lib/utils/apiResponse';
import { toast } from 'svelte-sonner';

export class RoomMessageState {
	items = $state<ChatMessageUI[]>([]);
	loading = $state(false);
	error = $state<string | null>(null);
	beforeId = $state<string | null>(null);
	hasMore = $state(true);
	roomId = $state('');

	replyingMessage = $state<ChatMessage | null>(null);

	constructor(roomId: string) {
		this.roomId = roomId;
	}

	setReplyTarget(msg: ChatMessage) {
		this.replyingMessage = msg;
	}

	cancelReply() {
		this.replyingMessage = null;
	}

	async loadMessages(beforeId: string | null = null) {
		if (this.loading) return;

		this.loading = true;
		this.error = null;

		try {
			const params = new URLSearchParams({ limit: '20' });
			if (beforeId) params.set('beforeId', beforeId);

			const res = await fetch(`/api/rooms/${this.roomId}/messages?${params}`);
			const data: ApiResponse<CursorPagingResponse> = await res.json();

			if (!data.success || !data.data) {
				throw new Error(data.message || 'Không thể lấy dữ liệu tin nhắn');
			}

			const newItems: ChatMessageUI[] = (data.data.items ?? []).map((item) => ({
				...item,
				status: 'SENT'
			}));

			this.items = beforeId === null ? newItems : [...this.items, ...newItems];

			this.beforeId = data.data.beforeId ?? null;
			this.hasMore = data.data.hasMore;
		} catch (err) {
			this.error = err instanceof Error ? err.message : 'Không thể tải tin nhắn';
		} finally {
			this.loading = false;
		}
	}

	async loadMore() {
		if (!this.hasMore || this.loading) return;
		await this.loadMessages(this.beforeId);
	}

	async refresh() {
		await this.loadMessages(null);
	}

	// async sendMessage(content: string) {
	// 	if (!content.trim()) return;
	// 	toast.info(`gui`);
	// 	console.log('room', this.roomId, 'content', content, 'reply', this.replyingMessage?.id);

	// 	// const replyToId = this.replyingMessage?.id;

	// 	// try {
	// 	//    const sdk = getClientSdk();
	// 	//    const data = await sdk.SendRoomMessage({
	// 	//       roomId: this.roomId,
	// 	//       content,
	// 	//       replyToMessageId: replyToId ?? null // Truyền id reply nếu có
	// 	//    });

	// 	//    if (data.sendMessage) {
	// 	//       // Thêm tin nhắn mới vào danh sách
	// 	//       this.items = [data.sendMessage, ...this.items];
	// 	//    }

	// 	//    // Gửi xong tự động hủy trạng thái reply
	// 	//    this.cancelReply();
	// 	// } catch (err) {
	// 	//    this.error = err instanceof Error ? err.message : 'Gửi tin nhắn thất bại';
	// 	//    throw err;
	// 	// }
	// }

	addIncomingMessage(message: ChatMessage) {
		const idx = message.clientTempId
			? this.items.findIndex((i) => i.clientTempId === message.clientTempId && i.status !== 'SENT')
			: -1;

		if (idx !== -1) {
			this.items[idx] = { ...message, status: 'SENT' };
			this.items = [...this.items];
			return;
		}

		const exists = this.items.some((item) => item.id === message.id);
		if (!exists) {
			this.items = [{ ...message, status: 'SENT' }, ...this.items];
		}
	}

	sendMessageViaStomp(stompClient: any, content: string, currentUser: UserBaseProjection) {
		if (!content.trim()) return;

		let clientTempId = crypto.randomUUID();
		const replyToId = this.replyingMessage?.id ?? null;

		const tempMessage: ChatMessageUI = {
			id: clientTempId,
			clientTempId: clientTempId,
			roomId: this.roomId,
			userId: String(currentUser.id),
			userName: currentUser.username,
			userAvatarUrl: currentUser.avatarUrl,
			content,
			replyToMessageId: replyToId,
			replyToUserName: this.replyingMessage?.userName ?? null,
			replyToContentPreview: this.replyingMessage?.content ?? null,
			createdAt: new Date().toISOString(),
			status: 'SENDING'
		};

		this.items = [tempMessage, ...this.items];

		setTimeout(() => {
			stompClient.publish('/app/chat.send', {
				clientTempId: clientTempId,
				roomId: this.roomId,
				content,
				replyToMessageId: replyToId
			});
		}, 5000);

		this.cancelReply();
	}

	handleAck(ack: ChatAckMessage) {
		const idx = this.items.findIndex((i) => i.clientTempId === ack.clientTempId);
		if (idx === -1) return;

		if (ack.status === 'FAILED') {
			this.items[idx] = { ...this.items[idx], status: 'FAILED' };
			this.items = [...this.items];
		}
		if (ack.status === 'PENDING') {
			this.items[idx] = { ...this.items[idx], status: 'PENDING' };
			this.items = [...this.items];
		}
	}

	reset() {
		this.items = [];
		this.loading = false;
		this.error = null;
		this.beforeId = null;
		this.hasMore = true;
	}
}

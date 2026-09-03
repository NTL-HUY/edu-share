import type { ChatMessage as GeneratedChatMessage } from '$lib/generated/types';

export type AckStatus = 'PENDING' | 'FAILED' | 'SENT';


export interface ChatAckMessage {
   type: 'ACK';
   clientTempId: string;
   status: AckStatus;
   reason: string;
}

// Mở rộng ChatMessage để thêm các trường tạm phục vụ UI
export interface ChatMessageUI extends GeneratedChatMessage {
   clientTempId: string;
   status?: 'SENDING' | 'PENDING' | 'FAILED' | 'SENT';
}
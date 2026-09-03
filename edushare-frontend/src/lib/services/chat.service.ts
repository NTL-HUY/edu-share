import { API_ENDPOINTS } from "$lib/configs/api";
import type { ModelResponse } from "$lib/types/chatbot.types";
import { apiRequest } from "./base.service";



export const chatService = {
	chatbot: (fetchFn: typeof fetch, payload : {query: string | number}) =>
		apiRequest<ModelResponse>(fetchFn, API_ENDPOINTS.CHATBOT.CHAT, {
			method: 'POST',
			body: payload
		})
};

import { API_ENDPOINTS } from '$lib/configs/api';
import type { CreateCommentRequest } from '$lib/types/interaction';
import { apiRequest } from './base.service';

export type VoteRequest = {
	value: number;
};

export const interactionService = {
	vote: (fetchFn: typeof fetch, feedId: string, payload: VoteRequest) =>
		apiRequest<void>(fetchFn, API_ENDPOINTS.INTERACTION.VOTE(feedId), {
			method: 'PUT',
			body: payload
		}),
	unvote: (fetchFn: typeof fetch, feedId: string) =>
		apiRequest<void>(fetchFn, API_ENDPOINTS.INTERACTION.UNVOTE(feedId), {
			method: 'DELETE'
		}),
	createComment: (fetchFn: typeof fetch, feedId: string, payload: CreateCommentRequest) =>
		apiRequest<void>(fetchFn, API_ENDPOINTS.INTERACTION.CREATE_COMMENT(feedId), {
			method: 'POST',
			body: payload
		}),

	follow: (fetchFn: typeof fetch, username: string) =>
		apiRequest<void>(fetchFn, API_ENDPOINTS.USER.FOLLOW(username), {
			method: 'POST'
		}),

	unfollow: (fetchFn: typeof fetch, username: string) =>
		apiRequest<void>(fetchFn, API_ENDPOINTS.USER.UNFOLLOW(username), {
			method: 'DELETE'
		})
};

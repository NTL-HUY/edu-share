// src/routes/(app)/profile/_components/followerState.svelte.ts
import { PUBLIC_API_BASE_URL } from '$env/static/public';
import { API_ENDPOINTS } from '$lib/configs/api';

export interface Follower {
	id: number;
	username: string;
	fullName: string;
	avatarUrl: string | null;
}

export interface PageResponse<T> {
	content: T[];
	pageable: {
		pageNumber: number;
		pageSize: number;
		sort: {
			empty: boolean;
			sorted: boolean;
			unsorted: boolean;
		};
		offset: number;
		paged: boolean;
		unpaged: boolean;
	};
	last: boolean;
	totalPages: number;
	totalElements: number;
	size: number;
	number: number;
	sort: {
		empty: boolean;
		sorted: boolean;
		unsorted: boolean;
	};
	numberOfElements: number;
	first: boolean;
	empty: boolean;
}

export class FollowerState {
	items = $state<Follower[]>([]);
	loading = $state(false);
	error = $state<string | null>(null);
	page = $state(0);
	totalPages = $state(0);
	totalElements = $state(0);
	hasMore = $state(true);
	username = $state('');
	type: 'followers' | 'following' = 'followers';

	constructor(username: string, type: 'followers' | 'following' = 'followers') {
		this.username = username;
		this.type = type;
	}

	private getEndpoint(): string {
		if (this.type === 'followers') {
			return API_ENDPOINTS.USER.FOLLOWERS(this.username);
		} else {
			return API_ENDPOINTS.USER.FOLLOWING(this.username);
		}
	}

	async loadFollowers(page: number = 0, size: number = 20) {
		if (this.loading) return;

		this.loading = true;
		this.error = null;

		try {
			const url = `${this.getEndpoint()}?size=${size}&sort=createdAt,desc&page=${page}`;
			const response = await fetch(url, {
				headers: {
					'Content-Type': 'application/json'
				}
			});

			if (!response.ok) {
				throw new Error(`Failed to load followers: ${response.statusText}`);
			}

			const data: PageResponse<Follower> = await response.json();

			// Nếu page = 0 thì reset, không thì append
			if (page === 0) {
				this.items = data.content;
			} else {
				this.items = [...this.items, ...data.content];
			}

			this.page = data.number;
			this.totalPages = data.totalPages;
			this.totalElements = data.totalElements;
			this.hasMore = !data.last;
		} catch (err) {
			this.error = err instanceof Error ? err.message : 'Unknown error occurred';
			console.error('Error loading followers:', err);
		} finally {
			this.loading = false;
		}
	}

	// Load more (next page)
	async loadMore() {
		if (!this.hasMore || this.loading) return;
		await this.loadFollowers(this.page + 1);
	}

	// Refresh
	async refresh() {
		await this.loadFollowers(0);
	}

	// Reset state
	reset() {
		this.items = [];
		this.loading = false;
		this.error = null;
		this.page = 0;
		this.totalPages = 0;
		this.totalElements = 0;
		this.hasMore = true;
	}
}

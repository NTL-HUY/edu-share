import { type GetMyKnowledgeListQuery, type KnowledgeListByUsernameQuery } from '$lib/generated/types';
import { getClientSdk } from '$lib/graphql/client';
import { toast } from 'svelte-sonner';

type KnowledgePagePayload =
	GetMyKnowledgeListQuery['myKnowledgeList'] | KnowledgeListByUsernameQuery['knowledgeListByUsername'];

async function fetchPage(userProfile: any, page: number): Promise<KnowledgePagePayload> {
	const sdk = getClientSdk();

	try {
		let data;

		if (!userProfile.isMe) {
			data = await sdk.KnowledgeListByUsername({
				username: userProfile.username,
				input: {
					number: page,
					size: 10
				}
			});
			return data.knowledgeListByUsername;
		} else {
			data = await sdk.GetMyKnowledgeList({
				input: {
					number: page,
					size: 10
				}
			});
			return data.myKnowledgeList;
		}
	} catch (err: any) {
		const graphQLError = err?.response?.errors?.[0];
		const classification = graphQLError?.extensions?.classification;

		let errorMessage = 'Không thể tải dữ liệu, vui lòng thử lại';

		if (classification === 'ValidationError') {
			errorMessage = 'Dữ liệu gửi lên không hợp lệ';
		} else if (graphQLError?.message) {
			// fallback: log kỹ thuật, không show hết ra UI nếu không cần
			errorMessage = 'Đã có lỗi xảy ra, vui lòng thử lại sau';
		} else if (err?.message) {
			errorMessage = err.message;
		}

		console.error('Lỗi khi tải replies:', err); // log full lỗi kỹ thuật cho dev
		toast.error(errorMessage); // chỉ show message thân thiện
		throw err;
	}
}
export class KnowledgeListState {
	items = $state<KnowledgePagePayload['content']>([]);
	page = $state(0);
	totalPages = $state(0);
	totalElements = $state(0);
	loading = $state(false);
	error = $state<string | null>(null);
	userProfile = $state(null);
	hasMore = $derived(this.page + 1 < this.totalPages);

	constructor(profile : any) {
		this.userProfile = profile;
	}

	async load(reset = false) {
		this.loading = true;
		this.error = null;
		const targetPage = reset ? 0 : this.page;
		try {
			const result = await fetchPage(this.userProfile, targetPage);
			this.items = reset ? result.content : [...this.items, ...result.content];
			this.page = result.number;
			this.totalPages = result.totalPages;
			this.totalElements = result.totalElements;
		} catch (e) {
			this.error = e instanceof Error ? e.message : 'Lỗi tải dữ liệu';
		} finally {
			this.loading = false;
		}
	}

	loadMore = () => {
		if (!this.hasMore || this.loading) return;
		this.page += 1;
		this.load();
	};

	removeItem(id: string) {
		this.items = this.items.filter((i) => i.id !== id);
		this.totalElements -= 1;
	}

	updateItem(id: string, patch: Partial<KnowledgePagePayload['content'][number]>) {
		this.items = this.items.map((i) => (i.id === id ? { ...i, ...patch } : i));
	}
}

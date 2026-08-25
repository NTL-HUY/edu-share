<script lang="ts">
	import {
		BookOpen,
		HelpCircle,
		ThumbsUp,
		ThumbsDown,
		Eye,
		Pencil,
		Trash2,
		Globe,
		Lock,
		CheckCircle2,
		Search,
		GraduationCap,
		UserCheck,
		UserPlus,
		Sparkles,
		MessageSquare,
		Bookmark,
		Settings,
		Activity,
		ShieldCheck
	} from 'lucide-svelte';
	import ProfileHeader from './_components/ProfileHeader.svelte';
	import ActivitySidebar from './_components/ActivitySidebar.svelte';
	import { sidebarState } from './_components/activitySidebar.svelte.ts';
	import EditProfileModal from './_components/EditProfileModal.svelte';
	import type { PageData } from './$types';
	import { headerState } from './_components/profile.svelte.ts';
	import KnowledgeListSection from './_components/KnowledgeListSection.svelte';

  let { data }: { data: PageData; children: any } = $props();

	let isEditModalOpen = $state(false);

	$effect(() => {
		if (data.profile) {
			headerState.currentUser = data.profile;
		}
	});

	// ==================== STATE MANAGEMENT (Svelte 5 Runes) ====================
	type MainTab = 'profile' | 'activity' | 'saves' | 'settings';
	let activeTab = $state<MainTab>('activity');
	let activeSubTab = sidebarState.activeSubTab;
	let searchQuery = $state('');

	// 1. Mock Data: User & Profile (`users` + `profiles` tables)
	let currentUser = $state({
		id: '1',
		username: 'nguyenvana',
		fullName: 'Nguyễn Văn A',
		email: 'nguyenvana@hcmut.edu.vn',
		avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix',
		isFamous: true,
		userRole: 'ADMIN',
		// Table profiles
		studentId: '2012345',
		university: 'ĐH Bách Khoa TP.HCM',
		faculty: 'Khoa KH&KT Máy tính',
		major: 'Công nghệ Phần mềm',
		className: 'CC01',
		academicYear: '2022-2026',
		cpa: 3.65,
		bio: 'Chuyên về Spring Boot, GraphQL & AI RAG System. Đang thực hiện đồ án tốt nghiệp.',
		createdAt: '2026-08-01T00:00:00Z'
	});

	// 2. Mock Data: Knowledge (`knowledge`, `lesson`, `question` tables)
	let items = $state([
		{
			id: '1',
			type: 'LESSON',
			title: 'Hướng dẫn tích hợp GraphQL với Spring Boot & Svelte 5',
			abstractText: 'Bài viết chi tiết về cách dựng kiến trúc GraphQL backend và frontend...',
			isPublic: true,
			voteScore: 42,
			viewsCount: 1250,
			commentCount: 18,
			createdAt: '2026-08-20T10:00:00Z',
			category: { name: 'Spring Boot' }
		},
		{
			id: '2',
			type: 'QUESTION',
			title: 'Xử lý lỗi Type Mismatch trong GraphQL Spring Boot như thế nào?',
			abstractText: 'Mình gặp lỗi khi parse enum giữa Schema và Java Entity...',
			isPublic: true,
			isResolved: true,
			voteScore: 5,
			viewsCount: 340,
			commentCount: 4,
			createdAt: '2026-08-18T14:30:00Z',
			category: { name: 'GraphQL' }
		}
	]);

	// 3. Mock Data: Comments (`comment` table)
	let comments = $state([
		{
			id: '101',
			knowledgeId: '1',
			knowledgeTitle: 'Hướng dẫn tích hợp GraphQL với Spring Boot & Svelte 5',
			content: 'Bài viết rất rõ ràng, cho mình hỏi thêm phần cache query với Redis?',
			replyToUserName: null,
			createdAt: '2026-08-21T08:00:00Z'
		},
		{
			id: '102',
			knowledgeId: '2',
			knowledgeTitle: 'Xử lý lỗi Type Mismatch trong GraphQL Spring Boot',
			content: 'Bạn kiểm tra lại Coercing trong Custom Scalar xem sao.',
			replyToUserName: 'tranvanb',
			createdAt: '2026-08-19T11:20:00Z'
		}
	]);

	// 4. Mock Data: Votes (`vote` table)
	let votedItems = $state([
		{
			id: '10',
			title: 'Tối ưu Vector Search với pgvector trong PostgreSQL',
			type: 'LESSON',
			value: 1,
			createdAt: '2026-08-22T09:00:00Z'
		},
		{
			id: '11',
			title: 'Tại sao không nên dùng MySQL cho RAG System?',
			type: 'QUESTION',
			value: -1,
			createdAt: '2026-08-17T15:10:00Z'
		}
	]);

	// 5. Mock Data: Follows (`follows` table)
	let followers = $state([
		{
			id: '2',
			username: 'tranvanb',
			fullName: 'Trần Văn B',
			avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=John'
		},
		{
			id: '3',
			username: 'lethic',
			fullName: 'Lê Thị C',
			avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Jane'
		}
	]);

	let following = $state([
		{
			id: '4',
			username: 'dev_master',
			fullName: 'Senior Spring Dev',
			avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Master'
		}
	]);

	// 6. Mock Data: AI Chunks (`knowledge_chunk` table)
	let aiChunks = $state([
		{
			id: '1',
			knowledgeId: '1',
			chunkIndex: 0,
			content: 'Hướng dẫn tích hợp GraphQL với Spring Boot...',
			dimensions: 768,
			isPublic: true
		},
		{
			id: '2',
			knowledgeId: '1',
			chunkIndex: 1,
			content: 'Cấu hình GraphQLClient trong SvelteKit...',
			dimensions: 768,
			isPublic: true
		}
	]);

	// Computed State Filter
	let filteredItems = $derived(
		items.filter((item) => {
			const matchType = activeSubTab === 'lessons' ? item.type === 'LESSON' : item.type === 'QUESTION';
			return matchType && item.title.toLowerCase().includes(searchQuery.toLowerCase());
		})
	);

	let totalReputation = $derived(items.reduce((acc, curr) => acc + curr.voteScore * 10, 0));

	function toggleVisibility(id: string) {
		const item = items.find((i) => i.id === id);
		if (item) item.isPublic = !item.isPublic;
	}

	function deleteItem(id: string) {
		if (confirm('Bạn có chắc chắn muốn xóa bài này?')) {
			items = items.filter((i) => i.id !== id);
		}
	}
</script>

<div class=" space-y-6 text-slate-800">
	<!-- 1. HEADER PROFILE (Gồm User + Profile Student Info) -->
	<ProfileHeader profile={data.profile} onEditProfile={() => (isEditModalOpen = true)} />
	<!-- 3. TAB ACTIVITY CONTENT -->
	{#if activeTab === 'activity'}
		<div class="flex flex-col gap-6 sm:flex-row">
			<!-- SUB-SIDEBAR BÊN TRÁI -->
			<ActivitySidebar />

			<!-- KHU VỰC HIỂN THỊ NỘI DUNG (BÊN PHẢI) -->
			<div class="min-w-0 flex-1 space-y-4">
				<!-- SUB-TAB: SUMMARY -->
				{#if sidebarState.activeSubTab === 'summary'}
					<div class="grid grid-cols-3 gap-4">
						<div class="rounded-lg border border-slate-200 bg-slate-50/50 p-4 text-center">
							<span class="block text-2xl font-bold text-slate-800">{items.length}</span>
							<span class="text-xs text-slate-500">Bài viết & Câu hỏi</span>
						</div>
						<div class="rounded-lg border border-slate-200 bg-slate-50/50 p-4 text-center">
							<span class="block text-2xl font-bold text-slate-800">{followers.length}</span>
							<span class="text-xs text-slate-500">Người theo dõi</span>
						</div>
						<div class="rounded-lg border border-slate-200 bg-slate-50/50 p-4 text-center">
							<span class="block text-2xl font-bold text-slate-800">{comments.length}</span>
							<span class="text-xs text-slate-500">Bình luận</span>
						</div>
					</div>

					<div class="space-y-2 pt-2">
						<h3 class="text-sm font-semibold text-slate-700">Nội dung đóng góp nổi bật</h3>
						<div class="divide-y divide-slate-100 rounded-md border border-slate-200">
							{#each items as item}
								<div class="flex items-center justify-between p-3 text-xs">
									<a href="/feed/{item.id}" class="font-medium text-blue-600 hover:underline">{item.title}</a>
									<span class="font-bold text-slate-600">+{item.voteScore} votes</span>
								</div>
							{/each}
						</div>
					</div>
				{/if}

				<!-- SUB-TAB: LESSONS / QUESTIONS -->
				{#if sidebarState.activeSubTab === 'knowledge'}
					<KnowledgeListSection/>
				{/if}

				<!-- SUB-TAB: COMMENTS (`comment` Table) -->
				{#if sidebarState.activeSubTab === 'comments'}
					<h2 class="text-lg font-normal text-slate-800">Bình luận & Phản hồi đã viết</h2>
					<div class="divide-y divide-slate-100 rounded-md border border-slate-200 bg-white">
						{#each comments as c}
							<div class="space-y-1 p-3 text-xs">
								<div class="flex items-center gap-2 text-slate-500">
									<span>{c.replyToUserName ? `Reply tới @${c.replyToUserName}` : 'Bình luận bài:'}</span>
									<a href="/feed/{c.knowledgeId}" class="font-semibold text-blue-600 hover:underline">
										{c.knowledgeTitle}
									</a>
								</div>
								<p class="rounded border border-slate-100 bg-slate-50 p-2 text-slate-800">{c.content}</p>
							</div>
						{/each}
					</div>
				{/if}

				<!-- SUB-TAB: VOTES (`vote` Table) -->
				{#if sidebarState.activeSubTab === 'votes'}
					<h2 class="text-lg font-normal text-slate-800">Lịch sử Vote</h2>
					<div class="divide-y divide-slate-100 rounded-md border border-slate-200 bg-white">
						{#each votedItems as v}
							<div class="flex items-center justify-between p-3 text-xs">
								<div class="flex items-center gap-2">
									{#if v.value === 1}
										<ThumbsUp class="h-4 w-4 text-emerald-600" />
									{:else}
										<ThumbsDown class="h-4 w-4 text-rose-500" />
									{/if}
									<a href="/feed/{v.id}" class="font-medium text-slate-800 hover:text-blue-600">{v.title}</a>
								</div>
								<span class="text-slate-400">{new Date(v.createdAt).toLocaleDateString('vi-VN')}</span>
							</div>
						{/each}
					</div>
				{/if}

				<!-- SUB-TAB: FOLLOWERS & FOLLOWING (`follows` Table) -->
				{#if sidebarState.activeSubTab === 'followers' || sidebarState.activeSubTab === 'following'}
					<h2 class="text-lg font-normal text-slate-800">
						{sidebarState.activeSubTab === 'followers' ? 'Người theo dõi (Followers)' : 'Đang theo dõi (Following)'}
					</h2>
					<div class="grid grid-cols-2 gap-3">
						{#each sidebarState.activeSubTab === 'followers' ? followers : following as f}
							<div class="flex items-center gap-3 rounded-lg border border-slate-200 bg-white p-3">
								<img src={f.avatarUrl} alt="Avatar" class="h-10 w-10 rounded-full bg-slate-100" />
								<div>
									<p class="text-xs font-semibold text-slate-800">{f.fullName}</p>
									<p class="text-[11px] text-slate-400">@{f.username}</p>
								</div>
							</div>
						{/each}
					</div>
				{/if}

				<!-- SUB-TAB: AI KNOWLEDGE CHUNKS (`knowledge_chunk` Table) -->
				{#if sidebarState.activeSubTab === 'ai_chunks'}
					<div class="flex items-center justify-between">
						<h2 class="text-lg font-normal text-slate-800">Quản lý Chunk & Embedding (RAG Service)</h2>
						<span class="font-mono text-xs text-purple-600">pgvector(768)</span>
					</div>
					<div class="divide-y divide-slate-100 rounded-md border border-slate-200 bg-white">
						{#each aiChunks as chunk}
							<div class="space-y-1 p-3 text-xs">
								<div class="flex items-center justify-between font-mono text-[11px] text-slate-500">
									<span>Chunk #{chunk.chunkIndex} (Knowledge ID: {chunk.knowledgeId})</span>
									<span class="rounded bg-emerald-100 px-1.5 py-0.5 text-emerald-700">Vector Synced</span>
								</div>
								<p class="truncate text-slate-700">{chunk.content}</p>
							</div>
						{/each}
					</div>
				{/if}
			</div>
		</div>
	{/if}
</div>
<!-- Render Modal -->
<EditProfileModal bind:isOpen={isEditModalOpen} onClose={() => (isEditModalOpen = false)} />

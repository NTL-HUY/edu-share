<script lang="ts">
	import { page } from '$app/state';
	import type { UserBaseProjection } from '$lib/types/user';
	import { House, Search, AlignHorizontalDistributeEndIcon, Home, SquarePlus, User, Users } from 'lucide-svelte';

	let { user }: { user: UserBaseProjection | null } = $props();

	let navItems = $derived([
		{ label: 'Dành cho bạn', path: '/', icon: Home },
		{ label: 'Tìm kiếm & Khám phá', path: '/search', icon: Search },
		...(user
			? [
					{ label: 'Tạo nội dung mới', path: '/feed/create', icon: SquarePlus },
					{ label: 'Trang cá nhân', path: `/profile/${user.username}`, icon: User }
				]
			: []),
		{ label: 'Cộng đồng', path: '/community', icon: Users }
	]);

	function isActive(path: string) {
		if (path === '/') {
			return page.url.pathname === '/';
		}
		return page.url.pathname.startsWith(path);
	}

	function linkClass(path: string) {
		const base = 'flex items-center gap-3 rounded-md px-3 py-2 text-xs font-medium hover:bg-gray-100 transition-colors';
		const active = isActive(path) ? 'bg-gray-100 font-bold text-black' : 'text-gray-600 hover:text-black';

		return `${base} ${active}`.trim();
	}
</script>

<!-- Phần HTML bị thiếu khiến Sidebar không hiển thị -->
<aside class="w-52 shrink-0">
	<nav class="flex h-full flex-col gap-1 border-r border-gray-200 bg-white p-2 text-gray-700">
		{#each navItems as item}
			{@const Icon = item.icon}
			<a href={item.path} class={linkClass(item.path)}>
				<Icon class="h-5 w-5 shrink-0" />
				<span>{item.label}</span>
			</a>
		{/each}
	</nav>
</aside>

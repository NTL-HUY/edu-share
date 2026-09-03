<script lang="ts">
	import { Search } from 'lucide-svelte';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { page } from '$app/state';
	import { goto, invalidateAll } from '$app/navigation';
	import { authService } from '$lib/services/auth.service';
	import { clearSessionCookies } from '$lib/auth/session';
	import { toast } from 'svelte-sonner';
	let username = $derived(page.data.user?.username || '');
	let urlQuery = $derived(page.url.searchParams.get('q') || '');

	let searchQuery = $state('');

	$effect(() => {
		searchQuery = urlQuery;
	});

	function handleSearch(e: SubmitEvent | KeyboardEvent) {
		e.preventDefault();
		const trimmed = searchQuery.trim();

		if (!trimmed) {
			// Nếu ô tìm kiếm trống mà bấm Enter -> Xóa param 'q' khỏi URL
			const newUrl = new URL(page.url);
			newUrl.searchParams.delete('q');
			goto(newUrl.toString());
			return;
		}

		// Ngược lại, đẩy query param mới lên URL
		const newUrl = new URL(page.url);
		newUrl.pathname = '/search';
		newUrl.searchParams.set('q', trimmed);
		goto(newUrl.toString());
	}

	async function handleLogout() {
		try {
			const res = await fetch('/api/logout', { method: 'POST' });
			const result = await res.json();

			if (result.success) {
				toast.success('Đăng xuất thành công!');
			} else {
				toast.error(result.message || 'Đăng xuất không hoàn tất trên server', { id: toastId });
			}
		} catch (error) {
			console.error('Lỗi kết nối khi đăng xuất:', error);
			toast.error('Lỗi kết nối tới máy chủ');
		} finally {
			await invalidateAll();
			goto('/');
		}
	}
</script>

<header class="sticky top-0 z-50 w-full border-b border-gray-200 bg-white p-2 shadow-xs">
	<!-- Thanh cam đặc trưng của Stack Overflow chạy dọc viền trên -->
	<div class="absolute top-0 right-0 left-0 h-[2px] bg-[#f48024]"></div>

	<div class="mx-auto flex h-12 max-w-[1265px] items-center gap-4 px-4">
		<!-- Logo -->
		<a href="/" class="flex shrink-0 items-center gap-2 py-1.5 focus:outline-hidden">
			<svg class="h-6 w-6" viewBox="0 0 32 37">
				<path fill="#BCBBBB" d="M26 33v-9h4v13H0V24h4v9h22Z" />
				<path
					fill="#F48024"
					d="m21.5 0-2.7 2 9.9 13.3 2.7-2L21.5 0ZM26 18.4l-12.2-6.5 1.8-3.4 12.2 6.5-1.8 3.4ZM22.7 22l-13.6-3.3.8-3.7 13.6 3.3-.8 3.7ZM20.6 26H6.8v-3.9h13.8V26Z" />
			</svg>
			<span class="text-[15px] font-normal tracking-tight whitespace-nowrap text-gray-900">
				Stack <strong class="font-bold">Overflow</strong>
			</span>
		</a>

		<!-- Navigation Links -->
		<nav class="hidden shrink-0 items-center gap-1 text-[13px] text-gray-600 md:flex">
			<a href="/about" class="rounded-full px-3 py-1.5 transition-colors hover:bg-gray-100 hover:text-gray-900">
				About
			</a>
			<a href="/products" class="rounded-full px-3 py-1.5 transition-colors hover:bg-gray-100 hover:text-gray-900">
				Products
			</a>
			<a href="/internal" class="rounded-full px-3 py-1.5 transition-colors hover:bg-gray-100 hover:text-gray-900">
				Stack Internal
			</a>
		</nav>

		<!-- Search Input (Giữa, cân đối) -->
		<form onsubmit={handleSearch} class="relative max-w-2xl flex-1">
			<Search class="pointer-events-none absolute top-1/2 left-3 h-4 w-4 -translate-y-1/2 text-gray-400" />
			<Input
				type="text"
				placeholder="Search..."
				bind:value={searchQuery}
				class="h-9 w-full rounded-md border-gray-300 bg-white pr-4 pl-9 text-xs text-gray-900 shadow-inner focus-visible:border-sky-500 focus-visible:ring-2 focus-visible:ring-sky-500/20" />
		</form>

		<!-- Actions (Phải) -->
		<div class="flex shrink-0 items-center gap-2">
			{#if page.data.user}
				<!-- Nếu người dùng đã đăng nhập, hiển thị thông tin người dùng -->
				<a href="/profile/{username}" class="flex items-center gap-2">
					<img src={page.data.user.avatarUrl} alt="User Avatar" class="h-6 w-6 rounded-full" />
					<span class="text-xs font-medium text-gray-900">
						{page.data.user.username}
					</span>
				</a>

				<!-- Nút Đăng xuất -->
				<Button
					variant="ghost"
					size="sm"
					class="h-8 rounded px-2 text-xs font-medium text-gray-600 hover:bg-gray-100 hover:text-red-600"
					onclick={handleLogout}>
					Đăng xuất
				</Button>
			{:else}
				<!-- Nếu người dùng chưa đăng nhập, hiển thị nút Đăng nhập và Đăng ký -->
				<a href="/login">
					<Button
						variant="outline"
						size="sm"
						class="h-8 rounded border-sky-600 bg-sky-50 px-3 text-xs font-medium text-sky-700 shadow-xs hover:bg-sky-100 hover:text-sky-800">
						Đăng nhập
					</Button>
				</a>

				<a href="/register">
					<Button
						size="sm"
						class="h-8 rounded bg-[#0a95ff] px-3 text-xs font-medium text-white shadow-xs hover:bg-[#0074cc]">
						Đăng ký
					</Button>
				</a>
			{/if}
		</div>
	</div>
</header>

<script>
	import { MessageCircle, ShieldAlert } from 'lucide-svelte';
</script>

<header
	class="flex flex-row items-center gap-6 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm md:justify-between">
	<div class="flex items-start gap-5">
		<div
			class="hidden h-10 w-10 items-center justify-center rounded-2xl border border-emerald-100 bg-emerald-50 text-emerald-600 shadow-sm sm:flex">
			<MessageCircle class="h-5 w-5" />
		</div>

		<div class="space-y-1.5">
			<h1 class="text-sm font-bold tracking-tight text-slate-900">Cùng nhau học, không học một mình</h1>
			<p class="max-w-xl text-xs text-slate-500">
				Hỏi nhanh, sửa câu, bàn câu khó — tất cả ở một nơi an toàn dành cho người học.
			</p>
		</div>
	</div>

	<!-- Action Buttons -->
	<div class="flex shrink-0 flex-wrap items-center gap-3">
		<button
			class="inline-flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-xs font-semibold text-slate-700 shadow-sm transition-all hover:bg-slate-50 hover:text-slate-900 active:scale-[0.98]">
			<ShieldAlert class="h-4 w-4 text-slate-500" />
			<span>Xem quy tắc</span>
		</button>
	</div>
</header>

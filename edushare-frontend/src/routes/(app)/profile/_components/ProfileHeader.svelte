<script lang="ts">
	import { ShieldCheck, GraduationCap, Pencil } from 'lucide-svelte';
	import { headerState, type UserProfile } from './profile.svelte';

	interface Props {
		profile: UserProfile | null;
		onEditProfile?: () => void;
	}

	let { profile: user, onEditProfile }: Props = $props();
</script>

{#if user}
	<div class="flex flex-col gap-4 border-b border-slate-200 pb-4 sm:flex-row sm:items-start sm:justify-between">
		<div class="flex gap-4">
			<div class="relative">
				<img
					src={user.avatarUrl}
					alt="Avatar"
					class="h-20 w-20 rounded-xl border border-slate-200 bg-slate-50 object-cover sm:h-24 sm:w-24" />
			</div>

			<div class="space-y-1">
				<div class="flex items-center gap-2">
					<h1 class="text-2xl font-bold text-slate-900">{user.fullName}</h1>
					{#if user.userRole === 'ADMIN'}
						<span
							class="inline-flex items-center gap-1 rounded bg-slate-800 px-2 py-0.5 text-[10px] font-semibold text-white">
							<ShieldCheck class="h-3 w-3" /> ADMIN
						</span>
					{/if}
				</div>

				<p class="text-xs font-medium text-slate-500">@{user.username} • {user.email}</p>
				<p class="max-w-xl text-xs text-slate-600">{user.bio}</p>

				<div class="flex flex-wrap items-center gap-x-4 gap-y-1 pt-1 text-xs text-slate-500">
					<span class="flex items-center gap-1">
						<GraduationCap class="h-3.5 w-3.5 text-slate-400" />
						{user.university}
					</span>
					<span>{user.faculty}</span>
					<span>
						MSSV: <strong>{user.studentId}</strong>
					</span>
					<span>
						CPA: <strong class="text-slate-800">{user.cpa}</strong>
					</span>
				</div>
			</div>
		</div>

		<div class="flex items-center gap-2">
			<button
				onclick={onEditProfile}
				class="inline-flex items-center gap-1.5 rounded-md border border-slate-300 bg-white px-3 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-50">
				<Pencil class="h-3.5 w-3.5 text-slate-500" /> Edit Profile
			</button>
		</div>
	</div>
{:else}
	<!-- Skeleton loading hoặc thông báo chưa đăng nhập -->
	<div class="animate-pulse space-y-2 border-b border-slate-200 pb-4">
		<div class="h-20 w-20 rounded-xl bg-slate-200"></div>
		<div class="h-4 w-40 rounded bg-slate-200"></div>
	</div>
{/if}

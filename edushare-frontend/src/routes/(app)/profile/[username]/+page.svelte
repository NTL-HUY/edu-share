<script lang="ts">
   import ProfileHeader from '../_components/ProfileHeader.svelte';
   import ActivitySidebar from '../_components/ActivitySidebar.svelte';
   import { sidebarState } from '../_components/activitySidebar.svelte.ts';
   import EditProfileModal from '../_components/EditProfileModal.svelte';
   import type { PageData } from './$types';
   import { headerState } from '../_components/profile.svelte.ts';
   import KnowledgeListSection from '../_components/KnowledgeListSection.svelte';
   import { FollowerState } from '$lib/hooks/profile/followerState.svelte.ts';
   import { page } from '$app/stores';
   import { toast } from 'svelte-sonner';

   let { data }: { data: PageData; children: any } = $props();
   let currentUsername = $derived($page.params.username || data.profile?.username || '');
   let isEditModalOpen = $state(false);

   let isFollowing = $state(data.profile?.isFollowing ?? false);

   $effect(() => {
      if (data.profile) {
         headerState.currentUser = data.profile;
         isFollowing = data.profile.isFollowing ?? false;
      }
   });

   // type MainTab = 'profile' | 'activity' | 'saves' | 'settings';
   // let activeTab = $state<MainTab>('activity');

   let followerState = $state(new FollowerState(currentUsername || '', 'followers'));
   let followingState = $state(new FollowerState(currentUsername || '', 'following'));
   

   let loaded = $state(false);
   $effect(() => {
      if (data.profile?.username && !loaded) {
         followerState.loadFollowers();
         followingState.loadFollowers();
         loaded = true;
      }
   });


   async function handleFollow() {
      if (!data.profile) return;
      
      // 1. Optimistic UI update
      isFollowing = true;
      if (data.profile) data.profile.isFollowing = true;

      try {
         const res = await fetch(`/api/users/${currentUsername}/follow`, {
            method: 'POST'
         });
         const result = await res.json();

         if (!result.success) {
            throw new Error(result.message || 'Không thể theo dõi người dùng này');
         }

         toast.success(`Đã theo dõi ${data.profile.fullName || currentUsername}`);
         
         // Reload lại danh sách follower/following nếu đang mở tab đó
         followerState.loadFollowers();
         followingState.loadFollowers();
      } catch (error: any) {
         // Rollback UI nếu lỗi
         isFollowing = false;
         if (data.profile) data.profile.isFollowing = false;
         toast.error(error.message);
      }
   }

   async function handleUnfollow() {
      if (!data.profile) return;

      // 1. Optimistic UI update
      isFollowing = false;
      if (data.profile) data.profile.isFollowing = false;

      try {
         // Sửa DELETE -> POST để khớp với API Route Handler
         const res = await fetch(`/api/users/${currentUsername}/unfollow`, {
            method: 'delete'
         });
         const result = await res.json();

         if (!result.success) {
            throw new Error(result.message || 'Không thể bỏ theo dõi');
         }

         toast.success(`Đã bỏ theo dõi ${data.profile.fullName || currentUsername}`);
         
         // Reload lại danh sách follower/following nếu đang mở tab đó
         followerState.loadFollowers();
         followingState.loadFollowers();
      } catch (error: any) {
         // Rollback UI nếu lỗi
         isFollowing = true;
         if (data.profile) data.profile.isFollowing = true;
         toast.error('Có lỗi xảy ra, vui lòng thử lại!');
      }
   }
</script>

<div class="space-y-2 text-slate-800">
   <!-- 1. HEADER PROFILE -->
   <!-- Đảm bảo component ProfileHeader nhận prop profile có isFollowing cập nhật -->
   <ProfileHeader
      profile={data.profile ? { ...data.profile, isFollowing } : data.profile}
      onEditProfile={() => (isEditModalOpen = true)}
      onFollow={handleFollow}
      onUnfollow={handleUnfollow} />

   <!-- 2. TAB ACTIVITY CONTENT -->
   <!-- {#if activeTab === 'activity'} -->
      <div class="flex flex-col gap-6 sm:flex-row">
         <!-- SUB-SIDEBAR BÊN TRÁI -->
         <ActivitySidebar />

         <!-- KHU VỰC NỘI DUNG BÊN PHẢI -->
         <div class="min-w-0 flex-1 space-y-4">
            <!-- SUB-TAB: KNOWLEDGE -->
            {#if sidebarState.activeSubTab === 'knowledge'}
               <KnowledgeListSection profile={data.profile} />
            {/if}

            <!-- SUB-TAB: FOLLOWERS & FOLLOWING -->
            {#if sidebarState.activeSubTab === 'followers' || sidebarState.activeSubTab === 'following'}
               <div class="flex items-center justify-between">
                  <h2 class="text-lg font-normal text-slate-800">
                     {sidebarState.activeSubTab === 'followers'
                        ? `Người theo dõi (${followerState.totalElements})`
                        : `Đang theo dõi (${followingState.totalElements})`}
                  </h2>
                  {#if sidebarState.activeSubTab === 'followers' ? followerState.loading : followingState.loading}
                     <span class="text-sm text-slate-400">Đang tải...</span>
                  {/if}
               </div>

               {#if sidebarState.activeSubTab === 'followers' ? followerState.error : followingState.error}
                  <div class="rounded-lg bg-red-50 p-4 text-sm text-red-600">
                     {sidebarState.activeSubTab === 'followers' ? followerState.error : followingState.error}
                  </div>
               {/if}

               <div class="grid grid-cols-2 gap-3">
                  {#each sidebarState.activeSubTab === 'followers' ? followerState.items : followingState.items as f}
                     <div class="flex items-center gap-3 rounded-lg border border-slate-200 bg-white p-3">
                        <a href="/profile/{f.username}" class="block">
                           {#if f.avatarUrl}
                              <img src={f.avatarUrl} alt="Avatar" class="h-10 w-10 rounded-full bg-slate-100" />
                           {:else}
                              <div class="flex h-10 w-10 items-center justify-center rounded-full bg-slate-200 text-slate-600">
                                 {f.fullName?.charAt(0) || f.username?.charAt(0) || '?'}
                              </div>
                           {/if}
                        </a>
                        <div>
                           <p class="text-xs font-semibold text-slate-800">{f.fullName}</p>
                           <p class="text-[11px] text-slate-400">@{f.username}</p>
                        </div>
                     </div>
                  {:else}
                     <div class="col-span-2 py-8 text-center text-sm text-slate-500">
                        Chưa có {sidebarState.activeSubTab === 'followers' ? 'người theo dõi' : 'ai đang theo dõi'}.
                     </div>
                  {/each}
               </div>

               <!-- Nút Load More -->
               {#if sidebarState.activeSubTab === 'followers' ? followerState.hasMore && !followerState.loading : followingState.hasMore && !followingState.loading}
                  <div class="flex justify-center pt-2">
                     <button
                        onclick={() => {
                           if (sidebarState.activeSubTab === 'followers') {
                              followerState.loadMore();
                           } else {
                              followingState.loadMore();
                           }
                        }}
                        class="rounded-lg border border-slate-200 px-6 py-2 text-sm text-slate-600 transition-colors hover:bg-slate-50">
                        Xem thêm
                     </button>
                  </div>
               {/if}
            {/if}
         </div>
      </div>
   <!-- {/if} -->
</div>

<!-- Render Modal -->
<EditProfileModal bind:isOpen={isEditModalOpen} onClose={() => (isEditModalOpen = false)} />
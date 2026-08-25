<script lang="ts">
   import { 
      BookOpen, HelpCircle, ThumbsUp, ThumbsDown, Eye, 
      Pencil, Trash2, Globe, Lock, CheckCircle2, Search, 
      GraduationCap, UserCheck, UserPlus, Sparkles, 
      MessageSquare, Bookmark, Settings, Activity, ShieldCheck
   } from 'lucide-svelte';

   // ==================== STATE MANAGEMENT (Svelte 5 Runes) ====================
   type MainTab = 'profile' | 'activity' | 'saves' | 'settings';
   type ActivitySubTab = 'summary' | 'lessons' | 'questions' | 'comments' | 'votes' | 'followers' | 'following' | 'ai_chunks';

   let activeTab = $state<MainTab>('activity');
   let activeSubTab = $state<ActivitySubTab>('summary');
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
      { id: '10', title: 'Tối ưu Vector Search với pgvector trong PostgreSQL', type: 'LESSON', value: 1, createdAt: '2026-08-22T09:00:00Z' },
      { id: '11', title: 'Tại sao không nên dùng MySQL cho RAG System?', type: 'QUESTION', value: -1, createdAt: '2026-08-17T15:10:00Z' }
   ]);

   // 5. Mock Data: Follows (`follows` table)
   let followers = $state([
      { id: '2', username: 'tranvanb', fullName: 'Trần Văn B', avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=John' },
      { id: '3', username: 'lethic', fullName: 'Lê Thị C', avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Jane' }
   ]);

   let following = $state([
      { id: '4', username: 'dev_master', fullName: 'Senior Spring Dev', avatarUrl: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Master' }
   ]);

   // 6. Mock Data: AI Chunks (`knowledge_chunk` table)
   let aiChunks = $state([
      { id: '1', knowledgeId: '1', chunkIndex: 0, content: 'Hướng dẫn tích hợp GraphQL với Spring Boot...', dimensions: 768, isPublic: true },
      { id: '2', knowledgeId: '1', chunkIndex: 1, content: 'Cấu hình GraphQLClient trong SvelteKit...', dimensions: 768, isPublic: true }
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

<div class="mx-auto max-w-6xl space-y-6 p-6 font-sans text-slate-800">
   
   <!-- 1. HEADER PROFILE (Gồm User + Profile Student Info) -->
   <div class="flex flex-col gap-4 border-b border-slate-200 pb-6 sm:flex-row sm:items-start sm:justify-between">
      <div class="flex gap-4">
         <div class="relative">
            <img
               src={currentUser.avatarUrl}
               alt="Avatar"
               class="h-20 w-20 rounded-xl border border-slate-200 bg-slate-50 object-cover sm:h-24 sm:w-24" />
            {#if currentUser.isFamous}
               <span class="absolute -bottom-1 -right-1 rounded-full bg-amber-400 p-1 text-white" title="Famous Contributor">
                  <Sparkles class="h-3.5 w-3.5" />
               </span>
            {/if}
         </div>

         <div class="space-y-1">
            <div class="flex items-center gap-2">
               <h1 class="text-2xl font-bold text-slate-900">{currentUser.fullName}</h1>
               {#if currentUser.userRole === 'ADMIN'}
                  <span class="inline-flex items-center gap-1 rounded bg-slate-800 px-2 py-0.5 text-[10px] font-semibold text-white">
                     <ShieldCheck class="h-3 w-3" /> ADMIN
                  </span>
               {/if}
               <span class="rounded-full bg-orange-100 px-2.5 py-0.5 text-xs font-bold text-orange-700">
                  {totalReputation} Rep
               </span>
            </div>

            <p class="text-xs font-medium text-slate-500">@{currentUser.username} • {currentUser.email}</p>
            <p class="text-xs text-slate-600 max-w-xl">{currentUser.bio}</p>
            
            <!-- Học vấn từ profiles table -->
            <div class="flex flex-wrap items-center gap-x-4 gap-y-1 pt-1 text-xs text-slate-500">
               <span class="flex items-center gap-1"><GraduationCap class="h-3.5 w-3.5 text-slate-400" /> {currentUser.university}</span>
               <span>{currentUser.faculty}</span>
               <span>MSSV: <strong>{currentUser.studentId}</strong></span>
               <span>CPA: <strong class="text-slate-800">{currentUser.cpa}</strong></span>
            </div>
         </div>
      </div>

      <div class="flex items-center gap-2">
         <button
            onclick={() => (activeTab = 'settings')}
            class="inline-flex items-center gap-1.5 rounded-md border border-slate-300 bg-white px-3 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-50">
            <Pencil class="h-3.5 w-3.5 text-slate-500" /> Edit Profile
         </button>
      </div>
   </div>

   <!-- 2. MAIN HORIZONTAL TABS (StackOverflow Style) -->
   <div class="flex gap-1 border-b border-slate-200 text-sm">
      <button
         onclick={() => (activeTab = 'profile')}
         class="px-4 py-2 font-medium transition-colors border-b-2 -mb-px {activeTab === 'profile' ? 'border-orange-500 text-slate-900 font-semibold' : 'border-transparent text-slate-600 hover:text-slate-900'}">
         Profile
      </button>
      <button
         onclick={() => (activeTab = 'activity')}
         class="px-4 py-2 font-medium transition-colors border-b-2 -mb-px {activeTab === 'activity' ? 'border-orange-500 text-slate-900 font-semibold' : 'border-transparent text-slate-600 hover:text-slate-900'}">
         Activity
      </button>
      <button
         onclick={() => (activeTab = 'saves')}
         class="px-4 py-2 font-medium transition-colors border-b-2 -mb-px {activeTab === 'saves' ? 'border-orange-500 text-slate-900 font-semibold' : 'border-transparent text-slate-600 hover:text-slate-900'}">
         Saves
      </button>
      <button
         onclick={() => (activeTab = 'settings')}
         class="px-4 py-2 font-medium transition-colors border-b-2 -mb-px {activeTab === 'settings' ? 'border-orange-500 text-slate-900 font-semibold' : 'border-transparent text-slate-600 hover:text-slate-900'}">
         Settings
      </button>
   </div>

   <!-- 3. TAB ACTIVITY CONTENT -->
   {#if activeTab === 'activity'}
      <div class="grid grid-cols-12 gap-6 pt-2">
         
         <!-- SUB-SIDEBAR BÊN TRÁI -->
         <div class="col-span-12 sm:col-span-3 space-y-1 text-xs">
            <button
               onclick={() => (activeSubTab = 'summary')}
               class="flex w-full items-center gap-2 px-3 py-2 rounded-md font-medium transition {activeSubTab === 'summary' ? 'bg-slate-100 font-bold text-slate-900' : 'text-slate-600 hover:bg-slate-50'}">
               <Activity class="h-3.5 w-3.5" /> Summary
            </button>

            <button
               onclick={() => (activeSubTab = 'lessons')}
               class="flex w-full items-center justify-between px-3 py-2 rounded-md font-medium transition {activeSubTab === 'lessons' ? 'bg-slate-100 font-bold text-slate-900' : 'text-slate-600 hover:bg-slate-50'}">
               <span class="flex items-center gap-2"><BookOpen class="h-3.5 w-3.5" /> Bài học</span>
               <span class="rounded bg-slate-200 px-1.5 py-0.5 text-[10px]">{items.filter(i => i.type === 'LESSON').length}</span>
            </button>

            <button
               onclick={() => (activeSubTab = 'questions')}
               class="flex w-full items-center justify-between px-3 py-2 rounded-md font-medium transition {activeSubTab === 'questions' ? 'bg-slate-100 font-bold text-slate-900' : 'text-slate-600 hover:bg-slate-50'}">
               <span class="flex items-center gap-2"><HelpCircle class="h-3.5 w-3.5" /> Câu hỏi</span>
               <span class="rounded bg-slate-200 px-1.5 py-0.5 text-[10px]">{items.filter(i => i.type === 'QUESTION').length}</span>
            </button>

            <button
               onclick={() => (activeSubTab = 'comments')}
               class="flex w-full items-center justify-between px-3 py-2 rounded-md font-medium transition {activeSubTab === 'comments' ? 'bg-slate-100 font-bold text-slate-900' : 'text-slate-600 hover:bg-slate-50'}">
               <span class="flex items-center gap-2"><MessageSquare class="h-3.5 w-3.5" /> Bình luận</span>
               <span class="rounded bg-slate-200 px-1.5 py-0.5 text-[10px]">{comments.length}</span>
            </button>

            <button
               onclick={() => (activeSubTab = 'votes')}
               class="flex w-full items-center justify-between px-3 py-2 rounded-md font-medium transition {activeSubTab === 'votes' ? 'bg-slate-100 font-bold text-slate-900' : 'text-slate-600 hover:bg-slate-50'}">
               <span class="flex items-center gap-2"><ThumbsUp class="h-3.5 w-3.5" /> Lịch sử Vote</span>
               <span class="rounded bg-slate-200 px-1.5 py-0.5 text-[10px]">{votedItems.length}</span>
            </button>

            <button
               onclick={() => (activeSubTab = 'followers')}
               class="flex w-full items-center justify-between px-3 py-2 rounded-md font-medium transition {activeSubTab === 'followers' ? 'bg-slate-100 font-bold text-slate-900' : 'text-slate-600 hover:bg-slate-50'}">
               <span class="flex items-center gap-2"><UserCheck class="h-3.5 w-3.5" /> Followers</span>
               <span class="rounded bg-slate-200 px-1.5 py-0.5 text-[10px]">{followers.length}</span>
            </button>

            <button
               onclick={() => (activeSubTab = 'following')}
               class="flex w-full items-center justify-between px-3 py-2 rounded-md font-medium transition {activeSubTab === 'following' ? 'bg-slate-100 font-bold text-slate-900' : 'text-slate-600 hover:bg-slate-50'}">
               <span class="flex items-center gap-2"><UserPlus class="h-3.5 w-3.5" /> Following</span>
               <span class="rounded bg-slate-200 px-1.5 py-0.5 text-[10px]">{following.length}</span>
            </button>

            <button
               onclick={() => (activeSubTab = 'ai_chunks')}
               class="flex w-full items-center justify-between px-3 py-2 rounded-md font-medium transition {activeSubTab === 'ai_chunks' ? 'bg-slate-100 font-bold text-slate-900' : 'text-slate-600 hover:bg-slate-50'}">
               <span class="flex items-center gap-2 text-purple-600"><Sparkles class="h-3.5 w-3.5" /> Vector Chunks</span>
               <span class="rounded bg-purple-100 text-purple-700 px-1.5 py-0.5 text-[10px]">{aiChunks.length}</span>
            </button>
         </div>

         <!-- KHU VỰC HIỂN THỊ NỘI DUNG (BÊN PHẢI) -->
         <div class="col-span-12 sm:col-span-9 space-y-4">
            
            <!-- SUB-TAB: SUMMARY -->
            {#if activeSubTab === 'summary'}
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
                  <div class="rounded-md border border-slate-200 divide-y divide-slate-100">
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
            {#if activeSubTab === 'lessons' || activeSubTab === 'questions'}
               <div class="flex items-center justify-between gap-4">
                  <h2 class="text-lg font-normal text-slate-800">
                     {filteredItems.length} {activeSubTab === 'lessons' ? 'Bài học' : 'Câu hỏi'}
                  </h2>
                  <div class="relative w-64">
                     <Search class="absolute left-2.5 top-1/2 h-3.5 w-3.5 -translate-y-1/2 text-slate-400" />
                     <input
                        type="text"
                        bind:value={searchQuery}
                        placeholder="Lọc tiêu đề..."
                        class="w-full rounded-md border border-slate-300 py-1.5 pl-8 pr-3 text-xs focus:border-orange-500 focus:outline-none" />
                  </div>
               </div>

               <div class="rounded-md border border-slate-200 divide-y divide-slate-100 bg-white">
                  {#each filteredItems as item (item.id)}
                     <div class="flex items-center justify-between p-4 hover:bg-slate-50/70 transition">
                        <div class="flex items-center gap-4 min-w-0">
                           <div class="flex flex-col items-center justify-center min-w-12 rounded border border-slate-200 bg-slate-50 px-2 py-1">
                              <span class="text-xs font-bold text-slate-700">{item.voteScore}</span>
                              <span class="text-[9px] text-slate-400">votes</span>
                           </div>
                           <div class="min-w-0 space-y-1">
                              <a href="/feed/{item.id}" class="font-medium text-blue-600 hover:text-blue-800 text-sm truncate block">
                                 {item.title}
                              </a>
                              <div class="flex items-center gap-3 text-xs text-slate-400">
                                 <span>{item.viewsCount} views</span>
                                 <span>{item.commentCount} comments</span>
                              </div>
                           </div>
                        </div>

                        <div class="flex items-center gap-2">
                           <button onclick={() => toggleVisibility(item.id)} class="p-1.5 text-slate-400 hover:text-slate-600">
                              {#if item.isPublic}<Globe class="h-4 w-4 text-emerald-600" />{:else}<Lock class="h-4 w-4 text-slate-400" />{/if}
                           </button>
                           <a href="/feed/{item.id}/edit" class="p-1.5 text-slate-400 hover:text-blue-600"><Pencil class="h-4 w-4" /></a>
                           <button onclick={() => deleteItem(item.id)} class="p-1.5 text-slate-400 hover:text-rose-600"><Trash2 class="h-4 w-4" /></button>
                        </div>
                     </div>
                  {/each}
               </div>
            {/if}

            <!-- SUB-TAB: COMMENTS (`comment` Table) -->
            {#if activeSubTab === 'comments'}
               <h2 class="text-lg font-normal text-slate-800">Bình luận & Phản hồi đã viết</h2>
               <div class="rounded-md border border-slate-200 divide-y divide-slate-100 bg-white">
                  {#each comments as c}
                     <div class="p-3 text-xs space-y-1">
                        <div class="flex items-center gap-2 text-slate-500">
                           <span>{c.replyToUserName ? `Reply tới @${c.replyToUserName}` : 'Bình luận bài:'}</span>
                           <a href="/feed/{c.knowledgeId}" class="font-semibold text-blue-600 hover:underline">{c.knowledgeTitle}</a>
                        </div>
                        <p class="text-slate-800 bg-slate-50 p-2 rounded border border-slate-100">{c.content}</p>
                     </div>
                  {/each}
               </div>
            {/if}

            <!-- SUB-TAB: VOTES (`vote` Table) -->
            {#if activeSubTab === 'votes'}
               <h2 class="text-lg font-normal text-slate-800">Lịch sử Vote</h2>
               <div class="rounded-md border border-slate-200 divide-y divide-slate-100 bg-white">
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
            {#if activeSubTab === 'followers' || activeSubTab === 'following'}
               <h2 class="text-lg font-normal text-slate-800">
                  {activeSubTab === 'followers' ? 'Người theo dõi (Followers)' : 'Đang theo dõi (Following)'}
               </h2>
               <div class="grid grid-cols-2 gap-3">
                  {#each (activeSubTab === 'followers' ? followers : following) as f}
                     <div class="flex items-center gap-3 rounded-lg border border-slate-200 p-3 bg-white">
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
            {#if activeSubTab === 'ai_chunks'}
               <div class="flex items-center justify-between">
                  <h2 class="text-lg font-normal text-slate-800">Quản lý Chunk & Embedding (RAG Service)</h2>
                  <span class="text-xs font-mono text-purple-600">pgvector(768)</span>
               </div>
               <div class="rounded-md border border-slate-200 divide-y divide-slate-100 bg-white">
                  {#each aiChunks as chunk}
                     <div class="p-3 text-xs space-y-1">
                        <div class="flex items-center justify-between font-mono text-[11px] text-slate-500">
                           <span>Chunk #{chunk.chunkIndex} (Knowledge ID: {chunk.knowledgeId})</span>
                           <span class="rounded bg-emerald-100 text-emerald-700 px-1.5 py-0.5">Vector Synced</span>
                        </div>
                        <p class="text-slate-700 truncate">{chunk.content}</p>
                     </div>
                  {/each}
               </div>
            {/if}

         </div>
      </div>
   {/if}

   <!-- 4. TAB SETTINGS (Sửa thông tin bảng profiles) -->
   {#if activeTab === 'settings'}
      <div class="max-w-xl space-y-4 pt-2">
         <h2 class="text-lg font-bold text-slate-800">Cập nhật thông tin Sinh viên</h2>
         <form class="space-y-3 text-xs" onsubmit={(e) => e.preventDefault()}>
            <div>
               <label for="fullName" class="block font-medium text-slate-700">Họ và tên</label>
               <input id="fullName" type="text" bind:value={currentUser.fullName} class="mt-1 w-full rounded-md border border-slate-300 p-2" />
            </div>
            <div class="grid grid-cols-2 gap-3">
               <div>
                  <label for="studentId" class="block font-medium text-slate-700">Mã số sinh viên (MSSV)</label>
                  <input id="studentId" type="text" bind:value={currentUser.studentId} class="mt-1 w-full rounded-md border border-slate-300 p-2" />
               </div>
               <div>
                  <label for="cpa" class="block font-medium text-slate-700">CPA</label>
                  <input id="cpa" type="number" step="0.01" bind:value={currentUser.cpa} class="mt-1 w-full rounded-md border border-slate-300 p-2" />
               </div>
            </div>
            <div>
               <label for="bio" class="block font-medium text-slate-700">Bio / Giới thiệu</label>
               <textarea id="bio" rows="3" bind:value={currentUser.bio} class="mt-1 w-full rounded-md border border-slate-300 p-2"></textarea>
            </div>
            <button type="submit" class="rounded-md bg-orange-600 px-4 py-2 font-semibold text-white hover:bg-orange-700">Lưu thay đổi</button>
         </form>
      </div>
   {/if}
</div>
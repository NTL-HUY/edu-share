<script lang="ts">
   import { page } from '$app/state';
   import { House, Bot, Tag, Trophy, MessageSquare, FileText, Users, Building2, CodeXml, CircleHelpIcon, Search } from 'lucide-svelte';

   const navItems = [
      { label: 'Dành cho bạn', path: '/', icon: House },
      { label: 'Tìm kiếm & Khám phá', path: '/search', icon: Search },
      { label: 'AI Assist', path: '/feed', icon: Bot, isSpecial: true },
      { label: 'Tags', path: '/tags', icon: Tag },
      {
         label: 'Stack Overflow',
         sublabel: 'for Agents',
         path: '/agents',
         icon: CodeXml,
         isSpecial: true
      },
      { label: 'Challenges', path: '/challenges', icon: Trophy },
      { label: 'Chat', path: '/chat', icon: MessageSquare, isSpecial: true },
      { label: 'Articles', path: '/articles', icon: FileText },
      { label: 'Users', path: '/users', icon: Users },
      { label: 'Companies', path: '/companies', icon: Building2 }
   ];

   function isActive(path: string) {
      if (path === '/') {
         return page.url.pathname === '/';
      }
      // Check active cho cả đường dẫn con (ví dụ: /questions/123 vẫn active menu Questions)
      return page.url.pathname.startsWith(path);
   }

   function linkClass(path: string) {
      const base = 'flex items-center gap-3 rounded-md px-3 py-2 text-xs font-medium hover:bg-gray-100 transition-colors';
      const active = isActive(path) 
         ? 'bg-gray-100 font-bold text-black' 
         : 'text-gray-600 hover:text-black';
      
      return `${base} ${active}`.trim();
   }
</script>

<nav class="flex h-screen flex-col gap-1 border-r border-gray-200 bg-white p-2 pr-2 text-gray-700">
   {#each navItems as item}
      {@const Icon = item.icon}
      
      <!-- Thẻ <a> trong SvelteKit mặc định đã tự động chuyển trang client-side (SPA) -->
      <a href={item.path} class={linkClass(item.path)}>
         {#if item.isSpecial}
            <div class="flex h-7 w-7 items-center justify-center rounded-full bg-indigo-100 text-indigo-600">
               <Icon class="h-4 w-4" />
            </div>
         {:else}
            <Icon class="h-5 w-5" />
         {/if}

         {#if item.sublabel}
            <span class="leading-tight">
               {item.label}
               <br />
               <span class="text-[10px] text-gray-500 font-normal">{item.sublabel}</span>
            </span>
         {:else}
            <span>{item.label}</span>
         {/if}
      </a>
   {/each}
</nav>
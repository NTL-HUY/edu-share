<script lang="ts">
   import { Carta, MarkdownEditor } from 'carta-md';
   import DOMPurify from 'isomorphic-dompurify';
   import 'carta-md/default.css';
   import { lessonLevels } from '$lib/stores/reference.store';
	import type { CreatePostState } from '../create/createPost.svelte';

   let { form }: { form: CreatePostState } = $props();

   const carta = new Carta({
      sanitizer: DOMPurify.sanitize
   });
</script>

<div class="space-y-4">
   <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
      <div>
         <label for="level" class="form-label">Mức độ</label>
         <select id="level" bind:value={form.level} class="form-select">
            {#each $lessonLevels as item (item.code)}
               <option value={item.code}>{item.displayName}</option>
            {/each}
         </select>
      </div>
      <div>
         <label for="time" class="form-label">Thời gian đọc (phút)</label>
         <input id="time" type="number" min="1" bind:value={form.estimateTimeInMinutes} class="form-input" />
      </div>
   </div>



   <div>
      <label for="contentMarkdown" class="form-label">
         Nội dung Bài học (Markdown) <span class="text-rose-500">*</span>
      </label>
      <div class="editor-container overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm transition-all focus-within:border-blue-500 focus-within:ring-2 focus-within:ring-blue-100">
         <MarkdownEditor {carta} bind:value={form.contentMarkdown} />
      </div>
   </div>
</div>
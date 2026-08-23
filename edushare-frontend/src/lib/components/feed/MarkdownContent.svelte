<script lang="ts">
	import { marked } from 'marked';
	import hljs from 'highlight.js';
	import DOMPurify from 'isomorphic-dompurify'; // an toàn cả SSR lẫn client
	import 'highlight.js/styles/github-dark.css';

	// KHÔNG dùng marked-highlight: bản 2.2.4 không tương thích với API renderer
	// mới của marked v18, khiến cờ `escaped` không được set đúng => marked tự
	// escape luôn HTML mà hljs đã render ra (lồng 2 lớp entity, ra "&amp;lt;span...").
	// Tự viết renderer.code để trả thẳng HTML cuối cùng, marked không đụng vào nữa.
	const renderer = new marked.Renderer();
	renderer.code = ({ text, lang }: { text: string; lang?: string }) => {
		const language = lang && hljs.getLanguage(lang) ? lang : 'plaintext';
		const highlighted = hljs.highlight(text, { language }).value;
		return `<pre><code class="hljs language-${language}">${highlighted}</code></pre>`;
	};
	marked.use({ renderer });
	marked.setOptions({ breaks: true, gfm: true });

	let { content = '' }: { content: string } = $props();

	// $derived thay vì $effect: chỉ tính lại khi content đổi, không side-effect
	let html = $derived(
		content ? DOMPurify.sanitize(marked.parse(content, { async: false }) as string) : ''
	);
</script>

{#if html}
	<div
		class="markdown-body prose prose-sm min-w-0 max-w-none text-gray-800
		       prose-headings:font-bold prose-headings:text-gray-900
		       prose-a:text-blue-600 prose-a:no-underline hover:prose-a:underline prose-a:break-all
		       prose-code:rounded prose-code:bg-gray-100 prose-code:px-1 prose-code:py-0.5 prose-code:text-red-600 prose-code:before:content-none prose-code:after:content-none
		       prose-pre:bg-gray-900 prose-pre:text-gray-100
		       prose-img:rounded-lg"
	>
		{@html html}
	</div>
{:else}
	<p class="text-sm text-gray-400 italic">Không có nội dung.</p>
{/if}

<style>
	/* Ép cứng, không phụ thuộc @tailwindcss/typography có load rule overflow-x
	   cho <pre> hay không — chặn tận gốc nguyên nhân tràn ngang toàn trang. */
	.markdown-body :global(pre) {
		overflow-x: auto;
		max-width: 100%;
	}
	.markdown-body :global(code) {
		overflow-wrap: anywhere;
	}
	/* code trong <pre> thì giữ nguyên định dạng, không word-break để không vỡ cú pháp */
	.markdown-body :global(pre code) {
		overflow-wrap: normal;
		white-space: pre;
	}
	/* văn bản thường, link dài không khoảng trắng cũng không được đẩy ngang */
	.markdown-body :global(p),
	.markdown-body :global(li),
	.markdown-body :global(a) {
		overflow-wrap: anywhere;
	}
	/* table (nếu markdown có) cũng hay là thủ phạm gây tràn ngang */
	.markdown-body :global(table) {
		display: block;
		overflow-x: auto;
		max-width: 100%;
	}
</style>
<script lang="ts">
	import { marked } from 'marked';
	import hljs from 'highlight.js';
	import DOMPurify from 'isomorphic-dompurify';
	import 'highlight.js/styles/github-dark.css';

	const renderer = new marked.Renderer();
	renderer.code = ({ text, lang }: { text: string; lang?: string }) => {
		const language = lang && hljs.getLanguage(lang) ? lang : 'plaintext';
		const highlighted = hljs.highlight(text, { language }).value;
		return `<pre><code class="hljs language-${language}">${highlighted}</code></pre>`;
	};
	marked.use({ renderer });
	marked.setOptions({ breaks: true, gfm: true });

	let { content = '' }: { content: string } = $props();

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

	.markdown-body :global(pre) {
		overflow-x: auto;
		max-width: 100%;
	}
	.markdown-body :global(code) {
		overflow-wrap: anywhere;
	}
	.markdown-body :global(pre code) {
		overflow-wrap: normal;
		white-space: pre;
	}
	.markdown-body :global(p),
	.markdown-body :global(li),
	.markdown-body :global(a) {
		overflow-wrap: anywhere;
	}
	.markdown-body :global(table) {
		display: block;
		overflow-x: auto;
		max-width: 100%;
	}
</style>
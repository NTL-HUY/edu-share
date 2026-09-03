<!-- src/routes/(app)/feed/_components/PostForm.svelte -->
<script lang="ts">
	import { Send } from 'lucide-svelte';
	import '$lib/styles/base-feed.css';
	import { categories } from '$lib/stores/reference.store';
	import LessonFormFields from './LessonFormFields.svelte';
	import QuestionFormFields from './QuestionFormFields.svelte';
	import type { CreatePostState } from '../create/createPost.svelte';
	console.log('PostForm render');
	let { form }: { form: CreatePostState } = $props();
	const isEdit = $derived(form.editingId !== null);

	$inspect(form);
</script>

<div class="text-slate-800">
	<!-- Header -->
	<div class="mb-6 flex flex-col justify-between gap-4 border-b border-slate-200 pb-4 sm:flex-row sm:items-center">
		<div>
			<h1 class="page-header-title">{isEdit ? 'Chỉnh sửa nội dung' : 'Tạo nội dung mới'}</h1>
			<p class="page-header-desc">
				{isEdit
					? 'Cập nhật lại bài học hoặc câu hỏi của bạn'
					: 'Chia sẻ kiến thức chuyên môn hoặc đặt câu hỏi cho cộng đồng'}
			</p>
		</div>
		<div class="filter-tabs-group">
			<button
				type="button"
				disabled={isEdit}
				onclick={() => (form.postType = 'LESSON')}
				class="filter-tab-btn {form.postType === 'LESSON' ? 'filter-tab-btn-active' : ''}">
				Bài học (Lesson)
			</button>
			<button
				type="button"
				disabled={isEdit}
				onclick={() => (form.postType = 'QUESTION')}
				class="filter-tab-btn {form.postType === 'QUESTION' ? 'filter-tab-btn-active' : ''}">
				Hỏi đáp (Question)
			</button>
		</div>
	</div>

	<form onsubmit={(e) => form.handleSubmit(e)} class="space-y-6">
		<div class="space-y-5">
			<!-- Title -->
			<div>
				<label for="title" class="form-label">
					Tiêu đề <span class="text-rose-500">*</span>
				</label>
				<input
					id="title"
					type="text"
					bind:value={form.title}
					placeholder={form.postType === 'LESSON'
						? 'VD: Hướng dẫn tích hợp GraphQL với Spring Boot'
						: 'VD: Xử lý lỗi Type Mismatch trong GraphQL Spring Boot như thế nào?'}
					required
					class="form-input" />
			</div>

			<!-- Category & Thumbnail -->
			<div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
				<div>
					<label for="category" class="form-label">Danh mục</label>
					<select id="category" bind:value={form.categoryId} class="form-select">
						{#each $categories as category (category.id)}
							<option value={category.id}>{category.name}</option>
						{/each}
					</select>
				</div>

				<div>
					<label for="thumbnail" class="form-label">Ảnh Thumbnail</label>
					<div class="flex gap-2">
						<div class="relative flex-1">
							<input
								id="thumbnail"
								type="url"
								bind:value={form.thumbnailUrl}
								placeholder="https://example.com/image.png"
								class="form-input pl-10" />
						</div>

						<label
							class="flex cursor-pointer items-center justify-center rounded-xl border border-slate-200 bg-slate-50 px-4 py-2.5 text-xs font-semibold text-slate-600 transition hover:bg-slate-100">
							{form.isUploadingThumbnail ? 'Đang tải...' : 'Tải ảnh lên'}
							<input type="file" accept="image/*" class="hidden" onchange={(e) => form.handleThumbnailUpload(e)} />
						</label>
					</div>
				</div>
			</div>

			<!-- Abstract -->
			<div>
				<div class="flex items-end justify-end gap-6">
					<label class="checkbox-label">
						<input type="checkbox" bind:checked={form.isPublic} class="checkbox-input" />
						Công khai
					</label>
					<label class="checkbox-label">
						<input type="checkbox" bind:checked={form.allowComment} class="checkbox-input" />
						Cho phép bình luận
					</label>
				</div>
				<label for="abstractText" class="form-label">Tóm tắt ngắn</label>
				<textarea
					id="abstractText"
					bind:value={form.abstractText}
					rows="2"
					placeholder="Mô tả tóm tắt 1-2 câu hiển thị trên bảng tin..."
					class="form-textarea">
				</textarea>
			</div>

			<!-- Dynamic Content Zone -->
			<div class="border-t border-slate-200/80 pt-5">
				{#if form.postType === 'LESSON'}
					<LessonFormFields {form} />
				{:else}
					<QuestionFormFields {form} />
				{/if}
			</div>

			<!-- Actions Footer -->
			<div class="flex w-full items-center justify-end border-t border-slate-200/80 pt-4">
				<div class="flex items-center gap-3">
					<button
						type="button"
						class="rounded-xl px-4 py-2.5 text-xs font-semibold text-slate-600 transition hover:bg-slate-100">
						Hủy
					</button>

					<button
						type="submit"
						disabled={form.isSubmitting}
						class="flex items-center gap-2 rounded-xl px-5 py-2.5 text-xs font-semibold text-white shadow-sm transition active:scale-95 disabled:opacity-50 {form.postType ===
						'LESSON'
							? 'bg-blue-600 hover:bg-blue-700'
							: 'bg-amber-600 hover:bg-amber-700'}">
						<Send class="h-4 w-4" />
						{form.isSubmitting
							? isEdit
								? 'Đang lưu...'
								: 'Đang tạo...'
							: isEdit
								? 'Lưu thay đổi'
								: form.postType === 'LESSON'
									? 'Đăng Bài Học'
									: 'Đăng Câu Hỏi'}
					</button>
				</div>
			</div>
		</div>
	</form>
</div>

<style>
	/* --- CARTA EDITOR STYLES --- (giữ nguyên toàn bộ style cũ) */
	:global(.carta-font-code) {
		font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace;
		font-size: 0.875rem;
		line-height: 1.6;
	}
	:global(.carta-editor) {
		border: none !important;
		background-color: transparent !important;
	}
	:global(.carta-toolbar) {
		background-color: #f8fafc !important;
		border-bottom: 1px solid #e2e8f0 !important;
		padding: 0.375rem 0.5rem !important;
		gap: 0.25rem !important;
	}
	:global(.carta-toolbar-left),
	:global(.carta-toolbar-right) {
		gap: 0.25rem !important;
	}
	:global(.carta-icon) {
		border-radius: 0.375rem !important;
		padding: 0.375rem !important;
		color: #475569 !important;
		transition: all 0.15s ease !important;
	}
	:global(.carta-icon:hover) {
		background-color: #e2e8f0 !important;
		color: #0f172a !important;
	}
	:global(.carta-active) {
		background-color: #ffffff !important;
		color: #2563eb !important;
		box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05) !important;
	}
	:global(.carta-input) {
		min-height: 280px !important;
		padding: 1rem !important;
		outline: none !important;
	}
	:global(.carta-renderer) {
		min-height: 280px !important;
		padding: 1rem !important;
		background-color: #ffffff !important;
	}
	:global(.carta-renderer h1) {
		font-size: 1.5rem;
		font-weight: 700;
		margin-top: 1rem;
		margin-bottom: 0.5rem;
		color: #0f172a;
	}
	:global(.carta-renderer h2) {
		font-size: 1.25rem;
		font-weight: 600;
		margin-top: 1.25rem;
		margin-bottom: 0.5rem;
		color: #1e293b;
	}
	:global(.carta-renderer blockquote) {
		border-left: 4px solid #3b82f6;
		background-color: #eff6ff;
		padding: 0.75rem 1rem;
		border-radius: 0 0.5rem 0.5rem 0;
		margin: 1rem 0;
		color: #1e40af;
	}
	:global(.carta-renderer table) {
		width: 100%;
		border-collapse: collapse;
		margin: 1rem 0;
		font-size: 0.875rem;
	}
	:global(.carta-renderer th),
	:global(.carta-renderer td) {
		border: 1px solid #e2e8f0;
		padding: 0.5rem 0.75rem;
		text-align: left;
	}
	:global(.carta-renderer th) {
		background-color: #f8fafc;
		font-weight: 600;
	}
	:global(.carta-renderer pre) {
		background-color: #0f172a;
		color: #f8fafc;
		padding: 1rem;
		border-radius: 0.5rem;
		overflow-x: auto;
		margin: 1rem 0;
	}
</style>

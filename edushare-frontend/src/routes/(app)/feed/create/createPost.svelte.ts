import { categories, lessonLevels } from '$lib/stores/reference.store';
import { contentMarkDown } from '$lib/utils/mockData';
import type {
	CreateLessonInput,
	CreateQuestionInput,
	LessonLevel,
	UpdateLessonInput,
	UpdateQuestionInput
} from '$lib/generated/types';
import { toast } from 'svelte-sonner';
import { getClientSdk } from '$lib/graphql/client';

export class CreatePostState {
	editingId = $state<string | null>(null);
	isLoadingDetail = $state(false);

	postType = $state<'LESSON' | 'QUESTION'>('LESSON');
	title = $state('');
	categoryId = $state('');
	abstractText = $state('');
	thumbnailUrl = $state('');
	isPublic = $state(true);
	allowComment = $state(true);

	// Lesson fields
	contentMarkdown = $state(contentMarkDown);
	level = $state<LessonLevel>('BEGINNER');
	estimateTimeInMinutes = $state(5);

	// Question fields
	questionContent = $state('');

	// Status flags
	isSubmitting = $state(false);
	isUploadingThumbnail = $state(false);

	async loadForEdit(id: string) {
		this.isLoadingDetail = true;
		this.editingId = id;
		const sdk = getClientSdk();

		try {
			const data = await sdk.GetKnowledge({ id });
			const item = data.knowledge;
			if (!item) {
				toast.error('Không tìm thấy bài viết');
				return;
			}

			console.log('Loaded isPublic:', item.isPublic);
			console.log('Form isPublic after assign:', this.isPublic);
			console.log('Loaded allowComment:', item.allowComment);
			// đổ dữ liệu chung
			this.title = item.title;
			this.categoryId = item.category?.id ?? '';
			this.abstractText = item.abstractText ?? '';
			this.thumbnailUrl = item.thumbnailUrl ?? '';
			this.isPublic = item.isPublic;
			this.allowComment = item.allowComment;
			this.postType = item.type;

			// đổ dữ liệu riêng theo loại
			if (item.type === 'LESSON' && 'contentMarkdown' in item) {
				this.contentMarkdown = item.contentMarkdown ?? '';
				this.level = item.level ?? 'BEGINNER';
				this.estimateTimeInMinutes = item.estimateTimeInMinutes ?? 5;
			} else if (item.type === 'QUESTION' && 'content' in item) {
				this.questionContent = item.content ?? '';
			}
		} catch (err: any) {
			const graphQLError = err?.response?.errors?.[0]?.message;
			const errorMessage = graphQLError || err?.message || 'Không thể tải dữ liệu bài viết';
			console.error('Lỗi khi tải chi tiết:', err);
			toast.error(errorMessage);
		} finally {
			this.isLoadingDetail = false;
		}
	}

	async handleThumbnailUpload(e: Event) {
		const target = e.target as HTMLInputElement;
		const file = target.files?.[0];
		if (!file) return;

		try {
			this.isUploadingThumbnail = true;
			const formData = new FormData();
			formData.append('file', file);
			formData.append('folder', 'LESSON_THUMBNAIL');

			const res = await fetch('/api/media/upload', {
				method: 'POST',
				body: formData
			});

			const result = await res.json();
			if (result.ok || result.success) {
				this.thumbnailUrl = result.data?.url ?? result.data;
				toast.success(result.message || 'Tải ảnh thành công');
			} else {
				toast.error(result.message || 'Đã xảy ra lỗi khi tải lên ảnh');
			}
		} catch (err) {
			console.error('Upload thumbnail lỗi:', err);
			toast.error('Không thể kết nối đến server');
		} finally {
			this.isUploadingThumbnail = false;
			target.value = '';
		}
	}

	async handleSubmit(e: SubmitEvent) {
		e.preventDefault();
		this.isSubmitting = true;

		const sdk = getClientSdk();
		const isEdit = this.editingId != null;

		try {
			if (this.postType === 'LESSON') {
				if (isEdit) {
					const input: UpdateLessonInput = {
						id: this.editingId!,
						title: this.title,
						categoryId: this.categoryId || undefined,
						abstractText: this.abstractText || undefined,
						thumbnailUrl: this.thumbnailUrl || undefined,
						isPublic: this.isPublic,
						allowComment: this.allowComment,
						contentMarkdown: this.contentMarkdown,
						level: this.level,
						estimateTimeInMinutes: Number(this.estimateTimeInMinutes)
					};
					const data = await sdk.UpdateLesson({ input });
					toast.success('Đã cập nhật bài học thành công!');
					console.log('Updated lesson:', data.updateLesson);
				} else {
					const lessonInput: CreateLessonInput = {
						title: this.title,
						categoryId: this.categoryId || undefined,
						abstractText: this.abstractText || undefined,
						thumbnailUrl: this.thumbnailUrl || undefined,
						isPublic: this.isPublic,
						allowComment: this.allowComment,
						contentMarkdown: this.contentMarkdown,
						level: this.level,
						estimateTimeInMinutes: Number(this.estimateTimeInMinutes)
					};

					const data = await sdk.CreateLesson({ input: lessonInput });
					toast.success('Đã tạo bài học thành công!');
					console.log('Created lesson:', data.createLesson);
				}
			} else {
				if (isEdit) {
					const input: UpdateQuestionInput = {
						id: this.editingId!,
						title: this.title,
						categoryId: this.categoryId || undefined,
						abstractText: this.abstractText || undefined,
						thumbnailUrl: this.thumbnailUrl || undefined,
						isPublic: this.isPublic,
						allowComment: this.allowComment,
						content: this.questionContent
					};
					const data = await sdk.UpdateQuestion({ input });
					toast.success('Đã cập nhật câu hỏi thành công!');
					console.log('Updated question:', data.updateQuestion);
				} else {
					const input: CreateQuestionInput = {
						title: this.title,
						categoryId: this.categoryId || undefined,
						abstractText: this.abstractText || undefined,
						thumbnailUrl: this.thumbnailUrl || undefined,
						isPublic: this.isPublic,
						allowComment: this.allowComment,
						content: this.questionContent
					};
					const data = await sdk.CreateQuestion({ input });
					toast.success('Đã tạo câu hỏi thành công!');
					console.log('Created question:', data.createQuestion);
				}
			}
		} catch (err: any) {
			const graphQLError = err?.response?.errors?.[0]?.message;
			const errorMessage = graphQLError || err?.message || 'Không thể tạo bài viết';
			console.error('Lỗi khi submit post:', err);
			toast.error(errorMessage);
		} finally {
			this.isSubmitting = false;
		}
	}
}

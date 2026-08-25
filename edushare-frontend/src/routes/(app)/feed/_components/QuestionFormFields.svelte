<script lang="ts">

   let { form }: { form: CreatePostState } = $props();
</script>

<div>
   <label for="questionContent" class="form-label">
      Chi tiết câu hỏi <span class="text-rose-500">*</span>
   </label>
   <textarea
      id="questionContent"
      bind:value={form.questionContent}
      rows="10"
      placeholder="Mô tả vấn đề bạn gặp phải, đính kèm đoạn code lỗi nếu có..."
      required
      class="form-textarea py-3">
   </textarea>
</div>
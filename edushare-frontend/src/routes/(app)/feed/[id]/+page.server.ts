// src/routes/(app)/feed/[id]/+page.server.ts
import type { GetKnowledgeDetailQuery } from '$lib/generated/types';
import { createSdk } from '$lib/graphql/client';
import type { PageServerLoad } from './$types';

export type FeedPageData = {
   knowledge: GetKnowledgeDetailQuery['knowledge'] | null;
   serverError: string | null;
};

export const load: PageServerLoad = async (event): Promise<FeedPageData> => {
   const { id } = event.params;
   const sdk = createSdk(event.fetch);

   try {
      const data = await sdk.GetKnowledgeDetail({
         id,
         commentInput: {cursor: null, limit: 2 }
      });

      if (!data.knowledge) {
         return {
            knowledge: null,
            serverError: 'Bài viết không tồn tại hoặc đã bị xóa'
         };
      }

      return {
         knowledge: data.knowledge,
         serverError: null
      };
   } catch (err: any) {
      const gqlError = err?.response?.errors?.[0] || err?.errors?.[0];
      const message = gqlError?.message || 'Không thể kết nối đến máy chủ';
      console.error("PageServerLoad: " , err)

      return {
         knowledge: null,
         serverError: message
      };
   }
};
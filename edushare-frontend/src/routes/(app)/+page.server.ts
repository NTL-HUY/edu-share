import { createSdk } from "$lib/graphql/client";
import { ClientError } from "graphql-request";
import type { PageServerLoad } from "./$types";

export const load: PageServerLoad = async ({ fetch }) => {
  const sdk = createSdk(fetch);

  try {
    const data = await sdk.GetFeed({ input: { limit: 10 } });
    return { feed: data.getFeed, error: null };
  } catch (err) {
    if (err instanceof ClientError) {
      const gqlError = err.response.errors?.[0];
      const code = gqlError?.extensions?.code as string | undefined;

      if (code === 'UNAUTHENTICATED') {
        // ví dụ redirect về login
        // throw redirect(302, '/login');
      }

      console.error('GraphQL error:', code, gqlError?.message);
      return { feed: null, error: gqlError?.message ?? 'Có lỗi xảy ra' };
    }

    console.error('Network/unknown error:', err);
    return { feed: null, error: 'Không thể kết nối máy chủ' };
  }
};
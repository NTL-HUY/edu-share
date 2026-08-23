import { createSdk } from "$lib/graphql/client";
import { json, type RequestHandler } from "@sveltejs/kit";

export const GET: RequestHandler = async (event) => {
  const cursor = event.url.searchParams.get('cursor');
  const limit = Number(event.url.searchParams.get('limit') ?? 10);

  const sdk = createSdk(event.fetch); 
  try {
    const data = await sdk.GetFeed({ input: { cursor, limit } });
    return json({ ...data.getFeed, error: null });
  } catch (err) {
    console.error('load more feed error:', err);
    return json({ feed: null, error: 'Không thể tải thêm dữ liệu' }, { status: 500 });
  }
};
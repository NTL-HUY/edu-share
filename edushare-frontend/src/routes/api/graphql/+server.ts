import { json, type RequestHandler } from '@sveltejs/kit';
import { PUBLIC_GRAPHQL_URL } from '$env/static/public';

export const POST: RequestHandler = async ({ request, fetch }) => {
   const body = await request.json();

   const response = await fetch(PUBLIC_GRAPHQL_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
   });

   const data = await response.json();
   return json(data, { status: response.status });
};
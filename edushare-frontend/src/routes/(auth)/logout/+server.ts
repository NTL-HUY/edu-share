import { json } from "@sveltejs/kit";
import type { RequestHandler } from "./$types";

export const POST: RequestHandler = async ({ cookies }) => {
   // Xóa các cookie session/token bằng phương thức cookies.delete
   cookies.delete('session', { path: '/' });
   cookies.delete('token', { path: '/' });

   return json({ success: true });
};
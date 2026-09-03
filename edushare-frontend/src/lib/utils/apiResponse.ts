import { json } from "@sveltejs/kit";

export const apiSuccess = (data: any, message: string = 'Thành công') => 
   json({ success: true, data, message, errors: null });

export const apiError = (message: string, errors: any = null, status: number = 400) => 
   json({ success: false, data: null, message, errors }, { status });

export interface ApiResponse<T = any> {
	success: boolean;
	data: T | null;
	message: string;
	errors: any | null;
}
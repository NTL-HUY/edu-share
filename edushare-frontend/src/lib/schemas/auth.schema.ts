import { z } from 'zod';

export const loginSchema = z.object({
  usernameOrEmail: z.string().trim().min(1, 'Vui lòng nhập Email hoặc Username'),
  password: z.string().min(6, 'Mật khẩu phải từ 6 ký tự trở lên')
});

export const registerSchema = z.object({
  username: z.string().trim().min(3, 'Username phải từ 3 ký tự trở lên'),
  email: z.string().trim().email('Email không đúng định dạng'),
  password: z.string().min(6, 'Mật khẩu phải từ 6 ký tự trở lên'),
  fullName: z.string().trim().min(1, 'Vui lòng nhập họ tên')
});

export type LoginRequest = z.infer<typeof loginSchema>;
export type RegisterRequest = z.infer<typeof registerSchema>;
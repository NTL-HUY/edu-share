export interface AuthTokenResponse {
  refreshToken: string;
  accessToken: string;
  accessTokenExpiresIn: number;
  accessTokenType: string;
  refreshTokenExpiresIn: number;
}

export interface UserResponse {
  id: string;
  username: string;
  email: string;
  fullName: string;
  role: 'CANDIDATE' | 'EMPLOYER' | 'ADMIN';
}
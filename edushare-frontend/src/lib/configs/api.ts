import { PUBLIC_API_BASE_URL } from '$env/static/public';

export const API_ENDPOINTS = {
	AUTH: {
		LOGIN: `${PUBLIC_API_BASE_URL}/auth/login`,
		REGISTER: `${PUBLIC_API_BASE_URL}/auth/register`,
		REFRESH_TOKEN: `${PUBLIC_API_BASE_URL}/auth/refresh`,
		LOGOUT: `${PUBLIC_API_BASE_URL}/auth/logout`
	},
	USER: {
		ME: `${PUBLIC_API_BASE_URL}/users/me`,
		ME_PROFILE: `${PUBLIC_API_BASE_URL}/users/me/profile`,
		UPDATE_PROFILE: `${PUBLIC_API_BASE_URL}/users/me/profile`,

	},
	INTERACTION: {
		VOTE: (feedId: string) => `${PUBLIC_API_BASE_URL}/feed/${feedId}/vote`,
		UNVOTE: (feedId: string) => `${PUBLIC_API_BASE_URL}/feed/${feedId}/vote`,
		CREATE_COMMENT: (feedId: string) => `${PUBLIC_API_BASE_URL}/feed/${feedId}/comments`
	},
	GRAPHQL: {
		ENDPOINT: `${PUBLIC_API_BASE_URL}/graphql`
	},
	MEDIA: {
		UPLOAD_IMAGE: `${PUBLIC_API_BASE_URL}/media/upload`
	}
} as const;

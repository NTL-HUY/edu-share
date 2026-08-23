export enum UserRole {
	ADMIN = 'ADMIN',
	USER = 'USER'
}

export interface User {
	id : String;
	username : String;
	password : String;
	readonly role : UserRole;
	readonly createdAt : Date;
	readonly updatedAt : Date;
}


export interface UserBaseProjection {
    id: string;
    username: string;
    avatarUrl: string;
    fullName: string;
}


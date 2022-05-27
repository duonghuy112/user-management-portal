import { Role } from "./role";

export class User {
    id: number;
    username: string;
	firstName: string;
	lastName: string;
	email: string;
	avatarUrl: string;
    lastLoginDate: Date;
    joinDate: Date;
	isActive: boolean;
	role: Role;
}
export interface UserUpdate {
    id: number;
    username: string;
	firstName: string;
	lastName: string;
	email: string;
	avatar: File;
	isActive: boolean;
	roleId: number;
}
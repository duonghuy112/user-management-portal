export interface UserAdd {
    username: string;
	firstName: string;
	lastName: string;
	email: string;
	avatar: File;
	isActive: boolean;
	roleId: number;
}
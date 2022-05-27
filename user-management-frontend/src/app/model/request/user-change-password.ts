export class UserChangePassword {
    id: number;
	currentPassword: string;
	newPassword: string;

	constructor(id: number, currentPassword: string, newPassword: string) {
		this.id = id;
		this.currentPassword = currentPassword;
		this.newPassword = newPassword;
	}
}
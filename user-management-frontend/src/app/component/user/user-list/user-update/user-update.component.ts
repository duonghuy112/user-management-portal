import { UserUpdate } from './../../../../model/request/user-update';
import { HttpErrorResponse } from '@angular/common/http';
import { NotificationType } from './../../../../model/enum/notification-tpye.enum';
import { NotificationService } from './../../../../service/notification.service';
import { RoleService } from './../../../../service/role.service';
import { UserService } from './../../../../service/user.service';
import { Subscription } from 'rxjs';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Role } from './../../../../model/response/role';
import { User } from './../../../../model/response/user';
import { Component, OnInit, Input, Output, EventEmitter, OnChanges, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-user-update',
  templateUrl: './user-update.component.html',
  styleUrls: ['./user-update.component.css']
})
export class UserUpdateComponent implements OnInit,OnChanges, OnDestroy {
  @Input() userId: number;
  @Input() user: User;
  @Output() refresh = new EventEmitter<boolean>();
  public selectedUser: User;
  public roles: Role[];
  public fileName: string;
  public userUpdateFormGroup: FormGroup;
  private subscriptions: Subscription[] = [];
  
  constructor(private userService: UserService,
              private roleService: RoleService, 
              private notificationService: NotificationService,
              private formBuilder: FormBuilder) { }

  ngOnChanges(): void {
    if (this.userId) {
      this.onSelectUser(this.userId);
    }
  }

  ngOnInit(): void {
    this.setupUserUpdateForm();
    this.getRoleList();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  saveUpdateUser(): void {
    this.clickButton('saveUserUpdate');
  }

  onSubmitUserUpdate() {
    const formDataUserUpdate = this.createFormData(this.userUpdateFormGroup.value);
    this.subscriptions.push(
      this.userService.update(formDataUserUpdate).subscribe(
        (response: User) => {
          this.sendNotification(NotificationType.SUCCESS, `${response.firstName} ${response.lastName} updated successfully`);
          this.closeUserUpdateModal();
          this.refresh.emit(true);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    );
  }

  onProfileImageChange(fileName: string, profileImage: File): void {
    this.fileName =  fileName;
    this.userUpdateFormGroup.patchValue({
      avatar: profileImage
    });
  }

  onSelectUser(id: number) {
    this.subscriptions.push(
      this.userService.getUser(id).subscribe(
        (response: User) => {
          this.selectedUser = response;
          this.setValueUserUpdateForm();
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.closeUserUpdateModal();
        }
      )
    );
  }

  closeUserUpdateModal() {
    this.clickButton('closeEditUserModal');
    this.fileName = null;
  }

  public get isUser(): boolean {
    return this.user.role.id == 1;
  }
  public get isHrOrManager(): boolean {
    return this.user.role.id == 2 || this.user.role.id == 3;
  }
  public get isAdmin(): boolean {
    return this.user.role.id == 4;
  }
  public get isSuperAdmin(): boolean {
    return this.user.role.id == 5;
  }

  private setupUserUpdateForm() {
    this.userUpdateFormGroup = this.formBuilder.group({
      id: ['', [Validators.required, Validators.minLength(0)]],
      username: ['', [Validators.required, Validators.minLength(0)]],
      firstName: ['', [Validators.required, Validators.minLength(0)]],
      lastName: ['', [Validators.required, Validators.minLength(0)]],
      email: ['', [Validators.required, Validators.minLength(0)]],
      roleId: ['', [Validators.required, Validators.minLength(0)]],
      isActive: [false, [Validators.required]],
      avatar: ['']
    });
  }

  private setValueUserUpdateForm() {
    this.userUpdateFormGroup.setValue({
      id: this.selectedUser.id,
      username: this.selectedUser.username,
      firstName: this.selectedUser.firstName,
      lastName: this.selectedUser.lastName,
      email: this.selectedUser.email,
      roleId: this.selectedUser.role.id,
      isActive: this.selectedUser.isActive,
      avatar: ''
    });
  }

  private createFormData(userUpdate: UserUpdate): FormData {
    const formData = new FormData();
    formData.append('id', userUpdate.id.toString());
    formData.append('username', userUpdate.username);
    formData.append('firstName', userUpdate.firstName);
    formData.append('lastName', userUpdate.lastName);
    formData.append('email', userUpdate.email);
    formData.append('roleId', userUpdate.roleId.toString());
    formData.append('isActive', JSON.stringify(userUpdate.isActive));
    if (userUpdate.avatar) {
      formData.append('avatar', userUpdate.avatar);
    }
    return formData;
  }

  private getRoleList() {
    this.subscriptions.push(
      this.roleService.getUsers().subscribe(
        (response: Role[]) => {
          this.roles = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    )
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'An error occurred. Please try again.');
    }
  }

  private clickButton(buttonId: string): void {
    document.getElementById(buttonId).click();
  }
}

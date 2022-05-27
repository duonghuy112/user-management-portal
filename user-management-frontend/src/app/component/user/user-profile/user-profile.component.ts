import { Router } from '@angular/router';
import { FileUploadStatus } from './../../../model/response/file-upload-status';
import { Role } from './../../../model/response/role';
import { RoleService } from './../../../service/role.service';
import { UserUpdate } from './../../../model/request/user-update';
import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { NotificationService } from './../../../service/notification.service';
import { NotificationType } from './../../../model/enum/notification-tpye.enum';
import { AuthenticationService } from './../../../service/authentication.service';
import { UserService } from './../../../service/user.service';
import { Subscription } from 'rxjs';
import { User } from './../../../model/response/user';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {
  @Output() updatedUser = new EventEmitter<User>();
  @Input() user: User;
  public userProfileForm: FormGroup;
  public roles: Role[];
  public refreshing = false;
  public fileName: string;
  public profileImage: File;
  public fileStatus = new FileUploadStatus();
  private subscriptions: Subscription[] = [];

  constructor(private userService: UserService,
              private roleService: RoleService,
              private authenticationService: AuthenticationService,
              private notificationService: NotificationService,
              private formBuilder: FormBuilder,
              private router: Router) { }

  ngOnInit(): void {
    this.setupUserProfileForm();
    this.setValueUserProfileForm();
    this.getRoleList();
  }

  onUpdateUserProfile() {
    const formDataUserUpdate = this.createFormData(this.userProfileForm.value);
    console.log(this.userProfileForm.value);
    this.subscriptions.push(
      this.userService.update(formDataUserUpdate).subscribe(
        (response: User) => {
          this.authenticationService.addUserToLocalCache(response);
          this.user = response;
          this.sendNotification(NotificationType.SUCCESS, `${response.firstName} ${response.lastName} updated successfully`);
          this.setValueUserProfileForm();
          this.updatedUser.emit(response);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    );
  }

  onUpdateUserAvatar() {
    const formData = new FormData();
    formData.append('id', this.user.id.toString());
    formData.append('avatar', this.profileImage);
    this.subscriptions.push(
      this.userService.updateAvatar(formData).subscribe(
        (event: HttpEvent<any>) => {
          this.reportUploadProgress(event);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.fileStatus.status = 'done';
        }
      )
    );
  }

  onProfileImageChange(fileName: string, profileImage: File): void {
    this.fileName =  fileName;
    this.profileImage = profileImage;
  }

  onOpenChangeAvatar(): void {
    this.clickButton('changeAvatar');
  }

  onLogOut(): void {
    this.authenticationService.logout();
    this.router.navigate(['/login']);
    this.sendNotification(NotificationType.SUCCESS, `You've been successfully logged out`);
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

  private setupUserProfileForm() {
    this.userProfileForm = this.formBuilder.group({
      id: ['', [Validators.required, Validators.minLength(0)]],
      username: ['', [Validators.required, Validators.minLength(0)]],
      firstName: ['', [Validators.required, Validators.minLength(0)]],
      lastName: ['', [Validators.required, Validators.minLength(0)]],
      email: ['', [Validators.required, Validators.minLength(0)]],
      roleId: ['', [Validators.required, Validators.minLength(0)]],
      isActive: [true]
    })
  }

  private setValueUserProfileForm() {
    this.userProfileForm.setValue({
      id: this.user.id,
      username: this.user.username,
      firstName: this.user.firstName,
      lastName: this.user.lastName,
      email: this.user.email,
      roleId: this.user.role.id,
      isActive: true
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
    formData.append('isActive', userUpdate.isActive.toString());
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

  private reportUploadProgress(event: HttpEvent<any>): void {
    switch (event.type) {
      case HttpEventType.UploadProgress:
        this.fileStatus.percentage = Math.round(100 * event.loaded / event.total);
        this.fileStatus.status = 'progress';
        break;
      case HttpEventType.Response:
        if (event.status === 200) {
          this.user.avatarUrl = `${event.body.avatarUrl}?time=${new Date().getTime()}`;
          this.sendNotification(NotificationType.SUCCESS, `${event.body.firstName}\'s profile image updated successfully`);
          this.fileStatus.status = 'done';
          break;
        } else {
          this.sendNotification(NotificationType.ERROR, `Unable to upload image. Please try again`);
          break;
        }
      default:
        `Finished all processes`;
    }
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

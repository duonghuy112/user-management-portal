import { UserAdd } from './../../../../model/request/user-add';
import { HttpErrorResponse } from '@angular/common/http';
import { User } from './../../../../model/response/user';
import { NotificationType } from './../../../../model/enum/notification-tpye.enum';
import { NotificationService } from './../../../../service/notification.service';
import { RoleService } from './../../../../service/role.service';
import { UserService } from './../../../../service/user.service';
import { Subscription } from 'rxjs';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Role } from './../../../../model/response/role';
import { Component, OnInit, EventEmitter, Output, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-user-add',
  templateUrl: './user-add.component.html',
  styleUrls: ['./user-add.component.css']
})
export class UserAddComponent implements OnInit, OnDestroy {
  @Output() refresh = new EventEmitter<boolean>();
  public roles: Role[];
  public fileName: string;
  public userAddFormGroup: FormGroup;
  private subscriptions: Subscription[] = [];

  constructor(private userService: UserService,
              private roleService: RoleService,
              private notificationService: NotificationService,
              private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.setupFormGroup();
    this.getRoleList();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  public onProfileImageChange(fileName: string, profileImage: File): void {
    this.fileName =  fileName;
    this.userAddFormGroup.patchValue({
      avatar: profileImage
    });
  }

  public saveNewUser(): void {
    this.clickButton('saveUserAdd');
  }

  public onAddNewUser(): void {
    const formDataUserAdd = this.createFormData(this.userAddFormGroup.value);
    this.subscriptions.push(
      this.userService.addByAdmin(formDataUserAdd).subscribe(
        (response: User) => {
          this.sendNotification(NotificationType.SUCCESS, `${response.firstName} ${response.lastName} added successfully`);
          this.closeUserAddModal();
          this.refresh.emit(true);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    );
  }

  public closeUserAddModal() {
    this.clickButton('closeAddUserModal');
    this.fileName = null;
    this.userAddFormGroup.reset({
      username: '',
      firstName: '',
      lastName: '',
      email: '',
      roleId: '',
      isActive: false,
      avatar: ''
    });
  }

  private setupFormGroup() {
    this.userAddFormGroup = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(0)]],
      firstName: ['', [Validators.required, Validators.minLength(0)]],
      lastName: ['', [Validators.required, Validators.minLength(0)]],
      email: ['', [Validators.required, Validators.minLength(0)]],
      roleId: ['', [Validators.required, Validators.minLength(0)]],
      isActive: [false, [Validators.required]],
      avatar: ['']
    });
  }

  private createFormData(userAdd: UserAdd): FormData {
    const formData = new FormData();
    formData.append('username', userAdd.username);
    formData.append('firstName', userAdd.firstName);
    formData.append('lastName', userAdd.lastName);
    formData.append('email', userAdd.email);
    formData.append('roleId', userAdd.roleId.toString());
    formData.append('isActive', JSON.stringify(userAdd.isActive));
    if (userAdd.avatar) {
      formData.append('avatar', userAdd.avatar);
    }
    return formData;
  }

  private getRoleList() {
    this.subscriptions.push(
      this.roleService.getUsers().subscribe(
        (response: Role[]) => {
          this.roles = response.filter(role => (role.id !=4 && role.id != 5));
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

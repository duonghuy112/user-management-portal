import { HttpErrorResponse } from '@angular/common/http';
import { NotificationService } from './../../../service/notification.service';
import { NotificationType } from './../../../model/enum/notification-tpye.enum';
import { User } from './../../../model/response/user';
import { Subscription } from 'rxjs';
import { UserService } from './../../../service/user.service';
import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {
  @Input() user: User;
  public users: User[];
  public refreshing = false;
  public selectedUserId: number;
  public selectedUserName: string;
  private subscriptions: Subscription[] = [];

  constructor(private userService: UserService,
              private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.onGetUsers(true);
  }

  onGetUsers(showNotification: boolean) {
    this.refreshing = true;
    this.subscriptions.push(
      this.userService.getUsers(this.user.id).subscribe(
        (response: User[]) => {
          this.users = response;
          this.refreshing = false;
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} user(s) loaded successfully.`);
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;
        }
      )
    );
  }

  onSelectUserId(id: number) {
    this.selectedUserId = id;
    this.clickButton('openUserInfo');
  }

  refreshUsers(isRefresh: boolean) {
    if (isRefresh) {
      this.onGetUsers(false);
    }
  }

  onSelectUserUpdateId(id: number) {
    this.selectedUserId = id;
    this.clickButton('openUserEdit'); 
  }
  
  onSelectDeleteUserId(id: number, firstName: string, lastName: string) {
    this.selectedUserId = id;
    this.selectedUserName = `${firstName} ${lastName}`;
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

import { HttpErrorResponse } from '@angular/common/http';
import { UserService } from './../../../service/user.service';
import { Subscription } from 'rxjs';
import { UserChangePassword } from './../../../model/request/user-change-password';
import { NotificationService } from './../../../service/notification.service';
import { NotificationType } from './../../../model/enum/notification-tpye.enum';
import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { NgForm } from '@angular/forms';
import { HttpResponseCustom } from 'src/app/model/response/http-response';

@Component({
  selector: 'app-user-change-password',
  templateUrl: './user-change-password.component.html',
  styleUrls: ['./user-change-password.component.css']
})
export class UserChangePasswordComponent implements OnInit, OnDestroy {
  @Input() userId: number;
  public refreshing = false;
  private subscriptions: Subscription[] = [];

  constructor(private userService: UserService,
              private notificationService: NotificationService) { }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  onChangePassword(changePasswordForm: NgForm) {
    this.refreshing = true;
    const newPassword = changePasswordForm.value['newPassword'];
    const reNewPassword = changePasswordForm.value['reNewPassword'];
    if (newPassword != reNewPassword) {
      this.sendNotification(NotificationType.WARNING, 'Re-type password is not correct!');
      this.refreshing = false;
    } else {
      const currentPassword = changePasswordForm.value['currentPassword'];
      const userChangePassword = new UserChangePassword(this.userId, currentPassword, newPassword);
      this.subscriptions.push(
        this.userService.changePassword(userChangePassword).subscribe(
          (response: HttpResponseCustom) => {
              this.sendNotification(NotificationType.SUCCESS, response.message);
              this.refreshing = false;
            },
            (error: HttpErrorResponse) => {
              this.sendNotification(NotificationType.WARNING, error.error.message);
              this.refreshing = false;
            },
            () => changePasswordForm.reset()
        )
      );
    }
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'An error occurred. Please try again.');
    }
  }
}

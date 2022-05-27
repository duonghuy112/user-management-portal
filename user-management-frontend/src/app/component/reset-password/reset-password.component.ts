import { HttpResponseCustom } from './../../model/response/http-response';
import { UserResetPassword } from './../../model/request/user-reset-password';
import { User } from './../../model/response/user';
import { HttpErrorResponse } from '@angular/common/http';
import { UserRegister } from './../../model/request/user-register';
import { NotificationType } from './../../model/enum/notification-tpye.enum';
import { NotificationService } from './../../service/notification.service';
import { AuthenticationService } from './../../service/authentication.service';
import { UserService } from './../../service/user.service';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Component, OnInit, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit, OnDestroy {
  public showLoading: boolean;
  private subscriptions: Subscription[] = [];

  constructor(private router: Router, 
              private userService: UserService,
              private authenticationService: AuthenticationService,
              private notificationService: NotificationService) {}

  ngOnInit(): void {
    if (this.authenticationService.isLoggedIn()) {
      this.router.navigateByUrl('/user-management');
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  public onResetPassword(userResetPassword: UserResetPassword): void {
    this.showLoading = true;
    this.subscriptions.push(
      this.userService.resetPassword(userResetPassword).subscribe(
        (response: HttpResponseCustom) => {
          this.showLoading = false;
          this.sendNotification(NotificationType.SUCCESS, 
            `${response.message}.
            Please check your email for password to log in.`);
          this.router.navigateByUrl('/login');
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.showLoading = false;
        }
      )
    );
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'An error occurred. Please try again.');
    }
  }
}
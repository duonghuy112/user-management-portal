import { NotificationType } from './../../model/enum/notification-tpye.enum';
import { HttpErrorResponse } from '@angular/common/http';
import { User } from './../../model/response/user';
import { UserService } from './../../service/user.service';
import { UserRegister } from './../../model/request/user-register';
import { NotificationService } from './../../service/notification.service';
import { AuthenticationService } from './../../service/authentication.service';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
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

  public onRegister(userRegister: UserRegister): void {
    this.showLoading = true;
    this.subscriptions.push(
      this.userService.register(userRegister).subscribe(
        (response: User) => {
          this.showLoading = false;
          this.sendNotification(NotificationType.SUCCESS, 
            `A new account was created for ${response.firstName}.
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

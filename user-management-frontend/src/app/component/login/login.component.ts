import { HeaderType } from './../../model/enum/header-type.enum';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { UserLogin } from './../../model/request/user-login';
import { User } from './../../model/response/user';
import { NotificationService } from './../../service/notification.service';
import { AuthenticationService } from './../../service/authentication.service';
import { Router } from '@angular/router';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { NotificationType } from 'src/app/model/enum/notification-tpye.enum';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  public showLoading = false;
  private subscription: Subscription[] = [];

  constructor(private router: Router,
              private authenticationService: AuthenticationService,
              private notificationService: NotificationService) { }
  
  ngOnInit(): void {
    if (this.authenticationService.isLoggedIn()) {
      this.router.navigateByUrl('/user-management');
    } else {
      this.router.navigateByUrl('/login');
    }
  }
  
  ngOnDestroy(): void {
    this.subscription.forEach(sub => sub.unsubscribe());
  }

  public onLogin(userLogin: UserLogin): void {
    this.showLoading = true;
    this.subscription.push(
      this.authenticationService.login(userLogin).subscribe(
        (response: HttpResponse<User>) => {
          const token = response.headers.get(HeaderType.JWT_TOKEN);
          const user = response.body;
          this.authenticationService.saveToken(token);
          this.authenticationService.addUserToLocalCache(user);
          this.router.navigateByUrl('/user-management');
          this.showLoading = false;
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

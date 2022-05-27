import { NotificationType } from './../../../../model/enum/notification-tpye.enum';
import { HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { User } from './../../../../model/response/user';
import { NotificationService } from './../../../../service/notification.service';
import { UserService } from './../../../../service/user.service';
import { Component, OnInit, Input, OnChanges, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-user-info',
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.css']
})
export class UserInfoComponent implements OnInit, OnChanges, OnDestroy {
  @Input() userId: number;
  public selectedUser: User;
  private subscriptions: Subscription[] = [];

  constructor(private userService: UserService, 
              private notificationService: NotificationService) { }

  ngOnChanges(): void {
    if (this.userId) {
		  this.onSelectUser(this.userId);
		}
  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  onSelectUser(id: number) {
    this.subscriptions.push(
      this.userService.getUser(id).subscribe(
        (response: User) => {
          this.selectedUser = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
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

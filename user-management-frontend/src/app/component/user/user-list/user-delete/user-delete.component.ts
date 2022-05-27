import { HttpErrorResponse } from '@angular/common/http';
import { NotificationType } from './../../../../model/enum/notification-tpye.enum';
import { HttpResponseCustom } from './../../../../model/response/http-response';
import { NotificationService } from './../../../../service/notification.service';
import { UserService } from './../../../../service/user.service';
import { Subscription } from 'rxjs';
import { Component, OnInit, Input, Output, EventEmitter, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-user-delete',
  templateUrl: './user-delete.component.html',
  styleUrls: ['./user-delete.component.css']
})
export class UserDeleteComponent implements OnInit, OnDestroy {
  @Input() userId: number;
  @Input() fullname: string;
  @Output() refresh = new EventEmitter<boolean>();
  private subscriptions: Subscription[] = [];

  constructor(private userService: UserService, 
              private notificationService: NotificationService) { }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  onDeleteUser() {
    this.subscriptions.push(
      this.userService.delete(this.userId).subscribe(
        (response: HttpResponseCustom) => {
          this.sendNotification(NotificationType.SUCCESS, response.message);
          this.refresh.emit(true);
          this.clickButton('closeDeleteUserModal');
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

  private clickButton(buttonId: string): void {
    document.getElementById(buttonId).click();
  }
}

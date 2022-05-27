import { NotificationType } from '../model/enum/notification-tpye.enum';
import { Injectable } from '@angular/core';
import { NotifierService } from 'angular-notifier';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private notifier: NotifierService) { }

  public notify(type: NotificationType, message: string) {
    this.notifier.notify(type, message);
  }
}
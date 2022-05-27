import { User } from './../../model/response/user';
import { AuthenticationService } from './../../service/authentication.service';
import { BehaviorSubject } from 'rxjs';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {
  private titleSubject = new BehaviorSubject<string>('Users');
  public titleAction$ =  this.titleSubject.asObservable();
  public user: User;
  public userLoginId: number;

  constructor(private authenticationService: AuthenticationService) { }

  ngOnInit(): void {
    this.user = this.authenticationService.getUserFromLocalCache();
    this.userLoginId = this.user.id;
  }

  onChangeTitle(title: string) {
    this.titleSubject.next(title);
  }

  refreshUser(updatedUser: User) {
    this.user = updatedUser;
  }
}

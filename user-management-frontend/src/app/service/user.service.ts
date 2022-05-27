import { UserResetPassword } from './../model/request/user-reset-password';
import { UserChangePassword } from './../model/request/user-change-password';
import { UserRegister } from './../model/request/user-register';
import { User } from './../model/response/user';
import { Observable } from 'rxjs';
import { HttpClient, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpResponseCustom } from '../model/response/http-response';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public getUsers(id: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.host}/user/list?id=${id}`);
  }

  public getUser(id: number): Observable<User> {
    return this.http.get<User>(`${this.host}/user/${id}`);
  }

  public register(userRegister: UserRegister): Observable<User> {
    return this.http.post<User>(`${this.host}/user/register`, userRegister);
  }

  public addByAdmin(formDataUserAdd: FormData): Observable<User> {
    return this.http.post<User>(`${this.host}/user/add`, formDataUserAdd);
  }

  public resetPassword(userResetPassword: UserResetPassword): Observable<HttpResponseCustom> {
    return this.http.post<HttpResponseCustom>(`${this.host}/user/reset-password`, userResetPassword);
  }

  public update(formDataUserUpdate: FormData): Observable<User> {
    return this.http.put<User>(`${this.host}/user/update/${formDataUserUpdate.get('id')}`, formDataUserUpdate);
  }
  
  public updateAvatar(formDataUserUpdateAvatar: FormData): Observable<HttpEvent<User>> {
    return this.http.patch<User>(`${this.host}/user/update-avatar/${formDataUserUpdateAvatar.get('id')}`, formDataUserUpdateAvatar, {reportProgress: true, observe: 'events'});
  }

  public changePassword(userChangePassword: UserChangePassword): Observable<HttpResponseCustom> {
    return this.http.patch<HttpResponseCustom>(`${this.host}/user/change-password/${userChangePassword.id}`, userChangePassword);
  }

  public delete(id: number): Observable<HttpResponseCustom> {
    return this.http.delete<HttpResponseCustom>(`${this.host}/user/delete/${id}`);
  }
}
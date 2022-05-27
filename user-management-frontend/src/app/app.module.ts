import { RoleService } from './service/role.service';
import { NotificationService } from './service/notification.service';
import { NotificationModule } from './notification.module';
import { AuthenticationGuard } from './guard/authentication.guard';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthenticationService } from './service/authentication.service';
import { UserService } from './service/user.service';
import { AuthInterceptor } from './interceptor/auth.interceptor';
import { RegisterComponent } from './component/register/register.component';
import { LoginComponent } from './component/login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UserComponent } from './component/user/user.component';
import { UserListComponent } from './component/user/user-list/user-list.component';
import { ResetPasswordComponent } from './component/reset-password/reset-password.component';
import { UserChangePasswordComponent } from './component/user/user-change-password/user-change-password.component';
import { UserProfileComponent } from './component/user/user-profile/user-profile.component';
import { UserInfoComponent } from './component/user/user-list/user-info/user-info.component';
import { UserAddComponent } from './component/user/user-list/user-add/user-add.component';
import { UserUpdateComponent } from './component/user/user-list/user-update/user-update.component';
import { UserDeleteComponent } from './component/user/user-list/user-delete/user-delete.component';

@NgModule({
  declarations: [
    AppComponent,
    RegisterComponent,
    LoginComponent,
    UserComponent,
    UserListComponent,
    ResetPasswordComponent,
    UserChangePasswordComponent,
    UserProfileComponent,
    UserInfoComponent,
    UserAddComponent,
    UserUpdateComponent,
    UserDeleteComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    HttpClientModule,
    NotificationModule,
  ],
  providers: [AuthenticationGuard, AuthenticationService, UserService, RoleService, NotificationService,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }],
  bootstrap: [AppComponent]
})
export class AppModule { }

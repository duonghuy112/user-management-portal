package nguyenduonghuy.usermanagement.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserServiceImplConstant {

	public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String NO_USER_FOUND_BY_ID = "No user found by id: ";
    public static final String NO_USER_FOUND_BY_USERNAME = "No user found by username: ";
    public static final String NO_USER_FOUND_BY_EMAIL = "No user found for email: ";
    public static final String FOUND_USER_BY_USERNAME = "Returning found user by username: ";
    public static final String PASSWORD_INCORRECT = "Password is not incorrect!";
    public static final String USERNAME_EMAIL_INCORRECT = "Username/Email is not incorrect!";
}

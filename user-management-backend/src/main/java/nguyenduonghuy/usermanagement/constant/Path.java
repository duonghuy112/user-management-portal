package nguyenduonghuy.usermanagement.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Path {

	public static final String LOGIN_URL = "/user/login";
	public static final String REGISTER_URL = "/user/register";
	public static final String RESET_PASSWORD_URL = "/user/reset-password/**";
	public static final String USER_IMAGE_URL = "/user/image/**";
}

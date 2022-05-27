package nguyenduonghuy.usermanagement.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailConstant {

	public static final String SIMPLE_MAIL_TRANSFER_PROTOCOL = "smtps";
    public static final String USERNAME = "duoonghuy1123@gmail.com";
    public static final String PASSWORD = "******";
    public static final String FROM_EMAIL = "duoonghuy1123@gmail.com";
    public static final String EMAIL_SUBJECT = "User Management Portal - New Password";
    public static final String GMAIL_SMTP_SERVER = "smtp.gmail.com";
    public static final String SMTP_HOST = "mail.smtp.host";
    public static final String SMTP_AUTH = "mail.smtp.auth";
    public static final String SMTP_PORT = "mail.smtp.port";
    public static final int DEFAULT_PORT = 465;
    public static final String SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    public static final String SMTP_STARTTLS_REQUIRED = "mail.smtp.starttls.required";
    public static final String EMAIL_SENT = "An email with a new password was sent to: ";

    public static String contentText(String firstName, String password) {
		return "Hello " + firstName + ", \n \n Your new account password is: " + password + "\n \n Administrator - User Management Portal";
	}
}
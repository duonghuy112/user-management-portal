package nguyenduonghuy.usermanagement.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import nguyenduonghuy.usermanagement.service.LoginAttemptService;

@Component
public final class AuthenticationFailureListener {

	@Autowired
	private LoginAttemptService loginAttemptService;

	@EventListener
	public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
		Object principal = event.getAuthentication().getPrincipal();
		if (principal instanceof String) {
			String username = (String) principal;
			loginAttemptService.addUserToLoginAttemptCache(username);
		}
	}
}
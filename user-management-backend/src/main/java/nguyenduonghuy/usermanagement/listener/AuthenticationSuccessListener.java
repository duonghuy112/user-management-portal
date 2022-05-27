package nguyenduonghuy.usermanagement.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import nguyenduonghuy.usermanagement.domain.dto.UserPrincipal;
import nguyenduonghuy.usermanagement.service.LoginAttemptService;

@Component
public final class AuthenticationSuccessListener {

	@Autowired
	private LoginAttemptService loginAttemptService;

	@EventListener
	public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
		Object principal = event.getAuthentication().getPrincipal();
		if (principal instanceof UserPrincipal) {
			UserPrincipal user = (UserPrincipal) principal;
			loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
		}
	}
}
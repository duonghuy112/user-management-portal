package nguyenduonghuy.usermanagement.domain.dto;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nguyenduonghuy.usermanagement.domain.entity.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

	private static final long serialVersionUID = 1L;

	private User user;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.user.getRole().getAuthorities()
						.stream()
						.map(authority -> new SimpleGrantedAuthority(authority.getDescription()))
						.collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		return this.user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.user.getIsActive();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.user.getIsActive();
	}
}

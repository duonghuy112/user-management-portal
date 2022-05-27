package nguyenduonghuy.usermanagement.domain.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nguyenduonghuy.usermanagement.domain.entity.Role;
import nguyenduonghuy.usermanagement.domain.entity.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

	private Long id;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String avatarUrl;
	private LocalDateTime lastLoginDate;
	private LocalDateTime joinDate;
	private Boolean isActive;
	private Role role;

	public UserResponse(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.email = user.getEmail();
		this.avatarUrl = user.getAvatarUrl();
		this.lastLoginDate = user.getLastLoginDate();
		this.joinDate = user.getJoinDate();
		this.isActive = user.getIsActive();
		this.role = user.getRole();
	}
}

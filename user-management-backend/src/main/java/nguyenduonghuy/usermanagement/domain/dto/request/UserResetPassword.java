package nguyenduonghuy.usermanagement.domain.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResetPassword {

	@NotBlank(message = "username is mandatory")
	private String username;

	@Email(message = "email is incorrect format")
	@NotBlank(message = "email is mandatory")
	private String email;
}

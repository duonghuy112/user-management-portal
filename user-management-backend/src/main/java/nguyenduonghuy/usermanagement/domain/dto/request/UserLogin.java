package nguyenduonghuy.usermanagement.domain.dto.request;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLogin {

	@NotBlank(message = "username is mandatory")
	private String username;

	@NotBlank(message = "password is mandatory")
	private String password;
}

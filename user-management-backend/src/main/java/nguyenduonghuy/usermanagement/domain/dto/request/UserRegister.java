package nguyenduonghuy.usermanagement.domain.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegister {

	@NotBlank(message = "username is mandatory")
	@Size(min=2, max=45, message = "username must be longer than 2 and less than 45 characters")
	private String username;

	@NotBlank(message = "firstName is mandatory")
	@Size(min=2, max=45, message = "firstName must be longer than 2 and less than 45 characters")
	private String firstName;

	@NotBlank(message = "lastName is mandatory")
	@Size(min=2, max=45, message = "lastName must be longer than 2 and less than 45 characters")
	private String lastName;

	@Email(message = "email is incorrect format")
	@NotBlank(message = "email is mandatory")
	@Size(min=2, max=45, message = "email must be longer than 2 and less than 45 characters")
	private String email;
}
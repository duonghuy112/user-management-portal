package nguyenduonghuy.usermanagement.domain.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChangePassword {

	@NotNull(message = "id is mandatory")
	private Long id;
	
	@NotBlank(message = "currentPassword is mandatory")
	@Size(min=6, max=24, message = "newPassword must be longer than 6 and less than 24 characters")
	private String currentPassword;

	@NotBlank(message = "newPassword is mandatory")
	@Size(min=6, max=24, message = "newPassword must be longer than 6 and less than 24 characters")
	private String newPassword;
}

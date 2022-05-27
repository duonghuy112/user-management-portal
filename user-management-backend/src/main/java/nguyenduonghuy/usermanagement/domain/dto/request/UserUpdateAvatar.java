package nguyenduonghuy.usermanagement.domain.dto.request;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateAvatar {

	@NotNull(message = "username is mandatory")
	private Long id;

	@NotNull(message = "avatar is mandatory")
	private MultipartFile avatar;
}

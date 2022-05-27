package nguyenduonghuy.usermanagement.controller;

import static nguyenduonghuy.usermanagement.constant.EmailConstant.EMAIL_SENT;
import static nguyenduonghuy.usermanagement.constant.ExceptionMessage.CHANGE_PASSWORD_SUCCESSFULLY;
import static nguyenduonghuy.usermanagement.constant.ExceptionMessage.USER_DELETED_SUCCESSFULLY;
import static nguyenduonghuy.usermanagement.constant.FileConstant.FORWARD_SLASH;
import static nguyenduonghuy.usermanagement.constant.FileConstant.TEMP_PROFILE_IMAGE_BASE_URL;
import static nguyenduonghuy.usermanagement.constant.FileConstant.USER_FOLDER;
import static nguyenduonghuy.usermanagement.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import nguyenduonghuy.usermanagement.domain.dto.UserPrincipal;
import nguyenduonghuy.usermanagement.domain.dto.request.UserAdd;
import nguyenduonghuy.usermanagement.domain.dto.request.UserChangePassword;
import nguyenduonghuy.usermanagement.domain.dto.request.UserLogin;
import nguyenduonghuy.usermanagement.domain.dto.request.UserRegister;
import nguyenduonghuy.usermanagement.domain.dto.request.UserResetPassword;
import nguyenduonghuy.usermanagement.domain.dto.request.UserUpdate;
import nguyenduonghuy.usermanagement.domain.dto.request.UserUpdateAvatar;
import nguyenduonghuy.usermanagement.domain.dto.response.HttpResponse;
import nguyenduonghuy.usermanagement.domain.dto.response.UserResponse;
import nguyenduonghuy.usermanagement.exception.EmailExistException;
import nguyenduonghuy.usermanagement.exception.EmailNotFoundException;
import nguyenduonghuy.usermanagement.exception.NotAnImageFileException;
import nguyenduonghuy.usermanagement.exception.PasswordIncorrectException;
import nguyenduonghuy.usermanagement.exception.UserNotFoundException;
import nguyenduonghuy.usermanagement.exception.UsernameEmailIncorrectException;
import nguyenduonghuy.usermanagement.exception.UsernameExistException;
import nguyenduonghuy.usermanagement.service.UserService;
import nguyenduonghuy.usermanagement.utils.JwtTokenProvider;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@CrossOrigin("http://localhost:4200")
public class UserController {

	private UserService userService;
	private AuthenticationManager authenticationManager;
	private JwtTokenProvider jwtTokenProvider;

	@GetMapping("/list")
	public ResponseEntity<List<UserResponse>> getAll(@RequestParam Long id) {
		List<UserResponse> userResponses = userService.getAll(id);
		return new ResponseEntity<>(userResponses, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> get(@PathVariable Long id) {
		UserResponse userResponse = userService.getById(id);
		return new ResponseEntity<>(userResponse, HttpStatus.OK);
	}

	@GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getAvatar(@PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

	@GetMapping(path = "/image/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempAvatar(@PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

	@PostMapping("/login")
	public ResponseEntity<UserResponse> login(@RequestBody @Valid UserLogin userLogin) {
		this.authenticate(userLogin);
		UserPrincipal userPrincipal = (UserPrincipal) userService.loadUserByUsername(userLogin.getUsername());
		HttpHeaders jwtHeaders = this.setHttpHeaders(userPrincipal);
		UserResponse userResponse = userService.getByUsername(userLogin.getUsername());
		return new ResponseEntity<>(userResponse, jwtHeaders, HttpStatus.OK);
	}

	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRegister userRegister) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
		UserResponse userResponse = userService.register(userRegister);
		return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
	}

	@PostMapping("/add")
	@PreAuthorize("hasAnyAuthority('user:create')")
	public ResponseEntity<UserResponse> addByAdmin(@ModelAttribute @Valid UserAdd userAdd) throws UserNotFoundException, UsernameExistException, EmailExistException, NotAnImageFileException, IOException, MessagingException {
		UserResponse userResponse = userService.addByAdmin(userAdd);
		return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
	}

	@PostMapping("/reset-password")
    public ResponseEntity<HttpResponse> resetPassword(@RequestBody UserResetPassword userResetPassword) throws EmailNotFoundException, MessagingException, UsernameEmailIncorrectException {
		userService.resetPassword(userResetPassword);
		HttpResponse httpResponse = this.setHttpResponse(HttpStatus.OK, EMAIL_SENT + userResetPassword.getEmail());
        return new ResponseEntity<>(httpResponse, HttpStatus.OK);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<UserResponse> update(@PathVariable Long id, @ModelAttribute @Valid UserUpdate userUpdate) throws UserNotFoundException, UsernameExistException, EmailExistException, NotAnImageFileException, IOException {
		UserResponse userResponse = userService.update(id, userUpdate);
		return new ResponseEntity<>(userResponse, HttpStatus.OK);
	}

	@PatchMapping("/update-avatar/{id}")
	public ResponseEntity<UserResponse> updateAvatar(@PathVariable Long id, @ModelAttribute @Valid UserUpdateAvatar userUpdateAvatar) throws UserNotFoundException, UsernameExistException, EmailExistException, NotAnImageFileException, IOException {
		UserResponse userResponse = userService.updateAvatar(id, userUpdateAvatar);
		return new ResponseEntity<>(userResponse, HttpStatus.OK);
	}

	@PatchMapping("/change-password/{id}")
	public ResponseEntity<HttpResponse> changePassword(@PathVariable Long id, @RequestBody @Valid UserChangePassword userChangePassword) throws PasswordIncorrectException {
		userService.changePassword(id, userChangePassword);
		HttpResponse httpResponse = this.setHttpResponse(HttpStatus.OK, CHANGE_PASSWORD_SUCCESSFULLY);
        return new ResponseEntity<>(httpResponse, HttpStatus.OK);
	}

	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasAnyAuthority('user:delete')")
	public ResponseEntity<HttpResponse> delete(@PathVariable Long id) throws IOException {
		userService.delete(id);
		HttpResponse httpResponse = this.setHttpResponse(HttpStatus.OK, USER_DELETED_SUCCESSFULLY);
		return new ResponseEntity<>(httpResponse, HttpStatus.OK);
	}

	private void authenticate(UserLogin userLogin) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getUsername(), userLogin.getPassword()));
	}

	private HttpHeaders setHttpHeaders(UserPrincipal userPrincipal) {
		HttpHeaders jwtHeaders = new HttpHeaders();
		jwtHeaders.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
		return jwtHeaders;
	}

	private HttpResponse setHttpResponse(HttpStatus httpStatus, String message) {
		return HttpResponse.builder()
							.timeStamp(LocalDateTime.now())
							.httpStatusCode(httpStatus.value())
							.httpStatus(httpStatus)
							.reason(httpStatus.getReasonPhrase().toUpperCase())
							.message(message)
							.build();
	}
}
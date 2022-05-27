package nguyenduonghuy.usermanagement.service.impl;

import static nguyenduonghuy.usermanagement.constant.UserServiceImplConstant.*;
import static nguyenduonghuy.usermanagement.constant.FileConstant.*;
import static org.springframework.http.MediaType.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nguyenduonghuy.usermanagement.constant.Status;
import nguyenduonghuy.usermanagement.domain.dto.UserPrincipal;
import nguyenduonghuy.usermanagement.domain.dto.request.UserAdd;
import nguyenduonghuy.usermanagement.domain.dto.request.UserChangePassword;
import nguyenduonghuy.usermanagement.domain.dto.request.UserRegister;
import nguyenduonghuy.usermanagement.domain.dto.request.UserResetPassword;
import nguyenduonghuy.usermanagement.domain.dto.request.UserUpdate;
import nguyenduonghuy.usermanagement.domain.dto.request.UserUpdateAvatar;
import nguyenduonghuy.usermanagement.domain.dto.response.UserResponse;
import nguyenduonghuy.usermanagement.domain.entity.Role;
import nguyenduonghuy.usermanagement.domain.entity.User;
import nguyenduonghuy.usermanagement.exception.EmailExistException;
import nguyenduonghuy.usermanagement.exception.EmailNotFoundException;
import nguyenduonghuy.usermanagement.exception.NotAnImageFileException;
import nguyenduonghuy.usermanagement.exception.PasswordIncorrectException;
import nguyenduonghuy.usermanagement.exception.UserNotFoundException;
import nguyenduonghuy.usermanagement.exception.UsernameEmailIncorrectException;
import nguyenduonghuy.usermanagement.exception.UsernameExistException;
import nguyenduonghuy.usermanagement.repository.RoleRepository;
import nguyenduonghuy.usermanagement.repository.UserRepository;
import nguyenduonghuy.usermanagement.service.EmailService;
import nguyenduonghuy.usermanagement.service.LoginAttemptService;
import nguyenduonghuy.usermanagement.service.UserService;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private PasswordEncoder passwordEncoder;
	private LoginAttemptService loginAttemptService;
	private EmailService emailService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = this.findByUsername(username);
		this.validateLoginAttempt(user);
		user.setLastLoginDate(LocalDateTime.now());
		userRepository.save(user); // save last login date
		UserPrincipal userPrincipal = new UserPrincipal(user);
		log.info(FOUND_USER_BY_USERNAME + username);
		return userPrincipal;
	}

	@Override
	public List<UserResponse> getAll(Long id) {
		return userRepository.findAllByIsDeleted(Status.WORKING, PageRequest.of(0, 100))
							.toList()
							.stream()
							.filter(user -> id != user.getId())
							.map(UserResponse::new)
							.collect(Collectors.toList());
	}

	@Override
	public UserResponse getById(Long id) throws UserNotFoundException {
		User user = this.findById(id);
		return new UserResponse(user);
	}

	@Override
	public UserResponse getByUsername(String username) {
		User user = this.findByUsername(username);
		return new UserResponse(user);
	}

	@Override
	public UserResponse getByEmail(String email) {
		User user = this.findByEmail(email);
		return new UserResponse(user);
	}

	@Override
	public UserResponse register(UserRegister userRegister) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException {
		this.validateNewUsernameAndEmail(userRegister.getUsername(), userRegister.getEmail());
		String password = this.generatePassword();
		User user = User.builder()
						.username(userRegister.getUsername())
						.firstName(userRegister.getFirstName())
						.lastName(userRegister.getLastName())
						.email(userRegister.getEmail())
						.avatarUrl(this.getTempAvatarUrl(userRegister.getUsername()))
						.password(this.encodePassword(password))
						.joinDate(LocalDateTime.now())
						.isActive(Status.ACTIVE)
						.isDeleted(Status.WORKING)
						.role(roleRepository.getById(Role.USER))
						.build();
		userRepository.save(user);
		log.info("New user's password: " + password);
		// emailService.sendNewPassword(user.getFirstName(), password, user.getEmail());
		return new UserResponse(user);
	}

	@Override
	public UserResponse addByAdmin(UserAdd userAdd) throws UserNotFoundException, UsernameExistException,
			EmailExistException, IOException, NotAnImageFileException, MessagingException {
		this.validateNewUsernameAndEmail(userAdd.getUsername(), userAdd.getEmail());
		String password = this.generatePassword();
		User user = User.builder()
						.username(userAdd.getUsername())
						.firstName(userAdd.getFirstName())
						.lastName(userAdd.getLastName())
						.email(userAdd.getEmail())
						.avatarUrl(Objects.nonNull(userAdd.getAvatar()) ? this.getAvatarUrl(userAdd.getUsername()) : this.getTempAvatarUrl(userAdd.getUsername()))
						.password(this.encodePassword(password))
						.joinDate(LocalDateTime.now())
						.isActive(userAdd.getIsActive())
						.isDeleted(Status.WORKING)
						.role(roleRepository.getById(userAdd.getRoleId()))
						.build();
		userRepository.save(user);
		this.saveAvatar(user, userAdd.getAvatar());
		log.info("New user's password: " + password);
		// emailService.sendNewPassword(user.getFirstName(), password, user.getEmail());
		return new UserResponse(user);
	}

	@Override
	public UserResponse update(Long id, UserUpdate userUpdate) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
		this.validateUpdateUsernameAndEmail(id, userUpdate.getUsername(), userUpdate.getEmail());
		User user = this.findById(id);
		user = User.builder()
				.id(id)
				.username(userUpdate.getUsername())
				.firstName(userUpdate.getFirstName())
				.lastName(userUpdate.getLastName())
				.email(userUpdate.getEmail())
				.avatarUrl(Objects.nonNull(userUpdate.getAvatar()) ? this.getAvatarUrl(userUpdate.getUsername()) : user.getAvatarUrl())
				.password(user.getPassword())
				.joinDate(user.getJoinDate())
				.lastLoginDate(user.getLastLoginDate())
				.isActive(userUpdate.getIsActive())
				.isDeleted(Status.WORKING)
				.role(roleRepository.getById(userUpdate.getRoleId()))
				.build();
		userRepository.save(user);
		this.saveAvatar(user, userUpdate.getAvatar());
		return new UserResponse(user);
	}

	@Override
	public UserResponse updateAvatar(Long id, UserUpdateAvatar userUpdateAvatar) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException {
		this.validateUpdateUsernameAndEmail(id, StringUtils.EMPTY, StringUtils.EMPTY);
		User user = this.findById(id);
		user.setAvatarUrl(this.getAvatarUrl(user.getUsername()));
		userRepository.save(user);
		this.saveAvatar(user, userUpdateAvatar.getAvatar());
		return new UserResponse(user);
	}

	@Override
	public void resetPassword(UserResetPassword userResetPassword) throws MessagingException, EmailNotFoundException, UsernameEmailIncorrectException {
		User user = this.findByEmail(userResetPassword.getEmail());
		if (!StringUtils.equalsIgnoreCase(userResetPassword.getUsername(), user.getUsername())) {
			throw new UsernameEmailIncorrectException(USERNAME_EMAIL_INCORRECT);
		}
		String password = this.generatePassword();
		user.setPassword(this.encodePassword(password));
		userRepository.save(user);
		log.info("New user's password: " + password);
		emailService.sendNewPassword(user.getFirstName(), password, user.getEmail());
	}

	@Override
	public void changePassword(Long id, UserChangePassword userChangePassword) throws PasswordIncorrectException {
		User user = this.findById(id);
		if (passwordEncoder.matches(userChangePassword.getCurrentPassword(), user.getPassword())) {
			user.setPassword(this.encodePassword(userChangePassword.getNewPassword()));
			userRepository.save(user);
		} else {
			throw new PasswordIncorrectException(PASSWORD_INCORRECT);
		}
	}

	@Override
	public void delete(Long id) throws IOException {
		User user = this.findById(id);
		user.setIsDeleted(Status.DELETED);
		Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
		FileUtils.deleteDirectory(new File(userFolder.toString()));
		userRepository.save(user);
	}

	private void validateNewUsernameAndEmail(String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByNewUsername = userRepository.findByUsernameAndIsDeleted(username, Status.WORKING);
        User userByNewEmail = userRepository.findByEmailAndIsDeleted(email, Status.WORKING);
        if(Objects.nonNull(userByNewUsername)) {
        	throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
        }
        if(Objects.nonNull(userByNewEmail)) {
        	throw new EmailExistException(EMAIL_ALREADY_EXISTS);
        }
	}

	private void validateUpdateUsernameAndEmail(Long id, String username, String email) {
		Optional<User> userById = userRepository.findById(id);
		User userByNewUsername = userRepository.findByUsernameAndIsDeleted(username, Status.WORKING);
        User userByNewEmail = userRepository.findByEmailAndIsDeleted(email, Status.WORKING);
        if(userById.isEmpty()) {
        	throw new UserNotFoundException(NO_USER_FOUND_BY_ID + id);
        }
        if (Objects.nonNull(userByNewUsername) && !id.equals(userByNewUsername.getId())) {
			throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
		}
		if (Objects.nonNull(userByNewEmail) && !id.equals(userByNewEmail.getId())) {
			throw new EmailExistException(EMAIL_ALREADY_EXISTS);
		}
	}

	private User findById(Long id) {
		return Optional.ofNullable(userRepository.findByIdAndIsDeleted(id, Status.WORKING))
						.orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND_BY_ID + id));
	}

	private User findByUsername(String username) {
		return Optional.ofNullable(userRepository.findByUsernameAndIsDeleted(username, Status.WORKING))
						.orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + username));
	}

	private User findByEmail(String email) {
		return Optional.ofNullable(userRepository.findByEmailAndIsDeleted(email, Status.WORKING))
						.orElseThrow(() -> new UserNotFoundException(NO_USER_FOUND_BY_EMAIL + email));
	}

	private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

	private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

	private void validateLoginAttempt(User user) {
		if (user.getIsActive()) {
			if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
				user.setIsActive(Status.LOCK);
			}
		} else {
			loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
		}
	}

	private void saveAvatar(User user, MultipartFile avatar) throws IOException, NotAnImageFileException {
        if (avatar != null) {
            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(avatar.getContentType())) {
                throw new NotAnImageFileException(avatar.getOriginalFilename() + NOT_AN_IMAGE_FILE);
            }
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if(!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                log.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));
            Files.copy(avatar.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION), StandardCopyOption.REPLACE_EXISTING);
            log.info(FILE_SAVED_IN_FILE_SYSTEM + avatar.getOriginalFilename());
        }
    }

	private String getAvatarUrl(String username) {
        return ServletUriComponentsBuilder
        		.fromCurrentContextPath()
        		.path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION)
        		.toUriString();
    }

	private String getTempAvatarUrl(String username) {
      return ServletUriComponentsBuilder
    		  .fromCurrentContextPath()
    		  .path(DEFAULT_USER_IMAGE_PATH + username)
    		  .toUriString();
	}
}
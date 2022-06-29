package nguyenduonghuy.usermanagement.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import nguyenduonghuy.usermanagement.domain.dto.request.UserAdd;
import nguyenduonghuy.usermanagement.domain.dto.request.UserChangePassword;
import nguyenduonghuy.usermanagement.domain.dto.request.UserRegister;
import nguyenduonghuy.usermanagement.domain.dto.request.UserResetPassword;
import nguyenduonghuy.usermanagement.domain.dto.request.UserUpdate;
import nguyenduonghuy.usermanagement.domain.dto.request.UserUpdateAvatar;
import nguyenduonghuy.usermanagement.domain.dto.response.UserResponse;
import nguyenduonghuy.usermanagement.exception.EmailExistException;
import nguyenduonghuy.usermanagement.exception.EmailNotFoundException;
import nguyenduonghuy.usermanagement.exception.NotAnImageFileException;
import nguyenduonghuy.usermanagement.exception.PasswordIncorrectException;
import nguyenduonghuy.usermanagement.exception.UserNotFoundException;
import nguyenduonghuy.usermanagement.exception.UsernameEmailIncorrectException;
import nguyenduonghuy.usermanagement.exception.UsernameExistException;

public interface UserService extends UserDetailsService {
	
	Page<UserResponse> getAllPaging(Long id, int pageNum, int pageSize, String keyword);

	List<UserResponse> getAll(Long id);

	UserResponse getById(Long id) throws UserNotFoundException;

	UserResponse getByUsername(String username) throws UserNotFoundException;

	UserResponse getByEmail(String email) throws UserNotFoundException;

	UserResponse register(UserRegister userRegister) throws UserNotFoundException, UsernameExistException, EmailExistException, MessagingException;

	UserResponse addByAdmin(UserAdd userAdd) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException, MessagingException;

	UserResponse update(Long id, UserUpdate userUpdate) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;

	UserResponse updateAvatar(Long id, UserUpdateAvatar userUpdateAvatar) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException;

    void resetPassword(UserResetPassword userResetPassword) throws MessagingException, EmailNotFoundException, UsernameEmailIncorrectException;
    
    void changePassword(Long id, UserChangePassword userChangePassword) throws PasswordIncorrectException;

    void delete(Long id) throws IOException;
}
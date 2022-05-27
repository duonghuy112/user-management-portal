package nguyenduonghuy.usermanagement.exception.handle;

import static nguyenduonghuy.usermanagement.constant.ExceptionMessage.*;
import static org.springframework.http.HttpStatus.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.NoResultException;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.auth0.jwt.exceptions.TokenExpiredException;

import lombok.extern.slf4j.Slf4j;
import nguyenduonghuy.usermanagement.domain.dto.response.HttpResponse;
import nguyenduonghuy.usermanagement.exception.EmailExistException;
import nguyenduonghuy.usermanagement.exception.EmailNotFoundException;
import nguyenduonghuy.usermanagement.exception.NotAnImageFileException;
import nguyenduonghuy.usermanagement.exception.PasswordIncorrectException;
import nguyenduonghuy.usermanagement.exception.UserNotFoundException;
import nguyenduonghuy.usermanagement.exception.UsernameEmailIncorrectException;
import nguyenduonghuy.usermanagement.exception.UsernameExistException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandle {

	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<HttpResponse> accountDisabledException() {
		log.error(ACCOUNT_LOCKED);
		return this.createHttpResponse(BAD_REQUEST, ACCOUNT_LOCKED);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<HttpResponse> badCredentialsException() {
		log.error(INCORRECT_CREDENTIALS);
		return this.createHttpResponse(BAD_REQUEST, INCORRECT_CREDENTIALS);
	}

	@ExceptionHandler(EmailExistException.class)
	public ResponseEntity<HttpResponse> emailExistException(EmailExistException exception) {
		log.error(exception.getMessage());
		return this.createHttpResponse(BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(EmailNotFoundException.class)
	public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception) {
		log.error(exception.getMessage());
		return this.createHttpResponse(BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(UsernameExistException.class)
	public ResponseEntity<HttpResponse> usernameExistException(UsernameExistException exception) {
		log.error(exception.getMessage());
		return this.createHttpResponse(BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException exception) {
		log.error(exception.getMessage());
		return this.createHttpResponse(BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(PasswordIncorrectException.class)
	public ResponseEntity<HttpResponse> passwordIncorrectException(PasswordIncorrectException exception) {
		log.error(exception.getMessage());
		return this.createHttpResponse(BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(UsernameEmailIncorrectException.class)
	public ResponseEntity<HttpResponse> usernameEmailIncorrectException(UsernameEmailIncorrectException exception) {
		log.error(exception.getMessage());
		return this.createHttpResponse(BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(NotAnImageFileException.class)
	public ResponseEntity<HttpResponse> notAnImageFileException(NotAnImageFileException exception) {
		log.error(exception.getMessage());
		return this.createHttpResponse(BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(LockedException.class)
	public ResponseEntity<HttpResponse> lockedException() {
		log.error(ACCOUNT_LOCKED);
		return this.createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED);
	}

	@ExceptionHandler(TokenExpiredException.class)
	public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception) {
		log.error(exception.getMessage());
		return this.createHttpResponse(UNAUTHORIZED, exception.getMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<HttpResponse> accessDeniedException() {
		log.error(NOT_ENOUGH_PERMISSION);
		return this.createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
	}

	@ExceptionHandler(NoResultException.class)
	public ResponseEntity<HttpResponse> notFoundException(NoResultException exception) {
		log.error(exception.getMessage());
		return this.createHttpResponse(NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<HttpResponse> notFound404() {
		log.error(PAGE_NOT_FOUND);
		return this.createHttpResponse(NOT_FOUND, PAGE_NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<HttpResponse> methodArgumentNotValidException( MethodArgumentNotValidException exception) {
		log.error(exception.getMessage());
		return this.createHttpResponse(BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
		log.error(exception.getMessage());
		HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
		return this.createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
		log.error(exception.getMessage());
		return this.createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
	}

	@ExceptionHandler(IOException.class)
	public ResponseEntity<HttpResponse> ioException(IOException exception) {
		log.error(exception.getMessage());
		return this.createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
	}

	private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
		HttpResponse httpResponse = HttpResponse.builder()
												.timeStamp(LocalDateTime.now())
												.httpStatusCode(httpStatus.value())
												.httpStatus(httpStatus)
												.reason(httpStatus.getReasonPhrase().toUpperCase())
												.message(message)
												.build();
		return new ResponseEntity<>(httpResponse, httpStatus);
	}
}

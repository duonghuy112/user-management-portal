package nguyenduonghuy.usermanagement.exception;

public class PasswordIncorrectException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PasswordIncorrectException() {
		super();
	}

	public PasswordIncorrectException(String message) {
		super(message);
	}
}

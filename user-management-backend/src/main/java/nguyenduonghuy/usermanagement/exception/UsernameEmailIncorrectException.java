package nguyenduonghuy.usermanagement.exception;

public class UsernameEmailIncorrectException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UsernameEmailIncorrectException() {
		super();
	}

	public UsernameEmailIncorrectException(String message) {
		super(message);
	}
}

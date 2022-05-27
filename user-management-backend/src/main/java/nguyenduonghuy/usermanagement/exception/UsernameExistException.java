package nguyenduonghuy.usermanagement.exception;

public class UsernameExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UsernameExistException() {
		super();
	}

	public UsernameExistException(String message) {
		super(message);
	}
}

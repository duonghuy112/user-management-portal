package nguyenduonghuy.usermanagement.exception;

public class EmailExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmailExistException() {
		super();
	}

	public EmailExistException(String message) {
		super(message);
	}
}

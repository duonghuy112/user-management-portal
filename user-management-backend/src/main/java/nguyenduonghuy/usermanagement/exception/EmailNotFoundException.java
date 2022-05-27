package nguyenduonghuy.usermanagement.exception;

public class EmailNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmailNotFoundException() {
		super();
	}

	public EmailNotFoundException(String message) {
		super(message);
	}
}

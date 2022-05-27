package nguyenduonghuy.usermanagement.exception;

public class NotAnImageFileException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotAnImageFileException() {
		super();
	}

	public NotAnImageFileException(String message) {
		super(message);
	}
}

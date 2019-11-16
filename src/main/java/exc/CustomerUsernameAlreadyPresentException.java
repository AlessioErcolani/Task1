package exc;

public class CustomerUsernameAlreadyPresentException extends Exception {

	public CustomerUsernameAlreadyPresentException() {
		super();
	}

	public CustomerUsernameAlreadyPresentException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CustomerUsernameAlreadyPresentException(String message, Throwable cause) {
		super(message, cause);
	}

	public CustomerUsernameAlreadyPresentException(String message) {
		super(message);
	}

	public CustomerUsernameAlreadyPresentException(Throwable cause) {
		super(cause);
	}
}

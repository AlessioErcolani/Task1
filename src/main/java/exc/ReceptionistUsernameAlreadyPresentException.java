package exc;

public class ReceptionistUsernameAlreadyPresentException extends Exception {

	public ReceptionistUsernameAlreadyPresentException() {
	}

	public ReceptionistUsernameAlreadyPresentException(String message) {
		super(message);
	}

	public ReceptionistUsernameAlreadyPresentException(Throwable cause) {
		super(cause);
	}

	public ReceptionistUsernameAlreadyPresentException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReceptionistUsernameAlreadyPresentException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

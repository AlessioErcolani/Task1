package exc;

public class ReceptionistAuthenticationFailure extends Exception {

	public ReceptionistAuthenticationFailure() {
	}

	public ReceptionistAuthenticationFailure(String message) {
		super(message);
	}

	public ReceptionistAuthenticationFailure(Throwable cause) {
		super(cause);
	}

	public ReceptionistAuthenticationFailure(String message, Throwable cause) {
		super(message, cause);
	}

	public ReceptionistAuthenticationFailure(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

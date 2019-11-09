package exc;

public class CustomerAuthenticationFailure extends Exception {

	public CustomerAuthenticationFailure() {
	}

	public CustomerAuthenticationFailure(String message) {
		super(message);
	}

	public CustomerAuthenticationFailure(Throwable cause) {
		super(cause);
	}

	public CustomerAuthenticationFailure(String message, Throwable cause) {
		super(message, cause);
	}

	public CustomerAuthenticationFailure(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}

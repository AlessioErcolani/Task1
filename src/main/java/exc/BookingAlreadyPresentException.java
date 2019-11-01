package exc;

public class BookingAlreadyPresentException extends Exception {

	public BookingAlreadyPresentException() {
	}

	public BookingAlreadyPresentException(String message) {
		super(message);
	}

	public BookingAlreadyPresentException(Throwable cause) {
		super(cause);
	}

	public BookingAlreadyPresentException(String message, Throwable cause) {
		super(message, cause);
	}

	public BookingAlreadyPresentException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

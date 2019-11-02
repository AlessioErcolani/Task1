package exc;

public class BookingNotFoundException extends Exception {

	public BookingNotFoundException() {
	}

	public BookingNotFoundException(String message) {
		super(message);
	}

	public BookingNotFoundException(Throwable cause) {
		super(cause);
	}

	public BookingNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public BookingNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

package exc;

public class HotelAlreadyPresentException extends Exception {

	public HotelAlreadyPresentException() {
	}

	public HotelAlreadyPresentException(String message) {
		super(message);
	}

	public HotelAlreadyPresentException(Throwable cause) {
		super(cause);
	}

	public HotelAlreadyPresentException(String message, Throwable cause) {
		super(message, cause);
	}

	public HotelAlreadyPresentException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

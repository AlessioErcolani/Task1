package exc;

public class ReservationAlreadyPresentException extends Exception {

	public ReservationAlreadyPresentException() {
		super();
	}

	public ReservationAlreadyPresentException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ReservationAlreadyPresentException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReservationAlreadyPresentException(String message) {
		super(message);
	}

	public ReservationAlreadyPresentException(Throwable cause) {
		super(cause);
	}	
}

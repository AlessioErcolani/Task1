package exc;

public class ReservationAlreadyPresentException extends Exception {

	public ReservationAlreadyPresentException() {
		super();
	}

	public ReservationAlreadyPresentException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public ReservationAlreadyPresentException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ReservationAlreadyPresentException(String arg0) {
		super(arg0);
	}

	public ReservationAlreadyPresentException(Throwable arg0) {
		super(arg0);
	}
}

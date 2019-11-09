package exc;

public class ReservationNotFoundException extends Exception {

	public ReservationNotFoundException() {
	}

	public ReservationNotFoundException(String arg0) {
		super(arg0);
	}

	public ReservationNotFoundException(Throwable arg0) {
		super(arg0);
	}

	public ReservationNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ReservationNotFoundException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}

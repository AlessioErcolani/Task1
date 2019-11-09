package exc;

public class HotelNotFoundException extends Exception {

	public HotelNotFoundException() {
	}

	public HotelNotFoundException(String arg0) {
		super(arg0);
	}

	public HotelNotFoundException(Throwable arg0) {
		super(arg0);
	}

	public HotelNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public HotelNotFoundException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}

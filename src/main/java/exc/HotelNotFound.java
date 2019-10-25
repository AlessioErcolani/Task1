package exc;

public class HotelNotFound extends Exception {

	public HotelNotFound() {
		super();
	}

	public HotelNotFound(String msg) {
		super(msg);
	}
}
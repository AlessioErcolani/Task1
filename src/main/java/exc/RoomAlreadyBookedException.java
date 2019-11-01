package exc;

public class RoomAlreadyBookedException extends Exception {

	public RoomAlreadyBookedException() {
	}

	public RoomAlreadyBookedException(String arg0) {
		super(arg0);
	}

	public RoomAlreadyBookedException(Throwable arg0) {
		super(arg0);
	}

	public RoomAlreadyBookedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public RoomAlreadyBookedException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}

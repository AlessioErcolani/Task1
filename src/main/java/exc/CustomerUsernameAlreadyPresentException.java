package exc;

public class CustomerUsernameAlreadyPresentException extends Exception {

	public CustomerUsernameAlreadyPresentException() {
		super();
	}

	public CustomerUsernameAlreadyPresentException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public CustomerUsernameAlreadyPresentException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CustomerUsernameAlreadyPresentException(String arg0) {
		super(arg0);
	}

	public CustomerUsernameAlreadyPresentException(Throwable arg0) {
		super(arg0);
	}

	
}

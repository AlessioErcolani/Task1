package exc;

public class UniqueConstraintException extends DatabaseManagerException {

	public UniqueConstraintException() {
		super();
	}

	public UniqueConstraintException(String msg) {
		super(msg);
	}

}

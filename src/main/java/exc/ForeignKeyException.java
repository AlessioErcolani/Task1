package exc;

public class ForeignKeyException extends DatabaseManagerException {

	public ForeignKeyException() {
	}

	public ForeignKeyException(String msg) {
		super(msg);
	}
}

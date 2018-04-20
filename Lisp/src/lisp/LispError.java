package lisp;

public class LispError extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2223777048769685045L;
	
	public LispError() {
		super();
	}
	
	public LispError(Throwable cause) {
        super(cause);
    }

}

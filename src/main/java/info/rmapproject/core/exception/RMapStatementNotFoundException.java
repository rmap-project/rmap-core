/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * @author smorrissey
 *
 */
public class RMapStatementNotFoundException extends RMapObjectNotFoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 104926169038506991L;

	/**
	 * 
	 */
	public RMapStatementNotFoundException() {
			super();
	}

	/**
	 * @param message
	 */
	public RMapStatementNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RMapStatementNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RMapStatementNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public RMapStatementNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * @author smorrissey
 *
 */
public class RMapObjectNotFoundException extends RMapException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5455801171833854862L;

	/**
	 * 
	 */
	public RMapObjectNotFoundException() {
		super();
	}

	/**
	 * @param message
	 */
	public RMapObjectNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RMapObjectNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RMapObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public RMapObjectNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

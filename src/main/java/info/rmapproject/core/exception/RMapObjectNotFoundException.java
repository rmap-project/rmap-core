/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * General object not found exception for RMap
 *
 * @author  khansen, smorrissey
 */
public class RMapObjectNotFoundException extends RuntimeException{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5455801171833854862L;

	/**
	 * Instantiates a new RMap object not found exception.
	 */
	public RMapObjectNotFoundException() {
		super();
	}

	/**
	 * Instantiates a new RMap object not found exception.
	 *
	 * @param message the message
	 */
	public RMapObjectNotFoundException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new RMap object not found exception.
	 *
	 * @param cause the cause
	 */
	public RMapObjectNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new RMap object not found exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public RMapObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new RMap object not found exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public RMapObjectNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

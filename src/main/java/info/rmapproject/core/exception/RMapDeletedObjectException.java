/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * Exception occurs when a deleted RMap object is requested by a user
 *
 * @author smorrissey
 */
public class RMapDeletedObjectException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -232792600855868381L;

	/**
	 * Instantiates a new RMap deleted object exception.
	 */
	public RMapDeletedObjectException() {
		super();
	}

	/**
	 * Instantiates a new RMap deleted object exception.
	 *
	 * @param message the message
	 */
	public RMapDeletedObjectException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new RMap deleted object exception.
	 *
	 * @param cause the cause
	 */
	public RMapDeletedObjectException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new RMap deleted object exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public RMapDeletedObjectException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new RMap deleted object exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression 
	 * @param writableStackTrace 
	 */
	public RMapDeletedObjectException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

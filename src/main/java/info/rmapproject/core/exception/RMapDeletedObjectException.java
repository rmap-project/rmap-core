/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * @author smorrissey
 *
 */
public class RMapDeletedObjectException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -232792600855868381L;

	/**
	 * 
	 */
	public RMapDeletedObjectException() {
		super();
	}

	/**
	 * @param message
	 */
	public RMapDeletedObjectException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RMapDeletedObjectException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RMapDeletedObjectException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public RMapDeletedObjectException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

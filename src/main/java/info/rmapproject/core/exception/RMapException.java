/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * @author khanson
 *
 */
@SuppressWarnings("serial")
public class RMapException extends RuntimeException {

	/**
	 * 
	 */
	public RMapException() {
		super();
	}

	/**
	 * @param message
	 */
	public RMapException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RMapException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RMapException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public RMapException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

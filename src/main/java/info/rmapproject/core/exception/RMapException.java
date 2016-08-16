/**
 * 
 */
package info.rmapproject.core.exception;

// TODO: Auto-generated Javadoc
/**
 * General RMap Exception
 *
 * @author khanson
 */
@SuppressWarnings("serial")
public class RMapException extends RuntimeException {

	/**
	 * Instantiates a new RMap exception.
	 */
	public RMapException() {
		super();
	}

	/**
	 * Instantiates a new RMap exception.
	 *
	 * @param message the message
	 */
	public RMapException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new RMap exception.
	 *
	 * @param cause the cause
	 */
	public RMapException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new RMap exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public RMapException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new RMap exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression 
	 * @param writableStackTrace 
	 */
	public RMapException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

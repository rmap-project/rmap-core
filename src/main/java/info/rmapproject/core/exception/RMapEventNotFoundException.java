/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * Exception occurs when an RMap Event is requested but cannot be found
 *
 * @author smorrissey
 */
public class RMapEventNotFoundException extends RMapObjectNotFoundException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2200390531474455796L;

	/**
	 * Instantiates a new RMap Event not found exception.
	 */
	public RMapEventNotFoundException() {
		super();
	}

	/**
	 * Instantiates a new RMap Event not found exception.
	 *
	 * @param message the message
	 */
	public RMapEventNotFoundException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new RMap Event not found exception.
	 *
	 * @param cause the cause
	 */
	public RMapEventNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new RMap Event not found exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public RMapEventNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new RMap Event not found exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public RMapEventNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

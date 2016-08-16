/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * Exception occurs when a User attempts to update a DiSCO version that is no longer active.
 *
 * @author khanson
 */
public class RMapInactiveVersionException extends RMapObjectNotFoundException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2993203728345433963L;

	/**
	 * Instantiates a new RMap inactive version exception.
	 */
	public RMapInactiveVersionException() {
		super();
	}

	/**
	 * Instantiates a new RMap inactive version exception.
	 *
	 * @param message the message
	 */
	public RMapInactiveVersionException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new RMap inactive version exception.
	 *
	 * @param cause the cause
	 */
	public RMapInactiveVersionException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new RMap inactive version exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public RMapInactiveVersionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new RMap inactive version exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression 
	 * @param writableStackTrace
	 */
	public RMapInactiveVersionException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

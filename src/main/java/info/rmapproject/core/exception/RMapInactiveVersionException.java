/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * @author khanson
 *
 */
public class RMapInactiveVersionException extends RMapObjectNotFoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2993203728345433963L;

	/**
	 * 
	 */
	public RMapInactiveVersionException() {
		super();
	}

	/**
	 * @param message
	 */
	public RMapInactiveVersionException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RMapInactiveVersionException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RMapInactiveVersionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public RMapInactiveVersionException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

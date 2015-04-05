/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * @author smorrissey
 *
 */
public class RMapIdentityNotFoundException extends RMapObjectNotFoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5462576426697569986L;

	/**
	 * 
	 */
	public RMapIdentityNotFoundException() {
		super();
	}

	/**
	 * @param message
	 */
	public RMapIdentityNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RMapIdentityNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RMapIdentityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public RMapIdentityNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

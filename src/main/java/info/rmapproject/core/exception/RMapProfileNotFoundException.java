/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * @author smorrissey
 *
 */
public class RMapProfileNotFoundException extends RMapObjectNotFoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2601907353472918556L;

	/**
	 * 
	 */
	public RMapProfileNotFoundException() {
		super();
	}

	/**
	 * @param message
	 */
	public RMapProfileNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RMapProfileNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RMapProfileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public RMapProfileNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

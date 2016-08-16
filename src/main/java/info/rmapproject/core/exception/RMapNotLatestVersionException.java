/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * Exception is thrown when a User attempts to update a DiSCO that is not hte latest version of that DiSCO
 * Only the latest version can be updated.
 * 
 * @author khanson
 *
 */
public class RMapNotLatestVersionException extends RMapObjectNotFoundException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2993203569234963L;

	/**
	 * Instantiates a new RMap not latest version exception.
	 */
	public RMapNotLatestVersionException() {
		super();
	}

	/**
	 * Instantiates a new RMap not latest version exception.
	 *
	 * @param message the message
	 */
	public RMapNotLatestVersionException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new RMap not latest version exception.
	 *
	 * @param cause the cause
	 */
	public RMapNotLatestVersionException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new RMap not latest version exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public RMapNotLatestVersionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new RMap not latest version exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression 
	 * @param writableStackTrace
	 */
	public RMapNotLatestVersionException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

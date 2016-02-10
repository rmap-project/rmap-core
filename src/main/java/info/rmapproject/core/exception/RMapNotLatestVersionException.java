/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * @author khanson
 *
 */
public class RMapNotLatestVersionException extends RMapObjectNotFoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2993203569234963L;

	/**
	 * 
	 */
	public RMapNotLatestVersionException() {
		super();
	}

	/**
	 * @param message
	 */
	public RMapNotLatestVersionException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RMapNotLatestVersionException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RMapNotLatestVersionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public RMapNotLatestVersionException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

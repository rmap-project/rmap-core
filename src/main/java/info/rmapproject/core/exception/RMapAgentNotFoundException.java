/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * @author smorrissey
 *
 */
public class RMapAgentNotFoundException extends RMapObjectNotFoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2993203728356933963L;

	/**
	 * 
	 */
	public RMapAgentNotFoundException() {
		super();
	}

	/**
	 * @param message
	 */
	public RMapAgentNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RMapAgentNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RMapAgentNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public RMapAgentNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

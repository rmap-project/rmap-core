/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * Exception catches case where RMap Agent is not found
 *
 * @author smorrissey
 */
public class RMapAgentNotFoundException extends RMapObjectNotFoundException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2993203728356933963L;

	/**
	 * Instantiates a new RMap Agent not found exception.
	 */
	public RMapAgentNotFoundException() {
		super();
	}

	/**
	 * Instantiates a new RMap Agent not found exception.
	 *
	 * @param message the message
	 */
	public RMapAgentNotFoundException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new RMap Agent not found exception.
	 *
	 * @param cause the cause
	 */
	public RMapAgentNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new RMap Agent not found exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public RMapAgentNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new RMap Agent not found exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression 
	 * @param writableStackTrace 
	 */
	public RMapAgentNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

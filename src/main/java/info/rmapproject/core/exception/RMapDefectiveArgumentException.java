/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * Exception captures case where a defective argument is passed to RMap
 *
 * @author smorrissey
 */
public class RMapDefectiveArgumentException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8793191899781932688L;

	/**
	 * Instantiates a new RMap defective argument exception.
	 */
	public RMapDefectiveArgumentException() {
		super();
	}

	/**
	 * Instantiates a new RMap defective argument exception.
	 *
	 * @param message the message
	 */
	public RMapDefectiveArgumentException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new RMap defective argument exception.
	 *
	 * @param cause the cause
	 */
	public RMapDefectiveArgumentException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new RMap defective argument exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public RMapDefectiveArgumentException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new RMap defective argument exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression 
	 * @param writableStackTrace 
	 */
	public RMapDefectiveArgumentException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

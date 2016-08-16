/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * Exception occurs when a DiSCO is requested but cannot be found
 *
 * @author smorrissey
 */
public class RMapDiSCONotFoundException extends RMapObjectNotFoundException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1247316939616876804L;

	/**
	 * Instantiates a new RMap DiSCO not found exception.
	 */
	public RMapDiSCONotFoundException() {
		super();
	}

	/**
	 * Instantiates a new RMap DiSCO not found exception.
	 *
	 * @param message the message
	 */
	public RMapDiSCONotFoundException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new RMap DiSCO not found exception.
	 *
	 * @param cause the cause
	 */
	public RMapDiSCONotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new RMap DiSCO not found exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public RMapDiSCONotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new RMap DiSCO not found exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression 
	 * @param writableStackTrace
	 */
	public RMapDiSCONotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

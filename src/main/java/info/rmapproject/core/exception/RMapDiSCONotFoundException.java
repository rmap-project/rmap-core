/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * @author smorrissey
 *
 */
public class RMapDiSCONotFoundException extends RMapObjectNotFoundException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1247316939616876804L;

	/**
	 * 
	 */
	public RMapDiSCONotFoundException() {
		super();
	}

	/**
	 * @param message
	 */
	public RMapDiSCONotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public RMapDiSCONotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RMapDiSCONotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public RMapDiSCONotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

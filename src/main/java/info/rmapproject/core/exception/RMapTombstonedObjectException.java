/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * Exception thrown when a Tombstoned RMap DiSCO is requested
 *
 * @author smorrissey
 */
public class RMapTombstonedObjectException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 463336118510679173L;

	/**
	 * Instantiates a new RMap tombstoned object exception.
	 */
	public RMapTombstonedObjectException() {
		super();
	}

	/**
	 * Instantiates a new RMap tombstoned object exception.
	 *
	 * @param message the message
	 */
	public RMapTombstonedObjectException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new RMap tombstoned object exception.
	 *
	 * @param cause the cause
	 */
	public RMapTombstonedObjectException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new RMap tombstoned object exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public RMapTombstonedObjectException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new RMap tombstoned object exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public RMapTombstonedObjectException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

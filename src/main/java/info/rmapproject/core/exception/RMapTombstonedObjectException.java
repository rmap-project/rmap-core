/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * @author smorrissey
 *
 */
public class RMapTombstonedObjectException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 463336118510679173L;

	/**
	 * 
	 */
	public RMapTombstonedObjectException() {
		super();
	}

	/**
	 * @param arg0
	 */
	public RMapTombstonedObjectException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public RMapTombstonedObjectException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public RMapTombstonedObjectException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public RMapTombstonedObjectException(String arg0, Throwable arg1,
			boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}

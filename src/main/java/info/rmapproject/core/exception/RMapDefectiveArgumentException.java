/**
 * 
 */
package info.rmapproject.core.exception;

/**
 * @author smorrissey
 *
 */
public class RMapDefectiveArgumentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8793191899781932688L;

	/**
	 * 
	 */
	public RMapDefectiveArgumentException() {
		super();
	}

	/**
	 * @param arg0
	 */
	public RMapDefectiveArgumentException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public RMapDefectiveArgumentException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public RMapDefectiveArgumentException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public RMapDefectiveArgumentException(String arg0, Throwable arg1,
			boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}

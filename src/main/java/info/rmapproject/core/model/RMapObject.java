/**
 * 
 */
package info.rmapproject.core.model;


import info.rmapproject.core.exception.RMapException;

/**
 * @author smorrissey, khanson
 *
 */
public interface RMapObject {

	/**
	 * 
	 * @return
	 */
	public RMapUri getId();
	/**
	 * 
	 * @return
	 */
	public RMapUri getType() throws RMapException;

}

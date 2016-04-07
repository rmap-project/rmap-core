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
	public RMapIri getId();
	/**
	 * 
	 * @return
	 */
	public RMapObjectType getType() throws RMapException;

}

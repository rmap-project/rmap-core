/**
 * 
 */
package info.rmapproject.core.model;


import info.rmapproject.core.exception.RMapException;

/**
 * The Interface for RMapObjects
 *
 * @author smorrissey, khanson
 */
public interface RMapObject {

	/**
	 * Gets the ID of the object.
	 *
	 * @return the id
	 */
	public RMapIri getId();
	
	/**
	 * Gets the type
	 *
	 * @return the object type (DISCO, AGENT, EVENT)
	 * @throws RMapException the RMap Exception
	 */
	public RMapObjectType getType() throws RMapException;

}

/**
 * 
 */
package info.rmapproject.core.model;

import info.rmapproject.core.exception.RMapException;

import java.util.List;

/**
 * @author smorrissey
 *
 */
public interface RMapEventCreation extends RMapEvent {



	
	/**
	 * @return the createdObjects
	 * @throws RMapException 
	 */
	public List<RMapUri> getCreatedObjectIds() throws RMapException;

	/**
	 * @param createdObjects the createdObjects to set
	 * @throws RMapException 
	 */
	public void setCreatedObjectIds(List<RMapUri> createdObjects) throws RMapException;

}

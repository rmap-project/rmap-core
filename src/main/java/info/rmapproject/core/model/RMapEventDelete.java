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
public interface RMapEventDelete extends RMapEvent {
	


	/**
	 * @return the deletedObjectIds
	 * @throws RMapException 
	 */
	public List<RMapUri> getDeletedObjectIds() throws RMapException;

	/**
	 * @param deletedObjectIds the deletedObjectIds to set
	 * @throws RMapException 
	 */
	public void setDeletedObjectIds(List<RMapUri> deletedObjectIds) throws RMapException;

}

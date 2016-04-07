/**
 * 
 */
package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;

import java.util.List;

/**
 * @author smorrissey
 *
 */
public interface RMapEventDeletion extends RMapEvent {
	


	/**
	 * @return the deletedObjectIds
	 * @throws RMapException 
	 */
	public List<RMapIri> getDeletedObjectIds() throws RMapException;

	/**
	 * @param deletedObjectIds the deletedObjectIds to set
	 * @throws RMapException 
	 */
	public void setDeletedObjectIds(List<RMapIri> deletedObjectIds) throws RMapException, RMapDefectiveArgumentException;

}

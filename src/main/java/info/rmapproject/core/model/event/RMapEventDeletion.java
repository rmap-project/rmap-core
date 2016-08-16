/**
 * 
 */
package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;

import java.util.List;

/**
 * Interface for Events that involve deletion of an object. 
 * Currently only DiSCO deletion is possible.
 * @author smorrissey
 *
 */
public interface RMapEventDeletion extends RMapEvent {
	


	/**
	 * Retrieve the IRIs of the deleted RMap objects as a List
	 * @return the list of deleted RMap objects
	 * @throws RMapException 
	 */
	public List<RMapIri> getDeletedObjectIds() throws RMapException;

	/**
	 * Set the list of IRIs for the deleted objects
	 * @param deletedObjectIds the deleted object ID list to set
	 * @throws RMapException 
	 */
	public void setDeletedObjectIds(List<RMapIri> deletedObjectIds) throws RMapException, RMapDefectiveArgumentException;

}

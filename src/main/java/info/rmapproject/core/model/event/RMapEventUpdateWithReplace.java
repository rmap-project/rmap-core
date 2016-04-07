/**
 * 
 */
package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;

/**
 * Update event for objects that are overwritten by a new version without being assigned a new ID.
 * This is used for Agent updates where previous versions of the Agent are not preserved, 
 * unlike a DiSCO update where new DiSCOs are created as part of the update
 * @author khanson
 *
 */
public interface RMapEventUpdateWithReplace extends RMapEvent {
	
	/**
	 * @return the updatedObjectId
	 * @throws RMapException 
	 */
	public RMapIri getUpdatedObjectId() throws RMapException;

	/**
	 * @param updatedObjectId ID of replaced object
	 * @throws RMapException 
	 */
	public void setUpdatedObjectId(RMapIri updatedObjectId) throws RMapException, RMapDefectiveArgumentException;

}

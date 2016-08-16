/**
 * 
 */
package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;

/**
 * Interface for update event in which objects are overwritten by a new version without being assigned a new ID.
 * This is used for Agent updates where previous versions of the Agent are not preserved, 
 * unlike a DiSCO update where new DiSCOs are created as part of the update
 * @author khanson
 *
 */
public interface RMapEventUpdateWithReplace extends RMapEvent {
	
	/**
	 * Gets the IRI of the updated object
	 *
	 * @return the IRI of the updated object
	 * @throws RMapException the RMap exception
	 */
	public RMapIri getUpdatedObjectId() throws RMapException;

	/**
	 * Sets the IRI of the updated object.
	 *
	 * @param updatedObjectId The IRI of the updated object
	 * @throws RMapException the RMap exception
	 * @throws RMapDefectiveArgumentException the RMap defective argument exception
	 */
	public void setUpdatedObjectId(RMapIri updatedObjectId) throws RMapException, RMapDefectiveArgumentException;

}

/**
 * 
 */
package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;

/**
 * @author smorrissey
 *
 */
public interface RMapEventInactivation extends RMapEvent {

	/**
	 * @return the inactivatedObject
	 * @throws RMapException 
	 */
	public RMapIri getInactivatedObjectId() throws RMapException;
	/**
	 * 
	 * @param uri
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public void setInactivatedObjectId(RMapIri uri) throws RMapException, RMapDefectiveArgumentException;
	
}

package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;

import java.util.List;
/**
 * Interface for Events that generate new objects.
 * @author smorrissey
 */
public interface RMapEventWithNewObjects extends RMapEvent {

	
	/**
	 * Gets a list of IRIs of the objects that were created for this event
	 *
	 * @return list of IRIs of the objects that were created
	 * @throws RMapException the r map exception
	 */
	public List<RMapIri> getCreatedObjectIds() throws RMapException;

	
	/**
	 * Sets the list of IRIs of the objects that were created for this event
	 *
	 * @param createdObjects list of IRIs of the objects that were created
	 * @throws RMapException the RMap exception
	 * @throws RMapDefectiveArgumentException the RMap defective argument exception
	 */
	public void setCreatedObjectIds(List<RMapIri> createdObjects) throws RMapException, RMapDefectiveArgumentException;

}

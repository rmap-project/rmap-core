/**
 * 
 */
package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapObject;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;

import java.util.Date;

/**
 * @author smorrissey
 *
 */
public interface RMapEvent extends RMapObject{



	/**
	 * @return the eventType
	 * @throws RMapException
	 */
	public RMapEventType getEventType() throws RMapException;

	/**
	 * @return the eventTargetType
	 * @throws RMapException
	 */
	public RMapEventTargetType getEventTargetType() throws RMapException;

	/**
	 * @return the associatedAgent
	 */
	public RMapUri getAssociatedAgent() throws RMapException;

	/**
	 * @return the description
	 * @throws RMapException 
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapValue getDescription() throws RMapException;

	/**
	 * @param endTime the endTime to set
	 * @throws RMapException
	 */
	public void setDescription(RMapValue description) throws RMapException, RMapDefectiveArgumentException;
	
	/**
	 * Start time will be set by the constructor
	 * @return the startTime
	 * @throws RMapException
	 */
	public Date getStartTime() throws RMapException;

	/**
	 * @return the endTime
	 * @throws RMapException
	 */
	public Date getEndTime() throws RMapException;

	/**
	 * @param endTime the endTime to set
	 * @throws RMapException
	 */
	public void setEndTime(Date endTime) throws RMapException;

	
}

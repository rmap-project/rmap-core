/**
 * 
 */
package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapUri;


/**
 * 
 * @author smorrissey
 *
 */
public interface RMapEventUpdate extends RMapEventWithNewObjects {
	

	/**
	 * @return the inactivatedObject
	 * @throws RMapException 
	 */
	public RMapUri getInactivatedObjectId() throws RMapException;
	
	/**
	 * 
	 * @param uri
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public void setInactivatedObjectId(RMapUri uri) throws RMapException, RMapDefectiveArgumentException;

	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public RMapUri getDerivedObjectId() throws RMapException;
	/**
	 * 
	 * @param uri
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public void setDerivedObjectId(RMapUri uri) throws RMapException, RMapDefectiveArgumentException;
}

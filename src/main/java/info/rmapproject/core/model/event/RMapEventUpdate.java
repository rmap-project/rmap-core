/**
 * 
 */
package info.rmapproject.core.model.event;

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
	 */
	public void setInactivatedObjectId(RMapUri uri) throws RMapException;

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
	 */
	public void setDerivedObjectId(RMapUri uri) throws RMapException;
}

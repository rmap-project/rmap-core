/**
 * 
 */
package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapUri;


/**
 * @author smorrissey
 *
 */
public interface RMapEventDerivation extends RMapEventWithNewObjects {

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
	
	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public RMapUri getSourceObjectId() throws RMapException;
	
	/**
	 * 
	 * @throws RMapException
	 */
	public void setSourceObjectId(RMapUri uri)  throws RMapException;
		

}

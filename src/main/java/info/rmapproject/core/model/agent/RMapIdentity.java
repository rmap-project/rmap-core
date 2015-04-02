/**
 * 
 */
package info.rmapproject.core.model.agent;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapObject;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;

/**
 * @author smorrissey
 *
 */
public interface RMapIdentity extends RMapObject {
	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public RMapValue getIdentityLocalPart() throws RMapException;
	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public RMapUri getIdentityProviderId() throws RMapException;
	
	
	
}

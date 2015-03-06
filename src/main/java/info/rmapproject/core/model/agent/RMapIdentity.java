/**
 * 
 */
package info.rmapproject.core.model.agent;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapObject;

/**
 * @author smorrissey
 *
 */
public interface RMapIdentity extends RMapObject {

	public RMapIdentityProvider getIdProvider() throws RMapException;
	
	
	//TODO get local part
}

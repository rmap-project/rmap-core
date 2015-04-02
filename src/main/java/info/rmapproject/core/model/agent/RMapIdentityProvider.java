/**
 * 
 */
package info.rmapproject.core.model.agent;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapObject;
import info.rmapproject.core.model.RMapUri;

/**
 * @author smorrissey
 *
 */
public interface RMapIdentityProvider extends RMapObject {

	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public RMapUri getConfigurationId() throws RMapException;
}

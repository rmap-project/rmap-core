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
public interface RMapAgent extends RMapObject {

	/**
	 * Get URI of agent that created this Agent
	 * @return
	 * @throws RMapException
	 */
	public RMapUri getCreator() throws RMapException;

}

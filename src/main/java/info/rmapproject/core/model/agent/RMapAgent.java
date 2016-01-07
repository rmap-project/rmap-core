/**
 * 
 */
package info.rmapproject.core.model.agent;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapObject;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;


/**
 * @author smorrissey, khanson
 *
 */
public interface RMapAgent extends RMapObject {

	/**
	 * Get name associated with the Agent 
	 * @return
	 * @throws RMapException
	 */
	public RMapValue getName() throws RMapException;
	/**
	 * Get ID of provider used to authenticate the RMap user that is associated with the Agent
	 * @return
	 * @throws RMapException
	 */
	public RMapUri getIdProvider() throws RMapException;
	/**
	 * Get Auth URI of agent - this is generated using the id provider and idP username
	 * @return
	 * @throws RMapException
	 */
	public RMapUri getAuthId() throws RMapException;

}

/**
 * 
 */
package info.rmapproject.core.model.agent;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapObject;
import info.rmapproject.core.model.RMapUri;

import java.util.List;

/**
 * @author smorrissey
 *
 */
public interface RMapAgent extends RMapObject {

	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public List<RMapUri> getProfileIds() throws RMapException;
	/**
	 * 
	 * @param profileIds
	 * @throws RMapException
	 */
	public void setProfileIds(List<RMapUri> profileIds) throws RMapException;
	/**
	 * 
	 * @param profileId
	 * @throws RMapException
	 */
	public void addProfileId(RMapUri profileId) throws RMapException;
	/**
	 * Get URI of agent that created this Agent
	 * @return
	 * @throws RMapException
	 */
	public RMapUri getCreator() throws RMapException;

}

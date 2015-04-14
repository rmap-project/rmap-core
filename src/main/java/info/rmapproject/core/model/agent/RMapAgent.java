/**
 * 
 */
package info.rmapproject.core.model.agent;

import java.util.List;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapObject;
import info.rmapproject.core.model.RMapTriple;
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
	/**
	 * Get Resource URI of agent for which RMap agent is a representation
	 * @return
	 * @throws RMapException
	 */
	public RMapUri getRepresentationId() throws RMapException;
	/**
	 * @return
	 * @throws RMapException
	 */
	public List<RMapTriple> getProperties() throws RMapException;

}

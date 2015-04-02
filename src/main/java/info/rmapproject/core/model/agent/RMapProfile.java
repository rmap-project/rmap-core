/**
 * 
 */
package info.rmapproject.core.model.agent;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapObject;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;

import java.util.List;
import java.util.Map;

/**
 * @author smorrissey
 *
 */
public interface RMapProfile extends RMapObject {
	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public Map<RMapUri, RMapValue> getProperties() throws RMapException;
	/**
	 * 
	 * @param propertyMap
	 * @throws RMapException
	 */
	public void setProperties(Map<RMapUri, RMapValue> propertyMap) throws RMapException;
	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public List<RMapValue> getIdentities() throws RMapException;
	/**
	 * 
	 * @param idents
	 * @throws RMapException
	 */
	public void setIdentities (List<RMapValue> idents)  throws RMapException;
	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public RMapUri getPreferredIdentity() throws RMapException;
	/**
	 * 
	 * @param ident
	 * @throws RMapException
	 */
	public void setPreferredIdentity (RMapUri ident) throws RMapException;
	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public RMapUri getParentAgentId ()  throws RMapException;
	/**
	 * Get URI of agent that created this Agent
	 * @return
	 * @throws RMapException
	 */
	public RMapUri getCreator() throws RMapException;

}

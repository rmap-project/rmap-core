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
	 * @return
	 * @throws RMapException
	 */
	public List<RMapIdentity> getIdentities() throws RMapException;
	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public RMapIdentity getPreferredIdentity() throws RMapException;
}

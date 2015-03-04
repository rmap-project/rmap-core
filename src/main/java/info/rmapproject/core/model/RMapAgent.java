/**
 * 
 */
package info.rmapproject.core.model;

import info.rmapproject.core.exception.RMapException;

import java.net.URI;

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
	public URI getAgentId() throws RMapException;

}

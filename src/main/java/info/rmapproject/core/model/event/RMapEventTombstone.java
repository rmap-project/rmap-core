/**
 * 
 */
package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapUri;



/**
 * @author smorrissey
 *
 */
public interface RMapEventTombstone extends RMapEvent {

	/**
	 * @return the tombstonedResource
	 * @throws RMapException 
	 */
	public RMapUri getTombstonedResourceId() throws RMapException;

}

/**
 * 
 */
package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;



/**
 * @author smorrissey
 *
 */
public interface RMapEventTombstone extends RMapEvent {

	/**
	 * @return the tombstonedResource
	 * @throws RMapException 
	 */
	public RMapIri getTombstonedResourceId() throws RMapException;

}

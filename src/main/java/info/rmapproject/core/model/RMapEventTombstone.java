/**
 * 
 */
package info.rmapproject.core.model;

import info.rmapproject.core.exception.RMapException;



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

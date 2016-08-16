/**
 * 
 */
package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;



/**
 * Interface for Events that tombstone an RMap object e.g. a DiSCO goes from ACTIVE to TOMBSTONED
 * Tombstoned objects are not visible through public interfaces though they still exist in the RMap
 * database.
 * @author smorrissey
 *
 */
public interface RMapEventTombstone extends RMapEvent {

	/**
	 * @return IRI of the Tombstoned resource
	 * @throws RMapException 
	 */
	public RMapIri getTombstonedResourceId() throws RMapException;

}

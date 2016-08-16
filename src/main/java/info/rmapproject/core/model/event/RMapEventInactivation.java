package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;

/**
 * Interface for Events that inactivate an RMap object e.g. a DiSCO goes from ACTIVE to INACTIVE
 *
 * @author smorrissey
 */
public interface RMapEventInactivation extends RMapEvent {

	/**
	 * Gets the inactivated object IRI.
	 *
	 * @return the inactivatedObject
	 * @throws RMapException the RMap exception
	 */
	public RMapIri getInactivatedObjectId() throws RMapException;
	
	/**
	 * Sets the inactivated object IRI.
	 *
	 * @param iri of the inactivated object
	 * @throws RMapException the RMap exception
	 * @throws RMapDefectiveArgumentException the RMap defective argument exception
	 */
	public void setInactivatedObjectId(RMapIri iri) throws RMapException, RMapDefectiveArgumentException;
	
}

package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;

/**
 * Interface for Events that involve an update of an RMap object e.g. an existing DiSCO
 * is update with a new version of the DiSCO. The original DiSCO still exists, but is marked
 * INACTIVE. A new DISCO is created and its event references the previous verison.
 *
 * @author smorrissey
 */
public interface RMapEventUpdate extends RMapEventWithNewObjects {
	

	/**
	 * Gets the inactivated object IRI.
	 *
	 * @return the IRI of the inactivated object
	 * @throws RMapException the RMap exception
	 */
	public RMapIri getInactivatedObjectId() throws RMapException;
	
	/**
	 * Sets the IRI of the inactivated object.
	 *
	 * @param iri the IRI of the newly inactivated object 
	 * @throws RMapException the RMap exception
	 * @throws RMapDefectiveArgumentException the RMap defective argument exception
	 */
	public void setInactivatedObjectId(RMapIri iri) throws RMapException, RMapDefectiveArgumentException;

	/**
	 * Gets the IRI of the derived object.
	 *
	 * @return the IRI of the derived object
	 * @throws RMapException the RMap exception
	 */
	public RMapIri getDerivedObjectId() throws RMapException;
	
	/**
	 * Sets the IRI of the derived object.
	 *
	 * @param iri the IRI of the newly derived object
	 * @throws RMapException the RMap exception
	 * @throws RMapDefectiveArgumentException the RMap defective argument exception
	 */
	public void setDerivedObjectId(RMapIri iri) throws RMapException, RMapDefectiveArgumentException;
}

package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;

/**
 * Interface for events that involve a derivation of a new object from an existing object.
 * The main case this is used is when an Agent submits a new version of a DiSCO to an existing 
 * DiSCO that another Agent created.
 * 
 * @author smorrissey
 */
public interface RMapEventDerivation extends RMapEventWithNewObjects {

	/**
	 * Gets the derived object's IRI.
	 *
	 * @return the derived object's IRI
	 * @throws RMapException the RMap exception
	 */
	public RMapIri getDerivedObjectId() throws RMapException;
	
	/**
	 * Sets the derived object's IRI.
	 *
	 * @param iri the new derived object IRI
	 * @throws RMapException the RMap exception
	 * @throws RMapDefectiveArgumentException the r map defective argument exception
	 */
	public void setDerivedObjectId(RMapIri iri) throws RMapException, RMapDefectiveArgumentException;
	
	/**
	 * Gets the source object's IRI.
	 *
	 * @return the source object's IRI
	 * @throws RMapException the RMap exception
	 */
	public RMapIri getSourceObjectId() throws RMapException;
	
	/**
	 * Sets the source object IRI
	 *
	 * @param iri the source object's IRI
	 * @throws RMapException the RMap exception
	 * @throws RMapDefectiveArgumentException the RMap defective argument exception
	 */
	public void setSourceObjectId(RMapIri iri) throws RMapException, RMapDefectiveArgumentException;
		

}

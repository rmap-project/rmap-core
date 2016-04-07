/**
 * 
 */
package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;


/**
 * @author smorrissey
 *
 */
public interface RMapEventDerivation extends RMapEventWithNewObjects {

	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public RMapIri getDerivedObjectId() throws RMapException;
	/**
	 * 
	 * @param uri
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public void setDerivedObjectId(RMapIri uri) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public RMapIri getSourceObjectId() throws RMapException;
	
	/**
	 * 
	 * @throws RMapException
	 */
	public void setSourceObjectId(RMapIri uri) throws RMapException, RMapDefectiveArgumentException;
		

}

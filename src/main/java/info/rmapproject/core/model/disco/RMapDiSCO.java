/**
 * 
 */
package info.rmapproject.core.model.disco;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapObject;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapTriple;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.event.RMapEvent;

import java.net.URI;
import java.util.List;


/**
 * @author smorrissey
 *
 */

public interface RMapDiSCO extends RMapObject  {
	
	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getAggregratedResources() throws RMapException;

	/**
	 * @param aggregratedResources the aggregratedResources to set
	 * @throws RMapException 
	 */
	public void setAggregratedResources(List<URI> aggregratedResources) 
			throws RMapException;

	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public List<RMapTriple> getRelatedStatements() throws RMapException;

	/**
	 * @param relatedStatements the relatedResources to set
	 * @throws RMapException 
	 */
	public void setRelatedStatements(List<RMapTriple> relatedStatements) throws RMapException;
	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public RMapResource getCreator() throws RMapException;
	/**
	 * 
	 * @param creator
	 * @throws RMapException
	 */
	public void setCreator(RMapResource creator) throws RMapException;

	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public RMapValue getDescription() throws RMapException;

	/**
	 * 
	 * @param description
	 * @throws RMapException
	 */
	public void setDescription(RMapValue description) throws RMapException ;

	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public RMapStatus getStatus() throws RMapException;
	
	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public List<RMapEvent> getRelatedEvents() throws RMapException;
	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public String getProviderId() throws RMapException;
	
}

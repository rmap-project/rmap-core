/**
 * 
 */
package info.rmapproject.core.model;

import info.rmapproject.core.exception.RMapException;

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
	 * @return the relatedResources
	 * @throws RMapException 
	 */
	public RMapStatementBag getRelatedStatements() throws RMapException ;

	/**
	 * @param relatedStatements the relatedResources to set
	 * @throws RMapException 
	 */
	public void setRelatedStatements(RMapStatementBag relatedStatements) throws RMapException;
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
	public RMapResource getDescription() throws RMapException;

	/**
	 * 
	 * @param description
	 * @throws RMapException
	 */
	public void setDescription(RMapResource description) throws RMapException ;

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

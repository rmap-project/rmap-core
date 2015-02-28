/**
 * 
 */
package info.rmapproject.core.rmapservice;

import java.net.URI;
import java.util.List;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapAgent;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapDiSCO;
import info.rmapproject.core.model.RMapEvent;
import info.rmapproject.core.model.RMapStatement;
import info.rmapproject.core.model.RMapStatementBag;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapValue;

/**
 *
 *  @author khansen, smorrissey
 *
 */
public interface RMapService {

	// Resource services
	/**
	 * Get URI of all RMap object types with a specified status code related to a Resource URI 
	 * @param uri
	 * @param statusCode
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getResourceRelatedAll (URI uri, RMapStatus statusCode) throws RMapException;
	/**
	 * Get all RMap Statements with a specified status code whose subject or object matches 
	 * a Resource URI 
	 * @param uri
	 * @param statusCode
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getResourceRelatedStmts (URI uri, RMapStatus statusCode) throws RMapException;
	/**
	 * Get all RMapEvents related to a Resource URI
	 * @param uri
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getResourceRelatedEvents (URI uri) throws RMapException;
	/**
	 * Get all RMapDiSCOs with a specified status code related to a Resource URI 
	 * @param uri
	 * @param statusCode
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getResourceRelatedDiSCOs (URI uri, RMapStatus statusCode) throws RMapException;
	/**
	 * Get all RMapAgents with a specified status code related to a Resource URI 
	 * @param uri
	 * @param statusCode
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getResourceRelatedAgents (URI uri, RMapStatus statusCode) throws RMapException;
	
	// Statement services 
	/**
	 * Get the RMapStatement identified by a specific URI
	 * @param id
	 * @return
	 * @throws RMapException
	 */
	public RMapStatement readStatement (URI id) throws RMapException;
	/**
	 * Get the URI for the RMapStatement matching the supplied subject, predicate and objectc
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return
	 * @throws RMapException
	 */
	public URI getStatementID (RMapResource subject, RMapUri predicate, RMapValue object) throws RMapException;
	/**
	 * Return the RMapStatements identified by the listed URIS
	 * @param ids
	 * @return
	 * @throws RMapException
	 */
	//TODO  add status as parameter, and only allow Active, Inactive
	public List<RMapStatement> readStatements (List<URI> ids) throws RMapException;
	/**
	 * Get the status of the RMapStatement identified by the provided URI
	 * @param stmtId
	 * @return
	 * @throws RMapException
	 */
	public RMapStatus getStatementStatus(URI stmtId) throws RMapException;
	/**
	 * Get all RMapEvents related to the RMapStatement identified by the provided URI
	 * @param stmtId
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getStatementEvents(URI stmtId) throws RMapException;
	
	
	// DiSCO services
	/**
	 * Return the DiSCO
	 * @param discoID
	 * @return
	 * @throws RMapException
	 */
	public RMapDiSCO readDiSCO(URI discoID) throws RMapException;
	/**
	 * 
	 * @param aggregatedResources
	 * @param creator
	 * @param relatedStatements
	 * @param desc
	 * @return
	 * @throws RMapException
	 */
	public RMapEvent createDiSCO(RMapAgent systemAgent, List<URI> aggregatedResources,
			RMapValue creator, RMapStatementBag relatedStatements, RMapValue desc)  throws RMapException;
	/**
	 * 
	 * @param discoId
	 * @return
	 * @throws RMapException
	 */
	public RMapStatus getDiSCOStatus(URI discoId) throws RMapException;
	/**
	 *@param systemAgent
	 * @param oldDiscoId
	 * @param aggregatedResources
	 * @param relatedStatements
	 * @param creator
	 * @param desc
	 * @return
	 * @throws RMapException
	 */
	
	public RMapEvent updateDiSCO (RMapAgent systemAgent, URI oldDiscoId, 
			List<URI> aggregatedResources,RMapStatementBag relatedStatements, RMapValue creator, 
			RMapValue desc) throws RMapException;
	/**
	 * Soft delete (tombstone) of a DiSCO
	 * @param discoID
	 * @return
	 * @throws RMapException
	 */
	public RMapEvent deleteDiSCO (URI discoID, RMapAgent systemAgent) throws RMapException;
	/**
	 * Get all versions of a DiSCO whether created by original creator of DiSCO or by some
	 * other agent
	 * @param discoID
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getDiSCOAllVersions(URI discoID) throws RMapException;
	/**
	 * Get all versions of a DiSCO whose creator is the same as the creator
	 * of that DiSCO
	 * @param discoID
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getDiSCOAllAgentVersions(URI discoID) throws RMapException;
	/**
	 * Get latest version of DiSCO (same agent as creator of DiSCO)
	 * @param discoID
	 * @return
	 * @throws RMapException
	 */
	public RMapDiSCO getDiSCOLatestVersion (URI discoID) throws RMapException;
	/**
	 * Get previous version created by same system agent, if any, of this DiSCO
	 * @param discoID
	 * @return
	 * @throws RMapException
	 */
	public RMapDiSCO getDiSCOPreviousVersion (URI discoID) throws RMapException;
	/**
	 * Get next version created by same system agent, if any, of this DiSCO
	 * @param discoID
	 * @return
	 * @throws RMapException
	 */
	public RMapDiSCO getDiSCONextVersion (URI discoID)throws RMapException;
	/**
	 * Get all events associated with a DiSCO
	 * @param discoID
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getDiSCOEvents(URI discoID) throws RMapException;
	

	// Event services
	/**
	 * 
	 * @param eventId
	 * @return
	 * @throws RMapException
	 */
	public RMapEvent readEvent(URI eventId) throws RMapException;
	/**
	 * 
	 * @param eventID
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getEventRelatedStatements(URI eventID) throws RMapException;
	/**
	 * 
	 * @param eventID
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getEventRelatedResources (URI eventID) throws RMapException;
	/**
	 * 
	 * @param eventID
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getEventRelatedDiSCOS (URI eventID) throws RMapException;
	
	//TODO  add method to API to get event-related scholarly agents
	/**
	 * 
	 * @param eventID
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getEventRelatedAgents (URI eventID) throws RMapException;
	
	// Agent services
	/**
	 * Get RMapAgent corresponding to agentID
	 * @param agentID
	 * @return
	 * @throws RMapException
	 */
	public RMapAgent readAgent (URI agentID) throws RMapException;

}

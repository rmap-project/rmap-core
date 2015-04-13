/**
 * 
 */
package info.rmapproject.core.rmapservice;

import java.net.URI;
import java.util.List;

import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapDiSCONotFoundException;
import info.rmapproject.core.exception.RMapEventNotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapProfileNotFoundException;
import info.rmapproject.core.exception.RMapStatementNotFoundException;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.agent.RMapProfile;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.model.statement.RMapStatement;
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
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getResourceRelatedAll (URI uri, RMapStatus statusCode) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Get all RMap Statements with a specified status code whose subject or object matches 
	 * a Resource URI 
	 * @param uri
	 * @param statusCode
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getResourceRelatedStmts (URI uri, RMapStatus statusCode) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Get all RMapEvents related to a Resource URI
	 * @param uri
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getResourceRelatedEvents (URI uri) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Get all RMapDiSCOs with a specified status code related to a Resource URI 
	 * @param uri
	 * @param statusCode
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getResourceRelatedDiSCOs (URI uri, RMapStatus statusCode) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Get all RMapAgents with a specified status code related to a Resource URI 
	 * @param uri
	 * @param statusCode
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getResourceRelatedAgents (URI uri, RMapStatus statusCode) throws RMapException, RMapDefectiveArgumentException;
	
	// Statement services 
	/**
	 * Get the RMapStatement identified by a specific URI
	 * @param id
	 * @return
	 * @throws RMapException
	 * @throws RMapStatementNotFoundException 
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapStatement readStatement (URI id) throws RMapException, RMapStatementNotFoundException, RMapDefectiveArgumentException;
	/**
	 * Get the URI for the RMapStatement matching the supplied subject, predicate and objectc
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public URI getStatementID (RMapResource subject, RMapUri predicate, RMapValue object) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Return the RMapStatements identified by the listed URIS
	 * @param ids
	 * @return
	 * @throws RMapException
	 * @throws RMapStatementNotFoundException 
	 * @throws RMapDefectiveArgumentException 
	 */
	//TODO  add status as parameter, and only allow Active, Inactive
	public List<RMapStatement> readStatements (List<URI> ids) throws RMapException, RMapStatementNotFoundException, RMapDefectiveArgumentException;
	/**
	 * Get the status of the RMapStatement identified by the provided URI
	 * @param stmtId
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapStatus getStatementStatus(URI stmtId) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Get all RMapEvents related to the RMapStatement identified by the provided URI
	 * @param stmtId
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getStatementEvents(URI stmtId) throws RMapException, RMapDefectiveArgumentException;
	
	
	// DiSCO services
	/**
	 * Return the DiSCO
	 * @param discoID
	 * @return
	 * @throws RMapException
	 * @throws RMapDiSCONotFoundException 
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapDiSCO readDiSCO(URI discoID) throws RMapException, RMapDiSCONotFoundException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @param systemAgent
	 * @param disco
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapEvent createDiSCO(RMapUri systemAgent, RMapDiSCO disco)  throws RMapException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @param discoId
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapStatus getDiSCOStatus(URI discoId) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @param systemAgent
	 * @param oldDiscoId
	 * @param disco
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapEvent updateDiSCO(RMapUri systemAgent, URI oldDiscoId, RMapDiSCO disco) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Soft delete (tombstone) of a DiSCO
	 * @param discoID
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapEvent deleteDiSCO (URI discoID, RMapUri systemAgent) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Get all versions of a DiSCO whether created by original creator of DiSCO or by some
	 * other agent
	 * @param discoID
	 * @return
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException 
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getDiSCOAllVersions(URI discoID) throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException;
	/**
	 * Get all versions of a DiSCO whose creator is the same as the creator
	 * of that DiSCO
	 * @param discoID
	 * @return
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException 
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getDiSCOAllAgentVersions(URI discoID) throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException;
	/**
	 * Get latest version of DiSCO (same agent as creator of DiSCO)
	 * @param discoID
	 * @return
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException 
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapDiSCO getDiSCOLatestVersion (URI discoID) throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException;
	/**
	 * Get previous version created by same system agent, if any, of this DiSCO
	 * @param discoID
	 * @return
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException 
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapDiSCO getDiSCOPreviousVersion (URI discoID) throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException;
	/**
	 * Get next version created by same system agent, if any, of this DiSCO
	 * @param discoID
	 * @return
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException 
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapDiSCO getDiSCONextVersion (URI discoID)throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException;
	/**
	 * Get all events associated with a DiSCO
	 * @param discoID
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getDiSCOEvents(URI discoID) throws RMapException, RMapDefectiveArgumentException;
	

	// Event services
	/**
	 * 
	 * @param eventId
	 * @return
	 * @throws RMapException
	 * @throws RMapEventNotFoundException 
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapEvent readEvent(URI eventId)  throws RMapException, RMapEventNotFoundException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @param eventID
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getEventRelatedStatements(URI eventID) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @param eventID
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getEventRelatedResources (URI eventID) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @param eventID
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getEventRelatedDiSCOS (URI eventID) throws RMapException, RMapDefectiveArgumentException;
	
	//TODO  add method to API to get event-related scholarly agents
	/**
	 * 
	 * @param eventID
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getEventRelatedAgents (URI eventID) throws RMapException, RMapDefectiveArgumentException;
	
	// Agent services
	/**
	 * Get RMapAgent corresponding to agentID
	 * @param agentID
	 * @return
	 * @throws RMapException
	 * @throws RMapAgentNotFoundException 
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapAgent readAgent (URI agentID) throws RMapException, RMapAgentNotFoundException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @param agentId
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getAgentRelatedProfiles (URI agentId) throws RMapException, RMapDefectiveArgumentException;
	
	// Agent profile services
	
	/**
	 * 
	 * @param profileId
	 * @return
	 * @throws RMapException
	 * @throws RMapProfileNotFoundException 
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapProfile readProfile (URI profileId)  throws RMapException, RMapProfileNotFoundException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @param profileId
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getProfileRelatedIdentities (URI profileId) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @param profileId
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public URI getProfilePreferredIdentity (URI profileId) throws RMapException, RMapDefectiveArgumentException;
	
	

}

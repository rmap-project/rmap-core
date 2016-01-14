/**
 * 
 */
package info.rmapproject.core.rmapservice;

import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapDiSCONotFoundException;
import info.rmapproject.core.exception.RMapEventNotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapTriple;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.model.event.RMapEvent;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 *
 *  @author khanson, smorrissey
 *
 */
public interface RMapService {
	/**
	 * Get the list of triples comprised by statements that reference a resource and whose status matches provided status code
	 * @param uri Resource to be matched in statements
	 * @param statusCode
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public List<RMapTriple>getResourceRelatedTriples(URI uri, RMapStatus statusCode) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Get the list of triples comprised by statements that reference a resource and whose properties match the filters provided
	 * @param uri Resource to be matched in statements
	 * @param statusCode
	 * @param systemAgents
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public List<RMapTriple>getResourceRelatedTriples(URI uri, RMapStatus statusCode, List<URI> systemAgents, Date dateFrom, Date dateTo) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Get all RMapEvents related to a Resource URI
	 * @param uri
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getResourceRelatedEvents (URI uri) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Get all RMapEvents related to a Resource URI that match the filters provided
	 * @param uri
	 * @param systemAgents
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getResourceRelatedEvents (URI uri, List<URI> systemAgents, Date dateFrom, Date dateTo) throws RMapException, RMapDefectiveArgumentException;
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
	 * Get all RMapDiSCOs related to a Resource URI that match the filters provided
	 * @param uri
	 * @param statusCode
	 * @param systemAgents
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getResourceRelatedDiSCOs (URI uri, RMapStatus statusCode, List<URI> systemAgents, Date dateFrom, Date dateTo) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Get all RMapAgents that asserted a statement containing the Resource URI provided 
	 * @param uri
	 * @param statusCode - applies to DiSCO status
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getResourceAssertingAgents (URI uri, RMapStatus statusCode) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Get all RMapAgents that asserted a statement containing the Resource URI provided that match the filters specified
	 * @param uri
	 * @param statusCode - applies to DiSCO status
	 * @param systemAgents
	 * @param dateFrom - applies to DiSCO status
	 * @param dateTo - applies to DiSCO status
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getResourceAssertingAgents (URI uri, RMapStatus statusCode, List<URI> systemAgents, Date dateFrom, Date dateTo) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Determine what types are associated with a given resource in a specific context (e.g. within a DiSCO)
	 * @param resourceUri URI for resource whose type is being checked
	 * @param contextURI URI for context in which type is to be checked
	 * @return Set of URIs indicating type of the resource, or null if no type statement for resource is found in that context
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public Set<URI> getResourceRdfTypes(URI resourceUri, URI contextURI) throws RMapException, RMapDefectiveArgumentException;
	
	/**
	 * Determine what types are associated with a given resource in any context
	 * @param resourceUri URI for resource whose type is being checked
	 * @param statusCode
	 * @return Map from context to set of any type statements in that context for that resources, or null if no type statement
	 * for resource is found in any context
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public Map<URI, Set<URI>> getResourceRdfTypesAllContexts(URI resourceUri, RMapStatus statusCode) throws RMapException, RMapDefectiveArgumentException;
	
	
	//Statement Services
	/**
	 * Get a list of DiSCOs that contain the statement passed in
	 * @param subject of statement
	 * @param predicate of statement
	 * @param object of statement
	 * @param statusCode to match DiSCO status
	 * @return URI list of DiSCOs containing statement
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public List<URI> getStatementRelatedDiSCOs(URI subject, URI predicate, RMapValue object, RMapStatus statusCode) 
							throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Get a list of DiSCOs that contain the statement passed in and match the filters provided
	 * @param subject of statement
	 * @param predicate of statement
	 * @param object of statement
	 * @param statusCode to match DiSCO status
	 * @param systemAgents
	 * @param dateFrom
	 * @param dateTo
	 * @return URI list of DiSCOs containing statement
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public List<URI> getStatementRelatedDiSCOs(URI subject, URI predicate, RMapValue object, 
							RMapStatus statusCode, List<URI> systemAgents, Date dateFrom, Date dateTo) 
							throws RMapException, RMapDefectiveArgumentException;
	

	/**
	 * Get a list of Agents that have asserted the statement passed in
	 * @param subject of statement
	 * @param predicate of statement
	 * @param object of statement
	 * @param statusCode to match DiSCO status
	 * @param dateFrom
	 * @param dateTo
	 * @return URI list of Agents containing statement
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public List<URI> getStatementAssertingAgents(URI subject, URI predicate, RMapValue object, 
												RMapStatus statusCode) throws RMapException, RMapDefectiveArgumentException;

	/**
	 * Get a list of Agents that have asserted the statement passed and were asserted within the date range provided
	 * @param subject of statement
	 * @param predicate of statement
	 * @param object of statement
	 * @param statusCode to match DiSCO status
	 * @param dateFrom
	 * @param dateTo
	 * @return URI list of Agents containing statement
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public List<URI> getStatementAssertingAgents(URI subject, URI predicate, RMapValue object, 
												RMapStatus statusCode, Date dateFrom, Date dateTo) throws RMapException, RMapDefectiveArgumentException;
	
	
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
	 * Return DiSCO, with associated Status and URIs for next, previous and latest versions, if any
	 * @param discoID URI of DiSCO to be read
	 * @return DTO containing DiSCO, with associated Status and URIs for next, previous and latest versions, if any
	 * @throws RMapException
	 * @throws RMapDiSCONotFoundException if Not found
	 * @throws RMapDefectiveArgumentException
	 */
	public RMapDiSCODTO readDiSCODTO (URI discoID) throws RMapException, RMapDiSCONotFoundException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @param systemAgent
	 * @param disco
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapEvent createDiSCO(URI systemAgent, RMapDiSCO disco)  throws RMapException, RMapDefectiveArgumentException;
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
	public RMapEvent updateDiSCO(URI systemAgent, URI oldDiscoId, RMapDiSCO disco) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Inactivate a DiSCO.  Can only be performed by same agent that created DiSCO
	 * @param systemAgent
	 * @param oldDiscoId
	 * @return
	 * @throws RMapException
	 * @throws RMapDiSCONotFoundException
	 * @throws RMapDefectiveArgumentException
	 */
	public RMapEvent inactivateDiSCO(URI systemAgent, URI oldDiscoId) throws RMapException, RMapDiSCONotFoundException,
	RMapDefectiveArgumentException;
	/**
	 * Soft delete (tombstone) of a DiSCO
	 * @param discoID
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapEvent deleteDiSCO (URI discoID, URI systemAgent) throws RMapException, RMapDefectiveArgumentException;
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
	 * 
	 * @param discoID
	 * @return
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 * @throws RMapDefectiveArgumentException
	 */
	public RMapDiSCODTO getDiSCODTOLatestVersion (URI discoID) throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @param discoID
	 * @return
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 * @throws RMapDefectiveArgumentException
	 */
	public URI getDiSCOIdLatestVersion(URI discoID) throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException;
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
	 * 
	 * @param discoID
	 * @return
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 * @throws RMapDefectiveArgumentException
	 */
	public RMapDiSCODTO getDiSCODTOPreviousVersion (URI discoID) throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @param discoID
	 * @return
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 * @throws RMapDefectiveArgumentException
	 */
	public URI getDiSCOIdPreviousVersion(URI discoID) throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @param discoID
	 * @return
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 * @throws RMapDefectiveArgumentException
	 */
	public URI getDiSCOIdNextVersion (URI discoID)throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException;
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
	 * 
	 * @param discoID
	 * @return
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 * @throws RMapDefectiveArgumentException
	 */
	public RMapDiSCODTO getDiSCODTONextVersion (URI discoID)throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException;
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
	public List<URI> getEventRelatedResources (URI eventID) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @param eventID
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public List<URI> getEventRelatedDiSCOS (URI eventID) throws RMapException, RMapDefectiveArgumentException;
	
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
	 * Create a new agent. Note: In most instances the agentID should match the URI in agent.getId()
	 * - in other words agents typically create their own record if they registered through the GUI.  
	 * There is an option to indicate that an agent wasn't created by themselves, which might be used for batch loading etc.
	 * @param agent RMapAgent object to be instantiated in system
	 * @param creatingAgentID ID of (system) agent creating this new Agent
	 * @return RMapEvent associated with creation of Agent
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public RMapEvent createAgent(RMapAgent agent, URI creatingAgentID) throws RMapException, RMapDefectiveArgumentException;
	
	/**
	 * Create a new agent using the agent properties. Note: In most instances the agentID should match the URI in creatingAgentId
	 * - in other words agents typically create their own record if they registered through the GUI.  
	 * There is an option to indicate that an agent wasn't created by themselves, which might be used for batch loading etc.
	 * @param agentID
	 * @param name
	 * @param identityProvider
	 * @param authKeyUri
	 * @param creatingAgentID
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public RMapEvent createAgent(URI agentID, String name, URI identityProvider, URI authKeyUri, URI creatingAgentID) throws RMapException, RMapDefectiveArgumentException;
	
	/**
	 * Update existing agent using the agent properties. Note: In most instances the agentID should match the URI in agent.getId()
	 * - in other words agents typically create their own record if they registered through the GUI.  
	 * There is an option to indicate that an agent wasn't created by themselves, which might be used for batch loading etc.
	 * @param agent RMapAgent object to be instantiated in system
	 * @param creatingAgentID ID of (system) agent creating this new Agent
	 * @return RMapEvent associated with creation of Agent
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public RMapEvent updateAgent(RMapAgent agent, URI creatingAgentID) throws RMapException, RMapDefectiveArgumentException;
	
	/**
	 * Update existing agent using the agent properties. Note: In most instances the agentID should match the URI in agent.getId()
	 * - in other words agents typically create their own record if they registered through the GUI.  
	 * There is an option to indicate that an agent wasn't created by themselves, which might be used for batch loading etc.
	 * @param agentID
	 * @param name
	 * @param identityProvider
	 * @param authKeyUri
	 * @param creatingAgentID
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public RMapEvent updateAgent(URI agentID, String name, URI identityProvider, URI authKeyUri, URI creatingAgentID) throws RMapException, RMapDefectiveArgumentException;
	
	
//	REMOVED FOR NOW - NOT CURRENTLY SUPPORTING AGENT DELETION	
//	/**
//	 * Tombstone an existing agent
//	 * @param systemAgentId
//	 * @param targetAgentID
//	 * @return
//	 * @throws RMapException
//	 * @throws RMapDefectiveArgumentException
//	 */
//	public RMapEvent deleteAgent(URI systemAgentId, URI targetAgentID) throws RMapException, RMapAgentNotFoundException, 
//	RMapDefectiveArgumentException;	
	
	
	/**
	 * 
	 * @param agentId
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public List<URI> getAgentEvents(URI agentId) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @param agentId
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 * @throws RMapAgentNotFoundException
	 */
	public RMapStatus getAgentStatus(URI agentId) throws RMapException, RMapDefectiveArgumentException, RMapAgentNotFoundException;
	
	/**
	 * @param id
	 * @return
	 * @throws RMapException, RMapDefectiveArgumentException
	 */
	public boolean isAgentId(URI id) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @param id
	 * @return
	 * @throws RMapException, RMapDefectiveArgumentException
	 */
	public boolean isDiSCOId(URI id) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * 
	 * @param id
	 * @return
	 * @throws RMapException, RMapDefectiveArgumentException
	 */
	public boolean isEventId(URI id) throws RMapException, RMapDefectiveArgumentException;
	
	
	/**
     * Closes triplestore connection if it is still open.
     * @throws RMapException
     */
    public void closeConnection() throws RMapException;

}

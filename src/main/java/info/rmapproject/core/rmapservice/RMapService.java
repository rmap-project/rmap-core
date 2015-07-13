/**
 * 
 */
package info.rmapproject.core.rmapservice;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapDiSCONotFoundException;
import info.rmapproject.core.exception.RMapEventNotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapTriple;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.model.event.RMapEvent;
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
	 * Get the list of triples comprised by statements that reference a resource and whose status matches provided status code
	 * @param uri Resource to be matched in statements
	 * @param statusCode 
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public List<RMapTriple>getResourceRelatedTriples(URI uri, RMapStatus statusCode) throws RMapException, RMapDefectiveArgumentException;
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
	 * @return Map from context to set of any type statements in that context for that resources, or null if no type statement
	 * for resource is found in any context
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public Map<URI, Set<URI>> getResourceRdfTypesAllContexts(URI resourceUri)throws RMapException, RMapDefectiveArgumentException;
	
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
	 * Inactivate a DiSCO.  Can only be performed by same agent that created DiSCO
	 * @param systemAgent
	 * @param oldDiscoId
	 * @return
	 * @throws RMapException
	 * @throws RMapDiSCONotFoundException
	 * @throws RMapDefectiveArgumentException
	 */
	public RMapEvent inactivateDiSCO(RMapUri systemAgent, URI oldDiscoId) throws RMapException, RMapDiSCONotFoundException,
	RMapDefectiveArgumentException;
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
	 * Create a new agent
	 * @param agentID ID of (system) agent creating this new Agent
	 * @param agent RMapAgent object to be instantiated in system
	 * @return RMapEvent associated with creation of Agent
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public RMapEvent createAgent(URI agentID, RMapAgent agent) throws RMapException, RMapDefectiveArgumentException;
	
	/**
	 * Tombstone an existing agent
	 * @param systemAgentId
	 * @param targetAgentID
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public RMapEvent deleteAgent(URI systemAgentId, URI targetAgentID) throws RMapException, RMapAgentNotFoundException, 
	RMapDefectiveArgumentException;	
	/**
	 * 
	 * @param agentId
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public List<URI> getAgentEvents(URI agentId) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Find all RMapAgents that are representations of an agent identified by some external (non-RMAP) uri
	 * Include RMapAgents created by any RMapAgent
	 * @param uri External (non-RMAP) identifier for an Agent
	 * @return List of ids for Agents that represent the agent identified by external URI
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public List<URI> getAgentRepresentationsAnyCreator(URI uri) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Find all RMapAgents  that are representations of an agent identified by some external (non-RMAP) uri
	 * and that were created by a particular RMapAgent
	 * @param agentURI id of RMapAgent who created agents
	 * @param repURI URI for some non-RMap identified agent
	 * @return List of ids for RMapAgents that represent the agent identified by external URI
	 * @throws RMapException
	 * @throws RMapAgentNotFoundException
	 * @throws RMapDefectiveArgumentException
	 */
	public List<URI> getAgentRepresentations(URI agentURI, URI repURI) throws RMapException, 
	RMapAgentNotFoundException, RMapDefectiveArgumentException;
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
     * Closes triplestore connection if it is still open.
     * @throws RMapException
     */
    public void closeConnection() throws RMapException;

}

/**
 * 
 */
package info.rmapproject.core.rmapservice;

import java.net.URI;
import java.util.List;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapAgent;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapNonLiteral;
import info.rmapproject.core.model.RMapDiSCO;
import info.rmapproject.core.model.RMapEvent;
import info.rmapproject.core.model.RMapStatement;
import info.rmapproject.core.model.RMapStatementBag;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapResource;

/**
 *
 * @author smorrissey
 *
 */
public interface RMapService {

	// Resource services
	/**
	 * Get all Resources of all RMap object types with a specified status code related to a Resource URI 
	 * @param uri
	 * @param statusCode
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getRelatedAll (URI uri, RMapStatus statusCode) throws RMapException;
	/**
	 * Get all RMap Statements with a specified status code related to a Resource URI 
	 * @param uri
	 * @param statusCode
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getRelatedStmts (URI uri, RMapStatus statusCode) throws RMapException;
	/**
	 * Get all RMapEvents related to a Resource URI
	 * @param uri
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getRelatedEvents (URI uri) throws RMapException;
	/**
	 * Get all RMapDiSCOs with a specified status code related to a Resource URI 
	 * @param uri
	 * @param statusCode
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getRelatedDiSCOs (URI uri, RMapStatus statusCode) throws RMapException;
	/**
	 * Get all RMapAgents with a specified status code related to a Resource URI 
	 * @param uri
	 * @param statusCode
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getAgents (URI uri, RMapStatus statusCode) throws RMapException;
	
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
	public URI getStatementID (RMapNonLiteral subject, RMapUri predicate, RMapResource object) throws RMapException;
	/**
	 * Return the RMapStatements identified by the listed URIS
	 * @param ids
	 * @return
	 * @throws RMapException
	 */
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
	 * 
	 * @param aggregatedResources
	 * @param creator
	 * @param relatedStatements
	 * @param desc
	 * @return
	 * @throws RMapException
	 */
	public RMapEvent createDisco(RMapAgent systemAgent, List<URI> aggregatedResources,
			RMapResource creator, RMapStatementBag relatedStatements, RMapResource desc)  throws RMapException;
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
			List<URI> aggregatedResources,RMapStatementBag relatedStatements, RMapResource creator, 
			RMapResource desc) throws RMapException;
	/**
	 * 
	 * @param discoID
	 * @return
	 * @throws RMapException
	 */
	public RMapEvent deleteDiSCO (URI discoID, RMapAgent systemAgent) throws RMapException;
	/**
	 * 
	 * @param discoID
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getAllDiSCOVersions(URI discoID) throws RMapException;
	/**
	 * 
	 * @param discoID
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getAllAgentDiSCOVersions(URI discoID) throws RMapException;
	/**
	 * 
	 * @param discoID
	 * @return
	 * @throws RMapException
	 */
	public RMapDiSCO getLatestVersionDiSCO (URI discoID) throws RMapException;
	/**
	 * Get previous version created by same system agent, if any, of this DiSCO
	 * @param discoID
	 * @return
	 * @throws RMapException
	 */
	public RMapDiSCO getPreviousVersionDiSCO (URI discoID) throws RMapException;
	/**
	 * Get next version created by same system agent, if any, of this DiSCO
	 * @param discoID
	 * @return
	 * @throws RMapException
	 */
	public RMapDiSCO getNextVersionDiSCO (URI discoID)throws RMapException;
	/**
	 * 
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

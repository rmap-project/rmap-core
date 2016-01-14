/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

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
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.rmapservice.RMapDiSCODTO;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryException;

/**
 *  @author khanson, smorrissey
 *
 */
public class ORMapService implements RMapService {

	protected SesameTriplestore ts = null;
	/**
	 * 
	 */
	public ORMapService() throws RMapException{		
		try {
			ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
		} catch (Exception e) {
			throw new RMapException("Unable to create Sesame TripleStore: ", e);
		}	
	}

	@Override
	public void closeConnection() throws RMapException {
		try {
            ts.closeConnection();
		}
		catch(Exception e)  {
            throw new RMapException("Could not close connection");
		}
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getResourceRelatedTriples(URI, RMapStatus)
	 */
	public List<RMapTriple> getResourceRelatedTriples(URI uri, RMapStatus statusCode) 
			throws RMapException, RMapDefectiveArgumentException {
		return getResourceRelatedTriples(uri, statusCode, null, null, null);
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getResourceRelatedTriples(URI, RMapStatus, List<URI>, Date, Date)
	 */
	public List<RMapTriple> getResourceRelatedTriples(URI uri, RMapStatus statusCode, List<URI> systemAgents, Date dateFrom, Date dateTo)
			throws RMapException, RMapDefectiveArgumentException {
		
		ORMapResourceMgr resourcemgr = new ORMapResourceMgr();
		
		if (uri==null){
			throw new RMapDefectiveArgumentException("null uri");
		}
		org.openrdf.model.URI mUri = ORAdapter.uri2OpenRdfUri(uri);

		List<org.openrdf.model.URI> mSystemAgents = ORAdapter.uriList2OpenRdfUriList(systemAgents);
		
		Set<Statement> stmts = resourcemgr.getRelatedTriples(mUri, statusCode, mSystemAgents, dateFrom, dateTo, ts);
		
		List<RMapTriple> triples = new ArrayList<RMapTriple>();
		for (Statement stmt:stmts){
			RMapTriple triple = ORAdapter.openRdfStatement2RMapTriple(stmt);
			triples.add(triple);
		}
		return triples;
	}
	

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getRelatedEvents(java.net.URI)
	 */
	public List<URI> getResourceRelatedEvents (URI uri) 
			throws RMapException, RMapDefectiveArgumentException {
		return getResourceRelatedEvents(uri, null, null, null);
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getRelatedEvents(java.net.URI, List<URI>, Date, Date)
	 */
	public List<URI> getResourceRelatedEvents (URI uri, List<URI> systemAgents, Date dateFrom, Date dateTo) throws RMapException, RMapDefectiveArgumentException {
		if (uri==null){
			throw new RMapDefectiveArgumentException("null uri");
		}

		ORMapResourceMgr resourcemgr = new ORMapResourceMgr();
		
		org.openrdf.model.URI mUri = ORAdapter.uri2OpenRdfUri(uri);

		List<org.openrdf.model.URI> mSystemAgents = ORAdapter.uriList2OpenRdfUriList(systemAgents);
		
		Set<org.openrdf.model.URI> orEvents = resourcemgr.getRelatedEvents(mUri, mSystemAgents, dateFrom, dateTo, ts);
		List<URI> uris = new ArrayList<URI>();
		for (org.openrdf.model.URI event:orEvents){
			URI dUri = ORAdapter.openRdfUri2URI(event);
			uris.add(dUri);
		}
		return uris;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getRelatedDiSCOs(java.net.URI, info.rmapproject.core.model.RMapStatus)
	 */
	public List<URI> getResourceRelatedDiSCOs (URI uri, RMapStatus statusCode)
			throws RMapException, RMapDefectiveArgumentException {
		return getResourceRelatedDiSCOs(uri, statusCode, null, null, null);
	}
		
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getRelatedDiSCOs(java.net.URI, info.rmapproject.core.model.RMapStatus, List<URI>, Date, Date)
	 */
	public List<URI> getResourceRelatedDiSCOs (URI uri, RMapStatus statusCode, List<URI> systemAgents, Date dateFrom, Date dateTo)
			throws RMapException, RMapDefectiveArgumentException {
		if (uri==null){
			throw new RMapDefectiveArgumentException("null uri");
		}

		ORMapResourceMgr resourcemgr = new ORMapResourceMgr();
		
		org.openrdf.model.URI mUri = ORAdapter.uri2OpenRdfUri(uri);	

		List<org.openrdf.model.URI> mSystemAgents = ORAdapter.uriList2OpenRdfUriList(systemAgents);

		Set<org.openrdf.model.URI> orDiscos = resourcemgr.getRelatedDiSCOS(mUri, statusCode, mSystemAgents, dateFrom, dateTo, ts);
		List<URI> uris = new ArrayList<URI>();
		for (org.openrdf.model.URI disco:orDiscos){
			URI dUri = ORAdapter.openRdfUri2URI(disco);
			uris.add(dUri);
		}
		return uris;		
	}
	
	

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getResourceAssertingAgents(URI, RMapStatus)
	 */
	public List<URI> getResourceAssertingAgents(URI uri, RMapStatus statusCode) 
			throws RMapException, RMapDefectiveArgumentException {
		return getResourceAssertingAgents(uri, statusCode, null, null, null);
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getResourceAssertingAgents (URI, RMapStatus, List<URI>, Date, Date)
	 */
	public List<URI> getResourceAssertingAgents (URI uri, RMapStatus statusCode, List<URI> systemAgents, Date dateFrom, Date dateTo)
			throws RMapException, RMapDefectiveArgumentException {
		if (uri==null){
			throw new RMapDefectiveArgumentException("null uri");
		}

		ORMapResourceMgr resourcemgr = new ORMapResourceMgr();
		org.openrdf.model.URI resource = ORAdapter.uri2OpenRdfUri(uri);

		List<org.openrdf.model.URI> mSystemAgents = ORAdapter.uriList2OpenRdfUriList(systemAgents);
		
		Set<org.openrdf.model.URI> resourceAgents = resourcemgr.getResourceAssertingAgents(resource, statusCode, mSystemAgents, dateFrom, dateTo, ts);
		List<URI> uris = new ArrayList<URI>();
		for (org.openrdf.model.URI agent:resourceAgents){
			URI dUri = ORAdapter.openRdfUri2URI(agent);
			uris.add(dUri);
		}
		
		return uris;
	}
	
	@Override
	public Set<URI> getResourceRdfTypes(URI resourceUri, URI contextURI)
			throws RMapException, RMapDefectiveArgumentException {
		if (resourceUri==null){
			throw new RMapDefectiveArgumentException("null resource URI");
		}
		if (contextURI==null){
			throw new RMapDefectiveArgumentException("null context URI");
		}
		ORMapResourceMgr resourcemgr = new ORMapResourceMgr();
		org.openrdf.model.URI rUri = ORAdapter.uri2OpenRdfUri(resourceUri);
		org.openrdf.model.URI cUri = ORAdapter.uri2OpenRdfUri(contextURI);
		Set<URI> returnSet = null;
		Set<org.openrdf.model.URI> uris = resourcemgr.getResourceRdfTypes(rUri,cUri, ts);
		if (uris != null && uris.size()>0){
			returnSet = ORAdapter.openRdfUriSet2UriSet(uris);
		}
		return returnSet;
	}

	@Override
	public Map<URI, Set<URI>> getResourceRdfTypesAllContexts(URI resourceUri, RMapStatus statusCode)
			throws RMapException, RMapDefectiveArgumentException {
		if (resourceUri==null){
			throw new RMapDefectiveArgumentException("null resource URI");
		}
		ORMapResourceMgr resourcemgr = new ORMapResourceMgr();
		org.openrdf.model.URI rUri = ORAdapter.uri2OpenRdfUri(resourceUri);
		Map<URI, Set<URI>> map = null;
		Map<org.openrdf.model.URI, Set<org.openrdf.model.URI>> typesMap = 
				resourcemgr.getResourceRdfTypesAllContexts(rUri, ts);
		if (typesMap != null && typesMap.keySet().size()>0){
			map = new HashMap<URI, Set<URI>>();
			for (org.openrdf.model.URI uri : typesMap.keySet()){
				Set<org.openrdf.model.URI> types = typesMap.get(uri);
				URI key = ORAdapter.openRdfUri2URI(uri);
				
				Set<URI> values = ORAdapter.openRdfUriSet2UriSet(types);
				
				map.put(key, values);
			}				
		}
		return map;
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getStmtRelatedDiSCOs(java.net.URI, java.net.URI, RMapValue, RMapStatus)
	 */
	public List<URI> getStatementRelatedDiSCOs(URI subject, URI predicate, RMapValue object, 
												RMapStatus statusCode) 
												throws RMapException, RMapDefectiveArgumentException {
		return getStatementRelatedDiSCOs(subject, predicate, object, statusCode, null, null, null);
	}
	
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getStmtRelatedDiSCOs(java.net.URI, java.net.URI, RMapValue, RMapStatus, List<java.net.URI>, Date, Date)
	 */
	public List<URI> getStatementRelatedDiSCOs(URI subject, URI predicate, RMapValue object, 
												RMapStatus statusCode, List<URI> systemAgents, Date dateFrom, Date dateTo) 
												throws RMapException, RMapDefectiveArgumentException {
		if (subject==null){
			throw new RMapDefectiveArgumentException("null subject");
		}
		if (predicate==null){
			throw new RMapDefectiveArgumentException("null predicate");
		}
		if (object==null){
			throw new RMapDefectiveArgumentException("null object");
		}
		ORMapStatementMgr stmtmgr = new ORMapStatementMgr();
		org.openrdf.model.URI orSubject = ORAdapter.uri2OpenRdfUri(subject);
		org.openrdf.model.URI orPredicate = ORAdapter.uri2OpenRdfUri(predicate);
		org.openrdf.model.Value orObject = ORAdapter.rMapValue2OpenRdfValue(object);


		List<org.openrdf.model.URI> mSystemAgents = ORAdapter.uriList2OpenRdfUriList(systemAgents);
		
		List <org.openrdf.model.URI> relatedDiSCOs = 
				stmtmgr.getRelatedDiSCOs(orSubject, orPredicate, orObject, statusCode, 
						 						mSystemAgents, dateFrom, dateTo, ts);
				
		List<URI> returnSet = null;
		if (relatedDiSCOs != null && relatedDiSCOs.size()>0){
			returnSet = ORAdapter.openRdfUriList2UriList(relatedDiSCOs);
		}		
		return returnSet;
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getStmtAssertingAgents(java.net.URI, java.net.URI, RMapValue, RMapStatus)
	 */
	public List<URI> getStatementAssertingAgents(java.net.URI subject, java.net.URI predicate, RMapValue object, 
											RMapStatus statusCode) throws RMapException, RMapDefectiveArgumentException {
		return getStatementAssertingAgents(subject, predicate, object, statusCode, null, null);
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getStmtAssertingAgents(java.net.URI, java.net.URI, RMapValue, RMapStatus, Date, Date)
	 */
	public List<URI> getStatementAssertingAgents(java.net.URI subject, java.net.URI predicate, RMapValue object, 
											RMapStatus statusCode, Date dateFrom, Date dateTo) throws RMapException, RMapDefectiveArgumentException {
		if (subject==null){
			throw new RMapDefectiveArgumentException("null subject");
		}
		if (predicate==null){
			throw new RMapDefectiveArgumentException("null predicate");
		}
		if (object==null){
			throw new RMapDefectiveArgumentException("null object");
		}
		ORMapStatementMgr stmtmgr = new ORMapStatementMgr();
		org.openrdf.model.URI orSubject = ORAdapter.uri2OpenRdfUri(subject);
		org.openrdf.model.URI orPredicate = ORAdapter.uri2OpenRdfUri(predicate);
		org.openrdf.model.Value orObject = ORAdapter.rMapValue2OpenRdfValue(object);
		
		Set <org.openrdf.model.URI> assertingAgents = 
				stmtmgr.getAssertingAgents(orSubject, orPredicate, orObject, statusCode, dateFrom, dateTo, ts);
		
		List<URI> returnSet = null;
		if (assertingAgents != null && assertingAgents.size()>0){
			returnSet = new ArrayList<URI>();
			for (org.openrdf.model.URI uri:assertingAgents){
				returnSet.add(ORAdapter.openRdfUri2URI(uri));
			}
		}		
		return returnSet;
	}
	

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#readDiSCO(java.net.URI)
	 */
	public RMapDiSCO readDiSCO(URI discoID) 
	throws RMapException, RMapDiSCONotFoundException, RMapDefectiveArgumentException {
		if (discoID == null){
			throw new RMapDefectiveArgumentException("Null DiSCO id provided");
		}
		ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
		ORMapDiSCODTO dto = discomgr.readDiSCO(ORAdapter.uri2OpenRdfUri(discoID), false, null, null, ts);
		return dto.getRMapDiSCO();
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#readDiSCODTO(java.net.URI)
	 */
	public RMapDiSCODTO readDiSCODTO (URI discoID)
	throws RMapException, RMapDiSCONotFoundException, RMapDefectiveArgumentException {
		if (discoID == null){
			throw new RMapDefectiveArgumentException("Null DiSCO id provided");
		}
		ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
		ORMapDiSCODTO dto = discomgr.readDiSCO(ORAdapter.uri2OpenRdfUri(discoID), true, null, null, ts);
		return dto;
	}

	@Override
	public RMapEvent createDiSCO(URI systemAgent, RMapDiSCO disco)
			throws RMapException, RMapDefectiveArgumentException {
		if (systemAgent==null){
			throw new RMapDefectiveArgumentException("Null Agent id provided");
		}
		if (disco==null){
			throw new RMapDefectiveArgumentException("Null DiSCO provided");
		}
		if (!(disco instanceof ORMapDiSCO)){
			throw new RMapDefectiveArgumentException("disco not instance of ORMapDiSCO");
		}
		ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
		RMapEvent createEvent = discomgr.createDiSCO(ORAdapter.uri2OpenRdfUri(systemAgent), (ORMapDiSCO)disco, ts);
		return createEvent;
	}

	/**
	 * Create DiSCO from list of OpenRdf Statements
	 * @param systemAgent Agent creating DiSCO
	 * @param stmts List of Statements making up DiSCO
	 * @return Creation Event for successfully created DiSCO
	 * @throws RMapException if Statements do not comprise a valid DiSCO, or
	 * if the DiSCO cannot be created in the triplestore
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapEvent createDisco(URI systemAgent, List<Statement> stmts )
	throws RMapException, RMapDefectiveArgumentException{
		if (systemAgent==null){
			throw new RMapDefectiveArgumentException("Null Agent id provided");
		}
		if (stmts==null || stmts.size()==0){
			throw new RMapDefectiveArgumentException("Null or empty Statement List provided");
		}
		RMapEvent createEvent = null;
		try {
			ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
			ORMapDiSCO disco = new ORMapDiSCO(stmts);
			createEvent = discomgr.createDiSCO(ORAdapter.uri2OpenRdfUri(systemAgent), disco, ts);
		} catch (RMapException | RMapDefectiveArgumentException ex) {
			try {
				//there has been an error during an update so try to rollback the transaction
				ts.rollbackTransaction();
			} catch(RepositoryException rollbackException) {
				throw new RMapException("Could not rollback changes after error. Please check your DiSCO record for errors.", ex);
			}
			throw ex;	
		}	
		
		return createEvent;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getDiSCOStatus(java.net.URI)
	 */
	public RMapStatus getDiSCOStatus(URI discoId) throws RMapException, RMapDefectiveArgumentException {
		if (discoId ==null){
			throw new RMapDefectiveArgumentException("Null DiSCO id provided");
		}
		ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
		RMapStatus status = discomgr.getDiSCOStatus(ORAdapter.uri2OpenRdfUri(discoId), ts);
		return status;
	}

	@Override
	public RMapEvent updateDiSCO(URI systemAgent, URI oldDiscoId, RMapDiSCO disco)
			throws RMapException, RMapDefectiveArgumentException {
		if (systemAgent==null){
			throw new RMapDefectiveArgumentException ("null system agent");
		}
		if (oldDiscoId==null){
			throw new RMapDefectiveArgumentException ("null id for old DiSCO");
		}
		if (disco==null){
			throw new RMapDefectiveArgumentException ("null disco");
		}
		if (!(disco instanceof ORMapDiSCO)){
			throw new RMapDefectiveArgumentException ("disco not instance of ORMapDISCO");
		}
		
		RMapEvent updateEvent = null;
		try {
			ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
			updateEvent = discomgr.updateDiSCO(
										ORAdapter.uri2OpenRdfUri(systemAgent),
										false, 
										ORAdapter.uri2OpenRdfUri(oldDiscoId), 
										(ORMapDiSCO)disco, 
										ts);
		} catch (RMapException | RMapDefectiveArgumentException ex) {
			try {
				//there has been an error during an update so try to rollback the transaction
				ts.rollbackTransaction();
			} catch(RepositoryException rollbackException) {
				throw new RMapException("Could not rollback changes after error. Please check your DiSCO record for errors.", ex);
			}
			throw ex;	
		}	
		
		return updateEvent;
	}

	/**
	 * Update DiSCO with new DiSCO provided as list of OpenRdf Statements.
	 * If list of Statements is null, then just inactivate old DiSCO.
	 * @param systemAgent
	 * @param oldDiscoId
	 * @param stmts
	 * @param justInactivate 
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapEvent updateDiSCO(URI systemAgent, URI oldDiscoId, 
			List<Statement> stmts, boolean justInactivate)
	throws RMapException, RMapDefectiveArgumentException {
		if (systemAgent==null){
			throw new RMapDefectiveArgumentException ("null system agent");
		}
		if (oldDiscoId==null){
			throw new RMapDefectiveArgumentException ("null id for old DiSCO");
		}
		if (stmts==null || stmts.size()==0){
			throw new RMapDefectiveArgumentException("Null or empty Statement List provided");
		}
		RMapEvent updateEvent = null;
		try {
			ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
			ORMapDiSCO disco = new ORMapDiSCO(stmts);
			updateEvent = discomgr.updateDiSCO(ORAdapter.uri2OpenRdfUri(systemAgent),
							false, ORAdapter.uri2OpenRdfUri(oldDiscoId), disco, ts);

		} catch (RMapException | RMapDefectiveArgumentException ex) {
			try {
				//there has been an error during an update so try to rollback the transaction
				ts.rollbackTransaction();
			} catch(RepositoryException rollbackException) {
				throw new RMapException("Could not rollback changes after error. Please check your DiSCO record for errors.", ex);
			}
			throw ex;	
		}	
		return updateEvent;
	}

	@Override
	public RMapEvent inactivateDiSCO(URI systemAgent, URI oldDiscoId)
			throws RMapException, RMapDiSCONotFoundException,
			RMapDefectiveArgumentException {
		if (systemAgent==null){
			throw new RMapDefectiveArgumentException ("null system agent");
		}
		if (oldDiscoId==null){
			throw new RMapDefectiveArgumentException ("null id for old DiSCO");
		}
		RMapEvent inactivateEvent = null;
		try {
			ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
			org.openrdf.model.URI agentUri = ORAdapter.uri2OpenRdfUri(systemAgent);
			inactivateEvent = discomgr.updateDiSCO(agentUri,
								true, ORAdapter.uri2OpenRdfUri(oldDiscoId), null, ts);
		} catch (RMapException | RMapDefectiveArgumentException ex) {
			try {
				//there has been an error during an update so try to rollback the transaction
				ts.rollbackTransaction();
			} catch(RepositoryException rollbackException) {
				throw new RMapException("Could not rollback changes after error. Please check your DiSCO record for errors.", ex);
			}
			throw ex;	
		}	
		return inactivateEvent;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#deleteDiSCO(java.net.URI, java.net.URI)
	 */
	public RMapEvent deleteDiSCO(URI discoID, URI systemAgent) 
			throws RMapException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		if (systemAgent==null){
			throw new RMapDefectiveArgumentException ("null system agent");
		}
		RMapEvent tombstoneEvent = null;
		try {
			ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
			tombstoneEvent = discomgr.tombstoneDiSCO(ORAdapter.uri2OpenRdfUri(systemAgent),
							ORAdapter.uri2OpenRdfUri(discoID), ts);
		} catch (RMapException ex) {
			try {
				//there has been an error during an update so try to rollback the transaction
				ts.rollbackTransaction();
			} catch(RepositoryException rollbackException) {
				throw new RMapException("Could not rollback changes after error. Please check your DiSCO record for errors.", ex);
			}
			throw ex;	
		}	
		return tombstoneEvent;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getAllDiSCOVersions(java.net.URI)
	 */
	public List<URI> getDiSCOAllVersions(URI discoID) 
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				discomgr.getAllDiSCOVersions(ORAdapter.uri2OpenRdfUri(discoID),
						false,ts);
		List<org.openrdf.model.URI> versions = new ArrayList<org.openrdf.model.URI>();
		versions.addAll(event2disco.values());
		List<URI> uris = new ArrayList<URI>();
		for (org.openrdf.model.URI version:versions){
			uris.add(ORAdapter.openRdfUri2URI(version));
		}
		return uris;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getAllAgentDiSCOVersions(java.net.URI)
	 */
	public List<URI> getDiSCOAllAgentVersions(URI discoID) 
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				discomgr.getAllDiSCOVersions(ORAdapter.uri2OpenRdfUri(discoID), true, ts);
		List<org.openrdf.model.URI> versions = new ArrayList<org.openrdf.model.URI>();
		versions.addAll(event2disco.values());		
		List<URI> uris = new ArrayList<URI>();
		for (org.openrdf.model.URI version:versions){
			uris.add(ORAdapter.openRdfUri2URI(version));
		}
		return uris;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getLatestVersionDiSCO(java.net.URI)
	 */
	public RMapDiSCO getDiSCOLatestVersion(URI discoID) 
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {			
		if (discoID == null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
		org.openrdf.model.URI dUri  = ORAdapter.uri2OpenRdfUri(discoID);
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				discomgr.getAllDiSCOVersions(dUri, true, ts);
		org.openrdf.model.URI latestDiscoURi = discomgr.getLatestDiSCOUri(dUri, ts, event2disco);
		RMapDiSCODTO latestDisco = null;		
		if (latestDiscoURi != null){
			latestDisco = discomgr.readDiSCO(latestDiscoURi, false, event2disco, null, ts);
		}
		RMapDiSCO disco = null;
		if (latestDisco != null){
			disco = latestDisco.getRMapDiSCO();
		}
		return disco;
	}
	
	@Override
	public RMapDiSCODTO getDiSCODTOLatestVersion(URI discoID)
			throws RMapException, RMapObjectNotFoundException,
			RMapDefectiveArgumentException {
		if (discoID == null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
		org.openrdf.model.URI dUri  = ORAdapter.uri2OpenRdfUri(discoID);
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				discomgr.getAllDiSCOVersions(dUri, true, ts);
		org.openrdf.model.URI latestDiscoURi = discomgr.getLatestDiSCOUri(dUri, ts, event2disco);
		RMapDiSCODTO latestDisco = null;		
		if (latestDiscoURi != null){
			latestDisco = discomgr.readDiSCO(latestDiscoURi, true, event2disco, null, ts);
		}
		return latestDisco;
	}
	
	@Override
	public URI getDiSCOIdLatestVersion(URI discoID) throws RMapException,
			RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
		org.openrdf.model.URI dUri  = ORAdapter.uri2OpenRdfUri(discoID);
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				discomgr.getAllDiSCOVersions(dUri, true, ts);
		org.openrdf.model.URI latestDisco = discomgr.getLatestDiSCOUri(dUri, ts, event2disco);
		URI discoURI = null;
		if (latestDisco != null){
			ORAdapter.openRdfUri2URI(latestDisco);	
		}
		return discoURI;
	}
	

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getPreviousVersionDiSCO(java.net.URI)
	 */
	public RMapDiSCO getDiSCOPreviousVersion(URI discoID) 
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		RMapDiSCO nextDisco = null;
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
		ORMapEventMgr eventmgr = new ORMapEventMgr();
		org.openrdf.model.URI thisUri = ORAdapter.uri2OpenRdfUri(discoID);
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				discomgr.getAllDiSCOVersions(thisUri, true, ts);
		Map <Date, org.openrdf.model.URI> date2event = eventmgr.getDate2EventMap(event2disco.keySet(),ts);			
		org.openrdf.model.URI prevDisco = discomgr.getPreviousURI(thisUri, event2disco, date2event, ts);		
		RMapDiSCODTO dto  = discomgr.readDiSCO(prevDisco, false, event2disco, date2event, ts);
		if (dto != null){
			nextDisco = dto.getRMapDiSCO();
		}
		return nextDisco;
	}


	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getDiSCODTOPreviousVersion(java.net.URI)
	 */
	@Override
	public RMapDiSCODTO getDiSCODTOPreviousVersion(URI discoID)
			throws RMapException, RMapObjectNotFoundException,
			RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
		ORMapEventMgr eventmgr = new ORMapEventMgr();
		org.openrdf.model.URI thisUri = ORAdapter.uri2OpenRdfUri(discoID);
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				discomgr.getAllDiSCOVersions(thisUri, true, ts);
		Map <Date, org.openrdf.model.URI> date2event = 
				eventmgr.getDate2EventMap(event2disco.keySet(),ts);			
		org.openrdf.model.URI prevDisco = discomgr.getPreviousURI(thisUri, event2disco, date2event, ts);		
		RMapDiSCODTO dto  = discomgr.readDiSCO(prevDisco, true, event2disco, date2event, ts);		
		return dto;
	}

	@Override
	public URI getDiSCOIdPreviousVersion(URI discoID)
			throws RMapException, RMapObjectNotFoundException,
			RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
		ORMapEventMgr eventmgr = new ORMapEventMgr();
		org.openrdf.model.URI thisUri = ORAdapter.uri2OpenRdfUri(discoID);
		URI nextDiscoUri = null;		
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco= discomgr.getAllDiSCOVersions(thisUri, true, ts);
		Map <Date, org.openrdf.model.URI> date2event =  eventmgr.getDate2EventMap(event2disco.keySet(),ts);	
		org.openrdf.model.URI prevDisco = discomgr.getPreviousURI(thisUri, event2disco, date2event, ts);
		if (prevDisco != null){
			nextDiscoUri = ORAdapter.openRdfUri2URI(prevDisco);
		}
		return nextDiscoUri;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getNextVersionDiSCO(java.net.URI)
	 */
	public RMapDiSCO getDiSCONextVersion(URI discoID) 
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
		ORMapEventMgr eventmgr = new ORMapEventMgr();
		org.openrdf.model.URI thisUri = ORAdapter.uri2OpenRdfUri(discoID);		
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco= discomgr.getAllDiSCOVersions(thisUri,true, ts);
		Map <Date, org.openrdf.model.URI> date2event = eventmgr.getDate2EventMap(event2disco.keySet(),ts);
		org.openrdf.model.URI nextDiscoId = discomgr.getNextURI(thisUri, event2disco, date2event, ts);
		RMapDiSCODTO dto = discomgr.readDiSCO(nextDiscoId, false, event2disco, date2event, ts);	
		RMapDiSCO nextDisco = null;
		if (dto != null){
			nextDisco = dto.getRMapDiSCO();
		}
		return nextDisco;
	}
	

	@Override
	public RMapDiSCODTO getDiSCODTONextVersion(URI discoID)
			throws RMapException, RMapObjectNotFoundException,
			RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
		ORMapEventMgr eventmgr = new ORMapEventMgr();
		org.openrdf.model.URI thisUri = ORAdapter.uri2OpenRdfUri(discoID);
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				discomgr.getAllDiSCOVersions(thisUri,true, ts);
		Map <Date, org.openrdf.model.URI> date2event = 
				eventmgr.getDate2EventMap(event2disco.keySet(),ts);
		org.openrdf.model.URI nextDiscoId = discomgr.getNextURI(thisUri, event2disco, date2event, ts);
		RMapDiSCODTO dto = discomgr.readDiSCO(nextDiscoId, true, event2disco, date2event, ts);		
		return dto;
	}
	
	@Override
	public URI getDiSCOIdNextVersion(URI discoID) throws RMapException,
			RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
		org.openrdf.model.URI thisUri = ORAdapter.uri2OpenRdfUri(discoID);
		URI nextDiscoUri = null;
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				discomgr.getAllDiSCOVersions(thisUri,true, ts);
		org.openrdf.model.URI uri = discomgr.getNextURI(thisUri, event2disco, null, ts);
		if (uri != null){
			nextDiscoUri = ORAdapter.openRdfUri2URI(uri);
		}
		return nextDiscoUri;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getDiSCOEvents(java.net.URI)
	 */
	public List<URI> getDiSCOEvents(URI discoID) throws RMapException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		ORMapEventMgr eventmgr = new ORMapEventMgr();
		Set<org.openrdf.model.URI> events = eventmgr.getDiscoRelatedEventIds(ORAdapter.uri2OpenRdfUri(discoID), ts);
		List<URI> uris = new ArrayList<URI>();
		for (org.openrdf.model.URI event:events){
			uris.add(ORAdapter.openRdfUri2URI(event));
		}
		return uris;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#readEvent(java.net.URI)
	 */
	public RMapEvent readEvent(URI eventId) 
	throws RMapException, RMapEventNotFoundException, RMapDefectiveArgumentException {
		if (eventId ==null){
			throw new RMapDefectiveArgumentException ("null event id");
		}
		ORMapEventMgr eventmgr = new ORMapEventMgr();
		return eventmgr.readEvent(ORAdapter.uri2OpenRdfUri(eventId), ts);
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getEventRelatedResources(java.net.URI)
	 */
	public List<URI> getEventRelatedResources(URI eventID) throws RMapException, RMapDefectiveArgumentException {
		if (eventID ==null){
			throw new RMapDefectiveArgumentException ("null event id");
		}
		ORMapEventMgr eventmgr = new ORMapEventMgr();
		List<org.openrdf.model.URI> resources = eventmgr.getRelatedResources(ORAdapter.uri2OpenRdfUri(eventID), ts);
		List<URI> resourceIds = new ArrayList<URI>();
		for (org.openrdf.model.URI resource:resources){
			resourceIds.add(ORAdapter.openRdfUri2URI(resource));
		}
		return resourceIds;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getEventRelatedDiSCOS(java.net.URI)
	 */
	public List<URI> getEventRelatedDiSCOS(URI eventID) throws RMapException, RMapDefectiveArgumentException {
		if (eventID ==null){
			throw new RMapDefectiveArgumentException ("null event id");
		}
		ORMapEventMgr eventmgr = new ORMapEventMgr();
		List<org.openrdf.model.URI> discos = eventmgr.getRelatedDiSCOs(
				ORAdapter.uri2OpenRdfUri(eventID), ts);
		List<URI> discoIds = new ArrayList<URI>();
			for (org.openrdf.model.URI disco:discos){
				discoIds.add(ORAdapter.openRdfUri2URI(disco));
			}
		return discoIds;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getEventRelatedAgents(java.net.URI)
	 */
	public List<URI> getEventRelatedAgents(URI eventID) throws RMapException, RMapDefectiveArgumentException {
		if (eventID ==null){
			throw new RMapDefectiveArgumentException ("null event id");
		}
		ORMapEventMgr eventmgr = new ORMapEventMgr();
		List<org.openrdf.model.URI> agents = eventmgr.getRelatedAgents(ORAdapter.uri2OpenRdfUri(eventID), ts);
		List<URI> agentIds = ORAdapter.openRdfUriList2UriList(agents);

		return agentIds;
	}

	public RMapAgent readAgent(URI agentId) 
	throws RMapException, RMapAgentNotFoundException, RMapDefectiveArgumentException {
		if (agentId==null){
			throw new RMapDefectiveArgumentException("Null agentid");
		}
		ORMapAgentMgr agentmgr = new ORMapAgentMgr();
		ORMapAgent agent = agentmgr.readAgent(ORAdapter.uri2OpenRdfUri(agentId), ts);
		return agent;
	}
	
	@Override
	public RMapEvent createAgent(RMapAgent agent, URI creatingAgentID) 
			throws RMapException, RMapDefectiveArgumentException {
		if (agent==null){
			throw new RMapDefectiveArgumentException("null agent");
		}
		if (!(agent instanceof ORMapAgent)){
			throw new RMapDefectiveArgumentException("unrecognized type for agent");
		}
		if (creatingAgentID==null){
			throw new RMapDefectiveArgumentException("null system agent");
		}
		RMapEvent event = null;
		try {
			ORMapAgentMgr agentmgr = new ORMapAgentMgr();
			org.openrdf.model.URI creatingAgentUri = ORAdapter.uri2OpenRdfUri(creatingAgentID);
			ORMapAgent orAgent = (ORMapAgent)agent;
			event = agentmgr.createAgent(orAgent, creatingAgentUri, ts);
		} catch (RMapException ex) {
			try {
				//there has been an error during an update so try to rollback the transaction
				ts.rollbackTransaction();
			} catch(RepositoryException rollbackException) {
				throw new RMapException("Could not rollback changes after error. Please check your Agent record for errors.", ex);
			}
			throw ex;	
		}	
		
		return event;
	}
		
	@Override
	public RMapEvent createAgent(URI agentID, String name, URI identityProvider, URI authKeyUri, URI creatingAgentID) 
			throws RMapException, RMapDefectiveArgumentException {
		if (agentID==null){
			throw new RMapDefectiveArgumentException("null Agent ID");
		}
		if (name==null){
			throw new RMapDefectiveArgumentException("null Agent name");
		}
		if (identityProvider==null){
			throw new RMapDefectiveArgumentException("null Agent identity provider");
		}
		if (authKeyUri==null){
			throw new RMapDefectiveArgumentException("null Agent authorization ID");
		}
		if (creatingAgentID==null){
			throw new RMapDefectiveArgumentException("null creating agent");
		}
		
		Value nameValue = ORAdapter.getValueFactory().createLiteral(name);
		org.openrdf.model.URI oAgentId = ORAdapter.uri2OpenRdfUri(agentID);
		org.openrdf.model.URI oIdentityProvider = ORAdapter.uri2OpenRdfUri(identityProvider);
		org.openrdf.model.URI oAuthKeyUri = ORAdapter.uri2OpenRdfUri(authKeyUri);
		RMapAgent agent = new ORMapAgent(oAgentId, oIdentityProvider, oAuthKeyUri, nameValue);
		RMapEvent event = createAgent(agent, creatingAgentID);
		return event;
	}


	@Override
	public RMapEvent updateAgent(RMapAgent agent, URI creatingAgentID) 
			throws RMapException, RMapDefectiveArgumentException {
		if (agent==null){
			throw new RMapDefectiveArgumentException("null agent");
		}
		if (!(agent instanceof ORMapAgent)){
			throw new RMapDefectiveArgumentException("unrecognized type for agent");
		}
		if (creatingAgentID==null){
			throw new RMapDefectiveArgumentException("null system agent");
		}
		RMapEvent event = null;
		try{
			ORMapAgentMgr agentmgr = new ORMapAgentMgr();
			org.openrdf.model.URI creatingAgentUri = ORAdapter.uri2OpenRdfUri(creatingAgentID);
			ORMapAgent orAgent = (ORMapAgent)agent;
			event = agentmgr.updateAgent(orAgent, creatingAgentUri, ts);
		} catch (RMapException ex) {
			try {
				//there has been an error during an update so try to rollback the transaction
				ts.rollbackTransaction();
			} catch(RepositoryException rollbackException) {
				throw new RMapException("Could not rollback changes after error. Please check your Agent record for errors.", ex);
			}
			throw ex;	
		}	
		return event;
	}
		
	@Override
	public RMapEvent updateAgent(URI agentID, String name, URI identityProvider, URI authKeyUri, URI creatingAgentID) 
			throws RMapException, RMapDefectiveArgumentException {
		if (agentID==null){
			throw new RMapDefectiveArgumentException("null Agent ID");
		}
		if (name==null){
			throw new RMapDefectiveArgumentException("null Agent name");
		}
		if (identityProvider==null){
			throw new RMapDefectiveArgumentException("null Agent identity provider");
		}
		if (authKeyUri==null){
			throw new RMapDefectiveArgumentException("null Agent authorization ID");
		}
		if (creatingAgentID==null){
			throw new RMapDefectiveArgumentException("null creating agent");
		}
		
		Value nameValue = ORAdapter.getValueFactory().createLiteral(name);
		org.openrdf.model.URI oAgentId = ORAdapter.uri2OpenRdfUri(agentID);
		org.openrdf.model.URI oIdentityProvider = ORAdapter.uri2OpenRdfUri(identityProvider);
		org.openrdf.model.URI oAuthKeyUri = ORAdapter.uri2OpenRdfUri(authKeyUri);
		RMapAgent agent = new ORMapAgent(oAgentId, oIdentityProvider, oAuthKeyUri, nameValue);
		RMapEvent event = updateAgent(agent, creatingAgentID);
		return event;
	}
	

//	REMOVED FOR NOW - NOT CURRENTLY SUPPORTING AGENT DELETION
//	private RMapEvent deleteAgent(URI systemAgentId, URI targetAgentID)
//			throws RMapException, RMapAgentNotFoundException,
//			RMapDefectiveArgumentException {
//		if (systemAgentId==null){
//			throw new RMapDefectiveArgumentException ("null system agent id");
//		}
//		if (targetAgentID==null){
//			throw new RMapDefectiveArgumentException ("null target agent id");
//		}
//		org.openrdf.model.URI systemAgentUri = ORAdapter.uri2OpenRdfUri(systemAgentId);
//		org.openrdf.model.URI targetAgentUri = ORAdapter.uri2OpenRdfUri(targetAgentID);
//
//		ORMapAgentMgr agentmgr = new ORMapAgentMgr();
//		RMapEvent event = agentmgr.tombstoneAgent(systemAgentUri, targetAgentUri, ts);
//		return event;
//	}
	
	@Override
	public List<URI> getAgentEvents(URI agentId) throws RMapException,
			RMapDefectiveArgumentException, RMapAgentNotFoundException {
		org.openrdf.model.URI uri = ORAdapter.uri2OpenRdfUri(agentId);
		if (agentId==null){
			throw new RMapDefectiveArgumentException("null agentId");
		}
		ORMapEventMgr eventmgr = new ORMapEventMgr();
		List<org.openrdf.model.URI> events = eventmgr.getAgentRelatedEventIds(uri, ts);
		
		List<URI> eventUris = ORAdapter.openRdfUriList2UriList(events);
		
		return eventUris;
	}
	
	@Override
	public RMapStatus getAgentStatus(URI agentId) throws RMapException,
			RMapDefectiveArgumentException, RMapAgentNotFoundException {
		if (agentId==null){
			throw new RMapDefectiveArgumentException ("null agentId");
		}
		ORMapAgentMgr agentmgr = new ORMapAgentMgr();
		org.openrdf.model.URI id = ORAdapter.uri2OpenRdfUri(agentId);
		RMapStatus status = agentmgr.getAgentStatus(id, ts);
		return status;
	}

	@Override
	public boolean isAgentId(URI agentId) throws RMapException, RMapDefectiveArgumentException {
		if (agentId==null){
			throw new RMapDefectiveArgumentException ("null Agent ID");
		}
		ORMapAgentMgr agentmgr = new ORMapAgentMgr();
		org.openrdf.model.URI id = ORAdapter.uri2OpenRdfUri(agentId);
		boolean isAgentId = agentmgr.isAgentId(id, ts);
		return isAgentId;
	}

	@Override
	public boolean isEventId(URI eventId) throws RMapException, RMapDefectiveArgumentException {
		if (eventId==null){
			throw new RMapDefectiveArgumentException ("null Event ID");
		}
		ORMapEventMgr eventmgr = new ORMapEventMgr();
		org.openrdf.model.URI id = ORAdapter.uri2OpenRdfUri(eventId);
		boolean isEventId = eventmgr.isEventId(id, ts);
		return isEventId;
	}
	
	@Override
	public boolean isDiSCOId(URI discoId) throws RMapException, RMapDefectiveArgumentException {
		if (discoId==null){
			throw new RMapDefectiveArgumentException ("null DiSCO ID");
		}
		ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
		org.openrdf.model.URI id = ORAdapter.uri2OpenRdfUri(discoId);
		boolean isDiSCOId = discomgr.isDiscoId(id, ts);
		return isDiSCOId;
	}
	
	

}

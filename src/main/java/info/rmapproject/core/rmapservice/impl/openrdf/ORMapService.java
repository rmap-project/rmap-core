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
import info.rmapproject.core.model.RMapUri;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Statement;

/**
 *  @author khanson, smorrissey
 *
 */
public class ORMapService implements RMapService {

	protected ORMapResourceMgr resourcemgr = new ORMapResourceMgr();
	protected ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
	protected ORMapEventMgr eventmgr = new ORMapEventMgr();
	protected ORMapAgentMgr agentmgr = new ORMapAgentMgr();
	protected ORMapStatementMgr stmtmgr = new ORMapStatementMgr();

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
	 * @see info.rmapproject.core.rmapservice.RMapService#getRelatedAll(java.net.URI, info.rmapproject.core.model.RMapStatus)
	 */
	public List<URI> getResourceRelatedAll (URI uri, RMapStatus statusCode)
			throws RMapException, RMapDefectiveArgumentException {
		return getResourceRelatedAll(uri, statusCode, null, null, null);
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getRelatedAll(URI, RMapStatus, List<URI>, Date, Date)
	 */
	public List<URI> getResourceRelatedAll (URI uri, RMapStatus statusCode, List<URI> systemAgents, Date dateFrom, Date dateTo)
			throws RMapException, RMapDefectiveArgumentException {
		if (uri==null){
			throw new RMapDefectiveArgumentException("null uri");
		}
		List<URI>uris = new ArrayList<URI>();
		uris.addAll(this.getResourceRelatedDiSCOs(uri, statusCode, systemAgents, dateFrom, dateTo));
		uris.addAll(this.getResourceRelatedAgents(uri, statusCode, systemAgents, dateFrom, dateTo));
		uris.addAll(this.getResourceRelatedEvents(uri, systemAgents, dateFrom, dateTo));
		return uris;
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
		
		if (uri==null){
			throw new RMapDefectiveArgumentException("null uri");
		}
		org.openrdf.model.URI mUri = ORAdapter.uri2OpenRdfUri(uri);
		List<org.openrdf.model.URI> mSystemAgents = new ArrayList<org.openrdf.model.URI>();
		if (systemAgents != null) {
			for (URI sysAgent : systemAgents){
				mSystemAgents.add(ORAdapter.uri2OpenRdfUri(sysAgent));
			}
		}
		else {
			mSystemAgents = null;
		}
		Set<Statement> stmts = 
				this.resourcemgr.getRelatedTriples(mUri, statusCode, mSystemAgents, dateFrom, dateTo, ts);
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
		org.openrdf.model.URI mUri = ORAdapter.uri2OpenRdfUri(uri);
		List<org.openrdf.model.URI> mSystemAgents = new ArrayList<org.openrdf.model.URI>();
		if (systemAgents != null) {
			for (URI sysAgent : systemAgents){
				mSystemAgents.add(ORAdapter.uri2OpenRdfUri(sysAgent));
			}
		}
		else {
			mSystemAgents = null;
		}
		Set<org.openrdf.model.URI> orEvents =
				this.resourcemgr.getRelatedEvents(mUri, mSystemAgents, dateFrom, dateTo, ts);
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
		org.openrdf.model.URI mUri = ORAdapter.uri2OpenRdfUri(uri);
		List<org.openrdf.model.URI> mSystemAgents = new ArrayList<org.openrdf.model.URI>();
		if (systemAgents != null) {
			for (URI sysAgent : systemAgents){
				mSystemAgents.add(ORAdapter.uri2OpenRdfUri(sysAgent));
			}
		}
		else {
			mSystemAgents = null;
		}
		Set<org.openrdf.model.URI> orDiscos = 
				this.resourcemgr.getRelatedDiSCOS(mUri, statusCode, mSystemAgents, dateFrom, dateTo, ts);
		List<URI> uris = new ArrayList<URI>();
		for (org.openrdf.model.URI disco:orDiscos){
			URI dUri = ORAdapter.openRdfUri2URI(disco);
			uris.add(dUri);
		}
		return uris;		
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getResourceRelatedAgents (URI, RMapStatus)
	 */
	public List<URI> getResourceRelatedAgents (URI uri, RMapStatus statusCode) throws RMapException, RMapDefectiveArgumentException{
		return getResourceRelatedAgents (uri, statusCode, null, null, null);
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getResourceRelatedAgents (URI, RMapStatus, List<URI>, Date, Date)
	 */
	public List<URI> getResourceRelatedAgents (URI uri, RMapStatus statusCode, List<URI> systemAgents, Date dateFrom, Date dateTo)
			throws RMapException, RMapDefectiveArgumentException {
		if (uri==null){
			throw new RMapDefectiveArgumentException("null uri");
		}
		org.openrdf.model.URI resource = ORAdapter.uri2OpenRdfUri(uri);
		List<org.openrdf.model.URI> mSystemAgents = new ArrayList<org.openrdf.model.URI>();
		if (systemAgents != null) {
			for (URI sysAgent : systemAgents){
				mSystemAgents.add(ORAdapter.uri2OpenRdfUri(sysAgent));
			}
		}
		else {
			mSystemAgents = null;
		}
		Set<org.openrdf.model.URI> resourceAgents = 
				this.resourcemgr.getRelatedAgents(resource, statusCode, mSystemAgents, dateFrom, dateTo, ts);
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
		org.openrdf.model.URI rUri = ORAdapter.uri2OpenRdfUri(resourceUri);
		org.openrdf.model.URI cUri = ORAdapter.uri2OpenRdfUri(contextURI);
		Set<URI> returnSet = null;
		Set<org.openrdf.model.URI> uris = resourcemgr.getResourceRdfTypes(rUri,cUri, ts);
		if (uris != null && uris.size()>0){
			returnSet = new HashSet<URI>();
			for (org.openrdf.model.URI uri:uris){
				returnSet.add(ORAdapter.openRdfUri2URI(uri));
			}
		}
		return returnSet;
	}

	@Override
	public Map<URI, Set<URI>> getResourceRdfTypesAllContexts(URI resourceUri, RMapStatus statusCode)
			throws RMapException, RMapDefectiveArgumentException {
		if (resourceUri==null){
			throw new RMapDefectiveArgumentException("null resource URI");
		}
		org.openrdf.model.URI rUri = ORAdapter.uri2OpenRdfUri(resourceUri);
		Map<URI, Set<URI>> map = null;
		Map<org.openrdf.model.URI, Set<org.openrdf.model.URI>> typesMap = 
				resourcemgr.getResourceRdfTypesAllContexts(rUri, ts);
		if (typesMap != null && typesMap.keySet().size()>0){
			map = new HashMap<URI, Set<URI>>();
			for (org.openrdf.model.URI uri : typesMap.keySet()){
				Set<org.openrdf.model.URI> types = typesMap.get(uri);
				URI key = ORAdapter.openRdfUri2URI(uri);
				Set<URI> values = new HashSet<URI>();
				for (org.openrdf.model.URI type:types){
					URI typeURI = ORAdapter.openRdfUri2URI(type);
					values.add(typeURI);
				}
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
		org.openrdf.model.URI orSubject = ORAdapter.uri2OpenRdfUri(subject);
		org.openrdf.model.URI orPredicate = ORAdapter.uri2OpenRdfUri(predicate);
		org.openrdf.model.Value orObject = ORAdapter.rMapValue2OpenRdfValue(object);

		List<org.openrdf.model.URI> mSystemAgents = new ArrayList<org.openrdf.model.URI>();
		if (systemAgents != null) {
			for (URI sysAgent : systemAgents){
				mSystemAgents.add(ORAdapter.uri2OpenRdfUri(sysAgent));
			}
		}
		else {
			mSystemAgents = null;
		}
		
		List <org.openrdf.model.URI> relatedDiSCOs = 
				this.stmtmgr.getRelatedDiSCOs(orSubject, orPredicate, orObject, statusCode, 
						 						mSystemAgents, dateFrom, dateTo, ts);
		List<URI> returnSet = null;
		if (relatedDiSCOs != null && relatedDiSCOs.size()>0){
			returnSet = new ArrayList<URI>();
			for (org.openrdf.model.URI uri:relatedDiSCOs){
				returnSet.add(ORAdapter.openRdfUri2URI(uri));
			}
		}		
		return returnSet;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getStmtRelatedAgents(java.net.URI, java.net.URI, RMapValue, RMapStatus)
	 */
	public List<URI> getStatementRelatedAgents(java.net.URI subject, java.net.URI predicate, RMapValue object, 
						RMapStatus statusCode) 
						throws RMapException, RMapDefectiveArgumentException {
		return getStatementRelatedAgents(subject, predicate, object, statusCode);
	}
	
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getStmtRelatedAgents(java.net.URI, java.net.URI, RMapValue, RMapStatus, List<URI>, Date, Date)
	 */
	public List<URI> getStatementRelatedAgents(java.net.URI subject, java.net.URI predicate, RMapValue object, 
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
		org.openrdf.model.URI orSubject = ORAdapter.uri2OpenRdfUri(subject);
		org.openrdf.model.URI orPredicate = ORAdapter.uri2OpenRdfUri(predicate);
		org.openrdf.model.Value orObject = ORAdapter.rMapValue2OpenRdfValue(object);

		List<org.openrdf.model.URI> mSystemAgents = new ArrayList<org.openrdf.model.URI>();
		if (systemAgents != null) {
			for (URI sysAgent : systemAgents){
				mSystemAgents.add(ORAdapter.uri2OpenRdfUri(sysAgent));
			}
		}
		else {
			mSystemAgents = null;
		}
		
		List <org.openrdf.model.URI> relatedAgents 
				= this.stmtmgr.getRelatedAgents(orSubject, orPredicate, orObject, statusCode, mSystemAgents, dateFrom, dateTo, ts);
		List<URI> returnSet = null;
		if (relatedAgents != null && relatedAgents.size()>0){
			returnSet = new ArrayList<URI>();
			for (org.openrdf.model.URI uri:relatedAgents){
				returnSet.add(ORAdapter.openRdfUri2URI(uri));
			}
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
		org.openrdf.model.URI orSubject = ORAdapter.uri2OpenRdfUri(subject);
		org.openrdf.model.URI orPredicate = ORAdapter.uri2OpenRdfUri(predicate);
		org.openrdf.model.Value orObject = ORAdapter.rMapValue2OpenRdfValue(object);
		
		Set <org.openrdf.model.URI> assertingAgents = 
				this.stmtmgr.getAssertingAgents(orSubject, orPredicate, orObject, statusCode, dateFrom, dateTo, ts);
		
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
		ORMapDiSCODTO dto = this.discomgr.readDiSCO(ORAdapter.uri2OpenRdfUri(discoID), 
				false, null, null, this.eventmgr, ts);
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
		ORMapDiSCODTO dto = this.discomgr.readDiSCO(ORAdapter.uri2OpenRdfUri(discoID), 
				true, null, null, this.eventmgr, ts);
		return dto;
	}

	@Override
	public RMapEvent createDiSCO(RMapUri systemAgent, RMapDiSCO disco)
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
		RMapEvent createEvent = 
				this.discomgr.createDiSCO(ORAdapter.rMapUri2OpenRdfUri(systemAgent),
				(ORMapDiSCO)disco, this.eventmgr, ts);
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
		ORMapDiSCO disco = new ORMapDiSCO(stmts);
		RMapEvent createEvent = 
				this.discomgr.createDiSCO(ORAdapter.uri2OpenRdfUri(systemAgent),
				disco, this.eventmgr, ts);
		return createEvent;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getDiSCOStatus(java.net.URI)
	 */
	public RMapStatus getDiSCOStatus(URI discoId) throws RMapException, RMapDefectiveArgumentException {
		if (discoId ==null){
			throw new RMapDefectiveArgumentException("Null DiSCO id provided");
		}
		RMapStatus status = null;
		status = this.discomgr.getDiSCOStatus(ORAdapter.uri2OpenRdfUri(discoId), ts);
		return status;
	}

	@Override
	public RMapEvent updateDiSCO(RMapUri systemAgent, URI oldDiscoId, RMapDiSCO disco)
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
		org.openrdf.model.URI agentUri = ORAdapter.rMapUri2OpenRdfUri(systemAgent);
		RMapEvent updateEvent = 
				this.discomgr.updateDiSCO(agentUri,
					false, ORAdapter.uri2OpenRdfUri(oldDiscoId), (ORMapDiSCO)disco, 
					this.eventmgr, ts);
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
		ORMapDiSCO disco = new ORMapDiSCO(stmts);
		RMapEvent updateEvent = 
				this.discomgr.updateDiSCO(ORAdapter.uri2OpenRdfUri(systemAgent),
					false, ORAdapter.uri2OpenRdfUri(oldDiscoId), disco, 
					this.eventmgr, ts);
		return updateEvent;
	}

	@Override
	public RMapEvent inactivateDiSCO(RMapUri systemAgent, URI oldDiscoId)
			throws RMapException, RMapDiSCONotFoundException,
			RMapDefectiveArgumentException {
		if (systemAgent==null){
			throw new RMapDefectiveArgumentException ("null system agent");
		}
		if (oldDiscoId==null){
			throw new RMapDefectiveArgumentException ("null id for old DiSCO");
		}
		org.openrdf.model.URI agentUri = ORAdapter.rMapUri2OpenRdfUri(systemAgent);
		RMapEvent inactivateEvent = 
				this.discomgr.updateDiSCO(agentUri,
					true, ORAdapter.uri2OpenRdfUri(oldDiscoId), null, 
					this.eventmgr, ts);
		return inactivateEvent;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#deleteDiSCO(java.net.URI)
	 */
	public RMapEvent deleteDiSCO(URI discoID, RMapUri systemAgent) 
	throws RMapException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		if (systemAgent==null){
			throw new RMapDefectiveArgumentException ("null system agent");
		}
		RMapEvent tombstoneEvent = 
				this.discomgr.tombstoneDiSCO(ORAdapter.rMapUri2OpenRdfUri(systemAgent),
						ORAdapter.uri2OpenRdfUri(discoID), this.eventmgr, ts);
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
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(ORAdapter.uri2OpenRdfUri(discoID),
						false,this.eventmgr, ts);
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
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(ORAdapter.uri2OpenRdfUri(discoID), 
						true,this.eventmgr, ts);
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
		org.openrdf.model.URI dUri  = ORAdapter.uri2OpenRdfUri(discoID);
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(dUri,
						true,this.eventmgr,ts);
		org.openrdf.model.URI latestDiscoURi = this.discomgr.getLatestDiSCOUri(dUri,
				eventmgr, ts, event2disco);
		RMapDiSCODTO latestDisco = null;		
		if (latestDiscoURi != null){
			latestDisco = this.discomgr.readDiSCO(latestDiscoURi, false, event2disco, null, this.eventmgr, ts);
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
		org.openrdf.model.URI dUri  = ORAdapter.uri2OpenRdfUri(discoID);
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(dUri,
						true,this.eventmgr,ts);
		org.openrdf.model.URI latestDiscoURi = this.discomgr.getLatestDiSCOUri(dUri,
				eventmgr, ts, event2disco);
		RMapDiSCODTO latestDisco = null;		
		if (latestDiscoURi != null){
			latestDisco = this.discomgr.readDiSCO(latestDiscoURi, true, event2disco, null, this.eventmgr, ts);
		}
		return latestDisco;
	}
	
	@Override
	public URI getDiSCOIdLatestVersion(URI discoID) throws RMapException,
			RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		org.openrdf.model.URI dUri  = ORAdapter.uri2OpenRdfUri(discoID);
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(dUri,
						true,this.eventmgr,ts);
		org.openrdf.model.URI latestDisco = this.discomgr.getLatestDiSCOUri(dUri,
				eventmgr, ts, event2disco);
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
		org.openrdf.model.URI thisUri = ORAdapter.uri2OpenRdfUri(discoID);
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(thisUri,
						true,this.eventmgr,ts);
		Map <Date, org.openrdf.model.URI> date2event = 
				this.eventmgr.getDate2EventMap(event2disco.keySet(),ts);			
		org.openrdf.model.URI prevDisco = this.discomgr.getPreviousURI(thisUri, event2disco, date2event, eventmgr, ts);		
		RMapDiSCODTO dto  = this.discomgr.readDiSCO(prevDisco, false, event2disco, date2event, this.eventmgr, ts);
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
		org.openrdf.model.URI thisUri = ORAdapter.uri2OpenRdfUri(discoID);
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(thisUri,
						true,this.eventmgr,ts);
		Map <Date, org.openrdf.model.URI> date2event = 
				this.eventmgr.getDate2EventMap(event2disco.keySet(),ts);			
		org.openrdf.model.URI prevDisco = this.discomgr.getPreviousURI(thisUri, event2disco, date2event, eventmgr, ts);		
		RMapDiSCODTO dto  = this.discomgr.readDiSCO(prevDisco, true, event2disco, date2event, this.eventmgr, ts);		
		return dto;
	}

	@Override
	public URI getDiSCOIdPreviousVersion(URI discoID)
			throws RMapException, RMapObjectNotFoundException,
			RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		org.openrdf.model.URI thisUri = ORAdapter.uri2OpenRdfUri(discoID);
		URI nextDiscoUri = null;		
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(thisUri,
						true,this.eventmgr,ts);
		Map <Date, org.openrdf.model.URI> date2event = 
				this.eventmgr.getDate2EventMap(event2disco.keySet(),ts);	
		org.openrdf.model.URI prevDisco = this.discomgr.getPreviousURI(thisUri, event2disco, date2event, eventmgr, ts);
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
		org.openrdf.model.URI thisUri = ORAdapter.uri2OpenRdfUri(discoID);		
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(thisUri,true,this.eventmgr,ts);
		Map <Date, org.openrdf.model.URI> date2event = 
				this.eventmgr.getDate2EventMap(event2disco.keySet(),ts);
		org.openrdf.model.URI nextDiscoId = this.discomgr.getNextURI(thisUri, event2disco, date2event, this.eventmgr, ts);
		RMapDiSCODTO dto = this.discomgr.readDiSCO(nextDiscoId, false, event2disco, date2event, this.eventmgr, ts);	
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
		org.openrdf.model.URI thisUri = ORAdapter.uri2OpenRdfUri(discoID);
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(thisUri,true,this.eventmgr,ts);
		Map <Date, org.openrdf.model.URI> date2event = 
				this.eventmgr.getDate2EventMap(event2disco.keySet(),ts);
		org.openrdf.model.URI nextDiscoId = this.discomgr.getNextURI(thisUri, event2disco, date2event, this.eventmgr, ts);
		RMapDiSCODTO dto = this.discomgr.readDiSCO(nextDiscoId, true, event2disco, date2event, this.eventmgr, ts);		
		return dto;
	}
	
	@Override
	public URI getDiSCOIdNextVersion(URI discoID) throws RMapException,
			RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		org.openrdf.model.URI thisUri = ORAdapter.uri2OpenRdfUri(discoID);
		URI nextDiscoUri = null;
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(thisUri,true,this.eventmgr,ts);
		org.openrdf.model.URI uri = this.discomgr.getNextURI(thisUri, event2disco, null, this.eventmgr, ts);
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
		Set<org.openrdf.model.URI> events = 
				this.eventmgr.getDiscoRelatedEventIds(ORAdapter.uri2OpenRdfUri(discoID), ts);
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
		return this.eventmgr.readEvent(ORAdapter.uri2OpenRdfUri(eventId), ts);
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getEventRelatedResources(java.net.URI)
	 */
	public List<URI> getEventRelatedResources(URI eventID) throws RMapException, RMapDefectiveArgumentException {
		if (eventID ==null){
			throw new RMapDefectiveArgumentException ("null event id");
		}
		List<org.openrdf.model.URI> resources = this.eventmgr.getRelatedResources(
				ORAdapter.uri2OpenRdfUri(eventID),this.discomgr, ts);
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
		List<org.openrdf.model.URI> discos = this.eventmgr.getRelatedDiSCOs(
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
		List<org.openrdf.model.URI> agents = this.eventmgr.getRelatedAgents(
				ORAdapter.uri2OpenRdfUri(eventID), ts);
		List<URI> agentIds = new ArrayList<URI>();
		for (org.openrdf.model.URI agent:agents){
			agentIds.add(ORAdapter.openRdfUri2URI(agent));
		}
		return agentIds;
	}

	public RMapAgent readAgent(URI agentId) 
	throws RMapException, RMapAgentNotFoundException, RMapDefectiveArgumentException {
		if (agentId==null){
			throw new RMapDefectiveArgumentException("Null agentid");
		}
		ORMapAgent agent = agentmgr.readAgent(ORAdapter.uri2OpenRdfUri(agentId), ts);
		return agent;
	}
	
	@Override
	public RMapEvent createAgent(URI systemAgent, RMapAgent agent)
			throws RMapException, RMapDefectiveArgumentException {
		if (agent==null){
			throw new RMapDefectiveArgumentException("null agent");
		}
		if (!(agent instanceof ORMapAgent)){
			throw new RMapDefectiveArgumentException("unrecognized type for agent");
		}
		if (systemAgent==null){
			throw new RMapDefectiveArgumentException("null system agent");
		}
		org.openrdf.model.URI systemAgentUri = ORAdapter.uri2OpenRdfUri(systemAgent);
		ORMapAgent orAgent = (ORMapAgent)agent;
		RMapEvent event = this.agentmgr.createAgent(orAgent, systemAgentUri, this.eventmgr, ts);
		return event;
	}

	@Override
	public RMapEvent deleteAgent(URI systemAgentId, URI targetAgentID)
			throws RMapException, RMapAgentNotFoundException,
			RMapDefectiveArgumentException {
		if (systemAgentId==null){
			throw new RMapDefectiveArgumentException ("null system agent id");
		}
		if (targetAgentID==null){
			throw new RMapDefectiveArgumentException ("null target agent id");
		}
		org.openrdf.model.URI systemAgentUri = ORAdapter.uri2OpenRdfUri(systemAgentId);
		org.openrdf.model.URI targetAgentUri = ORAdapter.uri2OpenRdfUri(targetAgentID);
		RMapEvent event = this.agentmgr.tombstoneAgent(systemAgentUri, targetAgentUri, 
				this.eventmgr, ts);
		return event;
	}
	
	@Override
	public List<URI> getAgentEvents(URI agentId) throws RMapException,
			RMapDefectiveArgumentException, RMapAgentNotFoundException {
		org.openrdf.model.URI uri = ORAdapter.uri2OpenRdfUri(agentId);
		if (agentId==null){
			throw new RMapDefectiveArgumentException("null agentId");
		}
		List<org.openrdf.model.URI> events = this.eventmgr.getAgentRelatedEventIds(uri, ts);
		List<URI> eventUris = new ArrayList<URI>();
		for (org.openrdf.model.URI event:events){
			eventUris.add(ORAdapter.openRdfUri2URI(event));
		}
		return eventUris;
	}
	@Override
	public List<URI> getAgentRepresentations(URI agentURI, URI repURI)
			throws RMapException, RMapAgentNotFoundException,
			RMapDefectiveArgumentException {
		if (agentURI==null){
			throw new RMapDefectiveArgumentException ("null agent URI");
		}	
		if (repURI==null){
			throw new RMapDefectiveArgumentException ("null representation URI");
		}	
		Set<org.openrdf.model.URI> ids = this.agentmgr.getAgentRepresentations(
				ORAdapter.uri2OpenRdfUri(agentURI), ORAdapter.uri2OpenRdfUri(repURI), ts);
		List<URI> uris = new ArrayList<URI>();
		for (org.openrdf.model.URI idUri:ids){
			uris.add(ORAdapter.openRdfUri2URI(idUri));
		}
		return uris;
	}
	@Override
	public List<URI> getAgentRepresentationsAnyCreator(URI uri) throws RMapException,
			RMapDefectiveArgumentException {
		if (uri==null){
			throw new RMapDefectiveArgumentException ("null uri");
		}
		org.openrdf.model.URI id = ORAdapter.uri2OpenRdfUri(uri);
		Set<org.openrdf.model.URI> ids = this.agentmgr.getAgentRepresentations(id, ts);
		List<URI> uris = new ArrayList<URI>();
		for (org.openrdf.model.URI idUri:ids){
			uris.add(ORAdapter.openRdfUri2URI(idUri));
		}
		return uris;
	}
	@Override
	public RMapStatus getAgentStatus(URI agentId) throws RMapException,
			RMapDefectiveArgumentException, RMapAgentNotFoundException {
		if (agentId==null){
			throw new RMapDefectiveArgumentException ("null agentId");
		}
		org.openrdf.model.URI id = ORAdapter.uri2OpenRdfUri(agentId);
		RMapStatus status = this.agentmgr.getAgentStatus(id, ts);
		return status;
	}

	/**
	 * @return the resourcemgr
	 */
	public ORMapResourceMgr getResourcemgr() {
		return resourcemgr;
	}

	/**
	 * @return the discomgr
	 */
	public ORMapDiSCOMgr getDiscomgr() {
		return discomgr;
	}

	/**
	 * @return the eventmgr
	 */
	public ORMapEventMgr getEventmgr() {
		return eventmgr;
	}

	/**
	 * @return the agentmgr
	 */
	public ORMapAgentMgr getagentmgr() {
		return agentmgr;
	}


}

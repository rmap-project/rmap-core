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
import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapTriple;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.model.request.RMapRequestAgent;
import info.rmapproject.core.model.request.RMapSearchParams;
import info.rmapproject.core.rmapservice.RMapDiSCODTO;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.IRI;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *  @author khanson, smorrissey
 *
 */
public class ORMapService implements RMapService {
	
	private ORMapResourceMgr resourcemgr;
	private ORMapDiSCOMgr discomgr;
	private ORMapAgentMgr agentmgr;
	private ORMapStatementMgr statementmgr;
	private ORMapEventMgr eventmgr;
	private ORAdapter typeAdapter;
	private SesameTriplestore triplestore;
	
	@Autowired
	public ORMapService(ORMapResourceMgr resourcemgr,
						ORMapDiSCOMgr discomgr,
						ORMapAgentMgr agentmgr,
						ORMapStatementMgr statementmgr,
						ORMapEventMgr eventmgr,
						SesameTriplestore triplestore) {
		this.resourcemgr = resourcemgr;
		this.discomgr = discomgr;
		this.agentmgr = agentmgr;
		this.statementmgr = statementmgr;
		this.eventmgr = eventmgr;
		this.triplestore = triplestore;
		this.typeAdapter = new ORAdapter(triplestore);
	}
	

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#closeConnection()
	 */
	@Override
	public void closeConnection() throws RMapException {
		try {
            triplestore.closeConnection();
		}
		catch(Exception e)  {
            throw new RMapException("Could not close connection");
		}
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getResourceRelatedTriples(URI, RMapStatus, List<URI>, Date, Date)
	 */
	@Override
	public List<RMapTriple> getResourceRelatedTriples(URI uri, RMapSearchParams params)
			throws RMapException, RMapDefectiveArgumentException {
		if (uri==null){
			throw new RMapDefectiveArgumentException("null uri");
		}
		if (params==null){
			params = new RMapSearchParams();
		}
		org.openrdf.model.IRI mIri = typeAdapter.uri2OpenRdfIri(uri);
		
		Set<Statement> stmts = resourcemgr.getRelatedTriples(mIri, params, triplestore); //, limit, offset
		
		List<RMapTriple> triples = new ArrayList<RMapTriple>();
		for (Statement stmt:stmts){
			RMapTriple triple = typeAdapter.openRdfStatement2RMapTriple(stmt);
			triples.add(triple);
		}
		return triples;
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getResourceRelatedEvents(java.net.URI, List<URI>, Date, Date)
	 */
	@Override
	public List<URI> getResourceRelatedEvents (URI uri, RMapSearchParams params) throws RMapException, RMapDefectiveArgumentException {
		if (uri==null){
			throw new RMapDefectiveArgumentException("null uri");
		}
		if (params==null){
			params = new RMapSearchParams();
		}
		
		org.openrdf.model.IRI mUri = typeAdapter.uri2OpenRdfIri(uri);
		
		Set<org.openrdf.model.IRI> orEvents = resourcemgr.getResourceRelatedEvents(mUri, params, triplestore); //, limit, offset
		List<URI> uris = new ArrayList<URI>();
		for (org.openrdf.model.IRI event:orEvents){
			URI dUri = typeAdapter.openRdfIri2URI(event);
			uris.add(dUri);
		}
		return uris;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getRelatedDiSCOs(java.net.URI, info.rmapproject.core.model.RMapStatus, List<URI>, Date, Date)
	 */
	@Override
	public List<URI> getResourceRelatedDiSCOs (URI uri, RMapSearchParams params)
			throws RMapException, RMapDefectiveArgumentException {
		if (uri==null){
			throw new RMapDefectiveArgumentException("null uri");
		}
		if (params==null){
			params = new RMapSearchParams();
		}
		
		org.openrdf.model.IRI mUri = typeAdapter.uri2OpenRdfIri(uri);	

		Set<org.openrdf.model.IRI> orDiscos = resourcemgr.getResourceRelatedDiSCOS(mUri, params, triplestore);//, limit, offset
		List<URI> uris = new ArrayList<URI>();
		for (org.openrdf.model.IRI disco:orDiscos){
			URI dUri = typeAdapter.openRdfIri2URI(disco);
			uris.add(dUri);
		}
		return uris;		
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getResourceAssertingAgents (URI, RMapStatus, List<URI>, Date, Date)
	 */
	@Override
	public List<URI> getResourceAssertingAgents (URI uri, RMapSearchParams params)
			throws RMapException, RMapDefectiveArgumentException {
		if (uri==null){
			throw new RMapDefectiveArgumentException("null uri");
		}
		if (params==null){
			params = new RMapSearchParams();
		}
		org.openrdf.model.IRI resource = typeAdapter.uri2OpenRdfIri(uri);
		
		Set<org.openrdf.model.IRI> resourceAgents = resourcemgr.getResourceAssertingAgents(resource, params, triplestore);
		List<URI> uris = new ArrayList<URI>();
		for (org.openrdf.model.IRI agent:resourceAgents){
			URI dUri = typeAdapter.openRdfIri2URI(agent);
			uris.add(dUri);
		}
		
		return uris;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getResourceRdfTypes(java.net.URI, java.net.URI)
	 */
	@Override
	public Set<URI> getResourceRdfTypesInDiSCO(URI resourceUri, URI discoUri)
			throws RMapException, RMapDefectiveArgumentException {
		if (resourceUri==null){
			throw new RMapDefectiveArgumentException("null resource URI");
		}
		if (discoUri==null){
			throw new RMapDefectiveArgumentException("null context URI");
		}
		org.openrdf.model.IRI rUri = typeAdapter.uri2OpenRdfIri(resourceUri);
		org.openrdf.model.IRI cUri = typeAdapter.uri2OpenRdfIri(discoUri);
		Set<URI> returnSet = null;
		Set<org.openrdf.model.IRI> uris = resourcemgr.getResourceRdfTypes(rUri,cUri, triplestore);
		if (uris != null && uris.size()>0){
			returnSet = typeAdapter.openRdfIriSet2UriSet(uris);
		}
		return returnSet;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getResourceRdfTypesAllContexts(java.net.URI, RMapStatus)
	 */
	@Override
	public Map<URI, Set<URI>> getResourceRdfTypesAllContexts(URI resourceUri, RMapSearchParams params)
			throws RMapException, RMapDefectiveArgumentException {
		if (resourceUri==null){
			throw new RMapDefectiveArgumentException("null resource URI");
		}
		if (params==null){
			params = new RMapSearchParams();
		}
		IRI rUri = typeAdapter.uri2OpenRdfIri(resourceUri);
		Map<URI, Set<URI>> map = null;
		Map<IRI, Set<IRI>> typesMap = resourcemgr.getResourceRdfTypesAllContexts(rUri, params, triplestore);
		if (typesMap != null && typesMap.keySet().size()>0){
			map = new HashMap<URI, Set<URI>>();
			for (IRI uri : typesMap.keySet()){
				Set<IRI> types = typesMap.get(uri);
				URI key = typeAdapter.openRdfIri2URI(uri);
				Set<URI> values = typeAdapter.openRdfIriSet2UriSet(types);
				map.put(key, values);
			}				
		}
		return map;
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getStmtRelatedDiSCOs(java.net.URI, java.net.URI, RMapValue, RMapStatus, List<java.net.URI>, Date, Date)
	 */
	@Override
	public List<URI> getStatementRelatedDiSCOs(URI subject, URI predicate, RMapValue object, RMapSearchParams params) 
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
		if (params==null){
			params = new RMapSearchParams();
		}
		IRI orSubject = typeAdapter.uri2OpenRdfIri(subject);
		IRI orPredicate = typeAdapter.uri2OpenRdfIri(predicate);
		Value orObject = typeAdapter.rMapValue2OpenRdfValue(object);
		
		List <IRI> relatedDiSCOs = 
				statementmgr.getRelatedDiSCOs(orSubject, orPredicate, orObject, params, triplestore);
				
		List<URI> returnSet = null;
		if (relatedDiSCOs != null && relatedDiSCOs.size()>0){
			returnSet = typeAdapter.openRdfUriList2UriList(relatedDiSCOs);
		}		
		return returnSet;
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getStmtAssertingAgents(java.net.URI, java.net.URI, RMapValue, RMapStatus, Date, Date)
	 */
	@Override
	public List<URI> getStatementAssertingAgents(java.net.URI subject, java.net.URI predicate, RMapValue object, RMapSearchParams params) 
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
		if (params==null){
			params = new RMapSearchParams();
		}
		IRI orSubject = typeAdapter.uri2OpenRdfIri(subject);
		IRI orPredicate = typeAdapter.uri2OpenRdfIri(predicate);
		Value orObject = typeAdapter.rMapValue2OpenRdfValue(object);
		
		Set <IRI> assertingAgents = 
				statementmgr.getAssertingAgents(orSubject, orPredicate, orObject, params, triplestore);
		
		List<URI> returnSet = null;
		if (assertingAgents != null && assertingAgents.size()>0){
			returnSet = new ArrayList<URI>();
			for (IRI uri:assertingAgents){
				returnSet.add(typeAdapter.openRdfIri2URI(uri));
			}
		}		
		return returnSet;
	}
	

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#readDiSCO(java.net.URI)
	 */
	@Override
	public RMapDiSCO readDiSCO(URI discoID) 
	throws RMapException, RMapDiSCONotFoundException, RMapDefectiveArgumentException {
		if (discoID == null){
			throw new RMapDefectiveArgumentException("Null DiSCO id provided");
		}
		ORMapDiSCODTO dto = discomgr.readDiSCO(typeAdapter.uri2OpenRdfIri(discoID), false, null, null, triplestore);
		return dto.getRMapDiSCO();
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#readDiSCODTO(java.net.URI)
	 */
	@Override
	public RMapDiSCODTO readDiSCODTO (URI discoID)
	throws RMapException, RMapDiSCONotFoundException, RMapDefectiveArgumentException {
		if (discoID == null){
			throw new RMapDefectiveArgumentException("Null DiSCO id provided");
		}
		ORMapDiSCODTO dto = discomgr.readDiSCO(typeAdapter.uri2OpenRdfIri(discoID), true, null, null, triplestore);
		return dto;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#createDiSCO(java.net.URI, RMapDiSCO)
	 */
	@Override
	public RMapEvent createDiSCO(RMapDiSCO disco, RMapRequestAgent requestAgent)
			throws RMapException, RMapDefectiveArgumentException {
		if (disco==null){
			throw new RMapDefectiveArgumentException("Null DiSCO provided");
		}
		if (requestAgent==null){
			throw new RMapDefectiveArgumentException("Null Agent id provided");
		}
		if (!(disco instanceof ORMapDiSCO)){
			throw new RMapDefectiveArgumentException("disco not instance of ORMapDiSCO");
		}
		RMapEvent createEvent = discomgr.createDiSCO((ORMapDiSCO)disco, requestAgent, triplestore);
		return createEvent;
	}


	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getDiSCOStatus(java.net.URI)
	 */
	@Override
	public RMapStatus getDiSCOStatus(URI discoId) throws RMapException, RMapDefectiveArgumentException {
		if (discoId ==null){
			throw new RMapDefectiveArgumentException("Null DiSCO id provided");
		}
		RMapStatus status = discomgr.getDiSCOStatus(typeAdapter.uri2OpenRdfIri(discoId), triplestore);
		return status;
	}

	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#updateDiSCO(java.net.URI, java.net.URI, RMapDiSCO, java.net.URI)
	 */
	@Override
	public RMapEvent updateDiSCO(URI oldDiscoId, RMapDiSCO disco, RMapRequestAgent requestAgent)
			throws RMapException, RMapDefectiveArgumentException {
		if (requestAgent==null){
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
			updateEvent = discomgr.updateDiSCO(
										typeAdapter.uri2OpenRdfIri(oldDiscoId), 
										(ORMapDiSCO)disco, 
										requestAgent,
										false, 
										triplestore);
		} catch (RMapException | RMapDefectiveArgumentException ex) {
			try {
				//there has been an error during an update so try to rollback the transaction
				triplestore.rollbackTransaction();
			} catch(RepositoryException rollbackException) {
				throw new RMapException("Could not rollback changes after error. Please check your DiSCO record for errors.", ex);
			}
			throw ex;	
		}	
		
		return updateEvent;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#inactivateDiSCO(java.net.URI, java.net.URI)
	 */
	@Override
	public RMapEvent inactivateDiSCO(URI oldDiscoId, RMapRequestAgent requestAgent)
			throws RMapException, RMapDiSCONotFoundException,
			RMapDefectiveArgumentException {
		if (oldDiscoId==null){
			throw new RMapDefectiveArgumentException ("null id for old DiSCO");
		}
		if (requestAgent==null){
			throw new RMapDefectiveArgumentException ("null system agent");
		}
		RMapEvent inactivateEvent = null;
		try {
			inactivateEvent = discomgr.updateDiSCO(typeAdapter.uri2OpenRdfIri(oldDiscoId), null, requestAgent, true, triplestore);
		} catch (RMapException | RMapDefectiveArgumentException ex) {
			try {
				//there has been an error during an update so try to rollback the transaction
				triplestore.rollbackTransaction();
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
	@Override
	public RMapEvent deleteDiSCO(URI discoID, RMapRequestAgent requestAgent) 
			throws RMapException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		if (requestAgent==null){
			throw new RMapDefectiveArgumentException ("null system agent");
		}
		RMapEvent tombstoneEvent = null;
		try {
			tombstoneEvent = discomgr.tombstoneDiSCO(typeAdapter.uri2OpenRdfIri(discoID), requestAgent, triplestore);
		} catch (RMapException ex) {
			try {
				//there has been an error during an update so try to rollback the transaction
				triplestore.rollbackTransaction();
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
	@Override
	public List<URI> getDiSCOAllVersions(URI discoID) 
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		Map<IRI,IRI>event2disco=
				discomgr.getAllDiSCOVersions(typeAdapter.uri2OpenRdfIri(discoID),
						false,triplestore);
		List<IRI> versions = new ArrayList<IRI>();
		versions.addAll(event2disco.values());
		List<URI> uris = new ArrayList<URI>();
		for (IRI version:versions){
			uris.add(typeAdapter.openRdfIri2URI(version));
		}
		return uris;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getAllAgentDiSCOVersions(java.net.URI)
	 */
	@Override
	public List<URI> getDiSCOAllAgentVersions(URI discoID) 
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		Map<IRI,IRI>event2disco=
				discomgr.getAllDiSCOVersions(typeAdapter.uri2OpenRdfIri(discoID), true, triplestore);
		List<IRI> versions = new ArrayList<IRI>();
		versions.addAll(event2disco.values());		
		List<URI> uris = new ArrayList<URI>();
		for (IRI version:versions){
			uris.add(typeAdapter.openRdfIri2URI(version));
		}
		return uris;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getLatestVersionDiSCO(java.net.URI)
	 */
	@Override
	public RMapDiSCO getDiSCOLatestVersion(URI discoID) 
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {			
		if (discoID == null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		IRI dUri  = typeAdapter.uri2OpenRdfIri(discoID);
		Map<IRI,IRI>event2disco=
				discomgr.getAllDiSCOVersions(dUri, true, triplestore);
		IRI latestDiscoURi = discomgr.getLatestDiSCOIri(dUri, triplestore, event2disco);
		RMapDiSCODTO latestDisco = null;		
		if (latestDiscoURi != null){
			latestDisco = discomgr.readDiSCO(latestDiscoURi, false, event2disco, null, triplestore);
		}
		RMapDiSCO disco = null;
		if (latestDisco != null){
			disco = latestDisco.getRMapDiSCO();
		}
		return disco;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getDiSCODTOLatestVersion(java.net.URI)
	 */
	@Override
	public RMapDiSCODTO getDiSCODTOLatestVersion(URI discoID)
			throws RMapException, RMapObjectNotFoundException,
			RMapDefectiveArgumentException {
		if (discoID == null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		IRI dUri  = typeAdapter.uri2OpenRdfIri(discoID);
		Map<IRI,IRI>event2disco=
				discomgr.getAllDiSCOVersions(dUri, true, triplestore);
		IRI latestDiscoURi = discomgr.getLatestDiSCOIri(dUri, triplestore, event2disco);
		RMapDiSCODTO latestDisco = null;		
		if (latestDiscoURi != null){
			latestDisco = discomgr.readDiSCO(latestDiscoURi, true, event2disco, null, triplestore);
		}
		return latestDisco;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getDiSCOIdLatestVersion(java.net.URI)
	 */
	@Override
	public URI getDiSCOIdLatestVersion(URI discoID) throws RMapException,
			RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		IRI dUri  = typeAdapter.uri2OpenRdfIri(discoID);
		Map<IRI,IRI>event2disco=
				discomgr.getAllDiSCOVersions(dUri, true, triplestore);
		IRI latestDisco = discomgr.getLatestDiSCOIri(dUri, triplestore, event2disco);
		URI discoURI = null;
		if (latestDisco != null){
			typeAdapter.openRdfIri2URI(latestDisco);	
		}
		return discoURI;
	}
	

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getPreviousVersionDiSCO(java.net.URI)
	 */
	@Override
	public RMapDiSCO getDiSCOPreviousVersion(URI discoID) 
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		RMapDiSCO nextDisco = null;
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		IRI thisUri = typeAdapter.uri2OpenRdfIri(discoID);
		Map<IRI,IRI>event2disco=
				discomgr.getAllDiSCOVersions(thisUri, true, triplestore);
		Map <Date, IRI> date2event = eventmgr.getDate2EventMap(event2disco.keySet(),triplestore);			
		IRI prevDisco = discomgr.getPreviousIRI(thisUri, event2disco, date2event, triplestore);		
		RMapDiSCODTO dto  = discomgr.readDiSCO(prevDisco, false, event2disco, date2event, triplestore);
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
		IRI thisUri = typeAdapter.uri2OpenRdfIri(discoID);
		Map<IRI,IRI>event2disco=
				discomgr.getAllDiSCOVersions(thisUri, true, triplestore);
		Map <Date, IRI> date2event = 
				eventmgr.getDate2EventMap(event2disco.keySet(),triplestore);			
		IRI prevDisco = discomgr.getPreviousIRI(thisUri, event2disco, date2event, triplestore);		
		RMapDiSCODTO dto  = discomgr.readDiSCO(prevDisco, true, event2disco, date2event, triplestore);		
		return dto;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getDiSCOIdPreviousVersion(java.net.URI)
	 */
	@Override
	public URI getDiSCOIdPreviousVersion(URI discoID)
			throws RMapException, RMapObjectNotFoundException,
			RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		IRI thisUri = typeAdapter.uri2OpenRdfIri(discoID);
		URI nextDiscoUri = null;		
		Map<IRI,IRI>event2disco= discomgr.getAllDiSCOVersions(thisUri, true, triplestore);
		Map <Date, IRI> date2event =  eventmgr.getDate2EventMap(event2disco.keySet(),triplestore);	
		IRI prevDisco = discomgr.getPreviousIRI(thisUri, event2disco, date2event, triplestore);
		if (prevDisco != null){
			nextDiscoUri = typeAdapter.openRdfIri2URI(prevDisco);
		}
		return nextDiscoUri;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getNextVersionDiSCO(java.net.URI)
	 */
	@Override
	public RMapDiSCO getDiSCONextVersion(URI discoID) 
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		IRI thisUri = typeAdapter.uri2OpenRdfIri(discoID);		
		Map<IRI,IRI>event2disco= discomgr.getAllDiSCOVersions(thisUri, true, triplestore);
		Map <Date, IRI> date2event = eventmgr.getDate2EventMap(event2disco.keySet(),triplestore);
		IRI nextDiscoId = discomgr.getNextIRI(thisUri, event2disco, date2event, triplestore);
		RMapDiSCODTO dto = discomgr.readDiSCO(nextDiscoId, false, event2disco, date2event, triplestore);	
		RMapDiSCO nextDisco = null;
		if (dto != null){
			nextDisco = dto.getRMapDiSCO();
		}
		return nextDisco;
	}
	

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getDiSCODTONextVersion(java.net.URI)
	 */
	@Override
	public RMapDiSCODTO getDiSCODTONextVersion(URI discoID)
			throws RMapException, RMapObjectNotFoundException,
			RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		IRI thisUri = typeAdapter.uri2OpenRdfIri(discoID);
		Map<IRI,IRI>event2disco=
				discomgr.getAllDiSCOVersions(thisUri,true, triplestore);
		Map <Date, IRI> date2event = 
				eventmgr.getDate2EventMap(event2disco.keySet(),triplestore);
		IRI nextDiscoId = discomgr.getNextIRI(thisUri, event2disco, date2event, triplestore);
		RMapDiSCODTO dto = discomgr.readDiSCO(nextDiscoId, true, event2disco, date2event, triplestore);		
		return dto;
	}
	

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getDiSCOIdNextVersion(java.net.URI)
	 */
	@Override
	public URI getDiSCOIdNextVersion(URI discoID) throws RMapException,
			RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		IRI thisUri = typeAdapter.uri2OpenRdfIri(discoID);
		URI nextDiscoUri = null;
		Map<IRI,IRI>event2disco=
				discomgr.getAllDiSCOVersions(thisUri,true, triplestore);
		IRI uri = discomgr.getNextIRI(thisUri, event2disco, null, triplestore);
		if (uri != null){
			nextDiscoUri = typeAdapter.openRdfIri2URI(uri);
		}
		return nextDiscoUri;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getDiSCOEvents(java.net.URI)
	 */
	@Override
	public List<URI> getDiSCOEvents(URI discoID) throws RMapException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		Set<IRI> events = eventmgr.getDiscoRelatedEventIds(typeAdapter.uri2OpenRdfIri(discoID), triplestore);
		List<URI> uris = new ArrayList<URI>();
		for (IRI event:events){
			uris.add(typeAdapter.openRdfIri2URI(event));
		}
		return uris;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#readEvent(java.net.URI)
	 */
	@Override
	public RMapEvent readEvent(URI eventId) 
	throws RMapException, RMapEventNotFoundException, RMapDefectiveArgumentException {
		if (eventId ==null){
			throw new RMapDefectiveArgumentException ("null event id");
		}
		return eventmgr.readEvent(typeAdapter.uri2OpenRdfIri(eventId), triplestore);
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getEventRelatedResources(java.net.URI)
	 */
	@Override
	public List<URI> getEventRelatedResources(URI eventID) throws RMapException, RMapDefectiveArgumentException {
		if (eventID ==null){
			throw new RMapDefectiveArgumentException ("null event id");
		}
		List<IRI> resources = eventmgr.getAffectedResources(typeAdapter.uri2OpenRdfIri(eventID), triplestore);
		List<URI> resourceIds = new ArrayList<URI>();
		for (IRI resource:resources){
			resourceIds.add(typeAdapter.openRdfIri2URI(resource));
		}
		return resourceIds;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getEventRelatedDiSCOS(java.net.URI)
	 */
	@Override
	public List<URI> getEventRelatedDiSCOS(URI eventID) throws RMapException, RMapDefectiveArgumentException {
		if (eventID ==null){
			throw new RMapDefectiveArgumentException ("null event id");
		}
		List<IRI> discos = eventmgr.getAffectedDiSCOs(
				typeAdapter.uri2OpenRdfIri(eventID), triplestore);
		List<URI> discoIds = new ArrayList<URI>();
			for (IRI disco:discos){
				discoIds.add(typeAdapter.openRdfIri2URI(disco));
			}
		return discoIds;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getEventRelatedAgents(java.net.URI)
	 */
	@Override
	public List<URI> getEventRelatedAgents(URI eventID) throws RMapException, RMapDefectiveArgumentException {
		if (eventID ==null){
			throw new RMapDefectiveArgumentException ("null event id");
		}
		List<IRI> agents = eventmgr.getAffectedAgents(typeAdapter.uri2OpenRdfIri(eventID), triplestore);
		List<URI> agentIds = typeAdapter.openRdfUriList2UriList(agents);

		return agentIds;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#readAgent(java.net.URI)
	 */
	@Override
	public RMapAgent readAgent(URI agentId) 
	throws RMapException, RMapAgentNotFoundException, RMapDefectiveArgumentException {
		if (agentId==null){
			throw new RMapDefectiveArgumentException("Null agentid");
		}
		ORMapAgent agent = agentmgr.readAgent(typeAdapter.uri2OpenRdfIri(agentId), triplestore);
		return agent;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#readAgent(RMapAgent, java.net.URI)
	 */
	@Override
	public RMapEvent createAgent(RMapAgent agent, RMapRequestAgent requestAgent) 
			throws RMapException, RMapDefectiveArgumentException {
		if (agent==null){
			throw new RMapDefectiveArgumentException("null agent");
		}
		if (!(agent instanceof ORMapAgent)){
			throw new RMapDefectiveArgumentException("unrecognized type for agent");
		}
		if (requestAgent==null){
			throw new RMapDefectiveArgumentException("null system agent");
		}
		RMapEvent event = null;
		try {
			ORMapAgent orAgent = (ORMapAgent)agent;
			event = agentmgr.createAgent(orAgent, requestAgent, triplestore);
		} catch (RMapException ex) {
			try {
				//there has been an error during an update so try to rollback the transaction
				triplestore.rollbackTransaction();
			} catch(RepositoryException rollbackException) {
				throw new RMapException("Could not rollback changes after error. Please check your Agent record for errors.", ex);
			}
			throw ex;	
		}	
		
		return event;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#createAgent(java.net.URI, String, java.net.URI, java.net.URI, java.net.URI)
	 */
	@Override
	public RMapEvent createAgent(URI agentID, String name, URI identityProvider, URI authKeyUri, RMapRequestAgent requestAgent) 
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
		if (requestAgent==null){
			throw new RMapDefectiveArgumentException("null system agent");
		}
				
		Value nameValue = typeAdapter.getValueFactory().createLiteral(name);
		IRI oAgentId = typeAdapter.uri2OpenRdfIri(agentID);
		IRI oIdentityProvider = typeAdapter.uri2OpenRdfIri(identityProvider);
		IRI oAuthKeyUri = typeAdapter.uri2OpenRdfIri(authKeyUri);
		RMapAgent agent = new ORMapAgent(oAgentId, oIdentityProvider, oAuthKeyUri, nameValue);
		RMapEvent event = createAgent(agent, requestAgent);
		return event;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#createAgent(String, java.net.URI, java.net.URI)
	 */
	@Override
	public RMapEvent createAgent(String name, URI identityProvider, URI authKeyUri) 
			throws RMapException, RMapDefectiveArgumentException {
		if (name==null){
			throw new RMapDefectiveArgumentException("null Agent name");
		}
		if (identityProvider==null){
			throw new RMapDefectiveArgumentException("null Agent identity provider");
		}
		if (authKeyUri==null){
			throw new RMapDefectiveArgumentException("null Agent authorization ID");
		}
		
		RMapEvent event = null;
		try {
			RMapValue rName = new RMapLiteral(name);
			RMapIri rIdentityProvider = new RMapIri(identityProvider);
			RMapIri rAuthKeyUri = new RMapIri(authKeyUri);
			RMapAgent agent = new ORMapAgent(rIdentityProvider, rAuthKeyUri, rName);	
			RMapRequestAgent requestAgent = new RMapRequestAgent(new URI(agent.getId().toString()));
			event = createAgent(agent, requestAgent);
		} catch (RMapException ex) {
			try {
				//there has been an error during an update so try to rollback the transaction
				triplestore.rollbackTransaction();
			} catch(RepositoryException rollbackException) {
				throw new RMapException("Could not rollback changes after error. Please check your Agent record for errors.", ex);
			}
			throw ex;	
		} catch (URISyntaxException e) {
			throw new RMapException("Could not convert Agent Id to URI", e);
		}	
		
		return event;
		
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#createAgent(RMapAgent, java.net.URI)
	 */
	@Override
	public RMapEvent updateAgent(RMapAgent agent, RMapRequestAgent requestAgent) 
			throws RMapException, RMapDefectiveArgumentException {
		if (agent==null){
			throw new RMapDefectiveArgumentException("null agent");
		}
		if (!(agent instanceof ORMapAgent)){
			throw new RMapDefectiveArgumentException("unrecognized type for agent");
		}
		if (requestAgent==null){
			throw new RMapDefectiveArgumentException("null system agent");
		}
		RMapEvent event = null;
		try{
			ORMapAgent orAgent = (ORMapAgent)agent;
			event = agentmgr.updateAgent(orAgent, requestAgent, triplestore);
		} catch (RMapException ex) {
			try {
				//there has been an error during an update so try to rollback the transaction
				triplestore.rollbackTransaction();
			} catch(RepositoryException rollbackException) {
				throw new RMapException("Could not rollback changes after error. Please check your Agent record for errors.", ex);
			}
			throw ex;	
		}	
		return event;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#createAgent(java.net.URI, String, java.net.URI, java.net.URI, java.net.URI)
	 */
	@Override
	public RMapEvent updateAgent(URI agentID, String name, URI identityProvider, URI authKeyUri, RMapRequestAgent requestAgent) 
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
		if (requestAgent==null){
			throw new RMapDefectiveArgumentException("null creating agent");
		}
		
		Value nameValue = typeAdapter.getValueFactory().createLiteral(name);
		IRI oAgentId = typeAdapter.uri2OpenRdfIri(agentID);
		IRI oIdentityProvider = typeAdapter.uri2OpenRdfIri(identityProvider);
		IRI oAuthKeyUri = typeAdapter.uri2OpenRdfIri(authKeyUri);
		RMapAgent agent = new ORMapAgent(oAgentId, oIdentityProvider, oAuthKeyUri, nameValue);
		RMapEvent event = updateAgent(agent, requestAgent);
		return event;
	}
		
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getAgentDiSCOs(java.net.URI, RMapStatus, Date, Date)
	 */
	@Override
	public List<URI> getAgentDiSCOs(URI agentId, RMapSearchParams params) throws RMapException,
			RMapDefectiveArgumentException {
		if (agentId==null){
			throw new RMapDefectiveArgumentException("null agentId");
		}
		if (params==null){
			params = new RMapSearchParams();
		}
		IRI uri = typeAdapter.uri2OpenRdfIri(agentId);
		List<IRI> events = agentmgr.getAgentDiSCOs(uri, params, triplestore); //, limit, offset
		List<URI> discoUris = typeAdapter.openRdfUriList2UriList(events);
		return discoUris;
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getAgentEvents(java.net.URI)
	 */
	@Override
	public List<URI> getAgentEvents(URI agentId) throws RMapException,
			RMapDefectiveArgumentException, RMapAgentNotFoundException {
		IRI uri = typeAdapter.uri2OpenRdfIri(agentId);
		if (agentId==null){
			throw new RMapDefectiveArgumentException("null agentId");
		}
		Set<IRI> eventset = eventmgr.getAgentRelatedEventIds(uri, triplestore);		
		List <URI> eventUris = new ArrayList<URI>();
		for (IRI eventid:eventset){
			eventUris.add(typeAdapter.openRdfIri2URI(eventid));
		}
		return eventUris;
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getAgentEventsInitiated(java.net.URI, Date, Date)
	 */
	@Override
	public List<URI> getAgentEventsInitiated(URI agentId, RMapSearchParams params) throws RMapException,
			RMapDefectiveArgumentException, RMapAgentNotFoundException {
		if (agentId==null){
			throw new RMapDefectiveArgumentException("null agentId");
		}
		if (params==null){
			params = new RMapSearchParams();
		}
		IRI uri = typeAdapter.uri2OpenRdfIri(agentId);
		List<IRI> events = agentmgr.getAgentEventsInitiated(uri, params, triplestore); //, limit, offset
		List<URI> eventUris = typeAdapter.openRdfUriList2UriList(events);
		return eventUris;
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getAgentStatus(java.net.URI)
	 */
	@Override
	public RMapStatus getAgentStatus(URI agentId) throws RMapException,
			RMapDefectiveArgumentException, RMapAgentNotFoundException {
		if (agentId==null){
			throw new RMapDefectiveArgumentException ("null agentId");
		}
		IRI id = typeAdapter.uri2OpenRdfIri(agentId);
		RMapStatus status = agentmgr.getAgentStatus(id, triplestore);
		return status;
	}

	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#isAgentId(java.net.URI)
	 */
	@Override
	public boolean isAgentId(URI agentId) throws RMapException, RMapDefectiveArgumentException {
		if (agentId==null){
			throw new RMapDefectiveArgumentException ("null Agent ID");
		}
		IRI id = typeAdapter.uri2OpenRdfIri(agentId);
		boolean isAgentId = agentmgr.isAgentId(id, triplestore);
		return isAgentId;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#isEventId(java.net.URI)
	 */
	@Override
	public boolean isEventId(URI eventId) throws RMapException, RMapDefectiveArgumentException {
		if (eventId==null){
			throw new RMapDefectiveArgumentException ("null Event ID");
		}
		IRI id = typeAdapter.uri2OpenRdfIri(eventId);
		boolean isEventId = eventmgr.isEventId(id, triplestore);
		return isEventId;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#isDiSCOId(java.net.URI)
	 */
	@Override
	public boolean isDiSCOId(URI discoId) throws RMapException, RMapDefectiveArgumentException {
		if (discoId==null){
			throw new RMapDefectiveArgumentException ("null DiSCO ID");
		}
		IRI id = typeAdapter.uri2OpenRdfIri(discoId);
		boolean isDiSCOId = discomgr.isDiscoId(id, triplestore);
		return isDiSCOId;
	}
	

}

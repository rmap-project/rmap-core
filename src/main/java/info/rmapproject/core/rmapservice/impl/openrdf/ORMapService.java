/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapDiSCONotFoundException;
import info.rmapproject.core.exception.RMapEventNotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapStatementNotFoundException;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.model.statement.RMapStatement;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;
import info.rmapproject.core.utils.Utils;

/**
 *  @author khansen, smorrissey
 *
 */
public class ORMapService implements RMapService {

	protected ORMapResourceMgr resourcemgr = new ORMapResourceMgr();
	protected ORMapStatementMgr stmtmgr = new ORMapStatementMgr();
	protected ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
	protected ORMapEventMgr eventmgr = new ORMapEventMgr();
	protected ORMapAgentMgr agentgmr = new ORMapAgentMgr();
	
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
	public List<URI> getResourceRelatedAll(URI uri, RMapStatus statusCode)
			throws RMapException, RMapDefectiveArgumentException {
		if (uri==null){
			throw new RMapDefectiveArgumentException("null uri");
		}
		List<URI>uris = new ArrayList<URI>();
		uris.addAll(this.getResourceRelatedStmts(uri, statusCode));
		uris.addAll(this.getResourceRelatedDiSCOs(uri, statusCode));
		uris.addAll(this.getResourceRelatedAgents(uri, statusCode));
		uris.addAll(this.getResourceRelatedEvents(uri));
		return uris;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getRelatedStmts(java.net.URI, info.rmapproject.core.model.RMapStatus)
	 */
	public List<URI> getResourceRelatedStmts(URI uri, RMapStatus statusCode)
			throws RMapException, RMapDefectiveArgumentException {
		if (uri==null){
			throw new RMapDefectiveArgumentException("null uri");
		}
		org.openrdf.model.URI mUri = ORAdapter.uri2OpenRdfUri(uri);
		Set<org.openrdf.model.URI> stmtIds = 
				this.resourcemgr.getRelatedStatements(mUri, statusCode, stmtmgr, 
						discomgr, ts);
		List<URI> ids = new ArrayList<URI>();
		for (org.openrdf.model.URI id:stmtIds){
			ids.add(ORAdapter.openRdfUri2URI(id));
		}
		return ids;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getRelatedEvents(java.net.URI)
	 */
	public List<URI> getResourceRelatedEvents(URI uri) throws RMapException, RMapDefectiveArgumentException {
		if (uri==null){
			throw new RMapDefectiveArgumentException("null uri");
		}
		org.openrdf.model.URI mUri = ORAdapter.uri2OpenRdfUri(uri);
		Set<org.openrdf.model.URI> orEvents =
				this.resourcemgr.getRelatedEvents(mUri, stmtmgr, discomgr, eventmgr, ts);
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
	public List<URI> getResourceRelatedDiSCOs(URI uri, RMapStatus statusCode)
			throws RMapException, RMapDefectiveArgumentException {
		if (uri==null){
			throw new RMapDefectiveArgumentException("null uri");
		}
		org.openrdf.model.URI mUri = ORAdapter.uri2OpenRdfUri(uri);
		Set<org.openrdf.model.URI> orDiscos = 
				this.resourcemgr.getRelatedDiSCOS(mUri, statusCode, discomgr, ts);
		List<URI> uris = new ArrayList<URI>();
		for (org.openrdf.model.URI disco:orDiscos){
			URI dUri = ORAdapter.openRdfUri2URI(disco);
			uris.add(dUri);
		}
		return uris;		
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getAgents(java.net.URI, info.rmapproject.core.model.RMapStatus)
	 */
	public List<URI> getResourceRelatedAgents(URI uri, RMapStatus statusCode)
			throws RMapException, RMapDefectiveArgumentException {
		if (uri==null){
			throw new RMapDefectiveArgumentException("null uri");
		}
		org.openrdf.model.URI resource = ORAdapter.uri2OpenRdfUri(uri);
		Set<org.openrdf.model.URI> resourceAgents = 
				this.resourcemgr.getRelatedAgents(resource, statusCode, stmtmgr, discomgr, eventmgr, agentgmr, ts);
		List<URI> uris = new ArrayList<URI>();
		for (org.openrdf.model.URI agent:resourceAgents){
			URI dUri = ORAdapter.openRdfUri2URI(agent);
			uris.add(dUri);
		}
		return uris;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#readStatement(java.net.URI)
	 */
	public RMapStatement readStatement(URI id) 
	throws RMapException, RMapStatementNotFoundException, RMapDefectiveArgumentException {
		org.openrdf.model.URI openUri;
		if (id==null){
			throw new RMapDefectiveArgumentException("Null Statement id provided");
		}
		try {
			openUri = ORAdapter.uri2OpenRdfUri(id);
		} catch (Exception e) {
			throw new RMapException("Unable to convert URI to OpenRDF URI", e);
		}
		return this.stmtmgr.getRMapStatement(openUri, ts);
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getStatementID(info.rmapproject.core.model.RMapNonLiteral, info.rmapproject.core.model.RMapUri, info.rmapproject.core.model.RMapResource)
	 */
	public URI getStatementID(RMapResource subject, RMapUri predicate,
			RMapValue object) throws RMapException, RMapDefectiveArgumentException {
		if (subject==null){
			throw new RMapDefectiveArgumentException("Null subject provided");
		}
		if (predicate ==null){
			throw new RMapDefectiveArgumentException("Null predicate provided");
		}
		if (object==null){
			throw new RMapDefectiveArgumentException("Null object provided");
		}
		Resource orSubject = ORAdapter.rMapNonLiteral2OpenRdfResource(subject);
		org.openrdf.model.URI orPredicate = ORAdapter.rMapUri2OpenRdfUri(predicate);
		Value orValue = ORAdapter.rMapValue2OpenRdfValue(object);
		org.openrdf.model.URI id = this.stmtmgr.getStatementID(orSubject, orPredicate, orValue, ts);
		return ORAdapter.openRdfUri2RMapUri(id).getIri();
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#readStatements(java.util.List)
	 */
	public List<RMapStatement> readStatements(List<URI> ids)
			throws RMapException, RMapStatementNotFoundException, RMapDefectiveArgumentException {
		if (ids==null || ids.size()==0){
			throw new RMapDefectiveArgumentException("Null or empty list of statement ids provided");
		}
		List<RMapStatement> stmts =  new ArrayList<RMapStatement>();
		for (URI id:ids){
			stmts.add(this.readStatement(id));
		}
		return stmts;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getStatementStatus(java.net.URI)
	 */
	public RMapStatus getStatementStatus(URI stmtId) throws RMapException, RMapDefectiveArgumentException {
		if (stmtId==null){
			throw new RMapDefectiveArgumentException ("Null statement id");
		}
		org.openrdf.model.URI uri = ORAdapter.uri2OpenRdfUri(stmtId);
		return this.stmtmgr.getStatementStatus(uri, this.discomgr, ts);
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getStatementEvents(java.net.URI)
	 */
	public List<URI> getStatementEvents(URI stmtId) throws RMapException, RMapDefectiveArgumentException {
		if (stmtId==null){
			throw new RMapDefectiveArgumentException("null uri");
		}
		org.openrdf.model.URI uri = ORAdapter.uri2OpenRdfUri(stmtId);
		List<org.openrdf.model.URI>uris = this.stmtmgr.getRelatedEvents(uri, this.eventmgr, ts);
		List<URI>events = new ArrayList<URI>();
		for (org.openrdf.model.URI event:uris){
			events.add(ORAdapter.openRdfUri2URI(event));
		}
		return events;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#readDiSCO(java.net.URI)
	 */
	public RMapDiSCO readDiSCO(URI discoID) 
	throws RMapException, RMapDiSCONotFoundException, RMapDefectiveArgumentException {
		if (discoID == null){
			throw new RMapDefectiveArgumentException("Null DiSCO id provided");
		}
		return this.discomgr.readDiSCO(ORAdapter.uri2OpenRdfUri(discoID), ts);
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
				(ORMapDiSCO)disco, this.eventmgr, this.stmtmgr, ts);
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
				disco, this.eventmgr, this.stmtmgr, ts);
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
					this.stmtmgr, this.eventmgr, ts);
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
					this.stmtmgr, this.eventmgr, ts);
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
					this.stmtmgr, this.eventmgr, ts);
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
		URI discoUri = this.getDiSCOIdLatestVersion(discoID);
		ORMapDiSCO latestDisco = null;		
		if (discoUri != null){
			org.openrdf.model.URI discoId = ORAdapter.uri2OpenRdfUri(discoID);
			latestDisco = this.discomgr.readDiSCO(discoId, ts);
		}
		return latestDisco;
	}
	@Override
	public URI getDiSCOIdLatestVersion(URI discoID) throws RMapException,
			RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(ORAdapter.uri2OpenRdfUri(discoID),
						true,this.eventmgr,ts);
		org.openrdf.model.URI lastEvent = 
				this.eventmgr.getLatestEvent(event2disco.keySet(),ts);
		org.openrdf.model.URI discoId = event2disco.get(lastEvent);
		URI discoURI = ORAdapter.openRdfUri2URI(discoId);
		return discoURI;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getPreviousVersionDiSCO(java.net.URI)
	 */
	public RMapDiSCO getDiSCOPreviousVersion(URI discoID) 
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		RMapDiSCO nextDisco = null;
		URI discoId = this.getDiSCOIdPreviousVersion(discoID);
		if (discoId != null){
			org.openrdf.model.URI prevDiscoId = ORAdapter.uri2OpenRdfUri(discoId);
			nextDisco = this.discomgr.readDiSCO(prevDiscoId,ts);
		}
		return nextDisco;
	}
	@Override
	public URI getDiSCOIdPreviousVersion(URI discoID)
			throws RMapException, RMapObjectNotFoundException,
			RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		URI nextDiscoUri = null;
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(ORAdapter.uri2OpenRdfUri(discoID),
						true,this.eventmgr,ts);
		Map<org.openrdf.model.URI,org.openrdf.model.URI> disco2event = 
				Utils.invertMap(event2disco);
		org.openrdf.model.URI discoEventId = disco2event.get(ORAdapter.uri2OpenRdfUri(discoID));
		Map <Date, org.openrdf.model.URI> date2event = 
				this.eventmgr.getDate2EventMap(event2disco.keySet(),ts);
		Map<org.openrdf.model.URI,Date> event2date = Utils.invertMap(date2event);
		Date eventDate = event2date.get(discoEventId);
		SortedSet<Date> sortedDates = new TreeSet<Date>();
		sortedDates.addAll(date2event.keySet());
		SortedSet<Date>earlierDates = sortedDates.headSet(eventDate);
		if (earlierDates.size()>0){
			Date previousDate = earlierDates.last()	;
			org.openrdf.model.URI prevEventId = date2event.get(previousDate);
			org.openrdf.model.URI prevDiscoId = event2disco.get(prevEventId);
			nextDiscoUri = ORAdapter.openRdfUri2URI(prevDiscoId);
		}		
		return nextDiscoUri;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getNextVersionDiSCO(java.net.URI)
	 */
	public RMapDiSCO getDiSCONextVersion(URI discoID) 
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		RMapDiSCO nextDisco = null;
		URI nextDiscoURI = this.getDiSCOIdNextVersion(discoID);
		if (nextDiscoURI != null){
			org.openrdf.model.URI nextDiscoId = ORAdapter.uri2OpenRdfUri(nextDiscoURI);
			nextDisco = this.discomgr.readDiSCO(nextDiscoId, ts);
		}
		return nextDisco;
	}
	@Override
	public URI getDiSCOIdNextVersion(URI discoID) throws RMapException,
			RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}
		URI nextDiscoUri = null;
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(ORAdapter.uri2OpenRdfUri(discoID),
						true,this.eventmgr,ts);
		Map<org.openrdf.model.URI,org.openrdf.model.URI> disco2event = 
				Utils.invertMap(event2disco);
		org.openrdf.model.URI discoEventId = disco2event.get(ORAdapter.uri2OpenRdfUri(discoID));
		Map <Date, org.openrdf.model.URI> date2event = 
				this.eventmgr.getDate2EventMap(event2disco.keySet(),ts);
		Map<org.openrdf.model.URI,Date> event2date = Utils.invertMap(date2event);
		Date eventDate = event2date.get(discoEventId);
		SortedSet<Date> sortedDates = new TreeSet<Date>();
		sortedDates.addAll(date2event.keySet());
		SortedSet<Date> laterDates = sortedDates.tailSet(eventDate);
		if (laterDates.size()>1){
			Date[] dateArray = laterDates.toArray(new Date[laterDates.size()]);	
			org.openrdf.model.URI nextEventId = date2event.get(dateArray[1]);
			org.openrdf.model.URI nextDiscoId = event2disco.get(nextEventId);
			nextDiscoUri = ORAdapter.openRdfUri2URI(nextDiscoId);
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
		List<org.openrdf.model.URI> events = 
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
	 * @see info.rmapproject.core.rmapservice.RMapService#getEventRelatedStatements(java.net.URI)
	 */
	public List<URI> getEventRelatedStatements(URI eventID)
			throws RMapException, RMapDefectiveArgumentException {
		if (eventID ==null){
			throw new RMapDefectiveArgumentException ("null event id");
		}
		List<org.openrdf.model.URI> stmts = this.eventmgr.getRelatedStatements(
				ORAdapter.uri2OpenRdfUri(eventID), this.discomgr, this.stmtmgr,ts);
		List<URI>stmtIds = new ArrayList<URI>();
		for (org.openrdf.model.URI id:stmts){
			stmtIds.add(ORAdapter.openRdfUri2URI(id));
		}
		return stmtIds;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getEventRelatedResources(java.net.URI)
	 */
	public List<URI> getEventRelatedResources(URI eventID) throws RMapException, RMapDefectiveArgumentException {
		if (eventID ==null){
			throw new RMapDefectiveArgumentException ("null event id");
		}
		List<org.openrdf.model.URI> resources = this.eventmgr.getRelatedResources(
				ORAdapter.uri2OpenRdfUri(eventID),this.discomgr, this.stmtmgr, ts);
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
		ORMapAgent agent = agentgmr.readAgent(ORAdapter.uri2OpenRdfUri(agentId), ts);
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
		RMapEvent event = this.agentgmr.createAgent(orAgent, systemAgentUri, this.eventmgr, ts);
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
		RMapEvent event = this.agentgmr.tombstoneAgent(systemAgentUri, targetAgentUri, 
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
		Set<org.openrdf.model.URI> ids = this.agentgmr.getAgentRepresentations(
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
		Set<org.openrdf.model.URI> ids = this.agentgmr.getAgentRepresentations(id, ts);
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
		RMapStatus status = this.agentgmr.getAgentStatus(id, ts);
		return status;
	}

	/**
	 * @return the resourcemgr
	 */
	public ORMapResourceMgr getResourcemgr() {
		return resourcemgr;
	}

	/**
	 * @return the stmtmgr
	 */
	public ORMapStatementMgr getStmtmgr() {
		return stmtmgr;
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
	 * @return the agentgmr
	 */
	public ORMapAgentMgr getAgentgmr() {
		return agentgmr;
	}

}

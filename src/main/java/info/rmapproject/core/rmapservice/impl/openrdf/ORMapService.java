/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapAgent;
import info.rmapproject.core.model.RMapDiSCO;
import info.rmapproject.core.model.RMapEvent;
import info.rmapproject.core.model.RMapNonLiteral;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapStatement;
import info.rmapproject.core.model.RMapStatementBag;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;
import info.rmapproject.core.utils.Utils;

/**
 * @author smorrissey
 *
 */
public class ORMapService implements RMapService {

	protected ORMapResourceMgr resourcemgr = new ORMapResourceMgr();
	protected ORMapStatementMgr stmtmgr = new ORMapStatementMgr();
	protected ORMapDiSCOMgr discomgr = new ORMapDiSCOMgr();
	protected ORMapEventMgr eventmgr = new ORMapEventMgr();
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

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getRelatedAll(java.net.URI, info.rmapproject.core.model.RMapStatus)
	 */
	public List<URI> getResourceRelatedAll(URI uri, RMapStatus statusCode)
			throws RMapException {
		if (uri==null){
			throw new RMapException("null uri");
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
			throws RMapException {
		if (uri==null){
			throw new RMapException("null uri");
		}
		org.openrdf.model.URI mUri = ORAdapter.uri2OpenRdfUri(uri);
		List<org.openrdf.model.URI>mUris = this.stmtmgr.getRelatedStmts(mUri, this.ts);
		List<org.openrdf.model.URI>sUris = this.resourcemgr.getRelatedStmts(mUris, statusCode);
		List<URI>stmts = new ArrayList<URI>();
		for (org.openrdf.model.URI sUri:sUris){
			URI stmtUri = ORAdapter.openRdfUri2URI(sUri);
			stmts.add(stmtUri);
		}
		return stmts;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getRelatedEvents(java.net.URI)
	 */
	public List<URI> getResourceRelatedEvents(URI uri) throws RMapException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getRelatedDiSCOs(java.net.URI, info.rmapproject.core.model.RMapStatus)
	 */
	public List<URI> getResourceRelatedDiSCOs(URI uri, RMapStatus statusCode)
			throws RMapException {
		if (statusCode==null){
			throw new RMapException("Null status code provided");
		}
		org.openrdf.model.URI mUri = ORAdapter.uri2OpenRdfUri(uri);
		List<ORMapDiSCO> orDiscos = this.getResourceAllRelatedDiSCOS(mUri, statusCode);
		List<URI> uris = new ArrayList<URI>();
		for (ORMapDiSCO disco:orDiscos){
			URI dUri = disco.getId();
			uris.add(dUri);
		}
		return uris;
		
	}
	/**
	 * Protected method to allow all status code (NOT provided as choice to public interface,
	 * used by implementation methods
	 * @param uri
	 * @param statusCode
	 * @return
	 * @throws RMapException
	 */
	protected List<ORMapDiSCO> getResourceAllRelatedDiSCOS(org.openrdf.model.URI uri, RMapStatus statusCode)
		throws RMapException {
			// TODO Auto-generated method stub
			return null;			
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getAgents(java.net.URI, info.rmapproject.core.model.RMapStatus)
	 */
	public List<URI> getResourceRelatedAgents(URI uri, RMapStatus statusCode)
			throws RMapException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#readStatement(java.net.URI)
	 */
	public RMapStatement readStatement(URI id) throws RMapException {
		org.openrdf.model.URI openUri;
		if (id==null){
			throw new RMapException("Null Statement id provided");
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
	public URI getStatementID(RMapNonLiteral subject, RMapUri predicate,
			RMapResource object) throws RMapException {
		Resource orSubject = ORAdapter.rMapNonLiteral2OpenRdfResource(subject);
		org.openrdf.model.URI orPredicate = ORAdapter.rMapUri2OpenRdfUri(predicate);
		Value orValue = ORAdapter.rMapResource2OpenRdfValue(object);
		org.openrdf.model.URI id = this.stmtmgr.getStatementID(orSubject, orPredicate, orValue, ts);
		return ORAdapter.openRdfUri2RMapUri(id).getIri();
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#readStatements(java.util.List)
	 */
	public List<RMapStatement> readStatements(List<URI> ids)
			throws RMapException {
		if (ids==null){
			throw new RMapException("Null list of statement ids provided");
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
	public RMapStatus getStatementStatus(URI stmtId) throws RMapException {
		if (stmtId==null){
			throw new RMapException ("Null statement id");
		}
		org.openrdf.model.URI uri = ORAdapter.uri2OpenRdfUri(stmtId);
		return this.stmtmgr.getStatementStatus(uri);
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getStatementEvents(java.net.URI)
	 */
	public List<URI> getStatementEvents(URI stmtId) throws RMapException {
		if (stmtId==null){
			throw new RMapException("null uri");
		}
		org.openrdf.model.URI uri = ORAdapter.uri2OpenRdfUri(stmtId);
		List<URI>uris = this.stmtmgr.getRelatedEvents(uri, ts);
		return uris;
	}
	/**
	 * Get ids of all DiSCOS containing Statement identified by stmtID
	 * @param stmtId
	 * @return
	 * @throws RMapException
	 */
	public List<org.openrdf.model.URI>getStatementRelatedDiSCOs(org.openrdf.model.URI stmtId)
			throws RMapException {
		// TODO implement body
		return null;
	}
	/**
	 * Return all Resources (URI or Bnode) in subject or object of Statement
	 * @param stmtId
	 * @return
	 * @throws RMapException
	 */
	public List<org.openrdf.model.URI>getStatementRelatedResources(org.openrdf.model.URI stmtId)
			throws RMapException {
		// TODO implement body
		return null;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#readDiSCO(java.net.URI)
	 */
	public RMapDiSCO readDiSCO(URI discoID) throws RMapException {
		return this.discomgr.readDiSCO(ORAdapter.uri2OpenRdfUri(discoID), ts);
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#createDisco(java.util.List, info.rmapproject.core.model.RMapStatementBag, info.rmapproject.core.model.RMapResource, info.rmapproject.core.model.RMapResource)
	 */
	public RMapEvent createDiSCO(RMapAgent systemAgent,
			List<URI> aggregatedResources, RMapResource creator,
			RMapStatementBag relatedStatements, RMapResource desc) throws RMapException {
		ORMapDiSCO disco = new ORMapDiSCO(creator, aggregatedResources, desc, relatedStatements);
		RMapEvent createEvent = 
				this.discomgr.createDiSCO(ORAdapter.uri2OpenRdfUri(systemAgent.getId()),
				disco, ts);
		return createEvent;
	}
	
	public RMapEvent createDiSCO(org.openrdf.model.URI systemAgentId, List<Statement> stmts) throws RMapException{
		RMapEvent createEvent = 
				this.discomgr.createDiSCO(systemAgentId, stmts, ts);
		return createEvent;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getDiSCOStatus(java.net.URI)
	 */
	public RMapStatus getDiSCOStatus(URI discoId) throws RMapException {
		RMapStatus status = null;
		status = this.discomgr.getDiSCOStatus(ORAdapter.uri2OpenRdfUri(discoId), ts);
		return status;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#updateDiSCO(java.net.URI, java.util.List, info.rmapproject.core.model.RMapStatementBag, info.rmapproject.core.model.RMapResource, info.rmapproject.core.model.RMapResource)
	 */
	public RMapEvent updateDiSCO(RMapAgent systemAgent, URI oldDiscoId,
			List<URI> aggregatedResources, RMapStatementBag relatedStatements,
			RMapResource creator, RMapResource desc) throws RMapException {
		ORMapDiSCO disco = new ORMapDiSCO(creator, aggregatedResources, desc, 
				relatedStatements);
		
		RMapEvent updateEvent = 
				this.discomgr.updateDiSCO(ORAdapter.uri2OpenRdfUri(systemAgent.getId()),
					ORAdapter.uri2OpenRdfUri(oldDiscoId), disco, ts);
		return updateEvent;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#deleteDiSCO(java.net.URI)
	 */
	public RMapEvent deleteDiSCO(URI discoID, RMapAgent systemAgent) throws RMapException {
		RMapEvent tombstoneEvent = 
				this.discomgr.tombstoneDiSCO(ORAdapter.uri2OpenRdfUri(systemAgent.getId()),
						ORAdapter.uri2OpenRdfUri(discoID), ts);
		return tombstoneEvent;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getAllDiSCOVersions(java.net.URI)
	 */
	public List<URI> getDiSCOAllVersions(URI discoID) throws RMapException {
		List<org.openrdf.model.URI> versions = 
		   this.getAllDiSCOVersionORdf(ORAdapter.uri2OpenRdfUri(discoID));
		List<URI> uris = new ArrayList<URI>();
		for (org.openrdf.model.URI version:versions){
			uris.add(ORAdapter.openRdfUri2URI(version));
		}
		return uris;
	}
	/**
	 * 
	 * @param discoId
	 * @return
	 * @throws RMapException
	 */
	public List<org.openrdf.model.URI> getAllDiSCOVersionORdf (org.openrdf.model.URI discoId)
	throws RMapException {
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(discoId, false, ts);
		List<org.openrdf.model.URI> discos = new ArrayList<org.openrdf.model.URI>();
		discos.addAll(event2disco.values())	;	
		return discos;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getAllAgentDiSCOVersions(java.net.URI)
	 */
	public List<URI> getDiSCOAllAgentVersions(URI discoID) throws RMapException {
		List<org.openrdf.model.URI> versions = 
				this.getDiSCOAllAgentVersions(ORAdapter.uri2OpenRdfUri(discoID));
		List<URI> uris = new ArrayList<URI>();
		for (org.openrdf.model.URI version:versions){
			uris.add(ORAdapter.openRdfUri2URI(version));
		}
		return uris;
	}
	
	public List<org.openrdf.model.URI> getDiSCOAllAgentVersions(org.openrdf.model.URI discoID) 
	throws RMapException {
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(discoID, true, ts);
		List<org.openrdf.model.URI> discos = new ArrayList<org.openrdf.model.URI>();
		discos.addAll(event2disco.values())	;		
		return discos;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getLatestVersionDiSCO(java.net.URI)
	 */
	public RMapDiSCO getDiSCOLatestVersion(URI discoID) throws RMapException {
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(ORAdapter.uri2OpenRdfUri(discoID),true,ts);
		org.openrdf.model.URI lastEvent = 
				this.eventmgr.getLatestEvent(event2disco.keySet(),ts);
		org.openrdf.model.URI discoId = event2disco.get(lastEvent);
		return this.discomgr.readDiSCO(discoId, ts);
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getPreviousVersionDiSCO(java.net.URI)
	 */
	public RMapDiSCO getDiSCOPreviousVersion(URI discoID) throws RMapException {
		RMapDiSCO nextDisco = null;
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(ORAdapter.uri2OpenRdfUri(discoID),true,ts);
		Map<org.openrdf.model.URI,org.openrdf.model.URI> disco2event = 
				Utils.invertMap(event2disco);
		org.openrdf.model.URI discoEventId = disco2event.get(discoID);
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
			nextDisco = this.discomgr.readDiSCO(prevDiscoId,ts);
		}
		return nextDisco;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getNextVersionDiSCO(java.net.URI)
	 */
	public RMapDiSCO getDiSCONextVersion(URI discoID) throws RMapException {
		RMapDiSCO nextDisco = null;
		Map<org.openrdf.model.URI,org.openrdf.model.URI>event2disco=
				this.discomgr.getAllDiSCOVersions(ORAdapter.uri2OpenRdfUri(discoID),true,ts);
		Map<org.openrdf.model.URI,org.openrdf.model.URI> disco2event = 
				Utils.invertMap(event2disco);
		org.openrdf.model.URI discoEventId = disco2event.get(discoID);
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
			nextDisco = this.discomgr.readDiSCO(nextDiscoId, ts);
		}
		return nextDisco;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getDiSCOEvents(java.net.URI)
	 */
	public List<URI> getDiSCOEvents(URI discoID) throws RMapException {
		List<org.openrdf.model.URI> events = 
				this.getDiSCOEvents(ORAdapter.uri2OpenRdfUri(discoID));
		List<URI> uris = new ArrayList<URI>();
		for (org.openrdf.model.URI event:events){
			uris.add(ORAdapter.openRdfUri2URI(event));
		}
		return uris;
	}
	/**
	 * 
	 */
	public List<org.openrdf.model.URI> getDiSCOEvents(org.openrdf.model.URI discoID)
			throws RMapException {
		List<org.openrdf.model.URI> events = this.discomgr.getDiscoEvents(discoID, ts);
		return events;
	}
	/**
	 * Get ids of all Statements that are part of a DiSCO
	 * @param discoID
	 * @return
	 * @throws RMapException
	 */
	public List<org.openrdf.model.URI>getDiSCORelatedStatements(org.openrdf.model.URI discoID)
			throws RMapException {
		// TODO implement body
		return null;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#readEvent(java.net.URI)
	 */
	public RMapEvent readEvent(URI eventId) throws RMapException {
		return this.eventmgr.readEvent(ORAdapter.uri2OpenRdfUri(eventId), ts);
	}
	/**
	 * 
	 */
	public RMapEvent readEvent(org.openrdf.model.URI eventId) throws RMapException {
		return this.eventmgr.readEvent((eventId), ts);
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getEventRelatedStatements(java.net.URI)
	 */
	public List<URI> getEventRelatedStatements(URI eventID)
			throws RMapException {
		List<org.openrdf.model.URI> stmts = this.eventmgr.getRelatedStatements(
				ORAdapter.uri2OpenRdfUri(eventID), ts);
		List<URI>stmtIds = new ArrayList<URI>();
		for (org.openrdf.model.URI id:stmts){
			stmtIds.add(ORAdapter.openRdfUri2URI(id));
		}
		return stmtIds;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getEventRelatedResources(java.net.URI)
	 */
	public List<URI> getEventRelatedResources(URI eventID) throws RMapException {
		List<org.openrdf.model.URI> resources = this.eventmgr.getRelatedResources(
				ORAdapter.uri2OpenRdfUri(eventID), ts);
		List<URI> resourceIds = new ArrayList<URI>();
		for (org.openrdf.model.URI resource:resources){
			resourceIds.add(ORAdapter.openRdfUri2URI(resource));
		}
		return resourceIds;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapService#getEventRelatedDiSCOS(java.net.URI)
	 */
	public List<URI> getEventRelatedDiSCOS(URI eventID) throws RMapException {
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
	public List<URI> getEventRelatedAgents(URI eventID) throws RMapException {
		List<org.openrdf.model.URI> agents = this.eventmgr.getRelatedAgents(
				ORAdapter.uri2OpenRdfUri(eventID), ts);
		List<URI> agentIds = new ArrayList<URI>();
		for (org.openrdf.model.URI agent:agents){
			agentIds.add(ORAdapter.openRdfUri2URI(agent));
		}
		return agentIds;
	}

	public RMapAgent readAgent(URI agentID) throws RMapException {
		// TODO Auto-generated method stub
		return null;
	}


}

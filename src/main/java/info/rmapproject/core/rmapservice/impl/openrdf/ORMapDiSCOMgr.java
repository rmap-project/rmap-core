/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;


import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapDeletedObjectException;
import info.rmapproject.core.exception.RMapDiSCONotFoundException;
import info.rmapproject.core.exception.RMapEventNotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapTombstonedObjectException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.model.impl.openrdf.ORMapEvent;
import info.rmapproject.core.model.impl.openrdf.ORMapEventCreation;
import info.rmapproject.core.model.impl.openrdf.ORMapEventDerivation;
import info.rmapproject.core.model.impl.openrdf.ORMapEventInactivation;
import info.rmapproject.core.model.impl.openrdf.ORMapEventTombstone;
import info.rmapproject.core.model.impl.openrdf.ORMapEventUpdate;
import info.rmapproject.core.model.impl.openrdf.ORMapEventWithNewObjects;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;
import info.rmapproject.core.utils.Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * Class that creates actual triples for DiSCO, related Events, and
 * Statements in the tripleStore;
 * 
 *  @author khanson, smorrissey
 *
 */
public class ORMapDiSCOMgr extends ORMapObjectMgr {

	/**
	 * 
	 */
	public ORMapDiSCOMgr() {
		super();
	}
	/**
	 * Return DiSCO DTO corresponding to discoID
	 * @param discoID
	 * @param getLinks 
	 * @param event2disco 
	 * @param date2event 
	 * @param eventMgr 
	 * @param ts
	 * @return
	 * @throws RMapTombstonedObjectException
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapObjectNotFoundException 
	 * @throws RMapException 
	 */
	public ORMapDiSCODTO readDiSCO(URI discoID, boolean getLinks, Map<URI, URI> event2disco, 
			Map<Date, URI> date2event, ORMapEventMgr eventMgr, SesameTriplestore ts) 
	throws RMapTombstonedObjectException, RMapDeletedObjectException, RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		ORMapDiSCO disco = null;
		if (discoID ==null){
			throw new RMapException ("null discoID");
		}
		if (ts==null){
			throw new RMapException("null triplestore");
		}
		if (! (this.isDiscoId(discoID, ts))){
			throw new RMapDiSCONotFoundException("No DiSCO with id " + discoID.stringValue());
		}
		
		RMapStatus status = this.getDiSCOStatus(discoID, ts);
		switch (status){
		case TOMBSTONED :
			throw new RMapTombstonedObjectException("DiSCO "+ discoID.stringValue() + " has been (soft) deleted");
		case DELETED :
			throw new RMapDeletedObjectException ("DiSCO "+ discoID.stringValue() + " has been deleted");
		default:
			break;		
		}		
		
		List<Statement> discoStmts = null;
		try {
			discoStmts = this.getNamedGraph(discoID, ts);		
		}
		catch (RMapObjectNotFoundException e){
			throw new RMapDiSCONotFoundException("No DiSCO found with id " + discoID.stringValue(), e);
		}
		disco = new ORMapDiSCO(discoStmts);
		
		ORMapDiSCODTO dto = new ORMapDiSCODTO();
		dto.setDisco(disco);
		dto.setStatus(status);
		
		if (getLinks){
			// get latest version of this DiSCO
			if (event2disco==null){
				event2disco = this.getAllDiSCOVersions(discoID,true,eventMgr,ts);
			}
			dto.setLatest(this.getLatestDiSCOUri(discoID, eventMgr, ts, event2disco));
			if (date2event==null){
				date2event = 
						eventMgr.getDate2EventMap(event2disco.keySet(),ts);
			}
			// get next version of this DiSCO
			URI nextDiscoId = this.getNextURI(discoID, event2disco, date2event, eventMgr, ts);
			dto.setNext(nextDiscoId);			
			// get previous version of this DiSCO
			URI prevDiscoId = this.getPreviousURI(discoID, event2disco, date2event, eventMgr, ts);
			dto.setPrevious(prevDiscoId);
		}
		return dto;		
	}
	
	/**
	 * 
	 * @param systemAgentId
	 * @param stmts
	 * @param stmtMgr 
	 * @param eventMgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public RMapEvent createDiSCO(org.openrdf.model.URI systemAgentId,
			List<Statement> stmts, ORMapEventMgr eventMgr, SesameTriplestore ts) 
					throws RMapException {
		if (systemAgentId==null){
			throw new RMapException ("Null system agent id");			
		}
		if (stmts==null || stmts.size()==0){
			throw new RMapException ("null or emtpy list of statements");
		}
		if (ts==null){
			throw new RMapException ("null value for triplestore");
		}
		ORMapDiSCO disco = new ORMapDiSCO(stmts);		
		return this.createDiSCO(systemAgentId, disco, eventMgr, ts);
	}
	
	/**
	 * 
	 * @param systemAgentId
	 * @param disco
	 * @param eventMgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public ORMapEvent createDiSCO(URI systemAgentId, ORMapDiSCO disco, 
			ORMapEventMgr eventMgr,SesameTriplestore ts) 
			throws RMapException{		
		// confirm non-null disco
		if (disco==null){
			throw new RMapException ("Null disco parameter");
		}
		if (systemAgentId==null){
			throw new RMapException("System Agent ID required: was null");
		}
		// Confirm systemAgentId (not null, is Agent)
		if (! this.isAgentId(systemAgentId, ts)){
			throw new RMapAgentNotFoundException("No agent with id " + systemAgentId.stringValue());
		}
		// get the event started
		ORMapEventCreation event = new ORMapEventCreation(systemAgentId, RMapEventTargetType.DISCO);
		// Create reified statements for aggregrated resources if needed
		List<Statement> aggResources = disco.getAggregatedResourceStatements();
		if (aggResources == null){
			throw new RMapException("Null aggregated resources in DiSCO");
		}
		// set up triplestore and start transaction
		boolean doCommitTransaction = false;
		try {
			if (!ts.hasTransactionOpen())	{
				doCommitTransaction = true;
				ts.beginTransaction();
			}
		} catch (Exception e) {
			throw new RMapException("Unable to begin Sesame transaction: ", e);
		} 
		// create triples for all statements in DiSCO
		// Keep track of resources created by this Event
		Set<URI> created = new HashSet<URI>();
		// add the DiSCO id as an event-created Resource
		created.add(disco.getDiscoContext());
		
		//  for type statement add the triple
		this.createTriple(ts, disco.getTypeStatement());
		
		// discoCreator  add the triple
		this.createTriple(ts, disco.getCreatorStmt());
		//  description add the triple
		if (disco.getDescriptonStatement() != null){
			this.createTriple(ts, disco.getDescriptonStatement());
		}
		
		// create triple for disco id the provider used
		if (disco.getProviderIdStmt()!= null){
			this.createTriple(ts,disco.getProviderIdStmt());
		}
		
		// for each aggregated resource, create reified statement if necessary, create triples
		for (Statement stmt:aggResources){
			this.createTriple(ts, stmt);
		}
		do {
			List<Statement> relatedStmts = disco.getRelatedStatementsAsList();
			if (relatedStmts == null){
				break;
			}
			// Replace any BNodes in incoming statements with RMap identifiers
			List<Statement> filteredRelatedStatements = this.replaceBNodeWithRMapId(relatedStmts, ts);
			// for each statement in relatedStatements
			//  add the triple
			for (Statement stmt:filteredRelatedStatements){
				this.createTriple(ts, stmt);
			}	
		} while (false);
		// update the event with created object IDS
		event.setCreatedObjectIdsFromURI(created);		
		// end the event, write the event triples, and commit everything
		event.setEndTime(new Date());
		eventMgr.createEvent(event, ts);

		if (doCommitTransaction){
			try {
				ts.commitTransaction();
			} catch (Exception e) {
				throw new RMapException("Exception thrown committing new triples to triplestore");
			}
		}
		return event;
	}

	/**
	 * 
	 * @param systemAgentId
	 * @param justInactivate
	 * @param oldDiscoId
	 * @param disco
	 * @param stmtMgr
	 * @param eventMgr
	 * @param ts
	 * @return
	 * @throws RMapDefectiveArgumentException
	 * @throws RMapAgentNotFoundException
	 * @throws RMapException
	 */
	public RMapEvent updateDiSCO(URI systemAgentId, 
			boolean justInactivate, URI oldDiscoId, ORMapDiSCO disco,
			ORMapEventMgr eventMgr, SesameTriplestore ts) 
	throws RMapDefectiveArgumentException, RMapAgentNotFoundException, RMapException {
		// confirm non-null old disco
		if (oldDiscoId==null){
			throw new RMapDefectiveArgumentException ("Null value for id of target DiSCO");
		}
		// Confirm systemAgentId (not null, is Agent)
		if (systemAgentId==null){
			throw new RMapException("System Agent ID required: was null");
		}		
		if (! this.isAgentId(systemAgentId, ts)){
			throw new RMapAgentNotFoundException("No agent with id " + systemAgentId.stringValue());
		}			
		// get the event started
		ORMapEvent event = null;	
		if (justInactivate){
			// must be same agent
			if (this.isSameCreatorAgent(oldDiscoId, systemAgentId, eventMgr, ts)){
				ORMapEventInactivation iEvent = new ORMapEventInactivation(systemAgentId, RMapEventTargetType.DISCO);
				iEvent.setInactivatedObjectId(ORAdapter.openRdfUri2RMapUri(oldDiscoId));
				event = iEvent;
			}
			else {
				throw new RMapDefectiveArgumentException("Agent is not the same as creating agent; " +
						" cannot inactivate another agent's DiSCO");
			}
		}
		else {
			// if same agent, it's an update; otherwise it's a derivation
			// in either case, must have non-null new DiSCO
			if (disco==null){
				throw new RMapDefectiveArgumentException("No new DiSCO provided for update");
			}
			if (this.isSameCreatorAgent(oldDiscoId, systemAgentId, eventMgr, ts)){
				ORMapEventUpdate uEvent = new ORMapEventUpdate(systemAgentId, RMapEventTargetType.DISCO,
						oldDiscoId, disco.getDiscoContext());
				event = uEvent;
			}
			else {
				ORMapEventDerivation dEvent = new ORMapEventDerivation(systemAgentId, RMapEventTargetType.DISCO,
						oldDiscoId, disco.getDiscoContext());
				event = dEvent;
			}
		}
			
		// set up triplestore and start transaction
		boolean doCommitTransaction = false;
		try {
			if (!ts.hasTransactionOpen())	{
				doCommitTransaction = true;
				ts.beginTransaction();
			}
		} catch (Exception e) {
			throw new RMapException("Unable to begin Sesame transaction: ", e);
		}
		do {
			if (disco==null){
				// just inactivating; no new disco
				break;
			}
			// create any new triples for all statements in DiSCO
			// Keep track of resources created by this Event
			Set<URI> created = new HashSet<URI>();			
			// add the DiSCO id as an event-created Resource
			created.add(disco.getDiscoContext());
			
			this.createTriple(ts, disco.getTypeStatement());

			this.createTriple(ts, disco.getCreatorStmt());
			// Create reified statement for description if necessary, and add the triple
			if (disco.getDescriptonStatement()!= null){
				this.createTriple(ts, disco.getDescriptonStatement());
			}			
			// create reified statement for disco id the provider used
			if (disco.getProviderIdStmt()!= null){
				this.createTriple(ts,disco.getProviderIdStmt());
			}
			
			// for each aggregated resource, create triples
			List<Statement> aggResources = disco.getAggregatedResourceStatements();
			for (Statement stmt:aggResources){
				this.createTriple(ts, stmt);
			}
			
			do {
				List<Statement> relatedStmts = disco.getRelatedStatementsAsList();
				if (relatedStmts == null){
					break;
				}
				// Replace any BNodes in incoming statements with RMap identifiers
				List<Statement> filteredRelatedStatements = this.replaceBNodeWithRMapId(relatedStmts, ts);
				// for each statement in relatedStatements
				//   add the triple
				for (Statement stmt:filteredRelatedStatements){
					this.createTriple(ts, stmt);
				}	
			} while (false);
			if (event instanceof ORMapEventWithNewObjects){
				((ORMapEventWithNewObjects)event).setCreatedObjectIdsFromURI(created);		
			}
		} while (false);
			
		// end the event, write the event triples, and commit everything
		event.setEndTime(new Date());
		eventMgr.createEvent(event, ts);
		if (doCommitTransaction){
			try {
				ts.commitTransaction();
			} catch (Exception e) {
				throw new RMapException("Exception thrown committing new triples to triplestore");
			}
		}
		return event;
	}
	/**
	 * Soft-delete a DiSCO A read of this DiSCO should return tombstone notice rather 
	 * than statements in the DiSCO,but DiSCO named graph is not deleted from triplestore
	 * @param systemAgentId
	 * @param oldDiscoId
	 * @param eventMgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public RMapEvent tombstoneDiSCO(URI systemAgentId,URI oldDiscoId, ORMapEventMgr eventMgr, 
			SesameTriplestore ts) 
	throws RMapException {
		// confirm non-null old disco
		if (oldDiscoId==null){
			throw new RMapException ("Null value for id of DiSCO to be tombstoned");
		}
		if (systemAgentId==null){
			throw new RMapException("System Agent ID required: was null");
		}
		// Confirm systemAgentId (not null, is Agent)
		if (!(this.isAgentId(systemAgentId, ts))){
			throw new RMapAgentNotFoundException("No agent with id " + systemAgentId.stringValue());
		}
		// make sure same Agent created the DiSCO now being inactivated
		if (! this.isSameCreatorAgent(oldDiscoId, systemAgentId, eventMgr, ts)){
			throw new RMapException(
					"Agent attempting to tombstone DiSCO is not same as its creating Agent");
		}
		
		// get the event started
		ORMapEventTombstone event = new ORMapEventTombstone(systemAgentId, 
				RMapEventTargetType.DISCO, oldDiscoId);

		// set up triplestore and start transaction
		boolean doCommitTransaction = false;
		try {
			if (!ts.hasTransactionOpen())	{
				doCommitTransaction = true;
				ts.beginTransaction();
			}
		} catch (Exception e) {
			throw new RMapException("Unable to begin Sesame transaction: ", e);
		}
		// end the event, write the event triples, and commit everything
		event.setEndTime(new Date());
		eventMgr.createEvent(event, ts);

		if (doCommitTransaction){
			try {
				ts.commitTransaction();
			} catch (Exception e) {
				throw new RMapException("Exception thrown committing new triples to triplestore");
			}
		}
		return event;
	}
	/**
	 * Get status of DiSCO
	 * If DiSCO has been deleted, then its status is deleted,
	 * else if DiSCO has been tombstoned, then its status is tombsoned
	 * else if DiSCO has been updated, then its satus is inactive,
	 * else status is active
	 * @param discoId
	 * @param ts
	 * @return
	 * @throws RMapDiSCONotFoundException
	 * @throws RMapException
	 */
	public RMapStatus getDiSCOStatus(URI discoId, SesameTriplestore ts) 
			throws RMapDiSCONotFoundException, RMapException {
		RMapStatus status = null;
		if (discoId==null){
			throw new RMapException ("Null disco");
		}
		// first ensure Exists statement URI rdf:TYPE rmap:DISCO  if not: raise NOTFOUND exception
		if (! this.isDiscoId(discoId, ts)){
			throw new RMapDiSCONotFoundException ("No DisCO found with id " + discoId.stringValue());
		}
		do {
			List<Statement> eventStmts = null;
			try {
				//   ? RMap:Deletes discoId  done return deleted
				eventStmts = ts.getStatements(null, RMAP.EVENT_TARGET_DELETED, discoId);
				if (eventStmts!=null && ! eventStmts.isEmpty()){
					status = RMapStatus.DELETED;
					break;
				}
				//   ? RMap:TombStones discoID	done return tombstoned
				eventStmts = ts.getStatements(null, RMAP.EVENT_TARGET_TOMBSTONED, discoId);
				if (eventStmts!=null && ! eventStmts.isEmpty()){
					status = RMapStatus.TOMBSTONED;
					break;
				}
				//   ? RMap:Updates discoID	done return Inactive
				eventStmts = ts.getStatements(null, RMAP.EVENT_INACTIVATED_OBJECT, discoId);
				if (eventStmts!=null && ! eventStmts.isEmpty()){
					status = RMapStatus.INACTIVE;
					break;
				}
			   //   else return active if create event found
				eventStmts = ts.getStatements(null, PROV.GENERATED, discoId);
				if (eventStmts!=null && ! eventStmts.isEmpty()){
					status = RMapStatus.ACTIVE;
					break;
				}
				// else throw exception
				throw new RMapException ("No Events found for determing status of  " +
						discoId.stringValue());
			} catch (Exception e) {
				throw new RMapException("Exception thrown querying triplestore for events", e);
			}
		}while (false);
		
		return status;
	}

	/**
	 * Method to get all versions of DiSCO
	 * If matchAgent = true, then return only versions created by same agent as creating agent
	 *                 if false, then return all versions by all agents
	 * @param discoId
	 * @param matchAgent true if searching for versions of disco by a particlar agent;
	 *                   false if searching for all versions regardless of agent
	 * @param ts triplestore
	 * @return Map from URI of an Event to the URI of DiSCO created by event, either as creation,
	 *  update, or derivation
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 */
	public Map<URI,URI> getAllDiSCOVersions(URI discoId, boolean matchAgent, 
			ORMapEventMgr eventmgr, SesameTriplestore ts) 
			throws RMapObjectNotFoundException, RMapException {
		if (discoId==null){
			throw new RMapException ("Null disco");
		}
		if (! this.isDiscoId(discoId, ts)){
			throw new RMapDiSCONotFoundException("No disco found with identifer " + 
					discoId.stringValue());
		}
		Map<URI,URI> event2Disco = lookBack(discoId, null, true, matchAgent, eventmgr, ts);		
		return event2Disco;
	}
	/**
	 * 
	 * @param discoId
	 * @param agentId
	 * @param lookFoward
	 * @param matchAgent
	 * @param ts
	 * @return
	 * @throws RMapObjectNotFoundException 
	 */
	protected Map<URI,URI> lookBack(URI discoId, URI agentId, boolean lookFoward, 
			boolean matchAgent, ORMapEventMgr eventmgr, SesameTriplestore ts) 
					throws RMapObjectNotFoundException, RMapException {
		Statement eventStmt = eventmgr.getRMapObjectCreateEventStatement(discoId, ts);
		if (eventStmt==null){
			throw new RMapEventNotFoundException("No creating event found for DiSCO id " +
		             discoId.stringValue());
		}
		Map<URI,URI> event2Disco = new HashMap<URI,URI>();
		do {
			URI eventId = (URI)eventStmt.getSubject();
			URI oldAgentId = agentId;
			if (matchAgent){
				// first time through, agentID will be null
				if (agentId==null){
					oldAgentId = eventmgr.getEventAssocAgent(eventId, ts);
				}
				URI uAgent = eventmgr.getEventAssocAgent(eventId, ts);
				if (!(oldAgentId.equals(uAgent))){
					break;
				}
			}
			event2Disco.put(eventId,discoId);			
			if(eventmgr.isCreationEvent(eventId, ts)){					
				if (lookFoward){
					event2Disco.putAll(this.lookFoward(discoId, oldAgentId, matchAgent,eventmgr, ts));
				}
				break;
			}
			if ((eventmgr.isUpdateEvent(eventId, ts)) || (eventmgr.isDerivationEvent(eventId, ts))){
				// get id of old DiSCO
				URI oldDiscoID = eventmgr.getIdOfOldDisco(eventId, ts);
				if (oldDiscoID==null){
					throw new RMapDiSCONotFoundException("Event " + eventId.stringValue() + 
							" does not have Derived Object DiSCO for DISCO " + discoId.stringValue());
				}
				// look back recursively on create/updates for oldDiscoID
				// DONT look forward on the backward search - you'll already have stuff
				 event2Disco.putAll(this.lookBack(oldDiscoID, oldAgentId, false, 
						 matchAgent, eventmgr, ts));
				// now look ahead for any derived discos
				 event2Disco.putAll(this.lookFoward(discoId, oldAgentId, matchAgent, eventmgr,ts));
				break;
			}
		} while (false);		
		return event2Disco;
	}
	/**
	 * 
	 * @param discoId
	 * @param agentId
	 * @param matchAgent
	 * @param ts
	 * @return
	 * @throws RMapObjectNotFoundException
	 */
	protected Map<URI,URI> lookFoward(URI discoId, URI agentId, boolean matchAgent, 
			ORMapEventMgr eventmgr, SesameTriplestore ts) throws RMapObjectNotFoundException{
		Map<URI,URI> event2Disco = new HashMap<URI,URI>();			
		do {
			List<Statement> eventStmts = eventmgr.getUpdateEvents(discoId, ts);
			if (eventStmts==null || eventStmts.size()==0){
				break;
			}
			// get created objects from update event, and find the DiSCO
			for (Statement eventStmt:eventStmts){
				URI updateEventId = (URI) eventStmt.getSubject();
				// confirm matching agent if necessary	
				if (matchAgent){
					URI uAgent = eventmgr.getEventAssocAgent(updateEventId, ts);
					if (!(agentId.equals(uAgent))){
						continue;
					}	
				}
				// get id of new DiSCO
				URI newDisco = eventmgr.getIdOfCreatedDisco(updateEventId, ts);
				if (newDisco != null && !newDisco.equals(discoId)){
					event2Disco.put(updateEventId,newDisco);
					// follow new DiSCO forward
					event2Disco.putAll(lookFoward(newDisco,agentId,matchAgent,eventmgr,ts));
				}
			}				
		} while (false);			 
		return event2Disco;
	}	
	
	

	
	/**
	 * Get ids of any Agents that asserted a DiSCO i.e isAssociatedWith the create or derive event
	 * @param uri ID of DiSCO
	 * @param statusCode
	 * @param eventMgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapDiSCONotFoundException
	 * @throws RMapObjectNotFoundException
	 */
	public Set<URI> getAssertingAgents(URI uri, RMapStatus statusCode, ORMapEventMgr eventMgr, SesameTriplestore ts) 
	throws RMapException, RMapDiSCONotFoundException, RMapObjectNotFoundException {
		Set<URI>agents = new HashSet<URI>();
		do {
			//don't get agent if DiSCO status doesn't match OR the DiSCO should be hidden from public view.
			RMapStatus dStatus = this.getDiSCOStatus(uri, ts);
			if ((statusCode != null && !dStatus.equals(statusCode))
					|| dStatus.equals(RMapStatus.DELETED)
					|| dStatus.equals(RMapStatus.TOMBSTONED)){
					break;
			}
			
			List<URI> events = eventMgr.getMakeObjectEvents(uri, ts);
			
           //For each event associated with DiSCOID, return AssociatedAgent
			for (URI event:events){
				URI assocAgent = eventMgr.getEventAssocAgent(event, ts);
				if (assocAgent!=null) {
					agents.add(assocAgent);
				}
			}
		} while (false);		
		return agents;
	}
	
	
	/**
	 * Get ids of any Agents associated with a DiSCO
	 * @param uri ID of DiSCO
	 * @param statusCode
	 * @param eventMgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapDiSCONotFoundException
	 * @throws RMapObjectNotFoundException
	 */
	public Set<URI> getRelatedAgents(URI uri, RMapStatus statusCode, ORMapEventMgr eventMgr, SesameTriplestore ts) 
	throws RMapException, RMapDiSCONotFoundException, RMapObjectNotFoundException {
		Set<URI>agents = new HashSet<URI>();
		do {
			if (statusCode != null){
				RMapStatus dStatus = this.getDiSCOStatus(uri, ts);
				if (!(dStatus.equals(statusCode))){
					break;
				}
			}
			List<URI>events = eventMgr.getDiscoRelatedEventIds(uri, ts);
           //For each event associated with DiSCOID, return AssociatedAgent
			for (URI event:events){
				URI assocAgent = eventMgr.getEventAssocAgent(event, ts);
				agents.add(assocAgent);
			}
			//TODO  ask:  do we want these?
			List<Statement> dStatements = this.getNamedGraph(uri, ts);
			//	 For each statement in the Disco, find any agents referenced
			for (Statement stmt:dStatements){
				Resource subject = stmt.getSubject();
				if (subject instanceof URI){
					if (this.isAgentId((URI)subject,ts)){
						agents.add((URI) subject);
					}
				}
				Value object = stmt.getObject();
				if (object instanceof URI){
					if (this.isAgentId((URI)object,ts)){
						agents.add((URI) object);
					}
				}
			}
		} while (false);		
		return agents;
	}
	/**
	 * Get ID (URI) of latest version of a Disco (might be same as DiSCO)
	 * @param disco ID of DiSCO whose latest version is being rquested
	 * @param eventmgr
	 * @param ts
	 * @param event2disco Map from events to all versions of DiSCOs
	 * @return id of latest version of DiSCO
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 * @throws RMapDefectiveArgumentException
	 */
	protected URI getLatestDiSCOUri(URI disco, 
			ORMapEventMgr eventmgr, SesameTriplestore ts,
			Map<URI,URI>event2disco)
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (disco ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}	
		if (event2disco==null){
			throw new RMapDefectiveArgumentException ("Null event2disco map");
		}
		URI lastEvent = 
				eventmgr.getLatestEvent(event2disco.keySet(),ts);
		URI discoId = event2disco.get(lastEvent);
		return discoId;
	}
	/**
	 * Get URI of previous version of this DiSCO
	 * @param discoID id/uri of DiSOC
	 * @param event2disco Map from events to all versions of DiSCOs
	 * @param date2event  Map from date events associated with version of DiSCO
	 * @param eventmgr
	 * @param ts
	 * @return URI of previous version of this DiSCO, or null if none found
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 * @throws RMapDefectiveArgumentException
	 */
	protected URI getPreviousURI(URI discoID, Map<URI,URI>event2disco, Map<Date, URI> date2event, 
			ORMapEventMgr eventmgr, SesameTriplestore ts)
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}	
		if (event2disco==null){
			throw new RMapDefectiveArgumentException ("Null event2disco map");
		}
		Map<URI,URI> disco2event = 
				Utils.invertMap(event2disco);
		
		if (date2event==null){
			date2event = eventmgr.getDate2EventMap(event2disco.keySet(),ts);
		}
		
		Map<URI,Date> event2date = Utils.invertMap(date2event);
		
		URI discoEventId = disco2event.get(discoID);
		Date eventDate = event2date.get(discoEventId);
		
		SortedSet<Date> sortedDates = new TreeSet<Date>();
		sortedDates.addAll(date2event.keySet());
		SortedSet<Date>earlierDates = sortedDates.headSet(eventDate);
		URI prevDiscoId = null;
		if (earlierDates.size()>0){
			Date previousDate = earlierDates.last()	;
			URI prevEventId = date2event.get(previousDate);
			prevDiscoId = event2disco.get(prevEventId);
		}
		return prevDiscoId;
	}
	/**
	 * Get URI of next version of a DiSCO
	 * @param discoID  id/uri of DISCO
	 * @param event2disco Map from events to all versions of DiSCOs
	 * @param date2event  Map from date events associated with version of DiSCO
	 * @param eventmgr
	 * @param ts
	 * @return URI of next version of DiSCO, or null if none found
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 * @throws RMapDefectiveArgumentException
	 */
	protected URI getNextURI(URI discoID, Map<URI,URI>event2disco, Map<Date, URI> date2event, 
			ORMapEventMgr eventmgr, SesameTriplestore ts)
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}	
		if (event2disco==null){
			throw new RMapDefectiveArgumentException ("Null event2disco map");
		}
		Map<URI,URI> disco2event = 
				Utils.invertMap(event2disco);		
		if (date2event==null){
			date2event = eventmgr.getDate2EventMap(event2disco.keySet(),ts);
		}		
		Map<URI,Date> event2date = Utils.invertMap(date2event);		
		URI discoEventId = disco2event.get(discoID);
		Date eventDate = event2date.get(discoEventId);
		SortedSet<Date> sortedDates = new TreeSet<Date>();
		sortedDates.addAll(date2event.keySet());
		SortedSet<Date> laterDates = sortedDates.tailSet(eventDate);
		URI nextDiscoId = null;
		if (laterDates.size()>1){
			Date[] dateArray = laterDates.toArray(new Date[laterDates.size()]);	
			URI nextEventId = date2event.get(dateArray[1]);
			nextDiscoId = event2disco.get(nextEventId);
		}
		return nextDiscoId;
	}

}

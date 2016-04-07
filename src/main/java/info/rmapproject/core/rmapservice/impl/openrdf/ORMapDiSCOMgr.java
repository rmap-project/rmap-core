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
import info.rmapproject.core.exception.RMapInactiveVersionException;
import info.rmapproject.core.exception.RMapNotLatestVersionException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapTombstonedObjectException;
import info.rmapproject.core.idservice.IdService;
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
import info.rmapproject.core.model.request.RMapRequestAgent;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.utils.Utils;
import info.rmapproject.core.vocabulary.impl.openrdf.PROV;
import info.rmapproject.core.vocabulary.impl.openrdf.RMAP;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openrdf.model.BNode;
import org.openrdf.model.IRI;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class that creates actual triples for DiSCO, related Events, and
 * Statements in the tripleStore;
 * 
 *  @author khanson, smorrissey
 *
 */
public class ORMapDiSCOMgr extends ORMapObjectMgr {
	
	private IdService rmapIdService;
	private ORMapAgentMgr agentmgr;
	private ORMapEventMgr eventmgr;
	
	@Autowired
	public ORMapDiSCOMgr(IdService rmapIdService, ORMapAgentMgr agentmgr, ORMapEventMgr eventmgr) throws RMapException {
		super();
		this.rmapIdService = rmapIdService;
		this.agentmgr = agentmgr;
		this.eventmgr = eventmgr;
	}
	
	
	/**
	 * Return DiSCO DTO corresponding to discoID
	 * @param discoID
	 * @param getLinks 
	 * @param event2disco 
	 * @param date2event 
	 * @param ts
	 * @return
	 * @throws RMapTombstonedObjectException
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapObjectNotFoundException 
	 * @throws RMapException 
	 */
	public ORMapDiSCODTO readDiSCO(IRI discoID, boolean getLinks, Map<IRI, IRI> event2disco, Map<Date, IRI> date2event, SesameTriplestore ts) 
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
		
		ORMapDiSCODTO dto = new ORMapDiSCODTO(ts);
		dto.setDisco(disco);
		dto.setStatus(status);
		
		if (getLinks){
			// get latest version of this DiSCO
			if (event2disco==null){
				event2disco = this.getAllDiSCOVersions(discoID,true,ts);
			}
			dto.setLatest(this.getLatestDiSCOIri(discoID, ts, event2disco));
			if (date2event==null){
				date2event = eventmgr.getDate2EventMap(event2disco.keySet(),ts);
			}
			// get next version of this DiSCO
			IRI nextDiscoId = this.getNextIRI(discoID, event2disco, date2event, ts);
			dto.setNext(nextDiscoId);			
			// get previous version of this DiSCO
			IRI prevDiscoId = this.getPreviousIRI(discoID, event2disco, date2event, ts);
			dto.setPrevious(prevDiscoId);
		}
		return dto;		
	}
	
	
	/**
	 * 
	 * @param systemAgentId
	 * @param disco
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapAgentNotFoundException 
	 */
	public ORMapEvent createDiSCO(ORMapDiSCO disco, RMapRequestAgent requestAgent, SesameTriplestore ts) 
			throws RMapException, RMapAgentNotFoundException, RMapDefectiveArgumentException{		
		// confirm non-null disco
		if (disco==null){
			throw new RMapException ("Null disco parameter");
		}
		if (requestAgent==null){
			throw new RMapException("Request Agent required: value was null");
		}
		
		agentmgr.validateRequestAgent(requestAgent, ts);
		
		// get the event started
		ORMapEventCreation event = new ORMapEventCreation(requestAgent, RMapEventTargetType.DISCO);
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
		Set<IRI> created = new HashSet<IRI>();
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
		
		// create triple for prov generated by IRI
		if (disco.getProvGeneratedBy()!= null){
			this.createTriple(ts,disco.getProvGeneratedByStmt());
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
		
		// end the event, write the event triples, and commit everything
		// update the event with created object IDS
		event.setCreatedObjectIdsFromIRI(created);		
		event.setEndTime(new Date());
		eventmgr.createEvent(event, ts);

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
	 * @param ts
	 * @return
	 * @throws RMapDefectiveArgumentException
	 * @throws RMapAgentNotFoundException
	 * @throws RMapException
	 */
	//try to roll this back!
	public RMapEvent updateDiSCO(IRI oldDiscoId, ORMapDiSCO disco, RMapRequestAgent requestAgent,  boolean justInactivate, SesameTriplestore ts) 
	throws RMapDefectiveArgumentException, RMapAgentNotFoundException, RMapException {
		// confirm non-null old disco
		if (oldDiscoId==null){
			throw new RMapDefectiveArgumentException ("Null value for id of target DiSCO");
		}
		// Confirm systemAgentId (not null, is Agent)
		if (requestAgent==null){
			throw new RMapException("System Agent ID required: was null");
		}		
		agentmgr.validateRequestAgent(requestAgent, ts);
		
		//check that they are updating the latest version of the DiSCO otherwise throw exception
		Map<IRI,IRI>event2disco=this.getAllDiSCOVersions(oldDiscoId, true, ts);
		IRI latestDiscoIri = this.getLatestDiSCOIri(oldDiscoId, ts, event2disco);
		if (!latestDiscoIri.stringValue().equals(oldDiscoId.stringValue())){
			//NOTE:the IRI of the latest DiSCO should always appear in angle brackets at the end of the message
			//so that it can be parsed as needed
			throw new RMapNotLatestVersionException("The DiSCO '" + oldDiscoId.toString() + "' has a newer version. "
									+ "Only the latest version of the DiSCO can be updated. The latest version can be found at "
									+ "<" + latestDiscoIri.stringValue() +">");
		}		
		
		//check that they are updating an active DiSCO
		if (getDiSCOStatus(oldDiscoId,ts)!=RMapStatus.ACTIVE) {
			throw new RMapInactiveVersionException("The DiSCO '" + oldDiscoId.toString() + "' is inactive. "
									+ "Only active DiSCOs can be updated.");				
		}
		
		
		// get the event started
		ORMapEvent event = null;	
		boolean creatorSameAsOrig = this.isSameCreatorAgent(oldDiscoId, requestAgent, ts);
				
		if (justInactivate){
			// must be same agent
			if (creatorSameAsOrig){
				ORMapEventInactivation iEvent = new ORMapEventInactivation(requestAgent, RMapEventTargetType.DISCO);

				ORAdapter typeAdapter = new ORAdapter(ts);
				iEvent.setInactivatedObjectId(typeAdapter.openRdfIri2RMapIri(oldDiscoId));
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
			if (creatorSameAsOrig){
				ORMapEventUpdate uEvent = new ORMapEventUpdate(requestAgent, RMapEventTargetType.DISCO, oldDiscoId, disco.getDiscoContext());
				event = uEvent;
			}
			else {
				ORMapEventDerivation dEvent = new ORMapEventDerivation(requestAgent, RMapEventTargetType.DISCO, oldDiscoId, disco.getDiscoContext());
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
			Set<IRI> created = new HashSet<IRI>();			
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
			
			// create  reified statement for prov generated by IRI
			if (disco.getProvGeneratedBy()!= null){
				this.createTriple(ts,disco.getProvGeneratedByStmt());
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
				((ORMapEventWithNewObjects)event).setCreatedObjectIdsFromIRI(created);		
			}
		} while (false);
			
		// end the event, write the event triples, and commit everything
		event.setEndTime(new Date());
		eventmgr.createEvent(event, ts);
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
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapAgentNotFoundException 
	 */
	public RMapEvent tombstoneDiSCO(IRI oldDiscoId, RMapRequestAgent requestAgent, SesameTriplestore ts) 
	throws RMapException, RMapAgentNotFoundException, RMapDefectiveArgumentException {
		// confirm non-null old disco
		if (oldDiscoId==null){
			throw new RMapException ("Null value for id of DiSCO to be tombstoned");
		}
		if (requestAgent==null){
			throw new RMapException("System Agent ID required: was null");
		}
				
		//validate agent
		agentmgr.validateRequestAgent(requestAgent, ts);
		
		// make sure same Agent created the DiSCO now being inactivated
		if (! this.isSameCreatorAgent(oldDiscoId, requestAgent, ts)){
			throw new RMapException(
					"Agent attempting to tombstone DiSCO is not same as its creating Agent");
		}
			
		// get the event started
		ORMapEventTombstone event = new ORMapEventTombstone(requestAgent, RMapEventTargetType.DISCO, oldDiscoId);

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
		eventmgr.createEvent(event, ts);

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
	public RMapStatus getDiSCOStatus(IRI discoId, SesameTriplestore ts) 
			throws RMapDiSCONotFoundException, RMapException {
		RMapStatus status = null;
		if (discoId==null){
			throw new RMapException ("Null disco");
		}
		// first ensure Exists statement IRI rdf:TYPE rmap:DISCO  if not: raise NOTFOUND exception
		if (! this.isDiscoId(discoId, ts)){
			throw new RMapDiSCONotFoundException ("No DisCO found with id " + discoId.stringValue());
		}
		do {
			List<Statement> eventStmts = null;
			try {
				//   ? RMap:Deletes discoId  done return deleted
				eventStmts = ts.getStatements(null, RMAP.DELETEDOBJECT, discoId);
				if (eventStmts!=null && ! eventStmts.isEmpty()){
					status = RMapStatus.DELETED;
					break;
				}
				//   ? RMap:TombStones discoID	done return tombstoned
				eventStmts = ts.getStatements(null, RMAP.TOMBSTONEDOBJECT, discoId);
				if (eventStmts!=null && ! eventStmts.isEmpty()){
					status = RMapStatus.TOMBSTONED;
					break;
				}
				//   ? RMap:Updates discoID	done return Inactive
				eventStmts = ts.getStatements(null, RMAP.INACTIVATEDOBJECT, discoId);
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
	 * @return Map from IRI of an Event to the IRI of DiSCO created by event, either as creation,
	 *  update, or derivation
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 */
	public Map<IRI,IRI> getAllDiSCOVersions(IRI discoId, boolean matchAgent, SesameTriplestore ts) 
			throws RMapObjectNotFoundException, RMapException {
		if (discoId==null){
			throw new RMapException ("Null disco");
		}
		if (! this.isDiscoId(discoId, ts)){
			throw new RMapDiSCONotFoundException("No disco found with identifer " + 
					discoId.stringValue());
		}
		Map<IRI,IRI> event2Disco = lookBack(discoId, null, true, matchAgent, ts);		
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
	protected Map<IRI,IRI> lookBack(IRI discoId, IRI agentId, boolean lookFoward, boolean matchAgent, SesameTriplestore ts) 
					throws RMapObjectNotFoundException, RMapException {
		Statement eventStmt = eventmgr.getRMapObjectCreateEventStatement(discoId, ts);
		if (eventStmt==null){
			throw new RMapEventNotFoundException("No creating event found for DiSCO id " +
		             discoId.stringValue());
		}
		Map<IRI,IRI> event2Disco = new HashMap<IRI,IRI>();
		do {
			IRI eventId = (IRI)eventStmt.getSubject();
			IRI oldAgentId = agentId;
			if (matchAgent){
				// first time through, agentID will be null
				if (agentId==null){
					oldAgentId = eventmgr.getEventAssocAgent(eventId, ts);
				}
				IRI uAgent = eventmgr.getEventAssocAgent(eventId, ts);
				if (!(oldAgentId.equals(uAgent))){
					break;
				}
			}
			event2Disco.put(eventId,discoId);			
			if(eventmgr.isCreationEvent(eventId, ts)){					
				if (lookFoward){
					event2Disco.putAll(this.lookFoward(discoId, oldAgentId, matchAgent,ts));
				}
				break;
			}
			if ((eventmgr.isUpdateEvent(eventId, ts)) || (eventmgr.isDerivationEvent(eventId, ts))){
				// get id of old DiSCO
				IRI oldDiscoID = eventmgr.getIdOfOldDisco(eventId, ts);
				if (oldDiscoID==null){
					throw new RMapDiSCONotFoundException("Event " + eventId.stringValue() + 
							" does not have Derived Object DiSCO for DISCO " + discoId.stringValue());
				}
				// look back recursively on create/updates for oldDiscoID
				// DONT look forward on the backward search - you'll already have stuff
				 event2Disco.putAll(this.lookBack(oldDiscoID, oldAgentId, false, matchAgent, ts));
				// now look ahead for any derived discos
				 event2Disco.putAll(this.lookFoward(discoId, oldAgentId, matchAgent, ts));
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
	protected Map<IRI,IRI> lookFoward(IRI discoId, IRI agentId, boolean matchAgent, SesameTriplestore ts) throws RMapObjectNotFoundException{
		Map<IRI,IRI> event2Disco = new HashMap<IRI,IRI>();			
		do {
			List<Statement> eventStmts = eventmgr.getUpdateEvents(discoId, ts);
			if (eventStmts==null || eventStmts.size()==0){
				break;
			}
			// get created objects from update event, and find the DiSCO
			for (Statement eventStmt:eventStmts){
				IRI updateEventId = (IRI) eventStmt.getSubject();
				// confirm matching agent if necessary	
				if (matchAgent){
					IRI uAgent = eventmgr.getEventAssocAgent(updateEventId, ts);
					if (!(agentId.equals(uAgent))){
						continue;
					}	
				}
				// get id of new DiSCO
				IRI newDisco = eventmgr.getIdOfCreatedDisco(updateEventId, ts);
				if (newDisco != null && !newDisco.equals(discoId)){
					event2Disco.put(updateEventId,newDisco);
					// follow new DiSCO forward
					event2Disco.putAll(lookFoward(newDisco,agentId,matchAgent,ts));
				}
			}				
		} while (false);			 
		return event2Disco;
	}	
	
	

	
	/**
	 * Get ID of Agent that asserted a DiSCO i.e isAssociatedWith the create or derive event
	 * @param iri ID of DiSCO
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapDiSCONotFoundException
	 * @throws RMapObjectNotFoundException
	 */
	public IRI getDiSCOAssertingAgent(IRI discoIri, SesameTriplestore ts) 
			throws RMapException, RMapDiSCONotFoundException, RMapObjectNotFoundException {
		IRI assocAgent = null;
		
		List<IRI> events = eventmgr.getMakeObjectEvents(discoIri, ts);
		if (events.size()>0){
			//any event from this list will do, should only be one and if there is more than one they should have same agent	
			IRI eventIri = events.get(0);
			assocAgent = eventmgr.getEventAssocAgent(eventIri, ts);
		}
		
		return assocAgent;
	}
	
	
	/**
	 * Get ids of any Agents associated with a DiSCO
	 * @param iri ID of DiSCO
	 * @param statusCode
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapDiSCONotFoundException
	 * @throws RMapObjectNotFoundException
	 */
	public Set<IRI> getRelatedAgents(IRI iri, RMapStatus statusCode, SesameTriplestore ts) 
	throws RMapException, RMapDiSCONotFoundException, RMapObjectNotFoundException {
		Set<IRI>agents = new HashSet<IRI>();
		do {
			if (statusCode != null){
				RMapStatus dStatus = this.getDiSCOStatus(iri, ts);
				if (!(dStatus.equals(statusCode))){
					break;
				}
			}
			Set<IRI>events = eventmgr.getDiscoRelatedEventIds(iri, ts);
           //For each event associated with DiSCOID, return AssociatedAgent
			for (IRI event:events){
				IRI assocAgent = eventmgr.getEventAssocAgent(event, ts);
				agents.add(assocAgent);
			}
			//TODO  ask:  do we want these?
			List<Statement> dStatements = this.getNamedGraph(iri, ts);
			//	 For each statement in the Disco, find any agents referenced
			for (Statement stmt:dStatements){
				Resource subject = stmt.getSubject();
				if (subject instanceof IRI){
					if (this.isAgentId((IRI)subject,ts)){
						agents.add((IRI) subject);
					}
				}
				Value object = stmt.getObject();
				if (object instanceof IRI){
					if (this.isAgentId((IRI)object,ts)){
						agents.add((IRI) object);
					}
				}
			}
		} while (false);		
		return agents;
	}
	/**
	 * Get ID (IRI) of latest version of a Disco (might be same as DiSCO)
	 * @param disco ID of DiSCO whose latest version is being rquested
	 * @param ts
	 * @param event2disco Map from events to all versions of DiSCOs
	 * @return id of latest version of DiSCO
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 * @throws RMapDefectiveArgumentException
	 */
	protected IRI getLatestDiSCOIri(IRI disco, SesameTriplestore ts, Map<IRI,IRI>event2disco)
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (disco ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}	
		if (event2disco==null){
			throw new RMapDefectiveArgumentException ("Null event2disco map");
		}
		IRI lastEvent = eventmgr.getLatestEvent(event2disco.keySet(),ts);
		IRI discoId = event2disco.get(lastEvent);
		return discoId;
	}
	/**
	 * Get IRI of previous version of this DiSCO
	 * @param discoID id/IRI of DiSCO
	 * @param event2disco Map from events to all versions of DiSCOs
	 * @param date2event  Map from date events associated with version of DiSCO
	 * @param ts
	 * @return IRI of previous version of this DiSCO, or null if none found
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 * @throws RMapDefectiveArgumentException
	 */
	protected IRI getPreviousIRI(IRI discoID, Map<IRI,IRI>event2disco, Map<Date, IRI> date2event, SesameTriplestore ts)
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}	
		if (event2disco==null){
			throw new RMapDefectiveArgumentException ("Null event2disco map");
		}
		Map<IRI,IRI> disco2event = 
				Utils.invertMap(event2disco);
		
		if (date2event==null){
			date2event = eventmgr.getDate2EventMap(event2disco.keySet(),ts);
		}
		
		Map<IRI,Date> event2date = Utils.invertMap(date2event);
		
		IRI discoEventId = disco2event.get(discoID);
		Date eventDate = event2date.get(discoEventId);
		
		SortedSet<Date> sortedDates = new TreeSet<Date>();
		sortedDates.addAll(date2event.keySet());
		SortedSet<Date>earlierDates = sortedDates.headSet(eventDate);
		IRI prevDiscoId = null;
		if (earlierDates.size()>0){
			Date previousDate = earlierDates.last()	;
			IRI prevEventId = date2event.get(previousDate);
			prevDiscoId = event2disco.get(prevEventId);
		}
		return prevDiscoId;
	}
	
	/**
	 * Get IRI of next version of a DiSCO
	 * @param discoID  id/IRI of DISCO
	 * @param event2disco Map from events to all versions of DiSCOs
	 * @param date2event  Map from date events associated with version of DiSCO
	 * @param ts
	 * @return IRI of next version of DiSCO, or null if none found
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 * @throws RMapDefectiveArgumentException
	 */
	protected IRI getNextIRI(IRI discoID, Map<IRI,IRI>event2disco, Map<Date, IRI> date2event, SesameTriplestore ts)
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		if (discoID ==null){
			throw new RMapDefectiveArgumentException ("null DiSCO id");
		}	
		if (event2disco==null){
			throw new RMapDefectiveArgumentException ("Null event2disco map");
		}
		Map<IRI,IRI> disco2event = 
				Utils.invertMap(event2disco);		
		if (date2event==null){
			date2event = eventmgr.getDate2EventMap(event2disco.keySet(),ts);
		}		
		Map<IRI,Date> event2date = Utils.invertMap(date2event);		
		IRI discoEventId = disco2event.get(discoID);
		Date eventDate = event2date.get(discoEventId);
		SortedSet<Date> sortedDates = new TreeSet<Date>();
		sortedDates.addAll(date2event.keySet());
		SortedSet<Date> laterDates = sortedDates.tailSet(eventDate);
		IRI nextDiscoId = null;
		if (laterDates.size()>1){
			Date[] dateArray = laterDates.toArray(new Date[laterDates.size()]);	
			IRI nextEventId = date2event.get(dateArray[1]);
			nextDiscoId = event2disco.get(nextEventId);
		}
		return nextDiscoId;
	}

	/**
	 * Replaces any occurrences of BNodes in list of statements with RMapidentifier IRIs
	 * @param stmts
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected List<Statement> replaceBNodeWithRMapId(List<Statement> stmts, SesameTriplestore ts) throws RMapException {
		if (stmts==null){
			throw new RMapException ("null stmts");
		}
		ORAdapter typeAdapter = new ORAdapter(ts);
		List<Statement>newStmts = new ArrayList<Statement>();
		Map<BNode, IRI> bnode2iri = new HashMap<BNode, IRI>();
		for (Statement stmt:stmts){
			Resource subject = stmt.getSubject();
			Value object = stmt.getObject();
			BNode bSubject = null;
			BNode bObject = null;
			if (subject instanceof BNode ) {
				bSubject = (BNode)subject;				
			}
			if (object instanceof BNode){
				bObject = (BNode)object;
			}
			if (bSubject==null && bObject==null){
				newStmts.add(stmt);
				continue;
			}
			Resource newSubject = null;
			Value newObject = null;
			// if subject is BNODE, replace with IRI (if necessary, create the IRI and add mapping)
			if (bSubject != null){
				IRI bReplace = bnode2iri.get(bSubject);
				if (bReplace==null){
					java.net.URI newId=null;
					try {
						newId = rmapIdService.createId();
					} catch (Exception e) {
						throw new RMapException (e);
					}

					bReplace = typeAdapter.uri2OpenRdfIri(newId);
					bnode2iri.put(bSubject, bReplace);
					newSubject = bReplace;
				}
				else {
					newSubject = bReplace;
				}
			}
			else {
				newSubject = subject;
			}
			// if object is BNODE, replace with IRI (if necessary, create the IRI and add mapping)
			if (bObject != null){
				IRI bReplace = bnode2iri.get(bObject);
				if (bReplace==null){
					java.net.URI newId=null;
					try {
						newId = rmapIdService.createId();
					} catch (Exception e) {
						throw new RMapException (e);
					}
					bReplace = typeAdapter.uri2OpenRdfIri(newId);
					bnode2iri.put(bObject, bReplace);
					newObject = bReplace;
				}
				else {
					newObject = bReplace;
				}
			}
			else {
				newObject = object;
			}
			// now create new statement with bnodes replaced
			Statement newStmt=null;
			try {
				newStmt = ts.getValueFactory().createStatement(newSubject, stmt.getPredicate(), newObject, stmt.getContext());
			} catch (RepositoryException e) {
				e.printStackTrace();
				throw new RMapException (e);
			}
			newStmts.add(newStmt);
			continue;
		}
		return newStmts;
	}
	
	/**
	 * Confirm 2 identifiers refer to the same creating agent - Agents can only update own DiSCO
	 * @param discoIri
	 * @param systemAgentId
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected boolean isSameCreatorAgent (IRI discoIri, RMapRequestAgent requestAgent, SesameTriplestore ts) 
			throws RMapException {
		boolean isSame = false;		
		Statement stmt = eventmgr.getRMapObjectCreateEventStatement(discoIri, ts);
		do {
			if (stmt==null){
				break;
			}
			if (! (stmt.getSubject() instanceof IRI)){
				throw new RMapException ("Event ID is not IRI: " + stmt.getSubject().stringValue());
			}
			IRI eventId = (IRI)stmt.getSubject();
			IRI createAgent = eventmgr.getEventAssocAgent(eventId, ts);
			String iriSysAgent = requestAgent.getSystemAgent().toString();			
			isSame = (iriSysAgent.equals(createAgent.stringValue()));
		}while (false);
		return isSame;
	}
}

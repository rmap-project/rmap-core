/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapAgent;
import info.rmapproject.core.model.RMapEvent;
import info.rmapproject.core.model.RMapEventTargetType;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.model.impl.openrdf.ORMapEvent;
import info.rmapproject.core.model.impl.openrdf.ORMapEventCreation;
import info.rmapproject.core.model.impl.openrdf.ORMapEventTombstone;
import info.rmapproject.core.model.impl.openrdf.ORMapEventUpdate;
import info.rmapproject.core.model.impl.openrdf.ORMapStatement;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

/**
 * Class that creates actual triples for DiSCO, and related Events, Agents, and
 * reified RMapStatements in the tripleStore;
 * @author smorrissey
 *
 */
public class ORMapDiSCOMgr extends ORMapObjectMgr {

	public static List<URI> agentRelations;

	static {
		agentRelations = new ArrayList<URI>();
		agentRelations.add(DC.CREATOR);
		agentRelations.add(DC.CONTRIBUTOR);
		agentRelations.add(DCTERMS.CONTRIBUTOR);
		agentRelations.add(DCTERMS.CREATOR);
		agentRelations.add(DCTERMS.AGENT);
		agentRelations.add(DCTERMS.PUBLISHER);
	}

	/**
	 * 
	 */
	public ORMapDiSCOMgr() {
		super();
	}
	/**
	 * Return DiSCO corresponding to discoID
	 * @param discoID
	 * @param ts
	 * @return
	 * @throws RMapObjectNotFoundException
	 */
	public ORMapDiSCO readDiSCO(URI discoID, SesameTriplestore ts) 
	throws RMapObjectNotFoundException {
		ORMapDiSCO disco = null;
		if (discoID ==null){
			throw new RMapException ("null discoID");
		}
		if (ts==null){
			throw new RMapException("null triplestore");
		}
		List<Statement> discoStmts = this.getNamedGraph(discoID, ts);
		
		disco = new ORMapDiSCO(discoStmts);
		return disco;
	}
	
	/**
	 *
	 * @param systemAgentId
	 * @param stmts
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public RMapEvent createDiSCO(org.openrdf.model.URI systemAgentId,
			List<Statement> stmts, SesameTriplestore ts) throws RMapException {
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
		return this.createDiSCO(systemAgentId, disco, ts);
	}
	
	/**
	 * Create a new DiSCO
	 * @param systemAgentId
	 * @param disco
	 * @param ts 
	 * @return
	 * @throws RMapException
	 */
	public ORMapEvent createDiSCO(URI systemAgentId, ORMapDiSCO disco, SesameTriplestore ts) 
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
			throw new RMapObjectNotFoundException("No agent with id " + systemAgentId.stringValue());
		}
		// get the event started
		ORMapEventCreation event = new ORMapEventCreation(systemAgentId, RMapEventTargetType.DISCO);
		// Create reified statements for aggregrated resources if needed
		List<ORMapStatement> aggResources = disco.getAggregatedResourceStatements();
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
		// create any new reified statements needed, and triples for all statements in DiSCO
		ORMapStatementMgr stmtMgr = new ORMapStatementMgr();
		// Keep track of resources created by this Event
		// use Set, not list in case of duplicate statement IDs within transaction
		Set<URI> created = new HashSet<URI>();
		// add the DiSCO id as an event-created Resource
		created.add(disco.getDiscoContext());
		// Create reified statment for type statement if necessary, and add the triple
		URI typeId = stmtMgr.createStatement(disco.getTypeStatement(), ts);
		if (typeId != null){
			created.add(typeId);
		}
		this.createTriple(ts, disco.getTypeStatement());
		// Create reified statement for discoCreater if necessary, and add the triple
		URI discoCreator = stmtMgr.createStatement(disco.getCreatorStmt(), ts);
		if (discoCreator != null){
			created.add(discoCreator);
		}
		this.createTriple(ts, disco.getCreatorStmt());
		// Create new Agent for discoCreater if necessary, and add the triple(s)
		// TODO

		// Create reified statement for description if necessary, and add the triple
		URI desc = stmtMgr.createStatement(disco.getDescriptonStatement(), ts);
		if (desc != null){
			created.add(desc);
		}
		this.createTriple(ts, disco.getDescriptonStatement());
		// create reified statement for disco id the provider used
		if (disco.getProviderIdStmt()!= null){
			URI providerId = stmtMgr.createStatement(disco.getProviderIdStmt(),ts);
			if (providerId != null){
				created.add(providerId);
			}
		}
		// for each aggregated resource, create reified statement if necessary, create triples
		for (ORMapStatement stmt:aggResources){
			URI aggResource = stmtMgr.createStatement(stmt, ts);
			if (aggResource != null){
				created.add(aggResource);
			}
			this.createTriple(ts, stmt);
		}
		// for each statement in relatedStatements
		//   create reified statement if necessary, and add the triple
		//   if dct:create or dc:creator create agent, agent profile as needed,and add the triple
		for (ORMapStatement stmt:disco.getRelatedStatementsAsStatements()){
			URI relStmt = stmtMgr.createStatement(stmt, ts);
			if (relStmt != null){
				created.add(relStmt);
			}
			this.createTriple(ts, stmt);
			URI predicate = stmt.getRmapStmtPredicate();
			if (agentRelations.contains(predicate)){
				// see if you need to create or update Agent here
				//TODO
			}
		}		
		// update the event with created object IDS
		event.setCreatedObjectIdsFromURI(created);		
		// end the event, write the event triples, and commit everything
		event.setEndTime(new Date());
		ORMapEventMgr eventMgr = new ORMapEventMgr();
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
	 * Update: inactivate an existing DiSCO; if new DiSCO provided, also update
	 * by creating new DiSCO
	 * @param systemAgentId
	 * @param oldDiscoId
	 * @param disco
	 * @param ts
	 * @return
	 */
	public RMapEvent updateDiSCO(URI systemAgentId,
			URI oldDiscoId, ORMapDiSCO disco,
			SesameTriplestore ts) {
		// confirm non-null old disco
		if (oldDiscoId==null){
			throw new RMapException ("Null value for id of target DiSCO");
		}
		// Confirm systemAgentId (not null, is Agent)
		if (systemAgentId==null){
			throw new RMapException("System Agent ID required: was null");
		}		
		if (! this.isAgentId(systemAgentId, ts)){
			throw new RMapObjectNotFoundException("No agent with id " + systemAgentId.stringValue());
		}
		// if Disco, cannot have null agg resources
		List<ORMapStatement> aggResources = null;
		if (disco != null){
			aggResources = disco.getAggregatedResourceStatements();
			if (aggResources == null){
				throw new RMapException("Null aggregated resources in DiSCO");
			}
		}				
		// get the event started
		ORMapEventUpdate event = new ORMapEventUpdate(systemAgentId, RMapEventTargetType.DISCO, oldDiscoId);	
		//if Update is different Agent then DO NOT inactivate target object; new Disco is a derivation
		if (this.isSameDiscoAgent(oldDiscoId, systemAgentId, ts)){
			event.inactivateTarget();
			if (disco!=null){
				event.deriveFromTarget(ORAdapter.openRdfUri2RMapUri(disco.getDiscoContext()));
			}
		}
		else {
			// but if it is a new Agent, MUST have new DiSCO, or else it's an illegal attempted update
			if (disco==null){
				throw new RMapException("No new DiSCO provided; Agent is not the same as creating agent; " +
						" cannot inactivate another agent's DiSCO");
			}
			else {
				event.deriveFromTarget(ORAdapter.openRdfUri2RMapUri(disco.getDiscoContext()));
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
			// create any new reified statements needed, and triples for all statements in DiSCO
			ORMapStatementMgr stmtMgr = new ORMapStatementMgr();
			// Keep track of resources created by this Event
			// use Set, not list in case of duplicate statement IDs within transaction
			Set<URI> created = new HashSet<URI>();			
			// add the DiSCO id as an event-created Resource
			created.add(disco.getDiscoContext());
			// Create reified statment for type statement if necessary, and add the triple
			URI typeId = stmtMgr.createStatement(disco.getTypeStatement(), ts);
			if (typeId != null){
				created.add(typeId);
			}
			this.createTriple(ts, disco.getTypeStatement());
			// Create reified statement for discoCreater if necessary, and add the triple
			URI discoCreator = stmtMgr.createStatement(disco.getCreatorStmt(), ts);
			if (discoCreator != null){
				created.add(discoCreator);
			}
			this.createTriple(ts, disco.getCreatorStmt());
			// Create new Agent for discoCreater if necessary, and add the triple(s)
			// TODO

			// Create reified statement for description if necessary, and add the triple
			URI desc = stmtMgr.createStatement(disco.getDescriptonStatement(), ts);
			if (desc != null){
				created.add(desc);
			}
			this.createTriple(ts, disco.getDescriptonStatement());
			// create reified statement for disco id the provider used
			if (disco.getProviderIdStmt()!= null){
				URI providerId = stmtMgr.createStatement(disco.getProviderIdStmt(),ts);
				if (providerId != null){
					created.add(providerId);
				}
			}
			// for each aggregated resource, create reified statement if necessary, create triples
			for (ORMapStatement stmt:aggResources){
				URI aggResource = stmtMgr.createStatement(stmt, ts);
				if (aggResource != null){
					created.add(aggResource);
				}
				this.createTriple(ts, stmt);
			}
			// for each statement in relatedStatements
			//   create reified statement if necessary, and add the triple
			//   if dct:create or dc:creator create agent, agent profile as needed,and add the triple
			for (ORMapStatement stmt:disco.getRelatedStatementsAsStatements()){
				URI relStmt = stmtMgr.createStatement(stmt, ts);
				if (relStmt != null){
					created.add(relStmt);
				}
				this.createTriple(ts, stmt);
				URI predicate = stmt.getRmapStmtPredicate();
				if (agentRelations.contains(predicate)){
					// see if you need to create or update Agent here
					//TODO
				}
			}		
			// update the event with created object IDS
			event.setCreatedObjectIdsFromURI(created);		
		} while (false);
		// end the event, write the event triples, and commit everything
		event.setEndTime(new Date());
		ORMapEventMgr eventMgr = new ORMapEventMgr();
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
	 * Soft-delete a DiSCO
	 * A read of this DiSCO should return tombstone notice rather than statements in the DiSCO,
	 * but DiSCO named graph is not deleted from triplestore
	 * @param systemAgentId
	 * @param oldDiscoId
	 * @param ts
	 * @return
	 */
	public RMapEvent tombstoneDiSCO(URI systemAgentId,
			URI oldDiscoId, SesameTriplestore ts) {
		// confirm non-null old disco
		if (oldDiscoId==null){
			throw new RMapException ("Null value for id of DiSCO to be tombstoned");
		}
		if (systemAgentId==null){
			throw new RMapException("System Agent ID required: was null");
		}
		// Confirm systemAgentId (not null, is Agent)
		ORMapService service = this.getORMapService();
		RMapAgent agent = service.readAgent(ORAdapter.openRdfUri2URI(systemAgentId));
		if (agent==null){
			throw new RMapObjectNotFoundException("No agent with id " + systemAgentId.stringValue());
		}
		// make sure same Agent created the DiSCO now being inactivated
		if (! this.isSameDiscoAgent(oldDiscoId, systemAgentId, ts)){
			throw new RMapException(
					"Agent attempting to tombstoned DiSCO is not same as creating Agent");
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
		ORMapEventMgr eventMgr = new ORMapEventMgr();
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
	 * @throws RMapObjectNotFoundException
	 * @throws RMapException
	 */
	public RMapStatus getDiSCOStatus(URI discoId, SesameTriplestore ts) 
			throws RMapObjectNotFoundException, RMapException {
		RMapStatus status = null;
		if (discoId==null){
			throw new RMapException ("Null disco");
		}
		// first ensure Exists statement URI rdf:TYPE rmap:DISO  if not: raise NOTFOUND exception
		if (! this.isDiscoId(discoId, ts)){
			throw new RMapObjectNotFoundException ("No DisCO found with id " + discoId.stringValue());
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
				eventStmts = ts.getStatements(null, RMAP.EVENT_TARGET_INACTIVATED, discoId);
				if (eventStmts!=null && ! eventStmts.isEmpty()){
					status = RMapStatus.INACTIVE;
					break;
				}
			   //   else return active if create event found
				eventStmts = ts.getStatements(null, PROV.GENERATED, discoId);
				if (eventStmts!=null && ! eventStmts.isEmpty()){
					status = RMapStatus.INACTIVE;
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
	 * Get ids of all events associated with an RMapObject
	 * "Associated" means the  id is the object of one of 5 predicates in triple whose subject
	 * is an eventid.  Those predicates are:
	 * 	RMAP.EVENT_TARGET_DELETED
	 *  MAP.EVENT_TARGET_TOMBSTONED
	 *  RMAP.EVENT_TARGET_INACTIVATED
	 *  PROV.GENERATED
	 *  PROV.WASDERIVEDFROM
	 * @param id
	 * @param ts
	 * @return
	 * @throws RMapObjectNotFoundException
	 * @throws RMapException
	 */
	public List<URI> getDiscoEvents(URI id, SesameTriplestore ts) 
			throws RMapObjectNotFoundException, RMapException {
		List<URI> events = null;
		if (id==null){
			throw new RMapException ("Null disco");
		}
		// first ensure Exists statement URI rdf:TYPE rmap:DISO  if not: raise NOTFOUND exception
		if (! this.isDiscoId(id, ts)){
			throw new RMapObjectNotFoundException ("No object found with id " + id.stringValue());
		}
		do {
			List<Statement> eventStmts = new ArrayList<Statement>();
			try {
				eventStmts.addAll(ts.getStatements(null, RMAP.EVENT_TARGET_DELETED, id));
				eventStmts.addAll(ts.getStatements(null, RMAP.EVENT_TARGET_TOMBSTONED, id));
				eventStmts.addAll(ts.getStatements(null, RMAP.EVENT_TARGET, id));
				eventStmts.addAll(ts.getStatements(null, PROV.GENERATED, id));
				if (eventStmts.isEmpty()){
					break;
				}
				events = new ArrayList<URI>();
				for (Statement stmt:eventStmts){
					URI eventId = (URI)stmt.getSubject();
					if (this.isEventId(eventId,ts)){
						events.add(eventId);
					}
				}
			} catch (Exception e) {
				throw new RMapException("Exception thrown querying triplestore for events", e);
			}
		} while (false);
		return events;
	}
	/**
	 * Get ids of Statements associated with a DiSCO
	 * @param discoId
	 * @param stmtmgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getDiSCOStatements(URI discoId, ORMapStatementMgr stmtmgr,
			SesameTriplestore ts) throws RMapException {
		if (discoId==null){
			throw new RMapException("Null discoID");
		}
		if (ts==null){
			throw new RMapException("Null triplestore");
		}
		List<Statement>stmts = new ArrayList<Statement>();
		try {
			stmts.addAll(this.getNamedGraph(discoId, ts));
		}
		catch (RMapObjectNotFoundException e){}
		catch (RMapException e) {throw e;}
		List<URI>uris = new ArrayList<URI>();
		for (Statement stmt:stmts){
			URI stmtId = stmtmgr.getStatementID(stmt.getSubject(), stmt.getPredicate(),
					stmt.getObject(), ts);
			uris.add(stmtId);
		}
		return uris;
	}
	/**
	 * Method to get all versions of DiSCO
	 * If matchAgent = true, then return only versions created by same agent as creating agent
	 *                 if false, then return all versions by all agents
	 * @param discoId
	 * @param matchAgent true if searching for versions of disco by a particlar agent;
	 *                   false if searching for all versions regardless of agent
	 * @param ts triplestore
	 * @return List of DiSCO ids, or null if none found
	 * @throws RMapException
	 */
	public Map<URI,URI> getAllDiSCOVersions(URI discoId, boolean matchAgent, SesameTriplestore ts) 
			throws RMapObjectNotFoundException, RMapException {
		if (discoId==null){
			throw new RMapException ("Null disco");
		}
		if (! this.isDiscoId(discoId, ts)){
			throw new RMapObjectNotFoundException("No disco found with identifer " + 
					discoId.stringValue());
		}
		Map<URI,URI> event2Disco = lookBack(discoId, null, true, matchAgent,ts);		
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
	 */
	protected Map<URI,URI> lookBack(URI discoId, URI agentId, boolean lookFoward, 
			boolean matchAgent, SesameTriplestore ts) 
					throws RMapObjectNotFoundException, RMapException {
		Statement eventStmt = this.getDiSCOCreateEventStatement(discoId, ts);
		if (eventStmt==null){
			throw new RMapObjectNotFoundException("No creating event found for DiSCO id " +
		             discoId.stringValue());
		}
		Map<URI,URI> event2Disco = new HashMap<URI,URI>();
		do {
			URI eventId = (URI)eventStmt.getSubject();
			if (matchAgent){
				// first time through, agentID will be null
				URI oldAgentId = agentId;
				if (agentId==null){
					oldAgentId = this.getEventAssocAgent(eventId, ts);
				}
				URI uAgent = this.getEventAssocAgent(eventId, ts);
				if (!(oldAgentId.equals(uAgent))){
					break;
				}
			}
			event2Disco.put(eventId,discoId);			
			if(this.isCreationEvent(eventId, ts)){					
				if (lookFoward){
					event2Disco.putAll(this.lookFoward(discoId, agentId, matchAgent, ts));
				}
				break;
			}
			if (this.isUpdateEvent(eventId, ts)){
				// get id of old DiSCO
				ORMapEventUpdate uEvent  = this.getUpdateEvent(eventId);
				if (uEvent.getDerivationStmt().getRmapStmtObject()!=null){
					URI oldDiscoID = (URI)uEvent.getDerivationStmt().getRmapStmtObject();
					// look back recursively on create/updates for oldDiscoID
					// DONT look forward on the backward search - you'll already have stuff
					 event2Disco.putAll(this.lookBack(oldDiscoID, agentId, false, matchAgent, ts));
					// now look ahead for any derived discos
					 event2Disco.putAll(this.lookFoward(discoId, agentId, matchAgent, ts));
				}
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
	 */
	protected Map<URI,URI> lookFoward(URI discoId, URI agentId, boolean matchAgent, 
			SesameTriplestore ts) throws RMapObjectNotFoundException{
		Map<URI,URI> event2Disco = new HashMap<URI,URI>();			
		do {
			List<Statement> eventStmts = this.getUpdateEvents(discoId, ts);
			if (eventStmts==null || eventStmts.size()==0){
				break;
			}
			// get created objects from update event, and find the DiSCO
			for (Statement eventStmt:eventStmts){
				URI updateEventId = (URI) eventStmt.getSubject();
				// confirm matching agent if necessary	
				if (matchAgent){
					URI uAgent = this.getEventAssocAgent(updateEventId, ts);
					if (!(agentId.equals(uAgent))){
						continue;
					}	
				}
				// get id of new DiSCO
				URI newDisco = this.getIdOfCreatedDisco(updateEventId, ts);
				if (newDisco != null){
					event2Disco.put(updateEventId,newDisco);
					// follow new DiSCO forward
					event2Disco.putAll(lookFoward(newDisco,agentId,matchAgent,ts));
				}
			}				
		} while (false);			 
		return event2Disco;
	}
	/**
	 * 
	 * @param updateEventID
	 * @return
	 * @throws RMapException
	 */
	protected ORMapEventUpdate getUpdateEvent(URI updateEventID)
	throws RMapException {
		ORMapEventUpdate uEvent = null;
		ORMapService service = this.getORMapService();
		RMapEvent event = service.readEvent(updateEventID);
		if (event==null){
			throw new RMapObjectNotFoundException("Event id " + updateEventID.stringValue());
		}
		if (! (event instanceof ORMapEventUpdate)){
			throw new RMapException("Event is not an update event: " + updateEventID.stringValue());
		}
		uEvent = (ORMapEventUpdate)event;
		return uEvent;
	}
	/**
	 * 
	 * @param updateEventID
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected URI getIdOfCreatedDisco(URI updateEventID, SesameTriplestore ts)
	throws RMapException {
		URI discoId = null;
		ORMapEventUpdate uEvent = this.getUpdateEvent(updateEventID);
		if (uEvent.getCreatedObjectStatements() != null){
			for (ORMapStatement stmt:uEvent.getCreatedObjectStatements()){
				URI obj = (URI)stmt.getObject();
				if (this.isDiscoId(obj, ts)){
					discoId = obj;
					break;
				}
			}
		}
		return discoId;
	}

	/**
	 * Confirm 2 identifiers refer to the same agent
	 * @param oldDisco
	 * @param systemAgentId
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected boolean isSameDiscoAgent (URI oldDisco, URI systemAgentId, SesameTriplestore ts) 
			throws RMapException {
		boolean isSame = false;
		Statement stmt = this.getDiSCOCreateEventStatement(oldDisco, ts);
		do {
			if (stmt==null){
				break;
			}
			if (! (stmt.getSubject() instanceof URI)){
				throw new RMapException ("Event ID is not URI: " + stmt.getSubject().stringValue());
			}
			URI eventId = (URI)stmt.getSubject();
			URI createAgent = this.getEventAssocAgent(eventId, ts);
			isSame = (systemAgentId.equals(createAgent));
		}while (false);
		return isSame;
	}
	/**
	 * Find the creation Event associated with a DiSCO
	 * @param disco
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected Statement getDiSCOCreateEventStatement(URI disco, SesameTriplestore ts) 
	throws RMapException {
		Statement stmt = null;
		try {
			stmt = ts.getStatement(null, PROV.GENERATED, disco);
			// make sure this is an event
			if (stmt != null && stmt.getSubject().equals(stmt.getContext())){
				Statement typeStmt = ts.getStatement(stmt.getSubject(), RDF.TYPE, 
						RMAP.EVENT, stmt.getContext());
				if (typeStmt==null){
					stmt = null;
				}
			}
			else {
				stmt = null;
			}
		} catch (Exception e) {
			throw new RMapException ("Exception thrown when querying for Disco Create event", e);
		}		
		return stmt;
	}
	

}

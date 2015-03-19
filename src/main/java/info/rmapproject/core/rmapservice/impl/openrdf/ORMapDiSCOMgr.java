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
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;

import info.rmapproject.core.exception.RMapDeletedObjectException;
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

/**
 * Class that creates actual triples for DiSCO, and related Events, Agents, and
 * reified RMapStatements in the tripleStore;
 * 
 *  @author khansen, smorrissey
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
	 * @throws RMapObjectNotFoundException, RMapTombstonedObjectException, RMapDeletedObjectException
	 */
	public ORMapDiSCO readDiSCO(URI discoID, SesameTriplestore ts) 
	throws RMapObjectNotFoundException, RMapTombstonedObjectException {
		ORMapDiSCO disco = null;
		if (discoID ==null){
			throw new RMapException ("null discoID");
		}
		if (ts==null){
			throw new RMapException("null triplestore");
		}
		if (! (this.isDiscoId(discoID, ts))){
			throw new RMapObjectNotFoundException("No DiSCO with id " + discoID.stringValue());
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
		List<Statement> discoStmts = this.getNamedGraph(discoID, ts);		
		disco = new ORMapDiSCO(discoStmts);
		return disco;
	}
	
	/**
	 *
	 * @param systemAgentId
	 * @param stmts
	 * @param agentMgr 
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public RMapEvent createDiSCO(org.openrdf.model.URI systemAgentId,
			List<Statement> stmts, ORMapAgentMgr agentMgr, ORMapProfileMgr profilemgr,
			ORMapEventMgr eventMgr, SesameTriplestore ts) 
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
		return this.createDiSCO(systemAgentId, disco, eventMgr, agentMgr, profilemgr, ts);
	}
	
	/**
	 * Create a new DiSCO
	 * @param systemAgentId
	 * @param disco
	 * @param agentMgr 
	 * @param profilemgr TODO
	 * @param ts 
	 * @return
	 * @throws RMapException
	 */
	public ORMapEvent createDiSCO(URI systemAgentId, ORMapDiSCO disco, 
			ORMapEventMgr eventMgr, ORMapAgentMgr agentMgr, ORMapProfileMgr profilemgr, 
			SesameTriplestore ts) 
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
		// create any new reified statements needed, and triples for all statements in DiSCO
		ORMapStatementMgr stmtMgr = new ORMapStatementMgr();
		// Keep track of resources created by this Event
		// use Set, not list in case of duplicate statement IDs within transaction
		Set<URI> created = new HashSet<URI>();
		// add the DiSCO id as an event-created Resource
		created.add(disco.getDiscoContext());
		
		// Create reified statment for type statement if necessary, and add the triple
		URI typeId = stmtMgr.getStatementID(disco.getTypeStatement().getSubject(),
				disco.getTypeStatement().getPredicate(), disco.getTypeStatement().getObject(), ts);		
		if (typeId == null){
			typeId = stmtMgr.createReifiedStatement(disco.getTypeStatement(), ts);
			created.add(typeId);
		}
		this.createTriple(ts, disco.getTypeStatement());
		
		// Create reified statement for discoCreater if necessary, and add the triple
		URI discoCreator = stmtMgr.getStatementID(disco.getCreatorStmt().getSubject(),
				disco.getCreatorStmt().getPredicate(), disco.getCreatorStmt().getObject(), ts);	
		if (discoCreator == null){
			discoCreator = stmtMgr.createReifiedStatement(disco.getCreatorStmt(), ts);
			created.add(discoCreator);
		}
		this.createTriple(ts, disco.getCreatorStmt());
		// if necessary, create new Agent, and add its id to list of objects created by event
		List<URI> newCreatorObjects = this.createAgentCreator(disco.getCreatorStmt().getObject(), systemAgentId,
				agentMgr, profilemgr, ts);
		if (newCreatorObjects != null){
			created.addAll(newCreatorObjects);
		}
		
		// Create reified statement for description if necessary, and add the triple
		if (disco.getDescriptonStatement() != null){
			URI desc = stmtMgr.getStatementID(disco.getDescriptonStatement().getSubject(),
					disco.getDescriptonStatement().getPredicate(), disco.getDescriptonStatement().getObject(), ts);
			if (desc == null){
				desc = stmtMgr.createReifiedStatement(disco.getDescriptonStatement(), ts);
				created.add(desc);
			}
			this.createTriple(ts, disco.getDescriptonStatement());
		}
		
		// create reified statement for disco id the provider used
		if (disco.getProviderIdStmt()!= null){
			URI providerId = stmtMgr.getStatementID(disco.getProviderIdStmt().getSubject(),
					disco.getProviderIdStmt().getPredicate(), disco.getProviderIdStmt().getObject(), ts);
			if (providerId == null){
				providerId= stmtMgr.createReifiedStatement(disco.getProviderIdStmt(),ts);
				created.add(providerId);
			}
			this.createTriple(ts,disco.getProviderIdStmt());
		}
		
		// for each aggregated resource, create reified statement if necessary, create triples
		for (Statement stmt:aggResources){
			URI aggResource = stmtMgr.getStatementID(stmt.getSubject(),
					stmt.getPredicate(), stmt.getObject(), ts);
			if (aggResource == null){
				aggResource = stmtMgr.createReifiedStatement(stmt, ts);
				created.add(aggResource);
			}
			this.createTriple(ts, stmt);
		}
		
		// for each statement in relatedStatements
		//   create reified statement if necessary, and add the triple
		//   if dct:create or dc:creator create agent, agent profile as needed,and add the triple
		for (Statement stmt:disco.getRelatedStatementsAsList()){
			URI relStmt = stmtMgr.getStatementID(stmt.getSubject(),
					stmt.getPredicate(), stmt.getObject(), ts);
			if (relStmt == null){
				relStmt = stmtMgr.createReifiedStatement(stmt, ts);
				created.add(relStmt);
			}
			this.createTriple(ts, stmt);
			URI predicate = stmt.getPredicate();
			if (agentRelations.contains(predicate)){
				//TODO see if you need to create or update Agent here
			}
		}		
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
	 * Update: inactivate an existing DiSCO (same agent); derive from existing disco (same or different agent)
	 * by creating new DiSCO
	 * @param systemAgentId
	 * @param oldDiscoId
	 * @param disco
	 * @param eventMgr
	 * @param agentMgr 
	 * @param ts
	 * @return
	 */
	public RMapEvent updateDiSCO(URI systemAgentId,
			URI oldDiscoId, ORMapDiSCO disco, ORMapEventMgr eventMgr,
			ORMapAgentMgr agentMgr, ORMapProfileMgr profilemgr,
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
		// get the event started
		ORMapEvent event = null;	
		// same agent:  can be inactivation or update
		if (this.isSameDiscoAgent(oldDiscoId, systemAgentId, eventMgr, ts)){
			// either an inactivation or update event
			if (disco==null){
				// inactivation
				ORMapEventInactivation iEvent = new ORMapEventInactivation(systemAgentId, RMapEventTargetType.DISCO);
				iEvent.setInactivatedObjectId(ORAdapter.openRdfUri2RMapUri(oldDiscoId));
				event = iEvent;
			}
			else{
				ORMapEventUpdate uEvent = new ORMapEventUpdate(systemAgentId, RMapEventTargetType.DISCO,
						oldDiscoId, disco.getDiscoContext());
				event = uEvent;
			}
		}
		else {
			// but if it is a new Agent, MUST have new DiSCO, or else it's an illegal attempted update
			// and it's a derivation event
			if (disco==null){
				throw new RMapException("No new DiSCO provided; Agent is not the same as creating agent; " +
						" cannot inactivate another agent's DiSCO");
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
			// create any new reified statements needed, and triples for all statements in DiSCO
			ORMapStatementMgr stmtMgr = new ORMapStatementMgr();
			// Keep track of resources created by this Event
			// use Set, not list in case of duplicate statement IDs within transaction
			Set<URI> created = new HashSet<URI>();			
			// add the DiSCO id as an event-created Resource
			created.add(disco.getDiscoContext());
			
			// Create reified statment for type statement if necessary, and add the triple
			URI typeId = stmtMgr.getStatementID(disco.getTypeStatement().getSubject(),
					disco.getTypeStatement().getPredicate(), disco.getTypeStatement().getObject(), ts);		
			if (typeId == null){
				typeId = stmtMgr.createReifiedStatement(disco.getTypeStatement(), ts);
				created.add(typeId);
			}
			this.createTriple(ts, disco.getTypeStatement());
			
			// Create reified statement for discoCreater if necessary, and add the triple
			URI discoCreator = stmtMgr.getStatementID(disco.getCreatorStmt().getSubject(),
					disco.getCreatorStmt().getPredicate(), disco.getCreatorStmt().getObject(), ts);	
			if (discoCreator == null){
				discoCreator = stmtMgr.createReifiedStatement(disco.getCreatorStmt(), ts);
				created.add(discoCreator);
			}
			this.createTriple(ts, disco.getCreatorStmt());
			// if necessary, create new Agent, and add its id to list of objects created by event
			List<URI> newAgentObjects = this.createAgentCreator(disco.getCreatorStmt().getObject(),
					systemAgentId, agentMgr, profilemgr, ts);
			if (newAgentObjects != null){
				created.addAll(newAgentObjects);
			}

			// Create reified statement for description if necessary, and add the triple
			if (disco.getDescriptonStatement()!= null){
				URI desc = stmtMgr.getStatementID(disco.getDescriptonStatement().getSubject(),
						disco.getDescriptonStatement().getPredicate(), disco.getDescriptonStatement().getObject(), ts);
				if (desc == null){
					desc = stmtMgr.createReifiedStatement(disco.getDescriptonStatement(), ts);
					created.add(desc);
				}
				this.createTriple(ts, disco.getDescriptonStatement());
			}
			
			// create reified statement for disco id the provider used
			if (disco.getProviderIdStmt()!= null){
				URI providerId = stmtMgr.getStatementID(disco.getProviderIdStmt().getSubject(),
						disco.getProviderIdStmt().getPredicate(), disco.getProviderIdStmt().getObject(), ts);
				if (providerId == null){
					providerId= stmtMgr.createReifiedStatement(disco.getProviderIdStmt(),ts);
					created.add(providerId);
				}
				this.createTriple(ts,disco.getProviderIdStmt());
			}
			
			// for each aggregated resource, create reified statement if necessary, create triples
			List<Statement> aggResources = disco.getAggregatedResourceStatements();
			for (Statement stmt:aggResources){
				URI aggResource = stmtMgr.getStatementID(stmt.getSubject(),
						stmt.getPredicate(), stmt.getObject(), ts);
				if (aggResource == null){
					aggResource = stmtMgr.createReifiedStatement(stmt, ts);
					created.add(aggResource);
				}
				this.createTriple(ts, stmt);
			}
			
			// for each statement in relatedStatements
			//   create reified statement if necessary, and add the triple
			//   if dct:create or dc:creator create agent, agent profile as needed,and add the triple
			for (Statement stmt:disco.getRelatedStatementsAsList()){
				URI relStmt = stmtMgr.getStatementID(stmt.getSubject(),
						stmt.getPredicate(), stmt.getObject(), ts);
				if (relStmt == null){
					relStmt = stmtMgr.createReifiedStatement(stmt, ts);
					created.add(relStmt);
				}
				this.createTriple(ts, stmt);
				URI predicate = stmt.getPredicate();
				if (agentRelations.contains(predicate)){
					//TODO see if you need to create or update Agent here
				}
			}		
			// update the event with created object IDS for update and derivation events
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
	 * See if agent already exists.  If not, create Agent triples in triplestore
	 * @param agent
	 * @param agentMgr 
	 * @param profilemgr 
	 * @param ts
	 * @return ID of new Agent, or null if agent already exisits
	 * @throws RMapException
	 */
	protected List<URI> createAgentCreator(Value agent, URI systemAgentId,
			ORMapAgentMgr agentMgr, ORMapProfileMgr profilemgr, SesameTriplestore ts) 
			throws RMapException{
		if (!(agent instanceof URI)){
			throw new RMapException ("Agent not a URI: " + agent.stringValue());
		}
		if (systemAgentId==null){
			throw new RMapException("Null systemAgentId");
		}
		URI agentUri = (URI)agent;
		List<URI>newObjects = null;
		if (!(this.isAgentId(agentUri, ts))){
			newObjects = agentMgr.createAgentAndProfiles(agentUri, 
					systemAgentId, profilemgr, ts);
		}			
		return newObjects;
	}
	/**
	 * Soft-delete a DiSCO
	 * A read of this DiSCO should return tombstone notice rather than statements in the DiSCO,
	 * but DiSCO named graph is not deleted from triplestore
	 * @param systemAgentId
	 * @param oldDiscoId
	 * @param eventMgr 
	 * @param ts
	 * @return
	 */
	public RMapEvent tombstoneDiSCO(URI systemAgentId,URI oldDiscoId, ORMapEventMgr eventMgr, SesameTriplestore ts) {
		// confirm non-null old disco
		if (oldDiscoId==null){
			throw new RMapException ("Null value for id of DiSCO to be tombstoned");
		}
		if (systemAgentId==null){
			throw new RMapException("System Agent ID required: was null");
		}
		// Confirm systemAgentId (not null, is Agent)
		if (!(this.isAgentId(systemAgentId, ts))){
			throw new RMapObjectNotFoundException("No agent with id " + systemAgentId.stringValue());
		}
		// make sure same Agent created the DiSCO now being inactivated
		if (! this.isSameDiscoAgent(oldDiscoId, systemAgentId, eventMgr, ts)){
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
				eventStmts = ts.getStatements(null, RMAP.EVENT_INACTIVATED_OBJECT, discoId);
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
	public Map<URI,URI> getAllDiSCOVersions(URI discoId, boolean matchAgent, 
			ORMapEventMgr eventmgr, SesameTriplestore ts) 
			throws RMapObjectNotFoundException, RMapException {
		if (discoId==null){
			throw new RMapException ("Null disco");
		}
		if (! this.isDiscoId(discoId, ts)){
			throw new RMapObjectNotFoundException("No disco found with identifer " + 
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
	 */
	protected Map<URI,URI> lookBack(URI discoId, URI agentId, boolean lookFoward, 
			boolean matchAgent, ORMapEventMgr eventmgr, SesameTriplestore ts) 
					throws RMapObjectNotFoundException, RMapException {
		Statement eventStmt = eventmgr.getDiSCOCreateEventStatement(discoId, ts);
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
					event2Disco.putAll(this.lookFoward(discoId, agentId, matchAgent,eventmgr, ts));
				}
				break;
			}
			if ((eventmgr.isUpdateEvent(eventId, ts)) || (eventmgr.isDerivationEvent(eventId, ts))){
				// get id of old DiSCO
				URI oldDiscoID = eventmgr.getIdOfOldDisco(eventId, ts);
				// look back recursively on create/updates for oldDiscoID
				// DONT look forward on the backward search - you'll already have stuff
				 event2Disco.putAll(this.lookBack(oldDiscoID, agentId, false, 
						 matchAgent, eventmgr, ts));
				// now look ahead for any derived discos
				 event2Disco.putAll(this.lookFoward(discoId, agentId, matchAgent, eventmgr,ts));
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
				URI newDisco = eventmgr.getIdOfOldDisco(updateEventId, ts);
				if (newDisco != null){
					event2Disco.put(updateEventId,newDisco);
					// follow new DiSCO forward
					event2Disco.putAll(lookFoward(newDisco,agentId,matchAgent,eventmgr,ts));
				}
			}				
		} while (false);			 
		return event2Disco;
	}

	/**
	 * Confirm 2 identifiers refer to the same agent
	 * @param oldDisco
	 * @param systemAgentId
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected boolean isSameDiscoAgent (URI oldDisco, URI systemAgentId, 
			ORMapEventMgr eventmgr, SesameTriplestore ts) 
			throws RMapException {
		boolean isSame = false;
		Statement stmt = eventmgr.getDiSCOCreateEventStatement(oldDisco, ts);
		do {
			if (stmt==null){
				break;
			}
			if (! (stmt.getSubject() instanceof URI)){
				throw new RMapException ("Event ID is not URI: " + stmt.getSubject().stringValue());
			}
			URI eventId = (URI)stmt.getSubject();
			URI createAgent = eventmgr.getEventAssocAgent(eventId, ts);
			isSame = (systemAgentId.equals(createAgent));
		}while (false);
		return isSame;
	}

	
}

/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapEventTargetType;
import info.rmapproject.core.model.RMapEventType;
import info.rmapproject.core.model.impl.openrdf.ORMapEvent;
import info.rmapproject.core.model.impl.openrdf.ORMapEventCreation;
import info.rmapproject.core.model.impl.openrdf.ORMapEventDelete;
import info.rmapproject.core.model.impl.openrdf.ORMapEventTombstone;
import info.rmapproject.core.model.impl.openrdf.ORMapEventUpdate;
import info.rmapproject.core.model.impl.openrdf.ORMapStatement;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;
import info.rmapproject.core.utils.DateUtils;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.RDF;

/**
 *  @author khansen, smorrissey
 *
 */
public class ORMapEventMgr extends ORMapObjectMgr {

	/**
	 * 
	 */
	public ORMapEventMgr() {
		super();
	}
	
	/**
	 * Creates triples that comprise the Event object, and puts into triplesotre
	 * @param event
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public URI createEvent (ORMapEvent event, SesameTriplestore ts) throws RMapException {
		if (event==null){
			throw new RMapException ("Cannot create null Event");
		}
		URI eventId = event.getContext();
		this.createTriple(ts, event.getTypeStatement());
		this.createTriple(ts, event.getEventTypeStmt());
		this.createTriple(ts, event.getEventTargetTypeStmt());
		this.createTriple(ts, event.getAssociatedAgentStmt());
		this.createTriple(ts, event.getStartTypeStmt());
		this.createTriple(ts, event.getEndTimeStmt());
		if (event.getDescriptionStmt()!= null){
			this.createTriple(ts, event.getDescriptionStmt());
		}
		if (event instanceof ORMapEventCreation){
			ORMapEventCreation crEvent = (ORMapEventCreation)event;
			List<ORMapStatement> stmts = crEvent.getCreatedObjectStatements();
			if (stmts != null && !stmts.isEmpty()){
				for (ORMapStatement stmt:stmts){
					this.createTriple(ts, stmt);
				}
			}			
		}
		else if (event instanceof ORMapEventUpdate){
			ORMapEventUpdate upEvent = (ORMapEventUpdate)event;
			ORMapStatement target = upEvent.getTargetObjectStmt();
			this.createTriple(ts, target);
			ORMapStatement inactivated = upEvent.getInactivatedObjectStmt();
			if (inactivated != null){
				this.createTriple(ts, inactivated);
			}
			ORMapStatement derivationSource = upEvent.getDerivationStmt();
			if (derivationSource != null){
				this.createTriple(ts, derivationSource);
			}
			List<ORMapStatement> stmts = upEvent.getCreatedObjectStatements();
			if (stmts != null && !stmts.isEmpty()){
				for (ORMapStatement stmt:stmts){
					this.createTriple(ts, stmt);
				}
			}	
		}
		else if (event instanceof ORMapEventTombstone){
			ORMapEventTombstone tsEvent = (ORMapEventTombstone)event;
			this.createTriple(ts, tsEvent.getTombstonedResourceStmt());
		}
		else if (event instanceof ORMapEventDelete){
			ORMapEventDelete dEvent = (ORMapEventDelete)event;
			List<ORMapStatement> stmts = dEvent.getDeletedObjectStmts();
			if (stmts != null && !stmts.isEmpty()){
				for (ORMapStatement stmt:stmts){
					this.createTriple(ts, stmt);
				}
			}
		}
		else {
			throw new RMapException ("Unrecognized event type");
		}
		return eventId;
	}
	/**
	 * 
	 * @param eventId
	 * @param ts
	 * @return
	 * @throws RMapObjectNotFoundException
	 */
	public ORMapEvent readEvent(URI eventId, SesameTriplestore ts) 
	throws RMapObjectNotFoundException {
		ORMapEvent event = null;
		if (eventId ==null){
			throw new RMapException ("null eventId");
		}
		if (ts==null){
			throw new RMapException("null triplestore");
		}
		List<Statement> eventStmts = this.getNamedGraph(eventId, ts);
		
		event = createORMapEventFromStmts(eventStmts, ts);
		return event;
	}
	/**
	 * Construct ORMapEvent object from OpenRdf Statements
	 * @param eventStmts
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public  ORMapEvent createORMapEventFromStmts (List<Statement> eventStmts,
			SesameTriplestore ts) throws RMapException {
		if (eventStmts==null || eventStmts.size()==0){
			throw new RMapException ("null or emtpy list of event statements");	
		}
		ORMapStatement eventTypeStmt = null;
		ORMapStatement eventTargetTypeStmt = null;
		ORMapStatement associatedAgentStmt = null; 
		ORMapStatement descriptionStmt = null;
		ORMapStatement startTimeStmt = null;  
		ORMapStatement endTimeStmt = null;
		URI context = null;
		ORMapStatement typeStatement = null;
		// for create  and update events
		List<ORMapStatement> createdObjects = new ArrayList<ORMapStatement>();
		// for update events
		ORMapStatement targetObjectStatement = null;
		ORMapStatement derivationStatement = null;
		ORMapStatement inactivatedObjectStatement = null;
		// for Tombstone events
		ORMapStatement tombstoned = null;
		// for Delete events
		List<ORMapStatement> deletedObjects = new ArrayList<ORMapStatement>();;	
		ORMapEvent event = null;		
		
		context = (URI) eventStmts.get(0).getContext(); 
		for (Statement stmt:eventStmts){
			if (! (context.equals(stmt.getContext()))){
				throw new RMapException("Non-match of context in event named graph: " 
						+ "Expected context: " + context.stringValue() +
						"; actual context: " + stmt.getContext().stringValue());
			}
			URI predicate = stmt.getPredicate();
			if (predicate.equals(RDF.TYPE)){
				typeStatement = new ORMapStatement(stmt);
				continue;
			}
			if (predicate.equals(RMAP.EVENT_TYPE)){
				eventTypeStmt = new ORMapStatement(stmt);
				continue;
			}
			if (predicate.equals(RMAP.EVENT_TARGET_TYPE)){
				eventTargetTypeStmt = new ORMapStatement(stmt);
				continue;
			}
			if (predicate.equals(PROV.STARTEDATTIME)){
				startTimeStmt = new ORMapStatement(stmt);
				continue;
			}
			if (predicate.equals(PROV.ENDEDATTIME)){
				endTimeStmt = new ORMapStatement(stmt);
				continue;
			}
			if (predicate.equals(PROV.WASASSOCIATEDWITH)){
				associatedAgentStmt = new ORMapStatement(stmt);
				continue;
			}
			if (predicate.equals(DC.DESCRIPTION)){
				descriptionStmt = new ORMapStatement(stmt);
				continue;
			}
			if (predicate.equals(PROV.GENERATED)){
				createdObjects.add(new ORMapStatement(stmt));
				continue;
			}
			if (predicate.equals(RMAP.EVENT_TARGET)){
				targetObjectStatement = new ORMapStatement(stmt);
				continue;
			}
			if (predicate.equals(RMAP.EVENT_NEW_OBJECT_DERIVATION_SOURCE)){
				derivationStatement = new ORMapStatement(stmt);
				continue;
			}
			if (predicate.equals(RMAP.EVENT_TARGET_INACTIVATED)){
				inactivatedObjectStatement = new ORMapStatement(stmt);
				continue;
			}
			if (predicate.equals(RMAP.EVENT_TARGET_TOMBSTONED)){
				tombstoned = new ORMapStatement(stmt);
				continue;
			}
			if (predicate.equals(RMAP.EVENT_TARGET_DELETED)){
				deletedObjects.add(new ORMapStatement(stmt));
				continue;
			}
		}
		// validate all required statements for all event types
		if (typeStatement != null){				
			if (!(typeStatement.getObject().equals(RMAP.EVENT))){
				throw new RMapException("RDF type should be " + RMAP.EVENT.stringValue()
						+ " but is " + typeStatement.getObject().getStringValue());
			}
		}
		boolean isCreateEvent = false;
		boolean isUpdateEvent = false;
		boolean isTombstoneEvent = false;
		boolean isDeleteEvent = false;
		if (eventTypeStmt==null){
			throw new RMapException ("No event type in event graph " + context.stringValue());
		}
		else {
			String type = eventTypeStmt.getObject().getStringValue();
			do {
				if (type.equals(RMapEventType.CREATION.getTypeString())){
					isCreateEvent = true;
					break;
				}
				if (type.equals(RMapEventType.UPDATE.getTypeString())){
					isUpdateEvent = true;
					break;
				}
				if (type.equals(RMapEventType.TOMBSTONE.getTypeString())){
					isTombstoneEvent = true;
					break;
				}
				if (type.equals(RMapEventType.DELETION.getTypeString())){
					isDeleteEvent = true;
					break;
				}
				throw new RMapException ("Unrecognized event type: " + type
						+ " in event " + context.stringValue());
			}while (false);
		}
		if (eventTargetTypeStmt==null){
			throw new RMapException("No event target type in event graph " + 
					context.stringValue());
		}
		String targetType = eventTargetTypeStmt.getObject().getStringValue();
		do {
			if (targetType.equals(RMapEventTargetType.DISCO.uriString())){
				break;
			}
			if (targetType.equals(RMapEventTargetType.AGENT.uriString())){
				break;
			}
			throw new RMapException ("Unrecognized event target type: " + targetType
					+ " in event " + context.stringValue());
		} while (false);
		if (associatedAgentStmt == null){
			throw new RMapException("No associated agent in event graph " 
					+ context.stringValue());
		}
		URI agentID = (URI)associatedAgentStmt.getObject();
		if (!(this.isAgentId(agentID, ts))){
			throw new RMapException ("Event associated agent id " + agentID.stringValue() +
					" does not match any Agent");
		}
		if (startTimeStmt == null){
			throw new RMapException("No start time in event graph " + context.stringValue());
		}
		if (endTimeStmt == null){
			throw new RMapException("No end time in event graph " + context.stringValue());
		}
		// validate specific for each event type
		if (isCreateEvent){
			if (createdObjects.size()==0){
				throw new RMapException ("No new objects created in create event");
			}
			else {
				event = new ORMapEventCreation(eventTypeStmt,eventTargetTypeStmt, associatedAgentStmt, 
						descriptionStmt, startTimeStmt,endTimeStmt, context, typeStatement,
						createdObjects);
			}
		}
		else if (isUpdateEvent){
			if (targetObjectStatement==null){
				throw new RMapException("Update event missing target object statement");
			}
			if (derivationStatement == null &&inactivatedObjectStatement==null ){			
				throw new RMapException("Update event missing derivation and inactivated object statements");	
			}

			if (createdObjects.size()==0 && inactivatedObjectStatement == null){
				throw new RMapException("Updated is derivation but has no new created objects ");
			}
			event = new ORMapEventUpdate(eventTypeStmt,eventTargetTypeStmt, associatedAgentStmt, 
					descriptionStmt, startTimeStmt,endTimeStmt, context, typeStatement,
					createdObjects,targetObjectStatement,derivationStatement,inactivatedObjectStatement);
		}
		else if (isTombstoneEvent){
			if (tombstoned==null){
				throw new RMapException("Tombstone event missing tombstoned object statement");
			}
			event = new ORMapEventTombstone(eventTypeStmt,eventTargetTypeStmt, associatedAgentStmt, 
					descriptionStmt, startTimeStmt,endTimeStmt, context, typeStatement,tombstoned);
		}
		else if (isDeleteEvent){
			if(deletedObjects.size()==0){
				throw new RMapException ("Delete event has no deleted object ids");
			}
			event = new ORMapEventDelete(eventTypeStmt,eventTargetTypeStmt, associatedAgentStmt, 
					descriptionStmt, startTimeStmt,endTimeStmt, context, typeStatement,deletedObjects);
		}
		else {
			throw new RMapException ("Unrecognized event type");
		}
		return event;
	}
	/**
	 * Return id of event with latest end date
	 * @param eventIds
	 * @param ts
	 * @return
	 */
	public URI getLatestEvent (Set<URI> eventIds,SesameTriplestore ts)
	throws RMapException {
		Map <Date, URI>date2event = this.getDate2EventMap(eventIds, ts);
				new HashMap<Date, URI>();
		SortedSet<Date> dates = new TreeSet<Date>();
		dates.addAll(date2event.keySet());
		Date latestDate = dates.last(); 
		return date2event.get(latestDate);
	}
	/**
	 * 
	 * @param eventIds
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public Map <Date, URI> getDate2EventMap(Set<URI> eventIds,SesameTriplestore ts)
	throws RMapException {
		if (eventIds==null){
			throw new RMapException("List of eventIds is null");
		}
		Map <Date, URI>date2event = new HashMap<Date, URI>();
		for (URI eventId:eventIds){
			Statement stmt = null;
			try {
				stmt = ts.getStatement(eventId, PROV.ENDEDATTIME, null, eventId);
				
			} catch (Exception e) {
				throw new RMapException("Exception thrown getting end time statement for event "
							+ eventId.stringValue());
			}
			String endTimeStr = stmt.getObject().stringValue();
			Date date = null;
			try {
				date = DateUtils.getDateFromIsoString(endTimeStr);
			} catch (ParseException e) {
				throw new RMapException("Cannot parse date string " +
						endTimeStr + " for event id " + eventId.stringValue());
			}
			date2event.put(date, eventId);
		}
		return date2event;
	}
	/**
	 * Get discos related to an event
	 * @param eventId
	 * @param ts
	 * @return
	 */
	public List<URI> getRelatedDiSCOs(URI eventId, SesameTriplestore ts) 
	throws RMapException{
		List<URI> relatedDiSCOs = new ArrayList<URI>();
		RMapEventType eventType = this.getEventType(eventId, ts);
		switch (eventType){
		case CREATION :
			try {
				List<Statement> createdObjects= ts.getStatements(eventId, PROV.GENERATED, null, eventId);
				for (Statement stmt:createdObjects){
					Value obj = stmt.getObject();
					if (obj instanceof URI){
						URI uri = (URI)obj;
						if (this.isDiscoId(uri, ts)){
							relatedDiSCOs.add(uri);
						}
					}
				}
			} catch (Exception e) {
				throw new RMapException("exception thrown getting created objects for event "
						+ eventId.stringValue(), e);
			}
			break;
		case UPDATE:
			try {
				List<Statement> createdObjects= ts.getStatements(eventId, PROV.GENERATED, null, eventId);
				for (Statement stmt:createdObjects){
					Value obj = stmt.getObject();
					if (obj instanceof URI){
						URI uri = (URI)obj;
						if (this.isDiscoId(uri, ts)){
							relatedDiSCOs.add(uri);
						}
					}
				}
				Statement stmt = ts.getStatement(eventId, RMAP.EVENT_TARGET, null, eventId);
				if (stmt != null){
					Value obj = stmt.getObject();
					if (obj instanceof URI){
						URI uri = (URI)obj;
						if (this.isDiscoId(uri, ts)){
							relatedDiSCOs.add(uri);
						}
					}
				}
			} catch (Exception e) {
				throw new RMapException("exception thrown getting created/updated objects for event "
						+ eventId.stringValue(), e);
			}			
			break;			
		case TOMBSTONE:
			try {
				Statement stmt = ts.getStatement(eventId, RMAP.EVENT_TARGET_TOMBSTONED, null, eventId);
				if (stmt != null){
					Value obj = stmt.getObject();
					if (obj instanceof URI){
						URI uri = (URI)obj;
						if (this.isDiscoId(uri, ts)){
							relatedDiSCOs.add(uri);
						}
					}
				}
			} catch (Exception e) {
				throw new RMapException("exception thrown getting tombstoned objects for event "
						+ eventId.stringValue(), e);
			}
			break;
		case DELETION:
			try {
				List<Statement> createdObjects= ts.getStatements(eventId, RMAP.EVENT_TARGET_DELETED, null, eventId);
				for (Statement stmt:createdObjects){
					Value obj = stmt.getObject();
					if (obj instanceof URI){
						URI uri = (URI)obj;
						if (this.isDiscoId(uri, ts)){
							relatedDiSCOs.add(uri);
						}
					}
				}
			} catch (Exception e) {
				throw new RMapException("exception thrown getting deleted objects for event "
						+ eventId.stringValue(), e);
			}
			break;
		default:
			throw new RMapException("Unrecognized event type");
		}		
		return relatedDiSCOs;
	}
	/**
	 * Get ids of all events associated with a DiSCO
	 * "Associated" means the  id is the object of one of 4 predicates in triple whose subject
	 * is an eventid.  Those predicates are:
	 * 	RMAP.EVENT_TARGET_DELETED
	 *  MAP.EVENT_TARGET_TOMBSTONED
	 *  RMAP.EVENT_TARGET (update event)
	 *  PROV.GENERATED (Create and Update events)
	 * @param id
	 * @param ts
	 * @return
	 * @throws RMapObjectNotFoundException
	 * @throws RMapException
	 */
	public List<URI> getDiscoRelatedEventIds(URI id, SesameTriplestore ts) 
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
	 * Return ids of Statements associated with an event
	 * Note that if event is a Deletion, then no statement ids are ever returned,
	 * otherwise it would be possible to reconstitute a deleted DiSCO
	 * @param eventId
	 * @param ts
	 * @return
	 */
	public List<URI> getRelatedStatements(URI eventId, ORMapDiSCOMgr discomgr,
			ORMapStatementMgr stmtmgr, SesameTriplestore ts) 
	throws RMapException {
		if (eventId==null){
			throw new RMapException("Null event id");
		}
		List<URI>stmtIds = new ArrayList<URI>();
		if (!(this.isDeleteEvent(eventId, ts))){
			List<URI> relatedDiSCOs = this.getRelatedDiSCOs(eventId, ts);
			for (URI disco:relatedDiSCOs){
				stmtIds.addAll(discomgr.getDiSCOStatements(disco, stmtmgr, ts));
			}
		}
		return stmtIds;
	}
	/**
	 * Return ids of all resources associated with an Event
	 * Resources include DiSCOs associated with event
	 * For creation and update events, includes statements associated with 
	 * DiSCOs associated with event
	 * Includes Agent associated with event
	 * Does NOT descend on Statements to get subject and object resources
	 * @param eventId
	 * @param ts
	 * @return
	 */
	public List<URI> getRelatedResources (URI eventId, ORMapDiSCOMgr discomgr,
			ORMapStatementMgr stmtmgr, SesameTriplestore ts){
		Set<URI> resources = new TreeSet<URI>();
		// get DiSCO resources
		Set<URI> relatedDiSCOs = new TreeSet<URI>();
		relatedDiSCOs.addAll(this.getRelatedDiSCOs(eventId, ts));;
		resources.addAll(relatedDiSCOs);
		// get Statement resources
		Set<URI>stmtIds = new TreeSet<URI>();
		if (this.isCreationEvent(eventId, ts) || this.isUpdateEvent(eventId, ts)){			
			for (URI disco:relatedDiSCOs){
				stmtIds.addAll(discomgr.getDiSCOStatements(disco, stmtmgr, ts));
			}
			resources.addAll(stmtIds);
		}
		// get Agent resources
		resources.addAll(this.getRelatedAgents(eventId, ts));
		List<URI> lResources = new ArrayList<URI>();
		lResources.addAll(resources);
		return lResources;
	}
	/**
	 * Get Event's associate Agent
	 * Currently ONLY getting single system agent
	 * @param eventId
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getRelatedAgents (URI eventId, SesameTriplestore ts)
    throws RMapException {
		URI agentId =  this.getEventAssocAgent(eventId, ts);
		List<URI> agents = new ArrayList<URI>();
		agents.add(agentId);
		return agents;
	}
	/**
	 * 
	 * @param eventId
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public RMapEventType getEventType (URI eventId, SesameTriplestore ts) 
	throws RMapObjectNotFoundException, RMapException{
		if (eventId == null){
			throw new RMapException("null eventID");
		}
		if (ts==null){
			throw new RMapException ("null triplestore");
		}
		Value type = null;
		Statement stmt = null;
		try {
			stmt = ts.getStatement(eventId, RMAP.EVENT_TYPE, null, eventId);
		} catch (Exception e) {
			throw new RMapException ("Exception thrown getting event type for " 
					+ eventId.stringValue(), e);
		}
		if (stmt == null){
			throw new RMapObjectNotFoundException("No event type statement found for ID " +
		            eventId.stringValue());
		}
		else {
			type = stmt.getObject();
		}
		RMapEventType eType = RMapEventType.getEventTypeFromString(type.stringValue());
		return eType;
	}
	/**
	 * 
	 * @param eventId
	 * @param ts
	 * @return
	 */
	protected boolean isCreationEvent(URI eventId, SesameTriplestore ts){
		RMapEventType et = this.getEventType(eventId, ts);
		return et.equals(RMapEventType.CREATION);
	}
	/**
	 * 
	 * @param eventId
	 * @param ts
	 * @return
	 */
	protected boolean isUpdateEvent(URI eventId, SesameTriplestore ts){
		RMapEventType et = this.getEventType(eventId, ts);
		return et.equals(RMapEventType.UPDATE);
	}
	/**
	 * 
	 * @param eventId
	 * @param ts
	 * @return
	 */
	protected boolean isTombstoneEvent(URI eventId, SesameTriplestore ts){
		RMapEventType et = this.getEventType(eventId, ts);
		return et.equals(RMapEventType.TOMBSTONE);
	}
	/**
	 * 
	 * @param eventId
	 * @param ts
	 * @return
	 */
	protected boolean isDeleteEvent(URI eventId, SesameTriplestore ts){
		RMapEventType et = this.getEventType(eventId, ts);
		return et.equals(RMapEventType.DELETION);
	}
	
	/**
	 * Get identifier for the system agent associated an event
	 * @param event
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected URI getEventAssocAgent (URI event, SesameTriplestore ts) throws RMapException {
		URI agent = null;
		Statement stmt = null;
		try {
			stmt = ts.getStatement(event, PROV.WASASSOCIATEDWITH, null, event);
		} catch (Exception e) {
			throw new RMapException ("Exception thrown when querying for event associated agent", e);
		}
		if (stmt!=null){
			Value vAgent = stmt.getObject();
			if (vAgent instanceof URI){
				agent = (URI)vAgent;
			}
			else {
				throw new RMapException ("Associated Agent ID is not URI");
			}
		}
		else {
			throw new RMapException ("No system agent associated with event " + event.toString());
		}
		return agent;
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
	/**
	 * Get ids of Events that update (whether or not they also inactivate) a DiSCO
	 * @param targetId
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected List<Statement> getUpdateEvents(URI targetId, SesameTriplestore ts)
			throws RMapException {
		List<Statement> stmts = null;
		List<Statement> returnStmts = new ArrayList<Statement>();
		try {
			stmts = ts.getStatements(null, RMAP.EVENT_NEW_OBJECT_DERIVATION_SOURCE, targetId);
			for (Statement stmt:stmts){
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
				if (stmt != null){
					returnStmts.add(stmt);
				}
			}
		} catch (Exception e) {
			throw new RMapException (
					"Exception thrown when querying for Inactivate event for id " 
							+ targetId.stringValue(), e);
		}		
		return returnStmts;
	}
	/**
	 * Get id of Events where Resource is inactivated, whether updated or not
	 * @param targetId
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected List<Statement> getInactivateEvents(URI targetId, SesameTriplestore ts)
			throws RMapException {
		List<Statement> stmts = null;
		List<Statement> returnStmts = new ArrayList<Statement>();
		try {
			stmts = ts.getStatements(null, RMAP.EVENT_TARGET_INACTIVATED, targetId);
			for (Statement stmt:stmts){
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
				if (stmt != null){
					returnStmts.add(stmt);
				}
			}
		} catch (Exception e) {
			throw new RMapException (
					"Exception thrown when querying for Inactivate event for id " 
							+ targetId.stringValue(), e);
		}		
		return returnStmts;
	}
	/**
	 * Get id of source of new version of DiSCO from Update event
	 * @param updateEventID id of update event
	 * @param ts Triplestore
	 * @return new version of DiSCO from Update event or null if not found
	 */
	protected URI getIdOfSourceDisco(URI updateEventID, SesameTriplestore ts){
		URI sourceDisco = null;
		Statement stmt = null;
		try {
			stmt = ts.getStatement(updateEventID, RMAP.EVENT_NEW_OBJECT_DERIVATION_SOURCE, null, updateEventID);
			if (stmt != null){
				Value vObject = stmt.getObject();
				if (vObject instanceof URI){
					sourceDisco = (URI)vObject;
				}
			}
		} catch (Exception e) {
			throw new RMapException (e);
		}
		return sourceDisco;
	}
	/**
	 * Get id of created DiSCO from UpdateEvent
	 * @param updateEventID ID of update event
	 * @param ts Triplestore
	 * @return id of created DiSCO from UpdateEvent, or null if not found
	 */
	protected URI getIdOfCreatedDisco(URI updateEventID, SesameTriplestore ts){
		URI createdDisco = null;
		List<Statement> stmts = null;
		try {
			stmts = ts.getStatements(updateEventID, PROV.GENERATED, null, updateEventID);
			if (stmts != null){
					for (Statement stmt:stmts){
						Value vObject = stmt.getObject();
						if (vObject instanceof URI){
							URI uri = (URI)vObject;
							if (this.isDiscoId(uri, ts)){
								createdDisco = (URI)vObject;
								break;
							}
						}
					}
				}
		} catch (Exception e) {
			throw new RMapException (e);
		}
		return createdDisco;
	}
}

/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapDiSCONotFoundException;
import info.rmapproject.core.exception.RMapEventNotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventType;
import info.rmapproject.core.model.impl.openrdf.ORMapEvent;
import info.rmapproject.core.model.impl.openrdf.ORMapEventCreation;
import info.rmapproject.core.model.impl.openrdf.ORMapEventDeletion;
import info.rmapproject.core.model.impl.openrdf.ORMapEventDerivation;
import info.rmapproject.core.model.impl.openrdf.ORMapEventInactivation;
import info.rmapproject.core.model.impl.openrdf.ORMapEventTombstone;
import info.rmapproject.core.model.impl.openrdf.ORMapEventUpdate;
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
			List<Statement> stmts = crEvent.getCreatedObjectStatements();
			if (stmts != null && !stmts.isEmpty()){
				for (Statement stmt:stmts){
					this.createTriple(ts, stmt);
				}
			}			
		}
		else if (event instanceof ORMapEventUpdate){
			ORMapEventUpdate upEvent = (ORMapEventUpdate)event;
			Statement inactivated = upEvent.getInactivatedObjectStmt();
			if (inactivated != null){
				this.createTriple(ts, inactivated);
			}
			Statement derivationSource = upEvent.getDerivationStmt();
			if (derivationSource != null){
				this.createTriple(ts, derivationSource);
			}
			List<Statement> stmts = upEvent.getCreatedObjectStatements();
			if (stmts != null && !stmts.isEmpty()){
				for (Statement stmt:stmts){
					this.createTriple(ts, stmt);
				}
			}	
		}
		else if (event instanceof ORMapEventInactivation){
			ORMapEventInactivation inEvent = (ORMapEventInactivation)event;
			Statement inactivated = inEvent.getInactivatedObjectStatement();
			if (inactivated != null){
				this.createTriple(ts, inactivated);
			}
		}
		else if (event instanceof ORMapEventDerivation){
			ORMapEventDerivation dEvent = (ORMapEventDerivation)event;
			Statement sourceStmt = dEvent.getSourceObjectStatement();
			if (sourceStmt != null){
				this.createTriple(ts,sourceStmt);
			}
			Statement derivationSource = dEvent.getSourceObjectStatement();
			if (derivationSource != null){
				this.createTriple(ts, derivationSource);
			}
			List<Statement> stmts = dEvent.getCreatedObjectStatements();
			if (stmts != null && !stmts.isEmpty()){
				for (Statement stmt:stmts){
					this.createTriple(ts, stmt);
				}
			}
		}
		else if (event instanceof ORMapEventTombstone){
			ORMapEventTombstone tsEvent = (ORMapEventTombstone)event;
			this.createTriple(ts, tsEvent.getTombstonedResourceStmt());
		}
		else if (event instanceof ORMapEventDeletion){
			ORMapEventDeletion dEvent = (ORMapEventDeletion)event;
			List<Statement> stmts = dEvent.getDeletedObjectStmts();
			if (stmts != null && !stmts.isEmpty()){
				for (Statement stmt:stmts){
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
	 * @throws RMapEventNotFoundException
	 */
	public ORMapEvent readEvent(URI eventId, SesameTriplestore ts) 
	throws RMapEventNotFoundException {
		ORMapEvent event = null;
		if (eventId ==null){
			throw new RMapException ("null eventId");
		}
		if (ts==null){
			throw new RMapException("null triplestore");
		}
		List<Statement> eventStmts = null;
		try {
			eventStmts=this.getNamedGraph(eventId, ts);
		}
		catch (RMapObjectNotFoundException e){
			throw new RMapEventNotFoundException ("No event found for id " + eventId.stringValue(), e);
		}		
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
		Statement eventTypeStmt = null;
		Statement eventTargetTypeStmt = null;
		Statement associatedAgentStmt = null; 
		Statement descriptionStmt = null;
		Statement startTimeStmt = null;  
		Statement endTimeStmt = null;
		URI context = null;
		Statement typeStatement = null;
		// for create  and update events
		List<Statement> createdObjects = new ArrayList<Statement>();
		// for update events
		Statement sourceObjectStatement = null;
		Statement derivationStatement = null;
		Statement inactivatedObjectStatement = null;
		// for Tombstone events
		Statement tombstoned = null;
		// for Delete events
		List<Statement> deletedObjects = new ArrayList<Statement>();;	
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
				typeStatement = stmt;
				continue;
			}
			if (predicate.equals(RMAP.EVENT_TYPE)){
				eventTypeStmt = stmt;
				continue;
			}
			if (predicate.equals(RMAP.EVENT_TARGET_TYPE)){
				eventTargetTypeStmt = stmt;
				continue;
			}
			if (predicate.equals(PROV.STARTEDATTIME)){
				startTimeStmt =stmt;
				continue;
			}
			if (predicate.equals(PROV.ENDEDATTIME)){
				endTimeStmt = stmt;
				continue;
			}
			if (predicate.equals(PROV.WASASSOCIATEDWITH)){
				associatedAgentStmt = stmt;
				continue;
			}
			if (predicate.equals(DC.DESCRIPTION)){
				descriptionStmt = stmt;
				continue;
			}
			if (predicate.equals(PROV.GENERATED)){
				createdObjects.add(stmt);
				continue;
			}
			if (predicate.equals(RMAP.EVENT_SOURCE_OBJECT)){
				sourceObjectStatement = stmt;
				continue;
			}
			if (predicate.equals(RMAP.EVENT_DERIVED_OBJECT)){
				derivationStatement = stmt;
				continue;
			}
			if (predicate.equals(RMAP.EVENT_INACTIVATED_OBJECT)){
				inactivatedObjectStatement = stmt;
				continue;
			}
			if (predicate.equals(RMAP.EVENT_TARGET_TOMBSTONED)){
				tombstoned = stmt;
				continue;
			}
			if (predicate.equals(RMAP.EVENT_TARGET_DELETED)){
				deletedObjects.add(stmt);
				continue;
			}
		}
		// validate all required statements for all event types
		if (typeStatement != null){				
			if (!(typeStatement.getObject().equals(RMAP.EVENT))){
				throw new RMapException("RDF type should be " + RMAP.EVENT.stringValue()
						+ " but is " + typeStatement.getObject().stringValue());
			}
		}
		boolean isCreateEvent = false;
		boolean isUpdateEvent = false;
		boolean isInactivateEvent = false;
		boolean isDerivationEvent = false;
		boolean isTombstoneEvent = false;
		boolean isDeleteEvent = false;
		if (eventTypeStmt==null){
			throw new RMapException ("No event type in event graph " + context.stringValue());
		}
		else {
			String type = eventTypeStmt.getObject().stringValue();
			do {
				if (type.equals(RMapEventType.CREATION.getTypeString())){
					isCreateEvent = true;
					break;
				}
				if (type.equals(RMapEventType.UPDATE.getTypeString())){
					isUpdateEvent = true;
					break;
				}
				if (type.equals(RMapEventType.INACTIVATION.getTypeString())){
					isInactivateEvent = true;
					break;
				}
				if (type.equals(RMapEventType.DERIVATION.getTypeString())){
					isDerivationEvent = true;
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
		String targetType = eventTargetTypeStmt.getObject().stringValue();
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
			if (inactivatedObjectStatement==null){
				throw new RMapException("Update event missing inactivated object statement");
			}
			if (derivationStatement == null ){			
				throw new RMapException("Update event missing derived objec statement");	
			}

			if (createdObjects.size()==0 ){
				throw new RMapException("Updated has no new created objects ");
			}
			event = new ORMapEventUpdate(eventTypeStmt,eventTargetTypeStmt, associatedAgentStmt, 
					descriptionStmt, startTimeStmt,endTimeStmt, context, typeStatement,
					createdObjects,derivationStatement,inactivatedObjectStatement);
		}
		else if (isInactivateEvent){
			if (inactivatedObjectStatement==null){
				throw new RMapException("Update event missing inactivated object statement");
			}
			event = new ORMapEventInactivation(eventTypeStmt,eventTargetTypeStmt, associatedAgentStmt, 
					descriptionStmt, startTimeStmt, endTimeStmt, context, typeStatement,
					inactivatedObjectStatement);
		}
		else if (isDerivationEvent){
			if (sourceObjectStatement==null){
				throw new RMapException("Update event missing source object statement");
			}
			if (derivationStatement == null ){			
				throw new RMapException("Update event missing derived objec statement");	
			}

			if (createdObjects.size()==0 ){
				throw new RMapException("Updated has no new created objects ");
			}
			new ORMapEventDerivation(eventTypeStmt,eventTargetTypeStmt, associatedAgentStmt, 
					descriptionStmt, startTimeStmt,endTimeStmt, context, typeStatement,
					createdObjects,derivationStatement,sourceObjectStatement);
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
			event = new ORMapEventDeletion(eventTypeStmt,eventTargetTypeStmt, associatedAgentStmt, 
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
		RMapEventTargetType targetType = this.getEventTargetType(eventId, ts);
		switch (targetType){
		case DISCO:
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
					Statement stmt = ts.getStatement(eventId, RMAP.EVENT_INACTIVATED_OBJECT, null, eventId);
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
			case INACTIVATION:
				try {
					Statement stmt = ts.getStatement(eventId,  RMAP.EVENT_INACTIVATED_OBJECT, null, eventId);
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
					throw new RMapException("exception thrown getting created/inactivated objects for event "
							+ eventId.stringValue(), e);
				}
				break;
			case DERIVATION:
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
					Statement stmt = ts.getStatement(eventId, RMAP.EVENT_SOURCE_OBJECT, null, eventId);
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
			break;
		case AGENT:
			break;
		default:
			throw new RMapException ("Unrecognized event target type")	;
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
	 * @throws RMapDiSCONotFoundException
	 * @throws RMapException
	 */
	public List<URI> getDiscoRelatedEventIds(URI id, SesameTriplestore ts) 
			throws RMapDiSCONotFoundException, RMapException {
		List<URI> events = null;
		if (id==null){
			throw new RMapException ("Null disco");
		}
		// first ensure Exists statement URI rdf:TYPE rmap:DISCO  if not: raise NOTFOUND exception
		if (! this.isDiscoId(id, ts)){
			throw new RMapDiSCONotFoundException ("No object found with id " + id.stringValue());
		}
		do {
			List<Statement> eventStmts = new ArrayList<Statement>();
			try {
				eventStmts.addAll(ts.getStatements(null, RMAP.EVENT_TARGET_DELETED, id));
				eventStmts.addAll(ts.getStatements(null, RMAP.EVENT_TARGET_TOMBSTONED, id));
				eventStmts.addAll(ts.getStatements(null, RMAP.EVENT_INACTIVATED_OBJECT, id));
				eventStmts.addAll(ts.getStatements(null, RMAP.EVENT_DERIVED_OBJECT, id));
				eventStmts.addAll(ts.getStatements(null, RMAP.EVENT_SOURCE_OBJECT, id));
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
	 * Find any events associated with an Agent ID
	 * @param id
	 * @param ts
	 * @return
	 * @throws RMapAgentNotFoundException
	 * @throws RMapException
	 */
	public List<URI> getAgentRelatedEventIds(URI id, SesameTriplestore ts) 
			throws RMapAgentNotFoundException, RMapException {
		if (id==null){
			throw new RMapException ("Null disco");
		}		
		if (! this.isAgentId(id, ts)){
			throw new RMapAgentNotFoundException ("No agent object found with id " + id.stringValue());
		}	
		List<URI> events = new ArrayList<URI>();
		do {
			// get events where Agent created, tombstoned, deleted
			List<Statement> eventStmts = new ArrayList<Statement>();
			try {
				eventStmts.addAll(ts.getStatements(null, RMAP.EVENT_TARGET_DELETED, id));
				eventStmts.addAll(ts.getStatements(null, RMAP.EVENT_TARGET_TOMBSTONED, id));
				eventStmts.addAll(ts.getStatements(null, PROV.GENERATED, id));
				if (eventStmts.isEmpty()){
					break;
				}				
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
		do {
			// now get events where Agent is the associatedAgent
			//stmt = ts.getStatement(event, PROV.WASASSOCIATEDWITH, null, event);
			List<Statement> assocAgentStmts = new ArrayList<Statement>();
			try {
				assocAgentStmts.addAll(ts.getStatements(null,PROV.WASASSOCIATEDWITH,id,null));
				for (Statement stmt:assocAgentStmts){
					URI eventId = (URI)stmt.getSubject();
					if (this.isEventId(eventId,ts)){
						events.add(eventId);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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
		if (!((this.isDeleteEvent(eventId, ts)) || (this.isUpdateEvent(eventId, ts)))){
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
		Set<URI> resources = new HashSet<URI>();
		// get DiSCO resources
		Set<URI> relatedDiSCOs = new HashSet<URI>();
		relatedDiSCOs.addAll(this.getRelatedDiSCOs(eventId, ts));;
		resources.addAll(relatedDiSCOs);
		// get Statement resources
		Set<URI>stmtIds = new HashSet<URI>();
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
	 * Get Event's associated Agent
	 * Currently ONLY getting single system agent
	 * @param eventId
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getRelatedAgents (URI eventId, SesameTriplestore ts)
    throws RMapException, RMapAgentNotFoundException, RMapEventNotFoundException {
		URI agentId =  this.getEventAssocAgent(eventId, ts);
		List<URI> agents = new ArrayList<URI>();
		agents.add(agentId);
		ORMapEvent event = this.readEvent(eventId, ts);
		if (event.getEventTargetType().equals(RMapEventTargetType.AGENT)){
			RMapEventType eventType = event.getEventType();
			switch (eventType){
			case CREATION:
				ORMapEventCreation crEvent = (ORMapEventCreation)event;
				for (Statement stmt:crEvent.getCreatedObjectStatements()){
					Value object = stmt.getObject();
					if (object instanceof URI){
						URI uri = (URI)object;
						if (this.isAgentId(uri, ts)){
							agents.add(uri);
						}
					}
				}
				break;
			case TOMBSTONE:
				ORMapEventTombstone tEvent = (ORMapEventTombstone)event;
				Statement stmt = tEvent.getTombstonedResourceStmt();
				Value object = stmt.getObject();
				if (object instanceof URI){
					URI uri = (URI)object;
					if (this.isAgentId(uri, ts)){
						agents.add(uri);
					}
				}
				break;
			case DELETION:
				ORMapEventDeletion dEvent = (ORMapEventDeletion)event;
				for (Statement stment:dEvent.getDeletedObjectStmts()){
					Value obj = stment.getObject();
					if (obj instanceof URI){
						URI uri = (URI)obj;
						if (this.isAgentId(uri, ts)){
							agents.add(uri);
						}
					}
				}
				break;
			default:
				break;			
			}
		}
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
	throws RMapEventNotFoundException, RMapException{
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
			throw new RMapEventNotFoundException("No event type statement found for ID " +
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
	 * @throws RMapEventNotFoundException
	 * @throws RMapException
	 */
	public RMapEventTargetType getEventTargetType (URI eventId, SesameTriplestore ts) 
	throws RMapEventNotFoundException, RMapException{
		if (eventId == null){
			throw new RMapException("null eventID");
		}
		if (ts==null){
			throw new RMapException ("null triplestore");
		}
		Value type = null;
		Statement stmt = null;
		try {
			stmt = ts.getStatement(eventId, RMAP.EVENT_TARGET_TYPE, null, eventId);
		} catch (Exception e) {
			throw new RMapException ("Exception thrown getting event type for " 
					+ eventId.stringValue(), e);
		}
		if (stmt == null){
			throw new RMapEventNotFoundException("No event type statement found for ID " +
		            eventId.stringValue());
		}
		else {
			type = stmt.getObject();
		}
		RMapEventTargetType tType = RMapEventTargetType.getTargetTypeFromString(type.stringValue());
		return tType;
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
	protected boolean isDerivationEvent (URI eventId, SesameTriplestore ts){
		RMapEventType et = this.getEventType(eventId, ts);
		return et.equals(RMapEventType.DERIVATION);
	}
	/**
	 * 
	 * @param eventId
	 * @param ts
	 * @return
	 */
	protected boolean isInactivateEvent (URI eventId, SesameTriplestore ts){
		RMapEventType et = this.getEventType(eventId, ts);
		return et.equals(RMapEventType.INACTIVATION);
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
	 * @throws RMapAgentNotFoundException
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
			throw new RMapAgentNotFoundException ("No system agent associated with event " + event.toString());
		}
		return agent;
	}
	/**
	 * Find the creation Event associated with a DiSCO
	 * @param uri
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected Statement getRMapObjectCreateEventStatement(URI uri, SesameTriplestore ts) 
	throws RMapException {
		Statement stmt = null;
		try {
			stmt = ts.getStatementAnyContext(null, PROV.GENERATED, uri);
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
			throw new RMapException ("Exception thrown when querying for Create event", e);
		}		
		return stmt;
	}

	
	
	/**
	 * Get ids of Events that update or derive from a DiSCO
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
			//TOD check this against new event types
			stmts = ts.getStatements(null, RMAP.EVENT_INACTIVATED_OBJECT, targetId);
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
//	/**
//	 * Get id of Events where Resource is inactivated, whether updated or not
//	 * @param targetId
//	 * @param ts
//	 * @return
//	 * @throws RMapException
//	 */
//	protected List<Statement> getInactivateEvents(URI targetId, SesameTriplestore ts)
//			throws RMapException {
//		List<Statement> stmts = null;
//		List<Statement> returnStmts = new ArrayList<Statement>();
//		try {
//			stmts = ts.getStatements(null, RMAP.EVENT_INACTIVATED_OBJECT, targetId);
//			for (Statement stmt:stmts){
//				// make sure this is an event
//				if (stmt != null && stmt.getSubject().equals(stmt.getContext())){
//					Statement typeStmt = ts.getStatement(stmt.getSubject(), RDF.TYPE, 
//							RMAP.EVENT, stmt.getContext());
//					if (typeStmt==null){
//						stmt = null;
//					}
//				}
//				else {
//					stmt = null;
//				}
//				if (stmt != null){
//					returnStmts.add(stmt);
//				}
//			}
//		} catch (Exception e) {
//			throw new RMapException (
//					"Exception thrown when querying for Inactivate event for id " 
//							+ targetId.stringValue(), e);
//		}		
//		return returnStmts;
//	}
	/**
	 * Get id of source of old version of DiSCO from Update event
	 * @param eventId id of update event
	 * @param ts Triplestore
	 * @return new version of DiSCO from Update event or null if not found
	 */
	protected URI getIdOfOldDisco(URI eventId, SesameTriplestore ts){
		URI sourceDisco = null;
		Statement stmt = null;
		try {
			stmt = ts.getStatement(eventId, RMAP.EVENT_INACTIVATED_OBJECT, null, eventId);
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

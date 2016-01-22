/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import info.rmapproject.core.controlledlist.AgentPredicate;
import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapDeletedObjectException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapTombstonedObjectException;
import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORMapEvent;
import info.rmapproject.core.model.impl.openrdf.ORMapEventCreation;
import info.rmapproject.core.model.impl.openrdf.ORMapEventUpdateWithReplace;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.utils.OSparqlUtils;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;
import info.rmapproject.core.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;


/**
 * @author smorrissey, khanson0
 *
 */
public class ORMapAgentMgr extends ORMapObjectMgr {

	static List<URI> agentPredicates = new ArrayList<URI>();
	static {
		List<java.net.URI>preds = AgentPredicate.getAgentPredicates();
		for (java.net.URI uri:preds){
			URI aPred = ORAdapter.uri2OpenRdfUri(uri);
			agentPredicates.add(aPred);
		}
	}
	/**
	 * 
	 */
	public ORMapAgentMgr() {
		super();
	}
	
	/**
	 * 
	 * @param agentId
	 * @param ts
	 * @return
	 * @throws RMapAgentNotFoundException
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public ORMapAgent readAgent(URI agentId, SesameTriplestore ts)
	throws RMapAgentNotFoundException, RMapException,  RMapTombstonedObjectException, 
	       RMapDeletedObjectException, RMapDefectiveArgumentException {		
		if (agentId == null){
			throw new RMapException("null agentId");
		}
		if (ts==null){
			throw new RMapException("null triplestore");
		}
		
		if (!(this.isAgentId(agentId, ts))){
			throw new RMapAgentNotFoundException("Not an agentID: " + agentId.stringValue());
		}
		RMapStatus status = this.getAgentStatus(agentId, ts);
		switch (status){
		case TOMBSTONED :
			throw new RMapTombstonedObjectException("Agent "+ agentId.stringValue() + " has been (soft) deleted");
		case DELETED :
			throw new RMapDeletedObjectException ("Agent "+ agentId.stringValue() + " has been deleted");
		default:
			break;		
		}		
		List<Statement> agentStmts = null;
		try {
			agentStmts = this.getNamedGraph(agentId, ts);	
		}
		catch (RMapObjectNotFoundException e) {
			throw new RMapAgentNotFoundException ("No agent found with id " + agentId.toString(), e);
		}
		ORMapAgent agent = new ORMapAgent(agentStmts);
		return agent;
	}
	/**
	 * 
	 * @param agentId
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapAgentNotFoundException
	 */
	public RMapStatus getAgentStatus(URI agentId, SesameTriplestore ts) 
	throws RMapException, RMapAgentNotFoundException {
		RMapStatus status = null;
		if (agentId==null){
			throw new RMapException ("Null disco");
		}
		// first ensure Exists statement URI rdf:TYPE RMAP:AGENT  if not: raise NOTFOUND exception
		if (! this.isAgentId(agentId, ts)){
			throw new RMapAgentNotFoundException ("No Agent found with id " + agentId.stringValue());
		}
		do {
			List<Statement> eventStmts = null;
			try {
				//   ? RMap:Deletes discoId  done return deleted
				eventStmts = ts.getStatements(null, RMAP.EVENT_DELETED_OBJECT, agentId);
				if (eventStmts!=null && ! eventStmts.isEmpty()){
					status = RMapStatus.DELETED;
					break;
				}
				//   ? RMap:TombStones discoID	done return tombstoned
				eventStmts = ts.getStatements(null, RMAP.EVENT_TOMBSTONED_OBJECT, agentId);
				if (eventStmts!=null && ! eventStmts.isEmpty()){
					status = RMapStatus.TOMBSTONED;
					break;
				}
			   //   else return active if create event found
				eventStmts = ts.getStatements(null, PROV.GENERATED, agentId);
				if (eventStmts!=null && ! eventStmts.isEmpty()){
					status = RMapStatus.ACTIVE;
					break;
				}
				// else throw exception
				throw new RMapException ("No Events found for determing status of  " +
						agentId.stringValue());
			} catch (Exception e) {
				throw new RMapException("Exception thrown querying triplestore for events", e);
			}
		}while (false);
		
		return status;
	}

	/**
	 * 
	 * @param agent
	 * @param creatingAgentUri 
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public ORMapEvent createAgent (ORMapAgent agent, URI creatingAgentUri, SesameTriplestore ts)
	throws RMapException, RMapDefectiveArgumentException {
		if (agent==null){
			throw new RMapException ("null agent");
		}
		if (creatingAgentUri==null){
			throw new RMapException("System Agent ID required: was null");
		}
		if (ts==null){
			throw new RMapException ("null triplestore");
		}

		URI newAgentId = ORAdapter.rMapUri2OpenRdfUri(agent.getId());
		
		// Usually agents create themselves, where this isn't the case we need to check the creating agent exists already
		if (!creatingAgentUri.toString().equals(newAgentId.toString()) && !this.isAgentId(creatingAgentUri, ts)){
			throw new RMapAgentNotFoundException("No agent with id " + creatingAgentUri.stringValue());
		}		
		
		// Confirm that the agent being created doesn't already exist
		if (isAgentId(newAgentId,ts)) {
			throw new RMapException("The Agent being created already exists");
		}
		
		// Get the event started
		ORMapEventCreation event = new ORMapEventCreation(creatingAgentUri, RMapEventTargetType.AGENT);
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
		// keep track of objects created during this event
		Set<URI> created = new HashSet<URI>();
		created.add(agent.getContext());
		this.createAgentTriples(agent, ts);
		// update the event with created object IDS
		event.setCreatedObjectIdsFromURI(created);		
		// end the event, write the event triples, and commit everything
		event.setEndTime(new Date());
		ORMapEventMgr eventmgr = new ORMapEventMgr();
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
	 * @param updatedAgent
	 * @param creatingAgentUri 
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public ORMapEvent updateAgent (ORMapAgent updatedAgent, URI creatingAgentUri, SesameTriplestore ts)
	throws RMapException, RMapDefectiveArgumentException {
		if (updatedAgent==null){
			throw new RMapException ("null agent");
		}
		if (creatingAgentUri==null){
			throw new RMapException("System Agent ID required: was null");
		}
		if (ts==null){
			throw new RMapException ("null triplestore");
		}
		
		URI agentId = ORAdapter.rMapUri2OpenRdfUri(updatedAgent.getId());
				
		//check Agent Id exists
		if (!this.isAgentId(agentId, ts)){
			throw new RMapAgentNotFoundException("No agent with id " + agentId.stringValue());			
		}		
		
		// Usually agents create themselves, where this isn't the case we need to check the creating agent also exists
		if (!creatingAgentUri.toString().equals(agentId.toString()) && !this.isAgentId(creatingAgentUri, ts)){
			throw new RMapAgentNotFoundException("No agent with id " + creatingAgentUri.stringValue());
		}		

		//Get original agent
		RMapAgent origAgent = this.readAgent(agentId, ts);
		if (origAgent==null){
			throw new RMapAgentNotFoundException("Could not retrieve agent " + agentId.stringValue());						
		}
		
		// set up triplestore and start transaction
		boolean doCommitTransaction = false;
		try {
			if (!ts.hasTransactionOpen())	{
				doCommitTransaction = true;
				ts.beginTransaction();
			}
		} catch (Exception e) {
			throw new RMapException("Unable to begin Sesame transaction", e);
		}
				
		// Get the event started
		ORMapEventUpdateWithReplace event = new ORMapEventUpdateWithReplace(creatingAgentUri, 
							RMapEventTargetType.AGENT, agentId);
		
		String sEventDescrip = "";
		boolean updatesFound = false;
		
		//Remove elements of original agent and replace them with new elements
		try {
			Value origName = ORAdapter.rMapValue2OpenRdfValue(origAgent.getName());
			URI origIdProvider = ORAdapter.rMapUri2OpenRdfUri(origAgent.getIdProvider());
			URI origAuthId = ORAdapter.rMapUri2OpenRdfUri(origAgent.getAuthId());
			
			Value newName = ORAdapter.rMapValue2OpenRdfValue(updatedAgent.getName());
			URI newIdProvider = ORAdapter.rMapUri2OpenRdfUri(updatedAgent.getIdProvider());
			URI newAuthId = ORAdapter.rMapUri2OpenRdfUri(updatedAgent.getAuthId());
			
			//as a precaution take one predicate at a time to make sure we don't delete anything we shouldn't
			if (!origName.equals(newName)) {
				List <Statement> stmts = ts.getStatements(agentId, FOAF.NAME, null, agentId);
				ts.removeStatements(stmts);		
				ts.addStatement(agentId, FOAF.NAME, newName, agentId);	
				sEventDescrip=sEventDescrip + "foaf:name=" + origName + " -> " + newName + "; ";
				updatesFound=true;
			}
			if (!origIdProvider.equals(newIdProvider)) {
				List <Statement> stmts = ts.getStatements(agentId, RMAP.IDENTITY_PROVIDER, null, agentId);
				ts.removeStatements(stmts);		
				ts.addStatement(agentId, RMAP.IDENTITY_PROVIDER, newIdProvider, agentId);	
				sEventDescrip=sEventDescrip + "rmap:identityProvider=" + origIdProvider + " -> " + newIdProvider + "; ";
				updatesFound=true;
			}
			if (!origAuthId.equals(newAuthId)) {
				List <Statement> stmts = ts.getStatements(agentId, RMAP.USER_AUTH_ID, null, agentId);
				ts.removeStatements(stmts);	
				ts.addStatement(agentId, RMAP.USER_AUTH_ID, newAuthId, agentId);
				sEventDescrip=sEventDescrip + "rmap:userAuthId=" + origAuthId + " -> " + newAuthId + "; ";
				updatesFound=true;
			}
		} catch (Exception e) {
			throw new RMapException("Unable to remove previous version of Agent " + agentId.toString(), e);
		}

		if (updatesFound) {
			// end the event, write the event triples, and commit everything
			event.setDescription(new RMapLiteral("Updates: "+ sEventDescrip));
			event.setEndTime(new Date());
			ORMapEventMgr eventmgr = new ORMapEventMgr();
			eventmgr.createEvent(event, ts);
		}
		else {
			throw new RMapException("The Agent (" + agentId + " ) did not change and therefore does not need to be updated ");
		}

		if (doCommitTransaction){
			try {
				ts.commitTransaction();
			} catch (Exception e) {
				throw new RMapException("Exception thrown committing new triples to triplestore");
			}
		}

		return event;
	}
	

//  REMOVED - NOT CURRENTLY SUPPORTED
//	/**
//	 * Tombstone (soft-delete) an Agent
//	 * @param systemAgentId
//	 * @param oldAgentId
//	 * @param ts
//	 * @return
//	 * @throws RMapException
//	 */
//	public RMapEvent tombstoneAgent(URI systemAgentId,URI oldAgentId, SesameTriplestore ts) 
//	throws RMapException {
//		// confirm non-null old agent
//		if (oldAgentId==null){
//			throw new RMapException ("Null value for id of Agent to be tombstoned");
//		}
//		if (systemAgentId==null){
//			throw new RMapException("System Agent ID required: was null");
//		}
//		// Confirm systemAgentId (not null, is Agent)
//		if (!(this.isAgentId(systemAgentId, ts))){
//			throw new RMapAgentNotFoundException("No agent with id " + systemAgentId.stringValue());
//		}
//		// make sure same Agent created the Agent now being inactivated
//		if (! this.isSameCreatorAgent(oldAgentId, systemAgentId, ts)){
//			throw new RMapException(
//					"Agent attempting to tombstone Agent is not same as its creating Agent");
//		}		
//		
//		// get the event started
//		ORMapEventTombstone event = new ORMapEventTombstone(systemAgentId, 
//				RMapEventTargetType.AGENT, oldAgentId);
//
//		// set up triplestore and start transaction
//		boolean doCommitTransaction = false;
//		try {
//			if (!ts.hasTransactionOpen())	{
//				doCommitTransaction = true;
//				ts.beginTransaction();
//			}
//		} catch (Exception e) {
//			throw new RMapException("Unable to begin Sesame transaction: ", e);
//		}
//		
//		// end the event, write the event triples, and commit everything
//		ORMapEventMgr eventmgr = new ORMapEventMgr();
//		event.setEndTime(new Date());
//		eventmgr.createEvent(event, ts);
//
//		if (doCommitTransaction){
//			try {
//				ts.commitTransaction();
//			} catch (Exception e) {
//				throw new RMapException("Exception thrown committing new triples to triplestore");
//			}
//		}
//		return event;
//	}
	
	/**
	 * Get DiSCOs that were created by the Agent provided
	 * @param agentId
	 * @param status
	 * @param dateFrom
	 * @param dateTo
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public List<URI> getAgentDiSCOs(URI agentId, RMapStatus status, Date dateFrom, Date dateTo, SesameTriplestore ts) 
			throws RMapException, RMapDefectiveArgumentException {
		if (agentId==null){
			throw new RMapDefectiveArgumentException ("null agentId");
		}
		
		/*
		 * Query gets DiSCOs created by a specific agent.
		 * SELECT DISTINCT ?rmapObjId ?startDate 
			WHERE { 
			GRAPH ?rmapObjId  
				{
				?rmapObjId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/DiSCO> .						} . 
			 GRAPH ?eventId {
				?eventId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Event> .
				{?eventId <http://www.w3.org/ns/prov#generated> ?rmapObjId} UNION 
				{?eventId <http://rmap-project.org/rmap/terms/derivedObject> ?rmapObjId} .
				?eventId <http://www.w3.org/ns/prov#startedAtTime> ?startDate .
				?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmaptestagent> .
			} .
			FILTER NOT EXISTS {?statusChangeEventId <http://rmap-project.org/rmap/terms/tombstonedObject> ?rmapObjId} .
			FILTER NOT EXISTS {?statusChangeEventId <http://rmap-project.org/rmap/terms/inactivatedObject> ?rmapObjId}
			}
		 */
		
		String statusFilterSparql = OSparqlUtils.convertRMapStatusToSparqlFilter(status);

		String sparqlQuery = "SELECT DISTINCT ?rmapObjId ?startDate "
							+ "WHERE { "
							+ " GRAPH ?rmapObjId "
							+ "	  {"
							+ "     ?rmapObjId <" + RDF.TYPE + "> <" + RMAP.DISCO + "> . "							
							+ "	  } . "
							+ " GRAPH ?eventId {"
							+ "   ?eventId <" + RDF.TYPE + "> <" + RMAP.EVENT + "> ."
							+ "   {?eventId <" + PROV.GENERATED + "> ?rmapObjId} UNION "
							+ "   {?eventId <" + RMAP.EVENT_DERIVED_OBJECT + "> ?rmapObjId} ."
							+ "   ?eventId <" + PROV.STARTEDATTIME + "> ?startDate ."
							+ "	  ?eventId <" + PROV.WASASSOCIATEDWITH + "> <" + agentId.toString() + "> . "
							+ "  } "
							+ statusFilterSparql
							+ "} ";
		
		TupleQueryResult resultset = null;
		try {
			resultset = ts.getSPARQLQueryResults(sparqlQuery);
		}
		catch (Exception e) {
			throw new RMapException("Could not retrieve SPARQL query results using " + sparqlQuery, e);
		}
		
		List<URI> discos = new ArrayList<URI>();
		
		try{
			while (resultset.hasNext()) {
				BindingSet bindingSet = resultset.next();
				URI discoid = (URI) bindingSet.getBinding("rmapObjId").getValue();
				Literal startDateLiteral = (Literal) bindingSet.getBinding("startDate").getValue();
				Date startDate = DateUtils.xmlGregorianCalendarToDate(startDateLiteral.calendarValue());
					
				if ((dateFrom != null && startDate.before(dateFrom))
						|| (dateTo != null && startDate.after(dateTo))) { 
					continue; // don't include out of range date
				}
				discos.add(discoid);
			}
		}	
		catch (RMapException r){throw r;}
		catch (Exception e){
			throw new RMapException("Could not process SPARQL results for DiSCOs created by Agent", e);
		}
				
		return discos;
	}
	
	/**
	 * Get a list of URIs for Events initiated by the Agent provided.
	 * @param agentId
	 * @param dateFrom
	 * @param dateTo
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */	
	public List<URI> getAgentEventsInitiated(URI agentId, Date dateFrom, Date dateTo, SesameTriplestore ts) 
			throws RMapException, RMapDefectiveArgumentException {
		if (agentId==null){
			throw new RMapDefectiveArgumentException ("null agentId");
		}

		//query gets eventIds and startDates of Events initiated by agent
		/*  SELECT DISTINCT ?eventId ?startDate 
			WHERE {
			GRAPH ?eventId {
			 	?eventId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Event> .
			 	?eventId <http://www.w3.org/ns/prov#startedAtTime> ?startDate .
			 	?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmaptestagent> .
			 	}
			}
			*/
		String sparqlQuery = "SELECT DISTINCT ?eventId ?startDate "
							+ "WHERE { "
							+ " GRAPH ?eventId {"
							+ "   ?eventId <" + RDF.TYPE + "> <" + RMAP.EVENT + "> ."
							+ "   ?eventId <" + PROV.STARTEDATTIME + "> ?startDate ."
							+ "	  ?eventId <" + PROV.WASASSOCIATEDWITH + "> <" + agentId.toString() + ">} . "
							+ "  } "
							+ "} ";
		
		TupleQueryResult resultset = null;
		try {
			resultset = ts.getSPARQLQueryResults(sparqlQuery);
		}
		catch (Exception e) {
			throw new RMapException("Could not retrieve SPARQL query results using " + sparqlQuery, e);
		}
		
		List<URI> events = new ArrayList<URI>();
		
		try{
			while (resultset.hasNext()) {
				BindingSet bindingSet = resultset.next();
				URI eventId = (URI) bindingSet.getBinding("eventId").getValue();
				Literal startDateLiteral = (Literal) bindingSet.getBinding("startDate").getValue();
				Date startDate = DateUtils.xmlGregorianCalendarToDate(startDateLiteral.calendarValue());
					
				if ((dateFrom != null && startDate.before(dateFrom))
						|| (dateTo != null && startDate.after(dateTo))) { 
					continue; // don't include out of range date
				}
				events.add(eventId);
			}
		}	
		catch (RMapException r){throw r;}
		catch (Exception e){
			throw new RMapException("Could not process results for Agent's initiated Events", e);
		}

		return events;		
	}
	
	
	
	
	/**
	 * 
	 * @param agent
	 * @param ts
	 * @throws RMapException
	 */
	public void createAgentTriples (ORMapAgent agent, SesameTriplestore ts) 
	throws RMapException {
		Model model = agent.getAsModel();
		Iterator<Statement> iterator = model.iterator();
		while (iterator.hasNext()){
			Statement stmt = iterator.next();
			this.createTriple(ts, stmt);
		}
		return;
	}
		
}

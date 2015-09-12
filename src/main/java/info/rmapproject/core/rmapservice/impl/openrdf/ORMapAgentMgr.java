/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;



import info.rmapproject.core.controlledlist.AgentPredicate;
import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapDeletedObjectException;
import info.rmapproject.core.exception.RMapDiSCONotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapTombstonedObjectException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORMapEvent;
import info.rmapproject.core.model.impl.openrdf.ORMapEventCreation;
import info.rmapproject.core.model.impl.openrdf.ORMapEventTombstone;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.DCTERMS;


/**
 * @author smorrissey
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
	 */
	public ORMapAgent readAgent(URI agentId, SesameTriplestore ts)
	throws RMapAgentNotFoundException, RMapException,  RMapTombstonedObjectException, 
	       RMapDeletedObjectException {		
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
		ORMapAgent agent = new ORMapAgent(agentStmts, null);
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
				eventStmts = ts.getStatements(null, RMAP.EVENT_TARGET_DELETED, agentId);
				if (eventStmts!=null && ! eventStmts.isEmpty()){
					status = RMapStatus.DELETED;
					break;
				}
				//   ? RMap:TombStones discoID	done return tombstoned
				eventStmts = ts.getStatements(null, RMAP.EVENT_TARGET_TOMBSTONED, agentId);
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
	 * @param systemAgentId 
	 * @param eventMgr 
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public ORMapEvent createAgent (ORMapAgent agent, URI systemAgentId, ORMapEventMgr eventMgr, SesameTriplestore ts)
	throws RMapException {
		if (agent==null){
			throw new RMapException ("null agent");
		}
		if (systemAgentId==null){
			throw new RMapException("System Agent ID required: was null");
		}
		if (eventMgr==null){
			throw new RMapException ("null event manager");
		}
		if (ts==null){
			throw new RMapException ("null triplestore");
		}
		// Confirm systemAgentId (not null, is Agent)
		if (! this.isAgentId(systemAgentId, ts)){
			throw new RMapAgentNotFoundException("No agent with id " + systemAgentId.stringValue());
		}		
		// get the event started
		ORMapEventCreation event = new ORMapEventCreation(systemAgentId, RMapEventTargetType.AGENT);
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
	 * Tombstone (soft-delete) an Agent
	 * @param systemAgentId
	 * @param oldAgentId
	 * @param eventMgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public RMapEvent tombstoneAgent(URI systemAgentId,URI oldAgentId, 
			ORMapEventMgr eventMgr, SesameTriplestore ts) 
	throws RMapException {
		// confirm non-null old disco
		if (oldAgentId==null){
			throw new RMapException ("Null value for id of Agent to be tombstoned");
		}
		if (systemAgentId==null){
			throw new RMapException("System Agent ID required: was null");
		}
		// Confirm systemAgentId (not null, is Agent)
		if (!(this.isAgentId(systemAgentId, ts))){
			throw new RMapAgentNotFoundException("No agent with id " + systemAgentId.stringValue());
		}
		// make sure same Agent created the DiSCO now being inactivated
		if (! this.isSameCreatorAgent(oldAgentId, systemAgentId, eventMgr, ts)){
			throw new RMapException(
					"Agent attempting to tombstone Agent is not same as its creating Agent");
		}		
		
		// get the event started
		ORMapEventTombstone event = new ORMapEventTombstone(systemAgentId, 
				RMapEventTargetType.AGENT, oldAgentId);

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
	 * 
	 * @param targetId
	 * @param systemAgent
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected ORMapAgent createAgentObject(URI targetId, URI systemAgent,
			SesameTriplestore ts) 
	throws RMapException {
		ORMapAgent agent = null;
		agent = new ORMapAgent(targetId, systemAgent);	
		this.createAgentTriples(agent, ts);
		return agent;
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
	
	/**
	 * Get ids of any Agents that are a representation of the agent identified by non-RMap URI 
	 * created by any agent
	 * @param id
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public Set<URI> getAgentRepresentations(URI id, SesameTriplestore ts) 
	throws RMapException {
		if (id==null){
			throw new RMapException ("null id");
		}
		if (ts==null){
			throw new RMapException ("null triplestore");
		}
		Set<URI>agentIds = new HashSet<URI>();
		try {
			List<Statement> stmts = ts.getStatementsAnyContext(null, DCTERMS.IS_FORMAT_OF, id, false);
			for (Statement stmt:stmts){
				Resource context = stmt.getContext();
				if (context instanceof URI){
					URI agentId = (URI)context;
					if (this.isAgentId(agentId, ts)){
						agentIds.add(agentId);
					}
				}
			}
		} catch (Exception e) {
			throw new RMapException ("Exception thrown searching for statements with object " + id.stringValue(), e);
		}	
		return agentIds;
	}
	/**
	 * Find identifiers of RMap agents representing a particular resource and created by 
	 * a particular agent
	 * @param agentURI
	 * @param repURI
	 * @param ts
	 * @return
	 * @throws RMapDefectiveArgumentException
	 * @throws RMapAgentNotFoundException
	 * @throws RMapException
	 */
	public Set<org.openrdf.model.URI> getAgentRepresentations(URI agentURI, 
			URI repURI, SesameTriplestore ts)
	throws RMapDefectiveArgumentException, RMapAgentNotFoundException, RMapException {
		if (agentURI==null){
			throw new RMapDefectiveArgumentException("null agent URI");
		}
		if (repURI==null){
			throw new RMapDefectiveArgumentException("nullrepURI");
		}
		if (ts==null){
			throw new RMapDefectiveArgumentException("null triplestore");
		}
		if (!(this.isAgentId(agentURI, ts))){
			throw new RMapAgentNotFoundException("No RMap agent with id " + 
					agentURI.stringValue());
		}
		Set<URI>agents = this.getAgentRepresentations(repURI, ts);
		Set<URI>matchingAgents = new HashSet<URI>();
		String agentStr = agentURI.stringValue();
		for (URI uri:agents){
			ORMapAgent agent = this.readAgent(uri, ts);
			URI agentRepUri = (URI)(agent.getRepresentationStmt().getObject());
			if (agentRepUri.stringValue().equals(agentStr)){
				matchingAgents.add(agentRepUri);
			}
		}
		return matchingAgents;
	}
	
	/**
	 * Find identifiers of RMap agents associated with an Agent ID 
	 * @param uri
	 * @param statusCode
	 * @param ts
	 * @return
	 * @throws RMapDefectiveArgumentException
	 * @throws RMapAgentNotFoundException
	 * @throws RMapException
	 */
	public Set<URI> getRelatedAgents (URI uri, RMapStatus statusCode, SesameTriplestore ts)
									throws RMapDefectiveArgumentException, RMapAgentNotFoundException, RMapException {
		if (uri == null){
			throw new RMapDefectiveArgumentException("null agentId");
		}
		if (ts==null){
			throw new RMapDefectiveArgumentException("null triplestore");
		}
		
		if (!(this.isAgentId(uri, ts))){
			throw new RMapAgentNotFoundException("Not an agentID: " + uri.stringValue());
		}
		Set<URI>agents = new HashSet<URI>();
		agents.add(uri);
		do {
			if (statusCode != null){
				RMapStatus status = this.getAgentStatus(uri, ts);
				if (!(status.equals(statusCode))){
					break;
				}
			}
			ORMapAgent agent = null;
			try{
				agent = this.readAgent(uri, ts);
			}
			catch (RMapTombstonedObjectException RMapDeletedObjectException ){
				break;
			}
			catch (Exception e){
				throw new RMapException(e);
			}
			agents.add((URI)(agent.getCreatorStmt().getObject()));
			for (Statement stmt:agent.getPropertyStatemts()){
				Resource subject = stmt.getSubject();
				if (subject instanceof URI){
					if (this.isAgentId((URI)subject, ts)){
						agents.add((URI)subject);
					}
				}
				Value object = stmt.getObject();
				if (object instanceof URI){
					if (this.isAgentId((URI)object, ts)){
						agents.add((URI)object);
					}
				}
			}
		}while (false);
		return agents;
	}
	

	/**
	 * Get ids of any Agents that asserted an Agent i.e isAssociatedWith the create event
	 * @param uri ID of Agent
	 * @param statusCode
	 * @param eventMgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapDiSCONotFoundException
	 * @throws RMapObjectNotFoundException
	 */
	public Set<URI> getAssertingAgents(URI uri, RMapStatus statusCode, Date dateFrom, Date dateTo, ORMapEventMgr eventMgr, SesameTriplestore ts) 
	throws RMapException, RMapDiSCONotFoundException, RMapObjectNotFoundException {
		Set<URI>agents = new HashSet<URI>();
		do {
			//don't get agent if DiSCO status doesn't match OR the DiSCO should be hidden from public view.
			RMapStatus dStatus = this.getAgentStatus(uri, ts);
			if ((statusCode != null && !dStatus.equals(statusCode))
					|| dStatus.equals(RMapStatus.DELETED)
					|| dStatus.equals(RMapStatus.TOMBSTONED)){
					break;
			}
			
			List<URI> events = eventMgr.getMakeObjectEvents(uri, ts);
			
           //For each event associated with Agent, return AssociatedAgent
			for (URI event:events){
				boolean addToList = true;
				URI assocAgent = eventMgr.getEventAssocAgent(event, ts);
				if (assocAgent==null) {
					addToList = false;
				}
				if (dateFrom != null || dateTo != null) { //if a date is passed, checked within the range.
					Date eventDate = eventMgr.getEventStartDate(event, ts);
					if ((dateFrom != null && eventDate.before(dateFrom))
							|| (dateTo != null && eventDate.after(dateTo))) {
						addToList = false;
					}
				}
				
					
					
				agents.add(assocAgent);
			}
		} while (false);		
		return agents;
	}
	

}

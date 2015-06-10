package info.rmapproject.core.rmapservice.impl.openrdf;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapDiSCONotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapStatementNotFoundException;
import info.rmapproject.core.exception.RMapTombstonedObjectException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;

/**
 * 
 *  @author khansen, smorrissey
 *
 */
public class ORMapResourceMgr extends ORMapObjectMgr {

	/**
	 * 
	 */
	public ORMapResourceMgr() {
		super();
	}
	/**
	 * Find all triples with subject or object equal to resource
	 * @param resource
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected List<Statement> getRelatedTriples(URI resource, SesameTriplestore ts)
	throws RMapException{
		List<Statement> triples = null;
		try {
			triples = ts.getStatements(resource, null, null);
			triples.addAll(ts.getStatements(null, null, resource));
		} catch (Exception e) {
			throw new RMapException (e);
		}		
		return triples;
	}
	/**
	 * Find ids for Statements whose subject or object matches resource URI
	 * If statusCode anything but null, only return statement id if statement
	 * status matches statusCode
	 * @param uri
	 * @param statusCode
	 * @param stmtmgr
	 * @param discomgr
	 * @param ts
	 * @return
	 * @throws RMapDefectiveArgumentException
	 */
	public Set<URI> getRelatedStatementIds(URI uri, RMapStatus statusCode,
			ORMapStatementMgr stmtmgr, ORMapDiSCOMgr discomgr, SesameTriplestore ts) 
			throws RMapDefectiveArgumentException, RMapException {
		if (uri==null){
			throw new RMapDefectiveArgumentException ("null URI");
		}
		Set<URI> relatedStmtIds = new HashSet<URI>();
		do {
			// get all triples with uri in subject or object
			List<Statement>stmts = this.getRelatedTriples(uri, ts);
			// now make sure triple comes from DiSCO with status is same as statusCode
			// context of each statement is URI of disco containing it
			List<Statement>statusStmts = new ArrayList<Statement>();
			for (Statement stmt:stmts){
				URI context = (URI)stmt.getContext();
				if (context!=null && this.isDiscoId(context, ts)){
					if (statusCode==null){
						statusStmts.add(stmt);
					}
					else {
						try {
							RMapStatus dStatus = discomgr.getDiSCOStatus(context, ts);
							if (dStatus.equals(statusCode)){
								statusStmts.add(stmt);				
							}
						}
						catch (RMapDiSCONotFoundException nf){}
						catch (RMapException r) {throw r;}
					}
				}
			}
			// now get the ids of the DiSCO triples		
			for (Statement stmt:statusStmts){
				try{
					URI stmId = stmtmgr.getStatementID(stmt.getSubject(),
							stmt.getPredicate(), stmt.getObject(), ts);
					relatedStmtIds.add(stmId);
				} 
				catch (RMapStatementNotFoundException nf){}
				catch (RMapException e){}
 			}
		} while (false);
		return relatedStmtIds;
	}
	/**
	 * Get Statements referencing a URI in subject or object, whose Subject, Predicate, and Object comprise an RMapStatement, 
	 * and (if statusCode is not null), whose status matches statusCodeE
	 * @param uri Resource to be matched
	 * @param statusCode Status to be matched, or null if any status code
	 * @param stmtmgr
	 * @param discomgr
	 * @param ts
	 * @return 
	 * @throws RMapDefectiveArgumentException
	 * @throws RMapException
	 */
	public Set<Statement> getRelatedStatementTriples(URI uri,
			RMapStatus statusCode, ORMapStatementMgr stmtmgr,
			ORMapDiSCOMgr discomgr, SesameTriplestore ts) 
	throws RMapDefectiveArgumentException, RMapException {
		if (uri==null){
			throw new RMapDefectiveArgumentException ("null URI");
		}
		Set<Statement> relatedStmts = new HashSet<Statement>();		
		do {
			// get all triples with uri in subject or object
			List<Statement>stmts = this.getRelatedTriples(uri, ts);
			// now make sure triple comes from DiSCO with status is same as statusCode
			// context of each statement is URI of disco containing it
			List<Statement>statusStmts = new ArrayList<Statement>();
			for (Statement stmt:stmts){
				URI context = (URI)stmt.getContext();
				if (this.isDiscoId(context, ts)){
					if (statusCode==null){
						statusStmts.add(stmt);
					}
					else {
						RMapStatus dStatus = discomgr.getDiSCOStatus(context, ts);
						if (dStatus.equals(statusCode)){
							statusStmts.add(stmt);				
						}
					}
				}
			}
			// now filter out any triples that do not correspond to an RMapStatement	
			for (Statement stmt:statusStmts){
				try{
					@SuppressWarnings("unused")
					URI stmId = stmtmgr.getStatementID(stmt.getSubject(),
							stmt.getPredicate(), stmt.getObject(), ts);
					// if no exception, then we found a matching RMapStatement
					relatedStmts.add(stmt);
				} 
				catch (RMapStatementNotFoundException nf){}
				catch (RMapException e){}
 			}
		} while (false);
		return relatedStmts;
	}
	/**
	 * 
	 * @param uri
	 * @param statusCode  if null, match any status code
	 * @param discomgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public Set<URI> getRelatedDiSCOS(URI uri, RMapStatus statusCode,
			ORMapDiSCOMgr discomgr, SesameTriplestore ts)
			throws RMapException {
		// get all Statements with uri in subject or object
		List<Statement>stmts = this.getRelatedTriples(uri, ts);
		Set<URI> discos = new HashSet<URI>();
		// make sure DiSCO in which statement appears matches statusCode
		for (Statement stmt:stmts){
			URI context = (URI)stmt.getContext();
			if (this.isDiscoId(context, ts)){
				if (statusCode==null){
					// match any status
					discos.add(context);
				}
				else {
					try {
						RMapStatus dStatus = discomgr.getDiSCOStatus(context, ts);
						if (dStatus.equals(statusCode)){
							discos.add(context);
						}
					}
					catch (RMapDiSCONotFoundException rf){}
					catch (RMapException r){throw r;}
				}
			}
		}
		return discos;		
	}
	/**
	 * Get ids of Events related to resource.
	 * @param resource
	 * @param stmtmgr
	 * @param discomgr
	 * @param ts
	 * @return
	 * @throws RMapDefectiveArgumentException 
	 */
	public Set<URI> getRelatedEvents(URI resource,ORMapStatementMgr stmtmgr, 
			ORMapDiSCOMgr discomgr, ORMapEventMgr eventMgr, SesameTriplestore ts) 
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		Set<URI>events = new HashSet<URI>();
		do {
			if (this.isEventId(resource, ts)){
				events.add(resource);
				break;
			}
			if (this.isDiscoId(resource, ts)){
				events.addAll(eventMgr.getDiscoRelatedEventIds(resource, ts));
				break;
			}
			if (this.isStatementId(resource, ts)){
				events.addAll(stmtmgr.getRelatedEvents(resource, eventMgr, ts));
				break;
			}
			if (this.isAgentId(resource, ts)){
				events.addAll(eventMgr.getAgentRelatedEventIds(resource, ts));
				break;
			}
			// it's just a resource - get all Statements in appears in, and
			// get events related to those statements
			Set<URI>stmts = this.getRelatedStatementIds(resource, null, stmtmgr,
					discomgr, ts);
			for (URI stmt:stmts){
				events.addAll(stmtmgr.getRelatedEvents(stmt, eventMgr, ts));
			}
		}while (false);
		return events;
	}
	
	public Set<URI> getRelatedAgents(URI uri, RMapStatus statusCode, 
			ORMapStatementMgr stmtmgr, ORMapDiSCOMgr discomgr, ORMapEventMgr eventMgr, 
			ORMapAgentMgr agentmgr, SesameTriplestore ts) 
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		Set<URI>agents = new HashSet<URI>();		
		do {
			if (this.isDiscoId(uri, ts)){
				agents.addAll(discomgr.getRelatedAgents(uri, statusCode, eventMgr, ts));
				break;	
			}// end DiSCO				
			if (this.isStatementId(uri, ts)){
				agents.addAll(stmtmgr.getRelatedAgents(uri, statusCode, eventMgr, discomgr, ts));
				break;
			}// end Statement
			if (this.isEventId(uri, ts)){
				agents.addAll(eventMgr.getRelatedAgents(uri, ts));;				
				break;
			}// end Event
			if (this.isAgentId(uri, ts)){
				agents.addAll(agentmgr.getRelatedAgents(uri, statusCode, ts));
				break;
			}
			// just a resource
			agents.addAll(this.getResourceRelatedAgents(uri, statusCode, stmtmgr, discomgr, eventMgr, agentmgr, ts));
			break;
		} while (false);
		return agents;
	}
	
	/**
	 * Figure out the object type for object containing a triple in which a resource appears, 
	 * and find any agents associated with that object
	 * @param uri
	 * @param statusCode
	 * @param stmtmgr
	 * @param discomgr
	 * @param eventMgr
	 * @param agentmgr
	 * @param ts
	 * @return
	 */
	protected Set<URI> getResourceRelatedAgents(URI uri, RMapStatus statusCode, 
			ORMapStatementMgr stmtmgr, ORMapDiSCOMgr discomgr, ORMapEventMgr eventMgr, 
			ORMapAgentMgr agentmgr, SesameTriplestore ts){
		Set<URI>agents = new HashSet<URI>();
		List<Statement>stmts = this.getRelatedTriples(uri, ts);
		for (Statement stmt:stmts){
			if (!(stmt.getContext() instanceof URI)){
				continue;
			}
			URI id = (URI) stmt.getContext();
			do {
				if (this.isDiscoId(id, ts)){
					if (statusCode != null){
						RMapStatus dStatus = discomgr.getDiSCOStatus(uri, ts);
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
					break;
				}
				if (this.isStatementId(id, ts)){
					if (statusCode != null){
						RMapStatus status = stmtmgr.getStatementStatus(uri, discomgr, ts);
						if (!(status.equals(statusCode))){
							break;
						}
					}
					//For each event associated with statement ID, return AssociatedAgent
					List<URI>events = stmtmgr.getRelatedEvents(uri, eventMgr, ts);
					for (URI event:events){
						URI assocAgent = eventMgr.getEventAssocAgent(event, ts);
						agents.add(assocAgent);
					}
					break;
				}
				if (this.isEventId(id, ts)){
					agents.addAll(eventMgr.getRelatedAgents(id, ts));
					break;
				}
				if (this.isAgentId(id, ts)){
					if (statusCode != null){
						RMapStatus status = agentmgr.getAgentStatus(uri, ts);
						if (!(status.equals(statusCode))){
							break;
						}
					}
					agents.add(id);
					ORMapAgent agent = null;
					try{
						agent = agentmgr.readAgent(uri, ts);
					}
					catch (RMapTombstonedObjectException RMapDeletedObjectException ){
						break;
					}
					catch (Exception e){
						throw new RMapException(e);
					}
					agents.add((URI)(agent.getCreatorStmt().getObject()));
					break;
				}
				break;
			} while (false);
		}	
		return agents;
	}
	/**
	 * Find types of resource in specfic context
	 * @param rUri resource whose type is being checked
	 * @param cUri context in which to check
	 * @param ts Triplestore
	 * @return Set of URIs that are type(s) of resource in given context, or null if none found
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public Set<URI> getResourceRdfTypes(URI rUri, URI cUri,  SesameTriplestore ts)
			throws RMapException, RMapDefectiveArgumentException {
		if (rUri==null || cUri == null || ts == null){
			throw new RMapDefectiveArgumentException ("Null parameter");
		}
		List<Statement> triples = null;
		try {
			triples = ts.getStatements(rUri, RDF.TYPE, null, cUri);
		} catch (Exception e) {
			throw new RMapException (e);
		}	
		Set<URI> returnSet = null;
		if (triples!=null && triples.size()>0){
			returnSet = new HashSet<URI>();
			for (Statement stmt:triples){
				Value object = stmt.getObject();
				if (object instanceof URI){
					returnSet.add((URI)object);
				}
			}
			// correct if only triples found had non-URI objects
			if (returnSet.size()==0){
				returnSet = null;
			}
		}
		return returnSet;
	}
	/**
	 * Find types of resource in specfic context
	 * @param rUri resource whose type is being checked
	 * @param ts TripleStore
	 * @return Map from context to any types found for that resource in that context, or null if no type statement
	 * found for resource in any context
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public Map<URI, Set<URI>> getResourceRdfTypesAllContexts(URI rUri, SesameTriplestore ts) 
			throws RMapException, RMapDefectiveArgumentException {
		if (rUri==null || ts == null){
			throw new RMapDefectiveArgumentException ("Null parameter");
		}
		List<Statement> triples = null;	
		try {
			triples = ts.getStatementsAnyContext(rUri, RDF.TYPE, null, false);
		} catch (Exception e) {
			throw new RMapException (e);
		}	
		Map<URI, Set<URI>> map = null;
		if (triples !=null && triples.size()>0){
			map = new HashMap<URI, Set<URI>>();
			for (Statement stmt:triples){
				Resource context = stmt.getContext();
				if (!(context instanceof URI)){
					continue;
				}
				Value object = stmt.getObject();
				if (!(object instanceof URI)){
					continue;
				}
				URI uContext = (URI)context;
				URI uObject = (URI)object;
				if (map.containsKey(uContext)){
					Set<URI>types = map.get(uContext);
					types.add(uObject);
				}
				else {
					Set<URI>types = new HashSet<URI>();
					types.add(uObject);
					map.put(uContext, types);
				}
			}
			if (map.keySet().size()==0){
				map = null;
			}
		}
		return map;
	}
}

package info.rmapproject.core.rmapservice.impl.openrdf;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;

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
	public List<Statement> getRelatedTriples(URI resource, SesameTriplestore ts)
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
	 * @throws RMapException
	 */
	public Set<URI> getRelatedStatements(URI uri, RMapStatus statusCode,
			ORMapStatementMgr stmtmgr, ORMapDiSCOMgr discomgr, SesameTriplestore ts) 
			throws RMapException {
		// get all Statements with uri in subject or object
		List<Statement>stmts = this.getRelatedTriples(uri, ts);
		// now make sure Statement status is same as statusCode
		// context of each statement is URI of disco containing it
		List<Statement>statusStmts = new ArrayList<Statement>();
		if (statusCode==null){
			statusStmts.addAll(stmts);
		}
		else {
			for (Statement stmt:stmts){
				URI context = (URI)stmt.getContext();
				if (this.isDiscoId(context, ts)){
					RMapStatus dStatus = discomgr.getDiSCOStatus(context, ts);
					if (dStatus.equals(statusCode)){
						statusStmts.add(stmt);				
					}
				}
			}
		}
		// now get the ids of the statements
		Set<URI> relatedStmtIds = new TreeSet<URI>();
		for (Statement stmt:statusStmts){
			try{
				URI stmId = stmtmgr.getStatementID(stmt.getSubject(),
						stmt.getPredicate(), stmt.getObject(), ts);
				relatedStmtIds.add(stmId);
			} 
			catch (RMapException e){}
		}
		return relatedStmtIds;
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
		Set<URI> discos = new TreeSet<URI>();
		// make sure DiSCO in which statement appears matches statusCode
		for (Statement stmt:stmts){
			URI context = (URI)stmt.getContext();
			if (statusCode==null){
				// match any status
				discos.add(context);
			}
			else {
				RMapStatus dStatus = discomgr.getDiSCOStatus(context, ts);
				if (dStatus.equals(statusCode)){
					discos.add(context);
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
	 */
	public Set<URI> getRelatedEvents(URI resource,ORMapStatementMgr stmtmgr, 
			ORMapDiSCOMgr discomgr, ORMapEventMgr eventMgr, SesameTriplestore ts) {
		Set<URI>events = new TreeSet<URI>();
		do {
			if (this.isDiscoId(resource, ts)){
				events.addAll(eventMgr.getDiscoRelatedEventIds(resource, ts));
				break;
			}
			if (this.isStatementId(resource, ts)){
				events.addAll(stmtmgr.getRelatedEvents(resource, eventMgr, ts));
				break;
			}
			if (this.isAgentId(resource, ts)){
				//TODO need agentmgr with getRelatedEvents() method
				break;
			}
			// it's just a resource - get all Statements in appears in, and
			// get events related to those statements
			Set<URI>stmts = this.getRelatedStatements(resource, null, stmtmgr,
					discomgr, ts);
			for (URI stmt:stmts){
				events.addAll(stmtmgr.getRelatedEvents(stmt, eventMgr, ts));
			}
		}while (false);
		return events;
	}
}

package info.rmapproject.core.rmapservice.impl.openrdf;

import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapDiSCONotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *  @author khanson, smorrissey
 *
 */

public class ORMapStatementMgr extends ORMapObjectMgr {
	
	/**
	 * Get DiSCO URIs that contains Statement corresponding to subject, predicate, object provided
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param ts 
	 * @return List of DiSCO URIs
	 * @throws RMapException
	 */

	public List<URI> getRelatedDiSCOs (URI subject, URI predicate, Value object, RMapStatus statusCode, 
		Date dateFrom, Date dateTo, List<java.net.URI> systemAgents, ORMapDiSCOMgr discomgr,
		SesameTriplestore ts) throws RMapObjectNotFoundException, RMapException {
		// get all Statements with uri in subject or object

		List<Statement> stmts = null;
		try {
			stmts = ts.getStatements(subject, predicate, object);
		} catch (Exception e) {
			throw new RMapException (e);
		}		
				
		List<URI> discos = new ArrayList<URI>();
		// make sure DiSCO in which statement appears matches statusCode
		for (Statement stmt:stmts){
			URI context = (URI)stmt.getContext();

			if (context != null && this.isDiscoId(context, ts)){
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
	 * Get Agent URIs that contains Statement corresponding to subject, predicate, object provided
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param ts 
	 * @return List of Agent URIs
	 * @throws RMapException
	 */
	public List<URI> getRelatedAgents (Resource subject, URI predicate, Value object, RMapStatus statusCode, ORMapAgentMgr agentmgr,
			SesameTriplestore ts) throws RMapObjectNotFoundException, RMapException {
			// get all Statements with uri in subject or object

			List<Statement> stmts = null;
			try {
				stmts = ts.getStatements(subject, predicate, object);
			} catch (Exception e) {
				throw new RMapException (e);
			}		
					
			List<URI> agents = new ArrayList<URI>();
			// make sure Agent in which statement appears matches statusCode
			for (Statement stmt:stmts){
				URI context = (URI)stmt.getContext();

				if (context != null && this.isAgentId(context, ts)){
					if (statusCode==null){
						// match any status
						agents.add(context);
					}
					else {
						try {
							RMapStatus aStatus = agentmgr.getAgentStatus(context, ts);
							
							if (aStatus.equals(statusCode)){
								agents.add(context);
							}
						}
						catch (RMapAgentNotFoundException rf){}
						catch (RMapException r){throw r;}
					}
				}
			}
			return agents;		
		}

	


	/**
	 * Get Agent URIs that asserted the Statement corresponding to subject, predicate, object provided
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param eventmgr 
	 * @param discomgr
	 * @param agentmgr
	 * @param ts 
	 * @return List of Agent URIs
	 * @throws RMapException
	 */
	public Set<URI> getAssertingAgents (Resource subject, URI predicate, Value object, RMapStatus statusCode, Date dateFrom, Date dateTo,
					ORMapEventMgr eventmgr, ORMapDiSCOMgr discomgr, ORMapAgentMgr agentmgr,
					SesameTriplestore ts) throws RMapObjectNotFoundException, RMapException {
			
			List<Statement> stmts = null;
			try {
				stmts = ts.getStatements(subject, predicate, object);
			} catch (Exception e) {
				throw new RMapException (e);
			}		
											
			Set<URI> agents = null;
			// make sure Agent in which statement appears matches statusCode
			for (Statement stmt:stmts){
				URI context = (URI)stmt.getContext();
				if (context != null && this.isAgentId(context, ts)){
					agents = agentmgr.getAssertingAgents(context, statusCode, dateFrom, dateTo, eventmgr, ts);
				}
				else if (context != null && this.isDiscoId(context, ts)){
					agents = discomgr.getAssertingAgents(context, statusCode, dateFrom, dateTo, eventmgr, ts);
				}
			}
			return agents;		
		}


	
	
	
}

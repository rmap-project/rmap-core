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


public class ORMapResourceMgr extends ORMapObjectMgr {

	/**
	 * 
	 */
	public ORMapResourceMgr() {
		super();
	}
	/**
	 * 
	 * @param uri
	 * @param statusCode
	 * @param stmtmgr
	 * @param discomgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getRelatedStatements(URI uri, RMapStatus statusCode,
			ORMapStatementMgr stmtmgr, ORMapDiSCOMgr discomgr, SesameTriplestore ts) 
			throws RMapException {
		// get all Statements with uri in subject or object
		List<Statement>stmts = stmtmgr.getRelatedTriples(uri,ts);
		// now make sure Statement status is same as statusCode
		// context of each statement is URI of disco containing it
		List<Statement>statusStmts = new ArrayList<Statement>();
		if (statusCode==null){
			statusStmts.addAll(stmts);
		}
		else {
			for (Statement stmt:stmts){
				URI context = (URI)stmt.getContext();
				RMapStatus dStatus = discomgr.getDiSCOStatus(context, ts);
				if (dStatus.equals(statusCode)){
					statusStmts.add(stmt);				
				}
			}
		}
		// now get the ids of the statements
		List<URI> relatedStmtIds = new ArrayList<URI>();
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
	 * @param stmtmgr
	 * @param discomgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public Set<URI> getRelatedDiSCOS(URI uri, RMapStatus statusCode,
			ORMapStatementMgr stmtmgr, ORMapDiSCOMgr discomgr, SesameTriplestore ts)
			throws RMapException {
		// get all Statements with uri in subject or object
		List<Statement>stmts = stmtmgr.getRelatedTriples(uri,ts);
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
}

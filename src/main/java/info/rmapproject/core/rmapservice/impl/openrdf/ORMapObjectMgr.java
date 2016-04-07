/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.vocabulary.impl.openrdf.RMAP;

import java.util.List;

import org.openrdf.model.IRI;
import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.RDF;

/**
 *  @author khanson, smorrissey
 *
 */
public abstract class ORMapObjectMgr {
	
	/**
	 * 
	 */
	protected ORMapObjectMgr() {}

	/**
	 * 
	 * @param ts
	 * @param stmt
	 * @throws RMapException
	 */
	public void createTriple(SesameTriplestore ts, Statement stmt) throws RMapException {
		try {
			ts.addStatement(stmt);
		} catch (Exception e) {
			throw new RMapException ("Exception thrown creating triple from ORMapStatement ", e);
		}
		return;
	}
	
	
	public boolean isRMapType(SesameTriplestore ts, IRI id, IRI typeIRI) throws RMapException {
		if (ts==null || id==null || typeIRI==null){
			throw new RMapException("Null parameter passed");
		}
		boolean isCorrectType = false;
		try {
			List<Statement> stmts = ts.getStatementsAnyContext(id, RDF.TYPE, typeIRI, false);
			if (stmts != null && stmts.size()>0){
				isCorrectType = true;
			}
		} catch (Exception e) {
			throw new RMapException ("Exception thrown searching for object " + id.stringValue(), e);
		}		
		return isCorrectType;	
	}

	/**
	 * Confirm that IRI is a DiSCO id
	 * @param discoId
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public boolean isDiscoId(IRI id, SesameTriplestore ts) throws RMapException {	
		return this.isRMapType(ts, id, RMAP.DISCO);		
	}
	
	/**
	 * Confirm that a IRI is an Event identifier
	 * @param id
	 * @return
	 * @throws RMapException
	 */
	public boolean isEventId (IRI id, SesameTriplestore ts) throws RMapException {	
		return this.isRMapType(ts, id, RMAP.EVENT);
	}
	/**

	/**
	 * 
	 * @param id
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public boolean isAgentId(IRI id, SesameTriplestore ts) throws RMapException {	
		return this.isRMapType(ts, id, RMAP.AGENT);		
	}
	
	/**
	 * 
	 * @param stmtId
	 * @param ts 
	 * @return
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 */
	protected List<Statement> getNamedGraph(IRI id, SesameTriplestore ts) throws RMapObjectNotFoundException, RMapException {
		List<Statement> matchingTriples = null;
		try {
			matchingTriples = ts.getStatements(null, null, null, false, id);     
		} catch (Exception e) {
			throw new RMapException("Exception fetching triples matching named graph id "
					+ id.stringValue(), e);
		}
		if (matchingTriples.isEmpty()){
			throw new RMapObjectNotFoundException("could not find triples matching named graph id " + id.toString());
		}
		return matchingTriples;
	}
	
}

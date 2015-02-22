/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.impl.openrdf.ORMapStatement;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

/**
 *  @author khansen, smorrissey
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
	public void createTriple(SesameTriplestore ts, ORMapStatement stmt) throws RMapException {
		try {
			ts.addStatement(stmt.getRmapStmtStatement());
		} catch (Exception e) {
			throw new RMapException ("Exception thrown creating triple from ORMapStatement ", e);
		}
		return;
	}
	
	public boolean isRMapType(SesameTriplestore ts, URI id, URI typeURI) throws RMapException {
		if (ts==null || id==null || typeURI==null){
			throw new RMapException("Null parameter passed");
		}
		boolean isCorrectType = false;
		try {
			Statement stmt = ts.getStatement(id, RDF.TYPE, typeURI, null);
			if (stmt != null){
				isCorrectType = true;
			}
		} catch (Exception e) {
			throw new RMapException ("Exception thrown searching for object " + id.stringValue(), e);
		}		
		return isCorrectType;	
	}
	/**
	 * Confirm that URI is Statement id
	 * @param id
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public boolean isStatementId(URI id, SesameTriplestore ts) throws RMapException {	
		return this.isRMapType(ts, id, RMAP.STATEMENT);		
	}
	/**
	 * Confirm that URI is a DiSCO id
	 * @param discoId
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public boolean isDiscoId(URI id, SesameTriplestore ts) throws RMapException {	
		return this.isRMapType(ts, id, RMAP.DISCO);		
	}
	
	/**
	 * Confirm that a URI is an Event identifier
	 * @param id
	 * @return
	 * @throws RMapException
	 */
	public boolean isEventId (URI id, SesameTriplestore ts) throws RMapException {	
		return this.isRMapType(ts, id, RMAP.EVENT);		
	}
	/**
	 * 
	 * @param id
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public boolean isAgentId(URI id, SesameTriplestore ts) throws RMapException {	
		return this.isRMapType(ts, id, RMAP.AGENT);		
	}
	
	/**
	 * 
	 * @param stmtId
	 * @param ts 
	 * @return
	 * @throws RMapException
	 */
	protected List<Statement> getNamedGraph(URI id, SesameTriplestore ts) throws RMapException {
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

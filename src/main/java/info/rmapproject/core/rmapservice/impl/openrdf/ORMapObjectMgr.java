/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.vocabulary.impl.openrdf.RMAP;

import java.util.List;
import java.util.Set;

import org.openrdf.model.IRI;
import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.RDF;

/**
 * An abstract class for openrdf versions of RMap Objects, implemented using openrdf
 *
 * @author khanson, smorrissey
 */
public abstract class ORMapObjectMgr {
	
	/**
	 * Instantiates a new RMap Object Manager
	 */
	protected ORMapObjectMgr() {}

	/**
	 * Creates a triple in the RMap database
	 *
	 * @param ts the triplestore instance
	 * @param stmt the statement to be persisted
	 * @throws RMapException the RMap exception
	 */
	public void createStatement(SesameTriplestore ts, Statement stmt) throws RMapException {
		try {
			ts.addStatement(stmt);
		} catch (Exception e) {
			throw new RMapException ("Exception thrown creating triple from ORMapStatement ", e);
		}
		return;
	}
	
	
	/**
	 * Looks up an IRI's type in the database to see if it matches the type IRI provided
	 *
	 * @param ts the triplestore instance
	 * @param id the IRI to be checked
	 * @param typeIRI the type IRI to be checked against
	 * @return true, if the id parameter has the type specified in typeIRI
	 * @throws RMapException the RMap exception
	 */
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
	 * Confirm that IRI is a DiSCO IRI.
	 *
	 * @param id the IRI to be type checked
	 * @param ts the triplestore instance
	 * @return true, if the IRI is a DiSCO IRI
	 * @throws RMapException the RMap exception
	 */
	public boolean isDiscoId(IRI id, SesameTriplestore ts) throws RMapException {	
		return this.isRMapType(ts, id, RMAP.DISCO);		
	}
	
	/**
	 * Confirm that IRI is a Event IRI.
	 *
	 * @param id the IRI to be type checked
	 * @param ts the triplestore instance
	 * @return true, if the IRI is a Event IRI
	 * @throws RMapException the RMap exception
	 */
	public boolean isEventId (IRI id, SesameTriplestore ts) throws RMapException {	
		return this.isRMapType(ts, id, RMAP.EVENT);
	}
	
	/**
	 * Confirm that IRI is an Agent IRI.
	 *
	 * @param id the IRI to be type checked
	 * @param ts the triplestore instance
	 * @return true, if the IRI is an Agent IRI
	 * @throws RMapException the RMap exception
	 */
	public boolean isAgentId(IRI id, SesameTriplestore ts) throws RMapException {	
		return this.isRMapType(ts, id, RMAP.AGENT);		
	}
	
	/**
	 * Gets the named graph for an object using it's named graph IRI (or "context").
	 * The named graph is represented as a list of Statements
	 *
	 * @param id the IRI of the named graph in the database
	 * @param ts the triplestore instance
	 * @return a list of statements that form the named graph
	 * @throws RMapObjectNotFoundException the RMap object not found exception
	 * @throws RMapException the RMap exception
	 */
	protected Set<Statement> getNamedGraph(IRI id, SesameTriplestore ts) throws RMapObjectNotFoundException, RMapException {
		Set<Statement> matchingTriples = null;
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

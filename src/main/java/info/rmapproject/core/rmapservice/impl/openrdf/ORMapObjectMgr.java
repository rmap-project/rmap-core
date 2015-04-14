/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
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
	public void createTriple(SesameTriplestore ts, Statement stmt) throws RMapException {
		try {
			ts.addStatement(stmt);
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
			List<Statement> stmts = ts.getStatementsAnyContext(id, RDF.TYPE, typeURI, false);
			if (stmts != null && stmts.size()>0){
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
//	/**
//	 * 
//	 * @param id
//	 * @param ts
//	 * @return
//	 * @throws RMapException
//	 */
//	public boolean isProfileId(URI id, SesameTriplestore ts) throws RMapException {	
//		return this.isRMapType(ts, id, RMAP.PROFILE);		
//	}
//	/**
//	 * 
//	 * @param id
//	 * @param ts
//	 * @return
//	 * @throws RMapException
//	 */
//	public boolean isIdentityId (URI id, SesameTriplestore ts) throws RMapException {
//		return this.isRMapType(ts, id, RMAP.IDENTITY);
//	}
	
	/**
	 * 
	 * @param stmtId
	 * @param ts 
	 * @return
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 */
	protected List<Statement> getNamedGraph(URI id, SesameTriplestore ts) throws RMapObjectNotFoundException, RMapException {
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
	/**
	 * Replaces any occurrences of BNodes in list of statements with RMapidentifier URIs
	 * @param stmts
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected List<Statement> replaceBNodeWithRMapId(List<Statement> stmts, SesameTriplestore ts) throws RMapException {
		if (stmts==null){
			throw new RMapException ("null stmts");
		}
		List<Statement>newStmts = new ArrayList<Statement>();
		Map<BNode, URI> bnode2uri = new HashMap<BNode, URI>();
		for (Statement stmt:stmts){
			Resource subject = stmt.getSubject();
			Value object = stmt.getObject();
			BNode bSubject = null;
			BNode bObject = null;
			if (subject instanceof BNode ) {
				bSubject = (BNode)subject;				
			}
			if (object instanceof BNode){
				bObject = (BNode)object;
			}
			if (bSubject==null && bObject==null){
				newStmts.add(stmt);
				continue;
			}
			Resource newSubject = null;
			Value newObject = null;
			// if subject is BNODE, replace with URI (if necessary, create the URI and add mapping)
			if (bSubject != null){
				URI bReplace = bnode2uri.get(bSubject);
				if (bReplace==null){
					java.net.URI newId=null;
					try {
						newId = IdServiceFactoryIOC.getFactory().createService().createId();
					} catch (Exception e) {
						throw new RMapException (e);
					}
					bReplace = ORAdapter.uri2OpenRdfUri(newId);
					bnode2uri.put(bSubject, bReplace);
					newSubject = bReplace;
				}
				else {
					newSubject = bReplace;
				}
			}
			else {
				newSubject = subject;
			}
			// if object is BNODE, replace with URI (if necessary, create the URI and add mapping)
			if (bObject != null){
				URI bReplace = bnode2uri.get(bObject);
				if (bReplace==null){
					java.net.URI newId=null;
					try {
						newId = IdServiceFactoryIOC.getFactory().createService().createId();
					} catch (Exception e) {
						throw new RMapException (e);
					}
					bReplace = ORAdapter.uri2OpenRdfUri(newId);
					bnode2uri.put(bObject, bReplace);
					newObject = bReplace;
				}
				else {
					newObject = bReplace;
				}
			}
			else {
				newObject = object;
			}
			// now create new statement with bnodes replaced
			Statement newStmt=null;
			try {
				newStmt = ts.getValueFactory().createStatement(newSubject, stmt.getPredicate(), newObject);
			} catch (RepositoryException e) {
				e.printStackTrace();
				throw new RMapException (e);
			}
			newStmts.add(newStmt);
			continue;
		}
		return newStmts;
	}
	
	/**
	 * Confirm 2 identifiers refer to the same creating agent
	 * @param uri
	 * @param systemAgentId
	 * @param eventmgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected boolean isSameCreatorAgent (URI uri, URI systemAgentId, 
			ORMapEventMgr eventmgr, SesameTriplestore ts) 
			throws RMapException {
		boolean isSame = false;
		Statement stmt = eventmgr.getRMapObjectCreateEventStatement(uri, ts);
		do {
			if (stmt==null){
				break;
			}
			if (! (stmt.getSubject() instanceof URI)){
				throw new RMapException ("Event ID is not URI: " + stmt.getSubject().stringValue());
			}
			URI eventId = (URI)stmt.getSubject();
			URI createAgent = eventmgr.getEventAssocAgent(eventId, ts);
			isSame = (systemAgentId.stringValue().equals(createAgent.stringValue()));
		}while (false);
		return isSame;
	}
}

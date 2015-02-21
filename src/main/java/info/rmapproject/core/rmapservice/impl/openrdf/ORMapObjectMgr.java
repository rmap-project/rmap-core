/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapEventType;
import info.rmapproject.core.model.impl.openrdf.ORMapStatement;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

/**
 * @author smorrissey
 *
 */
public abstract class ORMapObjectMgr {

	/**
	 * 
	 */
	protected ORMapObjectMgr() {}
	
	public ORMapService getORMapService() throws RMapException{
		RMapService rservice = null;
		try {
			rservice = RMapServiceFactoryIOC.getFactory().createService();
		} catch (Exception e) {
			throw new RMapException (e);
		}
		ORMapService service = null;
		if (!(rservice instanceof ORMapService)){		
			throw new RMapException("Unable to instantiate OpenRDF service implmentation.");
		}
		service = (ORMapService)rservice;
		return service;
	}
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
			Statement stmt = ts.getStatement(id, RDF.TYPE, typeURI, id);
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
	/**
	 * 
	 * @param eventId
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected RMapEventType getEventType (URI eventId, SesameTriplestore ts) 
	throws RMapObjectNotFoundException, RMapException{
		if (eventId == null){
			throw new RMapException("null eventID");
		}
		if (ts==null){
			throw new RMapException ("null triplestore");
		}
		Value type = null;
		Statement stmt = null;
		try {
			stmt = ts.getStatement(eventId, RMAP.EVENT_TYPE, null, eventId);
		} catch (Exception e) {
			throw new RMapException ("Exception thrown getting event type for " 
					+ eventId.stringValue(), e);
		}
		if (stmt == null){
			throw new RMapObjectNotFoundException("No event type statement found for ID " +
		            eventId.stringValue());
		}
		else {
			type = stmt.getObject();
		}
		RMapEventType eType = RMapEventType.getEventTypeFromString(type.stringValue());
		return eType;
	}
	/**
	 * 
	 * @param eventId
	 * @param ts
	 * @return
	 */
	protected boolean isCreationEvent(URI eventId, SesameTriplestore ts){
		RMapEventType et = this.getEventType(eventId, ts);
		return et.equals(RMapEventType.CREATION);
	}
	/**
	 * 
	 * @param eventId
	 * @param ts
	 * @return
	 */
	protected boolean isUpdateEvent(URI eventId, SesameTriplestore ts){
		RMapEventType et = this.getEventType(eventId, ts);
		return et.equals(RMapEventType.UPDATE);
	}
	/**
	 * 
	 * @param eventId
	 * @param ts
	 * @return
	 */
	protected boolean isTombstoneEvent(URI eventId, SesameTriplestore ts){
		RMapEventType et = this.getEventType(eventId, ts);
		return et.equals(RMapEventType.TOMBSTONE);
	}
	/**
	 * 
	 * @param eventId
	 * @param ts
	 * @return
	 */
	protected boolean isDeleteEvent(URI eventId, SesameTriplestore ts){
		RMapEventType et = this.getEventType(eventId, ts);
		return et.equals(RMapEventType.DELETION);
	}
	
	/**
	 * 
	 * @param targetId
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected List<Statement> getInactivateEvents(URI targetId, SesameTriplestore ts)
			throws RMapException {
		List<Statement> stmts = null;
		List<Statement> returnStmts = new ArrayList<Statement>();
		try {
			stmts = ts.getStatements(null, RMAP.EVENT_TARGET_INACTIVATED, targetId);
			for (Statement stmt:stmts){
				// make sure this is an event
				if (stmt != null && stmt.getSubject().equals(stmt.getContext())){
					Statement typeStmt = ts.getStatement(stmt.getSubject(), RDF.TYPE, 
							RMAP.EVENT, stmt.getContext());
					if (typeStmt==null){
						stmt = null;
					}
				}
				else {
					stmt = null;
				}
				if (stmt != null){
					returnStmts.add(stmt);
				}
			}
		} catch (Exception e) {
			throw new RMapException (
					"Exception thrown when querying for Inactivate event for id " 
							+ targetId.stringValue(), e);
		}		
		return returnStmts;
	}
	
	/**
	 * 
	 * @param targetId
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected List<Statement> getUpdateEvents(URI targetId, SesameTriplestore ts)
			throws RMapException {
		List<Statement> stmts = null;
		List<Statement> returnStmts = new ArrayList<Statement>();
		try {
			stmts = ts.getStatements(null, RMAP.EVENT_TARGET_DERIVATION_SOURCE, targetId);
			for (Statement stmt:stmts){
				// make sure this is an event
				if (stmt != null && stmt.getSubject().equals(stmt.getContext())){
					Statement typeStmt = ts.getStatement(stmt.getSubject(), RDF.TYPE, 
							RMAP.EVENT, stmt.getContext());
					if (typeStmt==null){
						stmt = null;
					}
				}
				else {
					stmt = null;
				}
				if (stmt != null){
					returnStmts.add(stmt);
				}
			}
		} catch (Exception e) {
			throw new RMapException (
					"Exception thrown when querying for Inactivate event for id " 
							+ targetId.stringValue(), e);
		}		
		return returnStmts;
	}
	
	/**
	 * Get identifier for the system agent associated an event
	 * @param event
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected URI getEventAssocAgent (URI event, SesameTriplestore ts) throws RMapException {
		URI agent = null;
		Statement stmt = null;
		try {
			stmt = ts.getStatement(event, PROV.WASASSOCIATEDWITH, null, event);
		} catch (Exception e) {
			throw new RMapException ("Exception thrown when querying for event associated agent", e);
		}
		if (stmt!=null){
			Value vAgent = stmt.getObject();
			if (vAgent instanceof URI){
				agent = (URI)vAgent;
			}
			else {
				throw new RMapException ("Associated Agent ID is not URI");
			}
		}
		else {
			throw new RMapException ("No system agent associated with event " + event.toString());
		}
		return agent;
	}
}

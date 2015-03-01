package info.rmapproject.core.rmapservice.impl.openrdf;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapStatement;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;

/**
 * 
 *  @author khansen, smorrisseyy
 *
 */
public class ORMapStatementMgr extends ORMapObjectMgr {

	public static final String SCHEME = "http";
	public static final String AUTHORITY = "http://rmap-project.org";
	public static final String PATH = "/statementContextPrefix/";
	private final Logger log = LogManager.getLogger(this.getClass());
	/**
	 * 
	 */
	public ORMapStatementMgr() {
		super();
	}
	
	/**
	 * Create a new RMapStatement in the repository. 
	 * 
	 * A Statement is always created in the context of DiSCO. If context is null, raise an exception
	 * 
	 * This method does NOT create the triples in a DiSCO.  That is done directly in createDisco() method.
	 *  
	 * If this is the first time the triple is seen create a reified RMapStatement 
	 * (with rdf:type, rdf:subject, rdf:object, 
	 * rdf:predicate), whose Resource identifier will be used going forward as
	 * the "id" for all occurrences of this statement (when service queries by statement ID)
	 *   
	 * Context for all statements in the reified statement will be the concatenation of
	 * subject, predicate, object (with RMAP namespace prefix)
	 *
	 * @param rmapStatement
	 * @param ts 
	 * @return Resource URi of the reified statement corresponding to this triple
	 * @throws Exception
	 */
	public URI createStatement(Statement rmapStatement, SesameTriplestore ts) 
			throws RMapException {	
		if (rmapStatement == null){
			throw new RMapException("Null RMapStatement");
		}
		Resource context = rmapStatement.getContext();
		if (context==null){
			// Statements should be coming in here with DiSCO id as context
			throw new RMapException("Null Context in ORMapStatement");
		}
		URI stmtId = this.getStatementID(rmapStatement.getSubject(), 
				rmapStatement.getPredicate(), rmapStatement.getObject(), ts);
		if (stmtId==null){
			// first time we are seeing this triple:  create reified statement before adding simple triple
			stmtId = this.createReifiedStatement(rmapStatement, ts);
		}						
		return stmtId;
	}
	/**
	 * Create triples that comprise reified version of statement
	 * Context of triples for each statement will be concatenation of subject, predicate, object
	 * @param rmapStatement Statement to be reified
	 * @param ts 
	 * @return URI that is identifier for reified statements
	 * @throws Exception
	 */
	protected URI createReifiedStatement (Statement rmapStatement, SesameTriplestore ts ) 
			throws RMapException {
		String contextString = this.createContextURIString(rmapStatement);
		URI context = ORAdapter.getValueFactory().createURI(contextString);	
		java.net.URI stmtURI;
		try {
			stmtURI = IdServiceFactoryIOC.getFactory().createService().createId();
		} catch (Exception e1) {
			throw new RMapException("failed to create new ID",e1);
		}
		URI stmtId = ORAdapter.uri2OpenRdfUri(stmtURI);
		try {
			URI predicate = RDF.TYPE;
			Value object = RMAP.STATEMENT;
			ts.addStatement(stmtId, predicate,object,context);	
			
			predicate = RDF.SUBJECT;
			object = rmapStatement.getSubject();
			ts.addStatement(stmtId, predicate,object,context);	
			
			predicate = RDF.PREDICATE;
			object = rmapStatement.getPredicate();
			ts.addStatement(stmtId, predicate,object,context);	
			
			predicate = RDF.OBJECT;
			object = rmapStatement.getObject();
			ts.addStatement(stmtId, predicate,object,context);	
		} catch (Exception e){
			throw new RMapException("Exception creating reified statement", e);
		}		
		return stmtId;
	}

	/**
	 * Construct valid URI string from concatenated Statement subject, predicate, object
	 * @param contextString
	 * @return
	 * @throws RMapException
	 */
	public String createContextURIString(Statement rmapStatement) throws RMapException {
		if (rmapStatement == null){
			throw new RMapException("Null RMapStatement");
		}
		// Construct the context by concatenating subject, predicate, object	
		return this.createContextURIString(rmapStatement.getSubject().toString(), 
				rmapStatement.getPredicate().toString(), rmapStatement.getObject().stringValue());
	}
			
	/**
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return
	 * @throws RMapException
	 */
	public String createContextURIString(String subject, String predicate, String object) throws RMapException {		
		StringBuffer sb = new StringBuffer();
		sb.append(subject);
		sb.append(predicate);
		sb.append(object);
		java.net.URI uri = null;
		String contextString = sb.toString();	
		try {
			uri = new java.net.URI(SCHEME, AUTHORITY, PATH, contextString, null);
		} catch (URISyntaxException e) {
			throw new RMapException("Unable to create URI string from " + contextString, e);
		}
		return uri.toASCIIString();
	}
	/**
	 * Get (reified) statement id corresponding to subject, predicate, object
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param ts 
	 * @return URI that is statement ID
	 * @throws RMapException
	 */
	public URI getStatementID (Resource subject, URI predicate, Value object, 
			SesameTriplestore ts) throws RMapObjectNotFoundException, RMapException {
		// get statements whose context matches concatenated subject/object/predicate
		if (subject==null || predicate==null || object==null){
			throw new RMapException ("Null subject or predicate or object");
		}
		String contextString = 
				this.createContextURIString(subject.stringValue(), predicate.stringValue(), 
						object.stringValue());
		URI context = ORAdapter.getValueFactory().createURI(contextString);	
		// get statements whose context matches concatenated subject/object/predicate
		List <Statement> matchingTriples = null;
		try {
			matchingTriples = ts.getStatements(null, null, null, false, context);
		} catch (Exception e) {
			throw new RMapException (e);
		}
		if (matchingTriples.size()==0){
			throw new RMapObjectNotFoundException("No Statement found with subject " + subject.stringValue() +
					"  predicate " + predicate.stringValue() + " object " + object.stringValue());
		}
		Statement stmt = matchingTriples.get(0);		
		Resource rSubject = stmt.getSubject();
		URI id = null;
		if (rSubject instanceof URI){
			id = (URI)rSubject;
		}
		else {
			throw new RMapException("NON-URI Statement Subject: " + rSubject.stringValue());
		}
		URI stmtId = null;
		if (this.isStatementId(id, ts)){
			stmtId = id;
		}
		return stmtId;
	}
    /**
     * "Un-reifies" statements matching stmtid
     * @param stmtId
     * @param ts 
     * @return
     * @throws RMapExceptions
     */
	public ORMapStatement getRMapStatement(URI stmtId, SesameTriplestore ts) 
	throws RMapObjectNotFoundException, RMapException {
		ORMapStatement rmapStatement = null;		
		if ((stmtId==null) || (stmtId.toString().length()==0)){
			log.info("Invalid parameters for getMatchingRMapStmts, cannot pass null or empty values.");
			throw new RMapException ("Null or empty stmtId");
		}
		if (! this.isStatementId(stmtId, ts)){
			throw new RMapObjectNotFoundException("No Statement found with ID " + stmtId.stringValue());
		}
		Statement subjectStmt  = null;
		Statement predicateStmt = null;
		Statement objectStmt = null;
		try {
			do {
				subjectStmt = ts.getStatementAnyContext(stmtId, RDF.SUBJECT, null);
				if (subjectStmt==null){
					break;
				}
				predicateStmt = ts.getStatementAnyContext(stmtId, RDF.PREDICATE, null);
				if (predicateStmt==null){
					break;
				}
				objectStmt = ts.getStatementAnyContext(stmtId, RDF.OBJECT, null);
				if (objectStmt==null){
					break;
				}			
			} while (false);
		} catch (Exception e){
			throw new RMapException (e);
		}
		if (subjectStmt==null || predicateStmt==null || objectStmt==null){
			throw new RMapException("stmtID is missing subject or predicate or object");
		}
		rmapStatement = new ORMapStatement(subjectStmt, predicateStmt, objectStmt);		
		return rmapStatement;
	}

	/**
	 * Determine status of RMapStatement
	 * Status of an RMapStatement depends on status of DiSCOS in which it appears
	 * @param stmtId id of Statement
	 * @return status of statement
	 * @throws RMapException
	 */
	public RMapStatus getStatementStatus(URI stmtId, ORMapDiSCOMgr discomgr, 
			SesameTriplestore ts) throws RMapException {
		if (stmtId==null){
			throw new RMapException ("Null Statement ID");
		}
		RMapStatus status = null;
		Set<URI> discos = this.getRelatedDiSCOs(stmtId, ts);
		boolean activeFound = false;
		boolean inactiveFound = false;
		boolean tombstoneFound = false;
		boolean deletedFound = false;
		for (URI disco:discos){
			RMapStatus dStatus = discomgr.getDiSCOStatus(disco, ts);
			switch (dStatus){
			case ACTIVE:
				activeFound = true;
				break;
			case INACTIVE:
				inactiveFound = true;
				break;
			case TOMBSTONED:
				tombstoneFound = true;
				break;
			case DELETED:
				deletedFound = true;
				break;
			default:
				// should never happen
				throw new RMapException("Unrecognized status code");
			}
			if (activeFound){
				// active trumps everything
				break;
			}
		}
		if (activeFound){
			status = RMapStatus.ACTIVE;
		}
		else if (inactiveFound){
			status= RMapStatus.INACTIVE;
		}
		else if (tombstoneFound){
			status = RMapStatus.TOMBSTONED;
		}
		else if (deletedFound){
			status = RMapStatus.DELETED;
		}
		else {
			// should never happen
			throw new RMapException("Unrecognized status");
		}
		return status;
	}
	/**
	 * Get id of events related to an RMapStatement
	 * @param uri
	 * @param ts 
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getRelatedEvents(URI uri, ORMapEventMgr eventmgr,
			SesameTriplestore ts) 
			throws RMapObjectNotFoundException, RMapException {

		Set<URI>discoIds = this.getRelatedDiSCOs(uri, ts);
		List<URI> events = new ArrayList<URI>();
		for (URI discoId:discoIds){
			List<URI> discoEvents = eventmgr.getDiscoRelatedEventIds(discoId, ts);
			events.addAll(discoEvents);
		}
		return events;
	}	
	
	/**
	 * Return ids of DiSCOs containing triples corresponding to reifeid statement id
	 * @param uri
	 * @param ts
	 * @return
	 * @throws RMapObjectNotFoundException
	 * @throws RMapException
	 */
	public Set<URI> getRelatedDiSCOs(URI uri, SesameTriplestore ts)
	throws RMapObjectNotFoundException, RMapException {
		if ((uri==null) || (uri.toString().length()==0)){
			throw new RMapException ("Null or empty URI for statement ID");
		}
		// confirm this is a valid Statement ID
		if (! this.isStatementId(uri, ts)){
			throw new RMapObjectNotFoundException("No Statement found with ID " + uri.stringValue());
		}	
		// get the triples for this Statement
		ORMapStatement stmt = this.getRMapStatement(uri, ts);
		// get all discos in which this statement appears
		List<Statement>triples = null;
		try {
			triples = ts.getStatements((Resource)(stmt.getSubjectStatement().getObject()), 
					(URI)(stmt.getPredicateStatement().getObject()),
					stmt.getObjectStatement().getObject());
		} catch (Exception e) {
			throw new RMapException("Exception thrown matching triples for " + uri.stringValue(), e);
		}
		Set<URI>discoIds = new TreeSet<URI>();
		for (Statement triple:triples){
			Resource context = triple.getContext();
			if (context instanceof URI){
				discoIds.add((URI)context);
			}
		}
		return discoIds;
	}

}


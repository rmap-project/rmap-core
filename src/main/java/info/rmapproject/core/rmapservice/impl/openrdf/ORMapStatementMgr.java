package info.rmapproject.core.rmapservice.impl.openrdf;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapEvent;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.model.impl.openrdf.ORMapStatement;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;


import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;

public class ORMapStatementMgr extends ORMapObjectMgr {

	public static final String SCHEME = "http";
	public static final String AUTHORITY = "http://rmap-project.org/";
	public static final String PATH = "statementContextPrefix/";
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
	public URI createStatement(ORMapStatement rmapStatement, SesameTriplestore ts) 
			throws RMapException {	
		if (rmapStatement == null){
			throw new RMapException("Null RMapStatement");
		}
		Resource context = rmapStatement.getRmapStmtContext();
		if (context==null){
			// Statements should be coming in here with DiSCO id as context
			throw new RMapException("Null Context in ORMapStatement");
		}
		URI stmtURI = this.getStatementID(rmapStatement.getRmapStmtSubject(), 
				rmapStatement.getRmapStmtPredicate(), rmapStatement.getRmapStmtObject(), ts);
		URI stmtUri = null;
		if (stmtURI==null){
			// first time we are seeing this triple:  create reified statement before adding simple triple
			stmtUri = this.createReifiedStatement(rmapStatement, ts);
		}						
		return stmtUri;
	}
	/**
	 * Create triples that comprise reified version of statement
	 * Context of triples for each statement will be concatenation of subject, predicate, object
	 * @param rmapStatement Statement to be reified
	 * @param ts 
	 * @return URI that is identifier for reified statements
	 * @throws Exception
	 */
	protected URI createReifiedStatement (ORMapStatement rmapStatement, SesameTriplestore ts ) 
			throws RMapException {
		String contextString = this.createContextURIString(rmapStatement);
		URI context = ORAdapter.getValueFactory().createURI(contextString);			
		URI stmtId = ORAdapter.uri2OpenRdfUri(rmapStatement.getId());;
		try {
			URI predicate = RDF.TYPE;
			Value object = RMAP.STATEMENT;;
			ts.addStatement(stmtId, predicate,object,context);	
			
			predicate = RDF.SUBJECT;
			object = rmapStatement.getRmapStmtSubject();
			ts.addStatement(stmtId, predicate,object,context);	
			
			predicate = RDF.PREDICATE;
			object = rmapStatement.getRmapStmtPredicate();
			ts.addStatement(stmtId, predicate,object,context);	
			
			predicate = RDF.OBJECT;
			object = rmapStatement.getRmapStmtObject();
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
	protected String createContextURIString(ORMapStatement rmapStatement) throws RMapException {
		if (rmapStatement == null){
			throw new RMapException("Null RMapStatement");
		}
		// Construct the context by concatenating subject, predicate, object		
		return this.createContextURIString(rmapStatement.getRmapStmtSubject().toString(), 
				rmapStatement.getRmapStmtPredicate().toString(), rmapStatement.getRmapStmtObject().stringValue());
	}
	/**
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return
	 * @throws RMapException
	 */
	protected String createContextURIString(String subject, String predicate, String object) throws RMapException {		
		StringBuffer sb = new StringBuffer();
		sb.append(subject);
		sb.append(predicate);
		sb.append(object);
		java.net.URI uri = null;
		String contextString = sb.toString();
		try {
			uri = new java.net.URI(SCHEME, AUTHORITY, PATH, null, contextString);
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
				this.createContextURIString(subject.stringValue(), predicate.stringValue(), object.stringValue());
		URI context = ORAdapter.getValueFactory().createURI(contextString);	
		// get statements whose context matches concatenated subject/object/predicate
		List <Statement> matchingTriples = null;
		try {
			matchingTriples = ts.getStatements(subject, predicate, object, true, context);
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
		return id;
	}
    /**
     * "Un-reifies" statements matching stmtid
     * @param stmtId
     * @param ts 
     * @return
     * @throws RMapExceptions
     */
	public ORMapStatement getRMapStatement(URI stmtId, SesameTriplestore ts) throws RMapObjectNotFoundException, RMapException {
		ORMapStatement rmapStatement = null;		
		if ((stmtId==null) || (stmtId.toString().length()==0)){
			log.info("Invalid parameters for getMatchingRMapStmts, cannot pass null or empty values.");
			throw new RMapException ("Null or empty stmtId");
		}
		if (! this.isStatementId(stmtId, ts)){
			throw new RMapObjectNotFoundException("No Statement found with ID " + stmtId.stringValue());
		}
		Resource subject = null;
		URI predicate = null;
		Value object = null;
		Value value = null;
		List<Statement> matchingTriples = this.getNamedGraph(stmtId, ts);
		for (Statement triple:matchingTriples){
			if (triple.getPredicate().equals(RDF.SUBJECT)){
				value = triple.getObject();
				if (value instanceof Resource){
					subject = (Resource)value;
					continue;
				}
				else {
					throw new RMapException("Statement " + stmtId.toString() + " subject is not of type Resource");
				}
			}
			if (triple.getPredicate().equals(RDF.PREDICATE)){
				value = triple.getObject();
				if (value instanceof URI){
					predicate = (URI)value;
					continue;
				}
				else {
					throw new RMapException("Statement " + stmtId.toString() + " predicate is not of type URI");
				}
			}
			if (triple.getPredicate().equals(RDF.OBJECT)){
				value = triple.getObject();
				object = value;
			}
			continue;
		}
		if (subject==null || predicate==null || object==null){
			throw new RMapException("stmtID is missing subject or predicate or object");
		}
		rmapStatement = new ORMapStatement(subject, predicate, object);		
		return rmapStatement;
	}

	/**
	 * Determine status of RMapStatement
	 * Status of an RMapStatement depends on status of DiSCOS in which it appears
	 * @param stmtId id of Statement
	 * @return status of statement
	 * @throws RMapException
	 */
	public RMapStatus getStatementStatus(URI stmtId) throws RMapException {
		if (stmtId==null){
			throw new RMapException ("Null Statement ID");
		}
		RMapStatus status = null;
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
		List<ORMapDiSCO> discos = service.getAllRelatedDiSCOS(stmtId,null);
		boolean activeFound = false;
		boolean inactiveFound = false;
		boolean tombstoneFound = false;
		boolean deletedFound = false;
		for (ORMapDiSCO disco:discos){
			RMapStatus dStatus = service.getDiSCOStatus(disco.getId());
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
	public List<java.net.URI> getRelatedEvents(URI uri, SesameTriplestore ts) 
			throws RMapObjectNotFoundException, RMapException {
		if ((uri==null) || (uri.toString().length()==0)){
			log.info("Invalid parameters for getRelatedEvents, cannot pass null or empty values.");
			throw new RMapException ("Null or empty URI for statement ID");
		}
		// confirm this is a valid Statement ID
		if (! this.isStatementId(uri, ts)){
			throw new RMapObjectNotFoundException("No Statement found with ID " + uri.stringValue());
		}		
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
		List<ORMapDiSCO> discos = service.getAllRelatedDiSCOS(uri, null);
		Set <java.net.URI> events = new HashSet<java.net.URI>();;
		for (ORMapDiSCO disco:discos){
			List<RMapEvent> dEvents = disco.getRelatedEvents();
			for (RMapEvent dEvent:dEvents){
				events.add(dEvent.getId());
			}
		}		
		List<java.net.URI>eventList = new ArrayList<java.net.URI>();
		eventList.addAll(events);
		return eventList;
	}	
	
	/**
	 * 
	 * Get id for any RMapStatement in the repository whose subject or object matches the provided URI
	 * @param uri
	 * @param ts 
	 * @param statusCode
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getRelatedStmts(URI uri, SesameTriplestore ts) 
			throws RMapException {
		if (uri==null){
			throw new RMapException("Null URI for statement id");
		}
		List<Statement> statements = null;
		try {
			statements = ts.getStatements(null, RDF.SUBJECT, uri,true,null);
			statements.addAll(ts.getStatements(null, RDF.OBJECT, uri,true,null));		
		} catch (Exception e) {
			throw new RMapException (e);
		}
		Set<URI> idSet = new HashSet<URI>();
		for (Statement stmt:statements)	{
			Resource subject = stmt.getSubject();
			URI sUri = null;
			if (! (subject instanceof URI)){
				throw new RMapException ("RMapStatement with non-URI identifier: " + subject.stringValue());
			}
			else {
				sUri = (URI)subject;
			}
			idSet.add(sUri);
		}
		List<URI> idList = new ArrayList<URI>();
		idList.addAll(idSet);
		return idList;
	}	
	
	
	// THIS needs to return create ORMapStatements from the reified statements it gets from
	// the repository, and then use the getRelatedEvents to add the events and getStatus to getStatus
	// ALSO since we are NOT returning events or status in the media type, we might want to consider a
	// data structure with location, event links, and status link along with RDF of statement
	// THIS belongs in the RDF handler, not here
//	public String getRMapStatementAsRDF(String stmtId, String rdfType) throws Exception	{
//
//		String stmtRdf = null;
//		
//		if (stmtId!=null && stmtId.length()>0)	{
//
//			SesameTriplestore ts =  SesameTriplestoreFactory.getTriplestore();
//			URI stmtURI = null;
//			stmtURI = GeneralUtils.toURI(RMAP.STATEMENT_DATA_NS + stmtId);
//			
//			Statement statement = ts.getStatement(null, null, null, stmtURI);
//			List <Statement> eventStmts = ts.getStatements(stmtURI, PROV.WASGENERATEDBY, null);
//			List <Statement> stmtStmts = new ArrayList <Statement>();
//			
//			if (statement!=null)	{
//
//				Map<String, String> namespaces = new HashMap<String, String>();
//				namespaces.put("prov", "http://www.w3.org/ns/prov#");
//				namespaces.put("rmap", RMAP.NAMESPACE);
//				namespaces.put("foaf", "http://xmlns.com/foaf/0.1/");
//				
//				stmtStmts.add(new StatementImpl(stmtURI, RDF.TYPE, RMAP.STATEMENT));
//				stmtStmts.add(new StatementImpl(stmtURI, RDF.SUBJECT, statement.getSubject()));
//				stmtStmts.add(new StatementImpl(stmtURI, RDF.PREDICATE, statement.getPredicate()));
//				stmtStmts.add(new StatementImpl(stmtURI, RDF.OBJECT, statement.getObject()));
//				stmtStmts.addAll(eventStmts);
//
//				RDFHandler rdfHandler = RDFHandlerUtil.getFactory().getRDFHandler();
//				stmtRdf = rdfHandler.convertStmtListToRDF(stmtStmts, namespaces, rdfType);			
//				
//				}
//			else {
//				log.info("Statement could not be found.");
//			}
//		}
//		else {
//			log.info("Invalid parameters for getMatchingRMapStmts, cannot pass null or empty values.");
//		}
//		
//	return stmtRdf;
//	}	


}


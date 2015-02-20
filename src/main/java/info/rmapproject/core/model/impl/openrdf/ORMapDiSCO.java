/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.model.RMapDiSCO;
import info.rmapproject.core.model.RMapEvent;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapStatement;
import info.rmapproject.core.model.RMapStatementBag;
import info.rmapproject.core.model.RMapStatus;

import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

/**
 * Each DiSCO is a named graph.  Constituent statements will share same context, which is same
 * as DiSCO ID.
 * Status, as always, computed from events; related events also computed 
 * 
 * @author khansen, smorrissey
 *
 */
public class ORMapDiSCO extends ORMapObject implements RMapDiSCO {
	/**
	 * 1 or more Statements of the form discoID RMAP.AGGREGATES  Resource
	 * Context will be discoID
	 */
	protected List<ORMapStatement>aggregatedResources;
	/**
	 * 1 or more Statements related to the aggregated resources 
	 * All statements will have same context as DiscoID
	 */	
	protected List<ORMapStatement>relatedStatements;
	/**
	 * This is the "author" of the DiSCO (distinct from system Agent that creates Disco)
	 */
	protected ORMapStatement creator;
	/**
	 * Optional description of DiSCO
	 */
	protected ORMapStatement description;
	/**
	 * Statement declaring type of DiSCO
	 */
	protected ORMapStatement typeStatement;
	
	protected ORMapStatement providerIdStmt;
	/** 
	 * ID of DiSCO is also used as context	
	 */
	protected URI discoContext = null;	

	/**
	 * @throws Exception
	 */
	protected ORMapDiSCO() throws RMapException {
		super();
		this.discoContext = ORAdapter.uri2OpenRdfUri(this.getId());
		this.typeStatement = 
				new ORMapStatement(this.discoContext,RDF.TYPE,RMAP.DISCO,this.discoContext);
	}	
	/**
	 * 
	 * @param creator
	 * @param aggregatedResources
	 * @throws RMapException
	 */
	public ORMapDiSCO(RMapResource creator, List<java.net.URI> aggregatedResources) 
			throws RMapException {
		this();
		this.setCreator(creator);
		this.setAggregratedResources(aggregatedResources);
	}
	/**
	 * 
	 * @param creator
	 * @param aggregatedResources
	 * @param description
	 * @param relatedStatements
	 * @throws RMapException
	 */
	public ORMapDiSCO(RMapResource creator, List<java.net.URI> aggregatedResources, RMapResource description,
			RMapStatementBag relatedStatements) throws RMapException {		
		this(creator, aggregatedResources);
		this.setDescription(description);
		this.setRelatedStatements(relatedStatements);
	}
	/**
	 * 
	 * @param stmts
	 * @throws RMapException
	 */
	public ORMapDiSCO(List<Statement> stmts) throws RMapException{
		this();
		// Assuming RDF comes in, OpenRDF parser will create a bNode for the DiSCO
		// itself, and use that BNode identifier as resource - or
		// possibly also submitter used a local (non-RMap) identifier in RDF
		boolean discoFound = false;
		boolean isRmapId = false;
		Value incomingIdValue = null;
		String discoIncomingId = null;
		for (Statement stmt:stmts){
			Resource subject = stmt.getSubject();
			URI predicate = stmt.getPredicate();
			Value object = stmt.getObject();
			if (predicate.equals(RDF.TYPE)){
				if (object.equals(RMAP.DISCO)){
					discoFound = true;
					incomingIdValue = subject;
					discoIncomingId = ((Resource)subject).stringValue();
					break;
				}
			}
			continue;
		} 
		if (!discoFound){
			throw new RMapException ("No type statement found indicating DISCO");
		}
		if (discoIncomingId==null || discoIncomingId.length()==0){
			throw new RMapException ("null or empty disco identifier");
		}
		// check to make sure DiSCO has an RMAP id not a local one
		try {
			isRmapId  = 
					IdServiceFactoryIOC.getFactory().createService().isValidId(
							new java.net.URI(discoIncomingId));
		} catch (Exception e) {
			throw new RMapException ("Unable to validate DiSCO id " + 
					discoIncomingId, e);
		}			
		List<ORMapStatement> aggResources = new ArrayList<ORMapStatement>();
		List<ORMapStatement> relStatements = new ArrayList<ORMapStatement>();		
		if (isRmapId){
			// use incoming id
			try {
				this.id = new java.net.URI(discoIncomingId);
				this.discoContext = ORAdapter.uri2OpenRdfUri(this.getId()); 
				this.typeStatement =
						new ORMapStatement(this.discoContext,RDF.TYPE,RMAP.DISCO,this.discoContext);
			} catch (URISyntaxException e) {
				throw new RMapException ("Cannot convert incoming ID to URI: " + discoIncomingId,e);
			}			
		}
		else {
			// create a statement saying what original id was
			ORMapStatement idStmt = new ORMapStatement (this.discoContext, RMAP.PROVIDERID,
					incomingIdValue, this.discoContext);
			this.providerIdStmt = idStmt;
		}
		// sort out statements into type statement, aggregate resource statement,
		// creator statement, related statements, desc statement
		// replacing DiSCO id with new one if necessary

		for (Statement stmt:stmts){
			Resource subject = stmt.getSubject();
			URI predicate = stmt.getPredicate();
			Value object = stmt.getObject();
			Resource context = stmt.getContext();
			if (!isRmapId){
				if (subject.stringValue().equals(discoIncomingId)){
					subject = this.discoContext;
				}
				if (object.stringValue().equals(discoIncomingId)){
					object = this.discoContext;
				}
			}
			context = this.discoContext;
			boolean subjectIsDisco = subject.equals(context);
			if (predicate.equals(RDF.TYPE)){
				if (!subjectIsDisco){
					// we automatically created a type statement for disco					
					relStatements.add(new ORMapStatement(stmt));
				}
			}
			else if (predicate.equals(DCTERMS.CREATOR) || (predicate.equals(DC.CREATOR))){
				if (subjectIsDisco){
					this.creator = new ORMapStatement(stmt);
				}
				else {
					relStatements.add(new ORMapStatement(stmt));
				}
			}
			else if (predicate.equals(RMAP.AGGREGATES)){
				aggResources.add(new ORMapStatement(stmt));
			}
			else if (predicate.equals(DC.DESCRIPTION)){
				if (subjectIsDisco){
					this.description= new ORMapStatement(stmt);
				}
				else {
					relStatements.add(new ORMapStatement(stmt));
				}
			}
			else {
				relStatements.add(new ORMapStatement(stmt));
			}
		}
		this.aggregatedResources = aggResources;
		if (!this.referencesAggregate(relStatements)){
			throw new RMapException("related statements do no reference aggregated resources");
		}
		if (!this.isConnectedGraph(relStatements)){
			throw new RMapException ("related statements do not form a connected graph");
		}
		this.relatedStatements = relStatements;
	}
	/**
	 * Checks to see that at least once statement in DiSCO's RelatedStatement has
	 * one of the aggregated resources as its subject
	 * @param relatedResources
	 * @return
	 */
	protected boolean referencesAggregate(List<ORMapStatement> relatedStatements) throws RMapException{
		boolean refsAggs = false;
		if (this.aggregatedResources == null ||  this.aggregatedResources.size()==0){
			throw new RMapException ("Null or empty aggregated resources");
		}		
		List<Resource> resources = new ArrayList<Resource>();
		for (ORMapStatement stmt:aggregatedResources){
			resources.add((URI)stmt.getRmapStmtObject());
		}
		// find at least one statement that references at least one aggregated object
		for (ORMapStatement stmt:relatedStatements){
			Resource subject = stmt.getRmapStmtSubject();			
			if (resources.contains(subject)){
				refsAggs = true;
				break;
			}		
		}
		return refsAggs;
	}
	/**
	 * Convenience class for making + checking graph of statements
	 * @author smorrissey
	 *
	 */
	class Node {
		List<Node> neighbors;
		boolean wasVisited;		
		Node(){
			this.neighbors = new ArrayList<Node>();
			wasVisited = false;
		}		
		
		List<Node> getNeighbors(){
			return neighbors;
		}		
		boolean wasVisited(){
			return wasVisited;
		}		
		void setWasVisited(boolean isVisited){
			wasVisited = isVisited;
		}		
	}
	/**
	 * Check that related statements (along with aggregated resources) are non-disjoint
	 * @param relatedStatements ORMapStatements describing aggregrated resources
	 * @return true if related statements are non-disjoint; else false
	 * @throws RMapException
	 */
	protected boolean isConnectedGraph(List<ORMapStatement> relatedStatements) throws RMapException{
		boolean isConnected = false;
		HashMap<Value, Node> nodeMap = new HashMap<Value, Node>();
		List<Node> visitedNodes = new ArrayList<Node>();
		// get all the nodes in relatedStatements
		for (ORMapStatement stmt:relatedStatements){
			Value subj = stmt.getRmapStmtSubject();
			Value obj  = stmt.getRmapStmtObject();
			Node subjN = null;
			Node objN = null;
			subjN = nodeMap.get(subj);
			if (subjN==null){
				subjN = new Node();
				nodeMap.put(subj, subjN);
			}
			objN = nodeMap.get(obj);
			if (objN==null){
				objN = new Node();
				nodeMap.put(obj,objN);
			}				
			if (! subjN.getNeighbors().contains(objN)){
				subjN.getNeighbors().add(objN);
			}
		}
		int nodeCount = nodeMap.entrySet().size();
		// jump-start from first aggregate resource
		for (ORMapStatement stmt:this.aggregatedResources){
			Value aggResource = stmt.getRmapStmtObject();
			Node startNode = nodeMap.get(aggResource);
			if (startNode==null){
				continue;
			}
			this.markConnected(visitedNodes, startNode);
			if (visitedNodes.size()==nodeCount){
				break;
			}
		}
		if (visitedNodes.size()==nodeCount){
			isConnected = true;
		}
		return isConnected;				
	}
	/**
	 * Recursive vist to nodes to mark as visited
	 * @param visitedNodes
	 * @param startNode
	 */
	protected void markConnected (List<Node> visitedNodes,
		Node startNode){
		startNode.setWasVisited(true);
		visitedNodes.add(startNode);
		for (Node neighbor:startNode.getNeighbors()){
			if (! neighbor.wasVisited()){
				this.markConnected(visitedNodes, neighbor);
			}
		}
		return;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapDiSCO#getAggregratedResources()
	 */
	public List<java.net.URI> getAggregratedResources() throws RMapException {
		List <java.net.URI> resources = null;
		if (this.aggregatedResources != null){
			resources = new ArrayList<java.net.URI>();
			for (ORMapStatement statement:this.aggregatedResources){
				Value value = statement.getRmapStmtStatement().getObject();
				if (value instanceof URI){
					// guaranteed by constructor and setter methods so should always happen)
					URI resource = (URI)value;
					java.net.URI rmapResource = null;
					rmapResource = ORAdapter.openRdfUri2URI(resource);
					resources.add(rmapResource);
				}
				else {
					throw new RMapException ("Value of aggregrated resource triple is not a URI object");
				}
			}
		}
		return resources;
	}
	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public List<ORMapStatement> getAggregatedResourceStatements() throws RMapException{
		return this.aggregatedResources;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapDiSCO#setAggregratedResources(java.util.List)
	 */
	public void setAggregratedResources(List<java.net.URI> aggregratedResources) throws RMapException {
		List<ORMapStatement>aggResources = null;
		if (aggregratedResources != null){
			URI predicate = RMAP.AGGREGATES;
			aggResources = new ArrayList<ORMapStatement>();
				for (java.net.URI rmapResource:aggregratedResources){
					Resource resource = ORAdapter.uri2OpenRdfUri(rmapResource);
					try {
						ORMapStatement newStmt = new ORMapStatement(this.discoContext, predicate,resource,this.discoContext);
						aggResources.add(newStmt);
					} catch (Exception e) {
						throw new RMapException (e);
					}
				}// end for		
		}
		this.aggregatedResources = aggResources;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapDiSCO#getRelatedStatements()
	 */
	public RMapStatementBag getRelatedStatements() throws RMapException {
		RMapStatementBag bag = null;
		do {
			if (this.relatedStatements==null || this.relatedStatements.isEmpty()){
				break;
			}
			for (ORMapStatement stmt:this.relatedStatements){
				bag.add(stmt);
			}
		}while (false);		
		return bag;
	}
	/**
	 * 
	 * @return
	 */
	public List<ORMapStatement> getRelatedStatementsAsStatements (){
		return this.relatedStatements;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapDiSCO#setRelatedStatements(info.rmapproject.core.model.RMapStatementBag)
	 */
	public void setRelatedStatements(RMapStatementBag relatedStatements) 
			throws RMapObjectNotFoundException, RMapException {
		// make sure that at least one statement relatedResources references at least one aggregated resource,
		// and that aggregated resources and related resources comprise a connected graph
		if (relatedStatements==null){
			this.relatedStatements = null;
			return;
		}
		if (this.aggregatedResources==null || this.aggregatedResources.isEmpty()){
			throw new RMapException("Null or empty aggregated resources in DiSCO");
		}
		List<ORMapStatement> orStmts = new ArrayList<ORMapStatement>();
		do {	
			Object[] objects = relatedStatements.getContents();
			for (Object object:objects){
				if (object instanceof RMapStatement){
					RMapStatement stmt = (RMapStatement)object;
					Resource subject = ORAdapter.rMapNonLiteral2OpenRdfResource(stmt.getSubject());
					URI predicate = ORAdapter.rMapUri2OpenRdfUri(stmt.getPredicate());
					Value vObject = ORAdapter.rMapResource2OpenRdfValue(stmt.getObject());
					ORMapStatement rStmt = new ORMapStatement(subject, predicate, vObject,this.discoContext);
					orStmts.add(rStmt);
				}
				else if (object instanceof java.net.URI){
					java.net.URI uri = (java.net.URI)object;
					RMapService service = RMapServiceFactoryIOC.getFactory().createService();
					RMapStatement stmt = service.readStatement(uri);
					if (stmt==null){
						throw new RMapObjectNotFoundException(
								"List of related statements includes id of non-existent RMapStatement: " 
						+ object.toString());
						
					}
					else {
						orStmts.add(new ORMapStatement(stmt));
					}					
				}
				else {
					// should not happen					
					throw new RMapException ("RMapStatementBag contains object of illegal class:  " + 
							object.getClass().toString());
				}
			}
			if (!this.referencesAggregate(orStmts)){
				throw new RMapException("related statements do no reference aggregated resources");
			}
			if (!this.isConnectedGraph(orStmts)){
				throw new RMapException ("related statements do not form a connected graph");
			}
		}while (false);
		this.relatedStatements = orStmts;
		return;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapDiSCO#getCreators()
	 */
	public RMapResource getCreator() throws RMapException {
		RMapResource creator = null;
		if (this.creator != null){
			creator = this.creator.getObject();
		}
		return creator;
	}
	/**
	 * 
	 * @return
	 */
	public ORMapStatement getCreatorStmt() {
		return this.creator;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapDiSCO#setCreators(List<info.rmapproject.core.model.RMapResource)>
	 */
	public void setCreator(RMapResource creator) throws RMapException {
		ORMapStatement stmt = null;
		if (creator != null){
			URI predicate = DCTERMS.CREATOR;
			try {
				Resource subject = this.discoContext;		
				Value vcreator = ORAdapter.rMapResource2OpenRdfValue(creator);
				stmt = new ORMapStatement(subject,predicate,vcreator,this.discoContext);			
			} catch (Exception e) {
				throw new RMapException(e);
			}			
		}
		this.creator = stmt;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapDiSCO#getDescription()
	 */
	public RMapResource getDescription() throws RMapException {
		RMapResource desc = null;
		do {
			if (this.description==null){
				break;
			}
			desc = this.description.getObject();
		} while (false);
		return desc;
	}
	/**
	 * 
	 * @return
	 */
	public ORMapStatement getDescriptonStatement () {
		return this.description;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapDiSCO#setDescription(info.rmapproject.core.model.RMapResource)
	 */
	public void setDescription(RMapResource description) throws RMapException {
		ORMapStatement stmt = null;
		if (description != null){
			URI predicate = DC.DESCRIPTION;
			try {
				Resource subject = this.discoContext;		
				Value vdesc = ORAdapter.rMapResource2OpenRdfValue(description);
				stmt = new ORMapStatement(subject,predicate,vdesc,this.discoContext);			
			} catch (Exception e) {
				throw new RMapException(e);
			}			
		}
		this.description = stmt;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapDiSCO#getStatus()
	 */
	public RMapStatus getStatus() throws RMapException {
		RMapService service = RMapServiceFactoryIOC.getFactory().createService();
		return service.getDiSCOStatus(getId());
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapDiSCO#getRelatedEvents()
	 */
	public List<RMapEvent> getRelatedEvents() throws RMapException {
		RMapService service = RMapServiceFactoryIOC.getFactory().createService();
		List<java.net.URI> uris = service.getDiSCOEvents(getId());
		List<RMapEvent> events = new ArrayList<RMapEvent>();
		for (java.net.URI uri:uris){
			RMapEvent event = service.readEvent(uri);
			events.add(event);
		}
		return events;
	}
	/**
	 * @return the typeStatement
	 */
	public ORMapStatement getTypeStatement() {
		return typeStatement;
	}

	/**
	 * @return the discoContext
	 */
	public URI getDiscoContext() {
		return discoContext;
	}
	/**
	 * 
	 */
	public String getProviderId() throws RMapException {
		String id = null;
		if (this.providerIdStmt != null){
			Value vId = providerIdStmt.getRmapStmtObject();
			id = vId.stringValue();
		}
		return id;
	}
	
	public ORMapStatement getProviderIdStmt(){
		return this.providerIdStmt;
	}
}

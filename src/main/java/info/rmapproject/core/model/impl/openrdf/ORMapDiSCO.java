/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.Predicate;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.idvalidator.RMapIdPredicate;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapTriple;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.model.event.RMapEvent;

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
	protected List<Statement>aggregatedResources;
	/**
	 * 1 or more Statements related to the aggregated resources 
	 * All statements will have same context as DiscoID
	 */	
	protected List<Statement>relatedStatements;
	/**
	 * This is the "author" of the DiSCO (distinct from system Agent that creates Disco)
	 */
	protected Statement creator;
	/**
	 * Optional description of DiSCO
	 */
	protected Statement description;
	/**
	 * ID used by provider of DiSCO on their own system
	 */
	protected Statement providerIdStmt;
	/** 
	 * ID of DiSCO is also used as context	
	 */
	protected URI discoContext = null;	

	/**
	 * Base constructor
	 * Sets DiSCO context equal to DiSCO ID, so DiSCO is named graph
	 * @throws Exception
	 */
	protected ORMapDiSCO() throws RMapException {
		super();
		this.discoContext = ORAdapter.uri2OpenRdfUri(this.getId());
		this.typeStatement = 
				this.getValueFactory().createStatement(this.discoContext, RDF.TYPE,
						RMAP.DISCO,this.discoContext);
	}	
	/**
	 * Constructor
	 * Constructs statement triples aggregating resources in DiSCO
	 * @param creator Author of DiSCO
	 * @param aggregatedResources Resources comprising compound object
	 * @throws RMapException if unable to create Creator or aggregated resources Statements
	 */
	public ORMapDiSCO(RMapResource creator, List<java.net.URI> aggregatedResources) 
			throws RMapException {
		this();
		this.setCreator(creator);
		this.setAggregratedResources(aggregatedResources);
	}
	/**
	 * Constructor
	 * Constructs statement triples aggregating resources in DiSCO
	 * @param creator Author of DiSCO
	 * @param aggregatedResources Resources comprising compound object
	 * @param description Optional description of DiSCO
	 * @param relatedStatements Optional statements related to aggregated resources.
	 *           Related Statements must reference at least one aggregated resource, 
	 *           and must not comprise a disjoint graph
	 * @throws RMapException if related statements do not reference at least one resource, or
	 *            comprise a disjoint graph, or if cannot create Statements from parameters
	 */
	public ORMapDiSCO(RMapResource creator, List<java.net.URI> aggregatedResources, RMapValue description,
			List<RMapTriple> relatedStatements) throws RMapException {		
		this(creator, aggregatedResources);
		this.setDescription(description);
		this.setRelatedStatements(relatedStatements);
	}
	/**
	 * Constructs DiSCO from List of triples
	 * @param stmts Statements to be structured into DiSCO
	 * @throws RMapException if resources not present, or related statements do not reference at least one resource, or
	 *            comprise a disjoint graph, or if cannot create Statements from parameters
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
			Predicate<Object> predicate = RMapIdPredicate.rmapIdPredicate();
			isRmapId  = predicate.evaluate(new java.net.URI(discoIncomingId));
		} catch (Exception e) {
			throw new RMapException ("Unable to validate DiSCO id " + 
					discoIncomingId, e);
		}				
		if (isRmapId){
			// use incoming id
			try {
				this.id = new java.net.URI(discoIncomingId);
				this.discoContext = ORAdapter.uri2OpenRdfUri(this.getId()); 
				this.typeStatement =
						this.getValueFactory().createStatement(this.discoContext,RDF.TYPE,RMAP.DISCO,this.discoContext);
			} catch (URISyntaxException e) {
				throw new RMapException ("Cannot convert incoming ID to URI: " + discoIncomingId,e);
			}			
		}
		else {
			// create a statement saying what original id was, and use existing type statement
			Statement idStmt = this.getValueFactory().createStatement(this.discoContext, RMAP.PROVIDERID,
					incomingIdValue, this.discoContext);
			this.providerIdStmt = idStmt;
		}
		// sort out statements into type statement, aggregate resource statement,
		// creator statement, related statements, desc statement
		// replacing DiSCO id with new one if necessary
		
		List<Statement> aggResources = new ArrayList<Statement>();
		List<Statement> relStatements = new ArrayList<Statement>();
		
		for (Statement stmt:stmts){
			Resource subject = stmt.getSubject();
			URI predicate = stmt.getPredicate();
			Value object = stmt.getObject();
			// see if disco is subject of statement
			boolean subjectIsDisco = subject.stringValue().equals(discoIncomingId);
			if (!isRmapId){
				// convert incoming id to RMap id in subject and boject
				if (subjectIsDisco){
					subject = this.discoContext;
				}
				if (object.stringValue().equals(discoIncomingId)){
					object = this.discoContext;
				}
			}			
			if (predicate.equals(RDF.TYPE)){
				if (!subjectIsDisco){
					// we automatically created a type statement for disco					
					relStatements.add(this.getValueFactory().createStatement
							(subject, predicate, object, this.discoContext));
				}
			}
			else if (predicate.equals(DCTERMS.CREATOR)){
				if (subjectIsDisco){
					this.creator = this.getValueFactory().createStatement
							(subject, predicate, object, this.discoContext);
				}
				else {
					relStatements.add(this.getValueFactory().createStatement
							(subject, predicate, object, this.discoContext));
				}
			}
			else if (predicate.equals(RMAP.AGGREGATES)){
				aggResources.add(this.getValueFactory().createStatement
						(subject, predicate, object, this.discoContext));
			}
			else if (predicate.equals(DC.DESCRIPTION)){
				if (subjectIsDisco){
					this.description= this.getValueFactory().createStatement
							(subject, predicate, object, this.discoContext);
				}
				else {
					relStatements.add(this.getValueFactory().createStatement
							(subject, predicate, object, this.discoContext));
				}
			}
			else {
				relStatements.add(this.getValueFactory().createStatement
						(subject, predicate, object, this.discoContext));
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
	 * Checks to see that at least once statement in DiSCO's RelatedStatements has
	 * one of the aggregated resources as its subject
	 * @param relatedResources
	 * @return
	 */
	protected boolean referencesAggregate(List<Statement> relatedStatements) throws RMapException{
		boolean refsAggs = false;
		if (this.aggregatedResources == null ||  this.aggregatedResources.size()==0){
			throw new RMapException ("Null or empty aggregated resources");
		}		
		List<Resource> resources = new ArrayList<Resource>();
		for (Statement stmt:aggregatedResources){
			resources.add((URI)stmt.getObject());
		}
		// find at least one statement that references at least one aggregated object
		for (Statement stmt:relatedStatements){
			Resource subject = stmt.getSubject();			
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
	protected boolean isConnectedGraph(List<Statement> relatedStatements) throws RMapException{
		boolean isConnected = false;
		HashMap<Value, Node> nodeMap = new HashMap<Value, Node>();
		List<Node> visitedNodes = new ArrayList<Node>();
		// get all the nodes in relatedStatements
		for (Statement stmt:relatedStatements){
			Value subj = stmt.getSubject();
			Value obj  = stmt.getObject();
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
		for (Statement stmt:this.aggregatedResources){
			Value aggResource = stmt.getObject();
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
			for (Statement statement:this.aggregatedResources){
				Value value = statement.getObject();
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
	 * Get list of aggregated resources as list of OpenRDF Statements
	 * @return list of aggregated resources as list of OpenRDF Statements
	 * @throws RMapException
	 */
	public List<Statement> getAggregatedResourceStatements() throws RMapException{
		return this.aggregatedResources;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapDiSCO#setAggregratedResources(java.util.List)
	 */
	public void setAggregratedResources(List<java.net.URI> aggregratedResources) throws RMapException {
		List<Statement>aggResources = null;
		if (aggregratedResources != null){
			URI predicate = RMAP.AGGREGATES;
			aggResources = new ArrayList<Statement>();
				for (java.net.URI rmapResource:aggregratedResources){
					Resource resource = ORAdapter.uri2OpenRdfUri(rmapResource);
					try {
						Statement newStmt = this.getValueFactory().createStatement
								(this.discoContext, predicate,resource,this.discoContext);
						aggResources.add(newStmt);
					} catch (Exception e) {
						throw new RMapException (e);
					}
				}// end for		
		}
		this.aggregatedResources = aggResources;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapDiSCO#getCreators()
	 */
	public RMapResource getCreator() throws RMapException {
		RMapValue vCreator = null;
		RMapResource creator = null;
		if (this.creator != null){
			try {
				vCreator = ORAdapter.openRdfValue2RMapValue(this.creator.getObject());
				if (vCreator instanceof RMapResource){
					creator = (RMapResource)vCreator;
				}
				else {
					throw new RMapException ("DiSCO Creator not an RMapResource");
				}
			} catch (IllegalArgumentException | URISyntaxException e) {
				throw new RMapException(e);
			}
		}
		return creator;
	}
	/**
	 * Returns creator as ORMapStatement object
	 * @return creator as ORMapStatement object
	 */
	public Statement getCreatorStmt() {
		return this.creator;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapDiSCO#setCreators(List<info.rmapproject.core.model.RMapResource)>
	 */
	public void setCreator(RMapResource creator) throws RMapException {
		Statement stmt = null;
		if (creator != null){
			URI predicate = DCTERMS.CREATOR;
			try {
				Resource subject = this.discoContext;		
				Value vcreator = ORAdapter.rMapValue2OpenRdfValue(creator);
				stmt = this.getValueFactory().createStatement(subject,predicate,vcreator,this.discoContext);			
			} catch (Exception e) {
				throw new RMapException(e);
			}			
		}
		this.creator = stmt;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapDiSCO#getDescription()
	 */
	public RMapValue getDescription() throws RMapException {
		RMapValue desc = null;
		do {
			if (this.description==null){
				break;
			}
			try {
				desc = ORAdapter.openRdfValue2RMapValue(this.description.getObject());
			} catch (IllegalArgumentException | URISyntaxException e) {
				throw new RMapException(e);
			}
		} while (false);
		return desc;
	}
	/**
	 * Returns DiSCO description as ORMapStatement object
	 * @return ORMapStatement
	 */
	public Statement getDescriptonStatement () {
		return this.description;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapDiSCO#setDescription(info.rmapproject.core.model.RMapResource)
	 */
	public void setDescription(RMapValue description) throws RMapException {
		Statement stmt = null;
		if (description != null){
			URI predicate = DC.DESCRIPTION;
			try {
				Resource subject = this.discoContext;		
				Value vdesc = ORAdapter.rMapValue2OpenRdfValue(description);
				stmt = this.getValueFactory().createStatement(subject,predicate,vdesc,this.discoContext);			
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
		List<java.net.URI> uris = this.getService().getDiSCOEvents(getId());
		List<RMapEvent> events = new ArrayList<RMapEvent>();
		for (java.net.URI uri:uris){
			RMapEvent event = this.getService().readEvent(uri);
			events.add(event);
		}
		return events;
	}

	/**
	 * Get disco context URI
	 * @return the discoContext
	 */
	public URI getDiscoContext() {
		return discoContext;
	}
	/**
	 * Return id used by provider of DiSCO in their own system as String
	 */
	public String getProviderId() throws RMapException {
		String id = null;
		if (this.providerIdStmt != null){
			Value vId = providerIdStmt.getObject();
			id = vId.stringValue();
		}
		return id;
	}
	/**
	 * Return id used by provider of DiSCO in their own system as ORMapStatement
	 * @return
	 */
	public Statement getProviderIdStmt(){
		return this.providerIdStmt;
	}
	@Override
	public Model getAsModel() throws RMapException {
		Model discoModel = new LinkedHashModel();
		discoModel.add(typeStatement);
		discoModel.add(creator);
		if (description != null){
			discoModel.add(description);
		}
		if (providerIdStmt != null){
			discoModel.add(providerIdStmt);
		}
		for (Statement aggRes: aggregatedResources){
			discoModel.add(aggRes);
		}
		if (relatedStatements != null){
			for (Statement stmt:relatedStatements){
				discoModel.add(stmt);
			}
		}
		return discoModel;
	}
	@Override
	public List<RMapTriple> getRelatedStatements() throws RMapException {
		List<RMapTriple> triples = new ArrayList<RMapTriple>();
		if (this.relatedStatements!= null){
			for (Statement stmt:relatedStatements){
				RMapResource subject = null;
				try {
					subject = ORAdapter.openRdfResource2NonLiteral(stmt.getSubject());
				} catch (IllegalArgumentException | URISyntaxException e) {
					throw new RMapException(e);
				}
				RMapUri predicate = ORAdapter.openRdfUri2RMapUri(stmt.getPredicate());
				RMapValue object = null;
				try {
					object = ORAdapter.openRdfValue2RMapValue(stmt.getObject());
				} catch (IllegalArgumentException | URISyntaxException e) {
					throw new RMapException(e);
				}
				RMapTriple triple = new RMapTriple(subject, predicate, object);
				triples.add(triple);
			}
		}
		return triples;
	}
	/**
	 * Return related statement triples as list of ORMapStatement objects
	 * @return ist of ORMapStatement objects
	 */
	public List<Statement> getRelatedStatementsAsList (){
		return this.relatedStatements;
	}
	@Override
	public void setRelatedStatements(List<RMapTriple> relatedStatements)
			throws RMapException {
		if (relatedStatements==null){
			throw new RMapException("null list of related statements");
		}
		List<Statement>stmts = new ArrayList<Statement>();
		for (RMapTriple triple:relatedStatements){
			Resource subject = ORAdapter.rMapNonLiteral2OpenRdfResource(triple.getSubject());
			URI predicate = ORAdapter.rMapUri2OpenRdfUri(triple.getPredicate());
			Value object = ORAdapter.rMapValue2OpenRdfValue(triple.getObject());
			Statement stmt = this.getValueFactory().createStatement(subject, predicate, object, this.discoContext);
			stmts.add(stmt);
		}
		this.relatedStatements = stmts;
	}

}

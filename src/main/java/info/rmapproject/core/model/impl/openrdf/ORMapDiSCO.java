/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapTriple;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.ORE;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;

/**
 * Each DiSCO is a named graph.  Constituent statements will share same context, which is same
 * as DiSCO ID.
 * Status, as always, computed from events; related events also computed 
 * 
 * @author khanson, smorrissey
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
	 * Base constructor
	 * Sets DiSCO context equal to DiSCO ID, so DiSCO is named graph
	 * @throws RMapDefectiveArgumentException 
	 * @throws Exception
	 */
	protected ORMapDiSCO() throws RMapException {
		super();	
		this.setTypeStatement(RMAP.DISCO);
	}	
	
	/**
	 * Constructor
	 * Constructs statement triples aggregating resources in DiSCO
	 * @param creator Author of DiSCO
	 * @param aggregatedResources Resources comprising compound object
	 * @throws RMapException if unable to create Creator or aggregated resources Statements
	 * @throws RMapDefectiveArgumentException 
	 */
	public ORMapDiSCO(RMapUri creator, List<java.net.URI> aggregatedResources) 
			throws RMapException, RMapDefectiveArgumentException {
		this();
		this.setCreator(creator);
		this.setAggregratedResources(aggregatedResources);
	}
	
	/**
	 * Constructs DiSCO from List of triples
	 * @param stmts Statements to be structured into DiSCO
	 * @throws RMapException if resources not present, or related statements do not reference at least one resource, or
	 *            comprise a disjoint graph, or if cannot create Statements from parameters
	 * @throws RMapDefectiveArgumentException 
	 */
	public ORMapDiSCO(List<Statement> stmts) throws RMapException, RMapDefectiveArgumentException{
		this();	
		// Assuming RDF comes in, OpenRDF parser will create a bNode for the DiSCO
		// itself, and use that BNode identifier as resource - or
		// possibly also submitter used a local (non-RMap) identifier in RDF
		boolean discoFound = false;
		
		//openrdf is too forgiving wrt URIs - it allows new line characters, for example. 
		//This code checks the URIs can be converted to java.net.URI
		ORAdapter.checkOpenRdfUri2UriCompatibility(stmts);
		
		Resource incomingIdValue = null;
		String incomingIdStr = null;
		for (Statement stmt:stmts){
			Resource subject = stmt.getSubject();
			URI predicate = stmt.getPredicate();
			Value object = stmt.getObject();
			if (object.equals(RMAP.DISCO)&&predicate.equals(RDF.TYPE)){
				discoFound = true;
				incomingIdValue = subject;
				incomingIdStr = subject.stringValue();
				break;
			}
			continue;
		} 
		if (!discoFound){
			throw new RMapException ("No type statement found indicating DISCO");
		}
		if (incomingIdStr==null || incomingIdStr.length()==0){
			throw new RMapException ("null or empty disco identifier");
		}
		// check to make sure DiSCO has an RMAP id not a local one
		boolean isRmapUri = false;
		if (incomingIdValue instanceof URI){
			isRmapUri = isRMapUri((URI)incomingIdValue);
			if (isRmapUri){	// then use incoming id
				this.setId((URI)incomingIdValue);
			}
			else { //capture it if it's not a blank node - only URIs acceptable
				// create a statement saying what original id was, and use existing type statement
				Statement idStmt = this.getValueFactory().createStatement(this.id, RMAP.PROVIDER_ID,
																		incomingIdValue, this.context);
				this.providerIdStmt = idStmt;
			}
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
			boolean subjectIsDisco = subject.stringValue().equals(incomingIdStr);
			if (!isRmapUri){
				// convert incoming id to RMap id in subject and object
				if (subjectIsDisco){
					subject = this.id;
				}
				if (object.stringValue().equals(incomingIdStr)){
					object = this.id;
				}
			}			
			if (predicate.equals(RDF.TYPE)){
				if (!subjectIsDisco){
					// we automatically created a type statement for disco					
					relStatements.add(this.getValueFactory().createStatement
							(subject, predicate, object, this.context));
				}
			}
			else if (predicate.equals(DCTERMS.CREATOR)){
				if (subjectIsDisco){
					// make sure creator value is a URI
					if (!(object instanceof URI)){
						throw new RMapException("Object of DiSCO creator statement should be a URI and is not: "
								+ object.stringValue());
					}
					this.creator = this.getValueFactory().createStatement
							(subject, predicate, object, this.context);
				}
				else {
					relStatements.add(this.getValueFactory().createStatement
							(subject, predicate, object, this.context));
				}
			}
			else if (predicate.equals(RMAP.PROVIDER_ID)){
				if (subjectIsDisco){
					this.providerIdStmt = this.getValueFactory().createStatement
							(subject, predicate, object,this.context);
				}
				else {
					relStatements.add(this.getValueFactory().createStatement
							(subject, predicate, object, this.context));
				}
			}
			else if (predicate.equals(ORE.AGGREGATES)){
				aggResources.add(this.getValueFactory().createStatement
						(subject, predicate, object, this.context));
			}
			else if ((predicate.equals(DC.DESCRIPTION)) || (predicate.equals(DCTERMS.DESCRIPTION))){
				if (subjectIsDisco){
					this.description= this.getValueFactory().createStatement
							(subject, predicate, object, this.context);
				}
				else {
					relStatements.add(this.getValueFactory().createStatement
							(subject, predicate, object, this.context));
				}
			}
			else {
				relStatements.add(this.getValueFactory().createStatement
						(subject, predicate, object, this.context));
			}
		}
		if (this.creator==null){
			throw new RMapException("No disco creator statement found");
		}
		if (aggResources.isEmpty()){
			throw new RMapException("No aggregated resource statements found");
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
		Set<Node> visitedNodes = new HashSet<Node>();
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
	protected void markConnected (Set<Node> visitedNodes,
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
			URI predicate = ORE.AGGREGATES;
			aggResources = new ArrayList<Statement>();
				for (java.net.URI rmapResource:aggregratedResources){
					Resource resource = ORAdapter.uri2OpenRdfUri(rmapResource);
					try {
						Statement newStmt = this.getValueFactory().createStatement
								(this.context, predicate,resource,this.context);
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
	public RMapUri getCreator() throws RMapException {
		RMapValue vCreator = null;
		RMapUri creator = null;
		if (this.creator != null){
			try {
				vCreator = ORAdapter.openRdfValue2RMapValue(this.creator.getObject());
				if (vCreator instanceof RMapUri){
					creator = (RMapUri)vCreator;
				}
				else {
					throw new RMapException ("DiSCO Creator not an RMapUri");
				}
			} catch (RMapDefectiveArgumentException e) {
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
	public void setCreator(RMapUri creator) throws RMapException {
		Statement stmt = null;
		if (creator != null){
			URI predicate = DCTERMS.CREATOR;
			try {
				Resource subject = this.context;		
				URI vcreator = ORAdapter.rMapUri2OpenRdfUri(creator);
				stmt = this.getValueFactory().createStatement(subject,predicate,vcreator,this.context);			
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
			} catch (RMapDefectiveArgumentException e) {
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
				Resource subject = this.context;		
				Value vdesc = ORAdapter.rMapValue2OpenRdfValue(description);
				stmt = this.getValueFactory().createStatement(subject,predicate,vdesc,this.context);			
			} catch (Exception e) {
				throw new RMapException(e);
			}			
		}
		this.description = stmt;
	}

	/**
	 * Get disco context URI
	 * @return the discoContext
	 */
	public URI getDiscoContext() {
		return context;
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
				} catch (RMapDefectiveArgumentException e) {
					throw new RMapException(e);
				}
				RMapUri predicate = ORAdapter.openRdfUri2RMapUri(stmt.getPredicate());
				RMapValue object = null;
				try {
					object = ORAdapter.openRdfValue2RMapValue(stmt.getObject());
				} catch (RMapDefectiveArgumentException e) {
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
			Resource subject = null;
			URI predicate = null;
			Value object = null;
			try {
				subject = ORAdapter.rMapNonLiteral2OpenRdfResource(triple.getSubject());
				predicate=ORAdapter.rMapUri2OpenRdfUri(triple.getPredicate());
				object = ORAdapter.rMapValue2OpenRdfValue(triple.getObject());
			}
			catch(RMapDefectiveArgumentException e) {
				throw new RMapException(e);
			}
			Statement stmt = this.getValueFactory().createStatement(subject, predicate, object, this.context);
			stmts.add(stmt);
		}
		this.relatedStatements = stmts;
	}

}

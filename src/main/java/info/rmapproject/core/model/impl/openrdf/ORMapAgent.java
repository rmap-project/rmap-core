package info.rmapproject.core.model.impl.openrdf;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.Predicate;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.idvalidator.RMapIdPredicate;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapTriple;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;
/**
 * 
 *  @author khansen, smorrissey
 *
 */
public class ORMapAgent extends ORMapObject implements RMapAgent {
	protected URI context;
	protected Statement creatorStmt;
	protected Statement representationStmt;
	protected List<Statement> properties = new ArrayList<Statement>();

	/**
	 * 
	 * @throws RMapException
	 */
	protected ORMapAgent() throws RMapException {
		super();
		this.context = ORAdapter.uri2OpenRdfUri(getId());	
		this.typeStatement = 
				this.getValueFactory().createStatement(this.context,RDF.TYPE,RMAP.AGENT,this.context);
	}
	/**
	 * 
	 * @param targetRep
	 * @param creator
	 * @throws RMapException
	 */
	public ORMapAgent(URI targetRep, URI creator) throws RMapException {
		this();
		this.setRepresentationStmt(targetRep);
		this.setCreatorStmt(creator);
	}
	/**
	 * 
	 * @param targetRep
	 * @param creator
	 * @param properties
	 * @throws RMapException
	 */
	public ORMapAgent (URI targetRep, URI creator, List<Statement> properties) 
	throws RMapException {
		this(targetRep, creator);
		if (properties != null){
			for (Statement stmt:properties){
					this.properties.add(stmt);			
			}
		}
	}
	/**
	 * 
	 * @param property
	 * @return
	 */
	public boolean addProperty(Statement property){
		boolean changed = false;
		LinkedHashModel model = new LinkedHashModel();
		model.addAll(properties);
		Model filterModel = model.filter(property.getSubject(), property.getPredicate(), property.getObject());
		if (filterModel.size()==0){
			properties.add(property);
			changed = true;
		}
		return changed;
	}
		
	@Override
	public Model getAsModel() throws RMapException {
		Model model = new LinkedHashModel();
		model.add(typeStatement);
		model.add(creatorStmt);
		model.add(representationStmt);
		for (Statement stmt:properties){
			model.add(stmt);
		}	
		return model;
	}
	/**
	 * 
	 * @param stmts
	 * @param creator
	 * @throws RMapException
	 */
	public ORMapAgent(List<Statement> stmts, URI creator)throws RMapException {
		this();
		if (stmts==null){
			throw new RMapException("Null statement list");
		}	
		boolean typeFound = false;
		Value incomingIdValue = null;
		String incomingIdStr = null;
		boolean isRmapId = false;
		for (Statement stmt:stmts){
			Resource subject = stmt.getSubject();
			URI predicate = stmt.getPredicate();
			Value object = stmt.getObject();
			if (predicate.equals(RDF.TYPE)){
				if (object.equals(RMAP.AGENT)){
					typeFound = true;
					incomingIdValue = subject;
					incomingIdStr = subject.stringValue();
					// use incoming context if there is one
					Resource context = stmt.getContext();
					if (context != null && context instanceof URI){
						this.context = (URI)context;
					}
					break;
				}
			}
			continue;
		} 
		if (!typeFound || incomingIdValue==null){
			throw new RMapException ("No type statement found indicating AGENT");
		}
		if (incomingIdStr==null || incomingIdStr.length()==0){
			throw new RMapException ("null or empty agent identifier");
		}
		
		// check to make sure Agent has an RMAP id not a local one, or bnode id
		try {
			Predicate<Object> predicate = RMapIdPredicate.rmapIdPredicate();
			isRmapId  = predicate.evaluate(new java.net.URI(incomingIdStr));
		} catch (Exception e) {
			throw new RMapException ("Unable to validate DiSCO id " + 
					incomingIdStr, e);
		}				
		if (isRmapId){
			// use incoming id
			try {
				this.id = new java.net.URI(incomingIdStr);
				this.context = ORAdapter.uri2OpenRdfUri(this.getId()); 
				this.typeStatement =
						this.getValueFactory().createStatement(this.context,RDF.TYPE,RMAP.DISCO,this.context);
			} catch (URISyntaxException e) {
				throw new RMapException ("Cannot convert incoming ID to URI: " + incomingIdStr,e);
			}			
		}
		
		// creator should not be null if method invoked from service,
		// can be null when creating ORMapAgent from triplestore statements,
		// so we have to check at the end and make sure there is a non-null creator
		if (creator!=null){
			this.setCreatorStmt(creator);
		}		
				
		for (Statement stmt:stmts){
			Resource subject = stmt.getSubject();
			URI predicate = stmt.getPredicate();
			Value object = stmt.getObject();
			boolean agentIsSubject = false;
			if (subject.stringValue().equals(incomingIdValue.stringValue())){
				subject = this.context;
				agentIsSubject = true;
			}
			if (object.stringValue().equals(incomingIdValue.stringValue())){
				object = this.context;
			}
			if (predicate.equals(RDF.TYPE)){
				if (agentIsSubject && object.equals(RMAP.AGENT)){
					this.typeStatement= this.getValueFactory().createStatement(
							subject, predicate, object, this.context);
					continue;
				}
				else {
					// it's just another property
					Statement propStmt = this.getValueFactory().createStatement(
							subject, predicate, object, this.context);
					this.properties.add(propStmt);
					continue;
				}
			}
			if (predicate.equals(DCTERMS.CREATOR)){
				if (agentIsSubject){
					Statement creatorStmt = this.getValueFactory().createStatement(
							subject, predicate, object, this.context);
					this.creatorStmt = creatorStmt;
				}
				else {
					Statement propStmt = this.getValueFactory().createStatement(
							subject, predicate, object, this.context);
					this.properties.add(propStmt);
				}
				continue;
			}
			if (predicate.equals(RMAP.AGENTREP)){
				if (agentIsSubject){
					Statement repStmt = this.getValueFactory().createStatement(
							subject, predicate, object, this.context);
					this.representationStmt = repStmt;
				}
				else {
					Statement propStmt = this.getValueFactory().createStatement(
							subject, predicate, object, this.context);
					this.properties.add(propStmt);
				}
				continue;
			}
			Statement propStmt = this.getValueFactory().createStatement(
					subject, predicate, object, this.context);
			this.properties.add(propStmt);
			continue;
		}
		if (this.creatorStmt==null){
			throw new RMapException ("Null creator for Agent");
		}
		if (this.representationStmt==null){
			throw new RMapException ("Null representation target for agent");
		}

	}

	@Override
	public RMapUri getCreator() throws RMapException {
		RMapUri cUri = null;
		if (this.creatorStmt!= null){
			Value value = this.creatorStmt.getObject();
			if (value instanceof URI){
				cUri = ORAdapter.openRdfUri2RMapUri((URI)value);
			}
		}
		return cUri;
	}
	/**
	 * 
	 * @param creator
	 */
	protected void setCreatorStmt (URI creator){
		Statement stmt = this.getValueFactory().createStatement(this.context, 
				DCTERMS.CREATOR, creator, this.context);
		this.creatorStmt = stmt;
	}
	/**
	 * 
	 * @param target
	 */
	protected void setRepresentationStmt (URI target){
		Statement stmt = this.getValueFactory().createStatement(this.context, 
				RMAP.AGENTREP, target, this.context);
		this.representationStmt = stmt;
	}
	/**
	 * @return the context
	 */
	public URI getContext() {
		return context;
	}
	@Override
	public RMapUri getRepresentationId() throws RMapException {
		URI repValue = (URI)this.representationStmt.getObject();
		return ORAdapter.openRdfUri2RMapUri(repValue);
	}
	@Override
	public List<RMapTriple> getProperties() throws RMapException {
		List<RMapTriple> triples = new ArrayList<RMapTriple>();
		for (Statement stmt:this.properties){
			RMapResource subject= null;
			RMapValue object = null;
			try {
				subject = ORAdapter.openRdfResource2NonLiteral(stmt.getSubject());
				object = ORAdapter.openRdfValue2RMapValue(stmt.getObject());
			} catch (IllegalArgumentException | URISyntaxException e) {
				e.printStackTrace();
				throw new RMapException (e);
			}
			RMapUri predicate = ORAdapter.openRdfUri2RMapUri(stmt.getPredicate());
			RMapTriple triple = new RMapTriple(subject, predicate, object);
			triples.add(triple);
		}
		return triples;
	}

	/**
	 * @return the creatorStmt
	 */
	public Statement getCreatorStmt() {
		return creatorStmt;
	}

	/**
	 * @return the representationStmt
	 */
	public Statement getRepresentationStmt() {
		return representationStmt;
	}
	/**
	 * 
	 * @return
	 */
	public List<Statement> getPropertyStatemts() {
		return this.properties;
	}

}

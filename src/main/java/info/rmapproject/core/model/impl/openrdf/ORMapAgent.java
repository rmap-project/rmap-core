package info.rmapproject.core.model.impl.openrdf;

import java.net.URISyntaxException;
import java.util.List;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.idvalidator.IdValidator;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;
/**
 * 
 *  @author khansen, smorrissey
 * @param <T>
 *
 */
public class ORMapAgent extends ORMapObject implements RMapAgent {
	protected URI context;
	protected Statement creatorStmt;

	
	protected ORMapAgent() throws RMapException {
		super();	
		this.context = ORAdapter.uri2OpenRdfUri(getId());	
		this.typeStatement = 
				this.getValueFactory().createStatement(this.context,RDF.TYPE,RMAP.AGENT,this.context);
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
		String agentIncomingIdStr = null;
		Resource agentIncomingIdResource = null;
		for (Statement stmt:stmts){
			Resource subject = stmt.getSubject();
			URI predicate = stmt.getPredicate();
			Value object = stmt.getObject();
			if (predicate.equals(RDF.TYPE)){
				if (object.equals(RMAP.AGENT)){
					typeFound = true;
					incomingIdValue = subject;
					agentIncomingIdResource = subject;
					agentIncomingIdStr = subject.stringValue();
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
		if (agentIncomingIdStr==null || agentIncomingIdStr.length()==0){
			throw new RMapException ("null or empty agent identifier");
		}
		// creator should not be null if method invoked from service,
		// can be null when creating ORMapAgent from triplestore statements,
		// so we have to check at the end and make sure there is a non-null creator
		if (creator!=null){
			this.setCreatorStmt(creator);
		}			
		// check to make sure Agent has an RMAP-accepted id		
		boolean isValidId = false;
		if (agentIncomingIdResource instanceof URI){
			isValidId = IdValidator.isValidAgentId(ORAdapter.openRdfUri2URI((URI)agentIncomingIdResource));
		}		
		if (isValidId){
			// use incoming id
			try {
				this.id = new java.net.URI(agentIncomingIdStr);
				this.context = ORAdapter.uri2OpenRdfUri(this.getId()); 
			} catch (URISyntaxException e) {
				throw new RMapException ("Cannot convert incoming ID to URI: " + agentIncomingIdStr,e);
			}			
		}
		for (Statement stmt:stmts){
			Resource subject = stmt.getSubject();
			URI predicate = stmt.getPredicate();
			Value object = stmt.getObject();
			if (!isValidId){
				if (subject.stringValue().equals(incomingIdValue.stringValue())){
					subject = this.context;
				}
				if (object.stringValue().equals(incomingIdValue.stringValue())){
					object = this.context;
				}
			}
			if (predicate.equals(RDF.TYPE)){
				if (object.equals(RMAP.AGENT)){
					this.typeStatement= this.getValueFactory().createStatement(
							subject, predicate, object, this.context);
					continue;
				}
				else {
					throw new RMapException("Unrecognized RDF TYPE for Agent: " + object.stringValue());
				}
			}
			if (predicate.equals(DCTERMS.CREATOR)){
				Statement creatorStmt = this.getValueFactory().createStatement(
						subject, predicate, object, this.context);
				this.creatorStmt = creatorStmt;
				continue;
			}
			throw new RMapException ("Unrecognized predicate in Agent: " + predicate.stringValue());
		}
		if (this.creatorStmt==null){
			throw new RMapException ("Null creator for Agent");
		}

	}
	/**
	 * 
	 * @param creator
	 * @throws RMapException
	 */
	public ORMapAgent (URI creator) throws RMapException {
		this();
		if (creator==null){
			throw new RMapException ("Null creator");
		}
		this.typeStatement = 
				this.getValueFactory().createStatement(this.context,RDF.TYPE,RMAP.AGENT,this.context);
		this.setCreatorStmt(creator);
	}
	/**
	 * 
	 * @param agentId
	 * @param creator
	 * @throws RMapException
	 */
	public ORMapAgent (URI agentId,  URI creator)throws RMapException {
		this(ORAdapter.openRdfUri2RMapUri(agentId), ORAdapter.openRdfUri2RMapUri(creator));		
	}
	/**
	 * 
	 * @param agentId
	 * @param creator
	 * @throws RMapException
	 */
	public ORMapAgent (RMapUri agentId, RMapUri creator)throws RMapException {
		this();
		if (creator==null){
			throw new RMapException ("Null creator");
		}		
		URI agentURI = ORAdapter.rMapUri2OpenRdfUri(agentId);
		if (IdValidator.isValidAgentId(agentId.getIri())){
			// use provided id as identifier instead of generated RMapId
			this.context = agentURI;
			this.id = agentId.getIri();
		}
		this.typeStatement = 
				this.getValueFactory().createStatement(this.context,RDF.TYPE,RMAP.AGENT,this.context);
		this.setCreatorStmt(ORAdapter.rMapUri2OpenRdfUri(creator));
	}
	
	@Override
	public Model getAsModel() throws RMapException {
		Model model = new LinkedHashModel();
		model.add(typeStatement);
		if (creatorStmt != null){
			model.add(creatorStmt);
		}
		return model;
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
	

}

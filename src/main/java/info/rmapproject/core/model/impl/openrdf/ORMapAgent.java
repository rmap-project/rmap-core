package info.rmapproject.core.model.impl.openrdf;

import java.net.URISyntaxException;
import java.util.List;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.RDF;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.model.RMapAgent;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

/**
 * 
 *  @author khansen, smorrissey
 *
 */
public class ORMapAgent extends ORMapObject implements RMapAgent {
	protected URI context;
	protected Statement providerIdStmt;
	protected Statement agentIdStmt;

	protected ORMapAgent() throws RMapException {
		super();	
		this.context = ORAdapter.uri2OpenRdfUri(getId());	
		this.typeStatement = 
				this.getValueFactory().createStatement(this.context,RDF.TYPE,RMAP.AGENT,this.context);
	}
	
	public ORMapAgent(List<Statement> stmts)throws RMapException {
		this();
		boolean agentFound = false;
		boolean isRmapId = false;
		Value incomingIdValue = null;
		String agentIncomingId = null;
		for (Statement stmt:stmts){
			Resource subject = stmt.getSubject();
			URI predicate = stmt.getPredicate();
			Value object = stmt.getObject();
			if (predicate.equals(RDF.TYPE)){
				if (object.equals(RMAP.AGENT)){
					agentFound = true;
					incomingIdValue = subject;
					agentIncomingId = ((Resource)subject).stringValue();
					break;
				}
			}
			continue;
		} 
		if (!agentFound){
			throw new RMapException ("No type statement found indicating AGENT");
		}
		if (agentIncomingId==null || agentIncomingId.length()==0){
			throw new RMapException ("null or empty agent identifier");
		}
		// check to make sure Agent has an RMAP id not a local one
		try {
			isRmapId  = 
					IdServiceFactoryIOC.getFactory().createService().isValidId(
							new java.net.URI(agentIncomingId));
		} catch (Exception e) {
			throw new RMapException ("Unable to validate DiSCO id " + 
					agentIncomingId, e);
		}
		if (isRmapId){
			// use incoming id
			try {
				this.id = new java.net.URI(agentIncomingId);
				this.context = ORAdapter.uri2OpenRdfUri(this.getId()); 
			} catch (URISyntaxException e) {
				throw new RMapException ("Cannot convert incoming ID to URI: " + agentIncomingId,e);
			}			
		}
		else {
			// create a statement saying what original id was
			Statement idStmt = this.getValueFactory().createStatement(this.context, RMAP.PROVIDERID,
					incomingIdValue, this.context);
			this.providerIdStmt = idStmt;
		}
		for (Statement stmt:stmts){
			Resource subject = stmt.getSubject();
			URI predicate = stmt.getPredicate();
			Value object = stmt.getObject();
			if (!isRmapId){
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
			if (predicate.equals( RMAP.RMAP_AGENT_ID)){
				this.agentIdStmt= this.getValueFactory().createStatement(
						subject, predicate, object, this.context);
				continue;
			}
			throw new RMapException ("Unrecognized predicate in Agent: " + predicate.stringValue());
		}
	}
	
	public ORMapAgent (URI agentId)throws RMapException {
		this();
		this.context = ORAdapter.uri2OpenRdfUri(getId());
		this.agentIdStmt = this.getValueFactory().createStatement(context, RMAP.RMAP_AGENT_ID, 
				agentId, context);
	}
	
	public ORMapAgent (RMapUri agentId)throws RMapException {
		this(ORAdapter.rMapUri2OpenRdfUri(agentId));
	}

	@Override
	public Model getAsModel() throws RMapException {
		Model model = new LinkedHashModel();
		model.add(typeStatement);
		model.add(agentIdStmt);
		if (providerIdStmt != null){
			model.add(providerIdStmt);
		}
		return model;
	}

	@Override
	public java.net.URI getAgentId() throws RMapException {
		URI agent = (URI)this.agentIdStmt.getObject();
		return ORAdapter.openRdfUri2URI(agent);
	}

}

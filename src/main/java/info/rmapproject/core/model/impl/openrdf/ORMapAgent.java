package info.rmapproject.core.model.impl.openrdf;

import java.net.URISyntaxException;
import java.util.ArrayList;
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
	protected Statement providerIdStmt;
	protected List<Statement> profileStmts = new ArrayList<Statement>();

	
	protected ORMapAgent() throws RMapException {
		super();	
		this.context = ORAdapter.uri2OpenRdfUri(getId());	
		this.profileStmts = new ArrayList<Statement>();
		this.typeStatement = 
				this.getValueFactory().createStatement(this.context,RDF.TYPE,RMAP.AGENT,this.context);
	}
	/**
	 * 
	 * @param stmts
	 * @throws RMapException
	 */
	public ORMapAgent(List<Statement> stmts)throws RMapException {
		this();
		boolean agentFound = false;
		Value incomingIdValue = null;
		String agentIncomingIdStr = null;
		Resource agentIncomingIdResource = null;
		for (Statement stmt:stmts){
			Resource subject = stmt.getSubject();
			URI predicate = stmt.getPredicate();
			Value object = stmt.getObject();
			if (predicate.equals(RDF.TYPE)){
				if (object.equals(RMAP.AGENT)){
					agentFound = true;
					incomingIdValue = subject;
					agentIncomingIdResource = subject;
					agentIncomingIdStr = ((Resource)subject).stringValue();
					break;
				}
			}
			continue;
		} 
		if (!agentFound){
			throw new RMapException ("No type statement found indicating AGENT");
		}
		if (agentIncomingIdStr==null || agentIncomingIdStr.length()==0){
			throw new RMapException ("null or empty agent identifier");
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
		else {
			// move the provider ID into the profile
			//TODO is a provider ID an ID we would want to make part of a profile
			Statement idStmt = this.getValueFactory().createStatement(this.context, RMAP.PROVIDERID,
					incomingIdValue, this.context);
			this.providerIdStmt = idStmt;
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
			if (predicate.equals(DCTERMS.DESCRIPTION)){
				Statement profileStmt = this.getValueFactory().createStatement(
						subject, predicate, object, this.context);
				this.profileStmts.add(profileStmt);
				continue;
			}
			throw new RMapException ("Unrecognized predicate in Agent: " + predicate.stringValue());
		}
	}
	
	public ORMapAgent (URI agentId)throws RMapException {
		this(ORAdapter.openRdfUri2RMapUri(agentId));		
	}
	
	public ORMapAgent (RMapUri agentId)throws RMapException {
		this();
		URI agentURI = ORAdapter.rMapUri2OpenRdfUri(agentId);
		if (IdValidator.isValidAgentId(agentId.getIri())){
			// use provided id as identifier instead of generated RMapId
			this.context = agentURI;
		}
		else{
			// consider this provider id, and keep RMap id as identifier
//			TODO  do we need to create a Profile here
			this.typeStatement= this.getValueFactory().createStatement(
					this.context, RMAP.PROVIDERID, agentURI, this.context);
		}		
	}
	
	@Override
	public Model getAsModel() throws RMapException {
		Model model = new LinkedHashModel();
		model.add(typeStatement);
		if (providerIdStmt != null){
			model.add(providerIdStmt);
		}
		for (Statement stmt:profileStmts){
			model.add(stmt);
		}
		return model;
	}

	@Override
	public List<RMapUri> getProfileIds() throws RMapException {
		List<RMapUri> uris = new ArrayList<RMapUri>();
		for (Statement stmt:this.profileStmts){
			URI profile = (URI)stmt.getObject();
			uris.add(ORAdapter.openRdfUri2RMapUri(profile));
		}
		return uris;
	}

	@Override
	public void setProfileIds(List<RMapUri> profileIds)
			throws RMapException {
		if (profileIds==null){
			throw new RMapException("Null profileIds");
		}
		List<Statement>stmts = new ArrayList<Statement>();
		for (RMapUri id:profileIds){
			URI uri = ORAdapter.rMapUri2OpenRdfUri(id);
			Statement stmt = this.getValueFactory().createStatement(context, 
					DCTERMS.DESCRIPTION, uri, context);
			stmts.add(stmt);
		}
		this.profileStmts = stmts;
	}
	@Override
	public void addProfileId(RMapUri profileId) throws RMapException {
		if (profileId==null){
			throw new RMapException("null profileID");
		}
		URI uri = ORAdapter.rMapUri2OpenRdfUri(profileId);
		this.addProfileURI(uri);
	}
	/**
	 * 
	 * @param uri
	 * @throws RMapException
	 */
	public void addProfileURI (URI uri) throws RMapException{
		Statement stmt = this.getValueFactory().createStatement(this.context, 
				DCTERMS.DESCRIPTION, uri, this.context);
		this.profileStmts.add(stmt);
	}
	

}

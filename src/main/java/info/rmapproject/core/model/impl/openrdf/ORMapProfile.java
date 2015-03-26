/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;

import info.rmapproject.core.controlledlist.IdPredicate;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.idvalidator.IdValidator;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.agent.RMapProfile;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

/**
 * @author smorrissey
 *
 */
public class ORMapProfile extends ORMapObject implements RMapProfile {

	protected List<Statement> identityStmts = new ArrayList<Statement>();
	protected List<Statement> propertyStmts= new ArrayList<Statement>();
	protected Statement preferredIdentityStmt;
	protected Statement parentAgentStmt;
	protected Statement creatorStmt;
	protected URI context;
	/**
	 * @throws RMapException
	 */
	protected ORMapProfile() throws RMapException {
		super();
		this.context = ORAdapter.uri2OpenRdfUri(getId());
		this.typeStatement = this.getValueFactory().createStatement(this.context,
				RDF.TYPE, RMAP.PROFILE, this.context);
	}
	/**
	 * 
	 * @param parentAgentId
	 * @param creatorId
	 * @throws RMapException
	 */
	public ORMapProfile(URI parentAgentId, URI creatorId) throws RMapException{
		this();
		if (parentAgentId==null) {
			throw new RMapException("Null parent agent id");
		}
		Statement stmt = this.getValueFactory().createStatement(this.context, 
				RMAP.DESCRIBES_AGENT, parentAgentId, this.context);
		this.parentAgentStmt = stmt;
		this.setCreatorStmt(creatorId);
	}
	/**
	 * 
	 * @param stmts
	 * @param creatorId System agent who creates profile; can be null if constructor is using statements in triplestore;
	 * should NOT be null if service is creating statements from RDF and has not yet put statements in triplestore
	 * @throws RMapException
	 */
	public ORMapProfile(List<Statement> stmts, URI creatorId)throws RMapException {
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
				if (object.equals(RMAP.PROFILE)){
					typeFound = true;
					incomingIdValue = subject;
					agentIncomingIdResource = subject;
					agentIncomingIdStr = ((Resource)subject).stringValue();
					break;
				}
				continue;
			}
		}
		if (!typeFound){
			throw new RMapException ("no profile type statement found in statement list");
		}
		// creator should not be null if method invoked from service,
				// can be null when creating ORMapAgent from triplestore statements,
				// so we have to check at the end and make sure there is a non-null creator
		if (creatorId!=null){
			this.setCreatorStmt(creatorId);
		}	
		boolean isValidId = false;
		boolean isBNode = false;
		if (agentIncomingIdResource instanceof URI){
			isValidId = IdValidator.isValidAgentId(ORAdapter.openRdfUri2URI((URI)agentIncomingIdResource));
		}
		else {
			isBNode = true;
		}
		if (isValidId){
			// use incoming id
			try {
				this.id = new java.net.URI(agentIncomingIdStr);
				this.context = ORAdapter.uri2OpenRdfUri(this.getId()); 
			} catch (URISyntaxException e) {
				throw new RMapException 
				("Cannot convert incoming ID to URI: " + agentIncomingIdStr,e);
			}	
		}
		else {
			// if bNode, we don't care what the value is; if it's some other (non-RMap) identifier, keep it as an identity
			if (!isBNode){
				Statement idStmt = this.getValueFactory().createStatement(
						this.context, RMAP.PROFILE_ID_BY, agentIncomingIdResource, this.context);
				this.identityStmts.add(idStmt);
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
				// ONLY accepting properties with Profile id as subject
				else {
					throw new RMapException 
					("Profile property subject is not equal to supplied Profile id: " +
					  subject.stringValue());
				}
			}
			if (predicate.equals(RDF.TYPE)){
				if (object.equals(RMAP.PROFILE)){
					this.typeStatement= this.getValueFactory().createStatement(
							subject, predicate, object, this.context);
					continue;
				}
				else {
					throw new RMapException("Unrecognized RDF TYPE for PROFILE: " + object.stringValue());
				}
			}
			if (predicate.equals(DCTERMS.CREATOR)){
				Statement creatorStmt = this.getValueFactory().createStatement(
						subject, predicate, object, this.context);
				this.creatorStmt = creatorStmt;
				continue;
			}
			if (predicate.equals(RMAP.PROFILE_ID_BY)){
				Statement idStmt = this.getValueFactory().createStatement(
						subject, predicate, object, this.context);
				this.identityStmts.add(idStmt);
				continue;
			}
			java.net.URI predUri = ORAdapter.openRdfUri2URI(predicate);
			if (IdPredicate.isAgentIdPredicate(predUri)){
				Statement idStmt = this.getValueFactory().createStatement(
						subject, predicate, object, this.context);
				this.identityStmts.add(idStmt);
				continue;
			}
			// all other predicates presumed to be ok, and more descriptive info about agent
			Statement propStmt = this.getValueFactory().createStatement(
					subject, predicate, object, this.context);
			propertyStmts.add(propStmt);
			continue;
		}
		if (this.creatorStmt==null){
			throw new RMapException ("Null creator for Profile");
		}	
		if (this.parentAgentStmt == null){
			throw new RMapException ("Null parent agent statement for profile");
		}
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.agent.RMapProfile#getProperties()
	 */
	@Override
	public Map<RMapUri, RMapValue> getProperties() throws RMapException {
		Map<RMapUri, RMapValue> propMap = new HashMap<RMapUri, RMapValue>();
		if (propertyStmts != null){
			for (Statement stmt:propertyStmts){
				URI predicate = stmt.getPredicate();
				Value object = stmt.getObject();
				try {
					propMap.put(ORAdapter.openRdfUri2RMapUri(predicate), 
							ORAdapter.openRdfValue2RMapValue(object));
				} catch (IllegalArgumentException | URISyntaxException e) {
					throw new RMapException (e);
				}			
			}
		}
		return propMap;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.agent.RMapProfile#getIdentities()
	 */
	@Override
	public List<RMapUri> getIdentities() throws RMapException {
		List<RMapUri>uris = new ArrayList<RMapUri>();
		if (identityStmts != null){
			for (Statement stmt:identityStmts){
				Value object = stmt.getObject();
				if (object instanceof URI){
					URI uri = (URI)object;
					uris.add(ORAdapter.openRdfUri2RMapUri(uri));
				}
				else {
					throw new RMapException("non-uri identity: " + object.stringValue());
				}
			}
		}
		return uris;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.agent.RMapProfile#getPreferredIdentity()
	 */
	@Override
	public RMapUri getPreferredIdentity() throws RMapException {
		RMapUri preferredId = null;
		if (this.preferredIdentityStmt!= null){
			Value object = preferredIdentityStmt.getObject();
			if (object instanceof URI){
				preferredId = ORAdapter.openRdfUri2RMapUri((URI)object);
			}
			else  {
				throw new RMapException("non-uri preferred identity: " + object.stringValue());
			}
		}
		return preferredId;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.impl.openrdf.ORMapObject#getAsModel()
	 */
	@Override
	public Model getAsModel() throws RMapException {
		Model model = new LinkedHashModel();
		model.add(this.typeStatement);
		model.add(this.parentAgentStmt);
		if (this.creatorStmt!= null){
			model.add(this.creatorStmt);
		}
		if (this.preferredIdentityStmt!= null){
			model.add(this.preferredIdentityStmt);
		}
		if (this.identityStmts!=null){
			for (Statement stmt:this.identityStmts){
				model.add(stmt);
			}
		}
		if (this.propertyStmts!=null){
			for (Statement stmt:this.propertyStmts){
				model.add(stmt);
			}
		}
		return model;
	}

	@Override
	public void setProperties(Map<RMapUri, RMapValue> propertyMap)
			throws RMapException {
		if (propertyMap==null){
			return;
		}
		List<Statement>propStmts = new ArrayList<Statement>();
		for (RMapUri predicate:propertyMap.keySet()){
			RMapValue object = propertyMap.get(predicate);
			Statement stmt = this.getValueFactory().createStatement(
					this.context, ORAdapter.rMapUri2OpenRdfUri(predicate), 
					ORAdapter.rMapValue2OpenRdfValue(object),this.context);
			propStmts.add(stmt);
		}
		this.propertyStmts= propStmts;
		return;
	}

	@Override
	public void setIdentities(List<RMapUri> idents) throws RMapException {
		if (idents==null){
			return;
		}
		List<Statement> idStmts = new ArrayList<Statement>();
		for (RMapUri ident:idents){
			URI id = ORAdapter.rMapUri2OpenRdfUri(ident);
			Statement idStmt = this.getValueFactory().createStatement(this.context,
					RMAP.PROFILE_ID_BY, id, this.context);
			idStmts.add(idStmt);
		}
		this.identityStmts = idStmts;
	}

	@Override
	public void setPreferredIdentity(RMapUri ident) throws RMapException {
		if (ident==null){
			return;
		}
		URI idUri = ORAdapter.rMapUri2OpenRdfUri(ident);
		Statement idStmt = this.getValueFactory().createStatement(this.context,
				RMAP.PROFILE_PREFERRED_ID, idUri, this.context);
		this.preferredIdentityStmt = idStmt;
	}

	@Override
	public RMapUri getParentAgentId() throws RMapException {
		RMapUri parentId = null;
		if (this.parentAgentStmt==null){
			throw new RMapException("Null parent agent id in agent profile");
		}
		Value id = parentAgentStmt.getObject();
		if (!(id instanceof URI)){
			throw new RMapException("Parent agent id not URI");
		}
		URI uri = ((URI)id);
		parentId = ORAdapter.openRdfUri2RMapUri(uri);
		return parentId;
	}

	/**
	 * @return the identityStmts
	 */
	public List<Statement> getIdentityStmts() {
		return identityStmts;
	}

	/**
	 * @param identityStmts the identityStmts to set
	 */
	public void setIdentityStmts(List<Statement> identityStmts) {
		this.identityStmts = identityStmts;
	}

	/**
	 * @return the propertyStmts
	 */
	public List<Statement> getPropertyStmts() {
		return propertyStmts;
	}

	/**
	 * @param propertyStmts the propertyStmts to set
	 */
	public void setPropertyStmts(List<Statement> propertyStmts) {
		this.propertyStmts = propertyStmts;
	}

	/**
	 * @return the preferredIdentityStmt
	 */
	public Statement getPreferredIdentityStmt() {
		return preferredIdentityStmt;
	}

	/**
	 * @param preferredIdentityStmt the preferredIdentityStmt to set
	 */
	public void setPreferredIdentityStmt(Statement preferredIdentityStmt) {
		this.preferredIdentityStmt = preferredIdentityStmt;
	}

	/**
	 * @return the parentAgentStmt
	 */
	public Statement getParentAgentStmt() {
		return parentAgentStmt;
	}

	/**
	 * @param parentAgentStmt the parentAgentStmt to set
	 */
	public void setParentAgentStmt(Statement parentAgentStmt) {
		this.parentAgentStmt = parentAgentStmt;
	}

	public void addIdentity(URI id){
		Statement idStmt = this.getValueFactory().createStatement(this.context,
				RMAP.PROFILE_ID_BY, id, this.context);
		this.identityStmts.add(idStmt);
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

	@Override
	public RMapUri getCreator() throws RMapException {
		Value value = this.creatorStmt.getObject();
		RMapUri rUri = null;
		if (value instanceof URI){
			rUri = ORAdapter.openRdfUri2RMapUri((URI)value);
		}
		else {
			throw new RMapException ("Creator is not a URI: " + value.stringValue());
		}
		return rUri;
	}

	/**
	 * @return the creatorStmt
	 */
	public Statement getCreatorStmt() {
		return creatorStmt;
	}
}

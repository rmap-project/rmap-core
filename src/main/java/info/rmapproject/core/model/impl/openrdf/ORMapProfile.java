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
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.RDF;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.agent.RMapProfile;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

/**
 * @author smorrissey
 *
 */
public class ORMapProfile extends ORMapObject implements RMapProfile {

	protected List<Statement> identityStmts;
	protected List<Statement> propertyStmts;
	protected Statement preferredIdentityStmt;
	protected Statement parentAgentStmt;
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
	
	public ORMapProfile(URI parentAgentId) throws RMapException{
		this();
		if (parentAgentId==null) {
			throw new RMapException("Null parent agent id");
		}
		Statement stmt = this.getValueFactory().createStatement(this.context, 
				RMAP.DESCRIBES_AGENT, parentAgentId, this.context);
		this.parentAgentStmt = stmt;
	}
	
	public ORMapProfile(List<Statement> stmts)throws RMapException {
		this();
		//TODO complete body
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


}

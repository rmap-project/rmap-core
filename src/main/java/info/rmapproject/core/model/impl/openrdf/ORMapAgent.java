package info.rmapproject.core.model.impl.openrdf;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.model.RMapObjectType;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.vocabulary.impl.openrdf.RMAP;

import java.util.Set;

import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
/**
 * 
 *  @author khanson, smorrissey
 *
 */
public class ORMapAgent extends ORMapObject implements RMapAgent {
	protected Statement nameStmt;
	protected Statement idProviderStmt;
	protected Statement authIdStmt;

	/**
	 * 
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	protected ORMapAgent() throws RMapException {
		super();
		this.setId();	
		this.setTypeStatement(RMapObjectType.AGENT);
	}
	
	/**
	 * Creates new RMap Agent object based on ID Provider, User Auth ID, and name
	 * @param idProvider
	 * @param name
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public ORMapAgent(RMapIri idProvider, RMapIri authId, RMapValue name) 
			throws RMapException, RMapDefectiveArgumentException {
		this();
		this.setIdProviderStmt(ORAdapter.rMapIri2OpenRdfIri(idProvider));
		this.setAuthIdStmt(ORAdapter.rMapIri2OpenRdfIri(authId));
		this.setNameStmt(ORAdapter.rMapValue2OpenRdfValue(name));
	}
	
	/**
	 * Creates new RMap Agent object based on user provided agentUri, ID Provider, User Auth ID, and name
	 * @param agentUri
	 * @param idProvider
	 * @param name
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public ORMapAgent(IRI agentUri, IRI idProvider, IRI authId, Value name) 
			throws RMapException, RMapDefectiveArgumentException {		
		this.setId(agentUri);		
		this.setTypeStatement(RMapObjectType.AGENT);
		this.setContext(agentUri);
		this.setIdProviderStmt(idProvider);
		this.setAuthIdStmt(authId);
		this.setNameStmt(name);
	}
		
	@Override
	public Model getAsModel() throws RMapException {
		Model model = new LinkedHashModel();
		model.add(typeStatement);
		model.add(nameStmt);
		model.add(idProviderStmt);
		model.add(authIdStmt);
		return model;
	}
	
	/**
	 * Creates an RMapAgent object from a list of statements - must include statements for 1 name, 1 id provider, 1 user auth id.
	 * @param stmts
	 * @param creator
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public ORMapAgent(Set<Statement> stmts)throws RMapException, RMapDefectiveArgumentException {
		//this(); //sets default id and type
		if (stmts==null){
			throw new RMapDefectiveArgumentException("Null statement list");
		}	
		
		//Checks all URIs can be converted to java.net.URI - makes sure they are cross compatible
		ORAdapter.checkOpenRdfIri2UriCompatibility(stmts);
		
		//check there is a type statement, if so get the incoming ID value from that.
		boolean typeFound = false;
		Value assertedAgentId = null;
		Resource officialAgentId = null;
		
		for (Statement stmt:stmts){
			if (stmt.getPredicate().equals(RDF.TYPE) && stmt.getObject().equals(RMAP.AGENT)){
				typeFound = true;
				assertedAgentId = stmt.getSubject();
				officialAgentId = stmt.getContext();
				break;
			}
			continue;
		} 
		if (!typeFound){
			throw new RMapException ("No type statement found indicating AGENT");
		}
		if (assertedAgentId==null || assertedAgentId.stringValue().length()==0){
			throw new RMapException ("Null or empty agent identifier. The Agent object must be identified by either a blank node or an existing Agent URI");
		}

		//if agent has come in without a context, generate ID. This will happen if it's a new agent
		if (officialAgentId==null || officialAgentId.stringValue().length()==0){
			this.setId();
			officialAgentId = (Resource) this.getId();
		}
		else {
			this.setId((IRI) officialAgentId);
		}
		
		this.setTypeStatement(RMapObjectType.AGENT);
				
		//loop through and check we have all vital components for Agent.
		boolean typeRecorded = false;
		boolean nameRecorded = false;
		boolean idProviderRecorded = false;
		boolean authIdRecorded = false;
		
		for (Statement stmt:stmts){
			Resource subject = stmt.getSubject();
			IRI predicate = stmt.getPredicate();
			Value object = stmt.getObject();
			boolean agentIsSubject = subject.stringValue().equals(assertedAgentId.stringValue());
			if (agentIsSubject && predicate.equals(RDF.TYPE) && object.equals(RMAP.AGENT) && !typeRecorded){
				setTypeStatement(RMapObjectType.AGENT);
				typeRecorded=true;
				}
			else if (agentIsSubject && predicate.equals(FOAF.NAME) && !nameRecorded){
				setNameStmt(object);
				nameRecorded=true;
			}
			else if (agentIsSubject && predicate.equals(RMAP.IDENTITYPROVIDER) && !idProviderRecorded){
				setIdProviderStmt((IRI)object);
				idProviderRecorded=true;
			}
			else if (agentIsSubject && predicate.equals(RMAP.USERAUTHID) && !authIdRecorded){
				setAuthIdStmt((IRI)object);
				authIdRecorded=true;
			}
			else { //there is an invalid statement in there
				throw new RMapException ("Invalid statement found in RMap:Agent object: (" + subject + ", " + predicate + ", " + object +"). "
										+ "Agents should contain 1 rdf:type definition, 1 foaf:name, 1 rmap:idProvider, and 1 rmap:userAuthId.");
			}
		}
		if (!typeRecorded){ //should have already been caught but JIC.
			throw new RMapException ("The foaf:name statement is missing from the Agent");
		}
		if (!nameRecorded){
			throw new RMapException ("The foaf:name statement is missing from the Agent");
		}
		if (!idProviderRecorded){
			throw new RMapException ("The rmap:idProvider statement is missing from the Agent");
		}
		if (!authIdRecorded){
			throw new RMapException ("The rmap:userAuthId statement is missing from the Agent");
		}

	}

	@Override
	public RMapValue getName() throws RMapException {
		RMapValue name = null;
		if (this.nameStmt!= null){
			Value value = this.nameStmt.getObject();
			try {
				name = ORAdapter.openRdfValue2RMapValue(value);
			} catch(RMapDefectiveArgumentException e) {
				throw new RMapException("Could not convert Name value [" + value.stringValue() + "] to RMapValue");
			}
		}
		return name;
	}

	/**
	 * @return the nameStmt
	 */
	public Statement getNameStmt() {
		return nameStmt;
	}
	
	/**
	 * @param name
	 * @throws RMapDefectiveArgumentException 
	 */
	protected void setNameStmt (Value name) throws RMapDefectiveArgumentException{
		if (name == null || name.toString().length()==0)
			{throw new RMapDefectiveArgumentException("RMapAgent name is null or empty");}
		Statement stmt = ORAdapter.getValueFactory().createStatement(this.context, 
				FOAF.NAME, name, this.context);
		this.nameStmt = stmt;
	}

	/**
	 * @return idProvider
	 */
	@Override
	public RMapIri getIdProvider() throws RMapException {
		RMapIri idProvider = null;
		if (this.idProviderStmt!= null){
			IRI value = (IRI)this.idProviderStmt.getObject();
			idProvider = ORAdapter.openRdfIri2RMapIri(value);
		}
		return idProvider;
	}

	/**
	 * @return the idProviderStmt
	 */
	public Statement getIdProviderStmt() {
		return idProviderStmt;
	}
	
	/**
	 * @param idProvider
	 */
	protected void setIdProviderStmt (IRI idProvider) throws RMapDefectiveArgumentException{
		if (idProvider == null || idProvider.toString().length()==0)
			{throw new RMapDefectiveArgumentException("RMapAgent idProvider is null or empty");}
		
		Statement stmt = ORAdapter.getValueFactory().createStatement(this.context, 
				RMAP.IDENTITYPROVIDER, idProvider, this.context);
		this.idProviderStmt = stmt;
	}
	
	/**
	 * @return authId 
	 */
	@Override
	public RMapIri getAuthId() throws RMapException {
		RMapIri authIdValue = null;
		if (this.authIdStmt!= null){
			IRI value = (IRI)this.authIdStmt.getObject();
			authIdValue = ORAdapter.openRdfIri2RMapIri(value);
		}
		return authIdValue;
	}
	
	/**
	 * @return the authIdStmt
	 */
	public Statement getAuthIdStmt() {
		return authIdStmt;
	}
	
	/**
	 * @param authId
	 * @throws RMapDefectiveArgumentException 
	 */
	protected void setAuthIdStmt (IRI authId) throws RMapDefectiveArgumentException{
		if (authId == null || authId.toString().length()==0)
			{throw new RMapDefectiveArgumentException("RMapAgent authId is null or empty");}
		Statement stmt = ORAdapter.getValueFactory().createStatement(this.context, 
				RMAP.USERAUTHID, authId, this.context);
		this.authIdStmt = stmt;
	}


}

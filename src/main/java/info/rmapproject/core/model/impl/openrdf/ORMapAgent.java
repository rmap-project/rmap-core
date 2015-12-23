package info.rmapproject.core.model.impl.openrdf;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.util.List;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
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
		this.setTypeStatement(RMAP.AGENT);
	}
	
	/**
	 * Creates new RMap Agent object based on ID Provider, User Auth ID, and name
	 * @param idProvider
	 * @param name
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public ORMapAgent(RMapUri idProvider, RMapUri authId, RMapValue name) 
			throws RMapException, RMapDefectiveArgumentException {
		this();
		this.setIdProviderStmt(ORAdapter.rMapUri2OpenRdfUri(idProvider));
		this.setAuthIdStmt(ORAdapter.rMapUri2OpenRdfUri(authId));
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
	public ORMapAgent(URI agentUri, URI idProvider, URI authId, Value name) 
			throws RMapException, RMapDefectiveArgumentException {		
		this.setId(agentUri);		
		this.setTypeStatement(RMAP.AGENT);
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
	public ORMapAgent(List<Statement> stmts)throws RMapException, RMapDefectiveArgumentException {
		this(); //sets default id and type
		if (stmts==null){
			throw new RMapDefectiveArgumentException("Null statement list");
		}	
		
		//Checks all URIs can be converted to java.net.URI - makes sure they are cross compatible
		ORAdapter.checkOpenRdfUri2UriCompatibility(stmts);
		
		//check there is a type statement, if so get the incoming ID value from that.
		boolean typeFound = false;
		Value incomingIdValue = null;
		for (Statement stmt:stmts){
			if (stmt.getPredicate().equals(RDF.TYPE) && stmt.getObject().equals(RMAP.AGENT)){
				typeFound = true;
				incomingIdValue = stmt.getSubject();
				break;
			}
			continue;
		} 
		if (!typeFound){
			throw new RMapException ("No type statement found indicating AGENT");
		}
		if (incomingIdValue==null || incomingIdValue.stringValue().length()==0){
			throw new RMapException ("null or empty agent identifier");
		}

		// check to see if Agent has an RMAP id not a local one, or bnode id, if it does... use it!
		if (incomingIdValue instanceof URI){
			boolean isRmapUri = isRMapUri((URI)incomingIdValue);
			if (isRmapUri){	// then use incoming id
				this.setId((URI)incomingIdValue); 	
			}
		}
		
		//loop through and check we have all vital components for Agent.
		boolean typeRecorded = false;
		boolean nameRecorded = false;
		boolean idProviderRecorded = false;
		boolean authIdRecorded = false;
		
		for (Statement stmt:stmts){
			Resource subject = stmt.getSubject();
			URI predicate = stmt.getPredicate();
			Value object = stmt.getObject();
			boolean agentIsSubject = false;
			if (subject.stringValue().equals(incomingIdValue.stringValue())){
				agentIsSubject = true;
			}
			if (agentIsSubject && predicate.equals(RDF.TYPE) && object.equals(RMAP.AGENT) && !typeRecorded){
				setTypeStatement(RMAP.AGENT);
				typeRecorded=true;
				}
			else if (agentIsSubject && predicate.equals(FOAF.NAME) && !nameRecorded){
				setNameStmt(object);
				nameRecorded=true;
			}
			else if (agentIsSubject && predicate.equals(RMAP.IDENTITY_PROVIDER) && !idProviderRecorded){
				setIdProviderStmt((URI)object);
				idProviderRecorded=true;
			}
			else if (agentIsSubject && predicate.equals(RMAP.USER_AUTH_ID) && !authIdRecorded){
				setAuthIdStmt((URI)object);
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
		Statement stmt = this.getValueFactory().createStatement(this.context, 
				FOAF.NAME, name, this.context);
		this.nameStmt = stmt;
	}

	/**
	 * @return idProvider
	 */
	@Override
	public RMapUri getIdProvider() throws RMapException {
		RMapUri idProvider = null;
		if (this.idProviderStmt!= null){
			URI value = (URI)this.idProviderStmt.getObject();
			idProvider = ORAdapter.openRdfUri2RMapUri(value);
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
	protected void setIdProviderStmt (URI idProvider) throws RMapDefectiveArgumentException{
		if (idProvider == null || idProvider.toString().length()==0)
			{throw new RMapDefectiveArgumentException("RMapAgent idProvider is null or empty");}
		
		Statement stmt = this.getValueFactory().createStatement(this.context, 
				RMAP.IDENTITY_PROVIDER, idProvider, this.context);
		this.idProviderStmt = stmt;
	}
	
	/**
	 * @return authId 
	 */
	@Override
	public RMapUri getAuthId() throws RMapException {
		RMapUri authIdValue = null;
		if (this.authIdStmt!= null){
			URI value = (URI)this.authIdStmt.getObject();
			authIdValue = ORAdapter.openRdfUri2RMapUri(value);
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
	protected void setAuthIdStmt (URI authId) throws RMapDefectiveArgumentException{
		if (authId == null || authId.toString().length()==0)
			{throw new RMapDefectiveArgumentException("RMapAgent authId is null or empty");}
		Statement stmt = this.getValueFactory().createStatement(this.context, 
				RMAP.USER_AUTH_ID, authId, this.context);
		this.authIdStmt = stmt;
	}





}

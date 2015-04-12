/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import info.rmapproject.core.controlledlist.AgentPredicate;
import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.idvalidator.PreferredIdValidator;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORMapIdentity;
import info.rmapproject.core.model.impl.openrdf.ORMapProfile;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.repository.RepositoryException;

/**
 * @author smorrissey
 *
 */
public class ORMapAgentMgr extends ORMapObjectMgr {

	static List<URI> agentPredicates = new ArrayList<URI>();
	static {
		List<java.net.URI>preds = AgentPredicate.getAgentPredicates();
		for (java.net.URI uri:preds){
			URI aPred = ORAdapter.uri2OpenRdfUri(uri);
			agentPredicates.add(aPred);
		}
	}
	/**
	 * 
	 */
	public ORMapAgentMgr() {
		super();
	}
	
	/**
	 * 
	 * @param agentId
	 * @param ts
	 * @return
	 * @throws RMapAgentNotFoundException
	 * @throws RMapException
	 */
	public ORMapAgent readAgent(URI agentId, SesameTriplestore ts)
	throws RMapAgentNotFoundException, RMapException {		
		if (agentId == null){
			throw new RMapException("null agentId");
		}
		if (ts==null){
			throw new RMapException("null triplestore");
		}
		
		if (!(this.isAgentId(agentId, ts))){
			throw new RMapAgentNotFoundException("Not an agentID: " + agentId.stringValue());
		}
		List<Statement> agentStmts = null;
		try {
			agentStmts = this.getNamedGraph(agentId, ts);	
		}
		catch (RMapObjectNotFoundException e) {
			throw new RMapAgentNotFoundException ("No agent found with id " + agentId.toString(), e);
		}
		ORMapAgent agent = new ORMapAgent(agentStmts, null);
		return agent;
	}
	/**
	 * 
	 * @param agent
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public URI createAgent (ORMapAgent agent, SesameTriplestore ts)
	throws RMapException {
		if (agent==null){
			throw new RMapException ("null agent");
		}
		if (ts==null){
			throw new RMapException ("null triplestore");
		}
		URI agentId = agent.getContext();
		this.createAgentTriples(agent, ts);
		return agentId;
	}
	
	/**
	 * 
	 * @param agent
	 * @param ts
	 * @throws RMapException
	 */
	public void createAgentTriples (ORMapAgent agent, SesameTriplestore ts) 
	throws RMapException {
		Model model = agent.getAsModel();
		Iterator<Statement> iterator = model.iterator();
		while (iterator.hasNext()){
			Statement stmt = iterator.next();
			this.createTriple(ts, stmt);
		}
		return;
	}

	/**
	 * Create a bare Agent, with nothing more than ID and creator 
	 * @param agentId
	 * @param ts
	 * @return ORMapAgent new agent object
	 */
	protected ORMapAgent createAgent(URI agentId, URI systemAgent,
			SesameTriplestore ts) 
	throws RMapException {
		ORMapAgent agent = new ORMapAgent(agentId, systemAgent);		
		this.createAgentTriples(agent, ts);
		return agent;
	}
	/**
	 * Create agent and profiles for DiSCO creator
	 * @param creator
	 * @param systemAgent
	 * @param profilemgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public List<URI> createAgentAndProfiles(URI creator, URI systemAgent, 
			ORMapProfileMgr profilemgr, SesameTriplestore ts)
	throws RMapException {
		if (creator == null){
			throw new RMapException ("Null creator");
		}
		if (systemAgent==null){
			throw new RMapException ("Null systemAgent");
		}
		if (ts==null){
			throw new RMapException ("Null tripleStore");
		}
		List<URI> newObjects = new ArrayList<URI>();
		ORMapAgent agent = this.createAgent(creator, systemAgent, ts);
		URI agentUri = ORAdapter.uri2OpenRdfUri(agent.getId());
		newObjects.add(agentUri);
		// if agent ID not same as agentId, need to create Profile with agentID as providerID
		if (!(creator.equals(agentUri))){
			URI profileUri = profilemgr.createSuppliedIdProfile(creator, agentUri, 
					systemAgent, ts);
			newObjects.add(profileUri);
		}		
		return newObjects;
	}
	
	/**
	 * Create agent, profile objects from DiSCO related statemetns
	 * @param relatedStmts
	 * @param systemAgent DiSCO creator UIR
	 * @param profilemgr
	 * @param identitymgr 
	 * @param ts
	 * @return List of URIs of new Agent, Profile objects created from related statements
	 * Could modify list of relatedStatements in DiSCO to reflect new objects and their ids
	 * @throws RMapException
	 */
	public List<URI> createRelatedStatementsAgents (List<Statement> relatedStmts,
			URI systemAgent, ORMapProfileMgr profilemgr, ORMapIdentityMgr identitymgr,
			SesameTriplestore ts)
	throws RMapException {
		//TODO what about checking creator on any of these
		if (relatedStmts ==null){
			throw new RMapException("Null related statement map");
		}
		if (systemAgent==null){
			throw new RMapException ("Null system agent");
		}
		if (profilemgr ==null){
			throw new RMapException ("Null profile manager");
		}
		if (ts ==null){
			throw new RMapException ("Null triplestore");
		}
		List<URI> newObjects = new ArrayList<URI>();
		// We will be modifying some statements that appeared in DiSCO (change bnode to id)
		// and adding new statements (type for agent and profile, describes, etc.)
		List<Statement> toBeAddedStmts = new ArrayList<Statement>();
		List<Statement> toBeDeletedStmts = new ArrayList<Statement>();
		// get creator statements (statements saying some resources created by some agent)
		List<Statement> creatorStmts = new ArrayList<Statement>();
		Model model = new LinkedHashModel(relatedStmts);
		for (URI predicate:agentPredicates){
			Model filterModel = model.filter(null, predicate, null);
			creatorStmts.addAll(filterModel);
		}		
		// for each creator statement object (an agent)
		for (Statement crStmt:creatorStmts){						
			Value crObject = crStmt.getObject();				
			// if creator is URI
			if (crObject instanceof URI){
				URI crURI = (URI)crObject;	
				this.createAgentandProfileFromURI(crURI, toBeAddedStmts, 
						toBeDeletedStmts, newObjects, model, systemAgent, 
						profilemgr, identitymgr, ts);
			}
		    // else if creator is bnode
			else if (crObject instanceof BNode){				
				BNode crBnode = (BNode)crObject;
				this.createAgentandProfileFromBnode(crBnode, crStmt, 
						toBeAddedStmts, toBeDeletedStmts, newObjects, model, systemAgent, 
						profilemgr, identitymgr, ts);				
			}	
			else {
				Literal crLiteral = (Literal)crObject;
				this.createAgentandProfileFromLiteral(crLiteral, crStmt, toBeAddedStmts, 
						toBeDeletedStmts, newObjects, systemAgent, profilemgr, ts);						
			}
		}
		relatedStmts.removeAll(toBeDeletedStmts);
		relatedStmts.addAll(toBeAddedStmts);
		return newObjects;
	}				
	/**
	 * Create any required Agent and Profile objects for any creator URI in DiSCO related statements
	 * @param crURI
	 * @param toBeAddedStmts
	 * @param toBeDeletedStmts
	 * @param newObjects
	 * @param model  DiSCO related statements as Model
	 * @param systemAgent
	 * @param profilemgr
	 * @param identitymgr 
	 * @param ts
	 * @throws RMapException
	 */
	protected void createAgentandProfileFromURI(URI crURI, List<Statement> toBeAddedStmts, 
			List<Statement> toBeDeletedStmts,List<URI> newObjects, Model model, 
			URI systemAgent, ORMapProfileMgr profilemgr, ORMapIdentityMgr identitymgr, SesameTriplestore ts)
	throws RMapException {
		// get profile statements related to agent
		Model filterProfileModel = model.filter(crURI, null, null);
		if (this.isAgentId(crURI, ts)){
			this.createAgentandProfileFromAgentURI(crURI, toBeAddedStmts, toBeDeletedStmts,
					newObjects, filterProfileModel, systemAgent, profilemgr, identitymgr, ts);	
		}
		else if (this.isProfileId(crURI, ts)){
			this.createAgentandProfileFromProfileURI(crURI, toBeAddedStmts, toBeDeletedStmts,
					newObjects, filterProfileModel, systemAgent, profilemgr, identitymgr, ts);	
		}
		else if (profilemgr.isProfileIdentity(crURI,ts)){
			this.createAgentandProfileFromProfileIdentityURI(crURI, toBeAddedStmts, 
					toBeDeletedStmts, newObjects, filterProfileModel, systemAgent, 
					profilemgr, identitymgr, ts);			
		}
		else if (identitymgr.isLocalPartUri(crURI, ts)){
			this.createAgentandProfileFromIdLocalUri(crURI, toBeAddedStmts, toBeDeletedStmts,
					newObjects, filterProfileModel, systemAgent, profilemgr, identitymgr, ts);
		}
		else {	
			this.createAgentandProfileFromNewURI(crURI, toBeAddedStmts, toBeDeletedStmts, 
					newObjects, filterProfileModel, systemAgent, profilemgr, identitymgr, ts);			
		}
		return;
	}	
	
	protected void createAgentandProfileFromIdLocalUri(URI crURI,
			List<Statement> toBeAddedStmts, List<Statement> toBeDeletedStmts,
			List<URI> newObjects, Model filterProfileModel, URI systemAgent,
			ORMapProfileMgr profilemgr, ORMapIdentityMgr identitymgr,
			SesameTriplestore ts) {
		// get the agentId and agent
		URI agentUri = identitymgr.getParentAgentOfLocalPartUri(crURI, profilemgr, ts);
		if (agentUri==null){
			throw new RMapException("Parent agent of localPartURI " + crURI.stringValue() + " not found");
		}
		ORMapAgent agent = this.readAgent(agentUri, ts);
		// TODO if ID is an RMap preferred ID, and NOT same as agentId, do we need to create a new agent with preferred ID as ID;create sameAs relation????????? in what context???????? 
		toBeAddedStmts.addAll(this.makeAgentStatements(agent, ts));
		//create profile, crURI = parentAgent, systemAgent = creator
		if (!filterProfileModel.isEmpty()){
			ORMapProfile profile = profilemgr.createProfileFromRelatedStmts(crURI, filterProfileModel, 
					systemAgent, ts, crURI);
			// create any Identities needed
			this.createIdentities(profilemgr, filterProfileModel, identitymgr, profile, 
				systemAgent, newObjects, toBeAddedStmts, ts);
			// add identity for crURI (local part of an id)
			ORMapIdentity uriIdentity = identitymgr.getIdentityWithLocalPartUri(crURI, ts);
			profile.addIdentity(ORAdapter.uri2OpenRdfUri(uriIdentity.getId()));			
			newObjects.add(ORAdapter.uri2OpenRdfUri(profile.getId()));
			// add the new profile statements (NOT including context, which will need to be DiSCO context) to new related statements
			toBeAddedStmts.addAll(profilemgr.makeProfileStatments(profile, ts));
			toBeAddedStmts.addAll(this.getIdentitiesStmts(profile,identitymgr, ts));
			// add the old profile statements to list of statements to be deleted from related statements list
			toBeDeletedStmts.addAll(filterProfileModel);
		}
		return;
	}

	protected List<Statement> getIdentitiesStmts(ORMapProfile profile, ORMapIdentityMgr identityMgr, 
			SesameTriplestore ts) 
	throws RMapException {
		if (profile ==null){
			throw new RMapException ("null profile");
		}
		List<Statement>stmts = new ArrayList<Statement>();
		for (Statement idStmt: profile.getIdentityStmts()){
			URI idId = (URI)idStmt.getObject();
			ORMapIdentity identity = identityMgr.getIdentityWithLocalPartUri(idId, ts);
			if (identity != null){
				stmts.addAll(identity.getAsModel());
			}
		}
		return stmts;
	}

	/**
	 * 
	 * @param profilemgr
	 * @param filterProfileModel
	 * @param identitymgr
	 * @param profile
	 * @param systemAgent
	 * @param newObjects
	 * @param toBeAddedStmts
	 * @param ts
	 * @throws RMapException
	 */
	protected void createIdentities (ORMapProfileMgr profilemgr, Model filterProfileModel,
			ORMapIdentityMgr identitymgr, ORMapProfile profile, URI systemAgent,
			List<URI> newObjects, List<Statement> toBeAddedStmts, SesameTriplestore ts) 
	throws RMapException  {
		List<Statement>idStmts = profilemgr.getIdStmtsInRelatedStatments(filterProfileModel);
		if (idStmts.size()>0){							
			Map<ORMapIdentity,Boolean> identities = 
					identitymgr.createIdentitiesFromRelatedStmts(
							idStmts, systemAgent, ts);
			// add identities to Profile
			for (ORMapIdentity identity:identities.keySet()){
				identitymgr.addIdToProfile(identity,profile);
				Statement localStmt = identity.getLocalPartStmt();
				Value localId = localStmt.getObject();
				if (localId instanceof URI){
					URI localURI = (URI)localId;
					if (PreferredIdValidator.isPreferredAgentId(ORAdapter.openRdfUri2URI(localURI))){
						identitymgr.addPreferredIdToProfile(localURI, profile);
					}
				}
				// add (new) Identities to new objects, all identity statements to added statements
				boolean isNewId = identities.get(identity).booleanValue();
				if (isNewId){
					newObjects.add(ORAdapter.uri2OpenRdfUri(identity.getId()));
				}
				toBeAddedStmts.addAll(identitymgr.makeIdentityStatements(identity, ts));
			}
		}
	}
	/**
	 * Create any required Agent and Profile objects for any creator Agent URI in DiSCO related statements
	 * @param crURI
	 * @param toBeAddedStmts
	 * @param toBeDeletedStmts
	 * @param newObjects
	 * @param filterProfileModel
	 * @param systemAgent
	 * @param profilemgr
	 * @param identitymgr 
	 * @param ts
	 * @throws RMapException
	 */
	protected void createAgentandProfileFromAgentURI(URI crURI, List<Statement> toBeAddedStmts, 
			List<Statement> toBeDeletedStmts,List<URI> newObjects, Model filterProfileModel, 
			URI systemAgent, ORMapProfileMgr profilemgr, ORMapIdentityMgr identitymgr, SesameTriplestore ts)
	throws RMapException {
		// if agent exists with uri , keep original object creator statement,
		// add the Agent statements (NOT INCLUDING CONTEXT) to the new related statements
		ORMapAgent agent = this.readAgent(crURI, ts);
		toBeAddedStmts.addAll(this.makeAgentStatements(agent, ts));
		//create profile, crURI = parentAgent, systemAgent = creator
		if (!filterProfileModel.isEmpty()){
			ORMapProfile profile = profilemgr.createProfileFromRelatedStmts(crURI, filterProfileModel, 
					systemAgent, ts, crURI);
			// create any Identities needed
			this.createIdentities(profilemgr, filterProfileModel, identitymgr, profile, 
				systemAgent, newObjects, toBeAddedStmts, ts);
			newObjects.add(ORAdapter.uri2OpenRdfUri(profile.getId()));
			// add the new profile statements (NOT including context, which will need to be DiSCO context) to new related statements
			toBeAddedStmts.addAll(profilemgr.makeProfileStatments(profile, ts));	
			// add the old profile statements to list of statements to be deleted from related statements list
			toBeDeletedStmts.addAll(filterProfileModel);
		}
		return;
	}
	/**
	 * Create any required Agent and Profile objects for any creator Profile URI in DiSCO related statements
	 * @param crURI
	 * @param toBeAddedStmts
	 * @param toBeDeletedStmts
	 * @param newObjects
	 * @param filterProfileModel
	 * @param systemAgent
	 * @param profilemgr
	 * @param identitymgr 
	 * @param ts
	 * @throws RMapException
	 */
	protected void createAgentandProfileFromProfileURI(URI crURI, List<Statement> toBeAddedStmts, 
			List<Statement> toBeDeletedStmts,List<URI> newObjects, Model filterProfileModel, 
			URI systemAgent, ORMapProfileMgr profilemgr, ORMapIdentityMgr identitymgr, SesameTriplestore ts)
	throws RMapException {
		ORMapProfile profile = profilemgr.readProfile(crURI, ts);
		URI profileId = ORAdapter.uri2OpenRdfUri(profile.getId());
		URI parentURI = profilemgr.getParentAgentUri(profileId, ts);
		ORMapAgent agent = this.readAgent(parentURI, ts);
		toBeAddedStmts.addAll(this.makeAgentStatements(agent, ts));
		if (!filterProfileModel.isEmpty()) { 
			//  create new profile	
			profile = profilemgr.createProfileFromRelatedStmts(parentURI, filterProfileModel,
					systemAgent, ts, crURI);						
			// add the old profile statements to list of statements to be deleted from related statements list
			toBeDeletedStmts.addAll(filterProfileModel);
			newObjects.add(ORAdapter.uri2OpenRdfUri(profile.getId()));
			// create any Identities needed
			this.createIdentities(profilemgr, filterProfileModel, identitymgr, profile, 
				systemAgent, newObjects, toBeAddedStmts, ts);
		}
		// add profile (whether old or new) statements to DiSCO
		toBeAddedStmts.addAll(profilemgr.makeProfileStatments(profile, ts));
		return;
	}
	/**
	 * Create any required Agent and Profile objects for any creator Profile identity URI in DiSCO related statements
	 * @param crURI
	 * @param toBeAddedStmts
	 * @param toBeDeletedStmts
	 * @param newObjects
	 * @param filterProfileModel
	 * @param systemAgent
	 * @param profilemgr
	 * @param identitymgr 
	 * @param ts
	 * @throws RMapException
	 */
	protected void createAgentandProfileFromProfileIdentityURI(URI crURI, 
			List<Statement> toBeAddedStmts, List<Statement> toBeDeletedStmts,
			List<URI> newObjects, Model filterProfileModel, 
			URI systemAgent, ORMapProfileMgr profilemgr, ORMapIdentityMgr identitymgr, SesameTriplestore ts)
	throws RMapException {
		//	get parent agent,
		ORMapProfile idProfile = profilemgr.getProfileFromIdentity(crURI, ts);
		URI profileId = ORAdapter.uri2OpenRdfUri(idProfile.getId());
		URI agentUri = profilemgr.getParentAgentUri(profileId, ts);
		ORMapAgent agent = this.readAgent(agentUri, ts);
		toBeAddedStmts.addAll(this.makeAgentStatements(agent, ts));
		ORMapProfile profile = null;
		//   create new profile with parent agent, system-agent = creator, identity
		if (!filterProfileModel.isEmpty()){
			profile = profilemgr.createProfileFromRelatedStmts(agentUri, filterProfileModel, systemAgent, ts, crURI);						
			// add the old profile statements to list of statements to be deleted from related statements list
			toBeDeletedStmts.addAll(filterProfileModel);
			// create any Identities needed
			this.createIdentities(profilemgr, filterProfileModel, identitymgr, profile, 
				systemAgent, newObjects, toBeAddedStmts, ts);
		}
		else {
			profile = profilemgr.createProfileObject(agentUri, systemAgent, crURI);
		}
		List<Statement> idStmts = profile.getIdentityStmts();
		boolean foundCrURI = false;
		String crUriStr = crURI.stringValue();
		for (Statement stmt:idStmts){
			Value idValue = stmt.getObject();
			if (idValue instanceof URI){
				URI idURI = (URI)idValue;
				if (idURI.stringValue().equals(crUriStr)){
					foundCrURI = true;
					break;
				}
			}
		}
		if (!foundCrURI){
			profile.addIdentity(crURI);
			ORMapIdentity identity = identitymgr.read(crURI, ts);
			toBeAddedStmts.addAll(identity.getAsModel());
		}
		newObjects.add(ORAdapter.uri2OpenRdfUri(profile.getId()));
		// add the new profile statements (NOT including context, which will need to be DiSCO context) to new related statements
		toBeAddedStmts.addAll(profilemgr.makeProfileStatments(profile, ts));	
		return;
	}
	/**
	 * Create any required Agent and Profile objects for any previously unseen URI in DiSCO related statements
	 * @param crURI
	 * @param toBeAddedStmts
	 * @param toBeDeletedStmts
	 * @param newObjects
	 * @param filterProfileModel
	 * @param systemAgent
	 * @param profilemgr
	 * @param identitymgr 
	 * @param ts
	 * @throws RMapException
	 */
	protected void createAgentandProfileFromNewURI(URI crURI, List<Statement> toBeAddedStmts, 
			List<Statement> toBeDeletedStmts,List<URI> newObjects, Model filterProfileModel, 
			URI systemAgent, ORMapProfileMgr profilemgr, ORMapIdentityMgr identitymgr, SesameTriplestore ts)
	throws RMapException {
		// create new Agent and profile with new agent as parent and this uri as identity
		// create sameAs statement linking provided URI (If not "accepted URI") to profileID
		// add agent, profile, and identity to new objects list
		// keep original statement, add agent, profile, id statements, remove previous profile stmts
		ORMapAgent agent = null;
		// need to check first to see if there is an ORCID in related stmts
		// If there is, use it as agent ID
		URI preferredId = null;
		if (!filterProfileModel.isEmpty()){
			List<Statement>idStmts = profilemgr.getIdStmtsInRelatedStatments(filterProfileModel);
			if (idStmts.size()>0){
				preferredId = identitymgr.getPreferredIdInRelatedStatements(idStmts);
			}
		}
		boolean agentIsNew = false;
		if (preferredId==null){
			agent = new ORMapAgent(systemAgent);
			agentIsNew = true;
		}
		else {
			// make sure this agent doesn't already exist
			if (this.isAgentId(preferredId, ts)){
				agent = this.readAgent(preferredId, ts);
			}
			else {
				agent = new ORMapAgent(preferredId, systemAgent);
				agentIsNew = true;
			}
		}
		URI agentId = ORAdapter.uri2OpenRdfUri(agent.getId());
		if(agentIsNew){
			newObjects.add(agentId);
		}		
		toBeAddedStmts.addAll(this.makeAgentStatements(agent, ts));
		boolean isSameAgentId = crURI.equals(agentId);
		ORMapProfile profile = null;
		if (!filterProfileModel.isEmpty()){
			profile = profilemgr.createProfileFromRelatedStmts(agentId, filterProfileModel, 
					systemAgent, ts, crURI);						
			// add the old profile statements to list of statements to be deleted from related statements list
			toBeDeletedStmts.addAll(filterProfileModel);
			// create any Identities needed
			this.createIdentities(profilemgr, filterProfileModel, identitymgr, profile, 
				systemAgent, newObjects, toBeAddedStmts, ts);
		}
		else {
			profile = new ORMapProfile(agentId,systemAgent);
		}
		newObjects.add(ORAdapter.uri2OpenRdfUri(profile.getId()));		
		// add the new profile statements (NOT including context, which will need to be DiSCO context) to new related statements
		toBeAddedStmts.addAll(profilemgr.makeProfileStatments(profile, ts));	
		if (! isSameAgentId){
			Statement sameStmt;
			try {
				sameStmt = ts.getValueFactory().createStatement(
						crURI, OWL.SAMEAS, ORAdapter.uri2OpenRdfUri(profile.getId()));
				toBeAddedStmts.add(sameStmt);
			} catch (RepositoryException e) {
				e.printStackTrace();
				throw new RMapException (e);
			}						
		}
		return;
	}	
	/**
	 * Create any required Agent and Profile objects for any creator bnode in DiSCO related statements
	 * @param crBnode
	 * @param crStmt
	 * @param toBeAddedStmts
	 * @param toBeDeletedStmts
	 * @param newObjects
	 * @param model
	 * @param systemAgent
	 * @param profilemgr
	 * @param identitymgr 
	 * @param ts
	 * @throws RMapException
	 */
	protected void createAgentandProfileFromBnode(BNode crBnode, Statement crStmt,
			List<Statement> toBeAddedStmts, List<Statement> toBeDeletedStmts,
			List<URI> newObjects, Model model, URI systemAgent, ORMapProfileMgr profilemgr, 
			ORMapIdentityMgr identitymgr, SesameTriplestore ts)
	throws RMapException {
	    // create new agent, agent profile as with URI
		ORMapAgent agent = null;
		Model filterProfileModel = model.filter(crBnode, null, null);
		// need to check first to see if there is an ORCID in related stmts
		// If there is, use it as agent ID
		URI preferredId = null;
		if (!filterProfileModel.isEmpty()){
			List<Statement>idStmts = profilemgr.getIdStmtsInRelatedStatments(filterProfileModel);
			if (idStmts.size()>0){
				preferredId = identitymgr.getPreferredIdInRelatedStatements(idStmts);
			}
		}
		boolean agentIsNew = false;
		if (preferredId==null){
			agent = new ORMapAgent(systemAgent);
			agentIsNew = true;
		}
		else {
			// make sure this agent doesn't already exist
			if (this.isAgentId(preferredId, ts)){
				agent = this.readAgent(preferredId, ts);
			}
			else {
				agent = new ORMapAgent(preferredId, systemAgent);
				agentIsNew = true;
			}
		}
		URI agentId = ORAdapter.uri2OpenRdfUri(agent.getId());
		if(agentIsNew){
			newObjects.add(agentId);
		}
		Statement newCreateStmt= null;
		// replace bnode predicate in create statement with agentId
		try {
			newCreateStmt = ts.getValueFactory().createStatement(crStmt.getSubject(),
					crStmt.getPredicate(), agentId);
		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new RMapException(e);
		}
		toBeAddedStmts.add(newCreateStmt);
		toBeDeletedStmts.add(crStmt);				
		toBeAddedStmts.addAll(this.makeAgentStatements(agent, ts));
		ORMapProfile profile = null;
		
		if (!filterProfileModel.isEmpty()){
			profile = profilemgr.createProfileFromRelatedStmts(agentId, filterProfileModel, 
					systemAgent, ts, crBnode);						
			// add the old profile statements to list of statements to be deleted from related statements list
			toBeDeletedStmts.addAll(filterProfileModel);
			// create any Identities needed
			this.createIdentities(profilemgr, filterProfileModel, identitymgr, profile, 
					systemAgent, newObjects, toBeAddedStmts, ts);
		}
		else {
			// unlikely
			profile = new ORMapProfile(agentId,systemAgent);
		}					
		newObjects.add(ORAdapter.uri2OpenRdfUri(profile.getId()));
		// add the new profile statements (NOT including context, which will need to be DiSCO context) to new related statements
		toBeAddedStmts.addAll(profilemgr.makeProfileStatments(profile, ts));
		return;
	}
	/**
	 * Create any required Agent and Profile objects for any creator literal in DiSCO related statements
	 * @param crLiteral
	 * @param crStmt
	 * @param toBeAddedStmts
	 * @param toBeDeletedStmts
	 * @param newObjects
	 * @param systemAgent
	 * @param profilemgr
	 * @param ts
	 * @throws RMapException
	 */
	protected void createAgentandProfileFromLiteral(Literal crLiteral, Statement crStmt,
			List<Statement> toBeAddedStmts, List<Statement> toBeDeletedStmts,
			List<URI> newObjects, URI systemAgent, ORMapProfileMgr profilemgr, 
			SesameTriplestore ts)
	throws RMapException {
		// create new agent, agent profile
		// add ProfileID foaf:name value to profile properties
		// substitute ProfileID in create statement
	    // add agent, profile to createdObjectsList					
		ORMapAgent agent = new ORMapAgent(systemAgent);
		URI agentId = ORAdapter.uri2OpenRdfUri(agent.getId());
		newObjects.add(agentId);
		toBeAddedStmts.addAll(this.makeAgentStatements(agent, ts));
		ORMapProfile profile = new ORMapProfile(agentId,systemAgent);
		URI profileId = ORAdapter.uri2OpenRdfUri(profile.getId());
		Statement propStmt = null;
		try {
			propStmt = ts.getValueFactory().createStatement(profileId, 
					FOAF.NAME, crLiteral,profileId);
		} catch (RepositoryException e1) {
			e1.printStackTrace();
			throw new RMapException (e1);
		}
		profile.addPropertyStmt(propStmt);
		newObjects.add(profileId);
		// add the new profile statements (NOT including context, which will need to be DiSCO context) to new related statements
		toBeAddedStmts.addAll(profilemgr.makeProfileStatments(profile, ts));
		Statement newCreateStmt= null;
		try {
			newCreateStmt = ts.getValueFactory().createStatement(crStmt.getSubject(),
					crStmt.getPredicate(), profileId);
		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new RMapException(e);
		}
		toBeAddedStmts.add(newCreateStmt);
		toBeDeletedStmts.add(crStmt);
		return;
	}
	/**
	 * Create agent statements without context to be part of DiSCO (they will
	 * have DiSCO context within the DiSCO)
	 * @param agent
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	protected List<Statement> makeAgentStatements (ORMapAgent agent, SesameTriplestore ts) 
	throws RMapException{
		List<Statement> stmts = new ArrayList<Statement>();
		Model agentModel = agent.getAsModel();
		for (Statement mstmt:agentModel){
			Statement dmsmt = null;
			try {
				dmsmt = ts.getValueFactory().createStatement(mstmt.getSubject(), 
						mstmt.getPredicate(), mstmt.getObject());
				stmts.add(dmsmt);
			} catch (RepositoryException e) {
				e.printStackTrace();
				throw new RMapException (e);
			}			
		}
		return stmts;
	}

}

/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.rmapproject.core.controlledlist.AgentPredicate;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORMapProfile;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;

import org.openrdf.model.BNode;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.repository.RepositoryException;

/**
 * @author smorrissey
 *
 */
public class ORMapAgentMgr extends ORMapObjectMgr {

	static List<URI> agentPredicates = null;
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
	 * @throws RMapObjectNotFoundException
	 * @throws RMapException
	 */
	public ORMapAgent readAgent(URI agentId, SesameTriplestore ts)
	throws RMapObjectNotFoundException, RMapException {
		
		if (agentId == null){
			throw new RMapException("null agentId");
		}
		if (ts==null){
			throw new RMapException("null triplestore");
		}
		
		if (!(this.isAgentId(agentId, ts))){
			throw new RMapObjectNotFoundException("Not an agentID: " + agentId.stringValue());
		}

		List<Statement> agentStmts = this.getNamedGraph(agentId, ts);	
		ORMapAgent agent = new ORMapAgent(agentStmts, null);
		return agent;
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
	 * 
	 * @param relatedStmts
	 * @param systemAgent
	 * @param profilemgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public List<URI> createRelatedStatementsAgents (List<Statement> relatedStmts,
			URI systemAgent, ORMapProfileMgr profilemgr, SesameTriplestore ts)
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
		// get creator statements
		List<Statement> creatorStmts = new ArrayList<Statement>();
		Model model = new LinkedHashModel(relatedStmts);
		for (URI predicate:agentPredicates){
			Model filterModel = model.filter(null, predicate, null);
			creatorStmts.addAll(filterModel);
		}		
		// for each creator statement object (an agent)
		for (Statement crStmt:creatorStmts){						
			Value crObject = crStmt.getObject();				
			// if URI
			if (crObject instanceof URI){
				URI crURI = (URI)crObject;	
				// get profile statements related to agent
				Model filterProfileModel = model.filter(crURI, null, null);
				if (this.isAgentId(crURI, ts)){
					// if agent exists with uri , keep original stmt,
					// add the Agent statements (NOT INCLUDING CONTEXT) to the new related statements
					ORMapAgent agent = this.readAgent(crURI, ts);
					toBeAddedStmts.addAll(this.makeAgentStatements(agent, ts));
					//create profile, uri = parentAgent, systemAgent = creator
					if (!filterProfileModel.isEmpty()){
						ORMapProfile profile = profilemgr.createProfile(crURI, filterProfileModel, systemAgent, ts);
						newObjects.add(ORAdapter.uri2OpenRdfUri(profile.getId()));
						// add the new profile statements (NOT including context, which will need to be DiSCO context) to new related statements
						toBeAddedStmts.addAll(profilemgr.makeProfileStatments(profile, ts));	
						for (Statement idStmt:profile.getIdentityStmts()){
							//TODO create the Identity OJBECT
							URI id = (URI)idStmt.getObject();
							newObjects.add(id);
						}
						// add the old profile statements to list of statements to be deleted from related statements list
						toBeDeletedStmts.addAll(filterProfileModel);
					}
				}
				else if (this.isProfileId(crURI, ts)){
					ORMapProfile profile = profilemgr.readProfile(crURI, ts);
					URI profileId = ORAdapter.uri2OpenRdfUri(profile.getId());
					URI parentURI = profilemgr.getParentAgent(profileId, ts);
					ORMapAgent agent = this.readAgent(parentURI, ts);
					toBeAddedStmts.addAll(this.makeAgentStatements(agent, ts));
					if (!filterProfileModel.isEmpty()) { 
						//  create new profile	
						Model replacedIdModel = this.replaceIdInModel(filterProfileModel, crURI, ts);
						profile = profilemgr.createProfile(parentURI, replacedIdModel, systemAgent, ts);						
						// add the old profile statements to list of statements to be deleted from related statements list
						toBeDeletedStmts.addAll(filterProfileModel);
						newObjects.add(ORAdapter.uri2OpenRdfUri(profile.getId()));
					}
					toBeAddedStmts.addAll(profilemgr.makeProfileStatments(profile, ts));	
					for (Statement idStmt:profile.getIdentityStmts()){
						//TODO create the Identity OJBECT
						URI id = (URI)idStmt.getObject();
						newObjects.add(id);
					}
				}
				else if (profilemgr.isProfileIdentity(crURI,ts)){
					// else if agent profile(s) exists with URI as one of its IDs,
					// keep the original assertion in related statements
					//	get parent agent,
					ORMapProfile idProfile = profilemgr.getProfileFromIdentity(crURI, ts);
					URI profileId = ORAdapter.uri2OpenRdfUri(idProfile.getId());
					URI parentURI = profilemgr.getParentAgent(profileId, ts);
					ORMapAgent agent = this.readAgent(parentURI, ts);
					toBeAddedStmts.addAll(this.makeAgentStatements(agent, ts));
					ORMapProfile profile = null;
					//   create new profile with parent agent, system-agent = creator, identity
					if (!filterProfileModel.isEmpty()){
						Model replacedIdModel = this.replaceIdInModel(filterProfileModel, crURI, ts);
						profile = profilemgr.createProfile(parentURI, replacedIdModel, systemAgent, ts);						
						// add the old profile statements to list of statements to be deleted from related statements list
						toBeDeletedStmts.addAll(filterProfileModel);
					}
					else {
						profile = profilemgr.createProfile(crURI, parentURI, systemAgent, ts);
					}
					newObjects.add(ORAdapter.uri2OpenRdfUri(profile.getId()));
					// add the new profile statements (NOT including context, which will need to be DiSCO context) to new related statements
					toBeAddedStmts.addAll(profilemgr.makeProfileStatments(profile, ts));	
					for (Statement idStmt:profile.getIdentityStmts()){
						//TODO create the Identity OJBECT
						URI id = (URI)idStmt.getObject();
						newObjects.add(id);
					}
					try {
						Statement sameStmt = ts.getValueFactory().createStatement(crURI, OWL.SAMEAS, ORAdapter.uri2OpenRdfUri(profile.getId()));
						toBeAddedStmts.add(sameStmt);
					} catch (RepositoryException e) {
						e.printStackTrace();
						throw new RMapException (e);
					}
				}
				else {					
					// else create new Agent and profile with this parent agent and this uri as identity
					// create sameAs statement linking provided URI (If not "accepted URI) to profileID
					// add agent, profile, and identity to new objects list
					// keep original statement, add agent, profile, id statements, remove previous profile stmts
					ORMapAgent agent = new ORMapAgent(crURI, systemAgent);
					URI agentID = ORAdapter.uri2OpenRdfUri(agent.getId());
					newObjects.add(agentID);
					toBeAddedStmts.addAll(this.makeAgentStatements(agent, ts));
					boolean isSameAgentId = crURI.equals(agentID);
					ORMapProfile profile = null;
					if (!filterProfileModel.isEmpty()){
						Model replacedIdModel = this.replaceIdInModel(filterProfileModel, crURI, ts);
						profile = profilemgr.createProfile(agentID, replacedIdModel, systemAgent, ts);						
						// add the old profile statements to list of statements to be deleted from related statements list
						toBeDeletedStmts.addAll(filterProfileModel);
					}
					else {
						profile = new ORMapProfile(agentID,systemAgent);
					}					
					profile.addIdentity(crURI);
					newObjects.add(ORAdapter.uri2OpenRdfUri(profile.getId()));
					// add the new profile statements (NOT including context, which will need to be DiSCO context) to new related statements
					toBeAddedStmts.addAll(profilemgr.makeProfileStatments(profile, ts));	
					for (Statement idStmt:profile.getIdentityStmts()){
						//TODO create the Identity OJBECT
						URI id = (URI)idStmt.getObject();
						newObjects.add(id);
					}
					if (! isSameAgentId){
						Statement sameStmt;
						try {
							sameStmt = ts.getValueFactory().createStatement(crURI, OWL.SAMEAS, ORAdapter.uri2OpenRdfUri(profile.getId()));
							toBeAddedStmts.add(sameStmt);
						} catch (RepositoryException e) {
							e.printStackTrace();
							throw new RMapException (e);
						}						
					}
				}
			}
		// else if bnode
			else if (crObject instanceof BNode){
				BNode crBnode= (BNode)crObject;	
			    // create new agent, agent profile as above
				ORMapAgent agent = new ORMapAgent(systemAgent);
				URI agentId = ORAdapter.uri2OpenRdfUri(agent.getId());
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
				newObjects.add(agentId);
				toBeAddedStmts.addAll(this.makeAgentStatements(agent, ts));
				ORMapProfile profile = null;
				Model filterProfileModel = model.filter(crBnode, null, null);
				if (!filterProfileModel.isEmpty()){
					profile = profilemgr.createProfile(agentId, filterProfileModel, systemAgent, ts);						
					// add the old profile statements to list of statements to be deleted from related statements list
					toBeDeletedStmts.addAll(filterProfileModel);
				}
				else {
					// unlikely
					profile = new ORMapProfile(agentId,systemAgent);
				}					
				newObjects.add(ORAdapter.uri2OpenRdfUri(profile.getId()));
				// add the new profile statements (NOT including context, which will need to be DiSCO context) to new related statements
				toBeAddedStmts.addAll(profilemgr.makeProfileStatments(profile, ts));	
			}	
			else {
			// else if value
			// create new agent, agent profile as above; add ProfileID foaf:name value to profile properties
			// substitute ProfileID in create statement
		    // add agent, profile to createdObjectsList						 
				ORMapAgent agent = new ORMapAgent(systemAgent);
				URI agentId = ORAdapter.uri2OpenRdfUri(agent.getId());
				newObjects.add(agentId);
				toBeAddedStmts.addAll(this.makeAgentStatements(agent, ts));
				ORMapProfile profile = new ORMapProfile(agentId,systemAgent);
				newObjects.add(ORAdapter.uri2OpenRdfUri(profile.getId()));
				// add the new profile statements (NOT including context, which will need to be DiSCO context) to new related statements
				toBeAddedStmts.addAll(profilemgr.makeProfileStatments(profile, ts));
				URI profileId = ORAdapter.uri2OpenRdfUri(profile.getId());
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
			}
		}
		relatedStmts.removeAll(toBeDeletedStmts);
		relatedStmts.addAll(toBeAddedStmts);
		return newObjects;
	}
	
	protected List<Statement> makeAgentStatements (ORMapAgent agent, SesameTriplestore ts) throws RMapException{
		List<Statement> stmts = new ArrayList<Statement>();
		Model agentModel = agent.getAsModel();
		for (Statement mstmt:agentModel){
			Statement dmsmt = null;
			try {
				dmsmt = ts.getValueFactory().createStatement(mstmt.getSubject(), mstmt.getPredicate(), mstmt.getObject());
				stmts.add(dmsmt);
			} catch (RepositoryException e) {
				e.printStackTrace();
				throw new RMapException (e);
			}			
		}
		return stmts;
	}

	protected Model replaceIdInModel(Model filterProfileModel, URI crURI, SesameTriplestore ts) 
	throws RMapException {
		List<Statement> newStmts = new ArrayList<Statement>();
		BNode bnode = null;
		try {
			bnode = ts.getValueFactory().createBNode();
		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new RMapException (e);
		}
		for (Statement oldStmt:filterProfileModel){
			Resource subject = oldStmt.getSubject();
			if (subject.equals(crURI)){
				subject = bnode;
			}
			URI predicate = oldStmt.getPredicate();
			Value object = oldStmt.getObject();
			if (object.equals(crURI)){
				object = bnode;
			}
			Statement newStmt = null;
			try {
				newStmt = ts.getValueFactory().createStatement(subject, predicate, object);
			} catch (RepositoryException e) {
				e.printStackTrace();
				throw new RMapException (e);
			}
			newStmts.add(newStmt);
		}
		Model model = new LinkedHashModel(newStmts);
		return model;
	}

}

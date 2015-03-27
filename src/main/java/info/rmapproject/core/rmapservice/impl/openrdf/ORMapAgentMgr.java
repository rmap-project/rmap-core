/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;

/**
 * @author smorrissey
 *
 */
public class ORMapAgentMgr extends ORMapObjectMgr {

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
	
	public List<URI> createRelatedStatementsAgents (Map<Statement, Boolean> stmt2related,
			URI systemAgent, ORMapProfileMgr profilemgr, SesameTriplestore ts)
	throws RMapException {
		Model relStmtModel = new LinkedHashModel(stmt2related.keySet());
		// get creator statements
		// for each creator statement object (an agent)
		// if URI
		     // add to creatorURI list
			 // if agent exists with uri create profile, uri = parentAgent, systemAgent = creator
		     //  addIdentity URI
		     // add profile, to createdProfilesMap
		
			// else if agent profile(s) exists with ID, get parent agent, create new
		    // profile with parent agent, system-agent = creator, identity
		    // add profile to createdProfilesMap
	
			// else create new Agent with uri, and profile with this parent agent and this uri
			// as identity
		    // add agent to createdAgentsMap, profile to createdProfilesMap
		
		// else if bnode
		    // create new agent, agentprofile as above
		    // add bnode: agentId to bnode2agentId
		    //  add agent to createdAgentsList, profile to createdProfilesMap
		// else if value
			// create new agent, agent profile as above
		    // add agent, profile to createdObjectsList
		// for each URI in creatorURI list
		//TODO complete
		//TODO make sure any new reified statements created are in created object list
		return null;
	}
}

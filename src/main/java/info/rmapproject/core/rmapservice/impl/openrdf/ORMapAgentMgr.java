/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

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
	protected ORMapAgent createAgent(URI agentId, URI creator, SesameTriplestore ts) throws RMapException {
		ORMapAgent agent = new ORMapAgent(agentId, creator);		
		this.createAgentTriples(agent, ts);
		return agent;
	}
	
	public List<URI> createAgentAndProfiles(URI agentId, URI creator, 
			ORMapProfileMgr profilemgr, SesameTriplestore ts)
	throws RMapException {
		if (agentId == null){
			throw new RMapException ("Null agentID");
		}
		if (creator==null){
			throw new RMapException ("Null creator");
		}
		if (ts==null){
			throw new RMapException ("Null tripleStore");
		}
		List<URI> newObjects = new ArrayList<URI>();
		ORMapAgent agent = this.createAgent(agentId, creator, ts);
		URI agentUri = ORAdapter.uri2OpenRdfUri(agent.getId());
		newObjects.add(agentUri);
		// if agent ID not same as agentId, need to create Profile with agentID as providerID
		if (!(agentId.equals(agentUri))){
			URI profileUri = profilemgr.createSuppliedIdProfile(agentId, agentUri, 
					creator, ts);
			newObjects.add(profileUri);
		}
		
		return newObjects;
	}
}

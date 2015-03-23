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
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;

/**
 * @author smorrissey
 *
 */
public class ORMapAgentMgr extends ORMapObjectMgr {

	public static List<URI> agentRelations;

	static {
		agentRelations = new ArrayList<URI>();
		agentRelations.add(DC.CREATOR);
		agentRelations.add(DC.CONTRIBUTOR);
		agentRelations.add(DCTERMS.CONTRIBUTOR);
		agentRelations.add(DCTERMS.CREATOR);
		agentRelations.add(DCTERMS.AGENT);
		agentRelations.add(DCTERMS.PUBLISHER);
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
	
	public List<URI> createRelatedStatementsAgents (List<Statement> relatedStmts,
			URI systemAgent, ORMapProfileMgr profilemgr, SesameTriplestore ts)
	throws RMapException {
		//TODO complete
		return null;
	}
}

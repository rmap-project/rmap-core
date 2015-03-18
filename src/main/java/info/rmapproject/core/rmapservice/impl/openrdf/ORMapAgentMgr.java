/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

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
		ORMapAgent agent = new ORMapAgent(agentStmts);
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
		URI uri = null;
		if (agent == null){
			throw new RMapException ("null agent");
		}
		if (ts == null){
			throw new RMapException ("null triplestore");
		}
		Model model = agent.getAsModel();
		Iterator<Statement> iterator = model.iterator();
		while (iterator.hasNext()){
			Statement stmt = iterator.next();
			this.createTriple(ts, stmt);
		}
		uri = ORAdapter.rMapUri2OpenRdfUri(agent.getCreator());
		return uri;
	}
	/**
	 * 
	 * @param agentId
	 * @param ts
	 * @return
	 */
	public URI createAgent(URI agentId, SesameTriplestore ts){
		ORMapAgent agent = new ORMapAgent(agentId);		
		return this.createAgent(agent, ts);
	}
}

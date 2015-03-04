/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import java.util.List;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;

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
}

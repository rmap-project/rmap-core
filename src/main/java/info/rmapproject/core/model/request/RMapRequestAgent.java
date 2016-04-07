package info.rmapproject.core.model.request;

import info.rmapproject.core.exception.RMapException;

import java.net.URI;

/**
 * Used to manage requesting agent properties that are relevant to the request
 * for use when creating provenance information
 * @author khanson
 *
 */
public class RMapRequestAgent {

	URI systemAgent;
	URI agentKeyId = null;
	
	public RMapRequestAgent(URI systemAgent){
		if (systemAgent == null){
			throw new RMapException("Requesting System Agent cannot be null.");
		}
		this.systemAgent = systemAgent;
	}
	
	public RMapRequestAgent(URI systemAgent, URI agentKeyId){
		if (systemAgent == null){
			throw new RMapException("Requesting System Agent cannot be null.");
		}
		this.systemAgent = systemAgent;
		this.agentKeyId = agentKeyId; //null ok
	}
	
	public URI getSystemAgent() {
		return systemAgent;
	}
	
	public void setSystemAgent(URI systemAgent) {
		this.systemAgent = systemAgent;
	}
	
	public URI getAgentKeyId() {
		return agentKeyId;
	}
	
	public void setAgentKeyId(URI agentKeyId) {
		this.agentKeyId = agentKeyId;
	}
		
	
	
}

package info.rmapproject.core.model.request;

import info.rmapproject.core.exception.RMapException;

import java.net.URI;

/**
 * Used to manage requesting agent properties that are relevant to the request
 * for use when creating provenance information.
 *
 * @author khanson
 */
public class RMapRequestAgent {

	/** The system agent. */
	URI systemAgent;
	
	/** The agent key URI. */
	URI agentKeyId = null;
	
	/**
	 * Instantiates a new RMap request agent.
	 *
	 * @param systemAgent the system agent URI
	 */
	public RMapRequestAgent(URI systemAgent){
		if (systemAgent == null){
			throw new RMapException("Requesting System Agent cannot be null.");
		}
		this.systemAgent = systemAgent;
	}
	
	/**
	 * Instantiates a new RMap request agent.
	 *
	 * @param systemAgent the system agent URI
	 * @param agentKeyId the agent key URI - null if none specified
	 */
	public RMapRequestAgent(URI systemAgent, URI agentKeyId){
		if (systemAgent == null){
			throw new RMapException("Requesting System Agent cannot be null.");
		}
		this.systemAgent = systemAgent;
		this.agentKeyId = agentKeyId; //null ok
	}
	
	/**
	 * Gets the system agent URI
	 *
	 * @return the system agent URI
	 */
	public URI getSystemAgent() {
		return systemAgent;
	}
	
	/**
	 * Sets the system agent URI
	 *
	 * @param systemAgent the new system agent URI
	 */
	public void setSystemAgent(URI systemAgent) {
		this.systemAgent = systemAgent;
	}
	
	/**
	 * Gets the agent key URI
	 *
	 * @return the agent key URI
	 */
	public URI getAgentKeyId() {
		return agentKeyId;
	}
	
	/**
	 * Sets the agent key URI
	 *
	 * @param agentKeyId the new agent key URI
	 */
	public void setAgentKeyId(URI agentKeyId) {
		this.agentKeyId = agentKeyId;
	}
		
	
	
}

package info.rmapproject.core.rmapservice.impl.openrdf;

import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;

import org.junit.Before;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

public class ORMapMgrTest {

	protected URI AGENT_URI;
	protected URI IDPROVIDER_URI;
	protected URI AUTH_ID;
	protected Value NAME;
	protected RMapService rmapService;
	protected java.net.URI agentId; //used to pass back into rmapService since all of these use java.net.URI
	
	
	public ORMapMgrTest() {
		super();
	}

	@Before
	public void setUp() throws Exception {
		this.rmapService=RMapServiceFactoryIOC.getFactory().createService();
		this.agentId = createTestAgent();
	}


	protected java.net.URI createTestAgent() {
		AGENT_URI = ORAdapter.getValueFactory().createURI("ark:/22573/rmaptestagent");
		IDPROVIDER_URI = ORAdapter.getValueFactory().createURI("http://orcid.org/");
		AUTH_ID = ORAdapter.getValueFactory().createURI("http://rmap-project.org/identities/rmaptestauthid");
		NAME = ORAdapter.getValueFactory().createLiteral("RMap test Agent");
	
		java.net.URI agentUri = null ;
		
		//create through ORMapAgentMgr
		try {
			RMapAgent agent = new ORMapAgent(AGENT_URI, IDPROVIDER_URI, AUTH_ID, NAME);
			@SuppressWarnings("unused")
			RMapEvent event = rmapService.createAgent(agent.getId().getIri(), agent);
			agentUri=agent.getId().getIri();
			
			if (rmapService.isAgentId(agentUri)){
				System.out.println("Test Agent successfully created!  URI is ark:/29297/rmaptestagent");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return agentUri;
	}

}
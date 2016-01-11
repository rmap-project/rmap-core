/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * @author smorrissey
 *
 */
public class ORMapAgentMgrTest{

	private URI AGENT_URI = null; 
	private URI ID_PROVIDER_URI = null;
	private URI AUTH_ID_URI = null;
	private Value NAME = null;
	
	@Before
	public void setUp() throws Exception {
		//these will be used for a test agent.
		this.AGENT_URI = ORAdapter.getValueFactory().createURI("ark:/22573/rmaptestagent");
		this.ID_PROVIDER_URI = ORAdapter.getValueFactory().createURI("http://orcid.org/");
		this.AUTH_ID_URI = ORAdapter.getValueFactory().createURI("http://rmap-project.org/identities/rmaptestauthid");
		this.NAME = ORAdapter.getValueFactory().createLiteral("RMap test Agent");		
	}

	
	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#readAgent(org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapException 
	 * @throws RMapAgentNotFoundException 
	 */
	@Test
	public void testReadAgent() throws RMapAgentNotFoundException, RMapException, RMapDefectiveArgumentException {
				
		RMapService rmapService;
		rmapService=RMapServiceFactoryIOC.getFactory().createService();
		
		java.net.URI agentId; //used to pass back into rmapService since all of these use java.net.URI
		
		try {
			//create new test agent
			RMapAgent agent = new ORMapAgent(AGENT_URI, ID_PROVIDER_URI, AUTH_ID_URI, NAME);
			rmapService.createAgent(agent,agent.getId().getIri());
			agentId=agent.getId().getIri();
			if (rmapService.isAgentId(agentId)){
				System.out.println("Test Agent successfully created!  URI is " + agentId);
			}
			
			// Check the agent was created
			assertTrue(rmapService.isAgentId(agentId));	
			
			//now read agent and check it.
			RMapAgent readagent = rmapService.readAgent(agentId);

			String name1=readagent.getName().getStringValue();
			String name2=NAME.stringValue();
			assertEquals(name1, name2);
			assertEquals(readagent.getType().getStringValue(), RMAP.AGENT.stringValue());
			assertEquals(readagent.getIdProvider().getStringValue(),ID_PROVIDER_URI.stringValue());
			assertEquals(readagent.getAuthId().getStringValue(),AUTH_ID_URI.stringValue());
		}
		catch (RMapAgentNotFoundException e){
			fail("agent not found");
		}
		catch (RMapException e){
			fail("exception");
		}
		finally {
			rmapService.closeConnection();
		}
	}
	

}

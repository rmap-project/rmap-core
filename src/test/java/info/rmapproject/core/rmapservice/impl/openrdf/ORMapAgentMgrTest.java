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
import info.rmapproject.core.model.RMapObjectType;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.request.RMapRequestAgent;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author smorrissey
 *
 */

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration({ "classpath*:/spring-rmapcore-testcontext.xml" })
public class ORMapAgentMgrTest{

	@Autowired
	RMapService rmapService;
	
	@Autowired
	SesameTriplestore triplestore;
	
	ORAdapter typeAdapter;
	
	private IRI AGENT_IRI = null; 
	private IRI ID_PROVIDER_IRI = null;
	private IRI AUTH_ID_IRI = null;
	private Value NAME = null;
	private RMapRequestAgent requestAgent = null;
	
	@Before
	public void setUp() throws Exception {
		typeAdapter = new ORAdapter(triplestore);
		//these will be used for a test agent.
		this.AGENT_IRI = typeAdapter.getValueFactory().createIRI("ark:/22573/rmaptestagent");
		this.ID_PROVIDER_IRI = typeAdapter.getValueFactory().createIRI("http://orcid.org/");
		this.AUTH_ID_IRI = typeAdapter.getValueFactory().createIRI("http://rmap-project.org/identities/rmaptestauthid");
		this.NAME = typeAdapter.getValueFactory().createLiteral("RMap test Agent");		
		requestAgent = new RMapRequestAgent(new URI(AGENT_IRI.stringValue()));
	}

	
	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#readAgent(org.openrdf.model.IRI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapException 
	 * @throws RMapAgentNotFoundException 
	 */
	@Test
	public void testReadAgent() throws RMapAgentNotFoundException, RMapException, RMapDefectiveArgumentException {
						
		java.net.URI agentId; //used to pass back into rmapService since all of these use java.net.URI
		
		try {
			//create new test agent
			RMapAgent agent = new ORMapAgent(AGENT_IRI, ID_PROVIDER_IRI, AUTH_ID_IRI, NAME);
			agentId=agent.getId().getIri();
			
			if (!rmapService.isAgentId(agentId)) {
				rmapService.createAgent(agent,requestAgent);
			}
			
			if (rmapService.isAgentId(agentId)){
				System.out.println("Test Agent successfully created!  URI is " + agentId.toString());
			}
			
			// Check the agent was created
			assertTrue(rmapService.isAgentId(agentId));	
			
			//now read agent and check it.
			RMapAgent readagent = rmapService.readAgent(agentId);

			String name1=readagent.getName().getStringValue();
			String name2=NAME.stringValue();
			assertEquals(name1, name2);
			
			assertEquals(readagent.getType(), RMapObjectType.AGENT);
			
			assertEquals(readagent.getIdProvider().toString(),ID_PROVIDER_IRI.toString());
			assertEquals(readagent.getAuthId().toString(),AUTH_ID_IRI.toString());
		}
		catch (RMapAgentNotFoundException e){
			fail("agent not found. " + e.getMessage());
		}
		catch (RMapException e){
			fail("exception" + e.getMessage());
		}
		finally {
			rmapService.closeConnection();
		}
	}
	
	

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#updateAgent(org.openrdf.model.IRI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapException 
	 * @throws RMapAgentNotFoundException 
	 */
	@SuppressWarnings("unused")
	@Test
	public void testUpdateAgent() throws RMapAgentNotFoundException, RMapException, RMapDefectiveArgumentException {
				
		java.net.URI agentId; //used to pass back into rmapService since all of these use java.net.URI
		
		try {
			//create new test agent
			RMapAgent agent = new ORMapAgent(AGENT_IRI, ID_PROVIDER_IRI, AUTH_ID_IRI, NAME);
			agentId=agent.getId().getIri();
			if (!rmapService.isAgentId(agentId)) {
				rmapService.createAgent(agent,requestAgent);
			}
			if (rmapService.isAgentId(agentId)){
				System.out.println("Test Agent successfully created!  URI is " + agentId);
			}	
			
			// Check the agent was created
			assertTrue(rmapService.isAgentId(agentId));	
			//(IRI agentID, String name, IRI identityProvider, IRI authKeyIri, IRI creatingAgentID)
			RMapEvent event = rmapService.updateAgent(agentId, 
										"RMap Test Name Change", 
										new java.net.URI(ID_PROVIDER_IRI.toString()), 
										new java.net.URI(AUTH_ID_IRI.toString()), 
										requestAgent);
			
			assertTrue(event.getAssociatedAgent().toString().equals(agentId.toString()));
			assertTrue(event.getDescription().toString().contains("foaf:name"));

			//now read agent and check it was updated.
			RMapAgent readagent = rmapService.readAgent(agentId);
			String name1=readagent.getName().getStringValue();
			assertEquals(name1, "RMap Test Name Change");

			RMapEvent event2 = rmapService.updateAgent(agentId, 
										NAME.stringValue(), 
										new java.net.URI(ID_PROVIDER_IRI.toString()), 
										new java.net.URI(AUTH_ID_IRI.toString()), 
										requestAgent);
			
			//now read agent and check it was updated.
			readagent = rmapService.readAgent(agentId);
			name1=readagent.getName().getStringValue();
			assertEquals(name1, NAME.stringValue());
			
			
		}
		catch (RMapAgentNotFoundException e){
			fail("agent not found");
		}
		catch (RMapException e){
			fail("exception");
		}
		catch (URISyntaxException e){
			fail("agent not found");
		}
		finally {
			rmapService.closeConnection();
		}
		
		
	}
	

}

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
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import org.junit.Test;

/**
 * @author smorrissey
 *
 */
public class ORMapAgentMgrTest extends ORMapMgrTest {

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#readAgent(org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapException 
	 * @throws RMapAgentNotFoundException 
	 */
	@Test
	public void testReadAgent() throws RMapAgentNotFoundException, RMapException, RMapDefectiveArgumentException {
		// Check the agent was created
		assertTrue(rmapService.isAgentId(agentId));	
		RMapAgent agent = rmapService.readAgent(agentId);
		
		try {
			String name1=agent.getName().getStringValue();
			String name2=NAME.stringValue();
			assertEquals(name1, name2);
			assertEquals(agent.getType().getStringValue(), RMAP.AGENT.stringValue());
			assertEquals(agent.getIdProvider().getStringValue(),IDPROVIDER_URI.stringValue());
			assertEquals(agent.getAuthId().getStringValue(),AUTH_ID.stringValue());
		}
		catch (RMapDefectiveArgumentException e){
			fail("an invalid argument was passed");
		}
		catch (RMapAgentNotFoundException e){
			fail("agent not found");
		}
		catch (RMapException e){
			fail("exception");
		}
	}
	
	


}

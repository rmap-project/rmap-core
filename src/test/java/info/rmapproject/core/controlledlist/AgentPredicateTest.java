/**
 * 
 */
package info.rmapproject.core.controlledlist;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.openrdf.model.IRI;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author smorrissey
 *
 */

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration({ "classpath*:/spring-rmapcore-testcontext.xml" })
public class AgentPredicateTest {

	protected  List<IRI> agentRelations;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		agentRelations = new ArrayList<IRI>();
		agentRelations.add(DC.CREATOR);
		agentRelations.add(DC.CONTRIBUTOR);
		agentRelations.add(DCTERMS.CONTRIBUTOR);
		agentRelations.add(DCTERMS.CREATOR);
		agentRelations.add(DCTERMS.AGENT);
		agentRelations.add(DCTERMS.PUBLISHER);
		agentRelations.add(DCTERMS.AGENT);
	}

//	/**
//	 * Test method for {@link info.rmapproject.core.controlledlist.AgentPredicate#isAgentPredicate(java.net.IRI)}.
//	 */
//	@Test
//	public void testIsAgentPredicate() {
//		//TODO: Not currently working because not used or configured - may use code for identifying types later, 
//		//for now leave this here.
//		for (IRI agent:agentRelations){
//			java.net.URI uri = ORAdapter.openRdfIri2URI(agent);
//			assertTrue(AgentPredicate.isAgentPredicate(uri));
//		}
//	}

}

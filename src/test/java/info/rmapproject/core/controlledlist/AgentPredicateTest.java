/**
 * 
 */
package info.rmapproject.core.controlledlist;

import static org.junit.Assert.*;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.IRI;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private SesameTriplestore triplestore;

	private ORAdapter typeAdapter;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.typeAdapter = new ORAdapter(this.triplestore);
		agentRelations = new ArrayList<IRI>();
		agentRelations.add(DC.CREATOR);
		agentRelations.add(DC.CONTRIBUTOR);
		agentRelations.add(DCTERMS.CONTRIBUTOR);
		agentRelations.add(DCTERMS.CREATOR);
		agentRelations.add(DCTERMS.AGENT);
		agentRelations.add(DCTERMS.PUBLISHER);
		agentRelations.add(DCTERMS.AGENT);
	}

	/**
	 * Test method for {@link info.rmapproject.core.controlledlist.AgentPredicate#isAgentPredicate(java.net.IRI)}.
	 */
	@Test
	public void testIsAgentPredicate() {
		//TODO: Not currently working because not used or configured - may use code for identifying types later, 
		//for now leave this here.
		for (IRI agent:agentRelations){
			java.net.URI uri = typeAdapter.openRdfIri2URI(agent);
			assertTrue(AgentPredicate.isAgentPredicate(uri));
		}
	}

}

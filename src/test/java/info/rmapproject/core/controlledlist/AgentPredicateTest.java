/**
 * 
 */
package info.rmapproject.core.controlledlist;

import static org.junit.Assert.*;

import info.rmapproject.core.model.impl.openrdf.ORAdapter;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;

/**
 * @author smorrissey
 *
 */
public class AgentPredicateTest {

	protected  List<URI> agentRelations;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		agentRelations = new ArrayList<URI>();
		agentRelations.add(DC.CREATOR);
		agentRelations.add(DC.CONTRIBUTOR);
		agentRelations.add(DCTERMS.CONTRIBUTOR);
		agentRelations.add(DCTERMS.CREATOR);
		agentRelations.add(DCTERMS.AGENT);
		agentRelations.add(DCTERMS.PUBLISHER);
		agentRelations.add(DCTERMS.AGENT);
	}

	/**
	 * Test method for {@link info.rmapproject.core.controlledlist.AgentPredicate#isAgentPredicate(java.net.URI)}.
	 */
	@Test
	public void testIsAgentPredicate() {
		for (URI agent:agentRelations){
			java.net.URI uri = ORAdapter.openRdfUri2URI(agent);
			assertTrue(AgentPredicate.isAgentPredicate(uri));
		}
	}

}

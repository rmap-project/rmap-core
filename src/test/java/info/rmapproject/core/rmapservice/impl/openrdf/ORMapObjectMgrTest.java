/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.model.impl.openrdf.ORMapEventCreation;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
//import org.openrdf.model.Model;
//import org.openrdf.model.vocabulary.DCTERMS;

/**
 * @author smorrissey
 *
 */
public class ORMapObjectMgrTest {

	protected SesameTriplestore ts = null;
	ValueFactory vf = null;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		try {
			ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			vf = ts.getValueFactory();
		} catch (Exception e) {
			throw new RMapException("Unable to create Sesame TripleStore: ", e);
		}
	}


	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapObjectMgr#isEventId(org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testIsEventId() {
		List<java.net.URI> resourceList = new ArrayList<java.net.URI>();
		try {
		    URI creatorUri = vf.createURI("http://orcid.org/0000-0003-2069-1219");
			resourceList.add(new java.net.URI("http://rmap-info.org"));
			resourceList.add(new java.net.URI
					("https://rmap-project.atlassian.net/wiki/display/RMAPPS/RMap+Wiki"));
			RMapUri associatedAgent = ORAdapter.openRdfUri2RMapUri(creatorUri);
			ORMapDiSCO disco = new ORMapDiSCO(associatedAgent, resourceList);
			// Make list of created objects
			List<URI> uris = new ArrayList<URI>();
			URI discoContext = disco.getDiscoContext();
			uris.add(discoContext);
			List<RMapUri> createdObjIds = new ArrayList<RMapUri>();
			for (URI uri:uris){
				createdObjIds.add(ORAdapter.openRdfUri2RMapUri(uri));
			}
			ORMapEventCreation event = new ORMapEventCreation(associatedAgent, 
					RMapEventTargetType.DISCO, null, createdObjIds);
			Date end = new Date();
			event.setEndTime(end);
			ORMapEventMgr eventMgr = new ORMapEventMgr();
			URI crEventId = eventMgr.createEvent(event, ts);
			assertTrue(eventMgr.isEventId(crEventId, ts));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapObjectMgr#isAgentId(org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testIsAgentId() {
		java.net.URI SYSAGENT_URI = null;
		try {
			SYSAGENT_URI = new java.net.URI("http://orcid.org/0000-0003-2069-1219");
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail("cant create URI");
		}
		//yep, agent creates itself… just for now.
		URI agentURI = ORAdapter.uri2OpenRdfUri(SYSAGENT_URI);
		
		//create through ORMapAgentMgr
		SesameTriplestore ts = null;
		try {
			ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			ORMapAgent agent = new ORMapAgent(agentURI, agentURI);
			ORMapAgentMgr agentMgr = new ORMapAgentMgr();
			agentMgr.createAgentTriples (agent, ts);
			try {
				ts.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				fail("cant commit");
			}
			assertTrue(agentMgr.isAgentId(agent.getContext(), ts));
		} catch (Exception e) {
			e.printStackTrace();
			fail("cant create triplestore");
		}		
	}

}

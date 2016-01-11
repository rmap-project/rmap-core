/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.model.impl.openrdf.ORMapEventCreation;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
//import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
//import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;

/**
 * @author smorrissey
 *
 */
public class ORMapObjectMgrTest {

	private URI AGENT_URI = null; 
	private URI ID_PROVIDER_URI = null;
	private URI AUTH_ID_URI = null;
	private Value NAME = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		//these will be used for a test agent.
		this.AGENT_URI = ORAdapter.getValueFactory().createURI("ark:/22573/rmaptestagent");
		this.ID_PROVIDER_URI = ORAdapter.getValueFactory().createURI("http://orcid.org/");
		this.AUTH_ID_URI = ORAdapter.getValueFactory().createURI("http://rmap-project.org/identities/rmaptestauthid");
		this.NAME = ORAdapter.getValueFactory().createLiteral("RMap test Agent");	
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapObjectMgr#createTriple(info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore, org.openrdf.model.Statement)}.
	 */
	@Test
	public void testCreateTriple() {
		java.net.URI id1 =null;
		SesameTriplestore ts = null;
		try {
			ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			id1 = IdServiceFactoryIOC.getFactory().createService().createId();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		URI subject = ORAdapter.uri2OpenRdfUri(id1);
		URI predicate = RDF.TYPE;
		URI object = RMAP.DISCO;
//		ORMapStatementMgr mgr = new ORMapStatementMgr();
//		String contextString = mgr.createContextURIString(subject.stringValue(),
//				predicate.stringValue(), object.stringValue());
		URI context = subject;

		try {
			Statement stmt = ts.getValueFactory().createStatement(subject, predicate, object,context);
			ORMapDiSCOMgr mgr = new ORMapDiSCOMgr();
			mgr.createTriple(ts, stmt);
			Statement gStmt = null;
			gStmt = ts.getStatement(subject, predicate, object, context);
			assertNotNull(gStmt);
			assertEquals(subject, gStmt.getSubject());
			assertEquals(predicate, gStmt.getPredicate());
			assertEquals(object, gStmt.getObject());
			assertEquals(context, gStmt.getContext());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapObjectMgr#isRMapType(info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore, org.openrdf.model.URI, org.openrdf.model.URI)}.
	 */
	@Test
	public void testIsRMapType() {
		ORMapDiSCOMgr mgr = new ORMapDiSCOMgr();
		java.net.URI id1 =null;
		SesameTriplestore ts = null;
		try {
			ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			id1 = IdServiceFactoryIOC.getFactory().createService().createId();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		URI subject = ORAdapter.uri2OpenRdfUri(id1);
		URI predicate = RDF.TYPE;
		Value object = RMAP.DISCO;
		Statement stmt = null;
		try {
			stmt = ts.getValueFactory().createStatement(subject, predicate, object);
			ts.addStatement(stmt);
			Statement stmt2 = ts.getStatement(subject, predicate, object);
			assertNotNull(stmt2);
			assertEquals(stmt.getSubject(),stmt2.getSubject());
			assertEquals(stmt.getPredicate(), stmt2.getPredicate());
			assertEquals(stmt.getObject(), stmt2.getObject());
			assertEquals(stmt.getContext(), stmt2.getContext());
		} catch (RepositoryException e1) {
			e1.printStackTrace();
			fail(e1.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
		boolean istype = mgr.isRMapType(ts, subject, RMAP.DISCO);
		assertTrue(istype);
	}


	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapObjectMgr#isEventId(org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapException 
	 */
	@Test
	public void testIsEventId() throws RMapException, RMapDefectiveArgumentException {
		List<java.net.URI> resourceList = new ArrayList<java.net.URI>();
		try {
			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
		    URI creatorUri = ts.getValueFactory().createURI("http://orcid.org/0000-0003-2069-1219");
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
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapObjectMgr#isAgentId(org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testIsAgentId() throws URISyntaxException {
		try {
			RMapService rmapService=RMapServiceFactoryIOC.getFactory().createService();
			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			RMapAgent agent = new ORMapAgent(AGENT_URI, ID_PROVIDER_URI, AUTH_ID_URI, NAME);
			rmapService.createAgent(agent, agent.getId().getIri());
			RMapUri agentId=agent.getId();
			ORMapAgentMgr agentMgr = new ORMapAgentMgr();
			assertTrue(agentMgr.isAgentId(ORAdapter.rMapUri2OpenRdfUri(agentId), ts));
			rmapService.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
			fail("could not create test agent.");
		}		
		
	}

}

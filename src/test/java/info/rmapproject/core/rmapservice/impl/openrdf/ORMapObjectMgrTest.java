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
import info.rmapproject.core.idservice.IdService;
import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.model.impl.openrdf.ORMapEventCreation;
import info.rmapproject.core.model.request.RMapRequestAgent;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.vocabulary.impl.openrdf.RMAP;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.IRI;
//import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
//import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author smorrissey, khanson
 *
 */

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration({ "classpath*:/spring-rmapcore-testcontext.xml" })
public class ORMapObjectMgrTest {

	@Autowired
	private RMapService rmapService;
	
	@Autowired 
	private IdService rmapIdService;

	@Autowired 
	private SesameTriplestore triplestore;
	
	@Autowired 
	ORMapDiSCOMgr discomgr;
	
	private ORAdapter typeAdapter;
	
	private IRI AGENT_IRI = null; 
	private IRI ID_PROVIDER_IRI = null;
	private IRI AUTH_ID_IRI = null;
	private Value NAME = null;
	

	private RMapRequestAgent requestAgent = null;
	
	/**
	 * @throws java.lang.Exception
	 */
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
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapObjectMgr#createTriple(info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore, org.openrdf.model.Statement)}.
	 */
	@Test
	public void testCreateTriple() {
		java.net.URI id1 =null;
		try {
			id1 = rmapIdService.createId();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		IRI subject = typeAdapter.uri2OpenRdfIri(id1);
		IRI predicate = RDF.TYPE;
		IRI object = RMAP.DISCO;
//		ORMapStatementMgr mgr = new ORMapStatementMgr();
//		String contextString = mgr.createContextIRIString(subject.stringValue(),
//				predicate.stringValue(), object.stringValue());
		IRI context = subject;

		try {
			Statement stmt = triplestore.getValueFactory().createStatement(subject, predicate, object,context);
			discomgr.createTriple(triplestore, stmt);
			Statement gStmt = null;
			gStmt = triplestore.getStatement(subject, predicate, object, context);
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
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapObjectMgr#isRMapType(info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore, org.openrdf.model.IRI, org.openrdf.model.IRI)}.
	 */
	@Test
	public void testIsRMapType() {
		java.net.URI id1 =null;
		try {
			id1 = rmapIdService.createId();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		IRI subject = typeAdapter.uri2OpenRdfIri(id1);
		IRI predicate = RDF.TYPE;
		Value object = RMAP.DISCO;
		Statement stmt = null;
		try {
			stmt = triplestore.getValueFactory().createStatement(subject, predicate, object);
			triplestore.addStatement(stmt);
			Statement stmt2 = triplestore.getStatement(subject, predicate, object);
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
		boolean istype = discomgr.isRMapType(triplestore, subject, RMAP.DISCO);
		assertTrue(istype);
	}


	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapObjectMgr#isEventId(org.openrdf.model.IRI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapException 
	 */
	@Test
	public void testIsEventId() throws RMapException, RMapDefectiveArgumentException {
		List<java.net.URI> resourceList = new ArrayList<java.net.URI>();
		try {
		    IRI creatorIRI = triplestore.getValueFactory().createIRI("http://orcid.org/0000-0003-2069-1219");
			resourceList.add(new java.net.URI("http://rmap-info.org"));
			resourceList.add(new java.net.URI
					("https://rmap-project.atlassian.net/wiki/display/RMAPPS/RMap+Wiki"));
			RMapIri associatedAgent = typeAdapter.openRdfIri2RMapIri(creatorIRI);
			ORMapDiSCO disco = new ORMapDiSCO(associatedAgent, resourceList);
			// Make list of created objects
			List<IRI> iris = new ArrayList<IRI>();
			IRI discoContext = disco.getDiscoContext();
			iris.add(discoContext);
			List<RMapIri> createdObjIds = new ArrayList<RMapIri>();
			for (IRI iri:iris){
				createdObjIds.add(typeAdapter.openRdfIri2RMapIri(iri));
			}
			
			requestAgent.setAgentKeyId(new java.net.URI("ark:/29297/testkey"));
			ORMapEventCreation event = new ORMapEventCreation(requestAgent, RMapEventTargetType.DISCO, null, createdObjIds);
			Date end = new Date();
			event.setEndTime(end);
			ORMapEventMgr eventMgr = new ORMapEventMgr();
			IRI crEventId = eventMgr.createEvent(event, triplestore);
			assertTrue(eventMgr.isEventId(crEventId, triplestore));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapObjectMgr#isAgentId(org.openrdf.model.IRI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testIsAgentId() throws URISyntaxException {
		try {
			RMapAgent agent = new ORMapAgent(AGENT_IRI, ID_PROVIDER_IRI, AUTH_ID_IRI, NAME);
			java.net.URI agentId=agent.getId().getIri();
			if (!rmapService.isAgentId(agentId)) {
				rmapService.createAgent(agent,requestAgent);
			}
			if (rmapService.isAgentId(agentId)){
				System.out.println("Test Agent successfully created!  URI is " + agentId);
			}	
			ORMapAgentMgr agentMgr = new ORMapAgentMgr();
			assertTrue(agentMgr.isAgentId(typeAdapter.uri2OpenRdfIri(agentId), triplestore));
			rmapService.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
			fail("could not create test agent.");
		}		
		
	}

}

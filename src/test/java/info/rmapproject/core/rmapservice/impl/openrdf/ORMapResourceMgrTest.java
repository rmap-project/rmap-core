package info.rmapproject.core.rmapservice.impl.openrdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.model.impl.openrdf.ORMapEvent;
import info.rmapproject.core.rdfhandler.impl.openrdf.RioRDFHandler;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;

public class ORMapResourceMgrTest {

	protected String discoRDF = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "  
			+ "<rdf:RDF "  
			+ " xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""  
			+ " xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\""  
			+ " xmlns:rmap=\"http://rmap-project.org/rmap/terms/\""  		
			+ " xmlns:ore=\"http://www.openarchives.org/ore/terms/\""
			+ " xmlns:dcterms=\"http://purl.org/dc/terms/\""  
			+ " xmlns:dc=\"http://purl.org/dc/elements/1.1/\""  
			+ " xmlns:foaf=\"http://xmlns.com/foaf/0.1/\""  
			+ " xmlns:fabio=\"http://purl.org/spar/fabio/\">"  
			+ "<rmap:DiSCO>"  
			+ "<dcterms:creator rdf:resource=\"http://orcid.org/0000-0000-0000-0000\"/>"
			+ "<dc:description>"  
			+ "This is an example DiSCO aggregating different file formats for an article on IEEE Xplore as well as multimedia content related to the article."  
			+ "</dc:description>"  
			+ "<ore:aggregates rdf:resource=\"http://dx.doi.org/10.1109/ACCESS.2014.2332453\"/>"  
			+ "<ore:aggregates rdf:resource=\"http://ieeexplore.ieee.org/ielx7/6287639/6705689/6842585/html/mm/6842585-mm.zip\"/>"  
	    	+ "</rmap:DiSCO>"  
	    	+ "<fabio:JournalArticle rdf:about=\"http://dx.doi.org/10.1109/ACCESS.2014.2332453\">"  
	    	+ "<dc:title>Toward Scalable Systems for Big Data Analytics: A Technology Tutorial</dc:title>"  
	    	+ "<dc:creator>Yonggang Wen</dc:creator>"  
	    	+ "<dc:creator>Tat-Seng Chua</dc:creator>"  
	    	+ "<dc:creator>Xuelong Li</dc:creator>"  
	    	+ "<dc:subject>Hadoop</dc:subject>"  
	    	+ "<dc:subject>Big data analytics</dc:subject>"  
	    	+ "<dc:subject>data acquisition</dc:subject>"  
	    	+ "</fabio:JournalArticle>"  
	    	+ "<rdf:Description rdf:about=\"http://ieeexplore.ieee.org/ielx7/6287639/6705689/6842585/html/mm/6842585-mm.zip\">"  
	    	+ "<dc:format>application/zip</dc:format>"  
	    	+ "<dc:description>Zip file containing an AVI movie and a README file in Word format.</dc:description>"  
	    	+ "<dc:hasPart rdf:resource=\"http://ieeexplore.ieee.org/ielx7/6287639/6705689/6842585/html/mm/6842585-mm.zip#big%32data%32intro.avi\"/>"  
	    	+ "<dc:hasPart rdf:resource=\"http://ieeexplore.ieee.org/ielx7/6287639/6705689/6842585/html/mm/6842585-mm.zip#README.docx\"/>"  
	    	+ "</rdf:Description>"  
	    	+ "<rdf:Description rdf:about=\"http://ieeexplore.ieee.org/ielx7/6287639/6705689/6842585/html/mm/6842585-mm.zip#big%32data%32intro.avi\">"  
	    	+ "<dc:format>video/x-msvideo</dc:format>"  
	    	+ "<dc:extent>194KB</dc:extent>"  
	    	+ "</rdf:Description>"  
	    	+ "</rdf:RDF>";
		
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
	
		
	@SuppressWarnings("unused")
	@Test
	public void testGetRelatedDiSCOS() {	
		RMapService rmapService=RMapServiceFactoryIOC.getFactory().createService();
		ORMapResourceMgr resourceMgr = new ORMapResourceMgr();
		ORMapDiSCOMgr discoMgr = new ORMapDiSCOMgr();
		java.net.URI agentId; 
		
		try {

			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			//create new test agent
			RMapAgent agent = new ORMapAgent(AGENT_URI, ID_PROVIDER_URI, AUTH_ID_URI, NAME);
			rmapService.createAgent(agent,agent.getId().getIri());
			agentId=agent.getId().getIri();
			if (rmapService.isAgentId(agentId)){
				System.out.println("Test Agent successfully created!  URI is " + agentId);
			}	
		
			//create disco				
			InputStream stream = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
			RioRDFHandler handler = new RioRDFHandler();	
			List<Statement>stmts = handler.convertRDFToStmtList(
					stream, "http://rmapdns.ddns.net:8080/api/disco/", "RDFXML");
			ORMapDiSCO disco = new ORMapDiSCO(stmts);
			ORMapEvent event = discoMgr.createDiSCO(ORAdapter.uri2OpenRdfUri(agentId), disco, ts);
		
			//get related discos
			URI uri = ts.getValueFactory().createURI("http://dx.doi.org/10.1109/ACCESS.2014.2332453");
			List <URI> sysAgents = new ArrayList<URI>();
			sysAgents.add(ORAdapter.uri2OpenRdfUri(agentId));
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date dateFrom = dateFormat.parse("2014-1-1");
			Date dateTo = dateFormat.parse("2050-1-1");
		
			Set <URI> discoUris = resourceMgr.getRelatedDiSCOS(uri, null, sysAgents, dateFrom, dateTo, ts);
			
			assertTrue(discoUris.size()==1);
			
			Iterator<URI> iter = discoUris.iterator();
			URI matchingUri = iter.next();
			assertTrue(matchingUri.toString().equals(disco.getId().toString()));

			discoMgr.updateDiSCO(ORAdapter.uri2OpenRdfUri(agentId), true, matchingUri, null, ts);
			discoUris = resourceMgr.getRelatedDiSCOS(uri, RMapStatus.ACTIVE, sysAgents, dateFrom, dateTo, ts);
			assertTrue(discoUris.size()==0);

			discoUris = resourceMgr.getRelatedDiSCOS(uri, RMapStatus.INACTIVE, sysAgents, dateFrom, dateTo, ts);
			assertTrue(discoUris.size()==1);
			rmapService.deleteDiSCO(disco.getId().getIri(), agentId);
		
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		finally {
			rmapService.closeConnection();
		}
	}
	

	@SuppressWarnings("unused")
	@Test
	public void testGetRelatedAgents() {	

		RMapService rmapService=RMapServiceFactoryIOC.getFactory().createService();
		ORMapResourceMgr resourceMgr = new ORMapResourceMgr();
		ORMapDiSCOMgr discoMgr = new ORMapDiSCOMgr();
		java.net.URI agentId; 
		
		try {

			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			
			//create new test agent
			RMapAgent agent = new ORMapAgent(AGENT_URI, ID_PROVIDER_URI, AUTH_ID_URI, NAME);
			rmapService.createAgent(agent, agent.getId().getIri());
			agentId=agent.getId().getIri();
			if (rmapService.isAgentId(agentId)){
				System.out.println("Test Agent successfully created!  URI is " + agentId);
			}	
		
			//create disco				
			InputStream stream = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
			RioRDFHandler handler = new RioRDFHandler();	
			List<Statement>stmts = handler.convertRDFToStmtList(
					stream, "http://rmapdns.ddns.net:8080/api/disco/", "RDFXML");
			ORMapDiSCO disco = new ORMapDiSCO(stmts);
			ORMapEvent event = discoMgr.createDiSCO(ORAdapter.uri2OpenRdfUri(agentId), disco, ts);
		
			//get related agents
			List <URI> sysAgents = new ArrayList<URI>();
			sysAgents.add(ORAdapter.uri2OpenRdfUri(agentId));
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date dateFrom = dateFormat.parse("2014-1-1");
			Date dateTo = dateFormat.parse("2050-1-1");
		
			Set <URI> agentUris = resourceMgr.getResourceAssertingAgents(ORAdapter.uri2OpenRdfUri(agentId), RMapStatus.ACTIVE, sysAgents, dateFrom, dateTo, ts);
			
			assertTrue(agentUris.size()==1);
			
			Iterator<URI> iter = agentUris.iterator();
			URI matchingUri = iter.next();
			assertTrue(matchingUri.toString().equals(agentId.toString()));
			rmapService.deleteDiSCO(disco.getId().getIri(), agentId);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			rmapService.closeConnection();
		}
		
	}
	

	@Test
	public void testGetRelatedEvents() {	

		RMapService rmapService=RMapServiceFactoryIOC.getFactory().createService();
		ORMapResourceMgr resourceMgr = new ORMapResourceMgr();
		ORMapDiSCOMgr discoMgr = new ORMapDiSCOMgr();
		
		try {

			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			
			//create new test agent
			RMapAgent agent = new ORMapAgent(AGENT_URI, ID_PROVIDER_URI, AUTH_ID_URI, NAME);
			rmapService.createAgent(agent, agent.getId().getIri());
			java.net.URI agentId=agent.getId().getIri();
			if (rmapService.isAgentId(agentId)){
				System.out.println("Test Agent successfully created!  URI is " + agentId);
			}	
		
			//create disco				
			InputStream stream = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
			RioRDFHandler handler = new RioRDFHandler();	
			List<Statement>stmts = handler.convertRDFToStmtList(
					stream, "http://rmapdns.ddns.net:8080/api/disco/", "RDFXML");
			ORMapDiSCO disco = new ORMapDiSCO(stmts);
			ORMapEvent event = discoMgr.createDiSCO(ORAdapter.uri2OpenRdfUri(agentId), disco, ts);
			
			//get related events
			RMapUri eventId = event.getId();
			URI discoId = ORAdapter.rMapUri2OpenRdfUri(disco.getId());
			RMapEvent updateEvent = discoMgr.updateDiSCO(ORAdapter.uri2OpenRdfUri(agentId), true, discoId, disco, ts);
			URI updateEventId = ORAdapter.rMapUri2OpenRdfUri(updateEvent.getId());
			
			URI uri = ts.getValueFactory().createURI("http://dx.doi.org/10.1109/ACCESS.2014.2332453");
			List <URI> sysAgents = new ArrayList<URI>();
			sysAgents.add(ORAdapter.uri2OpenRdfUri(agentId));
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date dateFrom = dateFormat.parse("2014-1-1");
			Date dateTo = dateFormat.parse("2050-1-1");
		
			Set <URI> eventUris = resourceMgr.getRelatedEvents(uri, sysAgents, dateFrom, dateTo, ts);
			
			assertTrue(eventUris.size()==2);

			Set<String> sEventUris = new HashSet<String>();
			for (URI eventUri : eventUris){
				sEventUris.add(eventUri.toString());
			}
			
			assertTrue(sEventUris.contains(eventId.toString()));
			assertTrue(sEventUris.contains(updateEventId.toString()));
			rmapService.deleteDiSCO(disco.getId().getIri(), agentId);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			rmapService.closeConnection();
		}
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testGetRelatedTriples() {	

		RMapService rmapService=RMapServiceFactoryIOC.getFactory().createService();
		ORMapResourceMgr resourceMgr = new ORMapResourceMgr();
		ORMapDiSCOMgr discoMgr = new ORMapDiSCOMgr();
		
		try {

			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			
			//create new test agent
			RMapAgent agent = new ORMapAgent(AGENT_URI, ID_PROVIDER_URI, AUTH_ID_URI, NAME);
			rmapService.createAgent(agent, agent.getId().getIri());
			java.net.URI agentId=agent.getId().getIri();
			if (rmapService.isAgentId(agentId)){
				System.out.println("Test Agent successfully created!  URI is " + agentId);
			}	
		
			//create disco				
			InputStream stream = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
			RioRDFHandler handler = new RioRDFHandler();	
			List<Statement>stmts = handler.convertRDFToStmtList(
					stream, "http://rmapdns.ddns.net:8080/api/disco/", "RDFXML");
			ORMapDiSCO disco = new ORMapDiSCO(stmts);
			ORMapEvent event = discoMgr.createDiSCO(ORAdapter.uri2OpenRdfUri(agentId), disco, ts);
		
			//get related triples			
			URI uri = ts.getValueFactory().createURI("http://ieeexplore.ieee.org/ielx7/6287639/6705689/6842585/html/mm/6842585-mm.zip");
			List <URI> sysAgents = new ArrayList<URI>();
			sysAgents.add(ORAdapter.uri2OpenRdfUri(agentId));
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date dateFrom = dateFormat.parse("2014-1-1");
			Date dateTo = dateFormat.parse("2050-1-1");
		
			Set <Statement> matchingStmts = resourceMgr.getRelatedTriples(uri, null, sysAgents, dateFrom, dateTo, ts);
			
			//should return 3 results - agent creator stmt, agent isFormatOf stmt, and disco creator stmt.
			assertTrue(matchingStmts.size()==5);

			Iterator<Statement> iter = matchingStmts.iterator();
			Statement stmt = iter.next();
			assertTrue(stmt.getSubject().toString().equals(uri.toString()) || stmt.getObject().toString().equals(uri.toString()));
			stmt = iter.next();
			assertTrue(stmt.getSubject().toString().equals(uri.toString()) || stmt.getObject().toString().equals(uri.toString()));
			stmt = iter.next();
			assertTrue(stmt.getSubject().toString().equals(uri.toString()) || stmt.getObject().toString().equals(uri.toString()));
			stmt = iter.next();
			assertTrue(stmt.getSubject().toString().equals(uri.toString()) || stmt.getObject().toString().equals(uri.toString()));
			rmapService.deleteDiSCO(disco.getId().getIri(), agentId);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			rmapService.closeConnection();
		}
		
	}
	
	
	@Test
	public void testGetResourceRdfTypes() {	
		ORMapResourceMgr resourceMgr = new ORMapResourceMgr();
		try {		

			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			java.net.URI context = IdServiceFactoryIOC.getFactory().createService().createId();
			URI resource01 = ORAdapter.uri2OpenRdfUri(context);
			context = IdServiceFactoryIOC.getFactory().createService().createId();
			URI resource02 = ts.getValueFactory().createURI("http://dx.doi.org/10.1109/ACCESS.2014.2332453/ORMapResourceMgrTest");
			
			Statement s1 = ts.getValueFactory().createStatement(resource01, RDF.TYPE, RMAP.DISCO, resource01);
			ts.addStatement(s1);
			Set<URI> uris = resourceMgr.getResourceRdfTypes(resource01, resource01, ts);
			assertNotNull(uris);
			assertEquals(1,uris.size());
			for (URI uri:uris){
				assertEquals(uri.stringValue(), RMAP.DISCO.stringValue());
			}
			uris = resourceMgr.getResourceRdfTypes(resource01, resource02, ts);
			assertNull(uris);
			try {
				uris = resourceMgr.getResourceRdfTypes(resource01, null, ts);
				fail("should have thrownRMapDefectiveArgumentException ");
			}
			catch (RMapDefectiveArgumentException ex) {}
			catch (Exception ex1){
				throw ex1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
	}

	@Test
	public void testGetResourceRdfTypesAllContexts() {
		ORMapResourceMgr resourceMgr = new ORMapResourceMgr();
		URI predJA = null;
		URI predA = null;
		try {	

			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			java.net.URI context = IdServiceFactoryIOC.getFactory().createService().createId();
			URI resource01 = ORAdapter.uri2OpenRdfUri(context);
			context = IdServiceFactoryIOC.getFactory().createService().createId();
			URI resource02 = ts.getValueFactory().createURI("http://dx.doi.org/10.1109/ACCESS.2014.2332453/ORMapResourceMgrTest");
			
			predJA = ts.getValueFactory().createURI("http://purl.org/spar/fabio/JournalArticle");
			predA = ts.getValueFactory().createURI("http://purl.org/spar/fabio/Article");
			Statement s1 = ts.getValueFactory().createStatement(resource02, RDF.TYPE, predJA, resource01);
			ts.addStatement(s1);
			Statement s2 = ts.getValueFactory().createStatement(resource02, RDF.TYPE, predA, resource02);			
			ts.addStatement(s2);
			Statement s3 = ts.getValueFactory().createStatement(resource02, RDF.TYPE, predJA, resource02);
			ts.addStatement(s3);
			Map<URI, Set<URI>> map = resourceMgr.getResourceRdfTypesAllContexts(resource02, ts);
			assertNotNull(map);
			assertEquals(2,map.keySet().size());
			assertTrue(map.containsKey(resource01));
			assertTrue(map.containsKey(resource02));
			Set<URI> values = map.get(resource01);
			assertEquals(1, values.size());
			assertTrue(values.contains(predJA));
			assertFalse(values.contains(predA));
			values = map.get(resource02);
			assertEquals(2, values.size());
			assertTrue(values.contains(predJA));
			assertTrue(values.contains(predA));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}		
	}

	
	

}

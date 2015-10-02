package info.rmapproject.core.rmapservice.impl.openrdf;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.model.impl.openrdf.ORMapEvent;
import info.rmapproject.core.rdfhandler.impl.openrdf.RioRDFHandler;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

public class ORMapStatementMgrTest {
	
	protected SesameTriplestore ts = null;
	protected ORMapAgentMgr agentMgr;
	protected ORMapDiSCOMgr discoMgr;
	protected ORMapEventMgr eventMgr;
	protected ORMapStatementMgr stmtMgr;
	protected URI systemAgentURI;
	protected String systemAgentId = "http://orcid.org/0000-0000-0000-0000";
	protected URI newAgentURI;
	protected String newAgentId = "http://orcid.org/0000-0000-0000-1234";

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
			+ "<dcterms:creator rdf:resource=\"http://orcid.org/0000-0000-0000-1234\"/>"
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
	@Before
	public void setUp() throws Exception {
		ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
		agentMgr = new ORMapAgentMgr();
		discoMgr = new ORMapDiSCOMgr();
		eventMgr = new ORMapEventMgr();
		stmtMgr = new ORMapStatementMgr();
		systemAgentURI = ts.getValueFactory().createURI(systemAgentId);
		newAgentURI = ts.getValueFactory().createURI(newAgentId);
	}

	@Test
	public void testGetRelatedDiSCOs() {

		try {			
			URI agentId = this.createSystemAgent(systemAgentURI, systemAgentURI);
			java.net.URI discoId = this.createDiSCO(agentId);
			List <URI> sysAgents = new ArrayList<URI>();
			sysAgents.add(agentId);
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date dateFrom = dateFormat.parse("2014-1-1");
			Date dateTo = dateFormat.parse("2050-1-1");
			URI subject = ts.getValueFactory().createURI("http://dx.doi.org/10.1109/ACCESS.2014.2332453");
			URI predicate = ts.getValueFactory().createURI("http://purl.org/dc/elements/1.1/subject");
			Value object = ts.getValueFactory().createLiteral("Hadoop");
		
			List <URI> discoIds = stmtMgr.getRelatedDiSCOs(subject, predicate, object, RMapStatus.ACTIVE, sysAgents, dateFrom, dateTo, ts);
			assertTrue(discoIds.size()==1);
			Iterator<URI> iter = discoIds.iterator();
			URI matchingDiscoId = iter.next();
			assertTrue(matchingDiscoId.toString().equals(discoId.toString()));

			discoMgr.updateDiSCO(agentId, true, matchingDiscoId, null, eventMgr, ts);
			discoIds = stmtMgr.getRelatedDiSCOs(subject, predicate, object, RMapStatus.ACTIVE, sysAgents, dateFrom, dateTo, ts);
			assertTrue(discoIds.size()==0);
			
			discoIds = stmtMgr.getRelatedDiSCOs(subject, predicate, object, RMapStatus.INACTIVE, sysAgents, dateFrom, dateTo, ts);
			assertTrue(discoIds.size()==1);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
	}

	@Test
	public void testGetRelatedAgents() {

		try {			
			//create system agent
			URI sysAgentId = this.createSystemAgent(systemAgentURI, systemAgentURI);
			List <URI> sysAgents = new ArrayList<URI>();
			sysAgents.add(sysAgentId);
			
			//create another agent using first system agent
			ORMapAgent agent2 = agentMgr.createAgentObject(newAgentURI, sysAgentId, ts);
			URI agentId2 = agent2.getContext();
			assertTrue(agentMgr.isAgentId(agentId2, ts));	
			RMapService service = RMapServiceFactoryIOC.getFactory().createService();
			try {
				service.createAgent(ORAdapter.openRdfUri2URI(sysAgentId), agent2);
			} catch (RMapException | RMapDefectiveArgumentException e1) {
				e1.printStackTrace();
				fail();
			}
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date dateFrom = dateFormat.parse("2014-1-1");
			Date dateTo = dateFormat.parse("2050-1-1");
			URI subject = ts.getValueFactory().createURI(agentId2.toString());
			URI predicate = ts.getValueFactory().createURI("http://purl.org/dc/terms/isFormatOf");
			Value object = ts.getValueFactory().createURI("http://orcid.org/0000-0000-0000-1234");
		
			List <URI> agentIds = stmtMgr.getRelatedAgents(subject, predicate, object, sysAgents, dateFrom, dateTo, ts);
			
			assertTrue(agentIds.size()==1);

			Iterator<URI> iter = agentIds.iterator();
			URI matchingAgentId = iter.next();
			assertTrue(matchingAgentId.toString().equals(agentId2.toString()));

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetAssertingAgents() {

		try {			
			URI agentId = this.createSystemAgent(systemAgentURI, systemAgentURI);
			@SuppressWarnings("unused")
			java.net.URI discoId = this.createDiSCO(agentId);
			List <URI> sysAgents = new ArrayList<URI>();
			sysAgents.add(agentId);
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date dateFrom = dateFormat.parse("2014-1-1");
			Date dateTo = dateFormat.parse("2050-1-1");
			URI subject = ts.getValueFactory().createURI("http://dx.doi.org/10.1109/ACCESS.2014.2332453");
			URI predicate = ts.getValueFactory().createURI("http://purl.org/dc/elements/1.1/subject");
			Value object = ts.getValueFactory().createLiteral("Hadoop");
		
			Set <URI> agentIds = stmtMgr.getAssertingAgents(subject, predicate, object, null, dateFrom, dateTo, ts);
			
			assertTrue(agentIds.size()==1);

			Iterator<URI> iter = agentIds.iterator();
			URI matchingAgentId = iter.next();
			assertTrue(matchingAgentId.toString().equals(agentId.toString()));

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
	private URI createSystemAgent(URI targetId, URI systemAgentId) {
		ORMapAgent agent = agentMgr.createAgentObject(targetId, systemAgentId, ts);
		URI agentId = agent.getContext();
		assertTrue(agentMgr.isAgentId(agentId, ts));	
		agentId = ORAdapter.uri2OpenRdfUri(agent.getId());
		assertTrue(agentMgr.isAgentId(agentId, ts));
		RMapService service = RMapServiceFactoryIOC.getFactory().createService();
		try {
			service.createAgent(ORAdapter.openRdfUri2URI(agentId), agent);
		} catch (RMapException | RMapDefectiveArgumentException e1) {
			e1.printStackTrace();
			fail();
		}
		return agentId;
	}
	
	private java.net.URI createDiSCO(URI agentId){
		InputStream stream = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
		RioRDFHandler handler = new RioRDFHandler();	
		List<Statement>stmts = handler.convertRDFToStmtList(
				stream, "http://rmapdns.ddns.net:8080/api/disco/", "RDFXML");
		ORMapDiSCO disco = new ORMapDiSCO(stmts);
		java.net.URI discoId = disco.getId();
		@SuppressWarnings("unused")
		ORMapEvent event = discoMgr.createDiSCO(agentId, disco, eventMgr, ts);
		return discoId;
	}
}

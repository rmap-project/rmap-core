package info.rmapproject.core.rmapservice.impl.openrdf;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.agent.RMapAgent;
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

	
	
	@Test
	public void testGetRelatedDiSCOs() {

		RMapService rmapService=RMapServiceFactoryIOC.getFactory().createService();
		ORMapDiSCOMgr discoMgr = new ORMapDiSCOMgr();
		
		try {

			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			
			//create new test agent
			RMapAgent agent = new ORMapAgent(AGENT_URI, ID_PROVIDER_URI, AUTH_ID_URI, NAME);
			rmapService.createAgent(agent.getId().getIri(), agent);
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
			discoMgr.createDiSCO(ORAdapter.uri2OpenRdfUri(agentId), disco, ts);
			RMapUri discoId = disco.getId();
			
			//get related discos
			List <URI> sysAgents = new ArrayList<URI>();
			sysAgents.add(ORAdapter.uri2OpenRdfUri(agentId));
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date dateFrom = dateFormat.parse("2014-1-1");
			Date dateTo = dateFormat.parse("2050-1-1");
			URI subject = ts.getValueFactory().createURI("http://dx.doi.org/10.1109/ACCESS.2014.2332453");
			URI predicate = ts.getValueFactory().createURI("http://purl.org/dc/elements/1.1/subject");
			Value object = ts.getValueFactory().createLiteral("Hadoop");
			ORMapStatementMgr stmtMgr = new ORMapStatementMgr();
			List <URI> discoIds = stmtMgr.getRelatedDiSCOs(subject, predicate, object, RMapStatus.ACTIVE, sysAgents, dateFrom, dateTo, ts);
			assertTrue(discoIds.size()==1);
			Iterator<URI> iter = discoIds.iterator();
			URI matchingDiscoId = iter.next();
			assertTrue(matchingDiscoId.toString().equals(discoId.toString()));
			
			discoMgr.updateDiSCO(ORAdapter.uri2OpenRdfUri(agentId), true, matchingDiscoId, null, ts);
			discoIds = stmtMgr.getRelatedDiSCOs(subject, predicate, object, RMapStatus.ACTIVE, sysAgents, dateFrom, dateTo, ts);
			assertTrue(discoIds.size()==0);
			
			discoIds = stmtMgr.getRelatedDiSCOs(subject, predicate, object, RMapStatus.INACTIVE, sysAgents, dateFrom, dateTo, ts);
			assertTrue(discoIds.size()==1);
			rmapService.deleteDiSCO(disco.getId().getIri(), agentId);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
	}

	@SuppressWarnings("unused")
	@Test
	public void testGetAssertingAgents() {


		RMapService rmapService=RMapServiceFactoryIOC.getFactory().createService();
		ORMapDiSCOMgr discoMgr = new ORMapDiSCOMgr();
		
		try {

			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			
			//create new test agent
			RMapAgent agent = new ORMapAgent(AGENT_URI, ID_PROVIDER_URI, AUTH_ID_URI, NAME);
			rmapService.createAgent(agent.getId().getIri(), agent);
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
			
			List <URI> sysAgents = new ArrayList<URI>();
			sysAgents.add(ORAdapter.uri2OpenRdfUri(agentId));
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date dateFrom = dateFormat.parse("2014-1-1");
			Date dateTo = dateFormat.parse("2050-1-1");
			URI subject = ts.getValueFactory().createURI("http://dx.doi.org/10.1109/ACCESS.2014.2332453");
			URI predicate = ts.getValueFactory().createURI("http://purl.org/dc/elements/1.1/subject");
			Value object = ts.getValueFactory().createLiteral("Hadoop");
			
			ORMapStatementMgr stmtMgr = new ORMapStatementMgr();
			Set <URI> agentIds = stmtMgr.getAssertingAgents(subject, predicate, object, null, dateFrom, dateTo, ts);
			
			assertTrue(agentIds.size()==1);

			Iterator<URI> iter = agentIds.iterator();
			URI matchingAgentId = iter.next();
			assertTrue(matchingAgentId.toString().equals(agentId.toString()));
			rmapService.deleteDiSCO(disco.getId().getIri(), agentId);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}

package info.rmapproject.core.rmapservice.impl.openrdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.idservice.IdServiceFactoryIOC;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;

public class ORMapResourceMgrTest {
	protected SesameTriplestore ts = null;
	protected URI typePred = null;
	protected URI context01 = null;
	protected URI context02 = null;
	protected URI resource01 = null;
	protected URI resource02 = null;
	protected ORMapResourceMgr resourceMgr;
	protected ORMapAgentMgr agentMgr;
	protected URI systemAgentURI;
	protected String systemAgentId = "http://orcid.org/0000-0003-2069-1219";

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
		
	@Before
	public void setUp() throws Exception {
		ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
		typePred = RDF.TYPE;	
		resourceMgr = new ORMapResourceMgr();
		agentMgr = new ORMapAgentMgr();
		systemAgentURI = ts.getValueFactory().createURI(systemAgentId);
		try {
			java.net.URI context = IdServiceFactoryIOC.getFactory().createService().createId();
			context01 = ORAdapter.uri2OpenRdfUri(context);
			resource01 = ORAdapter.uri2OpenRdfUri(context);
			context = IdServiceFactoryIOC.getFactory().createService().createId();
			context02 = ORAdapter.uri2OpenRdfUri(context);
			resource02 = ts.getValueFactory().createURI("http://dx.doi.org/10.1109/ACCESS.2014.2332453/ORMapResourceMgrTest");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	

	
	
	@Test
	public void testGetRelatedDiSCOS() {	
		try {			
			// Prime the pump by creating a system agent
			ORMapAgent systemAgent = agentMgr.createAgentObject(systemAgentURI, systemAgentURI, ts);
			URI agentId = systemAgent.getContext();
			assertTrue(agentMgr.isAgentId(agentId, ts));	
			agentId = ORAdapter.uri2OpenRdfUri(systemAgent.getId());
			assertTrue(agentMgr.isAgentId(agentId, ts));
			RMapService service = RMapServiceFactoryIOC.getFactory().createService();
			try {
				service.createAgent(ORAdapter.openRdfUri2URI(agentId), systemAgent);
			} catch (RMapException | RMapDefectiveArgumentException e1) {
				e1.printStackTrace();
				fail();
			}
			InputStream stream = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
			RioRDFHandler handler = new RioRDFHandler();	
			List<Statement>stmts = handler.convertRDFToStmtList(
					stream, "http://rmapdns.ddns.net:8080/api/disco/", "RDFXML");
			ORMapDiSCO disco = new ORMapDiSCO(stmts);
			java.net.URI idURI = disco.getId();
			ORMapDiSCOMgr discoMgr = new ORMapDiSCOMgr();
			ORMapEventMgr eventMgr = new ORMapEventMgr();
			@SuppressWarnings("unused")
			ORMapEvent event = discoMgr.createDiSCO(agentId, disco, eventMgr, ts);
			
			URI uri = ts.getValueFactory().createURI("http://dx.doi.org/10.1109/ACCESS.2014.2332453");
			List <URI> sysAgents = new ArrayList<URI>();
			sysAgents.add(agentId);
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date dateFrom = dateFormat.parse("2014-1-1");
			Date dateTo = dateFormat.parse("2050-1-1");
		
			Set <URI> discoUris = resourceMgr.getRelatedDiSCOS(uri, 
								null, sysAgents, dateFrom, dateTo, discoMgr, ts);
			
			assertTrue(discoUris.size()==1);
			
			Iterator<URI> iter = discoUris.iterator();
			URI matchingUri = iter.next();
			assertTrue(matchingUri.toString().equals(idURI.toString()));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	public void testGetResourceRdfTypes() {	
		try {			
			Statement s1 = ts.getValueFactory().createStatement(resource01, typePred, RMAP.DISCO, resource01);
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
		URI predJA = null;
		URI predA = null;
		try {	
			predJA = ts.getValueFactory().createURI("http://purl.org/spar/fabio/JournalArticle");
			predA = ts.getValueFactory().createURI("http://purl.org/spar/fabio/Article");
			Statement s1 = ts.getValueFactory().createStatement(resource02, typePred, predJA, resource01);
			ts.addStatement(s1);
			Statement s2 = ts.getValueFactory().createStatement(resource02, typePred, predA, resource02);			
			ts.addStatement(s2);
			Statement s3 = ts.getValueFactory().createStatement(resource02, typePred, predJA, resource02);
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

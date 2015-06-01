/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.model.impl.openrdf.ORMapEvent;
import info.rmapproject.core.rdfhandler.impl.openrdf.RioRDFHandler;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;


/**
 * @author smorrissey
 *
 */
public class ORMapDiSCOMgrTest {
	
	protected SesameTriplestore ts = null;
	protected ORMapAgentMgr agentMgr;
	protected URI systemAgentURI;
	protected String systemAgentId = "http://orcid.org/0000-0003-2069-1219";
	protected RMapService service;
	
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
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
		agentMgr = new ORMapAgentMgr();
		systemAgentURI = ts.getValueFactory().createURI(systemAgentId);
		
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapDiSCOMgr#readDiSCO(org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testReadDiSCO() {
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
		ORMapStatementMgr stmtMgr = new ORMapStatementMgr();
		@SuppressWarnings("unused")
		ORMapEvent event = discoMgr.createDiSCO(ORAdapter.uri2OpenRdfUri(systemAgent.getId()), disco, eventMgr, stmtMgr, ts);
		URI dUri = ORAdapter.uri2OpenRdfUri(idURI);
		ORMapDiSCO rDisco = discoMgr.readDiSCO(dUri, ts);
		java.net.URI idURI2 = rDisco.getId();
		assertEquals(idURI.toASCIIString(),idURI2.toASCIIString());
		
	}

}

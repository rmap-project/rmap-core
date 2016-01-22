/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.rdfhandler.impl.openrdf.RioRDFHandler;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;


/**
 * @author smorrissey, khanson
 *
 */
public class ORMapDiSCOMgrTest  {
	
	protected String description = "This is an example DiSCO aggregating different file formats for an article on IEEE Xplore as well as multimedia content related to the article.";
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
			+ description  
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
	
	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapDiSCOMgr#readDiSCO(org.openrdf.model.URI, boolean, Map, Map, ORMapEventMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapException 
	 */
	@Test
	public void testReadDiSCO() throws RMapException, RMapDefectiveArgumentException {
		
		RMapService rmapService=RMapServiceFactoryIOC.getFactory().createService();
		
		java.net.URI agentId; //used to pass back into rmapService since all of these use java.net.URI
		
		try {
			
			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			//create new test agent
			RMapAgent agent = new ORMapAgent(AGENT_URI, ID_PROVIDER_URI, AUTH_ID_URI, NAME);
			rmapService.createAgent(agent,agent.getId().getIri());
			agentId=agent.getId().getIri();
			if (rmapService.isAgentId(agentId)){
				System.out.println("Test Agent successfully created!  URI is " + agentId);
			}
			
			// Check the agent was created
			assertTrue(rmapService.isAgentId(agentId));	
		
			// now create DiSCO	
			InputStream stream = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
			RioRDFHandler handler = new RioRDFHandler();	
			List<Statement>stmts = handler.convertRDFToStmtList(stream, "http://rmapdns.ddns.net:8080/api/disco/", "RDFXML");
			ORMapDiSCO disco = new ORMapDiSCO(stmts);
			RMapUri idURI = disco.getId();
			ORMapDiSCOMgr discoMgr = new ORMapDiSCOMgr();
			discoMgr.createDiSCO(ORAdapter.uri2OpenRdfUri(agentId), disco, ts);
			
			//read DiSCO back
			URI dUri = ORAdapter.rMapUri2OpenRdfUri(idURI);
			ORMapDiSCO rDisco = discoMgr.readDiSCO(dUri, true, null, null, ts).getDisco();
			RMapUri idURI2 = rDisco.getId();
			assertEquals(idURI.toString(),idURI2.toString());
			String description2 = rDisco.getDescription().toString();
			assertEquals(description,description2);
			rmapService.deleteDiSCO(new java.net.URI(idURI.toString()), agentId);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}	
	}
	

	/**
	 * Test method first creates a DiSCO and then attempts to update it twice.
	 * The second update should be rejected as you can only update the latest version.
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapException 
	 */
	@Test
	public void testCreateAndUpdateDiSCO() throws RMapException, RMapDefectiveArgumentException {
		
		RMapService rmapService=RMapServiceFactoryIOC.getFactory().createService();
		
		java.net.URI agentId; //used to pass back into rmapService since all of these use java.net.URI
		
		try {
			
			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			//create new test agent
			RMapAgent agent = new ORMapAgent(AGENT_URI, ID_PROVIDER_URI, AUTH_ID_URI, NAME);
			rmapService.createAgent(agent,agent.getId().getIri());
			agentId=agent.getId().getIri();
			if (rmapService.isAgentId(agentId)){
				System.out.println("Test Agent successfully created!  URI is " + agentId);
			}
			
			// Check the agent was created
			assertTrue(rmapService.isAgentId(agentId));	
		
			// now create DiSCO	
			InputStream stream = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
			RioRDFHandler handler = new RioRDFHandler();	
			List<Statement>stmts = handler.convertRDFToStmtList(stream, "http://rmapdns.ddns.net:8080/api/disco/", "RDFXML");
			ORMapDiSCO disco = new ORMapDiSCO(stmts);
			RMapUri idURI = disco.getId();
			ORMapDiSCOMgr discoMgr = new ORMapDiSCOMgr();
			discoMgr.createDiSCO(ORAdapter.uri2OpenRdfUri(agentId), disco, ts);
			
			//read DiSCO back
			URI dUri = ORAdapter.rMapUri2OpenRdfUri(idURI);
			ORMapDiSCO rDisco = discoMgr.readDiSCO(dUri, true, null, null, ts).getDisco();
			RMapUri rIdURI = rDisco.getId();
			assertEquals(idURI.toString(),rIdURI.toString());
			String description2 = rDisco.getDescription().toString();
			assertEquals(description,description2);

			boolean correctErrorThrown = false;
			// now update DiSCO	
			ORMapDiSCO disco2 = new ORMapDiSCO(stmts);
			discoMgr.updateDiSCO(ORAdapter.uri2OpenRdfUri(agentId), false, dUri, disco2, ts);
			ORMapDiSCO disco3 = new ORMapDiSCO(stmts);
			try{
				discoMgr.updateDiSCO(ORAdapter.uri2OpenRdfUri(agentId), false, dUri, disco3, ts);
			} catch(RMapException ex){
				if (ex.getMessage().contains("latest version")){
					correctErrorThrown=true;
				}
			}
			if (!correctErrorThrown)	{
				fail("A 'not latest version' exception should have been thrown!"); 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}	
	}
	
	

}

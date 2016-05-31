/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapNotLatestVersionException;
import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.model.request.RMapRequestAgent;
import info.rmapproject.core.rdfhandler.RDFType;
import info.rmapproject.core.rdfhandler.impl.openrdf.RioRDFHandler;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.IRI;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author smorrissey, khanson
 *
 */

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration({ "classpath*:/spring-rmapcore-testcontext.xml" })
public class ORMapDiSCOMgrTest  {
	
	@Autowired
	RMapService rmapService;
	
	@Autowired 
	SesameTriplestore triplestore;
	
	@Autowired 
	ORMapDiSCOMgr discomgr;
		
	private String description = "This is an example DiSCO aggregating different file formats for an article on IEEE Xplore as well as multimedia content related to the article.";
	private String discoRDF = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "  
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

	
	protected String discoRDFBnodes = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "  
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
			+ "<ore:aggregates rdf:nodeID=\"N65570\" />"
			+ "<ore:aggregates rdf:nodeID=\"N65575\" />"
			+ "<ore:aggregates rdf:nodeID=\"N65580\" />"
			+ "</rmap:DiSCO>"  
	    	+ "<fabio:JournalArticle rdf:about=\"http://dx.doi.org/10.1109/ACCESS.2014.2332453\">"  
	    	+ "<dc:title>Toward Scalable Systems for Big Data Analytics: A Technology Tutorial</dc:title>"  
			+ "<dcterms:creator rdf:nodeID=\"N65570\" />"
			+ "<dcterms:creator rdf:nodeID=\"N65575\" />"
			+ "<dcterms:creator rdf:nodeID=\"N65580\" />" 
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
	    	+ "<rdf:Description rdf:nodeID=\"N65570\">"
	    	+ "<foaf:name>Carlchristian Eckert</foaf:name>"
	    	+ "<rdf:type rdf:resource=\"http://purl.org/dc/terms/Agent\" />"
		    + "</rdf:Description>"
	    	+ "<rdf:Description rdf:nodeID=\"N65575\">"
	    	+ "<foaf:name>Axel Huebl</foaf:name>"
	    	+ "<rdf:type rdf:resource=\"http://purl.org/dc/terms/Agent\" />"
		    + "</rdf:Description>"
	    	+ "<rdf:Description rdf:nodeID=\"N65580\">"
	    	+ "<foaf:name>Ren� Widera</foaf:name>"
	    	+ "<rdf:type rdf:resource=\"http://purl.org/dc/terms/Agent\" />"
	    	+ "</rdf:Description>"	    	
	    	+ "</rdf:RDF>";

	
	private IRI AGENT_IRI = null; 
	private IRI ID_PROVIDER_IRI = null;
	private IRI AUTH_ID_IRI = null;
	private Value NAME = null;
	private IRI AGENT_IRI2 = null; 
	private IRI ID_PROVIDER_IRI2 = null;
	private IRI AUTH_ID_IRI2 = null;
	private Value NAME2 = null;
	private RMapRequestAgent requestAgent = null;
	private RMapRequestAgent requestAgent2 = null;
	
	@Before
	public void setUp() throws Exception {
		//these will be used for a test agent.
		this.AGENT_IRI = ORAdapter.getValueFactory().createIRI("ark:/22573/rmaptestagent");
		this.ID_PROVIDER_IRI = ORAdapter.getValueFactory().createIRI("http://orcid.org/");
		this.AUTH_ID_IRI = ORAdapter.getValueFactory().createIRI("http://rmap-project.org/identities/rmaptestauthid");
		this.NAME = ORAdapter.getValueFactory().createLiteral("RMap test Agent");	
		
		this.AGENT_IRI2 = ORAdapter.getValueFactory().createIRI("ark:/22573/rmaptestagent2");
		this.ID_PROVIDER_IRI2 = ORAdapter.getValueFactory().createIRI("http://orcid.org/");
		this.AUTH_ID_IRI2 = ORAdapter.getValueFactory().createIRI("http://rmap-project.org/identities/rmaptestauthid2");
		this.NAME2 = ORAdapter.getValueFactory().createLiteral("RMap test Agent 2");		
		
		requestAgent = new RMapRequestAgent(new URI(AGENT_IRI.stringValue()));
		requestAgent2 = new RMapRequestAgent(new URI(AGENT_IRI2.stringValue()));
	}
	
	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapDiSCOMgr#readDiSCO(org.openrdf.model.IRI, boolean, Map, Map, ORMapEventMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapException 
	 */
	@Test
	public void testReadDiSCO() throws RMapException, RMapDefectiveArgumentException {
				
		java.net.URI agentId; //used to pass back into rmapService since all of these use java.net.URI
		
		try {
			//create new test agent
			RMapAgent agent = new ORMapAgent(AGENT_IRI, ID_PROVIDER_IRI, AUTH_ID_IRI, NAME);
			agentId=agent.getId().getIri();
			if (!rmapService.isAgentId(agentId)) {
				rmapService.createAgent(agent,requestAgent);
			}
			if (rmapService.isAgentId(agentId)){
				System.out.println("Test Agent successfully created!  URI is " + agentId);
			}
			
			// Check the agent was created
			assertTrue(rmapService.isAgentId(agentId));	
		
			// now create DiSCO	
			InputStream stream = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
			RioRDFHandler handler = new RioRDFHandler();	
			List<Statement>stmts = handler.convertRDFToStmtList(stream, RDFType.RDFXML, "");
			ORMapDiSCO disco = new ORMapDiSCO(stmts);
			RMapIri idIRI = disco.getId();
			
			requestAgent.setAgentKeyId(new java.net.URI("ark:/29297/testkey"));
			
			discomgr.createDiSCO(disco, requestAgent, triplestore);
			
			//read DiSCO back
			IRI dIri = ORAdapter.rMapIri2OpenRdfIri(idIRI);
			ORMapDiSCO rDisco = discomgr.readDiSCO(dIri, true, null, null, triplestore).getDisco();
			RMapIri idIRI2 = rDisco.getId();
			assertEquals(idIRI.toString(),idIRI2.toString());
			String description2 = rDisco.getDescription().toString();
			assertEquals(description,description2);
						
			rmapService.deleteDiSCO(new java.net.URI(idIRI.toString()), requestAgent);
									
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
		
		java.net.URI agentId; //used to pass back into rmapService since all of these use java.net.URI
		java.net.URI agentId2; //used to pass back into rmapService since all of these use java.net.URI
		
		try {
			//create new test agent
			RMapAgent agent = new ORMapAgent(AGENT_IRI, ID_PROVIDER_IRI, AUTH_ID_IRI, NAME);
			agentId=agent.getId().getIri();
			if (!rmapService.isAgentId(agentId)) {
				rmapService.createAgent(agent,requestAgent);
			}
			if (rmapService.isAgentId(agentId)){
				System.out.println("Test Agent successfully created!  URI is " + agentId);
			}
			
			//create second test agent
			RMapAgent agent2 = new ORMapAgent(AGENT_IRI2, ID_PROVIDER_IRI2, AUTH_ID_IRI2, NAME2);
			agentId2=agent2.getId().getIri();
			if (!rmapService.isAgentId(agentId2)) {
				rmapService.createAgent(agent2,requestAgent2);
			}
			if (rmapService.isAgentId(agentId2)){
				System.out.println("Test Agent 2 successfully created!  URI is " + agentId2);
			}
			
			// Check the agent was created
			assertTrue(rmapService.isAgentId(agentId));	
		
			// now create DiSCO	
			InputStream stream = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
			RioRDFHandler handler = new RioRDFHandler();	
			List<Statement>stmts = handler.convertRDFToStmtList(stream, RDFType.RDFXML, "");
			ORMapDiSCO disco = new ORMapDiSCO(stmts);
			RMapIri idIRI = disco.getId();
			
			URI keyId = new URI("ark:/29297/testkey");
			requestAgent.setAgentKeyId(keyId);
			RMapEvent event = discomgr.createDiSCO(disco, requestAgent, triplestore);
			
			//read DiSCO back
			IRI dIri = ORAdapter.rMapIri2OpenRdfIri(idIRI);
			ORMapDiSCO rDisco = discomgr.readDiSCO(dIri, true, null, null, triplestore).getDisco();
			RMapIri rIdIRI = rDisco.getId();
			assertEquals(idIRI.toString(),rIdIRI.toString());
			//check key is associated with event
			String keyfromevent = event.getAssociatedKey().toString();
			String keyused = keyId.toString();
			
			assertEquals(keyfromevent,keyused);
			
			boolean correctErrorThrown = false;
			// now update DiSCO	
			ORMapDiSCO disco2 = new ORMapDiSCO(stmts);
			discomgr.updateDiSCO(dIri, disco2, requestAgent, false, triplestore);
			ORMapDiSCO disco3 = new ORMapDiSCO(stmts);
			try{
				discomgr.updateDiSCO(dIri, disco3, requestAgent, false, triplestore);
			} catch(RMapNotLatestVersionException ex){
				if (ex.getMessage().contains("latest version")){
					correctErrorThrown=true;
				}
			}
			if (!correctErrorThrown)	{
				fail("A 'not latest version' exception should have been thrown!"); 
			}
			//now update with different agent using latest id
			try{
				IRI dIri2 = ORAdapter.rMapIri2OpenRdfIri(disco2.getId());
				discomgr.updateDiSCO(dIri2, disco3, requestAgent2, false, triplestore);
			} catch(Exception ex){
				ex.printStackTrace();
			}
			//read back derived disco
			ORMapDiSCO rDisco2 = discomgr.readDiSCO(dIri, true, null, null, triplestore).getDisco();
			RMapIri rIdIRI2 = rDisco2.getId();
			assertEquals(idIRI.toString(),rIdIRI2.toString());
			String description3 = rDisco2.getDescription().toString();
			assertEquals(description,description3);

			List<java.net.URI> agentVersions = rmapService.getDiSCOAllAgentVersions(rIdIRI2.getIri());
			assertTrue(agentVersions.size()==2);
			
			rmapService.deleteDiSCO(disco.getId().getIri(), requestAgent);
			rmapService.deleteDiSCO(disco2.getId().getIri(), requestAgent);
			rmapService.deleteDiSCO(disco3.getId().getIri(), requestAgent2);
						
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}	
	}

	/**
	 * Test method creates a DiSCO with blank nodes in the body and as aggregates, 
	 * it then updates with the same DiSCO to confirm Update works too
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapException 
	 */
	/*@Test
	public void testCreateAndUpdateDiSCOWithBNodes() throws RMapException, RMapDefectiveArgumentException {
		
		RMapService rmapService=RMapServiceFactoryIOC.getFactory().createService();
		
		java.net.URI agentId; //used to pass back into rmapService since all of these use java.net.URI
		
		try {
			
			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			//check test agent ok
			agentId=ORAdapter.openRdfIri2URI(AGENT_IRI);
			// Check the agent was created
			assertTrue(rmapService.isAgentId(agentId));	
		
			// now create DiSCO	with bnodes
			InputStream stream = new ByteArrayInputStream(discoRDFBnodes.getBytes(StandardCharsets.UTF_8));
			RioRDFHandler handler = new RioRDFHandler();	
			List<Statement>stmts = handler.convertRDFToStmtList(stream, "RDFXML", "http://rmapdns.ddns.net:8080/api/disco/");
			ORMapDiSCO disco = new ORMapDiSCO(stmts);
			RMapIri idIRI = disco.getId();
			ORMapDiSCOMgr discoMgr = new ORMapDiSCOMgr();
			discoMgr.createDiSCO(ORAdapter.uri2OpenRdfIri(agentId), disco, ts);
			
			//read DiSCO back
			IRI dIri = ORAdapter.rMapIri2OpenRdfIri(idIRI);
			ORMapDiSCO rDisco = discoMgr.readDiSCO(dIri, true, null, null, ts).getDisco();
			RMapIri rIdIRI = rDisco.getId();
			assertEquals(idIRI.toString(),rIdIRI.toString());
			String description2 = rDisco.getDescription().toString();
			assertEquals(description,description2);
			
			// now update with Bnodes	
			ORMapDiSCO disco2 = new ORMapDiSCO(stmts);
			discoMgr.updateDiSCO(ORAdapter.uri2OpenRdfIri(agentId), false, dIri, disco2, ts);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}	
	}*/
	

}

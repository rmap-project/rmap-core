/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import static org.junit.Assert.*;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.rdfhandler.impl.openrdf.RioRDFHandler;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;


/**
 * @author smorrissey
 *
 */
public class ORMapDiscoTest {
	protected ValueFactory vf = null;
	protected SesameTriplestore ts = null;
	protected Statement rStmt;
	protected Statement rStmt2;
	protected Statement s1;
	protected Statement s2;
	protected Statement s3;
	protected Statement s4;
	protected List<Statement> relatedStmts;
	protected URI r;
	protected URI r2;
	protected Literal a;
	protected URI b;
	protected URI c;
	protected URI d;
	
	protected Literal creator;
	protected URI creatorUri;
	protected URI creatorUri2;
	
	protected String discoRDF = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "  
			+ "<rdf:RDF "  
			+ " xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""  
			+ " xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\""  
			+ " xmlns:rmap=\"http://rmap-project.org/rmap/terms/\""  
			+ " xmlns:dcterms=\"http://purl.org/dc/terms/\""  
			+ " xmlns:dc=\"http://purl.org/dc/elements/1.1/\""  
			+ " xmlns:foaf=\"http://xmlns.com/foaf/0.1/\""  
			+ " xmlns:fabio=\"http://purl.org/spar/fabio/\">"  
			+ "<rmap:DiSCO>"  
			+ "<dcterms:creator rdf:resource=\"http://orcid.org/0000-0000-0000-0000\"/>"
			+ "<dc:description>"  
			+ "This is an example DiSCO aggregating different file formats for an article on IEEE Xplore as well as multimedia content related to the article."  
			+ "</dc:description>"  
			+ "<rmap:aggregates rdf:resource=\"http://dx.doi.org/10.1109/ACCESS.2014.2332453\"/>"  
			+ "<rmap:aggregates rdf:resource=\"http://ieeexplore.ieee.org/ielx7/6287639/6705689/6842585/html/mm/6842585-mm.zip\"/>"  
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
	
	protected String discoRDFdcterms = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<rdf:RDF  xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" " +
			"    xmlns:rmap=\"http://rmap-project.org/rmap/terms/\" " +
			"    xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " +
			"    xmlns:dcterms=\"http://purl.org/dc/terms/\" " +
			"    xmlns:premis=\"http://www.loc.gov/premis/rdf/v1#\" " +
			"    xmlns:modsrdf=\"http://www.loc.gov/mods/modsrdf/v1#\">" +
			"    <rmap:DiSCO>" +
			"        <dcterms:creator rdf:resource=\"http://portico.org\"/>" +
			"        <dcterms:description>The group of digital objects relating to a single article that are preserved in the Portico digital archive.</dcterms:description>" +
			"        <rmap:aggregates rdf:resource=\"ark:/27927/pf1xrp1cv9\"/>" +
			"        <rmap:aggregates rdf:resource=\"http://dx.doi.org/10.1109/ICAL.2007.4339019\"/>" +
			"        <rmap:aggregates rdf:resource=\"ark:/27927/pf1xrntg38\"/>" +
			"        <rmap:aggregates rdf:resource=\"ark:/27927/pf1xrsw3hq\"/>" +
			"        <rmap:aggregates rdf:resource=\"ark:/27927/pf1xrnsz85\"/>" +
			"    </rmap:DiSCO>" +
			"    <rdf:Description rdf:about=\"http://dx.doi.org/10.1109/ICAL.2007.4339019\">" +
			"        <rdf:type rdf:resource=\"http://purl.org/spar/fabio/JournalArticle\"/>" +
			"    </rdf:Description>" +
			"    <rdf:Description rdf:about=\"ark:/27927/pf1xrp1cv9\">" +
			"        <dcterms:isFormatOf rdf:resource=\"http://dx.doi.org/10.1109/ICAL.2007.4339019\"/>" +
			"        <modsrdf:locationOfResource rdf:resource=\"http://portico.org\"/>" +
			"        <dcterms:hasPart rdf:resource=\"ark:/27927/pf1xrntg38\"/>" +
			"        <dcterms:hasPart rdf:resource=\"ark:/27927/pf1xrsw3hq\"/>" +
			"        <dcterms:hasPart rdf:resource=\"ark:/27927/pf1xrnsz85\"/>" +
			"   </rdf:Description>" +
			"    <rdf:Description rdf:about=\"ark:/27927/pf1xrntg38\">" +
			"        <modsrdf:locationOfResource rdf:resource=\"http://portico.org\"/>" +
			"        <dcterms:format>application/xml</dcterms:format>" +
			"        <dcterms:format>IEEE IDAMS DTD:1.0:2005-06-27</dcterms:format>" +
			"        <premis:hasOriginalName>CONFPROC_4338502_4338503.tar/CONFPROC_4338502/4338503/04339019.xml</premis:hasOriginalName>" +
			"        <dcterms:extent>8807 Bytes</dcterms:extent>" +
			"    </rdf:Description>" +
			"    <rdf:Description rdf:about=\"ark:/27927/pf1xrsw3hq\">" +
			"        <modsrdf:locationOfResource rdf:resource=\"http://portico.org\"/>" +
			"        <dcterms:format>application/xml</dcterms:format>" +
			"        <dcterms:format>Portico Journal Archiving DTD:2.0:2006-02-28</dcterms:format>" +
			"        <premis:hasOriginalName>unknown</premis:hasOriginalName>" +
			"        <dcterms:extent>6945 Bytes</dcterms:extent>" +
			"    </rdf:Description>" +
			"    <rdf:Description rdf:about=\"ark:/27927/pf1xrnsz85\">" +
			"        <modsrdf:locationOfResource rdf:resource=\"http://portico.org\"/>" +
			"        <dcterms:format>application/pdf</dcterms:format>" +
			"        <dcterms:format>Portable Document Format:1.4:2001</dcterms:format>" +
			"        <premis:hasOriginalName>CONFPROC_4338502_4338503.tar/CONFPROC_4338502/4338503/04339019.pdf</premis:hasOriginalName>" +
			"        <dcterms:extent>769543 Bytes</dcterms:extent>" +
			"    </rdf:Description>" +
			"</rdf:RDF>";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		vf = ORAdapter.getValueFactory();
		r = vf.createURI("http://rmap-info.org");	
		r2 = vf.createURI("https://rmap-project.atlassian.net/wiki/display/RMAPPS/RMap+Wiki");
		a = vf.createLiteral("a");
		b = vf.createURI("http://b.org");
		c = vf.createURI("http://c.org");
		d = vf.createURI("http://d.org");		
		relatedStmts = new ArrayList<Statement>();
		//predicates are nonsense here
		// first test connected r->a r->b b->c b->d
		s1 = vf.createStatement(r,RMAP.ACTIVE,a);
		s2 = vf.createStatement(r,RMAP.ACTIVE,b);
		s3 = vf.createStatement(b,RMAP.ACTIVE,c);
		s4 = vf.createStatement(b,RMAP.ACTIVE,d);
		creator = vf.createLiteral("Mary Smith");
		creatorUri = vf.createURI("http://orcid.org/0000-0003-2069-1219");
		creatorUri2 = vf.createURI("http://orcid.org/2222-0003-2069-1219");
	}


	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#ORMapDiSCO(RMapUri, java.util.List)}.
	 */
	@Test
	public void testORMapDiSCORMapResourceListOfURI() {
		List<java.net.URI> resourceList = new ArrayList<java.net.URI>();
		try {
			resourceList.add(new java.net.URI("http://rmap-info.org"));
			resourceList.add(new java.net.URI
					("https://rmap-project.atlassian.net/wiki/display/RMAPPS/RMap+Wiki"));
			RMapUri author = ORAdapter.openRdfUri2RMapUri(creatorUri);
			ORMapDiSCO disco = new ORMapDiSCO(author, resourceList);
			assertEquals(author.toString(),disco.getCreator().getStringValue());
			List<Statement>resources = disco.getAggregatedResourceStatements();
			assertEquals(2, resources.size());
			Model model = new LinkedHashModel();
			model.addAll(resources);
			Set<URI> predicates = model.predicates();
			assertEquals(1,predicates.size());
			assertTrue(predicates.contains(RMAP.AGGREGATES));
			Set<Value> objects = model.objects();
			assertTrue(objects.contains(r));
			assertTrue(objects.contains (r2));
			Statement cstmt = disco.getCreatorStmt();
			assertEquals(DCTERMS.CREATOR,cstmt.getPredicate());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}

	@Test
	public void testORMapDisco() {
		InputStream stream = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
		RioRDFHandler handler = new RioRDFHandler();	
		List <Statement> stmts = handler.convertRDFToStmtList(
				stream, "http://rmapdns.ddns.net:8080/api/disco/", "RDFXML");
		ORMapDiSCO disco = new ORMapDiSCO(stmts);
		assertEquals(14, disco.getRelatedStatementsAsList().size());
		OutputStream os = handler.disco2Rdf(disco, "RDFXML");
		String output = os.toString();
		assertTrue(output.contains("Yonggang Wen"));
		stream = new ByteArrayInputStream(discoRDFdcterms.getBytes(StandardCharsets.UTF_8));
		stmts = handler.convertRDFToStmtList(stream,"http://rmapdns.ddns.net:8080/api/disco/", "RDFXML");
		try {
			disco = new ORMapDiSCO(stmts);
			assertTrue(true);
		}
		catch (RMapException e){
			fail("should have handled dcTerms");			
		}
	}
	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#referencesAggregate(java.util.List)}.
	 */
	@Test
	public void testReferencesAggregate() {
		ORMapDiSCO disco = new ORMapDiSCO();
		Statement rStmt = vf.createStatement(disco.discoContext, RMAP.AGGREGATES, r,disco.discoContext);
		Statement rStmt2 = vf.createStatement(disco.discoContext, RMAP.AGGREGATES, r2,disco.discoContext);
		disco.aggregatedResources = new ArrayList<Statement>();
		disco.aggregatedResources.add(rStmt);
		disco.aggregatedResources.add(rStmt2);
		relatedStmts.add(s1);
		relatedStmts.add(s2);
		relatedStmts.add(s3);
		relatedStmts.add(s4);
		boolean referencesAggs = disco.referencesAggregate(relatedStmts);
		assertTrue(referencesAggs);
		relatedStmts.remove(s1);
		referencesAggs = disco.referencesAggregate(relatedStmts);
		assertTrue(referencesAggs);
		relatedStmts.remove(s2);
		referencesAggs = disco.referencesAggregate(relatedStmts);
		assertFalse(referencesAggs);
		
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#isConnectedGraph(java.util.List)}.
	 */
	@Test
	public void testIsConnectedGraph() {
		ORMapDiSCO disco = new ORMapDiSCO();
		Statement rStmt = vf.createStatement(disco.discoContext, RMAP.AGGREGATES, r,disco.discoContext);
		Statement rStmt2 = vf.createStatement(disco.discoContext, RMAP.AGGREGATES, r2,disco.discoContext);
		disco.aggregatedResources = new ArrayList<Statement>();
		disco.aggregatedResources.add(rStmt);
		disco.aggregatedResources.add(rStmt2);
		relatedStmts.add(s1);
		relatedStmts.add(s2);
		relatedStmts.add(s3);
		relatedStmts.add(s4);
		boolean isConnected = disco.isConnectedGraph(relatedStmts);
		assertTrue (isConnected);
		// second test disjoint r->a  b->c
		relatedStmts.remove(s2);
		relatedStmts.remove(s4);
		isConnected = disco.isConnectedGraph(relatedStmts);
		assertFalse(isConnected);
		// third test connected r->a  b->c r2->c c->b, handles cycle, duplicates
		Statement s5 = vf.createStatement(r2,RMAP.ACTIVE,c);
		Statement s6 = vf.createStatement(c,RMAP.ACTIVE,b);
		Statement s7 = vf.createStatement(c,RMAP.ACTIVE,b);
		relatedStmts.add(s6);
		relatedStmts.add(s5);
		relatedStmts.add(s7);
		isConnected = disco.isConnectedGraph(relatedStmts);
		assertTrue (isConnected);
	}


	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#getAggregatedResourceStatements()}.
	 */
	@Test
	public void testGetAggregatedResourceStatements() {
		ORMapDiSCO disco = new ORMapDiSCO();
		Statement rStmt = vf.createStatement(disco.discoContext, RMAP.AGGREGATES, r,disco.discoContext);
		Statement rStmt2 = vf.createStatement(disco.discoContext, RMAP.AGGREGATES, r2,disco.discoContext);
		List<java.net.URI> list1 = new ArrayList<java.net.URI>();
		list1.add(ORAdapter.openRdfUri2URI(r));
		list1.add(ORAdapter.openRdfUri2URI(r2));
		disco.setAggregratedResources(list1);	
		List<Statement>list2 = disco.getAggregatedResourceStatements();
		assertEquals(2,list2.size());
		assertTrue(list2.contains(rStmt));
		assertTrue(list2.contains(rStmt2));
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#setAggregratedResources(java.util.List)}.
	 */
	@Test
	public void testSetAggregratedResources() {
		ORMapDiSCO disco = new ORMapDiSCO();
		List<java.net.URI> list1 = new ArrayList<java.net.URI>();
		list1.add(ORAdapter.openRdfUri2URI(r));
		list1.add(ORAdapter.openRdfUri2URI(r2));
		disco.setAggregratedResources(list1);				
		List<java.net.URI>list2 = disco.getAggregratedResources();
		assertEquals(2,list2.size());
		assertTrue(list2.contains(ORAdapter.openRdfUri2URI(r)));
		assertTrue(list2.contains(ORAdapter.openRdfUri2URI(r2)));
	}


	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#setCreator(RMapUri)}.
	 */
	@Test
	public void testSetCreator() {
		List<java.net.URI> resourceList = new ArrayList<java.net.URI>();
		try {
			resourceList.add(new java.net.URI("http://rmap-info.org"));
			resourceList.add(new java.net.URI
					("https://rmap-project.atlassian.net/wiki/display/RMAPPS/RMap+Wiki"));
			RMapUri author = ORAdapter.openRdfUri2RMapUri(creatorUri);
			ORMapDiSCO disco = new ORMapDiSCO(author, resourceList);
			assertEquals(author.toString(),disco.getCreator().getStringValue());
			try {
				RMapUri author2 = ORAdapter.openRdfUri2RMapUri(creatorUri2);
				disco.setCreator(author2);
				assertEquals(author2.toString(),disco.getCreator().getStringValue());
			}catch (RMapException r){
				fail(r.getMessage());
			}
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#setDescription(RMapValue)}.
	 */
	@Test
	public void testSetDescription() {
		List<java.net.URI> resourceList = new ArrayList<java.net.URI>();
		try {
			resourceList.add(new java.net.URI("http://rmap-info.org"));
			resourceList.add(new java.net.URI
					("https://rmap-project.atlassian.net/wiki/display/RMAPPS/RMap+Wiki"));
			RMapUri author = ORAdapter.openRdfUri2RMapUri(creatorUri);
			ORMapDiSCO disco = new ORMapDiSCO(author, resourceList);
			Literal desc = vf.createLiteral("this is a description");
			RMapValue rdesc = ORAdapter.openRdfValue2RMapValue(desc);
			disco.setDescription(rdesc);
			RMapValue gDesc = disco.getDescription();
			assertEquals (rdesc.getStringValue(), gDesc.getStringValue());
			Statement descSt = disco.getDescriptonStatement();
			assertEquals(desc, descSt.getObject());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}	}


	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#getTypeStatement()}.
	 */
	@Test
	public void testGetTypeStatement() {
		List<java.net.URI> resourceList = new ArrayList<java.net.URI>();
		try {
			resourceList.add(new java.net.URI("http://rmap-info.org"));
			resourceList.add(new java.net.URI
					("https://rmap-project.atlassian.net/wiki/display/RMAPPS/RMap+Wiki"));
			RMapUri author = ORAdapter.openRdfUri2RMapUri(creatorUri);
			ORMapDiSCO disco = new ORMapDiSCO(author, resourceList);
			Statement stmt = disco.getTypeStatement();
			assertEquals(disco.getId().toASCIIString(), stmt.getSubject().stringValue());
			assertEquals(RDF.TYPE, stmt.getPredicate());
			assertEquals(RMAP.DISCO, stmt.getObject());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#getDiscoContext()}.
	 */
	@Test
	public void testGetDiscoContext() {
		List<java.net.URI> resourceList = new ArrayList<java.net.URI>();
		try {
			resourceList.add(new java.net.URI("http://rmap-info.org"));
			resourceList.add(new java.net.URI
					("https://rmap-project.atlassian.net/wiki/display/RMAPPS/RMap+Wiki"));
			RMapUri author = ORAdapter.openRdfUri2RMapUri(creatorUri);
			ORMapDiSCO disco = new ORMapDiSCO(author, resourceList);
			URI context = disco.getDiscoContext();
			Model model = new LinkedHashModel();
			for (Statement stm:model){
				assertEquals(context, stm.getContext());
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}

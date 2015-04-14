/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
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
public class ORMapAgentMgrTest {

	protected String systemAgentId = "http://orcid.org/0000-0003-2069-1219";
	protected ORMapAgentMgr agentMgr;
//	protected ORMapProfileMgr profilemgr;
//	protected ORMapIdentityMgr identitymgr;
	protected SesameTriplestore ts;
	protected URI systemAgentURI;
	protected String doi = "DOI:10.1080/13614576.2014.883935";
	protected URI doiURI;
	protected String mbox = "mailto:mary.smith@example.org";
	protected URI mboxURI;
	protected List<Statement> toBeAddedStmts;
	protected List<Statement> toBeDeletedStmts;
	protected List<URI> newObjects;
	protected URI textType;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		agentMgr = new ORMapAgentMgr();
//		profilemgr = new ORMapProfileMgr();
//		identitymgr = new ORMapIdentityMgr();
		ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
		systemAgentURI = ts.getValueFactory().createURI(systemAgentId);
		doiURI = ts.getValueFactory().createURI(doi);
		mboxURI = ts.getValueFactory().createURI(mbox);
		toBeAddedStmts = new ArrayList<Statement>();
		toBeDeletedStmts = new ArrayList<Statement>();
		newObjects = new ArrayList<URI>();
		textType = ts.getValueFactory().createURI("http://purl.org/dc/dcmitype/Text");
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#readAgent(org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testReadAgent() {
		//TODO change to use service to create Agent so events exist so we can get status
		ORMapAgent systemAgent = agentMgr.createAgentObject(systemAgentURI, systemAgentURI, ts);
		URI newAgentURI = systemAgent.getContext();
		ORMapAgent readAgent = null;
		try {
			readAgent = agentMgr.readAgent(newAgentURI, ts);
			assertEquals(systemAgent.getRepresentationId().getStringValue(),readAgent.getRepresentationId().getStringValue());
			assertEquals(systemAgent.getCreator().getStringValue(), readAgent.getCreator().getStringValue());
			assertEquals(systemAgent.getType(), readAgent.getType());
		}
		catch (RMapAgentNotFoundException e){
			fail("agent not found");
		}
		catch (RMapException e){
			fail("exception");
		}
	}


//	/**
//	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#createRelatedStatementsAgents(java.util.List, org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.ORMapProfileMgr, ORMapIdentityMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
//	 */
//	@Test
//	public void testCreateRelatedStatementsAgents() {
//		URI newUri =null;
//		BNode bnode  = null;
//		Statement crStmt=null, crStmt2 = null;
//		try {
//			newUri = ts.getValueFactory().createURI("http://orcid.org/0000-0000-0000-0007");
//			bnode = ts.getValueFactory().createBNode();
//			crStmt = ts.getValueFactory().createStatement(doiURI, DCTERMS.CREATOR, newUri);
//			crStmt2 = ts.getValueFactory().createStatement(doiURI, DCTERMS.CREATOR, bnode);
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		Literal dcLit= null;
//		try {
//			dcLit = ts.getValueFactory().createLiteral("Data Conservancy");
//		} catch (RepositoryException e1) {
//			e1.printStackTrace();
//			fail();
//		}
//		List<Statement> model = new ArrayList<Statement>();
//		try {
//			model.add(crStmt);
//			model.add(crStmt2);
//			model.add(ts.getValueFactory().createStatement(doiURI, DCTERMS.TYPE, textType));
//			model.add(ts.getValueFactory().createStatement(bnode, RDF.TYPE, FOAF.PERSON));
//			model.add(ts.getValueFactory().createStatement(bnode, FOAF.NAME, dcLit));
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		List<URI> newAgentRelatedObjects= agentMgr.createRelatedStatementsAgents(model, systemAgentURI, profilemgr, identitymgr, ts);
//		assertEquals(4,newAgentRelatedObjects.size());
//		assertEquals(20,model.size());
//		
//	}

//	/**
//	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#createAgentandProfileFromURI(org.openrdf.model.URI, Statement, java.util.List, java.util.List, java.util.List, org.openrdf.model.Model, org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.ORMapProfileMgr, ORMapIdentityMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
//	 */
//	@Test
//	public void testCreateAgentandProfileFromURI() {
//		// URI is agent
//		this.testCreateAgentandProfileFromURIAgent("http://orcid.org/0000-0000-0000-0004");
//		assertEquals(3,newObjects.size());
//		assertEquals(19, toBeAddedStmts.size());
//		assertEquals(5, toBeDeletedStmts.size());
//		this.clearLists();
//		// URI is profile
//		this.testCreateAgentandProfileFromURIProfile("http://orcid.org/0000-0000-0000-0005");
//		// URI is identity
//		this.clearLists();
//		this.testCreateAgentandProfileFromURIIdentity("http://orcid.org/0000-0000-0000-0006",
//				"http://dataconservancy.org/");
//		// URI is identityLocalPart
//		this.clearLists();
//		URI dcUri= null;
//		try {
//			dcUri = ts.getValueFactory().createURI("http://dataconservancy.org/");
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		assertTrue(identitymgr.isLocalPartUri(dcUri, ts));
//		List<Statement> stmts = null;
//		try {
//			stmts = ts.getStatementsAnyContext(null, RMAP.IDLOCALPART, dcUri, false);
//			for (Statement stmt:stmts){			
//				URI idUri = (URI) stmt.getSubject();
//				assertTrue(identitymgr.isIdentityId(idUri, ts));
//				List<Statement> profileStmts = ts.getStatementsAnyContext(
//						null, RMAP.PROFILE_ID_BY, idUri, false);
//				for (Statement pstmt:profileStmts){
//					URI profileId = (URI)pstmt.getSubject();
//					assertTrue(profilemgr.isProfileId(profileId, ts));
//					ORMapProfile profile = profilemgr.readProfile(profileId, ts);
//					URI parentAgent = ORAdapter.rMapUri2OpenRdfUri(profile.getParentAgentId());
//					assertTrue(agentMgr.isAgentId(parentAgent, ts));
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RMapException(e);
//		}
//		this.testCreateAgentandProfileFromURIIdLocalPart(dcUri);
//	}

	protected void clearLists() {
		newObjects.clear();
		toBeAddedStmts.clear();
		toBeDeletedStmts.clear();
	}
	
//	protected void testCreateAgentandProfileFromURIIdLocalPart(URI localPartURI){
//		Statement crStmt= null;
//		try {
//			crStmt = ts.getValueFactory().createStatement(doiURI, DCTERMS.CREATOR, localPartURI);
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		Literal dcLit= null;
//		try {
//			dcLit = ts.getValueFactory().createLiteral("Data Conservancy");
//		} catch (RepositoryException e1) {
//			e1.printStackTrace();
//			fail();
//		}
//		Model model = new LinkedHashModel();
//		try {
//			model.add(crStmt);
//			model.add(ts.getValueFactory().createStatement(localPartURI, RDF.TYPE, FOAF.ORGANIZATION));
//			model.add(ts.getValueFactory().createStatement(localPartURI, FOAF.NAME, dcLit));
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		agentMgr.createAgentandProfileFromURI(localPartURI, crStmt, toBeAddedStmts,
//				toBeDeletedStmts, newObjects, model, systemAgentURI, profilemgr, identitymgr, ts);	
//		assertEquals(1,newObjects.size());
//		assertEquals(12, toBeAddedStmts.size());
//		assertEquals(3, toBeDeletedStmts.size());
//	}
		
//	protected void testCreateAgentandProfileFromURIIdentity(String pAgentStr, 
//			String idStr){
//		java.net.URI jUri = null;
//		try {
//			jUri = new java.net.URI(pAgentStr);
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//			fail();
//		}
//		URI agentUri = ORAdapter.uri2OpenRdfUri(jUri);
//		ORMapAgent pAgent = new ORMapAgent(agentUri,systemAgentURI);
//		agentUri = agentMgr.createAgent(pAgent, ts);
//		assertTrue(agentMgr.isAgentId(agentUri, ts));
//		pAgent = agentMgr.readAgent(agentUri, ts);
//	
//		java.net.URI idJUri = null;
//		URI idUri;
//		try {
//			idJUri = new java.net.URI(idStr);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail();
//		}
//		idUri = ORAdapter.uri2OpenRdfUri(idJUri);
//		ORMapIdentity identity = new ORMapIdentity(idUri,systemAgentURI);
//		URI identityURI = identitymgr.createIdentity(identity, ts);
//		assertTrue(identitymgr.isIdentityId(identityURI, ts));
//		identity = identitymgr.read(identityURI, ts);
//		
//		ORMapProfile profile = profilemgr.createProfileObject(agentUri, systemAgentURI, identityURI);
//		URI profileUri = profilemgr.createProfile(profile, ts);
//		assertTrue(profilemgr.isProfileId(profileUri, ts));
//		profile = profilemgr.readProfile(profileUri, ts);		
//		Statement crStmt= null;
//		try {
//			crStmt = ts.getValueFactory().createStatement(doiURI, DCTERMS.CREATOR, identityURI);
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		Model model = new LinkedHashModel();
//		try {
//			model.add(crStmt);
//			model.add(ts.getValueFactory().createStatement(identityURI, RDF.TYPE, FOAF.ORGANIZATION));
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		agentMgr.createAgentandProfileFromURI(identityURI, crStmt, toBeAddedStmts, toBeDeletedStmts, newObjects, model, 
//				systemAgentURI, profilemgr, identitymgr, ts);
//		assertEquals(1,newObjects.size());
//		assertEquals(11, toBeAddedStmts.size());
//		assertEquals(2, toBeDeletedStmts.size());
//		List<Statement> stmts = null;
//		try {
//			stmts = ts.getStatementsAnyContext(null, RMAP.IDLOCALPART, idUri, false);
//			assertEquals(1,stmts.size());
//			Resource subject = stmts.get(0).getSubject();
//			assertTrue(subject instanceof URI);
//			URI subjectUri = (URI)subject;
//			assertEquals(identityURI.stringValue(), subjectUri.stringValue());
//			stmts = ts.getStatementsAnyContext(null,RMAP.PROFILE_ID_BY,identityURI,false);
//			assertEquals(2,stmts.size());
//		}
//		catch (Exception e){
//			fail();
//		}
//	}
	
//	protected void testCreateAgentandProfileFromURIProfile (String pAgentStr){
//		java.net.URI jUri = null;
//		try {
//			jUri = new java.net.URI(pAgentStr);
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//			fail();
//		}
//		URI agentUri = ORAdapter.uri2OpenRdfUri(jUri);
//		ORMapAgent pAgent = new ORMapAgent(agentUri,systemAgentURI);
//		agentUri = agentMgr.createAgent(pAgent, ts);
//		assertTrue(agentMgr.isAgentId(agentUri, ts));
//		pAgent = agentMgr.readAgent(agentUri, ts);
//		ORMapProfile profile = profilemgr.createProfileObject(agentUri, systemAgentURI, null);
//		URI profileUri = profilemgr.createProfile(profile, ts);
//		assertTrue(profilemgr.isProfileId(profileUri, ts));
//		profile = profilemgr.readProfile(profileUri, ts);
//		Model model = new LinkedHashModel();
//		Statement crStmt= null;
//		try {
//			crStmt = ts.getValueFactory().createStatement(doiURI, DCTERMS.CREATOR, profileUri);
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		// first try with no change to profile
//		agentMgr.createAgentandProfileFromURI(profileUri, crStmt, toBeAddedStmts, toBeDeletedStmts,
//				newObjects, model, systemAgentURI, profilemgr, identitymgr, ts);
//		assertEquals(0,newObjects.size());
//		assertEquals(5, toBeAddedStmts.size());
//		assertEquals(0, toBeDeletedStmts.size());
//		// now try with changes to profile
//		Literal authorOtherIdLiteral = null;
//		try {
//			authorOtherIdLiteral = ts.getValueFactory().createLiteral("http://ieee.org/author-id01");
//		} catch (RepositoryException e1) {
//			e1.printStackTrace();
//			fail();
//		}
//		Literal authorNameLiteral = null;
//		try {
//			authorNameLiteral = ts.getValueFactory().createLiteral("name 01");
//		} catch (RepositoryException e1) {
//			e1.printStackTrace();
//			fail();
//		}
//		try {
//			model.add(ts.getValueFactory().createStatement(doiURI, DCTERMS.TYPE, textType));
//			model.add(crStmt);
//			model.add(ts.getValueFactory().createStatement(profileUri, RDF.TYPE, FOAF.PERSON));
//			model.add(ts.getValueFactory().createStatement(profileUri, DCTERMS.IDENTIFIER, agentUri));
//			model.add(ts.getValueFactory().createStatement(profileUri, DCTERMS.IDENTIFIER, authorOtherIdLiteral));
//			model.add(ts.getValueFactory().createStatement(profileUri, FOAF.NAME, authorNameLiteral));
//			model.add(ts.getValueFactory().createStatement(profileUri, FOAF.MBOX, mboxURI));
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		this.clearLists();
//		agentMgr.createAgentandProfileFromURI(profileUri, crStmt, toBeAddedStmts, toBeDeletedStmts,
//				newObjects, model, systemAgentURI, profilemgr, identitymgr, ts);
//		assertEquals(3,newObjects.size());
//		assertEquals(20, toBeAddedStmts.size());
//		assertEquals(6, toBeDeletedStmts.size());
//	}
	
//	protected void testCreateAgentandProfileFromURIAgent(String pAgentStr){
//		java.net.URI jUri = null;
//		try {
//			jUri = new java.net.URI(pAgentStr);
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//			fail();
//		}
//		URI agentUri = ORAdapter.uri2OpenRdfUri(jUri);
//		assertFalse (agentMgr.isAgentId(agentUri, ts));
//		ORMapAgent agent = new ORMapAgent(agentUri,systemAgentURI);
//		agentUri = agentMgr.createAgent(agent, ts);
//		assertTrue(agentMgr.isAgentId(agentUri, ts));
//		agent = agentMgr.readAgent(agentUri, ts);
//		Literal agentLit = null;
//		Literal authorOtherIdLiteral = null;
//		Statement crStmt = null;
//		Literal authorNameLiteral = null;
//		try {
//			agentLit = ts.getValueFactory().createLiteral(pAgentStr);			
//			crStmt = ts.getValueFactory().createStatement(doiURI, DCTERMS.CREATOR, agentUri);
//			authorOtherIdLiteral = ts.getValueFactory().createLiteral("http://ieee.org/author-id06");
//			authorNameLiteral = ts.getValueFactory().createLiteral("name 02");
//		} catch (RepositoryException e1) {
//			fail();
//		}
//		Model model = new LinkedHashModel();
//		try {
//			model.add(ts.getValueFactory().createStatement(doiURI, DCTERMS.TYPE, textType));
//			model.add(crStmt);
//			model.add(ts.getValueFactory().createStatement(agentUri, RDF.TYPE, FOAF.PERSON));
//			model.add(ts.getValueFactory().createStatement(agentUri, DCTERMS.IDENTIFIER, agentLit));
//			model.add(ts.getValueFactory().createStatement(agentUri, DCTERMS.IDENTIFIER, authorOtherIdLiteral));
//			model.add(ts.getValueFactory().createStatement(agentUri, FOAF.NAME, authorNameLiteral));
//			model.add(ts.getValueFactory().createStatement(agentUri, FOAF.MBOX, mboxURI));
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		agentMgr.createAgentandProfileFromURI(agentUri, crStmt, toBeAddedStmts, toBeDeletedStmts, 
//				newObjects, model, systemAgentURI, profilemgr, identitymgr, ts);		
//	}
	
//	/**
//	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#createAgentandProfileFromAgentURI(org.openrdf.model.URI, Statement, java.util.List, java.util.List, java.util.List, org.openrdf.model.Model, org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.ORMapProfileMgr, ORMapIdentityMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
//	 */
//	@Test
//	public void testCreateAgentandProfileFromAgentURI() {
//		String pAgentStr = "http://orcid.org/0000-0000-0000-0002";
//		java.net.URI jUri = null;
//		try {
//			jUri = new java.net.URI(pAgentStr);
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//			fail();
//		}
//		URI agentUri = ORAdapter.uri2OpenRdfUri(jUri);
//		assertFalse (agentMgr.isAgentId(agentUri, ts));
//		ORMapAgent agent = new ORMapAgent(agentUri,systemAgentURI);
//		agentUri = agentMgr.createAgent(agent, ts);
//		assertTrue(agentMgr.isAgentId(agentUri, ts));
//		agent = agentMgr.readAgent(agentUri, ts);
//		Literal agentLit = null;
//		Literal authorOtherIdLiteral = null;
//		try {
//			agentLit = ts.getValueFactory().createLiteral(pAgentStr);
//			authorOtherIdLiteral = ts.getValueFactory().createLiteral("http://ieee.org/author-id02");
//		} catch (RepositoryException e1) {
//			fail();;
//		}
//		Literal authorNameLiteral= null;
//		Statement crStmt =null;
//		try {
//			authorNameLiteral = ts.getValueFactory().createLiteral("name 03");
//			crStmt = ts.getValueFactory().createStatement(doiURI, DCTERMS.CREATOR, agentUri);
//		} catch (RepositoryException e1) {
//			e1.printStackTrace();
//			fail();
//		}
//		Model model = new LinkedHashModel();
//		try {
//			model.add(ts.getValueFactory().createStatement(agentUri, RDF.TYPE, FOAF.PERSON));
//			model.add(ts.getValueFactory().createStatement(agentUri, DCTERMS.IDENTIFIER, agentLit));
//			model.add(ts.getValueFactory().createStatement(agentUri, DCTERMS.IDENTIFIER, authorOtherIdLiteral));
//			model.add(ts.getValueFactory().createStatement(agentUri, FOAF.NAME, authorNameLiteral));
//			model.add(ts.getValueFactory().createStatement(agentUri, FOAF.MBOX, mboxURI));
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		agentMgr.createAgentandProfileFromAgentURI(agentUri, crStmt, toBeAddedStmts, 
//				toBeDeletedStmts, newObjects, model, systemAgentURI, profilemgr, identitymgr, ts);
//		assertEquals(3,newObjects.size());
//		assertEquals(19, toBeAddedStmts.size());
//		assertEquals(5, toBeDeletedStmts.size());
//	}

//	/**
//	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#createAgentandProfileFromProfileURI(org.openrdf.model.URI, Statement, java.util.List, java.util.List, java.util.List, org.openrdf.model.Model, org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.ORMapProfileMgr, ORMapIdentityMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
//	 */
//	@Test
//	public void testCreateAgentandProfileFromProfileURI() {
//		String pAgentStr = "http://orcid.org/0000-0000-0000-0003";
//		java.net.URI jUri = null;
//		try {
//			jUri = new java.net.URI(pAgentStr);
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//			fail();
//		}
//		URI agentUri = ORAdapter.uri2OpenRdfUri(jUri);
//		ORMapAgent pAgent = new ORMapAgent(agentUri,systemAgentURI);
//		agentUri = agentMgr.createAgent(pAgent, ts);
//		assertTrue(agentMgr.isAgentId(agentUri, ts));
//		pAgent = agentMgr.readAgent(agentUri, ts);
//		ORMapProfile profile = profilemgr.createProfileObject(agentUri, systemAgentURI, null);
//		URI profileUri = profilemgr.createProfile(profile, ts);
//		assertTrue(profilemgr.isProfileId(profileUri, ts));
//		profile = profilemgr.readProfile(profileUri, ts);
//		Model model = new LinkedHashModel();
//		Statement crStmt= null;
//		Literal authorOtherIdLiteral = null;
//		try {
//			crStmt = ts.getValueFactory().createStatement(doiURI, DCTERMS.CREATOR, profileUri);
//			authorOtherIdLiteral = ts.getValueFactory().createLiteral("http://ieee.org/author-id03");
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		// first try with no change to profile
//		agentMgr.createAgentandProfileFromProfileURI(profileUri, crStmt, toBeAddedStmts, toBeDeletedStmts, newObjects, 
//				model, systemAgentURI, profilemgr, identitymgr, ts);
//		assertEquals(0,newObjects.size());
//		assertEquals(5, toBeAddedStmts.size());
//		assertEquals(0, toBeDeletedStmts.size());
//		// now try with changes to profile
//		Literal authorNameLiteral= null;
//		try {
//			authorNameLiteral = ts.getValueFactory().createLiteral("name 04");
//		} catch (RepositoryException e1) {
//			e1.printStackTrace();
//			fail();
//		}
//		try {
//			model.add(ts.getValueFactory().createStatement(profileUri, RDF.TYPE, FOAF.PERSON));
//			model.add(ts.getValueFactory().createStatement(profileUri, DCTERMS.IDENTIFIER, agentUri));
//			model.add(ts.getValueFactory().createStatement(profileUri, DCTERMS.IDENTIFIER, authorOtherIdLiteral));
//			model.add(ts.getValueFactory().createStatement(profileUri, FOAF.NAME, authorNameLiteral));
//			model.add(ts.getValueFactory().createStatement(profileUri, FOAF.MBOX, mboxURI));
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		newObjects.clear();
//		toBeAddedStmts.clear();
//		toBeDeletedStmts.clear();
//		agentMgr.createAgentandProfileFromProfileURI(profileUri, crStmt, toBeAddedStmts, toBeDeletedStmts, newObjects, 
//				model, systemAgentURI, profilemgr, identitymgr, ts);
//		assertEquals(3,newObjects.size());
//		assertEquals(20, toBeAddedStmts.size());
//		assertEquals(6, toBeDeletedStmts.size());
//	}

//	/**
//	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#createAgentandProfileFromProfileIdentityURI(org.openrdf.model.URI, Statement, java.util.List, java.util.List, java.util.List, org.openrdf.model.Model, org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.ORMapProfileMgr, ORMapIdentityMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
//	 */
//	@Test
//	public void testCreateAgentandProfileFromProfileIdentityURI() {
//		String pAgentStr = "http://orcid.org/0000-0000-0000-0001";
//		java.net.URI jUri = null;
//		try {
//			jUri = new java.net.URI(pAgentStr);
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//			fail();
//		}
//		URI agentUri = ORAdapter.uri2OpenRdfUri(jUri);
//		ORMapAgent pAgent = new ORMapAgent(agentUri,systemAgentURI);
//		agentUri = agentMgr.createAgent(pAgent, ts);
//		assertTrue(agentMgr.isAgentId(agentUri, ts));
//		pAgent = agentMgr.readAgent(agentUri, ts);
//	
//		java.net.URI idJUri = null;
//		URI idUri;
//		try {
//			idJUri = new java.net.URI("http://www.portico.org");
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail();
//		}
//		idUri = ORAdapter.uri2OpenRdfUri(idJUri);
//		ORMapIdentity identity = new ORMapIdentity(idUri,systemAgentURI);
//		idUri = identitymgr.createIdentity(identity, ts);
//		assertTrue(identitymgr.isIdentityId(idUri, ts));
//		identity = identitymgr.read(idUri, ts);
//		
//		ORMapProfile profile = profilemgr.createProfileObject(agentUri, systemAgentURI, idUri);
//		URI profileUri = profilemgr.createProfile(profile, ts);
//		assertTrue(profilemgr.isProfileId(profileUri, ts));
//		profile = profilemgr.readProfile(profileUri, ts);
//		
//		Statement crStmt= null;
//		try {
//			crStmt = ts.getValueFactory().createStatement(doiURI, DCTERMS.CREATOR, idUri);
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		
//		Model model = new LinkedHashModel();
//		try {
//			model.add(ts.getValueFactory().createStatement(idUri, RDF.TYPE, FOAF.ORGANIZATION));
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		agentMgr.createAgentandProfileFromProfileIdentityURI(idUri, crStmt, toBeAddedStmts,
//				toBeDeletedStmts, newObjects, model, systemAgentURI, profilemgr, identitymgr, ts);
//		assertEquals(1,newObjects.size());
//		assertEquals(11, toBeAddedStmts.size());
//		assertEquals(2, toBeDeletedStmts.size());
//	}
	
//	/**
//	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#createAgentandProfileFromNewURI(org.openrdf.model.URI, java.util.List, java.util.List, java.util.List, org.openrdf.model.Model, org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.ORMapProfileMgr, ORMapIdentityMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
//	 */
//	@Test
//	public void testCreateAgentandProfileFromNewURI() {
//		String authorOrcId = "http://orcid.org/9999-9999-9999-9999";
//		URI authorOrcURI = null;
//		Literal authorOrcLiteral = null;
//		Literal authorOtherIdLiteral = null;
//		Literal authorNameLiteral = null;
//		try {
//			authorOrcURI = ts.getValueFactory().createURI(authorOrcId);
//			authorOrcLiteral = ts.getValueFactory().createLiteral(authorOrcId);
//			authorOtherIdLiteral = ts.getValueFactory().createLiteral("http://ieee.org/author-id04");
//			authorNameLiteral = ts.getValueFactory().createLiteral("name 05");
//		} catch (RepositoryException e1) {
//			e1.printStackTrace();
//			fail();
//		}
//		
//		assertFalse(agentMgr.isAgentId(authorOrcURI, ts));
//		assertFalse(agentMgr.isProfileId(authorOrcURI, ts));
//		assertFalse(agentMgr.isIdentityId(authorOrcURI, ts));
//		assertFalse(identitymgr.isLocalPartUri(authorOrcURI, ts));
//		
//		
//		Model model = new LinkedHashModel();
//		try {
//			model.add(ts.getValueFactory().createStatement(authorOrcURI, RDF.TYPE, FOAF.PERSON));
//			model.add(ts.getValueFactory().createStatement(authorOrcURI, DCTERMS.IDENTIFIER, authorOrcLiteral));
//			model.add(ts.getValueFactory().createStatement(authorOrcURI, DCTERMS.IDENTIFIER, authorOtherIdLiteral));
//			model.add(ts.getValueFactory().createStatement(authorOrcURI, FOAF.NAME, authorNameLiteral));
//			model.add(ts.getValueFactory().createStatement(authorOrcURI, FOAF.MBOX, mboxURI));
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		agentMgr.createAgentandProfileFromNewURI(authorOrcURI, toBeAddedStmts, toBeDeletedStmts, newObjects, 
//				model, systemAgentURI, profilemgr, identitymgr, ts);
//		assertEquals(4,newObjects.size());
//		assertEquals(19, toBeAddedStmts.size());
//		assertEquals(5, toBeDeletedStmts.size());
//	}

//	/**
//	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#createAgentandProfileFromBnode(org.openrdf.model.BNode, org.openrdf.model.Statement, java.util.List, java.util.List, java.util.List, org.openrdf.model.Model, org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.ORMapProfileMgr, ORMapIdentityMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
//	 */
//	@Test
//	public void testCreateAgentandProfileFromBnode() {
//		BNode bnode = null;
//		try {
//			bnode = ts.getValueFactory().createBNode();
//		} catch (RepositoryException e1) {
//			e1.printStackTrace();
//			fail();
//		}
//		URI predicate = DCTERMS.CREATOR;
//		URI subject = doiURI;		
//		Statement crStmt = null;
//		Literal authorOtherIdLiteral = null;
//		try {
//			crStmt = ts.getValueFactory().createStatement(subject, predicate, bnode);
//			authorOtherIdLiteral = ts.getValueFactory().createLiteral("http://ieee.org/author-id05");
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		Model model = new LinkedHashModel();
//		Literal authorNameLiteral = null;
//		String authorOrcId = "http://orcid.org/8888-8888-8888-8888";
//		Literal authorOrcLiteral = null;
//		try {
//			authorNameLiteral = ts.getValueFactory().createLiteral("name 06");
//			authorOrcLiteral = ts.getValueFactory().createLiteral(authorOrcId);
//		} catch (RepositoryException e1) {
//			e1.printStackTrace();
//			fail();
//		}
//		try {
//			model.add(ts.getValueFactory().createStatement(bnode, RDF.TYPE, FOAF.PERSON));
//			model.add(ts.getValueFactory().createStatement(bnode, DCTERMS.IDENTIFIER, authorOrcLiteral));
//			model.add(ts.getValueFactory().createStatement(bnode, DCTERMS.IDENTIFIER, authorOtherIdLiteral));
//			model.add(ts.getValueFactory().createStatement(bnode, FOAF.NAME, authorNameLiteral));
//			model.add(ts.getValueFactory().createStatement(bnode, FOAF.MBOX, mboxURI));
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		agentMgr.createAgentandProfileFromBnode(bnode, crStmt, 
//				toBeAddedStmts, toBeDeletedStmts, newObjects, 
//				model, systemAgentURI, profilemgr, identitymgr, ts);
//		assertEquals(4,newObjects.size());
//		assertEquals(20, toBeAddedStmts.size());
//		assertEquals(6, toBeDeletedStmts.size());
//	}

//	/**
//	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#createAgentandProfileFromLiteral(org.openrdf.model.Literal, org.openrdf.model.Statement, java.util.List, java.util.List, java.util.List, org.openrdf.model.Model, org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.ORMapProfileMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
//	 */
//	@Test
//	public void testCreateAgentandProfileFromLiteral() {		
//		URI predicate = DC.CREATOR;
//		URI subject = doiURI;		
//		Statement crStmt = null;
//		Literal authorNameLiteral = null;
//		try {
//			authorNameLiteral = ts.getValueFactory().createLiteral("name 07");
//			crStmt = ts.getValueFactory().createStatement(subject, predicate, authorNameLiteral);
//			agentMgr.createAgentandProfileFromLiteral(authorNameLiteral, crStmt, toBeAddedStmts,
//					toBeDeletedStmts, newObjects, systemAgentURI, profilemgr, ts);
//			assertEquals(2, newObjects.size());
//			assertEquals(7, toBeAddedStmts.size());
//			assertEquals(1, toBeDeletedStmts.size());
//			assertTrue(toBeDeletedStmts.contains(crStmt));
//			Model model = new LinkedHashModel();
//			model.addAll(toBeAddedStmts);
//			Model predicates = model.filter(null, FOAF.NAME, null);
//			assertEquals (1, predicates.size());
//			for (Statement stmt:predicates){
//				Value object = stmt.getObject();
//				assertEquals(authorNameLiteral.stringValue(), object.stringValue());
//			}
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//			fail();
//		}
//		
//	}

}

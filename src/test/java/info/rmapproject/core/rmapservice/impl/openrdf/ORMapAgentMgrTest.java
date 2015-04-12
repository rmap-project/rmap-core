/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORMapIdentity;
import info.rmapproject.core.model.impl.openrdf.ORMapProfile;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;

/**
 * @author smorrissey
 *
 */
public class ORMapAgentMgrTest {

	protected String systemAgentId = "http://orcid.org/0000-0003-2069-1219";
	protected ORMapAgentMgr agentMgr;
	protected ORMapProfileMgr profilemgr;
	protected ORMapIdentityMgr identitymgr;
	protected SesameTriplestore ts;
	protected URI systemAgentURI;
	protected String doi = "DOI:10.1080/13614576.2014.883935";
	protected URI doiURI;
	protected String authorOrcId = "http://orcid.org/0000-0000-0000-0000";
	protected URI authorOrcURI;
	protected Literal authorOrcLiteral;
	protected String authorOtherId = "http://ieee.org/author-id";
	protected URI authorOtherURI;
	protected Literal authorOtherIdLiteral;
	protected Literal authorNameLiteral;
	protected String authorName = "Mary Smith";
	protected String mbox = "mailto:mary.smith@example.org";
	protected URI mboxURI;
	protected List<Statement> toBeAddedStmts;
	protected List<Statement> toBeDeletedStmts;
	protected List<URI> newObjects;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		agentMgr = new ORMapAgentMgr();
		profilemgr = new ORMapProfileMgr();
		identitymgr = new ORMapIdentityMgr();
		ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
		systemAgentURI = ts.getValueFactory().createURI(systemAgentId);
		doiURI = ts.getValueFactory().createURI(doi);
		authorOrcURI = ts.getValueFactory().createURI(authorOrcId);
		authorOrcLiteral = ts.getValueFactory().createLiteral(authorOrcId);
		authorOtherURI = ts.getValueFactory().createURI(authorOtherId);	
		authorOtherIdLiteral = ts.getValueFactory().createLiteral(authorOtherId);
		authorNameLiteral = ts.getValueFactory().createLiteral(authorName);
		mboxURI = ts.getValueFactory().createURI(mbox);
		toBeAddedStmts = new ArrayList<Statement>();
		toBeDeletedStmts = new ArrayList<Statement>();
		newObjects = new ArrayList<URI>();
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#readAgent(org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testReadAgent() {
		ORMapAgent systemAgent = agentMgr.createAgent(systemAgentURI, systemAgentURI, ts);
		ORMapAgent readAgent = null;
		try {
			readAgent = agentMgr.readAgent(systemAgentURI, ts);
			assertEquals(systemAgent.getId(),readAgent.getId());
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

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#createAgentAndProfiles(org.openrdf.model.URI, org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.ORMapProfileMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testCreateAgentAndProfiles() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#createRelatedStatementsAgents(java.util.List, org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.ORMapProfileMgr, ORMapIdentityMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testCreateRelatedStatementsAgents() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#createAgentandProfileFromURI(org.openrdf.model.URI, Statement, java.util.List, java.util.List, java.util.List, org.openrdf.model.Model, org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.ORMapProfileMgr, ORMapIdentityMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testCreateAgentandProfileFromURI() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#createAgentandProfileFromAgentURI(org.openrdf.model.URI, java.util.List, java.util.List, java.util.List, org.openrdf.model.Model, org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.ORMapProfileMgr, ORMapIdentityMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testCreateAgentandProfileFromAgentURI() {
		String pAgentStr = "http://orcid.org/0000-0000-0000-0002";
		java.net.URI jUri = null;
		try {
			jUri = new java.net.URI(pAgentStr);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		URI agentUri = ORAdapter.uri2OpenRdfUri(jUri);
		assertFalse (agentMgr.isAgentId(agentUri, ts));
		ORMapAgent agent = new ORMapAgent(agentUri,systemAgentURI);
		agentUri = agentMgr.createAgent(agent, ts);
		assertTrue(agentMgr.isAgentId(agentUri, ts));
		agent = agentMgr.readAgent(agentUri, ts);
		Literal agentLit = null;
		try {
			agentLit = ts.getValueFactory().createLiteral(pAgentStr);
		} catch (RepositoryException e1) {
			fail();;
		}
		
		Model model = new LinkedHashModel();
		try {
			model.add(ts.getValueFactory().createStatement(agentUri, RDF.TYPE, FOAF.PERSON));
			model.add(ts.getValueFactory().createStatement(agentUri, DCTERMS.IDENTIFIER, agentLit));
			model.add(ts.getValueFactory().createStatement(agentUri, DCTERMS.IDENTIFIER, authorOtherIdLiteral));
			model.add(ts.getValueFactory().createStatement(agentUri, FOAF.NAME, authorNameLiteral));
			model.add(ts.getValueFactory().createStatement(agentUri, FOAF.MBOX, mboxURI));
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail();
		}
		agentMgr.createAgentandProfileFromAgentURI(agentUri, toBeAddedStmts, toBeDeletedStmts, 
				newObjects, model, systemAgentURI, profilemgr, identitymgr, ts);
		assertEquals(3,newObjects.size());
		assertEquals(19, toBeAddedStmts.size());
		assertEquals(5, toBeDeletedStmts.size());
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#createAgentandProfileFromProfileURI(org.openrdf.model.URI, Statement, java.util.List, java.util.List, java.util.List, org.openrdf.model.Model, org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.ORMapProfileMgr, ORMapIdentityMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testCreateAgentandProfileFromProfileURI() {
		String pAgentStr = "http://orcid.org/0000-0000-0000-0003";
		java.net.URI jUri = null;
		try {
			jUri = new java.net.URI(pAgentStr);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		URI agentUri = ORAdapter.uri2OpenRdfUri(jUri);
		ORMapAgent pAgent = new ORMapAgent(agentUri,systemAgentURI);
		agentUri = agentMgr.createAgent(pAgent, ts);
		assertTrue(agentMgr.isAgentId(agentUri, ts));
		pAgent = agentMgr.readAgent(agentUri, ts);
		ORMapProfile profile = profilemgr.createProfileObject(agentUri, systemAgentURI, null);
		URI profileUri = profilemgr.createProfile(profile, ts);
		assertTrue(profilemgr.isProfileId(profileUri, ts));
		profile = profilemgr.readProfile(profileUri, ts);
		Model model = new LinkedHashModel();
		Statement crStmt= null;
		try {
			crStmt = ts.getValueFactory().createStatement(profileUri, DCTERMS.CREATOR, profileUri);
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail();
		}
		// first try with no change to profile
		agentMgr.createAgentandProfileFromProfileURI(profileUri, crStmt, toBeAddedStmts, toBeDeletedStmts, newObjects, 
				model, systemAgentURI, profilemgr, identitymgr, ts);
		assertEquals(0,newObjects.size());
		assertEquals(5, toBeAddedStmts.size());
		assertEquals(0, toBeDeletedStmts.size());
		// now try with changes to profile
		try {
			model.add(ts.getValueFactory().createStatement(profileUri, RDF.TYPE, FOAF.PERSON));
			model.add(ts.getValueFactory().createStatement(profileUri, DCTERMS.IDENTIFIER, agentUri));
			model.add(ts.getValueFactory().createStatement(profileUri, DCTERMS.IDENTIFIER, authorOtherIdLiteral));
			model.add(ts.getValueFactory().createStatement(profileUri, FOAF.NAME, authorNameLiteral));
			model.add(ts.getValueFactory().createStatement(profileUri, FOAF.MBOX, mboxURI));
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail();
		}
		newObjects.clear();
		toBeAddedStmts.clear();
		toBeDeletedStmts.clear();
		agentMgr.createAgentandProfileFromProfileURI(profileUri, crStmt, toBeAddedStmts, toBeDeletedStmts, newObjects, 
				model, systemAgentURI, profilemgr, identitymgr, ts);
		assertEquals(3,newObjects.size());
		assertEquals(20, toBeAddedStmts.size());
		assertEquals(6, toBeDeletedStmts.size());
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#createAgentandProfileFromProfileIdentityURI(org.openrdf.model.URI, java.util.List, java.util.List, java.util.List, org.openrdf.model.Model, org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.ORMapProfileMgr, ORMapIdentityMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testCreateAgentandProfileFromProfileIdentityURI() {
		String pAgentStr = "http://orcid.org/0000-0000-0000-0001";
		java.net.URI jUri = null;
		try {
			jUri = new java.net.URI(pAgentStr);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		URI agentUri = ORAdapter.uri2OpenRdfUri(jUri);
		ORMapAgent pAgent = new ORMapAgent(agentUri,systemAgentURI);
		agentUri = agentMgr.createAgent(pAgent, ts);
		assertTrue(agentMgr.isAgentId(agentUri, ts));
		pAgent = agentMgr.readAgent(agentUri, ts);
	
		java.net.URI idJUri = null;
		URI idUri;
		try {
			idJUri = new java.net.URI("http://www.portico.org");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		idUri = ORAdapter.uri2OpenRdfUri(idJUri);
		ORMapIdentity identity = new ORMapIdentity(idUri,systemAgentURI);
		idUri = identitymgr.createIdentity(identity, ts);
		assertTrue(identitymgr.isIdentityId(idUri, ts));
		identity = identitymgr.read(idUri, ts);
		
		ORMapProfile profile = profilemgr.createProfileObject(agentUri, systemAgentURI, idUri);
		URI profileUri = profilemgr.createProfile(profile, ts);
		assertTrue(profilemgr.isProfileId(profileUri, ts));
		profile = profilemgr.readProfile(profileUri, ts);
		
		Model model = new LinkedHashModel();
		try {
			model.add(ts.getValueFactory().createStatement(idUri, RDF.TYPE, FOAF.ORGANIZATION));
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail();
		}
		agentMgr.createAgentandProfileFromProfileIdentityURI(idUri, toBeAddedStmts, toBeDeletedStmts,
				newObjects, model, systemAgentURI, profilemgr, identitymgr, ts);
		assertEquals(1,newObjects.size());
		assertEquals(10, toBeAddedStmts.size());
		assertEquals(1, toBeDeletedStmts.size());
	}
	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#createAgentandProfileFromNewURI(org.openrdf.model.URI, java.util.List, java.util.List, java.util.List, org.openrdf.model.Model, org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.ORMapProfileMgr, ORMapIdentityMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testCreateAgentandProfileFromNewURI() {
		assertFalse(agentMgr.isAgentId(authorOrcURI, ts));
		assertFalse(agentMgr.isProfileId(authorOrcURI, ts));
		assertFalse(agentMgr.isIdentityId(authorOrcURI, ts));
		assertFalse(identitymgr.isLocalPartUri(authorOrcURI, ts));
		
		Model model = new LinkedHashModel();
		try {
			model.add(ts.getValueFactory().createStatement(authorOrcURI, RDF.TYPE, FOAF.PERSON));
			model.add(ts.getValueFactory().createStatement(authorOrcURI, DCTERMS.IDENTIFIER, authorOrcLiteral));
			model.add(ts.getValueFactory().createStatement(authorOrcURI, DCTERMS.IDENTIFIER, authorOtherIdLiteral));
			model.add(ts.getValueFactory().createStatement(authorOrcURI, FOAF.NAME, authorNameLiteral));
			model.add(ts.getValueFactory().createStatement(authorOrcURI, FOAF.MBOX, mboxURI));
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail();
		}
		agentMgr.createAgentandProfileFromNewURI(authorOrcURI, toBeAddedStmts, toBeDeletedStmts, newObjects, 
				model, systemAgentURI, profilemgr, identitymgr, ts);
		assertEquals(4,newObjects.size());
		assertEquals(19, toBeAddedStmts.size());
		assertEquals(5, toBeDeletedStmts.size());
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#createAgentandProfileFromBnode(org.openrdf.model.BNode, org.openrdf.model.Statement, java.util.List, java.util.List, java.util.List, org.openrdf.model.Model, org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.ORMapProfileMgr, ORMapIdentityMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testCreateAgentandProfileFromBnode() {
		BNode bnode = null;
		try {
			bnode = ts.getValueFactory().createBNode();
		} catch (RepositoryException e1) {
			e1.printStackTrace();
			fail();
		}
		URI predicate = DCTERMS.CREATOR;
		URI subject = doiURI;		
		Statement crStmt = null;
		try {
			crStmt = ts.getValueFactory().createStatement(subject, predicate, bnode);
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail();
		}
		Model model = new LinkedHashModel();
		try {
			model.add(ts.getValueFactory().createStatement(bnode, RDF.TYPE, FOAF.PERSON));
			model.add(ts.getValueFactory().createStatement(bnode, DCTERMS.IDENTIFIER, authorOrcLiteral));
			model.add(ts.getValueFactory().createStatement(bnode, DCTERMS.IDENTIFIER, authorOtherIdLiteral));
			model.add(ts.getValueFactory().createStatement(bnode, FOAF.NAME, authorNameLiteral));
			model.add(ts.getValueFactory().createStatement(bnode, FOAF.MBOX, mboxURI));
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail();
		}
		agentMgr.createAgentandProfileFromBnode(bnode, crStmt, 
				toBeAddedStmts, toBeDeletedStmts, newObjects, 
				model, systemAgentURI, profilemgr, identitymgr, ts);
		assertEquals(4,newObjects.size());
		assertEquals(20, toBeAddedStmts.size());
		assertEquals(6, toBeDeletedStmts.size());
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#createAgentandProfileFromLiteral(org.openrdf.model.Literal, org.openrdf.model.Statement, java.util.List, java.util.List, java.util.List, org.openrdf.model.Model, org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.ORMapProfileMgr, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testCreateAgentandProfileFromLiteral() {		
		URI predicate = DC.CREATOR;
		URI subject = doiURI;		
		Statement crStmt = null;
		try {
			crStmt = ts.getValueFactory().createStatement(subject, predicate, authorNameLiteral);
		} catch (RepositoryException e) {
			e.printStackTrace();
			fail();
		}
		agentMgr.createAgentandProfileFromLiteral(authorNameLiteral, crStmt, toBeAddedStmts,
				toBeDeletedStmts, newObjects, systemAgentURI, profilemgr, ts);
		assertEquals(2, newObjects.size());
		assertEquals(7, toBeAddedStmts.size());
		assertEquals(1, toBeDeletedStmts.size());
		assertTrue(toBeDeletedStmts.contains(crStmt));
		Model model = new LinkedHashModel();
		model.addAll(toBeAddedStmts);
		Model predicates = model.filter(null, FOAF.NAME, null);
		assertEquals (1, predicates.size());
		for (Statement stmt:predicates){
			Value object = stmt.getObject();
			assertEquals(authorNameLiteral.stringValue(), object.stringValue());
		}
	}

}

/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import static org.junit.Assert.*;

import java.util.List;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;

/**
 * @author smorrissey
 *
 */
public class ORMapObjectMgrTest {

	protected SesameTriplestore ts = null;
	ValueFactory vf = null;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		try {
			ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			vf = ts.getValueFactory();
		} catch (Exception e) {
			throw new RMapException("Unable to create Sesame TripleStore: ", e);
		}
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapObjectMgr#createTriple(info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore, org.openrdf.model.Statement)}.
	 */
	@Test
	public void testCreateTriple() {
		java.net.URI id1 =null;
		try {
			id1 = IdServiceFactoryIOC.getFactory().createService().createId();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		URI subject = ORAdapter.uri2OpenRdfUri(id1);
		URI predicate = RDF.TYPE;
		URI object = RMAP.STATEMENT;
		ORMapStatementMgr mgr = new ORMapStatementMgr();
		String contextString = mgr.createContextURIString(subject.stringValue(),
				predicate.stringValue(), object.stringValue());
		URI context = ORAdapter.getValueFactory().createURI(contextString);
		Statement stmt = vf.createStatement(subject, predicate, object,context);	
		mgr.createTriple(ts, stmt);
		Statement gStmt = null;
		try {
			gStmt = ts.getStatement(subject, predicate, object, context);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(gStmt);
		assertEquals(subject, gStmt.getSubject());
		assertEquals(predicate, gStmt.getPredicate());
		assertEquals(object, gStmt.getObject());
		assertEquals(context, gStmt.getContext());
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapObjectMgr#isRMapType(info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore, org.openrdf.model.URI, org.openrdf.model.URI)}.
	 */
	@Test
	public void testIsRMapType() {
		ORMapStatementMgr mgr = new ORMapStatementMgr();
		java.net.URI id1 =null;
		try {
			id1 = IdServiceFactoryIOC.getFactory().createService().createId();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		URI subject = ORAdapter.uri2OpenRdfUri(id1);
		URI predicate = RDF.TYPE;
		Value object = RMAP.STATEMENT;
		Statement stmt = null;
		try {
			stmt = ts.getValueFactory().createStatement(subject, predicate, object);
			ts.addStatement(stmt);
		} catch (RepositoryException e1) {
			e1.printStackTrace();
			fail(e1.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		try {
			Statement stmt2 = ts.getStatement(subject, predicate, object);
			assertNotNull(stmt2);
			assertEquals(stmt.getSubject(),stmt2.getSubject());
			assertEquals(stmt.getPredicate(), stmt2.getPredicate());
			assertEquals(stmt.getObject(), stmt2.getObject());
			assertEquals(stmt.getContext(), stmt2.getContext());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
		boolean istype = mgr.isRMapType(ts, subject, RMAP.STATEMENT);
		assertTrue(istype);
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapObjectMgr#isStatementId(org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testIsStatementId() {		
		ORMapStatementMgr mgr = new ORMapStatementMgr();
		URI subject = vf.createURI("http://portico.org");
		URI predicate = DCTERMS.CREATOR;
		Value object = vf.createLiteral("this test");
		Statement stmt = vf.createStatement(subject, predicate, object);
		URI stmtId = mgr.createReifiedStatement(stmt,ts);
		List<Statement>stmts = null;
		try {
			stmts = ts.getStatements(stmtId, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertNotNull(stmts);
		assertEquals(4,stmts.size());
		boolean foundit=false;
		boolean foundId = false;
		boolean foundObj = false;
		for (Statement st:stmts){
			URI pred = st.getPredicate();
			if (!pred.equals(RDF.TYPE)){
				continue;
			}
			URI subj = (URI)st.getSubject();
			if (!subj.equals(stmtId)){
				continue;
			}
			foundId = true;
			URI obj = (URI)st.getObject();
			if (! obj.equals(RMAP.STATEMENT)){
				continue;
			}
			foundObj = true;
			foundit = true;
		}
		assertTrue(foundId);
		assertTrue(foundObj);
		assertTrue(foundit);
		boolean isStmtId = mgr.isStatementId(stmtId,ts);
		assertTrue(isStmtId);
	}


	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapObjectMgr#isDiscoId(org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testIsDiscoId(){
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapObjectMgr#isEventId(org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testIsEventId() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapObjectMgr#isAgentId(org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testIsAgentId() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapObjectMgr#getNamedGraph(org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testGetNamedGraph() {
		fail("Not yet implemented");
	}

}

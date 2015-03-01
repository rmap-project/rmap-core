/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import static org.junit.Assert.*;

import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapStatementBag;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.rmapservice.impl.openrdf.ORMapStatementMgr;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;


/**
 * @author smorrissey
 *
 */
public class ORMapDiscoTest {
	ValueFactory vf = null;
	protected SesameTriplestore ts = null;
	Statement rStmt;
	Statement rStmt2;
	Statement s1;
	Statement s2;
	Statement s3;
	Statement s4;
	List<Statement> relatedStmts;
	URI r;
	URI r2;
	Literal a;
	URI b;
	URI c;
	URI d;
	
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
		relatedStmts.add(s1);
		relatedStmts.add(s2);
		relatedStmts.add(s3);
		relatedStmts.add(s4);
	}


	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#ORMapDiSCO(info.rmapproject.core.model.RMapResource, java.util.List)}.
	 */
	@Test
	public void testORMapDiSCORMapResourceListOfURI() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#ORMapDiSCO(info.rmapproject.core.model.RMapResource, java.util.List, RMapLiteral, info.rmapproject.core.model.RMapStatementBag)}.
	 */
	@Test
	public void testORMapDiSCORMapResourceListOfURIRMapResourceRMapStatementBag() {
		fail("Not yet implemented");
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
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#getAggregratedResources()}.
	 */
	@Test
	public void testGetAggregratedResources() {
		
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
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#getRelatedStatementsAsList()}.
	 */
	@Test
	public void testGetRelatedStatementsAsStatements() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#setRelatedStatements(info.rmapproject.core.model.RMapStatementBag)}.
	 */
	@Test
	public void testSetRelatedStatements() {
		ORMapDiSCO disco = new ORMapDiSCO();
		Statement rStmt = vf.createStatement(disco.discoContext, RMAP.AGGREGATES, r,disco.discoContext);
		Statement rStmt2 = vf.createStatement(disco.discoContext, RMAP.AGGREGATES, r2,disco.discoContext);
		disco.aggregatedResources = new ArrayList<Statement>();
		disco.aggregatedResources.add(rStmt);
		disco.aggregatedResources.add(rStmt2);
		RMapStatementBag bag = new RMapStatementBag();
		List<Object>oList = new ArrayList<Object>();

		java.net.URI id1 =null, id2=null;
		try {
			id1 = IdServiceFactoryIOC.getFactory().createService().createId();
			id2  = IdServiceFactoryIOC.getFactory().createService().createId(); 
			assertFalse(id1.equals(id2));
		} catch (Exception e) {
			fail("could not create id");
		}
		URI rid1= ORAdapter.uri2OpenRdfUri(id1);
		URI rid2= ORAdapter.uri2OpenRdfUri(id2);
		ORMapStatementMgr smgr = new ORMapStatementMgr();
		String context1 = smgr.createContextURIString(s1);
		String context2= smgr.createContextURIString(s2);
		Statement subject1 = vf.createStatement(rid1, RDF.SUBJECT, s1.getSubject(), vf.createURI(context1));
		Statement subject2 = vf.createStatement(rid2, RDF.SUBJECT, s2.getSubject(), vf.createURI(context2));
		Statement predicate1 = vf.createStatement(rid1, RDF.PREDICATE, s1.getPredicate(), vf.createURI(context1));
		Statement predicate2 = vf.createStatement(rid2, RDF.PREDICATE, s2.getPredicate(), vf.createURI(context2));
		Statement object1 = vf.createStatement(rid1, RDF.OBJECT, s1.getObject(), vf.createURI(context1));
		Statement object2 = vf.createStatement(rid2, RDF.OBJECT, s2.getObject(), vf.createURI(context2));		
		ORMapStatement ors1 = new ORMapStatement(subject1,predicate1,object1);
		ORMapStatement ors2= new ORMapStatement(subject2,predicate2,object2);
		oList.add(ors1);
		oList.add(ors2);
		bag.addAll(oList);
		// put statements in triplestore
		Statement t1 = vf.createStatement(rid1, RDF.TYPE, RMAP.STATEMENT, vf.createURI(context1));
		Statement t2 = vf.createStatement(rid2, RDF.TYPE, RMAP.STATEMENT, vf.createURI(context2));
		try {
			ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			ts.addStatement(subject1);
			ts.addStatement(predicate1);
			ts.addStatement(object1);
			ts.addStatement(t1);
			ts.addStatement(subject2);
			ts.addStatement(predicate2);
			ts.addStatement(object2);
			ts.addStatement(t2);
			ts.commitTransaction();
		} catch (Exception e) {
			fail("Unable to create Sesame TripleStore: ");
		}
		
		disco.setRelatedStatements(bag);
		RMapStatementBag bag2 = disco.getRelatedStatements();
		Object[] objects = bag2.getContents();
		assertEquals(2, objects.length);
		List<Statement>relStmts = disco.getRelatedStatementsAsList();
		assertEquals(2,relStmts.size());
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#getCreator()}.
	 */
	@Test
	public void testGetCreator() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#getCreatorStmt()}.
	 */
	@Test
	public void testGetCreatorStmt() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#setCreator(info.rmapproject.core.model.RMapResource)}.
	 */
	@Test
	public void testSetCreator() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#getDescription()}.
	 */
	@Test
	public void testGetDescription() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#getDescriptonStatement()}.
	 */
	@Test
	public void testGetDescriptonStatement() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#setDescription(RMapValue)}.
	 */
	@Test
	public void testSetDescription() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#getStatus()}.
	 */
	@Test
	public void testGetStatus() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#getRelatedEvents()}.
	 */
	@Test
	public void testGetRelatedEvents() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#getTypeStatement()}.
	 */
	@Test
	public void testGetTypeStatement() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#getDiscoContext()}.
	 */
	@Test
	public void testGetDiscoContext() {
		fail("Not yet implemented");
	}

}

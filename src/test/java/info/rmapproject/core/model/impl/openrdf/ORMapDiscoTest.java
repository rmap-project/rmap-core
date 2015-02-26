/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import static org.junit.Assert.*;

import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;


/**
 * @author smorrissey
 *
 */
public class ORMapDiscoTest {
	ValueFactory vf = null;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		vf = ORAdapter.getValueFactory();
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
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#isConnectedGraph(java.util.List)}.
	 */
	@Test
	public void testIsConnectedGraph() {
		ORMapDiSCO disco = new ORMapDiSCO();
		URI r = vf.createURI("http://rmap-info.org");	
		URI r2 = vf.createURI("https://rmap-project.atlassian.net/wiki/display/RMAPPS/RMap+Wiki");
		Statement rStmt = vf.createStatement(disco.discoContext, RMAP.AGGREGATES, r,disco.discoContext);
		Statement rStmt2 = vf.createStatement(disco.discoContext, RMAP.AGGREGATES, r2,disco.discoContext);
		disco.aggregatedResources = new ArrayList<Statement>();
		disco.aggregatedResources.add(rStmt);
		disco.aggregatedResources.add(rStmt2);
		
		Literal a = vf.createLiteral("a");
		URI b = vf.createURI("http://b.org");
		URI c = vf.createURI("http://c.org");
		URI d = vf.createURI("http://d.org");
		
		List<Statement> relatedStmts = new ArrayList<Statement>();
		//predicates are nonsense here
		// first test connected r->a r->b b->c b->d
		Statement s1 = vf.createStatement(r,RMAP.ACTIVE,a);
		Statement s2 = vf.createStatement(r,RMAP.ACTIVE,b);
		Statement s3 = vf.createStatement(b,RMAP.ACTIVE,c);
		Statement s4 = vf.createStatement(b,RMAP.ACTIVE,d);
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
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#getAggregratedResources()}.
	 */
	@Test
	public void testGetAggregratedResources() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#getAggregatedResourceStatements()}.
	 */
	@Test
	public void testGetAggregatedResourceStatements() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#setAggregratedResources(java.util.List)}.
	 */
	@Test
	public void testSetAggregratedResources() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#getRelatedStatements()}.
	 */
	@Test
	public void testGetRelatedStatements() {
		fail("Not yet implemented");
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
		fail("Not yet implemented");
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

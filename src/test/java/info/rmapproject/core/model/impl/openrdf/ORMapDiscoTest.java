/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import static org.junit.Assert.*;

import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.openrdf.model.Literal;
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
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#ORMapDiSCO()}.
	 */
	@Test
	public void testORMapDiSCO() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#ORMapDiSCO(info.rmapproject.core.model.RMapResource, java.util.List)}.
	 */
	@Test
	public void testORMapDiSCORMapResourceListOfURI() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#ORMapDiSCO(info.rmapproject.core.model.RMapResource, java.util.List, info.rmapproject.core.model.RMapResource, info.rmapproject.core.model.RMapStatementBag)}.
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
		ORMapStatement rStmt = new ORMapStatement(disco.discoContext, RMAP.AGGREGATES, r,disco.discoContext);
		ORMapStatement rStmt2 = new ORMapStatement(disco.discoContext, RMAP.AGGREGATES, r2,disco.discoContext);
		disco.aggregatedResources = new ArrayList<ORMapStatement>();
		disco.aggregatedResources.add(rStmt);
		disco.aggregatedResources.add(rStmt2);
		
		Literal a = vf.createLiteral("a");
		URI b = vf.createURI("http://b.org");
		URI c = vf.createURI("http://c.org");
		URI d = vf.createURI("http://d.org");
		
		List<ORMapStatement> relatedStmts = new ArrayList<ORMapStatement>();
		//predicates are nonsense here
		// first test connected r->a r->b b->c b->d
		ORMapStatement s1 = new ORMapStatement(r,RMAP.ACTIVE,a);
		ORMapStatement s2 = new ORMapStatement(r,RMAP.ACTIVE,b);
		ORMapStatement s3 = new ORMapStatement(b,RMAP.ACTIVE,c);
		ORMapStatement s4 = new ORMapStatement(b,RMAP.ACTIVE,d);
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
		ORMapStatement s5 = new ORMapStatement(r2,RMAP.ACTIVE,c);
		ORMapStatement s6 = new ORMapStatement(c,RMAP.ACTIVE,b);
		ORMapStatement s7 = new ORMapStatement(c,RMAP.ACTIVE,b);
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
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#getRelatedStatementsAsStatements()}.
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
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapDiSCO#setDescription(info.rmapproject.core.model.RMapResource)}.
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

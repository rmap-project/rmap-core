/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import static org.junit.Assert.*;

import java.util.Set;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.ORMapStatementMgr;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;

/**
 * @author smorrissey
 *
 */
public class ORMapStatementTest {

	protected Statement subject;
	protected Statement predicate;
	protected Statement object;
	protected ValueFactory vf = null;
	protected SesameTriplestore ts = null;
	protected URI r;
	protected URI creatorUri;
	protected ORMapStatementMgr mgr;
	protected URI idUri = null;
	protected URI context = null;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		vf = ORAdapter.getValueFactory();
		ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
		mgr = new ORMapStatementMgr();
		r = vf.createURI("http://rmap-info.org");
		java.net.URI id1 = null;
		id1 = IdServiceFactoryIOC.getFactory().createService().createId(); 
		idUri = ORAdapter.uri2OpenRdfUri(id1);	
		creatorUri = vf.createURI("http://orcid.org/0000-0000-0000-0000");
		String subStr = r.stringValue();
		String predStr = (DCTERMS.CREATED).stringValue();
		String objStr = creatorUri.stringValue();
		String contextString = 
				mgr.createContextURIString(subStr, predStr, objStr);
		context = ORAdapter.getValueFactory().createURI(contextString);	
		subject = vf.createStatement(idUri, RDF.SUBJECT, r, context);
		predicate = vf.createStatement(idUri, RDF.PREDICATE, DCTERMS.CREATED, context);
		object = vf.createStatement(idUri, RDF.OBJECT, creatorUri, context);
		
		
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapStatement#getAsModel()}.
	 */
	@Test
	public void testGetAsModel() {
		ORMapStatement orStmt = new ORMapStatement(subject, predicate, object);
		Model model = orStmt.getAsModel();
		assertEquals(4,model.size());
		Set<Resource>subjects = model.subjects();
		assertEquals(1,subjects.size());
		assertTrue(subjects.contains(idUri));
		Set<URI> predicates = model.predicates();
		assertEquals(4, predicates.size());
		assertTrue(predicates.contains(RDF.SUBJECT));
		assertTrue(predicates.contains(RDF.OBJECT));
		assertTrue(predicates.contains(RDF.PREDICATE));
		assertTrue(predicates.contains(RDF.TYPE));
		Set<Value> objects = model.objects();
		assertEquals(4, objects.size());
		assertTrue(objects.contains(r));
		assertTrue(objects.contains(DCTERMS.CREATED));
		assertTrue(objects.contains(creatorUri));
		assertTrue(objects.contains(RMAP.STATEMENT));
		for (Statement stmt:model){
			assertEquals(context,stmt.getContext());
		}
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapStatement#ORMapStatement(org.openrdf.model.Statement, org.openrdf.model.Statement, org.openrdf.model.Statement)}.
	 */
	@Test
	public void testORMapStatementStatementStatementStatement() {
		ORMapStatement orStmt = new ORMapStatement(subject, predicate, object);
		assertEquals(subject,orStmt.getSubjectStatement());
		assertEquals(predicate,orStmt.getPredicateStatement());
		assertEquals(object,orStmt.getObjectStatement());
		java.net.URI tUri = ORAdapter.openRdfUri2URI(RMAP.STATEMENT);
		assertEquals(tUri,orStmt.getType());
		// now break cnstructor by using statements with no context
		subject = vf.createStatement(idUri, RDF.SUBJECT, r);
		predicate = vf.createStatement(idUri, RDF.PREDICATE, DCTERMS.CREATED, context);
		object = vf.createStatement(idUri, RDF.OBJECT, creatorUri, context);
		try {
			 orStmt = new ORMapStatement(subject, predicate, object);
			 fail("should not accept statements without context");
		} catch (RMapException e){}
		subject = vf.createStatement(idUri, RDF.SUBJECT, r, context);
		predicate = vf.createStatement(idUri, RDF.PREDICATE, DCTERMS.CREATED);
		object = vf.createStatement(idUri, RDF.OBJECT, creatorUri, context);
		try {
			 orStmt = new ORMapStatement(subject, predicate, object);
			 fail("should not accept statements without context");
		} catch (RMapException e){}
		subject = vf.createStatement(idUri, RDF.SUBJECT, r, context);
		predicate = vf.createStatement(idUri, RDF.PREDICATE, DCTERMS.CREATED, context);
		object = vf.createStatement(idUri, RDF.OBJECT, creatorUri);
		try {
			 orStmt = new ORMapStatement(subject, predicate, object);
			 fail("should not accept statements without context");
		} catch (RMapException e){}
		// now break with different context
		subject = vf.createStatement(idUri, RDF.SUBJECT, r, context);
		predicate = vf.createStatement(idUri, RDF.PREDICATE, DCTERMS.CREATED, context);
		object = vf.createStatement(idUri, RDF.OBJECT, creatorUri, idUri);
		try {
			 orStmt = new ORMapStatement(subject, predicate, object);
			 fail("should not accept statements different contexts");
		} catch (RMapException e){}
	}

}

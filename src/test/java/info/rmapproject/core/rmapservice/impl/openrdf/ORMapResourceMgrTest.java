package info.rmapproject.core.rmapservice.impl.openrdf;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;



import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;

public class ORMapResourceMgrTest {
	protected SesameTriplestore ts = null;
	protected URI typePred = null;
	protected URI context01 = null;
	protected URI context02 = null;
	protected URI resource01 = null;
	protected URI resource02 = null;
	protected ORMapResourceMgr resourceMgr;

		
	@Before
	public void setUp() throws Exception {
		ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
		typePred = RDF.TYPE;	
		resourceMgr = new ORMapResourceMgr();
		try {
			java.net.URI context = IdServiceFactoryIOC.getFactory().createService().createId();
			context01 = ORAdapter.uri2OpenRdfUri(context);
			resource01 = ORAdapter.uri2OpenRdfUri(context);
			context = IdServiceFactoryIOC.getFactory().createService().createId();
			context02 = ORAdapter.uri2OpenRdfUri(context);
			resource02 = ts.getValueFactory().createURI("http://dx.doi.org/10.1109/ACCESS.2014.2332453");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetResourceRdfTypes() {	
		try {			
			Statement s1 = ts.getValueFactory().createStatement(resource01, typePred, RMAP.DISCO, resource01);
			ts.addStatement(s1);
			Set<URI> uris = resourceMgr.getResourceRdfTypes(resource01, resource01, ts);
			assertNotNull(uris);
			assertEquals(1,uris.size());
			for (URI uri:uris){
				assertEquals(uri.stringValue(), RMAP.DISCO.stringValue());
			}
			uris = resourceMgr.getResourceRdfTypes(resource01, resource02, ts);
			assertNull(uris);
			try {
				uris = resourceMgr.getResourceRdfTypes(resource01, null, ts);
				fail("should have thrownRMapDefectiveArgumentException ");
			}
			catch (RMapDefectiveArgumentException ex) {}
			catch (Exception ex1){
				throw ex1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		
	}

	@Test
	public void testGetResourceRdfTypesAllContexts() {
		URI predJA = null;
		URI predA = null;
		try {	
			predJA = ts.getValueFactory().createURI("http://purl.org/spar/fabio/JournalArticle");
			predA = ts.getValueFactory().createURI("http://purl.org/spar/fabio/Article");
			Statement s1 = ts.getValueFactory().createStatement(resource02, typePred, predJA, resource01);
			ts.addStatement(s1);
			Statement s2 = ts.getValueFactory().createStatement(resource02, typePred, predA, resource02);			
			ts.addStatement(s2);
			Statement s3 = ts.getValueFactory().createStatement(resource02, typePred, predJA, resource02);
			ts.addStatement(s3);
			Map<URI, Set<URI>> map = resourceMgr.getResourceRdfTypesAllContexts(resource02, ts);
			assertNotNull(map);
			assertEquals(2,map.keySet().size());
			assertTrue(map.containsKey(resource01));
			assertTrue(map.containsKey(resource02));
			Set<URI> values = map.get(resource01);
			assertEquals(1, values.size());
			assertTrue(values.contains(predJA));
			assertFalse(values.contains(predA));
			values = map.get(resource02);
			assertEquals(2, values.size());
			assertTrue(values.contains(predJA));
			assertTrue(values.contains(predA));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}		
	}

}

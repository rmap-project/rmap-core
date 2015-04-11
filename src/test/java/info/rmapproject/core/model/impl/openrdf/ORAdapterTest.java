package info.rmapproject.core.model.impl.openrdf;

import static org.junit.Assert.*;

import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.model.RMapBlankNode;
import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.RMapUri;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.sail.memory.model.MemValueFactory;

public class ORAdapterTest {
	
	
	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testGetValueFactory() {
		ValueFactory vf = ORAdapter.getValueFactory();
		assertNotNull(vf);
		assertTrue(vf instanceof MemValueFactory);
	}

	@Test
	public void testUri2OpenRdfUri() {	
		String urString = "http://rmap-project.info/rmap/";
		URI uri = null;
		try {
			uri = new URI(urString);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		org.openrdf.model.URI rUri = ORAdapter.uri2OpenRdfUri(uri);
		assertEquals(urString, rUri.stringValue());
		URI uri2 = ORAdapter.openRdfUri2URI(rUri);
		assertEquals(urString, uri2.toASCIIString());
		assertEquals(uri,uri2);
	}

	@Test
	public void testRMapUri2OpenRdfUri() {
		String urString = "http://rmap-project.info/rmap/";
		URI uri = null;
		try {
			uri = new URI(urString);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		RMapUri rmUri = new RMapUri(uri);
		org.openrdf.model.URI rUri = ORAdapter.rMapUri2OpenRdfUri(rmUri);
		assertEquals(urString, rUri.stringValue());
		URI uri2 = ORAdapter.openRdfUri2URI(rUri);
		assertEquals(urString, uri2.toASCIIString());
		assertEquals(uri,uri2);
	}

	@Test
	public void testRMapBlankNode2OpenRdfBNode() {
		String bnId = null;
		try {
			bnId = IdServiceFactoryIOC.getFactory().createService().createId().toASCIIString();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		RMapBlankNode bn = new RMapBlankNode(bnId);
		BNode bnode = ORAdapter.rMapBlankNode2OpenRdfBNode(bn);
		assertNotNull (bnode);
		assertEquals(bnId, bnode.getID());
		
	}

	@Test
	public void testRMapNonLiteral2OpenRdfResource() {
		String bnId = null;
		try {
			bnId = IdServiceFactoryIOC.getFactory().createService().createId().toASCIIString();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		RMapBlankNode bn = new RMapBlankNode(bnId);
		Resource resource = ORAdapter.rMapNonLiteral2OpenRdfResource(bn);
		assertEquals(bnId, resource.stringValue());
		assertTrue (resource instanceof BNode);
		String urString = "http://rmap-project.info/rmap/";
		URI uri = null;
		try {
			uri = new URI(urString);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		RMapUri rmUri = new RMapUri(uri);
		resource = ORAdapter.rMapNonLiteral2OpenRdfResource(rmUri);
		assertTrue (resource instanceof org.openrdf.model.URI);
		assertEquals(urString, resource.stringValue());
	}

	@Test
	public void testRMapLiteral2OpenRdfLiteral() {
		RMapLiteral lit = new RMapLiteral("RMapLiteral");
		org.openrdf.model.Literal oLit = ORAdapter.rMapLiteral2OpenRdfLiteral(lit);
		assertEquals (lit.getStringValue(),oLit.stringValue());
		
	}

	@Test
	public void testRMapResource2OpenRdfValue() {
		String bnId = null;
		try {
			bnId = IdServiceFactoryIOC.getFactory().createService().createId().toASCIIString();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		RMapBlankNode bn = new RMapBlankNode(bnId);
		Value resource = ORAdapter.rMapValue2OpenRdfValue(bn);
		assertEquals(bnId, resource.stringValue());
		assertTrue (resource instanceof BNode);
		String urString = "http://rmap-project.info/rmap/";
		URI uri = null;
		try {
			uri = new URI(urString);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		RMapUri rmUri = new RMapUri(uri);
		resource = ORAdapter.rMapValue2OpenRdfValue(rmUri);
		assertTrue (resource instanceof org.openrdf.model.URI);
		assertEquals(urString, resource.stringValue());
		RMapLiteral lit = new RMapLiteral("RMapLiteral");
		resource = ORAdapter.rMapValue2OpenRdfValue(lit);
		assertTrue (resource instanceof org.openrdf.model.Literal);
		assertEquals(lit.getStringValue(), resource.stringValue());
	}

	@Test
	public void testOpenRdfUri2URI() {
		String urString = "http://rmap-project.info/rmap/";
		org.openrdf.model.URI rUri =ORAdapter.getValueFactory().createURI(urString);
		URI uri = ORAdapter.openRdfUri2URI(rUri);
		assertEquals(uri.toASCIIString(), rUri.stringValue());
	}

	@Test
	public void testOpenRdfUri2RMapUri() {
		String urString = "http://rmap-project.info/rmap/";
		org.openrdf.model.URI rUri = ORAdapter.getValueFactory().createURI(urString);
		RMapUri uri = ORAdapter.openRdfUri2RMapUri(rUri);
		assertEquals(uri.getStringValue(), rUri.stringValue());
		assertEquals(uri.getIri().toASCIIString(), rUri.stringValue());
	}

	@Test
	public void testOpenRdfBNode2RMapBlankNode() {
		String bnId = null;
		try {
			bnId = IdServiceFactoryIOC.getFactory().createService().createId().toASCIIString();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		BNode bnode = ORAdapter.getValueFactory().createBNode(bnId);
		RMapBlankNode rb = ORAdapter.openRdfBNode2RMapBlankNode(bnode);
		assertEquals(bnode.getID(), rb.getId());
		System.out.println(bnode.getID());
	}

	@Test
	public void testOpenRdfResource2NonLiteralResource() {
		String bnId = null;
		try {
			bnId = IdServiceFactoryIOC.getFactory().createService().createId().toASCIIString();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		BNode bnode = ORAdapter.getValueFactory().createBNode(bnId);
		RMapResource nonLit = null;
		try {
			nonLit = ORAdapter.openRdfResource2NonLiteral(bnode);
			assertTrue(nonLit instanceof RMapBlankNode);
			assertEquals(bnode.getID(), nonLit.getStringValue());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		String urString = "http://rmap-project.info/rmap/";
		org.openrdf.model.URI rUri =ORAdapter.getValueFactory().createURI(urString);
		try {
			nonLit = ORAdapter.openRdfResource2NonLiteral(rUri);
			assertTrue (nonLit instanceof RMapUri);
			assertEquals(nonLit.getStringValue(), rUri.stringValue());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testOpenRdfLiteral2RMapLiteral() {
		org.openrdf.model.Literal oLit = ORAdapter.getValueFactory().createLiteral("OpenRDF Literal");
		RMapLiteral rLit = ORAdapter.openRdfLiteral2RMapLiteral(oLit);
		assertEquals(oLit.stringValue(), rLit.getStringValue());
	}

	@Test
	public void testOpenRdfValue2RMapResource() {
		Value value = ORAdapter.getValueFactory().createLiteral("OpenRDF Literal");
		RMapValue rmr = null;
		try {
			 rmr = ORAdapter.openRdfValue2RMapValue(value);
				assertTrue (rmr instanceof RMapLiteral);
				assertEquals(value.stringValue(), rmr.getStringValue());
				assertEquals(value.stringValue(), rmr.toString());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		String bnId = null;
		try {
			bnId = IdServiceFactoryIOC.getFactory().createService().createId().toASCIIString();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		value = ORAdapter.getValueFactory().createBNode(bnId);
		try {
			 rmr = ORAdapter.openRdfValue2RMapValue(value);
				assertTrue(rmr instanceof RMapBlankNode);
				assertEquals(value.stringValue(), rmr.toString());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		String urString = "http://rmap-project.info/rmap/";
		value = ORAdapter.getValueFactory().createURI(urString);
		try {
			 rmr = ORAdapter.openRdfValue2RMapValue(value);
				assertTrue(rmr instanceof RMapUri);
				assertEquals(value.toString(), rmr.toString());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
	}

}

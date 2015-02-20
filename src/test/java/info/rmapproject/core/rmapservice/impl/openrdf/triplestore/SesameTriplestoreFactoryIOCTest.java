package info.rmapproject.core.rmapservice.impl.openrdf.triplestore;

import static org.junit.Assert.*;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.http.SesameHttpTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.sail.SesameSailMemoryTriplestore;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unused")
public class SesameTriplestoreFactoryIOCTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetFactory() {
		try {
			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			assertTrue(ts instanceof SesameSailMemoryTriplestore);
//			assertTrue(ts instanceof SesameHttpTriplestore);
		} catch (RMapException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail (e.getMessage());
		}
	}

}

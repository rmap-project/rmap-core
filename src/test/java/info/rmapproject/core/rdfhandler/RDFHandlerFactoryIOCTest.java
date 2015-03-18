package info.rmapproject.core.rdfhandler;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import info.rmapproject.core.rdfhandler.impl.openrdf.RioRDFHandlerFactory;

public class RDFHandlerFactoryIOCTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetFactory() {
		RDFHandlerFactory factory =  RDFHandlerFactoryIOC.getFactory();
		assertTrue (factory instanceof RioRDFHandlerFactory);
	}

}

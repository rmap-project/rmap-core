package info.rmapproject.core.rmapservice;

import static org.junit.Assert.*;
import info.rmapproject.core.rmapservice.impl.openrdf.ORMapService;

import org.junit.Before;
import org.junit.Test;

public class RMapServiceFactoryIOCTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetFactory() {
		RMapService service = RMapServiceFactoryIOC.getFactory().createService();
		assertTrue (service instanceof ORMapService);	
	}
}

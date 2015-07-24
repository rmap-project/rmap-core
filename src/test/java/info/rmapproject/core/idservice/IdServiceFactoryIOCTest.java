/**
 * 
 */
package info.rmapproject.core.idservice;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;

import org.junit.Test;

/**
 * @author smorrissey
 *
 */
public class IdServiceFactoryIOCTest {

	

	/**
	 * Test method for {@link info.rmapproject.core.idservice.IdServiceFactoryIOC#getFactory()}.
	 */
	@Test
	public void testGetFactory() {
		try {
			IdService service = IdServiceFactoryIOC.getFactory().createService();
			assertTrue(service instanceof info.rmapproject.core.idservice.impl.ark.ArkIdService);
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	

	/**
	 * Test method for {@link info.rmapproject.core.idservice.IdServiceFactoryIOC#getFactory()}.
	 */
	@Test
	public void testGetNoids() {
		try {
			IdService service = IdServiceFactoryIOC.getFactory().createService();
			URI noid1 = service.createId();
			URI noid2 = service.createId();
			URI noid3 = service.createId();
			
			assertTrue(noid1 instanceof URI);
			assertTrue(noid2 instanceof URI);
			assertTrue(noid3 instanceof URI);
			assertTrue(noid1 != noid2);
			assertTrue(noid2 != noid3);
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();
		}
	}

}

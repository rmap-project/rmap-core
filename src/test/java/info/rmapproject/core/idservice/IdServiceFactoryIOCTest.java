/**
 * 
 */
package info.rmapproject.core.idservice;

import static org.junit.Assert.*;

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

}

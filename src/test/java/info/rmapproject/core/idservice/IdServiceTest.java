/**
 * 
 */
package info.rmapproject.core.idservice;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author smorrissey, khanson
 *
 */

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration({ "classpath*:/spring-rmapcore-testcontext.xml" })
public class IdServiceTest {

	@Autowired
	private IdService rmapIdService;

	/**
	 * Test method for {@link info.rmapproject.core.idservice.ArkIdService}.
	 */
	@Test
	public void testGetArkIdService() {
		try {
			assertTrue(rmapIdService instanceof info.rmapproject.core.idservice.ArkIdService);
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	

	/**
	 * Test method for {@link info.rmapproject.core.idservice.ArkIdService#createId}.
	 */
	@Test
	public void testGetNoids() {
		try {
			URI noid1 = rmapIdService.createId();
			URI noid2 = rmapIdService.createId();
			URI noid3 = rmapIdService.createId();
			
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

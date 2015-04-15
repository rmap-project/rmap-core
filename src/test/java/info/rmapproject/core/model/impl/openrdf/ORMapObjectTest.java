/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

/**
 * @author smorrissey
 *
 */
public class ORMapObjectTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapObject#getId()}.
	 */
	@Test
	public void testGetId() {
		ORMapObject obj = new ORMapAgent();
		URI id = obj.getId();
		String idString = id.toString();
		assertTrue(idString.startsWith("ark:/22573/"));
	}

}

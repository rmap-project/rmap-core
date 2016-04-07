/**
 * 
 */
package info.rmapproject.core.utils;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author smorrissey
 *
 */

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration({ "classpath*:/spring-rmapcore-testcontext.xml" })
public class UtilsTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link info.rmapproject.core.utils.Utils#invertMap(java.util.Map)}.
	 */
	@Test
	public void testInvertMap() {
		Map<Integer,String> inMap = new HashMap<Integer, String>();
		Integer one = Integer.valueOf(1);
		Integer two = Integer.valueOf(2);
		String str1 = new String ("string1");
		String str2 = new String ("string2");
		inMap.put(one, str1);
		inMap.put(two,str2);
		Map<String,Integer> outMap = Utils.invertMap(inMap);
		assertTrue(outMap.keySet().contains(str1));
		assertTrue(outMap.keySet().contains(str2));
		assertEquals(outMap.get(str1),one);
		assertEquals(outMap.get(str2),two);	
	
	}

}

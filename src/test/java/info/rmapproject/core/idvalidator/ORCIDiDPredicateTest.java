/**
 * 
 */
package info.rmapproject.core.idvalidator;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.collections4.Predicate;
import org.junit.Before;
import org.junit.Test;

/**
 * @author smorrissey
 *
 */
public class ORCIDiDPredicateTest {
	
	protected String validOrcStr = "http://orcid.org/0000-0000-0000-0000";
	protected String invalidOrcStr1 = "http://orcid.org/0000-000-0000-0000";
	protected String invalidOrcStr2 = "http://orcid.org/0000000000000000";
	protected String invalidOrcStr3 = "orcid.org/0000-0000-0000-0000";
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link info.rmapproject.core.idvalidator.ORCIDiDPredicate#evaluate(java.lang.Object)}.
	 */
	@Test
	public void testEvaluate() {
		Predicate<Object> predicate = ORCIDiDPredicate.orcidIdPredicate();
		assertFalse(predicate.evaluate(validOrcStr));
		URI uri = null;
		try {
			uri = new URI(validOrcStr);
			assertTrue(predicate.evaluate(uri));
			uri = new URI(invalidOrcStr1);
			assertFalse(predicate.evaluate(uri));
			uri = new URI(invalidOrcStr2);
			assertFalse(predicate.evaluate(uri));
			uri = new URI(invalidOrcStr3);
			assertFalse(predicate.evaluate(uri));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
				
	}

}

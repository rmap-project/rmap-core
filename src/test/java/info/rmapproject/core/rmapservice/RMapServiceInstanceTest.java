package info.rmapproject.core.rmapservice;

import static org.junit.Assert.assertTrue;
import info.rmapproject.core.rmapservice.impl.openrdf.ORMapService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration({ "classpath*:/spring-rmapcore-testcontext.xml" })
public class RMapServiceInstanceTest {

	@Autowired
	RMapService rmapService;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetFactory() {
		assertTrue (rmapService instanceof ORMapService);	
	}
}

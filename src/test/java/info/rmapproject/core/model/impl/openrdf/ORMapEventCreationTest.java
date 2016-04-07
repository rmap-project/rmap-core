/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventType;
import info.rmapproject.core.model.request.RMapRequestAgent;
import info.rmapproject.core.rmapservice.impl.openrdf.ORMapEventMgr;
//import info.rmapproject.core.rmapservice.impl.openrdf.ORMapStatementMgr;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.vocabulary.impl.openrdf.RMAP;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author smorrissey
 *
 */

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration({ "classpath*:/spring-rmapcore-testcontext.xml" })
public class ORMapEventCreationTest {
	@Autowired
	private SesameTriplestore triplestore;
	
	@Autowired
	private ORMapEventMgr eventmgr;
	
	private ORAdapter typeAdapter;
	
	protected ValueFactory vf = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		typeAdapter = new ORAdapter(triplestore);
		vf = typeAdapter.getValueFactory();
		
	}


	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapEventCreation#ORMapEventCreation(info.rmapproject.core.model.RMapIri, info.rmapproject.core.model.event.RMapEventTargetType, info.rmapproject.core.model.RMapValue, java.util.List)}.
	 */
	@Test
	public void testORMapEventCreationRMapIriRMapEventTargetTypeRMapValueListOfRMapIri() throws Exception {
		List<java.net.URI> resourceList = new ArrayList<java.net.URI>();
		try {
		    IRI creatorIRI = vf.createIRI("http://orcid.org/0000-0003-2069-1219");
			resourceList.add(new java.net.URI("http://rmap-info.org"));
			resourceList.add(new java.net.URI
					("https://rmap-project.atlassian.net/wiki/display/RMAPPS/RMap+Wiki"));
			RMapIri associatedAgent = typeAdapter.openRdfIri2RMapIri(creatorIRI);
			ORMapDiSCO disco = new ORMapDiSCO(associatedAgent, resourceList);
			// Make list of created objects
			List<IRI> iris = new ArrayList<IRI>();
			IRI discoContext = disco.getDiscoContext();
			iris.add(discoContext);
			Model model = disco.getAsModel();
			assertEquals(4,model.size());
			List<RMapIri> createdObjIds = new ArrayList<RMapIri>();
			for (IRI iri:iris){
				createdObjIds.add(typeAdapter.openRdfIri2RMapIri(iri));
			}
			RMapRequestAgent requestAgent = new RMapRequestAgent(associatedAgent.getIri());
			ORMapEventCreation event = new ORMapEventCreation(requestAgent, RMapEventTargetType.DISCO, null, createdObjIds);
			Date end = new Date();
			event.setEndTime(end);
			Model eventModel = event.getAsModel();
			assertEquals(7, eventModel.size());
			IRI context = event.getContext();
			for (Statement stmt:eventModel){
				assertEquals(context,stmt.getContext());
			}
			assertEquals(1,event.getCreatedObjectStatements().size());
			assertEquals(createdObjIds.size(),event.getCreatedObjectIds().size());
			assertEquals(RMapEventType.CREATION, event.getEventType());
			assertEquals(RMapEventTargetType.DISCO, event.getEventTargetType());

			//Date sdate = event.getStartTime();
			//Date edate = event.getEndTime();
			
			Statement tStmt = event.getTypeStatement();
			assertEquals(RMAP.EVENT, tStmt.getObject());
			IRI crEventId = eventmgr.createEvent(event, triplestore);
			assertEquals(context, crEventId);
			assertFalse(context.stringValue().equals(discoContext.stringValue()));
			assertTrue(eventmgr.isEventId(context, triplestore));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}


}

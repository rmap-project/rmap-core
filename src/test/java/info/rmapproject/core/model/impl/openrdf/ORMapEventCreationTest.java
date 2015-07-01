/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import static org.junit.Assert.*;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventType;
import info.rmapproject.core.rmapservice.impl.openrdf.ORMapEventMgr;
//import info.rmapproject.core.rmapservice.impl.openrdf.ORMapStatementMgr;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;

/**
 * @author smorrissey
 *
 */
public class ORMapEventCreationTest {
	protected ValueFactory vf = null;
	protected SesameTriplestore ts = null;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		vf = ORAdapter.getValueFactory();
		ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
	}


	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapEventCreation#ORMapEventCreation(info.rmapproject.core.model.RMapUri, info.rmapproject.core.model.event.RMapEventTargetType, info.rmapproject.core.model.RMapValue, java.util.List)}.
	 */
	@Test
	public void testORMapEventCreationRMapUriRMapEventTargetTypeRMapValueListOfRMapUri() {
		List<java.net.URI> resourceList = new ArrayList<java.net.URI>();
		try {
		    URI creatorUri = vf.createURI("http://orcid.org/0000-0003-2069-1219");
			resourceList.add(new java.net.URI("http://rmap-info.org"));
			resourceList.add(new java.net.URI
					("https://rmap-project.atlassian.net/wiki/display/RMAPPS/RMap+Wiki"));
			RMapUri associatedAgent = ORAdapter.openRdfUri2RMapUri(creatorUri);
			ORMapDiSCO disco = new ORMapDiSCO(associatedAgent, resourceList);
			// Make list of created objects
			List<URI> uris = new ArrayList<URI>();
			URI discoContext = disco.getDiscoContext();
			uris.add(discoContext);
			Model model = disco.getAsModel();
			assertEquals(4,model.size());
//			ORMapStatementMgr stmtMgr = new ORMapStatementMgr();
//			for (Statement stmt:model){
//				URI stmtUri = stmtMgr.createReifiedStatement(stmt, ts);
//				uris.add(stmtUri);
//			}
			List<RMapUri> createdObjIds = new ArrayList<RMapUri>();
			for (URI uri:uris){
				createdObjIds.add(ORAdapter.openRdfUri2RMapUri(uri));
			}
			ORMapEventCreation event = new ORMapEventCreation(associatedAgent, 
					RMapEventTargetType.DISCO, null, createdObjIds);
			Date end = new Date();
			event.setEndTime(end);
			Model eventModel = event.getAsModel();
			assertEquals(7, eventModel.size());
			URI context = event.getContext();
			for (Statement stmt:eventModel){
				assertEquals(context,stmt.getContext());
			}
			assertEquals(1,event.getCreatedObjectStatements().size());
			assertEquals(createdObjIds.size(),event.getCreatedObjectIds().size());
			assertEquals(RMapEventType.CREATION, event.getEventType());
			assertEquals(RMapEventTargetType.DISCO, event.getEventTargetType());
			Statement tStmt = event.getTypeStatement();
			assertEquals(RMAP.EVENT, tStmt.getObject());
			ORMapEventMgr eventMgr = new ORMapEventMgr();
			URI crEventId = eventMgr.createEvent(event, ts);
			assertEquals(context, crEventId);
			assertNotEquals(context, discoContext);
			assertTrue(eventMgr.isEventId(context, ts));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}


}

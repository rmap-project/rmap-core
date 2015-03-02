/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.model.RMapEventTargetType;
import info.rmapproject.core.model.RMapEventType;
import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;
import info.rmapproject.core.utils.DateUtils;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.RDF;

/**
 * @author smorrissey
 *
 */
public class ORMapEventDeletionTest {

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
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapEventDeletion#ORMapEventDeletion(org.openrdf.model.Statement, org.openrdf.model.Statement, org.openrdf.model.Statement, org.openrdf.model.Statement, org.openrdf.model.Statement, org.openrdf.model.Statement, org.openrdf.model.URI, org.openrdf.model.Statement, java.util.List)}.
	 */
	@Test
	public void testORMapEventDeletionStatementStatementStatementStatementStatementStatementURIStatementListOfStatement() {
		java.net.URI id1 = null, id2 = null;
		try {
			id1 = IdServiceFactoryIOC.getFactory().createService().createId();
			id2 = IdServiceFactoryIOC.getFactory().createService().createId();
		} catch (Exception e) {
			e.printStackTrace();
			fail("unable to create id");
		} 
		URI context = ORAdapter.uri2OpenRdfUri(id1);
		Date start = new Date();
		String startTime = DateUtils.getIsoStringDate(start);
		Literal litStart = vf.createLiteral(startTime);
		Statement startTimeStmt = vf.createStatement(context, PROV.STARTEDATTIME, litStart, context);		
		Literal eType = vf.createLiteral(RMapEventType.DELETION.getTypeString());
		Statement eventTypeStmt = vf.createStatement(context, RMAP.EVENT_TYPE, eType,context); 
		Literal eTType = vf.createLiteral(RMapEventTargetType.DISCO.toString());
		Statement eventTargetTypeStmt = vf.createStatement(context,
				RMAP.EVENT_TARGET_TYPE, eTType,context);
		URI creatorUri = vf.createURI("http://orcid.org/0000-0000-0000-0000");
		Statement associatedAgentStmt= vf.createStatement(context,
				PROV.WASASSOCIATEDWITH, creatorUri,context);
		Literal desc = vf.createLiteral("This is a delete event");
		Statement descriptionStmt = vf.createStatement(context, DC.DESCRIPTION, desc, context);		
		Statement typeStatement = vf.createStatement(context, RDF.TYPE, RMAP.EVENT, context);
		List<Statement> deletedObjects= new ArrayList<Statement>();
		URI dId = ORAdapter.uri2OpenRdfUri(id2);
		Statement delStmt = vf.createStatement(context, RMAP.EVENT_TARGET_DELETED, dId, context);
		deletedObjects.add(delStmt);
		Date end = new Date();
		String endTime = DateUtils.getIsoStringDate(end);
		Literal litEnd = vf.createLiteral(endTime);
		Statement endTimeStmt = vf.createStatement(context, PROV.ENDEDATTIME, litEnd, context);
		ORMapEvent event = new ORMapEventDeletion(eventTypeStmt,eventTargetTypeStmt, 
				associatedAgentStmt,descriptionStmt, startTimeStmt,endTimeStmt, context, 
				typeStatement, deletedObjects);
		Model eventModel = event.getAsModel();
		assertEquals(8, eventModel.size());
		URI econtext = event.getContext();
		for (Statement stmt:eventModel){
			assertEquals(econtext,stmt.getContext());
		}
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapEventDeletion#ORMapEventDeletion(info.rmapproject.core.model.RMapUri, info.rmapproject.core.model.RMapEventTargetType, info.rmapproject.core.model.RMapValue)}.
	 */
	@Test
	public void testORMapEventDeletionRMapUriRMapEventTargetTypeRMapValue() {
		List<java.net.URI> resourceList = new ArrayList<java.net.URI>();
		try {
			URI creatorUri = vf.createURI("http://orcid.org/0000-0003-2069-1219");
			resourceList.add(new java.net.URI("http://rmap-info.org"));
			resourceList.add(new java.net.URI
					("https://rmap-project.atlassian.net/wiki/display/RMAPPS/RMap+Wiki"));
			RMapUri associatedAgent = ORAdapter.openRdfUri2RMapUri(creatorUri);
			RMapLiteral desc =  new RMapLiteral("this is a deletion event");
			ORMapDiSCO disco = new ORMapDiSCO(associatedAgent, resourceList);			
			ORMapEventDeletion event = new ORMapEventDeletion(associatedAgent, 
					RMapEventTargetType.DISCO, desc);
			RMapUri discoId = ORAdapter.openRdfUri2RMapUri(disco.getDiscoContext());
			List<RMapUri>deleted = new ArrayList<RMapUri>();
			deleted.add(discoId);
			event.setDeletedObjectIds(deleted);
			Date end = new Date();
			event.setEndTime(end);
			Model eventModel = event.getAsModel();
			assertEquals(8, eventModel.size());
			URI context = event.getContext();
			for (Statement stmt:eventModel){
				assertEquals(context,stmt.getContext());
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}

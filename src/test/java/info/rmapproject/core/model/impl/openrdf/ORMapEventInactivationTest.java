/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.Date;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventType;
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
public class ORMapEventInactivationTest {

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
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapEventInactivation#ORMapEventInactivation(org.openrdf.model.Statement, org.openrdf.model.Statement, org.openrdf.model.Statement, org.openrdf.model.Statement, org.openrdf.model.Statement, org.openrdf.model.Statement, org.openrdf.model.URI, org.openrdf.model.Statement, org.openrdf.model.Statement)}.
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapException 
	 */
	@Test
	public void testORMapEventInactivationStatementStatementStatementStatementStatementStatementURIStatementStatement() throws RMapException, RMapDefectiveArgumentException {
		java.net.URI id1 = null, id2 = null;
		try {
			// id for event
			id1 = IdServiceFactoryIOC.getFactory().createService().createId();
			// id for old disco
			id2 = IdServiceFactoryIOC.getFactory().createService().createId();
		} catch (Exception e) {
			e.printStackTrace();
			fail("unable to create id");
		} 
		// create new disco
		URI creatorUri = vf.createURI("http://orcid.org/0000-0000-0000-0000");
		
		URI context = ORAdapter.uri2OpenRdfUri(id1);
		
		Date start = new Date();
		String startTime = DateUtils.getIsoStringDate(start);

		Literal litStart = vf.createLiteral(startTime);
		Statement startTimeStmt = vf.createStatement(context, PROV.STARTEDATTIME, litStart, context);		
	
		Statement eventTypeStmt = vf.createStatement(context, RMAP.EVENT_TYPE, RMAP.EVENT_TYPE_INACTIVATION,context); 
		
		Literal eTType = vf.createLiteral(RMapEventTargetType.DISCO.uriString());
		Statement eventTargetTypeStmt = vf.createStatement(context,
				RMAP.EVENT_TARGET_TYPE, eTType,context);
		
		Statement associatedAgentStmt= vf.createStatement(context,
				PROV.WASASSOCIATEDWITH, creatorUri,context);
		
		Literal desc = vf.createLiteral("This is a delete event");
		
		Statement descriptionStmt = vf.createStatement(context, DC.DESCRIPTION, desc, context);		
		
		Statement typeStatement = vf.createStatement(context, RDF.TYPE, RMAP.EVENT, context);
		
		URI oldDiscoId = ORAdapter.uri2OpenRdfUri(id2);
		Statement sourceObjectStatement = vf.createStatement(context, RMAP.EVENT_INACTIVATED_OBJECT, oldDiscoId, context);
		
		
		Date end = new Date();
		String endTime = DateUtils.getIsoStringDate(end);
		Literal litEnd = vf.createLiteral(endTime);
		Statement endTimeStmt = vf.createStatement(context, PROV.ENDEDATTIME, litEnd, context);
		
		ORMapEventInactivation event = new ORMapEventInactivation(eventTypeStmt, eventTargetTypeStmt, associatedAgentStmt,  
				descriptionStmt, startTimeStmt,  endTimeStmt, context, typeStatement, 
				sourceObjectStatement) ;
		assertEquals(RMAP.EVENT_TYPE_INACTIVATION, event.getEventType());
		assertEquals(RMapEventTargetType.DISCO, event.getEventTargetType());
		Statement tStmt = event.getTypeStatement();
		assertEquals(RMAP.EVENT.toString(), tStmt.getObject().toString());
		Model eventModel = event.getAsModel();
		assertEquals(8, eventModel.size());
		assertEquals(oldDiscoId,ORAdapter.rMapUri2OpenRdfUri(event.getInactivatedObjectId()));		
		assertEquals(desc.stringValue(), event.getDescription().getStringValue());

		try{
			event = new ORMapEventInactivation(eventTypeStmt, eventTargetTypeStmt, associatedAgentStmt,  
					descriptionStmt, startTimeStmt,  endTimeStmt, context, typeStatement, 
					null) ;
			fail("Should not allow null source object");
		}catch(RMapException r){}	
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapEventInactivation#ORMapEventInactivation(info.rmapproject.core.model.RMapUri, info.rmapproject.core.model.event.RMapEventTargetType, info.rmapproject.core.model.RMapValue)}.
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapException 
	 */
	@Test
	public void testORMapEventInactivationRMapUriRMapEventTargetTypeRMapValue() throws RMapException, RMapDefectiveArgumentException {
		RMapUri associatedAgent= null;
		try {
			associatedAgent = new RMapUri(
					new java.net.URI("http://orcid.org/0000-0000-0000-0000"));
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			fail("could not create agent");
		}
		RMapLiteral desc = new RMapLiteral("This is an inactivation event");		
		ORMapEventInactivation event = new ORMapEventInactivation(associatedAgent, 
				RMapEventTargetType.DISCO, desc);
		Model model = event.getAsModel();
		assertEquals(6, model.size());
		
		java.net.URI id1 = null;
		try {
			// id for old disco (source object)
			id1 = IdServiceFactoryIOC.getFactory().createService().createId();
		} catch (Exception e) {
			e.printStackTrace();
			fail("unable to create id");
		} 
		URI inactivatedObject = ORAdapter.uri2OpenRdfUri(id1);
		event.setInactivatedObjectStmt(inactivatedObject);
		model = event.getAsModel();
		assertEquals(7,model.size());
		Date end = new Date();
		event.setEndTime(end);
		model = event.getAsModel();
		assertEquals(8,model.size());
		RMapUri iUri = event.getInactivatedObjectId();
		assertEquals(inactivatedObject.stringValue(), iUri.getStringValue());
		assertEquals(RMapEventType.INACTIVATION, event.getEventType());
		assertEquals(RMapEventTargetType.DISCO, event.getEventTargetType());
		Statement tStmt = event.getTypeStatement();
		assertEquals(RMAP.EVENT, tStmt.getObject());
	
	}

}

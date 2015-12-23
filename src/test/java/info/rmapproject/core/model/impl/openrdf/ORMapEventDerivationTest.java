/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventType;
//import info.rmapproject.core.rmapservice.impl.openrdf.ORMapStatementMgr;
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
public class ORMapEventDerivationTest {
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
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapEventDerivation#ORMapEventDerivation(org.openrdf.model.URI, info.rmapproject.core.model.event.RMapEventTargetType, org.openrdf.model.URI, org.openrdf.model.URI)}.
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapException 
	 */
	@Test
	public void testORMapEventDerivationURIRMapEventTargetTypeURIURI() throws RMapException, RMapDefectiveArgumentException { 
		URI associatedAgent = vf.createURI("http://orcid.org/0000-0000-0000-0000");
		java.net.URI id1 = null;
		try {
			// id for old disco (source object)
			id1 = IdServiceFactoryIOC.getFactory().createService().createId();
		} catch (Exception e) {
			e.printStackTrace();
			fail("unable to create id");
		} 
		URI sourceObject = ORAdapter.uri2OpenRdfUri(id1);
		
		List<java.net.URI> resourceList = new ArrayList<java.net.URI>();
		try {
			resourceList.add(new java.net.URI("http://rmap-info.org"));
			resourceList.add(new java.net.URI
					("https://rmap-project.atlassian.net/wiki/display/RMAPPS/RMap+Wiki"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail("unable to create resources");
		}	
		ORMapDiSCO newDisco = new ORMapDiSCO(ORAdapter.openRdfUri2RMapUri(associatedAgent), resourceList);
		URI derivedObject = newDisco.getDiscoContext();
		
		ORMapEventDerivation event = new ORMapEventDerivation(associatedAgent, RMapEventTargetType.DISCO,
				sourceObject, derivedObject);
		Model model = event.getAsModel();
		assertEquals(6,model.size());
		
		// Make list of created objects
		Set<URI> uris = new LinkedHashSet<URI>();
		URI newDiscoContext = newDisco.getDiscoContext();
		uris.add(newDiscoContext);
		event.setCreatedObjectIdsFromURI(uris);
		model = event.getAsModel();
		assertEquals(7,model.size());
		Date end = new Date();
		event.setEndTime(end);
		model = event.getAsModel();
		assertEquals(8,model.size());
		assertEquals(RMapEventType.DERIVATION, event.getEventType());
		assertEquals(RMapEventTargetType.DISCO, event.getEventTargetType());
	}

	/**
	 * Test method for {@link info.rmapproject.core.model.impl.openrdf.ORMapEventDerivation#ORMapEventDerivation(org.openrdf.model.Statement, org.openrdf.model.Statement, org.openrdf.model.Statement, org.openrdf.model.Statement, org.openrdf.model.Statement, org.openrdf.model.Statement, org.openrdf.model.URI, org.openrdf.model.Statement, java.util.List, org.openrdf.model.Statement, org.openrdf.model.Statement)}.
	 * @throws RMapDefectiveArgumentException 
	 * @throws RMapException 
	 */
	@Test
	public void testORMapEventDerivationStatementStatementStatementStatementStatementStatementURIStatementListOfStatementStatementStatement() throws RMapException, RMapDefectiveArgumentException {
		java.net.URI id1 = null, id2 = null;
		List<java.net.URI> resourceList = new ArrayList<java.net.URI>();
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
		try {
			resourceList.add(new java.net.URI("http://rmap-info.org"));
			resourceList.add(new java.net.URI
					("https://rmap-project.atlassian.net/wiki/display/RMAPPS/RMap+Wiki"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail("unable to create resources");
		}	
		RMapUri associatedAgent = ORAdapter.openRdfUri2RMapUri(creatorUri);
		ORMapDiSCO newDisco = new ORMapDiSCO(associatedAgent, resourceList);
		// Make list of created objects
		List<URI> uris = new ArrayList<URI>();
		URI newDiscoContext = newDisco.getDiscoContext();
		uris.add(newDiscoContext);
		Model model = newDisco.getAsModel();
		assertEquals(4,model.size());
		URI context = ORAdapter.uri2OpenRdfUri(id1);		
		Date start = new Date();
		String startTime = DateUtils.getIsoStringDate(start);
		
		// make list of statements out of list of created object IDS
		List<Statement> createdObjects = new ArrayList<Statement>();
		for (URI uri:uris){
			createdObjects.add(vf.createStatement(context, PROV.GENERATED, uri, context));
		}		
		
		Literal litStart = vf.createLiteral(startTime);
		Statement startTimeStmt = vf.createStatement(context, PROV.STARTEDATTIME, litStart, context);		
	
		Literal eType = vf.createLiteral(RMapEventType.DERIVATION.getTypeString());
		Statement eventTypeStmt = vf.createStatement(context, RMAP.EVENT_TYPE_DERIVATION, eType,context); 
		
		Literal eTType = vf.createLiteral(RMapEventTargetType.DISCO.uriString());
		Statement eventTargetTypeStmt = vf.createStatement(context,
				RMAP.EVENT_TARGET_TYPE, eTType,context);
		
		Statement associatedAgentStmt= vf.createStatement(context,
				PROV.WASASSOCIATEDWITH, creatorUri,context);
		
		Literal desc = vf.createLiteral("This is a delete event");
		
		Statement descriptionStmt = vf.createStatement(context, DC.DESCRIPTION, desc, context);		
		
		Statement typeStatement = vf.createStatement(context, RDF.TYPE, RMAP.EVENT, context);
		
		URI oldDiscoId = ORAdapter.uri2OpenRdfUri(id2);
		Statement sourceObjectStatement = vf.createStatement(context, RMAP.EVENT_SOURCE_OBJECT, oldDiscoId, context);
		
		Statement derivationStatement = vf.createStatement(context, RMAP.EVENT_DERIVED_OBJECT, newDiscoContext,
				 context);
		
		Date end = new Date();
		String endTime = DateUtils.getIsoStringDate(end);
		Literal litEnd = vf.createLiteral(endTime);
		Statement endTimeStmt = vf.createStatement(context, PROV.ENDEDATTIME, litEnd, context);
		
		ORMapEventDerivation event = new ORMapEventDerivation(eventTypeStmt, eventTargetTypeStmt, associatedAgentStmt,  
				descriptionStmt, startTimeStmt,  endTimeStmt, context, typeStatement, 
				createdObjects, derivationStatement, sourceObjectStatement) ;
		assertEquals(1,event.getCreatedObjectStatements().size());
		assertEquals(1,event.getCreatedObjectIds().size());
		assertEquals(RMapEventType.DERIVATION, event.getEventType());
		assertEquals(RMapEventTargetType.DISCO, event.getEventTargetType());
		Statement tStmt = event.getTypeStatement();
		assertEquals(RMAP.EVENT.toString(), tStmt.getObject().toString());
		Model eventModel = event.getAsModel();
		assertEquals(10, eventModel.size());
		assertEquals(oldDiscoId,ORAdapter.rMapUri2OpenRdfUri(event.getSourceObjectId()));
		assertEquals(newDiscoContext,ORAdapter.rMapUri2OpenRdfUri(event.getDerivedObjectId()));		
		assertEquals(desc.stringValue(), event.getDescription().getStringValue());
		
		try{
			event = new ORMapEventDerivation(eventTypeStmt, eventTargetTypeStmt, associatedAgentStmt,  
					descriptionStmt, startTimeStmt,  endTimeStmt, context, typeStatement, 
					null, derivationStatement, sourceObjectStatement) ;
			fail("Should not allow null created objects");
		}catch(RMapException r){}
		try{
			event = new ORMapEventDerivation(eventTypeStmt, eventTargetTypeStmt, associatedAgentStmt,  
					descriptionStmt, startTimeStmt,  endTimeStmt, context, typeStatement, 
					new ArrayList<Statement>(), derivationStatement, sourceObjectStatement) ;
			fail("Should not allow empty created objects");
		}catch(RMapException r){}
		try{
			event = new ORMapEventDerivation(eventTypeStmt, eventTargetTypeStmt, associatedAgentStmt,  
					descriptionStmt, startTimeStmt,  endTimeStmt, context, typeStatement, 
					createdObjects, null, sourceObjectStatement) ;
			fail("Should not allow null derived object");
		}catch(RMapException r){}
		try{
			event = new ORMapEventDerivation(eventTypeStmt, eventTargetTypeStmt, associatedAgentStmt,  
					descriptionStmt, startTimeStmt,  endTimeStmt, context, typeStatement, 
					createdObjects, derivationStatement, null) ;
			fail("Should not allow null source object");
		}catch(RMapException r){}		
				
	}


}

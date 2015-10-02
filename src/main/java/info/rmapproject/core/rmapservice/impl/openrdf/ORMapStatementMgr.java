package info.rmapproject.core.rmapservice.impl.openrdf;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.utils.OSparqlUtils;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;
import info.rmapproject.core.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

/**
 *  @author khanson, smorrissey
 *
 */

public class ORMapStatementMgr extends ORMapObjectMgr {
	
	/**
	 * Get DiSCO URIs that contains Statement corresponding to subject, predicate, object provided
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param ts 
	 * @return List of DiSCO URIs
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public List<URI> getRelatedDiSCOs (URI subject, URI predicate, Value object, RMapStatus statusCode, 
			List<URI> systemAgents, Date dateFrom, Date dateTo, SesameTriplestore ts) 
			throws RMapException, RMapDefectiveArgumentException {
		/*  
		 * query gets rmapObjectId and startDates of created DiSCOs that contain Statement
		 * Example SPARQL:
		 * 
		select DISTINCT ?rmapObjId ?startDate 
		WHERE { 
		 GRAPH ?rmapObjectId  {
			 <http://dx.doi.org/10.1145/356502.356500> <http://purl.org/dc/terms/issued> "1978-12-01"^^<http://www.w3.org/2001/XMLSchema#date> . 
			 ?discoId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/DiSCO> .
		  } .
		 GRAPH ?eventId {
			?eventId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Event> .
			{?eventId <http://www.w3.org/ns/prov#generated> ?rmapObjId} UNION
			{?eventId <http://rmap-project.org/rmap/terms/derivedObject> ?rmapObjId} .
			?eventId <http://www.w3.org/ns/prov#startedAtTime> ?startDate .
			{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd18nd2m3>} .
			} .
			FILTER NOT EXISTS {?statusChangeEventId <http://rmap-project.org/rmap/terms/tombstonedObject> ?rmapObjId} .
			FILTER NOT EXISTS {?statusChangeEventId <http://rmap-project.org/rmap/terms/inactivatedObject> ?rmapObjId} 
		}
		*/
		List<URI> discos = getRelatedObjects(subject, predicate, object, statusCode, systemAgents, dateFrom,dateTo,ts, RMAP.DISCO);
		return discos;		
	}
	

	/**
	 * Get Agent URIs that contains Statement corresponding to subject, predicate, object provided
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param ts 
	 * @return List of Agent URIs
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public List<URI> getRelatedAgents (URI subject, URI predicate, Value object, RMapStatus statusCode, 
			List<URI> systemAgents, Date dateFrom, Date dateTo, SesameTriplestore ts) 
			throws RMapException, RMapDefectiveArgumentException {
		/*
		 * query gets rmapObjId and startDates of created Agents that contain Statement.
		 * Example SPARQL:
		 * 
		select DISTINCT ?rmapObjId ?startDate
		WHERE { 
		  GRAPH ?rmapObjId {
		    <http://isni.org/isni/000000010941358X> <http://xmlns.com/foaf/0.1/name> "IEEE" .
		    ?rmapObjId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Agent> .
		  } .
		  GRAPH ?eventId {
			?eventId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Event> .
			{?eventId <http://www.w3.org/ns/prov#generated> ?rmapObjId} .
			?eventId <http://www.w3.org/ns/prov#startedAtTime> ?startDate .
			{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd3jq0>} .
		  } .
			FILTER NOT EXISTS {?statusChangeEventId <http://rmap-project.org/rmap/terms/tombstonedObject> ?rmapObjId} .
			FILTER NOT EXISTS {?statusChangeEventId <http://rmap-project.org/rmap/terms/inactivatedObject> ?rmapObjId} 
		}
		*/
		List<URI> agents = getRelatedObjects(subject, predicate, object, statusCode, systemAgents, dateFrom, dateTo, ts, RMAP.AGENT);
		return agents;		
	}

	/**
	 * Generic method for getRelatedAgents and getRelatedDiSCOs - returns list of URIs of objects that contain statement provided
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param statusCode
	 * @param systemAgents
	 * @param dateFrom
	 * @param dateTo
	 * @param ts
	 * @param rmapType
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	protected List <URI> getRelatedObjects(URI subject, URI predicate, Value object, RMapStatus statusCode, 
			List<URI> systemAgents, Date dateFrom, Date dateTo, SesameTriplestore ts, URI rmapType)
			throws RMapException, RMapDefectiveArgumentException {
		if (subject==null){
			throw new RMapDefectiveArgumentException ("Null value provided for the subject parameter");
		}
		if (predicate==null){
			throw new RMapDefectiveArgumentException ("Null value provided for the predicate parameter");
		}
		if (object==null){
			throw new RMapDefectiveArgumentException ("Null value provided for the object parameter");
		}

		List<URI> rmapObjIds = new ArrayList<URI>();
		String sSubject = OSparqlUtils.convertUriToSparqlParam(subject);
		String sPredicate = OSparqlUtils.convertUriToSparqlParam(predicate);
		String sObject = OSparqlUtils.convertValueToSparqlParam(object);
		String sysAgentSparql = OSparqlUtils.convertSysAgentUriListToSparqlFilter(systemAgents);
		String statusFilterSparql = OSparqlUtils.convertRMapStatusToSparqlFilter(statusCode);

		// see getRelatedDiSCOs and getRelatedAgents for example queries  
		String sparqlQuery = "SELECT DISTINCT ?rmapObjId ?startDate "
							+ "WHERE { "
							+ " GRAPH ?rmapObjId {"	
							+ 	  sSubject + " " + sPredicate + " " + sObject + " ."	
							+ "   ?rmapObjId <" + RDF.TYPE + "> <" + rmapType + "> . "					
							+ "	  } . "
							+ " GRAPH ?eventId {"
							+ "   ?eventId <" + RDF.TYPE + "> <" + RMAP.EVENT + "> ."
							+ "		{?eventId <" + PROV.GENERATED + "> ?rmapObjId} UNION"
							+ "		  {?eventId <" + RMAP.EVENT_DERIVED_OBJECT + "> ?rmapObjId} ."
							+ "   ?eventId <" + PROV.STARTEDATTIME + "> ?startDate ."
							+     sysAgentSparql
							+ "   } "
							+ statusFilterSparql
							+ "}";
		
		TupleQueryResult resultset = null;
		try {
			resultset = ts.getSPARQLQueryResults(sparqlQuery);
		}
		catch (Exception e) {
			throw new RMapException("Could not retrieve SPARQL query results", e);
		}
		
		try{
			while (resultset.hasNext()) {
				BindingSet bindingSet = resultset.next();
				URI rmapObjId = (URI) bindingSet.getBinding("rmapObjId").getValue();
				Literal startDateLiteral = (Literal) bindingSet.getBinding("startDate").getValue();
				Date startDate = DateUtils.xmlGregorianCalendarToDate(startDateLiteral.calendarValue());
					
				if ((dateFrom != null && startDate.before(dateFrom))
						|| (dateTo != null && startDate.after(dateTo))) { 
					continue; // don't include out of range date
				}		
				
				//all good
				rmapObjIds.add(rmapObjId);
			}
		}	
		catch (RMapException r){throw r;}
		catch (Exception e){
			throw new RMapException("Could not process SPARQL results for resource's RMap Objects", e);
		}

	return rmapObjIds;	
	}
	


	/**
	 * Get Agent URIs that asserted the Statement corresponding to subject, predicate, object provided
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param statusCode
	 * @param dateFrom
	 * @param dateTo
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public Set<URI> getAssertingAgents (URI subject, URI predicate, Value object, RMapStatus statusCode, Date dateFrom, Date dateTo,
					SesameTriplestore ts) throws RMapException, RMapDefectiveArgumentException {
		if (subject==null){
			throw new RMapDefectiveArgumentException ("Null value provided for the subject parameter");
		}
		if (predicate==null){
			throw new RMapDefectiveArgumentException ("Null value provided for the predicate parameter");
		}
		if (object==null){
			throw new RMapDefectiveArgumentException ("Null value provided for the object parameter");
		}
		
		Set<URI> agents = new HashSet<URI>();
		String sSubject = OSparqlUtils.convertUriToSparqlParam(subject);
		String sPredicate = OSparqlUtils.convertUriToSparqlParam(predicate);
		String sObject = OSparqlUtils.convertValueToSparqlParam(object);	
		String statusFilterSparql = OSparqlUtils.convertRMapStatusToSparqlFilter(statusCode);	
		
		/*
		 * select DISTINCT ?agentId ?startDate
			WHERE { 
			  GRAPH ?objectId {
			  <http://dx.doi.org/10.1145/356502.356500> <http://purl.org/dc/terms/issued> "1978-12-01"^^<http://www.w3.org/2001/XMLSchema#date> .
			  } .
			  GRAPH ?eventId {
				?eventId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Event> .
				{?eventId <http://www.w3.org/ns/prov#generated> ?objectId} UNION
				{?eventId <http://rmap-project.org/rmap/terms/derivedObject> ?objectId} .
				?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> ?agentId .
				?eventId <http://www.w3.org/ns/prov#startedAtTime> ?startDate .
				} 
			
			}
		 */

		String sparqlQuery = "SELECT DISTINCT ?agentId ?startDate "
							+ "WHERE { "
							+ "GRAPH ?rmapObjId {"
							+ 		sSubject + " " + sPredicate + " " + sObject
							+ "		} ."			
							+ "GRAPH ?eventId {"
							+ "		?eventId <" + RDF.TYPE + "> <" + RMAP.EVENT + "> ."
							+ "		{?eventId <" + PROV.GENERATED + "> ?rmapObjId} UNION"
							+ "		  {?eventId <" + RMAP.EVENT_DERIVED_OBJECT + "> ?rmapObjId} ."
							+ "     ?eventId <" + PROV.WASASSOCIATEDWITH + "> ?agentId . "
							+ "		?eventId <" + PROV.STARTEDATTIME + "> ?startDate ."
							+ "		} "
							+ statusFilterSparql
							+ "}";
		
		TupleQueryResult resultset = null;
		try {
			resultset = ts.getSPARQLQueryResults(sparqlQuery);
		}
		catch (Exception e) {
			throw new RMapException("Could not retrieve SPARQL query results", e);
		}
		
		try{
			while (resultset.hasNext()) {
				BindingSet bindingSet = resultset.next();
				URI agentId = (URI) bindingSet.getBinding("agentId").getValue();
				Literal startDateLiteral = (Literal) bindingSet.getBinding("startDate").getValue();
				Date startDate = DateUtils.xmlGregorianCalendarToDate(startDateLiteral.calendarValue());
					
				if ((dateFrom != null && startDate.before(dateFrom))
						|| (dateTo != null && startDate.after(dateTo))) { 
					continue; // don't include out of range date
				}	
				
				//all OK
				agents.add(agentId);
			}
		}	
		catch (RMapException r){throw r;}
		catch (Exception e){
			throw new RMapException("Could not process SPARQL results for Statement's asserting Agents", e);
		}

		return agents;		

		}
	
}

package info.rmapproject.core.rmapservice.impl.openrdf;

import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapDiSCONotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
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
	 */
	public List<URI> getRelatedDiSCOs (URI subject, URI predicate, Value object, RMapStatus statusCode, 
		Date dateFrom, Date dateTo, List<URI> systemAgents, ORMapDiSCOMgr discomgr,
		SesameTriplestore ts) throws RMapObjectNotFoundException, RMapException {
		
		List<URI> discos = new ArrayList<URI>();
		String sSubject = OSparqlUtils.convertUriToSparqlParam(subject);
		String sPredicate = OSparqlUtils.convertUriToSparqlParam(predicate);
		String sObject = OSparqlUtils.convertValueToSparqlParam(object);
		String sysAgentSparql = OSparqlUtils.convertSysAgentUriListToSparqlFilter(systemAgents);

		//query gets discoIds and startDates of created DiSCOs that contain Statement
		/*  
		select DISTINCT ?discoId ?startDate 
		WHERE { 
		 GRAPH ?discoId  {
			 <http://dx.doi.org/10.1145/356502.356500> <http://purl.org/dc/terms/issued> "1978-12-01"^^<http://www.w3.org/2001/XMLSchema#date> . 
			 ?discoId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/DiSCO> .
		  } .
		 GRAPH ?eventId {
			?eventId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Event> .
			{?eventId <http://www.w3.org/ns/prov#generated> ?discoId} UNION
			{?eventId <http://rmap-project.org/rmap/terms/derivedObject> ?discoId} .
			?eventId <http://www.w3.org/ns/prov#startedAtTime> ?startDate .
			{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd18nd2m3>} .
			} 
		}
		*/
		String sparqlQuery = "SELECT DISTINCT ?discoId ?startDate "
							+ "WHERE { "
							+ " GRAPH ?discoId {"
							+     sSubject + " " + sPredicate + " " + sObject + " ."
							+ "   ?discoId <" + RDF.TYPE + "> <" + RMAP.DISCO + "> . "							
							+ "	 } . "
							+ " GRAPH ?eventId {"
							+ "   ?eventId <" + RDF.TYPE + "> <" + RMAP.EVENT + "> ."
							+ "   {?eventId <" + PROV.GENERATED + "> ?discoId} UNION"
							+ "      {?eventId <" + RMAP.EVENT_DERIVED_OBJECT + "> ?discoId} ."
							+ "   ?eventId <" + PROV.STARTEDATTIME + "> ?startDate ."
							+     sysAgentSparql
							+ "  }"
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
				URI discoId = (URI) bindingSet.getBinding("discoId").getValue();
				Literal startDateLiteral = (Literal) bindingSet.getBinding("startDate").getValue();
				Date startDate = DateUtils.xmlGregorianCalendarToDate(startDateLiteral.calendarValue());

				RMapStatus dStatus = discomgr.getDiSCOStatus(discoId, ts);
				if (dStatus == RMapStatus.DELETED 
						|| dStatus == RMapStatus.TOMBSTONED
						|| (statusCode != null && !dStatus.equals(statusCode))) { 
					continue; //don't include deleted or mismatched status
				}
					
				if ((dateFrom != null && startDate.before(dateFrom))
						|| (dateTo != null && startDate.after(dateTo))) { 
					continue; // don't include out of range date
				}		
				
				//all good
				discos.add(discoId);
			}
		}	
		catch (RMapDiSCONotFoundException rf){}
		catch (RMapException r){throw r;}
		catch (Exception e){
			throw new RMapException("Could not process SPARQL results for resource's DiSCOs", e);
		}

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
	 */
	public List<URI> getRelatedAgents (URI subject, URI predicate, Value object, RMapStatus statusCode, 
			List<URI> systemAgents, Date dateFrom, Date dateTo, ORMapAgentMgr agentmgr,
			SesameTriplestore ts) throws RMapObjectNotFoundException, RMapException {

		List<URI> agents = new ArrayList<URI>();
		String sSubject = OSparqlUtils.convertUriToSparqlParam(subject);
		String sPredicate = OSparqlUtils.convertUriToSparqlParam(predicate);
		String sObject = OSparqlUtils.convertValueToSparqlParam(object);
		String sysAgentSparql = OSparqlUtils.convertSysAgentUriListToSparqlFilter(systemAgents);

		//query gets agentsIds and startDates of created Agents that contain Statement
		/*  
		select DISTINCT ?agentId ?startDate
		WHERE { 
		  GRAPH ?agentId {
		    <http://isni.org/isni/000000010941358X> <http://xmlns.com/foaf/0.1/name> "IEEE" .
		    ?agentId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Agent> .
		  } .
		  GRAPH ?eventId {
			?eventId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Event> .
			{?eventId <http://www.w3.org/ns/prov#generated> ?agentId} .
			?eventId <http://www.w3.org/ns/prov#startedAtTime> ?startDate .
			{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd3jq0>} .
		  }
		}
		*/
		String sparqlQuery = "SELECT DISTINCT ?agentId ?startDate "
							+ "WHERE { "
							+ " GRAPH ?agentId {"	
							+ 	  sSubject + " " + sPredicate + " " + sObject + " ."	
							+ "   ?agentId <" + RDF.TYPE + "> <" + RMAP.AGENT + "> . "					
							+ "	  } . "
							+ " GRAPH ?eventId {"
							+ "   ?eventId <" + RDF.TYPE + "> <" + RMAP.EVENT + "> ."
							+ "   {?eventId <" + PROV.GENERATED + "> ?agentId} ."
							+ "   ?eventId <" + PROV.STARTEDATTIME + "> ?startDate ."
							+     sysAgentSparql
							+ "   } "
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
				URI discoId = (URI) bindingSet.getBinding("agentId").getValue();
				Literal startDateLiteral = (Literal) bindingSet.getBinding("startDate").getValue();
				Date startDate = DateUtils.xmlGregorianCalendarToDate(startDateLiteral.calendarValue());

				RMapStatus dStatus = agentmgr.getAgentStatus(discoId, ts);
				if (dStatus == RMapStatus.DELETED 
						|| dStatus == RMapStatus.TOMBSTONED
						|| (statusCode != null && !dStatus.equals(statusCode))) { 
					continue; //don't include deleted or mismatched status
				}
					
				if ((dateFrom != null && startDate.before(dateFrom))
						|| (dateTo != null && startDate.after(dateTo))) { 
					continue; // don't include out of range date
				}		
				
				//all good
				agents.add(discoId);
			}
		}	
		catch (RMapAgentNotFoundException rf){}
		catch (RMapException r){throw r;}
		catch (Exception e){
			throw new RMapException("Could not process SPARQL results for resource's Agents", e);
		}

	return agents;		
	}

	


	/**
	 * Get Agent URIs that asserted the Statement corresponding to subject, predicate, object provided
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param eventmgr 
	 * @param discomgr
	 * @param agentmgr
	 * @param ts 
	 * @return List of Agent URIs
	 * @throws RMapException
	 */
	public Set<URI> getAssertingAgents (URI subject, URI predicate, Value object, RMapStatus statusCode, Date dateFrom, Date dateTo,
					ORMapEventMgr eventmgr, ORMapDiSCOMgr discomgr, ORMapAgentMgr agentmgr,
					SesameTriplestore ts) throws RMapObjectNotFoundException, RMapException {
		
		Set<URI> agents = new HashSet<URI>();
		String sSubject = OSparqlUtils.convertUriToSparqlParam(subject);
		String sPredicate = OSparqlUtils.convertUriToSparqlParam(predicate);
		String sObject = OSparqlUtils.convertValueToSparqlParam(object);		
		
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
							+ "GRAPH ?objectId {"
							+ 		sSubject + " " + sPredicate + " " + sObject
							+ "		} ."			
							+ "GRAPH ?eventId {"
							+ "		?eventId <" + RDF.TYPE + "> <" + RMAP.EVENT + "> ."
							+ "		{?eventId <" + PROV.GENERATED + "> ?objectId} UNION"
							+ "		  {?eventId <" + RMAP.EVENT_DERIVED_OBJECT + "> ?objectId} ."
							+ "     ?eventId <" + PROV.WASASSOCIATEDWITH + "> ?agentId . "
							+ "		?eventId <" + PROV.STARTEDATTIME + "> ?startDate ."
							+ "		} ."
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

				RMapStatus dStatus = agentmgr.getAgentStatus(agentId, ts);
				if (dStatus == RMapStatus.DELETED 
						|| dStatus == RMapStatus.TOMBSTONED
						|| (statusCode != null && !dStatus.equals(statusCode))) { 
					continue; //don't include deleted or mismatched status
				}
					
				if ((dateFrom != null && startDate.before(dateFrom))
						|| (dateTo != null && startDate.after(dateTo))) { 
					continue; // don't include out of range date
				}	
				
				//all OK
				agents.add(agentId);
			}
		}	
		catch (RMapAgentNotFoundException rf){}
		catch (RMapException r){throw r;}
		catch (Exception e){
			throw new RMapException("Could not process SPARQL results for Statement's asserting Agents", e);
		}

		return agents;		

		}
	
}

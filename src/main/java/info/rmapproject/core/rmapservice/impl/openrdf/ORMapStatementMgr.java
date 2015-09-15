package info.rmapproject.core.rmapservice.impl.openrdf;

import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapDiSCONotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;
import info.rmapproject.core.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
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
		Date dateFrom, Date dateTo, List<java.net.URI> systemAgents, ORMapDiSCOMgr discomgr,
		SesameTriplestore ts) throws RMapObjectNotFoundException, RMapException {
		// get all Statements with uri in subject or object

		List<URI> discos = new ArrayList<URI>();
		String sSubject = subject.stringValue();
		sSubject = sSubject.replace("\"","\\\"");
		
		String sPredicate = predicate.stringValue();
		sPredicate = sPredicate.replace("\"","\\\"");
		
		String sObject = object.stringValue();
		sObject = sObject.replace("\"","\\\"");
		
		//apply language or datatype filter for object of statement.
		String objectFilter = "";
		if (object instanceof Literal) {
			objectFilter = "\"" + sObject + "\""; // put in quotes
			String lang = ((Literal) object).getLanguage();
			URI type = ((Literal) object).getDatatype();
			if (lang != null && lang.length() > 0) {
				objectFilter = objectFilter + "@" + lang;
			}
			else if (type != null) {
				objectFilter = objectFilter + "^^<" + type.toString() + ">";
			}
		}
		else { // put in angle brackets
			objectFilter = "<" + objectFilter + ">";
		}
				
		//build system agent filter SPARQL
		String sysAgentSparql = "";
		if (systemAgents.size()>0) {
			Integer i = 0;			
			for (java.net.URI systemAgent : systemAgents) {
				i=i+1;
				if (i>1){
					sysAgentSparql = sysAgentSparql + " UNION ";
				}
				sysAgentSparql = sysAgentSparql + " {?eventId <" + PROV.WASASSOCIATEDWITH + "> <" + systemAgent.toString() + ">} ";
			}
			sysAgentSparql = sysAgentSparql + " . ";
		}
		
		//query gets discoIds and startDates of created DiSCOs that contain Resource
		/*  
select DISTINCT ?discoid ?startDate
WHERE { 
  GRAPH ?eventId {
	?eventId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Event> .
	{?eventId <http://www.w3.org/ns/prov#generated> ?discoId} UNION
	{?eventId <http://rmap-project.org/rmap/terms/derivedObject> ?discoId} .
	?eventId <http://www.w3.org/ns/prov#startedAtTime> ?startDate .
	{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd18m7mj4>} .
	} .
 GRAPH ?discoid
  {
 <http://dx.doi.org/10.1145/356502.356497> <http://purl.org/dc/terms/issued> "1978-12-01"^^<http://www.w3.org/2001/XMLSchema#date> . 
  } .
 ?discoId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/DiSCO>
}

	 */
		String sparqlQuery = "SELECT DISTINCT ?discoId ?startDate "
							+ "WHERE { "
							+ "GRAPH ?eventId {"
							+ "?eventId <" + RDF.TYPE + "> <" + RMAP.EVENT + "> ."
							+ "{?eventId <" + PROV.GENERATED + "> ?discoId} UNION"
							+ "{?eventId <" + RMAP.EVENT_DERIVED_OBJECT + "> ?discoId} ."
							+ "?eventId <" + PROV.STARTEDATTIME + "> ?startDate ."
							+ sysAgentSparql
							+ "} ."
							+ "GRAPH ?discoId "
							+ "	  {"
							+ "		{<" + sSubject + "> <" + sPredicate + "> " + objectFilter + "} ."
							+ "     ?discoId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/DiSCO> . "							
							+ "	  } "
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
				boolean addit = true;
				BindingSet bindingSet = resultset.next();
				URI discoId = (URI) bindingSet.getBinding("discoId").getValue();
				Literal startDateLiteral = (Literal) bindingSet.getBinding("startDate").getValue();
				Date startDate = DateUtils.xmlGregorianCalendarToDate(startDateLiteral.calendarValue());

				//apply filters for date and status
				if (dateFrom != null) { 
					addit = startDate.after(dateFrom);
				}
				if (dateTo != null && addit) {
					addit = startDate.before(dateTo);
				}
				if (statusCode != null && addit){
					RMapStatus dStatus = discomgr.getDiSCOStatus(discoId, ts);
					addit = dStatus.equals(statusCode);
				}
				
				if (addit) {
					discos.add(discoId);
				}
			}
		}	
		catch (RMapDiSCONotFoundException rf){}
		catch (RMapException r){throw r;}
		catch (Exception e){
			throw new RMapException("Could not process SPARQL results for resource's DiSCOs", e);
		}

		return discos;		
		
		
		/*
		
		
		
		
		
		List<Statement> stmts = null;
		try {
			stmts = ts.getStatements(subject, predicate, object);
		} catch (Exception e) {
			throw new RMapException (e);
		}		
				
		List<URI> discos = new ArrayList<URI>();
		// make sure DiSCO in which statement appears matches statusCode
		for (Statement stmt:stmts){
			URI context = (URI)stmt.getContext();

			if (context != null && this.isDiscoId(context, ts)){
				if (statusCode==null){
					// match any status
					discos.add(context);
				}
				else {
					try {
						RMapStatus dStatus = discomgr.getDiSCOStatus(context, ts);
						
						if (dStatus.equals(statusCode)){
							discos.add(context);
						}
					}
					catch (RMapDiSCONotFoundException rf){}
					catch (RMapException r){throw r;}
				}
			}
		}
		return discos;		*/
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
	public List<URI> getRelatedAgents (Resource subject, URI predicate, Value object, RMapStatus statusCode, ORMapAgentMgr agentmgr,
			SesameTriplestore ts) throws RMapObjectNotFoundException, RMapException {
			// get all Statements with uri in subject or object
		
			List<Statement> stmts = null;
			try {
				stmts = ts.getStatements(subject, predicate, object);
			} catch (Exception e) {
				throw new RMapException (e);
			}		
					
			List<URI> agents = new ArrayList<URI>();
			// make sure Agent in which statement appears matches statusCode
			for (Statement stmt:stmts){
				URI context = (URI)stmt.getContext();

				if (context != null && this.isAgentId(context, ts)){
					if (statusCode==null){
						// match any status
						agents.add(context);
					}
					else {
						try {
							RMapStatus aStatus = agentmgr.getAgentStatus(context, ts);
							
							if (aStatus.equals(statusCode)){
								agents.add(context);
							}
						}
						catch (RMapAgentNotFoundException rf){}
						catch (RMapException r){throw r;}
					}
				}
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
	public Set<URI> getAssertingAgents (Resource subject, URI predicate, Value object, RMapStatus statusCode, Date dateFrom, Date dateTo,
					ORMapEventMgr eventmgr, ORMapDiSCOMgr discomgr, ORMapAgentMgr agentmgr,
					SesameTriplestore ts) throws RMapObjectNotFoundException, RMapException {
			
			List<Statement> stmts = null;
			try {
				stmts = ts.getStatements(subject, predicate, object);
			} catch (Exception e) {
				throw new RMapException (e);
			}		
											
			Set<URI> agents = null;
			// make sure Agent in which statement appears matches statusCode
			for (Statement stmt:stmts){
				URI context = (URI)stmt.getContext();
				if (context != null && this.isAgentId(context, ts)){
					agents = agentmgr.getAssertingAgents(context, statusCode, dateFrom, dateTo, eventmgr, ts);
				}
				else if (context != null && this.isDiscoId(context, ts)){
					agents = discomgr.getAssertingAgents(context, statusCode, dateFrom, dateTo, eventmgr, ts);
				}
			}
			return agents;		
		}


	
	
	
}

package info.rmapproject.core.rmapservice.impl.openrdf;

import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapDiSCONotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.utils.OSparqlUtils;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;
import info.rmapproject.core.utils.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

/**
 * 
 *  @author khanson, smorrissey
 *
 */
public class ORMapResourceMgr extends ORMapObjectMgr {

	/**
	 * 
	 */
	public ORMapResourceMgr() {
		super();
	}
	
	/**
	 * Get list of DiSCO URIs that have a statement containing the resource.  
	 * @param uri
	 * @param statusCode
	 * @param discomgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 * @throws RMapDefectiveArgumentException
	 */
	public Set<URI> getRelatedDiSCOs(URI resource, RMapStatus statusCode, 
			ORMapDiSCOMgr discomgr, SesameTriplestore ts) 
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		return getRelatedDiSCOS(resource, statusCode, null, null, null, discomgr, ts);
	}
	
	/**
	 * Get list of DiSCO URIs that have a statement containing the resource.  
	 * @param uri
	 * @param statusCode  if null, match any status code
	 * @param systemAgents
	 * @param dateFrom
	 * @param dateTo
	 * @param discomgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public Set<URI> getRelatedDiSCOS(URI resource, RMapStatus statusCode,
						List<URI> systemAgents, Date dateFrom, Date dateTo,
						ORMapDiSCOMgr discomgr, SesameTriplestore ts)
						throws RMapException, RMapDefectiveArgumentException {		
		if (resource==null){
				throw new RMapDefectiveArgumentException ("null URI");
			}

		Set<URI> discos = new HashSet<URI>();
		String sResource = OSparqlUtils.convertUriToSparqlParam(resource);
		String sysAgentSparql = OSparqlUtils.convertSysAgentUriListToSparqlFilter(systemAgents);
		
		//query gets discoIds and startDates of created DiSCOs that contain Resource
		/*  SELECT DISTINCT ?discoId ?startDate 
			WHERE { 
			GRAPH ?discoId 
			  {
			     {?s ?p <http://dx.doi.org/10.1109/InPar.2012.6339604>} UNION 
			        {<http://dx.doi.org/10.1109/InPar.2012.6339604> ?p ?o} .
			     ?discoId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/DiSCO> .
			  } .
			GRAPH ?eventId {
			 	?eventId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Event> .
				{?eventId <http://www.w3.org/ns/prov#generated> ?discoId} UNION
				{?eventId <http://rmap-project.org/rmap/terms/derivedObject> ?discoId} .
			 	?eventId <http://www.w3.org/ns/prov#startedAtTime> ?startDate .
			 	{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd18nd2m3>} UNION
			 	{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd18nd2p4>} .
			 	}
			  }
			*/
		String sparqlQuery = "SELECT DISTINCT ?discoId ?startDate "
							+ "WHERE { "
							+ " GRAPH ?discoId "
							+ "	  {"
							+ "		{?s ?p " + sResource + "} UNION "
							+ "		  {" + sResource + " ?p ?o} ."
							+ "     ?discoId <" + RDF.TYPE + "> <" + RMAP.DISCO + "> . "							
							+ "	  } . "
							+ " GRAPH ?eventId {"
							+ "   ?eventId <" + RDF.TYPE + "> <" + RMAP.EVENT + "> ."
							+ "   {?eventId <" + PROV.GENERATED + "> ?discoId} UNION"
							+ "   {?eventId <" + RMAP.EVENT_DERIVED_OBJECT + "> ?discoId} ."
							+ "   ?eventId <" + PROV.STARTEDATTIME + "> ?startDate ."
							+     sysAgentSparql
							+ "  } "
							+ "} ";
		
		TupleQueryResult resultset = null;
		try {
			resultset = ts.getSPARQLQueryResults(sparqlQuery);
		}
		catch (Exception e) {
			throw new RMapException("Could not retrieve SPARQL query results using " + sparqlQuery, e);
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
	 * 
	 * @param uri
	 * @param statusCode
	 * @param agentmgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapObjectNotFoundException
	 * @throws RMapDefectiveArgumentException
	 */
	public Set<URI> getRelatedAgents(URI uri, RMapStatus statusCode, 
			ORMapAgentMgr agentmgr, SesameTriplestore ts) 
	throws RMapException, RMapObjectNotFoundException, RMapDefectiveArgumentException {
		return getRelatedAgents(uri, statusCode, null, null, null, agentmgr, ts);
	}

	/**
	 * Get list of RMap Agent URIs that have a statement containing the resource.  
	 * @param uri
	 * @param statusCode  if null, match any status code
	 * @param agentmgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public Set<URI> getRelatedAgents(URI resource, RMapStatus statusCode,
						List<URI> systemAgents, Date dateFrom, Date dateTo,
						ORMapAgentMgr agentmgr, SesameTriplestore ts)
					throws RMapException, RMapDefectiveArgumentException {		
		if (resource==null){
				throw new RMapDefectiveArgumentException ("null URI");
			}

		Set<URI> agents = new HashSet<URI>();
		String sResource = OSparqlUtils.convertUriToSparqlParam(resource);
		String sysAgentSparql = OSparqlUtils.convertSysAgentUriListToSparqlFilter(systemAgents);
		
		//query gets agentIds and startDates of created Agents that contain Resource
		/*  SELECT DISTINCT ?agentId ?startDate 
			WHERE { 
			GRAPH ?agentId 
			  {
			     {?s ?p <http://isni.org/isni/0000000406115044>} UNION 
			        {<http://isni.org/isni/0000000406115044> ?p ?o} .
			     ?agentId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Agent> .
			  } .
			GRAPH ?eventId {
			 	?eventId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Event> .
				?eventId <http://www.w3.org/ns/prov#generated> ?agentId .
			 	?eventId <http://www.w3.org/ns/prov#startedAtTime> ?startDate .
			 	{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd18nd2m3>} UNION
			 	{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd3jq0>} .
			 	}
			  }
			*/
		String sparqlQuery = "SELECT DISTINCT ?agentId ?startDate "
							+ "WHERE { "
							+ " GRAPH ?agentId "
							+ "	  {"
							+ "		{?s ?p " + sResource + "} UNION "
							+ "		  {" + sResource + " ?p ?o} ."
							+ "     ?agentId <" + RDF.TYPE + "> <" + RMAP.AGENT + "> . "							
							+ "	  } . "
							+ " GRAPH ?eventId {"
							+ "   ?eventId <" + RDF.TYPE + "> <" + RMAP.EVENT + "> ."
							+ "   ?eventId <" + PROV.GENERATED + "> ?agentId ."
							+ "   ?eventId <" + PROV.STARTEDATTIME + "> ?startDate ."
							+     sysAgentSparql
							+ "  } "
							+ "} ";
		
		TupleQueryResult resultset = null;
		try {
			resultset = ts.getSPARQLQueryResults(sparqlQuery);
		}
		catch (Exception e) {
			throw new RMapException("Could not retrieve SPARQL query results using " + sparqlQuery, e);
		}
		
		try{
			while (resultset.hasNext()) {
				BindingSet bindingSet = resultset.next();
				URI agentId = (URI) bindingSet.getBinding("agentId").getValue();
				Literal startDateLiteral = (Literal) bindingSet.getBinding("startDate").getValue();
				Date startDate = DateUtils.xmlGregorianCalendarToDate(startDateLiteral.calendarValue());

				RMapStatus aStatus = agentmgr.getAgentStatus(agentId, ts);
				
				if (aStatus == RMapStatus.DELETED 
						|| aStatus == RMapStatus.TOMBSTONED
						|| (statusCode != null && !aStatus.equals(statusCode))) { 
					continue; //don't include deleted or mismatched status
				}
					
				if ((dateFrom != null && startDate.before(dateFrom))
						|| (dateTo != null && startDate.after(dateTo))) { 
					continue; // don't include out of range date
				}
				agents.add(agentId);
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
	 * Get list of RMap Agent URIs that have a statement containing the resource.  
	 * @param uri
	 * @param statusCode  if null, match any status code
	 * @param agentmgr
	 * @param discomgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public Set<URI> getRelatedObjects(URI resource, RMapStatus statusCode,
						List<URI> systemAgents, Date dateFrom, Date dateTo,
						ORMapAgentMgr agentmgr, ORMapDiSCOMgr discomgr, SesameTriplestore ts)
		throws RMapException, RMapDefectiveArgumentException {		
		if (resource==null){
				throw new RMapDefectiveArgumentException ("null URI");
			}

		Set<URI> objects = new HashSet<URI>();
	
		try{
			Set<URI> discos = getRelatedDiSCOS(resource, statusCode, systemAgents, dateFrom, dateTo, discomgr, ts);
			if (discos!=null && discos.size()>0) {
				objects.addAll(discos);
			}

			Set<URI> agents = getRelatedAgents(resource, statusCode, systemAgents, dateFrom, dateTo, agentmgr, ts);
			if (agents!=null && agents.size()>0) {
				objects.addAll(agents);
			}
		}	
		catch (RMapAgentNotFoundException rf){}
		catch (RMapException r){throw r;}
		catch (Exception e){
			throw new RMapException("Could not process SPARQL results for resource's related objects", e);
		}

		return objects;		
	}
	
	/**
	 * Get ids of Events related to resource.
	 * @param resource
	 * @param systemAgents
	 * @param ts
	 * @return
	 * @throws RMapException 
	 */
	public Set<URI> getRelatedEvents(URI resource, List<URI> systemAgents, SesameTriplestore ts)
			throws RMapException, RMapDefectiveArgumentException {		
		return getRelatedEvents(resource,systemAgents, null, null, ts);
	}
	
	/**
	 * Get list of Events URIs that are associated with an object that references the resource passed in.  
	 * @param resource
	 * @param systemAgents
	 * @param dateFrom
	 * @param dateTo
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public Set<URI> getRelatedEvents(URI resource, List<URI> systemAgents, Date dateFrom, 
						Date dateTo, SesameTriplestore ts)
						throws RMapException, RMapDefectiveArgumentException {		
		if (resource==null){
				throw new RMapDefectiveArgumentException ("null URI");
		}

		Set<URI> events = new HashSet<URI>();
		String sResource = OSparqlUtils.convertUriToSparqlParam(resource);
		String sysAgentSparql = OSparqlUtils.convertSysAgentUriListToSparqlFilter(systemAgents);
		
		//query gets eventIds and startDates of created DiSCOs that contain Resource
		/*  SELECT DISTINCT ?eventId ?startDate 
			WHERE {
			GRAPH ?objectId 
			  {
			     {?s ?p <http://dx.doi.org/10.1109/InPar.2012.6339604>} UNION 
			        {<http://dx.doi.org/10.1109/InPar.2012.6339604> ?p ?o} .
			  } .
			GRAPH ?eventId {
			 	?eventId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Event> .
				?eventId ?eventtype ?objectId .
			 	?eventId <http://www.w3.org/ns/prov#startedAtTime> ?startDate .
			 	{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd18nd2m3>} UNION
			 	{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd18nd2p4>} .
			 	}
			}
			*/
		String sparqlQuery = "SELECT DISTINCT ?eventId ?startDate "
							+ "WHERE { "
							+ " GRAPH ?objectId "
							+ "	  {"
							+ "		{?s ?p " + sResource + "} UNION "
							+ "		  {" + sResource + " ?p ?o} ."						
							+ "	  } . "
							+ " GRAPH ?eventId {"
							+ "   ?eventId <" + RDF.TYPE + "> <" + RMAP.EVENT + "> ."
							+ "   ?eventId ?eventtype ?objectId . "
							+ "   ?eventId <" + PROV.STARTEDATTIME + "> ?startDate ."
							+     sysAgentSparql
							+ "  } "
							+ "} ";
		
		TupleQueryResult resultset = null;
		try {
			resultset = ts.getSPARQLQueryResults(sparqlQuery);
		}
		catch (Exception e) {
			throw new RMapException("Could not retrieve SPARQL query results using " + sparqlQuery, e);
		}
		
		try{
			while (resultset.hasNext()) {
				BindingSet bindingSet = resultset.next();
				URI eventId = (URI) bindingSet.getBinding("eventId").getValue();
				Literal startDateLiteral = (Literal) bindingSet.getBinding("startDate").getValue();
				Date startDate = DateUtils.xmlGregorianCalendarToDate(startDateLiteral.calendarValue());
					
				if ((dateFrom != null && startDate.before(dateFrom))
						|| (dateTo != null && startDate.after(dateTo))) { 
					continue; // don't include out of range date
				}
				events.add(eventId);
			}
		}	
		catch (RMapException r){throw r;}
		catch (Exception e){
			throw new RMapException("Could not process SPARQL results for resource's Events", e);
		}

		return events;		
	}


	/**
	 * Get Statements referencing a URI in subject or object, whose Subject, Predicate, and Object comprise an RMapStatement, 
	 * and (if statusCode is not null), whose status matches statusCodeE
	 * @param uri Resource to be matched
	 * @param statusCode Status to be matched, or null if any status code
	 * @param discomgr
	 * @param ts
	 * @return 
	 * @throws RMapDefectiveArgumentException
	 * @throws RMapException
	 */
	public Set<Statement> getRelatedStatementTriples(URI resource, RMapStatus statusCode, 			List<URI> systemAgents, Date dateFrom, Date dateTo, ORMapDiSCOMgr discomgr, 
			ORMapAgentMgr agentmgr, SesameTriplestore ts) 
			throws RMapDefectiveArgumentException, RMapException {
		if (resource==null){
			throw new RMapDefectiveArgumentException ("null URI");
		}
		Set<Statement> relatedStmts = new HashSet<Statement>();		
		String sResource = OSparqlUtils.convertUriToSparqlParam(resource);
		String sysAgentSparql = OSparqlUtils.convertSysAgentUriListToSparqlFilter(systemAgents);
		
		//query gets eventIds and startDates of created DiSCOs that contain Resource
		/*  SELECT DISTINCT ?s ?p ?o ?objectId ?startDate 
			WHERE {
			GRAPH ?objectId 
			 {
			     {BIND(<http://dx.doi.org/10.1109/InPar.2012.6339604> as ?o) . ?s ?p ?o} UNION 
			     {BIND(<http://dx.doi.org/10.1109/InPar.2012.6339604> as ?s) . ?s ?p ?o} .
			  } .
			GRAPH ?eventId {
			 	?eventId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Event> .
				{?eventId <http://www.w3.org/ns/prov#generated> ?objectId} UNION
				{?eventId <http://rmap-project.org/rmap/terms/derivedObject> ?objectId} .
			 	?eventId <http://www.w3.org/ns/prov#startedAtTime> ?startDate .
			 	{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd18nd2m3>} UNION
			 	{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd18nd2p4>} .
			 	}
			}
			*/
		String sparqlQuery = "SELECT DISTINCT ?s ?p ?o ?objectId ?startDate "
							+ "WHERE { "
							+ " GRAPH ?objectId "
							+ "	  {"
							+ "		{BIND(" + sResource + " as ?o) . ?s ?p ?o} UNION"
							+ "		{BIND(" + sResource + " as ?s) . ?s ?p ?o} . "						
							+ "	  } . "
							+ " GRAPH ?eventId {"
							+ "   ?eventId <" + RDF.TYPE + "> <" + RMAP.EVENT + "> ."
							+ "   {?eventId <" + PROV.GENERATED + "> ?objectId} UNION"
							+ "   {?eventId <" + RMAP.EVENT_DERIVED_OBJECT + "> ?objectId} ."
							+ "   ?eventId <" + PROV.STARTEDATTIME + "> ?startDate ."
							+     sysAgentSparql
							+ "  } "
							+ "} ";
		
		TupleQueryResult resultset = null;
		try {
			resultset = ts.getSPARQLQueryResults(sparqlQuery);
		}
		catch (Exception e) {
			throw new RMapException("Could not retrieve SPARQL query results using " + sparqlQuery, e);
		}
		
		try{
			while (resultset.hasNext()) {
				BindingSet bindingSet = resultset.next();
				Resource subj = (Resource) bindingSet.getBinding("s").getValue();
				URI pred = (URI) bindingSet.getBinding("p").getValue();
				Value obj = (Value) bindingSet.getBinding("o").getValue();
				URI rmapObjId = (URI) bindingSet.getBinding("objectId").getValue();
				Literal startDateLiteral = (Literal) bindingSet.getBinding("startDate").getValue();
				Date startDate = DateUtils.xmlGregorianCalendarToDate(startDateLiteral.calendarValue());
				
				if ((dateFrom != null && startDate.before(dateFrom))
						|| (dateTo != null && startDate.after(dateTo))) { 
					continue; // don't include out of range date
				}
				
				RMapStatus status = null;
				if (this.isDiscoId(rmapObjId, ts)) {
					status = discomgr.getDiSCOStatus(rmapObjId, ts);
				}
				else if (this.isAgentId(rmapObjId, ts)) {
					status = agentmgr.getAgentStatus(rmapObjId, ts);
				}
				
				if (status == RMapStatus.DELETED 
						|| status == RMapStatus.TOMBSTONED
						|| (statusCode != null && !status.equals(statusCode))) { 
					continue; //don't include deleted or mismatched status
				}
						
				Statement stmt = new StatementImpl(subj, pred, obj);	
				relatedStmts.add(stmt);
			}
		}	
		catch (RMapException r){throw r;}
		catch (Exception e){
			throw new RMapException("Could not process SPARQL results for resource's related triples", e);
		}
		
		return relatedStmts;
	}
	
	
	/**
	 * Find types of resource in specific context
	 * @param rUri resource whose type is being checked
	 * @param cUri context in which to check
	 * @param ts Triplestore
	 * @return Set of URIs that are type(s) of resource in given context, or null if none found
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public Set<URI> getResourceRdfTypes(URI rUri, URI cUri,  SesameTriplestore ts)
			throws RMapException, RMapDefectiveArgumentException {
		if (rUri==null || cUri == null || ts == null){
			throw new RMapDefectiveArgumentException ("Null parameter");
		}
		List<Statement> triples = null;
		try {
			triples = ts.getStatements(rUri, RDF.TYPE, null, cUri);
		} catch (Exception e) {
			throw new RMapException (e);
		}	
		Set<URI> returnSet = null;
		if (triples!=null && triples.size()>0){
			returnSet = new HashSet<URI>();
			for (Statement stmt:triples){
				Value object = stmt.getObject();
				if (object instanceof URI){
					returnSet.add((URI)object);
				}
			}
			// correct if only triples found had non-URI objects
			if (returnSet.size()==0){
				returnSet = null;
			}
		}
		return returnSet;
	}
	/**
	 * Find types of resource in specific context
	 * @param rUri resource whose type is being checked
	 * @param ts TripleStore
	 * @return Map from context to any types found for that resource in that context, or null if no type statement
	 * found for resource in any context
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public Map<URI, Set<URI>> getResourceRdfTypesAllContexts(URI rUri, SesameTriplestore ts) 
			throws RMapException, RMapDefectiveArgumentException {
		if (rUri==null || ts == null){
			throw new RMapDefectiveArgumentException ("Null parameter");
		}
		List<Statement> triples = null;	
		try {
			triples = ts.getStatementsAnyContext(rUri, RDF.TYPE, null, false);
		} catch (Exception e) {
			throw new RMapException (e);
		}	
		Map<URI, Set<URI>> map = null;
		if (triples !=null && triples.size()>0){
			map = new HashMap<URI, Set<URI>>();
			for (Statement stmt:triples){
				Resource context = stmt.getContext();
				if (!(context instanceof URI)){
					continue;
				}
				Value object = stmt.getObject();
				if (!(object instanceof URI)){
					continue;
				}
				URI uContext = (URI)context;
				URI uObject = (URI)object;
				if (map.containsKey(uContext)){
					Set<URI>types = map.get(uContext);
					types.add(uObject);
				}
				else {
					Set<URI>types = new HashSet<URI>();
					types.add(uObject);
					map.put(uContext, types);
				}
			}
			if (map.keySet().size()==0){
				map = null;
			}
		}
		return map;
	}
	
	
	
	
	
	
}

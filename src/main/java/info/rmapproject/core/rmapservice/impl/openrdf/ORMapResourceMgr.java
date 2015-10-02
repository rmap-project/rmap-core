package info.rmapproject.core.rmapservice.impl.openrdf;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
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
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public Set<URI> getRelatedDiSCOs(URI resource, RMapStatus statusCode, SesameTriplestore ts) 
	throws RMapException, RMapDefectiveArgumentException {
		return getRelatedDiSCOS(resource, statusCode, null, null, null, ts);
	}
	
	/**
	 * Get list of DiSCO URIs that have a statement containing the resource. 
	 * @param resource
	 * @param statusCode
	 * @param systemAgents
	 * @param dateFrom
	 * @param dateTo
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public Set<URI> getRelatedDiSCOS(URI resource, RMapStatus statusCode,
						List<URI> systemAgents, Date dateFrom, Date dateTo, SesameTriplestore ts)
						throws RMapException, RMapDefectiveArgumentException {		
		
	//query gets discoIds and startDates of created DiSCOs that contain Resource
	/*  SELECT DISTINCT ?rmapObjId ?startDate 
		WHERE { 
		GRAPH ?rmapObjId 
		  {
		     {?s ?p <http://dx.doi.org/10.1109/InPar.2012.6339604>} UNION 
		        {<http://dx.doi.org/10.1109/InPar.2012.6339604> ?p ?o} .
		     ?discoId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/DiSCO> .
		  } .
		GRAPH ?eventId {
		 	?eventId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Event> .
			{?eventId <http://www.w3.org/ns/prov#generated> ?rmapObjId} UNION
			{?eventId <http://rmap-project.org/rmap/terms/derivedObject> ?rmapObjId} .
		 	?eventId <http://www.w3.org/ns/prov#startedAtTime> ?startDate .
		 	{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd18nd2m3>} UNION
		 	{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd18nd2p4>} .
		 	}
		  }
		*/

		Set<URI> discos = getRelatedObjects(resource, statusCode, systemAgents, dateFrom, dateTo, ts, RMAP.DISCO);
		return discos;			
	}
	
	
	
	/**
	 * Get list of Agent URIs that have a statement containing the resource. 
	 * @param uri
	 * @param statusCode
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public Set<URI> getRelatedAgents(URI uri, SesameTriplestore ts) 
	throws RMapException, RMapDefectiveArgumentException {
		return getRelatedAgents(uri, null, null, null, ts);
	}

	/**
	 * Get list of RMap Agent URIs that have a statement containing the resource.  
	 * @param uri
	 * @param statusCode  if null, match any status code
	 * @param agentmgr
	 * @param ts
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public Set<URI> getRelatedAgents(URI resource, List<URI> systemAgents, Date dateFrom, Date dateTo, SesameTriplestore ts)
					throws RMapException, RMapDefectiveArgumentException {		
		/*query gets agentIds and startDates of created Agents that contain Resource
	    SELECT DISTINCT ?rmapObjId ?startDate 
		WHERE { 
		GRAPH ?rmapObjId 
		  {
		     {?s ?p <http://isni.org/isni/0000000406115044>} UNION 
		        {<http://isni.org/isni/0000000406115044> ?p ?o} .
		     ?agentId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Agent> .
		  } .
		GRAPH ?eventId {
		 	?eventId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Event> .
			?eventId <http://www.w3.org/ns/prov#generated> ?rmapObjId .
		 	?eventId <http://www.w3.org/ns/prov#startedAtTime> ?startDate .
		 	{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd18nd2m3>} UNION
		 	{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd3jq0>} .
		 	}
		  }
		*/
		//note - active is the only status that is visible, so that is the filter.
		return getRelatedObjects(resource, RMapStatus.ACTIVE, systemAgents, dateFrom, dateTo, ts, RMAP.AGENT);

	}

	/**
	 * Get list of RMap object URIs that have a statement containing the resource, filtered by type DiSCO or Agent.  
	 * @param resource
	 * @param statusCode
	 * @param systemAgents
	 * @param dateFrom
	 * @param dateTo
	 * @param ts
	 * @param rmapType
	 * @return Set<URI>
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	protected Set<URI> getRelatedObjects(URI resource, RMapStatus statusCode, List<URI> systemAgents, 
					Date dateFrom, Date dateTo, SesameTriplestore ts, URI rmapType)
					throws RMapException, RMapDefectiveArgumentException {	
		if (resource==null){
			throw new RMapDefectiveArgumentException ("Null value provided for the Resource URI");
		}
	
		Set<URI> rmapObjectIds = new HashSet<URI>();
		String sResource = OSparqlUtils.convertUriToSparqlParam(resource);
		String sysAgentSparql = OSparqlUtils.convertSysAgentUriListToSparqlFilter(systemAgents);
		String statusFilterSparql = OSparqlUtils.convertRMapStatusToSparqlFilter(statusCode);

		String sparqlQuery = "SELECT DISTINCT ?rmapObjId ?startDate "
							+ "WHERE { "
							+ " GRAPH ?rmapObjId "
							+ "	  {"
							+ "		{?s ?p " + sResource + "} UNION "
							+ "		  {" + sResource + " ?p ?o} ."
							+ "     ?rmapObjId <" + RDF.TYPE + "> <" + rmapType + "> . "							
							+ "	  } . "
							+ " GRAPH ?eventId {"
							+ "   ?eventId <" + RDF.TYPE + "> <" + RMAP.EVENT + "> ."
							+ "   {?eventId <" + PROV.GENERATED + "> ?rmapObjId} UNION "
							+ "   {?eventId <" + RMAP.EVENT_DERIVED_OBJECT + "> ?rmapObjId} ."
							+ "   ?eventId <" + PROV.STARTEDATTIME + "> ?startDate ."
							+     sysAgentSparql
							+ "  } "
							+ statusFilterSparql
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
				URI rmapObjId = (URI) bindingSet.getBinding("rmapObjId").getValue();
				Literal startDateLiteral = (Literal) bindingSet.getBinding("startDate").getValue();
				Date startDate = DateUtils.xmlGregorianCalendarToDate(startDateLiteral.calendarValue());
					
				if ((dateFrom != null && startDate.before(dateFrom))
						|| (dateTo != null && startDate.after(dateTo))) { 
					continue; // don't include out of range date
				}
				rmapObjectIds.add(rmapObjId);
			}
		}	
		catch (RMapException r){throw r;}
		catch (Exception e){
			throw new RMapException("Could not process SPARQL results for resource's associated Objects", e);
		}
	
		return rmapObjectIds;		
	}
	
	
	
	/**
	 * Get list of RMap Agent URIs that have a statement containing the resource.  
	 * @param uri
	 * @param statusCode  if null, match any status code
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public Set<URI> getAllRelatedObjects(URI resource, RMapStatus statusCode,
						List<URI> systemAgents, Date dateFrom, Date dateTo, SesameTriplestore ts)
		throws RMapException, RMapDefectiveArgumentException {		
		Set<URI> objects = new HashSet<URI>();
		try{
			Set<URI> discos = getRelatedDiSCOS(resource, statusCode, systemAgents, dateFrom, dateTo, ts);
			if (discos!=null && discos.size()>0) {
				objects.addAll(discos);
			}

			// Agents cannot be inactive, so if the status filter is set to this, no need to process Agents.
			if (statusCode != RMapStatus.INACTIVE) {
				Set<URI> agents = getRelatedAgents(resource, systemAgents, dateFrom, dateTo, ts);
				if (agents!=null && agents.size()>0) {
					objects.addAll(agents);
				}
			}
		}	
		catch (RMapException r){throw r;}
		catch (Exception e){
			throw new RMapException("Could not process results for resource's related objects", e);
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
	 * @throws RMapDefectiveArgumentException
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
	 * @throws RMapDefectiveArgumentException
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
		// default filter excludes tombstoned objects... applying this so tombstoned object events don't show up.
		String statusFilterSparql = OSparqlUtils.convertRMapStatusToSparqlFilter(null);
		
		//query gets eventIds and startDates of created DiSCOs that contain Resource
		/*  SELECT DISTINCT ?eventId ?startDate 
			WHERE {
			GRAPH ?rmapObjId 
			  {
			     {?s ?p <http://dx.doi.org/10.1109/InPar.2012.6339604>} UNION 
			        {<http://dx.doi.org/10.1109/InPar.2012.6339604> ?p ?o} .
			  } .
			GRAPH ?eventId {
			 	?eventId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Event> .
				?eventId ?eventtype ?rmapObjId .
			 	?eventId <http://www.w3.org/ns/prov#startedAtTime> ?startDate .
			 	{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd18nd2m3>} UNION
			 	{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd18nd2p4>} .
			 	}
			}
			*/
		String sparqlQuery = "SELECT DISTINCT ?eventId ?startDate "
							+ "WHERE { "
							+ " GRAPH ?rmapObjId "
							+ "	  {"
							+ "		{?s ?p " + sResource + "} UNION "
							+ "		  {" + sResource + " ?p ?o} ."						
							+ "	  } . "
							+ " GRAPH ?eventId {"
							+ "   ?eventId <" + RDF.TYPE + "> <" + RMAP.EVENT + "> ."
							+ "   ?eventId ?eventtype ?rmapObjId . "
							+ "   ?eventId <" + PROV.STARTEDATTIME + "> ?startDate ."
							+     sysAgentSparql
							+ "  } "
							+ statusFilterSparql
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
			throw new RMapException("Could not process results for resource's related Events", e);
		}

		return events;		
	}

	/**
	 * Get Statements referencing a URI in subject or object, whose Subject, Predicate, and Object comprise an RMapStatement, 
	 * and (if statusCode is not null), whose status matches statusCode, agent, and date filters
	 * @param resource
	 * @param statusCode
	 * @param systemAgents
	 * @param dateFrom
	 * @param dateTo
	 * @param ts
	 * @return
	 * @throws RMapDefectiveArgumentException
	 * @throws RMapException
	 */
	public Set<Statement> getRelatedTriples(URI resource, RMapStatus statusCode, 			List<URI> systemAgents, Date dateFrom, Date dateTo, SesameTriplestore ts) 
			throws RMapDefectiveArgumentException, RMapException {
		if (resource==null){
			throw new RMapDefectiveArgumentException ("null URI");
		}
		Set<Statement> relatedStmts = new HashSet<Statement>();		
		String sResource = OSparqlUtils.convertUriToSparqlParam(resource);
		String sysAgentSparql = OSparqlUtils.convertSysAgentUriListToSparqlFilter(systemAgents);
		String statusFilterSparql = OSparqlUtils.convertRMapStatusToSparqlFilter(statusCode);
		
		//query gets eventIds and startDates of created DiSCOs that contain Resource
		/*  SELECT DISTINCT ?s ?p ?o ?startDate 
			WHERE {
			GRAPH ?rmapObjId 
			 {
			     {BIND(<http://dx.doi.org/10.1109/InPar.2012.6339604> as ?o) . ?s ?p ?o} UNION 
			     {BIND(<http://dx.doi.org/10.1109/InPar.2012.6339604> as ?s) . ?s ?p ?o} .
			  } .
			GRAPH ?eventId {
			 	?eventId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rmap-project.org/rmap/terms/Event> .
				{?eventId <http://www.w3.org/ns/prov#generated> ?rmapObjId} UNION
				{?eventId <http://rmap-project.org/rmap/terms/derivedObject> ?rmapObjId} .
			 	?eventId <http://www.w3.org/ns/prov#startedAtTime> ?startDate .
			 	{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd18nd2m3>} UNION
			 	{?eventId <http://www.w3.org/ns/prov#wasAssociatedWith> <ark:/22573/rmd18nd2p4>} .
			 	} . 				
				FILTER NOT EXISTS {?statusChangeEventId <http://rmap-project.org/rmap/terms/tombstonedObject> ?rmapObjId} .
				FILTER NOT EXISTS {?statusChangeEventId <http://rmap-project.org/rmap/terms/inactivatedObject> ?rmapObjId} 
			}
			*/
		String sparqlQuery = "SELECT DISTINCT ?s ?p ?o ?startDate "
							+ "WHERE { "
							+ " GRAPH ?rmapObjId "
							+ "	  {"
							+ "		{BIND(" + sResource + " as ?o) . ?s ?p ?o} UNION"
							+ "		{BIND(" + sResource + " as ?s) . ?s ?p ?o} . "						
							+ "	  } . "
							+ " GRAPH ?eventId {"
							+ "   ?eventId <" + RDF.TYPE + "> <" + RMAP.EVENT + "> ."
							+ "   {?eventId <" + PROV.GENERATED + "> ?rmapObjId} UNION"
							+ "   {?eventId <" + RMAP.EVENT_DERIVED_OBJECT + "> ?rmapObjId} ."
							+ "   ?eventId <" + PROV.STARTEDATTIME + "> ?startDate ."
							+     sysAgentSparql
							+ "  } "
							+ statusFilterSparql
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
				Literal startDateLiteral = (Literal) bindingSet.getBinding("startDate").getValue();
				Date startDate = DateUtils.xmlGregorianCalendarToDate(startDateLiteral.calendarValue());
				
				if ((dateFrom != null && startDate.before(dateFrom))
						|| (dateTo != null && startDate.after(dateTo))) { 
					continue; // don't include out of range date
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

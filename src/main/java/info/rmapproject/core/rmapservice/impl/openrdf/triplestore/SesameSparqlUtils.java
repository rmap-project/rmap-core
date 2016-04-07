package info.rmapproject.core.rmapservice.impl.openrdf.triplestore;

import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.request.DateRange;
import info.rmapproject.core.vocabulary.impl.openrdf.PROV;
import info.rmapproject.core.vocabulary.impl.openrdf.RMAP;

import java.util.Set;

import org.openrdf.model.IRI;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;

/**
 * Some common conversions from openrdf model to SPARQL query pieces
 * @author khanson
 *
 */
public class SesameSparqlUtils {

	/**
	 * Converts openrdf Iri to a string that can be slotted into a SPARQL query
	 * @param iri
	 * @return
	 */
	public static String convertIriToSparqlParam(IRI iri) {
		String sIri = iri.stringValue();
		sIri = "<"  + sIri.replace("\"","\\\"") + ">";
		return sIri;
	}
	
	/**
	 * Converts openrdf Value to a string that can be slotted into a SPARQL query
	 * @param iri
	 * @return
	 */
	public static String convertValueToSparqlParam(Value value) {
		String sValueLabel = value.stringValue();
		sValueLabel = sValueLabel.replace("\"","\\\"");
		
		//apply language or datatype filter for object of statement.
		String sValueFull = "";
		if (value instanceof Literal) {
			sValueFull = "\"" + sValueLabel + "\""; // put in quotes
			String lang = "";
			
			Literal lval = (Literal) value;
			if (lval.getLanguage().isPresent()) {
				lang = lval.getLanguage().toString();
			}
			IRI type = ((Literal) value).getDatatype();
			if (lang != null && lang.length() > 0) {
				sValueFull = sValueFull + "@" + lang;
			}
			else if (type != null) {
				sValueFull = sValueFull + "^^<" + type.toString() + ">";
			}
		}
		else { // put it in angle brackets
			sValueFull = "<" + sValueLabel + ">";
		}
		return sValueFull;
	}
	
	
	/**
	 * Converts a list of systemAgent IRIs into a piece of SPARQL that can be added as a filter to some RMap service calls
	 * @param systemAgents
	 * @return
	 */
	public static String convertSysAgentIriListToSparqlFilter(Set<IRI> systemAgents){
		String sysAgentSparql = "";
		
		//build system agent filter SPARQL
		if (systemAgents != null && systemAgents.size()>0) {
			Integer i = 0;			
			for (IRI systemAgent : systemAgents) {
				i=i+1;
				if (i>1){
					sysAgentSparql = sysAgentSparql + " UNION ";
				}
				sysAgentSparql = sysAgentSparql + " {?eventId <" + PROV.WASASSOCIATEDWITH + "> <" + systemAgent.toString() + ">} ";
			}
			sysAgentSparql = sysAgentSparql + " . ";
		}
		
		return sysAgentSparql;
	}

	
	/**
	 * Converts an RMapStatus code to an appropriate SPARQL filter
	 * @param statusCode
	 * @return
	 */
	public static String convertRMapStatusToSparqlFilter(RMapStatus statusCode, String objIdQS) {
		// should not show TOMBSTONED objects... no need to exclude DELETED as these have no statements.
		String filterSparql = " FILTER NOT EXISTS {?statusChangeEventId <" + RMAP.TOMBSTONEDOBJECT + "> " + objIdQS + " } . ";
		if (statusCode != null) {
			if (statusCode == RMapStatus.ACTIVE)	{
				filterSparql = filterSparql + " FILTER NOT EXISTS {?statusChangeEventId <" + RMAP.INACTIVATEDOBJECT + "> " + objIdQS + "} .";
			}
			else if (statusCode == RMapStatus.INACTIVE)	{
				filterSparql = filterSparql + " FILTER EXISTS {?statusChangeEventId <" + RMAP.INACTIVATEDOBJECT + "> " + objIdQS + "} . ";
			}
		}
		return filterSparql;
	}
	

	
	/**
	 * Converts date range to date filter
	 * @param from (date)
	 * @param until (date)
	 * @return
	 */
	public static String convertDateRangeToSparqlFilter(DateRange dateRange, String startDateParam) {
        //FILTER (?startDate > "2016-03-22T10:20:13Z"^^xsd:dateTime) .        
        //FILTER (?startDate < "2016-03-23T10:20:13Z"^^xsd:dateTime)
		String filterSparql = "";
		if (dateRange!=null){
			if (dateRange.getDateFrom()!=null) {
				String from = dateRange.getUTCDateFrom();
				filterSparql = filterSparql + "FILTER (" + startDateParam + " >= \"" + from + "\"^^xsd:dateTime) . ";
			}
			if (dateRange.getDateUntil()!=null) {
				String until = dateRange.getUTCDateUntil();
				filterSparql = filterSparql + "FILTER (" + startDateParam + " <= \"" + until + "\"^^xsd:dateTime) . ";
			}
		}
		return filterSparql;
	}
	
	/**
	 * Creates limit and offset filter for sparql query
	 * @param limit
	 * @param offset
	 * @return
	 */
	public static String convertLimitOffsetToSparqlFilter(Integer limit, Integer offset) {
		String filterSparql = "";
		if (limit!=null){
			filterSparql = " LIMIT " + limit + " ";
		}
		if (offset!=null){
			filterSparql = filterSparql + " OFFSET " + offset + " ";
		}
		return filterSparql;
		
	}
	

	
}

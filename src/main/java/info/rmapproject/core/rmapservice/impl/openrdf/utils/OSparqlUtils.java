package info.rmapproject.core.rmapservice.impl.openrdf.utils;

import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.util.List;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * Some common conversions from openrdf model to SPARQL query pieces
 * @author khanson
 *
 */
public class OSparqlUtils {

	/**
	 * Converts openrdf URI to a string that can be slotted into a SPARQL query
	 * @param uri
	 * @return
	 */
	public static String convertUriToSparqlParam(URI uri) {
		String sUri = uri.stringValue();
		sUri = "<"  + sUri.replace("\"","\\\"") + ">";
		return sUri;
	}
	
	/**
	 * Converts openrdf Value to a string that can be slotted into a SPARQL query
	 * @param uri
	 * @return
	 */
	public static String convertValueToSparqlParam(Value value) {
		String sValueLabel = value.stringValue();
		sValueLabel = sValueLabel.replace("\"","\\\"");
		
		//apply language or datatype filter for object of statement.
		String sValueFull = "";
		if (value instanceof Literal) {
			sValueFull = "\"" + sValueLabel + "\""; // put in quotes
			String lang = ((Literal) value).getLanguage();
			URI type = ((Literal) value).getDatatype();
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
	 * Converts a list of systemAgent URIs into a piece of SPARQL that can be added as a filter to some RMap service calls
	 * @param systemAgents
	 * @return
	 */
	public static String convertSysAgentUriListToSparqlFilter(List<URI> systemAgents){
		String sysAgentSparql = "";
		
		//build system agent filter SPARQL
		if (systemAgents != null && systemAgents.size()>0) {
			Integer i = 0;			
			for (URI systemAgent : systemAgents) {
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
	public static String convertRMapStatusToSparqlFilter(RMapStatus statusCode) {
		// should not show TOMBSTONED objects... no need to exclude DELETED as these have no statements.
		String filterSparql = " . FILTER NOT EXISTS {?statusChangeEventId <" + RMAP.EVENT_TOMBSTONED_OBJECT + "> ?rmapObjId } ";
		if (statusCode != null) {
			if (statusCode == RMapStatus.ACTIVE)	{
				filterSparql = filterSparql + " . FILTER NOT EXISTS {?statusChangeEventId <" + RMAP.EVENT_INACTIVATED_OBJECT + "> ?rmapObjId } ";
			}
			else if (statusCode == RMapStatus.INACTIVE)	{
				filterSparql = filterSparql + " . FILTER EXISTS {?statusChangeEventId <" + RMAP.EVENT_INACTIVATED_OBJECT + "> ?rmapObjId } ";
			}
		}
		return filterSparql;
	}

	
}

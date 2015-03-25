/**
 * 
 */
package info.rmapproject.core.controlledlist;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author smorrissey
 *
 */
public class AgentPredicate {
	private static String PROPERTIES_FN = "agentPredicates";
	private static String separator = ",";
	private static List<URI> agentPredicates;
	/**
	 * 
	 */
	private AgentPredicate() {
		super();
		initAgentPredicates();
	}
	
	/**
	 * Initialize List of validators
	 */
	protected static void initAgentPredicates(){	
		List<String> strPredicates = ControlledList.initControlledList(PROPERTIES_FN, separator, true);
		if (strPredicates != null){
			agentPredicates = new ArrayList<URI>();
			for (String uriStr:strPredicates){
				URI uri = null;
				try {
					uri = new URI(uriStr);
					agentPredicates.add(uri);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}			
			}
		}
	}
	/**
	 * 
	 * @param predicate
	 * @return
	 */
	public static boolean isAgentPredicate(URI predicate){
		boolean isAP = false;
		if (agentPredicates==null){
			initAgentPredicates();
		}
		isAP = agentPredicates.contains(predicate);
		return isAP;
	}

}

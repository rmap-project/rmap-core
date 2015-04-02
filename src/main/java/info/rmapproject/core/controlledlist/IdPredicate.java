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
public class IdPredicate {

	private static String PROPERTIES_FN = "idPredicates";
	private static String separator = ",";
	private static List<URI> idPredicates;
	/**
	 * 
	 */
	private IdPredicate() {
		super();
		initIdPredicates();
	}
	
	/**
	 * Initialize List of validators
	 */
	protected static void initIdPredicates(){	
		List<String> strPredicates = ControlledList.initControlledList(PROPERTIES_FN, separator, true);
		if (strPredicates != null){
			idPredicates = new ArrayList<URI>();
			for (String uriStr:strPredicates){
				URI uri = null;
				try {
					uri = new URI(uriStr);
					idPredicates.add(uri);
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
	public static boolean isIdPredicate(URI predicate){
		boolean isAP = false;
		if (idPredicates==null){
			initIdPredicates();
		}
		isAP = idPredicates.contains(predicate);
		return isAP;
	}

}

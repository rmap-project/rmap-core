/**
 * 
 */
package info.rmapproject.core.idvalidator;

import info.rmapproject.core.utils.ConfigUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.Predicate;

/**
 * Class to validate "accepted" IDs
 * @author smorrissey
 *
 */
public class IdValidator {

	private static String PROPERTIES_FN = "idValidator";
	private static String agentValidatorPropName = "agentIdValidatorKeys";
	private static String separator = ",";
	private static List<Predicate<Object>> idValidators;	
	
	/**
	 * 
	 */
	protected IdValidator() {
		super();
		initValidators();
	}
	
	/**
	 * Initialize List of validators
	 */
	protected static void initValidators(){
		idValidators = new ArrayList<Predicate<Object>>();
		List<String> classKeys = ConfigUtils.getPropertyValueList(PROPERTIES_FN, 
				agentValidatorPropName, separator);
		if (classKeys != null){
			for (String classKey:classKeys){
				String className = ConfigUtils.getPropertyValue(PROPERTIES_FN, classKey);
				try {
					@SuppressWarnings("unchecked")
					Predicate<Object> predicate = (Predicate<Object>) 
							Class.forName(className).newInstance();
					idValidators.add(predicate);
				} catch (InstantiationException | IllegalAccessException
						| ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Check agent id against known agent id validators
	 * @param uri ID to be validated, as URI
	 * @return true if validates against one of id validators, else false
	 */
	public static boolean isValidAgentId (URI uri){
		if (idValidators==null){
			initValidators();
		}
		boolean isValid = false;
		for (Predicate<Object> idValidator:idValidators){
			isValid = idValidator.evaluate(uri);
			if (isValid){
				break;
			}
		}
		return isValid;
	}
}

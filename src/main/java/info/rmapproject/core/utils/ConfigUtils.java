/**
 * 
 */
package info.rmapproject.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.PatternSyntaxException;

/**
 * @author smorrissey
 *
 */
public class ConfigUtils {
	
	public static final String DEFAULT_PROP_SEPARATOR = ",";

	/**
	 * Convenience method for extracting a single property name/value pair from a property file
	 * @param propFileName base name for property file
	 * @param propKey propery name
	 * @return String containing proerty value, or null if not found
	 * @throws MissingResourceException
	 */
	public static String getPropertyValue(String propFileName, String propKey) throws NullPointerException, MissingResourceException {
		String propValue = null;
		ResourceBundle resources = ResourceBundle.getBundle(propFileName, Locale.getDefault());
		for (String key:resources.keySet()){
			if (key.equals(propKey)){
				propValue = resources.getString(key);
				break;
			}
		}			
		return propValue;
	}
	/**
	 * Convenience method for extracting several name/value pairs from a single property file
	 * @param propFileName base name of property file
	 * @return Map<String, String> of all name/value pairs in file
	 * @throws NullPointerException
	 * @throws MissingResourceException
	 */
	public static Map<String, String> getPropertyValues(String propFileName) throws NullPointerException, MissingResourceException {
		Map<String, String> props = null;
		try{
			ResourceBundle resources = ResourceBundle.getBundle(propFileName, Locale.getDefault());
			props = new HashMap<String, String>();
			for (String key:resources.keySet()){
				props.put(key, resources.getString(key));
			}			
		}
		catch (NullPointerException e){
			throw e;
		}
		catch(MissingResourceException e){
			throw e;
		}
		return props;
	}
	/**	
	 * Get property value that is a list of Strings rather than a single String value
	 * @param propFileName name of property file
	 * @param propKey Property key
	 * @param separator pattern for Separator character in list of String values (if null, default is ",")
	 * @return List of values for property, or null if property not found
	 * @throws NullPointerException
	 * @throws MissingResourceException
	 * @throws PatternSyntaxException
	 */
	public static List<String> getPropertyValueList (String propFileName, String propKey, String separator) 
	throws NullPointerException, MissingResourceException, PatternSyntaxException  {
		String valueString = null;
		List<String>valueList = null;
		ResourceBundle resources = ResourceBundle.getBundle(propFileName, Locale.getDefault());
		for (String key:resources.keySet()){
			if (key.equals(propKey)){
				valueString = resources.getString(key);
				break;
			}
		}
		if (valueString !=null){
			String sep = separator;
			if (sep == null){
				sep = DEFAULT_PROP_SEPARATOR;
			}
			String[] values = valueString.split(sep);
			valueList = new ArrayList<String>();
			for (String value:values){
				valueList.add(value);
			}
		}
		return valueList;
	}

}

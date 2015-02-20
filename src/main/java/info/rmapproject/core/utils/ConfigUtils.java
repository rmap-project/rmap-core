/**
 * 
 */
package info.rmapproject.core.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author smorrissey
 *
 */
public class ConfigUtils {

	/**
	 * Convenience method for extracting a single property name/value pair from a property file
	 * @param propFileName base name for property file
	 * @param propKey propert name
	 * @return String containing proerty value, or null if not found
	 * @throws MissingResourceException
	 */
	public static String getPropertyValue(String propFileName, String propKey) throws NullPointerException, MissingResourceException {
		String className = null;
		ResourceBundle resources = ResourceBundle.getBundle(propFileName, Locale.getDefault());
		for (String key:resources.keySet()){
			if (key.equals(propKey)){
				className = resources.getString(key);
			}
		}			
		return className;
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

}

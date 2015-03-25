/**
 * 
 */
package info.rmapproject.core.controlledlist;

import info.rmapproject.core.utils.ConfigUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author smorrissey
 *
 */
public abstract class ControlledList {

	/**
	 * 
	 */
	protected ControlledList() {
		super();
	}
	/**
	 * Construct controlled list from properties file
	 * @param PROPERTIES_FN property file name
	 * @param separator property list separator character
	 * @param concat indicates whether to concat property name to each value in property value list in returned list
	 * @return controlled list of values
	 */
	protected static List<String> initControlledList (String PROPERTIES_FN, String separator, boolean concat ){
		List <String> list = new ArrayList<String>();
		Map<String,List<String>> key2values = ConfigUtils.getPropertyValuesList(PROPERTIES_FN, separator);
		if (key2values != null){
			list = new ArrayList<String>();
			for (String key:key2values.keySet()){
				List<String> terms = key2values.get(key);
				for (String term: terms){
					if (concat){
						list.add(key.concat(term));
					}
					else {
						list.add(term);
					}
				}
			}
		}		
		return list;		
	}
}

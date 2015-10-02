/**
 * 
 */
package info.rmapproject.core.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author smorrissey
 *
 */
public class Utils {
	/**
	 * Method to invert keys and values in a Map
	 * @param inMap Map to be inverted
	 * @return inverted Map
	 */
	public static <K, V> Map<V, K> invertMap(Map<K,V> inMap){
		Map<V, K> outMap = new HashMap<V, K>();
		for (K key:inMap.keySet()){
			outMap.put(inMap.get(key), key);
		}
		return outMap;
	}
	
}

/**
 * 
 */
package info.rmapproject.core.utils;

import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * Some common utils used in RMap Core
 *
 * @author smorrissey
 */
public class Utils {
	
	/**
	 * Method to invert keys and values in a Map.
	 *
	 * @param <K> the key type
	 * @param <V> the value type
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

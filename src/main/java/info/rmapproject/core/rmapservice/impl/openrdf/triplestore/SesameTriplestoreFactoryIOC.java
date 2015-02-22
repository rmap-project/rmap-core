/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf.triplestore;

import java.util.MissingResourceException;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.utils.ConfigUtils;


/**
 * @author khansen, smorrissey
 *
 */
public class SesameTriplestoreFactoryIOC {

	private static final String FACTORY_PROPERTIES = "openrdf";  // Name of properties file with concrete factory class name
	private static final String FACTORY_KEY = "triplestoreFactoryClass";
	private static String factoryClassName = null;
	private static SesameTriplestoreFactory factory = null;
	
	static{
		try {
			factoryClassName = ConfigUtils.getPropertyValue(FACTORY_PROPERTIES, FACTORY_KEY);
			factory = (SesameTriplestoreFactory) Class.forName(factoryClassName).newInstance();
		}
		catch(MissingResourceException me){}
		catch (Exception e){}
	}
	/**
	 * 
	 */
	private SesameTriplestoreFactoryIOC() {}
	
	
	public static SesameTriplestoreFactory getFactory() throws RMapException {
		if (factory==null){
			throw new RMapException("Factory not available");
		}
		return factory;
	}

}

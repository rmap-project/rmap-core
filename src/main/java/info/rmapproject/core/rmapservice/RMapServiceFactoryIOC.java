/**
 * 
 */
package info.rmapproject.core.rmapservice;

import java.util.MissingResourceException;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.utils.ConfigUtils;


/**
 * @author smorrissey
 *
 */
public class RMapServiceFactoryIOC {

	private static final String FACTORY_PROPERTIES = "rmapServiceFactory";  // Name of properties file with concrete factory class name
	private static final String FACTORY_KEY = "rmapServiceFactoryClass";
	private static String factoryClassName = null;
	private static RMapServiceFactory factory = null;
	
	static{
		try {
			factoryClassName = ConfigUtils.getPropertyValue(FACTORY_PROPERTIES, FACTORY_KEY);
			factory = (RMapServiceFactory) Class.forName(factoryClassName).newInstance();
		}
		catch(MissingResourceException me){}
		catch (Exception e){}
	}
	/**
	 * 
	 */
	private RMapServiceFactoryIOC() {}
	
	
	public static RMapServiceFactory getFactory() throws RMapException {
		if (factory==null){
			throw new RMapException("Factory not available");
		}
		return factory;
	}

}

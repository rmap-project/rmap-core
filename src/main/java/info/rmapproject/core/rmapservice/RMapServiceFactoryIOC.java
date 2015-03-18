/**
 * 
 */
package info.rmapproject.core.rmapservice;

import java.util.MissingResourceException;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.utils.ConfigUtils;


/**
 *  @author khansen, smorrissey
 *
 */
public class RMapServiceFactoryIOC {

	private static final String FACTORY_PROPERTIES = "rmapServiceFactory";  // Name of properties file with concrete factory class name
	private static final String FACTORY_KEY = "rmapServiceFactoryClass";
	private static String factoryClassName = null;
	private static RMapServiceFactory factory = null;
	
	protected static void init () throws Exception{
		try {
			factoryClassName = ConfigUtils.getPropertyValue(FACTORY_PROPERTIES, FACTORY_KEY);
			factory = (RMapServiceFactory) Class.forName(factoryClassName).newInstance();
		}
		catch(MissingResourceException me){throw me;}
		catch (Exception e){throw e;}
	}
	/**
	 * 
	 */
	private RMapServiceFactoryIOC() {}
	
	
	public static RMapServiceFactory getFactory() throws RMapException {
		if (factory==null){
			try {
				init();
			}
			catch (Exception e) {throw new RMapException (e);}
		}
		return factory;
	}

}

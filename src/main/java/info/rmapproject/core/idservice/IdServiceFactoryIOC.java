/**
 * 
 */
package info.rmapproject.core.idservice;

import java.util.MissingResourceException;

import info.rmapproject.core.utils.ConfigUtils;


/**
 * @author  khansen, smorrissey
 *
 */
public class IdServiceFactoryIOC {

	private static final String FACTORY_PROPERTIES = "idServiceFactory";  // Name of properties file with concrete factory class name
	private static final String FACTORY_KEY = "idserviceFactoryClass";
	private static String factoryClassName = null;
	private static IdServiceFactory idserviceFactory = null;
	
	static{
		try {
			factoryClassName = ConfigUtils.getPropertyValue(FACTORY_PROPERTIES, FACTORY_KEY);
			idserviceFactory = (IdServiceFactory) Class.forName(factoryClassName).newInstance();
		}
		catch(MissingResourceException me){}
		catch (Exception e){}
	}
	/**
	 * 
	 */
	private IdServiceFactoryIOC() {}
	
	
	public static IdServiceFactory getFactory() throws Exception {
		if (idserviceFactory==null){
			throw new Exception("No idservicefactory configured");
		}
		return idserviceFactory;
	}
}

/**
 * 
 */
package info.rmapproject.core.rdfhandler;

import java.util.MissingResourceException;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.utils.ConfigUtils;

/**
 * @author smorrissey
 *
 */
public class RDFHandlerFactoryIOC {


	private static final String FACTORY_PROPERTIES = "rdfHandlerFactory";  // Name of properties file with concrete factory class name
	private static final String FACTORY_KEY = "rdfHandlerFactoryClass";
	private static String factoryClassName = null;
	private static RDFHandlerFactory factory = null;

	static{
		try {
			factoryClassName = ConfigUtils.getPropertyValue(FACTORY_PROPERTIES, FACTORY_KEY);
			factory = (RDFHandlerFactory) Class.forName(factoryClassName).newInstance();
		}
		catch(MissingResourceException me){}
		catch (Exception e){}
	}
	/**
	 * 
	 */
	private RDFHandlerFactoryIOC() {}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rdfhandler.RDFHandlerFactory#createRDFHandler()
	 */
	public static RDFHandlerFactory getFactory() throws RMapException {
		if (factory==null){
			throw new RMapException("Factory not available");
		}
		return factory;
	}

}

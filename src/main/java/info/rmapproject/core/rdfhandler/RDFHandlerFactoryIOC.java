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


	private static final String FACTORY_PROPERTIES = "rdfhandlerFactory";  // Name of properties file with concrete factory class name
	private static final String FACTORY_KEY = "rdfHandlerFactoryClass";
	private static String factoryClassName = null;
	protected static RDFHandlerFactory factory = null;

	/**
	 * 
	 */
	protected RDFHandlerFactoryIOC() {}
	
	protected static void init() throws Exception{
		try {
			factoryClassName = ConfigUtils.getPropertyValue(FACTORY_PROPERTIES, FACTORY_KEY);
			factory = (RDFHandlerFactory) Class.forName(factoryClassName).newInstance();
		}
		catch(MissingResourceException me){throw me;}
		catch (Exception e){throw e;}
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rdfhandler.RDFHandlerFactory#createRDFHandler()
	 */
	public static RDFHandlerFactory getFactory() throws RMapException {
		if (factory==null){
			try {
				init();
			}
			catch (Exception e){
				throw new RMapException (e);
			}
		}
		return factory;
	}

}

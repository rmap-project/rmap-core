package info.rmapproject.core.rdfhandler;

import info.rmapproject.core.utils.ConfigUtils;

import java.util.MissingResourceException;


public final class RDFHandlerIOC {
	private static final String FACTORY_PROPERTIES = "openrdf";  // Name of properties file with concrete factory class name
	private static final String FACTORY_KEY = "RDFPROCESSOR_TYPE";
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
    private RDFHandlerIOC(){};
	
    public static RDFHandlerFactory getFactory() throws Exception {
		if (factory==null){
			throw new Exception("No RDFHandlerFactory configured");
		}
		return factory;
	}
}

/**
 * 
 */
package info.rmapproject.core.idservice;

import java.net.URI;
import java.util.MissingResourceException;

import org.apache.commons.lang.RandomStringUtils;

import info.rmapproject.core.utils.ConfigUtils;
import info.rmapproject.core.utils.Constants;

/**
 * This is a random number generator that generates FAKE ARK IDs for testing
 * THIS SHOULD NOT BE USED IN PRODUCTION!
 *
 * @author khanson, smorrissey
 */
public class RNIdService implements IdService {

	/** The property key for the ARK's naan */
	private static final String NAAN_PROPERTY = "arkservice.idNaan";
	
	/** The naan. */
	private static String NAAN = null;
	
	/** The arkscheme. */
	private static final String ARKSCHEME = "ark";
	
	static{
		try {
			NAAN = ConfigUtils.getPropertyValue(Constants.ARKSERVICE_PROPFILE,NAAN_PROPERTY);
		}
		catch(MissingResourceException me){}
		catch (Exception e){}
	}

	/**
	 * Instantiates a new Random Number id service.
	 */
	public RNIdService() {}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.idservice.IdService#createId()
	 */
	public URI createId() throws Exception {
		URI uri = null;
		String id = RandomStringUtils.randomAlphanumeric(12).toLowerCase();
		String ssp = "/" + NAAN + "/" + id;
		uri= new URI(ARKSCHEME, ssp, null);
		return uri;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.idservice.IdService#isValidId(java.net.URI)
	 */
	public boolean isValidId(URI id) throws Exception {
		String prefix = "ark:/"+ NAAN + "/";
		boolean isValid = id.toASCIIString().startsWith(prefix);
		return isValid;
	}

}

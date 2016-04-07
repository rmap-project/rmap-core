/**
 * 
 */
package info.rmapproject.core.idservice;

import java.net.URI;
import java.util.MissingResourceException;

import org.apache.commons.lang.RandomStringUtils;

import info.rmapproject.core.utils.ConfigUtils;

/**
 * @author khanson, smorrissey
 *
 */
public class RNIdService implements IdService {
	
	private static final String ARK_PROPERTIES = "ark";
	private static String NAAN_KEY = "naan";
	private static String NAAN = null;
	private static String ARKSCHEME = "ark";
	
	static{
		try {
			NAAN = ConfigUtils.getPropertyValue(ARK_PROPERTIES,NAAN_KEY);
		}
		catch(MissingResourceException me){}
		catch (Exception e){}
	}

	/**
	 * 
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

	public boolean isValidId(URI id) throws Exception {
		String prefix = "ark:/"+ NAAN + "/";
		boolean isValid = id.toASCIIString().startsWith(prefix);
		return isValid;
	}

}

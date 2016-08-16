package info.rmapproject.core.idservice;

import info.rmapproject.core.utils.ConfigUtils;
import info.rmapproject.core.utils.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ARK ID generator, taken from Portico and modified for use in RMap. 
 * @author Nigel Kerr, khanson
 */
public class ArkIdService implements IdService {
	
	/** The log. */
	private final Logger log = LogManager.getLogger(this.getClass());
	
	/** The property key for buffer size */
	private static final String BUFFER_PROPERTY = "arkservice.bufferSize";
	
	/** The property key for prefix */
	private static final String PREFIX_PROPERTY = "arkservice.idPrefix";
	
	/** The property key for the ARK's naan */
	private static final String NAAN_PROPERTY = "arkservice.idNaan";
	
	/** The property key for the ARK service URL */
	private static final String URL_PROPERTY = "arkservice.url";
	
	/** The property key that determines how many retries are attempted after a failed service call*/
	private static final String MAX_RETRY_PROPERTY = "arkservice.maxRetries";
	
	/** The default maximum number of retries */
	private static final String DEFAULT_MAX_RETRY = "2";

	/** An instance of the ArkIdService */
	private static ArkIdService instance = new ArkIdService();

	/** List of available noids */
	private final List<String> noids = new ArrayList<String>();

	/** Set a maximum retry attempts value if you want to cap the property setting. -1 means not maximum */
	private int maxRetryAttempts = -1;
	
	/** The ARK ID service url. */
	private String serviceUrl = "";
	
	/** The ARK ID buffer size. */
	private int bufferSize = -1;
	
	/** The ARK ID naan identifier. */
	private String naanIdentifier = "";
	
	/** The ARK prefix. */
	private String arkPrefix = "ark:/";
	
	/**
	 * Instantiates a new ARK ID service.
	 */
	public ArkIdService() {
		this(Constants.ARKSERVICE_PROPFILE);
	}

	/**
	 * Instantiates a new ARK ID with properties service.
	 *
	 * @param propertyFileName the property file name
	 */
	public ArkIdService(String propertyFileName) {		
		Map<String, String> properties = new HashMap<String, String>();
		
		properties = ConfigUtils.getPropertyValues(propertyFileName);
		serviceUrl = properties.get(URL_PROPERTY);
		arkPrefix = properties.get(PREFIX_PROPERTY);
		naanIdentifier = properties.get(NAAN_PROPERTY);
		bufferSize = Integer.parseInt(properties.getOrDefault(BUFFER_PROPERTY,"1"));
		maxRetryAttempts = Integer.parseInt(properties.getOrDefault(MAX_RETRY_PROPERTY,DEFAULT_MAX_RETRY));
	}
	
	/**
	 * Check a valid id being returned.
	 *
	 * @param id the id
	 * @return boolean
	 * @throws Exception the exception
	 */
	public boolean isValidId(URI id) throws Exception {
		
		boolean isValid = id.toASCIIString().startsWith(arkPrefix.concat(naanIdentifier));
		return isValid;
	}
	

	/**
	 * Returns the noid id from the specific ArrayList tied to that Content Type
	 * in the Hashtable. If noidID list is zero, then it goes into a loop until
	 * the list is populated. List is populated by http request to noid service.
	 * Also, the noid ids obtained from the noid service is validated against
	 * the env prefix defined in LDAP. Also, the number of ids requested also
	 * defined in LDAP
	 *
	 * @return the noid id
	 * @throws Exception the exception
	 */

	public synchronized String getNoidId() throws Exception {
		log.debug("Getting noid id");

		if (noids.size() <= 0) {
			try {
				getMoreNoids();
			} catch (Exception e) {
				log.fatal("While trying to fill more noids, caught exception",
						e);
			}
		}
		if (noids.size() > 0) {
			return noids.remove(noids.size()-1);
		} else {
			throw new Exception(
					"Tried to fill noids and failed!  No Noids available!");
		}
	}

	/**
	 * Gets the number of NOIDs available in the list in memory
	 *
	 * @return the number of noids available
	 */
	public int howManyAvailable() {
		return noids.size();
	}


	/**
	 * When the list of NOID IDs to use for the ARKs is empty, this method refills the list
	 *
	 * @throws Exception the exception
	 */
	private synchronized void getMoreNoids() throws Exception {

		maxRetryAttempts = 2;
		int retryCounter = 0;

		int HTTP_STATUS_OK = 200;

		String url = serviceUrl + "?" + bufferSize;

		BufferedReader reader = null;
		boolean shouldRetry = true;
		do {
			retryCounter++;
			log.debug("Minting ids");
			log.debug("Requested ids = |" + bufferSize + "|");
			URL noidUrl = null;
			HttpURLConnection noidCon = null;
			try {
				noidUrl = new URL(url);
				noidCon = (HttpURLConnection) noidUrl.openConnection();
				noidCon.setDoInput(true);
				noidCon.setDoOutput(false);
				noidCon.connect();
				reader = new BufferedReader(new InputStreamReader(noidCon
						.getInputStream()));				
					String output = null;
					if (noidCon.getResponseCode() == HTTP_STATUS_OK) {
						while ((output = reader.readLine()) != null) {
							if (!output.equals("")) {
								if (output.indexOf(naanIdentifier) != -1) {
									output = output.trim().substring(output.indexOf(" ") + 1);
									output = arkPrefix + output;
									noids.add(output);
								} else {
									log.warn("NOID SERVICE RETURNED AN UNEXPECTED RESULT : "
													+ output);
								}
							}
						}
					} else {
						log.fatal("UNSUCCESSFUL HTTP REQUEST TO NOID SERVICE and  HTTP RETURN CODE is : " + noidCon.getResponseCode());
					}
			} catch(Exception e){
				log.fatal("EXCEPTION CONNECTING TO NOID SERVER", e);

			} finally {

				try {
					if (reader != null){
						reader.close();
					}
				} catch (Exception e) {
					log.fatal("Exception while closing Buffered Reader", e);
				}
				try {
					if (noidCon != null){
						noidCon.disconnect();
					}
				} catch (Exception e) {
					log.fatal("Exception while closing http connection to noid service ", e);
				}
			}
			shouldRetry = (retryCounter < maxRetryAttempts && noids.size() == 0);
			//WAIT FOR 10 SECS BEFORE RE-TRYING TO OVERCOME TEMPORARY NETWORK FAILURES
			//OR THE NOID SERVER BEING BUSY SERVICING ANOTHER REQUEST.
			if(shouldRetry){
				try{
					wait(10000);
				}catch(InterruptedException ie){
					log.fatal("Wait interrupted in retry loop", ie);
				}

			}

		} while (shouldRetry);

		log.debug("Extracted ids = |" + noids.size() + "|");
		if(noids.size() == 0){
			throw new Exception("Could not retrieve new ARK IDs after retries. maxRetryAttempts:"+maxRetryAttempts);
		}
	}

	/**
	 * Gets the current instance of ArkIdService.
	 *
	 * @return instance of ArkIdService
	 */
	public static ArkIdService getInstance() {
		return instance;
	}

	/**
	 * Sets the current ArkIdService instance.
	 *
	 * @param instance the new Ark ID service instance
	 */
	public static void setInstance(ArkIdService instance) {
		ArkIdService.instance = instance;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.idservice.IdService#createId()
	 */
	public URI createId() throws Exception {
			try {
				return new URI(ArkIdService.getInstance().getNoidId());
				// need to instead return configured per Component.getInstance("noidServiceImpl")
			} catch (Exception e) {
				throw new Exception("failed to get id from IdServiceImpl, caught exception", e);
			}
	}
}
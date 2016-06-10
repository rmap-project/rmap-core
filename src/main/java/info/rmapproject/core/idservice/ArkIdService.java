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
import org.springframework.context.annotation.Scope;

/**
 * ARK ID generator, taken from Portico and modified for use in RMap. 
 * @author Nigel Kerr, khanson
 */
public class ArkIdService implements IdService {
	
	private final Logger log = LogManager.getLogger(this.getClass());
	
	private static final String BUFFER_PROPERTY = "arkservice.bufferSize";
	private static final String PREFIX_PROPERTY = "arkservice.idPrefix";
	private static final String NAAN_PROPERTY = "arkservice.idNaan";
	private static final String URL_PROPERTY = "arkservice.url";
	private static final String MAX_RETRY_PROPERTY = "arkservice.maxRetries";
	private static final String DEFAULT_MAX_RETRY = "2";

	private static ArkIdService instance = new ArkIdService();

	private final List<String> noids = new ArrayList<String>();

	private int maxRetryAttempts = -1;
	private String serviceUrl = "";
	private int bufferSize = -1;
	private String naanIdentifier = "";
	private String arkPrefix = "ark:/";
	
	public ArkIdService() {
		this(Constants.ARKSERVICE_PROPFILE);
	}

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
	 * @return boolean
	 * @throws Exception
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
	 * @return
	 * @throws Exception
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

	public int howManyAvailable() {
		return noids.size();
	}


	/** replace this with httpclient or some such at some point.
	 * Does this method have to be synchronized?  getNoidId is the only caller.  */
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

	public static ArkIdService getInstance() {
		return instance;
	}

	public static void setInstance(ArkIdService instance) {
		ArkIdService.instance = instance;
	}

	public URI createId() throws Exception {
			try {
				return new URI(ArkIdService.getInstance().getNoidId());
				// need to instead return configured per Component.getInstance("noidServiceImpl")
			} catch (Exception e) {
				throw new Exception("failed to get id from IdServiceImpl, caught exception", e);
			}
	}
}
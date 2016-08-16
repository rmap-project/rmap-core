/**
 * 
 */
package info.rmapproject.core.idservice;

import java.net.URI;

/**
 * Interface for ID creation. 
 * @author  khanson, smorrissey
 *
 */
public interface IdService {
	
	/**
	 * Creates the id.
	 *
	 * @return a newly minted ID as a URI
	 * @throws Exception the exception
	 */
	public URI createId() throws Exception;
	
	/**
	 * Checks if is valid id.
	 *
	 * @param id the ID
	 * @return true, if the ID is valid
	 * @throws Exception the exception
	 */
	public boolean isValidId(URI id) throws Exception;
}

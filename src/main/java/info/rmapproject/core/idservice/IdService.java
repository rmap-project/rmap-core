/**
 * 
 */
package info.rmapproject.core.idservice;

import java.net.URI;


/**
 * @author  khanson, smorrissey
 *
 */
public interface IdService {
	public URI createId() throws Exception;
	public boolean isValidId(URI id) throws Exception;
}

/**
 * 
 */
package info.rmapproject.core.model;


import info.rmapproject.core.exception.RMapException;

import java.net.URI;

/**
 * @author smorrissey
 *
 */
public interface RMapObject {

	/**
	 * 
	 * @return
	 */
	public URI getId();
	/**
	 * 
	 * @return
	 */
	public URI getType() throws RMapException;

}

/**
 * 
 */
package info.rmapproject.core.rmapservice;

import info.rmapproject.core.exception.RMapException;

/**
 * @author smorrissey
 *
 */
public interface RMapServiceFactory {
	
	RMapService createService() throws RMapException;
}

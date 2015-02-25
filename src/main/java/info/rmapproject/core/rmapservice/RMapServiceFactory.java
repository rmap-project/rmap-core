/**
 * 
 */
package info.rmapproject.core.rmapservice;

import info.rmapproject.core.exception.RMapException;

/**
 *  @author khansen, smorrissey
 *
 */
public interface RMapServiceFactory {
	
	public RMapService createService() throws RMapException;
}

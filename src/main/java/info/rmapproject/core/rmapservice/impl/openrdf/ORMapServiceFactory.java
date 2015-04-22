/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactory;

/**
 * @author khansen, smorrissey
 *
 */
public class ORMapServiceFactory implements RMapServiceFactory {
	/**
	 * 
	 */
	public ORMapServiceFactory() {}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.RMapServiceFactory#createService()
	 */
	public RMapService createService() throws RMapException {
		return new ORMapService();
	}

}

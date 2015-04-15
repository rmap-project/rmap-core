/**
 * 
 */
package info.rmapproject.core.idservice.impl.randomnumber;

import info.rmapproject.core.idservice.IdService;
import info.rmapproject.core.idservice.IdServiceFactory;

/**
 * @author  khanson, smorrissey
 *
 */
public class RNIdServiceFactory implements IdServiceFactory {

	private static IdService service = new RNIdService();
	/**
	 * 
	 */
	public RNIdServiceFactory() {}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.idservice.IdServiceFactory#createService()
	 */
	public IdService createService() throws Exception {
		return service;
	}

}

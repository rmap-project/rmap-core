/**
 * 
 */
package info.rmapproject.core.idservice.impl.ark;

import info.rmapproject.core.idservice.IdService;
import info.rmapproject.core.idservice.IdServiceFactory;

/**
 * @author smorrissey
 *
 */
public class ArkIdServiceFactory implements IdServiceFactory {

	private static IdService service = new ArkIdService();
	/**
	 * 
	 */
	public ArkIdServiceFactory() {}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.idservice.IdServiceFactory#createService()
	 */
	public IdService createService() throws Exception {
		return service;
	}

}

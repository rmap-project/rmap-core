/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf.triplestore.http;

import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactory;

/**
 *  @author khansen, smorrissey
 *
 */
public class SesameHttpTriplestoreFactory implements SesameTriplestoreFactory {
	
	private static SesameTriplestore ts = new SesameHttpTriplestore();

	/**
	 * 
	 */
	public SesameHttpTriplestoreFactory() {}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactory#createTriplestore()
	 */
	public SesameTriplestore createTriplestore() throws Exception {
		return ts;
	}

}

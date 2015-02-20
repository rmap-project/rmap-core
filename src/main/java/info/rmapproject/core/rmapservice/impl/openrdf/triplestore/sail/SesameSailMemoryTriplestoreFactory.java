/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf.triplestore.sail;

import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactory;

/**
 * @author smorrissey
 *
 */
public class SesameSailMemoryTriplestoreFactory implements SesameTriplestoreFactory {
	private static SesameTriplestore ts = new SesameSailMemoryTriplestore();

	/**
	 * 
	 */
	public SesameSailMemoryTriplestoreFactory() {}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactory#createTriplestore()
	 */
	public SesameTriplestore createTriplestore() throws Exception {
		return ts;
	}

}

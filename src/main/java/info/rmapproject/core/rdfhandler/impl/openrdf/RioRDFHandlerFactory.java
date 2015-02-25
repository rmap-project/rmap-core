/**
 * 
 */
package info.rmapproject.core.rdfhandler.impl.openrdf;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactory;

/**
 * @author smorrissey
 *
 */
public class RioRDFHandlerFactory implements RDFHandlerFactory {

	/**
	 * 
	 */
	private RioRDFHandlerFactory() {}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rdfhandler.RDFHandlerFactory#createRDFHandler()
	 */
	@Override
	public RDFHandler createRDFHandler() throws RMapException {
		return new RioRDFHandler();
	}

}

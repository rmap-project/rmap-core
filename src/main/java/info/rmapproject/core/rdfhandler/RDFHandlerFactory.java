/**
 * 
 */
package info.rmapproject.core.rdfhandler;

import info.rmapproject.core.exception.RMapException;

/**
 * @author smorrissey
 *
 */
public interface RDFHandlerFactory {
	
	public RDFHandler createRDFHandler() throws RMapException;
}

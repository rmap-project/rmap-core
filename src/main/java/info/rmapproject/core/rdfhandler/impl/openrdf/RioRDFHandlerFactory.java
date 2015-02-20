package info.rmapproject.core.rdfhandler.impl.openrdf;

import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactory;

public class RioRDFHandlerFactory implements RDFHandlerFactory {

	private static RDFHandler rdfHandler = new RioRDFHandler();
	
	public RDFHandler getRDFHandler() throws Exception	{
		return rdfHandler;
	}
}

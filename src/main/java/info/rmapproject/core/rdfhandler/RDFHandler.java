/**
 * 
 */
package info.rmapproject.core.rdfhandler;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapAgent;
import info.rmapproject.core.model.RMapDiSCO;
import info.rmapproject.core.model.RMapEvent;
import info.rmapproject.core.model.RMapStatement;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author smorrissey
 *
 */
public interface RDFHandler {
	/**
	 * Deserialize an RDF InputStream into an RMapDiSCO
	 * @param rdfIn an RDF InputStream
	 * @param baseUri for resolving relative URIs; null if no relative URIs in stream
	 * @return RMapDiSCO built from RDF statements in InputStream
	 * @throws RMapException if InputStream cannot be converted to valid DiSCO
	 */
	public RMapDiSCO rdf2RMapDiSCO(InputStream rdfIn, String baseUri) throws RMapException;
	/**
	 * Serialize RMapStatement as RDF
	 * @param stmt RMapStatement to be serialized
	 * @param rdfFormat RDF Format to be used in serialization
	 * @return OutputStream with serialized RDF
	 * @throws RMapException if RMapStatement cannot be serialized as RDF
	 */
	public OutputStream statement2Rdf(RMapStatement stmt, String rdfFormat)throws RMapException;
	/**
	 * Serialize RMapDiSCO as RDF
	 * @param disco RMapDiSCO to be serialized
	 * @param rdfFormat RDF Format to be used in serialization
	 * @return OutputStream with serialized RDF
	 * @throws RMapException if RMapDiSCO cannot be serialized as RDF
	 */
	public OutputStream disco2Rdf(RMapDiSCO disco, String rdfFormat) throws RMapException;
	/**
	 * Serialize RMapEvent as RDF
	 * @param event RMapEvent to be serialized
	 * @param rdfFormat RDF Format to be used in serialization
	 * @return OutputStream with serialized RDF
	 * @throws RMapException if RMapEvent cannot be serialized as RDF
	 */
	public OutputStream event2Rdf(RMapEvent event, String rdfFormat)throws RMapException;
	/**
	 *  Serialize RMapAgent as RDF
	 * @param agent RMapAgent  to be serialized
	 * @param rdfFormat RDF Format to be used in serialization
	 * @return OutputStream with serialized RDF
	 * @throws RMapException if RMapAgent cannot be serialized as RDF
	 */
	public OutputStream agent2Rdf(RMapAgent agent, String rdfFormat)throws RMapException;
	
}

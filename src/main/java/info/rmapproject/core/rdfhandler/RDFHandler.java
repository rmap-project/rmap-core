/**
 * 
 */
package info.rmapproject.core.rdfhandler;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapTriple;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.model.event.RMapEvent;
//import info.rmapproject.core.model.statement.RMapStatement;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author smorrissey
 *
 */
public interface RDFHandler {
	/**
	 * Deserialize an RDF InputStream into an RMapDiSCO
	 * @param rdfIn an RDF InputStream
	 * @param baseUri for resolving relative URIs; empty string if no relative URIs in stream
	 * @param rdfFormat name of RDF format 
	 * @return RMapDiSCO built from RDF statements in InputStream
	 * @throws RMapException if InputStream cannot be converted to valid DiSCO
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapDiSCO rdf2RMapDiSCO(InputStream rdfIn, String baseUri, String rdfFormat) throws RMapException, RMapDefectiveArgumentException;
	/**
	 * Deserialize an RDF InputStream into an RMapAgent
	 * @param rdfIn
	 * @param baseUri; empty string if no relative URIs in stream
	 * @param rdfFormat
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public RMapAgent rdf2RMapAgent(InputStream rdfIn, String baseUri, String rdfFormat) throws RMapException, RMapDefectiveArgumentException;
	
	/**
	 * Serialize RMapTriple list as RDF
	 * @param List <RMapTriple> to be serialized
	 * @param rdfFormat RDF Format to be used in serialization
	 * @return OutputStream with serialized RDF
	 * @throws RMapException if RMapTriple list cannot be serialized as RDF
	 * @throws RMapDefectiveArgumentException 
	 */
	public OutputStream triples2Rdf(List<RMapTriple> triples, String rdfFormat)	throws RMapException, RMapDefectiveArgumentException;
	
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

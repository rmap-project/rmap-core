/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;


import java.net.URISyntaxException;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapBlankNode;
import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapNonLiteral;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Literal;
import org.openrdf.model.BNode;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;


/**
 * Adapter utilities for conversion between RMap classes and OpenRDF/Sesame classes
 * 
 * @author smorrissey
 *
 */
public class ORAdapter {
	
	private static ValueFactory valFactory = null;
	
	/**
	 * Creates ValueFactory from properties file.
	 * @return  ValueFactory of class specified in properties file
	 */
	private static void createValueFactory () throws RMapException {	
		try {
			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			valFactory = ts.getValueFactory();
		} catch (Exception e) {
			throw new RMapException("Unable to create Sesame TripleStore: ", e);
		}	
		return;
	}
	/**
	 * Get ValueFactory to be used to create Model objects
	 * @return ValueFactory
	 * @throws Exception 
	 */
	public static ValueFactory getValueFactory() throws RMapException {
		if (valFactory == null){
			createValueFactory();
		}
		return valFactory;
	}
	
	
	// Adapter methods to go from RMap classes to OpenRDF classes
	
	/**
	 * Convert java.net.URI to org.openrdf.model.URI
	 * @param uri java.net.URI to be converted
	 * @return org.openrdf.model.URI
	 * @throws Exception 
	 */
	public static URI uri2OpenRdfUri (java.net.URI uri) throws RMapException{
		URI openUri =  getValueFactory().createURI(uri.toString());
		return openUri;
	}
	/**
	 * Convert RMapUri to  org.openrdf.model.URI
	 * @param rUri RMapUri to be converted
	 * @return  org.openrdf.model.URI equivalent
	 * @throws Exception
	 */
	public static URI rMapUri2OpenRdfUri (RMapUri rUri)  throws RMapException {
		URI uri = null;
		if (rUri==null){
			throw new IllegalArgumentException("RMapUri is null");
		}
		else {
			uri = uri2OpenRdfUri(rUri.getIri());
		}		
		return uri;
	}
	/**
	 * Convert RMapBlankNode to  org.openrdf.model.Bnode
	 * @param blank RMapBlankNode to be converted
	 * @return org.openrdf.model.Bnode
	 * @throws RMapException
	 */
	public static BNode rMapBlankNode2OpenRdfBNode (RMapBlankNode blank) 
			throws RMapException {
		BNode newBlankNode = getValueFactory().createBNode(blank.getId());
		return newBlankNode;
	}

	/**
	 * 
	 * @param nonLiteral
	 * @return
	 * @throws Exception
	 */
	public static Resource rMapNonLiteral2OpenRdfResource(RMapNonLiteral nonLiteral) 
			throws RMapException {
		Resource resource = null;
		if (nonLiteral==null){
			throw new IllegalArgumentException("RMapNonLiteral is null");
		}				
		else if (nonLiteral instanceof RMapBlankNode){
			RMapBlankNode rb = (RMapBlankNode)nonLiteral;
			BNode blank = rMapBlankNode2OpenRdfBNode(rb);
			resource = blank;
		}
		else if (nonLiteral instanceof RMapUri){
			RMapUri rUri = (RMapUri)nonLiteral;
			URI uri = rMapUri2OpenRdfUri(rUri);
			resource = uri;
		}
		else {
			throw new IllegalArgumentException("Unrecognized RMapNonLiteral type");
		}
		return resource;
	}
	/**
	 * 
	 * @param rLiteral
	 * @return
	 * @throws Exception
	 */
	public static Literal rMapLiteral2OpenRdfLiteral(RMapLiteral rLiteral) throws RMapException {
		Literal literal = null;
		if (rLiteral == null){
			throw new IllegalArgumentException ("Null RMapLiteral");
		}
		String litString = rLiteral.getStringValue();
		literal = getValueFactory().createLiteral(litString);
		return literal;
	}
	
	public static Value rMapResource2OpenRdfValue (RMapResource resource) throws RMapException {
		Value value = null;
		if (resource==null){
			throw new IllegalArgumentException ("Null RMapLiteral");
		}
		if (resource instanceof RMapNonLiteral){
			value = rMapNonLiteral2OpenRdfResource((RMapNonLiteral)resource);
		}
		else if (resource instanceof RMapLiteral){
			value = rMapLiteral2OpenRdfLiteral((RMapLiteral)resource);
		}
		else {
			throw new IllegalArgumentException("Unrecognized RMapResourceType");
		}
		return value;
	}
	
	// Adapter Methods to go from OpenRDF to RMap
	/**
	 * 
	 * @param uri
	 * @return
	 * @throws URISyntaxException
	 */
	public static java.net.URI openRdfUri2URI (URI uri) throws RMapException{
		java.net.URI jUri;
		try {
			jUri = new java.net.URI(uri.toString());
		} catch (URISyntaxException e) {
			throw new RMapException("Cannot convert to URI: invalid syntax", e);
		}
		return jUri;
	}
	/**
	 * 
	 * @param uri
	 * @return
	 * @throws URISyntaxException
	 */
	public static RMapUri openRdfUri2RMapUri(URI uri) throws RMapException{
		RMapUri rmapUri = null;
		java.net.URI jUri = openRdfUri2URI(uri);
		rmapUri = new RMapUri(jUri);
		return rmapUri;
	}
	/**
	 * 
	 * @param b
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static RMapBlankNode openRdfBNode2RMapBlankNode (BNode b) throws IllegalArgumentException{
		RMapBlankNode rnode= null;
		if (b==null) {
			throw new IllegalArgumentException("BNode is null");
		}
		rnode = new RMapBlankNode(b.getID());
		return rnode;
	}
	/**
	 * 
	 * @param resource
	 * @return
	 * @throws IllegalArgumentException
	 * @throws URISyntaxException
	 */
	public static RMapNonLiteral openRdfResource2NonLiteralResource(Resource resource)
	throws IllegalArgumentException, URISyntaxException {
		RMapNonLiteral nlResource = null;
		if (resource==null){
			throw new IllegalArgumentException("Resource is null");
		}				
		else if (resource instanceof BNode){
			RMapBlankNode bnode = openRdfBNode2RMapBlankNode((BNode) resource);
			nlResource = bnode;
		}
		else if (resource instanceof URI){			
			RMapUri uri = openRdfUri2RMapUri((URI)resource);
			nlResource = uri;
		}
		else {
			throw new IllegalArgumentException("Unrecognized Resource type");
		}
		return nlResource;
	}
	
	public static  RMapLiteral openRdfLiteral2RMapLiteral(Literal literal)
	throws IllegalArgumentException{
		RMapLiteral rLiteral = null;
		if (literal==null){
			throw new IllegalArgumentException("Literal is null");
		}
		rLiteral = new RMapLiteral(literal.stringValue());
		return rLiteral;
	}
	/**
	 * 
	 * @param value
	 * @return
	 * @throws URISyntaxException
	 * @throws IllegalArgumentException
	 */
	public static RMapResource openRdfValue2RMapResource (Value value) 
			throws URISyntaxException, IllegalArgumentException{
		RMapResource resource = null;
		if (value==null){
			throw new IllegalArgumentException("Resource is null");
		}				
		else if (value instanceof Literal){			
			RMapLiteral rLiteral = openRdfLiteral2RMapLiteral((Literal)value);
			resource = rLiteral;
		}
		else if (value instanceof BNode){
			RMapBlankNode bnode = openRdfBNode2RMapBlankNode((BNode) value);
			resource = bnode;
		}
		else if (value instanceof URI){		
			RMapUri uri =openRdfUri2RMapUri((URI)value);
			resource = uri;
		}
		else {
			throw new IllegalArgumentException("Unrecognized Resource type");
		}
		return resource;
	}
		
}

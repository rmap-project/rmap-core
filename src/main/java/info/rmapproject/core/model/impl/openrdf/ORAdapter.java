/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;


import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapBlankNode;
import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapTriple;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;


/**
 * Adapter utilities for conversion between RMap classes and OpenRDF/Sesame classes
 * 
 * @author smorrissey, khanson
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
	public static URI rMapUri2OpenRdfUri (RMapUri rUri) throws RMapDefectiveArgumentException {
		URI uri = null;
		if (rUri==null){
			throw new RMapDefectiveArgumentException("RMapUri is null");
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
	public static BNode rMapBlankNode2OpenRdfBNode (RMapBlankNode blank) {
		BNode newBlankNode = getValueFactory().createBNode(blank.getId());
		return newBlankNode;
	}

	/**
	 * 
	 * @param nonLiteral
	 * @return
	 * @throws Exception
	 */
	public static Resource rMapNonLiteral2OpenRdfResource(RMapResource nonLiteral) throws RMapDefectiveArgumentException {
		Resource resource = null;
		if (nonLiteral==null){
			throw new RMapDefectiveArgumentException("RMapNonLiteral is null");
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
			throw new RMapDefectiveArgumentException("Unrecognized RMapNonLiteral type");
		}
		return resource;
	}
	/**
	 * 
	 * @param rLiteral
	 * @return
	 * @throws Exception
	 */
	public static Literal rMapLiteral2OpenRdfLiteral(RMapLiteral rLiteral) throws RMapDefectiveArgumentException {
		Literal literal = null;
		if (rLiteral == null){
			throw new RMapDefectiveArgumentException ("Null RMapLiteral");
		}
		String litString = rLiteral.getStringValue();
		if (rLiteral.getDatatype() != null){ //has a datatype associated with the literal
			URI datatype = rMapUri2OpenRdfUri(rLiteral.getDatatype());
			literal = getValueFactory().createLiteral(litString,datatype);			
		}
		else if (rLiteral.getLanguage() != null){ //has a language associated with the literal
			literal = getValueFactory().createLiteral(litString,rLiteral.getLanguage());				
		}
		else {//just a string value - no type or language
			literal = getValueFactory().createLiteral(litString);					
		}
		return literal;
	}
	
	public static Value rMapValue2OpenRdfValue (RMapValue resource) throws RMapDefectiveArgumentException {
		Value value = null;
		if (resource==null){
			throw new RMapDefectiveArgumentException ("Null RMapLiteral");
		}
		if (resource instanceof RMapResource){
			value = rMapNonLiteral2OpenRdfResource((RMapResource)resource);
		}
		else if (resource instanceof RMapLiteral){
			value = rMapLiteral2OpenRdfLiteral((RMapLiteral)resource);
		}
		else {
			throw new RMapDefectiveArgumentException("Unrecognized RMapResourceType");
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
	 * @throws RMapDefectiveArgumentException
	 */
	public static RMapBlankNode openRdfBNode2RMapBlankNode (BNode b) throws RMapDefectiveArgumentException{
		RMapBlankNode rnode= null;
		if (b==null) {
			throw new RMapDefectiveArgumentException("BNode is null");
		}
		rnode = new RMapBlankNode(b.getID());
		return rnode;
	}
	/**
	 * 
	 * @param resource
	 * @return
	 * @throws RMapDefectiveArgumentException
	 * @throws URISyntaxException
	 */
	public static RMapResource openRdfResource2NonLiteral(Resource resource) 
			throws RMapException, RMapDefectiveArgumentException {
		RMapResource nlResource = null;
		if (resource==null){
			throw new RMapDefectiveArgumentException("Resource is null");
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
			throw new RMapDefectiveArgumentException("Unrecognized Resource type");
		}
		return nlResource;
	}
	
	public static  RMapLiteral openRdfLiteral2RMapLiteral(Literal literal)
			throws RMapException, RMapDefectiveArgumentException {
		RMapLiteral rLiteral = null;
		if (literal==null){
			throw new RMapDefectiveArgumentException("Literal is null");
		}

		String litString = literal.getLabel();
		if (literal.getDatatype() != null){ //has a datatype associated with the literal
			RMapUri datatype = openRdfUri2RMapUri(literal.getDatatype());
			rLiteral = new RMapLiteral(litString, datatype);		
		}
		else if (literal.getLanguage() != null){ //has a language associated with the literal
			rLiteral = new RMapLiteral(litString,literal.getLanguage());			
		}
		else {//just a string value - no type or language
			rLiteral = new RMapLiteral(litString);					
		}
		return rLiteral;
	}
	/**
	 * 
	 * @param value
	 * @return
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException
	 */
	public static RMapValue openRdfValue2RMapValue (Value value) 
			throws RMapException, RMapDefectiveArgumentException {
		RMapValue resource = null;
		if (value==null){
			throw new RMapDefectiveArgumentException("Resource is null");
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
			RMapUri uri = openRdfUri2RMapUri((URI)value);
			resource = uri;
		}
		else {
			throw new RMapDefectiveArgumentException("Unrecognized Resource type");
		}
		return resource;
	}
	/**
	 * Covert OpenRdf Statement to RMapTriple
	 * @param stmt OpenRdf Statement to be converted
	 * @return RMapTriple corresponding to OpenRdf Statement
	 * @throws RMapDefectiveArgumentException
	 * @throws RMapException
	 */
	public static RMapTriple openRdfStatement2RMapTriple (Statement stmt)
		throws RMapDefectiveArgumentException, RMapException {
		if (stmt==null){
			throw new RMapDefectiveArgumentException("null stmt");
		}
		
		RMapResource subject = openRdfResource2NonLiteral(stmt.getSubject());
		RMapUri predicate = openRdfUri2RMapUri(stmt.getPredicate());
		RMapValue object = openRdfValue2RMapValue(stmt.getObject());		
		RMapTriple rtriple = new RMapTriple(subject, predicate, object);		
		return rtriple;
	}
	
	/**
	 * Attempt to convert URIs in openRdf URI to a java.net.URI to see if it is compatible
	 * openRdf is more relaxed about what characters to allow in the URI e.g. "/n" can be put in openRdfUri. 
	 * This ensures URIs in the statement are compatible with the narrower URI definition in java.net.URI.
	 * @param uri
	 * @return boolean
	 * @throws RMapException
	 */
	public static boolean checkOpenRdfUri2UriCompatibility(URI uri) throws RMapException {
		try {
			new java.net.URI(uri.toString());
		} catch (URISyntaxException e) {
			throw new RMapException("Cannot convert stmt resource reference to a URI: " );
		}
		return true;
	}
	
	/**
	 * Checks each openRDF URI in a statement for compatibility with java.net.URI. 
	 * @param stmt
	 * @return boolean
	 * @throws RMapException
	 */
	public static boolean checkOpenRdfUri2UriCompatibility (Statement stmt) throws RMapException {
		if (stmt.getSubject() instanceof URI) {
			checkOpenRdfUri2UriCompatibility((URI)stmt.getSubject());
		}
		
		checkOpenRdfUri2UriCompatibility((URI)stmt.getPredicate());
		
		if (stmt.getObject() instanceof URI) {
			checkOpenRdfUri2UriCompatibility((URI)stmt.getObject());
		}	
		return true;
	}
	
	/**
	 * Converts a list of openRdf URIs to a list of java.net.URIs
	 * @param openRdfUriList
	 * @return
	 */
	public static List<java.net.URI> openRdfUriList2UriList(List<org.openrdf.model.URI> openRdfUriList) {
		List<java.net.URI> javaUriList = new ArrayList<java.net.URI>();
		if (openRdfUriList != null) {
			for (org.openrdf.model.URI openRdfUri : openRdfUriList){
				javaUriList.add(openRdfUri2URI(openRdfUri));
			}
		}
		else {
			javaUriList = null;
		}
		return javaUriList;
	}
	
	/**
	 * Converts a list of java.net.URIs to a list of openRdf URIs	  
	 * @param javaUriList
	 * @return
	 */
	public static List<org.openrdf.model.URI> uriList2OpenRdfUriList(List<java.net.URI> javaUriList) {
		List<org.openrdf.model.URI> openRdfUriList = new ArrayList<org.openrdf.model.URI>();
		if (javaUriList != null) {
			for (java.net.URI sysAgent : javaUriList){
				openRdfUriList.add(uri2OpenRdfUri(sysAgent));
			}
		}
		else {
			openRdfUriList = null;
		}
		return openRdfUriList;
	}
	

	/**
	 * Converts a set of openRdf URIs to a set of java.net.URIs
	 * @param openRdfUriList
	 * @return
	 */
	public static Set<java.net.URI> openRdfUriSet2UriSet(Set<org.openrdf.model.URI> openRdfUriList) {
		Set<java.net.URI> javaUriList = new HashSet<java.net.URI>();
		if (openRdfUriList != null) {
			for (org.openrdf.model.URI openRdfUri : openRdfUriList){
				javaUriList.add(openRdfUri2URI(openRdfUri));
			}
		}
		else {
			javaUriList = null;
		}
		return javaUriList;
	}
	
	
	/**
	 * Checks each openRDF URI in a list statement for compatibility with java.net.URI. 
	 * @param stmts
	 * @return
	 * @throws RMapException
	 */
	public static boolean checkOpenRdfUri2UriCompatibility (List<Statement> stmts) throws RMapException{
		for (Statement stmt : stmts){
			boolean isCompatible = checkOpenRdfUri2UriCompatibility(stmt);
			if (!isCompatible){
				return false;
			}
		}
		return true;
	}
		
}

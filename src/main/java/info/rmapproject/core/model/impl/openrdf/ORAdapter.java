/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;


import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapBlankNode;
import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapTriple;
import info.rmapproject.core.model.RMapValue;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.IRI;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;


/**
 * Adapter utilities for conversion between RMap classes and OpenRDF/Sesame classes
 * 
 * @author smorrissey, khanson
 *
 */
public class ORAdapter {

	private static ValueFactory valFactory = null;
	
	/**
	 * Get ValueFactory to be used to create Model objects
	 * @return ValueFactory
	 * @throws Exception 
	 */
	public static ValueFactory getValueFactory() throws RMapException {
		if (valFactory == null){
			valFactory = SimpleValueFactory.getInstance();
		}
		return valFactory;
	}
	
	
	// Adapter methods to go from RMap classes to OpenRDF classes
	
	/**
	 * Convert java.net.URI to org.openrdf.model.IRI
	 * @param uri java.net.URI to be converted
	 * @return org.openrdf.model.IRI
	 * @throws Exception 
	 */
	public static IRI uri2OpenRdfIri (java.net.URI uri) throws RMapException{
		IRI openIri =  getValueFactory().createIRI(uri.toString());
		return openIri;
	}
	/**
	 * Convert RMapIri to  org.openrdf.model.IRI
	 * @param rIri RMapIri to be converted
	 * @return  org.openrdf.model.URI equivalent
	 * @throws Exception
	 */
	public static IRI rMapIri2OpenRdfIri (RMapIri rIri) throws RMapDefectiveArgumentException {
		IRI iri = null;
		if (rIri==null){
			throw new RMapDefectiveArgumentException("RMapUri is null");
		}
		else {
			iri = getValueFactory().createIRI(rIri.getIri().toString());
		}		
		return iri;
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
		else if (nonLiteral instanceof RMapIri){
			RMapIri rIri = (RMapIri)nonLiteral;
			IRI iri = rMapIri2OpenRdfIri(rIri);
			resource = iri;
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
			IRI datatype = rMapIri2OpenRdfIri(rLiteral.getDatatype());
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
	public static java.net.URI openRdfIri2URI (IRI iri) throws RMapException{
		java.net.URI jUri;
		try {
			jUri = new java.net.URI(iri.toString());
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
	public static RMapIri openRdfIri2RMapIri(IRI iri) throws RMapException{
		RMapIri rmapIri = null;
		java.net.URI jIri = openRdfIri2URI(iri);
		rmapIri = new RMapIri(jIri);
		return rmapIri;
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
		else if (resource instanceof IRI){			
			RMapIri uri = openRdfIri2RMapIri((IRI)resource);
			nlResource = uri;
		}
		else {
			throw new RMapDefectiveArgumentException("Unrecognized Resource type");
		}
		return nlResource;
	}
	
	public static RMapLiteral openRdfLiteral2RMapLiteral(Literal literal)
			throws RMapException, RMapDefectiveArgumentException {
		RMapLiteral rLiteral = null;
		if (literal==null){
			throw new RMapDefectiveArgumentException("Literal is null");
		}

		String litString = literal.getLabel();

		if (literal.getDatatype() != null){ //has a datatype associated with the literal
			RMapIri datatype = openRdfIri2RMapIri(literal.getDatatype());
			rLiteral = new RMapLiteral(litString, datatype);		
		}
		else if (literal.getLanguage().isPresent()){ //has a language associated with the literal
			String lang = literal.getLanguage().toString();
			rLiteral = new RMapLiteral(litString,lang);			
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
		else if (value instanceof IRI){		
			RMapIri iri = openRdfIri2RMapIri((IRI)value);
			resource = iri;
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
		RMapIri predicate = openRdfIri2RMapIri(stmt.getPredicate());
		RMapValue object = openRdfValue2RMapValue(stmt.getObject());		
		RMapTriple rtriple = new RMapTriple(subject, predicate, object);		
		return rtriple;
	}
	
	/**
	 * Attempt to convert IRIs in openRdf IRI to a java.net.URI to see if it is compatible
	 * openRdf is more relaxed about what characters to allow in the URI e.g. "/n" can be put in openRdfUri. 
	 * This ensures URIs in the statement are compatible with the narrower URI definition in java.net.URI.
	 * @param uri
	 * @return boolean
	 * @throws RMapException
	 */
	public static boolean checkOpenRdfUri2UriCompatibility(IRI iri) throws RMapException {
		try {
			
			new java.net.URI(iri.toString());
		} catch (URISyntaxException e) {
			throw new RMapException("Cannot convert stmt resource reference to a URI: " + iri);
		}
		return true;
	}
	
	/**
	 * Checks each openRDF IRI in a statement for compatibility with java.net.URI. 
	 * @param stmt
	 * @return boolean
	 * @throws RMapException
	 */
	public static boolean checkOpenRdfIri2UriCompatibility (Statement stmt) throws RMapException {
		if (stmt.getSubject() instanceof IRI) {
			checkOpenRdfUri2UriCompatibility((IRI)stmt.getSubject());
		}
		
		checkOpenRdfUri2UriCompatibility((IRI)stmt.getPredicate());
		
		if (stmt.getObject() instanceof IRI) {
			checkOpenRdfUri2UriCompatibility((IRI)stmt.getObject());
		}	
		return true;
	}
	
	/**
	 * Converts a list of openRdf URIs to a list of java.net.URIs
	 * @param openRdfUriList
	 * @return
	 */
	public static List<java.net.URI> openRdfUriList2UriList(List<IRI> openRdfUriList) {
		List<java.net.URI> javaUriList = new ArrayList<java.net.URI>();
		if (openRdfUriList != null) {
			for (IRI openRdfUri : openRdfUriList){
				javaUriList.add(openRdfIri2URI(openRdfUri));
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
	public static List<IRI> uriList2OpenRdfIriList(List<java.net.URI> javaUriList) {
		List<IRI> openRdfUriList = new ArrayList<IRI>();
		if (javaUriList != null) {
			for (java.net.URI sysAgent : javaUriList){
				openRdfUriList.add(uri2OpenRdfIri(sysAgent));
			}
		}
		else {
			openRdfUriList = null;
		}
		return openRdfUriList;
	}
	

	/**
	 * Converts a set of openRdf IRIs to a set of java.net.URIs
	 * @param openRdfIriList
	 * @return
	 */
	public static Set<java.net.URI> openRdfIriSet2UriSet(Set<IRI> openRdfIriList) {
		Set<java.net.URI> javaUriList = new HashSet<java.net.URI>();
		if (openRdfIriList != null) {
			for (IRI openRdfIri : openRdfIriList){
				javaUriList.add(openRdfIri2URI(openRdfIri));
			}
		}
		else {
			javaUriList = null;
		}
		return javaUriList;
	}
	
	

	/**
	 * Converts a set of java.net.URIs to an openRDF set of IRIs
	 * @param uriList
	 * @return
	 */
	public static Set<IRI> uriSet2OpenRdfIriSet(Set<java.net.URI> javaUriList) {
		Set<IRI> openRdfIriList = new HashSet<IRI>();
		
		if (javaUriList != null) {
			for (java.net.URI javaUri : javaUriList){
				openRdfIriList.add(uri2OpenRdfIri(javaUri));
			}
		}
		else {
			openRdfIriList = null;
		}
		return openRdfIriList;
	}
	
	
	
	/**
	 * Checks each openRDF IRI in a list statement for compatibility with java.net.URI. 
	 * @param stmts
	 * @return
	 * @throws RMapException
	 */
	public static boolean checkOpenRdfIri2UriCompatibility (List<Statement> stmts) throws RMapException{
		for (Statement stmt : stmts){
			boolean isCompatible = checkOpenRdfIri2UriCompatibility(stmt);
			if (!isCompatible){
				return false;
			}
		}
		return true;
	}
		
}

package info.rmapproject.core.rdfhandler;

/**
 * HTTP content types supported for RDF-based API calls.
 *
 * @author khanson
 */
public enum RDFType {
	
	/** JSON-LD see http://json-ld.org/. */
	JSONLD("JSONLD"), 
	
	/** RDF/XML see https://www.w3.org/TR/rdf-syntax-grammar/ */
	RDFXML("RDFXML"), 
	
	/** RDF Turtle see https://www.w3.org/TR/turtle/ */
	TURTLE("TURTLE");
	
	/** String representation of RDF type. */
	private final String rdfType;

	/**
	 * Instantiates a new RDF type.
	 *
	 * @param rdfType the rdf type as string
	 */
	private RDFType (String rdfType) {
		this.rdfType = rdfType;
	}
	
	/**
	 * Gets the current rdf type as a string
	 *
	 * @return the rdf type
	 */
	public String getRdfType()  {
		return rdfType;
	}
	
}

package info.rmapproject.core.rdfhandler;

/**
 * HTTP content types supported for RDF-based API calls
 * @author khanson
 *
 */
public enum RDFType {
	JSONLD("JSONLD"), 
	RDFXML("RDFXML"), 
	TURTLE("TURTLE");
	
	private final String rdfType;

	private RDFType (String rdfType) {
		this.rdfType = rdfType;
	}
	public String getRdfType()  {
		return rdfType;
	}
	
}

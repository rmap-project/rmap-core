/**
 * 
 */
package info.rmapproject.core.model;

/**
 * @author smorrissey
 * Concrete class for RDF resources represented by a literal string. 
 * @see http://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/#resources-and-statements
 */
public class RMapLiteral implements RMapValue {
	
	String value;
	String language;
	RMapUri datatype;

	/**
	 * 
	 */
	protected RMapLiteral() {
		super();
	}

	public RMapLiteral(String value){
		this();
		this.value = value;
	}

	public RMapLiteral(String value, String language){
		this();
		this.value = value;
		this.language = language;
	}

	public RMapLiteral(String value, RMapUri datatype){
		this();
		this.value = value;
		this.datatype = datatype;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.Resource#getStringValue()
	 */
	public String getStringValue() {
		return getValue();
	}

	public String getValue() {
		return value;
	}

	public String getLanguage() {
		return language;
	}

	public RMapUri getDatatype() {
		return datatype;
	}
	
	
	@Override
	public String toString(){
		return getStringValue();
	}

}

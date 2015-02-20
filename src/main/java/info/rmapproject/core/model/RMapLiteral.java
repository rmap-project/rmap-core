/**
 * 
 */
package info.rmapproject.core.model;

/**
 * @author smorrissey
 * Concrete class for RDF resources represented by a literal string. 
 * @see http://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/#resources-and-statements
 */
public class RMapLiteral implements RMapResource {
	
	String value;

	/**
	 * 
	 */
	protected RMapLiteral() {
		super();
	}

	public RMapLiteral(String r){
		this();
		this.value = r;
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
	@Override
	public String toString(){
		return getStringValue();
	}

}

/**
 * 
 */
package info.rmapproject.core.model;

/**
 * @author smorrissey
 *
 */
public abstract class RMapNonLiteral implements RMapResource{

	/**
	 * 
	 */
	public RMapNonLiteral() {}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RdfResource#getStringValue()
	 */
	public abstract String getStringValue();
}

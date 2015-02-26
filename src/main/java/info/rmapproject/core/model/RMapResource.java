/**
 * 
 */
package info.rmapproject.core.model;

/**
 * @author smorrissey
 *
 */
public abstract class RMapResource implements RMapValue{

	/**
	 * 
	 */
	public RMapResource() {}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RdfResource#getStringValue()
	 */
	public abstract String getStringValue();
}

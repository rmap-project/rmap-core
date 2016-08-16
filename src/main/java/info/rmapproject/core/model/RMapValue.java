/**
 * 
 */
package info.rmapproject.core.model;

/**
 * Interface for objects that are considered to be an RMapValue. The object of a triple (subject, predicate, object) must be 
 * either a Literal or a Resource.  Both of these should implement RMapValue
 *
 * @author smorrissey
 */
public interface RMapValue {
	
	/**
	 * Gets the string value.
	 *
	 * @return the string value
	 */
	public String getStringValue();
}

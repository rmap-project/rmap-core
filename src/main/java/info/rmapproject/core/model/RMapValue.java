/**
 * 
 */
package info.rmapproject.core.model;

/**
 * @author smorrissey
 * Interface for RDF resources  
 * A resource is something in the world that can be denoted by a literal or an IRI
 * @see http://www.w3.org/TR/rdf11-concepts/#resources-and-statements
 *
 */
public interface RMapValue {
	
	public String getStringValue();
}

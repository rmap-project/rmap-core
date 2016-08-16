/**
 * 
 */
package info.rmapproject.core.model;

/**
 * Models the concept of an RDF Resource.  RDF Resources can be 
 * represented by a Blank Node (see RMapBlankNode) or an IRI (see RMapIri)
 * This is the abstract class to support those concrete classes.
 *
 * @author smorrissey
 */
public abstract class RMapResource implements RMapValue{

	/**
	 * Instantiates a new RMap resource.
	 */
	public RMapResource() {}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RdfResource#getStringValue()
	 */
	public abstract String getStringValue();
}

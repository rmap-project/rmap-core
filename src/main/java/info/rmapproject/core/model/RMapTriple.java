/**
 * 
 */
package info.rmapproject.core.model;

/**
 * Models the concept of an RDF triple, which contains a subject, predicate and object
 *
 * @author smorrissey
 */
public class RMapTriple {

	/** The subject. */
	protected RMapResource subject;
	
	/** The predicate. */
	protected RMapIri predicate;
	
	/** The object. */
	protected RMapValue object;
	
	/**
	 * Instantiates a new RMap triple.
	 */
	protected RMapTriple() {
		super();
	}
	
	/**
	 * Instantiates a new RMap triple.
	 *
	 * @param subject the subject
	 * @param predicate the predicate
	 * @param object the object
	 */
	public RMapTriple(RMapResource subject, RMapIri predicate, RMapValue object){
		this();
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	/**
	 * Gets the subject.
	 *
	 * @return the subject
	 */
	public RMapResource getSubject() {
		return subject;
	}

	/**
	 * Gets the predicate.
	 *
	 * @return the predicate
	 */
	public RMapIri getPredicate() {
		return predicate;
	}

	/**
	 * Gets the object.
	 *
	 * @return the object
	 */
	public RMapValue getObject() {
		return object;
	}

}

/**
 * 
 */
package info.rmapproject.core.model;

/**
 * @author smorrissey
 *
 */
public class RMapTriple {

	protected RMapResource subject;
	protected RMapUri predicate;
	protected RMapValue object;
	/**
	 * 
	 */
	protected RMapTriple() {
		super();
	}
	
	public RMapTriple(RMapResource subject, RMapUri predicate, RMapValue object){
		this();
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	/**
	 * @return the subject
	 */
	public RMapResource getSubject() {
		return subject;
	}

	/**
	 * @return the predicate
	 */
	public RMapUri getPredicate() {
		return predicate;
	}

	/**
	 * @return the object
	 */
	public RMapValue getObject() {
		return object;
	}

}

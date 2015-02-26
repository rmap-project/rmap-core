/**
 * 
 */
package info.rmapproject.core.model;

/**
 * @author smorrissey
 *
 */
public class RMapBlankNode extends RMapResource {

	protected String id;
	/**
	 * 
	 */
	protected RMapBlankNode() {
		super();
	}
	/**
	 * Create new BlandNode with specific ID
	 * @param id String value of new id
	 */
	public RMapBlankNode(String id){
		this();
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RdfResource#getStringValue()
	 */
	public String getStringValue() {
		return id;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	@Override
	public String toString(){
		return getStringValue();
	}


}

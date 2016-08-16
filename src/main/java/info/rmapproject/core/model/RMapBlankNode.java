package info.rmapproject.core.model;

/**
 * RDF Resources can either be represented by IRIs (see RMapIri) or Blank Nodes 
 * This concrete class models the concept of a Blank Node as found in RDF
 *
 * @author smorrissey
 */
public class RMapBlankNode extends RMapResource {

	/** The blank node id. */
	protected String id;

	/**
	 * Instantiates a new RMap blank node.
	 */
	protected RMapBlankNode() {
		super();
	}
	
	/**
	 * Create new BlankNode with specific ID.
	 *
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
	 * Gets the blank node id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return getStringValue();
	}


}

package info.rmapproject.core.model;

import java.net.URI;

/**
 * Models concept of IRI. RDF Resources can either be represented by Blank Node (see RMapBlankNode)
 * or by IRIs.  This is a concrete class for RDF resources represented by an IRI.  
 *
 * @author smorrissey
 * @see http://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/#resources-and-statements
 */
public class RMapIri extends RMapResource  {

	/** The IRI. */
	URI iri;
	
	/**
	 * Instantiates a new RMap IRI
	 */
	protected RMapIri() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param iri IRI of resource
	 */
	public RMapIri(URI iri){
		this();
		this.iri = iri;
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RdfResource#getStringValue()
	 */
	public String getStringValue() {
		String uriString = null;
		if (iri != null){
			uriString = iri.toASCIIString();
		}
		return uriString;
	}

	/**
	 * Gets the iri.
	 *
	 * @return the iri
	 */
	public URI getIri() {
		return iri;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return getStringValue();
	}

}

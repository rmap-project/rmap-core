/**
 * 
 */
package info.rmapproject.core.model;

import java.net.URI;

/**
 * Concrete class for RDF resources represented by IRI.  
 * @see http://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/#resources-and-statements
 * @author smorrissey
 *
 */
public class RMapUri extends RMapResource  {

	URI iri;
	
	/**
	 * 
	 */
	protected RMapUri() {
		super();
	}

	/**
	 * Constructor
	 * @param iri IRI of resource
	 */
	public RMapUri(URI iri){
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
	 * @return the iri
	 */
	public URI getIri() {
		return iri;
	}
	
	@Override
	public String toString(){
		return getStringValue();
	}


}

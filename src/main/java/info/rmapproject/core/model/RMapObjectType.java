package info.rmapproject.core.model;

import info.rmapproject.core.utils.Terms;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * The Enum for RMap Object Types
 */
public enum RMapObjectType {
	
	/** RMap DiSCO object. */
	DISCO (Terms.RMAP_DISCO_PATH), 
	
	/** RMap Agent object. */
	AGENT (Terms.RMAP_AGENT_PATH),
	
	/** The RMap event object. */
	EVENT (Terms.RMAP_EVENT_PATH),
	
	/** The generic Object (unspecified type). */
	OBJECT (Terms.RMAP_OBJECT_PATH); 

	/** The object type ontology path. */
	private  RMapIri objectTypePath= null ;

	/**
	 * Instantiates a new RMap object type.
	 *
	 * @param path the ontology path for the type
	 */
	RMapObjectType(String path){		
		try {
			this.objectTypePath = new RMapIri(new URI(path));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the ontology path.
	 *
	 * @return the ontology path
	 */
	public RMapIri getPath()  {
		return this.objectTypePath;
	}

    /**
     * Gets the object type.
     *
     * @param path the ontology path
     * @return the object type
     */
    public static RMapObjectType getObjectType(RMapIri path) { 
    	Map<String, RMapObjectType> lookup = new HashMap<String, RMapObjectType>();
        for(RMapObjectType objtype : EnumSet.allOf(RMapObjectType.class)) {
            lookup.put(objtype.getPath().toString(), objtype);
        }
        return lookup.get(path.toString()); 
    }
	

}

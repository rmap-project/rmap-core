package info.rmapproject.core.model.event;

import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.utils.Terms;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * An Enum to define and retrieve the RMap Event Target types.
 *
 * @author smorrissey, khanson
 */
public enum RMapEventTargetType {
	
	/** RMap DiSCO */
	DISCO(Terms.RMAP_DISCO_PATH),
	
	/** RMap Agent. */
	AGENT(Terms.RMAP_AGENT_PATH);	
	
	/** The event target type ontology path. */
	private RMapIri eventTargetTypePath= null ;

	/**
	 * Instantiates a new RMap event target type.
	 *
	 * @param path the ontology path
	 */
	RMapEventTargetType(String path){		
		try {
			this.eventTargetTypePath = new RMapIri(new URI(path));
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
		return this.eventTargetTypePath;
	}

    /**
     * Gets the event target type.
     *
     * @param path the ontolgoy path
     * @return the event target type
     */
    public static RMapEventTargetType getEventTargetType(String path) { 
    	Map<String, RMapEventTargetType> lookup = new HashMap<String, RMapEventTargetType>();
        for(RMapEventTargetType eventtargettype : EnumSet.allOf(RMapEventTargetType.class)) {
            lookup.put(eventtargettype.getPath().toString(), eventtargettype);
        }
        return lookup.get(path); 
    }
	
	
}

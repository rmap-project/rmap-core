/**
 * 
 */
package info.rmapproject.core.model.event;

import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.utils.Terms;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;



/**
 * @author smorrissey, khanson
 *
 */
public enum RMapEventTargetType {
	DISCO(Terms.RMAP_DISCO_PATH),
	AGENT(Terms.RMAP_AGENT_PATH);	
	
	private RMapIri eventTargetTypePath= null ;

	RMapEventTargetType(String path){		
		try {
			this.eventTargetTypePath = new RMapIri(new URI(path));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public RMapIri getPath()  {
		return this.eventTargetTypePath;
	}

    public static RMapEventTargetType getEventTargetType(String path) { 
    	Map<String, RMapEventTargetType> lookup = new HashMap<String, RMapEventTargetType>();
        for(RMapEventTargetType eventtargettype : EnumSet.allOf(RMapEventTargetType.class)) {
            lookup.put(eventtargettype.getPath().toString(), eventtargettype);
        }
        return lookup.get(path); 
    }
	
	
}

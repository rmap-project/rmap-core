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
public enum RMapEventType {
	CREATION(Terms.RMAP_CREATION_PATH),
	INACTIVATION(Terms.RMAP_INACTIVATION_PATH),
	UPDATE(Terms.RMAP_UPDATE_PATH),
	DERIVATION(Terms.RMAP_DERIVATION_PATH),
	TOMBSTONE(Terms.RMAP_TOMBSTONE_PATH),
	DELETION(Terms.RMAP_DELETION_PATH),
	REPLACE(Terms.RMAP_REPLACE_PATH);
	
	private RMapIri eventTypePath= null ;

	RMapEventType(String path){		
		try {
			this.eventTypePath = new RMapIri(new URI(path));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public RMapIri getPath()  {
		return this.eventTypePath;
	}

    public static RMapEventType getEventType(String path) { 
    	Map<String, RMapEventType> lookup = new HashMap<String, RMapEventType>();
        for(RMapEventType eventtype : EnumSet.allOf(RMapEventType.class)) {
            lookup.put(eventtype.getPath().toString(), eventtype);
        }
        return lookup.get(path); 
    }
	
	
	
}

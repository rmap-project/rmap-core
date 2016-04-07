package info.rmapproject.core.model;

import info.rmapproject.core.utils.Terms;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum RMapObjectType {
	DISCO (Terms.RMAP_DISCO_PATH), 
	AGENT (Terms.RMAP_AGENT_PATH),
	EVENT (Terms.RMAP_EVENT_PATH),
	OBJECT (Terms.RMAP_OBJECT_PATH); //generic object type

	private  RMapIri objectTypePath= null ;

	RMapObjectType(String path){		
		try {
			this.objectTypePath = new RMapIri(new URI(path));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public RMapIri getPath()  {
		return this.objectTypePath;
	}

    public static RMapObjectType getObjectType(RMapIri path) { 
    	Map<String, RMapObjectType> lookup = new HashMap<String, RMapObjectType>();
        for(RMapObjectType objtype : EnumSet.allOf(RMapObjectType.class)) {
            lookup.put(objtype.getPath().toString(), objtype);
        }
        return lookup.get(path.toString()); 
    }
	

}

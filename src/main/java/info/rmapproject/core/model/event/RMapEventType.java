/**
 * 
 */
package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapException;

/**
 * @author smorrissey, khanson
 *
 */
public enum RMapEventType {
	CREATION ("http://rmap-project.org/rmap/terms/creation"),
	INACTIVATION("http://rmap-project.org/rmap/terms/inactivation"),
	UPDATE("http://rmap-project.org/rmap/terms/update"),
	DERIVATION("http://rmap-project.org/rmap/terms/derivation"),
	TOMBSTONE("http://rmap-project.org/rmap/terms/tombstone"),
	DELETION("http://rmap-project.org/rmap/terms/deletion"),
	REPLACE("http://rmap-project.org/rmap/terms/replace");
	
	private String typeString;
	
	RMapEventType (String s){
		this.typeString = s;
	}
	
	public String getTypeString(){
		return typeString;
	}
	
	public static RMapEventType getEventTypeFromString (String et) throws RMapException{
		if (et==null){
			throw new RMapException ("Null event type string");
		}
		if (et.equals(CREATION.getTypeString())){
			return CREATION;
		}
		else if (et.equals(UPDATE.getTypeString())){
			return UPDATE;
		}
		else if (et.equals(INACTIVATION.getTypeString())){
			return INACTIVATION;
		}
		else if (et.equals(DERIVATION.getTypeString())){
			return DERIVATION;
		}
		else if (et.equals(TOMBSTONE.getTypeString())){
			return TOMBSTONE;
		}
		else if (et.equals(DELETION.getTypeString())){
			return DELETION;
		}
		else if (et.equals(REPLACE.getTypeString())){
			return REPLACE;
		}
		else {
			throw new RMapException("Unrecognized RMapEvent type: " + et);
		}
	}

}

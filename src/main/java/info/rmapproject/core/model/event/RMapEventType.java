/**
 * 
 */
package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapException;

/**
 * @author smorrissey
 *
 */
public enum RMapEventType {
	CREATION ("creation"),
	INACTIVATION("inactivation"),
	UPDATE("update"),
	DERIVATION("derivation"),
	TOMBSTONE("tombstone"),
	DELETION("deletion");
	
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
		else {
			throw new RMapException("Unrecognized RMapEvent type: " + et);
		}
	}

}

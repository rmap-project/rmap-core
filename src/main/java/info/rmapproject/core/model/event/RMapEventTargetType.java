/**
 * 
 */
package info.rmapproject.core.model.event;

import info.rmapproject.core.exception.RMapException;


/**
 * @author smorrissey
 *
 */
public enum RMapEventTargetType {
	
	DISCO("http://rmap-project.org/rmap/terms/DiSCO"),
	AGENT("http://rmap-project.org/rmap/terms/Agent");
	
	private String targetURI = null;
	
	RMapEventTargetType(String target){
		this.targetURI = target;
	}
	
	public String uriString(){
		return targetURI; 
	}
	
	public static RMapEventTargetType getTargetTypeFromString (String tt) throws RMapException {
		if (tt==null){
			throw new RMapException ("Null Event Target Type string");
		}
		else if (tt.equals(DISCO.uriString())){
			return DISCO;
		}
		else if (tt.equals(AGENT.uriString())){
			return AGENT;
		}
		else {
			throw new RMapException ("Unrecognized Event Target Type: " + tt);
		}
	}
}

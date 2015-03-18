/**
 * 
 */
package info.rmapproject.core.model.statement;

import info.rmapproject.core.model.RMapObject;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;

/**
 * @author smorrissey
 *
 */
public interface RMapStatement extends RMapObject  {
	
	public RMapResource getSubject();
	
	public RMapUri getPredicate();
	
	public RMapValue getObject();
	
}

/**
 * 
 */
package info.rmapproject.core.model;

import java.net.URI;
import java.util.List;

/**
 * @author smorrissey
 *
 */
public interface RMapStatement extends RMapObject  {
	
	public RMapResource getSubject();
	
	public RMapUri getPredicate();
	
	public RMapValue getObject();
	
	// Status -- it is inferred from related DiSCOs and Events
	public RMapStatus getStatus() throws Exception;
	
	// Related events determined by status
	public List<URI> getRelatedEvents() throws Exception;
	

}

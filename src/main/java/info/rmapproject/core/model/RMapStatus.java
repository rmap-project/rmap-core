/**
 * 
 */
package info.rmapproject.core.model;

import java.net.URI;
import java.net.URISyntaxException;



/**
 * @author smorrissey
 *
 */
public enum RMapStatus {
	ACTIVE ("http://rmap-project.org/rmap/terms/1.0/status/Active"),
	INACTIVE ("http://rmap-project.org/rmap/terms/1.0/status/Inactive"),
	TOMBSTONED ("http://rmap-project.org/rmap/terms/1.0/status/Tombstoned"),
	DELETED("http://rmap-project.org/rmap/terms/1.0/status/Deleted");


	@SuppressWarnings("unused")
	private  URI statusDescription= null ;

	RMapStatus(String desc){		
		try {
			this.statusDescription = new URI(desc);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	

}

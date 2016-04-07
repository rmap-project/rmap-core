/**
 * 
 */
package info.rmapproject.core.model;

import info.rmapproject.core.utils.Terms;

import java.net.URI;
import java.net.URISyntaxException;



/**
 * @author smorrissey
 *
 */
public enum RMapStatus {
	ACTIVE (Terms.RMAP_ACTIVE_PATH, Terms.RMAP_ACTIVE),
	INACTIVE (Terms.RMAP_INACTIVE_PATH, Terms.RMAP_INACTIVE),
	TOMBSTONED (Terms.RMAP_TOMBSTONED_PATH, Terms.RMAP_TOMBSTONED),
	DELETED(Terms.RMAP_DELETED_PATH, Terms.RMAP_DELETED);

	private  URI statusPath= null ;
	private  String statusTerm= null ;

	RMapStatus(String statusPath, String statusTerm){		
		try {
			this.statusPath = new URI(statusPath);
			this.statusTerm = statusTerm;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public URI getPath()  {
		return this.statusPath;
	}
	
	public String getTerm()  {
		return this.statusTerm;
	}
	
	public static RMapStatus getStatusFromTerm(String term){
		for (RMapStatus stat: RMapStatus.values()){
			String statTerm = stat.getTerm().toLowerCase();
			if (statTerm.equals(term.toLowerCase())){
				return stat;
			}
		}
		return null;
	}
	

}

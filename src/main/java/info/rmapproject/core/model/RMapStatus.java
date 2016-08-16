package info.rmapproject.core.model;

import info.rmapproject.core.utils.Terms;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * An Enum to represent the range of object statuses
 * that can be associated with RMap objects. Not all statuses apply to all objects.
 *
 * @author smorrissey
 */
public enum RMapStatus {
	
	/** ACTIVE status - currently assumed to be true. Information is publicly visible */
	ACTIVE (Terms.RMAP_ACTIVE_PATH, Terms.RMAP_ACTIVE),
	
	/** INACTIVE status - information may have been updated or retracted. Information is publicly visible */
	INACTIVE (Terms.RMAP_INACTIVE_PATH, Terms.RMAP_INACTIVE),
	
	/** TOMBSTONED - information has been retracted and is no longer publicly visible though it still exists in the RMap database. */
	TOMBSTONED (Terms.RMAP_TOMBSTONED_PATH, Terms.RMAP_TOMBSTONED),
	
	/** DELETED - the information has been retracted and removed.  Only provenance metadata is visible in the RMap database. */
	DELETED(Terms.RMAP_DELETED_PATH, Terms.RMAP_DELETED);

	/** The status ontology path. */
	private  URI statusPath= null ;
	
	/** The status term. */
	private  String statusTerm= null ;

	/**
	 * Instantiates a new RMap status.
	 *
	 * @param statusPath the status ontology path
	 * @param statusTerm the status term
	 */
	RMapStatus(String statusPath, String statusTerm){		
		try {
			this.statusPath = new URI(statusPath);
			this.statusTerm = statusTerm;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public URI getPath()  {
		return this.statusPath;
	}
	
	/**
	 * Gets the term.
	 *
	 * @return the term
	 */
	public String getTerm()  {
		return this.statusTerm;
	}
	
	/**
	 * Gets the status from term.
	 *
	 * @param term the term
	 * @return the status from term
	 */
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

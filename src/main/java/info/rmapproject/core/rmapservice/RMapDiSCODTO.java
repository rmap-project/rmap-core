/**
 * 
 */
package info.rmapproject.core.rmapservice;

import java.net.URI;

import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.disco.RMapDiSCO;

/**
 * Data Transfer Object to package up information for response to a read of a DiSCO.
 *
 * @author smorrissey
 */
public interface RMapDiSCODTO {
	
	/**
	 * Get DiSCO itself.
	 *
	 * @return RMapDiSCO object
	 */
	public RMapDiSCO getRMapDiSCO();
	
	/**
	 * Get DiSCO status.
	 *
	 * @return RMapStatus of DiSCO
	 */
	public RMapStatus getStatus();
	
	/**
	 * Get URI of previous DiSCO.
	 *
	 * @return URI of previous DiSCO, or null if none
	 */
	public URI getPreviousURI();
	
	/**
	 * Get URI of next DiSCO.
	 *
	 * @return URI of next DiSCO, or null if none
	 */
	public URI getNextURI();
	/**
	 * Get URI of latest version of DiSCO.
	 * Might be that of DiSCO in thsi DTO
	 * @return URI of latest version of DiSCO, or null if DiSCO does not exist
	 */
	public URI getLatestURI();
}

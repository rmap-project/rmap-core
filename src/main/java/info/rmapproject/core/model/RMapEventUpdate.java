/**
 * 
 */
package info.rmapproject.core.model;

import info.rmapproject.core.exception.RMapException;

import java.util.List;

/**
 * 
 * @author smorrissey
 *
 */
public interface RMapEventUpdate extends RMapEvent {
	

	/**
	 * @return the createdObjects
	 * @throws RMapException
	 */
	public List<RMapUri> getCreatedObjectIds() throws RMapException;

	/**
	 * @param createdObjects the createdObjects to set
	 * @throws RMapException 
	 */
	public void setCreatedObjectIds(List<RMapUri> createdObjects) throws RMapException;

	/**
	 * @return the inactivatedObject
	 * @throws RMapException 
	 */
	public RMapUri getTargetObjectId() throws RMapException;

	/**
	 * 
	 * @return
	 * @throws RMapException
	 */
	public RMapUri getDerivedObjectId() throws RMapException;
	/**
	 * 
	 * @param derivedObject
	 * @throws RMapException
	 */
	
	public void deriveFromTarget(RMapUri derivedObject) throws RMapException;	
	/**
	 * Original object is inactivated (only by object creator), including by update to new
	 * version of object by object creator
	 * @throws RMapException
	 */
	public void inactivateTarget() throws RMapException;
	/**
	 * Was object that was target of update inactivated by the update
	 * @return
	 * @throws RMapException
	 */
	public boolean isTargetInactivated() throws RMapException;

}

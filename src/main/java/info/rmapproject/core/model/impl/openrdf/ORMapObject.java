/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;


import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.model.RMapObject;


/**
 * Base class for OpenRDF implementation classes of RMapObjects
 * @author smorrissey
 *
 */
public class ORMapObject implements RMapObject  {
	protected java.net.URI id;
	
	
	/**
	 * Base Constructor for all RMapObjects instances, which must have a unique java.net.URI identifier 
	 * @throws Exception 
	 * 
	 */
	protected ORMapObject() throws RMapException {
		super();
		try {
			this.id = IdServiceFactoryIOC.getFactory().createService().createId();
		} catch (Exception e) {
			throw new RMapException(e);
		}
	}
	
	/**
	 * Return identifier of object as java.net.URI
	 * @return
	 */
	public java.net.URI getId(){
		return id;
	}
	
	protected void setId(java.net.URI id){
		this.id = id;
	}

}

/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;


import org.openrdf.model.Model;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryException;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.model.RMapObject;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.ORMapService;


/**
 * Base class for OpenRDF implementation classes of RMapObjects
 * 
 *  @author khansen, smorrissey
 *
 */
public abstract class ORMapObject implements RMapObject  {
	protected java.net.URI id;
	
	private ORMapService service = null;
	private ValueFactory valueFactory=null;
	
	protected ORMapService getService() throws RMapException {
		if (service==null){
			RMapService rservice = RMapServiceFactoryIOC.getFactory().createService();
			if (!(rservice instanceof ORMapService)){
				throw new RMapException("RMapService is not ORMapService");
			}
			this.service = (ORMapService)rservice;
			try {
				this.valueFactory = this.getService().getTriplestore().getValueFactory();
			} catch (RepositoryException e) {
				throw new RMapException("Exception thrown creating ValueFactory", e);
			}
		}
		return this.service;
	}
	
	protected ValueFactory getValueFactory() throws RMapException{
		if (this.valueFactory == null){
			try {
				this.valueFactory = this.getService().getTriplestore().getValueFactory();
			} catch (RepositoryException e) {
				throw new RMapException("Exception thrown creating ValueFactory", e);
			}
		}
		return this.valueFactory;
	}
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
	
	public abstract Model getAsModel() throws RMapException;

}

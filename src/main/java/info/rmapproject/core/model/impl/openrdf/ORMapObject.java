/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;


import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.idvalidator.RMapIdPredicate;
import info.rmapproject.core.model.RMapObject;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;

import org.apache.commons.collections4.Predicate;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;


/**
 * Base class for OpenRDF implementation classes of RMapObjects
 * 
 *  @author khansen, smorrissey
 *
 */
public abstract class ORMapObject implements RMapObject  {
	protected URI id;
	protected ValueFactory valueFactory=null;	
	protected Statement typeStatement;
	protected URI context;

	protected ValueFactory getValueFactory() throws RMapException{
		if (this.valueFactory == null){
			try {
				this.valueFactory = SesameTriplestoreFactoryIOC.getFactory().createTriplestore().getValueFactory();
			} catch (Exception e) {
				throw new RMapException("Exception thrown creating ValueFactory", e);
			}
		}
		return this.valueFactory;
	}
	
	
	
	/**
	 * Base Constructor for all RMapObjects instances, which must have a unique java.net.URI identifier 
	 * @throws Exception 
	 */
	protected ORMapObject() throws RMapException {
		super();
		this.setId();	
	}
	
	/**
	 * Return identifier of object as java.net.URI
	 * @return
	 */
	public RMapUri getId(){
		RMapUri id = null;
		if (this.id!=null){
			id = ORAdapter.openRdfUri2RMapUri(this.id);
		}
		return id;
	}

	/**
	 * Assigns a new id
	 * @param id
	 * @throws RMapException
	 */
	protected void setId(URI id) {		
		if (id == null || id.toString().length()==0)
			{throw new RMapException("Object ID is null or empty");}
		this.id = id;
		setContext(id); //context always corresponds to ID
	}

	/**
	 * Creates and assigns a new id
	 * @param id
	 * @throws RMapException
	 */
	protected void setId() throws RMapException{		
		try {
			setId(ORAdapter.uri2OpenRdfUri(IdServiceFactoryIOC.getFactory().createService().createId()));
		} catch (Exception e) {
			throw new RMapException("Could not generate valid ID for RMap object", e);
		}
	}
	
	public abstract Model getAsModel() throws RMapException;

	/**
	 * @return the typeStatement
	 */
	public Statement getTypeStatement() {
		return typeStatement;
	}
	
	/**
	 * @param type
	 */
	protected void setTypeStatement (URI type) throws RMapException{
		if (type==null){
			throw new RMapException("The type statement could not be created because a valid type was not provided");
		}
		if (this.id == null || this.context==null){
			throw new RMapException("The object ID and context value must be set before creating a type statement");
		}
		Statement stmt = this.getValueFactory().createStatement(this.id, RDF.TYPE, type, this.context);
		this.typeStatement = stmt;
	}

	public RMapUri getType() throws RMapException {
		Value v = this.getTypeStatement().getObject();
		RMapUri uri = null;
		if (v instanceof URI){
			URI vUri = (URI)v;
			uri = ORAdapter.openRdfUri2RMapUri(vUri);
		}
		else {
			throw new RMapException("Type statement object is not a URI: " + v.stringValue());
		}
		return uri;
	}
	
	public boolean isRMapUri(URI uri){
		boolean isRMapId = false;
		java.net.URI javaNetUri = ORAdapter.openRdfUri2URI(uri);
		
		try {
			Predicate<Object> predicate = RMapIdPredicate.rmapIdPredicate();
			isRMapId  = predicate.evaluate(javaNetUri);
		} catch (Exception e) {
			throw new RMapException ("Unable to validate as an RMap URI: " + uri.toString(), e);
		}	
		return isRMapId;
	}
	

	/**
	 * @param context
	 */
	protected void setContext (URI context) {	
		this.context = context;
	}
	/**
	 * @return the context
	 */
	public URI getContext() {
		return context;
	}
	
	
}

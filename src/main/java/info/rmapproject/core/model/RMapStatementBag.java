/**
 * 
 */
package info.rmapproject.core.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.bag.PredicatedBag;

import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;

/**
 * The list of related resources submitted with a DiSCO can be a list of RMapStatements,
 * or a list of RMapStatment ids, or a lit of both RMapStatements and RMAPStatement ids.
 * This Bag class is created to reflect possible mixed nature of that list
 *
 * @author smorrissey
 *
 */
public class RMapStatementBag{
		
	protected PredicatedBag<Object> bag = null;	
	protected List<URI> uris = null;
	
	/**
	 * Class to validate objects added to Bag to make sure they are either RMapStatements or
	 * RMapStatement ids
	 * @return
	 */
	Predicate<Object> statementPredicate() {
		return new Predicate<Object>() {
			public boolean evaluate (Object object){
				boolean isValid = false;
				do {
					if (object instanceof RMapStatement){
						isValid=true;
						break;
					}
					if (! (object instanceof URI)){
						break;
					}
					URI uri = (URI)object;				
					try{
						RMapStatement stmt = (RMapServiceFactoryIOC.getFactory().createService())
								.readStatement(uri);
						if (stmt != null){
							isValid = true;
						}
						break;
					} catch (Exception e) {
						isValid = false;
						break;
					}
				}while (false);
				return isValid;
			};
		};
	}
	
	/**
	 * Constructor
	 */
	public RMapStatementBag() {
		super();
		bag = PredicatedBag.predicatedBag(new HashBag<Object>(), statementPredicate());
		uris = new ArrayList<URI>();
	}
	/**
	 * Add object to bag.  Object will be validated as either RMapStatment or RMapStatement id
	 * @param object object to be added to bag
	 * @return true the result of adding to underlying collection
	 * @throws IllegalArgumentException if object is not RMapStatement or RMapStatement id
	 */
	public boolean add(Object object) throws IllegalArgumentException {
		boolean isAdded = false;
		isAdded = bag.add(object);
		this.addURI(object);
		return isAdded;
	}
	/**
	 * Add list of objects to bag. Objects will be validated as either RMapStatments or RMapStatement ids
	 * @param objects to be added to bag
	 * @return the result of adding to the underlying collection  
	 * @throws IllegalArgumentException if any object in list is not RMapStatement or RMapStatement id
	 */
	public boolean addAll(List<Object> objects) throws IllegalArgumentException {
		boolean isAdded = false;
		isAdded= bag.addAll(objects);
		for (Object object:objects){
			this.addURI(object);
		}
		return isAdded;
	}
	/**
	 * Add URI of Statement to list of URIs in bag
	 * Will be needed when DiSCO is created to test if Resources form a graph
	 * @param object
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected boolean addURI(Object object) throws IllegalArgumentException{
		boolean added = false;
		URI id = null;
		if (object instanceof RMapStatement){
			RMapStatement stmt = (RMapStatement)object;
			id = stmt.getId();
		}
		else if (object instanceof URI){
			id = (URI)object;
		}
		else throw new IllegalArgumentException();
		added = uris.add(id);
		return added;
	}
	/**
	 * Get list of all statement ids in bag
	 * @return
	 */
	public List<URI> getStatementIds (){
		do {
			if (uris != null  ){
				break;
			}
		}while (false);
		return uris;
	}
	/**
	 * 
	 * @return count of items in bag
	 */
	public int size() {
		return this.bag.size();
	}
	/**
	 * 
	 * @return all objects in the bag
	 */
	public Object[] getContents(){
		Object[] objects = null;
		objects = this.bag.toArray();
		return objects;
	}

}

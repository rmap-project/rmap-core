package info.rmapproject.core.rmapservice.impl.openrdf.vocabulary;

import org.openrdf.model.Namespace;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * Vocabulary constants for the RMapProject Metadata Element Set, version 1.0
 * 
 * @see http://rmap-project.org/elements/
 * @author Karen Hanson
 */
public class RMAP {

	/**
	 * RMapProject elements namespace: http://rmap-project.org/terms/
	 */
	public static final String NAMESPACE = "http://rmap-project.org/rmap/terms/";
	
		
	/**
	 * Recommend prefix for the RMapProject elements namespace: "rmap"
	 */
	public static final String PREFIX = "rmap";

	/**
	 * An immutable {@link Namespace} constant that represents the RMapProject
	 * namespace.
	 */
	public static final Namespace NS = new NamespaceImpl(PREFIX, NAMESPACE);

	/**
	 * rmap:Statement
	 */
	public static final URI STATEMENT;

	/**
	 * rmap:DiSCO
	 */
	public static final URI DISCO;

	/**
	 * rmap:Agent
	 */
	public static final URI AGENT;
	
	/**
	 * 
	 */
	public static final URI EVENT;
	/**
	 * 
	 */
	public static final URI EVENT_TYPE;
	/**
	 * 
	 */
	public static final URI EVENT_TYPE_CREATION;
	/**
	 * 
	 */
	public static final URI EVENT_TYPE_UPDATE;
	/**
	 * 
	 */
	public static final URI EVENT_TYPE_INACTIVATION;
	/**
	 * 
	 */
	public static final URI EVENT_TYPE_DERIVATION;
	/**
	 * 
	 */
	public static final URI EVENT_TYPE_TOMBSTONE;
	/**
	 * 
	 */
	public static final URI EVENT_TYPE_DELETION;
	/**
	 * 
	 */
	public static final URI EVENT_TARGET_TYPE;
	/**
	 * 
	 */
	public static final URI EVENT_SOURCE_OBJECT;
	/**
	 * 	
	 */
	public static final URI EVENT_DERIVED_OBJECT;
	/**
	 * 
	 */
	public static final URI EVENT_INACTIVATED_OBJECT;
	/**
	 * 
	 */
	public static final URI EVENT_TARGET_TOMBSTONED;
	/**
	 * 
	 */
	public static final URI EVENT_TARGET_DELETED;
	/**
	 * 
	 */
	public static final URI PROVIDERID;
	/**
	* rmap:Status
	*/
		public static final URI HAS_STATUS;
	
	//*****************************NEEDS UPDTIG
	static {
		final ValueFactory f = ValueFactoryImpl.getInstance();
		
		//rmap data types 
		STATEMENT = f.createURI(NAMESPACE, "Statement");
		DISCO = f.createURI(NAMESPACE, "DiSCO");
		AGENT = f.createURI(NAMESPACE, "Agent");
		EVENT = f.createURI(NAMESPACE, "Event");
		EVENT_TYPE = f.createURI(NAMESPACE, "eventType");
		EVENT_TYPE_CREATION = f.createURI(NAMESPACE, "creation");
		EVENT_TYPE_UPDATE = f.createURI(NAMESPACE, "update");
		EVENT_TYPE_INACTIVATION = f.createURI(NAMESPACE, "inactivation");
		EVENT_TYPE_DERIVATION = f.createURI(NAMESPACE, "derivation");
		EVENT_TYPE_TOMBSTONE = f.createURI(NAMESPACE, "tombstone");
		EVENT_TYPE_DELETION = f.createURI(NAMESPACE, "deletion");
		EVENT_TARGET_TYPE = f.createURI(NAMESPACE, "eventTargetType");
		EVENT_SOURCE_OBJECT = f.createURI(NAMESPACE, "sourceObject");
		EVENT_INACTIVATED_OBJECT = f.createURI(NAMESPACE, "inactivatedObject");
		EVENT_DERIVED_OBJECT = f.createURI(NAMESPACE, "derivedObject");
		EVENT_TARGET_TOMBSTONED = f.createURI(NAMESPACE, "tombstonedObject");
		EVENT_TARGET_DELETED = f.createURI(NAMESPACE, "deletedObject");		  
		PROVIDERID = f.createURI(NAMESPACE, "providerId");
		HAS_STATUS = f.createURI(NAMESPACE, "hasStatus");
	}
}
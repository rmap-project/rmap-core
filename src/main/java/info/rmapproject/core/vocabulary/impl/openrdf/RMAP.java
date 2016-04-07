package info.rmapproject.core.vocabulary.impl.openrdf;

import info.rmapproject.core.utils.Terms;

import org.openrdf.model.IRI;
import org.openrdf.model.Namespace;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleNamespace;
import org.openrdf.model.impl.SimpleValueFactory;

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
	public static final String NAMESPACE = Terms.RMAP_NAMESPACE;
	
		
	/**
	 * Recommend prefix for the RMapProject elements namespace: "rmap"
	 */
	public static final String PREFIX = Terms.RMAP_PREFIX;

	/**
	 * An immutable {@link Namespace} constant that represents the RMapProject
	 * namespace.
	 */
	public static final Namespace NS = new SimpleNamespace(PREFIX, NAMESPACE);

	
	/**
	 * rmap:Object
	 */
	public static final IRI OBJECT;
	
	/**
	 * rmap:DiSCO
	 */
	public static final IRI DISCO;

	/**
	 * rmap:Agent
	 */
	public static final IRI AGENT;
	
	/**
	 * rmap:Event
	 */
	public static final IRI EVENT;
	/**
	 * rmap:eventType
	 */
	public static final IRI EVENTTYPE;
	/**
	 * rmap:Creation
	 */
	public static final IRI CREATION;
	/**
	 * rmap:Update
	 */
	public static final IRI UPDATE;
	/**
	 * rmap:Inactivation
	 */
	public static final IRI INACTIVATION;
	/**
	 * rmap:Derivation
	 */
	public static final IRI DERIVATION;
	/**
	 * rmap:Replace
	 */
	public static final IRI REPLACE;
	/**
	 * rmap:Tombstone
	 */
	public static final IRI TOMBSTONE;
	/**
	 * rmap:Deletion
	 */
	public static final IRI DELETION;
	/**
	 * rmap:targetType
	 */
	public static final IRI TARGETTYPE;
	/**
	 * rmap:hasSourceObject
	 */
	public static final IRI HASSOURCEOBJECT;
	/**
	 * 	rmap:derivedObject
	 */
	public static final IRI DERIVEDOBJECT;
	/**
	 * rmap:inactivatedObject
	 */
	public static final IRI INACTIVATEDOBJECT;
	/**
	 * rmap:tombstonedObject
	 */
	public static final IRI TOMBSTONEDOBJECT;
	/**
	 * rmap:deletedObject
	 */
	public static final IRI DELETEDOBJECT;
	/**
	 * rmap:updatedObject
	 */
	public static final IRI UPDATEDOBJECT;
	/**
	 * rmap:identityProvider
	 */
	public static final IRI IDENTITYPROVIDER;
	/**
	 * rmap:userAuthId
	 */
	public static final IRI USERAUTHID;
	/**
	 * rmap:providerId
	 */
	public static final IRI PROVIDERID;
	

	/**
	 * rmap:Active
	 */
	public static final IRI ACTIVE;
	/**
	 * rmap:Deleted
	 */
	public static final IRI DELETED;
	/**
	 * rmap:Tombstoned
	 */
	public static final IRI TOMBSTONED;
	/**
	 * rmap:Inactive
	 */
	public static final IRI INACTIVE;
	
	
	
	
	/**
	 * 
	 */
	/**
	* rmap:hasStatus
	*/
		public static final IRI HASSTATUS;
	
	static {
		final ValueFactory f = SimpleValueFactory.getInstance();

		OBJECT = f.createIRI(NAMESPACE, Terms.RMAP_OBJECT);
		
		//rmap object types 
		DISCO = f.createIRI(NAMESPACE, Terms.RMAP_DISCO);
		AGENT = f.createIRI(NAMESPACE, Terms.RMAP_AGENT);
		EVENT = f.createIRI(NAMESPACE, Terms.RMAP_EVENT);
				
		EVENTTYPE = f.createIRI(NAMESPACE, Terms.RMAP_EVENTTYPE);
		
		//Event types
		CREATION = f.createIRI(NAMESPACE, Terms.RMAP_CREATION);
		UPDATE = f.createIRI(NAMESPACE, Terms.RMAP_UPDATE);
		INACTIVATION = f.createIRI(NAMESPACE, Terms.RMAP_INACTIVATION);
		DERIVATION = f.createIRI(NAMESPACE, Terms.RMAP_DERIVATION);
		TOMBSTONE = f.createIRI(NAMESPACE, Terms.RMAP_TOMBSTONE);
		DELETION = f.createIRI(NAMESPACE, Terms.RMAP_DELETION);
		REPLACE = f.createIRI(NAMESPACE, Terms.RMAP_REPLACE);
		
		//Event target type
		TARGETTYPE = f.createIRI(NAMESPACE, Terms.RMAP_EVENTTARGETTYPE);
		
		//Relationships between Objects and Events
		HASSOURCEOBJECT = f.createIRI(NAMESPACE, Terms.RMAP_HASSOURCEOBJECT);
		INACTIVATEDOBJECT = f.createIRI(NAMESPACE, Terms.RMAP_INACTIVATEDOBJECT);
		DERIVEDOBJECT = f.createIRI(NAMESPACE, Terms.RMAP_DERIVEDOBJECT);
		TOMBSTONEDOBJECT = f.createIRI(NAMESPACE, Terms.RMAP_TOMBSTONED);
		DELETEDOBJECT = f.createIRI(NAMESPACE, Terms.RMAP_DELETEDOBJECT);	
		UPDATEDOBJECT = f.createIRI(NAMESPACE, Terms.RMAP_UPDATEDOBJECT);	
		
		//Statuses
		HASSTATUS = f.createIRI(NAMESPACE, Terms.RMAP_HASSTATUS); 
		ACTIVE = f.createIRI(NAMESPACE, Terms.RMAP_ACTIVE);
		INACTIVE = f.createIRI(NAMESPACE, Terms.RMAP_INACTIVE);
		DELETED = f.createIRI(NAMESPACE, Terms.RMAP_DELETED);	
		TOMBSTONED = f.createIRI(NAMESPACE, Terms.RMAP_TOMBSTONED);	
				 
		//Agent properties
		PROVIDERID = f.createIRI(NAMESPACE, Terms.RMAP_PROVIDERID);
		IDENTITYPROVIDER = f.createIRI(NAMESPACE, Terms.RMAP_IDENTITYPROVIDER);	
		USERAUTHID = f.createIRI(NAMESPACE, Terms.RMAP_USERAUTHID);	
	}
}
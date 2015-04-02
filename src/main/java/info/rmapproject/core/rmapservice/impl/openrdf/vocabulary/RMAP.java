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
	 * rmap:SystemAgent
	 */
	public static final URI SYSTEM_AGENT;
	/**
	 * rmap:ScholarlyAgent
	 */
	public static final URI SCHOLARLY_AGENT;
	/**
	 * 
	 */
	public static final URI PROFILE;
	/**
	 * 
	 */
	public static final URI DESCRIBES_AGENT;
	/**
	 * 
	 */
	public static final URI PROFILE_ID_BY;
	/**
	 * 
	 */
	public static final URI PROFILE_PREFERRED_ID;
	/**
	 * 
	 */
	public static final URI IDENTITY;
	/**
	 * 
	 */
	public static final URI IDLOCALPART;
	/**
	 *
	 */
	public static final URI IDPROVIDERID;
	/**
	 * 
	 */
	public static final URI IDENTITYPROVIDER;
	/**
	 * 
	 */
	public static final URI IDENTITYCONFIGID;
	/**
	 * 
	 */
	public static final URI IDENTITYCONFIG;
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
	 * rmap:RMapAgentId
	 */
	public static final URI RMAP_AGENT_ID;

	/**
	 * rmap:Aggregates
	 */
	public static final URI AGGREGATES;
	/**
	 * rmap:Status
	 */
	public static final URI HAS_STATUS;

	/**
	 * rmap:Active
	 */
	public static final URI ACTIVE;
	

	/**
	 * rmap:Inactive
	 */
	public static final URI INACTIVE;


	/**
	 * rmap:Deleted
	 */
	public static final URI DELETED;


	/**
	 * rmap:Tombstoned
	 */
	public static final URI TOMBSTONED;
	
	public static final URI PROVIDERID;
	
	//*****************************NEEDS UPDTIG
	static {
		final ValueFactory f = ValueFactoryImpl.getInstance();
		
		//rmap data types 
		STATEMENT = f.createURI(NAMESPACE, "Statement");
		DISCO = f.createURI(NAMESPACE, "DiSCO");
		AGENT = f.createURI(NAMESPACE, "Agent");
		SYSTEM_AGENT = f.createURI(NAMESPACE, "SystemAgent");
		SCHOLARLY_AGENT = f.createURI(NAMESPACE, "ScholarlyAgent");
		PROFILE = f.createURI(NAMESPACE, "Profile");
		PROFILE_ID_BY = f.createURI(NAMESPACE,"identifiedBy");
		PROFILE_PREFERRED_ID = f.createURI(NAMESPACE, "preferredId");
		IDENTITY = f.createURI(NAMESPACE, "Identity");
		IDLOCALPART = f.createURI(NAMESPACE, "identityLocalPart");
		IDPROVIDERID = f.createURI(NAMESPACE, "identityProviderId");
		IDENTITYPROVIDER = f.createURI(NAMESPACE, "IdentityProvider");
		IDENTITYCONFIGID = f.createURI(NAMESPACE, "identityConfigurationId");
		IDENTITYCONFIG = f.createURI(NAMESPACE, "IdentityConfiguration");
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
		AGGREGATES = f.createURI(NAMESPACE, "aggregates");   
		RMAP_AGENT_ID = f.createURI(NAMESPACE,"agentId");
		HAS_STATUS = f.createURI(NAMESPACE, "hasStatus");
		ACTIVE = f.createURI(NAMESPACE, "active"); 
		INACTIVE = f.createURI(NAMESPACE, "inactive");
		DELETED = f.createURI(NAMESPACE, "deleted");
		TOMBSTONED = f.createURI(NAMESPACE, "tombstoned");
		PROVIDERID = f.createURI(NAMESPACE, "providerId");
		DESCRIBES_AGENT = f.createURI(NAMESPACE, "describes");
	}
}
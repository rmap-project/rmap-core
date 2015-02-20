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

  //*****************************LETS TALK ***********************************************
	/**
	 * RMapProject ID urls: http://rmap-project.org/data?%
	 */
//	public static final String API_NS = "http://rmapdns.ddns.net:8080/api/";
//	public static final String DATA_NS = "http://rmapdns.ddns.net:8080/data/";
//	public static final String STATEMENT_DATA_NS = "http://rmapdns.ddns.net:8080/data/stmtid/";
	public static final String AGENT_DATA_NS = "http://rmapdns.ddns.net:8080/data/agentid/";
//	public static final String PROFILE_DATA_NS = "http://rmapdns.ddns.net:8080/data/profileid/";
//	public static final String DISCO_DATA_NS = "http://rmapdns.ddns.net:8080/data/discoid/";
//	public static final String EVENT_DATA_NS = "http://rmapdns.ddns.net:8080/data/eventid/";
	
		
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
	public static final URI EVENT;
	/**
	 * 
	 */
	public static final URI EVENT_TYPE;
	/**
	 * 
	 */
	public static final URI EVENT_TYPE_CREATE;
	/**
	 * 
	 */
	public static final URI EVENT_TYPE_UPDATE;
	/**
	 * 
	 */
	public static final URI EVENT_TYPE_TOMBSTONE;
	/**
	 * 
	 */
	public static final URI EVENT_TYPE_DELETE;
	/**
	 * 
	 */
	public static final URI EVENT_TARGET_TYPE;
	/**
	 * 
	 */
	public static final URI EVENT_TARGET;
	/**
	 * 
	 */
	public static final URI EVENT_TARGET_INACTIVATED;
	/**
	 * 
	 */
	public static final URI EVENT_TARGET_DERIVATION_SOURCE;
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
		EVENT_TYPE = f.createURI(NAMESPACE, "eventType");
		EVENT_TYPE_CREATE = f.createURI(NAMESPACE, "create");
		EVENT_TYPE_UPDATE = f.createURI(NAMESPACE, "update");
		EVENT_TYPE_TOMBSTONE = f.createURI(NAMESPACE, "tombstone");
		EVENT_TYPE_DELETE = f.createURI(NAMESPACE, "delete");
		EVENT_TARGET_TYPE = f.createURI(NAMESPACE, "eventTargetType");
		EVENT_TARGET = f.createURI(NAMESPACE, "eventTarget");
		EVENT_TARGET_INACTIVATED = f.createURI(NAMESPACE, "inactivated");
		EVENT_TARGET_DERIVATION_SOURCE = f.createURI(NAMESPACE, "derivationSource");
		EVENT_TARGET_TOMBSTONED = f.createURI(NAMESPACE, "tombstoned");
		EVENT_TARGET_DELETED = f.createURI(NAMESPACE, "deleted");
		EVENT = f.createURI(NAMESPACE, "Event");
		AGGREGATES = f.createURI(NAMESPACE, "aggregates");   
		RMAP_AGENT_ID = f.createURI(AGENT_DATA_NS,"RMap");
		HAS_STATUS = f.createURI(NAMESPACE, "hasStatus");
		ACTIVE = f.createURI(NAMESPACE, "active"); 
		INACTIVE = f.createURI(NAMESPACE, "inactive");
		DELETED = f.createURI(NAMESPACE, "deleted");
		TOMBSTONED = f.createURI(NAMESPACE, "tombstoned");
		PROVIDERID = f.createURI(NAMESPACE, "providerId");
	}
}
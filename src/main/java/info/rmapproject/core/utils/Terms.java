package info.rmapproject.core.utils;

public final class Terms  {
	
	 /*RMap vocabulary constants*/
	 public static final String RMAP_NAMESPACE = "http://rmap-project.org/rmap/terms/";
	 public static final String RMAP_PREFIX = "rmap";

	 public static final String RMAP_OBJECT = "Object";
	 public static final String RMAP_DISCO = "DiSCO";
	 public static final String RMAP_AGENT = "Agent";
	 public static final String RMAP_EVENT = "Event";
	 public static final String RMAP_EVENTTYPE = "eventType";
	 public static final String RMAP_CREATION = "Creation";
	 public static final String RMAP_UPDATE = "Update";
	 public static final String RMAP_INACTIVATION = "Inactivation";
	 public static final String RMAP_DERIVATION = "Derivation";
	 public static final String RMAP_TOMBSTONE = "Tombstone";
	 public static final String RMAP_DELETION = "Deletion";
	 public static final String RMAP_REPLACE = "Replace";
	 public static final String RMAP_EVENTTARGETTYPE = "eventTargetType";
	 public static final String RMAP_HASSOURCEOBJECT = "hasSourceObject";
	 public static final String RMAP_INACTIVATEDOBJECT = "inactivatedObject";
	 public static final String RMAP_DERIVEDOBJECT = "derivedObject";
	 public static final String RMAP_TOMBSTONEDOBJECT = "tombstonedObject";
	 public static final String RMAP_DELETEDOBJECT = "deletedObject";
	 public static final String RMAP_UPDATEDOBJECT = "updatedObject";
	 public static final String RMAP_PROVIDERID = "providerId";
	 public static final String RMAP_IDENTITYPROVIDER = "identityProvider";
	 public static final String RMAP_USERAUTHID = "userAuthId";

	 public static final String RMAP_HASSTATUS = "hasStatus";
	 public static final String RMAP_ACTIVE = "Active";
	 public static final String RMAP_INACTIVE = "Inactive";
	 public static final String RMAP_DELETED = "Deleted";
	 public static final String RMAP_TOMBSTONED = "Tombstoned";
	 
	 /*Path requests...*/
	 public static final String RMAP_OBJECT_PATH = RMAP_NAMESPACE + RMAP_OBJECT;
	 public static final String RMAP_DISCO_PATH = RMAP_NAMESPACE + RMAP_DISCO;
	 public static final String RMAP_AGENT_PATH = RMAP_NAMESPACE + RMAP_AGENT;
	 public static final String RMAP_EVENT_PATH = RMAP_NAMESPACE + RMAP_EVENT;
	 public static final String RMAP_EVENTTYPE_PATH = RMAP_NAMESPACE + RMAP_EVENTTYPE;
	 public static final String RMAP_CREATION_PATH = RMAP_NAMESPACE + RMAP_CREATION;
	 public static final String RMAP_UPDATE_PATH = RMAP_NAMESPACE + RMAP_UPDATE;
	 public static final String RMAP_INACTIVATION_PATH = RMAP_NAMESPACE + RMAP_INACTIVATION;
	 public static final String RMAP_DERIVATION_PATH = RMAP_NAMESPACE + RMAP_DERIVATION;
	 public static final String RMAP_TOMBSTONE_PATH = RMAP_NAMESPACE + RMAP_TOMBSTONE;
	 public static final String RMAP_DELETION_PATH = RMAP_NAMESPACE + RMAP_DELETION;
	 public static final String RMAP_REPLACE_PATH = RMAP_NAMESPACE + RMAP_REPLACE;
	 public static final String RMAP_EVENTTARGETTYPE_PATH = RMAP_NAMESPACE + RMAP_EVENTTARGETTYPE;
	 public static final String RMAP_HASSOURCEOBJECT_PATH = RMAP_NAMESPACE + RMAP_HASSOURCEOBJECT;
	 public static final String RMAP_INACTIVATEDOBJECT_PATH = RMAP_NAMESPACE + RMAP_INACTIVATEDOBJECT;
	 public static final String RMAP_DERIVEDOBJECT_PATH = RMAP_NAMESPACE + RMAP_DERIVEDOBJECT;
	 public static final String RMAP_TOMBSTONEDOBJECT_PATH = RMAP_NAMESPACE + RMAP_TOMBSTONEDOBJECT;
	 public static final String RMAP_DELETEDOBJECT_PATH = RMAP_NAMESPACE + RMAP_DELETEDOBJECT;
	 public static final String RMAP_UPDATEDOBJECT_PATH = RMAP_NAMESPACE + RMAP_UPDATEDOBJECT;
	 public static final String RMAP_HASSTATUS_PATH = RMAP_NAMESPACE + RMAP_HASSTATUS;
	 public static final String RMAP_ACTIVE_PATH = RMAP_NAMESPACE + RMAP_ACTIVE;
	 public static final String RMAP_INACTIVE_PATH = RMAP_NAMESPACE + RMAP_INACTIVE;
	 public static final String RMAP_DELETED_PATH = RMAP_NAMESPACE + RMAP_DELETED;
	 public static final String RMAP_TOMBSTONED_PATH = RMAP_NAMESPACE + RMAP_TOMBSTONED;
	 public static final String RMAP_PROVIDERID_PATH = RMAP_NAMESPACE + RMAP_PROVIDERID;
	 public static final String RMAP_IDENTITYPROVIDER_PATH = RMAP_NAMESPACE + RMAP_IDENTITYPROVIDER;
	 public static final String RMAP_USERAUTHID_PATH = RMAP_NAMESPACE + RMAP_USERAUTHID;

	 /*PROV vocabulary constants*/
	 public static final String PROV_NAMESPACE = "http://www.w3.org/ns/prov#";
	 public static final String PROV_PREFIX = "prov";
	 public static final String PROV_ACTIVITY = "Activity";
	 public static final String PROV_STARTEDATTIME = "startedAtTime";
	 public static final String PROV_ENDEDATTIME = "endedAtTime";
	 public static final String PROV_WASASSOCIATEDWITH = "wasAssociatedWith";
	 public static final String PROV_WASGENERATEDBY = "wasGeneratedBy";
	 public static final String PROV_WASDERIVEDFROM = "wasDerivedFrom";
	 public static final String PROV_GENERATED = "generated";
	 public static final String PROV_HADACTIVITY = "hadActivity";
	 public static final String PROV_WASATTRIBUTEDTO = "wasAttributedTo";
	 public static final String PROV_HASPROVENANCE = "has_provenance";
	 public static final String PROV_USED = "used";
	 
	 public static final String PROV_ACTIVITY_PATH = PROV_NAMESPACE + PROV_ACTIVITY;
	 public static final String PROV_STARTEDATTIME_PATH = PROV_NAMESPACE + PROV_STARTEDATTIME;
	 public static final String PROV_ENDEDATTIME_PATH = PROV_NAMESPACE + PROV_ENDEDATTIME;
	 public static final String PROV_WASASSOCIATEDWITH_PATH = PROV_NAMESPACE + PROV_WASASSOCIATEDWITH;
	 public static final String PROV_WASGENERATEDBY_PATH = PROV_NAMESPACE + PROV_WASGENERATEDBY;
	 public static final String PROV_WASDERIVEDFROM_PATH = PROV_NAMESPACE + PROV_WASDERIVEDFROM;
	 public static final String PROV_GENERATED_PATH = PROV_NAMESPACE + PROV_GENERATED;
	 public static final String PROV_HADACTIVITY_PATH = PROV_NAMESPACE + PROV_HADACTIVITY;
	 public static final String PROV_WASATTRIBUTEDTO_PATH = PROV_NAMESPACE + PROV_WASATTRIBUTEDTO;
	 public static final String PROV_HASPROVENANCE_PATH = PROV_NAMESPACE + PROV_HASPROVENANCE;
	 public static final String PROV_USED_PATH = PROV_NAMESPACE + PROV_USED;
	  
 
	 /*ORE vocabulary constants*/
	 public static final String ORE_NAMESPACE = "http://www.openarchives.org/ore/terms/";
	 public static final String ORE_PREFIX = "ore";
	 public static final String ORE_SIMILARTO = "simlarTo";
	 public static final String ORE_DESCRIBES = "describes";
	 public static final String ORE_AGGREGATION = "Aggregation";
	 public static final String ORE_AGGREGATES = "aggregates";

	 public static final String ORE_SIMILARTO_PATH = ORE_NAMESPACE + ORE_SIMILARTO;
	 public static final String ORE_DESCRIBES_PATH = ORE_NAMESPACE + ORE_DESCRIBES;
	 public static final String ORE_AGGREGATION_PATH = ORE_NAMESPACE + ORE_AGGREGATION;
	 public static final String ORE_AGGREGATES_PATH = ORE_NAMESPACE + ORE_AGGREGATES;
	 		  	  
	 private Terms(){
		    //this prevents even the native class from calling this ctor as well :
		    throw new AssertionError();
		  }
}

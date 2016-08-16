package info.rmapproject.core.utils;

/**
 * Class to define system constants used throughout the java project.
 */
public final class Constants  {

	  /** File path to RMap Core Spring context. */
	  public static final String SPRING_CONFIG_FILEPATH = "spring-rmapcore-context.xml";
	  
	  /** File path to RMap Core Properties. */
	  public static final String RMAPCORE_PROPFILE = "rmapcore";
	  
	  /** File path to Ark Properties. */
	  public static final String ARKSERVICE_PROPFILE = "rmapcore";
	  
	  /** File path to Sesame Service Properties. */
	  public static final String SESAMESERVICE_PROPFILE = "rmapcore";
	  	  
	  /** Default maximum number of records returned from triple store in one go. */
	  public static final String DEFAULT_QUERY_LIMIT_KEY="rmapcore.defaultQueryLimit"; 

	  /** Default status filter for queries that filter RMap Objects by status. */
	  public static final String MAX_QUERY_LIMIT_KEY="rmapcore.maxQueryLimit";    

	  /** Default status filter for queries that filter RMap Objects by status. */
	  public static final String DEFAULT_STATUS_FILTER_KEY="rmapcore.defaultStatusFilter";

	  /** Default status filter for queries that filter RMap Objects by status. */
	  public static final String DEFAULT_ORDERBY_FILTER_KEY="rmapcore.defaultOrderBy";   
	  
	  	  
	  /**
  	 * Instantiates a new constants.
  	 */
  	private Constants(){
		    //this prevents even the native class from calling this ctor as well :
		    throw new AssertionError();
		  }
}

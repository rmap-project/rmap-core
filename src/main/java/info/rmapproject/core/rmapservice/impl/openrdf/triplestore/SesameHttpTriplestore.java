package info.rmapproject.core.rmapservice.impl.openrdf.triplestore;


import info.rmapproject.core.utils.ConfigUtils;
import info.rmapproject.core.utils.Constants;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

/**
 * Class for a Sesame triplestore in which the API is available via an HTTP path.
 *
 * @author khanson, smorrissey
 */
public class SesameHttpTriplestore  extends SesameTriplestore{

	/** The key for the URL property. */
	private static final String URL_PROPERTY = "sesamehttp.url";
	
	/** The key for the Repository Name property. */
	private static final String REPOS_NAME_PROPERTY = "sesamehttp.repositoryName";
	
	/** The key for the Sesame user property. */
	private static final String USER_PROPERTY = "sesamehttp.user";
	
	/** The key for the Sesame password property. */
	private static final String PASSWORD_PROPERTY = "sesamehttp.password";
		
	/** The Sesame URL. */
	private String sesameUrl = "";
    
    /** The Sesame repository name. */
    private String sesameReposName = "";
    
    /** The Sesame user name. */
    private String sesameUserName = "";
    
    /** The Sesame password. */
    private String sesamePassword = "";
	
    /**
     * Instantiates a new Sesame HTTP triplestore.
     */
    public SesameHttpTriplestore()	{
		this(Constants.SESAMESERVICE_PROPFILE);
	}
	
	/**
     * Instantiates a new Sesame HTTP triplestore.
	 *
	 * @param propertyFileName the property file name
	 */
	public SesameHttpTriplestore(String propertyFileName) {	
		Map<String, String> properties = new HashMap<String, String>();
		properties = ConfigUtils.getPropertyValues(propertyFileName);
		sesameUrl = properties.get(URL_PROPERTY);
		sesameReposName = properties.get(REPOS_NAME_PROPERTY);
		sesameUserName = properties.get(USER_PROPERTY);
		sesamePassword = properties.get(PASSWORD_PROPERTY);
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore#intitializeRepository()
	 */
	protected Repository intitializeRepository() throws RepositoryException {
    	if (repository == null)	{
	    	//Create connection to Sesame DB
    		HTTPRepository rmapHttpRepo = new HTTPRepository(
    				sesameUrl,sesameReposName);
    		rmapHttpRepo.setUsernameAndPassword(sesameUserName,sesamePassword);
    		repository = rmapHttpRepo;
    		repository.initialize();	
    	}
    	return repository;
	}
		

	  
}

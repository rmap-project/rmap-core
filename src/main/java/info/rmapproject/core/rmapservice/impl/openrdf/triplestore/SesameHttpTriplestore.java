package info.rmapproject.core.rmapservice.impl.openrdf.triplestore;


import info.rmapproject.core.utils.ConfigUtils;
import info.rmapproject.core.utils.Constants;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

/**
 * 
 *  @author khanson, smorrissey
 *
 */
public class SesameHttpTriplestore  extends SesameTriplestore{

	private static final String URL_PROPERTY = "sesamehttp.url";
	private static final String REPOS_NAME_PROPERTY = "sesamehttp.repositoryName";
	private static final String USER_PROPERTY = "sesamehttp.user";
	private static final String PASSWORD_PROPERTY = "sesamehttp.password";
		
	private String sesameUrl = "";
    private String sesameReposName = "";
    private String sesameUserName = "";
    private String sesamePassword = "";
	
    public SesameHttpTriplestore()	{
		this(Constants.SESAMESERVICE_PROPFILE);
	}
	
	public SesameHttpTriplestore(String propertyFileName) {	
		Map<String, String> properties = new HashMap<String, String>();
		properties = ConfigUtils.getPropertyValues(propertyFileName);
		sesameUrl = properties.get(URL_PROPERTY);
		sesameReposName = properties.get(REPOS_NAME_PROPERTY);
		sesameUserName = properties.get(USER_PROPERTY);
		sesamePassword = properties.get(PASSWORD_PROPERTY);
	}

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

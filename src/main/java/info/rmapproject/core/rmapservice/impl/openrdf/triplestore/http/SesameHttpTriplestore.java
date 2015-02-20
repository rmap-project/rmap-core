package info.rmapproject.core.rmapservice.impl.openrdf.triplestore.http;


import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.utils.ConfigUtils;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;


public class SesameHttpTriplestore  extends SesameTriplestore{

    
	private static String SESAME_URL = null;
    private static String SESAME_REPOS_ID = null;
    private static String SESAME_USER = null;
    private static String SESAME_PASSWORD = null;

	private static Map<String, String> properties = new HashMap<String, String>();
	private static String PROPERTIES_FN = "sesamehttp";
	
	static{
		properties = ConfigUtils.getPropertyValues(PROPERTIES_FN);
		SESAME_URL = properties.get("SESAME_URL");
		SESAME_REPOS_ID = properties.get(SESAME_REPOS_ID);
		SESAME_USER = properties.get(SESAME_USER);
		SESAME_PASSWORD = properties.get(SESAME_PASSWORD);
	}
	 
	
	public SesameHttpTriplestore()	{
		super();
	}


	protected Repository intitializeRepository() throws RepositoryException {
    	if (repository == null)	{
	    	//Create connection to Sesame DB
    		HTTPRepository rmapHttpRepo = new HTTPRepository(
    				SESAME_URL,SESAME_REPOS_ID);
    		rmapHttpRepo.setUsernameAndPassword(SESAME_USER,SESAME_PASSWORD);
    		repository = rmapHttpRepo;
    		repository.initialize();	
    		try {
    			valueFactory = ((HTTPRepository)getRepository()).getValueFactory();
    		} catch (Exception e) {
    			throw new RepositoryException("Exception thrown creating HTTP value factory", e);
    		}
    	}
    	return repository;
	}
		

	  
}

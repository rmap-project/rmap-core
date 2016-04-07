/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf.triplestore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import info.rmapproject.core.utils.ConfigUtils;
import info.rmapproject.core.utils.Constants;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

/**
 *  @author khansen, smorrissey
 *
 */
public class SesameSailMemoryTriplestore extends SesameTriplestore {
	
	private static final String DATA_DIRECTORY_PROPERTY = "sesamesail.dataDirectory";
		
	private String dataDirectory = "";
	
	public SesameSailMemoryTriplestore()	{
		this(Constants.SESAMESERVICE_PROPFILE);
	}

	public SesameSailMemoryTriplestore(String propertyFileName) {	
		Map<String, String> properties = new HashMap<String, String>();
		properties = ConfigUtils.getPropertyValues(propertyFileName);
		dataDirectory = properties.get(DATA_DIRECTORY_PROPERTY);
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore#intitializeRepository()
	 */
	@Override
	protected Repository intitializeRepository() throws RepositoryException {
		
		if (repository==null){
			do {
				if (dataDirectory==null || dataDirectory.length()==0){
					// not persisting
					repository = new SailRepository(new MemoryStore());					
					break;
				}
				File dataFile = new File(dataDirectory);
				if (! dataFile.exists()){
					throw new RepositoryException ("Directory " + dataDirectory + " does not exist");
				}
				if (!dataFile.isDirectory()){
					throw new RepositoryException ("Directory " + dataDirectory + " is not a directory");
				}
				if (!dataFile.canRead()){
					throw new RepositoryException ("Directory " + dataDirectory + " cannot be read");
				}
				if (!dataFile.canWrite()){
					throw new RepositoryException ("Directory " + dataDirectory + " cannot be written to");
				}
				repository = new SailRepository(new MemoryStore(dataFile));
			}while (false);
			repository.initialize();
		}		
		return repository;
	}

}

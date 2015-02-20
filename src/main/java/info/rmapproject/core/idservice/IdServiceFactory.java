/**
 * 
 */
package info.rmapproject.core.idservice;

/**
 * @author smorrissey
 *
 */
public interface IdServiceFactory {
	
	IdService createService() throws Exception;
}

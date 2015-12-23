/**
 * 
 */
package info.rmapproject.core.idservice;

/**
 * @author  khanson, smorrissey
 *
 */
public interface IdServiceFactory {
	
	public IdService createService() throws Exception;
}

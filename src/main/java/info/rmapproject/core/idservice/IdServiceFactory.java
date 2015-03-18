/**
 * 
 */
package info.rmapproject.core.idservice;

/**
 * @author  khansen, smorrissey
 *
 */
public interface IdServiceFactory {
	
	public IdService createService() throws Exception;
}

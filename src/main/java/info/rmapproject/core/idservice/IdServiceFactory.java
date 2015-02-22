/**
 * 
 */
package info.rmapproject.core.idservice;

/**
 * @author  khansen, smorrissey
 *
 */
public interface IdServiceFactory {
	
	IdService createService() throws Exception;
}

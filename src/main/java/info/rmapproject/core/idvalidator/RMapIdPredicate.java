/**
 * 
 */
package info.rmapproject.core.idvalidator;

import java.net.URI;

import info.rmapproject.core.idservice.IdService;
import info.rmapproject.core.idservice.IdServiceFactoryIOC;
import info.rmapproject.core.model.RMapUri;

import org.apache.commons.collections4.Predicate;

/**
 * @author smorrissey
 * @param <T>
 *
 */
public class RMapIdPredicate<T> implements Predicate<T> {

	private static IdService idService = null;
	
	static{
		try {
			idService = IdServiceFactoryIOC.getFactory().createService();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 */
	public RMapIdPredicate() {
		super();
	}

	/**
	 * factory method
	 * @return
	 */
	public static <T> Predicate<T> rmapIdPredicate(){
		return new RMapIdPredicate<T>();
	}
	
	@Override
	public boolean evaluate(T object) {
		boolean isRMapId = false;
		URI uri = null;
		do {
			if (object==null){
				break;
			}
			if (object instanceof URI){
				uri = (URI)object;
			}
			else if (object instanceof RMapUri){
				uri = ((RMapUri)object).getIri();
			}
			if (uri != null){
				try {
					isRMapId = idService.isValidId(uri);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}while (false);
		return isRMapId;
	}

}

package info.rmapproject.core.rmapservice;

import info.rmapproject.core.utils.Constants;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This factory provides a way for non-Spring apps to initiate an instance of RMapService using 
 * the Spring bean settings so that internal autowiring works.
 * @author khanson
 *
 */
public class RMapServiceFactory {

	
	
	/** An instance of the RMapService object. */
	private static RMapService rmapService;
	
	/**
	 * Creates a new RMapService object.
	 *
	 * @return the RMapService instance
	 */
	public static RMapService createService() {
		try {
			if (rmapService == null){
				@SuppressWarnings("resource")
				ApplicationContext context = new ClassPathXmlApplicationContext(Constants.SPRING_CONFIG_FILEPATH);
				rmapService = (RMapService)context.getBean(Constants.RMAPSERVICE_BEANNAME, RMapService.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rmapService;
	}
	
}

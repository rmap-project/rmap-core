/**
 * 
 */
package info.rmapproject.core.model.request;




/**
 * @author khanson
 *
 */
public enum RMapStatusFilter {
	ACTIVE ("active"),
	INACTIVE ("inactive"),
	ALL("all");

	private String statusTerm= null ;

	RMapStatusFilter(String statusTerm){		
		this.statusTerm = statusTerm;
	}
	
	public String getStatusTerm()  {
		return this.statusTerm;
	}
	
	public static RMapStatusFilter getStatusFromTerm(String term){
		for (RMapStatusFilter stat: RMapStatusFilter.values()){
			String statTerm = stat.getStatusTerm();
			if (statTerm.equals(term.toLowerCase())){
				return stat;
			}
		}
		return null;
	}
	

}

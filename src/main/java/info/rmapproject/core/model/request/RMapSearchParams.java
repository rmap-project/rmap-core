/**
 * 
 */
package info.rmapproject.core.model.request;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.utils.ConfigUtils;
import info.rmapproject.core.utils.Constants;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Class defining filters to apply to an RMap search request, where results can potentially 
 * consist of >1 row.
 * @author khanson
 *
 */
public class RMapSearchParams  {

	/**
	 * Date range filter is applied to the start date of the Event associated with the object in the query
	 * For example, if you are getting a list of DiSCOs that reference a resource, the date will filter by 
	 * the start date of the event that created each DiSCO.
	 **/
	DateRange dateRange;
	
	/**
	 * Where searches involve returning DiSCO lists or the contents of DiSCOs, a status parameter 
	 * will filter according to the status of the DiSCO. For example, if you are retrieving a list 
	 * of triples that reference a resource, you can choose to only include triples from ACTIVE DiSCOs.
	 */
	RMapStatus status;

	/**
	 * Where searches involve returning DiSCO lists or the contents of DiSCOs, the SystemAgent list parameter 
	 * will filter according to the Agent that created the DiSCO. For example, if you are retrieving a list 
	 * of triples that reference a resource, you can choose to only include triples that belong to DiSCOs created
	 * by a specific set of System Agents.
	 */
	Set<URI> systemAgents;
	
	/**
	 * The limit determines how many results will be returned in the search.  Where no limit is set a default
	 * limit will be used retrieved from the config properties
	 */
	Integer limit;
	
	/** 
	 * The page number will determine how many pages into the result set will be returned.  
	 * If, for example, your result set has 500 records, and you have a limit of "200", 
	 * requesting page 2 will return records 201-400. Page 3 will return 401-500.
	 */
	Integer page;
	
	/** 
	 * Defines ORDER BY instructions.  The default can be set in the rmapcore properties file.
	 * For more information on the ORDER BY options, see the OrderBy class.
	 */
	OrderBy orderBy;
	
	public RMapSearchParams() {
	}

	public RMapSearchParams(String from, String until, String status, String systemAgentsCsv, String limit, String page) 
			throws RMapDefectiveArgumentException {
		setDateRange(new DateRange(from, until));
		setStatusCode(status); 
		setSystemAgents(systemAgentsCsv);	
		setLimit(limit);
		setPage(page);
		}
	
	/** 
	 * Retrieve the limit. If none has been configured, this will return the default limit.
	 * @return
	 */
	public Integer getLimit() {		
		if (this.limit==null){
			return getDefaultLimit();
		}
		else {
			return this.limit;
		}
	}

	public void setLimit(Integer limit) throws RMapException{
		if (limit==null) {
			this.limit=null;
		} else if (limit > 0) {
			Integer maxlimit=getMaxQueryLimit();
			if (limit>maxlimit){
				throw new RMapException("The maximum results that can be returned in one query is " 
											+ maxlimit.toString() + ". Please adjust your parameters");
			}
			this.limit = limit;
		} else {
			throw new RMapException("The limit must be an integer greater than 0");
		}
	}
	
	public void setLimit(String limit) throws RMapException {
		Integer iLimit = null;
		if (limit!=null){
			try{
				limit=limit.trim();
				iLimit = Integer.parseInt(limit);
			}
			catch (NumberFormatException ex) {
				throw new RMapException ("The limit provided is not a valid integer.", ex);
			}
		}
		setLimit(iLimit);
	}	

	public void setPage(Integer page) throws RMapException {
		if (page==null) {
			this.page=null;
		} else if (page > 0) {
			this.page = page;
		} else {
			throw new RMapException ("Page number must be greater than 0");
		}
	}
	
	public void setPage(String sPage) throws RMapDefectiveArgumentException {
		Integer iPage = null;
		if (sPage != null && sPage.length()>0) {
			try{
				sPage=sPage.trim();
				iPage = Integer.parseInt(sPage);
			}
			catch (Exception ex) {
				throw new RMapDefectiveArgumentException ("The page number provided is not a valid integer.", ex);
			}
		}
		this.setPage(iPage);
	}

	public Integer getPage() {
		if (this.page!=null && this.page>0){
			return this.page;			
		}
		else {
			return 1;
		}
	}



	/**
	 * Calculates the offset based on the limit and page number
	 * @return
	 */
	public Integer getOffset() {
		//page=1, offset=0
		//page=2, offset=limit
		//page=3, offset=limit*2
		Integer page = getPage();
		Integer limit = getLimit();
		if (page!=null && page>0){
			return (page-1)*limit;
		}
		else {
			return 0;
		}
	}

	public DateRange getDateRange() {
		return dateRange;
	}

	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}
	
	public void setDateRange(Date from, Date until) {
		this.dateRange = new DateRange(from, until);
	}	

	public RMapStatus getStatusCode() throws RMapException {
		if (this.status!=null){
			return this.status;
		}
		else {
			return getDefaultStatusCode();
		}
	}

	public void setStatusCode(RMapStatus status) {
		this.status = status;
	}

	public void setStatusCode(String sStatus) {
		RMapStatus status = null;
		if (sStatus!=null){
			status = RMapStatus.getStatusFromTerm(sStatus);
		}
		this.setStatusCode(status);		
	}
	
	public Set<URI> getSystemAgents() {
		return systemAgents;
	}

	public void setSystemAgents(Set<URI> systemAgents) {
		this.systemAgents = systemAgents;
	}
	
	public void addSystemAgent(URI agentUri){
		if (agentUri!=null) {
			systemAgents.add(agentUri);
		}
	}

	/**
	 * Converts a CSV passed through the URI as an encoded parameter into a URI list
	 * e.g. systemAgent list as string to List<URI>
	 * @param agentUri CSV
	 * @return
	 * @throws RMapApiException
	 */
	public void setSystemAgents(String systemAgentsCsv) throws RMapDefectiveArgumentException {
		//if empty return null - null is acceptable value for this optional param
		if(systemAgentsCsv == null || systemAgentsCsv.length()==0) {
			this.systemAgents=null;
			}
		else {
			//split string by commas
			String[] agentList = systemAgentsCsv.split(",");
			Set<URI> uriList = new HashSet<URI>(); 
			
			try {
				//convert to URI list
				for (String sAgent:agentList) {
					sAgent = sAgent.trim();
					if (sAgent.length()>0){
						URI uriAgent = new URI(sAgent);
						uriList.add(uriAgent);
					}
				}
				this.systemAgents=uriList;
			}
			catch (Exception ex) {
				throw new RMapDefectiveArgumentException ("One of the system agent filter parameters will not convert to a URI", ex);
			}
		}
	}

	public void setOrderBy(OrderBy orderBy) {
		this.orderBy = orderBy;		
	}
	
	public OrderBy getOrderBy() throws RMapException {
		if (this.orderBy!=null){
			return this.orderBy;			
		}
		else {
			return getDefaultOrderBy();
		}
	}
	
	public Integer getDefaultLimit() throws RMapException{
		Integer limit= null;
		try{
			String sLimit= ConfigUtils.getPropertyValue(Constants.RMAPCORE_PROPFILE, Constants.DEFAULT_QUERY_LIMIT_KEY);
			sLimit=sLimit.trim();
			limit = Integer.parseInt(sLimit);
		}
		catch (Exception ex) {
			throw new RMapException ("The default limit property in not configured correctly.", ex);
		}
		return limit;	
	}

	public RMapStatus getDefaultStatusCode() throws RMapException {
		try {
			String defaultStatus = ConfigUtils.getPropertyValue(Constants.RMAPCORE_PROPFILE, Constants.DEFAULT_STATUS_FILTER_KEY);
			if (defaultStatus==null){
				throw new RMapException("Default Status Code property is incorrectly configured");			
			}		
			return RMapStatus.getStatusFromTerm(defaultStatus);		
		} catch (Exception ex){
			throw new RMapException("Default Status Code property is incorrectly configured", ex);
		}
	}
	
	public OrderBy getDefaultOrderBy() throws RMapException {
		try {
			String defaultOrderBy = ConfigUtils.getPropertyValue(Constants.RMAPCORE_PROPFILE, Constants.DEFAULT_ORDERBY_FILTER_KEY);
			if (defaultOrderBy == null){
				throw new RMapException("Default OrderBy property is incorrectly configured");
			}
			return OrderBy.getOrderByFromProperty(defaultOrderBy);		
		} catch (Exception ex){
			throw new RMapException("Default OrderBy property is incorrectly configured", ex);
		}
	}
	
	public Integer getMaxQueryLimit() throws RMapException {
		Integer maxLimit= null;
		try{
			String sLimit= ConfigUtils.getPropertyValue(Constants.RMAPCORE_PROPFILE, Constants.MAX_QUERY_LIMIT_KEY);
			sLimit=sLimit.trim();
			maxLimit = Integer.parseInt(sLimit);
		}
		catch (Exception ex) {
			throw new RMapException ("The maximum query limit property in not configured correctly.", ex);
		}
		return maxLimit;	
	}
		
}

package info.rmapproject.core.model.request;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.utils.DateUtils;

import java.text.ParseException;
import java.util.Date;

/** 
 * Class to hold and control date range to be applied to RMap searches
 * Receives dates as either Date format or in text string format from APIs and converts
 * to UTC ISO standard date.  All requests are assumed to be UTC time zone
 * @author khanson
 *
 */
public class DateRange {

	private Date dateFrom;
	private Date dateUntil;
		
	public DateRange(){}
	
	public DateRange(Date from, Date until){
		this.dateFrom = from;
		this.dateUntil = until;
	}
	
	public DateRange(String from, String until) throws RMapDefectiveArgumentException{
		this.dateFrom = convertStrDateToDate(from, true);
		this.dateUntil = convertStrDateToDate(until, false);
	}
	
	/**
	 * Converts a date passed as yyyyMMddhhmmss as a string into a java Date. e.g. 20160115180000 -> 2016-01-15 6:00:00PM as date
	 * Supports either date only or datetime
	 * @param sDate
	 * @return
	 * @throws RMapException
	 */
	private Date convertStrDateToDate(String sDate, boolean isFromDate) throws RMapDefectiveArgumentException {
		//if empty return null - null is acceptable value for this optional param
		if(sDate == null || sDate.length()==0) {return null;}
		
		Date dDate = null;
	
		sDate = sDate.trim();
				
		// date can be yyyyMMdd or yyyyMMddhhmmss
		if (sDate.length()== 8) { //it's a date! 
			sDate = sDate.substring(0,4) + "-" + sDate.substring(4,6) + "-" + sDate.substring(6) ;
			if (isFromDate){
				sDate = sDate + "T00:00:00.000Z";
			}
			else {
				sDate = sDate + "T23:59:59.999Z";
			}
		}
		else if (sDate.length()== 14) { //it's a date and time! 
			sDate = sDate.substring(0,4) 
					+ "-" + sDate.substring(4,6) 
					+ "-" + sDate.substring(6,8) 
					+ "T" + sDate.substring(8,10) 
					+ ":" + sDate.substring(10,12) 
					+ ":" + sDate.substring(12,14);
			if (isFromDate){
				sDate = sDate + ".000Z";
			}
			else {
				sDate = sDate + ".999Z";
			}
		}
		else {
			throw new RMapDefectiveArgumentException("Invalid date provided.  Date must be in the format yyyyMMdd or yyyyMMddhhmmss");
		}

		try {	
			dDate = DateUtils.getDateFromIsoString(sDate);
		} catch (ParseException ex) {
			throw new RMapDefectiveArgumentException("Invalid date provided.  Date must be in the format yyyyMMdd or yyyyMMddhhmmss",ex);			
		}
		
		return dDate;
	}
	
	public Date getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}
	public Date getDateUntil() {
		return dateUntil;
	}
	public void setDateUntil(Date dateUntil) {
		this.dateUntil = dateUntil;
	}
	
	public String getUTCDateFrom(){
		String utcdate = null;
		if (this.dateFrom!=null){
			utcdate = DateUtils.getIsoStringDate(this.dateFrom);
		}
		return utcdate;
	}
	
	public String getUTCDateUntil(){
		String utcdate = null;
		if (this.dateUntil!=null){
			utcdate = DateUtils.getIsoStringDate(this.dateUntil);
		}
		return utcdate;
	}
		
}

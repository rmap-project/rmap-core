/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.model.RMapObjectType;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventType;
import info.rmapproject.core.model.request.RMapRequestAgent;
import info.rmapproject.core.utils.DateUtils;
import info.rmapproject.core.vocabulary.impl.openrdf.PROV;
import info.rmapproject.core.vocabulary.impl.openrdf.RMAP;

import java.net.URI;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.model.IRI;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.DC;

/**
 * @author khanson, smorrissey
 *
 */
public abstract class ORMapEvent extends ORMapObject implements RMapEvent {
	protected Statement eventTypeStmt;  // will be set by constructor of concrete Event class
	protected Statement eventTargetTypeStmt;
	protected Statement associatedAgentStmt; // must be non-null and set by constructor
	protected Statement descriptionStmt;
	protected Statement startTimeStmt;  // set by constructor
	protected Statement endTimeStmt;
	protected Statement associatedKeyStmt; // set by constructor
   
	/**
	 *Most likely use is to construct Event for read() method in RMapService from statements
	 * in Triplestore
	 * @param eventTypeStmt
	 * @param eventTargetTypeStmt
	 * @param associatedAgentStmt
	 * @param descriptionStmt
	 * @param startTimeStmt
	 * @param endTimeStmt
	 * @param context
	 * @param typeStatement
	 * @throws Exception 
	 * @throws RMapDefectiveArgumentException 
	 */
	protected  ORMapEvent(Statement eventTypeStmt, Statement eventTargetTypeStmt, 
			Statement associatedAgentStmt,  Statement descriptionStmt, 
			Statement startTimeStmt,  Statement endTimeStmt, IRI context, 
			Statement typeStatement, Statement associatedKeyStmt) throws RMapException {
		super();
		if (context != null){  //set it as the ID... this also sets the context
			setId(context);
		}
		else {
			setId();
		}
		this.eventTypeStmt = eventTypeStmt;
		this.eventTargetTypeStmt = eventTargetTypeStmt;
		this.associatedAgentStmt = associatedAgentStmt;
		this.descriptionStmt = descriptionStmt;
		this.startTimeStmt = startTimeStmt;
		this.endTimeStmt = endTimeStmt;
		this.associatedKeyStmt = associatedKeyStmt;
		setTypeStatement(RMapObjectType.EVENT);
	}
	/**
	 * Constructor sets the start time
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	protected ORMapEvent() throws RMapException {
		super();
		this.setId();	
		Date date = new Date();
		Literal dateLiteral = typeAdapter.getValueFactory().createLiteral(date);
		Statement startTime = typeAdapter.getValueFactory().createStatement(this.id, PROV.STARTEDATTIME, 
				dateLiteral, this.context);
		this.startTimeStmt = startTime;
		setTypeStatement(RMapObjectType.EVENT);
	}
	
	/**
	 * 
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	protected ORMapEvent(RMapRequestAgent associatedAgent, RMapEventTargetType targetType) throws RMapException {
		this();
		if (associatedAgent==null){
			throw new RMapException("Null agent not allowed in RMapEvent");
		}
		if (targetType==null){
			throw new RMapException("Null target type not allowed in RMapEvent");
		}
		URI systemAgentUri = associatedAgent.getSystemAgent();
		if (systemAgentUri==null){
			throw new RMapException("Null agent not allowed in RMapEvent");
		}		
		this.setAssociatedAgentStatement(typeAdapter.uri2OpenRdfIri(systemAgentUri));
		
		URI agentKeyUri = associatedAgent.getAgentKeyId();
		if (agentKeyUri!=null){
			this.setAssociatedKeyStatement(typeAdapter.uri2OpenRdfIri(agentKeyUri));
		}
		this.setEventTargetTypeStatement(targetType);	
	}
	
	/**
	 * 
	 * @param associatedAgent
	 * @param targetType
	 * @param desc
	 * @throws RMapException
	 */
	protected ORMapEvent(RMapRequestAgent associatedAgent, RMapEventTargetType targetType, RMapValue desc) 
			throws RMapException, RMapDefectiveArgumentException {
		this(associatedAgent, targetType);
		if (desc != null){
			Statement descSt = typeAdapter.getValueFactory().createStatement(this.context, 
					DC.DESCRIPTION, typeAdapter.rMapValue2OpenRdfValue(desc), this.context);
			this.descriptionStmt = descSt;
		}
	}	
	
	/**
	 * 
	 * @param eventType
	 * @return
	 * @throws RMapException
	 */
	protected void setEventTypeStatement (RMapEventType eventType) 
			throws RMapException{
		if (eventType==null){
			throw new RMapException("The event type statement could not be created because a valid type was not provided");
		}
		if (this.id == null || this.context==null){
			throw new RMapException("The object ID and context value must be set before creating an event type statement");
		}
		try {
			IRI eventtypeIri = typeAdapter.rMapIri2OpenRdfIri(eventType.getPath());
			Statement stmt = typeAdapter.getValueFactory().createStatement(this.context, RMAP.EVENTTYPE, eventtypeIri, this.context);
			this.eventTypeStmt = stmt;
		} catch (RMapDefectiveArgumentException e) {
			throw new RMapException("Invalid path for the object type provided.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#getEventType()
	 */
	public RMapEventType getEventType() throws RMapException {
		String etype = this.eventTypeStmt.getObject().stringValue();
		RMapEventType eventType = RMapEventType.getEventType(etype);
		if (eventType==null){
			throw new RMapException("Event has an invalid Event Type: " + etype);			
		} else {
			return eventType;
		}
	}
	/**
	 * 
	 * @return
	 */
	public Statement getEventTypeStmt() {
		return this.eventTypeStmt;
	}
	

	protected void setEventTargetTypeStatement (RMapEventTargetType eventTargetType) 
			throws RMapException{
		if (eventTargetType==null){
			throw new RMapException("The event target type statement could not be created because a valid type was not provided");
		}
		if (this.id == null || this.context==null){
			throw new RMapException("The object ID and context value must be set before creating an event target type statement");
		}
		try {
			IRI eventTTIri = typeAdapter.rMapIri2OpenRdfIri(eventTargetType.getPath());
			Statement stmt = typeAdapter.getValueFactory().createStatement(this.context, RMAP.TARGETTYPE, eventTTIri, this.context);
			this.eventTargetTypeStmt = stmt;
		} catch (RMapDefectiveArgumentException e) {
			throw new RMapException("Invalid path for the object type provided.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#getEventTargetType()
	 */
	public RMapEventTargetType getEventTargetType() throws RMapException {
		String tt = this.eventTargetTypeStmt.getObject().stringValue();
		RMapEventTargetType eventTargetType = RMapEventTargetType.getEventTargetType(tt);
		if (eventTargetType==null){
			throw new RMapException("Event has an invalid Event TargetType: " + tt);			
		} else {
			return eventTargetType;
		}
	}
	/**
	 * 
	 * @return
	 */
	public Statement getEventTargetTypeStmt(){
		return this.eventTargetTypeStmt;
	}

	
	protected void setAssociatedAgentStatement (IRI associatedAgent) 
			throws RMapException{
		if (associatedAgent==null){
			throw new RMapException("The associated agent statement could not be created because a valid agent was not provided");
		}
		if (this.id == null || this.context==null){
			throw new RMapException("The object ID and context value must be set before creating an associated agent statement");
		}
		Statement agent = typeAdapter.getValueFactory().createStatement(this.context, PROV.WASASSOCIATEDWITH, 
								associatedAgent, this.context);
		this.associatedAgentStmt=agent;
	}
		
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#getAssociatedAgent()
	 */
	public RMapIri getAssociatedAgent() throws RMapException{
		RMapIri rUri = null;
		IRI agentURI = (IRI)this.associatedAgentStmt.getObject();
		rUri = typeAdapter.openRdfIri2RMapIri(agentURI);
		return rUri;
	}
		
	/**
	 * 
	 * @return
	 */
	public Statement getAssociatedAgentStmt() {
		return this.associatedAgentStmt;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#getDescription()
	 */
	public RMapValue getDescription() throws RMapException {
		RMapValue rResource= null;
		if (this.descriptionStmt!= null){
			Value value = this.descriptionStmt.getObject();
			try {
				rResource = typeAdapter.openRdfValue2RMapValue(value);
			}
			catch(RMapDefectiveArgumentException e) {
				throw new RMapException(e);
			}
		}
		return rResource;
	}
	/**
	 * 
	 * @return
	 */
	public Statement getDescriptionStmt() {
		return this.descriptionStmt;
	}
	
	protected void setAssociatedKeyStatement (IRI associatedKey) 
			throws RMapException{
		if (this.id == null || this.context==null){
			throw new RMapException("The object ID and context value must be set before creating an associated key statement");
		}
		if (associatedKey!=null){
			Statement keystmt = typeAdapter.getValueFactory().createStatement(this.id, PROV.USED, 
										associatedKey, this.context);
			this.associatedKeyStmt=keystmt;
		}			
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#getAssociatedKey()
	 */
	public RMapIri getAssociatedKey() throws RMapException {
		RMapIri rUri = null;
		if (this.associatedKeyStmt!= null){
			IRI keyUri = (IRI)this.associatedKeyStmt.getObject();
			rUri = typeAdapter.openRdfIri2RMapIri(keyUri);
		}
		return rUri;
	}
	/**
	 * 
	 * @return
	 */
	public Statement getAssociatedKeyStmt() {
		return this.descriptionStmt;
	}
	

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#getStartTime()
	 */
	public Date getStartTime() throws RMapException {
		Date finalResult = null;
		try {
			Literal timeStr = (Literal)this.startTimeStmt.getObject();
			XMLGregorianCalendar startTime =  timeStr.calendarValue();
			finalResult = DateUtils.xmlGregorianCalendarToDate(startTime);
			//finalResult =  DateUtils.getDateFromIsoString(timeStr);
		} catch (Exception e){
			throw new RMapException (e);
		}		
		return finalResult;
	}
	/**
	 * 
	 * @return
	 */
	public Statement getStartTimeStmt(){
		return this.startTimeStmt;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#getEndTime()
	 */
	public Date getEndTime() throws RMapException {
		Date finalResult = null;
		try {
			Literal timeStr = (Literal)this.endTimeStmt.getObject();
			XMLGregorianCalendar endTime =  timeStr.calendarValue();
			finalResult = DateUtils.xmlGregorianCalendarToDate(endTime);
			//finalResult = DateUtils.getDateFromIsoString(timeStr);
		} catch (Exception e){
			throw new RMapException (e);
		}		
		return finalResult;
	}
	/**
	 * 
	 * @return
	 */
	public Statement getEndTimeStmt(){
		return this.endTimeStmt;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#setEndTime(java.util.Date)
	 */
	public void setEndTime(Date endTime) throws RMapException {
		Literal dateLiteral = typeAdapter.getValueFactory().createLiteral(endTime);
		Statement endTimeStmt = typeAdapter.getValueFactory().createStatement(this.context, PROV.ENDEDATTIME, 
				dateLiteral, this.context);
		this.endTimeStmt = endTimeStmt;
	}
	

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#setDescription(RMapUri)
	 */
	public void setDescription(RMapValue description) 
			throws RMapException, RMapDefectiveArgumentException {
		if (description != null){
			Statement descSt = typeAdapter.getValueFactory().createStatement(this.context, 
					DC.DESCRIPTION, typeAdapter.rMapValue2OpenRdfValue(description), this.context);
			this.descriptionStmt = descSt;
		}
	}
	
	
	/**
	 * @return the context
	 */
	public IRI getContext() {
		return context;
	}
		
	@Override
	public Model getAsModel() throws RMapException {
		Model eventModel = new LinkedHashModel();
		eventModel.add(typeStatement);
		eventModel.add(associatedAgentStmt);
		eventModel.add(eventTypeStmt);
		eventModel.add(eventTargetTypeStmt);
		eventModel.add(startTimeStmt);
		if (endTimeStmt != null){
			eventModel.add(endTimeStmt);
		}
		if (descriptionStmt != null){
			eventModel.add(descriptionStmt);
		}
		if (associatedKeyStmt != null){
			eventModel.add(associatedKeyStmt);
		}
		return eventModel;
	}
}

/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.RDF;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventType;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;
import info.rmapproject.core.utils.DateUtils;

/**
 * @author khansen, smorrissey
 *
 */
public abstract class ORMapEvent extends ORMapObject implements RMapEvent {
	protected Statement eventTypeStmt;  // will be set by constructor of concrete Event class
	protected Statement eventTargetTypeStmt;
	protected Statement associatedAgentStmt; // must be non-null and set by constructor
	protected Statement descriptionStmt;
	protected Statement startTimeStmt;  // set by constructor
	protected Statement endTimeStmt;
	protected URI context;
   
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
	 * @throws RMapException
	 */
	protected  ORMapEvent(Statement eventTypeStmt, Statement eventTargetTypeStmt, 
			Statement associatedAgentStmt,  Statement descriptionStmt, 
			Statement startTimeStmt,  Statement endTimeStmt, URI context, 
			Statement typeStatement) throws RMapException {
		super();
		if (context != null){
			this.context = context;
			this.id = ORAdapter.openRdfUri2URI(context);
		}
		else {
			this.context = ORAdapter.uri2OpenRdfUri(this.getId());
		}
		this.id = ORAdapter.openRdfUri2URI(this.context);
		this.eventTypeStmt = eventTypeStmt;
		this.eventTargetTypeStmt = eventTargetTypeStmt;
		this.associatedAgentStmt = associatedAgentStmt;
		this.descriptionStmt = descriptionStmt;
		this.startTimeStmt = startTimeStmt;
		this.endTimeStmt = endTimeStmt;
		this.typeStatement = 
				this.getValueFactory().createStatement(this.context,RDF.TYPE,RMAP.EVENT,this.context);
			
	}
	/**
	 * Constructor sets the start time
	 * @throws RMapException
	 */
	protected ORMapEvent() throws RMapException {
		super();
		this.context = ORAdapter.uri2OpenRdfUri(this.getId());
		Date date = new Date();
		String dateString = DateUtils.getIsoStringDate(date);
		Literal dateLiteral = this.getValueFactory().createLiteral(dateString);
		Statement startTime = this.getValueFactory().createStatement(this.context, PROV.STARTEDATTIME, 
				dateLiteral, this.context);
		this.startTimeStmt = startTime;
		this.typeStatement = 
				this.getValueFactory().createStatement(this.context,RDF.TYPE,RMAP.EVENT,this.context);
	}
	/**
	 * 
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	protected ORMapEvent(RMapUri associatedAgent, RMapEventTargetType targetType) throws RMapException {
		this();
		if (associatedAgent==null){
			throw new RMapException("Null associatedAgent not allowed in RMapEvent");
		}
		if (targetType==null){
			throw new RMapException("Null target type not allowed in RMapEvent");
		}
		Statement agent = this.getValueFactory().createStatement(this.context, PROV.WASASSOCIATEDWITH, 
				ORAdapter.rMapUri2OpenRdfUri(associatedAgent), this.context);
		this.associatedAgentStmt=agent;
		Statement tt = this.getValueFactory().createStatement(this.context, RMAP.EVENT_TARGET_TYPE, 
				this.getValueFactory().createLiteral(targetType.uriString()),
				this.context);
		this.eventTargetTypeStmt = tt;
	}
	
	/**
	 * 
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	protected ORMapEvent(URI associatedAgent, RMapEventTargetType targetType) throws RMapException {
		this();
		if (associatedAgent==null){
			throw new RMapException("Null associatedAgent not allowed in RMapEvent");
		}
		if (targetType==null){
			throw new RMapException("Null target type not allowed in RMapEvent");
		}
		Statement agent = this.getValueFactory().createStatement(this.context, PROV.WASASSOCIATEDWITH, 
				associatedAgent, this.context);
		this.associatedAgentStmt=agent;
		Statement tt = this.getValueFactory().createStatement(this.context, RMAP.EVENT_TARGET_TYPE, 
				this.getValueFactory().createLiteral(targetType.uriString()),
				this.context);
		this.eventTargetTypeStmt = tt;
	}
	
	/**
	 * 
	 * @param associatedAgent
	 * @param targetType
	 * @param desc
	 * @throws RMapException
	 */
	protected ORMapEvent(RMapUri associatedAgent, RMapEventTargetType targetType, RMapValue desc) 
			throws RMapException {
		this(associatedAgent, targetType);
		if (desc != null){
			Statement descSt = this.getValueFactory().createStatement(this.context, 
					DC.DESCRIPTION,
					ORAdapter.rMapValue2OpenRdfValue(desc), this.context);
			this.descriptionStmt = descSt;
		}
	}	

	/**
	 * 
	 * @param eventType
	 * @return
	 * @throws RMapException
	 */
	protected Statement makeEventTypeStatement (RMapEventType eventType) 
			throws RMapException{
		Statement et = null;
		et = this.getValueFactory().createStatement(context, RMAP.EVENT_TYPE, 
				this.getValueFactory().createLiteral(eventType.getTypeString()),context);
		return et;
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#getEventType()
	 */
	public RMapEventType getEventType() throws RMapException {
		String et = this.eventTypeStmt.getObject().stringValue();
		return RMapEventType.getEventTypeFromString(et);
	}
	/**
	 * 
	 * @return
	 */
	public Statement getEventTypeStmt() {
		return this.eventTypeStmt;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#getEventTargetType()
	 */
	public RMapEventTargetType getEventTargetType() throws RMapException {
		String tt = this.eventTargetTypeStmt.getObject().stringValue();
		return RMapEventTargetType.getTargetTypeFromString(tt);
	}
	/**
	 * 
	 * @return
	 */
	public Statement getEventTargetTypeStmt(){
		return this.eventTargetTypeStmt;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#getAssociatedAgent()
	 */
	public RMapUri getAssociatedAgent()  throws RMapException{
		RMapUri rUri = null;
		rUri = (RMapUri) this.associatedAgentStmt.getObject();
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
					rResource = ORAdapter.openRdfValue2RMapValue(value);
				} catch (IllegalArgumentException | URISyntaxException e) {
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

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#getStartTime()
	 */
	public Date getStartTime() throws RMapException {
		Date finalResult = null;
		String timeStr = this.startTimeStmt.getObject().stringValue();
		try {
			finalResult =  DateUtils.getDateFromIsoString(timeStr);
		} catch (ParseException e){
			throw new RMapException (e);
		}		
		return finalResult;
	}
	/**
	 * 
	 * @return
	 */
	public Statement getStartTypeStmt(){
		return this.startTimeStmt;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#getEndTime()
	 */
	public Date getEndTime() throws RMapException {
		Date finalResult = null;
		String timeStr = this.endTimeStmt.getObject().stringValue();
		try {
			finalResult = DateUtils.getDateFromIsoString(timeStr);
		} catch (ParseException e){
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
		String dateString = null;	
		try {
			dateString = DateUtils.getIsoStringDate(endTime);
		}
		catch (Exception e){
			throw new RMapException(e);
		}
		Literal dateLiteral = this.getValueFactory().createLiteral(dateString);
		Statement endTimeStmt = this.getValueFactory().createStatement(this.context, PROV.ENDEDATTIME, 
				dateLiteral, this.context);
		this.endTimeStmt = endTimeStmt;
	}
	/**
	 * @return the context
	 */
	public URI getContext() {
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
		return eventModel;
	}
}

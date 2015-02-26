/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.text.ParseException;
import java.util.Date;

import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.RDF;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapEvent;
import info.rmapproject.core.model.RMapEventTargetType;
import info.rmapproject.core.model.RMapEventType;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;
import info.rmapproject.core.utils.DateUtils;

/**
 * @author khansen, smorrissey
 *
 */
public abstract class ORMapEvent extends ORMapObject implements RMapEvent {
	protected ORMapStatement eventTypeStmt;  // will be set by constructor of concrete Event class
	protected ORMapStatement eventTargetTypeStmt;
	protected ORMapStatement associatedAgentStmt; // must be non-null and set by constructor
	protected ORMapStatement descriptionStmt;
	protected ORMapStatement startTimeStmt;  // set by constructor
	protected ORMapStatement endTimeStmt;
	protected URI context;
	protected ORMapStatement typeStatement;
   
	protected  ORMapEvent(ORMapStatement eventTypeStmt, ORMapStatement eventTargetTypeStmt, 
			ORMapStatement associatedAgentStmt,  ORMapStatement descriptionStmt, 
			ORMapStatement startTimeStmt,  ORMapStatement endTimeStmt, URI context, 
			ORMapStatement typeStatement) throws RMapException {
		super();
		this.context = context;
		this.id = ORAdapter.openRdfUri2URI(this.context);
		this.eventTypeStmt = eventTypeStmt;
		this.eventTargetTypeStmt = eventTargetTypeStmt;
		this.associatedAgentStmt = associatedAgentStmt;
		this.descriptionStmt = descriptionStmt;
		this.startTimeStmt = startTimeStmt;
		this.endTimeStmt = endTimeStmt;
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.CREATION);	
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
		ORMapStatement startTime = new ORMapStatement(this.context, PROV.STARTEDATTIME, 
				dateString, this.context);
		this.startTimeStmt = startTime;
		this.typeStatement = 
				new ORMapStatement(this.context,RDF.TYPE,RMAP.EVENT,this.context);
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
		ORMapStatement agent = new ORMapStatement(this.context, PROV.WASASSOCIATEDWITH, 
				ORAdapter.rMapUri2OpenRdfUri(associatedAgent), this.context);
		this.associatedAgentStmt=agent;
		ORMapStatement tt = new ORMapStatement(this.context, RMAP.EVENT_TARGET_TYPE, targetType.uriString(),
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
		ORMapStatement agent = new ORMapStatement(this.context, PROV.WASASSOCIATEDWITH, 
				associatedAgent, this.context);
		this.associatedAgentStmt=agent;
		ORMapStatement tt = new ORMapStatement(this.context, RMAP.EVENT_TARGET_TYPE, targetType.uriString(),
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
			ORMapStatement descSt = new ORMapStatement(this.context, DC.DESCRIPTION,
					ORAdapter.rMapResource2OpenRdfValue(desc), this.context);
			this.descriptionStmt = descSt;
		}
	}	

	/**
	 * 
	 * @param eventType
	 * @return
	 * @throws RMapException
	 */
	protected ORMapStatement makeEventTypeStatement (RMapEventType eventType) 
			throws RMapException{
		ORMapStatement et = null;
		et = new ORMapStatement(context, RMAP.EVENT_TYPE, 
				eventType.getTypeString(),context);
		return et;
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#getEventType()
	 */
	public RMapEventType getEventType() throws RMapException {
		String et = this.eventTypeStmt.getRmapStmtObject().stringValue();
		return RMapEventType.getEventTypeFromString(et);
	}
	/**
	 * 
	 * @return
	 */
	public ORMapStatement getEventTypeStmt() {
		return this.eventTypeStmt;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#getEventTargetType()
	 */
	public RMapEventTargetType getEventTargetType() throws RMapException {
		String tt = this.eventTargetTypeStmt.getRmapStmtObject().stringValue();
		return RMapEventTargetType.getTargetTypeFromString(tt);
	}
	/**
	 * 
	 * @return
	 */
	public ORMapStatement getEventTargetTypeStmt(){
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
	public ORMapStatement getAssociatedAgentStmt() {
		return this.associatedAgentStmt;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#getDescription()
	 */
	public RMapValue getDescription() throws RMapException {
		RMapValue rResource= null;
		if (this.descriptionStmt!= null){
				rResource = this.descriptionStmt.getObject();
		}
		return rResource;
	}
	/**
	 * 
	 * @return
	 */
	public ORMapStatement getDescriptionStmt() {
		return this.descriptionStmt;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#getStartTime()
	 */
	public Date getStartTime() throws RMapException {
		Date finalResult = null;
		String timeStr = this.startTimeStmt.getRmapStmtObject().stringValue();
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
	public ORMapStatement getStartTypeStmt(){
		return this.startTimeStmt;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEvent#getEndTime()
	 */
	public Date getEndTime() throws RMapException {
		Date finalResult = null;
		String timeStr = this.endTimeStmt.getRmapStmtObject().stringValue();
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
	public ORMapStatement getEndTimeStmt(){
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
		ORMapStatement endTimeStmt = new ORMapStatement(this.context, PROV.ENDEDATTIME, 
				dateString, this.context);
		this.endTimeStmt = endTimeStmt;
	}
	/**
	 * @return the typeStatement
	 */
	public ORMapStatement getTypeStatement() {
		return typeStatement;
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
		eventModel.add(typeStatement.rmapStmtStatement);
		eventModel.add(associatedAgentStmt.rmapStmtStatement);
		eventModel.add(eventTypeStmt.rmapStmtStatement);
		eventModel.add(eventTargetTypeStmt.rmapStmtStatement);
		eventModel.add(startTimeStmt.rmapStmtStatement);
		eventModel.add(endTimeStmt.rmapStmtStatement);
		if (descriptionStmt != null){
			eventModel.add(descriptionStmt.rmapStmtStatement);
		}
		return eventModel;
	}
}

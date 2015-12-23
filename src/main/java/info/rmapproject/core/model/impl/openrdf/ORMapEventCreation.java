/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.event.RMapEventCreation;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventType;

/**
 *  @author khansen, smorrissey
 *
 */
public class ORMapEventCreation extends ORMapEventWithNewObjects implements RMapEventCreation {

	/**
	 * @throws RMapException
	 */
	protected ORMapEventCreation() throws RMapException {
		super();
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.CREATION);
	}
	
	/**
	 * 
	 * @param eventTypeStmt
	 * @param eventTargetTypeStmt
	 * @param associatedAgentStmt
	 * @param descriptionStmt
	 * @param startTimeStmt
	 * @param endTimeStmt
	 * @param context
	 * @param typeStatement
	 * @param createdObjects
	 * @throws RMapException
	 */
	public ORMapEventCreation(Statement eventTypeStmt, Statement eventTargetTypeStmt, 
			Statement associatedAgentStmt,  Statement descriptionStmt, 
			Statement startTimeStmt,  Statement endTimeStmt, URI context, 
			Statement typeStatement, List<Statement> createdObjects) 
					throws RMapException {
		super(eventTypeStmt,eventTargetTypeStmt,associatedAgentStmt,descriptionStmt,
				startTimeStmt, endTimeStmt,context,typeStatement);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.CREATION);
		this.createdObjects = createdObjects;
	}
	
	/**
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public ORMapEventCreation(RMapUri associatedAgent,
			RMapEventTargetType targetType) throws RMapException, RMapDefectiveArgumentException {
		super(associatedAgent, targetType);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.CREATION);
	}
	
	/**
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	public ORMapEventCreation(URI associatedAgent,
			RMapEventTargetType targetType) throws RMapException {
		super(associatedAgent, targetType);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.CREATION);
	}


	/**
	 * @param associatedAgent
	 * @param targetType
	 * @param desc
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public ORMapEventCreation(RMapUri associatedAgent,
			RMapEventTargetType targetType, RMapValue desc)
			throws RMapException, RMapDefectiveArgumentException {
		super(associatedAgent, targetType, desc);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.CREATION);
	}
	/**
	 * 
	 * @param associatedAgent
	 * @param targetType
	 * @param desc
	 * @param createdObjIds
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public ORMapEventCreation(RMapUri associatedAgent,
			RMapEventTargetType targetType, RMapValue desc, List<RMapUri> createdObjIds)
		throws RMapException, RMapDefectiveArgumentException{
		this(associatedAgent, targetType, desc);
		this.setCreatedObjectIds(createdObjIds);	
	}


}

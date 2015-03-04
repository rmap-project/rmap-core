/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapEventCreation;
import info.rmapproject.core.model.RMapEventTargetType;
import info.rmapproject.core.model.RMapEventType;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.RMapUri;

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
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	public ORMapEventCreation(RMapUri associatedAgent,
			RMapEventTargetType targetType) throws RMapException {
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
	 * @param desc
	 * @throws RMapException
	 */
	public ORMapEventCreation(RMapUri associatedAgent,
			RMapEventTargetType targetType, RMapValue desc)
			throws RMapException {
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
	 */
	public ORMapEventCreation(RMapUri associatedAgent,
			RMapEventTargetType targetType, RMapValue desc, List<RMapUri> createdObjIds)
		throws RMapException{
		this(associatedAgent, targetType, desc);
		this.setCreatedObjectIds(createdObjIds);	
	}


}

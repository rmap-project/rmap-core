/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.event.RMapEventCreation;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventType;
import info.rmapproject.core.model.request.RMapRequestAgent;

import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.IRI;

/**
 *  @author khanson, smorrissey
 *
 */
public class ORMapEventCreation extends ORMapEventWithNewObjects implements RMapEventCreation {

	/**
	 * @throws RMapException
	 */
	protected ORMapEventCreation() throws RMapException {
		super();
		this.setEventTypeStatement(RMapEventType.CREATION);
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
			Statement startTimeStmt,  Statement endTimeStmt, IRI context, 
			Statement typeStatement, Statement associatedKeyStmt, List<Statement> createdObjects) 
					throws RMapException {
		super(eventTypeStmt,eventTargetTypeStmt,associatedAgentStmt,descriptionStmt,
				startTimeStmt, endTimeStmt,context,typeStatement, associatedKeyStmt);
		this.createdObjects = createdObjects;
	}
	
	/**
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public ORMapEventCreation(RMapRequestAgent associatedAgent, RMapEventTargetType targetType) 
			throws RMapException, RMapDefectiveArgumentException {
		super(associatedAgent, targetType);
		this.setEventTypeStatement(RMapEventType.CREATION);
	}
	

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @param desc
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public ORMapEventCreation(RMapRequestAgent associatedAgent, RMapEventTargetType targetType, RMapValue desc)
			throws RMapException, RMapDefectiveArgumentException {
		super(associatedAgent, targetType, desc);
		this.setEventTypeStatement(RMapEventType.CREATION);
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
	public ORMapEventCreation(RMapRequestAgent associatedAgent, RMapEventTargetType targetType, RMapValue desc, List<RMapIri> createdObjIds)
		throws RMapException, RMapDefectiveArgumentException{
		this(associatedAgent, targetType, desc);
		this.setEventTypeStatement(RMapEventType.CREATION);
		this.setCreatedObjectIds(createdObjIds);	
	}


}

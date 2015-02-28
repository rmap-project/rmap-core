/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapEventInactivation;
import info.rmapproject.core.model.RMapEventTargetType;
import info.rmapproject.core.model.RMapEventType;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

/**
 * @author smorrissey
 *
 */
public class ORMapEventInactivation extends ORMapEvent implements
		RMapEventInactivation {
	
	protected Statement inactivatedObjectStatement;
	
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
	 * @param inactivatedObjectStatement
	 * @throws RMapException
	 */
	public ORMapEventInactivation(Statement eventTypeStmt, Statement eventTargetTypeStmt, 
			Statement associatedAgentStmt,  Statement descriptionStmt, 
			Statement startTimeStmt,  Statement endTimeStmt, URI context, 
			Statement typeStatement, Statement inactivatedObjectStatement) 
	throws RMapException {
		super(eventTypeStmt,eventTargetTypeStmt,associatedAgentStmt,descriptionStmt,
				startTimeStmt, endTimeStmt,context,typeStatement);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.INACTIVATION);
		if (inactivatedObjectStatement==null){
			throw new RMapException("Null inactivated object statement");
		}
		this.inactivatedObjectStatement = inactivatedObjectStatement;
	}
	
	/**
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
	public ORMapEventInactivation(Statement eventTypeStmt,
			Statement eventTargetTypeStmt, Statement associatedAgentStmt,
			Statement descriptionStmt, Statement startTimeStmt,
			Statement endTimeStmt, URI context, Statement typeStatement)
			throws RMapException {
		super(eventTypeStmt, eventTargetTypeStmt, associatedAgentStmt,
				descriptionStmt, startTimeStmt, endTimeStmt, context,
				typeStatement);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.INACTIVATION);
	}

	/**
	 * @throws RMapException
	 */
	protected ORMapEventInactivation() throws RMapException {
		super();
		this.makeEventTypeStatement(RMapEventType.INACTIVATION);
	}

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	public ORMapEventInactivation(RMapUri associatedAgent,
			RMapEventTargetType targetType) throws RMapException {
		super(associatedAgent, targetType);
		this.makeEventTypeStatement(RMapEventType.INACTIVATION);
	}

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	public ORMapEventInactivation(URI associatedAgent,
			RMapEventTargetType targetType) throws RMapException {
		super(associatedAgent, targetType);
		this.makeEventTypeStatement(RMapEventType.INACTIVATION);
	}

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @param desc
	 * @throws RMapException
	 */
	public ORMapEventInactivation(RMapUri associatedAgent,
			RMapEventTargetType targetType, RMapValue desc)
			throws RMapException {
		super(associatedAgent, targetType, desc);
		this.makeEventTypeStatement(RMapEventType.INACTIVATION);
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventInactivation#getTargetObjectId()
	 */
	@Override
	public RMapUri getInactivatedObjectId() throws RMapException {
		RMapUri rid = null;
		if (this.inactivatedObjectStatement!= null){
			URI uri = (URI) this.inactivatedObjectStatement.getObject();
			rid = ORAdapter.openRdfUri2RMapUri(uri);
		}
		return rid;
	}
	/**
	 * 
	 * @param intactivatedObject
	 */
	protected void setInactivatedObjectStmt(URI intactivatedObject) {
		if (intactivatedObject != null){
			Statement stmt = this.getValueFactory().createStatement(this.context, 
					RMAP.EVENT_INACTIVATED_OBJECT,
					intactivatedObject, this.context);
			this.inactivatedObjectStatement = stmt;
		}
	}

	@Override
	public void setInactivatedObjectId(RMapUri uri) throws RMapException {
		URI inactiveUri = ORAdapter.rMapUri2OpenRdfUri(uri);
		this.setInactivatedObjectStmt(inactiveUri);
	}
	
	@Override
	public Model getAsModel() throws RMapException {
		Model model = super.getAsModel();
		if (inactivatedObjectStatement != null){
			model.add(inactivatedObjectStatement);
		}
		return model;
	}

	/**
	 * @return the inactivatedObjectStatement
	 */
	public Statement getInactivatedObjectStatement() {
		return inactivatedObjectStatement;
	}

}

/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.event.RMapEventInactivation;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventType;
import info.rmapproject.core.model.request.RMapRequestAgent;
import info.rmapproject.core.vocabulary.impl.openrdf.RMAP;

import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;

/**
 * @author smorrissey, khanson
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
			Statement startTimeStmt,  Statement endTimeStmt, IRI context, 
			Statement typeStatement, Statement associatedKeyStmt, Statement inactivatedObjectStatement) 
	throws RMapException {
		super(eventTypeStmt,eventTargetTypeStmt,associatedAgentStmt,descriptionStmt,
				startTimeStmt, endTimeStmt, context, typeStatement, associatedKeyStmt);
		if (inactivatedObjectStatement==null){
			throw new RMapException("Null inactivated object statement");
		}
		this.inactivatedObjectStatement = inactivatedObjectStatement;
	}


	/**
	 * @throws RMapException
	 */
	protected ORMapEventInactivation() throws RMapException {
		super();
		this.setEventTypeStatement(RMapEventType.INACTIVATION);
	}
	/**
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	public ORMapEventInactivation(RMapRequestAgent associatedAgent, RMapEventTargetType targetType) throws RMapException {
		super(associatedAgent, targetType);
		this.setEventTypeStatement(RMapEventType.INACTIVATION);
	}

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @param desc
	 * @throws RMapException
	 */
	public ORMapEventInactivation(RMapRequestAgent associatedAgent, RMapEventTargetType targetType, RMapValue desc)
			throws RMapException, RMapDefectiveArgumentException  {
		super(associatedAgent, targetType, desc);
		this.setEventTypeStatement(RMapEventType.INACTIVATION);
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventInactivation#getTargetObjectId()
	 */
	@Override
	public RMapIri getInactivatedObjectId() throws RMapException {
		RMapIri rid = null;
		if (this.inactivatedObjectStatement!= null){
			IRI iri = (IRI) this.inactivatedObjectStatement.getObject();
			rid = ORAdapter.openRdfIri2RMapIri(iri);
		}
		return rid;
	}
	/**
	 * 
	 * @param inactivatedObject
	 */
	protected void setInactivatedObjectStmt(IRI inactivatedObject) {
		if (inactivatedObject != null){
			Statement stmt = ORAdapter.getValueFactory().createStatement(this.context, 
					RMAP.INACTIVATEDOBJECT,
					inactivatedObject, this.context);
			this.inactivatedObjectStatement = stmt;
		}
	}

	@Override
	public void setInactivatedObjectId(RMapIri iri) throws RMapException, RMapDefectiveArgumentException {
		IRI inactiveIri = ORAdapter.rMapIri2OpenRdfIri(iri);
		this.setInactivatedObjectStmt(inactiveIri);
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

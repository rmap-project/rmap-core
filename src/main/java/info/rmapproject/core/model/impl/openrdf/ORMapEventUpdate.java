/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.util.List;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventType;
import info.rmapproject.core.model.event.RMapEventUpdate;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

/**
 * @author khansen, smorrissey
 *
 */
public class ORMapEventUpdate extends ORMapEventWithNewObjects implements RMapEventUpdate {
	protected Statement derivationStatement;
	protected Statement inactivatedObjectStatement;

	/**
	 * @throws RMapException
	 */
	protected ORMapEventUpdate() throws RMapException {
		super();
		this.makeEventTypeStatement(RMapEventType.UPDATE);
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
	 * @param derivationStatement
	 * @param inactivatedObjectStatement
	 * @throws RMapException
	 */
	public ORMapEventUpdate(Statement eventTypeStmt, Statement eventTargetTypeStmt, 
			Statement associatedAgentStmt,  Statement descriptionStmt, 
			Statement startTimeStmt,  Statement endTimeStmt, URI context, 
			Statement typeStatement, List<Statement> createdObjects,
			Statement derivationStatement, Statement inactivatedObjectStatement) 
	throws RMapException {
		super(eventTypeStmt,eventTargetTypeStmt,associatedAgentStmt,descriptionStmt,
				startTimeStmt, endTimeStmt,context,typeStatement);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.UPDATE);
		if (createdObjects==null || createdObjects.size()==0){
			throw new RMapException ("Null or empty list of created object in Update");
		}		
		if (derivationStatement==null){
			throw new RMapException("Null derived object");
		}
		if (inactivatedObjectStatement==null){
			throw new RMapException("Null inactivated object statement");
		}
		this.createdObjects = createdObjects;	
		this.derivationStatement = derivationStatement;
		this.inactivatedObjectStatement = inactivatedObjectStatement;
	}

	/**
	 * 
	 * @param associatedAgent
	 * @param targetType
	 * @param intactivatedObject
	 * @param derivedObject
	 * @throws RMapException
	 */
	public ORMapEventUpdate(URI associatedAgent,
			RMapEventTargetType targetType, URI intactivatedObject, URI derivedObject) 
	throws RMapException {
		super(associatedAgent, targetType);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.UPDATE);
		this.setInactivatedObjectStmt(intactivatedObject);
		this.setDerivationStmt(derivedObject);
	}
	/**
	 * 
	 * @param associatedAgent
	 * @param targetType
	 * @param intactivatedObject
	 * @param derivedObject
	 * @param createdObjects
	 * @throws RMapException
	 */
	public ORMapEventUpdate(URI associatedAgent,
			RMapEventTargetType targetType, URI intactivatedObject, URI derivedObject,
			List<Statement> createdObjects) 
	throws RMapException {
		this(associatedAgent, targetType, intactivatedObject, derivedObject);
		this.createdObjects = createdObjects;
	}

	/**
	 * 
	 * @param associatedAgent
	 * @param targetType
	 * @param inactivatedObject
	 * @param derivedObject
	 * @param createdObjects
	 * @param desc
	 * @throws RMapException
	 */
	public ORMapEventUpdate(RMapUri associatedAgent,
			RMapEventTargetType targetType, RMapUri inactivatedObject, RMapUri derivedObject,
			List<RMapUri> createdObjects, RMapValue desc)
			throws RMapException {
		super(associatedAgent, targetType, desc);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.UPDATE);
		this.setInactivatedObjectStmt(ORAdapter.rMapUri2OpenRdfUri(inactivatedObject));
		this.setDerivationStmt(ORAdapter.rMapUri2OpenRdfUri(derivedObject));
		this.setCreatedObjectIds(createdObjects);
		
	}
	
	@Override
	public Model getAsModel() throws RMapException {
		Model model = super.getAsModel();
		if (inactivatedObjectStatement != null){
			model.add(inactivatedObjectStatement);
		}
		if (derivationStatement != null){
			model.add(derivationStatement);
		}
		return model;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventUpdate#getInactivatedObjectId()
	 */
	public RMapUri getInactivatedObjectId() throws RMapException {
		RMapUri rid = null;
		if (this.inactivatedObjectStatement!= null){
			URI uri = (URI) this.inactivatedObjectStatement.getObject();
			rid = ORAdapter.openRdfUri2RMapUri(uri);
		}
		return rid;
	}
	@Override
	public void setInactivatedObjectId(RMapUri uri) throws RMapException {
		URI inactiveUri = ORAdapter.rMapUri2OpenRdfUri(uri);
		this.setInactivatedObjectStmt(inactiveUri);
	}
	/**
	 * 
	 * @return
	 */
	public Statement getInactivatedObjectStmt() {
		return this.inactivatedObjectStatement;
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
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventUpdate#getDerivationSourceObjectId()
	 */
	public RMapUri getDerivedObjectId() throws RMapException {
		RMapUri rid = null;
		if (this.derivationStatement!= null){
			URI uri = (URI) this.derivationStatement.getObject();
			rid = ORAdapter.openRdfUri2RMapUri(uri);
		}
		return rid;
	}
	/**
	 * 
	 * @return
	 */
	public Statement getDerivationStmt (){
		return this.derivationStatement;
	}
	
	@Override
	public void setDerivedObjectId(RMapUri uri) throws RMapException {
		URI derivedURI = ORAdapter.rMapUri2OpenRdfUri(uri);
		this.setDerivationStmt(derivedURI);
	}
	/**
	 * 
	 * @param derivedObject
	 * @throws RMapException
	 */
	protected void setDerivationStmt(URI derivedObject) throws RMapException {
		if (derivedObject != null){
			Statement stmt = this.getValueFactory().createStatement(this.context, 
					RMAP.EVENT_DERIVED_OBJECT,
					derivedObject, this.context);
			this.derivationStatement = stmt;
		}
	}

}

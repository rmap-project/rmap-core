/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.util.List;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapEventDerivation;
import info.rmapproject.core.model.RMapEventTargetType;
import info.rmapproject.core.model.RMapEventType;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

/**
 * @author smorrissey
 *
 */
public class ORMapEventDerivation extends ORMapEventWithNewObjects implements
		RMapEventDerivation {

	protected Statement sourceObjectStatement;
	protected Statement derivationStatement;

	/**
	 * @throws RMapException
	 */
	protected ORMapEventDerivation() throws RMapException {
		super();
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.DERIVATION);
	}

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	public ORMapEventDerivation(RMapUri associatedAgent,
			RMapEventTargetType targetType) throws RMapException {
		super(associatedAgent, targetType);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.DERIVATION);
	}

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @param desc
	 * @throws RMapException
	 */
	public ORMapEventDerivation(RMapUri associatedAgent,
			RMapEventTargetType targetType, RMapValue desc)
			throws RMapException {
		super(associatedAgent, targetType, desc);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.DERIVATION);
	}

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	public ORMapEventDerivation(URI associatedAgent,
			RMapEventTargetType targetType, URI sourceObject, URI derivedObject) 
	throws RMapException {
		super(associatedAgent, targetType);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.DERIVATION);
		this.setSourceObjectStmt(sourceObject);
		this.setDerivationStmt(derivedObject);
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
	 * @param sourceObjectStatement
	 * @throws RMapException
	 */
	public ORMapEventDerivation(Statement eventTypeStmt, Statement eventTargetTypeStmt, 
			Statement associatedAgentStmt,  Statement descriptionStmt, 
			Statement startTimeStmt,  Statement endTimeStmt, URI context, 
			Statement typeStatement, List<Statement> createdObjects,
			Statement derivationStatement, Statement sourceObjectStatement) 
	throws RMapException {
		super(eventTypeStmt,eventTargetTypeStmt,associatedAgentStmt,descriptionStmt,
				startTimeStmt, endTimeStmt,context,typeStatement);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.DERIVATION);
		
		if (createdObjects==null || createdObjects.size()==0){
			throw new RMapException ("Null or empty list of created object in Update");
		}		
		if (derivationStatement==null){
			throw new RMapException("Null derived object");
		}
		if (sourceObjectStatement==null){
			throw new RMapException("Null source object statement");
		}
		this.createdObjects = createdObjects;	
		this.derivationStatement = derivationStatement;
		this.sourceObjectStatement = sourceObjectStatement;
	}
	
	@Override
	public Model getAsModel() throws RMapException {
		Model model = super.getAsModel();
		if (sourceObjectStatement != null){
			model.add(sourceObjectStatement);
		}
		if (derivationStatement != null){
			model.add(derivationStatement);
		}
		return model;
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

	@Override
	public RMapUri getSourceObjectId() throws RMapException {
		RMapUri rid = null;
		if (this.sourceObjectStatement != null){
			URI uri = (URI) this.sourceObjectStatement.getObject();
			rid = ORAdapter.openRdfUri2RMapUri(uri);
		}
		return rid;
	}
	/**
	 * 
	 * @param sourceObject
	 * @throws RMapException
	 */
	protected void setSourceObjectStmt (URI sourceObject) throws RMapException {
		if (sourceObjectStatement != null){
			Statement stmt = this.getValueFactory().createStatement(this.context, 
					RMAP.EVENT_SOURCE_OBJECT,
					sourceObject, this.context);
			this.sourceObjectStatement = stmt;
		}
	}

	@Override
	public void setSourceObjectId(RMapUri uri) throws RMapException {
		URI sourceUri = ORAdapter.rMapUri2OpenRdfUri(uri);
		this.setSourceObjectStmt(sourceUri);
	}

	/**
	 * @return the sourceObjectStatement
	 */
	public Statement getSourceObjectStatement() {
		return sourceObjectStatement;
	}

}

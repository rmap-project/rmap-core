/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.util.List;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.IRI;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventType;
import info.rmapproject.core.model.event.RMapEventUpdate;
import info.rmapproject.core.model.request.RMapRequestAgent;
import info.rmapproject.core.vocabulary.impl.openrdf.RMAP;

/**
 * @author khanson, smorrissey
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
		this.setEventTypeStatement(RMapEventType.UPDATE);
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
			Statement startTimeStmt,  Statement endTimeStmt, IRI context, 
			Statement typeStatement, Statement associatedKeyStmt, List<Statement> createdObjects,
			Statement derivationStatement, Statement inactivatedObjectStatement) 
	throws RMapException {
		super(eventTypeStmt,eventTargetTypeStmt,associatedAgentStmt,descriptionStmt,
				startTimeStmt, endTimeStmt,context,typeStatement, associatedKeyStmt);
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
	 * @param inactivatedObject
	 * @param derivedObject
	 * @throws RMapException
	 */
	public ORMapEventUpdate(RMapRequestAgent associatedAgent, RMapEventTargetType targetType, IRI inactivatedObject, IRI derivedObject) 
	throws RMapException {
		super(associatedAgent, targetType);
		this.setEventTypeStatement(RMapEventType.UPDATE);
		this.setInactivatedObjectStmt(inactivatedObject);
		this.setDerivationStmt(derivedObject);
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
	public RMapIri getInactivatedObjectId() throws RMapException {
		RMapIri rid = null;
		if (this.inactivatedObjectStatement!= null){
			IRI iri = (IRI) this.inactivatedObjectStatement.getObject();
			rid = ORAdapter.openRdfIri2RMapIri(iri);
		}
		return rid;
	}
	@Override
	public void setInactivatedObjectId(RMapIri iri) throws RMapException, RMapDefectiveArgumentException {
		IRI inactiveIri = ORAdapter.rMapIri2OpenRdfIri(iri);
		this.setInactivatedObjectStmt(inactiveIri);
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
	protected void setInactivatedObjectStmt(IRI intactivatedObject) {
		if (intactivatedObject != null){
			Statement stmt = ORAdapter.getValueFactory().createStatement(this.context, 
					RMAP.INACTIVATEDOBJECT,
					intactivatedObject, this.context);
			this.inactivatedObjectStatement = stmt;
		}
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventUpdate#getDerivationSourceObjectId()
	 */
	public RMapIri getDerivedObjectId() throws RMapException {
		RMapIri rid = null;
		if (this.derivationStatement!= null){
			IRI iri = (IRI) this.derivationStatement.getObject();
			rid = ORAdapter.openRdfIri2RMapIri(iri);
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
	public void setDerivedObjectId(RMapIri iri) throws RMapException, RMapDefectiveArgumentException {
		IRI derivedIRI = ORAdapter.rMapIri2OpenRdfIri(iri);
		this.setDerivationStmt(derivedIRI);
	}
	/**
	 * 
	 * @param derivedObject
	 * @throws RMapException
	 */
	protected void setDerivationStmt(IRI derivedObject) throws RMapException {
		if (derivedObject != null){
			Statement stmt = ORAdapter.getValueFactory().createStatement(this.context, 
					RMAP.DERIVEDOBJECT,
					derivedObject, this.context);
			this.derivationStatement = stmt;
		}
	}

}

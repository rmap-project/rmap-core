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
import info.rmapproject.core.model.event.RMapEventDerivation;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventType;
import info.rmapproject.core.model.request.RMapRequestAgent;
import info.rmapproject.core.vocabulary.impl.openrdf.RMAP;

/**
 * @author smorrissey, khanson
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
		this.setEventTypeStatement(RMapEventType.DERIVATION);
	}


	/**
	 * 
	 * @param associatedAgent
	 * @param targetType
	 * @param sourceObject
	 * @param derivedObject
	 * @throws RMapException
	 */
	public ORMapEventDerivation(RMapRequestAgent associatedAgent, RMapEventTargetType targetType, IRI sourceObject, IRI derivedObject) 
	throws RMapException {
		super(associatedAgent, targetType);
		this.setEventTypeStatement(RMapEventType.DERIVATION);
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
			Statement startTimeStmt,  Statement endTimeStmt, IRI context, 
			Statement typeStatement, Statement associatedKeyStmt, List<Statement> createdObjects,
			Statement derivationStatement, Statement sourceObjectStatement) 
	throws RMapException {
		super(eventTypeStmt,eventTargetTypeStmt,associatedAgentStmt,descriptionStmt,
				startTimeStmt, endTimeStmt,context,typeStatement, associatedKeyStmt);
		
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
	public RMapIri getDerivedObjectId() throws RMapException {
		RMapIri rid = null;
		if (this.derivationStatement!= null){
			IRI iri = (IRI) this.derivationStatement.getObject();
			rid = typeAdapter.openRdfIri2RMapIri(iri);
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
		IRI derivedIRI = typeAdapter.rMapIri2OpenRdfIri(iri);
		this.setDerivationStmt(derivedIRI);
	}
	/**
	 * 
	 * @param derivedObject
	 * @throws RMapException
	 */
	protected void setDerivationStmt(IRI derivedObject) throws RMapException {
		if (derivedObject != null){
			Statement stmt = typeAdapter.getValueFactory().createStatement(this.context, 
					RMAP.DERIVEDOBJECT,
					derivedObject, this.context);
			this.derivationStatement = stmt;
		}
	}

	@Override
	public RMapIri getSourceObjectId() throws RMapException {
		RMapIri rid = null;
		if (this.sourceObjectStatement != null){
			IRI iri = (IRI) this.sourceObjectStatement.getObject();
			rid = typeAdapter.openRdfIri2RMapIri(iri);
		}
		return rid;
	}
	/**
	 * 
	 * @param sourceObject
	 * @throws RMapException
	 */
	protected void setSourceObjectStmt (IRI sourceObject) throws RMapException {
		if (sourceObject != null){
			Statement stmt = typeAdapter.getValueFactory().createStatement(this.context, 
					RMAP.HASSOURCEOBJECT,
					sourceObject, this.context);
			this.sourceObjectStatement = stmt;
		}
	}

	@Override
	public void setSourceObjectId(RMapIri iri) throws RMapException, RMapDefectiveArgumentException {
		IRI sourceIRI = typeAdapter.rMapIri2OpenRdfIri(iri);
		this.setSourceObjectStmt(sourceIRI);
	}

	/**
	 * @return the sourceObjectStatement
	 */
	public Statement getSourceObjectStatement() {
		return sourceObjectStatement;
	}

}

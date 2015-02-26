/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapEventTargetType;
import info.rmapproject.core.model.RMapEventType;
import info.rmapproject.core.model.RMapEventUpdate;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

/**
 * @author khansen, smorrissey
 *
 */
public class ORMapEventUpdate extends ORMapEvent implements RMapEventUpdate {
	protected List<Statement> createdObjects;
	protected Statement targetObjectStatement;
	protected Statement derivationStatement;
	protected Statement inactivatedObjectStatement;

	/**
	 * @throws RMapException
	 */
	protected ORMapEventUpdate() throws RMapException {
		super();
		this.makeEventTypeStatement(RMapEventType.UPDATE);
	}
	public ORMapEventUpdate(Statement eventTypeStmt, Statement eventTargetTypeStmt, 
			Statement associatedAgentStmt,  Statement descriptionStmt, 
			Statement startTimeStmt,  Statement endTimeStmt, URI context, 
			Statement typeStatement, List<Statement> createdObjects,
			Statement targetObjectStatement,Statement derivationStatement,
			Statement inactivatedObjectStatement) 
	throws RMapException {
		super(eventTypeStmt,eventTargetTypeStmt,associatedAgentStmt,descriptionStmt,
				startTimeStmt, endTimeStmt,context,typeStatement);
		this.createdObjects = createdObjects;	
		this.targetObjectStatement = targetObjectStatement;
		this.derivationStatement = derivationStatement;
		this.inactivatedObjectStatement = inactivatedObjectStatement;
	}
	/**
	 * @param associatedAgent
	 * @param targetType
	 * @param targetObject
	 * @throws RMapException
	 */
	public ORMapEventUpdate(RMapUri associatedAgent,
			RMapEventTargetType targetType, RMapUri targetObject) throws RMapException {
		super(associatedAgent, targetType);
		this.makeEventTypeStatement(RMapEventType.UPDATE);
		this.setTargetObjectStmt(ORAdapter.rMapUri2OpenRdfUri(targetObject));
	}
	/**
	 * 
	 * @param associatedAgent
	 * @param targetType
	 * @param targetObject
	 * @throws RMapException
	 */
	public ORMapEventUpdate(URI associatedAgent,
			RMapEventTargetType targetType, URI targetObject) throws RMapException {
		super(associatedAgent, targetType);
		this.makeEventTypeStatement(RMapEventType.UPDATE);
		this.setTargetObjectStmt(targetObject);
	}

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @param targetObject
	 * @param desc
	 * @throws RMapException
	 */
	public ORMapEventUpdate(RMapUri associatedAgent,
			RMapEventTargetType targetType, RMapUri targetObject, RMapValue desc)
			throws RMapException {
		super(associatedAgent, targetType, desc);
		this.makeEventTypeStatement(RMapEventType.UPDATE);
		this.setTargetObjectStmt(ORAdapter.rMapUri2OpenRdfUri(targetObject));
	}
	
	@Override
	public Model getAsModel() throws RMapException {
		Model model = super.getAsModel();
		model.add(targetObjectStatement);
		if (createdObjects != null){
			for (Statement stmt: createdObjects){
				model.add(stmt);
			}
		}
		if (inactivatedObjectStatement != null){
			model.add(inactivatedObjectStatement);
		}
		if (derivationStatement != null){
			model.add(derivationStatement);
		}
		return model;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventUpdate#getCreatedObjectIds()
	 */
	public List<RMapUri> getCreatedObjectIds() throws RMapException {	
		List<RMapUri> uris = null;
		if (this.createdObjects != null){
			uris = new ArrayList<RMapUri>();
			for (Statement stmt:this.createdObjects){
				URI idURI = (URI) stmt.getObject();
				RMapUri rid = ORAdapter.openRdfUri2RMapUri(idURI);
				uris.add(rid);
			}
		}
		return uris;
	}
	/**
	 * 
	 * @return
	 */
	public List<Statement> getCreatedObjectStatements() {
		return this.createdObjects;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventUpdate#setCreatedObjectIds(java.util.List)
	 */
	public void setCreatedObjectIds(List<RMapUri> createdObjects) throws RMapException {
		List<Statement> stmts = null;
		if (createdObjects != null){
			stmts = new ArrayList<Statement>();
			for (RMapUri rUri:createdObjects){
				URI id = ORAdapter.rMapUri2OpenRdfUri(rUri);
				Statement stmt = this.getValueFactory().createStatement(this.context, PROV.GENERATED, id, this.context);
				stmts.add(stmt);
			}
			this.createdObjects = stmts;
		}
	}

	/**
	 * 
	 * @param createdObjects
	 */
	public void setCreatedObjectIdsFromURI(Set<URI> createdObjects) {
		List<Statement> stmts = null;
		if (createdObjects != null){
			stmts = new ArrayList<Statement>();
			for (URI id:createdObjects){
				Statement stmt = this.getValueFactory().createStatement(this.context, PROV.GENERATED, id, 
						this.context);
				stmts.add(stmt);
			}
			this.createdObjects = stmts;
		}		
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventUpdate#getInactivatedObjectId()
	 */
	public RMapUri getTargetObjectId() throws RMapException {
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
	public Statement getTargetObjectStmt() {
		return this.targetObjectStatement;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventUpdate#setInactivatedObjectId(info.rmapproject.core.model.RMapUri)
	 */
	public void setTargetObjectId(RMapUri targetObject) throws RMapException {
		if (targetObject != null){
			Statement stmt = this.getValueFactory().createStatement(this.context, RMAP.EVENT_TARGET,
					ORAdapter.rMapUri2OpenRdfUri(targetObject), this.context);
			this.targetObjectStatement = stmt;
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
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventUpdate#deriveFromTarget(info.rmapproject.core.model.RMapUri)
	 */
	public void deriveFromTarget(RMapUri derivedObject) throws RMapException {
		if (this.targetObjectStatement==null){
			throw new RMapException ("Target object id is null");
		}
		if (derivedObject==null){
			throw new RMapException("Derived object id is null");
		}
		Statement stmt = this.getValueFactory().createStatement(this.context, RMAP.EVENT_NEW_OBJECT_DERIVATION_SOURCE,
				this.targetObjectStatement.getObject(), this.context);
		this.derivationStatement = stmt;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventUpdate#inactivateTarget()
	 */
	public void inactivateTarget() throws RMapException {
			URI targetUri = (URI)this.targetObjectStatement.getObject();
			Statement stmt = this.getValueFactory().createStatement(this.context, RMAP.EVENT_TARGET_INACTIVATED,
					targetUri, this.context);
			this.inactivatedObjectStatement = stmt;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventUpdate#isTargetInactivated()
	 */
	public boolean isTargetInactivated() throws RMapException {
		return (this.inactivatedObjectStatement!=null);
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
	 * @param inactivatedObject
	 * @throws RMapException
	 */
	protected void setTargetObjectStmt(URI targetObject) throws RMapException {
		if (targetObject != null){
			Statement stmt = this.getValueFactory().createStatement(this.context, RMAP.EVENT_TARGET,
					targetObject, this.context);
			this.targetObjectStatement = stmt;
		}
	}

}

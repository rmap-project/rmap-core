/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.IRI;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventWithNewObjects;
import info.rmapproject.core.model.request.RMapRequestAgent;
import info.rmapproject.core.vocabulary.impl.openrdf.PROV;

/**
 * @author smorrissey
 *
 */
public abstract class ORMapEventWithNewObjects extends ORMapEvent implements
		RMapEventWithNewObjects {
	
	protected List<Statement> createdObjects;

	/**
	 * @param eventTypeStmt
	 * @param eventTargetTypeStmt
	 * @param associatedAgentStmt
	 * @param descriptionStmt
	 * @param startTimeStmt
	 * @param endTimeStmt
	 * @param context
	 * @param typeStatement
	 * @param associatedKeyStmt - Null if none specified
	 * @throws RMapException
	 */
	protected ORMapEventWithNewObjects(Statement eventTypeStmt,
			Statement eventTargetTypeStmt, Statement associatedAgentStmt,
			Statement descriptionStmt, Statement startTimeStmt,
			Statement endTimeStmt, IRI context, Statement typeStatement, Statement associatedKeyStmt)
			throws RMapException {
		super(eventTypeStmt, eventTargetTypeStmt, associatedAgentStmt,
				descriptionStmt, startTimeStmt, endTimeStmt, context,
				typeStatement, associatedKeyStmt);
	}

	/**
	 * @throws RMapException
	 */
	protected ORMapEventWithNewObjects() throws RMapException {
		super();
	}

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	protected ORMapEventWithNewObjects(RMapRequestAgent associatedAgent, RMapEventTargetType targetType) throws RMapException {
		super(associatedAgent, targetType);
	}

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @param desc
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	protected ORMapEventWithNewObjects(RMapRequestAgent associatedAgent, RMapEventTargetType targetType, RMapValue desc)
			throws RMapException, RMapDefectiveArgumentException {
		super(associatedAgent, targetType, desc);
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventWithNewObjects#getCreatedObjectIds()
	 */
	@Override
	public List<RMapIri> getCreatedObjectIds() throws RMapException {
		List<RMapIri> iris = null;
		if (this.createdObjects != null){
			iris = new ArrayList<RMapIri>();
			for (Statement stmt:this.createdObjects){
				IRI idIRI = (IRI) stmt.getObject();
				RMapIri rid = ORAdapter.openRdfIri2RMapIri(idIRI);
				iris.add(rid);
			}
		}
		return iris;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventWithNewObjects#setCreatedObjectIds(java.util.List)
	 */
	@Override
	public void setCreatedObjectIds(List<RMapIri> createdObjects)
			throws RMapException, RMapDefectiveArgumentException {
		List<Statement> stmts = null;
		if (createdObjects != null){
			stmts = new ArrayList<Statement>();
			for (RMapIri rIri:createdObjects){
				IRI id = ORAdapter.rMapIri2OpenRdfIri(rIri);
				Statement stmt = ORAdapter.getValueFactory().createStatement(this.context, PROV.GENERATED, id, this.context);
				stmts.add(stmt);
			}
			this.createdObjects = stmts;
		}
	}

	/**
	 * 
	 * @return
	 */
	public List<Statement> getCreatedObjectStatements(){
		return this.createdObjects;
	}
		
	/**
	 * 
	 * @param createdObjects
	 * @throws RMapException
	 */
	public void setCreatedObjectIdsFromIRI (Set<IRI> createdObjects) throws RMapException {
		List<Statement> stmts = null;
		if (createdObjects != null){
			stmts = new ArrayList<Statement>();
			for (IRI id:createdObjects){
				Statement stmt = ORAdapter.getValueFactory().createStatement(this.context, PROV.GENERATED, id, 
						this.context);
				stmts.add(stmt);
			}
			this.createdObjects = stmts;
		}
	}
	
	@Override
	public Model getAsModel() throws RMapException {
		Model model = super.getAsModel();
		if (createdObjects != null){
			for (Statement stmt: createdObjects){
				model.add(stmt);
			}
		}
		return model;
	}


}

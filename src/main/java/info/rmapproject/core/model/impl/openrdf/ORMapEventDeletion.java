/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.IRI;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.model.event.RMapEventDeletion;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventType;
import info.rmapproject.core.model.request.RMapRequestAgent;
import info.rmapproject.core.vocabulary.impl.openrdf.RMAP;

/**
 *  @author khanson, smorrissey
 *
 */
public class ORMapEventDeletion extends ORMapEvent implements RMapEventDeletion {

	protected List<Statement> deletedObjects;
	/**
	 * @throws RMapException
	 */
	protected ORMapEventDeletion() throws RMapException {
		super();
		this.setEventTypeStatement(RMapEventType.DELETION);
	}
	/**
	 * Most likely use is to construct Event for read() method in RMapService from statements
	 * in Triplestore
	 * @param eventTypeStmt
	 * @param eventTargetTypeStmt
	 * @param associatedAgentStmt
	 * @param descriptionStmt
	 * @param startTimeStmt
	 * @param endTimeStmt
	 * @param context
	 * @param typeStatement
	 * @param deletedObjects
	 * @throws RMapException
	 */
	public ORMapEventDeletion(Statement eventTypeStmt, 
			Statement eventTargetTypeStmt, Statement associatedAgentStmt,
			Statement descriptionStmt, Statement startTimeStmt,  
			Statement endTimeStmt, IRI context, Statement typeStatement, Statement associatedKeyStmt, 
			List<Statement> deletedObjects) throws RMapException {
		
		super(eventTypeStmt,eventTargetTypeStmt,associatedAgentStmt,descriptionStmt,
				startTimeStmt, endTimeStmt,context,typeStatement, associatedKeyStmt);
		this.deletedObjects = deletedObjects;
	}

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @param desc
	 * @throws RMapException
	 */
	public ORMapEventDeletion(RMapRequestAgent associatedAgent, 
			RMapEventTargetType targetType, RMapValue desc)
			throws RMapException, RMapDefectiveArgumentException {
		super(associatedAgent, targetType, desc);
		this.setEventTypeStatement(RMapEventType.DELETION);
	}
	
	@Override
	public Model getAsModel() throws RMapException {
		Model model = super.getAsModel();
		for (Statement stmt:deletedObjects){
			model.add(stmt);
		}
		return model;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventDelete#getDeletedObjectIds()
	 */
	public List<RMapIri> getDeletedObjectIds() throws RMapException {
		List<RMapIri> iris = null;
		if (this.deletedObjects!= null){
			iris = new ArrayList<RMapIri>();
			for (Statement stmt:this.deletedObjects){
				IRI deletedIri = (IRI) stmt.getObject();
				iris.add(ORAdapter.openRdfIri2RMapIri(deletedIri));
			}
		}
		return iris;
	}
	
	public List<Statement> getDeletedObjectStmts(){
		return this.deletedObjects;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventDelete#setDeletedObjectIds(java.util.List)
	 */
	public void setDeletedObjectIds(List<RMapIri> deletedObjectIds) 
			throws RMapException, RMapDefectiveArgumentException {
		if (deletedObjectIds != null){
			List<Statement> stmts = new ArrayList<Statement>();
			for (RMapIri rid:deletedObjectIds){
				Statement stmt = ORAdapter.getValueFactory().createStatement(this.context, RMAP.DELETEDOBJECT,
						ORAdapter.rMapIri2OpenRdfIri(rid), this.context);
				stmts.add(stmt);
			}
			this.deletedObjects = stmts;
		}
	}

}

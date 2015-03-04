/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapEventDeletion;
import info.rmapproject.core.model.RMapEventTargetType;
import info.rmapproject.core.model.RMapEventType;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

/**
 *  @author khansen, smorrissey
 *
 */
public class ORMapEventDeletion extends ORMapEvent implements RMapEventDeletion {

	protected List<Statement> deletedObjects;
	/**
	 * @throws RMapException
	 */
	protected ORMapEventDeletion() throws RMapException {
		super();
		this.makeEventTypeStatement(RMapEventType.DELETION);
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
			Statement endTimeStmt, URI context, Statement typeStatement, 
			List<Statement> deletedObjects) throws RMapException {
		
		super(eventTypeStmt,eventTargetTypeStmt,associatedAgentStmt,descriptionStmt,
				startTimeStmt, endTimeStmt,context,typeStatement);
		this.deletedObjects = deletedObjects;
	}
	/**
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	public ORMapEventDeletion(RMapUri associatedAgent,
			RMapEventTargetType targetType) throws RMapException {
		super(associatedAgent, targetType);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.DELETION);
	}

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @param desc
	 * @throws RMapException
	 */
	public ORMapEventDeletion(RMapUri associatedAgent,
			RMapEventTargetType targetType, RMapValue desc)
			throws RMapException {
		super(associatedAgent, targetType, desc);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.DELETION);
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
	public List<RMapUri> getDeletedObjectIds() throws RMapException {
		List<RMapUri> uris = null;
		if (this.deletedObjects!= null){
			uris = new ArrayList<RMapUri>();
			for (Statement stmt:this.deletedObjects){
				URI deletedUri = (URI) stmt.getObject();
				uris.add(ORAdapter.openRdfUri2RMapUri(deletedUri));
			}
		}
		return uris;
	}
	
	public List<Statement> getDeletedObjectStmts(){
		return this.deletedObjects;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventDelete#setDeletedObjectIds(java.util.List)
	 */
	public void setDeletedObjectIds(List<RMapUri> deletedObjectIds) throws RMapException {
		if (deletedObjectIds != null){
			List<Statement> stmts = new ArrayList<Statement>();
			for (RMapUri rid:deletedObjectIds){
				Statement stmt = this.getValueFactory().createStatement(this.context, RMAP.EVENT_TARGET_DELETED,
						ORAdapter.rMapUri2OpenRdfUri(rid), this.context);
				stmts.add(stmt);
			}
			this.deletedObjects = stmts;
		}
	}

}

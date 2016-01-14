/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventType;
import info.rmapproject.core.model.event.RMapEventUpdateWithReplace;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

/**
 *  @author khanson, smorrissey
 *
 */
public class ORMapEventUpdateWithReplace extends ORMapEvent implements RMapEventUpdateWithReplace {

	protected Statement updatedObjectIdStmt;
	/**
	 * @throws RMapException
	 */
	protected ORMapEventUpdateWithReplace() throws RMapException {
		super();
		this.makeEventTypeStatement(RMapEventType.REPLACE);
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
	 * @param updatedObjectIdStmt
	 * @throws RMapException
	 */
	public ORMapEventUpdateWithReplace(Statement eventTypeStmt, 
			Statement eventTargetTypeStmt, Statement associatedAgentStmt,
			Statement descriptionStmt, Statement startTimeStmt,  
			Statement endTimeStmt, URI context, Statement typeStatement, 
			Statement updatedObjectIdStmt) throws RMapException {
		
		super(eventTypeStmt,eventTargetTypeStmt,associatedAgentStmt,descriptionStmt,
				startTimeStmt, endTimeStmt,context,typeStatement);
		this.updatedObjectIdStmt = updatedObjectIdStmt;
	}

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	public ORMapEventUpdateWithReplace(URI associatedAgent,
			RMapEventTargetType targetType) throws RMapException {
		super(associatedAgent, targetType);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.REPLACE);
	}
	

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public ORMapEventUpdateWithReplace(URI associatedAgent,	RMapEventTargetType targetType, URI updateObjectId) 
				throws RMapException, RMapDefectiveArgumentException {
		super(associatedAgent, targetType);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.REPLACE);
		this.setUpdatedObjectId(ORAdapter.openRdfUri2RMapUri(updateObjectId));
	}
	

	@Override
	public Model getAsModel() throws RMapException {
		Model model = super.getAsModel();
		if (this.updatedObjectIdStmt!=null){
			model.add(this.updatedObjectIdStmt);			
		}
		return model;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventUpdateWithReplace#getReplacedObjectIds()
	 */
	public RMapUri getUpdatedObjectId() throws RMapException {
		RMapUri updatedObjectUri = null;
		if (this.updatedObjectIdStmt!= null){
			URI uri = (URI) this.updatedObjectIdStmt.getObject();
			updatedObjectUri = ORAdapter.openRdfUri2RMapUri(uri);
		}
		return updatedObjectUri;
	}
	
	public Statement getUpdatedObjectStmt(){
		return this.updatedObjectIdStmt;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventUpdateWithReplace#setUpdatedObjectIds(java.util.List)
	 */
	public void setUpdatedObjectId(RMapUri updatedObjectId) 
			throws RMapException, RMapDefectiveArgumentException {
		if (updatedObjectId != null){
			Statement stmt = this.getValueFactory().createStatement(this.context, RMAP.EVENT_UPDATED_OBJECT,
						ORAdapter.rMapUri2OpenRdfUri(updatedObjectId), this.context);
			this.updatedObjectIdStmt = stmt;
		}
	}

}

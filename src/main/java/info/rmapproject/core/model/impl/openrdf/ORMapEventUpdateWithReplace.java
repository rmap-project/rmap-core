/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventType;
import info.rmapproject.core.model.event.RMapEventUpdateWithReplace;
import info.rmapproject.core.model.request.RMapRequestAgent;
import info.rmapproject.core.vocabulary.impl.openrdf.RMAP;

import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;

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
		this.setEventTypeStatement(RMapEventType.REPLACE);
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
			Statement endTimeStmt, IRI context, Statement typeStatement, Statement associatedKeyStmt,
			Statement updatedObjectIdStmt) throws RMapException {
		
		super(eventTypeStmt,eventTargetTypeStmt,associatedAgentStmt,descriptionStmt,
				startTimeStmt, endTimeStmt,context,typeStatement, associatedKeyStmt);
		this.updatedObjectIdStmt = updatedObjectIdStmt;
	}

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	public ORMapEventUpdateWithReplace(RMapRequestAgent associatedAgent, RMapEventTargetType targetType) throws RMapException {
		super(associatedAgent, targetType);
		this.setEventTypeStatement(RMapEventType.REPLACE);
	}
	
	/**
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	public ORMapEventUpdateWithReplace(RMapRequestAgent associatedAgent, RMapEventTargetType targetType, IRI updateObjectId) 
				throws RMapException, RMapDefectiveArgumentException {
		this(associatedAgent, targetType);
		this.setUpdatedObjectId(ORAdapter.openRdfIri2RMapIri(updateObjectId));
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
	public RMapIri getUpdatedObjectId() throws RMapException {
		RMapIri updatedObjectIri = null;
		if (this.updatedObjectIdStmt!= null){
			IRI iri = (IRI) this.updatedObjectIdStmt.getObject();
			updatedObjectIri = ORAdapter.openRdfIri2RMapIri(iri);
		}
		return updatedObjectIri;
	}
	
	public Statement getUpdatedObjectStmt(){
		return this.updatedObjectIdStmt;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventUpdateWithReplace#setUpdatedObjectIds(java.util.List)
	 */
	public void setUpdatedObjectId(RMapIri updatedObjectId) 
			throws RMapException, RMapDefectiveArgumentException {
		if (updatedObjectId != null){
			Statement stmt = ORAdapter.getValueFactory().createStatement(this.context, RMAP.UPDATEDOBJECT,
					ORAdapter.rMapIri2OpenRdfIri(updatedObjectId), this.context);
			this.updatedObjectIdStmt = stmt;
		}
	}

}

/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventTombstone;
import info.rmapproject.core.model.event.RMapEventType;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

/**
 *  @author khansen, smorrissey
 *
 */
public class ORMapEventTombstone extends ORMapEvent implements
		RMapEventTombstone {
	
	protected Statement tombstoned;

	/**
	 * @throws RMapException
	 */
	protected ORMapEventTombstone() throws RMapException {
		super();
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.TOMBSTONE);
	}
	
	public ORMapEventTombstone(Statement eventTypeStmt, 
			Statement eventTargetTypeStmt, Statement associatedAgentStmt,
			Statement descriptionStmt, Statement startTimeStmt,  
			Statement endTimeStmt, URI context, Statement typeStatement, 
			Statement tombstoned) throws RMapException {
		
		super(eventTypeStmt,eventTargetTypeStmt,associatedAgentStmt,descriptionStmt,
				startTimeStmt, endTimeStmt,context,typeStatement);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.TOMBSTONE);
		this.tombstoned = tombstoned;
	}

	/**
	 * 
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	public ORMapEventTombstone(URI associatedAgent,
			RMapEventTargetType targetType, URI tombstonedResource) throws RMapException {
		super(associatedAgent, targetType);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.TOMBSTONE);
		this.setTombstonedResourceIdStmt(tombstonedResource);
	}

	@Override
	public Model getAsModel() throws RMapException {
		Model model = super.getAsModel();
		model.add(tombstoned);
		return model;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventTombstone#getTombstonedResourceId()
	 */
	public RMapUri getTombstonedResourceId() throws RMapException {
		RMapUri uri = null;
		if (this.tombstoned!= null){
			URI tUri = (URI) this.tombstoned.getObject();
			uri = ORAdapter.openRdfUri2RMapUri(tUri);
		}
		return uri;
	}
	/**
	 * 
	 * @return
	 */
	public Statement getTombstonedResourceStmt(){
		return this.tombstoned;
	}
	/**
	 * 
	 * @param tombstonedResource
	 * @throws RMapException
	 */
	private void setTombstonedResourceIdStmt(URI tombstonedResource) throws RMapException {
		if (tombstonedResource != null){
			Statement stmt = this.getValueFactory().createStatement(this.context, RMAP.EVENT_TARGET_TOMBSTONED,
					tombstonedResource, this.context);
			this.tombstoned = stmt;
		}
	}

}

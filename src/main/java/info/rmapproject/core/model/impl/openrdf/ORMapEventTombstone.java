/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.IRI;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventTombstone;
import info.rmapproject.core.model.event.RMapEventType;
import info.rmapproject.core.model.request.RMapRequestAgent;
import info.rmapproject.core.vocabulary.impl.openrdf.RMAP;

/**
 *  @author khanson, smorrissey
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
		this.setEventTypeStatement(RMapEventType.TOMBSTONE);
	}
	
	public ORMapEventTombstone(Statement eventTypeStmt, 
			Statement eventTargetTypeStmt, Statement associatedAgentStmt,
			Statement descriptionStmt, Statement startTimeStmt,  
			Statement endTimeStmt, IRI context, Statement typeStatement, Statement associatedKeyStmt,
			Statement tombstoned) throws RMapException {
		
		super(eventTypeStmt,eventTargetTypeStmt,associatedAgentStmt,descriptionStmt,
				startTimeStmt, endTimeStmt,context,typeStatement, associatedKeyStmt);
		this.tombstoned = tombstoned;
	}

	/**
	 * 
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	public ORMapEventTombstone(RMapRequestAgent associatedAgent, RMapEventTargetType targetType, IRI tombstonedResource) throws RMapException {
		super(associatedAgent, targetType);
		this.setEventTypeStatement(RMapEventType.TOMBSTONE);
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
	public RMapIri getTombstonedResourceId() throws RMapException {
		RMapIri iri = null;
		if (this.tombstoned!= null){
			IRI tIri = (IRI) this.tombstoned.getObject();
			iri = ORAdapter.openRdfIri2RMapIri(tIri);
		}
		return iri;
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
	private void setTombstonedResourceIdStmt(IRI tombstonedResource) throws RMapException {
		if (tombstonedResource != null){
			Statement stmt = ORAdapter.getValueFactory().createStatement(this.context, RMAP.TOMBSTONEDOBJECT,
					tombstonedResource, this.context);
			this.tombstoned = stmt;
		}
	}

}

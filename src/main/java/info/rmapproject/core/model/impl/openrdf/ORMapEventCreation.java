/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openrdf.model.URI;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapEventCreation;
import info.rmapproject.core.model.RMapEventTargetType;
import info.rmapproject.core.model.RMapEventType;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;

/**
 *  @author khansen, smorrissey
 *
 */
public class ORMapEventCreation extends ORMapEvent implements RMapEventCreation {

	List<ORMapStatement> createdObjects;
	/**
	 * @throws RMapException
	 */
	protected ORMapEventCreation() throws RMapException {
		super();
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.CREATION);
	}
	
	/**
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	public ORMapEventCreation(RMapUri associatedAgent,
			RMapEventTargetType targetType) throws RMapException {
		super(associatedAgent, targetType);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.CREATION);
	}
	
	/**
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	public ORMapEventCreation(URI associatedAgent,
			RMapEventTargetType targetType) throws RMapException {
		super(associatedAgent, targetType);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.CREATION);
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
	 * @throws RMapException
	 */
	public ORMapEventCreation(ORMapStatement eventTypeStmt, ORMapStatement eventTargetTypeStmt, 
			ORMapStatement associatedAgentStmt,  ORMapStatement descriptionStmt, 
			ORMapStatement startTimeStmt,  ORMapStatement endTimeStmt, URI context, 
			ORMapStatement typeStatement, List<ORMapStatement> createdObjects) 
					throws RMapException {
		super(eventTypeStmt,eventTargetTypeStmt,associatedAgentStmt,descriptionStmt,
				startTimeStmt, endTimeStmt,context,typeStatement);
		this.createdObjects = createdObjects;
	}

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @param desc
	 * @throws RMapException
	 */
	public ORMapEventCreation(RMapUri associatedAgent,
			RMapEventTargetType targetType, RMapResource desc)
			throws RMapException {
		super(associatedAgent, targetType, desc);
		this.eventTypeStmt = this.makeEventTypeStatement(RMapEventType.CREATION);
	}

	public ORMapEventCreation(RMapUri associatedAgent,
			RMapEventTargetType targetType, RMapResource desc, List<RMapUri> createdObjIds)
		throws RMapException{
		this(associatedAgent, targetType, desc);
		this.setCreatedObjectIds(createdObjIds);
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventCreation#getCreatedObjectIds()
	 */
	public List<RMapUri> getCreatedObjectIds() throws RMapException {
		List<RMapUri> uris = null;
		if (this.createdObjects != null){
			uris = new ArrayList<RMapUri>();
			for (ORMapStatement stmt:this.createdObjects){
				URI idURI = (URI) stmt.getRmapStmtObject();
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
	public List<ORMapStatement> getCreatedObjectStatements(){
		return this.createdObjects;
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventCreation#setCreatedObjectIds(java.util.List)
	 */
	public void setCreatedObjectIds(List<RMapUri> createdObjects) throws RMapException {
		List<ORMapStatement> stmts = null;
		if (createdObjects != null){
			stmts = new ArrayList<ORMapStatement>();
			for (RMapUri rUri:createdObjects){
				URI id = ORAdapter.rMapUri2OpenRdfUri(rUri);
				ORMapStatement stmt = new ORMapStatement(this.context, PROV.GENERATED, id, this.context);
				stmts.add(stmt);
			}
			this.createdObjects = stmts;
		}
	}
	/**
	 * 
	 * @param createdObjects
	 * @throws RMapException
	 */
	public void setCreatedObjectIdsFromURI (Set<URI> createdObjects) throws RMapException {
		List<ORMapStatement> stmts = null;
		if (createdObjects != null){
			stmts = new ArrayList<ORMapStatement>();
			for (URI id:createdObjects){
				ORMapStatement stmt = new ORMapStatement(this.context, PROV.GENERATED, id, 
						this.context);
				stmts.add(stmt);
			}
			this.createdObjects = stmts;
		}
	}

}

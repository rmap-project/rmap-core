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
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.event.RMapEventTargetType;
import info.rmapproject.core.model.event.RMapEventWithNewObjects;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;

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
	 * @throws RMapException
	 */
	protected ORMapEventWithNewObjects(Statement eventTypeStmt,
			Statement eventTargetTypeStmt, Statement associatedAgentStmt,
			Statement descriptionStmt, Statement startTimeStmt,
			Statement endTimeStmt, URI context, Statement typeStatement)
			throws RMapException {
		super(eventTypeStmt, eventTargetTypeStmt, associatedAgentStmt,
				descriptionStmt, startTimeStmt, endTimeStmt, context,
				typeStatement);
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
	protected ORMapEventWithNewObjects(RMapUri associatedAgent,
			RMapEventTargetType targetType) throws RMapException {
		super(associatedAgent, targetType);
	}

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @throws RMapException
	 */
	protected ORMapEventWithNewObjects(URI associatedAgent,
			RMapEventTargetType targetType) throws RMapException {
		super(associatedAgent, targetType);
	}

	/**
	 * @param associatedAgent
	 * @param targetType
	 * @param desc
	 * @throws RMapException
	 */
	protected ORMapEventWithNewObjects(RMapUri associatedAgent,
			RMapEventTargetType targetType, RMapValue desc)
			throws RMapException {
		super(associatedAgent, targetType, desc);
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventWithNewObjects#getCreatedObjectIds()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapEventWithNewObjects#setCreatedObjectIds(java.util.List)
	 */
	@Override
	public void setCreatedObjectIds(List<RMapUri> createdObjects)
			throws RMapException {
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
	public void setCreatedObjectIdsFromURI (Set<URI> createdObjects) throws RMapException {
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

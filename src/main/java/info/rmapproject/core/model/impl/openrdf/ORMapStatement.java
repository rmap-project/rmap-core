/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.net.URISyntaxException;
import java.util.List;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapNonLiteral;
import info.rmapproject.core.model.RMapStatement;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;


import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.RDF;

/**
 * @author khansen, smorrissey
 *
 */
public class ORMapStatement extends ORMapObject implements RMapStatement {


	protected Statement rmapStmtStatement;
	protected Statement typeStatement;

	/**
	 * @throws RMapException
	 */
	protected ORMapStatement() throws RMapException {
		super();
	}
	/**
	 * Construct ORMapStatement from RMap model objects
	 * @param subject
	 * @param predicate
	 * @param object
	 * @throws RMapException
	 */
	public ORMapStatement (RMapNonLiteral subject, RMapUri predicate, RMapResource object) 
			throws RMapException {
		this();
		this.typeStatement = 
				this.makeRmapStmtStatement(ORAdapter.uri2OpenRdfUri(this.getId()), RDF.TYPE, RMAP.STATEMENT, null);
		this.rmapStmtStatement = this.makeRmapStmtStatement(
				 ORAdapter.rMapNonLiteral2OpenRdfResource(subject), 
				 ORAdapter.rMapUri2OpenRdfUri(predicate), 
				 ORAdapter.rMapResource2OpenRdfValue(object), null);
	}
	/**
	 * Construct ORMapStatement from RMap model objects
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param uri context
	 * @throws RMapException
	 */
	public ORMapStatement (RMapNonLiteral subject, RMapUri predicate, RMapResource object, RMapUri context) 
			throws RMapException {
		this();
		this.rmapStmtStatement = this.makeRmapStmtStatement(
				 ORAdapter.rMapNonLiteral2OpenRdfResource(subject), 
				 ORAdapter.rMapUri2OpenRdfUri(predicate), 
				 ORAdapter.rMapResource2OpenRdfValue(object), 
				 ORAdapter.rMapUri2OpenRdfUri(context));
	}
	/**
	 * Construct  ORMapStatement from org.openrdf.model.Statement
	 * @param stmt
	 * @throws RMapException
	 */
	public ORMapStatement(Statement stmt) throws RMapException {
		this();
		this.setRmapStmtStatement(stmt);
	}
	/**
	 * 
	 * @param rmapStmt
	 * @throws RMapException
	 */
	public ORMapStatement(RMapStatement rmapStmt) throws RMapException {
		this();
		if (rmapStmt instanceof ORMapStatement){
			this.setId(rmapStmt.getId());
			this.rmapStmtStatement = ((ORMapStatement)rmapStmt).rmapStmtStatement;			
		}
		else {
			this.rmapStmtStatement = this.makeRmapStmtStatement(
					 ORAdapter.rMapNonLiteral2OpenRdfResource(rmapStmt.getSubject()), 
					 ORAdapter.rMapUri2OpenRdfUri(rmapStmt.getPredicate()), 
					 ORAdapter.rMapResource2OpenRdfValue(rmapStmt.getObject()), 
					 null);
		}
	}
	/**
	 * Construct ORMapStatement from org.openrdf.model subject, predicate, and (String) object
	 * @param rmapStmtSubject
	 * @param rmapStmtPredicate
	 * @param rmapStmtObject
	 * @throws RMapException
	 */
	public ORMapStatement(Resource rmapStmtSubject, URI rmapStmtPredicate, 
			String rmapStmtObject) throws RMapException {
		this();
		Literal literal = ORAdapter.getValueFactory().createLiteral(rmapStmtObject);
		this.rmapStmtStatement = this.makeRmapStmtStatement(rmapStmtSubject,rmapStmtPredicate,literal,null);
	}
	/**
	 * Construct ORMapStatement from org.openrdf.model subject, predicate, and (Value) object
	 * @param rmapStmtSubject
	 * @param rmapStmtPredicate
	 * @param rmapStmtObject
	 * @throws RMapException
	 */
	public ORMapStatement(Resource rmapStmtSubject, URI rmapStmtPredicate, 
			Value rmapStmtObject) throws RMapException {
		this();
		this.rmapStmtStatement = this.makeRmapStmtStatement(rmapStmtSubject,rmapStmtPredicate,rmapStmtObject,null);
	}
	/**
	 * Construct ORMapStatement from org.openrdf.model subject, predicate, (String) object, and context
	 * @param rmapStmtSubject
	 * @param rmapStmtPredicate
	 * @param rmapStmtObject
	 * @param rmapStmtContext
	 * @throws RMapException
	 */
	public ORMapStatement(Resource rmapStmtSubject, URI rmapStmtPredicate, 
			String rmapStmtObject, URI rmapStmtContext) throws RMapException {
		this();
		Literal literal = ORAdapter.getValueFactory().createLiteral(rmapStmtObject);
		this.rmapStmtStatement = this.makeRmapStmtStatement(rmapStmtSubject,rmapStmtPredicate,
				literal,rmapStmtContext);
	}
	/**
	 * Construct ORMapStatement from org.openrdf.model subject, predicate, (Value) object, and context
	 * @param rmapStmtSubject
	 * @param rmapStmtPredicate
	 * @param rmapStmtObject
	 * @param rmapStmtContext
	 * @throws RMapException
	 */
	public ORMapStatement(Resource rmapStmtSubject, URI rmapStmtPredicate, 
			Value rmapStmtObject, URI rmapStmtContext) throws RMapException {
		this();
		this.rmapStmtStatement = this.makeRmapStmtStatement(rmapStmtSubject,rmapStmtPredicate,
				rmapStmtObject,rmapStmtContext);
	}

	
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapStatement#getSubject()
	 */
	public RMapNonLiteral getSubject() {
		RMapNonLiteral subject = null;
		try {
			subject = ORAdapter.openRdfResource2NonLiteralResource(
					this.rmapStmtStatement.getSubject());
		}
		catch (Exception e){}
		return subject;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapStatement#getPredicate()
	 */
	public RMapUri getPredicate() {
		RMapUri predicate = null;
		try {
			predicate = ORAdapter.openRdfUri2RMapUri(
					this.rmapStmtStatement.getPredicate());
		}
		catch (Exception e){}
		return predicate;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapStatement#getObject()
	 */
	public RMapResource getObject() {
		RMapResource object = null;
		try {
			object = ORAdapter.openRdfValue2RMapResource(
					this.rmapStmtStatement.getObject());
		}
		catch (Exception e){}
		return object;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapStatement#getStatus()
	 */
	public RMapStatus getStatus() throws Exception {
		RMapService service = RMapServiceFactoryIOC.getFactory().createService();
		RMapStatus status = service.getStatementStatus(this.getId());
		return status;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapStatement#getRelatedEvents()
	 */
	public List<java.net.URI> getRelatedEvents() throws Exception {
		RMapService service = RMapServiceFactoryIOC.getFactory().createService();
		List<java.net.URI> eventids = service.getStatementEvents(this.getId());
		return eventids;
	}
	
	
	/**
	 * Construct org.openrdf.model.Statement from subject, predicate, object, and context
	 * @param rmapStmtSubject
	 * @param rmapStmtPredicate
	 * @param rmapStmtObject
	 * @param rmapStmtContext
	 * @return
	 * @throws Exception
	 */
	protected Statement makeRmapStmtStatement(Resource rmapStmtSubject,
			URI rmapStmtPredicate, Value rmapStmtObject, Resource rmapStmtContext) 
					throws RMapException {
		if (rmapStmtSubject==null){
			throw new RMapException (new IllegalArgumentException("Null Statement Subject"));
		}
		if (rmapStmtPredicate==null){
			throw new RMapException (new IllegalArgumentException("Null Statement Predicate"));
		}
		if (rmapStmtObject==null){
			throw new RMapException (new IllegalArgumentException("Null StatementOobject"));
		}
		Statement rmapStmtStatement = ORAdapter.getValueFactory().createStatement(
					rmapStmtSubject, rmapStmtPredicate, rmapStmtObject, rmapStmtContext);
		return rmapStmtStatement;
	}
	/**
	 * @return the rmapStmtSubject
	 */
	public Resource getRmapStmtSubject() {
		return this.rmapStmtStatement.getSubject();
	}
	/**
	 * @return the rmapStmtPredicate
	 */
	public URI getRmapStmtPredicate() {
		return this.rmapStmtStatement.getPredicate();
	}
	/**
	 * @return the rmapStmtObject
	 */
	public Value getRmapStmtObject() {
		return this.rmapStmtStatement.getObject();
	}
	/**
	 * @return the rmapStmtContext
	 */
	public Resource getRmapStmtContext() {	
		return this.rmapStmtStatement.getContext();
	}
	/**
	 * Accessor 
	 * @return
	 */
	public Statement getRmapStmtStatement() {
		return rmapStmtStatement;
	}
	/**
	 * Set Statement object.  If Statement's context is not null, then set this ORMapStatement's id to the context
	 * @param statement
	 * @throws URISyntaxException
	 */
	public void setRmapStmtStatement(Statement statement) throws RMapException {
		this.rmapStmtStatement = statement;
		if ((statement.getContext() != null) && 
				(statement.getContext() instanceof URI)){
			URI context = (URI)statement.getContext();
			java.net.URI uri = ORAdapter.openRdfUri2URI(context);
			this.setId(uri);
		}
	}
	@Override
	public Model getAsModel() throws RMapException {
		Model stmtModel = new LinkedHashModel();
		stmtModel.add(typeStatement);
		stmtModel.add(rmapStmtStatement.getSubject(), rmapStmtStatement.getPredicate(), rmapStmtStatement.getObject());
		return stmtModel;
	}

}

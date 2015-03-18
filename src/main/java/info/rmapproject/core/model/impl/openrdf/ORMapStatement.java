/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.net.URISyntaxException;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.statement.RMapStatement;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

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

	protected Statement subjectStatement;
	protected Statement predicateStatement;
	protected Statement objectStatement;

	/**
	 * @throws RMapException
	 */
	protected ORMapStatement() throws RMapException {
		super();
	}
	/**
	 * Instantiate ORMapStatement from reified statement triples
	 * @param subject
	 * @param predicate
	 * @param object
	 * @throws RMapException
	 */
	public ORMapStatement(Statement subject, Statement predicate, Statement object)
	throws RMapException {
		this();
		if (subject==null || predicate==null || object==null){
			throw new RMapException("Null statement object passed as parameter");
		}
		// all Statements should have same subject, which is id of statement
		if (!(subject.getSubject().equals(predicate.getSubject()) &&
				predicate.getSubject().equals(object.getSubject()))){
			throw new RMapException("Statements do not have same ID");
		}
		if (subject.getContext()== null){
			throw new RMapException("Null subject context");
		}
		if (predicate.getContext()== null){
			throw new RMapException("Null subject context");
		}
		if (object.getContext()== null){
			throw new RMapException("Null subject context");
		}
		if (!(subject.getContext().equals(predicate.getContext()) &&
				predicate.getContext().equals(object.getContext()))){
			throw new RMapException("Statements do not have same Context");
		}
		if (!(subject.getObject() instanceof Resource)){
			throw new RMapException("subject value is not a resource");
		}
		if (!(predicate.getObject() instanceof URI)){
			throw new RMapException("predicate value is not a URI");
		}
		Resource subjId = subject.getSubject();
		if (!(subjId instanceof URI)){
			throw new RMapException ("Statement has non-URI id");
		}
		this.id = ORAdapter.openRdfUri2URI((URI)subject.getSubject());
		this.subjectStatement = subject;
		this.predicateStatement = predicate;
		this.objectStatement = object;
		this.typeStatement = this.getValueFactory().createStatement(subjId, 
				RDF.TYPE, RMAP.STATEMENT, subject.getContext());
			
	}
	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapStatement#getSubject()
	 */
	public RMapResource getSubject() {
		RMapResource subject = null;
		Value value = this.subjectStatement.getObject();
		Resource rValue = (Resource)value;
		try {
			subject = ORAdapter.openRdfResource2NonLiteral(rValue);
		} catch (IllegalArgumentException | URISyntaxException e) {
			throw new RMapException(e);
		}
		return subject;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapStatement#getPredicate()
	 */
	public RMapUri getPredicate() {
		RMapUri predicate = null;
		Value value = this.predicateStatement.getObject();
		URI uValue = (URI)value;
		predicate = ORAdapter.openRdfUri2RMapUri(uValue);
		return predicate;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.RMapStatement#getObject()
	 */
	public RMapValue getObject() {
		RMapValue object = null;
		try {
			object = ORAdapter.openRdfValue2RMapValue(
					this.objectStatement.getObject());
		}
		catch (IllegalArgumentException | URISyntaxException e){
			throw new RMapException(e);
		}
		return object;
	}

	@Override
	public Model getAsModel() throws RMapException {
		Model stmtModel = new LinkedHashModel();
		stmtModel.add(typeStatement);
		stmtModel.add(subjectStatement);
		stmtModel.add(predicateStatement);
		stmtModel.add(objectStatement);
		return stmtModel;
	}

	/**
	 * @return the subjectStatement
	 */
	public Statement getSubjectStatement() {
		return subjectStatement;
	}

	/**
	 * @return the predicateStatement
	 */
	public Statement getPredicateStatement() {
		return predicateStatement;
	}

	/**
	 * @return the objectStatement
	 */
	public Statement getObjectStatement() {
		return objectStatement;
	}


}

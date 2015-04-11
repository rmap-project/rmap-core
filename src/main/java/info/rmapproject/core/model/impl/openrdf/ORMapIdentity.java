/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.collections4.Predicate;
import org.openrdf.model.BNode;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.idvalidator.RMapIdPredicate;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.agent.RMapIdentity;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

/**
 * @author smorrissey
 *
 */
public class ORMapIdentity extends ORMapObject implements RMapIdentity {
	protected URI context;
	protected Statement localPartStmt;
	protected Statement idProviderStmt;
	protected Statement creatorStmt;
	/**
	 * @throws RMapException
	 */
	protected ORMapIdentity() throws RMapException {
		super();	
		this.context = ORAdapter.uri2OpenRdfUri(getId());	
		this.typeStatement = 
				this.getValueFactory().createStatement(this.context,RDF.TYPE,RMAP.IDENTITY,this.context);
	}
	/**
	 * 
	 * @param localPart
	 * @param creator
	 * @throws RMapException
	 */
	public ORMapIdentity(Value localPart, URI creator) throws RMapException {
		this();
		if (localPart==null || localPart.stringValue().length()==0){
			throw new RMapException ("null or empty local part");
		}
		if (creator==null){
			throw new RMapException ("null creator id");
		}
		if (localPart instanceof URI){
			URI localURI = (URI)localPart;
			this.id = ORAdapter.openRdfUri2URI(localURI);
			this.context = localURI;
		}
		Statement stmt = this.getValueFactory().createStatement(this.context, RMAP.IDLOCALPART, localPart,this.context);
		this.localPartStmt = stmt;
		this.setCreatorStmt(creator);
	}
	/**
	 * 
	 * @param localPart
	 * @param idProvider
	 * @param creator
	 * @throws RMapException
	 */
	public ORMapIdentity (Value localPart, Value idProvider, URI creator) throws RMapException {
		this(localPart, creator);
		if (idProvider == null){
			throw new RMapException ("Null idProvider"); 
		}
		Statement stmt = this.getValueFactory().createStatement(this.context, RMAP.IDPROVIDERID, idProvider,this.context);
		this.idProviderStmt = stmt;
	}
	/**
	 * 
	 * @param idStmts
	 * @param creator
	 */
	public ORMapIdentity(List<Statement> stmts, URI creatorId) {
		this();
		if (stmts==null){
			throw new RMapException("Null statement list");
		}
		boolean typeFound = false;
		Value incomingIdValue = null;
		Resource idIncomingIdResource = null;
		for (Statement stmt:stmts){
			Resource subject = stmt.getSubject();
			URI predicate = stmt.getPredicate();
			Value object = stmt.getObject();
			if (predicate.equals(RDF.TYPE)){
				if (object.equals(RMAP.IDENTITY)){
					typeFound = true;
					incomingIdValue = subject;
					idIncomingIdResource = subject;
					break;
				}
				continue;
			}
		}
		if (!typeFound || incomingIdValue==null){
			throw new RMapException ("no Identity type statement found in statement list");
		}
		// creator should not be null if method invoked from service,
				// can be null when creating ORMapAgent from triplestore statements,
				// so we have to check at the end and make sure there is a non-null creator
		if (creatorId!=null){
			this.setCreatorStmt(creatorId);
		}	
		boolean isBNode = (idIncomingIdResource instanceof BNode);
		// if bNode, we don't care what the value is; if it's a URI, keep it as this object's id		
		if (!isBNode){
			boolean isRmapId = false;
			URI idURI = (URI)idIncomingIdResource;
			java.net.URI jURI = ORAdapter.openRdfUri2URI(idURI);
			// check to make sure Identity has an RMAP id not a local one
			try {
				Predicate<Object> predicate = RMapIdPredicate.rmapIdPredicate();
				isRmapId  = predicate.evaluate(jURI);
			} 
			catch (Exception e) {
				throw new RMapException ("Unable to validate Identity id " + 
						idURI, e);
			}				
			if (isRmapId){
			// use incoming id				
				this.id = jURI;
				this.context = idURI; 
				this.typeStatement = this.getValueFactory().createStatement
						(this.context,RDF.TYPE,RMAP.IDENTITY,this.context);			
			}
		}
		for (Statement stmt:stmts){
			Resource subject = stmt.getSubject();
			URI predicate = stmt.getPredicate();
			Value object = stmt.getObject();
			if (subject.stringValue().equals(incomingIdValue.stringValue())){
				subject = this.context;
			}
			// ONLY accepting statements with Identity id as subject
			else {
				throw new RMapException 
				("Identity statement subject is not equal to supplied Identity id: " +
				  subject.stringValue());
			}
			if (predicate.equals(RDF.TYPE)){
				if (object.equals(RMAP.IDENTITY)){
					this.typeStatement= this.getValueFactory().createStatement(
							subject, predicate, object, this.context);
					continue;
				}
				else {
					throw new RMapException("Unrecognized type value: " + object.stringValue());
				}
			}
			if (predicate.equals(DCTERMS.CREATOR)){
				Statement creatorStmt = this.getValueFactory().createStatement(
						subject, predicate, object, this.context);
				this.creatorStmt = creatorStmt;
				continue;
			}
			if (predicate.equals(RMAP.IDLOCALPART)){
				Statement localStmt = this.getValueFactory().createStatement(
						subject, predicate, object, this.context);
				this.localPartStmt = localStmt;
				continue;
			}
			if (predicate.equals(RMAP.IDPROVIDERID)){
				Statement provStmt = this.getValueFactory().createStatement(
						subject, predicate, object, this.context);
				this.idProviderStmt = provStmt;
				continue;
			}
			throw new RMapException("Unrecognized predicate: " + predicate.stringValue());
		}
		if (creatorStmt==null){
			throw new RMapException ("No creator for identity");
		}
		if (localPartStmt == null){
			throw new RMapException ("No local part for identity");
		}
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.agent.RMapIdentity#getIdentity()
	 */
	@Override
	public RMapValue getIdentityLocalPart() throws RMapException {
		Value lp = this.localPartStmt.getObject();
		RMapValue value;
		try {
			value = ORAdapter.openRdfValue2RMapValue(lp);
		} catch (IllegalArgumentException | URISyntaxException e) {
			e.printStackTrace();
			throw new RMapException (e);
		}
		return value;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.agent.RMapIdentity#getIdProvider()
	 */
	@Override
	public RMapUri getIdentityProviderId() throws RMapException {
		RMapUri uri = null;
		if (this.idProviderStmt!= null){
			Value object = this.idProviderStmt.getObject();
			if (object instanceof URI){
				URI ipUri = (URI)object;
				uri = ORAdapter.openRdfUri2RMapUri(ipUri);
			}
		}
		return uri;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.impl.openrdf.ORMapObject#getAsModel()
	 */
	@Override
	public Model getAsModel() throws RMapException {
		Model model = new LinkedHashModel();
		model.add(typeStatement);
		model.add(creatorStmt);
		model.add(this.localPartStmt);
		if (this.idProviderStmt!=null){
			model.add(this.idProviderStmt);
		}
		return model;
	}

	/**
	 * @return the localPartStmt
	 */
	public Statement getLocalPartStmt() {
		return localPartStmt;
	}

	/**
	 * @return the idProviderStmt
	 */
	public Statement getIdProviderStmt() {
		return idProviderStmt;
	}
	/**
	 * 
	 * @param creator
	 */
	protected void setCreatorStmt (URI creator){
		Statement stmt = this.getValueFactory().createStatement(this.context, 
				DCTERMS.CREATOR, creator, this.context);
		this.creatorStmt = stmt;
	}
	/**
	 * @return the creatorStmt
	 */
	public Statement getCreatorStmt() {
		return creatorStmt;
	}

}

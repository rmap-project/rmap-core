/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.net.URISyntaxException;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.RDF;

import info.rmapproject.core.exception.RMapException;
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
	/**
	 * @throws RMapException
	 */
	protected ORMapIdentity() throws RMapException {
		super();	
		this.context = ORAdapter.uri2OpenRdfUri(getId());	
		this.typeStatement = 
				this.getValueFactory().createStatement(this.context,RDF.TYPE,RMAP.IDENTITY,this.context);
	}
	
	public ORMapIdentity(Value localPart) throws RMapException {
		this();
		if (localPart==null || localPart.stringValue().length()==0){
			throw new RMapException ("null or empty local part");
		}
		if (localPart instanceof URI){
			URI localURI = (URI)localPart;
			this.id = ORAdapter.openRdfUri2URI(localURI);
			this.context = localURI;
		}
		Statement stmt = this.getValueFactory().createStatement(this.context, RMAP.IDLOCALPART, localPart,this.context);
		this.localPartStmt = stmt;
	}
	
	public ORMapIdentity (Value localPart, URI idProvider) throws RMapException {
		this(localPart);
		if (idProvider == null){
			throw new RMapException ("Null idProvider URI"); 
		}
		Statement stmt = this.getValueFactory().createStatement(this.context, RMAP.IDPROVIDERID, idProvider,this.context);
		this.idProviderStmt = stmt;
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

}

/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.RDF;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.agent.RMapIdentityProvider;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

/**
 * @author smorrissey
 *
 */
public class ORMapIdentityProvider extends ORMapObject implements
		RMapIdentityProvider {

	protected URI context;
	protected Statement idProviderConfigStmt;
	
	/**
	 * @throws RMapException
	 */
	protected ORMapIdentityProvider() throws RMapException {
		super();
		this.context = ORAdapter.uri2OpenRdfUri(getId());	
		this.typeStatement = 
				this.getValueFactory().createStatement(this.context,RDF.TYPE,RMAP.IDENTITYPROVIDER,this.context);
	}
	
	public ORMapIdentityProvider(URI configId) throws RMapException {
		this();
		this.idProviderConfigStmt =
				this.getValueFactory().createStatement(this.context, RMAP.IDENTITYCONFIGID, configId, this.context);
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.impl.openrdf.ORMapObject#getAsModel()
	 */
	@Override
	public Model getAsModel() throws RMapException {
		Model model = new LinkedHashModel();
		model.add(typeStatement);
		model.add(this.idProviderConfigStmt);
		return model;
	}

	@Override
	public RMapUri getConfigurationId() throws RMapException {
		RMapUri rUri = null;
		Value object = this.idProviderConfigStmt.getObject();
		if (object instanceof URI){
			URI uri = (URI)object;
			rUri = ORAdapter.openRdfUri2RMapUri(uri);
		}
		return rUri;
	}

}

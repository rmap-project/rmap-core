/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.RepositoryException;

import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapIdentityNotFoundException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.idvalidator.PreferredIdValidator;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapIdentity;
import info.rmapproject.core.model.impl.openrdf.ORMapProfile;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

/**
 * @author smorrissey
 *
 */
public class ORMapIdentityMgr extends ORMapObjectMgr {

	/**
	 * 
	 */
	public ORMapIdentityMgr() {
		super();
	}
	/**
	 * 
	 * @param id
	 * @param ts
	 * @return
	 * @throws RMapIdentityNotFoundException
	 */
	public ORMapIdentity read(URI id, SesameTriplestore ts) 
	throws RMapIdentityNotFoundException {;
		if (id == null){
			throw new RMapException("null Identity id");
		}
		if (ts==null){
			throw new RMapException("null triplestore");
		}
		
		if (!(this.isIdentityId(id, ts))){
			throw new RMapAgentNotFoundException("Not an identity: " + id.stringValue());
		}
		List<Statement> idStmts = null;
		try {
			idStmts = this.getNamedGraph(id, ts);	
		}
		catch (RMapObjectNotFoundException e) {
			throw new RMapIdentityNotFoundException ("No Identity found with id " + id.toString(), e);
		}
		ORMapIdentity identity = new ORMapIdentity(idStmts, null);
		return identity;
	}
	/**
	 * 
	 * @param identity
	 * @param ts
	 * @return
	 */
	public List<Statement> makeIdentityStatements(
			ORMapIdentity identity, SesameTriplestore ts) {
		List<Statement> stmts = new ArrayList<Statement>();
		Model idModel = identity.getAsModel();
		for (Statement mstmt:idModel){
			Statement dmsmt = null;
			try {
				dmsmt = ts.getValueFactory().createStatement(mstmt.getSubject(), 
						mstmt.getPredicate(), mstmt.getObject());
				stmts.add(dmsmt);
			} catch (RepositoryException e) {
				e.printStackTrace();
				throw new RMapException (e);
			}			
		}
		return stmts;
	}


	
	public List<ORMapIdentity> createIdentitiesFromRelatedStmts(List<Statement> idStmts, URI systemAgent)
	throws RMapException {
		// for each identifier,  create identity(localpart, systemAgent)
		if (idStmts==null){
			throw new RMapException ("null idStmts");
		}
		if (systemAgent==null){
			throw new RMapException ("null system agent id");
		}
		List<ORMapIdentity>identities = new ArrayList<ORMapIdentity>();
		for (Statement stmt:idStmts){
			Value localPart = stmt.getObject();
			//TODO check to see if ID already exists with URI as local part; if so, reuse
			ORMapIdentity identity = new ORMapIdentity(localPart,systemAgent);
			identities.add(identity);
		}
		return identities;
	}
	/**
	 * 
	 * @param identity
	 * @param profile
	 * @throws RMapException
	 */
	public void addIdToProfile(ORMapIdentity identity, ORMapProfile profile) throws RMapException {
		URI idId = ORAdapter.uri2OpenRdfUri(identity.getId());
		profile.addIdentity(idId);
		return;
	}
	/**
	 * 
	 * @param preferredId
	 * @param profile
	 */
	public void addPreferredIdToProfile(URI preferredId, ORMapProfile profile) {
		if (preferredId != null && profile != null){
			RMapUri rUri = ORAdapter.openRdfUri2RMapUri(preferredId);
			profile.setPreferredIdentity(rUri);
		}
		return;
	}
	
	/**
	 * See if related identity statements for profile contain a preferred id
	 * @param idStmts
	 * @return URI of first preferred ID found, or null if none found
	 * @throws RMapException
	 */
	public URI getPreferredIdInRelatedStatements (List<Statement> idStmts) throws RMapException {
		URI id = null;
		for (Statement stmt:idStmts){
			Value object = stmt.getObject();
			java.net.URI objectUri = null;
			if (object instanceof URI){
				objectUri = ORAdapter.openRdfUri2URI((URI)object);
			}
			else {
				try {
					objectUri = new java.net.URI(object.stringValue());
				}
				catch (Exception e){}
			}
			if (objectUri==null){
				continue;
			}
			if (PreferredIdValidator.isPreferredAgentId(objectUri)){
				id = ORAdapter.uri2OpenRdfUri(objectUri);
				break;
			}
		}
		return id;
	}

	public ORMapIdentity getIdentityWithLocalPartUri(URI localpart, SesameTriplestore ts)
	throws RMapException {
		ORMapIdentity identity = null;
		if (localpart == null){
			throw new RMapException ("Null localpart uri");
		}
		if (ts ==null){
			throw new RMapException("null triplestore");
		}
		try {
			Statement stmt = ts.getStatementAnyContext(null, RMAP.IDLOCALPART, localpart);
			if (stmt != null){
				identity = this.read((URI)stmt.getSubject(), ts);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RMapException(e);
		}
		return identity;
	}
	

}
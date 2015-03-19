/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapProfile;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

/**
 * @author smorrissey
 *
 */
public class ORMapProfileMgr extends ORMapObjectMgr {

	/**
	 * 
	 */
	public ORMapProfileMgr() {
		super();
	}
	/**
	 * 
	 * @param profileId
	 * @param ts
	 * @return
	 * @throws RMapObjectNotFoundException
	 * @throws RMapException
	 */
	public ORMapProfile readProfile(URI profileId, SesameTriplestore ts)
	throws RMapObjectNotFoundException, RMapException {				
		if (profileId == null){
			throw new RMapException("null profileId");
		}
		if (ts==null){
			throw new RMapException("null triplestore");
		}
		
		if (!(this.isProfileId(profileId, ts))){
			throw new RMapObjectNotFoundException("Not a profile ID: " + profileId.stringValue());
		}

		List<Statement> stmts = this.getNamedGraph(profileId, ts);	
		ORMapProfile profile = new ORMapProfile(stmts);
		return profile;
	}
	
	/**
	 * 
	 * @param agentId
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getRelatedProfiles(URI agentId, SesameTriplestore ts) throws RMapException {
		if (agentId==null){
			throw new RMapException("null agent id");
		}
		List<Statement> stmts  = null;
		try {
			stmts = ts.getStatementsAnyContext(null, RMAP.DESCRIBES_AGENT, agentId,true);
		} catch (Exception e) {
			throw new RMapException (e);
		}		
		List<URI> profiles = new ArrayList<URI>();
		for (Statement stmt: stmts){
			Resource subject = stmt.getSubject();
			if (!(subject instanceof URI)) {
				continue;
			}
			URI uri = (URI)subject;
			if (this.isProfileId(uri, ts)){
				profiles.add(uri);
			}
		}		
		return profiles;
	} 
	
	/**
	 * 
	 * @param profile
	 * @param ts
	 * @throws RMapException
	 */
	public void createProfileTriples (ORMapProfile profile, SesameTriplestore ts) 
	throws RMapException {
		Model model = profile.getAsModel();
		Iterator<Statement> iterator = model.iterator();
		while (iterator.hasNext()){
			Statement stmt = iterator.next();
			this.createTriple(ts, stmt);
		}
		return;
	}
	
	/**
	 * 
	 * @param suppliedId
	 * @param agentId
	 * @param creator
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public URI createSuppliedIdProfile(URI suppliedId, URI agentId, URI creator, 
			SesameTriplestore ts) 
	throws RMapException {
		if (suppliedId == null){
			throw new RMapException ("null suppliedId");
		}
		if (agentId==null){
			throw new RMapException ("Null agentId");
		}
		if (creator==null){
			throw new RMapException ("null creator");
		}
		if (ts == null){
			throw new RMapException ("Null triplestore");
		}
		ORMapProfile profile = new ORMapProfile(agentId, creator);
		profile.addIdentity(suppliedId);
		this.createProfileTriples(profile, ts);
		URI uri = ORAdapter.uri2OpenRdfUri(profile.getId());
		return uri;
	}
}

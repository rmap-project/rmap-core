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
import org.openrdf.repository.RepositoryException;

/**
 * @author smorrissey
 *
 */
public class ORMapProfileMgr extends ORMapObjectMgr {

	/**
	 * Constructor
	 */
	public ORMapProfileMgr() {
		super();
	}
	/**
	 * Retrieve Profile from triplestore
	 * @param profileId ID of profile in triplestore
	 * @param ts
	 * @return Profile instantiated from triplestore
	 * @throws RMapObjectNotFoundException if no named graph corresponding to id exists in triplestore
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
		ORMapProfile profile = new ORMapProfile(stmts,null);
		return profile;
	}
	
	/**
	 * Get ids of all profiles related to an Agent id
	 * @param agentId URI of Agent
	 * @param ts
	 * @return (Possibly emtpy) list of IDS of all profiles whose parent agent has id agentId
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
	 * Create triple statements that represent a profile and insert into triplestore
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
	 * Create new Profile in triplestore
	 * @param suppliedId ID to be added to new Profile's identities
	 * @param parentAgentId id of new Profile's parent Agent
	 * @param creator id of Agent who is creating this profile
	 * @param ts
	 * @return id of new Profile
	 * @throws RMapException
	 */
	public URI createSuppliedIdProfile(URI suppliedId, URI parentAgentId, URI systemAgent, 
			SesameTriplestore ts) 
	throws RMapException {
		if (suppliedId == null){
			throw new RMapException ("null suppliedId");
		}
		if (parentAgentId==null){
			throw new RMapException ("Null agentId");
		}
		if (systemAgent==null){
			throw new RMapException ("null systemAgent");
		}
		if (ts == null){
			throw new RMapException ("Null triplestore");
		}
		ORMapProfile profile = new ORMapProfile(parentAgentId, systemAgent);
		profile.addIdentity(suppliedId);
		this.createProfileTriples(profile, ts);
		URI uri = ORAdapter.uri2OpenRdfUri(profile.getId());
		return uri;
	}
	/**
	 * Instantiate a new ORMapProfile object (but not its triplestore statements)
	 * @param suppliedId URI that will be one of new Profile's identities
	 * @param parentAgentId URI of Agent that is new Profile's parent Agent
	 * @param systemAgent URI of Agent that is new Profile's creator
	 * @param ts
	 * @return new ORMapProfile
	 * @throws RMapException
	 */
	public ORMapProfile createProfile (URI suppliedId, URI parentAgentId, URI systemAgent, 
			SesameTriplestore ts) throws RMapException {
		if (suppliedId == null){
			throw new RMapException ("null suppliedId");
		}
		if (parentAgentId==null){
			throw new RMapException ("Null agentId");
		}
		if (systemAgent==null){
			throw new RMapException ("null systemAgent");
		}
		if (ts == null){
			throw new RMapException ("Null triplestore");
		}
		ORMapProfile profile = new ORMapProfile(parentAgentId, systemAgent);
		profile.addIdentity(suppliedId);
		return profile;
	}
	/**
	 * Create list of statements (without a context) for profile-related statements in DiSCO's related statements
	 * @param profile Profile whose representation as statements we are creating
	 * @param ts
	 * @return List of Statements without context representing Pofile
	 * @throws RMapException
	 */
	public List<Statement> makeProfileStatments (ORMapProfile profile, SesameTriplestore ts) throws RMapException {
		List<Statement> stmts = new ArrayList<Statement>();
		Model profileModel = profile.getAsModel();
		// add the new profile statements (NOT including context, which will need to be DiSCO context) to new related statements
		for (Statement pstmt:profileModel){
			Statement dpstmt;
			try {
				dpstmt = ts.getValueFactory().createStatement(pstmt.getSubject(), pstmt.getPredicate(), pstmt.getObject());
				stmts.add(dpstmt);
			} catch (RepositoryException e) {
				e.printStackTrace();
				throw new RMapException (e);
			}							
		}	
		return stmts;
	}
	/**
	 * Determine if URI is one of the local identities of any profile
	 * @param idUri
	 * @param ts
	 * @return true if ID is among identities of any profile; otherwise false
	 */
	public boolean isProfileIdentity(URI idUri, SesameTriplestore ts) throws RMapException {
		boolean isPid = false;
		Statement idStmt = null;
		try {
			idStmt = ts.getStatementAnyContext(null, RMAP.PROFILE_ID_BY, idUri);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RMapException (e);
		}
		if (idStmt != null){
			Resource subject = idStmt.getSubject();
			if (subject instanceof URI){
				URI profileId = (URI)subject;
				isPid = this.isProfileId(profileId, ts);
			}
		}
		return isPid;
	}
	public ORMapProfile createProfile(
			URI agentUri, Model profileStmtsModel, URI systemAgent,
			SesameTriplestore ts) {
		// TODO Auto-generated method stub
		// TODO be sure to create profile triples
		// TODO be sure to create identity triples
		return null;
	}
	/**
	 * Get identifier of a Profile's parent Agent
	 * @param profileId ID of Profile
	 * @param ts
	 * @return ID of parent Agent
	 * @throws RMapObjectNotFoundException
	 * @throws RMapException
	 */
	public URI getParentAgent(URI profileId, SesameTriplestore ts) 
			throws RMapObjectNotFoundException, RMapException {
		URI agentId = null;
		ORMapProfile profile = null;
		profile = this.readProfile(profileId, ts);
		agentId = ORAdapter.rMapUri2OpenRdfUri(profile.getParentAgentId());
		return agentId;
	}
	/**
	 * Retrieve Profile from triplestore that contains URI in its identities
	 * @param idUri identity URI to be matched
	 * @param ts
	 * @return Profile with URI in identity, or null if none found
	 * @throws RMapObjectNotFoundException
	 * @throws RMapException 
	 */
	public ORMapProfile getProfileFromIdentity(URI idUri, SesameTriplestore ts)
	throws RMapObjectNotFoundException, RMapException {
		ORMapProfile profile = null;
		Statement idStmt = null;
		try {
			idStmt = ts.getStatementAnyContext(null, RMAP.PROFILE_ID_BY, idUri);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RMapException (e);
		}
		if (idStmt == null){
			throw new RMapObjectNotFoundException ("no profile with identity " + idUri.stringValue());
		}
		Resource subject = idStmt.getSubject();
		if (subject instanceof URI){
			URI profileId = (URI)subject;
			profile = this.readProfile(profileId, ts);
		}
		return profile;
	}
}

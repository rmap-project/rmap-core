/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import info.rmapproject.core.controlledlist.IdPredicate;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapProfileNotFoundException;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapProfile;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
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
	 * @throws RMapProfileNotFoundException if no named graph corresponding to id exists in triplestore
	 * @throws RMapException
	 */
	public ORMapProfile readProfile(URI profileId, SesameTriplestore ts)
	throws RMapProfileNotFoundException, RMapException {				
		if (profileId == null){
			throw new RMapException("null profileId");
		}
		if (ts==null){
			throw new RMapException("null triplestore");
		}
		
		if (!(this.isProfileId(profileId, ts))){
			throw new RMapProfileNotFoundException("Not a profile ID: " + profileId.stringValue());
		}

		List<Statement> stmts = null;
		try {
			stmts = this.getNamedGraph(profileId, ts);	
		}
		catch (RMapObjectNotFoundException e) {
			throw new RMapProfileNotFoundException("No profile found with id " + profileId.stringValue(),e);
		}
		ORMapProfile profile = new ORMapProfile(stmts,null);
		return profile;
	}
	/**
	 * 
	 * @param profile
	 * @param ts
	 * @return
	 * @throws RMapException
	 */
	public URI createProfile (ORMapProfile profile, SesameTriplestore ts)
	throws RMapException {
		if (profile ==null){
			throw new RMapException ("Null profile");
		}
		if (ts==null){
			throw new RMapException("Null triplestore");
		}
		URI profileId = profile.getContext();
		this.createProfileTriples(profile, ts);
		return profileId;
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
	 * @param parentAgentId URI of Agent that is new Profile's parent Agent
	 * @param systemAgent URI of Agent that is new Profile's creator
	 * @param suppliedId URI that will be one of new Profile's identities
	 * @return new ORMapProfile
	 * @throws RMapException
	 */
	public ORMapProfile createProfileObject (URI parentAgentId, URI systemAgent, URI suppliedId) throws RMapException {
		if (parentAgentId==null){
			throw new RMapException ("Null agentId");
		}
		if (systemAgent==null){
			throw new RMapException ("null systemAgent");
		}
		ORMapProfile profile = new ORMapProfile(parentAgentId, systemAgent);
		if (suppliedId != null){
			profile.addIdentity(suppliedId);
		}
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
	/**
	 * Filter related statements from DiSCO to create statement set and use to create new Profile
	 * @param agentUri URI of parent Agent
	 * @param relatedStmtsModel Statements from DiSCO's related statement pertaining to Profile
	 * @param systemAgent creator of Agent
	 * @param ts TripleStore
	 * @param crURI URI or Bnode in related statements to be replaced with Profile id
	 * @return new Profile
	 * @throws RMapException
	 */
	public ORMapProfile createProfileFromRelatedStmts( URI agentUri, Model relatedStmtsModel, URI systemAgent,
			SesameTriplestore ts, Resource crURI)
	throws RMapException {
		List<Statement> replacedIdStmts= this.replaceIdInModel(relatedStmtsModel, crURI, systemAgent, ts);
		ORMapProfile profile = new ORMapProfile(replacedIdStmts, systemAgent);
		return profile;
	}
	
	/**
	 * Filters statements to replace bnode or URI with bnode value to be passed to Profile constructor
	 * Creates rdf:type and rmap:describes statements if needes
	 * @param filterProfileModel Model created from DiSCO related Statements pertaining to profile
	 * @param crURI URI or bnode in original statements to be filtered
	 * @param agentURI URI of profiles parent agent
	 * @param ts
	 * @return List of filtered Statements
	 * @throws RMapException
	 */
	protected List<Statement> replaceIdInModel(Model filterProfileModel, Resource crURI, URI agentURI, SesameTriplestore ts) 
	throws RMapException {
		//TODO determine if need to filter for IDENTITY, IDPROVIDER, IDCONFIG statements
		List<Statement> newStmts = new ArrayList<Statement>();
		BNode bnode = null;
		boolean typeStmtFound = false;
		boolean parentAgentStmtFound = false;
		try {
			bnode = ts.getValueFactory().createBNode();
		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new RMapException (e);
		}
		for (Statement oldStmt:filterProfileModel){
			Resource newSubject = null;
			Value newObject = null;
			Resource subject = oldStmt.getSubject();
			URI predicate = oldStmt.getPredicate();
			Value object = oldStmt.getObject();
			if (subject.equals(crURI)){
				newSubject = bnode;
				if (predicate.equals(RDF.TYPE)){
					if (object.equals(RMAP.PROFILE)){
						typeStmtFound = true;
					}
				}
				else if (predicate.equals(RMAP.DESCRIBES_AGENT)){
					if (object.equals(agentURI)){
						parentAgentStmtFound = true;
					}
					else {
						//bypass this statement - we are creating new profile
						continue;
					}
				}
			}
			else {
				newSubject = subject;
			}			
			if (object.equals(crURI)){
				newObject = bnode;
			}
			else {
				newObject = object;
			}
			Statement newStmt = null;
			try {
				newStmt = ts.getValueFactory().createStatement(newSubject, predicate, newObject);
			} catch (RepositoryException e) {
				e.printStackTrace();
				throw new RMapException (e);
			}
			newStmts.add(newStmt);			
		}
		if (!typeStmtFound){
			try {
				Statement typeStmt = ts.getValueFactory().createStatement(bnode, RDF.TYPE, RMAP.PROFILE);
				newStmts.add(typeStmt);
			} catch (RepositoryException e) {
				e.printStackTrace();
				throw new RMapException (e);
			}
		}
		if (!parentAgentStmtFound){
			try {
				Statement parentStmt = ts.getValueFactory().createStatement(bnode, RMAP.DESCRIBES_AGENT, agentURI);
				newStmts.add(parentStmt);
			} catch (RepositoryException e) {
				e.printStackTrace();
				throw new RMapException (e);
			}
		}
		return newStmts;
	}
	
	/**
	 * Get identifier of a Profile's parent Agent
	 * @param profileId ID of Profile
	 * @param ts
	 * @return ID of parent Agent
	 * @throws RMapProfileNotFoundException
	 * @throws RMapException
	 */
	public URI getParentAgentUri(URI profileId, SesameTriplestore ts) 
			throws RMapProfileNotFoundException, RMapException {
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
	 * @throws RMapProfileNotFoundException
	 * @throws RMapException 
	 */
	public ORMapProfile getProfileFromIdentity(URI idUri, SesameTriplestore ts)
	throws RMapProfileNotFoundException, RMapException {
		ORMapProfile profile = null;
		Statement idStmt = null;
		try {
			idStmt = ts.getStatementAnyContext(null, RMAP.PROFILE_ID_BY, idUri);
			if (idStmt==null){
				//see if identity expressed as literal
				String uriStr = idUri.stringValue();
				Value uriStrValue = ts.getValueFactory().createLiteral(uriStr);
				idStmt = ts.getStatementAnyContext(null, RMAP.PROFILE_ID_BY, uriStrValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RMapException (e);
		}
		if (idStmt == null){
			throw new RMapProfileNotFoundException ("no profile with identity " + idUri.stringValue());
		}
		Resource subject = idStmt.getSubject();
		if (subject instanceof URI){
			URI profileId = (URI)subject;
			profile = this.readProfile(profileId, ts);
		}
		return profile;
	}
	/**
	 * 
	 * @param relStmts
	 * @return
	 * @throws RMapException
	 */
	public List<Statement> getIdStmtsInRelatedStatments (Model relStmts) throws RMapException{
		List<Statement> idStmts = new ArrayList<Statement>();
		for (Statement stmt:relStmts){
			URI predicate = stmt.getPredicate();
			if (IdPredicate.isIdPredicate(ORAdapter.openRdfUri2URI(predicate))){
				idStmts.add(stmt);
			}
		}
		return idStmts;
	}
	


}

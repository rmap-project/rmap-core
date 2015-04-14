/**
 * 
 */
package info.rmapproject.core.rmapservice.impl.openrdf;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

/**
 * @author smorrissey
 *
 */
public class ORMapAgentMgrTest {

	protected String systemAgentId = "http://orcid.org/0000-0003-2069-1219";
	protected ORMapAgentMgr agentMgr;
	protected SesameTriplestore ts;
	protected URI systemAgentURI;
	protected String doi = "DOI:10.1080/13614576.2014.883935";
	protected URI doiURI;
	protected String mbox = "mailto:mary.smith@example.org";
	protected URI mboxURI;
	protected List<Statement> toBeAddedStmts;
	protected List<Statement> toBeDeletedStmts;
	protected List<URI> newObjects;
	protected URI textType;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		agentMgr = new ORMapAgentMgr();
		ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
		systemAgentURI = ts.getValueFactory().createURI(systemAgentId);
		doiURI = ts.getValueFactory().createURI(doi);
		mboxURI = ts.getValueFactory().createURI(mbox);
		toBeAddedStmts = new ArrayList<Statement>();
		toBeDeletedStmts = new ArrayList<Statement>();
		newObjects = new ArrayList<URI>();
		textType = ts.getValueFactory().createURI("http://purl.org/dc/dcmitype/Text");
	}

	/**
	 * Test method for {@link info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr#readAgent(org.openrdf.model.URI, info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore)}.
	 */
	@Test
	public void testReadAgent() {
		// Prime the pump by creating a system agent
		ORMapAgent systemAgent = agentMgr.createAgentObject(systemAgentURI, systemAgentURI, ts);
		URI agentId = systemAgent.getContext();
		assertTrue(agentMgr.isAgentId(agentId, ts));	
		RMapService service = RMapServiceFactoryIOC.getFactory().createService();
		try {
			service.createAgent(ORAdapter.openRdfUri2URI(agentId), systemAgent);
		} catch (RMapException | RMapDefectiveArgumentException e1) {
			e1.printStackTrace();
			fail();
		}
		URI newAgentURI = systemAgent.getContext();
		ORMapAgent readAgent = null;
		try {
			readAgent = agentMgr.readAgent(newAgentURI, ts);
			assertEquals(systemAgent.getRepresentationId().getStringValue(),readAgent.getRepresentationId().getStringValue());
			assertEquals(systemAgent.getCreator().getStringValue(), readAgent.getCreator().getStringValue());
			assertEquals(systemAgent.getType(), readAgent.getType());
		}
		catch (RMapAgentNotFoundException e){
			fail("agent not found");
		}
		catch (RMapException e){
			fail("exception");
		}
	}


}

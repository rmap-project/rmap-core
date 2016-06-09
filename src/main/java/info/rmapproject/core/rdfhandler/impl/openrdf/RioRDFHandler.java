package info.rmapproject.core.rdfhandler.impl.openrdf;


import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapTriple;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.model.impl.openrdf.ORMapEvent;
import info.rmapproject.core.model.impl.openrdf.ORMapEventCreation;
import info.rmapproject.core.model.impl.openrdf.ORMapEventDeletion;
import info.rmapproject.core.model.impl.openrdf.ORMapEventTombstone;
import info.rmapproject.core.model.impl.openrdf.ORMapEventUpdate;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;


public class RioRDFHandler implements RDFHandler {
		
	public RioRDFHandler() {}
	/**
	 * Convert Model of RMap object to an OutputStream of RDF
	 * @param model Model of RMap object to be converted
	 * @param rdfType RDF Format for serialization
	 * @return OutputStream containing RDF serialization of RMap object
	 * @throws RMapException
	 */
	public OutputStream convertStmtListToRDF(Model model, RDFType rdfType) 
	throws RMapException	{
		if (model==null){
			throw new RMapException("Null or empty Statement model");
		}
		if (rdfType==null){
			throw new RMapException("RDF format name null");
		}	
		RDFFormat rdfFormat = null;
		OutputStream bOut = new ByteArrayOutputStream();
		try {
			rdfFormat = this.getRDFFormatConstant(rdfType);
			Rio.write(model, bOut, rdfFormat);
		} catch (Exception e) {
			throw new RMapException("Exception thrown creating RDF from statement list",e);
		}
		return bOut;		
	}
	/**
	 * Deserialize RDF InputStream into a list of Statements
	 * @param rdfIn InputStream of RDF
	 * @param rdfType Format of RDF in InputStream
	 * @param baseUri  String with base URI of any relative URI in InputStream.
	 * @return List of Statements created from RDF InputStsream
	 * @throws RMapException if null parameters, or invalid rdfType, or error parsing stream
	 */
	public Set <Statement> convertRDFToStmtList(InputStream rdfIn, RDFType rdfType, String baseUri) 
	throws RMapException	{
		if (rdfIn==null){
			throw new RMapException("Null rdf input stream");
		}
		if (rdfType==null){
			throw new RMapException("Null rdf type");
		}
		Set <Statement> stmts = new HashSet<Statement>();
		RDFFormat rdfFormat = null;
		try {
			rdfFormat = this.getRDFFormatConstant(rdfType);
		} catch (Exception e1) {
			throw new RMapException("Unable to match rdfType: " + rdfType, e1);
		}
		RDFParser rdfParser = Rio.createParser(rdfFormat);	
		StatementCollector collector = new StatementCollector(stmts);		
		rdfParser.setRDFHandler(collector);
		try {
			rdfParser.parse(rdfIn, baseUri);
		} catch (RDFParseException | RDFHandlerException | IOException e) {
			throw new RMapException("Unable to parse input RDF: ",e);
		}		
		return stmts;
	}

	@Override
	public RMapDiSCO rdf2RMapDiSCO(InputStream rdfIn, RDFType rdfFormat, String baseUri)
			throws RMapException, RMapDefectiveArgumentException {
		Set <Statement> stmts = this.convertRDFToStmtList(rdfIn, rdfFormat, baseUri);
		ORMapDiSCO disco = new ORMapDiSCO(stmts);
		return disco;
	}
	
	@Override
	public RMapAgent rdf2RMapAgent(InputStream rdfIn, RDFType rdfFormat, String baseUri) 
			throws RMapException, RMapDefectiveArgumentException {
		Set <Statement> stmts = this.convertRDFToStmtList(rdfIn, rdfFormat, baseUri);
		ORMapAgent agent = new ORMapAgent(stmts);
		return agent;
	}
	
	@Override
	public OutputStream triples2Rdf(List<RMapTriple> triples, RDFType rdfFormat) throws RMapException, RMapDefectiveArgumentException	{
		if (triples == null){
			throw new RMapException("Null triple list");			
		}
		if (rdfFormat==null){
			throw new RMapException("null rdf format name");
		}
		Model model = new LinkedHashModel();		
		
		for (RMapTriple triple:triples){
			model.add(ORAdapter.rMapNonLiteral2OpenRdfResource(triple.getSubject()), 
						ORAdapter.rMapIri2OpenRdfIri(triple.getPredicate()), 
						ORAdapter.rMapValue2OpenRdfValue(triple.getObject()));
		}
		OutputStream rdf = this.convertStmtListToRDF(model, rdfFormat);
		return rdf;
	}

	@Override
	public OutputStream disco2Rdf(RMapDiSCO disco, RDFType rdfFormat)
			throws RMapException {
		if (disco==null){
			throw new RMapException("Null DiSCO");
		}
		if (rdfFormat==null){
			throw new RMapException("null rdf format name");
		}
		if (!(disco instanceof ORMapDiSCO)){
			throw new RMapException("RMapStatement not instance of ORMapDiSCO");
		}
		ORMapDiSCO orDisco = (ORMapDiSCO)disco;
		Model model = orDisco.getAsModel();
		OutputStream os = this.convertStmtListToRDF(model, rdfFormat);
		return os;
	}

	@Override
	public OutputStream event2Rdf(RMapEvent event, RDFType rdfFormat)
			throws RMapException {
		if (event==null){
			throw new RMapException("Null Event");
		}
		if (rdfFormat==null){
			throw new RMapException("null rdf format name");
		}
		if (!(event instanceof ORMapEvent)){
			throw new RMapException("RMapStatement not instance of ORMapEvent");
		}
		ORMapEvent orEvent = (ORMapEvent)event;
		Model model = null;
		if (orEvent instanceof ORMapEventCreation){
			model =((ORMapEventCreation)orEvent).getAsModel();
		}
		else if (orEvent instanceof ORMapEventUpdate){
			model =((ORMapEventUpdate)orEvent).getAsModel();
		}
		else if (orEvent instanceof ORMapEventTombstone){
			model =((ORMapEventTombstone)orEvent).getAsModel();
		}
		else if (orEvent instanceof ORMapEventDeletion){
			model =((ORMapEventDeletion)orEvent).getAsModel();
		}
		else {
			throw new RMapException("Unrecognized event type");
		}
		OutputStream os = this.convertStmtListToRDF(model, rdfFormat);
		return os;
	}

	@Override
	public OutputStream agent2Rdf(RMapAgent agent, RDFType rdfFormat)
			throws RMapException {
		if (agent==null){
			throw new RMapException("Null agent");
		}
		if (rdfFormat==null){
			throw new RMapException("null rdf format name");
		}
		if (!(agent instanceof ORMapAgent)){
			throw new RMapException("RMapStatement not instance of ORMapAgent");
		}
		ORMapAgent orAgent = (ORMapAgent)agent;
		Model model = orAgent.getAsModel();
		OutputStream os = this.convertStmtListToRDF(model, rdfFormat);
		return os;
	}

	public RDFFormat getRDFFormatConstant(RDFType rdfType) throws Exception	{
		RDFFormat rdfFormat = null;		
        switch (rdfType) {
            case RDFXML:  rdfFormat = RDFFormat.RDFXML;
                     break;
            case TURTLE:  rdfFormat = RDFFormat.TURTLE;
                     break;
            case JSONLD:  rdfFormat = RDFFormat.JSONLD;
                     break;
            default: rdfFormat = RDFFormat.TURTLE;
                     break;
        }
        return rdfFormat;
	
	}
}

package info.rmapproject.core.rdfhandler.impl.openrdf;


import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapAgent;
import info.rmapproject.core.model.RMapDiSCO;
import info.rmapproject.core.model.RMapEvent;
import info.rmapproject.core.model.RMapStatement;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.model.vocabulary.DC;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;


public class RioRDFHandler implements RDFHandler {
	
   static Map<String, String>nsDefaults = null;
   
   static{
	   nsDefaults = new HashMap<String,String>();
	   nsDefaults.put(RMAP.PREFIX, RMAP.NAMESPACE);
	   nsDefaults.put(PROV.PREFIX, PROV.NAMESPACE);
	   nsDefaults.put(DC.PREFIX, DC.NAMESPACE);
	   nsDefaults.put(DCTERMS.PREFIX, DCTERMS.NAMESPACE);
	   nsDefaults.put(FOAF.PREFIX, FOAF.NAMESPACE);
	   nsDefaults.put(RDF.PREFIX, RDF.NAMESPACE);
	   nsDefaults.put(RDFS.PREFIX, RDFS.NAMESPACE);
   }

	public RioRDFHandler() {}

	public String convertStmtListToRDF(List<Statement> stmts, Map<String,String> namespaces, String rdfType) throws Exception	{
		String rdf = null;		
		OutputStream bOut = new ByteArrayOutputStream();
		//TODO: do this format constant better!
		RDFFormat rdfFormat = RioUtil.getRDFFormatConstant(rdfType);
		RDFWriter rdfWriter = Rio.createWriter(rdfFormat, bOut);
		rdfWriter.startRDF();

		if (namespaces!=null){
			for(Iterator<Map.Entry<String, String>> it = namespaces.entrySet().iterator(); it.hasNext();) 	{
				Map.Entry<String, String> ns = it.next();
				String nsAbbrev = ns.getKey();
				String nsURL = ns.getValue();
				rdfWriter.handleNamespace(nsAbbrev,nsURL);
			}
		}
		
		if (stmts!=null){
			for (Statement stmt : stmts) {
				if (stmt != null) {
					rdfWriter.handleStatement(stmt);
				}
			}
		}

		rdfWriter.endRDF();
		rdf = bOut.toString();
		return rdf;		
	}

	public List <Statement> convertRDFToStmtList(String rdf, String rdfType) throws Exception	{
		List <Statement> stmts = new ArrayList<Statement>();
		
		RDFFormat rdfFormat = RioUtil.getRDFFormatConstant(rdfType);
		RDFParser rdfParser = Rio.createParser(rdfFormat);	
		InputStream stream = new ByteArrayInputStream(rdf.getBytes());
		StatementCollector collector = new StatementCollector(stmts);
		
		rdfParser.setRDFHandler(collector);
		rdfParser.parse(stream, "");
		
		return stmts;
	}

	@Override
	public RMapDiSCO rdf2RMapDiSCO(InputStream rdfIn, String baseUri)
			throws RMapException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream statement2Rdf(RMapStatement stmt, String rdfFormat)
			throws RMapException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream disco2Rdf(RMapDiSCO disco, String rdfFormat)
			throws RMapException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream event2Rdf(RMapEvent event, String rdfFormat)
			throws RMapException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream agent2Rdf(RMapAgent agent, String rdfFormat)
			throws RMapException {
		// TODO Auto-generated method stub
		return null;
	}
}

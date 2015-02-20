package info.rmapproject.core.rdfhandler.impl.openrdf;

import info.rmapproject.core.rdfhandler.RDFHandler;
//import info.rmapproject.core.utils.GeneralUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;


public class RioRDFHandler implements RDFHandler {

	public RioRDFHandler()	{}

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
}

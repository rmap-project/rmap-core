package info.rmapproject.core.rdfhandler;

import java.util.List;
import java.util.Map;

import org.openrdf.model.Statement;

public interface RDFHandler {
	
	public String convertStmtListToRDF(List<Statement> stmts, Map<String,String> namespaces, String rdfType) 
			throws Exception;
	public List <Statement> convertRDFToStmtList(String rdf, String rdfType) 
			throws Exception;

}

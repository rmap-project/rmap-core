/**
 * 
 */
package info.rmapproject.core.rdfhandler.impl.openrdf;

import org.openrdf.rio.RDFFormat;

/**
 * @author khansen
 *
 */
public class RioUtil {

	public static RDFFormat getRDFFormatConstant(String rdfType) throws Exception	{
		RDFFormat rdfFormat = null;
		if (rdfType == "RDFXML")	{
			rdfFormat = RDFFormat.RDFXML; 			
		}
		else if (rdfType == "JSONLD")	{
			rdfFormat = RDFFormat.JSONLD; 			
		}
		else if (rdfType == "RDFNQUADS")	{
			rdfFormat = RDFFormat.NQUADS; 		
		}
		else if (rdfType == "RDFJSON")	{
			rdfFormat = RDFFormat.RDFJSON; 		
		}
		else {
			rdfFormat = RDFFormat.RDFXML; 			
		}
		return rdfFormat;
	}

}

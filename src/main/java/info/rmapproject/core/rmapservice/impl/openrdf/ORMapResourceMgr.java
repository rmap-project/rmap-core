package info.rmapproject.core.rmapservice.impl.openrdf;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.impl.openrdf.ORMapDiSCO;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;

import org.openrdf.model.URI;

public class ORMapResourceMgr extends ORMapObjectMgr {

	/**
	 * 
	 */
	public ORMapResourceMgr() {
		super();
	}
	/**
	 * 
	 * @param stmtIds
	 * @param statusCode
	 * @return
	 * @throws RMapException
	 */
	public List<URI> getRelatedStmts(List<URI> stmtIds, RMapStatus statusCode) throws RMapException{
		// for each statement, if it is part of a DiSCO or Agent with matching status code,
		// add it to return list
		if (stmtIds==null || statusCode==null){
			throw new RMapException ("Null URI list or status code");
		}
		RMapService rservice = null;
		try {
			rservice = RMapServiceFactoryIOC.getFactory().createService();
		} catch (Exception e) {
			throw new RMapException (e);
		}
		ORMapService service = null;
		if (!(rservice instanceof ORMapService)){		
			throw new RMapException("Unable to instantiate OpenRDF service implmentation.");
		}
		service = (ORMapService)rservice;
		Set<URI> relatedStmts = new HashSet<URI>();
		for (URI stmtId:stmtIds){
			List<ORMapDiSCO> discos = service.getResourceAllRelatedDiSCOS(stmtId,statusCode);
			if (discos.size()>0){
				relatedStmts.add(stmtId);
	//TODO  should we add agents??			
			}
		}
		List<URI> rStmts = new ArrayList<URI>();
		rStmts.addAll(relatedStmts);
		return rStmts;
	}

}

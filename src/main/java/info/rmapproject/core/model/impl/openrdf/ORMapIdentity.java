/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import org.openrdf.model.Model;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.agent.RMapIdentity;
import info.rmapproject.core.model.agent.RMapIdentityProvider;

/**
 * @author smorrissey
 *
 */
public class ORMapIdentity extends ORMapObject implements RMapIdentity {

	/**
	 * @throws RMapException
	 */
	public ORMapIdentity() throws RMapException {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.agent.RMapIdentity#getIdentity()
	 */
	@Override
	public RMapUri getIdentity() throws RMapException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.agent.RMapIdentity#getIdProvider()
	 */
	@Override
	public RMapIdentityProvider getIdProvider() throws RMapException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.impl.openrdf.ORMapObject#getAsModel()
	 */
	@Override
	public Model getAsModel() throws RMapException {
		// TODO Auto-generated method stub
		return null;
	}

}

/**
 * 
 */
package info.rmapproject.core.model.impl.openrdf;

import java.util.List;
import java.util.Map;

import org.openrdf.model.Model;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.agent.RMapIdentity;
import info.rmapproject.core.model.agent.RMapProfile;

/**
 * @author smorrissey
 *
 */
public class ORMapProfile extends ORMapObject implements RMapProfile {

	/**
	 * @throws RMapException
	 */
	public ORMapProfile() throws RMapException {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.agent.RMapProfile#getProperties()
	 */
	@Override
	public Map<RMapUri, RMapValue> getProperties() throws RMapException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.agent.RMapProfile#getIdentities()
	 */
	@Override
	public List<RMapIdentity> getIdentities() throws RMapException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.core.model.agent.RMapProfile#getPreferredIdentity()
	 */
	@Override
	public RMapIdentity getPreferredIdentity() throws RMapException {
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

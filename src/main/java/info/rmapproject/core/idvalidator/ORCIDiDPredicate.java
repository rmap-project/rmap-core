/**
 * 
 */
package info.rmapproject.core.idvalidator;

import info.rmapproject.core.model.RMapUri;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.Predicate;

/**
 * Validate object to see if it is a URI expressing a syntactically valid ORCID iD
 * @see http://support.orcid.org/knowledgebase/articles/116780-structure-of-the-orcid-identifier
 * @author smorrissey
 * @param <T> object to be validated; expected to be either java.net.URI or RMapURI
 *
 */
public class ORCIDiDPredicate<T> implements Predicate<T> {

	public static final String ORCID_SCHEME = "http";
	public static final String ORCID_HOST = "orcid.org";
	public static final String ORCID_PATH_PATTERN = "\\d{4}-\\d{4}-\\d{4}-\\d{4}";
	public static Pattern p = null;
 
	 static{
		 p = Pattern.compile(ORCID_PATH_PATTERN);			 
	 }

	/**
	 * 
	 */
	public ORCIDiDPredicate() {
		super();
	}

	/**
	 * factory
	 * @return
	 */
	public static <T> Predicate<T> orcidIdPredicate() {
		return new ORCIDiDPredicate<T>();
	}
	
	@Override
	public boolean evaluate(T object) {
		boolean isOrcidId = false;
		URI uri = null;
		do {
			if (object==null){
				break;
			}
			if (object instanceof URI){
				uri = (URI)object;
			}
			else if (object instanceof RMapUri){
				uri = ((RMapUri)object).getIri();
			}
			if (uri != null){
				String scheme = uri.getScheme();
				if (scheme==null){
					break;
				}
				String host = uri.getHost();
				if (host==null){
					break;
				}
				String path = uri.getPath();
				if (path==null || path.length()<2){
					break;
				}
				boolean schemeMatches = scheme.equals(ORCID_SCHEME);
				boolean hostMatches = host.equals(ORCID_HOST);
				Matcher m = p.matcher(path.substring(1));
				boolean pathMatches = m.matches();
				isOrcidId = (schemeMatches && hostMatches && pathMatches);
			}
		}while (false);
		return isOrcidId;
	}

}

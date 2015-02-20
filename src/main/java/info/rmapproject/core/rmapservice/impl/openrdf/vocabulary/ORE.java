package info.rmapproject.core.rmapservice.impl.openrdf.vocabulary;

import org.openrdf.model.Namespace;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

public class ORE {

	/**
	 * OAIS-ORE elements namespace: http://www.openarchives.org/ore/terms/
	 */
	public static final String NAMESPACE = "http://www.openarchives.org/ore/terms/";

	/**
	 * Recommend prefix for the OAIS-ORE elements namespace: "ore"
	 */
	public static final String PREFIX = "ore";

	/**
	 * An immutable {@link Namespace} constant that represents the RMapProject
	 * namespace.
	 */
	public static final Namespace NS = new NamespaceImpl(PREFIX, NAMESPACE);

	
	/**
	 * ore:similarTo
	 */
	public static final URI SIMILARTO;

	/**
	 * ore:describes
	 */
	public static final URI DESCRIBES;

	/**
	 * ore:aggregation
	 */
	public static final URI AGGREGATION;
	
	static {
		final ValueFactory f = ValueFactoryImpl.getInstance();

		SIMILARTO = f.createURI(NAMESPACE, "similarTo");
		DESCRIBES = f.createURI(NAMESPACE, "describes");
		AGGREGATION = f.createURI(NAMESPACE, "Aggregation");
	}
	
	
}

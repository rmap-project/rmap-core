package info.rmapproject.core.rmapservice.impl.openrdf.vocabulary;

import org.openrdf.model.Namespace;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

public class BIBO {

	/**
	 * BIBO elements namespace: http://purl.org/ontology/bibo/
	 */
	public static final String NAMESPACE = "http://purl.org/ontology/bibo/";

	/**
	 * Recommend prefix for the BIBO elements namespace: "bibo"
	 */
	public static final String PREFIX = "bibo";

	/**
	 * An immutable {@link Namespace} constant that represents the RMapProject
	 * namespace.
	 */
	public static final Namespace NS = new NamespaceImpl(PREFIX, NAMESPACE);

	/**
	 * bibo:volume
	 */
	public static final URI VOLUME;
	
	/**
	 * bibo:Issue
	 */
	public static final URI ISSUE;

	/**
	 * bibo:pageStart
	 */
	public static final URI PAGESTART;

	/**
	 * bibo:pageEnd
	 */
	public static final URI PAGEEND;

	/**
	 * bibo:Article
	 */
	public static final URI ARTICLE;

	/**
	 * bibo:doi
	 */
	public static final URI DOI;
	
	static {
		final ValueFactory f = ValueFactoryImpl.getInstance();

		VOLUME = f.createURI(NAMESPACE, "volume");
		ISSUE = f.createURI(NAMESPACE, "Issue");
		PAGESTART = f.createURI(NAMESPACE, "pageStart");
		PAGEEND = f.createURI(NAMESPACE, "pageEnd");
		ARTICLE = f.createURI(NAMESPACE, "Article");
		DOI = f.createURI(NAMESPACE, "doi");
	}
	
	
}

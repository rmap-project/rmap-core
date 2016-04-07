package info.rmapproject.core.vocabulary.impl.openrdf;

import info.rmapproject.core.utils.Terms;

import org.openrdf.model.IRI;
import org.openrdf.model.Namespace;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleNamespace;
import org.openrdf.model.impl.SimpleValueFactory;

public class ORE {

	/**
	 * OAI-ORE elements namespace: http://www.openarchives.org/ore/terms/
	 */
	public static final String NAMESPACE = Terms.ORE_NAMESPACE;

	/**
	 * Recommend prefix for the OAI-ORE elements namespace: "ore"
	 */
	public static final String PREFIX = Terms.ORE_PREFIX;

	/**
	 * An immutable {@link Namespace} constant that represents the RMapProject
	 * namespace.
	 */
	public static final Namespace NS = new SimpleNamespace(PREFIX, NAMESPACE);

	
	/**
	 * ore:similarTo
	 */
	public static final IRI SIMILARTO;

	/**
	 * ore:describes
	 */
	public static final IRI DESCRIBES;

	/**
	 * ore:aggregation
	 */
	public static final IRI AGGREGATION;
	
	/**
	 * 
	 */
	public static final IRI AGGREGATES;
	static {
		final ValueFactory f = SimpleValueFactory.getInstance();

		SIMILARTO = f.createIRI(NAMESPACE, Terms.ORE_SIMILARTO);
		DESCRIBES = f.createIRI(NAMESPACE, Terms.ORE_DESCRIBES);
		AGGREGATION = f.createIRI(NAMESPACE, Terms.ORE_AGGREGATION);
		AGGREGATES = f.createIRI(NAMESPACE, Terms.ORE_AGGREGATES);
	}
	
	
}

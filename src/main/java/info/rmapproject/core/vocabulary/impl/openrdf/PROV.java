package info.rmapproject.core.vocabulary.impl.openrdf;

import info.rmapproject.core.utils.Terms;

import org.openrdf.model.IRI;
import org.openrdf.model.Namespace;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleNamespace;
import org.openrdf.model.impl.SimpleValueFactory;

public class PROV {

	/**
	 * PROV-O elements namespace: http://www.w3.org/ns/prov#
	 */
	public static final String NAMESPACE = Terms.PROV_NAMESPACE;

	/**
	 * Recommend prefix for the PROV-O elements namespace: "ore"
	 */
	public static final String PREFIX = Terms.PROV_PREFIX;

	/**
	 * An immutable {@link Namespace} constant that represents the RMapProject
	 * namespace.
	 */
	public static final Namespace NS = new SimpleNamespace(PREFIX, NAMESPACE);

	
	/**
	 * prov:Activity
	 */
	public static final IRI ACTIVITY;

	/**
	 * prov:startedAtTime
	 */
	public static final IRI STARTEDATTIME;

	/**
	 * prov:endedAtTime
	 */
	public static final IRI ENDEDATTIME;

	/**
	 * prov:wasGeneratedBy
	 */
	public static final IRI WASGENERATEDBY;
	/**
	 * prov:wasDerivedFrom
	 */
	public static final IRI WASDERIVEDFROM;
	/**
	 * prov:generated
	 */
	public static final IRI GENERATED;

	/**
	 * prov:hadActivity
	 */
	public static final IRI HADACTIVITY;

	/**
	 * prov:wasAssociatedWith
	 */
	public static final IRI WASASSOCIATEDWITH;
	
	/**
	 * prov:wasAttributedTo
	 */
	public static final IRI WASATTRIBUTEDTO;

	/**
	 * prov:has_provenance
	 */
	public static final IRI HAS_PROVENANCE;

	/**
	 * prov:used
	 */
	public static final IRI USED;

	
	static {
		final ValueFactory f = SimpleValueFactory.getInstance();

		ACTIVITY = f.createIRI(NAMESPACE, Terms.PROV_ACTIVITY);
		STARTEDATTIME = f.createIRI(NAMESPACE, Terms.PROV_STARTEDATTIME);
		ENDEDATTIME = f.createIRI(NAMESPACE, Terms.PROV_ENDEDATTIME);
		WASASSOCIATEDWITH = f.createIRI(NAMESPACE, Terms.PROV_WASASSOCIATEDWITH);
		WASGENERATEDBY = f.createIRI(NAMESPACE, Terms.PROV_WASGENERATEDBY);
		WASDERIVEDFROM = f.createIRI(NAMESPACE, Terms.PROV_WASDERIVEDFROM);
		GENERATED = f.createIRI(NAMESPACE, Terms.PROV_GENERATED);
		HADACTIVITY = f.createIRI(NAMESPACE, Terms.PROV_HADACTIVITY);
		WASATTRIBUTEDTO = f.createIRI(NAMESPACE, Terms.PROV_WASATTRIBUTEDTO);
		HAS_PROVENANCE = f.createIRI(NAMESPACE, Terms.PROV_HASPROVENANCE);
		USED = f.createIRI(NAMESPACE, Terms.PROV_USED);
	}
	
	
}

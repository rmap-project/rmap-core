package info.rmapproject.core.vocabulary.impl.openrdf;

import info.rmapproject.core.utils.Terms;

import org.openrdf.model.IRI;
import org.openrdf.model.Namespace;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleNamespace;
import org.openrdf.model.impl.SimpleValueFactory;

/**
 * The ORE ontology class implemented using openrdf model
 * 
 * @author khanson
 */
public class PROV {

	/**
	 * PROV-O elements namespace: http://www.w3.org/ns/prov#
	 */
	public static final String NAMESPACE = Terms.PROV_NAMESPACE;

	/** Recommend prefix for the PROV-O elements namespace: "ore". */
	public static final String PREFIX = Terms.PROV_PREFIX;

	/**
	 * An immutable {@link Namespace} constant that represents the RMapProject
	 * namespace.
	 */
	public static final Namespace NS = new SimpleNamespace(PREFIX, NAMESPACE);

	
	/** IRI for prov:Activity class. */
	public static final IRI ACTIVITY;

	/** IRI for prov:startedAtTime property. */
	public static final IRI STARTEDATTIME;

	/** IRI for prov:endedAtTime property. */
	public static final IRI ENDEDATTIME;

	/** IRI for prov:wasGeneratedBy property. */
	public static final IRI WASGENERATEDBY;
	
	/** IRI for prov:wasDerivedFrom property. */
	public static final IRI WASDERIVEDFROM;
	
	/** IRI for prov:generated property. */
	public static final IRI GENERATED;

	/** IRI for prov:hadActivity property. */
	public static final IRI HADACTIVITY;

	/** IRI for prov:wasAssociatedWith property. */
	public static final IRI WASASSOCIATEDWITH;
	
	/** IRI for prov:wasAttributedTo property. */
	public static final IRI WASATTRIBUTEDTO;

	/** IRI for prov:has_provenance property. */
	public static final IRI HAS_PROVENANCE;

	/** IRI for prov:used property. */
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

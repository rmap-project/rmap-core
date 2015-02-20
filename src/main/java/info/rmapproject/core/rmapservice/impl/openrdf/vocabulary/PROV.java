package info.rmapproject.core.rmapservice.impl.openrdf.vocabulary;

import org.openrdf.model.Namespace;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

public class PROV {

	/**
	 * PROV-O elements namespace: http://www.w3.org/ns/prov#
	 */
	public static final String NAMESPACE = "http://www.w3.org/ns/prov#";

	/**
	 * Recommend prefix for the PROV-O elements namespace: "ore"
	 */
	public static final String PREFIX = "prov";

	/**
	 * An immutable {@link Namespace} constant that represents the RMapProject
	 * namespace.
	 */
	public static final Namespace NS = new NamespaceImpl(PREFIX, NAMESPACE);

	
	/**
	 * prov:Activity
	 */
	public static final URI ACTIVITY;

	/**
	 * prov:startedAtTime
	 */
	public static final URI STARTEDATTIME;

	/**
	 * prov:endedAtTime
	 */
	public static final URI ENDEDATTIME;

	/**
	 * prov:wasGeneratedBy
	 */
	public static final URI WASGENERATEDBY;
	/**
	 * prov:wasDerivedFrom
	 */
	public static final URI WASDERIVEDFROM;
	/**
	 * prov:generated
	 */
	public static final URI GENERATED;

	/**
	 * prov:hadActivity
	 */
	public static final URI HADACTIVITY;

	/**
	 * prov:wasAssociatedWith
	 */
	public static final URI WASASSOCIATEDWITH;
	
	/**
	 * prov:wasAttributedTo
	 */
	public static final URI WASATTRIBUTEDTO;

	static {
		final ValueFactory f = ValueFactoryImpl.getInstance();

		ACTIVITY = f.createURI(NAMESPACE, "Activity");
		STARTEDATTIME = f.createURI(NAMESPACE, "startedAtTime");
		ENDEDATTIME = f.createURI(NAMESPACE, "endedAtTime");
		WASASSOCIATEDWITH = f.createURI(NAMESPACE, "wasAssociatedWith");
		WASGENERATEDBY = f.createURI(NAMESPACE, "wasGeneratedBy");
		WASDERIVEDFROM = f.createURI(NAMESPACE, "prov:wasDerivedFrom");
		GENERATED = f.createURI(NAMESPACE, "generated");
		HADACTIVITY = f.createURI(NAMESPACE, "hadActivity");
		WASATTRIBUTEDTO = f.createURI(NAMESPACE, "wasAttributedTo");
	}
	
	
}

package com.vn.smartdata.constants;

import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;


/**
 * The Class OWLConstants.
 */
public class OWLConstants {

	/** The Constant OWL_OBJECT_SOME_VALUES_FROM. */
	public static final String OWL_OBJECT_SOME_VALUES_FROM = "some";

	/** The Constant OWL_THING_IRI. */
	public static final String OWL_THING_IRI = OWLRDFVocabulary.OWL_THING.getIRI().toString();

	/** The Constant OWL_THING_SHORT_FORM. */
	public static final String OWL_THING_SHORT_FORM = OWLRDFVocabulary.OWL_THING.getIRI().getShortForm();

	/** The Constant OWL_THING_CLASS_ID. */
	public static final String OWL_THING_CLASS_ID = "owlThingClassId";

	/** The Constant RDF_PLAIN_LITERAL_IRI. */
	public static final String RDF_PLAIN_LITERAL_IRI = OWL2Datatype.RDF_PLAIN_LITERAL.getIRI().toString();

	public static final String WORDNET_ONTOLOGY_INSTANCE_IRI_PREFIX = "http://www.w3.org/2006/03/wn/wn20/instances/";

	public static final String WORDNET_ONTOLOGY_ID = "http://www.w3.org/2006/03/wn/wn20/schema";
}

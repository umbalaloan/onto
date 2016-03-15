package com.vn.smartdata.constants;

import com.vn.smartdata.webapp.util.ApplicationUtil;

/**
 * The Class LuceneConstants.
 */
public class LuceneConstants {
	
	/** The Constant ANNOTATIONS. */
	public static final String ANNOTATIONS = "annotations";
	
	/** The Constant IRI. */
	public static final String IRI = "iri";
	
	/** The Constant SHORT_FORM_IRI. */
	public static final String SHORT_FORM_IRI = "shortFormIRI";
	
	/** The Constant TYPES. */
	public static final String TYPES = "types";
	
	/** The Constant SAME_AS. */
	public static final String SAME_AS = "sameAs";
	
	/** The Constant DIFFERENT. */
	public static final String DIFFERENT = "different";
	
	/** The Constant OBJECT_PROPERTY_ASSERTIONS. */
	public static final String OBJECT_PROPERTY_ASSERTIONS = "opa";
	
	/** The Constant DATA_PROPERTY_ASSERTIONS. */
	public static final String DATA_PROPERTY_ASSERTIONS = "dpa";
	
	/** The Constant NEGATIVE_OBJECT_PROPERTY_ASSERTIONS. */
	public static final String NEGATIVE_OBJECT_PROPERTY_ASSERTIONS = "nopa";
	
	/** The Constant NEGATIVE_DATA_PROPERTY_ASSERTIONS. */
	public static final String NEGATIVE_DATA_PROPERTY_ASSERTIONS = "ndpa";
	
	/** The Constant LUCENE_INDEX_DIRECTORY. */
	public static final String LUCENE_INDEX_DIRECTORY = ApplicationUtil.getApplicationValue("lucene.index.directory");
	
	/** The Constant MAX_SEARCH_RESULTS. */
	public static final int MAX_SEARCH_RESULTS = Integer.valueOf(ApplicationUtil.getApplicationValue("lucene.max.search.results"));
	
	/** The Constant ITEMS_PER_PAGE. */
	public static final int ITEMS_PER_PAGE = Integer.valueOf(ApplicationUtil.getApplicationValue("lucene.items.per.page"));
	
	/** The Constant SEARCH_FIELDS. */
	public static final String[] SEARCH_FIELDS = new String[] {TYPES, SHORT_FORM_IRI};
}

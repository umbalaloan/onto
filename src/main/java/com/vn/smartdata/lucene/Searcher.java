package com.vn.smartdata.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.vn.smartdata.constants.LuceneConstants;
import com.vn.smartdata.constants.OWLConstants;
import com.vn.smartdata.constants.StringPool;


/**
 * The Class Searcher.
 */
public class Searcher {

	protected final transient Log log = LogFactory.getLog(this.getClass());

	/** The index searcher. */
	IndexSearcher indexSearcher;

	DirectoryReader reader;

	/** The query. */
	Query query;

	/**
	 * Instantiates a new searcher.
	 *
	 * @param indexDirectoryPath the index directory path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Searcher(final String indexDirectoryPath) {
		try {
			// Same idea as index_writer, index searcher also should not be opened more than 1 time as it is very costly
			if(this.indexSearcher == null){
				final Directory indexDirectory = FSDirectory.open(Paths
						.get(indexDirectoryPath));

				final DirectoryReader reader = DirectoryReader.open(indexDirectory);
				this.reader = reader;

				this.indexSearcher = new IndexSearcher(reader);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	//	public TopDocs getAllDocuments () throws IOException {
	//		Bits liveDocs = MultiFields.getLiveDocs(reader);
	//		for (int i=0; i<reader.maxDoc(); i++) {
	//		    if (liveDocs != null && !liveDocs.get(i))
	//		        continue;
	//		    Document doc = reader.document(i);
	//		}
	//
	//		return null;
	//	}

	/**
	 * Gets the all documents.
	 *
	 * @param selectedClass the selected class: if blank => search individual of thing
	 * @param start the start
	 * @param itemPerPages the item per pages
	 * @param maxResults the max results
	 * @return the all documents
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public TopDocs getAllDocuments(final String selectedClass, final int start, final int itemPerPages, final int maxResults)
			throws IOException {
		final SortField sortField = new SortField(LuceneConstants.SHORT_FORM_IRI,SortField.Type.STRING,false);
		final Sort sort = new Sort(sortField);
		final TopFieldCollector topCollector = TopFieldCollector.create(sort, maxResults, true, false, false);

		// get all results without any conditions attached.
		Term term = new Term(LuceneConstants.SHORT_FORM_IRI, StringPool.STAR);

		if(!selectedClass.equals(StringPool.BLANK) && !selectedClass.equals(OWLConstants.OWL_THING_SHORT_FORM)){
			term = new Term(LuceneConstants.TYPES, selectedClass);
		}

		final Query query = new WildcardQuery(term);
		this.log.info("getAllDocuments() Query: " + query.toString());
		this.indexSearcher.search(query, topCollector);

		final TopDocs topDocs = topCollector.topDocs(start, itemPerPages);

		return topDocs;
	}

	/**
	 * Search english words.
	 *
	 * @param searchQuery the search query
	 * @param owlClass the owl class
	 * @return the top docs
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	public TopDocs searchEnglishWords(final String searchQuery, final String owlClass, final int start, final int itemsPerPage) throws IOException,
	ParseException {
		return this.searchWords(Indexer.getEnglishAnalyzer(), searchQuery, owlClass, LuceneConstants.MAX_SEARCH_RESULTS, start, itemsPerPage);
	}

	/**
	 * Search vn words.
	 *
	 * @param searchQuery the search query
	 * @param owlClass the owl class
	 * @return the top docs
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	public TopDocs searchVNWords(final String searchQuery, final String owlClass, final int start, final int itemsPerPage) throws IOException,
	ParseException {
		return this.searchWords(Indexer.getVnAnalyzer(), searchQuery, owlClass, LuceneConstants.MAX_SEARCH_RESULTS, start, itemsPerPage);
	}

	/**
	 * Search words.
	 *
	 * @param analyzer the analyzer
	 * @param searchQuery the search query
	 * @param owlClass the owl class
	 * @param maxResults the max results
	 * @return the top docs
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	public TopDocs searchWords(final Analyzer analyzer, String searchQuery, final String owlClass, final int maxResults, final int start, final int itemsPerPage) throws IOException,
	ParseException {

		// Enabling wildcard search
		if(!searchQuery.endsWith(StringPool.STAR)){
			searchQuery = searchQuery + StringPool.STAR;
		}

		if(owlClass.equalsIgnoreCase(OWLConstants.OWL_THING_SHORT_FORM)){
			final Term term = new Term(LuceneConstants.SHORT_FORM_IRI, searchQuery);

			// SimpleQueryParser queryParser = new SimpleQueryParser(analyzer, LuceneConstants.SHORT_FORM_IRI);
			// query = queryParser.parse(searchQuery);
			this.query = new WildcardQuery(term);
		} else {
			this.query = MultiFieldQueryParser.parse(new String[] {owlClass, searchQuery}, LuceneConstants.SEARCH_FIELDS, analyzer);
		}

		this.log.info("searchWords() Query: " + this.query);

		final TopScoreDocCollector collector = TopScoreDocCollector.create(maxResults);  // MAX_RESULTS is just an int limiting the total number of hits
		this.indexSearcher.search(this.query, collector);

		return collector.topDocs(start, itemsPerPage);
	}

	/**
	 * Gets the document.
	 *
	 * @param scoreDoc the score doc
	 * @return the document
	 * @throws CorruptIndexException the corrupt index exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Document getDocument(final ScoreDoc scoreDoc)
			throws CorruptIndexException, IOException {
		return this.indexSearcher.doc(scoreDoc.doc);
	}

	/**
	 * Close.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void close() throws IOException {
		//indexSearcher.close();
	}
}

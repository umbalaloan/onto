/*
 * 
 */
package com.vn.smartdata.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.EscapeUtils;

import com.bk.vnanalyzer.VNAnalyzer;
import com.bk.vnanalyzer.ViStopWordsProvider;
import com.vn.smartdata.constants.LuceneConstants;
import com.vn.smartdata.constants.OWLConstants;
import com.vn.smartdata.constants.StringPool;


// TODO: Auto-generated Javadoc
/**
 * The Class Indexer.
 */
public class Indexer {
	
	/** The log. */
	protected final transient Log log = LogFactory.getLog(this.getClass());
	
	/** The writer. */
	private IndexWriter writer;
	
	/** The vn analyzer. */
	private static VNAnalyzer vnAnalyzer;
	
	/** The english analyzer. */
	private static EnglishAnalyzer englishAnalyzer = new EnglishAnalyzer();
	
	/** The Constant FIELD_TYPE. */
	private static final FieldType FIELD_TYPE = new FieldType();
	
	private List<Document> userAddedDocuments = new ArrayList<>(); 
	
	static {
		ClassLoader classLoader = Indexer.class.getClassLoader();
		File file = new File(classLoader.getResource("stopwords.txt").getFile());
		vnAnalyzer = new VNAnalyzer(ViStopWordsProvider.getStopWords(file.getAbsolutePath()));
		
		FIELD_TYPE.setDocValuesType(DocValuesType.SORTED);
		FIELD_TYPE.setStored(true);
		FIELD_TYPE.setTokenized(true);
	}

	/**
	 * Instantiates a new indexer.
	 *
	 * @param indexDirectoryPath the index directory path
	 * @param useVnAnalyzer the use vn analyzer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Indexer(String indexDirectoryPath, boolean useVnAnalyzer) {
		try {
			// writer should not be opened more than 1 time as it is very costly to do so
			if(writer == null){
				// this directory that contains the indexes
				Directory indexDirectory = FSDirectory
						.open(Paths.get(indexDirectoryPath));
				
				IndexWriterConfig config = null;
				
				if(useVnAnalyzer){
					config = new IndexWriterConfig(vnAnalyzer);
				} else {
					config = new IndexWriterConfig(englishAnalyzer);
				}
				
				// create the indexer
				writer = new IndexWriter(indexDirectory, config);
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	/**
	 * Close.
	 *
	 * @throws CorruptIndexException the corrupt index exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}

	/**
	 * Converts an OWL individual to Lucene Document.
	 *
	 * @param individual the individual
	 * @param owlOntology the owl ontology
	 * @return the document
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Document getDocument(OWLNamedIndividual individual, OWLOntology owlOntology) throws IOException {
		IRI individualIRI = individual.getIRI();
		
		Document document = new Document();
		
		// Store IRI in order to get it when searcher hits this document
		Field iriField = new StoredField(LuceneConstants.IRI, individualIRI.toString());
		
		Set<OWLAnnotationAssertionAxiom> annotationAssertionSet = owlOntology.getAnnotationAssertionAxioms(individualIRI);
		
		// Indexing annotation
		for (final OWLAnnotationAssertionAxiom owlAnnotationAssertion: annotationAssertionSet) {
			final OWLLiteral value = owlAnnotationAssertion.getValue().asLiteral().get();
			final String literal = EscapeUtils.escapeString(value.getLiteral());
			
			Field annotationsField = new StringField(LuceneConstants.ANNOTATIONS, literal, Field.Store.NO);
			document.add(annotationsField);
		}
		
		final Collection<OWLClassExpression> typeSet = EntitySearcher.getTypes(individual, owlOntology);

		// Indexing FIELD_TYPE/class
		for(final OWLClassExpression oce: typeSet){
			String oceString = StringPool.BLANK;
					
			if(!oce.isAnonymous()){
				oceString = oce.asOWLClass().getIRI().getShortForm();
			} else {
				oceString = oceString.toString();
			}
			
			if(oceString.equals(StringPool.BLANK)){
				oceString = OWLConstants.OWL_THING_SHORT_FORM;
			}
			
			Field typesField = new StringField(LuceneConstants.TYPES, oceString, Field.Store.NO);
			document.add(typesField);
		}
		
		// index annotations, name/short iri
		
		// Short form IRI field: used to store and display value
		Field nameField = new StringField(LuceneConstants.SHORT_FORM_IRI, individualIRI.getShortForm(), Field.Store.YES);
		
		// Sorted field is used to sort when display the first time
		Field sortedField = new SortedDocValuesField(LuceneConstants.SHORT_FORM_IRI, new BytesRef(individualIRI.getShortForm()));
		//Field nameField = new Field(LuceneConstants.SHORT_FORM_IRI, individualIRI.getShortForm(), FIELD_TYPE);
		// same as, different, object property assertion, data property assertion, negative opa, negative dpa will be indexed if needed
		
		// Add fields to document
		document.add(nameField);
		document.add(sortedField);
		document.add(iriField);
		
		return document;
	}

	/**
	 * Creates the individual index.
	 *
	 * @param owlOntology the owl ontology
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public int createIndividualIndex(OWLOntology owlOntology) throws IOException {
		Set<OWLNamedIndividual> classInstances = owlOntology.getIndividualsInSignature();
		
		for(OWLNamedIndividual individual: classInstances){
			Document document = getDocument(individual, owlOntology);
			IRI individualIRI = individual.getIRI();
			log.info("Indexing " + individualIRI.getShortForm() + " <" + individualIRI.toString() + ">");
			writer.addDocument(document);
		}
		
		int numDocs = writer.numDocs();
		
		// Commit changes to index directory
		writer.commit();
		
		// Should not close index writer but always use a single instance
		//writer.close();
		
		return numDocs;
	}
	
	/**
	 * Index individual.
	 *
	 * @param individual the individual
	 * @param owlOntology the owl ontology
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void indexIndividual(OWLNamedIndividual individual, OWLOntology owlOntology) throws IOException{
		Document document = getDocument(individual, owlOntology);
		
		// Store in this array for rollback purpose later
		userAddedDocuments.add(document);
		
		writer.addDocument(document);
		writer.commit();
	}
	
	/**
	 * Rollback changes.
	 *
	 */
	public void rollbackChanges () {
		try {
			Term[] terms = new Term[userAddedDocuments.size()];
			
			for(int i = 0; i < userAddedDocuments.size(); i++){
				Document d = userAddedDocuments.get(i);
				Term term = new Term(LuceneConstants.IRI, d.get(LuceneConstants.IRI));
				terms[i] = term;
			}
			
			writer.deleteDocuments(terms);
		} catch (Exception e) {
			userAddedDocuments = new ArrayList<Document>();
		}
	}
	

	/**
	 * @return the vnAnalyzer
	 */
	public static VNAnalyzer getVnAnalyzer() {
		return vnAnalyzer;
	}

	/**
	 * @return the englishAnalyzer
	 */
	public static EnglishAnalyzer getEnglishAnalyzer() {
		return englishAnalyzer;
	}
}

package com.vn.smartdata.webapp.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.springframework.stereotype.Component;

import com.google.gson.stream.JsonWriter;
import com.vn.smartdata.constants.LuceneConstants;
import com.vn.smartdata.lucene.Searcher;
import com.vn.smartdata.manager.RelatedIndividualManager;
import com.vn.smartdata.model.Annotation;
import com.vn.smartdata.model.DataPropertyAssertion;
import com.vn.smartdata.model.Individual;
import com.vn.smartdata.model.ObjectPropertyAssertion;
import com.vn.smartdata.model.Ontology;
import com.vn.smartdata.model.RelatedIndividual;
import com.vn.smartdata.model.Type;
import com.vn.smartdata.model.User;


/**
 * The Class OWLUtil.
 *
 * @author TAQ1HC
 */

@Component
public class OWLIndividualUtil {

	protected final transient static Log log = LogFactory.getLog(OWLIndividualUtil.class);

	/**
	 * Build OWL named individual json.
	 *
	 * @param writer the writer
	 * @param owlOntology the owl ontology
	 * @param namedIndividual the named individual
	 * @throws Exception the exception
	 */
	public static void buildOWLNamedIndividualJSONDetails (final JsonWriter writer, final Ontology ontology, final OWLNamedIndividual namedIndividual, final OWLDataFactory factory,
			final Individual individual, final Set<RelatedIndividual> sameIndividuals, final Set<RelatedIndividual> differentIndividuals, final Set<ObjectPropertyAssertion> objectPropertyAssertions, final Set<ObjectPropertyAssertion> negativeObjectPropertyAssertions,
			final Set<DataPropertyAssertion> dataPropertyAssertions, final Set<DataPropertyAssertion> negativeDataPropertyAssertions) throws Exception {

		final IRI namedIndividualIRI = namedIndividual.getIRI();
		final String individualIRIString = namedIndividualIRI.toString();

		// Start OWL named individual JSON object
		writer.beginObject();
		writer.name("id").value(individualIRIString);
		writer.name("text").value(namedIndividualIRI.getShortForm());
		writer.name("icon").value("fa diamond-icon individual-color");
		writer.name("isIndividualNull").value(null == individual ? true : false );

		if(null != individual){
			// Start 'individual data'
			writer.name("data").beginObject();

			writer.name("nodeId").value(namedIndividual.getIRI().toString());

			// Get all annotations that belong to this individual
			final Set<Annotation> annotations = individual.getAnnotations();

			// Start 'annotations' array
			writer.name("annotations").beginArray();

			// Build annotation json
			for(final Annotation a: annotations){
				writer.beginObject();

				final String annotationProperty = a.getAnnotationPropertyIRI();
				final String annotationLanguage = a.getAnnotationLanguage();
				final String annotationDatatype = a.getAnnotationDataTypeIRI();
				final String annotationValue = a.getAnnotationValue();

				final OWLAnnotationAssertionAxiom axiom = OWLAnnotationPropertyUtil.createOWLAnnotationAssertionAxiom(factory, annotationProperty, annotationValue, annotationLanguage, annotationDatatype, individualIRIString, namedIndividualIRI);
				OWLAnnotationPropertyUtil.buildOWLAnnotationAssertionAxiomJSON(axiom, writer);

				writer.name("id").value(a.getId());
				writer.endObject();
			}

			writer.endArray();

			// Start 'types' array
			writer.name("types").beginArray();

			// Get all types that belong to this individual
			final Set<Type> types = individual.getTypes();

			OWLClassUtil.buildIndividualTypesJSON(types, writer);

			// End 'types' array
			writer.endArray();

			// Start 'same individual as' array
			writer.name("sameIndividuals").beginArray();

			OWLIndividualUtil.buildRelatedIndividualList(sameIndividuals, writer);
			//			for(final RelatedIndividual ri: sameIndividuals){
			//				//if(!oce.equals(namedIndividual)){
			//				writer.beginObject();
			//				writer.name("iri").value(ri.getIndividualIRI());
			//				writer.name("name").value(ri.getIndividualShortForm());
			//				writer.name("id").value(ri.getId());
			//				writer.endObject();
			//				//}
			//			}

			// End 'same individual as' array
			writer.endArray();

			// Start 'different individual as' array
			writer.name("differentIndividuals").beginArray();

			OWLIndividualUtil.buildRelatedIndividualList(differentIndividuals, writer);
			//			for(final RelatedIndividual ri: differentIndividuals){
			//				writer.beginObject();
			//				writer.name("iri").value(ri.getIndividualIRI());
			//				writer.name("name").value(ri.getIndividualShortForm());
			//				writer.name("id").value(ri.getId());
			//				writer.endObject();
			//			}

			// End 'different individual as' array
			writer.endArray();

			// Start 'object prop assertion' array
			writer.name("objectPropAssertions").beginArray();

			OWLObjectPropertyUtil.buildListOPAJSON(objectPropertyAssertions, writer);

			// End 'object prop assertion' array
			writer.endArray();

			// Start 'data prop assertion' array
			writer.name("dataPropAssertions").beginArray();

			for(final DataPropertyAssertion dpa: dataPropertyAssertions){
				final String dpDatatype = dpa.getDataPropertyTypeIRI();
				final String dpValue = dpa.getDataPropertyValue();
				final String dpLanguage = dpa.getDataPropertyLanguage();
				final String dataProperty = dpa.getDataPropertyIRI();
				final boolean isNegative = dpa.isNegative();

				final OWLPropertyAssertionAxiom<?, ?>  axiom = OWLDataPropertyUtil.createOWLPropertyAssertionAxiom(factory, namedIndividualIRI, dpDatatype, dpValue, dpLanguage, dataProperty, isNegative);

				writer.beginObject();
				OWLDataPropertyUtil.buildOWLDataPropertyAssertionAxiomJSON(axiom, writer);
				writer.name("id").value(dpa.getId());
				writer.endObject();
			}

			// End 'data prop assertion' array
			writer.endArray();

			// Start 'negative object prop assertion' array
			writer.name("negativeObjectPropAssertions").beginArray();

			OWLObjectPropertyUtil.buildListOPAJSON(negativeObjectPropertyAssertions, writer);

			// End 'negative object prop assertion' array
			writer.endArray();

			// Start 'negative data prop assertion' array
			writer.name("negativeDataPropAssertions").beginArray();

			for(final DataPropertyAssertion dpa: negativeDataPropertyAssertions){
				final String dpDatatype = dpa.getDataPropertyTypeIRI();
				final String dpValue = dpa.getDataPropertyValue();
				final String dpLanguage = dpa.getDataPropertyLanguage();
				final String dataProperty = dpa.getDataPropertyIRI();
				final boolean isNegative = dpa.isNegative();

				final OWLPropertyAssertionAxiom<?, ?>  axiom = OWLDataPropertyUtil.createOWLPropertyAssertionAxiom(factory, namedIndividualIRI, dpDatatype, dpValue, dpLanguage, dataProperty, isNegative);

				writer.beginObject();
				OWLDataPropertyUtil.buildOWLDataPropertyAssertionAxiomJSON(axiom, writer);
				writer.name("id").value(dpa.getId());
				writer.endObject();
			}

			// End 'negative data prop assertion' array
			writer.endArray();

			// End 'individual data'
			writer.endObject();
		}

		// End 'individual'
		writer.endObject();
	}

	/**
	 * Build OWL named individual json.
	 *
	 * @param writer the writer
	 * @param owlOntology the owl ontology
	 * @param namedIndividual the named individual
	 * @throws Exception the exception
	 */
	public static void buildOWLNamedIndividualJSONDetails (final JsonWriter writer, final OWLOntology owlOntology, final OWLNamedIndividual namedIndividual) throws Exception {
		final IRI namedIndividualIRI = namedIndividual.getIRI();

		// Start OWL named individual JSON object
		writer.beginObject();
		writer.name("id").value(namedIndividualIRI.toString());
		writer.name("text").value(namedIndividualIRI.getShortForm());
		writer.name("icon").value("fa diamond-icon individual-color");

		// Start 'individual data'
		writer.name("data").beginObject();

		writer.name("nodeId").value(namedIndividual.getIRI().toString());

		// Build annotation json details
		OWLAnnotationPropertyUtil.buildAnnotationJSONDetails(writer, owlOntology, namedIndividual);

		// Start 'types' array
		writer.name("types").beginArray();

		final Collection<OWLClassExpression> typeSet = EntitySearcher.getTypes(namedIndividual, owlOntology);

		for(final OWLClassExpression oce: typeSet){
			writer.beginObject();
			final boolean isAnonymous = oce.isAnonymous();

			if(!isAnonymous){
				final OWLClass owlClass = oce.asOWLClass();
				writer.name("classIRI").value(owlClass.getIRI().toString());
			}

			writer.name("isAnonymous").value(oce.isAnonymous());
			writer.name("name").value(oce.toString());
			writer.endObject();
		}

		// End 'types' array
		writer.endArray();

		// Start 'same individual as' array
		writer.name("sameIndividuals").beginArray();

		final Collection<OWLIndividual> sameIndividualSet = EntitySearcher.getSameIndividuals(namedIndividual, owlOntology);

		for(final OWLIndividual oce: sameIndividualSet){
			if(!oce.equals(namedIndividual)){
				writer.beginObject();
				writer.name("name").value(oce.toString());
				writer.endObject();
			}
		}

		// End 'same individual as' array
		writer.endArray();

		// Start 'different individual as' array
		writer.name("differentIndividuals").beginArray();

		final Collection<OWLIndividual> differentIndividualSet = EntitySearcher.getDifferentIndividuals(namedIndividual, owlOntology);

		for(final OWLIndividual oce: differentIndividualSet){
			writer.beginObject();
			writer.name("name").value(oce.toString());
			writer.endObject();
		}

		// End 'different individual as' array
		writer.endArray();

		// Start 'object prop assertion' array
		writer.name("objectPropAssertions").beginArray();

		final Collection<OWLObjectPropertyAssertionAxiom> objectPropAssertionAxiomSet = owlOntology.getObjectPropertyAssertionAxioms(namedIndividual);

		for(final OWLObjectPropertyAssertionAxiom axiom: objectPropAssertionAxiomSet){
			writer.beginObject();
			writer.name("propertyIRI").value(axiom.getProperty().asOWLObjectProperty().getIRI().toString());
			writer.name("objectIRI").value(axiom.getObject().asOWLNamedIndividual().getIRI().toString());
			writer.name("property").value(axiom.getProperty().toString());
			writer.name("object").value(axiom.getObject().toString());
			writer.endObject();
		}

		// End 'object prop assertion' array
		writer.endArray();

		// Start 'data prop assertion' array
		writer.name("dataPropAssertions").beginArray();

		final Collection<OWLDataPropertyAssertionAxiom> dataPropAssertionAxiomSet = owlOntology.getDataPropertyAssertionAxioms(namedIndividual);

		for(final OWLDataPropertyAssertionAxiom axiom: dataPropAssertionAxiomSet){
			writer.beginObject();
			OWLDataPropertyUtil.buildOWLDataPropertyAssertionAxiomJSON(axiom, writer);
			writer.endObject();
		}

		// End 'data prop assertion' array
		writer.endArray();

		// Start 'negative object prop assertion' array
		writer.name("negativeObjectPropAssertions").beginArray();

		final Collection<OWLNegativeObjectPropertyAssertionAxiom> negativeObjectPropAssertionAxiomSet = owlOntology.getNegativeObjectPropertyAssertionAxioms(namedIndividual);

		for(final OWLNegativeObjectPropertyAssertionAxiom axiom: negativeObjectPropAssertionAxiomSet){
			writer.beginObject();
			writer.name("propertyIRI").value(axiom.getProperty().asOWLObjectProperty().getIRI().toString());
			writer.name("objectIRI").value(axiom.getObject().asOWLNamedIndividual().getIRI().toString());
			writer.name("property").value(axiom.getProperty().toString());
			writer.name("object").value(axiom.getObject().toString());
			writer.endObject();
		}

		// End 'negative object prop assertion' array
		writer.endArray();

		// Start 'negative data prop assertion' array
		writer.name("negativeDataPropAssertions").beginArray();

		final Collection<OWLNegativeDataPropertyAssertionAxiom> negativeDataPropAssertionAxiomSet = owlOntology.getNegativeDataPropertyAssertionAxioms(namedIndividual);

		for(final OWLNegativeDataPropertyAssertionAxiom axiom: negativeDataPropAssertionAxiomSet){
			writer.beginObject();
			OWLDataPropertyUtil.buildOWLDataPropertyAssertionAxiomJSON(axiom, writer);
			writer.endObject();
		}

		// End 'negative data prop assertion' array
		writer.endArray();

		// End 'individual data'
		writer.endObject();

		writer.name("children").value(false);
		writer.name("state").beginObject();

		writer.name("selected").value("false");
		writer.name("opened").value("true");
		// writer.name("disabled").value("false");
		// checkbox plugin specific
		// writer.name("checked").value("false");
		// writer.name("undetermined ").value("false");

		// End 'state' object
		writer.endObject();
		writer.name("type").value("default");
		// writer.name("li_attr").value("");
		// writer.name("a_attr").value("");

		// End 'individual'
		writer.endObject();
	}

	/**
	 * Builds the individual json.
	 *
	 * @param searcher the searcher
	 * @param topDocs the top docs
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String buildIndividualJSON (final Searcher searcher, final TopDocs topDocs, final int startIndex, int endIndex) throws IOException {
		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		final int totalResults = topDocs.totalHits;
		// Total number of pages = always round up (total number of results / number of items per page)
		final int totalPages = (int) Math.ceil(totalResults / (double)LuceneConstants.ITEMS_PER_PAGE);

		if(endIndex > totalResults){
			endIndex = totalResults;
		}

		writer.beginObject();
		writer.name("itemsPerPage").value(LuceneConstants.ITEMS_PER_PAGE);
		writer.name("totalPages").value(totalPages);
		writer.name("totalResults").value(totalResults);

		writer.name("startIndex").value(startIndex);
		writer.name("endIndex").value(endIndex);

		writer.name("individualArray").beginArray();

		OWLIndividualUtil.log.info("Number of search results: " + topDocs.totalHits);

		for(final ScoreDoc scoreDoc: topDocs.scoreDocs){
			final Document doc = searcher.getDocument(scoreDoc);
			final String IRI = doc.get(LuceneConstants.IRI);
			final String shortFormIRI = doc.get(LuceneConstants.SHORT_FORM_IRI);

			writer.beginObject();
			writer.name("iri").value(IRI);
			writer.name("shortFormIRI").value(shortFormIRI);
			writer.endObject();
		}

		writer.endArray();
		writer.endObject();
		writer.close();

		return out.toString();
	}

	/**
	 * Builds the related individual list.
	 *
	 * @param riList the ri list
	 * @param writer the writer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void buildRelatedIndividualList(final Collection<RelatedIndividual> riList, final JsonWriter writer) throws IOException{
		for(final RelatedIndividual ri: riList){
			writer.beginObject();
			OWLIndividualUtil.buildRelatedIndividual(ri, writer);
			writer.endObject();
		}
	}

	/**
	 * Builds the related individual.
	 *
	 * @param ri the ri
	 * @param writer the writer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void buildRelatedIndividual(final RelatedIndividual ri, final JsonWriter writer) throws IOException{
		writer.name("iri").value(ri.getIndividualIRI());
		writer.name("name").value(ri.getIndividualShortForm());
		writer.name("id").value(ri.getId());
	}

	/**
	 * Gets the same and different individuals. (First object is same individual list, the second is different individual list)
	 *
	 * @param relatedIndividuals the related individuals
	 * @return the same and different individuals
	 */
	public static Object[] getSameAndDifferentIndividuals(final Set<RelatedIndividual> relatedIndividuals){
		final Set<RelatedIndividual> sameIndividuals = new HashSet<>();
		final Set<RelatedIndividual> differentIndividuals = new HashSet<>();

		final Object[] relatedIndividualList = new Object[2];

		if(null != relatedIndividuals && !relatedIndividuals.isEmpty()){
			for(final RelatedIndividual ri: relatedIndividuals){
				// Different individual
				if(ri.isSameOrDifferent()){
					differentIndividuals.add(ri);
				} else {
					sameIndividuals.add(ri);
				}
			}
		}

		// Add 2 lists to object array
		relatedIndividualList[0] = sameIndividuals;
		relatedIndividualList[1] = differentIndividuals;

		return relatedIndividualList;
	}

	/**
	 * Gets the object property assertion and its negative. (0: positive opa list, 1: negative opa list)
	 *
	 * @param objectPropertyAssertions the object property assertions
	 * @return the object property assertion and its negative
	 */
	public static Object[] getObjectPropertyAssertionAndItsNegative (final Set<ObjectPropertyAssertion> objectPropertyAssertions){
		final Set<ObjectPropertyAssertion> OPAs = new HashSet<>();
		final Set<ObjectPropertyAssertion> negativeOPAs = new HashSet<>();

		final Object[] objectPropertyAssertionList = new Object[2];

		if(null != objectPropertyAssertions && !objectPropertyAssertions.isEmpty()){
			for(final ObjectPropertyAssertion opa: objectPropertyAssertions){
				if(opa.isNegative()){
					negativeOPAs.add(opa);
				} else {
					OPAs.add(opa);
				}
			}
		}

		objectPropertyAssertionList[0] = OPAs;
		objectPropertyAssertionList[1] = negativeOPAs;

		return objectPropertyAssertionList;
	}

	/**
	 * Gets the data property assertion and its negative. (0: positive list, 1: negative list)
	 *
	 * @param dataPropertyAssertions the data property assertions
	 * @return the data property assertion and its negative
	 */
	public static Object[] getDataPropertyAssertionAndItsNegative (final Set<DataPropertyAssertion> dataPropertyAssertions){
		final Set<DataPropertyAssertion> DPAs = new HashSet<>();
		final Set<DataPropertyAssertion> negativeDPAs = new HashSet<>();

		final Object[] dataPropertyAssertionList = new Object[2];

		if(null != dataPropertyAssertions && !dataPropertyAssertions.isEmpty()){
			for(final DataPropertyAssertion dpa: dataPropertyAssertions){
				if(dpa.isNegative()){
					negativeDPAs.add(dpa);
				} else {
					DPAs.add(dpa);
				}
			}
		}

		dataPropertyAssertionList[0] = DPAs;
		dataPropertyAssertionList[1] = negativeDPAs;

		return dataPropertyAssertionList;
	}

	public static void insertSameIndividualsToDB (final RelatedIndividualManager riManager, final OWLNamedIndividual namedIndividual, final OWLOntology owlOntology, final User user, final Individual individual) {
		final Collection<OWLIndividual> sameIndividualSet = EntitySearcher.getSameIndividuals(namedIndividual, owlOntology);

		for(final OWLIndividual oce: sameIndividualSet){
			if(!oce.equals(namedIndividual)){
				//writer.name("name").value(oce.toString());
				final IRI iri = oce.asOWLNamedIndividual().getIRI();

				final RelatedIndividual ri = new RelatedIndividual();
				ri.setIndividualIRI(iri.toString());
				ri.setIndividualShortForm(iri.getShortForm());
				ri.setReferenceIndividualIRI(individual.getIndividualIRI());
				ri.setEditor(user);
				ri.setSameOrDifferent(false);

				riManager.save(ri);
			}
		}
	}

	public static void insertDifferentIndividualsToDB (final RelatedIndividualManager riManager, final OWLNamedIndividual namedIndividual, final OWLOntology owlOntology, final User user, final Individual individual) {
		final Collection<OWLIndividual> differentIndividualSet = EntitySearcher.getDifferentIndividuals(namedIndividual, owlOntology);

		for(final OWLIndividual oce: differentIndividualSet){
			//writer.name("name").value(oce.toString());
			final IRI iri = oce.asOWLNamedIndividual().getIRI();

			final RelatedIndividual ri = new RelatedIndividual();
			ri.setIndividualIRI(iri.toString());
			ri.setIndividualShortForm(iri.getShortForm());
			ri.setReferenceIndividualIRI(individual.getIndividualIRI());
			ri.setEditor(user);
			ri.setSameOrDifferent(true);

			riManager.save(ri);
		}
	}
}

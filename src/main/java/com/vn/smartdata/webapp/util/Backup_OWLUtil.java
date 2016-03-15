package com.vn.smartdata.webapp.util;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.EscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.stream.JsonWriter;
import com.vn.smartdata.constants.LuceneConstants;
import com.vn.smartdata.constants.OWLConstants;
import com.vn.smartdata.constants.StringPool;
import com.vn.smartdata.lucene.Searcher;
import com.vn.smartdata.manager.OntologyManager;
import com.vn.smartdata.model.jaxb.IndividualJAXB;
import com.vn.smartdata.webapp.controller.OntologyController;


/**
 * The Class OWLUtil.
 *
 * @author TAQ1HC
 */
public class Backup_OWLUtil {

	protected final transient static Log log = LogFactory.getLog(Backup_OWLUtil.class);

	@Autowired
	private static OntologyManager ontologyManager;

	/**
	 * Check if an OWL Individual existed or not based on given name.
	 *
	 * @param owlOntology the owl ontology
	 * @param individualName the individual name
	 * @return true, if existed or there is an exception
	 */
	public static boolean checkOWLIndividualIRIExistence (final OWLOntology owlOntology, final String individualName) {
		final String owlIndividualIRI = owlOntology.getOntologyID().getOntologyIRI().get().toString() + StringPool.POUND + individualName;

		boolean isAlreadyExisted = false;

		// If user enters illegal characters such as < > [ ] { } etc., return false
		try {
			final URI uri = URI.create(owlIndividualIRI);
			isAlreadyExisted = owlOntology.containsIndividualInSignature(IRI.create(uri));
		} catch (final Exception e) {
			isAlreadyExisted = true;
		}

		return isAlreadyExisted;
	}

	/**
	 * Check if an OWL Individual existed or not based on given name.
	 *
	 * @param owlOntology the owl ontology
	 * @param individualIRI the individual iri
	 * @return true, if existed
	 */
	public static boolean checkOWLIndividualIRIExistence (final OWLOntology owlOntology, final IRI individualIRI) {
		boolean isAlreadyExisted = false;

		try {
			isAlreadyExisted = owlOntology.containsIndividualInSignature(individualIRI);
		} catch (final Exception e) {
			isAlreadyExisted = true;
		}

		return isAlreadyExisted;
	}

	/**
	 * Creates the iri.
	 *
	 * @param owlOntology the owl ontology
	 * @param individualName the individual name
	 * @return the iri
	 */
	public static IRI createIRI(final OWLOntology owlOntology, final String individualName) {
		final String owlIndividualIRI = owlOntology.getOntologyID().getOntologyIRI().get().toString() + StringPool.POUND + individualName;
		return IRI.create(owlIndividualIRI);
	}

	/**
	 * Builds the owl class hierarchy.
	 *
	 * @param writer the writer
	 * @param reasoner the reasoner
	 * @param factory the factory
	 * @param owlOntology the owl ontology
	 * @param owlClass the owl class
	 * @throws Exception the exception
	 */
	public static void buildOWLClassHierarchy(final JsonWriter writer, final OWLReasoner reasoner, final OWLDataFactory factory, final OWLOntology owlOntology, final OWLClass owlClass) throws Exception {
		if(owlClass.isOWLThing()){
			writer.beginArray();
		}

		writer.beginObject();
		writer.name("id").value(owlClass.getIRI().toString());

		writer.name("text").value(
				owlClass.getIRI().getShortForm());
		writer.name("icon").value("fa fa-circle class-color");
		writer.name("nodeId").value(owlClass.getIRI().toString());

		// Have to remove owl:nothing class, it always displays as child of every class
		final Set<OWLClass> subClasses = reasoner
				.getSubClasses(owlClass, true).getFlattened();
		subClasses.remove(factory.getOWLNothing());

		// Query sub classes if current class is owl:Thing, else do not query anything
		if (subClasses != null && !subClasses.isEmpty()) {
			writer.name("children").beginArray();

			// Display sub classes
			for (final OWLClass directSubClass : HTMLRenderer.toSortedSet(subClasses)) {
				Backup_OWLUtil.buildOWLClassHierarchy(writer, reasoner, factory, owlOntology, directSubClass);
			}

			writer.endArray();
		} else {
			writer.name("children").value(false);
		}

		if (owlClass.isOWLThing()) {
			writer.name("state").beginObject();

			writer.name("selected").value("false");
			writer.name("opened").value("true");
			// writer.name("disabled").value("false");
			// checkbox plugin specific
			// writer.name("checked").value("false");
			// writer.name("undetermined ").value("false");

			writer.endObject();
			writer.name("type").value("default");
			// writer.name("li_attr").value("");
			// writer.name("a_attr").value("");
		}

		// writer.name("state").beginObject();
		//
		// writer.name("selected").value("false");
		// writer.name("opened").value("false");
		// writer.name("disabled").value("false");
		// checkbox plugin specific
		// writer.name("checked").value("false");
		// writer.name("undetermined ").value("false");
		// writer.name("type").value("default");
		// writer.name("li_attr").value("");
		// writer.name("a_attr").value("");

		// writer.endObject();
		writer.endObject();

		if(owlClass.isOWLThing()){
			writer.endArray();
		}
	}

	/**
	 * Build OWL named individual json.
	 *
	 * @param writer the writer
	 * @param owlOntology the owl ontology
	 * @param namedIndividual the named individual
	 * @throws Exception the exception
	 */
	private void buildOWLNamedIndividualJSONDetails (final JsonWriter writer, final IndividualJAXB individualJAXB, final OWLNamedIndividual namedIndividual) throws Exception {

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
		Backup_OWLUtil.log.debug("Individual: " + namedIndividual.toString());

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
		Backup_OWLUtil.buildAnnotationJSONDetails(writer, owlOntology, namedIndividual);

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
			final OWLLiteral literalObject = axiom.getObject().asLiteral().get();
			final boolean isRDFPlainLiteral = literalObject.isRDFPlainLiteral();

			final String literalValue = EscapeUtils.escapeString(literalObject.getLiteral());
			String lang = StringPool.BLANK;
			String dataTypeIRIString = StringPool.BLANK;

			if(isRDFPlainLiteral){
				if(literalObject.hasLang()){
					lang = literalObject.getLang();
				}
				dataTypeIRIString = OWLConstants.RDF_PLAIN_LITERAL_IRI;
			} else {
				final IRI dataTypeIRI = literalObject.getDatatype().getIRI();
				dataTypeIRIString = dataTypeIRI.toString();
			}

			writer.beginObject();
			writer.name("propertyIRI").value(axiom.getProperty().asOWLDataProperty().getIRI().toString());
			writer.name("isRDFPlainLiteral").value(isRDFPlainLiteral);
			writer.name("lang").value(lang);
			writer.name("literalValue").value(literalValue);
			writer.name("dataTypeIRI").value(dataTypeIRIString);

			writer.name("property").value(axiom.getProperty().toString());
			writer.name("object").value(axiom.getObject().toString());
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
			final OWLLiteral literalObject = axiom.getObject().asLiteral().get();
			final boolean isRDFPlainLiteral = literalObject.isRDFPlainLiteral();

			final String literalValue = EscapeUtils.escapeString(literalObject.getLiteral());
			String lang = StringPool.BLANK;
			String dataTypeIRIString = StringPool.BLANK;

			if(isRDFPlainLiteral){
				if(literalObject.hasLang()){
					lang = literalObject.getLang();
				}
			} else {
				final IRI dataTypeIRI = literalObject.getDatatype().getIRI();
				dataTypeIRIString = dataTypeIRI.toString();
			}

			writer.beginObject();
			writer.name("propertyIRI").value(axiom.getProperty().asOWLDataProperty().getIRI().toString());
			writer.name("isRDFPlainLiteral").value(isRDFPlainLiteral);
			writer.name("lang").value(lang);
			writer.name("literalValue").value(literalValue);
			writer.name("dataTypeIRI").value(dataTypeIRIString);

			writer.name("property").value(axiom.getProperty().toString());
			writer.name("object").value(axiom.getObject().toString());
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

		System.out.println("Number of search results: " + topDocs.totalHits);

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
	 * This method used to build JSON of an OWL Annotation Property including its details (i.e. id, name, annotations, properties)
	 *
	 * @param writer the writer
	 * @param reasoner the reasoner
	 * @param factory the factory
	 * @param owlOntology the owl ontology
	 * @param parentProp the parent prop
	 * @param subPropSet the sub prop set
	 * @throws Exception the exception
	 */
	public static void buildOWLAnnoPropDetailsJSON(final JsonWriter writer, final OWLReasoner reasoner, final OWLDataFactory factory, final OWLOntology owlOntology, final OWLAnnotationProperty parentProp, final Set<OWLAnnotationProperty> subPropSet) throws Exception{

		if(null != parentProp){
			Backup_OWLUtil.log.debug("\n---- Parent AP property: " + parentProp.getIRI().getShortForm() + "(" + parentProp.toString() + ") ------");
		}

		// Display sub classes
		for (final OWLAnnotationProperty directSubProp : HTMLRenderer.toSortedSet(subPropSet)) {
			final IRI directSubPropIRI = directSubProp.getIRI();
			writer.beginObject();
			//writer.name("id").value(directSubProp.getIRI().toString());
			writer.name("text").value(
					directSubPropIRI.getShortForm());
			writer.name("icon").value("fa fa-square ap-class");

			Backup_OWLUtil.log.debug("\nCurrent AP property: " + directSubPropIRI.getShortForm() + " (" + directSubProp.toString() + ")");

			// - this can be anything you want - it is metadata you want attached to the node - you will be able to access and modify it any time later - it has no effect on the visuals of the node.
			writer.name("data").beginObject();
			writer.name("nodeId").value(directSubProp.getIRI().toString());

			// Build annotation json
			Backup_OWLUtil.buildAnnotationJSONDetails(writer, owlOntology, directSubProp);

			// Start 'domains (intersection)'
			writer.name("domains").beginArray();

			final Set<OWLAnnotationPropertyDomainAxiom> domainAxiomSet = owlOntology.getAnnotationPropertyDomainAxioms(directSubProp);

			Backup_OWLUtil.log.debug("Start anno property domain.......................................");
			for(final OWLAnnotationPropertyDomainAxiom domainAxiom: domainAxiomSet){
				Backup_OWLUtil.log.debug("Domain axiom: " + domainAxiom);
				writer.beginObject();
				writer.name("domainString").value(OntologyController.htmlRenderer.getShortFormInHTML(domainAxiom.getDomain()));
				writer.endObject();
			}
			Backup_OWLUtil.log.debug("End anno property domain.......................................");

			// End 'domains'
			writer.endArray();

			// Start 'ranges (intersection)'
			writer.name("ranges").beginArray();

			final Set<OWLAnnotationPropertyRangeAxiom> rangeAxiomSet = owlOntology.getAnnotationPropertyRangeAxioms(directSubProp);

			Backup_OWLUtil.log.debug("Start anno property range.......................................");
			for(final OWLAnnotationPropertyRangeAxiom rangeAxiom: rangeAxiomSet){
				Backup_OWLUtil.log.debug("Range axiom: " + rangeAxiom);

				writer.beginObject();
				writer.name("rangeString").value(OntologyController.htmlRenderer.getShortFormInHTML(rangeAxiom.getRange()));
				writer.endObject();
			}
			Backup_OWLUtil.log.debug("End anno property range.......................................");

			// End 'ranges'
			writer.endArray();

			// Start 'superProperties'
			writer.name("superProperties").beginArray();

			final Set<OWLSubAnnotationPropertyOfAxiom> subAnnotationPropertyOfAxiomSet = owlOntology.getSubAnnotationPropertyOfAxioms(directSubProp);

			Backup_OWLUtil.log.debug("Start super anno property.......................................");
			for(final OWLSubAnnotationPropertyOfAxiom subPropertyAxiom: subAnnotationPropertyOfAxiomSet){
				Backup_OWLUtil.log.debug("subPropertyAxiom: " + subPropertyAxiom);
				Backup_OWLUtil.log.debug("Super Prop: " + subPropertyAxiom.getSuperProperty().getIRI().getShortForm());
				writer.beginObject();
				writer.name("superProperty").value(subPropertyAxiom.getSuperProperty().toString());
				writer.endObject();
			}
			Backup_OWLUtil.log.debug("End super anno property.......................................");

			// End 'superProperties'
			writer.endArray();

			// End 'data' object
			writer.endObject();

			final Collection<OWLAnnotationProperty> subAnnoPropSet = EntitySearcher.getSubProperties(directSubProp, owlOntology);

			if (!subAnnoPropSet.isEmpty()) {
				writer.name("children").value(true);
			} else {
				writer.name("children").value(false);
			}

			// writer.name("state").beginObject();
			//
			// writer.name("selected").value("false");
			// writer.name("opened").value("false");
			// writer.name("disabled").value("false");
			// checkbox plugin specific
			// writer.name("checked").value("false");
			// writer.name("undetermined ").value("false");
			// writer.name("type").value("default");
			// writer.name("li_attr").value("");
			// writer.name("a_attr").value("");

			// writer.endObject();
			writer.endObject();
		}

		if(null != parentProp){
			Backup_OWLUtil.log.debug("---- End Parent Prop: " + parentProp.getIRI().getShortForm() + "(" + parentProp.toString() + ") ------\n");
		}
	}

	/**
	 * This method used to build JSON of an OWL Object Property including its details (i.e. id, name, annotations, properties)
	 *
	 * @param writer the writer
	 * @param reasoner the reasoner
	 * @param factory the factory
	 * @param owlOntology the owl ontology
	 * @param parentProp the parent prop
	 * @param subPropSet the sub prop set
	 * @throws Exception the exception
	 */
	public static void buildOWLDataPropDetailsJSON(final JsonWriter writer, final OWLReasoner reasoner, final OWLDataFactory factory, final OWLOntology owlOntology, final OWLDataProperty parentProp, final Set<OWLDataProperty> subPropSet) throws Exception{

		Backup_OWLUtil.log.debug("\n---- Parent DP property: " + parentProp.getIRI().getShortForm() + "(" + parentProp.toString() + ") ------");
		// Display sub classes
		for (final OWLDataProperty directSubProp : HTMLRenderer.toSortedSet(subPropSet)) {
			final IRI directSubPropIRI = directSubProp.getIRI();
			writer.beginObject();
			//writer.name("id").value(directSubProp.getIRI().toString());
			writer.name("text").value(
					directSubPropIRI.getShortForm());
			writer.name("icon").value("fa fa-square dp-class");

			Backup_OWLUtil.log.debug("\nCurrent DP property: " + directSubPropIRI.getShortForm() + " (" + directSubProp.toString() + ")");

			// - this can be anything you want - it is metadata you want attached to the node - you will be able to access and modify it any time later - it has no effect on the visuals of the node.
			writer.name("data").beginObject();
			writer.name("nodeId").value(directSubProp.getIRI().toString());

			// build annotation json string
			Backup_OWLUtil.buildAnnotationJSONDetails(writer, owlOntology, directSubProp);

			// Start 'domains (intersection)'
			writer.name("domains").beginArray();

			final Set<OWLDataPropertyDomainAxiom> domainAxiomSet = owlOntology.getDataPropertyDomainAxioms(directSubProp);

			Backup_OWLUtil.log.debug("Start data property domain.......................................");
			for(final OWLDataPropertyDomainAxiom domainAxiom: domainAxiomSet){
				Backup_OWLUtil.log.debug("Domain axiom: " + domainAxiom);

				final OWLClassExpression oce = domainAxiom.getDomain();

				writer.beginObject();
				writer.name("domainString").value(oce.toString());
				writer.name("isAnonymous").value(oce.isAnonymous());
				writer.endObject();
			}
			Backup_OWLUtil.log.debug("End data property domain.......................................");

			// End 'domains'
			writer.endArray();

			// Start 'ranges (intersection)'
			writer.name("ranges").beginArray();

			final Set<OWLDataPropertyRangeAxiom> rangeAxiomSet = owlOntology.getDataPropertyRangeAxioms(directSubProp);

			Backup_OWLUtil.log.debug("Start data property range.......................................");
			for(final OWLDataPropertyRangeAxiom rangeAxiom: rangeAxiomSet){
				Backup_OWLUtil.log.debug("Range axiom: " + rangeAxiom);

				final OWLDataRange odr = rangeAxiom.getRange();

				writer.beginObject();
				writer.name("rangeString").value(odr.toString());
				writer.endObject();
			}
			Backup_OWLUtil.log.debug("End data property range.......................................");

			// End 'ranges'
			writer.endArray();

			// Start 'equivalent to'
			writer.name("equivalentTo").beginArray();

			final Set<OWLEquivalentDataPropertiesAxiom> equivalentObjectPropAxiom = owlOntology.getEquivalentDataPropertiesAxioms(directSubProp);

			Backup_OWLUtil.log.debug("Start equivalent data property.......................................");
			for(final OWLEquivalentDataPropertiesAxiom equivalentAxiom: equivalentObjectPropAxiom){
				Backup_OWLUtil.log.debug("equivalent axiom: " + equivalentAxiom);
				String equivalentString = equivalentAxiom.getPropertiesMinus(directSubProp).toString();
				equivalentString = StringUtils.replaceChars(equivalentString, StringPool.OPEN_CLOSE_BRACKET, "");

				writer.beginObject();
				writer.name("equivalentToString").value(equivalentString);
				writer.endObject();
			}

			Backup_OWLUtil.log.debug("End equivalent data property.......................................");

			// End 'equivalent to'
			writer.endArray();

			// Start 'subProperty Of'
			writer.name("subPropertyOf").beginArray();

			final Set<OWLSubDataPropertyOfAxiom> subPropertyAxiomSet = owlOntology.getDataSubPropertyAxiomsForSubProperty(directSubProp);

			Backup_OWLUtil.log.debug("Start sub data property.......................................");
			for(final OWLSubDataPropertyOfAxiom subPropertyAxiom: subPropertyAxiomSet){
				Backup_OWLUtil.log.debug("subPropertyAxiom: " + subPropertyAxiom);
				writer.beginObject();
				writer.name("superProperty").value(subPropertyAxiom.getSuperProperty().toString());
				writer.endObject();
			}
			Backup_OWLUtil.log.debug("End sub data property.......................................");

			// End 'subPropertyOf'
			writer.endArray();

			// Start 'disjointWith'
			writer.name("disjointWith").beginArray();

			final Set<OWLDisjointDataPropertiesAxiom> disjointWithAxiomSet = owlOntology.getDisjointDataPropertiesAxioms(directSubProp);

			Backup_OWLUtil.log.debug("Start disjointWith data property.......................................");
			for(final OWLDisjointDataPropertiesAxiom disjointWithPropertyAxiom: disjointWithAxiomSet){
				Backup_OWLUtil.log.debug("disjointWithPropertyAxiom: " + disjointWithPropertyAxiom);
				String disjointAxiomString = disjointWithPropertyAxiom.getPropertiesMinus(directSubProp).toString();
				disjointAxiomString = StringUtils.replaceChars(disjointAxiomString, StringPool.OPEN_CLOSE_BRACKET, "");

				writer.beginObject();
				writer.name("disjointWithString").value(disjointAxiomString);
				writer.endObject();
			}
			Backup_OWLUtil.log.debug("End disjointWith data property.......................................");

			// End 'disjointWith'
			writer.endArray();

			// Start 'characteristics' array
			writer.name("characteristics").beginArray();

			final boolean isFunctional = !owlOntology.getFunctionalDataPropertyAxioms(directSubProp).isEmpty();
			writer.beginObject();
			writer.name("isFunctional").value(isFunctional);
			writer.endObject();

			// End 'characteristics' array
			writer.endArray();

			// End 'data' object
			writer.endObject();

			// Remove bottom data property
			final Set<OWLDataProperty> subPropsOfSubProp = reasoner
					.getSubDataProperties(directSubProp, true).getFlattened();

			subPropsOfSubProp.remove(Backup_OWLUtil.ontologyManager.getBottomDataProperty(reasoner));

			if (!subPropsOfSubProp.isEmpty()) {
				writer.name("children").value(true);
			} else {
				writer.name("children").value(false);
			}

			// writer.name("state").beginObject();
			//
			// writer.name("selected").value("false");
			// writer.name("opened").value("false");
			// writer.name("disabled").value("false");
			// checkbox plugin specific
			// writer.name("checked").value("false");
			// writer.name("undetermined ").value("false");
			// writer.name("type").value("default");
			// writer.name("li_attr").value("");
			// writer.name("a_attr").value("");

			// writer.endObject();
			writer.endObject();
		}

		Backup_OWLUtil.log.debug("---- End Parent Prop: " + parentProp.getIRI().getShortForm() + "(" + parentProp.toString() + ") ------\n");
	}

	/**
	 * This method used to build JSON of an OWL Data Properties including its details (i.e. id, name, annotations, properties)
	 *
	 * @param writer the writer
	 * @param reasoner the reasoner
	 * @param factory the factory
	 * @param owlOntology the owl ontology
	 * @param parentProp the parent prop
	 * @param subPropSet the sub prop set
	 * @throws Exception the exception
	 */
	public static void buildOWLObjectPropDetailsJSON(final JsonWriter writer, final OWLReasoner reasoner, final OWLDataFactory factory, final OWLOntology owlOntology, final OWLObjectProperty parentProp, final Set<OWLObjectPropertyExpression> subPropSet) throws Exception{

		Backup_OWLUtil.log.debug("\n---- Parent property: " + parentProp.getIRI().getShortForm() + "(" + parentProp.toString() + ") ------");
		// Display sub classes
		for (final OWLObjectPropertyExpression directSubProp : HTMLRenderer.toSortedSet(subPropSet)) {
			final IRI directSubPropIRI = directSubProp.asOWLObjectProperty().getIRI();
			writer.beginObject();
			//writer.name("id").value(directSubProp.getIRI().toString());
			writer.name("text").value(
					directSubPropIRI.getShortForm());
			writer.name("icon").value("fa fa-square op-class");

			Backup_OWLUtil.log.debug("\nCurrent property: " + directSubPropIRI.getShortForm() + " (" + directSubProp.toString() + ")");

			// - this can be anything you want - it is metadata you want attached to the node - you will be able to access and modify it any time later - it has no effect on the visuals of the node.
			writer.name("data").beginObject();
			writer.name("nodeId").value(directSubPropIRI.toString());

			// build annotation json string
			Backup_OWLUtil.buildAnnotationJSONDetails(writer, owlOntology, directSubProp);

			// Start 'domains (intersection)'
			writer.name("domains").beginArray();

			final Set<OWLObjectPropertyDomainAxiom> domainAxiomSet = owlOntology.getObjectPropertyDomainAxioms(directSubProp);

			Backup_OWLUtil.log.debug("Start object property domain.......................................");
			for(final OWLObjectPropertyDomainAxiom domainAxiom: domainAxiomSet){
				Backup_OWLUtil.log.debug("Domain axiom: " + domainAxiom);

				final OWLClassExpression oce = domainAxiom.getDomain();

				writer.beginObject();
				writer.name("domainString").value(oce.toString());
				writer.name("isAnonymous").value(oce.isAnonymous());
				writer.endObject();
			}
			Backup_OWLUtil.log.debug("End object property domain.......................................");

			// End 'domains'
			writer.endArray();

			// Start 'inverse of'
			writer.name("inverseOf").beginArray();

			final Set<OWLInverseObjectPropertiesAxiom> inverseObjectPropAxiomSet = owlOntology.getInverseObjectPropertyAxioms(directSubProp);

			Backup_OWLUtil.log.debug("Start inverse object property.......................................");
			for(final OWLInverseObjectPropertiesAxiom inverseObjectPropAxiom: inverseObjectPropAxiomSet){
				final String firstProp = inverseObjectPropAxiom.getFirstProperty().asOWLObjectProperty().getIRI().toString();
				final String secondProp = inverseObjectPropAxiom.getSecondProperty().asOWLObjectProperty().getIRI().toString();
				final String directSubPropString = directSubPropIRI.toString();

				OWLObjectPropertyExpression ope = null;

				writer.beginObject();

				if(firstProp.equals(directSubPropString)) {
					ope = inverseObjectPropAxiom.getSecondProperty();
				} else if (secondProp.equals(directSubPropString)) {
					ope = inverseObjectPropAxiom.getFirstProperty();
				}

				writer.name("inverseOfProperty").value(ope.toString());
				writer.name("isAnonymous").value(ope.isAnonymous());

				Backup_OWLUtil.log.debug("Inverse OP axiom: " + inverseObjectPropAxiom);
				writer.endObject();
			}
			Backup_OWLUtil.log.debug("End inverse object property.......................................");

			// End 'inverseOf'
			writer.endArray();

			// Start 'ranges (intersection)'
			writer.name("ranges").beginArray();

			final Set<OWLObjectPropertyRangeAxiom> rangeAxiomSet = owlOntology.getObjectPropertyRangeAxioms(directSubProp);

			Backup_OWLUtil.log.debug("Start object property range.......................................");
			for(final OWLObjectPropertyRangeAxiom rangeAxiom: rangeAxiomSet){
				Backup_OWLUtil.log.debug("Range axiom: " + rangeAxiom);

				final OWLClassExpression oce = rangeAxiom.getRange();

				writer.beginObject();
				writer.name("isAnonymous").value(oce.isAnonymous());
				writer.name("rangeString").value(oce.toString());
				writer.endObject();
			}
			Backup_OWLUtil.log.debug("End object property range.......................................");

			// End 'ranges'
			writer.endArray();

			// Start 'equivalent to'
			writer.name("equivalentTo").beginArray();

			final Set<OWLEquivalentObjectPropertiesAxiom> equivalentObjectPropAxiom = owlOntology.getEquivalentObjectPropertiesAxioms(directSubProp);

			Backup_OWLUtil.log.debug("Start equivalent object property.......................................");
			for(final OWLEquivalentObjectPropertiesAxiom equivalentAxiom: equivalentObjectPropAxiom){
				Backup_OWLUtil.log.debug("equivalent axiom: " + equivalentAxiom);
				boolean isAnonymous = true;

				final Set<OWLObjectPropertyExpression> opeSet = equivalentAxiom.getPropertiesMinus(directSubProp);

				if(opeSet.size() == 1){
					final OWLObjectPropertyExpression ope = (OWLObjectPropertyExpression) opeSet.toArray()[0];
					isAnonymous = ope.isAnonymous();
				}

				String equivalentString = opeSet.toString();
				equivalentString = StringUtils.replaceChars(equivalentString, StringPool.OPEN_CLOSE_BRACKET, "");

				writer.beginObject();
				writer.name("equivalentToString").value(equivalentString);
				writer.name("isAnonymous").value(isAnonymous);
				writer.endObject();
			}

			Backup_OWLUtil.log.debug("End equivalent object property.......................................");

			// End 'equivalent to'
			writer.endArray();

			// Start 'subProperty Of'
			writer.name("subPropertyOf").beginArray();

			final Set<OWLSubObjectPropertyOfAxiom> subPropertyAxiomSet = owlOntology.getObjectSubPropertyAxiomsForSubProperty(directSubProp);

			Backup_OWLUtil.log.debug("Start sub object property.......................................");
			for(final OWLSubObjectPropertyOfAxiom subPropertyAxiom: subPropertyAxiomSet){
				Backup_OWLUtil.log.debug("subPropertyAxiom: " + subPropertyAxiom);

				final OWLObjectPropertyExpression ope = subPropertyAxiom.getSuperProperty();

				writer.beginObject();
				writer.name("superProperty").value(ope.toString());
				writer.name("isAnonymous").value(ope.isAnonymous());
				writer.endObject();
			}
			Backup_OWLUtil.log.debug("End sub object property.......................................");

			// End 'subPropertyOf'
			writer.endArray();

			// Start 'disjointWith'
			writer.name("disjointWith").beginArray();

			final Set<OWLDisjointObjectPropertiesAxiom> disjointWithAxiomSet = owlOntology.getDisjointObjectPropertiesAxioms(directSubProp);

			Backup_OWLUtil.log.debug("Start disjointWith object property.......................................");
			for(final OWLDisjointObjectPropertiesAxiom disjointWithPropertyAxiom: disjointWithAxiomSet){
				Backup_OWLUtil.log.debug("disjointWithPropertyAxiom: " + disjointWithPropertyAxiom);

				boolean isAnonymous = true;

				final Set<OWLObjectPropertyExpression> opeSet = disjointWithPropertyAxiom.getPropertiesMinus(directSubProp);

				if(opeSet.size() == 1){
					final OWLObjectPropertyExpression ope = (OWLObjectPropertyExpression) opeSet.toArray()[0];
					isAnonymous = ope.isAnonymous();
				}

				String disjointAxiomString = opeSet.toString();
				disjointAxiomString = StringUtils.replaceChars(disjointAxiomString, StringPool.OPEN_CLOSE_BRACKET, "");

				writer.beginObject();
				writer.name("disjointWithString").value(disjointAxiomString);
				writer.name("isAnonymous").value(isAnonymous);
				writer.endObject();
			}
			Backup_OWLUtil.log.debug("End disjointWith object property.......................................");

			// End 'disjointWith'
			writer.endArray();

			// Start 'characteristics' array
			writer.name("characteristics").beginArray();

			final boolean isFunctional = !owlOntology.getFunctionalObjectPropertyAxioms(directSubProp).isEmpty();
			final boolean isInverseFunctional = !owlOntology.getInverseFunctionalObjectPropertyAxioms(directSubProp).isEmpty();
			final boolean isTransitive = !owlOntology.getTransitiveObjectPropertyAxioms(directSubProp).isEmpty();
			final boolean isSymmetric = !owlOntology.getSymmetricObjectPropertyAxioms(directSubProp).isEmpty();
			final boolean isAsymmetric = !owlOntology.getAsymmetricObjectPropertyAxioms(directSubProp).isEmpty();
			final boolean isReflexive = !owlOntology.getReflexiveObjectPropertyAxioms(directSubProp).isEmpty();
			final boolean isIrreflexive = !owlOntology.getIrreflexiveObjectPropertyAxioms(directSubProp).isEmpty();

			// These characteristics should be declared in this order, which is isFunction -> isInverseFunctional -> ... -> isIrreflexive
			// So we can display it into UI easily
			writer.beginObject();
			writer.name("isFunctional").value(isFunctional);
			writer.endObject();

			writer.beginObject();
			writer.name("isInverseFunctional").value(isInverseFunctional);
			writer.endObject();

			writer.beginObject();
			writer.name("isTransitive").value(isTransitive);
			writer.endObject();

			writer.beginObject();
			writer.name("isSymmetric").value(isSymmetric);
			writer.endObject();

			writer.beginObject();
			writer.name("isAsymmetric").value(isAsymmetric);
			writer.endObject();

			writer.beginObject();
			writer.name("isReflexive").value(isReflexive);
			writer.endObject();

			writer.beginObject();
			writer.name("isIrreflexive").value(isIrreflexive);
			writer.endObject();

			// End 'characteristics' array
			writer.endArray();

			// End 'data' object
			writer.endObject();

			// Remove bottom object property
			final Set<OWLObjectPropertyExpression> subPropsOfSubProp = reasoner
					.getSubObjectProperties(directSubProp, true).getFlattened();

			final Set<OWLObjectProperty> filteredSubProperties = new HashSet<OWLObjectProperty>();

			// Because each of sub property of top object property always has its inverse object prop or bottom object prop ==> filter all of them
			for (final OWLObjectPropertyExpression subProp: subPropsOfSubProp) {
				if(!(subProp instanceof OWLObjectInverseOf) && !subProp.isOWLBottomObjectProperty()){
					filteredSubProperties.add(subProp.asOWLObjectProperty());
				}
			}

			if (!filteredSubProperties.isEmpty()) {
				writer.name("children").value(true);
			} else {
				writer.name("children").value(false);
			}

			// writer.name("state").beginObject();
			//
			// writer.name("selected").value("false");
			// writer.name("opened").value("false");
			// writer.name("disabled").value("false");
			// checkbox plugin specific
			// writer.name("checked").value("false");
			// writer.name("undetermined ").value("false");
			// writer.name("type").value("default");
			// writer.name("li_attr").value("");
			// writer.name("a_attr").value("");

			// writer.endObject();
			writer.endObject();
		}

		Backup_OWLUtil.log.debug("---- End Parent Prop: " + parentProp.getIRI().getShortForm() + "(" + parentProp.toString() + ") ------\n");
	}

	/**
	 * Build details of an OWL class as JSON String.
	 *
	 * @param writer the writer
	 * @param reasoner the reasoner
	 * @param factory the factory
	 * @param owlOntology the owl ontology
	 * @param owlClass the owl class
	 * @throws Exception the exception
	 */
	public static void buildOWLClassJSONDetails (final JsonWriter writer, final OWLReasoner reasoner, final OWLDataFactory factory, final OWLOntology owlOntology, final OWLClass owlClass) throws Exception {

		if(owlClass.isOWLThing()){
			writer.beginArray();
		}

		writer.beginObject();

		if(owlClass.isOWLThing()){
			writer.name("id").value(owlClass.getIRI().toString());
		}
		//writer.name("id").value(owlClass.getIRI().toString());
		writer.name("text").value(
				owlClass.getIRI().getShortForm());
		writer.name("icon").value("fa fa-circle class-color");
		writer.name("nodeId").value(owlClass.getIRI().toString());
		Backup_OWLUtil.log.debug("\nCurrent class: " + owlClass.getIRI().getShortForm() + " (" + owlClass.toString() + ")");

		// - this can be anything you want - it is metadata you want attached to the node - you will be able to access and modify it any time later - it has no effect on the visuals of the node.
		writer.name("data").beginObject();
		writer.name("nodeId").value(owlClass.getIRI().toString());

		// build annotation json string
		Backup_OWLUtil.buildAnnotationJSONDetails(writer, owlOntology, owlClass);

		// subClassOf
		writer.name("superClasses").beginArray();

		final Set<OWLSubClassOfAxiom> subClassAxiomSet = owlOntology.getSubClassAxiomsForSubClass(owlClass);

		final boolean hasSubClassAxiom = subClassAxiomSet.size() > 0;

		if(hasSubClassAxiom){
			Backup_OWLUtil.log.debug("--------------------- Start ---------------------");
		}

		for (final OWLSubClassOfAxiom subClassAxiom: subClassAxiomSet) {

			final OWLClassExpression owlClassExpression = subClassAxiom.getSuperClass();
			//final ClassExpressionType classExpressionType = owlClassExpression.getClassExpressionType();

			Backup_OWLUtil.log.debug("OWL Class Expression: " + owlClassExpression.toString());
			Backup_OWLUtil.log.debug("Subclass axiom: " + subClassAxiom.toString());

			writer.beginObject();
			writer.name("isAnonymous").value(owlClassExpression.isAnonymous());
			writer.name("superClass").value(owlClassExpression.toString());
			writer.endObject();
		}

		if(hasSubClassAxiom){
			Backup_OWLUtil.log.debug("--------------------- End ---------------------");
		}

		// End 'superClasses' array, i.e. subClassOf
		writer.endArray();

		// Start 'equivalentTo'
		writer.name("equivalentClasses").beginArray();

		final Set<OWLEquivalentClassesAxiom> equivalentClassesAxiomSet = owlOntology.getEquivalentClassesAxioms(owlClass);

		if(equivalentClassesAxiomSet.size() > 0){
			Backup_OWLUtil.log.debug("--------------- Equivalent classes ---------------");
		}

		for(final OWLEquivalentClassesAxiom equivalentClassesAxiom: equivalentClassesAxiomSet){
			Backup_OWLUtil.log.debug("Equivalent class: " + equivalentClassesAxiom.toString());
			boolean isAnonymous = true;
			final Set<OWLClassExpression> oceSet = equivalentClassesAxiom.getClassExpressionsMinus(owlClass);

			String equivalentClassString = oceSet.toString();

			if(oceSet.size() == 1){
				final OWLClassExpression oce = (OWLClassExpression) oceSet.toArray()[0];
				isAnonymous = oce.isAnonymous();
			}

			equivalentClassString = StringUtils.replaceChars(equivalentClassString, StringPool.OPEN_CLOSE_BRACKET, StringPool.BLANK);

			writer.beginObject();
			writer.name("isAnonymous").value(isAnonymous);
			writer.name("equivalentClass").value(equivalentClassString);
			writer.endObject();
		}

		// End 'equivalentClasses' array
		writer.endArray();

		// Start 'disjointWith'
		writer.name("disjointWith").beginArray();

		final Set<OWLDisjointClassesAxiom> disjointClassesAxiomSet = owlOntology.getDisjointClassesAxioms(owlClass);

		if(disjointClassesAxiomSet.size() > 0){
			Backup_OWLUtil.log.debug("--------------- Disjoint classes ---------------");
		}

		for(final OWLDisjointClassesAxiom disjointClassesAxiom: disjointClassesAxiomSet){
			Backup_OWLUtil.log.debug("Disjoint class: " + disjointClassesAxiom.toString());

			boolean isAnonymous = true;
			final Set<OWLClassExpression> oceSet = disjointClassesAxiom.getClassExpressionsMinus(owlClass);

			String disjointClassString = oceSet.toString();

			if(oceSet.size() == 1){
				final OWLClassExpression oce = (OWLClassExpression) oceSet.toArray()[0];
				isAnonymous = oce.isAnonymous();
			}

			disjointClassString = StringUtils.replaceChars(disjointClassString, StringPool.OPEN_CLOSE_BRACKET, StringPool.BLANK);

			writer.beginObject();
			writer.name("isAnonymous").value(isAnonymous);
			writer.name("disjointClass").value(disjointClassString);
			writer.endObject();
		}

		// End 'disjointWith'
		writer.endArray();

		// Start 'disjointUnionOf'
		writer.name("disjointUnionOf").beginArray();

		final Set<OWLDisjointUnionAxiom> disjointUnionOfAxiomSet = owlOntology.getDisjointUnionAxioms(owlClass);

		if(disjointUnionOfAxiomSet.size() > 0){
			Backup_OWLUtil.log.debug("--------------- Disjoint Union of classes ---------------");
		}

		for(final OWLDisjointUnionAxiom disjointUnionOfClassesAxiom: disjointUnionOfAxiomSet){
			Backup_OWLUtil.log.debug("Disjoint Union of class: " + disjointUnionOfClassesAxiom.toString());

			boolean isAnonymous = true;
			final Set<OWLClassExpression> oceSet = disjointUnionOfClassesAxiom.getClassExpressions();

			String disjointClassString = oceSet.toString();

			if(oceSet.size() == 1){
				final OWLClassExpression oce = (OWLClassExpression) oceSet.toArray()[0];
				isAnonymous = oce.isAnonymous();
			}

			disjointClassString = StringUtils.replaceChars(disjointClassString, StringPool.OPEN_CLOSE_BRACKET, StringPool.BLANK);

			writer.beginObject();
			writer.name("isAnonymous").value(isAnonymous);
			writer.name("disjointClass").value(disjointClassString);
			writer.endObject();
		}

		// End 'disjointUnionOf'
		writer.endArray();

		// Start 'targetForKey'
		writer.name("targetForKey").beginArray();

		final Set<OWLHasKeyAxiom> hasKeyAxiomSet = owlOntology.getHasKeyAxioms(owlClass);

		if(hasKeyAxiomSet.size() > 0){
			Backup_OWLUtil.log.debug("--------------- Has Key Axiom Set ---------------");
		}

		for(final OWLHasKeyAxiom hasKeyAxiom: hasKeyAxiomSet){
			Backup_OWLUtil.log.debug("Has Key Axiom: " + hasKeyAxiom.toString());

			writer.beginObject();
			writer.name("key").value(hasKeyAxiom.toString());
			writer.endObject();
		}

		// End 'targetForKey'
		writer.endArray();

		// Start 'members'
		writer.name("members").beginArray();

		Set<OWLNamedIndividual> individualSet = null;

		if(owlClass.isOWLThing()){
			individualSet = owlOntology.getIndividualsInSignature();
		} else {
			individualSet = reasoner.getInstances(owlClass, true).getFlattened();
		}

		if(!individualSet.isEmpty()){
			Backup_OWLUtil.log.debug("--------------- Individual Set ---------------");
		}

		// We do not display all of individuals, so skip this loop, and it is only used for testing purpose
		// Individuals of each class will be displayed in 'Individuals' tab, which can be accessed via getClassIndividuals() method
		//		for(final OWLNamedIndividual individual: individualSet){
		//			this.log.debug("Individual: " + individual.toString());
		//
		//			writer.beginObject();
		//			writer.name("invididual").value(individual.toString());
		//			writer.endObject();
		//		}

		writer.beginObject();
		writer.name("numberOfIndividuals").value(individualSet.size());
		writer.endObject();

		// End 'members'
		writer.endArray();

		// End 'data' object
		writer.endObject();

		// Have to remove owl:nothing class, it always displays as child of every class
		final Set<OWLClass> subClasses = reasoner
				.getSubClasses(owlClass, true).getFlattened();
		subClasses.remove(factory.getOWLNothing());

		// Query sub classes if current class is owl:Thing, else do not query anything
		if (subClasses != null
				&& !subClasses.isEmpty()) {

			if(owlClass.isOWLThing()){
				writer.name("children").beginArray();

				// Display sub classes
				for (final OWLClass directSubClass : HTMLRenderer.toSortedSet(subClasses)) {
					Backup_OWLUtil.buildOWLClassJSONDetails(writer, reasoner, factory, owlOntology, directSubClass);
				}

				writer.endArray();
			} else {
				writer.name("children").value(true);
			}

		} else {
			writer.name("children").value(false);
		}

		if (owlClass.isOWLThing()) {
			writer.name("state").beginObject();

			writer.name("selected").value("false");
			writer.name("opened").value("true");
			// writer.name("disabled").value("false");
			// checkbox plugin specific
			// writer.name("checked").value("false");
			// writer.name("undetermined ").value("false");

			writer.endObject();
			writer.name("type").value("default");
			// writer.name("li_attr").value("");
			// writer.name("a_attr").value("");
		}

		// writer.name("state").beginObject();
		//
		// writer.name("selected").value("false");
		// writer.name("opened").value("false");
		// writer.name("disabled").value("false");
		// checkbox plugin specific
		// writer.name("checked").value("false");
		// writer.name("undetermined ").value("false");
		// writer.name("type").value("default");
		// writer.name("li_attr").value("");
		// writer.name("a_attr").value("");

		// writer.endObject();
		writer.endObject();

		if(owlClass.isOWLThing()){
			writer.endArray();
		}
	}

	/**
	 * Build details of an OWL class as JSON String.
	 *
	 * @param writer the writer
	 * @param reasoner the reasoner
	 * @param factory the factory
	 * @param owlOntology the owl ontology
	 * @param owlClass the owl class
	 * @throws Exception the exception
	 */
	public static void buildOWLClassAndItsIndividuals (final JsonWriter writer, final OWLReasoner reasoner, final OWLDataFactory factory, final OWLOntology owlOntology, final OWLClass owlClass) throws Exception {

		if(owlClass.isOWLThing()){
			writer.beginArray();
		}

		writer.beginObject();

		if(owlClass.isOWLThing()){
			writer.name("id").value(OWLConstants.OWL_THING_CLASS_ID);
		} else {
			writer.name("id").value(owlClass.getIRI().toString());
		}

		writer.name("text").value(
				owlClass.getIRI().getShortForm());
		writer.name("icon").value("fa fa-circle class-color");
		Backup_OWLUtil.log.debug("\nCurrent class: " + owlClass.getIRI().getShortForm() + " (" + owlClass.toString() + ")");

		// - this can be anything you want - it is metadata you want attached to the node - you will be able to access and modify it any time later - it has no effect on the visuals of the node.
		writer.name("data").beginObject();
		writer.name("nodeId").value(owlClass.getIRI().toString());

		// Start 'members'
		writer.name("members").beginArray();

		// Start a new individual JSTree of this OWL class
		//			writer.beginObject();
		//			writer.name("id").value("individualParentNode");
		//			writer.name("text").value(
		//					owlClass.getIRI().getShortForm());
		//			writer.name("icon").value("fa fa-circle class-color");
		//			writer.name("children").beginArray();

		Set<OWLNamedIndividual> classInstances = null;

		if(owlClass.isOWLThing()) {
			classInstances = owlOntology.getIndividualsInSignature();
		} else {
			classInstances = reasoner.getInstances(owlClass, true).getFlattened();
		}

		if(!classInstances.isEmpty()){
			Backup_OWLUtil.log.debug("--------------- Individual Set ---------------");
		}

		//		StringBuilder sb = new StringBuilder("<ul>");
		//		int counter = 0;

		for(final OWLNamedIndividual individual: HTMLRenderer.toSortedSet(classInstances)){
			Backup_OWLUtil.buildOWLNamedIndividualJSONDetails(writer, owlOntology, individual);
			//buildOWLNamedIndividualHTML(sb, owlOntology, individual, counter++);
		}

		//sb.append("</ul>");

		// End 'children' array
		//			writer.endArray();

		// start 'state' object of js tree
		//			writer.name("state").beginObject();
		//
		//			writer.name("selected").value("false");
		//			writer.name("opened").value("true");
		// writer.name("disabled").value("false");
		// checkbox plugin specific
		// writer.name("checked").value("false");
		// writer.name("undetermined ").value("false");

		// End 'state' object
		//			writer.endObject();
		//			writer.name("type").value("default");
		// writer.name("li_attr").value("");
		// writer.name("a_attr").value("");

		// End 'individual' jstree
		//			writer.endObject();

		// End 'members'
		writer.endArray();

		//writer.name("individualJsTreeHTML").value(sb.toString());

		// End 'data' object
		writer.endObject();

		// Have to remove owl:nothing class, it always displays as child of every class
		final Set<OWLClass> subClasses = reasoner
				.getSubClasses(owlClass, true).getFlattened();
		subClasses.remove(factory.getOWLNothing());

		// Query sub classes if current class is owl:Thing, else do not query anything
		if (subClasses != null
				&& !subClasses.isEmpty()) {

			if(owlClass.isOWLThing()){
				writer.name("children").beginArray();

				// Display sub classes
				for (final OWLClass directSubClass : HTMLRenderer.toSortedSet(subClasses)) {
					Backup_OWLUtil.buildOWLClassAndItsIndividuals(writer, reasoner, factory, owlOntology, directSubClass);
				}

				writer.endArray();
			} else {
				writer.name("children").value(true);
			}

		} else {
			writer.name("children").value(false);
		}

		if (owlClass.isOWLThing()) {
			writer.name("state").beginObject();

			writer.name("selected").value("false");
			writer.name("opened").value("true");
			// writer.name("disabled").value("false");
			// checkbox plugin specific
			// writer.name("checked").value("false");
			// writer.name("undetermined ").value("false");

			writer.endObject();
			writer.name("type").value("default");
			// writer.name("li_attr").value("");
			// writer.name("a_attr").value("");
		}

		// writer.name("state").beginObject();
		//
		// writer.name("selected").value("false");
		// writer.name("opened").value("false");
		// writer.name("disabled").value("false");
		// checkbox plugin specific
		// writer.name("checked").value("false");
		// writer.name("undetermined ").value("false");
		// writer.name("type").value("default");
		// writer.name("li_attr").value("");
		// writer.name("a_attr").value("");

		// writer.endObject();
		writer.endObject();

		if(owlClass.isOWLThing()){
			writer.endArray();
		}
	}



	/**
	 * Build annotation json details of a given owl (generic) object.
	 *
	 * @param writer the writer
	 * @param owlOntology the owl ontology
	 * @param owlObject the owl object
	 * @throws Exception the exception
	 */
	public static void buildAnnotationJSONDetails (final JsonWriter writer, final OWLOntology owlOntology, final Object owlObject) throws Exception {
		if(!(owlObject instanceof HasIRI)){
			throw new Exception("Not an object that extends HasIRI class");
		}

		// Start 'annotations' array
		writer.name("annotations").beginArray();

		final Class<?> owlClass = owlObject.getClass();

		final Method getIRIMethod = owlClass.getMethod("getIRI", null);
		final IRI objectIRI = (IRI) getIRIMethod.invoke(owlObject, null);

		final Set<OWLAnnotationAssertionAxiom> annotationAssertionSet = owlOntology.getAnnotationAssertionAxioms(objectIRI);

		for (final OWLAnnotationAssertionAxiom owlAnnotationAssertion: annotationAssertionSet) {
			Backup_OWLUtil.log.debug("Annotation Property: " + owlAnnotationAssertion.toString());
			Backup_OWLUtil.log.debug("Name: " + owlAnnotationAssertion.getProperty().toString());

			final OWLAnnotationProperty oap = owlAnnotationAssertion.getProperty();

			writer.beginObject();
			writer.name("annotationName").value(oap.toString());
			writer.name("annotationNameIRI").value(oap.getIRI().toString());

			final OWLLiteral value = owlAnnotationAssertion.getValue().asLiteral().get();
			final String literal = EscapeUtils.escapeString(value.getLiteral());
			final boolean isRDFPlainLiteral = value.isRDFPlainLiteral();

			if(isRDFPlainLiteral){
				writer.name("annotationLanguage").value(value.getLang());
				Backup_OWLUtil.log.debug("Language: " + value.getLang());
			} else {
				writer.name("annotationDatatype").value(value.getDatatype().toString());
				writer.name("annotationDatatypeIRI").value(value.getDatatype().getIRI().toString());
				Backup_OWLUtil.log.debug("Datatype: "+ value.getDatatype().toString());
			}

			Backup_OWLUtil.log.debug("isRDFPlainLiteral: " + isRDFPlainLiteral);

			writer.name("isRDFPlainLiteral").value(isRDFPlainLiteral);
			writer.name("annotationValue").value(literal);

			writer.endObject();
		}

		// End 'annotations' array
		writer.endArray();
	}

	/**
	 * Builds the owl data prop hierarchy.
	 *
	 * @param writer the writer
	 * @param reasoner the reasoner
	 * @param factory the factory
	 * @param owlOntology the owl ontology
	 * @param dataProp the data prop
	 * @throws Exception the exception
	 */
	public static void buildOWLDataPropHierarchy(final JsonWriter writer, final OWLReasoner reasoner, final OWLDataFactory factory, final OWLOntology owlOntology, final OWLDataProperty dataProp) throws Exception{

		final IRI dataPropIRI = dataProp.getIRI();

		writer.beginObject();
		writer.name("id").value(dataPropIRI.toString());
		writer.name("text").value(dataPropIRI.getShortForm());
		writer.name("icon").value("fa fa-square dp-class");

		writer.name("data").beginObject();
		writer.name("nodeId").value(dataPropIRI.toString());
		writer.endObject();

		final Set<OWLDataProperty> dataSubProps = reasoner
				.getSubDataProperties(dataProp, true).getFlattened();

		dataSubProps.remove(Backup_OWLUtil.ontologyManager.getBottomDataProperty(reasoner));

		if (!dataSubProps.isEmpty()) {
			writer.name("children").beginArray();

			for(final OWLDataProperty odp: dataSubProps){
				Backup_OWLUtil.buildOWLDataPropHierarchy(writer, reasoner, factory, owlOntology, odp);
			}

			writer.endArray();
		} else {
			writer.name("children").value(false);
		}

		if (dataProp.isOWLTopDataProperty()) {
			writer.name("state").beginObject();
			writer.name("selected").value("false");
			writer.name("opened").value("true");
			writer.endObject();
		}

		// writer.name("state").beginObject();
		//
		// writer.name("selected").value("false");
		// writer.name("opened").value("false");
		// writer.name("disabled").value("false");
		// checkbox plugin specific
		// writer.name("checked").value("false");
		// writer.name("undetermined ").value("false");
		// writer.name("type").value("default");
		// writer.name("li_attr").value("");
		// writer.name("a_attr").value("");

		// writer.endObject();
		writer.endObject();
	}

	/**
	 * Builds the owl object prop hierarchy.
	 *
	 * @param writer the writer
	 * @param reasoner the reasoner
	 * @param factory the factory
	 * @param owlOntology the owl ontology
	 * @param objectProp the object prop
	 * @throws Exception the exception
	 */
	public static void buildOWLObjectPropHierarchy(final JsonWriter writer, final OWLReasoner reasoner, final OWLDataFactory factory, final OWLOntology owlOntology, final OWLObjectPropertyExpression objectProp) throws Exception{
		final OWLObjectProperty owlObjectProp = objectProp.asOWLObjectProperty();

		final IRI objectPropIRI = objectProp.asOWLObjectProperty().getIRI();
		writer.beginObject();
		writer.name("id").value(objectPropIRI.toString());
		writer.name("text").value(objectPropIRI.getShortForm());
		writer.name("icon").value("fa fa-square op-class");

		writer.name("data").beginObject();
		writer.name("nodeId").value(objectPropIRI.toString());
		writer.endObject();

		// Remove bottom object property
		final Set<OWLObjectPropertyExpression> subProps = reasoner
				.getSubObjectProperties(objectProp, true).getFlattened();

		final Set<OWLObjectPropertyExpression> filteredSubProperties = subProps
				.stream()
				.filter(subProp -> !(subProp instanceof OWLObjectInverseOf)
						&& !subProp.isOWLBottomObjectProperty())
						.collect(Collectors.toCollection(TreeSet::new));

		if (!filteredSubProperties.isEmpty()) {
			writer.name("children").beginArray();

			for(final OWLObjectPropertyExpression ope: filteredSubProperties){
				Backup_OWLUtil.buildOWLObjectPropHierarchy(writer, reasoner, factory, owlOntology, ope);
			}

			writer.endArray();
		} else {
			writer.name("children").value(false);
		}

		if (owlObjectProp.isOWLTopObjectProperty()) {
			writer.name("state").beginObject();
			writer.name("selected").value("false");
			writer.name("opened").value("true");
			writer.endObject();
		}

		// writer.name("state").beginObject();
		//
		// writer.name("selected").value("false");
		// writer.name("opened").value("false");
		// writer.name("disabled").value("false");
		// checkbox plugin specific
		// writer.name("checked").value("false");
		// writer.name("undetermined ").value("false");
		// writer.name("type").value("default");
		// writer.name("li_attr").value("");
		// writer.name("a_attr").value("");

		// writer.endObject();
		writer.endObject();
	}

	/**
	 * Builds the owl annotation hierarchy.
	 *
	 * @param writer the writer
	 * @param reasoner the reasoner
	 * @param factory the factory
	 * @param owlOntology the owl ontology
	 * @param annoProp the anno prop
	 * @throws Exception the exception
	 */
	public static void buildOWLAnnotationHierarchy(final JsonWriter writer, final OWLReasoner reasoner, final OWLDataFactory factory, final OWLOntology owlOntology, final OWLAnnotationProperty annoProp) throws Exception{
		final IRI annoPropIRI = annoProp.getIRI();
		writer.beginObject();
		writer.name("id").value(annoPropIRI.toString());
		writer.name("text").value(
				annoPropIRI.getShortForm());
		writer.name("icon").value("fa fa-square ap-class");

		// - this can be anything you want - it is metadata you want attached to the node - you will be able to access and modify it any time later - it has no effect on the visuals of the node.
		writer.name("data").beginObject();
		writer.name("nodeId").value(annoPropIRI.toString());

		// End 'data' object
		writer.endObject();

		final Collection<OWLAnnotationProperty> subAnnoPropSet = EntitySearcher.getSubProperties(annoProp, owlOntology);

		if (!subAnnoPropSet.isEmpty()) {
			writer.name("children").beginArray();

			for(final OWLAnnotationProperty oap: subAnnoPropSet){
				Backup_OWLUtil.buildOWLAnnotationHierarchy(writer, reasoner, factory, owlOntology, oap);
			}

			writer.endArray();
		} else {
			writer.name("children").value(false);
		}

		// writer.name("state").beginObject();
		//
		// writer.name("selected").value("false");
		// writer.name("opened").value("false");
		// writer.name("disabled").value("false");
		// checkbox plugin specific
		// writer.name("checked").value("false");
		// writer.name("undetermined ").value("false");
		// writer.name("type").value("default");
		// writer.name("li_attr").value("");
		// writer.name("a_attr").value("");

		// writer.endObject();
		writer.endObject();
	}
}

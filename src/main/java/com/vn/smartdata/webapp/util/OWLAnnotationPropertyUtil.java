package com.vn.smartdata.webapp.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semanticweb.owlapi.model.HasIRI;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.EscapeUtils;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import com.google.gson.stream.JsonWriter;
import com.vn.smartdata.constants.OWLConstants;
import com.vn.smartdata.manager.AnnotationManager;
import com.vn.smartdata.model.Annotation;
import com.vn.smartdata.model.Individual;
import com.vn.smartdata.model.User;
import com.vn.smartdata.webapp.controller.OntologyController;


/**
 * The Class OWLUtil.
 *
 * @author TAQ1HC
 */
public class OWLAnnotationPropertyUtil {

	protected final transient static Log log = LogFactory.getLog(OWLAnnotationPropertyUtil.class);

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

		// Display sub classes
		for (final OWLAnnotationProperty directSubProp : HTMLRenderer.toSortedSet(subPropSet)) {
			final IRI directSubPropIRI = directSubProp.getIRI();
			writer.beginObject();
			//writer.name("id").value(directSubProp.getIRI().toString());
			writer.name("text").value(
					directSubPropIRI.getShortForm());
			writer.name("icon").value("fa fa-square ap-class");

			// - this can be anything you want - it is metadata you want attached to the node - you will be able to access and modify it any time later - it has no effect on the visuals of the node.
			writer.name("data").beginObject();
			writer.name("nodeId").value(directSubProp.getIRI().toString());

			// Build annotation json
			OWLAnnotationPropertyUtil.buildAnnotationJSONDetails(writer, owlOntology, directSubProp);

			// Start 'domains (intersection)'
			writer.name("domains").beginArray();

			final Set<OWLAnnotationPropertyDomainAxiom> domainAxiomSet = owlOntology.getAnnotationPropertyDomainAxioms(directSubProp);

			for(final OWLAnnotationPropertyDomainAxiom domainAxiom: domainAxiomSet){
				writer.beginObject();
				writer.name("domainString").value(OntologyController.htmlRenderer.getShortFormInHTML(domainAxiom.getDomain()));
				writer.endObject();
			}

			// End 'domains'
			writer.endArray();

			// Start 'ranges (intersection)'
			writer.name("ranges").beginArray();

			final Set<OWLAnnotationPropertyRangeAxiom> rangeAxiomSet = owlOntology.getAnnotationPropertyRangeAxioms(directSubProp);

			for(final OWLAnnotationPropertyRangeAxiom rangeAxiom: rangeAxiomSet){
				writer.beginObject();
				writer.name("rangeString").value(OntologyController.htmlRenderer.getShortFormInHTML(rangeAxiom.getRange()));
				writer.endObject();
			}

			// End 'ranges'
			writer.endArray();

			// Start 'superProperties'
			writer.name("superProperties").beginArray();

			final Set<OWLSubAnnotationPropertyOfAxiom> subAnnotationPropertyOfAxiomSet = owlOntology.getSubAnnotationPropertyOfAxioms(directSubProp);

			for(final OWLSubAnnotationPropertyOfAxiom subPropertyAxiom: subAnnotationPropertyOfAxiomSet){
				writer.beginObject();
				writer.name("superProperty").value(subPropertyAxiom.getSuperProperty().toString());
				writer.endObject();
			}

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
			writer.beginObject();
			OWLAnnotationPropertyUtil.buildOWLAnnotationAssertionAxiomJSON(owlAnnotationAssertion, writer);
			writer.endObject();
		}

		// End 'annotations' array
		writer.endArray();
	}

	/**
	 * Builds the owl annotation assertion axiom json.
	 *
	 * @param owlAnnotationAssertion the owl annotation assertion
	 * @param writer the writer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void buildOWLAnnotationAssertionAxiomJSON (final OWLAnnotationAssertionAxiom owlAnnotationAssertion, final JsonWriter writer) throws IOException {
		final OWLAnnotationProperty oap = owlAnnotationAssertion.getProperty();

		writer.name("annotationName").value(oap.toString());
		writer.name("annotationNameIRI").value(oap.getIRI().toString());

		final OWLLiteral value = owlAnnotationAssertion.getValue().asLiteral().get();
		final String literal = EscapeUtils.escapeString(value.getLiteral());
		final boolean isRDFPlainLiteral = value.isRDFPlainLiteral();

		if(isRDFPlainLiteral){
			writer.name("annotationLanguage").value(value.getLang());
		} else {
			writer.name("annotationDatatype").value(value.getDatatype().toString());
			writer.name("annotationDatatypeIRI").value(value.getDatatype().getIRI().toString());
		}

		writer.name("isRDFPlainLiteral").value(isRDFPlainLiteral);
		writer.name("annotationValue").value(literal);
	}

	public static OWLAnnotationAssertionAxiom createOWLAnnotationAssertionAxiom (final OWLDataFactory factory, final String annotationProperty, final String annotationValue, final String annotationLanguage, final String annotationDatatype, final String individualIRIString, final IRI individualIRI) {
		final IRI annotationPropertyIRI = IRI.create(annotationProperty);
		IRI annotationDataTypeIRI = null;

		// Get owl annotation property
		final OWLAnnotationProperty owlAnnoProp = factory.getOWLAnnotationProperty(annotationPropertyIRI);
		OWLAnnotationValue owlAnnoValue = null;

		// Get owl annotation value
		if(null == annotationDatatype || annotationDatatype.equals(OWLConstants.RDF_PLAIN_LITERAL_IRI)){
			owlAnnoValue = factory.getOWLLiteral(annotationValue, annotationLanguage);
		} else {
			annotationDataTypeIRI = IRI.create(annotationDatatype);

			final OWL2Datatype dataType = OWL2Datatype.getDatatype(annotationDataTypeIRI);
			owlAnnoValue = factory.getOWLLiteral(annotationValue, dataType);
		}

		// Get owl annotation
		final OWLAnnotation owlAnno = factory.getOWLAnnotation(owlAnnoProp, owlAnnoValue);

		return factory.getOWLAnnotationAssertionAxiom(individualIRI, owlAnno);
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
				OWLAnnotationPropertyUtil.buildOWLAnnotationHierarchy(writer, reasoner, factory, owlOntology, oap);
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

	/**
	 * Build annotation json details of a given owl (generic) object.
	 *
	 * @param writer the writer
	 * @param owlOntology the owl ontology
	 * @param owlObject the owl object
	 * @throws Exception the exception
	 */
	public static void insertAnnotationToDB (final AnnotationManager annotationManager, final OWLOntology owlOntology, final Object owlObject, final User user, final Individual individual) throws Exception {
		if(!(owlObject instanceof HasIRI)){
			throw new Exception("Not an object that extends HasIRI class");
		}

		final Class<?> owlClass = owlObject.getClass();

		final Method getIRIMethod = owlClass.getMethod("getIRI", null);
		final IRI objectIRI = (IRI) getIRIMethod.invoke(owlObject, null);

		final Set<OWLAnnotationAssertionAxiom> annotationAssertionSet = owlOntology.getAnnotationAssertionAxioms(objectIRI);

		for (final OWLAnnotationAssertionAxiom owlAnnotationAssertion: annotationAssertionSet) {
			final Annotation a = new Annotation();

			final OWLAnnotationProperty oap = owlAnnotationAssertion.getProperty();

			final OWLLiteral value = owlAnnotationAssertion.getValue().asLiteral().get();
			final String literal = EscapeUtils.escapeString(value.getLiteral());

			final boolean isRDFPlainLiteral = value.isRDFPlainLiteral();

			if(isRDFPlainLiteral){
				a.setAnnotationLanguage(value.getLang());
			} else {
				a.setAnnotationDataType(value.getDatatype().toString());
				a.setAnnotationDataTypeIRI(value.getDatatype().getIRI().toString());
			}

			a.setAnnotationPropertyIRI(oap.getIRI().toString());
			a.setAnnotationPropertyShortForm(oap.getIRI().getRemainder().get());
			a.setAnnotationValue(literal);
			a.setEditor(user);
			a.setIndividual(individual);
			a.setReferenceIndividualIRI(objectIRI.toString());

			annotationManager.save(a);
		}
	}
}

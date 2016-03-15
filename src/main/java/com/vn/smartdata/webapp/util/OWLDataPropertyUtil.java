package com.vn.smartdata.webapp.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.EscapeUtils;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import com.google.gson.stream.JsonWriter;
import com.vn.smartdata.constants.OWLConstants;
import com.vn.smartdata.constants.StringPool;
import com.vn.smartdata.manager.DataPropertyAssertionManager;
import com.vn.smartdata.manager.OntologyManager;
import com.vn.smartdata.model.DataPropertyAssertion;
import com.vn.smartdata.model.Individual;
import com.vn.smartdata.model.User;


/**
 * The Class OWLUtil.
 *
 * @author TAQ1HC
 */
public class OWLDataPropertyUtil {

	protected final transient static Log log = LogFactory.getLog(OWLDataPropertyUtil.class);

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

		// Display sub classes
		for (final OWLDataProperty directSubProp : HTMLRenderer.toSortedSet(subPropSet)) {
			final IRI directSubPropIRI = directSubProp.getIRI();
			writer.beginObject();
			//writer.name("id").value(directSubProp.getIRI().toString());
			writer.name("text").value(
					directSubPropIRI.getShortForm());
			writer.name("icon").value("fa fa-square dp-class");

			// - this can be anything you want - it is metadata you want attached to the node - you will be able to access and modify it any time later - it has no effect on the visuals of the node.
			writer.name("data").beginObject();
			writer.name("nodeId").value(directSubProp.getIRI().toString());

			// build annotation json string
			OWLAnnotationPropertyUtil.buildAnnotationJSONDetails(writer, owlOntology, directSubProp);

			// Start 'domains (intersection)'
			writer.name("domains").beginArray();

			final Set<OWLDataPropertyDomainAxiom> domainAxiomSet = owlOntology.getDataPropertyDomainAxioms(directSubProp);

			for(final OWLDataPropertyDomainAxiom domainAxiom: domainAxiomSet){
				final OWLClassExpression oce = domainAxiom.getDomain();

				writer.beginObject();
				writer.name("domainString").value(oce.toString());
				writer.name("isAnonymous").value(oce.isAnonymous());
				writer.endObject();
			}

			// End 'domains'
			writer.endArray();

			// Start 'ranges (intersection)'
			writer.name("ranges").beginArray();

			final Set<OWLDataPropertyRangeAxiom> rangeAxiomSet = owlOntology.getDataPropertyRangeAxioms(directSubProp);

			for(final OWLDataPropertyRangeAxiom rangeAxiom: rangeAxiomSet){
				final OWLDataRange odr = rangeAxiom.getRange();

				writer.beginObject();
				writer.name("rangeString").value(odr.toString());
				writer.endObject();
			}

			// End 'ranges'
			writer.endArray();

			// Start 'equivalent to'
			writer.name("equivalentTo").beginArray();

			final Set<OWLEquivalentDataPropertiesAxiom> equivalentObjectPropAxiom = owlOntology.getEquivalentDataPropertiesAxioms(directSubProp);

			for(final OWLEquivalentDataPropertiesAxiom equivalentAxiom: equivalentObjectPropAxiom){
				String equivalentString = equivalentAxiom.getPropertiesMinus(directSubProp).toString();
				equivalentString = StringUtils.replaceChars(equivalentString, StringPool.OPEN_CLOSE_BRACKET, "");

				writer.beginObject();
				writer.name("equivalentToString").value(equivalentString);
				writer.endObject();
			}

			// End 'equivalent to'
			writer.endArray();

			// Start 'subProperty Of'
			writer.name("subPropertyOf").beginArray();

			final Set<OWLSubDataPropertyOfAxiom> subPropertyAxiomSet = owlOntology.getDataSubPropertyAxiomsForSubProperty(directSubProp);

			for(final OWLSubDataPropertyOfAxiom subPropertyAxiom: subPropertyAxiomSet){
				writer.beginObject();
				writer.name("superProperty").value(subPropertyAxiom.getSuperProperty().toString());
				writer.endObject();
			}

			// End 'subPropertyOf'
			writer.endArray();

			// Start 'disjointWith'
			writer.name("disjointWith").beginArray();

			final Set<OWLDisjointDataPropertiesAxiom> disjointWithAxiomSet = owlOntology.getDisjointDataPropertiesAxioms(directSubProp);

			for(final OWLDisjointDataPropertiesAxiom disjointWithPropertyAxiom: disjointWithAxiomSet){
				String disjointAxiomString = disjointWithPropertyAxiom.getPropertiesMinus(directSubProp).toString();
				disjointAxiomString = StringUtils.replaceChars(disjointAxiomString, StringPool.OPEN_CLOSE_BRACKET, "");

				writer.beginObject();
				writer.name("disjointWithString").value(disjointAxiomString);
				writer.endObject();
			}

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

			subPropsOfSubProp.remove(OntologyManager.getBottomDataProperty(reasoner));

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

		dataSubProps.remove(OntologyManager.getBottomDataProperty(reasoner));

		if (!dataSubProps.isEmpty()) {
			writer.name("children").beginArray();

			for(final OWLDataProperty odp: dataSubProps){
				OWLDataPropertyUtil.buildOWLDataPropHierarchy(writer, reasoner, factory, owlOntology, odp);
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
	 * Creates the owl property assertion axiom.
	 *
	 * @param factory the factory
	 * @param individualIRI the individual iri
	 * @param dpDatatype the dp datatype
	 * @param dpValue the dp value
	 * @param dpLanguage the dp language
	 * @param dataProperty the data property
	 * @param isNegative the is negative
	 * @return the OWL property assertion axiom
	 */
	public static OWLPropertyAssertionAxiom<?, ?> createOWLPropertyAssertionAxiom(final OWLDataFactory factory, final IRI individualIRI, final String dpDatatype, final String dpValue, final String dpLanguage, final String dataProperty, final boolean isNegative){
		final OWLNamedIndividual currentSelectedIndividual = factory.getOWLNamedIndividual(individualIRI);

		final OWLDataProperty selectedOWLDataProp = factory.getOWLDataProperty(IRI.create(dataProperty));
		OWLLiteral owlLiteral = null;

		// Get owl literal
		if(null == dpDatatype || dpDatatype.equals(OWLConstants.RDF_PLAIN_LITERAL_IRI)){
			owlLiteral = factory.getOWLLiteral(dpValue, dpLanguage);
		} else {
			final OWL2Datatype dataType = OWL2Datatype.getDatatype(IRI.create(dpDatatype));
			owlLiteral = factory.getOWLLiteral(dpValue, dataType);
		}

		OWLPropertyAssertionAxiom<?, ?> axiom = null;

		if(isNegative){
			axiom = factory.getOWLNegativeDataPropertyAssertionAxiom(selectedOWLDataProp, currentSelectedIndividual, owlLiteral);
		} else {
			axiom = factory.getOWLDataPropertyAssertionAxiom(selectedOWLDataProp, currentSelectedIndividual, owlLiteral);
		}

		return axiom;
	}

	/**
	 * Builds the owl annotation assertion axiom json.
	 *
	 * @param axiom the axiom
	 * @param writer the writer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void buildOWLDataPropertyAssertionAxiomJSON(final OWLPropertyAssertionAxiom<?, ?> axiom, final JsonWriter writer) throws IOException{
		OWLLiteral literalObject = null;
		OWLDataProperty owlDataProperty = null;

		if(axiom instanceof OWLNegativeDataPropertyAssertionAxiom){
			literalObject = ((OWLNegativeDataPropertyAssertionAxiom)axiom).getObject().asLiteral().get();
			owlDataProperty = ((OWLNegativeDataPropertyAssertionAxiom)axiom).getProperty().asOWLDataProperty();
		} else if (axiom instanceof OWLDataPropertyAssertionAxiom) {
			literalObject = ((OWLDataPropertyAssertionAxiom)axiom).getObject().asLiteral().get();
			owlDataProperty = ((OWLDataPropertyAssertionAxiom)axiom).getProperty().asOWLDataProperty();
		}

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

		writer.name("propertyIRI").value(owlDataProperty.getIRI().toString());
		writer.name("isRDFPlainLiteral").value(isRDFPlainLiteral);
		writer.name("lang").value(lang);
		writer.name("literalValue").value(literalValue);
		writer.name("dataTypeIRI").value(dataTypeIRIString);

		writer.name("property").value(axiom.getProperty().toString());
		writer.name("object").value(axiom.getObject().toString());
	}

	public static void insertNDPAsToDB(final DataPropertyAssertionManager dpaManager, final OWLNamedIndividual namedIndividual, final OWLOntology owlOntology, final User user, final Individual individual){
		final Collection<OWLNegativeDataPropertyAssertionAxiom> negativeDataPropAssertionAxiomSet = owlOntology.getNegativeDataPropertyAssertionAxioms(namedIndividual);

		for(final OWLNegativeDataPropertyAssertionAxiom axiom: negativeDataPropAssertionAxiomSet){
			final DataPropertyAssertion dpa = new DataPropertyAssertion();

			OWLLiteral literalObject = null;
			OWLDataProperty owlDataProperty = null;

			if(axiom instanceof OWLNegativeDataPropertyAssertionAxiom){
				literalObject = axiom.getObject().asLiteral().get();
				owlDataProperty = axiom.getProperty().asOWLDataProperty();
			} else if (axiom instanceof OWLDataPropertyAssertionAxiom) {
				literalObject = ((OWLDataPropertyAssertionAxiom)axiom).getObject().asLiteral().get();
				owlDataProperty = ((OWLDataPropertyAssertionAxiom)axiom).getProperty().asOWLDataProperty();
			}

			final boolean isRDFPlainLiteral = literalObject.isRDFPlainLiteral();

			final String literalValue = EscapeUtils.escapeString(literalObject.getLiteral());
			String lang = StringPool.BLANK;
			String dataTypeIRIString = StringPool.BLANK;

			if(isRDFPlainLiteral){
				if(literalObject.hasLang()){
					lang = literalObject.getLang();
				}
				dataTypeIRIString = OWLConstants.RDF_PLAIN_LITERAL_IRI;

				dpa.setDataPropertyLanguage(lang);
			} else {
				final IRI dataTypeIRI = literalObject.getDatatype().getIRI();
				dataTypeIRIString = dataTypeIRI.toString();

				dpa.setDataPropertyTypeIRI(dataTypeIRIString);
				dpa.setDataPropertyType(dataTypeIRI.getShortForm());
			}

			dpa.setDataPropertyIRI(owlDataProperty.getIRI().toString());
			dpa.setDataPropertyShortForm(owlDataProperty.getIRI().getShortForm());
			dpa.setDataPropertyValue(literalValue);

			dpa.setReferenceIndividualIRI(individual.getIndividualIRI());
			dpa.setEditor(user);
			dpa.setIndividual(individual);
			dpa.setNegative(true);

			dpaManager.save(dpa);
		}
	}

	public static void insertDPAsToDB(final DataPropertyAssertionManager dpaManager, final OWLNamedIndividual namedIndividual, final OWLOntology owlOntology, final User user, final Individual individual){
		final Collection<OWLDataPropertyAssertionAxiom> dataPropAssertionAxiomSet = owlOntology.getDataPropertyAssertionAxioms(namedIndividual);

		for(final OWLDataPropertyAssertionAxiom axiom: dataPropAssertionAxiomSet){
			final DataPropertyAssertion dpa = new DataPropertyAssertion();

			OWLLiteral literalObject = null;
			OWLDataProperty owlDataProperty = null;

			if(axiom instanceof OWLNegativeDataPropertyAssertionAxiom){
				literalObject = axiom.getObject().asLiteral().get();
				owlDataProperty = axiom.getProperty().asOWLDataProperty();
			} else if (axiom instanceof OWLDataPropertyAssertionAxiom) {
				literalObject = axiom.getObject().asLiteral().get();
				owlDataProperty = axiom.getProperty().asOWLDataProperty();
			}

			final boolean isRDFPlainLiteral = literalObject.isRDFPlainLiteral();

			final String literalValue = EscapeUtils.escapeString(literalObject.getLiteral());
			String lang = StringPool.BLANK;
			String dataTypeIRIString = StringPool.BLANK;

			if(isRDFPlainLiteral){
				if(literalObject.hasLang()){
					lang = literalObject.getLang();
				}
				dataTypeIRIString = OWLConstants.RDF_PLAIN_LITERAL_IRI;

				dpa.setDataPropertyLanguage(lang);
			} else {
				final IRI dataTypeIRI = literalObject.getDatatype().getIRI();
				dataTypeIRIString = dataTypeIRI.toString();

				dpa.setDataPropertyTypeIRI(dataTypeIRIString);
				dpa.setDataPropertyType(dataTypeIRI.getShortForm());
			}

			dpa.setDataPropertyIRI(owlDataProperty.getIRI().toString());
			dpa.setDataPropertyShortForm(owlDataProperty.getIRI().getShortForm());
			dpa.setDataPropertyValue(literalValue);

			dpa.setReferenceIndividualIRI(individual.getIndividualIRI());
			dpa.setEditor(user);
			dpa.setIndividual(individual);
			dpa.setNegative(false);

			dpaManager.save(dpa);
		}
	}
}

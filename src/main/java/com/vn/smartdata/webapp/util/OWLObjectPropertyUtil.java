package com.vn.smartdata.webapp.util;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import com.google.gson.stream.JsonWriter;
import com.vn.smartdata.constants.StringPool;
import com.vn.smartdata.manager.ObjectPropertyAssertionManager;
import com.vn.smartdata.model.Individual;
import com.vn.smartdata.model.ObjectPropertyAssertion;
import com.vn.smartdata.model.User;


/**
 * The Class OWLUtil.
 *
 * @author TAQ1HC
 */
public class OWLObjectPropertyUtil {

	protected final transient static Log log = LogFactory.getLog(OWLObjectPropertyUtil.class);

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
		// Display sub classes
		for (final OWLObjectPropertyExpression directSubProp : HTMLRenderer.toSortedSet(subPropSet)) {
			final IRI directSubPropIRI = directSubProp.asOWLObjectProperty().getIRI();
			writer.beginObject();
			//writer.name("id").value(directSubProp.getIRI().toString());
			writer.name("text").value(
					directSubPropIRI.getShortForm());
			writer.name("icon").value("fa fa-square op-class");

			// - this can be anything you want - it is metadata you want attached to the node - you will be able to access and modify it any time later - it has no effect on the visuals of the node.
			writer.name("data").beginObject();
			writer.name("nodeId").value(directSubPropIRI.toString());

			// build annotation json string
			OWLAnnotationPropertyUtil.buildAnnotationJSONDetails(writer, owlOntology, directSubProp);

			// Start 'domains (intersection)'
			writer.name("domains").beginArray();

			final Set<OWLObjectPropertyDomainAxiom> domainAxiomSet = owlOntology.getObjectPropertyDomainAxioms(directSubProp);

			for(final OWLObjectPropertyDomainAxiom domainAxiom: domainAxiomSet){
				final OWLClassExpression oce = domainAxiom.getDomain();

				writer.beginObject();
				writer.name("domainString").value(oce.toString());
				writer.name("isAnonymous").value(oce.isAnonymous());
				writer.endObject();
			}

			// End 'domains'
			writer.endArray();

			// Start 'inverse of'
			writer.name("inverseOf").beginArray();

			final Set<OWLInverseObjectPropertiesAxiom> inverseObjectPropAxiomSet = owlOntology.getInverseObjectPropertyAxioms(directSubProp);

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

				writer.endObject();
			}

			// End 'inverseOf'
			writer.endArray();

			// Start 'ranges (intersection)'
			writer.name("ranges").beginArray();

			final Set<OWLObjectPropertyRangeAxiom> rangeAxiomSet = owlOntology.getObjectPropertyRangeAxioms(directSubProp);

			for(final OWLObjectPropertyRangeAxiom rangeAxiom: rangeAxiomSet){
				OWLObjectPropertyUtil.log.debug("Range axiom: " + rangeAxiom);

				final OWLClassExpression oce = rangeAxiom.getRange();

				writer.beginObject();
				writer.name("isAnonymous").value(oce.isAnonymous());
				writer.name("rangeString").value(oce.toString());
				writer.endObject();
			}

			// End 'ranges'
			writer.endArray();

			// Start 'equivalent to'
			writer.name("equivalentTo").beginArray();

			final Set<OWLEquivalentObjectPropertiesAxiom> equivalentObjectPropAxiom = owlOntology.getEquivalentObjectPropertiesAxioms(directSubProp);

			for(final OWLEquivalentObjectPropertiesAxiom equivalentAxiom: equivalentObjectPropAxiom){
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

			// End 'equivalent to'
			writer.endArray();

			// Start 'subProperty Of'
			writer.name("subPropertyOf").beginArray();

			final Set<OWLSubObjectPropertyOfAxiom> subPropertyAxiomSet = owlOntology.getObjectSubPropertyAxiomsForSubProperty(directSubProp);

			for(final OWLSubObjectPropertyOfAxiom subPropertyAxiom: subPropertyAxiomSet){
				final OWLObjectPropertyExpression ope = subPropertyAxiom.getSuperProperty();

				writer.beginObject();
				writer.name("superProperty").value(ope.toString());
				writer.name("isAnonymous").value(ope.isAnonymous());
				writer.endObject();
			}

			// End 'subPropertyOf'
			writer.endArray();

			// Start 'disjointWith'
			writer.name("disjointWith").beginArray();

			final Set<OWLDisjointObjectPropertiesAxiom> disjointWithAxiomSet = owlOntology.getDisjointObjectPropertiesAxioms(directSubProp);

			for(final OWLDisjointObjectPropertiesAxiom disjointWithPropertyAxiom: disjointWithAxiomSet){
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
				OWLObjectPropertyUtil.buildOWLObjectPropHierarchy(writer, reasoner, factory, owlOntology, ope);
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
	 * Builds the list opajson.
	 *
	 * @param objectPropertyAssertions the object property assertions
	 * @param writer the writer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void buildListOPAJSON (final Collection<ObjectPropertyAssertion> objectPropertyAssertions, final JsonWriter writer) throws IOException {
		for(final ObjectPropertyAssertion opa: objectPropertyAssertions){
			writer.beginObject();
			OWLObjectPropertyUtil.buildOPAJSON(opa, writer);
			writer.endObject();
		}
	}

	/**
	 * Builds the opajson.
	 *
	 * @param opa the opa
	 * @param writer the writer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void buildOPAJSON (final ObjectPropertyAssertion opa, final JsonWriter writer) throws IOException {
		writer.name("propertyIRI").value(opa.getObjectPropertyIRI());
		writer.name("objectIRI").value(opa.getIndividualIRI());
		writer.name("property").value(opa.getObjectPropertyShortForm());
		writer.name("object").value(opa.getIndividualShortForm());
		writer.name("id").value(opa.getId());
	}

	public static void insertOPAsToDB(final ObjectPropertyAssertionManager opaManager, final OWLNamedIndividual namedIndividual, final OWLOntology owlOntology, final User user, final Individual individual){
		final Collection<OWLObjectPropertyAssertionAxiom> objectPropAssertionAxiomSet = owlOntology.getObjectPropertyAssertionAxioms(namedIndividual);

		for(final OWLObjectPropertyAssertionAxiom axiom: objectPropAssertionAxiomSet){
			final IRI individualIRI = axiom.getObject().asOWLNamedIndividual().getIRI();
			final IRI objectPropIRI = axiom.getProperty().asOWLObjectProperty().getIRI();

			final ObjectPropertyAssertion opa = new ObjectPropertyAssertion();
			opa.setIndividual(individual);
			opa.setEditor(user);
			opa.setNegative(false);
			opa.setIndividualIRI(individualIRI.toString());
			opa.setIndividualShortForm(individualIRI.getShortForm());
			opa.setObjectPropertyIRI(objectPropIRI.toString());
			opa.setObjectPropertyShortForm(objectPropIRI.getShortForm());
			opa.setReferenceIndividualIRI(individual.getIndividualIRI());

			opaManager.save(opa);
		}
	}

	public static void insertNOPAsToDB(final ObjectPropertyAssertionManager opaManager, final OWLNamedIndividual namedIndividual, final OWLOntology owlOntology, final User user, final Individual individual){
		final Collection<OWLNegativeObjectPropertyAssertionAxiom> negativeObjectPropAssertionAxiomSet = owlOntology.getNegativeObjectPropertyAssertionAxioms(namedIndividual);

		for(final OWLNegativeObjectPropertyAssertionAxiom axiom: negativeObjectPropAssertionAxiomSet){
			final IRI individualIRI = axiom.getObject().asOWLNamedIndividual().getIRI();
			final IRI objectPropIRI = axiom.getProperty().asOWLObjectProperty().getIRI();

			final ObjectPropertyAssertion opa = new ObjectPropertyAssertion();
			opa.setIndividual(individual);
			opa.setEditor(user);
			opa.setNegative(true);
			opa.setIndividualIRI(individualIRI.toString());
			opa.setIndividualShortForm(individualIRI.getShortForm());
			opa.setObjectPropertyIRI(objectPropIRI.toString());
			opa.setObjectPropertyShortForm(objectPropIRI.getShortForm());
			opa.setReferenceIndividualIRI(individual.getIndividualIRI());

			opaManager.save(opa);
		}
	}
}

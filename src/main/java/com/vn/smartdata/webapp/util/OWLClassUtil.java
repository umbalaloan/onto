package com.vn.smartdata.webapp.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;

import com.google.gson.stream.JsonWriter;
import com.vn.smartdata.constants.OWLConstants;
import com.vn.smartdata.constants.StringPool;
import com.vn.smartdata.manager.TypeManager;
import com.vn.smartdata.model.Individual;
import com.vn.smartdata.model.Type;
import com.vn.smartdata.model.User;


/**
 * The Class OWLUtil.
 *
 * @author TAQ1HC
 */
public class OWLClassUtil {

	protected final transient static Log log = LogFactory.getLog(OWLClassUtil.class);

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
				OWLClassUtil.buildOWLClassHierarchy(writer, reasoner, factory, owlOntology, directSubClass);
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

		// - this can be anything you want - it is metadata you want attached to the node - you will be able to access and modify it any time later - it has no effect on the visuals of the node.
		writer.name("data").beginObject();
		writer.name("nodeId").value(owlClass.getIRI().toString());

		// build annotation json string
		OWLAnnotationPropertyUtil.buildAnnotationJSONDetails(writer, owlOntology, owlClass);

		// subClassOf
		writer.name("superClasses").beginArray();

		final Set<OWLSubClassOfAxiom> subClassAxiomSet = owlOntology.getSubClassAxiomsForSubClass(owlClass);

		for (final OWLSubClassOfAxiom subClassAxiom: subClassAxiomSet) {

			final OWLClassExpression owlClassExpression = subClassAxiom.getSuperClass();
			//final ClassExpressionType classExpressionType = owlClassExpression.getClassExpressionType();

			writer.beginObject();
			writer.name("isAnonymous").value(owlClassExpression.isAnonymous());
			writer.name("superClass").value(owlClassExpression.toString());
			writer.endObject();
		}

		// End 'superClasses' array, i.e. subClassOf
		writer.endArray();

		// Start 'equivalentTo'
		writer.name("equivalentClasses").beginArray();

		final Set<OWLEquivalentClassesAxiom> equivalentClassesAxiomSet = owlOntology.getEquivalentClassesAxioms(owlClass);

		for(final OWLEquivalentClassesAxiom equivalentClassesAxiom: equivalentClassesAxiomSet){
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

		for(final OWLDisjointClassesAxiom disjointClassesAxiom: disjointClassesAxiomSet){
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

		for(final OWLDisjointUnionAxiom disjointUnionOfClassesAxiom: disjointUnionOfAxiomSet){
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

		for(final OWLHasKeyAxiom hasKeyAxiom: hasKeyAxiomSet){
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
					OWLClassUtil.buildOWLClassJSONDetails(writer, reasoner, factory, owlOntology, directSubClass);
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

		//		StringBuilder sb = new StringBuilder("<ul>");
		//		int counter = 0;

		for(final OWLNamedIndividual individual: HTMLRenderer.toSortedSet(classInstances)){
			OWLIndividualUtil.buildOWLNamedIndividualJSONDetails(writer, owlOntology, individual);
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
					OWLClassUtil.buildOWLClassAndItsIndividuals(writer, reasoner, factory, owlOntology, directSubClass);
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
	 * Builds the individual types json.
	 *
	 * @param types the types
	 * @param writer the writer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void buildIndividualTypesJSON (final Collection<Type> types, final JsonWriter writer) throws IOException {
		for(final Type t: types){
			writer.beginObject();
			//final boolean isAnonymous = t.isAnonymous();

			//if(!isAnonymous){
			//final OWLClass owlClass = oce.asOWLClass();
			writer.name("iri").value(t.getOwlClassIRI());
			//}

			//writer.name("isAnonymous").value(oce.isAnonymous());
			writer.name("name").value(t.getOwlClassShortForm());
			writer.name("id").value(t.getId());
			writer.endObject();
		}
	}

	/**
	 * Insert owl class to db.
	 *
	 * @param typeManager the type manager
	 * @param namedIndividual the named individual
	 * @param owlOntology the owl ontology
	 * @param user the user
	 * @param individual the individual
	 */
	public static void insertOWLClassToDB (final TypeManager typeManager, final OWLNamedIndividual namedIndividual, final OWLOntology owlOntology, final User user, final Individual individual) {
		final Collection<OWLClassExpression> typeSet = EntitySearcher.getTypes(namedIndividual, owlOntology);

		for(final OWLClassExpression oce: typeSet){
			final Type t = new Type();
			try {
				//final boolean isAnonymous = oce.isAnonymous();

				//if(!isAnonymous){
				final OWLClass owlClass = oce.asOWLClass();
				t.setOwlClassIRI(owlClass.getIRI().toString());
				//}

				//writer.name("isAnonymous").value(oce.isAnonymous());
				//writer.name("name").value(oce.toString());
				//t.setOwlClassShortForm(oce.toString());
				t.setOwlClassShortForm(owlClass.getIRI().getRemainder().get());
				t.setEditor(user);
				t.setIndividual(individual);
				t.setReferenceIndividualIRI(individual.getIndividualIRI());

				typeManager.save(t);
			} catch (final Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
}

package com.vn.smartdata.webapp.controller;

import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.stream.JsonWriter;
import com.vn.smartdata.constants.OWLConstants;
import com.vn.smartdata.constants.StringPool;
import com.vn.smartdata.constants.SystemConstants;
import com.vn.smartdata.manager.AnnotationManager;
import com.vn.smartdata.manager.DataPropertyAssertionManager;
import com.vn.smartdata.manager.IndividualManager;
import com.vn.smartdata.manager.ObjectPropertyAssertionManager;
import com.vn.smartdata.manager.OntologyContentManager;
import com.vn.smartdata.manager.OntologyManager;
import com.vn.smartdata.manager.RelatedIndividualManager;
import com.vn.smartdata.manager.RoleManager;
import com.vn.smartdata.manager.TypeManager;
import com.vn.smartdata.manager.UserManager;
import com.vn.smartdata.model.Annotation;
import com.vn.smartdata.model.DataPropertyAssertion;
import com.vn.smartdata.model.Individual;
import com.vn.smartdata.model.ObjectPropertyAssertion;
import com.vn.smartdata.model.Ontology;
import com.vn.smartdata.model.OntologyContent;
import com.vn.smartdata.model.RelatedIndividual;
import com.vn.smartdata.model.Type;
import com.vn.smartdata.model.User;
import com.vn.smartdata.webapp.util.OWLAnnotationPropertyUtil;
import com.vn.smartdata.webapp.util.OWLClassUtil;
import com.vn.smartdata.webapp.util.OWLDataPropertyUtil;
import com.vn.smartdata.webapp.util.OWLIndividualUtil;
import com.vn.smartdata.webapp.util.OWLObjectPropertyUtil;
import com.vn.smartdata.webapp.util.OWLUtil;
import com.vn.smartdata.webapp.util.RequestUtil;



@Controller
@RequestMapping("/individual/*")
public class IndividualController {

	/** The log. */
	protected final transient Log log = LogFactory.getLog(this.getClass());

	/** The role manager. */
	@Autowired
	private RoleManager roleManager;

	/** The ontology manager. */
	@Autowired
	private OntologyManager ontologyManager;

	/** The user manager. */
	@Autowired
	private UserManager userManager;

	/** The validator. */
	@Autowired(required = false)
	Validator validator;

	/** The remover. */
	private final OWLEntityRemover remover = null;

	/** The data type iri map. */
	private static Map<String, String> dataTypeIRIMap = new HashMap<>();

	/** The cache manager. */
	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private AnnotationManager annotationManager;

	@Autowired
	private IndividualManager individualManager;

	@Autowired
	private DataPropertyAssertionManager dpaManager;

	@Autowired
	private ObjectPropertyAssertionManager opaManager;

	@Autowired
	private TypeManager typeManager;

	@Autowired
	private RelatedIndividualManager relatedIndividualManager;

	@Autowired
	private OntologyContentManager ontologyContentManager;

	/**
	 * Add new individual.
	 *
	 * @param ontologyId the ontology id
	 * @param individualName the individual name
	 * @param classIRI the class iri
	 * @param request the request
	 * @param response the response
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/addNewIndividual", method = RequestMethod.POST)
	@ResponseBody
	public boolean addNewIndividual(@RequestParam("ontologyId") final Long ontologyId, @RequestParam(value="individualName") final String individualName,
			@RequestParam(value="classIRI") final String classIRI,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();
		final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);
		final OWLDataFactory factory = manager.getOWLDataFactory();

		boolean isChangeAppliedSuccessfully = false;
		OWLNamedIndividual newOWLNamedIndividual = null;

		try {

			if(!OWLUtil.checkOWLIndividualIRIExistenceForWordnetOntology(owlOntology, individualName)){
				newOWLNamedIndividual = factory.getOWLNamedIndividual(OWLUtil.createIRIForWordnetOntology(owlOntology, individualName));

				OWLClass owlClass = null;

				if(classIRI.equals(OWLConstants.OWL_THING_CLASS_ID)){
					owlClass = factory.getOWLThing();
				} else {
					owlClass = factory.getOWLClass(IRI.create(classIRI));
				}

				final OWLClassAssertionAxiom classAssertionAxiom = factory.getOWLClassAssertionAxiom(owlClass, newOWLNamedIndividual);

				// Apply change to this ontology
				if(manager.addAxiom(owlOntology, classAssertionAxiom) == ChangeApplied.SUCCESSFULLY){
					isChangeAppliedSuccessfully = true;

					// Add newly created individual into Lucene Index
					//Indexer indexer = new Indexer(LuceneConstants.LUCENE_INDEX_DIRECTORY, false);
					OntologyController.getIndexer().indexIndividual(newOWLNamedIndividual, owlOntology);

					// Please be careful that this may become a bug if user do not save this change into DB
				}

				// Or we can use another API :
				// AddAxiom addAxiom = new AddAxiom(owlOntology, classAssertionAxiom);
				// ChangeApplied changeApplied = manager.applyChange(addAxiom);
			}

		} catch (final Exception e) {
			this.log.error(e);
		}

		return isChangeAppliedSuccessfully;
	}

	/**
	 * Adds the individual annotation.
	 *
	 * @param ontologyId the ontology id
	 * @param individualIRIString the individual iri string
	 * @param annotationProperty the annotation property
	 * @param annotationValue the annotation value
	 * @param annotationDatatype the annotation type
	 * @param annotationLanguage the annotation language
	 * @param request the request
	 * @param response the response
	 * @return the string
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/addIndividualAnnotation", method = RequestMethod.POST)
	@ResponseBody
	public String addIndividualAnnotation(@RequestParam("ontologyId") final Long ontologyId, @RequestParam(value="currentIndividual") final String individualIRIString,
			@RequestParam(value="annotationProperty") final String annotationProperty, @RequestParam(value="annotationValue") final String annotationValue,
			@RequestParam(value="annotationDatatype") final String annotationDatatype, @RequestParam(value="annotationLanguage") final String annotationLanguage,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();
		final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);
		final OWLDataFactory factory = manager.getOWLDataFactory();

		final User currentUser = RequestUtil.getCurrentUser();

		boolean isSuccess = false;

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		OWLAnnotationAssertionAxiom owlAnnoAssertionAxiom = null;
		Annotation annotation = null;

		try {
			final IRI individualIRI = IRI.create(individualIRIString);

			if(OWLUtil.checkOWLIndividualIRIExistence(owlOntology, individualIRI)){
				// Get individual first
				final Individual individual = this.getIndividual(individualIRIString, currentUser, ontology);
				final long individualId = individual.getId();

				final IRI annotationPropertyIRI = IRI.create(annotationProperty);
				IRI annotationDataTypeIRI = null;

				// Get owl annotation property
				final OWLAnnotationProperty owlAnnoProp = factory.getOWLAnnotationProperty(annotationPropertyIRI);
				OWLAnnotationValue owlAnnoValue = null;

				// Get owl annotation value
				if(annotationDatatype.equals(OWLConstants.RDF_PLAIN_LITERAL_IRI)){
					owlAnnoValue = factory.getOWLLiteral(annotationValue, annotationLanguage);

					// Get annotation if it is existed
					annotation = this.annotationManager.getAnnotationWithLanguage(annotationProperty, annotationValue, annotationLanguage, individualId);
				} else {
					annotationDataTypeIRI = IRI.create(annotationDatatype);

					final OWL2Datatype dataType = OWL2Datatype.getDatatype(annotationDataTypeIRI);
					owlAnnoValue = factory.getOWLLiteral(annotationValue, dataType);

					// Get annotation if it is existed
					annotation = this.annotationManager.getAnnotation(annotationProperty, annotationValue, annotationDatatype, individualId);
				}

				// Get owl annotation
				final OWLAnnotation owlAnno = factory.getOWLAnnotation(owlAnnoProp, owlAnnoValue);

				// Create an annotation assertion axiom
				owlAnnoAssertionAxiom = factory.getOWLAnnotationAssertionAxiom(individualIRI, owlAnno);

				// If there is no annotation with the specified condition exist
				if(null == annotation){
					annotation = new Annotation();

					annotation.setIndividual(individual);
					annotation.setReferenceIndividualIRI(individualIRIString);
					annotation.setAnnotationPropertyIRI(annotationProperty);
					annotation.setAnnotationPropertyShortForm(annotationPropertyIRI.getShortForm());

					if(null != annotationDataTypeIRI){
						annotation.setAnnotationDataType(annotationDataTypeIRI.getShortForm());
						annotation.setAnnotationDataTypeIRI(annotationDatatype);
					} else {
						annotation.setAnnotationLanguage(annotationLanguage);
					}

					annotation.setAnnotationValue(annotationValue);
					annotation.setEditor(currentUser);

					annotation = this.annotationManager.save(annotation);

					isSuccess = true;
				}
			}

		} catch (final Exception e) {
			this.log.error(e);
		}

		writer.beginObject();
		writer.name("isSuccess").value(isSuccess);
		writer.name("annotationData").beginObject();

		// Build owl named individual data and display it into the UI
		if(isSuccess && owlAnnoAssertionAxiom != null){
			writer.name("id").value(annotation.getId());
			OWLAnnotationPropertyUtil.buildOWLAnnotationAssertionAxiomJSON(owlAnnoAssertionAxiom, writer);
		}

		// End individual data object
		writer.endObject();

		// End object
		writer.endObject();

		return out.toString();
	}

	/**
	 * Add new individual types.
	 *
	 * @param ontologyId the ontology id
	 * @param individualIRIString the individual iri string
	 * @param classIds the class ids
	 * @param request the request
	 * @param response the response
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/addIndividualTypes", method = RequestMethod.POST)
	@ResponseBody
	public String addIndividualTypes(@RequestParam("ontologyId") final Long ontologyId, @RequestParam(value="currentIndividual") final String individualIRIString,
			@RequestParam(value="selectedClassIds") final String classIds,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();
		final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		final User currentUser = RequestUtil.getCurrentUser();

		boolean isSuccess = true;

		final List<Type> successfullyAddedTypes = new ArrayList<>();

		try {
			final IRI individualIRI = IRI.create(individualIRIString);

			if(OWLUtil.checkOWLIndividualIRIExistence(owlOntology, individualIRI)){
				final Individual individual = this.getIndividual(individualIRIString, currentUser, ontology);
				final long individualId = individual.getId();

				for(final String classId : StringUtils.split(classIds, StringPool.COMMA)){
					Type type = this.typeManager.getType(classId, individualId);

					if(null == type){
						final IRI classIRI = IRI.create(classId);
						//final OWLClass owlClass = factory.getOWLClass(classIRI);
						//final OWLClassAssertionAxiom classAssertionAxiom = factory.getOWLClassAssertionAxiom(owlClass, currentSelectedIndividual);

						type = new Type();
						type.setOwlClassIRI(classId);
						type.setOwlClassShortForm(classIRI.getShortForm());
						type.setReferenceIndividualIRI(individualIRIString);
						type.setEditor(currentUser);
						type.setIndividual(individual);

						type = this.typeManager.save(type);

						successfullyAddedTypes.add(type);
					} else {
						isSuccess = false;
					}
				}
			}

			writer.beginObject();
			writer.name("isSuccess").value(isSuccess);
			writer.name("typeData").beginArray();

			// Build type json
			if(!successfullyAddedTypes.isEmpty()){
				OWLClassUtil.buildIndividualTypesJSON(successfullyAddedTypes, writer);
			}

			writer.endArray();

			// End object
			writer.endObject();

		} catch (final Exception e) {
			this.log.error(e);
		}

		return out.toString();
	}

	/**
	 * Adds the individual opa.
	 *
	 * @param ontologyId the ontology id
	 * @param individualIRIString the individual iri string
	 * @param selectedOP the selected op
	 * @param selectedIdv the selected idv
	 * @param isNegative the is negative
	 * @param request the request
	 * @param response the response
	 * @return the string
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/addIndividualOPA", method = RequestMethod.POST)
	@ResponseBody
	public String addIndividualOPA(@RequestParam("ontologyId") final Long ontologyId, @RequestParam(value="currentIndividual") final String individualIRIString,
			@RequestParam(value="selectedOP") final String selectedOP, @RequestParam(value="selectedIdv") final String selectedIdv, @RequestParam(value="isNegative") final boolean isNegative,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();
		final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		final User currentUser = RequestUtil.getCurrentUser();

		boolean isSuccess = true;

		ObjectPropertyAssertion opa = null;

		try {
			final IRI individualIRI = IRI.create(individualIRIString);

			if(OWLUtil.checkOWLIndividualIRIExistence(owlOntology, individualIRI)){
				final Individual individual = this.getIndividual(individualIRIString, currentUser, ontology);
				final long individualId = individual.getId();

				opa = this.opaManager.getObjectPropertyAssertion(selectedOP, selectedIdv, isNegative, individualId);

				if(opa == null){
					final IRI objectIRI = IRI.create(selectedOP);
					final IRI idvIRI = IRI.create(selectedIdv);

					opa = new ObjectPropertyAssertion();
					opa.setEditor(currentUser);
					opa.setIndividual(individual);
					opa.setObjectPropertyIRI(selectedOP);
					opa.setObjectPropertyShortForm(objectIRI.getShortForm());
					opa.setIndividualIRI(selectedIdv);
					opa.setIndividualShortForm(idvIRI.getShortForm());
					opa.setReferenceIndividualIRI(individualIRIString);
					opa.setNegative(isNegative);

					opa = this.opaManager.save(opa);
				} else {
					isSuccess = false;
				}

				//				final OWLNamedIndividual selectedIndividual = factory.getOWLNamedIndividual(IRI.create(selectedIdv));
				//				final OWLObjectProperty selectedOWLObjectProp = factory.getOWLObjectProperty(IRI.create(selectedOP));
				//
				//				OWLPropertyAssertionAxiom<?, ?> axiom = null;
				//
				//				if(isNegative){
				//					axiom = factory.getOWLNegativeObjectPropertyAssertionAxiom(selectedOWLObjectProp, currentSelectedIndividual, selectedIndividual);
				//				} else {
				//					axiom = factory.getOWLObjectPropertyAssertionAxiom(selectedOWLObjectProp, currentSelectedIndividual, selectedIndividual);
				//				}
				//
				//				// Apply change to this ontology
				//				if(manager.addAxiom(owlOntology, axiom) == ChangeApplied.SUCCESSFULLY){
				//					isChangeAppliedSuccessfully = true;
				//				}
			}

			writer.beginObject();
			writer.name("isSuccess").value(isSuccess);
			writer.name("opaData").beginObject();

			if(isSuccess && null != opa){
				OWLObjectPropertyUtil.buildOPAJSON(opa, writer);
			}

			writer.endObject();

			// End object
			writer.endObject();

		} catch (final Exception e) {
			this.log.error(e);
		}

		return out.toString();
	}

	/**
	 * Adds the individual data property assertion.
	 *
	 * @param ontologyId the ontology id
	 * @param individualIRIString the individual iri string
	 * @param dpDatatype the dp datatype
	 * @param dpLanguage the dp language
	 * @param dpValue the dp value
	 * @param isNegative the is negative
	 * @param dataProperty the data property
	 * @param request the request
	 * @param response the response
	 * @return the string
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/addIndividualDPA", method = RequestMethod.POST)
	@ResponseBody
	public String addIndividualDPA(@RequestParam("ontologyId") final Long ontologyId, @RequestParam(value="currentIndividual") final String individualIRIString,
			@RequestParam(value="dpDatatype") final String dpDatatype, @RequestParam(value="dpLanguage") final String dpLanguage, @RequestParam(value="dpValue") final String dpValue,
			@RequestParam(value="isNegative") final boolean isNegative, @RequestParam(value="dataProperty") final String dataProperty,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();
		final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);
		final OWLDataFactory factory = manager.getOWLDataFactory();

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		OWLNamedIndividual currentSelectedIndividual = null;
		final User currentUser = RequestUtil.getCurrentUser();

		boolean isSuccess = true;

		DataPropertyAssertion dpa = null;
		OWLPropertyAssertionAxiom<?, ?> axiom = null;

		try {
			final IRI individualIRI = IRI.create(individualIRIString);

			if(OWLUtil.checkOWLIndividualIRIExistence(owlOntology, individualIRI)){
				currentSelectedIndividual = factory.getOWLNamedIndividual(individualIRI);

				final Individual individual = this.getIndividual(individualIRIString, currentUser, ontology);
				final long individualId = individual.getId();

				final OWLDataProperty selectedOWLDataProp = factory.getOWLDataProperty(IRI.create(dataProperty));
				OWLLiteral owlLiteral = null;

				final IRI dpIRI = IRI.create(dataProperty);
				IRI dpDataTypeIRI = null;

				// Get owl literal
				if(dpDatatype.equals(OWLConstants.RDF_PLAIN_LITERAL_IRI)){
					owlLiteral = factory.getOWLLiteral(dpValue, dpLanguage);

					dpa = this.dpaManager.getDataPropertyAssertionWithLanguage(dataProperty, dpValue, dpLanguage, isNegative, individualId);
				} else {
					dpDataTypeIRI = IRI.create(dpDatatype);

					final OWL2Datatype dataType = OWL2Datatype.getDatatype(IRI.create(dpDatatype));
					owlLiteral = factory.getOWLLiteral(dpValue, dataType);

					dpa = this.dpaManager.getDataPropertyAssertion(dataProperty, dpValue, dpDatatype, isNegative, individualId);
				}

				if(null == dpa) {
					dpa = new DataPropertyAssertion();
					dpa.setIndividual(individual);
					dpa.setEditor(currentUser);
					dpa.setDataPropertyIRI(dataProperty);
					dpa.setDataPropertyShortForm(dpIRI.getShortForm());
					dpa.setNegative(isNegative);
					dpa.setDataPropertyValue(dpValue);
					dpa.setReferenceIndividualIRI(individualIRIString);

					if(null != dpDataTypeIRI){
						dpa.setDataPropertyTypeIRI(dpDatatype);
						dpa.setDataPropertyType(dpDataTypeIRI.getShortForm());
					} else {
						dpa.setDataPropertyLanguage(dpLanguage);
					}

					dpa = this.dpaManager.save(dpa);

					if(isNegative){
						axiom = factory.getOWLNegativeDataPropertyAssertionAxiom(selectedOWLDataProp, currentSelectedIndividual, owlLiteral);
					} else {
						axiom = factory.getOWLDataPropertyAssertionAxiom(selectedOWLDataProp, currentSelectedIndividual, owlLiteral);
					}
				} else {
					isSuccess = false;
				}

			}

			writer.beginObject();
			writer.name("isSuccess").value(isSuccess);
			writer.name("dpaData").beginObject();

			// Build owl named individual data and display it into the UI
			if(isSuccess && null != dpa){
				OWLDataPropertyUtil.buildOWLDataPropertyAssertionAxiomJSON(axiom, writer);
			}

			// End individual data object
			writer.endObject();

			// End object
			writer.endObject();

		} catch (final Exception e) {
			this.log.error(e);
		}

		return out.toString();
	}

	@RequestMapping(value = "/addRelatedIndividual", method = RequestMethod.POST)
	@ResponseBody
	public String addRelatedIndividual(@RequestParam("ontologyId") final Long ontologyId, @RequestParam(value="currentIndividual") final String individualIRIString,
			@RequestParam(value="selectedIdv") final String selectedIdv, @RequestParam(value="isSameOrDifferent") final boolean isSameOrDifferent,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();
		final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		final User currentUser = RequestUtil.getCurrentUser();

		boolean isSuccess = true;

		RelatedIndividual ri = null;

		try {
			final IRI individualIRI = IRI.create(individualIRIString);

			if(OWLUtil.checkOWLIndividualIRIExistence(owlOntology, individualIRI)){
				final Individual individual = this.getIndividual(individualIRIString, currentUser, ontology);
				final long individualId = individual.getId();

				ri = this.relatedIndividualManager.getRelatedIndividual(selectedIdv, isSameOrDifferent, individualId);

				if(ri == null){
					final IRI idvIRI = IRI.create(selectedIdv);

					ri = new RelatedIndividual();
					ri.setEditor(currentUser);
					ri.setIndividual(individual);
					ri.setIndividualIRI(selectedIdv);
					ri.setIndividualShortForm(idvIRI.getShortForm());
					ri.setReferenceIndividualIRI(individualIRIString);
					ri.setSameOrDifferent(isSameOrDifferent);

					ri = this.relatedIndividualManager.save(ri);
				} else {
					isSuccess = false;
				}

			}

			writer.beginObject();
			writer.name("isSuccess").value(isSuccess);
			writer.name("riData").beginObject();

			if(isSuccess && null != ri){
				OWLIndividualUtil.buildRelatedIndividual(ri, writer);
			}

			writer.endObject();

			// End object
			writer.endObject();

		} catch (final Exception e) {
			e.printStackTrace();
			this.log.error(e);
		}

		return out.toString();
	}

	@RequestMapping(value = "/getOtherIndividualVersions", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView getOtherIndividualVersions(@RequestParam("individualIRI")final String individualIRI, @RequestParam("pageNumber") int pageNumber, @RequestParam("ontologyId") final long ontologyId){
		final User currentUser = RequestUtil.getCurrentUser();
		final long userId = currentUser.getId();

		// Always reduce page number by 1 as the starting page of pageable is 0
		pageNumber = pageNumber - 1;

		// Get individual list by all users except the current users result
		final List<Individual> individualList = this.individualManager.getOtherVersionsOfIndividual(individualIRI, ontologyId, userId, pageNumber, SystemConstants.ITEM_PER_PAGE, null);
		final int totalItemsInPage = individualList.size();
		final int totalRecords = this.individualManager.countOtherVersionsOfIndividual(individualIRI, ontologyId, userId);

		// Calculate start index, end index, total pages, total number of items
		// We always + 1 here to display for user to understand
		final int startIndex = pageNumber * SystemConstants.ITEM_PER_PAGE + 1;
		int endIndex = startIndex + SystemConstants.ITEM_PER_PAGE - 1;

		if(endIndex > totalItemsInPage){
			endIndex = startIndex + totalItemsInPage - 1;
		}

		final int totalPages = (int) Math.ceil(totalRecords / (double) SystemConstants.ITEM_PER_PAGE);


		// User a jsp page to render all individuals
		final ModelAndView otherDescriptionPage = new ModelAndView("ontology/details/individual_content/other_description");
		otherDescriptionPage.addObject("individualList", individualList);
		otherDescriptionPage.addObject("startIndex", startIndex);
		otherDescriptionPage.addObject("endIndex", endIndex);
		otherDescriptionPage.addObject("totalPages", totalPages);
		otherDescriptionPage.addObject("totalRecords", totalRecords);

		return otherDescriptionPage;
	}

	@RequestMapping(value = "/getIndividualEditors", method = RequestMethod.GET)
	@ResponseBody
	public String getIndividualEditors(@RequestParam("individualIRI")final String individualIRI, @RequestParam("ontologyId") final long ontologyId) {
		final User currentUser = RequestUtil.getCurrentUser();
		final long userId = currentUser.getId();

		final List<Object[]> userList = this.individualManager.getIndividualUsers(individualIRI, ontologyId, userId);

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		try {
			writer.beginArray();

			if(null != userList && !userList.isEmpty()) {
				for(final Object[] o : userList){
					writer.beginObject();
					writer.name("userId").value((BigInteger) o[0]);
					writer.name("userName").value((String) o[1]);
					writer.name("fullName").value((String) o[3] + ' ' + (String) o[4]);
					writer.name("email").value((String) o[2]);
					writer.endObject();
				}
			}

			writer.endArray();

			writer.close();
		} catch (final Exception e) {
			this.log.error(e);
		}

		return out.toString();
	}

	@RequestMapping(value = "/cloneOtherVersion", method = RequestMethod.POST)
	@ResponseBody
	public boolean cloneOtherVersion(@RequestParam("individualIRI")final String individualIRI, @RequestParam("ontologyId") final long ontologyId, @RequestParam(required=false, value="selectedUserId") final Long selectedUserId,
			@RequestParam("selectedVersion") final String selectedVersion, @RequestParam("isMerged") boolean isMerged) throws Exception {
		final User currentUser = RequestUtil.getCurrentUser();
		final long userId = currentUser.getId();
		boolean isSuccess = true;

		// At the moment, merge option is disabled, but maybe enabled in the future? So we still keep it here
		isMerged = false;

		try {
			Individual currentUserIndividual = this.individualManager.getIndividual(individualIRI, userId, ontologyId);
			final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
			final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

			// This will also remove all child entities such as: annotation, dpa, opa, relatedIndividual, type
			if(null != currentUserIndividual){
				this.individualManager.remove(currentUserIndividual);
			}

			// If selected version is original version, get all data from the original and insert them into DB
			if(selectedVersion.equals("originalVersion")){
				// Then we will create an individual of this user again
				currentUserIndividual = this.getIndividual(individualIRI, currentUser, ontology);

				final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();

				final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);
				final OWLDataFactory factory = manager.getOWLDataFactory();

				//final OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
				//final OWLReasoner reasoner = reasonerFactory.createReasoner(owlOntology);

				final OWLNamedIndividual namedIndividual = factory.getOWLNamedIndividual(IRI.create(individualIRI));

				if(isMerged){

				} else {
					//				this.annotationManager.deleteAllAnnotationsOfIndividual(currentUserIndividualId);
					//				this.dpaManager.deleteAllDPAsOfIndividual(currentUserIndividualId);
					//				this.opaManager.deleteAllOPAsOfIndividual(currentUserIndividualId);
					//				this.relatedIndividualManager.deleteAllRIsOfIndividual(currentUserIndividualId);
					//				this.typeManager.deleteAllTypesOfIndividual(currentUserIndividualId);

					// After that we insert new data to DB
					OWLAnnotationPropertyUtil.insertAnnotationToDB(this.annotationManager, owlOntology, namedIndividual, currentUser, currentUserIndividual);
					OWLClassUtil.insertOWLClassToDB(this.typeManager, namedIndividual, owlOntology, currentUser, currentUserIndividual);
					OWLIndividualUtil.insertSameIndividualsToDB(this.relatedIndividualManager, namedIndividual, owlOntology, currentUser, currentUserIndividual);
					OWLIndividualUtil.insertDifferentIndividualsToDB(this.relatedIndividualManager, namedIndividual, owlOntology, currentUser, currentUserIndividual);
					OWLDataPropertyUtil.insertDPAsToDB(this.dpaManager, namedIndividual, owlOntology, currentUser, currentUserIndividual);
					OWLDataPropertyUtil.insertNDPAsToDB(this.dpaManager, namedIndividual, owlOntology, currentUser, currentUserIndividual);
					OWLObjectPropertyUtil.insertOPAsToDB(this.opaManager, namedIndividual, owlOntology, currentUser, currentUserIndividual);
					OWLObjectPropertyUtil.insertNOPAsToDB(this.opaManager, namedIndividual, owlOntology, currentUser, currentUserIndividual);
				}

				// If selected version is collaborative version
			} else if (selectedVersion.equals("collaborativeVersion")) {
				// Then we will create an individual of this user again
				currentUserIndividual = this.getIndividual(individualIRI, currentUser, ontology);

				if(isMerged){

				} else {

				}

				// Else the selected version is coming from other users
			} else if (selectedVersion.equals("otherVersion")) {
				// Then we will create an individual of this user again
				currentUserIndividual = this.getIndividual(individualIRI, currentUser, ontology);

				if(isMerged){

				} else {
					final Individual selectedUserIndividual = this.individualManager.getIndividualEagerly(individualIRI, selectedUserId, ontologyId);

					final Set<Annotation> annotations = selectedUserIndividual.getAnnotations();

					for(final Annotation a : annotations){
						a.setEditor(currentUser);
						a.setIndividual(currentUserIndividual);
						a.setId(null);

						this.annotationManager.save(a);
					}

					final Set<Type> types = selectedUserIndividual.getTypes();

					for(final Type t: types){
						t.setId(null);
						t.setEditor(currentUser);
						t.setIndividual(currentUserIndividual);
						this.typeManager.save(t);
					}

					final Set<ObjectPropertyAssertion> opas = selectedUserIndividual.getObjectPropertyAssertions();

					for(final ObjectPropertyAssertion opa: opas){
						opa.setId(null);
						opa.setEditor(currentUser);
						opa.setIndividual(currentUserIndividual);
						this.opaManager.save(opa);
					}

					final Set<DataPropertyAssertion> dpas = selectedUserIndividual.getDataPropertyAssertions();

					for(final DataPropertyAssertion dpa: dpas){
						dpa.setId(null);
						dpa.setEditor(currentUser);
						dpa.setIndividual(currentUserIndividual);
						this.dpaManager.save(dpa);
					}

					final Set<RelatedIndividual> ris = selectedUserIndividual.getRelatedIndividuals();

					for(final RelatedIndividual ri: ris){
						ri.setEditor(currentUser);
						ri.setIndividual(currentUserIndividual);
						ri.setId(null);
						this.relatedIndividualManager.save(ri);
					}
				}

			}

		} catch (final Exception e) {
			e.printStackTrace();
			this.log.error(e);
			isSuccess = false;
		}

		return isSuccess;
	}

	/**
	 * Gets the individual.
	 *
	 * @param individualIRI the individual iri
	 * @param userId the user id
	 * @param ontologyId the ontology id
	 * @return the individual
	 */
	private Individual getIndividual(final String individualIRI, final User user, final Ontology ontology) {
		final long userId = user.getId();
		final long ontologyId = ontology.getId();

		Individual individual = this.individualManager.getIndividual(individualIRI, userId, ontologyId);

		if(null == individual){
			individual = new Individual();
			individual.setEditor(user);
			individual.setIndividualIRI(individualIRI);
			individual.setIndividualShortForm(OWLUtil.getShortForm(individualIRI));
			individual.setOntology(ontology);

			individual = this.individualManager.save(individual);
		}

		return individual;
	}
}

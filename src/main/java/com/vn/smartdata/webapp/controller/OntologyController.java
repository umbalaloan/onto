package com.vn.smartdata.webapp.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.TopDocs;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.ToStringRenderer;
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
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.SimpleRenderer;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.stream.JsonWriter;
import com.vn.smartdata.constants.LuceneConstants;
import com.vn.smartdata.constants.OWLConstants;
import com.vn.smartdata.constants.StringPool;
import com.vn.smartdata.lucene.Indexer;
import com.vn.smartdata.lucene.Searcher;
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
import com.vn.smartdata.model.DataPropertyAssertion;
import com.vn.smartdata.model.Individual;
import com.vn.smartdata.model.ObjectPropertyAssertion;
import com.vn.smartdata.model.Ontology;
import com.vn.smartdata.model.OntologyContent;
import com.vn.smartdata.model.RelatedIndividual;
import com.vn.smartdata.model.User;
import com.vn.smartdata.webapp.util.HTMLRenderer;
import com.vn.smartdata.webapp.util.MapUtil;
import com.vn.smartdata.webapp.util.OWLAnnotationPropertyUtil;
import com.vn.smartdata.webapp.util.OWLClassUtil;
import com.vn.smartdata.webapp.util.OWLDataPropertyUtil;
import com.vn.smartdata.webapp.util.OWLIndividualUtil;
import com.vn.smartdata.webapp.util.OWLObjectPropertyUtil;
import com.vn.smartdata.webapp.util.OWLUtil;
import com.vn.smartdata.webapp.util.RequestUtil;


/**
 * Implementation of <strong>SimpleFormController</strong> that interacts with
 * the {@link UserManager} to retrieve/persist values to the database.
 *
 * <p>
 * <a href="UserFormController.java.html"><i>View Source</i></a>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
@Controller
@RequestMapping("/ontology/*")
public class OntologyController extends BaseFormController {

	/** The log. */
	protected final transient Log log = LogFactory.getLog(this.getClass());

	/** The role manager. */
	@Autowired
	private RoleManager roleManager;

	/** The ontology manager. */
	@Autowired
	private OntologyManager ontologyManager;

	public void setOntologyManager(final OntologyManager ontologyManager) {
		this.ontologyManager = ontologyManager;
	}

	/** The user manager. */
	@Autowired
	private UserManager userManager;

	/** The validator. */
	@Autowired(required = false)
	Validator validator;

	/** The html renderer. */
	public static HTMLRenderer htmlRenderer = new HTMLRenderer();

	/** The simple renderer. */
	private static SimpleRenderer simpleRenderer = new SimpleRenderer();

	/** The remover. */
	private OWLEntityRemover remover = null;

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

	private static Indexer indexer;

	static {
		ToStringRenderer.getInstance().setRenderer(OntologyController.htmlRenderer);

		final Set<IRI> builtIntDatatypes = OWL2Datatype.getDatatypeIRIs();

		for(final IRI iri: builtIntDatatypes){
			OntologyController.dataTypeIRIMap.put(iri.toString(), iri.getShortForm());
		}

		OntologyController.dataTypeIRIMap = MapUtil.sortByValue(OntologyController.dataTypeIRIMap);

		OntologyController.indexer = new Indexer(LuceneConstants.LUCENE_INDEX_DIRECTORY, false);
	}

	// public OntologyController() {
	// this.setCancelView("redirect:/home");
	// this.setSuccessView("redirect:/admin/users");
	// }

	/**
	 * Set up a custom property editor for converting form inputs to real objects.
	 *
	 * @param request the current request
	 * @param binder the data binder
	 */
	@Override
	@InitBinder
	protected void initBinder(final HttpServletRequest request,
			final ServletRequestDataBinder binder) {
		binder.registerCustomEditor(Integer.class, null,
				new CustomNumberEditor(Integer.class, null, true));
		binder.registerCustomEditor(Long.class, null,
				new CustomNumberEditor(Long.class, null, true));
		binder.registerCustomEditor(byte[].class,
				new ByteArrayMultipartFileEditor());
		final SimpleDateFormat dateFormat =
				new SimpleDateFormat(this.getText("date.format", request.getLocale()));
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, null,
				new CustomDateEditor(dateFormat, true));
	}

	/**
	 * Show create page.
	 *
	 * @param request the request
	 * @param response the response
	 * @return the model and view
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView showCreatePage(final HttpServletRequest request,
			final HttpServletResponse response) {

		final ModelAndView mav = new ModelAndView("ontology/createOntology");
		mav.addObject("createOntology", new Ontology());
		return mav;
	}

	/**
	 * Submit create ontology.
	 *
	 * @param ontology the ontology
	 * @param bindingResult the binding result
	 * @param request the request
	 * @param model the model
	 * @param redirect the redirect
	 * @return the string
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String submitCreateOntology(
			@ModelAttribute("createOntology") Ontology ontology,
			final BindingResult bindingResult,
			final HttpServletRequest request, final Model model,
			final RedirectAttributes redirect) throws Exception {
		// If not an administrator or user, make sure user is not trying to add
		// an
		// ontology
		// if ((!request.isUserInRole(Constants.ADMIN_ROLE) || !request
		// .isUserInRole(Constants.USER_ROLE))
		// && !this.isFormSubmission(request)) {
		// response.sendError(HttpServletResponse.SC_FORBIDDEN);
		// throw new AccessDeniedException(
		// "You do not have permission to add an ontology.");
		// }

		try {
			this.validator.validate(ontology, bindingResult);

			if (bindingResult.hasErrors()) {
				model.addAttribute("createOntology", ontology);

				return "create";
			}

			final byte[] fileBytes = ontology.getUploadedFile();

			String content = StringPool.BLANK;

			if (null != fileBytes && fileBytes.length != 0) {
				content = new String(fileBytes);
			}

			ontology.setCreatedDate(new Date());
			ontology.setModifiedDate(new Date());
			ontology.setCreator(this.userManager.getUserByUsername(request
					.getRemoteUser()));
			ontology = this.ontologyManager.save(ontology);

			OntologyContent oc = new OntologyContent();
			oc.setContent(content);
			oc.setOntology(ontology.getId());

			oc = this.ontologyContentManager.save(oc, ontology.getId());

			ontology.setOntologyContent(oc.getId());
			this.ontologyManager.save(ontology);

		} catch (final Exception e) {
			e.printStackTrace();
			this.log.error(e.toString(), e);
		}

		return "redirect:/ontology/list";
	}

	/**
	 * Show list page.
	 *
	 * @param request the request
	 * @param response the response
	 * @return the model and view
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView showListPage(final HttpServletRequest request,
			final HttpServletResponse response) {

		final ModelAndView mav = new ModelAndView("ontology/ontologyList");
		final List<Ontology> ontologyList = this.ontologyManager.getAllOntologies();
		mav.addObject("ontologyList", ontologyList);
		return mav;
	}

	/**
	 * View details.
	 *
	 * @param ontologyId the ontology id
	 * @param request the request
	 * @param response the response
	 * @return the model and view
	 * @throws OWLOntologyCreationException the OWL ontology creation exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public ModelAndView viewDetails(
			@RequestParam("ontologyId") final Long ontologyId,
			final HttpServletRequest request, final HttpServletResponse response)
					throws OWLOntologyCreationException, IOException {

		Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();

		// Get ontology content and load it into OWLOntology class (of
		// owlapi)
		final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

		// Create factory to get root class, i.e. owl:Thing
		final OWLDataFactory factory = manager.getOWLDataFactory();

		// Check if the ontology is indexed or not
		// If not, open directory, loop through all of individuals then indexing
		if(!ontology.getIsIndexed()){
			synchronized (this) {
				// Always switch to Simple Renderer when indexing individuals
				ToStringRenderer.getInstance().setRenderer(OntologyController.simpleRenderer);

				final int numberOfIndividuals = OntologyController.indexer.createIndividualIndex(owlOntology);
				this.log.info("Total number of individuals: " + numberOfIndividuals);

				// After indexing, set status of indexing to true
				ontology.setIsIndexed(true);
				ontology = this.ontologyManager.save(ontology);

				// And also switch back to HTML Renderer when finishing indexing
				ToStringRenderer.getInstance().setRenderer(OntologyController.htmlRenderer);
			}
		}

		final ModelAndView mav = new ModelAndView("ontology/ontologyDetails");

		mav.addObject("dataTypeIRIMap", OntologyController.dataTypeIRIMap);
		mav.addObject("ontology", ontology);
		mav.addObject("ontologyId", ontologyId);
		mav.addObject("iri", factory.getOWLThing().getIRI().toString());
		mav.addObject("displayName", factory.getOWLThing().getIRI()
				.getShortForm());
		mav.addObject("OPiri", factory.getOWLTopObjectProperty().getIRI().toString());
		mav.addObject("OPdisplayName", factory.getOWLTopObjectProperty().getIRI()
				.getShortForm());
		mav.addObject("DPiri", factory.getOWLTopDataProperty().getIRI().toString());
		mav.addObject("DPdisplayName", factory.getOWLTopDataProperty().getIRI()
				.getShortForm());
		return mav;
	}

	@RequestMapping(value = "/viewIndividualContent", method = RequestMethod.GET)
	public ModelAndView viewIndividualContent(
			@RequestParam("ontologyId") final Long ontologyId,
			final HttpServletRequest request, final HttpServletResponse response)
					throws OWLOntologyCreationException, IOException {

		Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();

		// Get ontology content and load it into OWLOntology class (of
		// owlapi)
		final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

		// Create factory to get root class, i.e. owl:Thing
		final OWLDataFactory factory = manager.getOWLDataFactory();

		// Check if the ontology is indexed or not
		// If not, open directory, loop through all of individuals then indexing
		if(!ontology.getIsIndexed()){
			// Always switch to Simple Renderer when indexing individuals
			ToStringRenderer.getInstance().setRenderer(OntologyController.simpleRenderer);

			final int numberOfIndividuals = OntologyController.indexer.createIndividualIndex(owlOntology);
			System.out.println(numberOfIndividuals);

			// After indexing, set status of indexing to true
			ontology.setIsIndexed(true);
			ontology = this.ontologyManager.save(ontology);

			// And also switch back to HTML Renderer when finishing indexing
			ToStringRenderer.getInstance().setRenderer(OntologyController.htmlRenderer);
		}

		final ModelAndView mav = new ModelAndView("ontology/ontologyDetails");

		mav.addObject("dataTypeIRIMap", OntologyController.dataTypeIRIMap);
		mav.addObject("ontology", ontology);
		mav.addObject("ontologyId", ontologyId);
		mav.addObject("iri", factory.getOWLThing().getIRI().toString());
		mav.addObject("displayName", factory.getOWLThing().getIRI()
				.getShortForm());
		mav.addObject("OPiri", factory.getOWLTopObjectProperty().getIRI().toString());
		mav.addObject("OPdisplayName", factory.getOWLTopObjectProperty().getIRI()
				.getShortForm());
		mav.addObject("DPiri", factory.getOWLTopDataProperty().getIRI().toString());
		mav.addObject("DPdisplayName", factory.getOWLTopDataProperty().getIRI()
				.getShortForm());
		return mav;
	}

	/**
	 * Search individuals.
	 *
	 * @param ontologyId the ontology id
	 * @param searchString the search string
	 * @param classIRIString the class iri string
	 * @param request the request
	 * @param response the response
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/searchIndividuals", method = RequestMethod.GET)
	@ResponseBody
	public String searchIndividuals(@RequestParam("ontologyId") final Long ontologyId, @RequestParam("str") final String searchString, @RequestParam("classIRI") String classIRIString,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();
		final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);
		final OWLDataFactory factory = manager.getOWLDataFactory();

		final OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		final OWLReasoner reasoner = reasonerFactory
				.createReasoner(owlOntology);

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		try {
			if(classIRIString.equals(OWLConstants.OWL_THING_CLASS_ID)){
				classIRIString = OWLConstants.OWL_THING_IRI;
			}

			final OWLClass owlClass = factory.getOWLClass(IRI.create(classIRIString));

			Set<OWLNamedIndividual> classInstances = null;

			if(owlClass.isOWLThing()){
				classInstances = owlOntology.getIndividualsInSignature();
			} else {
				classInstances = reasoner.getInstances(owlClass, true).getFlattened();
			}

			classInstances = classInstances
					.stream()
					.filter(classInstance -> classInstance.getIRI().getShortForm().contains(searchString))
					.collect(Collectors.toSet());

			writer.beginArray();

			for(final OWLNamedIndividual individual: classInstances){
				writer.value(individual.getIRI().getShortForm());
			}

			writer.endArray();
			writer.close();
		} catch (final Exception e) {
			this.log.error(e.toString(), e);
		}

		return out.toString();
	}

	/**
	 * Load individual content.
	 *
	 * @param ontologyId the ontology id
	 * @param classIRI the class iri
	 * @param individualIRI the individual iri
	 * @return the string
	 * @throws OWLOntologyCreationException the OWL ontology creation exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/loadIndividualContent", method = RequestMethod.GET)
	@ResponseBody
	public String loadIndividualContent(@RequestParam("ontologyId") final Long ontologyId, @RequestParam("classIRI") final String classIRI, @RequestParam("individualIRI") final String individualIRI) throws OWLOntologyCreationException{

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();
		final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);
		final OWLDataFactory factory = manager.getOWLDataFactory();

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		final OWLNamedIndividual owlNamedIndividual = factory.getOWLNamedIndividual(IRI.create(individualIRI));

		try {
			writer.beginObject();

			/* Original individual data version */
			writer.name("originalIndividualData").beginArray();

			// Build owl named individual data and display it into the UI
			if(null != owlNamedIndividual){
				OWLIndividualUtil.buildOWLNamedIndividualJSONDetails(writer, owlOntology, owlNamedIndividual);
			}

			// End Original individual data version
			writer.endArray();

			//			/* collaborative individual data version */
			//			writer.name("collaborativeIndividualData").beginArray();
			//
			//			// Build owl named individual data and display it into the UI
			//			if(null != owlNamedIndividual){
			//				OWLIndividualUtil.buildOWLNamedIndividualJSONDetails(writer, owlOntology, owlNamedIndividual);
			//			}
			//
			//			// End Collaborative individual data version
			//			writer.endArray();
			//
			/* Current logged-in user individual data version */
			writer.name("currentUserIndividualData").beginArray();

			// Build owl named individual data and display it into the UI
			if(null != owlNamedIndividual){
				final User currentUser = RequestUtil.getCurrentUser();
				final long userId = currentUser.getId();

				final Individual individual = this.individualManager.getIndividualEagerly(individualIRI, userId, ontologyId);

				if(null != individual){

					final Set<RelatedIndividual> relatedIndividuals = individual.getRelatedIndividuals();
					final Object[] relatedIndividualArray = OWLIndividualUtil.getSameAndDifferentIndividuals(relatedIndividuals);

					final Set<ObjectPropertyAssertion> opas = individual.getObjectPropertyAssertions();
					final Object[] opaArray = OWLIndividualUtil.getObjectPropertyAssertionAndItsNegative(opas);

					final Set<DataPropertyAssertion> dpas = individual.getDataPropertyAssertions();
					final Object[] dpaArray = OWLIndividualUtil.getDataPropertyAssertionAndItsNegative(dpas);

					OWLIndividualUtil.buildOWLNamedIndividualJSONDetails(writer, ontology, owlNamedIndividual, factory, individual,
							(Set<RelatedIndividual>)relatedIndividualArray[0], (Set<RelatedIndividual>)relatedIndividualArray[1], (Set<ObjectPropertyAssertion>)opaArray[0], (Set<ObjectPropertyAssertion>)opaArray[1], (Set<DataPropertyAssertion>)dpaArray[0], (Set<DataPropertyAssertion>)dpaArray[1]);
				} else {
					OWLIndividualUtil.buildOWLNamedIndividualJSONDetails(writer, ontology, owlNamedIndividual, factory, null, null, null, null, null, null, null);
				}

			}

			// End Current logged-in user individual data version
			writer.endArray();

			// We do not load other users versions here since it may become a heavy loading request
			// But we will see it in the future, or maybe apply the same technique as displaying individual?

			// End object
			writer.endObject();

		} catch (final Exception e) {
			this.log.error(e.toString(), e);
		}

		return out.toString();
	}

	/**
	 * Gets the all individuals.
	 *
	 * @param request the request
	 * @param response the response
	 * @return the all individuals
	 */
	@RequestMapping(value = "/getAllIndividuals", method = RequestMethod.GET)
	@ResponseBody
	public String getAllIndividuals (@RequestParam(required=false, value="pageNumber", defaultValue="1") final int pageNumber, @RequestParam(required=false, value="classIRI", defaultValue=StringPool.BLANK) final String classIRIString,
			final HttpServletRequest request, final HttpServletResponse response) {

		// our page is 1 based - so we need to convert to zero based
		final int startIndex = (pageNumber - 1) * LuceneConstants.ITEMS_PER_PAGE;
		final int endIndex = startIndex + LuceneConstants.ITEMS_PER_PAGE;
		String individualJSON = StringPool.BLANK;

		try {
			final IRI iri = IRI.create(classIRIString);
			final long startTime = System.currentTimeMillis();
			final Searcher searcher = new Searcher(LuceneConstants.LUCENE_INDEX_DIRECTORY);
			final TopDocs topDocs = searcher.getAllDocuments(iri.getShortForm(), startIndex, LuceneConstants.ITEMS_PER_PAGE, LuceneConstants.MAX_SEARCH_RESULTS);
			final long endTime = System.currentTimeMillis();

			this.log.info("Search time: " + (endTime - startTime) + "ms");

			individualJSON = OWLIndividualUtil.buildIndividualJSON(searcher, topDocs, startIndex + 1, endIndex);
		} catch (final Exception e) {
			this.log.error(e.toString(), e);
		}

		return individualJSON;
	}

	/**
	 * Search individuals based on Lucene index.
	 *
	 * @param ontologyId the ontology id
	 * @param searchString the search string
	 * @param classIRIString the class iri string
	 * @param request the request
	 * @param response the response
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/searchIndividualsLuceneIndex", method = RequestMethod.GET)
	@ResponseBody
	public String searchIndividualsLuceneIndex(@RequestParam("ontologyId") final Long ontologyId, @RequestParam("searchString") final String searchString, @RequestParam("classIRI") final String classIRIString,
			@RequestParam(required=false, value="pageNumber", defaultValue="1") final int pageNumber,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		String individualJSON = StringPool.BLANK;

		try {
			final IRI classIRI = IRI.create(classIRIString);
			final int startIndex = (pageNumber - 1) * LuceneConstants.ITEMS_PER_PAGE;  // our page is 1 based - so we need to convert to zero based
			final int endIndex = startIndex + LuceneConstants.ITEMS_PER_PAGE;

			final long startTime = System.currentTimeMillis();
			final Searcher searcher = new Searcher(LuceneConstants.LUCENE_INDEX_DIRECTORY);
			final TopDocs hits = searcher.searchEnglishWords(searchString, classIRI.getShortForm(), startIndex, LuceneConstants.ITEMS_PER_PAGE);
			final long endTime = System.currentTimeMillis();

			this.log.info("Search time: " + (endTime - startTime) + "ms");

			individualJSON = OWLIndividualUtil.buildIndividualJSON(searcher, hits, startIndex + 1, endIndex);
		} catch (final Exception e) {
			this.log.error(e.toString(), e);
		}

		return individualJSON;
	}

	/**
	 * Discard/Rollback changes of an ontology (data is loaded from DB).
	 *
	 * @param ontologyId the ontology id
	 * @param request the request
	 * @param response the response
	 * @return the string
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/discardChanges", method = RequestMethod.POST)
	@ResponseBody
	public boolean discardChanges(@RequestParam("ontologyId") final Long ontologyId,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontologyId);

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();
		OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

		boolean isChangeAppliedSuccessfully = true;

		try {
			// Remove from lucene index
			OntologyController.indexer.rollbackChanges();

			// First, remove loaded ontology from the OWLManager
			manager.removeOntology(owlOntology);

			// Get original version from DB & imported again into OWLManager
			owlOntology = manager.loadOntologyFromOntologyDocument(new StringDocumentSource(
					ontologyContent.getContent()));

			// Then, update the cache that has already loaded into the memory based the given key, which is ontology id
			final Cache owlOntologyContentCache = this.cacheManager.getCache("owlOntology");
			owlOntologyContentCache.put(ontologyId, owlOntology);

		} catch (final Exception e) {
			isChangeAppliedSuccessfully = false;
			this.log.error(e.toString(), e);
		}

		return isChangeAppliedSuccessfully;
	}

	/**
	 * Save changes of an ontology to database.
	 *
	 * @param ontologyId the ontology id
	 * @param request the request
	 * @param response the response
	 * @return the string
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/saveChanges", method = RequestMethod.POST)
	@ResponseBody
	public boolean saveChanges(@RequestParam("ontologyId") final Long ontologyId,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontologyId);

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();
		final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

		boolean isChangeAppliedSuccessfully = true;

		try {
			// Create an output stream to store the new changes content
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// Write all changes to the output stream
			manager.saveOntology(owlOntology, new OWLXMLDocumentFormat(), baos);

			// Set new content to the hibernate ontology object
			ontologyContent.setContent(baos.toString());
			// Perform save action
			ontologyContent = this.ontologyContentManager.save(ontologyContent);

			// Update cache
			final Cache ontologyModelCache = this.cacheManager.getCache("ontologyContent");
			ontologyModelCache.put(ontologyId, ontologyContent);

		} catch (final Exception e) {
			isChangeAppliedSuccessfully = false;
			this.log.error(e.toString(), e);
		}

		return isChangeAppliedSuccessfully;
	}

	/**
	 * Delete the individual data property assertion.
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
	@RequestMapping(value = "/deleteIndividualDPA", method = RequestMethod.POST)
	@ResponseBody
	public String deleteIndividualDPA(@RequestParam("ontologyId") final Long ontologyId, @RequestParam(value="currentIndividual") final String individualIRIString,
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
		ChangeApplied changes = null;

		boolean isChangeAppliedSuccessfully = false;

		try {
			final IRI individualIRI = IRI.create(individualIRIString);

			if(OWLUtil.checkOWLIndividualIRIExistence(owlOntology, individualIRI)){
				currentSelectedIndividual = factory.getOWLNamedIndividual(individualIRI);

				final OWLDataProperty selectedOWLDataProp = factory.getOWLDataProperty(IRI.create(dataProperty));
				OWLLiteral owlLiteral = null;

				// Get owl literal
				if(dpDatatype.equals(OWLConstants.RDF_PLAIN_LITERAL_IRI)){
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

				changes = manager.removeAxiom(owlOntology, axiom);

				// Apply change to this ontology
				if(null != changes){
					isChangeAppliedSuccessfully = true;
				}
			}

			writer.beginObject();
			writer.name("changeApplied").value(isChangeAppliedSuccessfully);
			writer.name("individualData").beginArray();

			// Build owl named individual data and display it into the UI
			if(null != currentSelectedIndividual){
				OWLIndividualUtil.buildOWLNamedIndividualJSONDetails(writer, owlOntology, currentSelectedIndividual);
			}

			// End individual data object
			writer.endArray();

			// End object
			writer.endObject();

		} catch (final Exception e) {
			this.log.error(e.toString(), e);
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

		boolean isChangeAppliedSuccessfully = false;

		try {
			final IRI individualIRI = IRI.create(individualIRIString);

			if(OWLUtil.checkOWLIndividualIRIExistence(owlOntology, individualIRI)){
				currentSelectedIndividual = factory.getOWLNamedIndividual(individualIRI);

				final OWLDataProperty selectedOWLDataProp = factory.getOWLDataProperty(IRI.create(dataProperty));
				OWLLiteral owlLiteral = null;

				// Get owl literal
				if(dpDatatype.equals(OWLConstants.RDF_PLAIN_LITERAL_IRI)){
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

				// Apply change to this ontology
				if(manager.addAxiom(owlOntology, axiom) == ChangeApplied.SUCCESSFULLY){
					isChangeAppliedSuccessfully = true;
				}
			}

			writer.beginObject();
			writer.name("changeApplied").value(isChangeAppliedSuccessfully);
			writer.name("individualData").beginArray();

			// Build owl named individual data and display it into the UI
			if(null != currentSelectedIndividual){
				OWLIndividualUtil.buildOWLNamedIndividualJSONDetails(writer, owlOntology, currentSelectedIndividual);
			}

			// End individual data object
			writer.endArray();

			// End object
			writer.endObject();

		} catch (final Exception e) {
			this.log.error(e.toString(), e);
		}

		return out.toString();
	}

	/**
	 * Gets the DP hierarchy.
	 *
	 * @param ontologyId the ontology id
	 * @param request the request
	 * @param response the response
	 * @return the DP hierarchy
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/getDPHierarchy", method = RequestMethod.GET)
	@ResponseBody
	public String getDPHierarchy(@RequestParam("ontologyId") final Long ontologyId,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());
		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		try {

			// Get ontology content and load it into OWLOntology class (of
			// owlapi)
			final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

			// Create factory to get root class, i.e. owl:Thing
			final OWLDataFactory factory = manager.getOWLDataFactory();

			// Create structural reasoner factory to traverse all the nodes of
			// owl
			final OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			final OWLReasoner reasoner = reasonerFactory
					.createReasoner(owlOntology);

			// Get OWL top data property, the root node of all object properties
			final OWLDataProperty topDataProp = factory.getOWLTopDataProperty();

			writer.beginArray();
			OWLDataPropertyUtil.buildOWLDataPropHierarchy(writer, reasoner, factory, owlOntology, topDataProp);
			writer.endArray();

			writer.close();
		} catch (final OWLOntologyCreationException e) {
			this.log.error(e.toString(), e);
		} catch (final IOException e) {
			this.log.error(e.toString(), e);
		}

		return out.toString();
	}

	/**
	 * Delete individual OPA/NOPA.
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
	@RequestMapping(value = "/deleteIndividualOPA", method = RequestMethod.POST)
	@ResponseBody
	public String deleteIndividualOPA(@RequestParam("ontologyId") final Long ontologyId, @RequestParam(value="currentIndividual") final String individualIRIString,
			@RequestParam(value="selectedOP") final String selectedOP, @RequestParam(value="selectedIdv") final String selectedIdv, @RequestParam(value="isNegative") final boolean isNegative,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();
		final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);
		final OWLDataFactory factory = manager.getOWLDataFactory();

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		OWLNamedIndividual currentSelectedIndividual = null;

		boolean isChangeAppliedSuccessfully = false;

		ChangeApplied changes = null;

		try {
			final IRI individualIRI = IRI.create(individualIRIString);

			if(OWLUtil.checkOWLIndividualIRIExistence(owlOntology, individualIRI)){
				currentSelectedIndividual = factory.getOWLNamedIndividual(individualIRI);

				final OWLNamedIndividual selectedIndividual = factory.getOWLNamedIndividual(IRI.create(selectedIdv));
				final OWLObjectProperty selectedOWLObjectProp = factory.getOWLObjectProperty(IRI.create(selectedOP));

				OWLPropertyAssertionAxiom<?, ?> axiom = null;

				if(isNegative){
					axiom = factory.getOWLNegativeObjectPropertyAssertionAxiom(selectedOWLObjectProp, currentSelectedIndividual, selectedIndividual);
				} else {
					axiom = factory.getOWLObjectPropertyAssertionAxiom(selectedOWLObjectProp, currentSelectedIndividual, selectedIndividual);
				}

				changes = manager.removeAxiom(owlOntology, axiom);

				// Apply change to this ontology
				if(null != changes){
					isChangeAppliedSuccessfully = true;
				}
			}

			writer.beginObject();
			writer.name("changeApplied").value(isChangeAppliedSuccessfully);
			writer.name("individualData").beginArray();

			// Build owl named individual data and display it into the UI
			if(null != currentSelectedIndividual){
				OWLIndividualUtil.buildOWLNamedIndividualJSONDetails(writer, owlOntology, currentSelectedIndividual);
			}

			// End individual data object
			writer.endArray();

			// End object
			writer.endObject();

		} catch (final Exception e) {
			this.log.error(e.toString(), e);
		}

		return out.toString();
	}

	/**
	 * Adds the individual object property assertion.
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
		final OWLDataFactory factory = manager.getOWLDataFactory();

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		OWLNamedIndividual currentSelectedIndividual = null;

		boolean isChangeAppliedSuccessfully = false;

		try {
			final IRI individualIRI = IRI.create(individualIRIString);

			if(OWLUtil.checkOWLIndividualIRIExistence(owlOntology, individualIRI)){
				currentSelectedIndividual = factory.getOWLNamedIndividual(individualIRI);

				final OWLNamedIndividual selectedIndividual = factory.getOWLNamedIndividual(IRI.create(selectedIdv));
				final OWLObjectProperty selectedOWLObjectProp = factory.getOWLObjectProperty(IRI.create(selectedOP));

				OWLPropertyAssertionAxiom<?, ?> axiom = null;

				if(isNegative){
					axiom = factory.getOWLNegativeObjectPropertyAssertionAxiom(selectedOWLObjectProp, currentSelectedIndividual, selectedIndividual);
				} else {
					axiom = factory.getOWLObjectPropertyAssertionAxiom(selectedOWLObjectProp, currentSelectedIndividual, selectedIndividual);
				}

				// Apply change to this ontology
				if(manager.addAxiom(owlOntology, axiom) == ChangeApplied.SUCCESSFULLY){
					isChangeAppliedSuccessfully = true;
				}
			}

			writer.beginObject();
			writer.name("changeApplied").value(isChangeAppliedSuccessfully);
			writer.name("individualData").beginArray();

			// Build owl named individual data and display it into the UI
			if(null != currentSelectedIndividual){
				OWLIndividualUtil.buildOWLNamedIndividualJSONDetails(writer, owlOntology, currentSelectedIndividual);
			}

			// End individual data object
			writer.endArray();

			// End object
			writer.endObject();

		} catch (final Exception e) {
			this.log.error(e.toString(), e);
		}

		return out.toString();
	}

	/**
	 * Gets the OP hierarchy.
	 *
	 * @param ontologyId the ontology id
	 * @param request the request
	 * @param response the response
	 * @return the OP hierarchy
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/getOPHierarchy", method = RequestMethod.GET)
	@ResponseBody
	public String getOPHierarchy(@RequestParam("ontologyId") final Long ontologyId,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());
		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		try {

			// Get ontology content and load it into OWLOntology class (of
			// owlapi)
			final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

			// Create factory to get root class, i.e. owl:Thing
			final OWLDataFactory factory = manager.getOWLDataFactory();

			// Create structural reasoner factory to traverse all the nodes of
			// owl
			final OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			final OWLReasoner reasoner = reasonerFactory
					.createReasoner(owlOntology);

			// Get OWL top object property, the root node of all object properties
			final OWLObjectProperty topObjectProp = factory.getOWLTopObjectProperty();

			writer.beginArray();
			OWLObjectPropertyUtil.buildOWLObjectPropHierarchy(writer, reasoner, factory, owlOntology, topObjectProp);
			writer.endArray();

			writer.close();
		} catch (final OWLOntologyCreationException e) {
			this.log.error(e.toString(), e);
		} catch (final IOException e) {
			this.log.error(e.toString(), e);
		}

		return out.toString();
	}

	/**
	 * Delete individual annotation.
	 *
	 * @param ontologyId the ontology id
	 * @param individualIRIString the individual iri string
	 * @param annotationProperty the annotation property
	 * @param annotationValue the annotation value
	 * @param annotationDatatype the annotation datatype
	 * @param annotationLanguage the annotation language
	 * @param isRDFPlainLiteral the is rdf plain literal
	 * @param request the request
	 * @param response the response
	 * @return the string
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/deleteIndividualAnnotation", method = RequestMethod.POST)
	@ResponseBody
	public String deleteIndividualAnnotation(@RequestParam("ontologyId") final Long ontologyId, @RequestParam(value="currentIndividual") final String individualIRIString,
			@RequestParam(value="annotationProperty") final String annotationProperty, @RequestParam(value="annotationValue") final String annotationValue,
			@RequestParam(value="annotationDatatype") final String annotationDatatype, @RequestParam(value="annotationLanguage") final String annotationLanguage,
			@RequestParam(value="isRDFPlainLiteral") final boolean isRDFPlainLiteral,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();
		final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);
		final OWLDataFactory factory = manager.getOWLDataFactory();

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		OWLNamedIndividual currentSelectedIndividual = null;

		boolean changeApplied = false;

		try {
			final IRI individualIRI = IRI.create(individualIRIString);

			if(OWLUtil.checkOWLIndividualIRIExistence(owlOntology, individualIRI)){
				currentSelectedIndividual = factory.getOWLNamedIndividual(individualIRI);

				// Get owl annotation property
				final OWLAnnotationProperty owlAnnoProp = factory.getOWLAnnotationProperty(IRI.create(annotationProperty));
				OWLAnnotationValue owlAnnoValue = null;

				// Get owl annotation value
				if(isRDFPlainLiteral){
					owlAnnoValue = factory.getOWLLiteral(annotationValue, annotationLanguage);
				} else {
					final OWL2Datatype dataType = OWL2Datatype.getDatatype(IRI.create(annotationDatatype));
					owlAnnoValue = factory.getOWLLiteral(annotationValue, dataType);
				}

				// Get owl annotation
				final OWLAnnotation owlAnno = factory.getOWLAnnotation(owlAnnoProp, owlAnnoValue);

				// Create an annotation assertion axiom
				final OWLAnnotationAssertionAxiom owlAnnoAssertionAxion = factory.getOWLAnnotationAssertionAxiom(individualIRI, owlAnno);

				// Remove axiom
				final ChangeApplied changes = manager.removeAxiom(owlOntology, owlAnnoAssertionAxion);

				if(null != changes){
					changeApplied = true;
				}
			}

			writer.beginObject();
			writer.name("changeApplied").value(changeApplied);
			writer.name("individualData").beginArray();

			// Build owl named individual data and display it into the UI
			if(null != currentSelectedIndividual){
				OWLIndividualUtil.buildOWLNamedIndividualJSONDetails(writer, owlOntology, currentSelectedIndividual);
			}

			// End individual data object
			writer.endArray();

			// End object
			writer.endObject();

		} catch (final Exception e) {
			this.log.error(e.toString(), e);
		}

		return out.toString();
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

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		OWLNamedIndividual currentSelectedIndividual = null;

		boolean isChangeAppliedSuccessfully = false;

		try {
			final IRI individualIRI = IRI.create(individualIRIString);

			if(OWLUtil.checkOWLIndividualIRIExistence(owlOntology, individualIRI)){
				currentSelectedIndividual = factory.getOWLNamedIndividual(individualIRI);

				// Get owl annotation property
				final OWLAnnotationProperty owlAnnoProp = factory.getOWLAnnotationProperty(IRI.create(annotationProperty));
				OWLAnnotationValue owlAnnoValue = null;

				// Get owl annotation value
				if(annotationDatatype.equals(OWLConstants.RDF_PLAIN_LITERAL_IRI)){
					owlAnnoValue = factory.getOWLLiteral(annotationValue, annotationLanguage);
				} else {
					final OWL2Datatype dataType = OWL2Datatype.getDatatype(IRI.create(annotationDatatype));
					owlAnnoValue = factory.getOWLLiteral(annotationValue, dataType);
				}

				// Get owl annotation
				final OWLAnnotation owlAnno = factory.getOWLAnnotation(owlAnnoProp, owlAnnoValue);

				// Create an annotation assertion axiom
				final OWLAnnotationAssertionAxiom owlAnnoAssertionAxion = factory.getOWLAnnotationAssertionAxiom(individualIRI, owlAnno);

				// Apply change to this ontology
				if(manager.addAxiom(owlOntology, owlAnnoAssertionAxion) == ChangeApplied.SUCCESSFULLY){
					isChangeAppliedSuccessfully = true;
				}
			}

			writer.beginObject();
			writer.name("changeApplied").value(isChangeAppliedSuccessfully);
			writer.name("individualData").beginArray();

			// Build owl named individual data and display it into the UI
			if(null != currentSelectedIndividual){
				OWLIndividualUtil.buildOWLNamedIndividualJSONDetails(writer, owlOntology, currentSelectedIndividual);
			}

			// End individual data object
			writer.endArray();

			// End object
			writer.endObject();

		} catch (final Exception e) {
			this.log.error(e.toString(), e);
		}

		return out.toString();
	}

	/**
	 * Gets the annotation hierarchy.
	 *
	 * @param ontologyId the ontology id
	 * @param request the request
	 * @param response the response
	 * @return the class hierarchy
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/getAnnotationHierarchy", method = RequestMethod.GET)
	@ResponseBody
	public String getAnnotationHierarchy(@RequestParam("ontologyId") final Long ontologyId,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());
		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		try {

			// Get ontology content and load it into OWLOntology class (of
			// owlapi)
			final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

			// Create factory to get root class, i.e. owl:Thing
			final OWLDataFactory factory = manager.getOWLDataFactory();

			// Create structural reasoner factory to traverse all the nodes of
			// owl
			final OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			final OWLReasoner reasoner = reasonerFactory
					.createReasoner(owlOntology);

			final Set<OWLAnnotationProperty> annoPropSet = owlOntology.getAnnotationPropertiesInSignature();
			final Set<OWLAnnotationProperty> builtInAnnoPropSet = this.ontologyManager.getBuiltInAnnotationProperties(factory);

			final Set<OWLAnnotationProperty> parentAnnoPropSet = new HashSet<>();

			// There is a bug (OWLAPI ver 4.0.2) that occurs when getting super/sub annotation properties of a specified annotation property
			// Third level children of an annotation property cannot be retrieve to its corresponding super property
			for(final OWLAnnotationProperty annoProp: annoPropSet){
				final Collection<OWLAnnotationProperty> superPropCollection = EntitySearcher.getSuperProperties(annoProp, owlOntology);

				if (superPropCollection.isEmpty()) {
					parentAnnoPropSet.add(annoProp);
				}
			}
			// end potential bug

			// Add built-in annotation properties to parent annotation properties
			for(final OWLAnnotationProperty builtInAnnoProp : builtInAnnoPropSet){
				if(!annoPropSet.contains(builtInAnnoProp)){
					parentAnnoPropSet.add(builtInAnnoProp);
				}
			}

			writer.beginArray();

			for(final OWLAnnotationProperty oap: parentAnnoPropSet){
				OWLAnnotationPropertyUtil.buildOWLAnnotationHierarchy(writer, reasoner, factory, owlOntology, oap);
			}

			writer.endArray();

			writer.close();
		} catch (final OWLOntologyCreationException e) {
			this.log.error(e.toString(), e);
		} catch (final IOException e) {
			this.log.error(e.toString(), e);
		}

		return out.toString();
	}

	/**
	 * delete individual type.
	 *
	 * @param ontologyId the ontology id
	 * @param individualIRIString the individual iri string
	 * @param targetClassId the target class id
	 * @param request the request
	 * @param response the response
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/deleteIndividualType", method = RequestMethod.POST)
	@ResponseBody
	public String deleteIndividualType(@RequestParam("ontologyId") final Long ontologyId, @RequestParam(value="currentIndividual") final String individualIRIString,
			@RequestParam(value="targetClassId") final String targetClassId,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();
		final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);
		final OWLDataFactory factory = manager.getOWLDataFactory();

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		OWLNamedIndividual currentSelectedIndividual = null;

		boolean changeApplied = false;

		try {
			final IRI individualIRI = IRI.create(individualIRIString);

			if(OWLUtil.checkOWLIndividualIRIExistence(owlOntology, individualIRI)){
				currentSelectedIndividual = factory.getOWLNamedIndividual(individualIRI);

				final OWLClass owlClass = factory.getOWLClass(IRI.create(targetClassId));
				final OWLClassAssertionAxiom classAssertionAxiom = factory.getOWLClassAssertionAxiom(owlClass, currentSelectedIndividual);

				// Remove axiom
				final ChangeApplied changes = manager.removeAxiom(owlOntology, classAssertionAxiom);

				if(null != changes){
					changeApplied = true;
				}
			}

			writer.beginObject();
			writer.name("changeApplied").value(changeApplied);
			writer.name("individualData").beginArray();

			// Build owl named individual data and display it into the UI
			if(null != currentSelectedIndividual){
				OWLIndividualUtil.buildOWLNamedIndividualJSONDetails(writer, owlOntology, currentSelectedIndividual);
			}

			// End individual data object
			writer.endArray();

			// End object
			writer.endObject();

		} catch (final Exception e) {
			this.log.error(e.toString(), e);
		}

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
		final OWLDataFactory factory = manager.getOWLDataFactory();

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		OWLNamedIndividual currentSelectedIndividual = null;

		// Is currently inactive
		final Map<String, Boolean> changeAppliedResults = new HashMap<String, Boolean>();

		try {
			final IRI individualIRI = IRI.create(individualIRIString);

			if(OWLUtil.checkOWLIndividualIRIExistence(owlOntology, individualIRI)){
				currentSelectedIndividual = factory.getOWLNamedIndividual(individualIRI);

				for(final String classId : StringUtils.split(classIds, ',')){
					final OWLClass owlClass = factory.getOWLClass(IRI.create(classId));

					final OWLClassAssertionAxiom classAssertionAxiom = factory.getOWLClassAssertionAxiom(owlClass, currentSelectedIndividual);

					// Apply change to this ontology
					if(manager.addAxiom(owlOntology, classAssertionAxiom) == ChangeApplied.SUCCESSFULLY){
						changeAppliedResults.put(owlClass.getIRI().getShortForm(), true);
					} else {
						changeAppliedResults.put(owlClass.getIRI().getShortForm(), false);
					}
				}
			}

			writer.beginObject();
			writer.name("individualData").beginArray();

			// Build owl named individual data and display it into the UI
			if(null != currentSelectedIndividual){
				OWLIndividualUtil.buildOWLNamedIndividualJSONDetails(writer, owlOntology, currentSelectedIndividual);
			}

			// End individual data object
			writer.endArray();

			// End object
			writer.endObject();

		} catch (final Exception e) {
			this.log.error(e.toString(), e);
		}

		return out.toString();
	}

	/**
	 * Check owl individual iri existence.
	 *
	 * @param ontologyId the ontology id
	 * @param individualName the individual name
	 * @param request the request
	 * @param response the response
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/checkOWLIndividualIRIExistence", method = RequestMethod.POST)
	@ResponseBody
	public boolean checkOWLIndividualIRIExistence(@RequestParam("ontologyId") final Long ontologyId, @RequestParam(value="individualName") final String individualName,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();
		final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

		return OWLUtil.checkOWLIndividualIRIExistenceForWordnetOntology(owlOntology, individualName);
	}

	/**
	 * Delete individual.
	 *
	 * @param ontologyId the ontology id
	 * @param individualIRIString the individual iri string
	 * @param request the request
	 * @param response the response
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/deleteIndividual", method = RequestMethod.POST)
	@ResponseBody
	public boolean deleteIndividual(@RequestParam("ontologyId") final Long ontologyId, @RequestParam(value="individualIRI") final String individualIRIString,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();
		final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);
		final OWLDataFactory factory = manager.getOWLDataFactory();

		boolean isChangeAppliedSuccessfully = false;
		final IRI individualIRI = IRI.create(individualIRIString);

		try {

			if(OWLUtil.checkOWLIndividualIRIExistence(owlOntology, individualIRI)){
				final OWLNamedIndividual owlNamedIndividual = factory.getOWLNamedIndividual(individualIRI);

				// Initialize/reset remover object
				if(this.remover == null){
					this.remover = new OWLEntityRemover(manager.getOntologies());
				} else {
					this.remover.reset();
				}

				// ask individual to accept a visit from the entity remover. The
				// remover will automatically accumulate the changes which are necessary
				// to remove the individual from the ontologies which it knows about
				owlNamedIndividual.accept(this.remover);

				// Apply changes
				final ChangeApplied changes = manager.applyChanges(this.remover.getChanges());

				if(null != changes){
					isChangeAppliedSuccessfully = true;
				}
			}

		} catch (final Exception e) {
			this.log.error(e.toString(), e);
		}

		return isChangeAppliedSuccessfully;
	}

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
	 *//*
	@RequestMapping(value = "/addNewIndividual", method = RequestMethod.POST)
	@ResponseBody
	public String addNewIndividual(@RequestParam("ontologyId") final Long ontologyId, @RequestParam(value="individualName") final String individualName,
			@RequestParam(value="classIRI") final String classIRI,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();
		final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontology);
		final OWLDataFactory factory = manager.getOWLDataFactory();

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		boolean isChangeAppliedSuccessfully = false;
		OWLNamedIndividual newOWLNamedIndividual = null;

		try {

			if(!OWLUtil.checkOWLIndividualIRIExistence(owlOntology, individualName)){
				newOWLNamedIndividual = factory.getOWLNamedIndividual(OWLUtil.createIRI(owlOntology, individualName));

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
				}

				// Or we can use another API :
				// AddAxiom addAxiom = new AddAxiom(owlOntology, classAssertionAxiom);
				// ChangeApplied changeApplied = manager.applyChange(addAxiom);
			}

			writer.beginObject();
			writer.name("changeApplied").value(isChangeAppliedSuccessfully);
			writer.name("individualData").beginArray();

			// Build owl named individual data and display it into the UI
			if(null != newOWLNamedIndividual){
				OWLIndividualUtil.buildOWLNamedIndividualJSONDetails(writer, owlOntology, newOWLNamedIndividual);
			}

			// End individual data object
			writer.endArray();

			// End object
			writer.endObject();

		} catch (final Exception e) {
			this.log.error(e.toString(), e);
		}

		return out.toString();
	}*/

	/**
	 * Gets the class hierarchy.
	 *
	 * @param ontologyId the ontology id
	 * @param request the request
	 * @param response the response
	 * @return the class hierarchy
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/getClassHierarchy", method = RequestMethod.GET)
	@ResponseBody
	public String getClassHierarchy(@RequestParam("ontologyId") final Long ontologyId,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();

		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		try {

			// Get ontology content and load it into OWLOntology class (of
			// owlapi)
			final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

			// Create factory to get root class, i.e. owl:Thing
			final OWLDataFactory factory = manager.getOWLDataFactory();

			// Create structural reasoner factory to traverse all the nodes of
			// owl
			final OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			final OWLReasoner reasoner = reasonerFactory
					.createReasoner(owlOntology);

			// Get root class
			final OWLClass owlThingClass = factory.getOWLThing();
			// build owl class hierarchy
			OWLClassUtil.buildOWLClassHierarchy(writer, reasoner, factory, owlOntology, owlThingClass);

			writer.close();
		} catch (final OWLOntologyCreationException e) {
			this.log.error(e.toString(), e);
		} catch (final IOException e) {
			this.log.error(e.toString(), e);
		}

		return out.toString();
	}

	/**
	 * Getting data for 'Classes' tab followed by JS tree JSON format: https://github.com/vakata/jstree#the-required-json-format
	 *
	 * @param ontologyId the ontology id
	 * @param classIRI the class iri
	 * @param request the request
	 * @param response the response
	 * @return the class details
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/getClassDetails", method = RequestMethod.GET)
	@ResponseBody
	public String getClassDetails(@RequestParam("ontologyId") final Long ontologyId, @RequestParam(value="id") final String classIRI,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		// Create an OWL manager to manipulate this ontology
		// For more reference of how to use OWLAPI, please go to
		// https://github.com/owlcs/owlapi/wiki/Documentation
		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();

		// Initialize json writer and write json format for jstree.
		// For reference, go to https://www.jstree.com/ or
		// https://github.com/vakata/jstree#the-required-json-format
		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		try {

			// Get ontology content and load it into OWLOntology class (of
			// owlapi)
			final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

			// Create factory to get root class, i.e. owl:Thing
			final OWLDataFactory factory = manager.getOWLDataFactory();

			// Create structural reasoner factory to traverse all the nodes of
			// owl
			final OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			final OWLReasoner reasoner = reasonerFactory
					.createReasoner(owlOntology);

			// Get root class
			final OWLClass owlClass = factory.getOWLClass(IRI.create(classIRI));

			if(owlClass.isOWLThing()){
				OWLClassUtil.buildOWLClassJSONDetails(writer, reasoner, factory, owlOntology, owlClass);
			} else {
				// Get direct subclasses of root class
				final Set<OWLClass> owlSubClasses = reasoner
						.getSubClasses(owlClass, true).getFlattened();
				owlSubClasses.remove(factory.getOWLNothing());

				writer.beginArray();

				// Display subclasses
				if (owlSubClasses != null
						&& !owlSubClasses.isEmpty()) {

					// Display sub classes
					for (final OWLClass directSubClass : HTMLRenderer.toSortedSet(owlSubClasses)) {
						OWLClassUtil.buildOWLClassJSONDetails(writer, reasoner, factory, owlOntology, directSubClass);
					}
				}

				writer.endArray();
			}

			writer.close();
		} catch (final OWLOntologyCreationException e) {
			this.log.error(e.toString(), e);
		} catch (final IOException e) {
			this.log.error(e.toString(), e);
		}

		return out.toString();
	}

	/**
	 * Using jsTree JSON data format
	 * Reference here: https://github.com/vakata/jstree#the-required-json-format
	 *
	 * @param ontologyId the ontology id
	 * @param request the request
	 * @param response the response
	 * @return the OP root content
	 */
	@RequestMapping(value = "/getOPRootContent", method = RequestMethod.GET)
	@ResponseBody
	public String getOPRootContent(@RequestParam("ontologyId") final Long ontologyId, final HttpServletRequest request,
			final HttpServletResponse response) {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		// Create an OWL manager to manipulate this ontology
		// For more reference of how to use OWLAPI, please go to
		// https://github.com/owlcs/owlapi/wiki/Documentation
		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();

		// Initialize json writer and write json format for jstree.
		// For reference, go to https://www.jstree.com/ or
		// https://github.com/vakata/jstree#the-required-json-format
		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		try {

			// Get ontology content and load it into OWLOntology class (of
			// owlapi)
			final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

			// Create factory to get root class, i.e. owl:Thing
			final OWLDataFactory factory = manager.getOWLDataFactory();

			// Create structural reasoner factory to traverse all the nodes of
			// owl
			final OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			final OWLReasoner reasoner = reasonerFactory
					.createReasoner(owlOntology);

			// Get OWL top object property, the root node of all object properties
			final OWLObjectProperty topObjectProp = factory.getOWLTopObjectProperty();

			// Get all sub properties of top object property
			final Set<OWLObjectPropertyExpression> subProps = reasoner.getSubObjectProperties(topObjectProp, true).getFlattened();

			// Because each of sub property of top object property always has its inverse object prop ==> filter all of them
			final Set<OWLObjectPropertyExpression> filteredSubProperties = subProps
					.stream()
					.filter(subProp -> !(subProp instanceof OWLObjectInverseOf)
							&& !subProp.isOWLBottomObjectProperty())
							.collect(Collectors.toCollection(TreeSet::new));

			writer.beginArray();
			writer.beginObject();
			writer.name("text").value(topObjectProp.getIRI().getShortForm());
			//writer.name("id").value(topObjectProp.getIRI().toString());
			writer.name("icon").value("fa fa-square op-class");

			// Specify an IRI to display it into UI or to retrieve its children later
			writer.name("data").beginObject();
			writer.name("nodeId").value(topObjectProp.getIRI().toString());
			writer.endObject();

			// Display subclasses
			if (subProps != null
					&& !subProps.isEmpty()) {
				writer.name("children").beginArray();

				// Display sub properties
				OWLObjectPropertyUtil.buildOWLObjectPropDetailsJSON(writer, reasoner, factory, owlOntology, topObjectProp, filteredSubProperties);

				writer.endArray();

			} else {
				writer.name("children").value(false);
			}

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

			writer.endObject();
			writer.endArray();

			writer.close();
		} catch (final Exception e) {
			this.log.error(e.toString(), e);
		}

		//log.debug(out.toString());

		return out.toString();
	}

	/**
	 * Gets the OP children content.
	 *
	 * @param ontologyId the ontology id
	 * @param classIRI the class iri
	 * @param request the request
	 * @param response the response
	 * @return the OP children content
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/getOPChildrenContent", method = RequestMethod.GET)
	@ResponseBody
	public String getOPChildrenContent(@RequestParam("ontologyId") final Long ontologyId, @RequestParam("id") final String classIRI,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		// Create an OWL manager to manipulate this ontology
		// For more reference of how to use OWLAPI, please go to
		// https://github.com/owlcs/owlapi/wiki/Documentation
		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();

		// Initialize json writer and write json format for jstree.
		// For reference, go to https://www.jstree.com/ or
		// https://github.com/vakata/jstree#the-required-json-format
		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		try {

			// Get ontology content and load it into OWLOntology class (of
			// owlapi)
			final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

			// Create factory to get root class, i.e. owl:Thing
			final OWLDataFactory factory = manager.getOWLDataFactory();

			// Create structural reasoner factory to traverse all the nodes of
			// owl
			final OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			final OWLReasoner reasoner = reasonerFactory
					.createReasoner(owlOntology);

			final IRI iri = IRI.create(classIRI);

			// Get parent prop
			final OWLObjectProperty owlObjectProp = factory.getOWLObjectProperty(iri);

			// Get direct subclasses of root class
			final Set<OWLObjectPropertyExpression> owlDirectSubProperties = reasoner
					.getSubObjectProperties(owlObjectProp, true).getFlattened();

			// Because each of sub property of top object property always has its inverse object prop or bottom object prop ==> filter all of them
			final Set<OWLObjectPropertyExpression> filteredSubProperties = owlDirectSubProperties
					.stream()
					.filter(subProp -> !(subProp instanceof OWLObjectInverseOf)
							&& !subProp.isOWLBottomObjectProperty())
							.collect(Collectors.toCollection(TreeSet::new));

			writer.beginArray();

			// Display subclasses
			if (owlDirectSubProperties != null
					&& !owlDirectSubProperties.isEmpty()) {
				OWLObjectPropertyUtil.buildOWLObjectPropDetailsJSON(writer, reasoner, factory, owlOntology, owlObjectProp, filteredSubProperties);
			}

			writer.endArray();
			writer.close();
		} catch (final OWLOntologyCreationException e) {
			this.log.error(e.toString(), e);
		} catch (final IOException e) {
			this.log.error(e.toString(), e);
		}

		//log.debug(out.toString());

		return out.toString();
	}

	/**
	 * Using jsTree JSON data format
	 * Reference here: https://github.com/vakata/jstree#the-required-json-format
	 *
	 * @param ontologyId the ontology id
	 * @param request the request
	 * @param response the response
	 * @return the DP root content
	 */
	@RequestMapping(value = "/getDPRootContent", method = RequestMethod.GET)
	@ResponseBody
	public String getDPRootContent(@RequestParam("ontologyId") final Long ontologyId, final HttpServletRequest request,
			final HttpServletResponse response) {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		// Create an OWL manager to manipulate this ontology
		// For more reference of how to use OWLAPI, please go to
		// https://github.com/owlcs/owlapi/wiki/Documentation
		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();

		// Initialize json writer and write json format for jstree.
		// For reference, go to https://www.jstree.com/ or
		// https://github.com/vakata/jstree#the-required-json-format
		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		try {

			// Get ontology content and load it into OWLOntology class (of
			// owlapi)
			final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

			// Create factory to get root class, i.e. owl:Thing
			final OWLDataFactory factory = manager.getOWLDataFactory();

			// Create structural reasoner factory to traverse all the nodes of
			// owl
			final OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			final OWLReasoner reasoner = reasonerFactory
					.createReasoner(owlOntology);

			// Get OWL top object property, the root node of all object properties
			final OWLDataProperty topDataProp = factory.getOWLTopDataProperty();

			// Get all sub properties of top object property
			final Set<OWLDataProperty> subProps = reasoner.getSubDataProperties(topDataProp, true).getFlattened();
			subProps.remove(OntologyManager.getBottomDataProperty(reasoner));

			writer.beginArray();
			writer.beginObject();
			writer.name("text").value(topDataProp.getIRI().getShortForm());
			//writer.name("id").value(topObjectProp.getIRI().toString());
			writer.name("icon").value("fa fa-square dp-class");

			// Specify an IRI to display it into UI or to retrieve its children later
			writer.name("data").beginObject();
			writer.name("nodeId").value(topDataProp.getIRI().toString());
			writer.endObject();

			// Display sub props
			if (!subProps.isEmpty()) {
				writer.name("children").beginArray();

				// Display sub properties
				OWLDataPropertyUtil.buildOWLDataPropDetailsJSON(writer, reasoner, factory, owlOntology, topDataProp, subProps);

				writer.endArray();

			} else {
				writer.name("children").value(false);
			}

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

			writer.endObject();
			writer.endArray();

			writer.close();
		} catch (final Exception e) {
			this.log.error(e.toString(), e);
		}

		//log.debug(out.toString());

		return out.toString();
	}

	/**
	 * Gets the DP children content.
	 *
	 * @param ontologyId the ontology id
	 * @param classIRI the class iri
	 * @param request the request
	 * @param response the response
	 * @return the DP children content
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/getDPChildrenContent", method = RequestMethod.GET)
	@ResponseBody
	public String getDPChildrenContent(@RequestParam("ontologyId") final Long ontologyId, @RequestParam("id") final String classIRI,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		// Create an OWL manager to manipulate this ontology
		// For more reference of how to use OWLAPI, please go to
		// https://github.com/owlcs/owlapi/wiki/Documentation
		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();

		// Initialize json writer and write json format for jstree.
		// For reference, go to https://www.jstree.com/ or
		// https://github.com/vakata/jstree#the-required-json-format
		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		try {

			// Get ontology content and load it into OWLOntology class (of
			// owlapi)
			final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

			// Create factory to get root class, i.e. owl:Thing
			final OWLDataFactory factory = manager.getOWLDataFactory();

			// Create structural reasoner factory to traverse all the nodes of
			// owl
			final OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			final OWLReasoner reasoner = reasonerFactory
					.createReasoner(owlOntology);

			final IRI iri = IRI.create(classIRI);

			// Get parent prop
			final OWLDataProperty owlDataProp = factory.getOWLDataProperty(iri);

			// Get direct subclasses of root class
			final Set<OWLDataProperty> owlDirectSubProperties = reasoner
					.getSubDataProperties(owlDataProp, true).getFlattened();

			final Set<OWLDataProperty> filteredSubProperties = new HashSet<OWLDataProperty>();

			// Because each of sub property of top object property always has its inverse object prop or bottom object prop ==> filter all of them
			for (final OWLDataProperty subProp: owlDirectSubProperties) {
				if(!subProp.isOWLBottomDataProperty()){
					filteredSubProperties.add(subProp);
				}
			}

			writer.beginArray();

			// Display subclasses
			if (owlDirectSubProperties != null
					&& !owlDirectSubProperties.isEmpty()) {
				OWLDataPropertyUtil.buildOWLDataPropDetailsJSON(writer, reasoner, factory, owlOntology, owlDataProp, filteredSubProperties);
			}

			writer.endArray();
			writer.close();
		} catch (final OWLOntologyCreationException e) {
			this.log.error(e.toString(), e);
		} catch (final IOException e) {
			this.log.error(e.toString(), e);
		}

		//log.debug(out.toString());

		return out.toString();
	}

	/**
	 * Using jsTree JSON data format
	 * Reference here: https://github.com/vakata/jstree#the-required-json-format
	 *
	 * @param ontologyId the ontology id
	 * @param request the request
	 * @param response the response
	 * @return the AP root content
	 */
	@RequestMapping(value = "/getAPRootContent", method = RequestMethod.GET)
	@ResponseBody
	public String getAPRootContent(@RequestParam("ontologyId") final Long ontologyId, final HttpServletRequest request,
			final HttpServletResponse response) {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		// Create an OWL manager to manipulate this ontology
		// For more reference of how to use OWLAPI, please go to
		// https://github.com/owlcs/owlapi/wiki/Documentation
		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();

		// Initialize json writer and write json format for jstree.
		// For reference, go to https://www.jstree.com/ or
		// https://github.com/vakata/jstree#the-required-json-format
		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		try {

			// Get ontology content and load it into OWLOntology class (of
			// owlapi)
			final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

			// Create factory to get root class, i.e. owl:Thing
			final OWLDataFactory factory = manager.getOWLDataFactory();

			// Create structural reasoner factory to traverse all the nodes of
			// owl
			final OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			final OWLReasoner reasoner = reasonerFactory
					.createReasoner(owlOntology);

			final Set<OWLAnnotationProperty> annoPropSet = owlOntology.getAnnotationPropertiesInSignature();
			final Set<OWLAnnotationProperty> builtInAnnoPropSet = this.ontologyManager.getBuiltInAnnotationProperties(factory);

			final Set<OWLAnnotationProperty> parentAnnoPropSet = new HashSet<>();

			//this.log.debug("Annotation Property size: " + annoPropSet.size());

			// There is a bug (OWLAPI ver 4.0.2) that occurs when getting super/sub annotation properties of a specified annotation property
			// Third level children of an annotation property cannot be retrieve to its corresponding super property
			for(final OWLAnnotationProperty annoProp: annoPropSet){
				final Collection<OWLAnnotationProperty> superPropCollection = EntitySearcher.getSuperProperties(annoProp, owlOntology);
				//final Collection<OWLAnnotationProperty> subPropCollection = EntitySearcher.getSubProperties(annoProp, owlOntology);

				final boolean isSuperPropEmpty = superPropCollection.isEmpty();


				if (isSuperPropEmpty) {
					parentAnnoPropSet.add(annoProp);
				}

				//				for(final OWLAnnotationProperty superProp: superPropCollection){
				//					this.log.debug("Super prop: " + superProp.toString());
				//				}
				//
				//				for(final OWLAnnotationProperty subProp: subPropCollection){
				//					this.log.debug("Sub prop: " + subProp.toString());
				//				}
				//
				//				this.log.debug("Sub Anno Prop Axiom : " + owlOntology.getSubAnnotationPropertyOfAxioms(annoProp));
			}
			// end potential bug

			// Add built-in annotation properties to parent annotation properties
			for(final OWLAnnotationProperty builtInAnnoProp : builtInAnnoPropSet){
				if(!annoPropSet.contains(builtInAnnoProp)){
					parentAnnoPropSet.add(builtInAnnoProp);
				}
			}

			//			for (final OWLAnnotationProperty annoProp: parentAnnoPropSet) {
			//				this.log.debug("Parent Anno Prop: " + annoProp.toString());
			//			}

			writer.beginArray();
			writer.beginObject();
			writer.name("text").value(this.getText("ontology.details.properties.annotation", request.getLocale()));
			//writer.name("id").value(topObjectProp.getIRI().toString());
			writer.name("icon").value("fa fa-square ap-class");

			// Specify an IRI to display it into UI or to retrieve its children later
			writer.name("data").beginObject();
			writer.name("nodeId").value(StringPool.NONE);
			writer.endObject();

			// Display subclasses
			if (parentAnnoPropSet != null
					&& !parentAnnoPropSet.isEmpty()) {
				writer.name("children").beginArray();

				// Display sub properties
				OWLAnnotationPropertyUtil.buildOWLAnnoPropDetailsJSON(writer, reasoner, factory, owlOntology, null, parentAnnoPropSet);

				writer.endArray();

			} else {
				writer.name("children").value(false);
			}

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

			writer.endObject();
			writer.endArray();

			writer.close();
		} catch (final Exception e) {
			this.log.error(e.toString(), e);
		}

		//log.debug(out.toString());

		return out.toString();
	}

	/**
	 * Gets the AP children content.
	 *
	 * @param ontologyId the ontology id
	 * @param annoIRI the anno iri
	 * @param request the request
	 * @param response the response
	 * @return the AP children content
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/getAPChildrenContent", method = RequestMethod.GET)
	@ResponseBody
	public String getAPChildrenContent(@RequestParam("ontologyId") final Long ontologyId, @RequestParam("id") final String annoIRI,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		// Create an OWL manager to manipulate this ontology
		// For more reference of how to use OWLAPI, please go to
		// https://github.com/owlcs/owlapi/wiki/Documentation
		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();

		// Initialize json writer and write json format for jstree.
		// For reference, go to https://www.jstree.com/ or
		// https://github.com/vakata/jstree#the-required-json-format
		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		try {

			// Get ontology content and load it into OWLOntology class (of
			// owlapi)
			final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

			// Create factory to get root class, i.e. owl:Thing
			final OWLDataFactory factory = manager.getOWLDataFactory();

			// Create structural reasoner factory to traverse all the nodes of
			// owl
			final OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			final OWLReasoner reasoner = reasonerFactory
					.createReasoner(owlOntology);

			final IRI iri = IRI.create(annoIRI);

			// Get parent prop
			final OWLAnnotationProperty owlAnnoProp = factory.getOWLAnnotationProperty(iri);

			// Get direct sub anno prop of parent anno prop
			final Set<OWLAnnotationProperty> owlDirectSubProperties = new HashSet<>(EntitySearcher.getSubProperties(owlAnnoProp, owlOntology));

			writer.beginArray();

			// Display sub annotation properties
			if (owlDirectSubProperties != null
					&& !owlDirectSubProperties.isEmpty()) {
				OWLAnnotationPropertyUtil.buildOWLAnnoPropDetailsJSON(writer, reasoner, factory, owlOntology, owlAnnoProp, owlDirectSubProperties);
			}

			writer.endArray();
			writer.close();
		} catch (final OWLOntologyCreationException e) {
			this.log.error(e.toString(), e);
		} catch (final IOException e) {
			this.log.error(e.toString(), e);
		}

		//log.debug(out.toString());

		return out.toString();
	}

	/**
	 * Using jsTree JSON data format
	 * Reference here: https://github.com/vakata/jstree#the-required-json-format
	 *
	 * @param ontologyId the ontology id
	 * @param iriString the iri string
	 * @param request the request
	 * @param response the response
	 * @return the class individuals
	 */
	@RequestMapping(value = "/getClassIndividuals", method = RequestMethod.GET)
	@ResponseBody
	public String getClassIndividuals(@RequestParam("ontologyId") final Long ontologyId, @RequestParam(value="id") final String iriString, final HttpServletRequest request,
			final HttpServletResponse response) {

		final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
		final OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());

		// Create an OWL manager to manipulate this ontology
		// For more reference of how to use OWLAPI, please go to
		// https://github.com/owlcs/owlapi/wiki/Documentation
		final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();

		// Initialize json writer and write json format for jstree.
		// For reference, go to https://www.jstree.com/ or
		// https://github.com/vakata/jstree#the-required-json-format
		final StringWriter out = new StringWriter();
		final JsonWriter writer = new JsonWriter(out);

		try {

			// Get ontology content and load it into OWLOntology class (of
			// owlapi)
			final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

			// Create factory to get root class, i.e. owl:Thing
			final OWLDataFactory factory = manager.getOWLDataFactory();

			// Create structural reasoner factory to traverse all the nodes of
			// owl
			final OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			final OWLReasoner reasoner = reasonerFactory
					.createReasoner(owlOntology);

			final OWLClass owlClass = factory.getOWLClass(IRI.create(iriString));

			if(owlClass.isOWLThing()){
				OWLClassUtil.buildOWLClassAndItsIndividuals(writer, reasoner, factory, owlOntology, owlClass);
			} else {
				// Get direct subclasses of root class
				final Set<OWLClass> owlSubClasses = reasoner
						.getSubClasses(owlClass, true).getFlattened();
				owlSubClasses.remove(factory.getOWLNothing());

				writer.beginArray();

				// Display subclasses
				if (owlSubClasses != null
						&& !owlSubClasses.isEmpty()) {

					// Display sub classes
					for (final OWLClass directSubClass : HTMLRenderer.toSortedSet(owlSubClasses)) {
						OWLClassUtil.buildOWLClassAndItsIndividuals(writer, reasoner, factory, owlOntology, directSubClass);
					}
				}
				writer.endArray();
			}

		} catch (final Exception e) {
			this.log.error(e.toString(), e);
		}

		return out.toString();
	}

	/**
	 * Gets the wordnet ontology indexer.
	 *
	 * @return the wordnet ontology indexer
	 */
	public static Indexer getIndexer () {
		return OntologyController.indexer;
	}

	/**
	 * Checks if is form submission.
	 *
	 * @param request the request
	 * @return true, if is form submission
	 */
	private boolean isFormSubmission(final HttpServletRequest request) {
		return request.getMethod().equalsIgnoreCase("post");
	}

	/**
	 * Checks if is adds the.
	 *
	 * @param request the request
	 * @return true, if is adds the
	 */
	protected boolean isAdd(final HttpServletRequest request) {
		final String method = request.getParameter("method");
		return method != null && method.equalsIgnoreCase("add");
	}

	/**
	 * @return the ontologyManager
	 */
	public OntologyManager getOntologyManager() {
		return this.ontologyManager;
	}
}

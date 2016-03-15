package com.vn.smartdata.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.ToStringRenderer;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.vn.smartdata.model.Ontology;
import com.vn.smartdata.model.OntologyContent;
import com.vn.smartdata.repository.OntologyRepository;


@Service("ontologyManager")
public class OntologyManager extends GenericManager<Ontology> {
	/** The log. */
	protected final transient Log log = LogFactory.getLog(this.getClass());

	private OntologyRepository ontologyRepository;

	@Autowired
	public void setOntologyRepository(final OntologyRepository ontologyRepository) {
		this.repository = ontologyRepository;
		this.ontologyRepository = ontologyRepository;
	}

	/**
	 * Gets the ontology.
	 *
	 * @param id the id
	 * @return the ontology
	 */
	public Ontology getOntology(final Long id) {
		return this.get(id);
	}

	/**
	 * Load content to owl ontology.
	 *
	 * @param manager the manager
	 * @param ontology the ontology
	 * @param ontologyId the ontology id
	 * @return the OWL ontology
	 * @throws OWLOntologyCreationException the OWL ontology creation exception
	 */
	//	@Cacheable(value="owlOntology", key="#ontology.getId().longValue()")
	//	public OWLOntology loadContentToOWLOntology (final OWLOntologyManager manager, final Ontology ontology) throws OWLOntologyCreationException {
	//		log.info("Executing loadContentToOWLOntology()... No cache data is available.");
	//		return manager.loadOntologyFromOntologyDocument(new StringDocumentSource(
	//				ontology.getContent()));
	//	}

	/**
	 * Load content to owl ontology.
	 *
	 * @param manager the manager
	 * @param ontology the ontology
	 * @param ontologyId the ontology id
	 * @return the OWL ontology
	 * @throws OWLOntologyCreationException the OWL ontology creation exception
	 */
	@Cacheable(value="owlOntologyContent", key="#ontologyContent.getId().longValue()")
	public OWLOntology loadContentToOWLOntology (final OWLOntologyManager manager, final OntologyContent ontologyContent) throws OWLOntologyCreationException {
		this.log.info("Executing loadContentToOWLOntology()... No cache data is available.");
		return manager.loadOntologyFromOntologyDocument(new StringDocumentSource(
				ontologyContent.getContent()));
	}

	/**
	 * Gets the OWL ontology manager.
	 *
	 * @return the OWL ontology manager
	 */
	@Cacheable(value="owlOntologyManager")
	public OWLOntologyManager getOWLOntologyManager() {
		this.log.info("Executing getOWLOntologyManager()... No cache data is available. ");
		return OWLManager.createOWLOntologyManager();
	}

	/**
	 * Gets the built in annotation properties.
	 *
	 * @param factory the factory
	 * @return the built in annotation properties
	 */
	@Cacheable(value="builtInAnnotationProperties")
	public Set<OWLAnnotationProperty> getBuiltInAnnotationProperties (final OWLDataFactory factory) {
		this.log.info("Executing getBuiltInAnnotationProperties()... No cache data is available.");
		//OWLDataFactory factory = manager.getOWLDataFactory();

		final Set<OWLAnnotationProperty> builtInAnnotationProps = new HashSet<OWLAnnotationProperty>();

		// Get rdfs:comment anno prop
		builtInAnnotationProps.add(factory.getRDFSComment());

		// Get rdfs:isDefinedBy anno prop
		builtInAnnotationProps.add(factory.getRDFSIsDefinedBy());

		// Get rdfs:label anno prop
		builtInAnnotationProps.add(factory.getRDFSLabel());

		// Get rdfs:seeAlso anno prop
		builtInAnnotationProps.add(factory.getRDFSSeeAlso());

		// Get owl:backwardCompatiableWith anno prop
		builtInAnnotationProps.add(factory.getOWLBackwardCompatibleWith());

		// Get owl:deprecated anno prop
		builtInAnnotationProps.add(factory.getOWLDeprecated());

		// Get owl:inCompatiableWith anno prop
		builtInAnnotationProps.add(factory.getOWLIncompatibleWith());

		// Get owl:versionInfo anno prop
		builtInAnnotationProps.add(factory.getOWLVersionInfo());

		// Get owl:priorVersion anno prop
		builtInAnnotationProps.add(factory.getOWLAnnotationProperty(OWLRDFVocabulary.OWL_PRIOR_VERSION.getIRI()));

		return builtInAnnotationProps;
	}

	/**
	 * Sets the ontology renderer.
	 *
	 * @param renderer the new ontology renderer
	 */
	public void setOntologyRenderer (final OWLObjectRenderer renderer) {
		ToStringRenderer.getInstance().setRenderer(renderer);
	}

	/**
	 * Gets the bottom object property.
	 *
	 * @param reasoner the reasoner
	 * @return the bottom object property
	 */
	public OWLObjectPropertyExpression getBottomObjectProperty (final OWLReasoner reasoner) {
		return reasoner.getBottomObjectPropertyNode().getRepresentativeElement();
	}

	/**
	 * Gets the bottom data property.
	 *
	 * @param reasoner the reasoner
	 * @return the bottom data property
	 */
	public static OWLDataProperty getBottomDataProperty (final OWLReasoner reasoner) {
		return reasoner.getBottomDataPropertyNode().getRepresentativeElement();
	}

	/**
	 * Gets the active ontology.
	 *
	 * @return the active ontology
	 */
	public Ontology getActiveOntology(){
		return this.ontologyRepository.getActiveOntology();

	}

	public List<Ontology> getAllOntologies(){
		return this.ontologyRepository.getAllOntologies();
	}
}

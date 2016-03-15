package com.vn.smartdata.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vn.smartdata.model.Annotation;
import com.vn.smartdata.repository.AnnotationRepository;


@Service("annotationManager")
public class AnnotationManager extends GenericManager<Annotation> {
	
	private AnnotationRepository annotationRepository;

	@Autowired
	public void setAnnoatationRepository(final AnnotationRepository annotationRepository) {
		this.repository = annotationRepository;
		this.annotationRepository = annotationRepository;
	}
	
	/**
	 * Gets the annotation.
	 *
	 * @param annoPropIRI the anno prop iri
	 * @param annoValue the anno value
	 * @param annoLang the anno lang
	 * @param refIndividualIRI the ref individual iri
	 * @param userId the user id
	 * @return the annotation
	 */
	public Annotation getAnnotationWithLanguage(String annoPropIRI, String annoValue, String annoLang, long individualId) {
		return this.annotationRepository.getAnnotationWithLanguage(annoPropIRI, annoValue, annoLang, individualId);
	}
	
	/**
	 * Gets the annotation.
	 *
	 * @param annoPropIRI the anno prop iri
	 * @param annoValue the anno value
	 * @param annoDataTypeIRI the anno data type iri
	 * @param refIndividualIRI the ref individual iri
	 * @param userId the user id
	 * @return the annotation
	 */
	public Annotation getAnnotation(String annoPropIRI, String annoValue, String annoDataTypeIRI, long individualId) {
		return this.annotationRepository.getAnnotation(annoPropIRI, annoValue, annoDataTypeIRI, individualId);
	}
	
	public void deleteAllAnnotationsOfIndividual(long individualId){
		this.annotationRepository.deleteAllAnnotationsOfIndividual(individualId);
	}
}

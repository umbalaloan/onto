package com.vn.smartdata.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.vn.smartdata.model.Annotation;


/**
 * The Interface OntologyRepository.
 */
public interface AnnotationRepository extends GenericRepository<Annotation> {
	
	@Query("SELECT a FROM Annotation a WHERE "
			+ "a.annotationPropertyIRI = :annoPropIRI AND "
			+ "a.annotationValue = :annoValue AND "
			+ "a.annotationDataTypeIRI IS NULL AND "
			+ "a.annotationLanguage = :annoLang AND "
			+ "a.individual.id = :individualId")
	public Annotation getAnnotationWithLanguage(@Param("annoPropIRI") String annoPropIRI, @Param("annoValue") String annoValue, @Param("annoLang") String annoLang, @Param("individualId") long individualId);
	
	@Query("SELECT a FROM Annotation a WHERE "
			+ "a.annotationPropertyIRI = :annoPropIRI AND "
			+ "a.annotationValue = :annoValue AND "
			+ "a.annotationDataTypeIRI = :annoDataTypeIRI AND "
			+ "a.annotationLanguage IS NULL AND "
			+ "a.individual.id = :individualId")
	public Annotation getAnnotation(@Param("annoPropIRI") String annoPropIRI, @Param("annoValue") String annoValue, @Param("annoDataTypeIRI") String annoDataTypeIRI, @Param("individualId") long individualId);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value="DELETE FROM annotation a WHERE a.belongs_to = :individualId")
	public void deleteAllAnnotationsOfIndividual(@Param("individualId") long individualId);
}

package com.vn.smartdata.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.vn.smartdata.model.ObjectPropertyAssertion;


/**
 * The Interface OntologyRepository.
 */
public interface ObjectPropertyAssertionRepository extends GenericRepository<ObjectPropertyAssertion> {
	
	@Query("SELECT opa FROM ObjectPropertyAssertion opa WHERE opa.isNegative = 0 AND opa.individual.id = :individualId")
	public Set<ObjectPropertyAssertion> getObjectPropertyAssertions(@Param("individualId") long individualId);
	
	@Query("SELECT opa FROM ObjectPropertyAssertion opa WHERE opa.isNegative = 1 AND opa.individual.id = :individualId")
	public Set<ObjectPropertyAssertion> getNegativeObjectPropertyAssertions(@Param("individualId") long individualId);
	
	@Query("SELECT opa FROM ObjectPropertyAssertion opa WHERE opa.objectPropertyIRI = :iri AND opa.individualIRI = :individualIRI AND opa.isNegative = :isNegative AND opa.individual.id = :individualId")
	public ObjectPropertyAssertion getObjectPropertyAssertion(@Param("iri") String objectPropIRI, @Param("individualIRI") String selectedIndividualIRI, @Param("isNegative") boolean isNegative, @Param("individualId") long individualId);
	
	@Query("SELECT opa FROM ObjectPropertyAssertion opa WHERE opa.individual.id = :individualId")
	public Set<ObjectPropertyAssertion> getAllObjectPropertyAssertions(@Param("individualId") long individualId);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value="DELETE FROM object_property_assertion opa WHERE opa.belongs_to = :individualId")
	public void deleteAllOPAsOfIndividual(@Param("individualId") long individualId);
}

package com.vn.smartdata.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.vn.smartdata.model.Type;




/**
 * The Interface OntologyRepository.
 */
public interface TypeRepository extends GenericRepository<Type> {
	
	@Query("SELECT t FROM Type t WHERE t.owlClassIRI = :iri AND t.individual.id = :individualId")
	public Type getType(@Param("iri") String classIRI, @Param("individualId") long individualId);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value="DELETE FROM type t WHERE t.belongs_to = :individualId")
	public void deleteAllTypesOfIndividual(@Param("individualId") long individualId);
}

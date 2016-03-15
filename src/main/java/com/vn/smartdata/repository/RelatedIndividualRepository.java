package com.vn.smartdata.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.vn.smartdata.model.RelatedIndividual;



/**
 * The Interface OntologyRepository.
 */
public interface RelatedIndividualRepository extends GenericRepository<RelatedIndividual> {
	
	@Query("SELECT ri FROM RelatedIndividual ri WHERE ri.individual.id = :individualId AND ri.isSameOrDifferent = 0")
	public Set<RelatedIndividual> getSameIndividuals(@Param("individualId") long individualId);
	
	@Query("SELECT ri FROM RelatedIndividual ri WHERE ri.individual.id = :individualId AND ri.isSameOrDifferent = 1")
	public Set<RelatedIndividual> getDifferentIndividuals(@Param("individualId") long individualId);
	
	/**
	 * Get related individual based on given params
	 * 
	 * Note: 
	 * - If isSameOrDifferent = true / 1, it means this individual is different
	 * - Else, isSameOrDifferent = false / 0, it means this individual is same
	 * 
	 * @param individualIRI
	 * @param isSameOrDifferent
	 * @param individualId
	 * @param userId
	 * @return
	 */
	@Query("SELECT ri FROM RelatedIndividual ri WHERE ri.individualIRI = :iri AND ri.isSameOrDifferent = :isSameOrDifferent AND ri.individual.id = :individualId")
	public RelatedIndividual getRelatedIndividual(@Param("iri") String individualIRI, @Param("isSameOrDifferent") boolean isSameOrDifferent, @Param("individualId") long individualId);
	
	@Query("SELECT ri FROM RelatedIndividual ri WHERE ri.individual.id = :individualId")
	public Set<RelatedIndividual> getRelatedIndividuals(@Param("individualId") long individualId);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value="DELETE FROM related_individual ri WHERE ri.belongs_to = :individualId")
	public void deleteAllRIsOfIndividual(@Param("individualId") long individualId);
}

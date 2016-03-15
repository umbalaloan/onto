package com.vn.smartdata.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vn.smartdata.model.Individual;



/**
 * The Interface OntologyRepository.
 */
public interface IndividualRepository extends GenericRepository<Individual> {

	@Query("SELECT i FROM Individual i WHERE i.individualIRI = :iri AND i.editor.id = :userId AND i.ontology.id = :ontologyId")
	public Individual getIndividual(@Param("iri") String iri, @Param("userId") long userId, @Param("ontologyId") long ontologyId);

	@Query("SELECT i FROM Individual i LEFT JOIN FETCH i.annotations"
			+ " LEFT JOIN FETCH i.types"
			+ " LEFT JOIN FETCH i.relatedIndividuals"
			+ " LEFT JOIN FETCH i.objectPropertyAssertions"
			+ " LEFT JOIN FETCH i.dataPropertyAssertions WHERE i.individualIRI = :iri AND i.editor.id = :userId AND i.ontology.id = :ontologyId")
	public Individual getIndividualEagerly(@Param("iri") String iri, @Param("userId") long userId, @Param("ontologyId") long ontologyId);

	@Query("SELECT i FROM Individual i LEFT JOIN FETCH i.annotations"
			+ " LEFT JOIN FETCH i.types"
			+ " LEFT JOIN FETCH i.relatedIndividuals"
			+ " LEFT JOIN FETCH i.objectPropertyAssertions"
			+ " LEFT JOIN FETCH i.dataPropertyAssertions WHERE i.individualIRI = :iri AND i.ontology.id = :ontologyId AND i.editor.id != :userId")
	public List<Individual> getOtherVersionsOfIndividual(@Param("iri") String selectedIndividualIRI, @Param("ontologyId") long ontologyId, @Param("userId") long currentUserId, Pageable pageable);
	
	@Query("SELECT count(i) FROM Individual i WHERE i.individualIRI = :iri AND i.ontology.id = :ontologyId AND i.editor.id != :userId")
	public int countOtherVersionsOfIndividual(@Param("iri") String selectedIndividualIRI, @Param("ontologyId") long ontologyId, @Param("userId") long currentUserId);
	
	@Query(nativeQuery = true, value = "SELECT u.id, u.username, u.email, u.first_name, u.last_name FROM app_user u WHERE u.id IN (SELECT i.editor FROM individual i WHERE i.iri = :iri AND i.ontology = :ontologyId AND i.editor != :userId)")
	public List<Object[]> getIndividualUsers(@Param("iri") String selectedIndividualIRI, @Param("ontologyId") long ontologyId, @Param("userId") long userId);

//	@Query("SELECT i FROM Individual i LEFT JOIN i.annotations"
//			+ " LEFT JOIN i.types"
//			+ " LEFT JOIN i.relatedIndividuals"
//			+ " LEFT JOIN i.objectPropertyAssertions"
//			+ " LEFT JOIN i.dataPropertyAssertions"
//			+ " WHERE i.individualIRI = :iri AND i.ontology.id = :ontologyId AND i.editor.id != :userId")
//	public Page<Individual> getOtherVersionsOfIndividualByPage(@Param("iri") String selectedIndividualIRI, @Param("ontologyId") long ontologyId, @Param("userId") long currentUserId, Pageable pageable);
}
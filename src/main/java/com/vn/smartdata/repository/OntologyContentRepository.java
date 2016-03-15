package com.vn.smartdata.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vn.smartdata.model.OntologyContent;

/**
 * The Interface OntologyRepository.
 */
public interface OntologyContentRepository extends GenericRepository<OntologyContent> {

	@Query("SELECT oc FROM OntologyContent oc WHERE oc.ontology = :ontologyId")
	public OntologyContent getOntologyContent(@Param("ontologyId") long ontologyId);
}

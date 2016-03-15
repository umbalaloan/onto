package com.vn.smartdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.vn.smartdata.model.Ontology;

/**
 * The Interface OntologyRepository.
 */
public interface OntologyRepository extends GenericRepository<Ontology> {

	@Query("SELECT o FROM Ontology o WHERE o.isActive = true")
	public Ontology getActiveOntology();

	@Query("SELECT o FROM Ontology o")
	public List<Ontology> getAllOntologies();
}

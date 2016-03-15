package com.vn.smartdata.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vn.smartdata.model.Individual;
import com.vn.smartdata.repository.IndividualRepository;


@Service("individualManager")
public class IndividualManager extends GenericManager<Individual> {

	private IndividualRepository individualRepository;

	@Autowired
	public void setIndividualRepository(final IndividualRepository individualRepository) {
		this.repository = individualRepository;
		this.individualRepository = individualRepository;
	}

	/**
	 * Gets the individual.
	 *
	 * @param iri the iri
	 * @param userId the user id
	 * @param ontologyId the ontology id
	 * @return the individual
	 */
	public Individual getIndividual(final String iri, final long userId, final long ontologyId){
		return this.individualRepository.getIndividual(iri, userId, ontologyId);
	}

	/**
	 * Gets the individual eagerly loading all of its relationship objects.
	 *
	 * @param iri the iri
	 * @param userId the user id
	 * @param ontologyId the ontology id
	 * @return the individual eagerly
	 */
	public Individual getIndividualEagerly(final String iri, final long userId, final long ontologyId){
		return this.individualRepository.getIndividualEagerly(iri, userId, ontologyId);
	}

	/**
	 * Gets the other versions of individual.
	 *
	 * @param selectedIndividualIRI the selected individual iri
	 * @param ontologyId the ontology id
	 * @param currentUserId the current user id
	 * @return the other versions of individual
	 */
	public List<Individual> getOtherVersionsOfIndividual (final String selectedIndividualIRI, final long ontologyId, final long currentUserId, final int page, final int size, final Sort sort) {
		final Pageable pageable = new PageRequest(page, size, sort);
		return this.individualRepository.getOtherVersionsOfIndividual(selectedIndividualIRI, ontologyId, currentUserId, pageable);
	}

	/**
	 * Count other versions of individual.
	 *
	 * @param selectedIndividualIRI the selected individual iri
	 * @param ontologyId the ontology id
	 * @param currentUserId the current user id
	 * @return the int
	 */
	public int countOtherVersionsOfIndividual(final String selectedIndividualIRI, final long ontologyId, final long currentUserId){
		return this.individualRepository.countOtherVersionsOfIndividual(selectedIndividualIRI, ontologyId, currentUserId);
	}
	
	/**
	 * Gets the individual users.
	 *
	 * @param selectedIndividualIRI the selected individual iri
	 * @param ontologyId the ontology id
	 * @return the individual users
	 */
	public List<Object[]> getIndividualUsers(String selectedIndividualIRI, long ontologyId, long userId){
		return this.individualRepository.getIndividualUsers(selectedIndividualIRI, ontologyId, userId);
	}
}

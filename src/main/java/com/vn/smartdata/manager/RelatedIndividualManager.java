package com.vn.smartdata.manager;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vn.smartdata.model.RelatedIndividual;
import com.vn.smartdata.repository.RelatedIndividualRepository;


@Service("relatedIndividualManager")
public class RelatedIndividualManager extends GenericManager<RelatedIndividual> {
	
	private RelatedIndividualRepository relatedIndividualRepository;

	@Autowired
	public void setRelatedIndividualRepository(final RelatedIndividualRepository relatedIndividualRepository) {
		this.repository = relatedIndividualRepository;
		this.relatedIndividualRepository = relatedIndividualRepository;
	}
	
	/**
	 * Gets the same individuals.
	 *
	 * @param individualId the individual id
	 * @param userId the user id
	 * @return the same individuals
	 */
	public Set<RelatedIndividual> getSameIndividuals (long individualId) {
		return this.relatedIndividualRepository.getSameIndividuals(individualId);
	}
	
	/**
	 * Gets the different individuals.
	 *
	 * @param individualId the individual id
	 * @param userId the user id
	 * @return the different individuals
	 */
	public Set<RelatedIndividual> getDifferentIndividuals (long individualId) {
		return this.relatedIndividualRepository.getDifferentIndividuals(individualId);
	}
	
	/**
	 * Gets the related individual. (Note: 0 = false = same ; 1 = true = different)
	 *
	 * @param individualIRI the individual iri
	 * @param isSameOrDifferent the is same or different
	 * @param individualId the individual id
	 * @param userId the user id
	 * @return the related individual
	 */
	public RelatedIndividual getRelatedIndividual (String individualIRI, boolean isSameOrDifferent, long individualId) {
		return this.relatedIndividualRepository.getRelatedIndividual(individualIRI, isSameOrDifferent, individualId);
	}
	
	public Set<RelatedIndividual> getRelatedIndividuals(long individualId) {
		return this.relatedIndividualRepository.getRelatedIndividuals(individualId);
	}
	
	public void deleteAllRIsOfIndividual(long individualId){
		this.relatedIndividualRepository.deleteAllRIsOfIndividual(individualId);
	}
}

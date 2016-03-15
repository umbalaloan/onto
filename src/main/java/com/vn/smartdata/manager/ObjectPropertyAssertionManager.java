package com.vn.smartdata.manager;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vn.smartdata.model.ObjectPropertyAssertion;
import com.vn.smartdata.repository.ObjectPropertyAssertionRepository;


@Service("opaManager")
public class ObjectPropertyAssertionManager extends GenericManager<ObjectPropertyAssertion> {
	
	private ObjectPropertyAssertionRepository objectPropertyAssertionRepository;

	@Autowired
	public void setObjectPropertyAssertionRepository(final ObjectPropertyAssertionRepository objectPropertyAssertionRepository) {
		this.repository = objectPropertyAssertionRepository;
		this.objectPropertyAssertionRepository = objectPropertyAssertionRepository;
	}
	
	public Set<ObjectPropertyAssertion> getObjectPropertyAssertions(long individualId){
		return this.objectPropertyAssertionRepository.getObjectPropertyAssertions(individualId);
	}
	
	public Set<ObjectPropertyAssertion> getNegativeObjectPropertyAssertions(long individualId){
		return this.objectPropertyAssertionRepository.getNegativeObjectPropertyAssertions(individualId);
	}
	
	public ObjectPropertyAssertion getObjectPropertyAssertion(String objectPropIRI, String selectedIndividualIRI, boolean isNegative, long individualId){
		return this.objectPropertyAssertionRepository.getObjectPropertyAssertion(objectPropIRI, selectedIndividualIRI, isNegative, individualId);
	}
	
	public Set<ObjectPropertyAssertion> getAllObjectPropertyAssertion(long individualId){
		return this.objectPropertyAssertionRepository.getAllObjectPropertyAssertions(individualId);
	}
	
	public void deleteAllOPAsOfIndividual(long individualId){
		this.objectPropertyAssertionRepository.deleteAllOPAsOfIndividual(individualId);
	}
}

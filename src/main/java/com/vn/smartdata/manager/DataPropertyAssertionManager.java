package com.vn.smartdata.manager;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vn.smartdata.model.DataPropertyAssertion;
import com.vn.smartdata.repository.DataPropertyAssertionRepository;


@Service("dpaManager")
public class DataPropertyAssertionManager extends GenericManager<DataPropertyAssertion> {
	
	private DataPropertyAssertionRepository dataPropertyAssertionRepository;

	@Autowired
	public void setDataPropertyAssertionRepository(final DataPropertyAssertionRepository dataPropertyAssertionRepository) {
		this.repository = dataPropertyAssertionRepository;
		this.dataPropertyAssertionRepository = dataPropertyAssertionRepository;
	}
	
	public Set<DataPropertyAssertion> getDataPropertyAssertions(long individualId){
		return this.dataPropertyAssertionRepository.getDataPropertyAssertions(individualId);
	}
	
	public Set<DataPropertyAssertion> getNegativeDataPropertyAssertions(long individualId){
		return this.dataPropertyAssertionRepository.getNegativeDataPropertyAssertions(individualId);
	}
	
	public DataPropertyAssertion getDataPropertyAssertionWithLanguage(String dataPropertyIRI, String dataPropertyValue, String dataPropertyLanguage,
			boolean isNegative, long individualId){
		return this.dataPropertyAssertionRepository.getDataPropertyAssertionWithLanguage(dataPropertyIRI, dataPropertyValue, dataPropertyLanguage, isNegative, individualId);
	}
	
	public DataPropertyAssertion getDataPropertyAssertion(String dataPropertyIRI, String dataPropertyValue, String dataPropertyTypeIRI,
			boolean isNegative, long individualId){
		return this.dataPropertyAssertionRepository.getDataPropertyAssertion(dataPropertyIRI, dataPropertyValue, dataPropertyTypeIRI, isNegative, individualId);
	}
	
	public Set<DataPropertyAssertion> getAllDataPropertyAssertions(long individualId){
		return this.dataPropertyAssertionRepository.getAllDataPropertyAssertions(individualId);
	}
	
	public void deleteAllDPAsOfIndividual(long individualId){
		this.dataPropertyAssertionRepository.deleteAllDPAsOfIndividual(individualId);
	}
}

package com.vn.smartdata.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vn.smartdata.model.Type;
import com.vn.smartdata.repository.TypeRepository;


@Service("typeManager")
public class TypeManager extends GenericManager<Type> {
	
	private TypeRepository typeRepository;

	@Autowired
	public void setTypeRepository(final TypeRepository typeRepository) {
		this.repository = typeRepository;
		this.typeRepository = typeRepository;
	}
	
	/**
	 * Gets the type.
	 *
	 * @param classIRI the class iri
	 * @param individualId the individual id
	 * @param userId the user id
	 * @return the type
	 */
	public Type getType(String classIRI, long individualId){
		return this.typeRepository.getType(classIRI, individualId);
	}
	
	public void deleteAllTypesOfIndividual(long individualId){
		this.typeRepository.deleteAllTypesOfIndividual(individualId);
	}
}

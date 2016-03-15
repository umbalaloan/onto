package com.vn.smartdata.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.vn.smartdata.model.DataPropertyAssertion;


/**
 * The Interface OntologyRepository.
 */
public interface DataPropertyAssertionRepository extends GenericRepository<DataPropertyAssertion> {
	
	@Query("SELECT dpa FROM DataPropertyAssertion dpa WHERE dpa.isNegative = 0 AND dpa.individual.id = :individualId")
	public Set<DataPropertyAssertion> getDataPropertyAssertions(@Param("individualId") long individualId);
	
	@Query("SELECT dpa FROM DataPropertyAssertion dpa WHERE dpa.isNegative = 1 AND dpa.individual.id = :individualId")
	public Set<DataPropertyAssertion> getNegativeDataPropertyAssertions(@Param("individualId") long individualId);
	
	@Query("SELECT dpa FROM DataPropertyAssertion dpa WHERE dpa.dataPropertyIRI = :iri AND dpa.dataPropertyValue = :value AND dpa.dataPropertyLanguage = :language AND "
			+ "dpa.isNegative = :isNegative AND dpa.individual.id = :individualId")
	public DataPropertyAssertion getDataPropertyAssertionWithLanguage(@Param("iri")String dataPropertyIRI, @Param("value") String dataPropertyValue, @Param("language") String dataPropertyLanguage,
			@Param("isNegative") boolean isNegative, @Param("individualId") long individualId);
	
	@Query("SELECT dpa FROM DataPropertyAssertion dpa WHERE dpa.dataPropertyIRI = :iri AND dpa.dataPropertyValue = :value AND dpa.dataPropertyTypeIRI = :dataTypeIRI AND "
			+ "dpa.isNegative = :isNegative AND dpa.individual.id = :individualId")
	public DataPropertyAssertion getDataPropertyAssertion(@Param("iri")String dataPropertyIRI, @Param("value") String dataPropertyValue, @Param("dataTypeIRI") String dataPropertyTypeIRI,
			@Param("isNegative") boolean isNegative, @Param("individualId") long individualId);
	
	@Query("SELECT dpa FROM DataPropertyAssertion dpa WHERE dpa.individual.id = :individualId")
	public Set<DataPropertyAssertion> getAllDataPropertyAssertions(@Param("individualId") long individualId);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true, value="DELETE FROM data_property_assertion dpa WHERE dpa.belongs_to = :individualId")
	public void deleteAllDPAsOfIndividual(@Param("individualId") long individualId);
}

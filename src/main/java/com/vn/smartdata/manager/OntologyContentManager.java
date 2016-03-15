package com.vn.smartdata.manager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.vn.smartdata.model.OntologyContent;
import com.vn.smartdata.repository.OntologyContentRepository;


@Service("ontologyContentManager")
public class OntologyContentManager extends GenericManager<OntologyContent> {
	/** The log. */
	protected final transient Log log = LogFactory.getLog(this.getClass());

	private OntologyContentRepository ontologyContentRepository;

	@Autowired
	public void setOntologyContentRepository(final OntologyContentRepository ontologyContentRepository) {
		this.repository = ontologyContentRepository;
		this.ontologyContentRepository = ontologyContentRepository;
	}

	@Cacheable(value="ontologyContent", key="#ontologyId")
	public OntologyContent getOntologyContent(final long ontologyId){
		return this.ontologyContentRepository.getOntologyContent(ontologyId);
	}

	@CachePut(value="ontologyContent", key="#ontologyId")
	public OntologyContent save(final OntologyContent ontologyContent, final long ontologyId){
		return super.save(ontologyContent);
	}
}

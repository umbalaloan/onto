package com.vn.smartdata.webapp.controller;


import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vn.smartdata.manager.OntologyContentManager;
import com.vn.smartdata.manager.OntologyManager;
import com.vn.smartdata.model.Ontology;
import com.vn.smartdata.model.OntologyContent;


/**
 * The Class DownloadController.
 */
@Controller
@RequestMapping("/download/*")
public class DownloadController {
	
	/** The Constant LOG. */
	private static final Log LOG = LogFactory.getLog(DownloadController.class);
	
	/** The ontology manager. */
	@Autowired
	OntologyManager ontologyManager;
	
	@Autowired
	OntologyContentManager ontologyContentManager;

	/*@RequestMapping("/{id}")
	public String download(@PathVariable("id") final Long fileId)
	{
		if (fileId != null) {
			final String fileName = this.attachmentManager.getAttachmentName(fileId);
			if(fileName != null){
				return String.format("redirect:/download/%d/%s", fileId, fileName);
			}
		}
		return String.format("redirect:/download/%d/filenotfound", fileId);
	}*/

	/**
	 * Download with changes.
	 *
	 * @param ontologyId the ontology id
	 * @param request the request
	 * @param response the response
	 * @return the response entity
	 * @throws OWLOntologyCreationException the OWL ontology creation exception
	 */
	@RequestMapping(value="/downloadChanges", method=RequestMethod.GET)
	public ResponseEntity<byte[]> downloadChanges(
			@RequestParam("ontologyId") final Long ontologyId, final HttpServletRequest request, final HttpServletResponse response)
					throws OWLOntologyCreationException {

		if (ontologyId != null) {
			final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
			OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontology.getId());
			
			final OWLOntologyManager manager = this.ontologyManager.getOWLOntologyManager();
			final OWLOntology owlOntology = this.ontologyManager.loadContentToOWLOntology(manager, ontologyContent);

			try {
				// Create an output stream to store the new changes content
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();

				// Write all changes to the output stream
				manager.saveOntology(owlOntology, new OWLXMLDocumentFormat(), baos);

				final HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.parseMediaType("application/owl+xml"));
				headers.set("Content-Disposition",
						"attachment; filename=" + URLEncoder.encode(ontology.getOntologyName() + "_withChanges.owl", "UTF-8"));

				return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	/**
	 * Download without changes.
	 *
	 * @param ontologyId the ontology id
	 * @param request the request
	 * @param response the response
	 * @return the response entity
	 * @throws OWLOntologyCreationException the OWL ontology creation exception
	 */
	@RequestMapping(value="/downloadWOChanges", method=RequestMethod.GET)
	public ResponseEntity<byte[]> downloadWOChanges(
			@RequestParam("ontologyId") final Long ontologyId, final HttpServletRequest request, final HttpServletResponse response) throws OWLOntologyCreationException {

		if (ontologyId != null) {
			final Ontology ontology = this.ontologyManager.getOntology(ontologyId);
			OntologyContent ontologyContentModel = this.ontologyContentManager.getOntologyContent(ontology.getId());
			
			try {
				final String ontologyContent = ontologyContentModel.getContent();

				final HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.parseMediaType("application/owl+xml"));

				headers.set("Content-Disposition",
						"attachment; filename=" + URLEncoder.encode(ontology.getOntologyName() + "_woChanges.owl", "UTF-8"));
				return new ResponseEntity<>(ontologyContent.getBytes(), headers, HttpStatus.OK);

			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}

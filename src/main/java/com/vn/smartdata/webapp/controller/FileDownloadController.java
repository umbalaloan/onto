package com.vn.smartdata.webapp.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vn.smartdata.manager.OntologyContentManager;
import com.vn.smartdata.manager.OntologyManager;
import com.vn.smartdata.model.Ontology;
import com.vn.smartdata.model.OntologyContent;


/**
 * This class is used for downloading file.
 *
 * @author RBVH
 */
@Controller
@RequestMapping("/download*")
public class FileDownloadController {

	/**  ServletContext. */
	@Autowired
	ServletContext context;

	/** The ontology manager. */
	@Autowired
	OntologyManager ontologyManager;
	
	@Autowired
	OntologyContentManager ontologyContentManager;

	/** Size of a byte buffer to read/write file. */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * Method for handling file download request from client.
	 *
	 * @param ontologyId the ontology id
	 * @param request            HttpServletRequest
	 * @param response            HttpServletResponse
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public void doDownload(@RequestParam("ontologyId") final long ontologyId,
			final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {

		final Ontology ontology = this.ontologyManager.get(ontologyId);
		OntologyContent ontologyContent = this.ontologyContentManager.getOntologyContent(ontologyId);
		
		final byte[] fileData = ontologyContent.getContent().getBytes();
		final InputStream input = new ByteArrayInputStream(fileData);

		// get MIME type of the file
		final String mimeType = "application/rdf+xml";

		// set content attributes for the response
		response.setContentType(mimeType);
		response.setContentLength(fileData.length);

		// set headers for the response
		final String headerKey = "Content-Disposition";
		final String headerValue = String.format("attachment; filename=\"%s\"",
				ontology.getOntologyName());
		response.setHeader(headerKey, headerValue);

		// get output stream of the response
		final OutputStream outStream = response.getOutputStream();

		final byte[] buffer = new byte[FileDownloadController.BUFFER_SIZE];
		int bytesRead = -1;

		// write bytes read from the input stream into the output stream
		while ((bytesRead = input.read(buffer)) != -1) {
			outStream.write(buffer, 0, bytesRead);
		}

		input.close();
		outStream.close();
	}

}
package com.vn.smartdata.webapp.util;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;

import com.vn.smartdata.constants.OWLConstants;
import com.vn.smartdata.constants.StringPool;


/**
 * The Class OWLUtil.
 *
 * @author TAQ1HC
 */
public class OWLUtil {

	protected final transient static Log log = LogFactory.getLog(OWLUtil.class);

	/**
	 * Check if an OWL Individual existed or not based on given name.
	 *
	 * @param owlOntology the owl ontology
	 * @param individualName the individual name
	 * @return true, if existed or there is an exception
	 */
	public static boolean checkOWLIndividualIRIExistence (final OWLOntology owlOntology, final String individualName) {
		final String owlIndividualIRI = owlOntology.getOntologyID().getOntologyIRI().get().toString() + StringPool.POUND + individualName;

		boolean isAlreadyExisted = false;

		// If user enters illegal characters such as < > [ ] { } etc., return false
		try {
			final URI uri = URI.create(owlIndividualIRI);
			isAlreadyExisted = owlOntology.containsIndividualInSignature(IRI.create(uri));
		} catch (final Exception e) {
			isAlreadyExisted = true;
		}

		return isAlreadyExisted;
	}
	
	/**
	 * Check if an OWL Individual existed or not based on given name.
	 *
	 * @param owlOntology the owl ontology
	 * @param individualName the individual name
	 * @return true, if existed or there is an exception
	 */
	public static boolean checkOWLIndividualIRIExistence2 (final OWLOntology owlOntology, final String individualName) {
		final String owlIndividualIRI = owlOntology.getOntologyID().getOntologyIRI().get().toString() + StringPool.POUND + individualName;

		boolean isAlreadyExisted = false;

		// If user enters illegal characters such as < > [ ] { } etc., return false
		try {
			final URI uri = URI.create(owlIndividualIRI);
			isAlreadyExisted = owlOntology.containsIndividualInSignature(IRI.create(uri), Imports.INCLUDED);
		} catch (final Exception e) {
			isAlreadyExisted = true;
		}

		return isAlreadyExisted;
	}
	
	/**
	 * Check owl individual iri existence for wordnet ontology (This method is only used for adding new individual, not for checking existence if the IRI has already known).
	 *
	 * @param owlOntology the owl ontology
	 * @param individualName the individual name
	 * @return true, if successful
	 */
	public static boolean checkOWLIndividualIRIExistenceForWordnetOntology (final OWLOntology owlOntology, final String individualName) {
		final String owlIndividualIRI = OWLConstants.WORDNET_ONTOLOGY_INSTANCE_IRI_PREFIX + individualName;

		boolean isAlreadyExisted = false;

		// If user enters illegal characters such as < > [ ] { } etc., return false
		try {
			final URI uri = URI.create(owlIndividualIRI);
			isAlreadyExisted = owlOntology.containsIndividualInSignature(IRI.create(uri));
		} catch (final Exception e) {
			isAlreadyExisted = true;
		}

		return isAlreadyExisted;
	}

	/**
	 * Check if an OWL Individual existed or not based on given name.
	 *
	 * @param owlOntology the owl ontology
	 * @param individualIRI the individual iri
	 * @return true, if existed
	 */
	public static boolean checkOWLIndividualIRIExistence (final OWLOntology owlOntology, final IRI individualIRI) {
		boolean isAlreadyExisted = false;

		try {
			isAlreadyExisted = owlOntology.containsIndividualInSignature(individualIRI);
		} catch (final Exception e) {
			isAlreadyExisted = true;
		}

		return isAlreadyExisted;
	}

	/**
	 * Creates the iri.
	 *
	 * @param owlOntology the owl ontology
	 * @param individualName the individual name
	 * @return the iri
	 */
	public static IRI createIRI(final OWLOntology owlOntology, final String individualName) {
		final String owlIndividualIRI = owlOntology.getOntologyID().getOntologyIRI().get().toString() + StringPool.POUND + individualName;
		return IRI.create(owlIndividualIRI);
	}
	
	/**
	 * Creates the iri.
	 *
	 * @param owlOntology the owl ontology
	 * @param individualName the individual name
	 * @return the iri
	 */
	public static IRI createIRIForWordnetOntology(final OWLOntology owlOntology, final String individualName) {
		final String owlIndividualIRI = OWLConstants.WORDNET_ONTOLOGY_INSTANCE_IRI_PREFIX + individualName;
		return IRI.create(owlIndividualIRI);
	}
	
	/**
	 * Gets the short form.
	 *
	 * @param iriString the iri string
	 * @return the short form
	 */
	public static String getShortForm(String iriString){
		IRI iri = IRI.create(iriString);
		return iri.getShortForm();
	}
}

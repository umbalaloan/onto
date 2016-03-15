package com.vn.smartdata.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "type")
@NamedQuery(name = "Type.findAll", query = "SELECT t FROM Type t")
public class Type extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -2067882498051479089L;
	
	@Column(name = "iri")
	private String owlClassIRI;
	
	@Column(name = "short_form")
	private String owlClassShortForm;
	
	@Column(name = "created_date")
	private Date createdDate;
	
	@Column(name = "reference_individual_iri")
	private String referenceIndividualIRI;

	@ManyToOne
	@JoinColumn(name = "editor")
	private User editor;
	
	@ManyToOne
	@JoinColumn(name = "belongs_to")
	private Individual individual;

	/**
	 * @return the owlClassIRI
	 */
	public String getOwlClassIRI() {
		return owlClassIRI;
	}

	/**
	 * @param owlClassIRI the owlClassIRI to set
	 */
	public void setOwlClassIRI(String owlClassIRI) {
		this.owlClassIRI = owlClassIRI;
	}

	/**
	 * @return the owlClassShortForm
	 */
	public String getOwlClassShortForm() {
		return owlClassShortForm;
	}

	/**
	 * @param owlClassShortForm the owlClassShortForm to set
	 */
	public void setOwlClassShortForm(String owlClassShortForm) {
		this.owlClassShortForm = owlClassShortForm;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the referenceIndividualIRI
	 */
	public String getReferenceIndividualIRI() {
		return referenceIndividualIRI;
	}

	/**
	 * @param referenceIndividualIRI the referenceIndividualIRI to set
	 */
	public void setReferenceIndividualIRI(String referenceIndividualIRI) {
		this.referenceIndividualIRI = referenceIndividualIRI;
	}

	/**
	 * @return the editor
	 */
	public User getEditor() {
		return editor;
	}

	/**
	 * @param editor the editor to set
	 */
	public void setEditor(User editor) {
		this.editor = editor;
	}

	/**
	 * @return the individual
	 */
	public Individual getIndividual() {
		return individual;
	}

	/**
	 * @param individual the individual to set
	 */
	public void setIndividual(Individual individual) {
		this.individual = individual;
	}
}

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
@Table(name = "object_property_assertion")
@NamedQuery(name = "ObjectPropertyAssertion.findAll", query = "SELECT opa FROM ObjectPropertyAssertion opa")
public class ObjectPropertyAssertion extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 365715981989643005L;

	@Column(name = "iri")
	private String objectPropertyIRI;

	@Column(name = "short_form")
	private String objectPropertyShortForm;

	@Column(name = "individual_iri")
	private String individualIRI;

	@Column(name = "individual_short_form")
	private String individualShortForm;

	@Column(name = "is_negative")
	private boolean isNegative;

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
	 * @return the objectPropertyIRI
	 */
	public String getObjectPropertyIRI() {
		return objectPropertyIRI;
	}

	/**
	 * @param objectPropertyIRI the objectPropertyIRI to set
	 */
	public void setObjectPropertyIRI(String objectPropertyIRI) {
		this.objectPropertyIRI = objectPropertyIRI;
	}

	/**
	 * @return the objectPropertyShortForm
	 */
	public String getObjectPropertyShortForm() {
		return objectPropertyShortForm;
	}

	/**
	 * @param objectPropertyShortForm the objectPropertyShortForm to set
	 */
	public void setObjectPropertyShortForm(String objectPropertyShortForm) {
		this.objectPropertyShortForm = objectPropertyShortForm;
	}

	/**
	 * @return the individualIRI
	 */
	public String getIndividualIRI() {
		return individualIRI;
	}

	/**
	 * @param individualIRI the individualIRI to set
	 */
	public void setIndividualIRI(String individualIRI) {
		this.individualIRI = individualIRI;
	}

	/**
	 * @return the individualShortForm
	 */
	public String getIndividualShortForm() {
		return individualShortForm;
	}

	/**
	 * @param individualShortForm the individualShortForm to set
	 */
	public void setIndividualShortForm(String individualShortForm) {
		this.individualShortForm = individualShortForm;
	}

	/**
	 * @return the isNegative
	 */
	public boolean isNegative() {
		return isNegative;
	}

	/**
	 * @param isNegative the isNegative to set
	 */
	public void setNegative(boolean isNegative) {
		this.isNegative = isNegative;
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

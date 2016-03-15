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
@Table(name = "data_property_assertion")
@NamedQuery(name = "DataPropertyAssertion.findAll", query = "SELECT dpa FROM DataPropertyAssertion dpa")
public class DataPropertyAssertion extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = -5532551372272839618L;

	@Column(name = "iri")
	private String dataPropertyIRI;

	@Column(name = "short_form")
	private String dataPropertyShortForm;

	@Column(name = "value")
	private String dataPropertyValue;

	@Column(name = "data_type")
	private String dataPropertyType;

	@Column(name = "data_type_iri")
	private String dataPropertyTypeIRI;

	@Column(name = "language")
	private String dataPropertyLanguage;
	
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
	 * @return the dataPropertyIRI
	 */
	public String getDataPropertyIRI() {
		return dataPropertyIRI;
	}

	/**
	 * @param dataPropertyIRI the dataPropertyIRI to set
	 */
	public void setDataPropertyIRI(String dataPropertyIRI) {
		this.dataPropertyIRI = dataPropertyIRI;
	}

	/**
	 * @return the dataPropertyShortForm
	 */
	public String getDataPropertyShortForm() {
		return dataPropertyShortForm;
	}

	/**
	 * @param dataPropertyShortForm the dataPropertyShortForm to set
	 */
	public void setDataPropertyShortForm(String dataPropertyShortForm) {
		this.dataPropertyShortForm = dataPropertyShortForm;
	}

	/**
	 * @return the dataPropertyValue
	 */
	public String getDataPropertyValue() {
		return dataPropertyValue;
	}

	/**
	 * @param dataPropertyValue the dataPropertyValue to set
	 */
	public void setDataPropertyValue(String dataPropertyValue) {
		this.dataPropertyValue = dataPropertyValue;
	}

	/**
	 * @return the dataPropertyType
	 */
	public String getDataPropertyType() {
		return dataPropertyType;
	}

	/**
	 * @param dataPropertyType the dataPropertyType to set
	 */
	public void setDataPropertyType(String dataPropertyType) {
		this.dataPropertyType = dataPropertyType;
	}

	/**
	 * @return the dataPropertyTypeIRI
	 */
	public String getDataPropertyTypeIRI() {
		return dataPropertyTypeIRI;
	}

	/**
	 * @param dataPropertyTypeIRI the dataPropertyTypeIRI to set
	 */
	public void setDataPropertyTypeIRI(String dataPropertyTypeIRI) {
		this.dataPropertyTypeIRI = dataPropertyTypeIRI;
	}

	/**
	 * @return the dataPropertyLanguage
	 */
	public String getDataPropertyLanguage() {
		return dataPropertyLanguage;
	}

	/**
	 * @param dataPropertyLanguage the dataPropertyLanguage to set
	 */
	public void setDataPropertyLanguage(String dataPropertyLanguage) {
		this.dataPropertyLanguage = dataPropertyLanguage;
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
}

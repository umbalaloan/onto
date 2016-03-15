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
@Table(name = "annotation")
@NamedQuery(name = "Annotation.findAll", query = "SELECT a FROM Annotation a")
public class Annotation extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 2446104630663006689L;

	@Column(name = "iri")
	private String annotationPropertyIRI;

	@Column(name = "short_form")
	private String annotationPropertyShortForm;

	@Column(name = "value")
	private String annotationValue;

	@Column(name = "data_type")
	private String annotationDataType;

	@Column(name = "data_type_iri")
	private String annotationDataTypeIRI;

	@Column(name = "language")
	private String annotationLanguage;

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
	 * @return the annotationPropertyIRI
	 */
	public String getAnnotationPropertyIRI() {
		return annotationPropertyIRI;
	}

	/**
	 * @param annotationPropertyIRI the annotationPropertyIRI to set
	 */
	public void setAnnotationPropertyIRI(String annotationPropertyIRI) {
		this.annotationPropertyIRI = annotationPropertyIRI;
	}

	/**
	 * @return the annotationPropertyShortForm
	 */
	public String getAnnotationPropertyShortForm() {
		return annotationPropertyShortForm;
	}

	/**
	 * @param annotationPropertyShortForm the annotationPropertyShortForm to set
	 */
	public void setAnnotationPropertyShortForm(String annotationPropertyShortForm) {
		this.annotationPropertyShortForm = annotationPropertyShortForm;
	}

	/**
	 * @return the annotationValue
	 */
	public String getAnnotationValue() {
		return annotationValue;
	}

	/**
	 * @param annotationValue the annotationValue to set
	 */
	public void setAnnotationValue(String annotationValue) {
		this.annotationValue = annotationValue;
	}

	/**
	 * @return the annotationLanguage
	 */
	public String getAnnotationLanguage() {
		return annotationLanguage;
	}

	/**
	 * @param annotationLanguage the annotationLanguage to set
	 */
	public void setAnnotationLanguage(String annotationLanguage) {
		this.annotationLanguage = annotationLanguage;
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
	 * @return the annotationDataType
	 */
	public String getAnnotationDataType() {
		return annotationDataType;
	}

	/**
	 * @param annotationDataType the annotationDataType to set
	 */
	public void setAnnotationDataType(String annotationDataType) {
		this.annotationDataType = annotationDataType;
	}

	/**
	 * @return the annotationDataTypeIRI
	 */
	public String getAnnotationDataTypeIRI() {
		return annotationDataTypeIRI;
	}

	/**
	 * @param annotationDataTypeIRI the annotationDataTypeIRI to set
	 */
	public void setAnnotationDataTypeIRI(String annotationDataTypeIRI) {
		this.annotationDataTypeIRI = annotationDataTypeIRI;
	}
}

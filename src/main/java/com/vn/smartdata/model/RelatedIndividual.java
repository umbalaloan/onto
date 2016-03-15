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
@Table(name = "related_individual")
@NamedQuery(name = "RelatedIndividual.findAll", query = "SELECT ri FROM RelatedIndividual ri")
public class RelatedIndividual extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 7323025194873689139L;
	
	@Column(name = "iri")
	private String individualIRI;
	
	@Column(name = "short_form")
	private String individualShortForm;
	
	/** The is same or different (false(0): same / true(1): different). */
	@Column(name = "is_same_or_different")
	private boolean isSameOrDifferent;
	
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
	 * @return the isSameOrDifferent
	 */
	public boolean isSameOrDifferent() {
		return isSameOrDifferent;
	}

	/**
	 * @param isSameOrDifferent the isSameOrDifferent to set
	 */
	public void setSameOrDifferent(boolean isSameOrDifferent) {
		this.isSameOrDifferent = isSameOrDifferent;
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

package com.vn.smartdata.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * The persistent class for the role database table.
 *
 */
@Entity
@Table(name = "ontology")
@NamedQuery(name = "Ontology.findAll", query = "SELECT o FROM Ontology o")
public class Ontology extends BaseEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The description. */
	@Column(name = "description")
	private String description;

	/** The ontology name. */
	@Column(name = "name")
	private String ontologyName;

	/** The created date. */
	@Column(name = "createdDate")
	private Date createdDate;

	/** The modified date. */
	@Column(name = "modifiedDate")
	private Date modifiedDate;

	/** The is indexed. */
	@Column(name = "isIndexed")
	private boolean isIndexed;

	/** The is active. */
	@Column(name = "isActive")
	private boolean isActive;

	/** The creator. */
	@ManyToOne
	@JoinColumn(name = "creator")
	private User creator;

	//@OneToOne(fetch=FetchType.LAZY, mappedBy="ontology", optional=false)
	@Column(name = "ontologyContent")
	private long ontologyContent;

	/** The uploaded file. */
	@Transient
	private byte[] uploadedFile;

	/**
	 * Instantiates a new ontology.
	 */
	public Ontology() {
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Gets the ontology name.
	 *
	 * @return the ontology name
	 */
	public String getOntologyName() {
		return this.ontologyName;
	}

	/**
	 * Sets the ontology name.
	 *
	 * @param ontologyName the new ontology name
	 */
	public void setOntologyName(final String ontologyName) {
		this.ontologyName = ontologyName;
	}

	/**
	 * Gets the created date.
	 *
	 * @return the created date
	 */
	public Date getCreatedDate() {
		return this.createdDate;
	}

	/**
	 * Sets the created date.
	 *
	 * @param createdDate the new created date
	 */
	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * Gets the modified date.
	 *
	 * @return the modified date
	 */
	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	/**
	 * Sets the modified date.
	 *
	 * @param modifiedDate the new modified date
	 */
	public void setModifiedDate(final Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * Gets the creator.
	 *
	 * @return the creator
	 */
	public User getCreator() {
		return this.creator;
	}

	/**
	 * Sets the creator.
	 *
	 * @param creator the new creator
	 */
	public void setCreator(final User creator) {
		this.creator = creator;
	}

	/**
	 * Gets the uploaded file.
	 *
	 * @return the uploadedFile
	 */
	public byte[] getUploadedFile() {
		return this.uploadedFile;
	}

	/**
	 * Sets the uploaded file.
	 *
	 * @param uploadedFile            the uploadedFile to set
	 */
	public void setUploadedFile(final byte[] uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	/**
	 * Gets the checks if is indexed.
	 *
	 * @return the isIndexed
	 */
	public boolean getIsIndexed() {
		return this.isIndexed;
	}

	/**
	 * Sets the checks if is indexed.
	 *
	 * @param isIndexed the isIndexed to set
	 */
	public void setIsIndexed(final boolean isIndexed) {
		this.isIndexed = isIndexed;
	}

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return this.isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(final boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the ontologyContent
	 */
	public long getOntologyContent() {
		return this.ontologyContent;
	}

	/**
	 * @param ontologyContent the ontologyContent to set
	 */
	public void setOntologyContent(final long ontologyContent) {
		this.ontologyContent = ontologyContent;
	}
}
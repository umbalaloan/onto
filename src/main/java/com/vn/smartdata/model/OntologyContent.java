package com.vn.smartdata.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the role database table.
 *
 */
@Entity
@Table(name = "ontology_content")
@NamedQuery(name = "OntologyContent.findAll", query = "SELECT oc FROM OntologyContent oc")
public class OntologyContent extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -7055788631901281009L;

	@Column(name = "content")
	private String content;

	//@OneToOne
	//@JoinColumn(name="ontology")
	@Column(name = "ontology")
	private long ontology;

	/**
	 * @return the content
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(final String content) {
		this.content = content;
	}

	/**
	 * @return the ontology
	 */
	public long getOntology() {
		return this.ontology;
	}

	/**
	 * @param ontology the ontology to set
	 */
	public void setOntology(final long ontology) {
		this.ontology = ontology;
	}
}
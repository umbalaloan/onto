package com.vn.smartdata.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The Class Individual.
 */
@Entity
@Table(name = "individual")
@NamedQuery(name = "Individual.findAll", query = "SELECT i FROM Individual i")
public class Individual extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 2171586834364793163L;

	/** The individual iri. */
	@Column(name = "iri")
	private String individualIRI;

	/** The individual short form. */
	@Column(name = "short_form")
	private String individualShortForm;

	@Column(name = "created_date")
	private Date createdDate;

	@ManyToOne
	@JoinColumn(name = "editor")
	private User editor;

	@ManyToOne
	@JoinColumn(name = "ontology")
	private Ontology ontology;

	@OneToMany(mappedBy="individual", fetch = FetchType.LAZY, orphanRemoval=true, cascade=CascadeType.REMOVE)
	private Set<Annotation> annotations;

	@OneToMany(mappedBy="individual", fetch = FetchType.LAZY, orphanRemoval=true, cascade=CascadeType.REMOVE)
	private Set<Type> types;

	@OneToMany(mappedBy="individual", fetch = FetchType.LAZY, orphanRemoval=true, cascade=CascadeType.REMOVE)
	private Set<RelatedIndividual> relatedIndividuals;

	@OneToMany(mappedBy="individual", fetch = FetchType.LAZY, orphanRemoval=true, cascade=CascadeType.REMOVE)
	private Set<ObjectPropertyAssertion> objectPropertyAssertions;

	@OneToMany(mappedBy="individual", fetch = FetchType.LAZY, orphanRemoval=true, cascade=CascadeType.REMOVE)
	private Set<DataPropertyAssertion> dataPropertyAssertions;

	@Transient
	private Set<RelatedIndividual> sameIndividuals = null;

	@Transient
	private Set<RelatedIndividual> differentIndividuals = null;

	@Transient
	private Set<ObjectPropertyAssertion> opas = null;

	@Transient
	private Set<ObjectPropertyAssertion> nopas = null;

	@Transient
	private Set<DataPropertyAssertion> dpas = null;

	@Transient
	private Set<DataPropertyAssertion> ndpas = null;
	
	@Transient
	private boolean isAlreadyGoThroughRelatedIndividualList = false;
	
	@Transient
	private boolean isAlreadyGoThroughOPAList = false;
	
	@Transient
	private boolean isAlreadyGoThroughDPAList = false;

	/**
	 * @return the individualIRI
	 */
	public String getIndividualIRI() {
		return this.individualIRI;
	}

	/**
	 * @param individualIRI the individualIRI to set
	 */
	public void setIndividualIRI(final String individualIRI) {
		this.individualIRI = individualIRI;
	}

	/**
	 * @return the individualShortForm
	 */
	public String getIndividualShortForm() {
		return this.individualShortForm;
	}

	/**
	 * @param individualShortForm the individualShortForm to set
	 */
	public void setIndividualShortForm(final String individualShortForm) {
		this.individualShortForm = individualShortForm;
	}

	/**
	 * @return the editor
	 */
	public User getEditor() {
		return this.editor;
	}

	/**
	 * @param editor the editor to set
	 */
	public void setEditor(final User editor) {
		this.editor = editor;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the annotations
	 */
	public Set<Annotation> getAnnotations() {
		return this.annotations;
	}

	/**
	 * @param annotations the annotations to set
	 */
	public void setAnnotations(final Set<Annotation> annotations) {
		this.annotations = annotations;
	}

	/**
	 * @return the types
	 */
	public Set<Type> getTypes() {
		return this.types;
	}

	/**
	 * @param types the types to set
	 */
	public void setTypes(final Set<Type> types) {
		this.types = types;
	}

	/**
	 * @return the relatedIndividuals
	 */
	public Set<RelatedIndividual> getRelatedIndividuals() {
		return this.relatedIndividuals;
	}

	/**
	 * @param relatedIndividuals the relatedIndividuals to set
	 */
	public void setRelatedIndividuals(final Set<RelatedIndividual> relatedIndividuals) {
		this.relatedIndividuals = relatedIndividuals;
	}

	/**
	 * @return the objectPropertyAssertions
	 */
	public Set<ObjectPropertyAssertion> getObjectPropertyAssertions() {
		return this.objectPropertyAssertions;
	}

	/**
	 * @param objectPropertyAssertions the objectPropertyAssertions to set
	 */
	public void setObjectPropertyAssertions(
			final Set<ObjectPropertyAssertion> objectPropertyAssertions) {
		this.objectPropertyAssertions = objectPropertyAssertions;
	}

	/**
	 * @return the dataPropertyAssertions
	 */
	public Set<DataPropertyAssertion> getDataPropertyAssertions() {
		return this.dataPropertyAssertions;
	}

	/**
	 * @param dataPropertyAssertions the dataPropertyAssertions to set
	 */
	public void setDataPropertyAssertions(
			final Set<DataPropertyAssertion> dataPropertyAssertions) {
		this.dataPropertyAssertions = dataPropertyAssertions;
	}

	/**
	 * @return the ontology
	 */
	public Ontology getOntology() {
		return this.ontology;
	}

	/**
	 * @param ontology the ontology to set
	 */
	public void setOntology(final Ontology ontology) {
		this.ontology = ontology;
	}

	public Set<RelatedIndividual> getSameIndividuals() {
		if(!isAlreadyGoThroughRelatedIndividualList){
			this.sameIndividuals = new HashSet<>();
			this.differentIndividuals = new HashSet<>();

			try {
				if(null != this.relatedIndividuals && !this.relatedIndividuals.isEmpty()){
					for(final RelatedIndividual ri: this.relatedIndividuals){
						if(ri.isSameOrDifferent()){
							this.differentIndividuals.add(ri);
						} else {
							this.sameIndividuals.add(ri);
						}
					}
					
					isAlreadyGoThroughRelatedIndividualList = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return this.sameIndividuals;
	}

	public void setSameIndividuals(final Set<RelatedIndividual> sameIndividuals) {
		this.sameIndividuals = sameIndividuals;
	}

	public Set<RelatedIndividual> getDifferentIndividuals() {
		if(!isAlreadyGoThroughRelatedIndividualList){
			this.differentIndividuals = new HashSet<>();
			this.sameIndividuals = new HashSet<>();

			try {
				if(null != this.relatedIndividuals && !this.relatedIndividuals.isEmpty()){
					for(final RelatedIndividual ri: this.relatedIndividuals){
						if(!ri.isSameOrDifferent()){
							this.sameIndividuals.add(ri);
						} else {
							this.differentIndividuals.add(ri);
						}
					}
					
					isAlreadyGoThroughRelatedIndividualList = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return this.differentIndividuals;
	}

	public void setDifferentIndividuals(final Set<RelatedIndividual> differentIndividuals) {
		this.differentIndividuals = differentIndividuals;
	}

	public Set<ObjectPropertyAssertion> getOpas() {
		if(!isAlreadyGoThroughOPAList){
			this.opas = new HashSet<>();
			this.nopas = new HashSet<>();
			
			try {
				if(null != this.objectPropertyAssertions && !this.objectPropertyAssertions.isEmpty()){
					for(final ObjectPropertyAssertion opa: this.objectPropertyAssertions){
						if(opa.isNegative()){
							this.nopas.add(opa);
						} else {
							this.opas.add(opa);
						}
					}
					
					isAlreadyGoThroughOPAList = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return this.opas;
	}

	public void setOpas(final Set<ObjectPropertyAssertion> opas) {
		this.opas = opas;
	}

	public Set<ObjectPropertyAssertion> getNopas() {
		if(!isAlreadyGoThroughOPAList){
			this.nopas = new HashSet<>();
			this.opas = new HashSet<>();

			try {
				if(null != this.objectPropertyAssertions && !this.objectPropertyAssertions.isEmpty()){
					for(final ObjectPropertyAssertion opa: this.objectPropertyAssertions){
						if(!opa.isNegative()){
							this.opas.add(opa);
						} else {
							this.nopas.add(opa);
						}
					}
					
					isAlreadyGoThroughOPAList = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return this.nopas;
	}

	public void setNopas(final Set<ObjectPropertyAssertion> nopas) {
		this.nopas = nopas;
	}

	public Set<DataPropertyAssertion> getDpas() {
		if(!isAlreadyGoThroughDPAList){
			this.dpas = new HashSet<>();
			this.ndpas = new HashSet<>();

			try {
				if(null != this.dataPropertyAssertions && !this.dataPropertyAssertions.isEmpty()){
					for(final DataPropertyAssertion dpa: this.dataPropertyAssertions){
						if(dpa.isNegative()){
							this.ndpas.add(dpa);
						} else {
							this.dpas.add(dpa);
						}
					}
					
					isAlreadyGoThroughDPAList = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return this.dpas;
	}

	public void setDpas(final Set<DataPropertyAssertion> dpas) {
		this.dpas = dpas;
	}

	public Set<DataPropertyAssertion> getNdpas() {
		if(!isAlreadyGoThroughDPAList){
			this.ndpas = new HashSet<>();
			this.dpas = new HashSet<>();

			try {
				if(null != this.dataPropertyAssertions && !this.dataPropertyAssertions.isEmpty()){
					for(final DataPropertyAssertion dpa: this.dataPropertyAssertions){
						if(!dpa.isNegative()){
							this.dpas.add(dpa);
						} else {
							this.ndpas.add(dpa);
						}
					}
					
					isAlreadyGoThroughDPAList = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return this.ndpas;
	}

	public void setNdpas(final Set<DataPropertyAssertion> ndpas) {
		this.ndpas = ndpas;
	}
}

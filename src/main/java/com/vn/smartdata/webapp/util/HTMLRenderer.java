package com.vn.smartdata.webapp.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLArgument;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLDataRangeAtom;
import org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLSameIndividualAtom;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.EscapeUtils;
import org.semanticweb.owlapi.util.IRIShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;

import com.vn.smartdata.constants.StringPool;


/**
 * This class is used to display an OWL Object as a string that is a html content.
 *
 * @author Quang
 */
public class HTMLRenderer implements OWLObjectVisitor, OWLObjectRenderer {
	
	/** The sb. */
	private StringBuilder sb;
	
	/** The short form provider. */
	private ShortFormProvider shortFormProvider;
	
	/** The iri short form provider. */
	private IRIShortFormProvider iriShortFormProvider;

	/**
	 *  default constructor.
	 */
	public HTMLRenderer() {
		this.sb = new StringBuilder();
		this.resetShortFormProvider();
	}

	/** reset the renderer. */
	public void reset() {
		this.sb = new StringBuilder();
	}

	/**
	 * Checks if is using default short form provider.
	 *
	 * @return true if default is used
	 */
	public boolean isUsingDefaultShortFormProvider() {
		return this.shortFormProvider instanceof HTMLPrefixManager;
	}

	/**
	 * Reset short form provider.
	 */
	public final void resetShortFormProvider() {
		final HTMLPrefixManager htmlPrefixManager = new HTMLPrefixManager();
		this.shortFormProvider = htmlPrefixManager;
		this.iriShortFormProvider = htmlPrefixManager;
	}
	
	/**
	 * Gets the short form in html.
	 *
	 * @param iri the iri
	 * @return the short form in html
	 */
	public String getShortFormInHTML(IRI iri) {
		return iriShortFormProvider.getShortForm(iri);
	}

	/**
	 * Resets the short form provider and adds prefix name to prefix mappings
	 * based on the specified ontology's format (if it is a prefix format) and
	 * possibly the ontologies in the imports closure.
	 *
	 * @param ontology
	 *        The ontology whose format will be used to obtain prefix mappings
	 * @param manager
	 *        A manager which can be used to obtain the format of the specified
	 *        ontology (and possibly ontologies in its imports closure)
	 * @param processImportedOntologies
	 *        Specifies whether or not the prefix mapping should be obtained
	 *        from imported ontologies.
	 */
	public void setPrefixesFromOntologyFormat(@Nonnull final OWLOntology ontology, @Nonnull final OWLOntologyManager manager,
			final boolean processImportedOntologies) {
		this.resetShortFormProvider();
		if (processImportedOntologies) {
			for (final OWLOntology importedOntology : manager.getImportsClosure(ontology)) {
				if (!importedOntology.equals(ontology)) {
					this.copyPrefixes(manager.getOntologyFormat(importedOntology));
				}
			}
		}
		final OWLDocumentFormat format = manager.getOntologyFormat(ontology);
		this.copyPrefixes(format);
	}

	/**
	 * Copy prefixes.
	 *
	 * @param ontologyFormat the ontology format
	 */
	private void copyPrefixes(final OWLDocumentFormat ontologyFormat) {
		if (!(ontologyFormat instanceof PrefixDocumentFormat)) {
			return;
		}
		final PrefixDocumentFormat prefixFormat = (PrefixDocumentFormat) ontologyFormat;
		for (final Map.Entry<String, String> e : prefixFormat.getPrefixName2PrefixMap().entrySet()) {
			this.setPrefix(e.getKey(), e.getValue());
		}
	}

	/**
	 * Sets a prefix name for a given prefix. Note that prefix names MUST end
	 * with a colon.
	 *
	 * @param prefixName
	 *        The prefix name (ending with a colon)
	 * @param prefix
	 *        The prefix that the prefix name maps to
	 */
	public void setPrefix(@Nonnull final String prefixName, @Nonnull final String prefix) {
		if (!this.isUsingDefaultShortFormProvider()) {
			this.resetShortFormProvider();
		}
		((DefaultPrefixManager) this.shortFormProvider).setPrefix(prefixName, prefix);
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLObjectRenderer#setShortFormProvider(org.semanticweb.owlapi.util.ShortFormProvider)
	 */
	@Override
	public void setShortFormProvider(final ShortFormProvider shortFormProvider) {
		this.shortFormProvider = shortFormProvider;
	}

	/**
	 * Gets the short form.
	 *
	 * @param iri        the iri to shorten
	 * @return the short form
	 */
	public String getShortForm(@Nonnull final IRI iri) {
		return this.iriShortFormProvider.getShortForm(iri);
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLObjectIntersectionOf)
	 */
	@Override
	public void visit(final OWLObjectIntersectionOf ce) {
		this.sb.append("<span class=\"owl-object-restriction owl-object-intersection-of\">");
		this.sb.append(StringPool.OPEN_PARENTHESIS_HTML);
		this.renderIntersectionOfOperands(ce.getOperands());
		this.sb.append(StringPool.CLOSE_PARENTHESIS_HTML);
		this.sb.append("</span>");

		//        sb.append("ObjectIntersectionOf(");
		//        render(ce.getOperands());
		//        sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLObjectUnionOf)
	 */
	@Override
	public void visit(final OWLObjectUnionOf ce) {
		this.sb.append("<span class=\"owl-object-restriction owl-object-union-of\">");
		this.renderUnionOfOperands(ce.getOperands());
		this.sb.append("</span>");

		//        sb.append("ObjectUnionOf(");
		//        render(ce.getOperands());
		//        sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLObjectComplementOf)
	 */
	@Override
	public void visit(final OWLObjectComplementOf ce) {
		this.sb.append("<span class=\"owl-object-restriction owl-object-complement-of\">");
		ce.getOperand().accept(this);
		this.sb.append("</span>");

		//        sb.append("ObjectComplementOf(");
		//        ce.getOperand().accept(this);
		//        sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom)
	 */
	@Override
	public void visit(final OWLObjectSomeValuesFrom ce) {
		final IRI iri = ce.getProperty().asOWLObjectProperty().getIRI();

		this.sb.append("<span class=\"owl-object-restriction owl-object-some-values-from\">");
		this.sb.append("<span class=\"owl-property owl-object-property-expression owl-object-some-values-from-property\" title=\"");
		this.sb.append(iri.toString());
		this.sb.append("\">");
		this.sb.append(iri.getShortForm());
		this.sb.append("</span>");
		this.insertHTMLSpace();
		this.sb.append("<span class=\"owl-keyword owl-object-keyword owl-object-some-values-from-keyword\">some</span>");
		this.insertHTMLSpace();
		this.sb.append("<span class=\"owl-filler owl-object-some-values-from-filler\">");
		ce.getFiller().accept(this);
		this.sb.append("</span>");
		this.sb.append("</span>");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLObjectAllValuesFrom)
	 */
	@Override
	public void visit(final OWLObjectAllValuesFrom ce) {
		final IRI iri = ce.getProperty().asOWLObjectProperty().getIRI();

		this.sb.append("<span class=\"owl-object-restriction owl-object-all-values-from\">");
		this.sb.append("<span class=\"owl-property owl-object-property-expression owl-object-all-values-from-property\" title=\"");
		this.sb.append(iri.toString());
		this.sb.append("\">");
		this.sb.append(iri.getShortForm());
		this.sb.append("</span>");
		this.insertHTMLSpace();
		this.sb.append("<span class=\"owl-keyword owl-object-keyword owl-object-all-values-from-keyword\">only</span>");
		this.insertHTMLSpace();
		this.sb.append("<span class=\"owl-filler owl-object-all-values-from-filler\">");
		ce.getFiller().accept(this);
		this.sb.append("</span>");
		this.sb.append("</span>");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLObjectHasValue)
	 */
	@Override
	public void visit(final OWLObjectHasValue ce) {
		final IRI iri = ce.getProperty().asOWLObjectProperty().getIRI();

		this.sb.append("<span class=\"owl-object-restriction owl-object-has-value\">");
		this.sb.append("<span class=\"owl-property owl-object-property-expression owl-object-has-value\" title=\"");
		this.sb.append(iri.toString());
		this.sb.append("\">");
		this.sb.append(iri.getShortForm());
		this.sb.append("</span>");
		this.insertHTMLSpace();
		this.sb.append("<span class=\"owl-keyword owl-object-keyword owl-object-has-value-keyword\">value</span>");
		this.insertHTMLSpace();
		this.sb.append("<span class=\"owl-filler owl-object-has-value-filler\">");
		ce.getFiller().accept(this);
		this.sb.append("</span>");
		this.sb.append("</span>");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLObjectMinCardinality)
	 */
	@Override
	public void visit(final OWLObjectMinCardinality ce) {
		final IRI iri = ce.getProperty().asOWLObjectProperty().getIRI();

		this.sb.append("<span class=\"owl-object-restriction owl-object-min-cardinality\">");
		this.sb.append("<span class=\"owl-property owl-object-property-expression owl-object-min-cardinality-property\" title=\"");
		this.sb.append(iri.toString());
		this.sb.append("\">");
		this.sb.append(iri.getShortForm());
		this.sb.append("</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"owl-keyword owl-object-keyword owl-object-min-cardinality-keyword\">min</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"cardinality\">");
		this.sb.append(ce.getCardinality());
		this.sb.append("</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"owl-filler owl-object-min-cardinality-filler\">");
		ce.getFiller().accept(this);
		this.sb.append("</span>");
		this.sb.append("</span>");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLObjectExactCardinality)
	 */
	@Override
	public void visit(final OWLObjectExactCardinality ce) {
		final IRI iri = ce.getProperty().asOWLObjectProperty().getIRI();

		this.sb.append("<span class=\"owl-object-restriction owl-object-exact-cardinality\">");
		this.sb.append("<span class=\"owl-property owl-object-property-expression owl-object-exact-cardinality-property\" title=\"");
		this.sb.append(iri.toString());
		this.sb.append("\">");
		this.sb.append(iri.getShortForm());
		this.sb.append("</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"owl-keyword owl-object-keyword owl-object-exact-cardinality-keyword\">exactly</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"cardinality\">");
		this.sb.append(ce.getCardinality());
		this.sb.append("</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"owl-filler owl-object-exact-cardinality-filler\">");
		ce.getFiller().accept(this);
		this.sb.append("</span>");
		this.sb.append("</span>");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLObjectMaxCardinality)
	 */
	@Override
	public void visit(final OWLObjectMaxCardinality ce) {
		final IRI iri = ce.getProperty().asOWLObjectProperty().getIRI();

		this.sb.append("<span class=\"owl-object-restriction owl-object-max-cardinality\">");
		this.sb.append("<span class=\"owl-property owl-object-property-expression owl-object-max-cardinality-property\" title=\"");
		this.sb.append(iri.toString());
		this.sb.append("\">");
		this.sb.append(iri.getShortForm());
		this.sb.append("</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"owl-keyword owl-object-keyword owl-object-max-cardinality-keyword\">max</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"cardinality\">");
		this.sb.append(ce.getCardinality());
		this.sb.append("</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"owl-filler owl-object-max-cardinality-filler\">");
		ce.getFiller().accept(this);
		this.sb.append("</span>");
		this.sb.append("</span>");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLObjectHasSelf)
	 */
	@Override
	public void visit(final OWLObjectHasSelf ce) {
		final IRI iri = ce.getProperty().asOWLObjectProperty().getIRI();

		this.sb.append("<span class=\"owl-object-restriction owl-object-has-self\">");
		this.sb.append("<span class=\"owl-property owl-object-property-expression owl-object-has-self-property\" title=\"");
		this.sb.append(iri.toString());
		this.sb.append("\">");
		this.sb.append(iri.getShortForm());
		this.sb.append("</span>");
		this.sb.append("</span>");

		//        sb.append("ObjectHasSelf(");
		//        ce.getProperty().accept(this);
		//        sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLObjectOneOf)
	 */
	@Override
	public void visit(final OWLObjectOneOf ce) {
		this.sb.append("<span class=\"owl-object-restriction owl-object-one-of\">");
		final Set<? extends OWLIndividual> individualSet = ce.getIndividuals();

		final boolean hasMultipleElements = individualSet.size() > 1;

		if(hasMultipleElements){
			this.sb.append(StringPool.OPEN_PARENTHESIS);
			this.sb.append(StringPool.OPEN_CURLY_BRACE);

			this.renderIndividuals(ce.getIndividuals());

			this.sb.append(StringPool.CLOSE_CURLY_BRACE);
			this.sb.append(StringPool.CLOSE_PARENTHESIS);
			this.sb.append("</span>");
		} else {
			this.renderIndividuals(ce.getIndividuals());
			this.sb.append("</span>");
		}
		//        sb.append("ObjectOneOf(");
		//        render(ce.getIndividuals());
		//        sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLDataSomeValuesFrom)
	 */
	@Override
	public void visit(final OWLDataSomeValuesFrom ce) {
		final IRI iri = ce.getProperty().asOWLDataProperty().getIRI();

		this.sb.append("<span class=\"owl-data-restriction owl-data-some-values-from\">");
		this.sb.append("<span class=\"owl-property owl-data-property-expression owl-data-some-values-from-property\" title=\"");
		this.sb.append(iri.toString());
		this.sb.append("\">");
		this.sb.append(iri.getShortForm());
		this.sb.append("</span>");
		this.insertHTMLSpace();
		this.sb.append("<span class=\"owl-keyword owl-data-keyword owl-data-some-values-from-keyword\">some</span>");
		this.insertHTMLSpace();
		this.sb.append("<span class=\"owl-filler owl-data-some-values-from-filler\">");
		ce.getFiller().accept(this);
		this.sb.append("</span>");
		this.sb.append("</span>");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLDataAllValuesFrom)
	 */
	@Override
	public void visit(final OWLDataAllValuesFrom ce) {
		final IRI iri = ce.getProperty().asOWLDataProperty().getIRI();

		this.sb.append("<span class=\"owl-object-restriction owl-data-all-values-from\">");
		this.sb.append("<span class=\"owl-property owl-data-property-expression owl-data-all-values-from-property\" title=\"");
		this.sb.append(iri.toString());
		this.sb.append("\">");
		this.sb.append(iri.getShortForm());
		this.sb.append("</span>");
		this.insertHTMLSpace();
		this.sb.append("<span class=\"owl-keyword owl-data-keyword owl-data-all-values-from-keyword\">only</span>");
		this.insertHTMLSpace();
		this.sb.append("<span class=\"owl-filler owl-data-all-values-from-filler\">");
		ce.getFiller().accept(this);
		this.sb.append("</span>");
		this.sb.append("</span>");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLDataHasValue)
	 */
	@Override
	public void visit(final OWLDataHasValue ce) {
		final IRI iri = ce.getProperty().asOWLDataProperty().getIRI();

		this.sb.append("<span class=\"owl-data-restriction owl-data-has-value\">");
		this.sb.append("<span class=\"owl-property owl-data-property-expression owl-data-has-value\" title=\"");
		this.sb.append(iri.toString());
		this.sb.append("\">");
		this.sb.append(iri.getShortForm());
		this.sb.append("</span>");
		this.insertHTMLSpace();
		this.sb.append("<span class=\"owl-keyword owl-data-keyword owl-data-has-value-keyword\">value</span>");
		this.insertHTMLSpace();
		this.sb.append("<span class=\"owl-filler owl-data-has-value-filler\">");
		ce.getFiller().accept(this);
		this.sb.append("</span>");
		this.sb.append("</span>");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLDataMinCardinality)
	 */
	@Override
	public void visit(final OWLDataMinCardinality ce) {
		final IRI iri = ce.getProperty().asOWLDataProperty().getIRI();

		this.sb.append("<span class=\"owl-data-restriction owl-data-min-cardinality\">");
		this.sb.append("<span class=\"owl-property owl-data-property-expression owl-data-min-cardinality-property\" title=\"");
		this.sb.append(iri.toString());
		this.sb.append("\">");
		this.sb.append(iri.getShortForm());
		this.sb.append("</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"owl-keyword owl-data-keyword owl-data-min-cardinality-keyword\">min</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"cardinality\">");
		this.sb.append(ce.getCardinality());
		this.sb.append("</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"owl-filler owl-data-min-cardinality-filler\">");
		ce.getFiller().accept(this);
		this.sb.append("</span>");
		this.sb.append("</span>");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLDataExactCardinality)
	 */
	@Override
	public void visit(final OWLDataExactCardinality ce) {
		final IRI iri = ce.getProperty().asOWLDataProperty().getIRI();

		this.sb.append("<span class=\"owl-data-restriction owl-data-exact-cardinality\">");
		this.sb.append("<span class=\"owl-property owl-data-property-expression owl-data-exact-cardinality-property\" title=\"");
		this.sb.append(iri.toString());
		this.sb.append("\">");
		this.sb.append(iri.getShortForm());
		this.sb.append("</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"owl-keyword owl-data-keyword owl-data-exact-cardinality-keyword\">exactly</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"cardinality\">");
		this.sb.append(ce.getCardinality());
		this.sb.append("</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"owl-filler owl-data-exact-cardinality-filler\">");
		ce.getFiller().accept(this);
		this.sb.append("</span>");
		this.sb.append("</span>");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLDataMaxCardinality)
	 */
	@Override
	public void visit(final OWLDataMaxCardinality ce) {
		final IRI iri = ce.getProperty().asOWLDataProperty().getIRI();

		this.sb.append("<span class=\"owl-data-restriction owl-data-max-cardinality\">");
		this.sb.append("<span class=\"owl-property owl-data-property-expression owl-data-max-cardinality-property\" title=\"");
		this.sb.append(iri.toString());
		this.sb.append("\">");
		this.sb.append(iri.getShortForm());
		this.sb.append("</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"owl-keyword owl-data-keyword owl-data-max-cardinality-keyword\">max</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"cardinality\">");
		this.sb.append(ce.getCardinality());
		this.sb.append("</span>");
		this.insertHTMLSpace();

		this.sb.append("<span class=\"owl-filler owl-data-max-cardinality-filler\">");
		ce.getFiller().accept(this);
		this.sb.append("</span>");
		this.sb.append("</span>");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLDataRangeVisitor#visit(org.semanticweb.owlapi.model.OWLDatatype)
	 */
	@Override
	public void visit(final OWLDatatype node) {
		this.sb.append(this.shortFormProvider.getShortForm(node));
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLDataRangeVisitor#visit(org.semanticweb.owlapi.model.OWLDataComplementOf)
	 */
	@Override
	public void visit(final OWLDataComplementOf node) {
		this.sb.append("<span class=\"owl-data-restriction owl-data-complement-of\">");
		node.getDataRange().accept(this);
		this.sb.append("</span>");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLDataRangeVisitor#visit(org.semanticweb.owlapi.model.OWLDataOneOf)
	 */
	@Override
	public void visit(final OWLDataOneOf node) {
		this.sb.append("<span class=\"owl-data-restriction owl-data-one-of\">");
		this.render(node.getValues());
		this.sb.append("</span>");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLDataRangeVisitor#visit(org.semanticweb.owlapi.model.OWLDataIntersectionOf)
	 */
	@Override
	public void visit(final OWLDataIntersectionOf node) {
		this.sb.append("<span class=\"owl-data-restriction owl-data-intersection-of\">");

		for (final Iterator<? extends OWLDataRange> it = HTMLRenderer.toSortedSet(node.getOperands()).iterator(); it
				.hasNext();) {
			final OWLDataRange rng = it.next();

			this.sb.append("<span class=\"owl-data-range\">");
			rng.accept(this);
			this.sb.append("</span>");

			it.next().accept(this);
			if (it.hasNext()) {
				this.insertHTMLCommaAndSpace();
			}
		}

		this.sb.append("<span class=\"owl-data-restriction owl-data-intersection-of\">");
		for (final OWLDataRange rng : node.getOperands()) {
			this.sb.append("<span class=\"owl-data-range\">");
			rng.accept(this);
			this.sb.append("</span>");
		}
		this.sb.append("</span>");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLDataRangeVisitor#visit(org.semanticweb.owlapi.model.OWLDataUnionOf)
	 */
	@Override
	public void visit(final OWLDataUnionOf node) {

		this.sb.append("<span class=\"owl-data-restriction owl-data-union-of\">");

		for (final Iterator<? extends OWLDataRange> it = HTMLRenderer.toSortedSet(node.getOperands()).iterator(); it
				.hasNext();) {

			this.sb.append("<span class=\"owl-data-range\">");
			it.next().accept(this);
			this.sb.append("</span>");

			if (it.hasNext()) {
				this.insertHTMLCommaAndSpace();
			}
		}

		//        for (OWLDataRange rng : node.getOperands()) {
		//        	sb.append("<span class=\"owl-data-range\">");
		//            rng.accept(this);
		//            sb.append("</span>");
		//        }

		this.sb.append("</span>");
	}

	//	public void renderEquivalentObjectProperties(final Set<? extends OWLObjectPropertyExpression> objectPropertiesExpression) {
	//		for (final Iterator<? extends OWLObject> it = HTMLRenderer.toSortedSet(objectPropertiesExpression).iterator(); it
	//				.hasNext();) {
	//			it.next().accept(this);
	//			if (it.hasNext()) {
	//				this.insertHTMLCommaAndSpace();
	//			}
	//		}
	//	}

	/**
	 * Render.
	 *
	 * @param objects the objects
	 */
	public void render(final Set<? extends OWLObject> objects) {
		for (final Iterator<? extends OWLObject> it = HTMLRenderer.toSortedSet(objects).iterator(); it
				.hasNext();) {
			it.next().accept(this);
			if (it.hasNext()) {
				this.insertHTMLCommaAndSpace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLDataVisitor#visit(org.semanticweb.owlapi.model.OWLLiteral)
	 */
	@Override
	public void visit(final OWLLiteral node) {
		this.sb.append("<span class=\"owl-literal\">");

		final String literal = EscapeUtils.escapeString(node.getLiteral());
		if (node.isRDFPlainLiteral()) {
			// We can use a syntactic shortcut
			this.sb.append(literal);
			if (node.hasLang()) {
				this.sb.append('@').append(node.getLang());
			}
		} else {
			this.sb.append('"').append(literal).append("\"^^");
			node.getDatatype().accept(this);
		}

		this.sb.append("</span>");
	}

	//    public void renderDataOfSet(Set<? extends OWLLiteral> objects) {
	//        for (Iterator<? extends OWLLiteral> it = toSortedSet(objects).iterator(); it
	//                .hasNext();) {
	//            String literal = it.next().getLiteral();
	//
	//            sb.append("<span class=\"owl-literal\" title=\"");
	//            sb.append(literal);
	//            sb.append("\">");
	//            sb.append(literal);
	//            sb.append("</span>");
	//
	//            if (it.hasNext()) {
	//            	insertHTMLCommaAndSpace();
	//            }
	//        }
	//    }

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLDeclarationAxiom)
	 */
	@Override
	public void visit(final OWLDeclarationAxiom axiom) {
		this.sb.append("Declaration(");
		this.writeAnnotations(axiom);
		final OWLEntity entity = axiom.getEntity();
		if (entity.isOWLClass()) {
			this.sb.append("Class(");
		} else if (entity.isOWLObjectProperty()) {
			this.sb.append("ObjectProperty(");
		} else if (entity.isOWLDataProperty()) {
			this.sb.append("DataProperty(");
		} else if (entity.isOWLNamedIndividual()) {
			this.sb.append("NamedIndividual(");
		} else if (entity.isOWLDatatype()) {
			this.sb.append("Datatype(");
		} else if (entity.isOWLAnnotationProperty()) {
			this.sb.append("AnnotationProperty(");
		}
		axiom.getEntity().accept(this);
		this.sb.append("))");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom)
	 */
	@Override
	public void visit(final OWLDatatypeDefinitionAxiom axiom) {
		this.sb.append("DatatypeDefinition(");
		this.writeAnnotations(axiom);
		axiom.getDatatype().accept(this);
		this.sb.append(' ');
		axiom.getDataRange().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLAnnotationAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom)
	 */
	@Override
	public void visit(final OWLAnnotationAssertionAxiom axiom) {
		this.sb.append("AnnotationAssertion(");
		this.writeAnnotations(axiom);
		axiom.getProperty().accept(this);
		this.insertSpace();
		axiom.getSubject().accept(this);
		this.insertSpace();
		axiom.getValue().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLAnnotationAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom)
	 */
	@Override
	public void visit(final OWLSubAnnotationPropertyOfAxiom axiom) {
		this.sb.append("SubAnnotationPropertyOf(");
		this.writeAnnotations(axiom);
		axiom.getSubProperty().accept(this);
		this.sb.append(' ');
		axiom.getSuperProperty().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLAnnotationAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom)
	 */
	@Override
	public void visit(final OWLAnnotationPropertyDomainAxiom axiom) {
		this.sb.append("AnnotationPropertyDomain(");
		axiom.getProperty().accept(this);
		this.sb.append(' ');
		axiom.getDomain().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLAnnotationAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom)
	 */
	@Override
	public void visit(final OWLAnnotationPropertyRangeAxiom axiom) {
		this.sb.append("AnnotationPropertyRange(");
		axiom.getProperty().accept(this);
		this.sb.append(' ');
		axiom.getRange().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLSubClassOfAxiom)
	 */
	@Override
	public void visit(final OWLSubClassOfAxiom axiom) {
		this.sb.append("SubClassOf(");
		this.writeAnnotations(axiom);
		axiom.getSubClass().accept(this);
		this.insertSpace();
		axiom.getSuperClass().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom)
	 */
	@Override
	public void visit(final OWLNegativeObjectPropertyAssertionAxiom axiom) {
		this.sb.append("NegativeObjectPropertyAssertion(");
		this.writeAnnotations(axiom);
		axiom.getProperty().accept(this);
		this.insertSpace();
		axiom.getSubject().accept(this);
		this.insertSpace();
		axiom.getObject().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom)
	 */
	@Override
	public void visit(final OWLAsymmetricObjectPropertyAxiom axiom) {
		this.sb.append("AsymmetricObjectProperty(");
		this.writeAnnotations(axiom);
		axiom.getProperty().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom)
	 */
	@Override
	public void visit(final OWLReflexiveObjectPropertyAxiom axiom) {
		this.sb.append("ReflexiveObjectProperty(");
		this.writeAnnotations(axiom);
		axiom.getProperty().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLDisjointClassesAxiom)
	 */
	@Override
	public void visit(final OWLDisjointClassesAxiom axiom) {
		this.sb.append("DisjointClasses(");
		this.writeAnnotations(axiom);
		this.render(axiom.getClassExpressions());
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom)
	 */
	@Override
	public void visit(final OWLDataPropertyDomainAxiom axiom) {
		this.sb.append("DataPropertyDomain(");
		this.writeAnnotations(axiom);
		axiom.getProperty().accept(this);
		this.insertSpace();
		axiom.getDomain().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom)
	 */
	@Override
	public void visit(final OWLObjectPropertyDomainAxiom axiom) {
		this.sb.append("ObjectPropertyDomain(");
		this.writeAnnotations(axiom);
		axiom.getProperty().accept(this);
		this.insertSpace();
		axiom.getDomain().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom)
	 */
	@Override
	public void visit(final OWLEquivalentObjectPropertiesAxiom axiom) {
		this.writeAnnotations(axiom);
		this.render(axiom.getProperties());

		//		this.sb.append("EquivalentObjectProperties(");
		//		this.writeAnnotations(axiom);
		//		this.render(axiom.getProperties());
		//		this.sb.append(" )");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom)
	 */
	@Override
	public void visit(final OWLNegativeDataPropertyAssertionAxiom axiom) {
		this.sb.append("NegativeDataPropertyAssertion(");
		this.writeAnnotations(axiom);
		axiom.getProperty().accept(this);
		this.insertSpace();
		axiom.getSubject().accept(this);
		this.insertSpace();
		axiom.getObject().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom)
	 */
	@Override
	public void visit(final OWLDifferentIndividualsAxiom axiom) {
		this.sb.append("DifferentIndividuals(");
		this.writeAnnotations(axiom);
		this.render(axiom.getIndividuals());
		this.sb.append(" )");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom)
	 */
	@Override
	public void visit(final OWLDisjointDataPropertiesAxiom axiom) {
		this.sb.append("DisjointDataProperties(");
		this.writeAnnotations(axiom);
		this.render(axiom.getProperties());
		this.sb.append(" )");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom)
	 */
	@Override
	public void visit(final OWLDisjointObjectPropertiesAxiom axiom) {
		this.writeAnnotations(axiom);
		this.render(axiom.getProperties());

		//		this.sb.append("DisjointObjectProperties(");
		//		this.writeAnnotations(axiom);
		//		this.render(axiom.getProperties());
		//		this.sb.append(" )");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom)
	 */
	@Override
	public void visit(final OWLObjectPropertyRangeAxiom axiom) {
		this.sb.append("ObjectPropertyRange(");
		this.writeAnnotations(axiom);
		axiom.getProperty().accept(this);
		this.insertSpace();
		axiom.getRange().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom)
	 */
	@Override
	public void visit(final OWLObjectPropertyAssertionAxiom axiom) {
		this.sb.append("ObjectPropertyAssertion(");
		this.writeAnnotations(axiom);
		axiom.getProperty().accept(this);
		this.insertSpace();
		axiom.getSubject().accept(this);
		this.insertSpace();
		axiom.getObject().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom)
	 */
	@Override
	public void visit(final OWLFunctionalObjectPropertyAxiom axiom) {
		this.sb.append("FunctionalObjectProperty(");
		this.writeAnnotations(axiom);
		axiom.getProperty().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom)
	 */
	@Override
	public void visit(final OWLSubObjectPropertyOfAxiom axiom) {
		this.sb.append("SubObjectPropertyOf(");
		this.writeAnnotations(axiom);
		axiom.getSubProperty().accept(this);
		this.insertSpace();
		axiom.getSuperProperty().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLDisjointUnionAxiom)
	 */
	@Override
	public void visit(final OWLDisjointUnionAxiom axiom) {
		this.sb.append("DisjointUnion(");
		this.writeAnnotations(axiom);
		axiom.getOWLClass().accept(this);
		this.insertSpace();
		this.render(axiom.getClassExpressions());
		this.sb.append(" )");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom)
	 */
	@Override
	public void visit(final OWLSymmetricObjectPropertyAxiom axiom) {
		this.sb.append("SymmetricObjectProperty(");
		this.writeAnnotations(axiom);
		axiom.getProperty().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom)
	 */
	@Override
	public void visit(final OWLDataPropertyRangeAxiom axiom) {
		this.sb.append("DataPropertyRange(");
		this.writeAnnotations(axiom);
		axiom.getProperty().accept(this);
		this.insertSpace();
		axiom.getRange().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom)
	 */
	@Override
	public void visit(final OWLFunctionalDataPropertyAxiom axiom) {
		this.sb.append("FunctionalDataProperty(");
		this.writeAnnotations(axiom);
		axiom.getProperty().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom)
	 */
	@Override
	public void visit(final OWLEquivalentDataPropertiesAxiom axiom) {
		this.sb.append("EquivalentDataProperties(");
		this.writeAnnotations(axiom);
		this.render(axiom.getProperties());
		this.sb.append(" )");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLClassAssertionAxiom)
	 */
	@Override
	public void visit(final OWLClassAssertionAxiom axiom) {
		this.sb.append("ClassAssertion(");
		this.writeAnnotations(axiom);
		axiom.getClassExpression().accept(this);
		this.insertSpace();
		axiom.getIndividual().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom)
	 */
	@Override
	public void visit(final OWLEquivalentClassesAxiom axiom) {
		this.writeAnnotations(axiom);
		this.render(axiom.getClassExpressions());

		//        sb.append("EquivalentClasses(");
		//        writeAnnotations(axiom);
		//        render(axiom.getClassExpressions());
		//        sb.append(" )");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom)
	 */
	@Override
	public void visit(final OWLDataPropertyAssertionAxiom axiom) {
		this.sb.append("DataPropertyAssertion(");
		this.writeAnnotations(axiom);
		axiom.getProperty().accept(this);
		this.insertSpace();
		axiom.getSubject().accept(this);
		this.insertSpace();
		axiom.getObject().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom)
	 */
	@Override
	public void visit(final OWLTransitiveObjectPropertyAxiom axiom) {
		this.sb.append("TransitiveObjectProperty(");
		this.writeAnnotations(axiom);
		axiom.getProperty().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom)
	 */
	@Override
	public void visit(final OWLIrreflexiveObjectPropertyAxiom axiom) {
		this.sb.append("IrreflexiveObjectProperty(");
		this.writeAnnotations(axiom);
		axiom.getProperty().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom)
	 */
	@Override
	public void visit(final OWLSubDataPropertyOfAxiom axiom) {
		this.sb.append("SubDataPropertyOf(");
		this.writeAnnotations(axiom);
		axiom.getSubProperty().accept(this);
		this.insertSpace();
		axiom.getSuperProperty().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom)
	 */
	@Override
	public void visit(final OWLInverseFunctionalObjectPropertyAxiom axiom) {
		this.sb.append("InverseFunctionalObjectProperty(");
		this.writeAnnotations(axiom);
		axiom.getProperty().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLSameIndividualAxiom)
	 */
	@Override
	public void visit(final OWLSameIndividualAxiom axiom) {
		this.sb.append("SameIndividual(");
		this.writeAnnotations(axiom);
		this.render(axiom.getIndividuals());
		this.sb.append(" )");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom)
	 */
	@Override
	public void visit(final OWLSubPropertyChainOfAxiom axiom) {
		this.sb.append("SubObjectPropertyOf(");
		this.writeAnnotations(axiom);
		this.sb.append("ObjectPropertyChain(");
		for (final OWLObjectPropertyExpression prop : axiom.getPropertyChain()) {
			this.insertSpace();
			prop.accept(this);
		}
		this.sb.append(" )");
		this.insertSpace();
		axiom.getSuperProperty().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom)
	 */
	@Override
	public void visit(final OWLInverseObjectPropertiesAxiom axiom) {
		this.sb.append("InverseObjectProperties(");
		this.writeAnnotations(axiom);
		axiom.getFirstProperty().accept(this);
		this.sb.append(' ');
		axiom.getSecondProperty().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.OWLHasKeyAxiom)
	 */
	@Override
	public void visit(final OWLHasKeyAxiom axiom) {
		this.sb.append("HasKey(");
		this.writeAnnotations(axiom);
		axiom.getClassExpression().accept(this);
		this.sb.append(" (");
		for (final OWLObjectPropertyExpression prop : axiom.getObjectPropertyExpressions()) {
			prop.accept(this);
			this.sb.append(' ');
		}
		this.sb.append(") (");
		for (final OWLDataPropertyExpression prop : axiom.getDataPropertyExpressions()) {
			prop.accept(this);
			this.sb.append(' ');
		}
		this.sb.append(')');
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLLogicalAxiomVisitor#visit(org.semanticweb.owlapi.model.SWRLRule)
	 */
	@Override
	public void visit(final SWRLRule rule) {
		this.sb.append("DLSafeRule(");
		this.writeAnnotations(rule);
		this.sb.append(" Body(");
		this.render(rule.getBody());
		this.sb.append(')');
		this.sb.append(" Head(");
		this.render(rule.getHead());
		this.sb.append(')');
		this.sb.append(" )");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLClassExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLClass)
	 */
	@Override
	public void visit(final OWLClass ce) {
		this.sb.append(this.shortFormProvider.getShortForm(ce));
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLDataVisitor#visit(org.semanticweb.owlapi.model.OWLFacetRestriction)
	 */
	@Override
	public void visit(final OWLFacetRestriction node) {
		this.sb.append("facetRestriction(");
		this.sb.append(node.getFacet());
		this.insertSpace();
		node.getFacetValue().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLDataRangeVisitor#visit(org.semanticweb.owlapi.model.OWLDatatypeRestriction)
	 */
	@Override
	public void visit(final OWLDatatypeRestriction node) {
		this.sb.append("DataRangeRestriction(");
		node.getDatatype().accept(this);
		for (final OWLFacetRestriction restriction : node.getFacetRestrictions()) {
			this.insertSpace();
			restriction.accept(this);
		}
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLPropertyExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLObjectProperty)
	 */
	@Override
	public void visit(final OWLObjectProperty property) {
		this.sb.append(this.shortFormProvider.getShortForm(property));
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLPropertyExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLObjectInverseOf)
	 */
	@Override
	public void visit(final OWLObjectInverseOf property) {
		//this.sb.append("InverseOf(");
		property.getInverse().accept(this);
		//this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLPropertyExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLDataProperty)
	 */
	@Override
	public void visit(final OWLDataProperty property) {
		this.sb.append(this.shortFormProvider.getShortForm(property));
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLPropertyExpressionVisitor#visit(org.semanticweb.owlapi.model.OWLAnnotationProperty)
	 */
	@Override
	public void visit(final OWLAnnotationProperty property) {
		this.sb.append(this.shortFormProvider.getShortForm(property));
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLEntityVisitor#visit(org.semanticweb.owlapi.model.OWLNamedIndividual)
	 */
	@Override
	public void visit(final OWLNamedIndividual individual) {
		this.sb.append("<span class=\"owl-individual owl-named-individual\" title=\"OWL Named Individual\">");
		this.sb.append(this.shortFormProvider.getShortForm(individual));
		this.sb.append("</span>");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLIndividualVisitor#visit(org.semanticweb.owlapi.model.OWLAnonymousIndividual)
	 */
	@Override
	public void visit(final OWLAnonymousIndividual individual) {
		this.sb.append("<span class=\"owl-individual owl-anonymous-individual\" title=\"OWL Anonymous Individual\">");
		this.sb.append(individual.getID());
		this.sb.append("</span>");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLAnnotationValueVisitor#visit(org.semanticweb.owlapi.model.IRI)
	 */
	@Override
	public void visit(final IRI iri) {
		//sb.append('<');
		this.sb.append(iri);
		//sb.append('>');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLAnnotationObjectVisitor#visit(org.semanticweb.owlapi.model.OWLAnnotation)
	 */
	@Override
	public void visit(final OWLAnnotation node) {
		this.sb.append("Annotation(");
		final Set<OWLAnnotation> annos = node.getAnnotations();
		for (final OWLAnnotation anno : annos) {
			anno.accept(this);
			this.sb.append(' ');
		}
		node.getProperty().accept(this);
		this.sb.append(' ');
		node.getValue().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.SWRLObjectVisitor#visit(org.semanticweb.owlapi.model.SWRLClassAtom)
	 */
	@Override
	public void visit(final SWRLClassAtom node) {
		this.sb.append("ClassAtom(");
		node.getPredicate().accept(this);
		this.sb.append(' ');
		node.getArgument().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.SWRLObjectVisitor#visit(org.semanticweb.owlapi.model.SWRLDataRangeAtom)
	 */
	@Override
	public void visit(final SWRLDataRangeAtom node) {
		this.sb.append("DataRangeAtom(");
		node.getPredicate().accept(this);
		this.sb.append(' ');
		node.getArgument().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.SWRLObjectVisitor#visit(org.semanticweb.owlapi.model.SWRLObjectPropertyAtom)
	 */
	@Override
	public void visit(final SWRLObjectPropertyAtom node) {
		this.sb.append("ObjectPropertyAtom(");
		node.getPredicate().accept(this);
		this.sb.append(' ');
		node.getFirstArgument().accept(this);
		this.sb.append(' ');
		node.getSecondArgument().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.SWRLObjectVisitor#visit(org.semanticweb.owlapi.model.SWRLDataPropertyAtom)
	 */
	@Override
	public void visit(final SWRLDataPropertyAtom node) {
		this.sb.append("DataPropertyAtom(");
		node.getPredicate().accept(this);
		this.sb.append(' ');
		node.getFirstArgument().accept(this);
		this.sb.append(' ');
		node.getSecondArgument().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.SWRLObjectVisitor#visit(org.semanticweb.owlapi.model.SWRLBuiltInAtom)
	 */
	@Override
	public void visit(final SWRLBuiltInAtom node) {
		this.sb.append("BuiltInAtom(");
		this.sb.append(this.getShortForm(node.getPredicate()));
		this.sb.append(' ');
		for (final SWRLArgument arg : node.getArguments()) {
			arg.accept(this);
			this.sb.append(' ');
		}
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.SWRLObjectVisitor#visit(org.semanticweb.owlapi.model.SWRLVariable)
	 */
	@Override
	public void visit(final SWRLVariable node) {
		this.sb.append("Variable(");
		this.sb.append(this.getShortForm(node.getIRI()));
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.SWRLObjectVisitor#visit(org.semanticweb.owlapi.model.SWRLIndividualArgument)
	 */
	@Override
	public void visit(final SWRLIndividualArgument node) {
		node.getIndividual().accept(this);
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.SWRLObjectVisitor#visit(org.semanticweb.owlapi.model.SWRLLiteralArgument)
	 */
	@Override
	public void visit(final SWRLLiteralArgument node) {
		node.getLiteral().accept(this);
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.SWRLObjectVisitor#visit(org.semanticweb.owlapi.model.SWRLSameIndividualAtom)
	 */
	@Override
	public void visit(final SWRLSameIndividualAtom node) {
		this.sb.append("SameAsAtom(");
		node.getFirstArgument().accept(this);
		this.sb.append(' ');
		node.getSecondArgument().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.SWRLObjectVisitor#visit(org.semanticweb.owlapi.model.SWRLDifferentIndividualsAtom)
	 */
	@Override
	public void visit(final SWRLDifferentIndividualsAtom node) {
		this.sb.append("DifferentFromAtom(");
		node.getFirstArgument().accept(this);
		this.sb.append(' ');
		node.getSecondArgument().accept(this);
		this.sb.append(')');
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLNamedObjectVisitor#visit(org.semanticweb.owlapi.model.OWLOntology)
	 */
	@Override
	public void visit(final OWLOntology ontology) {
		this.sb.append("Ontology(").append(ontology.getOntologyID()).append(" [Axioms: ").append(ontology.getAxiomCount())
		.append("] [Logical axioms: ").append(ontology.getLogicalAxiomCount()).append("])");
	}

	/**
	 * Render individuals.
	 *
	 * @param individuals the individuals
	 */
	public void renderIndividuals(final Set<? extends OWLIndividual> individuals) {
		for (final Iterator<? extends OWLIndividual> it = HTMLRenderer.toSortedSet(individuals).iterator(); it
				.hasNext();) {
			final OWLIndividual individual = it.next();
			this.sb.append("<span class=\"owl-individual\" title=\"");

			String shortFormIRI = StringPool.BLANK;

			if(individual.isAnonymous()){
				final OWLAnonymousIndividual anonymous = individual.asOWLAnonymousIndividual();
				final IRI iri = anonymous.asIRI().get();
				this.sb.append(iri.toString());
				shortFormIRI = iri.getShortForm();
			} else {
				final OWLNamedIndividual named = individual.asOWLNamedIndividual();
				final IRI iri = named.getIRI();
				this.sb.append(iri.toString());
				shortFormIRI = iri.getShortForm();
			}

			this.sb.append("\">");
			this.sb.append(shortFormIRI);
			this.sb.append("</span>");

			if (it.hasNext()) {
				this.insertHTMLCommaAndSpace();
			}
		}
	}

	/**
	 * Rendering equivalent classes.
	 *
	 * @param classExpressionSet the class expression set
	 */
	public void renderEquivalentClasses(final Set<? extends OWLClassExpression> classExpressionSet) {
		for (final Iterator<? extends OWLClassExpression> it = HTMLRenderer.toSortedSet(classExpressionSet).iterator(); it
				.hasNext();) {

			final OWLClassExpression owlClassExpression = it.next();
			this.sb.append("<span class=\"owl-equivalent-classes-child\">");
			owlClassExpression.accept(this);
			this.sb.append("</span>");

			if (it.hasNext()) {
				this.insertHTMLCommaAndSpace();
			}
		}
	}

	/**
	 * Rendering intersection operands.
	 *
	 * @param operands the operands
	 */
	protected void renderIntersectionOfOperands(final Set<? extends OWLClassExpression> operands) {
		for (final Iterator<? extends OWLClassExpression> it = HTMLRenderer.toSortedSet(operands).iterator(); it
				.hasNext();) {

			final OWLClassExpression owlClassExpression = it.next();

			final boolean isOWLClass = !owlClassExpression.isAnonymous();

			this.sb.append("<span class=\"owl-object-intersection-of-child\">");

			if(!isOWLClass){
				this.sb.append(StringPool.OPEN_PARENTHESIS_HTML);
				owlClassExpression.accept(this);
				this.sb.append(StringPool.CLOSE_PARENTHESIS_HTML);
			} else {
				owlClassExpression.accept(this);
			}

			this.sb.append("</span>");

			if (it.hasNext()) {
				this.insertAndOperator();
			}
		}
	}

	/**
	 * Rendering union of operands.
	 *
	 * @param operands the operands
	 */
	protected void renderUnionOfOperands(final Set<? extends OWLClassExpression> operands) {
		for (final Iterator<? extends OWLClassExpression> it = HTMLRenderer.toSortedSet(operands).iterator(); it
				.hasNext();) {

			final OWLClassExpression owlClassExpression = it.next();
			this.sb.append("<span class=\"owl-object-union-of-child\">");
			owlClassExpression.accept(this);
			this.sb.append("</span>");

			if (it.hasNext()) {
				this.insertOrOperator();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLObjectRenderer#render(org.semanticweb.owlapi.model.OWLObject)
	 */
	@Override
	public String render(final OWLObject object) {
		this.reset();
		object.accept(this);
		return this.sb.toString();
	}

	/**
	 * To sorted set.
	 *
	 * @param <N> the number type
	 * @param set the set
	 * @return the sets the
	 */
	public static <N extends OWLObject> Set<N> toSortedSet(final Set<N> set) {
		return new TreeSet<>(set);
	}

	/**
	 * This method is used to append "and" keyword for intersection of restriction.
	 */
	private void insertAndOperator() {
		this.insertHTMLSpace();
		this.sb.append("<span class=\"and-operator\">and</span>");
		this.insertHTMLSpace();
	}

	/**
	 * This method is used to append "or" keyword for union of restriction.
	 */
	private void insertOrOperator() {
		this.insertHTMLSpace();
		this.sb.append("<span class=\"or-operator\">or</span>");
		this.insertHTMLSpace();
	}

	/**
	 * Insert html comma and space.
	 */
	private void insertHTMLCommaAndSpace() {
		this.sb.append("&#44;&nbsp;");
	}

	/**
	 * Insert html space.
	 */
	private void insertHTMLSpace() {
		this.sb.append("&nbsp;");
	}

	/**
	 * Insert space.
	 */
	private void insertSpace() {
		this.sb.append(' ');
	}

	/**
	 * Write annotations.
	 *
	 * @param axiom        the axiom whose annotations should be written
	 */
	public void writeAnnotations(final OWLAxiom axiom) {
		for (final OWLAnnotation anno : axiom.getAnnotations()) {
			anno.accept(this);
			this.insertHTMLCommaAndSpace();
		}
	}
}

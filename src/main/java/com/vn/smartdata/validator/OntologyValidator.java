package com.vn.smartdata.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.vn.smartdata.model.Ontology;


/**
 * The Class OntologyValidator.
 */
@Component
public class OntologyValidator implements Validator {

	// Ticket Object can be validated by this validator
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> paramClass) {
		return Ontology.class.equals(paramClass);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(final Object obj, final Errors errors) {
		final Ontology ontology = (Ontology) obj;

		final String ontologyName = ontology.getOntologyName();
		final String description = ontology.getDescription();

		// if (ontologyName == null || StringUtils.isBlank(ontologyName)) {
		// errors.rejectValue("ontologyName", "ontologyName.");
		// }
		//
		// if (ticket.getTicketTitle() == null
		// || ticket.getTicketTitle().length() < SystemConstants.TITLE_MINIMUM
		// || ticket.getTicketTitle().length() > SystemConstants.TITLE_MAXIMUM)
		// {
		// errors.rejectValue("ticketTitle", "ticketTitle.length");
		// }
		//
		// if (ticket.getDescription() == null
		// || ticket.getDescription().length() <
		// SystemConstants.DESCRIPTION_MINIMUM
		// || ticket.getDescription().length() >
		// SystemConstants.DESCRIPTION_MAXIMUM) {
		// errors.rejectValue("description", "description.length");
		// }
		//
		// ValidationUtils.rejectIfEmptyOrWhitespace(errors, "ticketTitle",
		// "ticketTitle.required");
		// ValidationUtils.rejectIfEmptyOrWhitespace(errors,
		// "ticketCategory.id",
		// "ticketCategory.id.required");
		// ValidationUtils.rejectIfEmptyOrWhitespace(errors,
		// "ticketCategoryType.id", "ticketCategoryType.id.required");
		// ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description",
		// "description.required");
	}
}

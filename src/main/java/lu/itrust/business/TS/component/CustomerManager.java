/**
 * 
 */
package lu.itrust.business.TS.component;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lu.itrust.business.TS.controller.form.CustomerForm;
import lu.itrust.business.TS.controller.form.TicketingSystemForm;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOCustomer;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.TicketingSystem;
import lu.itrust.business.TS.validator.CustomerValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;

/**
 * @author eomar
 *
 */
@Component
public class CustomerManager {

	@Autowired
	private DAOCustomer daoCustomer;

	@Autowired
	private DAOAnalysis daoAnalysis;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private MessageSource messageSource;

	@Transactional
	public void switchCustomer(String identifier, int idCustomer, String username) throws Exception {
		List<Analysis> analyses = daoAnalysis.getAllByIdentifier(identifier);
		Customer customer = daoCustomer.get(idCustomer);
		/**
		 * Log
		 */
		analyses.stream().filter(analysis -> analysis.getCustomer() != customer).findAny()
				.ifPresent(analysis -> TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS,
						"log.user.switch.analysis.customer",
						String.format("Analysis: %s, old: %s, new: %s", analysis.getIdentifier(),
								analysis.getCustomer().getOrganisation(), customer.getOrganisation()),
						username, LogAction.SWITCH_CUSTOMER, analysis.getIdentifier(),
						analysis.getCustomer().getOrganisation(), customer.getOrganisation()));
		for (Analysis analysis : analyses) {
			analysis.setCustomer(customer);
			analysis.getUserRights().stream().forEach(userAnalysisRight -> {
				if (!userAnalysisRight.getUser().containsCustomer(customer))
					userAnalysisRight.getUser().addCustomer(customer);
			});
			daoAnalysis.saveOrUpdate(analysis);
		}

	}

	/**
	 * buildCustomer: <br>
	 * Description
	 * 
	 * @param errors
	 * @param customer
	 * @param source
	 * @param locale
	 * @param adminAccess
	 * @return
	 */
	@Transactional
	public Customer buildCustomer(Map<String, String> errors, CustomerForm form, Locale locale, boolean adminAccess) {
		Customer customer = null;
		try {
			customer = form.getId() > 0 ? daoCustomer.get(form.getId()) : new Customer();
			if (customer == null) {
				errors.put("customer", messageSource.getMessage("error.customer.not_found", null,
						"Customer can not be found", locale));
				return null;
			}

			ValidatorField validator = serviceDataValidation.findByClass(Customer.class);
			if (validator == null)
				serviceDataValidation.register(validator = new CustomerValidator());

			String error = null;

			error = validator.validate(customer, "organisation", form.getOrganisation());
			if (error != null)
				errors.put("organisation", serviceDataValidation.ParseError(error, messageSource, locale));
			else if (form.getId() > 0 && daoCustomer.existsByIdAndOrganisation(form.getId(), form.getOrganisation())
					|| form.getId() < 1 && daoCustomer.existsByOrganisation(form.getOrganisation()))
				errors.put("organisation",
						messageSource.getMessage("error.customer.name.already.exists",
								new String[] { form.getOrganisation() },
								String.format("A customer with this name '%s' already exists", form.getOrganisation()),
								locale));
			else
				customer.setOrganisation(form.getOrganisation());

			error = validator.validate(customer, "contactPerson", form.getContactPerson());
			if (error != null)
				errors.put("contactPerson", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setContactPerson(form.getContactPerson());

			error = validator.validate(customer, "phoneNumber", form.getPhoneNumber());
			if (error != null)
				errors.put("phoneNumber", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setPhoneNumber(form.getPhoneNumber());

			error = validator.validate(customer, "email", form.getEmail());
			if (error != null)
				errors.put("email", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setEmail(form.getEmail());

			error = validator.validate(customer, "address", form.getAddress());
			if (error != null)
				errors.put("address", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setAddress(form.getAddress());

			error = validator.validate(customer, "city", form.getCity());
			if (error != null)
				errors.put("city", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setCity(form.getCity());

			error = validator.validate(customer, "ZIPCode", form.getZipCode());
			if (error != null)
				errors.put("ZIPCode", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setZIPCode(form.getZipCode());

			error = validator.validate(customer, "country", form.getCountry());
			if (error != null)
				errors.put("country", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setCountry(form.getCountry());

			if (adminAccess) {
				customer.setCanBeUsed(form.isCanBeUsed());
				if (form.getTicketingSystem() != null) {
					if (customer.getTicketingSystem() == null) {
						customer.setTicketingSystem(new TicketingSystem());
						customer.getTicketingSystem().setCustomer(customer);
					}
					final TicketingSystem ticketingSystem = customer.getTicketingSystem();
					final TicketingSystemForm systemForm = form.getTicketingSystem();
					ticketingSystem.setEnabled(form.getTicketingSystem().isEnabled());
					if (systemForm.getType() == null) {
						if (ticketingSystem.isEnabled())
							errors.put("ticketingSystem.enabled",
									messageSource.getMessage("error.ticketing.system.type.empty", null, locale));
						else
							ticketingSystem.setType(null);
					} else
						ticketingSystem.setType(systemForm.getType());

					if (!StringUtils.hasText(systemForm.getName())) {
						if (ticketingSystem.isEnabled())
							errors.put("ticketingSystem.name",
									messageSource.getMessage("error.ticketing.system.name.empty", null, locale));
						else
							ticketingSystem.setName(null);
					} else
						ticketingSystem.setName(systemForm.getName());

					if (!StringUtils.hasText(systemForm.getUrl())) {
						if (ticketingSystem.isEnabled())
							errors.put("ticketingSystem.url",
									messageSource.getMessage("error.ticketing.system.url.empty", null, locale));
						else
							ticketingSystem.setUrl(null);
					} else if (!isHttpsUrl(systemForm.getUrl()))
						errors.put("ticketingSystem.url",
								messageSource.getMessage("error.ticketing.system.url.bad.protocol", null, locale));
					else
						ticketingSystem.setUrl(systemForm.getUrl());
				}

			}

		} catch (Exception e) {
			errors.put("customer",
					messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
		}
		return customer;
	}

	private boolean isHttpsUrl(String value) {
		try {
			URL url = new URL(value);
			return url.getProtocol().equalsIgnoreCase("https");
		} catch (Exception e) {
			return false;
		}
	}

}

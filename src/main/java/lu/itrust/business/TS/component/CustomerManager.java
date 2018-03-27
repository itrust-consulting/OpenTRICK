/**
 * 
 */
package lu.itrust.business.TS.component;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOCustomer;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
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
				.ifPresent(analysis -> TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.user.switch.analysis.customer",
						String.format("Analysis: %s, old: %s, new: %s", analysis.getIdentifier(), analysis.getCustomer().getOrganisation(), customer.getOrganisation()), username,
						LogAction.SWITCH_CUSTOMER, analysis.getIdentifier(), analysis.getCustomer().getOrganisation(), customer.getOrganisation()));
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
	 * @return
	 */
	@Transactional
	public boolean buildCustomer(Map<String, String> errors, Customer customer, String source, Locale locale) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			int id = jsonNode.get("id").asInt();
			if (id > 0)
				customer.setId(id);

			ValidatorField validator = serviceDataValidation.findByClass(Customer.class);
			if (validator == null)
				serviceDataValidation.register(validator = new CustomerValidator());

			String organisation = jsonNode.get("organisation").asText();
			String contactPerson = jsonNode.get("contactPerson").asText();
			String telephoneNumber = jsonNode.get("phoneNumber").asText();
			String email = jsonNode.get("email").asText();
			String address = jsonNode.get("address").asText();
			String city = jsonNode.get("city").asText();
			String ZIPCode = jsonNode.get("ZIPCode").asText();
			String country = jsonNode.get("country").asText();
			String error = null;

			error = validator.validate(customer, "organisation", organisation);
			if (error != null)
				errors.put("organisation", serviceDataValidation.ParseError(error, messageSource, locale));
			else if (id > 0 && daoCustomer.existsByIdAndOrganisation(id, organisation) || id < 1 && daoCustomer.existsByOrganisation(organisation))
				errors.put("organisation", messageSource.getMessage("error.customer.name.already.exists", new String[] { organisation },
						String.format("A customer with this name '%s' already exists", organisation), locale));
			else
				customer.setOrganisation(organisation);

			error = validator.validate(customer, "contactPerson", contactPerson);
			if (error != null)
				errors.put("contactPerson", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setContactPerson(contactPerson);

			error = validator.validate(customer, "phoneNumber", telephoneNumber);
			if (error != null)
				errors.put("phoneNumber", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setPhoneNumber(telephoneNumber);

			error = validator.validate(customer, "email", email);
			if (error != null)
				errors.put("email", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setEmail(email);

			error = validator.validate(customer, "address", address);
			if (error != null)
				errors.put("address", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setAddress(address);

			error = validator.validate(customer, "city", city);
			if (error != null)
				errors.put("city", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setCity(city);

			error = validator.validate(customer, "ZIPCode", ZIPCode);
			if (error != null)
				errors.put("ZIPCode", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setZIPCode(ZIPCode);

			error = validator.validate(customer, "country", country);
			if (error != null)
				errors.put("country", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				customer.setCountry(country);

			customer.setCanBeUsed(jsonNode.get("canBeUsed") == null ? true : !jsonNode.get("canBeUsed").asText().equals("on"));
		} catch (Exception e) {
			errors.put("customer", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
		}

		return errors.isEmpty();

	}

	
	

}

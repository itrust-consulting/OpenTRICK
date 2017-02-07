package lu.itrust.business.TS.validator;

import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.validator.field.ValidatorFieldImpl;

public class CustomerValidator extends ValidatorFieldImpl implements Validator {

	@Override
	public String validate( String fieldName, Object candidate) {
		if (fieldName == null || fieldName.trim().isEmpty())
			return null;
		switch (fieldName) {
			case "organisation":
				if (candidate == null || !(candidate instanceof String))
					return "error.customer.organisation.unsupported::Name value is not supported";
				else if (candidate.toString().trim().isEmpty())
					return "error.customer.organisation.empty::Name cannot be empty";
				break;
			case "address":
				if (candidate == null || !(candidate instanceof String))
					return "error.customer.address.unsupported::Address value is not supported";
				else if (candidate.toString().trim().isEmpty())
					return "error.customer.address.empty::Address cannot be empty";
				break;
			case "city":
				if (candidate == null || !(candidate instanceof String))
					return "error.customer.city.unsupported::City value is not supported";
				else if (candidate.toString().trim().isEmpty())
					return "error.customer.city.empty::City cannot be empty";
				else if (!candidate.toString().matches(Constant.REGEXP_VALID_NAME))
					return "error.customer.city.invalid::City name is not valid";
				break;
			case "ZIPCode":
				if (candidate == null || !(candidate instanceof String))
					return "error.customer.zip_code.unsupported::ZIP Code value is not supported";
				else if (candidate.toString().trim().isEmpty())
					return "error.customer.zip_code.empty::ZIP Code cannot be empty";
				break;
			case "country":
				if (candidate == null || !(candidate instanceof String))
					return "error.customer.country.unsupported::Country value is not supported";
				else if (candidate.toString().trim().isEmpty())
					return "error.customer.country.empty::Country cannot be empty";
				else if (!candidate.toString().matches(Constant.REGEXP_VALID_NAME))
					return "error.customer.country.invalid::Country name is not valid";
				break;
			case "contactPerson":
				if (candidate == null || !(candidate instanceof String))
					return "error.customer.contactPerson.unsupported::Contact Person value is not supported";
				else if (candidate.toString().trim().isEmpty())
					return "error.customer.contact_person.empty::Contact Person cannot be empty";
				else if (!candidate.toString().matches(Constant.REGEXP_VALID_NAME))
					return "error.customer.contact_person.invalid::Contact Person is not valid";
				break;
			case "phoneNumber":
				if (candidate == null || !(candidate instanceof String))
					return "error.customer.phone_number.unsupported::Phone number value is not supported";
				else if (candidate.toString().trim().isEmpty())
					return "error.customer.phone_number.empty::Phone number cannot be empty";
				break;
			case "email":
				if (candidate == null || !(candidate instanceof String))
					return "error.customer.email.unsupported::Email value is not supported";
				else if (candidate.toString().trim().isEmpty())
					return "error.customer.email.empty::Email cannot be empty";
				else if (!candidate.toString().matches(Constant.REGEXP_VALID_EMAIL))
					return "error.customer.email.invalid::Email is not valid";
				break;
		}
		return null;
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) throws TrickException {
		return validate(o, fieldName, candidate);
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate, List<Object> choose) throws TrickException {
		return validate(o, fieldName, candidate);
	}

	@Override
	public Class<?> supported() {
		return Customer.class;
	}

	@Override
	public void validate(Object arg0, Errors arg1) {
		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "organisation", "error.customer.organisation.empty", "Name cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "address", "error.customer.address.empty", "Address cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "city", "error.customer.city.empty", "City cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "ZIPCode", "error.customer.ZIPCode.empty", "ZIP code cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "country", "error.customer.country.empty", "Country cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "contactPerson", "error.customer.contactPerson.empty", "Contact Person cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "phoneNumber", "error.customer.phoneNumber.empty", "Phone Number cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "email", "error.customer.email.empty", "Email cannot be empty");
		Customer customer = (Customer) arg0;
		if (!arg1.hasFieldErrors("city") && !customer.getCity().matches(Constant.REGEXP_VALID_NAME))
			arg1.rejectValue("city", "error.customer.city.invalid", "City name is not valid");

		if (!arg1.hasFieldErrors("country") && !customer.getCountry().matches(Constant.REGEXP_VALID_NAME))
			arg1.rejectValue("country", "error.customer.country.invalid", "Country name is not valid");

		if (!arg1.hasFieldErrors("contactPerson") && !customer.getContactPerson().matches(Constant.REGEXP_VALID_NAME))
			arg1.rejectValue("contactPerson", "error.customer.contactPerson.invalid", "Contact Person is not valid");

		if (!arg1.hasFieldErrors("email") && !customer.getEmail().matches(Constant.REGEXP_VALID_EMAIL))
			arg1.rejectValue("email", "error.customer.email.invalid", "Email is not valid");
	}

}

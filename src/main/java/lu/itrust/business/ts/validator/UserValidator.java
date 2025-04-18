/**
 * 
 */
package lu.itrust.business.ts.validator;

import java.util.Collection;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.usermanagement.User;
import lu.itrust.business.ts.validator.field.ValidatorFieldImpl;

/**
 * This class is responsible for validating user objects. It implements the
 * Spring Framework's Validator interface
 * and provides validation logic for various fields of the User class.
 */
public class UserValidator extends ValidatorFieldImpl implements Validator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.isAssignableFrom(clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "login", "error.user.login.empty", "Login cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.user.password.empty",
				"Password cannot be empty");
		if (!errors.hasFieldErrors("password"))
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "repeatPassword", "error.user.password.empty",
					"Password cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "error.user.firstname.empty",
				"First name cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "error.user.lastname.empty",
				"Last name cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "error.user.email.empty",
				"Email address cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "locale", "error.user.locale.empty",
				"Language cannot be empty");

		User user = (User) target;

		if (!errors.hasFieldErrors("login") && !user.getLogin().matches(Constant.REGEXP_VALID_USERNAME))
			errors.rejectValue("login", "errors.user.login.invalid", "Login is not valid");

		if (!errors.hasFieldErrors("password") && !user.getPassword().matches(Constant.REGEXP_VALID_PASSWORD))
			errors.rejectValue("password", "errors.user.password.invalid",
					"Password does not match policy (8 characters, at least one digit, one lower and one uppercase)");

		if (!errors.hasFieldErrors("repeatPassword") && !user.getRepeatPassword().equals(user.getPassword()))
			errors.rejectValue("repeatPassword", "errors.user.repeatPassword.not_same", "Passwords are not the same");

		if (!errors.hasFieldErrors("firstName") && !user.getFirstName().matches(Constant.REGEXP_VALID_NAME))
			errors.rejectValue("firstName", "errors.user.firstname.invalid", "First name is not valid");

		if (!errors.hasFieldErrors("lastName") && !user.getLastName().matches(Constant.REGEXP_VALID_NAME))
			errors.rejectValue("lastName", "errors.user.lastname.invalid", "Last name is not valid");

		if (!errors.hasFieldErrors("email") && !EmailValidator.getInstance().isValid(user.getEmail()))
			errors.rejectValue("email", "errors.user.email.invalid", "Email address is not valid");

		if (!errors.hasFieldErrors("locale") && !user.getLocale().matches(Constant.REGEXP_VALID_ALPHA_2))
			errors.rejectValue("locale", "errors.user.locale.invalid", "Language is not valid");

	}

	/**
	 * The `String` class represents a sequence of characters. It provides various
	 * methods for manipulating and working with strings.
	 * Strings in Java are immutable, meaning that once a string object is created,
	 * its value cannot be changed.
	 * However, you can create new strings by concatenating existing strings or
	 * using methods that modify the string.
	 *
	 * @see java.lang.Object
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate) throws TrickException {
		if (o == null || !supports(o.getClass()) || fieldName == null || fieldName.trim().isEmpty())
			return null;
		User user = (User) o;
		switch (fieldName) {
			case "repeatPassword":
				if (candidate == null || !(candidate instanceof String))
					return "error.user.repeat_password.unsupported::Password value is not supported";
				if (!((String) candidate).equals(user.getPassword()))
					return "error.user.repeat_password.not_same::Repeated Password does not match Password";
				break;
			default:
				return validate(fieldName, candidate);

		}
		return null;
	}

	/**
	 * Validates the given object and returns a validation result as a string.
	 *
	 * @param o         the object to be validated
	 * @param fieldName the name of the field being validated
	 * @param candidate the candidate value to be validated
	 * @param choose    an array of options to choose from during validation
	 * @return a string representing the validation result
	 * @throws TrickException if an error occurs during validation
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) throws TrickException {
		return validate(o, fieldName, candidate);
	}

	/**
	 * Validates the given object and returns a string representation of the
	 * validation result.
	 *
	 * @param o         the object to be validated
	 * @param fieldName the name of the field being validated
	 * @param candidate the candidate value to be validated
	 * @param choose    a collection of objects to choose from during validation
	 * @return a string representation of the validation result
	 * @throws TrickException if an error occurs during validation
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Collection<Object> choose)
			throws TrickException {
		return validate(o, fieldName, candidate);
	}

	/**
	 * Returns the class that this validator supports.
	 *
	 * @return the class that this validator supports
	 */
	@Override
	public Class<?> supported() {
		return User.class;
	}

	/**
	 * Validates the given field name and candidate value.
	 * 
	 * @param fieldName the name of the field to validate
	 * @param candidate the value of the field to validate
	 * @return an error message if the field is invalid, otherwise null
	 * @throws TrickException if the validation method is not allowed for the given
	 *                        field
	 */
	@Override
	public String validate(String fieldName, Object candidate) throws TrickException {
		if (fieldName == null || fieldName.trim().isEmpty())
			return null;
		switch (fieldName) {
			case "login":
				if (candidate == null || !(candidate instanceof String))
					return "error.user.unsupported.login::Login value is not supported";
				if (!((String) candidate).matches(Constant.REGEXP_VALID_NAME))
					return "error.user.login.not_valid::Login is not valid";
				break;
			case "password":
				if (candidate == null || !(candidate instanceof String))
					return "error.user.unsupported.password::Password value is not supported";
				if (!((String) candidate).matches(Constant.REGEXP_VALID_PASSWORD))
					return "error.user.password.not_valid::Password does not match policy (8 characters, at least one digit, one lower and one uppercase)";
				break;
			case "repeatPassword":
				throw new TrickException("error.validator.method.not_allowed", "Validator method does not allowed");
			case "firstName":
				if (candidate == null || !(candidate instanceof String))
					return "error.user.firstname.unsupported::First Name value is not supported";
				if (!((String) candidate).trim().matches(Constant.REGEXP_VALID_NAME))
					return "error.user.firstname.not_valid::First name is not valid";
				break;

			case "lastName":
				if (candidate == null || !(candidate instanceof String))
					return "error.user.lastname.unsupported::Last Name value is not supported";
				if (!((String) candidate).trim().matches(Constant.REGEXP_VALID_NAME))
					return "error.user.lastname.not_valid::Last name is not valid";
				break;

			case "email":
				if (candidate == null || !(candidate instanceof String))
					return "error.user.email.unsupported::Email value is not supported";
				if (!((String) candidate).matches(Constant.REGEXP_VALID_EMAIL))
					return "error.user.data.not_valid:email:Email format is not valid";
				break;
			case "locale":
				if (candidate == null || !(candidate instanceof String))
					return "error.user.locale.unsupported::Language value is not supported";
				if (!((String) candidate).matches(Constant.REGEXP_VALID_ALPHA_2))
					return "error.user.data.not_valid:locale:Language format is not valid";
				break;
		}
		return null;
	}
}
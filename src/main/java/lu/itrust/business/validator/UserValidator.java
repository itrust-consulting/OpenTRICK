/**
 * 
 */
package lu.itrust.business.validator;

import java.util.List;

import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.validator.field.ValidatorFieldImpl;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * @author oensuifudine
 * 
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
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.user.password.empty", "Password cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "repeatPassword", "error.user.password.empty", "Password cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "error.user.firstname.empty", "First name cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "error.user.lastname.empty", "Last name cannot be empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "error.user.email.empty", "Email cannot be empty");

		User user = (User) target;

		if (!errors.hasFieldErrors("login") && !user.getLogin().matches(Constant.REGEXP_VALID_NAME))
			errors.rejectValue("login", "errors.user.login.rejected", "Login is not valid");
		
		if (!errors.hasFieldErrors("password") && !user.getPassword().matches(Constant.REGEXP_VALID_PASSWORD))
			errors.rejectValue("password", "errors.user.password.rejected", "Password does not match policy (8 characters, one number, lower and upper case)");

		if (!errors.hasFieldErrors("repeatPassword") && !user.getRepeatPassword().equals(user.getPassword()))
			errors.rejectValue("repeatPassword", "errors.user.repeatPassword.not_same", "Passwords are not the same");

		if (!errors.hasFieldErrors("firstName") && !user.getFirstName().matches(Constant.REGEXP_VALID_NAME))
			errors.rejectValue("firstName", "errors.user.firstName.rejected", "First name is not valid");

		if (!errors.hasFieldErrors("lastName") && !user.getLastName().matches(Constant.REGEXP_VALID_NAME))
			errors.rejectValue("lastName", "errors.user.lastName.rejected", "Last name is not valid");

		if (!errors.hasFieldErrors("email") && !user.getEmail().matches(Constant.REGEXP_VALID_EMAIL))
			errors.rejectValue("email", "errors.user.email.rejected", "Email is not valid");
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate) {
		if (o == null || !supports(o.getClass()) || fieldName == null || fieldName.trim().isEmpty())
			return null;
		User user = (User) o;
		switch (fieldName) {
		case "login":
			if (candidate == null || !(candidate instanceof String))
				return "error.user.data.unsupported:login:Login value is not supported";
			if (!((String) candidate).matches(Constant.REGEXP_VALID_NAME))
				return "error.user.data.not_valid:login:Login is not valid";
			break;
		case "password":
			if (candidate == null || !(candidate instanceof String))
				return "error.user.data.unsupported:password:Password value is not supported";
			if (!((String) candidate).matches(Constant.REGEXP_VALID_PASSWORD))
				return "error.user.data.not_valid:password:Password does not match password policy";
			break;
		case "repeatPassword":
			
			if (candidate == null || !(candidate instanceof String))
				return "error.user.data.unsupported:password:Password value is not supported";
			if (!((String) candidate).equals(user.getPassword()))
				return "error.user.data.not_same:repeatedPassword:Repeated Password does not match Password";
			break;
		case "firstName":
			if (candidate == null || !(candidate instanceof String))
				return "error.user.data.unsupported:firstName:First Name value is not supported";
			if (!((String) candidate).matches(Constant.REGEXP_VALID_NAME))
				return "error.user.data.not_valid:firstName:First Name is not valid";
			break;
		
		case "lastName":
			if (candidate == null || !(candidate instanceof String))
				return "error.user.data.unsupported:lastName:Last Name value is not supported";
			if (!((String) candidate).matches(Constant.REGEXP_VALID_NAME))
				return "error.user.data.not_valid:lastName:Last Name is not valid";
			break;
		
		case "email":
			if (candidate == null || !(candidate instanceof String))
				return "error.user.data.unsupported:email:Email value is not supported";
			if (!((String) candidate).matches(Constant.REGEXP_VALID_EMAIL))
				return "error.user.data.not_valid:email:Email format is not valid";
			break;
		}
		return null;
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) {
		return validate(o, fieldName, candidate);
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate, List<Object> choose) {
		return validate(o, fieldName, candidate);
	}

	@Override
	public Class<?> supported() {
		return User.class;
	}
}
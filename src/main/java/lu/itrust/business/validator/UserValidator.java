/**
 * 
 */
package lu.itrust.business.validator;

import lu.itrust.business.TS.User;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author oensuifudine
 *
 */
public class UserValidator implements Validator {

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return User.class.isAssignableFrom(clazz);
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {
		
		User user = (User) target;
		
		if(user.getLogin()==null || user.getLogin().trim().isEmpty())
			errors.rejectValue("login", "errors.user.login.empty");
		
		if(user.getPassword()==null || user.getPassword().trim().isEmpty())
			errors.rejectValue("password", "errors.user.password.empty");
		else if(user.getPassword().trim().length()<6)
			errors.rejectValue("password", "errors.user.password.short");
		
		if(user.getFirstName()==null || user.getFirstName().trim().isEmpty())
			errors.rejectValue("firstName", "errors.user.firstName.empty");
		
		if(user.getLastName()==null || user.getLastName().trim().isEmpty())
			errors.rejectValue("lastName", "errors.user.lastName.empty");
		
		if(user.getCountry()==null || user.getCountry().trim().isEmpty())
			errors.rejectValue("country", "errors.user.country.empty");
		
		if(user.getEmail()==null || user.getEmail().trim().isEmpty())
			errors.rejectValue("email", "errors.user.email.empty");
	}

}

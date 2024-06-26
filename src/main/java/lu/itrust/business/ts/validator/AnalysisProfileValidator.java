package lu.itrust.business.ts.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import lu.itrust.business.ts.model.analysis.helper.AnalysisProfile;

/**
 * This class is responsible for validating AnalysisProfile objects.
 * It implements the Validator interface.
 */
public class AnalysisProfileValidator implements Validator {

	@Override
	public boolean supports(Class<?> arg0) {
		return AnalysisProfile.class.isAssignableFrom(arg0);
	}

	@Override
	public void validate(Object arg0, Errors arg1) {
		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "name", "error.analysis.profile.name.empty", "Name cannot be empty");
		AnalysisProfile analysisProfile = (AnalysisProfile) arg0;
		
		if(analysisProfile.getIdAnalysis() <1)
			arg1.rejectValue("idAnalysis", "error.analysis.not_found", "Analysis cannot be found");
	}

}

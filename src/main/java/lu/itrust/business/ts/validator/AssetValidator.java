package lu.itrust.business.ts.validator;

import java.util.Collection;
import java.util.regex.Pattern;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.asset.AssetType;
import lu.itrust.business.ts.validator.field.ValidatorFieldImpl;

/**
 * The AssetValidator class is responsible for validating assets.
 * It implements the Validator interface and extends the ValidatorFieldImpl class.
 * This class provides methods to validate various fields of an asset object.
 */
public class AssetValidator extends ValidatorFieldImpl implements Validator {

	private Pattern editableField = Pattern.compile("comment|hiddenComment|relatedName");
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.ts.validator.field.ValidatorField#isEditable(java.lang.String)
	 */
	@Override
	public boolean isEditable(String fieldName) {
		return editableField.matcher(fieldName).find();
	}

	/**
	 * Validates the given field name and candidate value.
	 * 
	 * @param fieldName  the name of the field to validate
	 * @param candidate  the value of the field to validate
	 * @return           an error message if validation fails, otherwise null
	 */
	@Override
	public String validate(String fieldName, Object candidate) {
		if (fieldName == null || fieldName.trim().isEmpty())
			return null;
		switch (fieldName) {
		case "name":
			if (candidate == null || !(candidate instanceof String))
				return "error.asset.name.unsupported::Name value is not supported";
			else if (candidate.toString().trim().isEmpty())
				return "error.asset.name.empty::Name cannot be empty";
			break;
		case "assetType":
			if (candidate == null || !(candidate instanceof AssetType))
				return "error.asset.assetType.unsupported::Asset Type is not valid";
			break;
		case "value":
			if (candidate == null || !(candidate instanceof Double))
				return "error.asset.value.unsupported::Value is not supported";
			else if ((Double) candidate < 0)
				return "error.asset.value.invalid::Value has to be 0 or greater";
			break;
		case "comment":
			if (candidate == null || !(candidate instanceof String))
				return "error.asset.comment.unsupported::Comment should be a string";
			break;
		case "hiddenComment":
			if (candidate == null || !(candidate instanceof String))
				return "error.asset.hidden_comment.unsupported::Hidden comment should be a string";
			break;
		}
		return null;
	}

	/**
	 * Validates the given object and returns a string representation of the validation result.
	 *
	 * @param o the object to be validated
	 * @param fieldName the name of the field being validated
	 * @param candidate the candidate value to be validated
	 * @param choose an array of objects to choose from during validation
	 * @return a string representation of the validation result
	 * @throws TrickException if an error occurs during validation
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) throws TrickException {
		return validate(o, fieldName, candidate);
	}

	/**
	 * Validates the given object and returns a string representation of the validation result.
	 *
	 * @param o the object to be validated
	 * @param fieldName the name of the field being validated
	 * @param candidate the candidate value to be validated
	 * @param choose a collection of objects to choose from during validation
	 * @return a string representation of the validation result
	 * @throws TrickException if an error occurs during validation
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate, Collection<Object> choose) throws TrickException {
		return validate(o, fieldName, candidate);
	}

	@Override
	public Class<?> supported() {
		return Asset.class;
	}

	/**
	 * Validates the given object and populates the provided Errors object with any validation errors.
	 *
	 * @param arg0 The object to be validated.
	 * @param arg1 The Errors object to store validation errors.
	 */
	@Override
	public void validate(Object arg0, Errors arg1) {
		
		Asset asset = (Asset) arg0;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "name", "error.asset.name.empty", "Name cannot be empty");
		
		if (!arg1.hasFieldErrors("assetType") && !(asset.getAssetType() instanceof AssetType))
			arg1.rejectValue("assetType", "error.asset.assetType.invalid", "Asset Type is not valid");

		if (!arg1.hasFieldErrors("value") && asset.getValue() <= 0)
			arg1.rejectValue("name", "error.asset.value.invalid", "Value has to be 0 or greater");
	}

}

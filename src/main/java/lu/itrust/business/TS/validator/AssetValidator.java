package lu.itrust.business.TS.validator;

import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.validator.field.ValidatorFieldImpl;

public class AssetValidator extends ValidatorFieldImpl implements Validator {

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
			else if (!candidate.toString().matches(Constant.REGEXP_VALID_NAME))
				return "error.asset.name.invalid::Name is not valid";
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
		return Asset.class;
	}

	@Override
	public void validate(Object arg0, Errors arg1) {
		ValidationUtils.rejectIfEmptyOrWhitespace(arg1, "name", "error.asset.name.empty", "Name cannot be empty");

		Asset asset = (Asset) arg0;
		if (!arg1.hasFieldErrors("name") && !asset.getName().matches(Constant.REGEXP_VALID_NAME))
			arg1.rejectValue("name", "error.asset.name.invalid", "Name is not valid");

		if (!arg1.hasFieldErrors("assetType") && !(asset.getAssetType() instanceof AssetType))
			arg1.rejectValue("assetType", "error.asset.assetType.invalid", "Asset Type is not valid");

		if (!arg1.hasFieldErrors("value") && asset.getValue() <= 0)
			arg1.rejectValue("name", "error.asset.value.invalid", "Value has to be 0 or greater");
	}

}

/**
 * 
 */
package lu.itrust.business.validator;

import java.util.List;

import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.ParameterType;
import lu.itrust.business.validator.field.ValidatorFieldImpl;

/**
 * @author eomar
 * 
 */
public class MaturityParameterValidator extends ValidatorFieldImpl {

	protected static final String SMLLevel = "SMLLevel";
	protected static final String SMLLevel0 = "SMLLevel0";
	protected static final String SMLLevel1 = "SMLLevel1";
	protected static final String SMLLevel2 = "SMLLevel2";
	protected static final String SMLLevel3 = "SMLLevel3";
	protected static final String SMLLevel4 = "SMLLevel4";
	protected static final String SMLLevel5 = "SMLLevel5";
	protected static final String TYPE = "type";
	protected static final String DESCRIPTION = "description";
	protected static final String CATEGORY = "category";
	private static final String ERROR_PARAMETER_TYPE_NULL = "error.parameter.type.null::Type cannot be empty";
	private static final String ERROR_UNSUPPORTED_DATA_VALUE = "error.unsupported.data::Value is not supported";
	private static final String ERROR_PARAMETER_DESC_NULL_OR_EMPTY = "error.parameter.null::Description Cannot be empty";
	private static final String ERROR_PARAMETER_CAT_NULL_OR_EMPTY = "error.parameter.null::Category Cannot be empty";
	private static final String ERROR_PARAMETER_SMLLEVEL_NULL = "error.parameter.null::SMLLevel cannot be null";
	private static final String ERROR_PARAMETER_SMLLEVEL_INVALID = "error.parameter.null::SMLLevel value has to be: 0 <= VALUE <= 100!";

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.Validator#validate(java.lang.Object, java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public String validate(Object o, String fieldName, Object candidate) {
		if (!supports(o.getClass()))
			return null;
		switch (fieldName) {
			case DESCRIPTION:
				if (!(candidate instanceof String))
					return ERROR_UNSUPPORTED_DATA_VALUE;

				String description = (String) candidate;
				if (description == null || description.trim().isEmpty())
					return ERROR_PARAMETER_DESC_NULL_OR_EMPTY;
				break;
			case CATEGORY:
				if (!(candidate instanceof String))
					return ERROR_UNSUPPORTED_DATA_VALUE;

				String category = (String) candidate;
				if (category == null || category.trim().isEmpty())
					return ERROR_PARAMETER_CAT_NULL_OR_EMPTY;
				break;
			case TYPE:
				if (candidate == null)
					return ERROR_PARAMETER_TYPE_NULL;
				else if (!(candidate instanceof ParameterType))
					return ERROR_UNSUPPORTED_DATA_VALUE;
				break;
			case SMLLevel:
				if (candidate == null)
					return ERROR_PARAMETER_SMLLEVEL_NULL;
				else if (!(candidate instanceof Integer))
					return ERROR_UNSUPPORTED_DATA_VALUE;
				break;
			case SMLLevel0:
			case SMLLevel1:
			case SMLLevel2:
			case SMLLevel3:
			case SMLLevel4:
			case SMLLevel5:
				if (candidate == null || !(candidate instanceof Double))
					return ERROR_UNSUPPORTED_DATA_VALUE;
				Double value = (double) candidate;
				if (value < 0 || value > 100)
					return ERROR_PARAMETER_SMLLEVEL_INVALID;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.Validator#supported()
	 */
	@Override
	public Class<?> supported() {
		return Parameter.class;
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) {
		return validate(choose, fieldName, candidate);
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate, List<Object> choose) {
		return validate(choose, fieldName, candidate);
	}

}

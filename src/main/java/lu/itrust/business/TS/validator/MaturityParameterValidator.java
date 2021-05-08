/**
 * 
 */
package lu.itrust.business.TS.validator;

import java.util.Collection;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.parameter.impl.SimpleParameter;
import lu.itrust.business.TS.model.parameter.type.impl.ParameterType;
import lu.itrust.business.TS.validator.field.ValidatorFieldImpl;

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
	private static final String ERROR_PARAMETER_TYPE_NULL = "error.maturity_parameter.type.null::Type cannot be empty";
	private static final String ERROR_UNSUPPORTED_DATA_VALUE = "error.maturity_parameter.%s.unsupporte::%s value is not supported";
	private static final String ERROR_PARAMETER_DESC_NULL_OR_EMPTY = "error.maturity_parameter.description.null::Description cannot be empty";
	private static final String ERROR_PARAMETER_CAT_NULL_OR_EMPTY = "error.maturity_parameter.category.null::Category cannot be empty";
	private static final String ERROR_PARAMETER_SMLLEVEL_NULL = "error.maturity_parameter.sml_level_sml.null::SML level cannot be empty";
	private static final String ERROR_PARAMETER_SMLLEVEL_INVALID = "error.maturity_parameter.sml_level.invalid:%d:SML level %d value has to be between 0 and 100!";

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.validator.Validator#validate(java.lang.Object,
	 * java.lang.String, java.lang.Object)
	 */
	@Override
	public String validate(String fieldName, Object candidate) {
		int level = -1;
		switch (fieldName) {
		case DESCRIPTION:
			if (!(candidate instanceof String))
				return String.format(ERROR_UNSUPPORTED_DATA_VALUE, "description", "Description");
			String description = (String) candidate;
			if (description == null || description.trim().isEmpty())
				return ERROR_PARAMETER_DESC_NULL_OR_EMPTY;
			break;
		case CATEGORY:
			if (!(candidate instanceof String))
				return String.format(ERROR_UNSUPPORTED_DATA_VALUE, "category", "Category");

			String category = (String) candidate;
			if (category == null || category.trim().isEmpty())
				return ERROR_PARAMETER_CAT_NULL_OR_EMPTY;
			break;
		case TYPE:
			if (candidate == null)
				return ERROR_PARAMETER_TYPE_NULL;
			else if (!(candidate instanceof ParameterType))
				return String.format(ERROR_UNSUPPORTED_DATA_VALUE, TYPE, "Type");
			break;
		case SMLLevel:
			if (candidate == null)
				return ERROR_PARAMETER_SMLLEVEL_NULL;
			else if (!(candidate instanceof Integer))
				return String.format(ERROR_UNSUPPORTED_DATA_VALUE, "sml_level_sml", "SML level");
			break;
		case SMLLevel0:
			level = 0;
		case SMLLevel1:
			if (level == -1)
				level = 1;
		case SMLLevel2:
			if (level == -1)
				level = 2;
		case SMLLevel3:
			if (level == -1)
				level = 3;
		case SMLLevel4:
			if (level == -1)
				level = 4;
		case SMLLevel5:
			if (level == -1)
				level = 5;
			if (candidate == null || !(candidate instanceof Double))
				return String.format(ERROR_UNSUPPORTED_DATA_VALUE, "sml_level", String.format("SML Level %d", level));
			Double value = (double) candidate;
			if (value < 0 || value > 100)
				return String.format(ERROR_PARAMETER_SMLLEVEL_INVALID, level, level);
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
		return SimpleParameter.class;
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate, Object[] choose) throws TrickException {
		return validate(choose, fieldName, candidate);
	}

	@Override
	public String validate(Object o, String fieldName, Object candidate, Collection<Object> choose) throws TrickException {
		return validate(choose, fieldName, candidate);
	}

}

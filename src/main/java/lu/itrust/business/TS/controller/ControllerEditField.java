package lu.itrust.business.TS.controller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.component.FieldEditor;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.hbm.DAOHibernate;
import lu.itrust.business.TS.database.service.ServiceActionPlan;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceHistory;
import lu.itrust.business.TS.database.service.ServiceItemInformation;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServiceParameter;
import lu.itrust.business.TS.database.service.ServicePhase;
import lu.itrust.business.TS.database.service.ServiceRiskInformation;
import lu.itrust.business.TS.database.service.ServiceRiskRegister;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.assessment.helper.AssessmentManager;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.cssf.RiskRegisterItem;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.history.History;
import lu.itrust.business.TS.model.iteminformation.ItemInformation;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.model.parameter.MaturityParameter;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.parameter.helper.ParameterManager;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.measure.AssetMeasure;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.MeasureAssetValue;
import lu.itrust.business.TS.model.standard.measure.MeasureProperties;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;
import lu.itrust.business.TS.validator.AssessmentValidator;
import lu.itrust.business.TS.validator.ExtendedParameterValidator;
import lu.itrust.business.TS.validator.HistoryValidator;
import lu.itrust.business.TS.validator.MaturityParameterValidator;
import lu.itrust.business.TS.validator.MeasureValidator;
import lu.itrust.business.TS.validator.ParameterValidator;
import lu.itrust.business.TS.validator.RiskInformationValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;

import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * ControllerEditField.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl. :
 * @version
 * @since Feb 4, 2014
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@Controller
@RequestMapping("/Analysis/EditField")
public class ControllerEditField {

	@Autowired
	private ServiceItemInformation serviceItemInformation;

	@Autowired
	private ServiceParameter serviceParameter;

	@Autowired
	private ServiceAssessment serviceAssessment;

	@Autowired
	private ServiceHistory serviceHistory;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private ServiceMeasure serviceMeasure;

	@Autowired
	private ServiceActionPlan serviceActionPlan;

	@Autowired
	private ServiceRiskInformation serviceRiskInformation;

	@Autowired
	private AssessmentManager assessmentManager;

	@Autowired
	private ServicePhase servicePhase;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ServiceRiskRegister serviceRiskRegister;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private ServiceAssetType serviceAssetType;

	/**
	 * itemInformation: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/ItemInformation/{elementID}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'ItemInformation', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String itemInformation(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, Locale locale, HttpSession session, Principal principal)
			throws Exception {
		try {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());

			if (!serviceAnalysis.exists(id))
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", cutomLocale != null ? cutomLocale : locale));

			// get item information object from id
			ItemInformation itemInformation = serviceItemInformation.getFromAnalysisById(id, elementID);
			if (itemInformation == null)
				return JsonMessage.Error(messageSource.getMessage("error.item_information.not_found", null, "Item information cannot be found", cutomLocale != null ? cutomLocale
						: locale));

			// initialise field
			Field field = itemInformation.getClass().getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);

			// set field with new data
			if (SetFieldData(field, itemInformation, fieldEditor, null)) {

				// update iteminformation
				serviceItemInformation.saveOrUpdate(itemInformation);

				// return success message
				return JsonMessage.Success(messageSource.getMessage("success.item_information.updated", null, "Item information was successfully updated",
						cutomLocale != null ? cutomLocale : locale));
			} else

				// return error message
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", cutomLocale != null ? cutomLocale : locale));
		} catch (NumberFormatException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (IllegalAccessException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (InvocationTargetException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (TrickException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		}
		catch (RuntimeException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (Exception e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		}
	}

	@RequestMapping(value = "/Asset/{elementID}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String asset(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {
		try {

			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());

			// retrieve measure
			Asset asset = serviceAsset.getFromAnalysisById(idAnalysis, elementID);
			if (asset == null)
				return JsonMessage.Error(messageSource.getMessage("error.asset.not_found", null, "Asset cannot be found", cutomLocale != null ? cutomLocale : locale));
			// set field

			if (!fieldEditor.getFieldName().equalsIgnoreCase("assetType")) {

				Field field = asset.getClass().getDeclaredField(fieldEditor.getFieldName());
				// check if field is a phase

				field.setAccessible(true);

				field.set(asset, fieldEditor.getValue());
			} else {
				AssetType assetType = serviceAssetType.getByName(fieldEditor.getValue().toString());
				if (assetType != null)
					asset.setAssetType(assetType);
				else
					return JsonMessage
							.Error(messageSource.getMessage("error.asset_type.not_found", null, "Asset type cannot be found", cutomLocale != null ? cutomLocale : locale));
			}

			// update measure
			serviceAsset.saveOrUpdate(asset);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.asset.updated", null, "Asset was successfully updated", cutomLocale != null ? cutomLocale : locale));

		} catch (TrickException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (Exception e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.edit.save.field", null, "Data cannot be saved", cutomLocale != null ? cutomLocale : locale));
		}
	}

	/**
	 * parameter: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Parameter/{elementID}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Parameter', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String parameter(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, Locale locale, HttpSession session, Principal principal)
			throws Exception {

		try {

			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());

			if (!serviceAnalysis.exists(id))
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", cutomLocale != null ? cutomLocale : locale));

			// get parameter object
			Parameter parameter = serviceParameter.getFromAnalysisById(id, elementID);
			if (parameter == null)
				return JsonMessage.Error(messageSource.getMessage("error.parameter.not_found", null, "Parameter cannot be found", cutomLocale != null ? cutomLocale : locale));

			// validate parameter
			ValidatorField validator = serviceDataValidation.findByClass(parameter.getClass());
			if (validator == null)
				serviceDataValidation.register(new ParameterValidator());

			// retireve value
			Object value = FieldValue(fieldEditor, null);

			// validate value
			String error = serviceDataValidation.validate(parameter, fieldEditor.getFieldName(), value);
			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, cutomLocale != null ? cutomLocale : locale));
			switch (parameter.getType().getLabel()) {
			case Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME:
			case Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_LEVEL_PER_SML_NAME:
			case Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME:
				if (((double) value) < 0 || ((double) value) > 100)
					return JsonMessage.Error(messageSource.getMessage("error.parameter.value.out_of_bound", new Object[] { value },
							String.format("Invalid input: value (%f) should be between 0 and 100", value), cutomLocale != null ? cutomLocale : locale));
				break;
			case Constant.PARAMETERTYPE_TYPE_SINGLE_NAME:
				if (parameter.getDescription().equals(Constant.PARAMETER_LIFETIME_DEFAULT)) {
					if (((double) value) <= 0)
						return JsonMessage.Error(messageSource.getMessage("error.edit.parameter.default_lifetime", null, "Default lifetime has to be > 0",
								cutomLocale != null ? cutomLocale : locale));
				} else if (parameter.getDescription().equals(Constant.PARAMETER_MAX_RRF) || parameter.getDescription().equals(Constant.SOA_THRESHOLD)) {
					if (((double) value) < 0 || ((double) value) > 100)
						return JsonMessage.Error(messageSource.getMessage("error.parameter.value.out_of_bound", new Object[] { value },
								String.format("Invalid input: value (%f) should be between 0 and 100", value), cutomLocale != null ? cutomLocale : locale));
				}
				break;
			}
			// create field
			Field field = parameter.getClass().getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);
			// set field data
			if (SetFieldData(field, parameter, fieldEditor, null)) {
				// update field
				serviceParameter.saveOrUpdate(parameter);
				// return success message
				return JsonMessage.Success(messageSource.getMessage("success.parameter.updated", null, "Parameter was successfully updated", cutomLocale != null ? cutomLocale
						: locale));
			} else

				// return error message
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", cutomLocale != null ? cutomLocale : locale));
		} catch (NoSuchFieldException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (SecurityException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (NumberFormatException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (IllegalArgumentException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (IllegalAccessException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (ParseException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		}

		catch (TrickException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		}

		catch (Exception e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		}
	}

	/**
	 * extendedParameter: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/ExtendedParameter/{elementID}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Parameter', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String extendedParameter(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal)
			throws Exception {
		try {

			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());

			if (!serviceAnalysis.exists(id))
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", cutomLocale != null ? cutomLocale : locale));

			// retrieve parameter
			ExtendedParameter parameter = (ExtendedParameter) serviceParameter.getFromAnalysisById(id, elementID);
			if (parameter == null)
				return JsonMessage.Error(messageSource.getMessage("error.parameter.not_found", null, "Parameter cannot be found", cutomLocale != null ? cutomLocale : locale));

			String acronym = parameter.getAcronym();

			// set validator and validate parameter
			if (!serviceDataValidation.isRegistred(parameter.getClass()))
				serviceDataValidation.register(new ExtendedParameterValidator());

			// retireve value
			Object value = FieldValue(fieldEditor, null);

			// validate
			String error = serviceDataValidation.validate(parameter, fieldEditor.getFieldName(), value);

			// return error validation
			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, cutomLocale != null ? cutomLocale : locale));

			// set field
			Field field = null;
			if ("value id type description".contains(fieldEditor.getFieldName()))
				field = parameter.getClass().getSuperclass().getDeclaredField(fieldEditor.getFieldName());
			else
				field = parameter.getClass().getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);

			// set field data
			if (SetFieldData(field, parameter, fieldEditor, null)) {
				if ("value".equals(fieldEditor.getFieldName()) && Constant.PARAMETERTYPE_TYPE_IMPACT_NAME.equalsIgnoreCase(parameter.getType().getLabel()))
					parameter.setValue(parameter.getValue() * 1000);

				if (field.getName().equals("acronym")) {
					try {
						assessmentManager.UpdateAcronym(id, parameter, acronym);
					} catch (Exception e) {
						e.printStackTrace();
						return JsonMessage.Error(messageSource.getMessage("error.assessment.acronym.updated", new String[] { acronym, parameter.getAcronym() },
								"Assessment acronym (" + acronym + ") cannot be updated to (" + parameter.getAcronym() + ")", cutomLocale != null ? cutomLocale : locale));
					}
				}
				// update field
				serviceParameter.saveOrUpdate(parameter);

				// retrieve parameters
				List<ExtendedParameter> parameters = serviceParameter.getAllExtendedFromAnalysisAndType(id, parameter.getType());

				// update impact value
				ParameterManager.ComputeImpactValue(parameters);

				// update parameters
				serviceParameter.saveOrUpdate(parameters);

				// return success message
				return JsonMessage.Success(messageSource.getMessage("success.extendedParameter.update", null, "Parameter was successfully update",
						cutomLocale != null ? cutomLocale : locale));
			} else

				// return error message
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", cutomLocale != null ? cutomLocale : locale));
		} catch (NoSuchFieldException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (SecurityException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (NumberFormatException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (IllegalArgumentException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (IllegalAccessException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (ParseException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (TrickException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (Exception e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		}
	}

	@RequestMapping(value = "/MaturityParameter/{elementID}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Parameter', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String maturityparameter(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, Locale locale, HttpSession session, Principal principal)
			throws Exception {

		try {

			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());

			if (!serviceAnalysis.exists(id))
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", cutomLocale != null ? cutomLocale : locale));

			// get parameter object
			MaturityParameter parameter = (MaturityParameter) serviceParameter.getFromAnalysisById(id, elementID);
			if (parameter == null)
				return JsonMessage.Error(messageSource.getMessage("error.parameter.not_found", null, "Parameter cannot be found", cutomLocale != null ? cutomLocale : locale));

			// validate parameter
			ValidatorField validator = serviceDataValidation.findByClass(parameter.getClass());
			if (validator == null)
				serviceDataValidation.register(new MaturityParameterValidator());

			// retireve value
			Object value = FieldValue(fieldEditor, null);

			// validate value
			String error = serviceDataValidation.validate(parameter, fieldEditor.getFieldName(), value);
			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, cutomLocale != null ? cutomLocale : locale));

			// create field
			Field field = parameter.getClass().getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);

			// set value /100 to save as values between 0 and 1
			Double val = Double.valueOf(((String) fieldEditor.getValue())) / 100;

			fieldEditor.setValue(String.valueOf(val));

			// set field data
			if (SetFieldData(field, parameter, fieldEditor, null)) {

				// update field

				serviceParameter.saveOrUpdate(parameter);

				// return success message
				return JsonMessage.Success(messageSource.getMessage("success.parameter.updated", null, "Parameter was successfully updated", cutomLocale != null ? cutomLocale
						: locale));
			} else

				// return error message
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", cutomLocale != null ? cutomLocale : locale));
		} catch (NoSuchFieldException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (SecurityException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (NumberFormatException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (IllegalArgumentException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (IllegalAccessException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (ParseException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (TrickException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (Exception e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		}
	}

	/**
	 * assessment: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Assessment/{elementID}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Assessment', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String assessment(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal)
			throws Exception {

		try {

			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());

			if (!serviceAnalysis.exists(id))
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", cutomLocale != null ? cutomLocale : locale));

			// retrieve assessment
			Assessment assessment = serviceAssessment.getFromAnalysisById(id, elementID);
			if (assessment == null)
				return JsonMessage.Error(messageSource.getMessage("error.assessment.not_found", null, "Assessment cannot be found", cutomLocale != null ? cutomLocale : locale));

			// set validator
			if (!serviceDataValidation.isRegistred(assessment.getClass()))
				serviceDataValidation.register(new AssessmentValidator());

			// retrieve all acronyms of impact and likelihood
			List<String> chooses = null;
			if ("impactRep,impactOp,impactLeg,impactFin".contains(fieldEditor.getFieldName())) {
				try {
					chooses = serviceParameter.getExtendedParameterAcronymsFromAnalysisByType(id, Constant.PARAMETERTYPE_TYPE_IMPACT_NAME);
					if (!chooses.contains(fieldEditor.getValue()))
						fieldEditor.setValue(Double.toString(NumberFormat.getInstance(Locale.FRANCE).parse(fieldEditor.getValue().toString()).doubleValue() * 1000));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if ("likelihood".equals(fieldEditor.getFieldName())) {
				chooses = serviceParameter.getExtendedParameterAcronymsFromAnalysisByType(id, Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME);
				if (fieldEditor.getValue().equals("NA"))
					fieldEditor.setValue("0");
			}

			// get value
			Object value = FieldValue(fieldEditor, null);

			// validate new value
			String error = serviceDataValidation.validate(assessment, fieldEditor.getFieldName(), value, chooses != null ? chooses.toArray() : null);
			if (error != null)

				// return error message
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, cutomLocale != null ? cutomLocale : locale));

			// init field
			Field field = assessment.getClass().getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);

			// set data to field
			if (!SetFieldData(field, assessment, fieldEditor, null))

				// return error message
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", cutomLocale != null ? cutomLocale : locale));

			// retrieve parameters
			Map<String, ExtendedParameter> parameters = new LinkedHashMap<>();

			// parse parameters
			for (ExtendedParameter parameter : serviceParameter.getAllExtendedFromAnalysis(id))
				// add parameter into map
				parameters.put(parameter.getAcronym(), parameter);

			// compute new ALE
			AssessmentManager.ComputeAlE(assessment, parameters);

			// update assessment
			serviceAssessment.saveOrUpdate(assessment);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.assessment.updated", null, "Assessment was successfully updated", cutomLocale != null ? cutomLocale
					: locale));
		} catch (TrickException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (Exception e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		}

	}

	/**
	 * history: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/History/{elementID}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'History', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String history(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, Locale locale, HttpSession session, Principal principal)
			throws Exception {

		try {

			// retrieve analysis
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());

			// retireve history object
			History history = serviceHistory.getFromAnalysisById(id, elementID);
			if (history == null)
				return JsonMessage.Error(messageSource.getMessage("error.history.not_found", null, "History cannot be found", cutomLocale != null ? cutomLocale : locale));

			// get validator
			if (!serviceDataValidation.isRegistred(history.getClass()))
				serviceDataValidation.register(new HistoryValidator());

			// get new value
			Object value = FieldValue(fieldEditor, null);

			// validate
			String error = serviceDataValidation.validate(history, fieldEditor.getFieldName(), value);

			// return errors on validation fail
			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, cutomLocale != null ? cutomLocale : locale));

			// set field
			Field field = history.getClass().getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);

			// set field data
			if (SetFieldData(field, history, fieldEditor, null)) {

				// update history
				serviceHistory.saveOrUpdate(history);

				// return success message
				return JsonMessage.Success(messageSource
						.getMessage("success.history.updated", null, "History was successfully updated", cutomLocale != null ? cutomLocale : locale));
			} else

				// return error rmessage
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", cutomLocale != null ? cutomLocale : locale));
		} catch (NoSuchFieldException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error rmessage
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (SecurityException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error rmessage
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (NumberFormatException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error rmessage
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.format.number", null, "Number expected", cutomLocale != null ? cutomLocale : locale));
		} catch (IllegalArgumentException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error rmessage
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (IllegalAccessException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error rmessage
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (ParseException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error rmessage
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.format.date", null, "Date expected", cutomLocale != null ? cutomLocale : locale));
		} catch (TrickException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (Exception e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error rmessage
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		}
	}

	/**
	 * scenario: <br>
	 * Description
	 * 
	 * @param elementID
	 * @param fieldEditor
	 * @param session
	 * @param locale
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Scenario/{elementID}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String scenario(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal)
			throws Exception {
		try {

			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());

			// retrieve measure
			Scenario scenario = serviceScenario.getFromAnalysisById(idAnalysis, elementID);
			if (scenario == null)
				return JsonMessage.Error(messageSource.getMessage("error.scenario.not_found", null, "Scenario cannot be found", cutomLocale != null ? cutomLocale : locale));

			// set field

			Field field = ControllerEditField.FindField(Scenario.class, fieldEditor.getFieldName());
			// means that field belongs to the Measure class

			if (field != null) {
				// check if field is a phase

				field.setAccessible(true);

				if (field.getName().equals("preventive") || field.getName().equals("detective") || field.getName().equals("limitative") || field.getName().equals("corrective")) {
					if (!(fieldEditor.getValue() instanceof Integer))
						fieldEditor.setValue(Double.valueOf(String.valueOf(fieldEditor.getValue())));
				}

				field.set(scenario, fieldEditor.getValue());

				// update measure
				serviceScenario.saveOrUpdate(scenario);

			} else {

				if (Scenario.isCategoryKey(fieldEditor.getFieldName()))
					scenario.setCategoryValue(fieldEditor.getFieldName(), (Integer) fieldEditor.getValue());
				else {
					AssetTypeValue assetData = null;
					for (AssetTypeValue assetTypeValue : scenario.getAssetTypeValues()) {
						if (fieldEditor.getFieldName().equals(assetTypeValue.getAssetType().getType())) {
							assetData = assetTypeValue;
							break;
						}
					}
					if (assetData != null)
						assetData.setValue((Integer) fieldEditor.getValue());
					else
						return null;
				}
				serviceScenario.saveOrUpdate(scenario);

			}
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.scenario.updated", null, "Scenario was successfully updated", cutomLocale != null ? cutomLocale : locale));

		} catch (TrickException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (Exception e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.edit.save.field", null, "Data cannot be saved", cutomLocale != null ? cutomLocale : locale));
		}
	}

	/**
	 * measure: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Measure/{elementID}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String measure(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal)
			throws Exception {

		try {

			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());

			// retrieve measure
			Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);
			if (measure == null)
				return JsonMessage.Error(messageSource.getMessage("error.measure.not_found", null, "Measure cannot be found", cutomLocale != null ? cutomLocale : locale));

			// set field

			Field field = ControllerEditField.FindField(Measure.class, fieldEditor.getFieldName());

			// means that field belongs to the Measure class

			if (field != null) {
				// check if field is a phase
				field.setAccessible(true);

				if (fieldEditor.getFieldName().equals("phase")) {

					// retireve phase
					Integer number = null;
					number = (Integer) FieldValue(fieldEditor, null);
					if (number == null)
						return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", cutomLocale != null ? cutomLocale : locale));
					Phase phase = servicePhase.getFromAnalysisByPhaseNumber(idAnalysis, number);
					if (phase == null)
						return JsonMessage.Error(messageSource.getMessage("error.phase.not_found", null, "Phase cannot be found", cutomLocale != null ? cutomLocale : locale));

					// set new phase number
					measure.setPhase(phase);

					// set field data
				} else {

					Object value = FieldValue(fieldEditor, null);
					if (value == null)
						return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", cutomLocale != null ? cutomLocale : locale));

					// get validator
					if (!serviceDataValidation.isRegistred(Measure.class))
						serviceDataValidation.register(new MeasureValidator());

					String error = serviceDataValidation.validate(measure, fieldEditor.getFieldName(), value);
					if (error != null)
						return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, cutomLocale != null ? cutomLocale : locale));

					if (fieldEditor.getFieldName().equals("implementationRate"))
						if ((Double) value < 0. || (Double) value > 100.)
							return JsonMessage.Error(messageSource.getMessage("error.edit.implementationrate.field", null, "Implementation rate needs to be >= 0 and <= 100 !",
									cutomLocale != null ? cutomLocale : locale));
						else
							field.set(measure, value);
					else
						field.set(measure, value);

				}

				// retrieve parameters
				Analysis analysis = serviceAnalysis.get(idAnalysis);

				if (fieldEditor.getFieldName().equals("investment"))
					measure.setInvestment(measure.getInvestment() * 1000);

				if (fieldEditor.getFieldName().equals("recurrentInvestment"))
					measure.setRecurrentInvestment(measure.getRecurrentInvestment() * 1000);

				// compute new cost
				Measure.ComputeCost(measure, analysis);

				// update measure
				serviceMeasure.saveOrUpdate(measure);

			} else {

				if (measure instanceof NormalMeasure) {

					NormalMeasure normalMeasure = (NormalMeasure) measure;

					field = ControllerEditField.FindField(NormalMeasure.class, fieldEditor.getFieldName());

					// means that field belongs to either measure or
					// normalmeasure

					if (field != null) {

						field.setAccessible(true);

						// check if field is a phase
						if (!SetFieldData(field, measure, fieldEditor, null))
							return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", cutomLocale != null ? cutomLocale : locale));

						// update measure
						serviceMeasure.saveOrUpdate(measure);

					} else {

						field = ControllerEditField.FindField(MeasureProperties.class, fieldEditor.getFieldName());

						if (field != null) {
							field.setAccessible(true);
							MeasureProperties properties = DAOHibernate.Initialise(normalMeasure.getMeasurePropertyList());
							if (field.getName().equals("preventive") || field.getName().equals("detective") || field.getName().equals("limitative")
									|| field.getName().equals("corrective")) {
								if (!(fieldEditor.getValue() instanceof Integer))
									fieldEditor.setValue(Double.valueOf(String.valueOf(fieldEditor.getValue())));
							}
							field.set(properties, fieldEditor.getValue());
							normalMeasure.setMeasurePropertyList(properties);
						} else if (MeasureProperties.isCategoryKey(fieldEditor.getFieldName()))
							normalMeasure.getMeasurePropertyList().setCategoryValue(fieldEditor.getFieldName(), (Integer) fieldEditor.getValue());
						else {
							AssetTypeValue assetData = null;
							for (AssetTypeValue assetTypeValue : normalMeasure.getAssetTypeValues()) {
								if (fieldEditor.getFieldName().equals(assetTypeValue.getAssetType().getType())) {
									assetData = assetTypeValue;
									break;
								}
							}
							if (assetData != null)
								assetData.setValue((Integer) fieldEditor.getValue());
							else
								return null;
						}
						serviceMeasure.saveOrUpdate(normalMeasure);

					}
				} else if (measure instanceof AssetMeasure) {
					AssetMeasure assetMeasure = (AssetMeasure) measure;

					field = ControllerEditField.FindField(AssetMeasure.class, fieldEditor.getFieldName());

					// means that field belongs to either measure or
					// normalmeasure

					if (field != null) {

						field.setAccessible(true);

						// check if field is a phase
						if (!SetFieldData(field, measure, fieldEditor, null))
							return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", cutomLocale != null ? cutomLocale : locale));

						// update measure
						serviceMeasure.saveOrUpdate(measure);

					} else {

						field = ControllerEditField.FindField(MeasureProperties.class, fieldEditor.getFieldName());

						if (field != null) {
							field.setAccessible(true);
							MeasureProperties properties = DAOHibernate.Initialise(assetMeasure.getMeasurePropertyList());
							if (field.getName().equals("preventive") || field.getName().equals("detective") || field.getName().equals("limitative")
									|| field.getName().equals("corrective")) {
								if (!(fieldEditor.getValue() instanceof Integer))
									fieldEditor.setValue(Double.valueOf(String.valueOf(fieldEditor.getValue())));
							}
							field.set(properties, fieldEditor.getValue());
							assetMeasure.setMeasurePropertyList(properties);
						} else if (MeasureProperties.isCategoryKey(fieldEditor.getFieldName()))
							assetMeasure.getMeasurePropertyList().setCategoryValue(fieldEditor.getFieldName(), (Integer) fieldEditor.getValue());
						else {
							MeasureAssetValue assetData = null;
							for (MeasureAssetValue assetValue : assetMeasure.getMeasureAssetValues()) {
								if (fieldEditor.getFieldName().equals(assetValue.getAsset().getName())) {
									assetData = assetValue;
									break;
								}
							}
							if (assetData != null)
								assetData.setValue((Integer) fieldEditor.getValue());
							else
								return null;
						}
						serviceMeasure.saveOrUpdate(assetMeasure);

					}
				}
			}
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.measure.updated", null, "Measure was successfully updated", cutomLocale != null ? cutomLocale : locale));

		} catch (TrickException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (Exception e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.edit.save.field", null, "Data cannot be saved", cutomLocale != null ? cutomLocale : locale));
		}
	}

	/**
	 * measure: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/SOA/{elementID}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String soa(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {

		try {

			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
			// retrieve measure
			NormalMeasure measure = (NormalMeasure) serviceMeasure.getFromAnalysisById(idAnalysis, elementID);

			if (measure == null)
				return JsonMessage.Error(messageSource.getMessage("error.measure.not_found", null, "Measure cannot be found", cutomLocale != null ? cutomLocale : locale));

			// set field

			// System.out.println("Fildname: " + fieldEditor.getFieldName());

			MeasureProperties mesprep = DAOHibernate.Initialise(measure.getMeasurePropertyList());
			Field field = mesprep.getClass().getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);

			// check if field is a phase
			if (!SetFieldData(field, mesprep, fieldEditor, null))
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", cutomLocale != null ? cutomLocale : locale));

			measure.setMeasurePropertyList(mesprep);

			// update measure
			serviceMeasure.saveOrUpdate(measure);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.measure.updated", null, "Measure was successfully updated", cutomLocale != null ? cutomLocale : locale));

		} catch (TrickException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (Exception e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.edit.save.field", null, "Data cannot be saved", cutomLocale != null ? cutomLocale : locale));
		}
	}

	/**
	 * maturityMeasure: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/MaturityMeasure/{elementID}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String maturityMeasure(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal)
			throws Exception {

		try {

			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
			// retrieve measure
			Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);
			if (measure == null)
				return JsonMessage.Error(messageSource.getMessage("error.measure.not_found", null, "Measure cannot be found", cutomLocale != null ? cutomLocale : locale));

			// check if field is implementationrate
			if (fieldEditor.getFieldName().equalsIgnoreCase("implementationRate")) {

				// retrieve parameters
				List<Parameter> parameters = serviceParameter.getAllFromAnalysisByType(idAnalysis, Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME);

				// retrieve single parameters
				Analysis analysis = serviceAnalysis.get(idAnalysis);

				// get value
				double value = Double.parseDouble(fieldEditor.getValue().toString());

				// parse parameters
				for (Parameter parameter : parameters) {

					// find the parameter
					if (Math.abs(parameter.getValue() - value) < 1e-5) {

						// set new implementation rate
						measure.setImplementationRate(parameter);

						// recompute cost
						Measure.ComputeCost(measure, analysis);

						// update measure
						serviceMeasure.saveOrUpdate(measure);

						// return success message
						return JsonMessage.Success(messageSource.getMessage("success.measure.updated", null, "Measure was successfully updated", cutomLocale != null ? cutomLocale
								: locale));
					}
				}

				// return error message
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", cutomLocale != null ? cutomLocale : locale));
			} else

				// update as if it would be a normal measure
				return measure(elementID, fieldEditor, session, cutomLocale != null ? cutomLocale : locale, principal);
		} catch (TrickException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (Exception e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		}
	}

	/**
	 * actionplanentry: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param session
	 * @param locale
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/ActionPlanEntry/{elementID}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'ActionPlanEntry', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String actionplanentry(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal)
			throws Exception {

		try {

			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());

			// get acion plan entry
			ActionPlanEntry ape = serviceActionPlan.getFromAnalysisById(idAnalysis, elementID);
			if (ape == null)
				return JsonMessage.Error(messageSource.getMessage("error.actionplanentry.not_found", null, "Action Plan Entry cannot be found", cutomLocale != null ? cutomLocale
						: locale));

			// retrieve phase
			Integer number = null;
			number = (Integer) FieldValue(fieldEditor, null);
			if (number == null)
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", cutomLocale != null ? cutomLocale : locale));
			Phase phase = servicePhase.getFromAnalysisByPhaseNumber(idAnalysis, number);
			if (phase == null)
				return JsonMessage.Error(messageSource.getMessage("error.phase.not_found", null, "Phase cannot be found", cutomLocale != null ? cutomLocale : locale));

			// set new phase value of measure
			ape.getMeasure().setPhase(phase);

			// update measure
			serviceMeasure.saveOrUpdate(ape.getMeasure());

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.ationplan.updated", null, "ActionPlan entry was successfully updated", cutomLocale != null ? cutomLocale
					: locale));

		} catch (TrickException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (Exception e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// retrun error message
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.edit.save.field", null, "Data cannot be saved", cutomLocale != null ? cutomLocale : locale));
		}
	}

	/**
	 * phase: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Phase/{elementID}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Phase', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String phase(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {

		try {

			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
			// retireve phase
			Phase phase = servicePhase.getFromAnalysisById(idAnalysis, elementID);

			if (phase == null)
				return JsonMessage.Error(messageSource.getMessage("error.phase.not_found", null, "Phase cannot be found", cutomLocale != null ? cutomLocale : locale));

			// set field
			Field field = phase.getClass().getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);

			// set field date
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			field.set(phase, new Date(format.parse(fieldEditor.getValue().toString()).getTime()));

			// update phase
			servicePhase.saveOrUpdate(phase);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.phase.updated", null, "Phase was successfully updated", cutomLocale != null ? cutomLocale : locale));

		} catch (Exception e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.edit.save.field", null, "Data cannot be saved", cutomLocale != null ? cutomLocale : locale));
		}
	}

	@RequestMapping(value = "/RiskInformation/{elementID}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'RiskInformation', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String riskInformation(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal)
			throws Exception {
		try {
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());

			RiskInformation riskInformation = serviceRiskInformation.getFromAnalysisById(idAnalysis, elementID);
			if (riskInformation == null)
				return JsonMessage.Error(messageSource.getMessage("error.risk_information.not_found", null, "Risk information cannot be found", cutomLocale != null ? cutomLocale
						: locale));

			// set field
			Field field = riskInformation.getClass().getDeclaredField(fieldEditor.getFieldName());

			ValidatorField validatorField = serviceDataValidation.findByClass(RiskInformation.class);
			if (validatorField == null)
				serviceDataValidation.register(validatorField = new RiskInformationValidator());
			String error = validatorField.validate(riskInformation, fieldEditor.getFieldName(), fieldEditor.getValue());
			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, cutomLocale != null ? cutomLocale : locale));
			field.setAccessible(true);
			if (!SetFieldData(field, riskInformation, fieldEditor, null))
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", cutomLocale != null ? cutomLocale : locale));
			// update phase
			serviceRiskInformation.saveOrUpdate(riskInformation);
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.risk_information.updated", null, "Risk information was successfully updated",
					cutomLocale != null ? cutomLocale : locale));
		} catch (TrickException e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), cutomLocale != null ? cutomLocale : locale));
		} catch (Exception e) {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(id).getAlpha2());
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.edit.save.field", null, "Data cannot be saved", cutomLocale != null ? cutomLocale : locale));
		}
	}

	@RequestMapping(value = "/RiskRegister/{elementID}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'RiskRegister', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String riskRegister(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal)
			throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Locale cutomLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
		if (fieldEditor.getFieldName().matches("strategy|owner")) {
			RiskRegisterItem registerItem = serviceRiskRegister.get(elementID);
			if (registerItem == null)
				return JsonMessage.Error(messageSource.getMessage("error.risk_register.not_found", null, "Risk register cannot be found", cutomLocale != null ? cutomLocale
						: locale));
			try {
				PropertyAccessorFactory.forBeanPropertyAccess(registerItem).setPropertyValue(fieldEditor.getFieldName(), fieldEditor.getValue());
				serviceRiskRegister.saveOrUpdate(registerItem);
				return JsonMessage.Success(messageSource.getMessage("success.risk_register.updated", null, "Risk register was successfully updated",
						cutomLocale != null ? cutomLocale : locale));
			} catch (TrickException e) {
				e.printStackTrace();
				return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), cutomLocale != null ? cutomLocale : locale));
			} catch (Exception e) {
				e.printStackTrace();
				return JsonMessage.Error(messageSource.getMessage("error.edit.save.field", null, "Data cannot be saved", cutomLocale != null ? cutomLocale : locale));
			}
		} else
			return JsonMessage.Error(messageSource.getMessage("error.edit.field.unsupported", null, "Field cannot be edited", locale));
	}

	/**
	 * setFieldData: <br>
	 * Description
	 * 
	 * @param field
	 * @param object
	 * @param fieldEditor
	 * @param pattern
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws ParseException
	 * @throws NumberFormatException
	 */
	public static boolean SetFieldData(Field field, Object object, FieldEditor fieldEditor, String pattern) throws IllegalArgumentException, IllegalAccessException,
			ParseException, NumberFormatException {
		Object value = FieldValue(fieldEditor, pattern);
		if (value == null)
			return false;
		field.set(object, value);
		// return success
		return true;
	}

	/**
	 * value: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param pattern
	 * @return
	 */
	public static Object FieldValue(FieldEditor fieldEditor, String pattern) {
		try {
			// get the field type and return value in casted form
			if (fieldEditor.getType().equalsIgnoreCase("string"))
				return (String) fieldEditor.getValue();
			else if (fieldEditor.getType().equalsIgnoreCase("integer"))
				return NumberFormat.getInstance(Locale.FRANCE).parse(fieldEditor.getValue().toString()).intValue();
			else if (fieldEditor.getType().equalsIgnoreCase("double")) {
				return NumberFormat.getInstance(Locale.FRANCE).parse(fieldEditor.getValue().toString()).doubleValue();
			} else if (fieldEditor.getType().equalsIgnoreCase("float"))
				return NumberFormat.getInstance(Locale.FRANCE).parse(fieldEditor.getValue().toString()).floatValue();
			else if (fieldEditor.getType().equalsIgnoreCase("boolean"))
				return Boolean.parseBoolean(fieldEditor.getValue().toString());
			else if (fieldEditor.getType().equalsIgnoreCase("date")) {
				DateFormat format = new SimpleDateFormat(pattern == null ? "yyyy-MM-dd hh:mm:ss" : pattern);
				return format.parse(fieldEditor.getValue().toString());
			}
		} catch (NumberFormatException e) {
			// print error
			e.printStackTrace();
		} catch (ParseException e) {
			// print error
			e.printStackTrace();
		}
		// data type not found, return error
		return null;
	}

	/**
	 * FindField: <br>
	 * Description
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	public static Field FindField(Class<?> object, String fieldName) {
		for (Field field : object.getDeclaredFields())
			if (field.getName().equals(fieldName))
				return field;
		if (!object.equals(Object.class))
			return FindField(object.getSuperclass(), fieldName);
		return null;
	}
}

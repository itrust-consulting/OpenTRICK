package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.lang.reflect.Field;
import java.security.Principal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.component.FieldEditor;
import lu.itrust.business.TS.component.FieldValue;
import lu.itrust.business.TS.component.JSTLFunctions;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.Result;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.hbm.DAOHibernate;
import lu.itrust.business.TS.database.service.ServiceActionPlan;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceHistory;
import lu.itrust.business.TS.database.service.ServiceItemInformation;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServiceParameter;
import lu.itrust.business.TS.database.service.ServicePhase;
import lu.itrust.business.TS.database.service.ServiceRiskInformation;
import lu.itrust.business.TS.database.service.ServiceRiskProfile;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.cssf.helper.ParameterConvertor;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.model.history.History;
import lu.itrust.business.TS.model.iteminformation.ItemInformation;
import lu.itrust.business.TS.model.parameter.AcronymParameter;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.model.parameter.MaturityParameter;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.parameter.helper.ParameterManager;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.measure.AssetMeasure;
import lu.itrust.business.TS.model.standard.measure.MaturityMeasure;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.MeasureAssetValue;
import lu.itrust.business.TS.model.standard.measure.MeasureProperties;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;
import lu.itrust.business.TS.validator.AssessmentValidator;
import lu.itrust.business.TS.validator.AssetValidator;
import lu.itrust.business.TS.validator.ExtendedParameterValidator;
import lu.itrust.business.TS.validator.HistoryValidator;
import lu.itrust.business.TS.validator.MaturityParameterValidator;
import lu.itrust.business.TS.validator.MeasureValidator;
import lu.itrust.business.TS.validator.ParameterValidator;
import lu.itrust.business.TS.validator.RiskInformationValidator;
import lu.itrust.business.TS.validator.ScenarioValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;
import lu.itrust.business.expressions.StringExpressionParser;

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
	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	@Autowired
	private ServicePhase servicePhase;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private ServiceRiskProfile serviceRiskProfile;

	private Pattern computeCostPattern = Pattern.compile("internalWL|externalWL|investment|lifetime|internalMaintenance|externalMaintenance|recurrentInvestment");

	private Pattern riskProfileNoFieldPattern = Pattern.compile("^*\\.id$|^\\*.asset\\.*$|^*.scenario\\.*");

	private Pattern smlPatten = Pattern.compile("^SMLLevel[0-5]{1}$");

	/**
	 * itemInformation: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/ItemInformation/{elementID}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'ItemInformation', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String itemInformation(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, Locale locale, HttpSession session, Principal principal)
			throws Exception {
		try {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// get item information object from id
			ItemInformation itemInformation = serviceItemInformation.getFromAnalysisById(id, elementID);
			// initialise field
			Field field = itemInformation.getClass().getDeclaredField(fieldEditor.getFieldName());
			// set field with new data
			if (SetFieldData(field, itemInformation, fieldEditor)) {
				// update iteminformation
				serviceItemInformation.saveOrUpdate(itemInformation);
				// return success message
				return JsonMessage.Success(messageSource.getMessage("success.item_information.updated", null, "Item information was successfully updated", locale));
			} else
				// return error message
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.edit.field", null, "An unknown error occurred while updating field", locale));
		}
	}

	@RequestMapping(value = "/Asset/{elementID}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String asset(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {
		try {
			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// retrieve measure
			Asset asset = serviceAsset.getFromAnalysisById(idAnalysis, elementID);

			ValidatorField validator = serviceDataValidation.findByClass(Asset.class);
			if (validator == null)
				serviceDataValidation.register(validator = new AssetValidator());
			if (!validator.isEditable(fieldEditor.getFieldName()))
				return JsonMessage.Error(messageSource.getMessage("error.field.not.support.live.edition", null, "Field does not support editing on the fly", locale));

			Object value = FieldValue(fieldEditor);

			String error = serviceDataValidation.validate(asset, fieldEditor.getFieldName(), value);

			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, locale));

			Field field = asset.getClass().getDeclaredField(fieldEditor.getFieldName());
			// check if field is a phase
			if (!SetFieldValue(asset, field, value))
				JsonMessage.Error(messageSource.getMessage("error.edit.save.field", null, "Data cannot be saved", locale));

			// update measure
			serviceAsset.saveOrUpdate(asset);
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.asset.updated", null, "Asset was successfully updated", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.edit.field", null, "An unknown error occurred while updating field", locale));
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
	@RequestMapping(value = "/Parameter/{elementID}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Parameter', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String parameter(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, Locale locale, HttpSession session, Principal principal)
			throws Exception {

		try {
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// get parameter object
			Parameter parameter = serviceParameter.getFromAnalysisById(id, elementID);
			// validate parameter
			ValidatorField validator = serviceDataValidation.findByClass(parameter.getClass());
			if (validator == null)
				serviceDataValidation.register(new ParameterValidator());
			// retireve value
			Object value = FieldValue(fieldEditor);
			// validate value
			String error = serviceDataValidation.validate(parameter, fieldEditor.getFieldName(), value);
			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, locale));
			switch (parameter.getType().getLabel()) {
			case Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME:
			case Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_LEVEL_PER_SML_NAME:
			case Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME:
				if (((double) value) < 0 || ((double) value) > 100)
					return JsonMessage.Error(messageSource.getMessage("error.parameter.value.out_of_bound", new Object[] { value },
							String.format("Invalid input: value (%f) should be between 0 and 100", value), locale));
				break;
			case Constant.PARAMETERTYPE_TYPE_SINGLE_NAME:
				if (parameter.getDescription().equals(Constant.PARAMETER_LIFETIME_DEFAULT)) {
					if (((double) value) <= 0)
						return JsonMessage.Error(messageSource.getMessage("error.edit.parameter.default_lifetime", null, "Default lifetime has to be > 0", locale));
				} else if (parameter.getDescription().equals(Constant.PARAMETER_MAX_RRF) || parameter.getDescription().equals(Constant.SOA_THRESHOLD)) {
					if (((double) value) < 0 || ((double) value) > 100)
						return JsonMessage.Error(messageSource.getMessage("error.parameter.value.out_of_bound", new Object[] { value },
								String.format("Invalid input: value (%f) should be between 0 and 100", value), locale));
				}
				break;
			}
			// create field
			Field field = parameter.getClass().getDeclaredField(fieldEditor.getFieldName());
			// set field data
			if (SetFieldData(field, parameter, fieldEditor)) {
				// update field
				serviceParameter.saveOrUpdate(parameter);
				// return success message
				return JsonMessage.Success(messageSource.getMessage("success.parameter.updated", null, "Parameter was successfully updated", locale));
			} else
				// return error message
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.edit.field", null, "An unknown error occurred while updating field", locale));
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
	@RequestMapping(value = "/ExtendedParameter/{elementID}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Parameter', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String extendedParameter(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal)
			throws Exception {
		try {

			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// retrieve parameter
			ExtendedParameter parameter = (ExtendedParameter) serviceParameter.getFromAnalysisById(id, elementID);

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
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, locale));

			// set field
			Field field = null;
			for (Class<?> clazz = parameter.getClass(); Parameter.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
				try {
					field = clazz.getDeclaredField(fieldEditor.getFieldName());
					break;
				} catch (NoSuchFieldException ex) {
					// continue
				}
			}
			field.setAccessible(true);

			// set field data
			if (SetFieldData(field, parameter, fieldEditor, null)) {
				if ("value".equals(fieldEditor.getFieldName()) && Constant.PARAMETERTYPE_TYPE_IMPACT_NAME.equalsIgnoreCase(parameter.getType().getLabel()))
					parameter.setValue(parameter.getValue() * 1000);

				if (field.getName().equals("acronym")) {
					try {
						assessmentAndRiskProfileManager.UpdateAcronym(id, parameter, acronym);
					} catch (Exception e) {
						TrickLogManager.Persist(e);
						return JsonMessage.Error(messageSource.getMessage("error.assessment.acronym.updated", new String[] { acronym, parameter.getAcronym() },
								"Assessment acronym (" + acronym + ") cannot be updated to (" + parameter.getAcronym() + ")", locale));
					}
				}
				// update field
				serviceParameter.saveOrUpdate(parameter);

				// Update bounds for IMPACT and PROBABILITY
				if (parameter.getType().getLabel().equalsIgnoreCase(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME)
						|| parameter.getType().getLabel().equalsIgnoreCase(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME)) {
					// retrieve parameters
					List<ExtendedParameter> parameters = serviceParameter.getAllExtendedFromAnalysisAndType(id, parameter.getType());

					// update impact value
					ParameterManager.ComputeImpactValue(parameters);

					// update parameters
					serviceParameter.saveOrUpdate(parameters);
				}

				// return success message
				return JsonMessage.Success(messageSource.getMessage("success.extendedParameter.update", null, "Parameter was successfully update", locale));
			} else
				// return error message
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));

		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.edit.field", null, "An unknown error occurred while updating field", locale));
		}
	}

	@RequestMapping(value = "/MaturityParameter/{elementID}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Parameter', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String maturityparameter(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, Locale locale, HttpSession session, Principal principal)
			throws Exception {

		try {
			if (!smlPatten.matcher(fieldEditor.getFieldName()).matches())
				return JsonMessage.Error(messageSource.getMessage("error.field.not.support.live.edition", null, "Field does not support editing on the fly", locale));
			// retrieve analysis id
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// get parameter object
			MaturityParameter parameter = (MaturityParameter) serviceParameter.getFromAnalysisById(id, elementID);
			// validate parameter
			ValidatorField validator = serviceDataValidation.findByClass(parameter.getClass());
			if (validator == null)
				serviceDataValidation.register(new MaturityParameterValidator());
			// retireve value
			Object value = FieldValue(fieldEditor);
			// validate value
			String error = serviceDataValidation.validate(parameter, fieldEditor.getFieldName(), value);
			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, locale));
			// create field
			Field field = parameter.getClass().getDeclaredField(fieldEditor.getFieldName());
			// set value /100 to save as values between 0 and 1
			// set field data
			if (SetFieldValue(parameter, field, (Double) value * 0.01)) {
				// update field
				serviceParameter.saveOrUpdate(parameter);
				// return success message
				return JsonMessage.Success(messageSource.getMessage("success.parameter.updated", null, "Parameter was successfully updated", locale));
			} else
				// return error message
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.edit.field", null, "An unknown error occurred while updating field", locale));
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
	@RequestMapping(value = "/Assessment/{elementID}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Assessment', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String assessment(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal)
			throws Exception {
		int idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Result result = updateAssessment(fieldEditor, serviceAssessment.getFromAnalysisById(idAnalysis, elementID), idAnalysis, locale, false);
		return result.isError() ? JsonMessage.Error(result.getMessage()) : JsonMessage.Success(result.getMessage());
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
	@RequestMapping(value = "/RiskProfile/{elementID}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'RiskProfile', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String riskProfile(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal)
			throws Exception {
		int idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Result result = updateRiskProfile(fieldEditor, serviceRiskProfile.getFromAnalysisById(idAnalysis, elementID), idAnalysis, locale);
		return result.isError() ? JsonMessage.Error(result.getMessage()) : JsonMessage.Success(result.getMessage());
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
	@RequestMapping(value = "/Estimation/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idAsset, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY) and @permissionEvaluator.userIsAuthorized(#session, #idScenario, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Result estimation(@RequestBody FieldEditor fieldEditor, @RequestParam("asset") int idAsset, @RequestParam("scenario") int idScenario, HttpSession session,
			Locale locale, Principal principal) {
		if (fieldEditor.getFieldName().equals("scenario.description")) {
			Scenario scenario = serviceScenario.getFromAnalysisById((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), idScenario);
			if (scenario == null)
				return Result.Error(messageSource.getMessage("error.scenario.not_found", null, "Scenario cannot be found", locale));
			scenario.setDescription(fieldEditor.getValue().toString().trim());
			serviceScenario.saveOrUpdate(scenario);
			return Result.Success(messageSource.getMessage("success.scenario.updated", null, "Scenario was successfully updated", locale));
		} else if (fieldEditor.getFieldName().startsWith("riskProfile."))
			return updateRiskProfile(fieldEditor, idAsset, idScenario, session, locale);
		else
			return updateAssessment(fieldEditor, idAsset, idScenario, session, locale);
	}

	private Result updateRiskProfile(FieldEditor fieldEditor, int idAsset, int idScenario, HttpSession session, Locale locale) {
		return updateRiskProfile(fieldEditor, serviceRiskProfile.getByAssetAndScanrio(idAsset, idScenario), (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), locale);
	}

	private Result updateRiskProfile(FieldEditor fieldEditor, RiskProfile riskProfile, Integer idAnalysis, Locale locale) {
		try {
			if (riskProfileNoFieldPattern.matcher(fieldEditor.getFieldName()).matches())
				return Result.Error(messageSource.getMessage("error.field.not.support.live.edition", null, "Field does not support editing on the fly", locale));
			Result result = Result.Success(messageSource.getMessage("success.risk_profile.updated", null, "Risk profile was successfully updated", locale));
			String[] fields = fieldEditor.getFieldName().split("\\.");
			if (fields.length < 2)
				return Result.Error(messageSource.getMessage("error.field.not.support.live.edition", null, "Field does not support editing on the fly", locale));
			Field field = FindField(RiskProfile.class, fields[1]);
			if (field == null)
				return Result.Error(messageSource.getMessage("error.field.not.support.live.edition", null, "Field does not support editing on the fly", locale));
			field.setAccessible(true);
			if (field.getType().isAssignableFrom(RiskProbaImpact.class)) {
				if (fields.length != 3)
					return Result.Error(messageSource.getMessage("error.field.not.support.live.edition", null, "Field does not support editing on the fly", locale));
				RiskProbaImpact probaImpact = (RiskProbaImpact) field.get(riskProfile);
				if (probaImpact == null)
					probaImpact = new RiskProbaImpact();
				Field child = FindField(RiskProbaImpact.class, fields[2]);
				Integer idParameter = (Integer) FieldValue(fieldEditor);
				Parameter parameter = serviceParameter.getFromAnalysisById(idAnalysis, idParameter);
				if (SetFieldValue(probaImpact, child, parameter) && SetFieldValue(riskProfile, field, probaImpact))
					result.add(new FieldValue(fields[1].startsWith("raw") ? "rawComputedImportance" : "expComputedImportance", probaImpact.getImportance()));
				else
					return Result.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
			} else if (field.getType().isAssignableFrom(RiskStrategy.class))
				riskProfile.setRiskStrategy(RiskStrategy.valueOf(fieldEditor.getValue().toString()));
			else if (field.getName().equals("identifier")) {
				String identifier = fieldEditor.getValue() == null ? "" : fieldEditor.getValue().toString().trim();
				if (identifier.isEmpty())
					return Result.Error(messageSource.getMessage("error.identifier.null", null, "Identifier cannot be empty", locale));
				else if (serviceRiskProfile.isUsed(identifier, idAnalysis))
					return Result.Error(messageSource.getMessage("error.identifier.is_in_used", null, "Identifier are not available", locale));
				else
					riskProfile.setIdentifier(identifier);
			} else if (!SetFieldData(field, riskProfile, fieldEditor))
				return Result.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
			serviceRiskProfile.saveOrUpdate(riskProfile);
			return result;
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return Result.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return Result.Error(messageSource.getMessage("error.unknown.edit.field", null, "An unknown error occurred while updating field", locale));
		}
	}

	private Result updateAssessment(FieldEditor fieldEditor, int idAsset, int idScenario, HttpSession session, Locale locale) {
		return updateAssessment(fieldEditor, serviceAssessment.getByAssetAndScenario(idAsset, idScenario), (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), locale,
				true);
	}

	private Result updateAssessment(FieldEditor fieldEditor, Assessment assessment, Integer idAnalysis, Locale locale, boolean netImportance) {
		try {
			if (assessment == null)
				return Result.Error(messageSource.getMessage("error.assessment.not_found", null, "Assessment cannot be found", locale));
			// set validator
			if (!serviceDataValidation.isRegistred(Assessment.class))
				serviceDataValidation.register(new AssessmentValidator());
			List<? extends AcronymParameter> parameters = null;
			// retrieve all acronyms of impact and likelihood
			Object[] chooses = null;
			if ("impactRep,impactOp,impactLeg,impactFin".contains(fieldEditor.getFieldName())) {
				parameters = serviceParameter.getAllExtendedFromAnalysis(idAnalysis);
				List<String> impacts = parameters.stream().filter(parameter -> parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
						.map(parameter -> parameter.getAcronym()).collect(Collectors.toList());
				if (!impacts.contains(fieldEditor.getValue())) {
					try {
						double value = NumberFormat.getInstance(Locale.FRANCE).parse(fieldEditor.getValue().toString()).doubleValue() * 1000;
						if (value < 0)
							return Result.Error(messageSource.getMessage("error.negatif.impact.value", null, "Impact cannot be negative", locale));
						fieldEditor.setValue(value + "");
					} catch (ParseException e) {
						chooses = impacts.toArray();
					}
				} else
					chooses = impacts.toArray();
			} else if ("likelihood".equals(fieldEditor.getFieldName())) {
/*
				parameters = serviceParameter.getAllExtendedFromAnalysis(idAnalysis);
				chooses = parameters.stream().filter(parameter -> parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME))
						.map(parameter -> parameter.getAcronym()).toArray();
*/
				parameters = serviceParameter.getAllExpressionParametersFromAnalysis(idAnalysis);
				chooses = serviceParameter.getAllExpressionParameterAcronymsFromAnalysis(idAnalysis).toArray();
				if (fieldEditor.getValue().equals("NA"))
					fieldEditor.setValue("0");
			}
			// get value
			Object value = FieldValue(fieldEditor);
			// validate new value
			String error = serviceDataValidation.validate(assessment, fieldEditor.getFieldName(), value, chooses);
			if (error != null)
				// return error message
				return Result.Error(serviceDataValidation.ParseError(error, messageSource, locale));
			// init field
			Field field = assessment.getClass().getDeclaredField(fieldEditor.getFieldName());
			// set data to field
			if (!SetFieldData(field, assessment, fieldEditor))
				// return error message
				return Result.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));

			Result result = Result.Success(messageSource.getMessage("success.assessment.updated", null, "Assessment was successfully updated", locale));

			// compute new ALE
			if (parameters != null) {
				AssessmentAndRiskProfileManager.ComputeAlE(assessment, parameters.stream().collect(Collectors.toMap(AcronymParameter::getAcronym, Function.identity())));
				if (netImportance) {
					ParameterConvertor converter = new ParameterConvertor(parameters);
					result.add(new FieldValue("computedNextImportance",
							converter.getImpactLevel(assessment.getImpactReal()) * converter.getProbabiltyLevel(assessment.getLikelihoodReal())));
				}
				NumberFormat numberFormat = NumberFormat.getInstance(Locale.FRANCE);
				result.add(new FieldValue("ALE", format(assessment.getALE() * .001, numberFormat, 2), format(assessment.getALE(), numberFormat, 0) + " €"));
				result.add(new FieldValue("ALEO", format(assessment.getALEO() * .001, numberFormat, 2), format(assessment.getALEO(), numberFormat, 0) + " €"));
				result.add(new FieldValue("ALEP", format(assessment.getALEP() * .001, numberFormat, 2), format(assessment.getALEP(), numberFormat, 0) + " €"));
			}
			// update assessment
			serviceAssessment.saveOrUpdate(assessment);
			// return success message
			return result;
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return Result.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return Result.Error(messageSource.getMessage("error.unknown.edit.field", null, "An unknown error occurred while updating field", locale));
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
	@RequestMapping(value = "/History/{elementID}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'History', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String history(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, Locale locale, HttpSession session, Principal principal)
			throws Exception {

		try {
			// retrieve analysis
			Integer id = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// retireve history object
			History history = serviceHistory.getFromAnalysisById(id, elementID);
			// get validator
			if (!serviceDataValidation.isRegistred(history.getClass()))
				serviceDataValidation.register(new HistoryValidator());
			// get new value
			Object value = FieldValue(fieldEditor);
			// validate
			String error = serviceDataValidation.validate(history, fieldEditor.getFieldName(), value);
			// return errors on validation fail
			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, locale));

			// set field
			Field field = history.getClass().getDeclaredField(fieldEditor.getFieldName());

			// set field data
			if (SetFieldData(field, history, fieldEditor)) {

				// update history
				serviceHistory.saveOrUpdate(history);

				// return success message
				return JsonMessage.Success(messageSource.getMessage("success.history.updated", null, "History was successfully updated", locale));
			} else

				// return error rmessage
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.edit.field", null, "An unknown error occurred while updating field", locale));
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
	@RequestMapping(value = "/Scenario/{elementID}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String scenario(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal)
			throws Exception {
		try {

			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// retrieve measure
			Scenario scenario = serviceScenario.getFromAnalysisById(idAnalysis, elementID);

			ValidatorField validator = serviceDataValidation.findByClass(Scenario.class);

			if (validator == null)
				serviceDataValidation.register(validator = new ScenarioValidator());

			// set field
			Field field = FindField(Scenario.class, fieldEditor.getFieldName());
			// means that field belongs to the Measure class

			if (field != null) {
				// check if field is a phase
				field.setAccessible(true);
				if (field.getName().equals("preventive") || field.getName().equals("detective") || field.getName().equals("limitative") || field.getName().equals("corrective")) {
					if (!(fieldEditor.getValue() instanceof Integer))
						fieldEditor.setValue(Double.valueOf(String.valueOf(fieldEditor.getValue())));
				}
				String error = validator.validate(scenario, fieldEditor.getFieldName(), fieldEditor.getValue());
				if (error == null)
					field.set(scenario, fieldEditor.getValue());
				else
					return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, locale));
			} else {
				if (Scenario.isCategoryKey(fieldEditor.getFieldName()))
					scenario.setCategoryValue(fieldEditor.getFieldName(), (Integer) fieldEditor.getValue());
				else {
					AssetTypeValue assetData = scenario.getAssetTypeValues().stream().filter(assetTypeValue -> assetTypeValue.hasSameType(fieldEditor.getFieldName())).findAny()
							.orElse(null);
					if (assetData != null)
						assetData.setValue((Integer) fieldEditor.getValue());
					else
						return JsonMessage.Error(messageSource.getMessage("error.field.not.support.live.edition", null, "Field does not support editing on the fly", locale));
				}
			}

			serviceScenario.saveOrUpdate(scenario);
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.scenario.updated", null, "Scenario was successfully updated", locale));

		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.edit.field", null, "An unknown error occurred while updating field", locale));
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
	@RequestMapping(value = "/Measure/{elementID}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String measure(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal)
			throws Exception {

		try {

			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// retrieve measure
			Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);

			// set field
			Field field = FindField(Measure.class, fieldEditor.getFieldName());

			// means that field belongs to the Measure class

			if (field != null) {
				// check if field is a phase

				if (fieldEditor.getFieldName().equals("phase")) {

					// retireve phase
					Integer number = null;
					number = (Integer) FieldValue(fieldEditor);
					if (number == null)
						return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
					Phase phase = servicePhase.getFromAnalysisByPhaseNumber(idAnalysis, number);
					if (phase == null)
						return JsonMessage.Error(messageSource.getMessage("error.phase.not_found", null, "Phase cannot be found", locale));

					// set new phase number
					measure.setPhase(phase);

					// set field data
				} else {

					Object value = FieldValue(fieldEditor);
					if (value == null)
						return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));

					// get validator
					if (!serviceDataValidation.isRegistred(Measure.class))
						serviceDataValidation.register(new MeasureValidator());

					String error = serviceDataValidation.validate(measure, fieldEditor.getFieldName(), value);
					if (error != null)
						return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, locale));

					if (fieldEditor.getFieldName().equals("implementationRate")) {
						if (!(new StringExpressionParser((String)value)).isValid(serviceParameter.getAllExpressionParameterAcronymsFromAnalysis(idAnalysis)))
							return JsonMessage.Error(messageSource.getMessage("error.edit.type.field.expression", null, "Invalid expression. Check the syntax and make sure that all used parameters exist.", locale));
					}

					SetFieldValue(measure, field, value);
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

					field = FindField(NormalMeasure.class, fieldEditor.getFieldName());

					// means that field belongs to either measure or
					// normalmeasure

					if (field != null) {

						// check if field is a phase
						if (!SetFieldData(field, measure, fieldEditor))
							return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));

						// update measure
						serviceMeasure.saveOrUpdate(measure);

					} else {

						field = FindField(MeasureProperties.class, fieldEditor.getFieldName());

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

					field = FindField(AssetMeasure.class, fieldEditor.getFieldName());

					// means that field belongs to either measure or
					// normalmeasure

					if (field != null) {

						// check if field is a phase
						if (!SetFieldData(field, measure, fieldEditor))
							return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));

						// update measure
						serviceMeasure.saveOrUpdate(measure);

					} else {

						field = FindField(MeasureProperties.class, fieldEditor.getFieldName());

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
			return JsonMessage.Success(messageSource.getMessage("success.measure.updated", null, "Measure was successfully updated", locale));

		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.edit.field", null, "An unknown error occurred while updating field", locale));
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
	@RequestMapping(value = "/SOA/{elementID}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String soa(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {

		try {
			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// retrieve measure
			NormalMeasure measure = (NormalMeasure) serviceMeasure.getFromAnalysisById(idAnalysis, elementID);

			MeasureProperties mesprep = DAOHibernate.Initialise(measure.getMeasurePropertyList());
			Field field = mesprep.getClass().getDeclaredField(fieldEditor.getFieldName());

			// check if field is a phase
			if (!SetFieldData(field, mesprep, fieldEditor))
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));

			measure.setMeasurePropertyList(mesprep);

			// update measure
			serviceMeasure.saveOrUpdate(measure);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.measure.updated", null, "Measure was successfully updated", locale));

		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.edit.field", null, "An unknown error occurred while updating field", locale));
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
	@RequestMapping(value = "/MaturityMeasure/{elementID}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String maturityMeasure(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal)
			throws Exception {

		try {

			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// retrieve measure
			Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);
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
						return JsonMessage.Success(messageSource.getMessage("success.measure.updated", null, "Measure was successfully updated", locale));
					}
				}

				// return error message
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
			} else

				// update as if it would be a normal measure
				return measure(elementID, fieldEditor, session, locale, principal);
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.edit.field", null, "An unknown error occurred while updating field", locale));
		}
	}

	@RequestMapping(value = "/Measure/{id}/Update", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #id, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Result updateMeasure(@PathVariable int id, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) {
		Result result = null;
		try {
			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			if (fieldEditor.getFieldName().equalsIgnoreCase("cost"))
				return Result.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
			Measure measure = serviceMeasure.get(id);
			if (fieldEditor.getFieldName().equalsIgnoreCase("implementationRate")) {
				Object value = null;
				if (measure instanceof MaturityMeasure)
					value = serviceParameter.getFromAnalysisById(idAnalysis, (Integer) FieldValue(fieldEditor));
				else
					value = FieldValue(fieldEditor);
				measure.setImplementationRate(value);
			} else if (fieldEditor.getFieldName().equalsIgnoreCase("phase")) {
				measure.setPhase(servicePhase.getFromAnalysisById(idAnalysis, (Integer) FieldValue(fieldEditor)));
			} else {
				Field field = FindField(measure.getClass(), fieldEditor.getFieldName());
				if (field == null)
					return Result.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));

				Object value = FieldValue(fieldEditor);

				if (!serviceDataValidation.isRegistred(Measure.class))
					serviceDataValidation.register(new MeasureValidator());

				String error = serviceDataValidation.validate(measure, fieldEditor.getFieldName(), value);
				if (error != null)
					return Result.Error(serviceDataValidation.ParseError(error, messageSource, locale));

				if (SetFieldValue(measure, field, value))
					result = Result.Success(messageSource.getMessage("success.measure.updated", null, "Measure was successfully updated", locale));
				else
					return Result.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));

				Object realValue = null;
				if (fieldEditor.getFieldName().equals("investment"))
					measure.setInvestment((double) (realValue = measure.getInvestment() * 1000));
				if (fieldEditor.getFieldName().equals("recurrentInvestment"))
					measure.setRecurrentInvestment((double) (realValue = measure.getRecurrentInvestment() * 1000));
				if (computeCostPattern.matcher(fieldEditor.getFieldName()).find()) {
					Measure.ComputeCost(measure, serviceAnalysis.get(idAnalysis));
					NumberFormat numberFormat = NumberFormat.getInstance(Locale.FRANCE);
					result.add(new FieldValue("cost", format(measure.getCost() * .001, numberFormat, 0), format(measure.getCost(), numberFormat, 0) + " €"));
					if (realValue == null)
						result.add(new FieldValue(fieldEditor.getFieldName(), format((Double) field.get(measure), numberFormat, 3)));
					else
						result.add(
								new FieldValue(fieldEditor.getFieldName(), format((Double) realValue * .001, numberFormat, 3), format((Double) realValue, numberFormat, 0) + " €"));
				}
			}
			serviceMeasure.saveOrUpdate(measure);
			if (result == null)
				result = Result.Success(messageSource.getMessage("success.measure.updated", null, "Measure was successfully updated", locale));
		} catch (TrickException e) {
			if (result == null)
				result = Result.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			else
				result.turnOnError(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			if (result == null)
				result = Result.Error(messageSource.getMessage("error.unknown.edit.field", null, "An unknown error occurred while updating field", locale));
			else
				result.turnOnError(messageSource.getMessage("error.unknown.edit.field", null, "An unknown error occurred while updating field", locale));
			TrickLogManager.Persist(e);
		}
		return result;

	}

	private String format(double value, NumberFormat numberFormat, int decimal) {
		numberFormat.setMaximumFractionDigits(decimal);
		return numberFormat.format(JSTLFunctions.round(value, decimal));
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
	@RequestMapping(value = "/ActionPlanEntry/{elementID}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'ActionPlanEntry', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String actionplanentry(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal)
			throws Exception {

		try {

			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// get acion plan entry
			ActionPlanEntry ape = serviceActionPlan.getFromAnalysisById(idAnalysis, elementID);
			// retrieve phase
			Integer number = (Integer) FieldValue(fieldEditor);
			if (number == null)
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
			Phase phase = servicePhase.getFromAnalysisByPhaseNumber(idAnalysis, number);
			if (phase == null)
				return JsonMessage.Error(messageSource.getMessage("error.phase.not_found", null, "Phase cannot be found", locale));

			// set new phase value of measure
			ape.getMeasure().setPhase(phase);

			// update measure
			serviceMeasure.saveOrUpdate(ape.getMeasure());

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.ationplan.updated", null, "ActionPlan entry was successfully updated", locale));

		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.edit.field", null, "An unknown error occurred while updating field", locale));
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
	@RequestMapping(value = "/Phase/{elementID}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Phase', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String phase(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {

		try {
			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// retireve phase
			Phase phase = servicePhase.getFromAnalysisById(idAnalysis, elementID);
			// set field
			Field field = phase.getClass().getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);
			// set field date
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			field.set(phase, new Date(format.parse(fieldEditor.getValue().toString()).getTime()));
			// update phase
			servicePhase.saveOrUpdate(phase);
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.phase.updated", null, "Phase was successfully updated", locale));

		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.edit.field", null, "An unknown error occurred while updating field", locale));
		}
	}

	@RequestMapping(value = "/RiskInformation/{elementID}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'RiskInformation', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String riskInformation(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal)
			throws Exception {
		try {
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			RiskInformation riskInformation = serviceRiskInformation.getFromAnalysisById(idAnalysis, elementID);
			// set field
			Field field = riskInformation.getClass().getDeclaredField(fieldEditor.getFieldName());
			ValidatorField validatorField = serviceDataValidation.findByClass(RiskInformation.class);
			if (validatorField == null)
				serviceDataValidation.register(validatorField = new RiskInformationValidator());
			String error = validatorField.validate(riskInformation, fieldEditor.getFieldName(), fieldEditor.getValue());
			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, locale));
			field.setAccessible(true);
			if (!SetFieldData(field, riskInformation, fieldEditor, null))
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
			// update phase
			serviceRiskInformation.saveOrUpdate(riskInformation);
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.risk_information.updated", null, "Risk information was successfully updated", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.edit.field", null, "An unknown error occurred while updating field", locale));
		}
	}

	/*
	 * @RequestMapping(value = "/RiskRegister/{elementID}", method =
	 * RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	 * 
	 * @PreAuthorize(
	 * "@permissionEvaluator.userIsAuthorized(#session, #elementID, 'RiskRegister', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)"
	 * ) public @ResponseBody String riskRegister(@PathVariable int
	 * elementID, @RequestBody FieldEditor fieldEditor, HttpSession session,
	 * Locale locale, Principal principal) { try { Integer idAnalysis =
	 * (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS); if
	 * (fieldEditor.getFieldName().matches("strategy|owner")) { RiskRegisterItem
	 * registerItem = serviceRiskRegister.get(elementID); try {
	 * PropertyAccessorFactory.forBeanPropertyAccess(registerItem).
	 * setPropertyValue(fieldEditor.getFieldName(), fieldEditor.getValue());
	 * serviceRiskRegister.saveOrUpdate(registerItem); return
	 * JsonMessage.Success(messageSource.getMessage(
	 * "success.risk_register.updated", null,
	 * "Risk register was successfully updated", locale)); } catch
	 * (TrickException e) { TrickLogManager.Persist(e); return
	 * JsonMessage.Error(messageSource.getMessage(e.getCode(),
	 * e.getParameters(), e.getMessage(), locale)); } catch (Exception e) {
	 * TrickLogManager.Persist(e); return
	 * JsonMessage.Error(messageSource.getMessage("error.edit.save.field", null,
	 * "Data cannot be saved", locale)); } } else return
	 * JsonMessage.Error(messageSource.getMessage(
	 * "error.edit.field.unsupported", null, "Field cannot be edited", locale));
	 * } catch (TrickException e) { TrickLogManager.Persist(e); return
	 * JsonMessage.Error(messageSource.getMessage(e.getCode(),
	 * e.getParameters(), e.getMessage(), locale)); } catch (Exception e) { //
	 * return error TrickLogManager.Persist(e); return
	 * JsonMessage.Error(messageSource.getMessage("error.unknown.edit.field",
	 * null, "An unknown error occurred while updating field", locale)); } }
	 */

	/**
	 * setFieldData: <br>
	 * Description
	 * 
	 * @param field
	 * @param object
	 * @param fieldEditor
	 * @param pattern
	 * @return true / false
	 */
	public static boolean SetFieldData(Field field, Object object, FieldEditor fieldEditor) {
		return SetFieldData(field, object, fieldEditor, null);
	}

	private static Boolean SetFieldValue(Object object, Field field, Object value) {
		try {
			field.setAccessible(true);
			field.set(object, value);
			return true;
		} catch (TrickException e) {
			throw e;
		} catch (Exception e) {
			return false;
		}
	}

	private Object FieldValue(FieldEditor fieldEditor) {
		return FieldValue(fieldEditor, null);
	}

	/**
	 * setFieldData: <br>
	 * Description
	 * 
	 * @param field
	 * @param object
	 * @param fieldEditor
	 * @param pattern
	 * @return true / false
	 */
	public static boolean SetFieldData(Field field, Object object, FieldEditor fieldEditor, String pattern) {
		try {
			Object value = FieldValue(fieldEditor, pattern);
			if (value == null)
				return false;
			return SetFieldValue(object, field, value);
		} catch (TrickException e) {
			throw e;
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return false;
		}
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
			else if (fieldEditor.getType().equalsIgnoreCase("integer")) {
				if (fieldEditor.getValue() instanceof Integer)
					return (Integer) fieldEditor.getValue();
				return NumberFormat.getInstance(Locale.FRANCE).parse(fieldEditor.getValue().toString()).intValue();
			} else if (fieldEditor.getType().equalsIgnoreCase("double")) {
				if (fieldEditor.getValue() instanceof Double)
					return (Double) fieldEditor.getValue();
				return NumberFormat.getInstance(Locale.FRANCE).parse(fieldEditor.getValue().toString()).doubleValue();
			} else if (fieldEditor.getType().equalsIgnoreCase("float")) {
				if (fieldEditor.getValue() instanceof Float)
					return (Float) fieldEditor.getValue();
				return NumberFormat.getInstance(Locale.FRANCE).parse(fieldEditor.getValue().toString()).floatValue();
			} else if (fieldEditor.getType().equalsIgnoreCase("boolean"))
				return Boolean.parseBoolean(fieldEditor.getValue().toString());
			else if (fieldEditor.getType().equalsIgnoreCase("date")) {
				DateFormat format = new SimpleDateFormat(pattern == null ? "yyyy-MM-dd hh:mm:ss" : pattern);
				return format.parse(fieldEditor.getValue().toString());
			}
		} catch (NumberFormatException e) {
			throw new TrickException("error.parse.number", String.format("%s is not a number", fieldEditor.getValue()), String.valueOf(fieldEditor.getValue().toString()));
		} catch (ParseException e) {
			if (fieldEditor.getType().equalsIgnoreCase("date"))
				throw new TrickException("error.parse.date", String.format("%s is not valid date", fieldEditor.getValue()), String.valueOf(fieldEditor.getValue().toString()));
			else
				throw new TrickException("error.parse.number", String.format("%s is not a number", fieldEditor.getValue()), String.valueOf(fieldEditor.getValue().toString()));
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

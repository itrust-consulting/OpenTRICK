package lu.itrust.business.TS.controller.analysis;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.lang.reflect.Field;
import java.security.Principal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lu.itrust.business.TS.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.hbm.DAOHibernate;
import lu.itrust.business.TS.database.service.ServiceActionPlan;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssessment;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceDynamicParameter;
import lu.itrust.business.TS.database.service.ServiceHistory;
import lu.itrust.business.TS.database.service.ServiceImpactParameter;
import lu.itrust.business.TS.database.service.ServiceItemInformation;
import lu.itrust.business.TS.database.service.ServiceLikelihoodParameter;
import lu.itrust.business.TS.database.service.ServiceMaturityParameter;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServicePhase;
import lu.itrust.business.TS.database.service.ServiceRiskAcceptanceParameter;
import lu.itrust.business.TS.database.service.ServiceRiskInformation;
import lu.itrust.business.TS.database.service.ServiceRiskProfile;
import lu.itrust.business.TS.database.service.ServiceRiskRegister;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.database.service.ServiceSimpleParameter;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.helper.FieldEditor;
import lu.itrust.business.TS.helper.FieldValue;
import lu.itrust.business.TS.helper.JSTLFunctions;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.helper.Result;
import lu.itrust.business.TS.helper.chartJS.item.ColorBound;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.analysis.ReportSetting;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.RiskRegisterItem;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.history.History;
import lu.itrust.business.TS.model.iteminformation.ItemInformation;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.helper.ParameterManager;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.TS.model.parameter.impl.MaturityParameter;
import lu.itrust.business.TS.model.parameter.impl.RiskAcceptanceParameter;
import lu.itrust.business.TS.model.parameter.impl.SimpleParameter;
import lu.itrust.business.TS.model.parameter.value.AbstractValue;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.parameter.value.impl.FormulaValue;
import lu.itrust.business.TS.model.parameter.value.impl.RealValue;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.scale.ScaleType;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.impl.AssetMeasure;
import lu.itrust.business.TS.model.standard.measure.impl.MaturityMeasure;
import lu.itrust.business.TS.model.standard.measure.impl.MeasureAssetValue;
import lu.itrust.business.TS.model.standard.measure.impl.MeasureProperties;
import lu.itrust.business.TS.model.standard.measure.impl.NormalMeasure;
import lu.itrust.business.TS.validator.AssessmentValidator;
import lu.itrust.business.TS.validator.AssetValidator;
import lu.itrust.business.TS.validator.BounedParameterValidator;
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
@RestController
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/EditField")
public class ControllerFieldEditor {

	@Autowired
	private ServiceItemInformation serviceItemInformation;

	@Autowired
	private ServiceSimpleParameter serviceSimpleParameter;

	@Autowired
	private ServiceDynamicParameter serviceDynamicParameter;

	@Autowired
	private ServiceLikelihoodParameter serviceLikelihoodParameter;

	@Autowired
	private ServiceImpactParameter serviceImpactParameter;

	@Autowired
	private ServiceMaturityParameter serviceMaturityParameter;

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
	private ServiceRiskAcceptanceParameter serviceRiskAcceptanceParameter;

	@Autowired
	private ServicePhase servicePhase;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private ServiceRiskRegister serviceRiskRegister;

	@Autowired
	private ServiceRiskProfile serviceRiskProfile;

	private Pattern computeCostPattern = Pattern.compile("internalWL|externalWL|investment|lifetime|internalMaintenance|externalMaintenance|recurrentInvestment");

	private Pattern riskProfileNoFieldPattern = Pattern.compile("^*\\.id$|^\\*.asset\\.*$|^*.scenario\\.*");

	private Pattern assessmentEditableField = Pattern.compile("comment|hiddenComment|likelihood|uncertainty|owner");

	private Pattern hexColor = Pattern.compile("^#?[A-Fa-f0-9]{6}$");

	private Pattern smlPatten = Pattern.compile("^SMLLevel[0-5]{1}$");

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
	@PostMapping(value = "/ActionPlanEntry/{elementID}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'ActionPlanEntry', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String actionplanentry(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {
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
	 * assessment: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/Assessment/{elementID}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Assessment', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String assessment(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {
		int idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Result result = updateAssessment(fieldEditor, serviceAssessment.getFromAnalysisById(idAnalysis, elementID), idAnalysis, locale, false);
		return result.isError() ? JsonMessage.Error(result.getMessage()) : JsonMessage.Success(result.getMessage());
	}

	@PostMapping(value = "/Asset/{elementID}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String asset(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {
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

			Field field = FindField(Asset.class, fieldEditor.getFieldName());
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
	 * assessment: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/Estimation/Update", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idAsset, 'Asset', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY) and @permissionEvaluator.userIsAuthorized(#session, #idScenario, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public Result estimation(@RequestBody FieldEditor fieldEditor, @RequestParam("asset") int idAsset, @RequestParam("scenario") int idScenario, HttpSession session, Locale locale,
			Principal principal) {
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

	/**
	 * Impact: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/ImpactParameter/{elementID}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'ImpactParameter', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String extendedParameter(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {
		try {
			if (fieldEditor.getFieldName().equals("acronym"))
				return JsonMessage.Error(messageSource.getMessage("error.field.not.support.live.edition", null, "Field does not support editing on the fly", locale));
			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// retrieve parameter
			ImpactParameter parameter = serviceImpactParameter.findOne(elementID, idAnalysis);
			// set validator and validate parameter
			if (!serviceDataValidation.isRegistred(IBoundedParameter.class))
				serviceDataValidation.register(new BounedParameterValidator());
			// retireve value
			Object value = FieldValue(fieldEditor, null);
			// validate
			String error = serviceDataValidation.validate(parameter, fieldEditor.getFieldName(), value);
			// return error validation
			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, locale));
			// set field
			Field field = FindField(ImpactParameter.class, fieldEditor.getFieldName());
			if (field == null)
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
			field.setAccessible(true);
			// set field data
			if (SetFieldData(field, parameter, fieldEditor, null)) {
				switch (fieldEditor.getFieldName()) {
				case "value":
					parameter.setValue(parameter.getValue() * 1000);
					List<ImpactParameter> parameters = serviceImpactParameter.findByTypeAndAnalysisId(parameter.getType(), idAnalysis);
					ImpactParameter.ComputeScales(parameters);
					UpdateAssessment(idAnalysis, parameter.getType(), parameters);
					break;
				case "label":
					List<ImpactParameter> impactParameters = serviceImpactParameter.findByIdAnalysisAndLevel(idAnalysis, parameter.getLevel());
					impactParameters.forEach(impact -> impact.setLabel(fieldEditor.getValue().toString()));
					serviceImpactParameter.saveOrUpdate(impactParameters);
					break;
				default:
					serviceImpactParameter.saveOrUpdate(parameter);
					break;
				}
				// return success message
				return JsonMessage.Success(messageSource.getMessage("success.impact.update", null, "Impact was successfully update", locale));
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
	 * history: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/History/{elementID}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'History', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String history(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, Locale locale, HttpSession session, Principal principal) throws Exception {

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
			Field field = FindField(History.class, fieldEditor.getFieldName());

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
	 * itemInformation: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/ItemInformation/{elementID}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'ItemInformation', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String itemInformation(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, Locale locale, HttpSession session, Principal principal) throws Exception {
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
	@PostMapping(value = "/LikelihoodParameter/{elementID}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'LikelihoodParameter', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String likelihoodParameter(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {
		try {
			if (fieldEditor.getFieldName().equals("acronym"))
				return JsonMessage.Error(messageSource.getMessage("error.field.not.support.live.edition", null, "Field does not support editing on the fly", locale));
			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// retrieve parameter
			LikelihoodParameter parameter = serviceLikelihoodParameter.findOne(elementID, idAnalysis);
			// set validator and validate parameter
			if (!serviceDataValidation.isRegistred(IBoundedParameter.class))
				serviceDataValidation.register(new BounedParameterValidator());
			// retireve value
			Object value = FieldValue(fieldEditor, null);
			// validate
			String error = serviceDataValidation.validate(parameter, fieldEditor.getFieldName(), value);
			// return error validation
			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, locale));
			// set field
			Field field = FindField(LikelihoodParameter.class, fieldEditor.getFieldName());

			if (field == null)
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
			field.setAccessible(true);

			// set field data
			if (SetFieldData(field, parameter, fieldEditor, null)) {
				if ("value".equals(fieldEditor.getFieldName())) {
					parameter.setValue(parameter.getValue());
					List<LikelihoodParameter> parameters = serviceLikelihoodParameter.findByAnalysisId(idAnalysis);
					ParameterManager.ComputeLikehoodValue(parameters);
					UpdateAssessment(idAnalysis, parameters);
				} else
					serviceLikelihoodParameter.saveOrUpdate(parameter);

				// return success message
				return JsonMessage.Success(messageSource.getMessage("success.likelihood.update", null, "Likelihood was successfully update", locale));
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
	 * maturityMeasure: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/MaturityMeasure/{elementID}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String maturityMeasure(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {

		try {

			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// retrieve measure
			Measure measure = serviceMeasure.getFromAnalysisById(idAnalysis, elementID);
			// check if field is implementationrate
			if (fieldEditor.getFieldName().equalsIgnoreCase("implementationRate")) {

				// retrieve parameters
				List<SimpleParameter> simpleParameters = serviceSimpleParameter.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME, idAnalysis);

				// retrieve single parameters
				Analysis analysis = serviceAnalysis.get(idAnalysis);

				// get value
				double value = Double.parseDouble(fieldEditor.getValue().toString());

				// parse parameters
				for (IParameter parameter : simpleParameters) {

					// find the parameter
					if (Math.abs(parameter.getValue().doubleValue() - value) < 1e-5) {

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

	@PostMapping(value = "/MaturityParameter/{elementID}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'SimpleParameter', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String maturityparameter(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, Locale locale, HttpSession session, Principal principal) throws Exception {

		try {
			if (!smlPatten.matcher(fieldEditor.getFieldName()).matches())
				return JsonMessage.Error(messageSource.getMessage("error.field.not.support.live.edition", null, "Field does not support editing on the fly", locale));
			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// get parameter object
			MaturityParameter parameter = (MaturityParameter) serviceMaturityParameter.findOne(elementID, idAnalysis);
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
			Field field = FindField(MaturityParameter.class, fieldEditor.getFieldName());
			// set value /100 to save as values between 0 and 1
			// set field data
			if (SetFieldValue(parameter, field, (Double) value * 0.01)) {
				// update field
				serviceMaturityParameter.saveOrUpdate(parameter);
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
	 * measure: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/Measure/{elementID}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String measure(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {

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
						List<String> acronyms = serviceLikelihoodParameter.findAcronymByAnalysisId(idAnalysis);
						acronyms.addAll(serviceDynamicParameter.findAcronymByAnalysisId(idAnalysis));
						if (!(new StringExpressionParser(value.toString(), StringExpressionParser.IMPLEMENTATION).isValid(acronyms)))
							return JsonMessage.Error(messageSource.getMessage("error.edit.type.field.expression", null,
									"Invalid expression. Check the syntax and make sure that all used parameters exist.", locale));
						measure.setImplementationRate(value);
					} else
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
								if (fieldEditor.getFieldName().equals(assetTypeValue.getAssetType().getName())) {
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
	 * parameter: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/SimpleParameter/{elementID}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'SimpleParameter', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String parameter(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, Locale locale, HttpSession session, Principal principal) throws Exception {

		try {
			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// get parameter object
			SimpleParameter simpleParameter = serviceSimpleParameter.findOne(elementID, idAnalysis);
			// validate parameter
			ValidatorField validator = serviceDataValidation.findByClass(simpleParameter.getClass());
			if (validator == null)
				serviceDataValidation.register(new ParameterValidator());
			// retireve value
			Object value = FieldValue(fieldEditor);
			// validate value
			String error = serviceDataValidation.validate(simpleParameter, fieldEditor.getFieldName(), value);
			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, locale));
			switch (simpleParameter.getTypeName()) {
			case Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME:
			case Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_LEVEL_PER_SML_NAME:
			case Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME:
				if (((double) value) < 0 || ((double) value) > 100)
					return JsonMessage.Error(messageSource.getMessage("error.parameter.value.out_of_bound", new Object[] { value },
							String.format("Invalid input: value (%f) should be between 0 and 100", value), locale));
				break;
			case Constant.PARAMETERTYPE_TYPE_SINGLE_NAME:
				if (simpleParameter.getDescription().equals(Constant.PARAMETER_LIFETIME_DEFAULT)) {
					if (((double) value) <= 0)
						return JsonMessage.Error(messageSource.getMessage("error.edit.parameter.default_lifetime", null, "Default lifetime has to be > 0", locale));
				} else if (simpleParameter.getDescription().equals(Constant.PARAMETER_MAX_RRF) || simpleParameter.getDescription().equals(Constant.SOA_THRESHOLD)) {
					if (((double) value) < 0 || ((double) value) > 100)
						return JsonMessage.Error(messageSource.getMessage("error.parameter.value.out_of_bound", new Object[] { value },
								String.format("Invalid input: value (%f) should be between 0 and 100", value), locale));
				}
				break;
			}
			// create field
			Field field = FindField(SimpleParameter.class, fieldEditor.getFieldName());
			// set field data
			if (SetFieldData(field, simpleParameter, fieldEditor)) {
				// update field
				serviceSimpleParameter.saveOrUpdate(simpleParameter);
				// return success message
				return JsonMessage.Success(messageSource.getMessage("success.parameter.updated", null, "SimpleParameter was successfully updated", locale));
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
	 * phase: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param session
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/Phase/{elementID}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Phase', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String phase(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {

		try {
			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// retireve phase
			Phase phase = servicePhase.getFromAnalysisById(idAnalysis, elementID);
			// set field
			Field field = FindField(Phase.class, fieldEditor.getFieldName());
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

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	@PostMapping(value = "/ReportSetting", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public Object reportSetting(@RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) {
		final Map<String, String> result = new LinkedHashMap<>();
		final ReportSetting setting = ReportSetting.valueOf(fieldEditor.getFieldName());
		if (setting == null)
			result.put("error", messageSource.getMessage("error.report.setting.not.found", null, locale));
		else if (setting == ReportSetting.CEEL_COLOR)
			result.put("error", messageSource.getMessage("error.report.setting.not.allowed", null, locale));
		else {
			final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
			final String color = fieldEditor.getValue() == null ? null : fieldEditor.getValue().toString();
			if (StringUtils.isEmpty(color))
				analysis.setSetting(setting.name(), null);
			else if (!hexColor.matcher(color).matches())
				result.put("error", messageSource.getMessage("error.hex.color.excepted", null, locale));
			else
				analysis.setSetting(setting.name(), color.replaceFirst("#", ""));

			if (result.isEmpty()) {
				serviceAnalysis.saveOrUpdate(analysis);
				result.put("value", analysis.findSetting(setting));
				result.put("success", messageSource.getMessage("success.update.report.setting", null, locale));
			}
		}
		return result;
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
	@PostMapping(value = "/RiskAcceptanceParameter/{elementID}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'RiskAcceptanceParameter', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String riskAcceptanceParameter(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, Locale locale, HttpSession session, Principal principal)
			throws Exception {
		try {
			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// get parameter object
			RiskAcceptanceParameter simpleParameter = serviceRiskAcceptanceParameter.findOne(elementID, idAnalysis);
			// create field
			Field field = FindField(RiskAcceptanceParameter.class, fieldEditor.getFieldName());
			// set field data
			if (fieldEditor.getFieldName().equals("color") && !hexColor.matcher(fieldEditor.getValue().toString()).matches())
				return JsonMessage.Success(messageSource.getMessage("error.hex.color.excepted", null, locale));
			if (SetFieldData(field, simpleParameter, fieldEditor)) {
				// update field
				serviceRiskAcceptanceParameter.saveOrUpdate(simpleParameter);
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

	@PostMapping(value = "/RiskInformation/{elementID}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'RiskInformation', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String riskInformation(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {
		try {
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			RiskInformation riskInformation = serviceRiskInformation.getFromAnalysisById(idAnalysis, elementID);
			// set field
			Field field = FindField(RiskInformation.class, fieldEditor.getFieldName());
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
	@PostMapping(value = "/RiskProfile/{elementID}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'RiskProfile', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String riskProfile(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {
		int idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Result result = updateRiskProfile(fieldEditor, serviceRiskProfile.getFromAnalysisById(idAnalysis, elementID), idAnalysis, locale);
		return result.isError() ? JsonMessage.Error(result.getMessage()) : JsonMessage.Success(result.getMessage());
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
	@PostMapping(value = "/Scenario/{elementID}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Scenario', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String scenario(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {
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
	@PostMapping(value = "/SOA/{elementID}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String soa(@PathVariable int elementID, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) throws Exception {

		try {
			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// retrieve measure
			AbstractNormalMeasure measure = (AbstractNormalMeasure) serviceMeasure.getFromAnalysisById(idAnalysis, elementID);

			MeasureProperties mesprep = DAOHibernate.Initialise(measure.getMeasurePropertyList());

			Field field = FindField(MeasureProperties.class, fieldEditor.getFieldName());

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

	@PostMapping(value = "/Measure/{id}/Update", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #id, 'Measure', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public Result updateMeasure(@PathVariable int id, @RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) {
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
					value = serviceSimpleParameter.findOne((Integer) FieldValue(fieldEditor), idAnalysis);
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

	private List<ColorBound> createColorBounds(Integer idAnalysis) {
		return ChartGenerator.GenerateColorBounds(serviceRiskAcceptanceParameter.findByAnalysisId(idAnalysis));

	}

	private ValueFactory createFactoryForAssessment(Integer idAnalysis) {
		final ValueFactory factory = new ValueFactory(serviceImpactParameter.findByAnalysisId(idAnalysis));
		factory.add(serviceLikelihoodParameter.findByAnalysisId(idAnalysis));
		factory.add(serviceDynamicParameter.findByAnalysisId(idAnalysis));
		return factory;
	}

	private Object FieldValue(FieldEditor fieldEditor) {
		return FieldValue(fieldEditor, null);
	}

	private String format(double value, NumberFormat numberFormat, int decimal) {
		numberFormat.setMaximumFractionDigits(decimal);
		return numberFormat.format(JSTLFunctions.round(value, decimal));
	}

	private Result updateAssessment(FieldEditor fieldEditor, Assessment assessment, Integer idAnalysis, Locale locale, boolean netImportance) {

		try {
			if (assessment == null)
				return Result.Error(messageSource.getMessage("error.assessment.not_found", null, "Assessment cannot be found", locale));
			if (!(assessment.hasImpact(fieldEditor.getFieldName()) || assessmentEditableField.matcher(fieldEditor.getFieldName()).find()))
				return Result.Error(messageSource.getMessage("error.field.not.support.live.edition", null, "Field does not support editing on the fly", locale));
			// set validator
			if (!serviceDataValidation.isRegistred(Assessment.class))
				serviceDataValidation.register(new AssessmentValidator());
			ValueFactory factory = null;
			IValue impactToDelete = null;
			// retrieve all acronyms of impact and likelihood
			if (assessment.hasImpact(fieldEditor.getFieldName())) {
				final Optional<Double> optional = ParseFrNumber(fieldEditor.getValue().toString());
				factory = createFactoryForAssessment(idAnalysis);
				if (optional.isPresent()) {
					final double value = optional.get();
					if (value < 0)
						return Result.Error(messageSource.getMessage("error.negatif.impact.value", null, "Impact cannot be negative", locale));
					fieldEditor.setValue(value * 1000);
				} else if (!factory.hasAcronym(fieldEditor.getValue().toString(), fieldEditor.getFieldName())) {
					if (!fieldEditor.getFieldName().equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
						return Result.Error(messageSource.getMessage("error.edit.field.value.unsupported", null, "Given value is not supported", locale));
				}

				final IValue oldValue = assessment.getImpact(fieldEditor.getFieldName());

				final IValue newValue = factory.findValue(fieldEditor.getValue(), fieldEditor.getFieldName());

				if (newValue == null)
					return Result.Error(messageSource.getMessage("error.edit.field.value.unsupported", null, "Given value is not supported", locale));
				if (!oldValue.merge(newValue)) {
					assessment.setImpact(newValue);
					impactToDelete = oldValue;
				}
			} else if ("likelihood".equals(fieldEditor.getFieldName())) {
				factory = createFactoryForAssessment(idAnalysis);
				if (fieldEditor.getValue().toString().equalsIgnoreCase("na"))
					assessment.setLikelihood("0");
				else {
					final Optional<Double> optional = ParseFrNumber(fieldEditor.getValue().toString());
					if (optional.isPresent()) {
						final double value = optional.get();
						if (value < 0)
							return Result.Error(messageSource.getMessage("error.negatif.probability.value", null, "Probability cannot be negative", locale));
						fieldEditor.setValue(value);
					} else if (!new StringExpressionParser(fieldEditor.getValue().toString(), StringExpressionParser.PROBABILITY).isValid(factory))
						return Result.Error(messageSource.getMessage("error.edit.field.value.unsupported", null, "Given value is not supported", locale));
					assessment.setLikelihood(fieldEditor.getValue().toString());
				}
			} else {
				// get value
				final Object value = FieldValue(fieldEditor);
				// validate new value
				final String error = serviceDataValidation.validate(assessment, fieldEditor.getFieldName(), value);
				if (error != null)
					// return error message
					return Result.Error(serviceDataValidation.ParseError(error, messageSource, locale));
				// init field
				final Field field = assessment.getClass().getDeclaredField(fieldEditor.getFieldName());
				// set data to field
				if (!SetFieldData(field, assessment, fieldEditor))
					// return error message
					return Result.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
			}
			Result result = Result.Success(messageSource.getMessage("success.assessment.updated", null, "Assessment was successfully updated", locale));
			// compute new ALE
			if (factory != null || fieldEditor.getFieldName().equals("uncertainty")) {
				if (factory == null)
					factory = createFactoryForAssessment(idAnalysis);
				AnalysisType type = serviceAnalysis.getAnalysisTypeById(idAnalysis);
				if (netImportance && AnalysisType.isQualitative(type))
					result.add(updateFieldValue(idAnalysis, factory.findImportance(assessment), new FieldValue("computedNetImportance")));
				if (AnalysisType.isQuantitative(type)) {
					AssessmentAndRiskProfileManager.ComputeAlE(assessment, factory);
					NumberFormat numberFormat = NumberFormat.getInstance(Locale.FRANCE);
					result.add(new FieldValue("ALE", format(assessment.getALE() * .001, numberFormat, 2), format(assessment.getALE(), numberFormat, 0) + " €"));
					result.add(new FieldValue("ALEO", format(assessment.getALEO() * .001, numberFormat, 2), format(assessment.getALEO(), numberFormat, 0) + " €"));
					result.add(new FieldValue("ALEP", format(assessment.getALEP() * .001, numberFormat, 2), format(assessment.getALEP(), numberFormat, 0) + " €"));
					if (fieldEditor.getFieldName().equals("IMPACT"))
						result.add(new FieldValue("IMPACT", null, assessment.getImpact("IMPACT").getVariable()));
					else if (fieldEditor.getFieldName().equals("likelihood"))
						result.add(new FieldValue("likelihood", null, format(assessment.getLikelihoodReal(), numberFormat, 3)));
					else {
						RiskRegisterItem registerItem = serviceRiskRegister.getByAssetIdAndScenarioId(assessment.getAsset().getId(), assessment.getScenario().getId());
						if (registerItem != null) {
							double uncertainty = assessment.getUncertainty(), qUncertainty = 1.0 / uncertainty;
							result.add(new FieldValue("ALE-RAW", format(registerItem.getRawEvaluation().getImportance() * .001, numberFormat, 2),
									format(registerItem.getRawEvaluation().getImportance(), numberFormat, 0) + " €"));
							result.add(new FieldValue("ALEO-RAW", format(registerItem.getRawEvaluation().getImportance() * qUncertainty * .001, numberFormat, 2),
									format(registerItem.getRawEvaluation().getImportance(), numberFormat, 0) + " €"));
							result.add(new FieldValue("ALEP-RAW", format(registerItem.getRawEvaluation().getImportance() * uncertainty * .001, numberFormat, 2),
									format(registerItem.getRawEvaluation().getImportance() * assessment.getUncertainty(), numberFormat, 0) + " €"));

							result.add(new FieldValue("ALE-EXP", format(registerItem.getExpectedEvaluation().getImportance() * .001, numberFormat, 2),
									format(registerItem.getExpectedEvaluation().getImportance(), numberFormat, 0) + " €"));
							result.add(new FieldValue("ALEO-EXP", format(registerItem.getExpectedEvaluation().getImportance() * qUncertainty * .001, numberFormat, 2),
									format(registerItem.getExpectedEvaluation().getImportance(), numberFormat, 0) + " €"));
							result.add(new FieldValue("ALEP-EXP", format(registerItem.getExpectedEvaluation().getImportance() * uncertainty * .001, numberFormat, 2),
									format(registerItem.getExpectedEvaluation().getImportance() * assessment.getUncertainty(), numberFormat, 0) + " €"));
						}
					}
				}
			}
			if (impactToDelete != null)
				serviceAssessment.delete(impactToDelete);
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

	private Result updateAssessment(FieldEditor fieldEditor, int idAsset, int idScenario, HttpSession session, Locale locale) {
		return updateAssessment(fieldEditor, serviceAssessment.getByAssetAndScenario(idAsset, idScenario), (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), locale,
				true);
	}

	private void UpdateAssessment(Integer idAnalysis, List<LikelihoodParameter> parameters) {
		List<Assessment> assessments = serviceAssessment.getAllFromAnalysis(idAnalysis);
		if (assessments.isEmpty()) {
			ValueFactory factory = new ValueFactory(parameters);
			factory.add(serviceDynamicParameter.findByAnalysisId(idAnalysis));
			factory.add(serviceImpactParameter.findByTypeAndAnalysisId(Constant.DEFAULT_IMPACT_NAME, idAnalysis));
			assessments.forEach(assessment -> {
				AssessmentAndRiskProfileManager.ComputeAlE(assessment, factory);
				serviceAssessment.saveOrUpdate(assessment);
			});
		}
		serviceLikelihoodParameter.saveOrUpdate(parameters);
	}

	private void UpdateAssessment(Integer idAnalysis, ScaleType type, List<ImpactParameter> parameters) {
		final List<Assessment> assessments = serviceAssessment.getAllFromAnalysis(idAnalysis);
		if (!assessments.isEmpty()) {
			final ValueFactory factory = new ValueFactory(parameters);
			factory.add(serviceDynamicParameter.findByAnalysisId(idAnalysis));
			factory.add(serviceLikelihoodParameter.findByAnalysisId(idAnalysis));
			assessments.forEach(assessment -> {
				assessment.getImpacts().stream().filter(value -> (value instanceof RealValue || value instanceof FormulaValue) && value.getName().equals(type.getName())).findAny()
						.ifPresent(value -> {
							final IValue impact = value instanceof AbstractValue ? factory.findValue(value.getReal(), type.getName())
									: factory.findValue(value.getVariable(), type.getName());
							if (impact != null) {
								if (value instanceof AbstractValue)
									((RealValue) value).setParameter(((AbstractValue) impact).getParameter());
								else
									value.merge(factory.findValue(value.getVariable(), type.getName()));
							}
							AssessmentAndRiskProfileManager.ComputeAlE(assessment, value, factory);
							serviceAssessment.saveOrUpdate(assessment);
						});
			});
		}
		serviceImpactParameter.saveOrUpdate(parameters);
	}

	/**
	 * Value will be modify<br>
	 * Update : value (importance), color, title
	 * 
	 * @param idAnalysis
	 * @param importance
	 * @param value
	 * @return value
	 */
	private FieldValue updateFieldValue(Integer idAnalysis, int importance, FieldValue value) {
		createColorBounds(idAnalysis).stream().filter(v -> v.isAccepted(importance)).findAny().ifPresent(c -> {
			value.setColor(c.getColor());
			value.setTitle(c.getLabel());
		});
		value.setValue(importance);
		return value;
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
				Object id = FieldValue(fieldEditor);
				if (id instanceof Integer) {
					if (fields[2].equals("probability")) {
						LikelihoodParameter parameter = serviceLikelihoodParameter.findOne((Integer) id, idAnalysis);
						if (parameter == null)
							return Result.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
						probaImpact.setProbability(parameter);
					} else {
						ImpactParameter parameter = serviceImpactParameter.findOne((Integer) id, idAnalysis);
						if (parameter == null)
							return Result.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
						probaImpact.add(parameter);
					}
				} else
					return Result.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));

				result.add(
						updateFieldValue(idAnalysis, probaImpact.getImportance(), new FieldValue(fields[1].startsWith("raw") ? "computedRawImportance" : "computedExpImportance")));
			} else if (field.getType().isAssignableFrom(RiskStrategy.class))
				riskProfile.setRiskStrategy(RiskStrategy.valueOf(fieldEditor.getValue().toString()));
			else if (field.getName().equals("identifier")) {
				String identifier = fieldEditor.getValue() == null ? "" : fieldEditor.getValue().toString().trim();
				if (!identifier.isEmpty() && serviceRiskProfile.isUsed(identifier, idAnalysis))
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

	private static Optional<Double> ParseFrNumber(String raw) {
		final ParsePosition position = new ParsePosition(0);
		final Number value = NumberFormat.getInstance(Locale.FRANCE).parse(raw, position);
		if (position.getIndex() >= raw.length())
			return Optional.of(value == null ? 0d : value.doubleValue());
		return Optional.empty();
	}
}

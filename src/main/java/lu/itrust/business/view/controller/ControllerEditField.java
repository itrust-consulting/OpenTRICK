package lu.itrust.business.view.controller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.ExtendedParameter;
import lu.itrust.business.TS.History;
import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.MeasureProperties;
import lu.itrust.business.TS.NormMeasure;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.Phase;
import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.ParameterManager;
import lu.itrust.business.component.helper.FieldEditor;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.dao.hbm.DAOHibernate;
import lu.itrust.business.service.ServiceActionPlan;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAssessment;
import lu.itrust.business.service.ServiceDataValidation;
import lu.itrust.business.service.ServiceHistory;
import lu.itrust.business.service.ServiceItemInformation;
import lu.itrust.business.service.ServiceMeasure;
import lu.itrust.business.service.ServiceParameter;
import lu.itrust.business.service.ServicePhase;
import lu.itrust.business.validator.AssessmentValidator;
import lu.itrust.business.validator.ExtendedParameterValidator;
import lu.itrust.business.validator.HistoryValidator;
import lu.itrust.business.validator.ParameterValidator;
import lu.itrust.business.validator.field.ValidatorField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
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
@RequestMapping("/EditField")
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
	private ServicePhase servicePhase;

	/**
	 * itemInformation: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/ItemInformation", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String itemInformation(@RequestBody FieldEditor fieldEditor, Locale locale, HttpSession session, Principal principal) {

		try {

			// get item information object from id
			ItemInformation itemInformation = serviceItemInformation.get(fieldEditor.getId());
			if (itemInformation == null)
				return JsonMessage.Error(messageSource.getMessage("error.itemInformation.not_found", null, "ItemInformation cannot be found", locale));

			// initialise field
			Field field = itemInformation.getClass().getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);

			// set field with new data
			if (SetFieldData(field, itemInformation, fieldEditor, null)) {

				// update iteminformation
				serviceItemInformation.saveOrUpdate(itemInformation);

				// return success message
				return JsonMessage.Success(messageSource.getMessage("success.itemInformation.updated", null, "ItemInformation was successfully updated", locale));
			} else

				// return error message
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
		} catch (NumberFormatException e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (IllegalAccessException e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (InvocationTargetException e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (RuntimeException e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (Exception e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		}
	}

	/**
	 * parameter: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Parameter", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String parameter(@RequestBody FieldEditor fieldEditor, Locale locale, HttpSession session, Principal principal) {

		try {

			// get parameter object
			Parameter parameter = serviceParameter.get(fieldEditor.getId());
			if (parameter == null)
				return JsonMessage.Error(messageSource.getMessage("error.parameter.not_found", null, "Parameter cannot be found", locale));

			// validate parameter
			ValidatorField validator = serviceDataValidation.findByClass(parameter.getClass());
			if (validator == null)
				serviceDataValidation.register(new ParameterValidator());

			// retireve value
			Object value = FieldValue(fieldEditor, null);

			// validate value
			String error = serviceDataValidation.validate(parameter, fieldEditor.getFieldName(), value);
			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, locale));

			// create field
			Field field = parameter.getClass().getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);

			// set field data
			if (SetFieldData(field, parameter, fieldEditor, null)) {

				// update field
				serviceParameter.saveOrUpdate(parameter);

				// return success message
				return JsonMessage.Success(messageSource.getMessage("success.parameter.updated", null, "Parameter was successfully updated", locale));
			} else

				// return error message
				return JsonMessage.Success(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
		} catch (NoSuchFieldException e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (SecurityException e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (NumberFormatException e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (IllegalArgumentException e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (IllegalAccessException e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (ParseException e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (Exception e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
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
	 */
	@RequestMapping(value = "/ExtendedParameter", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String extendedParameter(@RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) {
		try {

			// retrieve analysis id
			Integer id = (Integer) session.getAttribute("selectedAnalysis");

			// check if analysis exist
			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));
			if (!serviceAnalysis.exist(id))
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			// retrieve parameter
			ExtendedParameter parameter = (ExtendedParameter) serviceParameter.get(fieldEditor.getId());
			if (parameter == null)
				return JsonMessage.Error(messageSource.getMessage("error.parameter.not_found", null, "Parameter cannot be found", locale));

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
			if ("value id type description".contains(fieldEditor.getFieldName()))
				field = parameter.getClass().getSuperclass().getDeclaredField(fieldEditor.getFieldName());
			else
				field = parameter.getClass().getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);

			// set field data
			if (SetFieldData(field, parameter, fieldEditor, null)) {

				// update field
				serviceParameter.saveOrUpdate(parameter);

				// retrieve parameters
				List<ExtendedParameter> parameters = serviceParameter.findExtendedByAnalysisAndType(id, parameter.getType());

				// update impact value
				ParameterManager.ComputeImpactValue(parameters);

				// update parameters
				serviceParameter.saveOrUpdate(parameters);

				// return success message
				return JsonMessage.Success(messageSource.getMessage("success.extendedParameter.update", null, "Parameter was successfully update", locale));
			} else

				// return error message
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
		} catch (NoSuchFieldException e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (SecurityException e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (NumberFormatException e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (IllegalArgumentException e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (IllegalAccessException e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (ParseException e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (Exception e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
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
	 */
	@RequestMapping(value = "/Assessment", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String assessment(@RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) {

		try {

			// retrieve analysis
			Integer id = (Integer) session.getAttribute("selectedAnalysis");

			if (id == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			// retrieve assessment
			Assessment assessment = serviceAssessment.get(fieldEditor.getId());
			if (assessment == null)
				return JsonMessage.Error(messageSource.getMessage("error.assessment.not_found", null, "Assessment cannot be found", locale));

			// set validator
			if (!serviceDataValidation.isRegistred(assessment.getClass()))
				serviceDataValidation.register(new AssessmentValidator());

			// get value
			Object value = FieldValue(fieldEditor, null);

			// retrieve all acronyms of impact and likelihood
			List<String> chooses = null;
			if ("impactRep,impactOp,impactLeg,impactFin".contains(fieldEditor.getFieldName()))
				chooses = serviceParameter.findAcronymByAnalysisAndType(id, Constant.PARAMETERTYPE_TYPE_IMPACT_NAME);
			else if ("likelihood".equals(fieldEditor.getFieldName()))
				chooses = serviceParameter.findAcronymByAnalysisAndType(id, Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME);

			// validate new value
			String error = serviceDataValidation.validate(assessment, fieldEditor.getFieldName(), value, chooses != null ? chooses.toArray() : null);
			if (error != null)

				// return error message
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, locale));

			// init field
			Field field = assessment.getClass().getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);

			// set data to field
			if (!SetFieldData(field, assessment, fieldEditor, null))

				// return error message
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));

			// retrieve parameters
			Map<String, ExtendedParameter> parameters = new LinkedHashMap<>();

			// parse parameters
			for (ExtendedParameter parameter : serviceParameter.findExtendedByAnalysis(id))

				// add parameter into map
				parameters.put(parameter.getAcronym(), parameter);

			// compute new ALE
			AssessmentManager.ComputeAlE(assessment, parameters);

			// update assessment
			serviceAssessment.saveOrUpdate(assessment);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.assessment.updated", null, "Assessment was successfully updated", locale));
		} catch (Exception e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		}

	}

	/**
	 * history: <br>
	 * Description
	 * 
	 * @param fieldEditor
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/History", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String history(@RequestBody FieldEditor fieldEditor, Locale locale, HttpSession session, Principal principal) {

		try {

			// retireve history object
			History history = serviceHistory.get(fieldEditor.getId());
			if (history == null)
				return JsonMessage.Error(messageSource.getMessage("error.history.not_found", null, "History cannot be found", locale));

			// get validator
			if (!serviceDataValidation.isRegistred(history.getClass()))
				serviceDataValidation.register(new HistoryValidator());

			// get new value
			Object value = FieldValue(fieldEditor, null);

			// validate
			String error = serviceDataValidation.validate(history, fieldEditor.getFieldName(), value);

			// return errors on validation fail
			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(error, messageSource, locale));

			// set field
			Field field = history.getClass().getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);

			// set field data
			if (SetFieldData(field, history, fieldEditor, null)) {

				// update history
				serviceHistory.saveOrUpdate(history);

				// return success message
				return JsonMessage.Success(messageSource.getMessage("success.history.updated", null, "History was successfully updated", locale));
			} else

				// return error rmessage
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
		} catch (NoSuchFieldException e) {

			// return error rmessage
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (SecurityException e) {

			// return error rmessage
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (NumberFormatException e) {

			// return error rmessage
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.format.number", null, "Number expected", locale));
		} catch (IllegalArgumentException e) {

			// return error rmessage
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (IllegalAccessException e) {

			// return error rmessage
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		} catch (ParseException e) {

			// return error rmessage
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.format.date", null, "Date expected", locale));
		} catch (Exception e) {

			// return error rmessage
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
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
	 */
	@RequestMapping(value = "/Measure", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String measure(@RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) {

		try {

			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			// retrieve measure
			Measure measure = serviceMeasure.findByIdAndAnalysis(fieldEditor.getId(), idAnalysis);
			if (measure == null)
				return JsonMessage.Error(messageSource.getMessage("error.measure.not_found", null, "Measure cannot be found", locale));

			// set field
			Field field = measure.getClass().getSuperclass().getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);

			// retrieve parameters
			List<Parameter> parameters = serviceParameter.findByAnalysisAndType(idAnalysis, Constant.PARAMETERTYPE_TYPE_SINGLE_NAME);

			// check if field is a phase
			if (fieldEditor.getFieldName().equals("phase")) {

				// retireve phase
				Integer number = 0;
				if (!fieldEditor.getValue().toString().equalsIgnoreCase("NA"))
					number = (Integer) FieldValue(fieldEditor, null);
				if (number == null)
					return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
				Phase phase = servicePhase.loadFromPhaseNumberAnalysis(number, idAnalysis);
				if (phase == null)
					return JsonMessage.Error(messageSource.getMessage("error.phase.not_found", null, "Phase cannot be found", locale));

				// set new phase number
				measure.setPhase(phase);

				// set field data
			} else if (!SetFieldData(field, measure, fieldEditor, null))
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));

			// compute new cost
			Measure.ComputeCost(measure, parameters);

			// update measure
			serviceMeasure.saveOrUpdate(measure);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.measure.updated", null, "Measure was successfully updated", locale));

		} catch (Exception e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.edit.save.field", null, "Data cannot be saved", locale));
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
	 */
	@RequestMapping(value = "/SOA", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String soa(@RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) {

		try {

			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			// retrieve measure
			NormMeasure measure = (NormMeasure) serviceMeasure.findByIdAndAnalysis(fieldEditor.getId(), idAnalysis);
			
			if (measure == null)
				return JsonMessage.Error(messageSource.getMessage("error.measure.not_found", null, "Measure cannot be found", locale));

			// set field
			
			// System.out.println("Fildname: " + fieldEditor.getFieldName());
			
			
			
			MeasureProperties mesprep = DAOHibernate.Initialise(measure.getMeasurePropertyList());
			Field field = mesprep.getClass().getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);

			// check if field is a phase
			if (!SetFieldData(field, mesprep, fieldEditor, null))
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));

			measure.setMeasurePropertyList(mesprep);
			
			// update measure
			serviceMeasure.saveOrUpdate(measure);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.measure.updated", null, "Measure was successfully updated", locale));

		} catch (Exception e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.edit.save.field", null, "Data cannot be saved", locale));
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
	 */
	@RequestMapping(value = "/MaturityMeasure", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String maturityMeasure(@RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) {

		try {

			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));

			// retrieve measure
			Measure measure = serviceMeasure.findOne(fieldEditor.getId());
			if (measure == null)
				return JsonMessage.Error(messageSource.getMessage("error.measure.not_found", null, "Measure cannot be found", locale));

			// check if field is implementationrate
			if (fieldEditor.getFieldName().equalsIgnoreCase("implementationRate")) {

				// retrieve parameters
				List<Parameter> parameters = serviceParameter.findByAnalysisAndType(idAnalysis, Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME);

				// retrieve single parameters
				List<Parameter> simpleParameters = serviceParameter.findByAnalysisAndType(idAnalysis, Constant.PARAMETERTYPE_TYPE_SINGLE_NAME);

				// get value
				double value = Double.parseDouble(fieldEditor.getValue().toString());

				// parse parameters
				for (Parameter parameter : parameters) {

					// TODO CHECK ???
					if (Math.abs(parameter.getValue() - value) < 1e-5) {

						// set new implementation rate
						measure.setImplementationRate(parameter);

						// recompute cost
						Measure.ComputeCost(measure, simpleParameters);

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
				return measure(fieldEditor, session, locale, principal);
		} catch (Exception e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
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
	 */
	@RequestMapping(value = "/ActionPlanEntry", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String actionplanentry(@RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) {

		try {

			// retrieve analysis
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");

			// get acion plan entry
			ActionPlanEntry ape = serviceActionPlan.get(fieldEditor.getId());
			if (ape == null)
				return JsonMessage.Error(messageSource.getMessage("error.actionplanentry.not_found", null, "Action Plan Entry cannot be found", locale));

			// retrieve phase
			Integer number = 0;
			if (!fieldEditor.getValue().toString().equalsIgnoreCase("NA"))
				number = (Integer) FieldValue(fieldEditor, null);
			if (number == null)
				return JsonMessage.Error(messageSource.getMessage("error.edit.type.field", null, "Data cannot be updated", locale));
			Phase phase = servicePhase.loadFromPhaseNumberAnalysis(number, idAnalysis);
			if (phase == null)
				return JsonMessage.Error(messageSource.getMessage("error.phase.not_found", null, "Phase cannot be found", locale));

			// set new phase value of measure
			ape.getMeasure().setPhase(phase);

			// update measure
			serviceMeasure.saveOrUpdate(ape.getMeasure());

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.ationplan.updated", null, "ActionPlan entry was successfully updated", locale));

		} catch (Exception e) {

			// retrun error message
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.edit.save.field", null, "Data cannot be saved", locale));
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
	 */
	@RequestMapping(value = "/Phase", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String phase(@RequestBody FieldEditor fieldEditor, HttpSession session, Locale locale, Principal principal) {

		try {

			// retireve phase
			Phase phase = servicePhase.get(fieldEditor.getId());
			if (phase == null)
				return JsonMessage.Error(messageSource.getMessage("error.phase.not_found", null, "Phase cannot be found", locale));

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

		} catch (Exception e) {

			// return error
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.edit.save.field", null, "Data cannot be saved", locale));
		}
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
	public static boolean SetFieldData(Field field, Object object, FieldEditor fieldEditor, String pattern) throws IllegalArgumentException, IllegalAccessException, ParseException,
			NumberFormatException {

		// check for data type to set field with data with cast to correct data
		// type
		if (fieldEditor.getType().equalsIgnoreCase("string"))
			field.set(object, (String) fieldEditor.getValue());
		else if (fieldEditor.getType().equalsIgnoreCase("integer"))
			field.set(object, Integer.parseInt(fieldEditor.getValue().toString()));
		else if (fieldEditor.getType().equalsIgnoreCase("double"))
			field.set(object, Double.parseDouble(fieldEditor.getValue().toString()));
		else if (fieldEditor.getType().equalsIgnoreCase("float"))
			field.set(object, Float.parseFloat(fieldEditor.getValue().toString()));
		else if (fieldEditor.getType().equalsIgnoreCase("boolean"))
			field.set(object, Boolean.parseBoolean(fieldEditor.getValue().toString()));
		else if (fieldEditor.getType().equalsIgnoreCase("date")) {
			DateFormat format = new SimpleDateFormat(pattern == null ? "yyyy-MM-dd hh:mm:ss" : pattern);
			field.set(object, format.parse(fieldEditor.getValue().toString()));
		} else
			// data type not recognized return error
			return false;

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
				return Integer.parseInt(fieldEditor.getValue().toString());
			else if (fieldEditor.getType().equalsIgnoreCase("double"))
				return Double.parseDouble(fieldEditor.getValue().toString());
			else if (fieldEditor.getType().equalsIgnoreCase("float"))
				return Float.parseFloat(fieldEditor.getValue().toString());
			else if (fieldEditor.getType().equalsIgnoreCase("boolean"))
				return Boolean.parseBoolean(fieldEditor.getValue().toString());
			else if (fieldEditor.getType().equalsIgnoreCase("date")) {
				DateFormat format = new SimpleDateFormat(pattern == null ? "yyyy-MM-dd hh:mm:ss" : pattern);
				return format.parse(fieldEditor.getValue().toString());
			}

			// data type not found, return error
			return null;
		} catch (NumberFormatException e) {

			// print error
			e.printStackTrace();
			return null;
		} catch (ParseException e) {

			// print error
			e.printStackTrace();
			return null;
		}
	}
	
	public static Field FindField(Class<?> object, String fieldName){
		for (Field  field: object.getDeclaredFields())
			if(field.getName().equals(fieldName))
				return field;
		if(!object.equals(Object.class))
			return FindField(object.getSuperclass(), fieldName);
		return null;
	}
}
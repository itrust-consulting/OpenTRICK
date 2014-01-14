/**
 * 
 */
package lu.itrust.business.view.controller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.Phase;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.JsonMessage;
import lu.itrust.business.component.ParameterManager;
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
import lu.itrust.business.view.model.FieldEditor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author eom
 * 
 */
@Secured("ROLE_USER")
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
	private ServicePhase servicePhase;

	protected boolean setFieldData(Field field, Object object,
			FieldEditor fieldEditor, String pattern)
			throws IllegalArgumentException, IllegalAccessException,
			ParseException, NumberFormatException {
		if (fieldEditor.getType().equalsIgnoreCase("string"))
			field.set(object, (String) fieldEditor.getValue());
		else if (fieldEditor.getType().equalsIgnoreCase("integer"))
			field.set(object, Integer.parseInt(fieldEditor.getValue()));
		else if (fieldEditor.getType().equalsIgnoreCase("double"))
			field.set(object, Double.parseDouble(fieldEditor.getValue()));
		else if (fieldEditor.getType().equalsIgnoreCase("float"))
			field.set(object, Float.parseFloat(fieldEditor.getValue()));
		else if (fieldEditor.getType().equalsIgnoreCase("boolean"))
			field.set(object, Boolean.parseBoolean(fieldEditor.getValue()));
		else if (fieldEditor.getType().equalsIgnoreCase("date")) {
			DateFormat format = new SimpleDateFormat(
					pattern == null ? "yyyy-MM-dd hh:mm:ss" : pattern);
			field.set(object, format.parse(fieldEditor.getValue()));
		} else
			return false;
		return true;
	}

	private Object value(FieldEditor fieldEditor, String pattern) {
		try {
			if (fieldEditor.getType().equalsIgnoreCase("string"))
				return (String) fieldEditor.getValue();
			else if (fieldEditor.getType().equalsIgnoreCase("integer"))
				return Integer.parseInt(fieldEditor.getValue());
			else if (fieldEditor.getType().equalsIgnoreCase("double"))
				return Double.parseDouble(fieldEditor.getValue());
			else if (fieldEditor.getType().equalsIgnoreCase("float"))
				return Float.parseFloat(fieldEditor.getValue());
			else if (fieldEditor.getType().equalsIgnoreCase("boolean"))
				return Boolean.parseBoolean(fieldEditor.getValue());
			else if (fieldEditor.getType().equalsIgnoreCase("date")) {
				DateFormat format = new SimpleDateFormat(
						pattern == null ? "yyyy-MM-dd hh:mm:ss" : pattern);
				return format.parse(fieldEditor.getValue());
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;

	}

	@RequestMapping(value = "/ItemInformation", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String itemInformation(@RequestBody FieldEditor fieldEditor, Locale locale) {
		try {
			ItemInformation itemInformation = serviceItemInformation
					.get(fieldEditor.getId());
			if (itemInformation == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.itemInformation.not_found", null,
						"ItemInformation cannot be found", locale));
			Field field = itemInformation.getClass().getDeclaredField(
					fieldEditor.getFieldName());
			field.setAccessible(true);

			if (setFieldData(field, itemInformation, fieldEditor, null)) {
				serviceItemInformation.saveOrUpdate(itemInformation);
				return JsonMessage.Success(messageSource.getMessage(
						"success.itemInformation.updated", null,
						"ItemInformation was successfully updated", locale));
			} else
				return JsonMessage.Error(messageSource.getMessage(
						"error.edit.type.field", null,
						"Data cannot be updated", locale));
		} catch (NumberFormatException e) {
			return JsonMessage.Error(messageSource.getMessage(
					"error.format.number", null, "Number expected", locale));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(),
					null, e.getMessage(), locale));
		}
		return JsonMessage.Error(messageSource.getMessage(
				"error.edit.save.field", null, "Data cannot be saved", locale));
	}

	@RequestMapping(value = "/Parameter", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String parameter(@RequestBody FieldEditor fieldEditor, Locale locale) {
		try {
			Parameter parameter = serviceParameter.get(fieldEditor.getId());
			if (parameter == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.parameter.not_found", null,
						"Parameter cannot be found", locale));

			ValidatorField validator = serviceDataValidation
					.findByClass(parameter.getClass());
			if (validator == null)
				serviceDataValidation.register(new ParameterValidator());

			Object value = value(fieldEditor, null);
			String error = serviceDataValidation.validate(parameter,
					fieldEditor.getFieldName(), value);
			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(
						error, messageSource, locale));
			Field field = parameter.getClass().getDeclaredField(
					fieldEditor.getFieldName());
			field.setAccessible(true);
			if (setFieldData(field, parameter, fieldEditor, null)) {
				serviceParameter.saveOrUpdate(parameter);
				return JsonMessage.Success(messageSource.getMessage(
						"success.parameter.updated", null,
						"Parameter was successfully updated", locale));
			} else
				return JsonMessage.Success(messageSource.getMessage(
						"error.edit.type.field", null,
						"Data cannot be updated", locale));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			return JsonMessage.Error(messageSource.getMessage(
					"error.format.number", null, "Number expected", locale));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(),
					null, e.getMessage(), locale));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(
					"error.format.date", null, "Date expected", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(),
					null, e.getMessage(), locale));
		}
		return JsonMessage.Error(messageSource.getMessage(
				"error.edit.save.field", null, "Data cannot be saved", locale));
	}

	@RequestMapping(value = "/ExtendedParameter", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String extendedParameter(@RequestBody FieldEditor fieldEditor,
			HttpSession session, Locale locale) {
		try {
			ExtendedParameter parameter = (ExtendedParameter) serviceParameter
					.get(fieldEditor.getId());
			if (parameter == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.parameter.not_found", null,
						"Parameter cannot be found", locale));
			if (!serviceDataValidation.isRegistred(parameter.getClass()))
				serviceDataValidation
						.register(new ExtendedParameterValidator());
			Object value = value(fieldEditor, null);
			String error = serviceDataValidation.validate(parameter,
					fieldEditor.getFieldName(), value);
			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(
						error, messageSource, locale));
			Field field = null;
			if ("value id type description"
					.contains(fieldEditor.getFieldName()))
				field = parameter.getClass().getSuperclass()
						.getDeclaredField(fieldEditor.getFieldName());
			else
				field = parameter.getClass().getDeclaredField(
						fieldEditor.getFieldName());
			field.setAccessible(true);
			if (setFieldData(field, parameter, fieldEditor, null)) {

				Integer id = (Integer) session.getAttribute("selectedAnalysis");

				if (id == null)
					return JsonMessage.Error(messageSource.getMessage(
							"error.analysis.no_selected", null,
							"No selected analysis", locale));

				if (!serviceAnalysis.exist(id))
					return JsonMessage.Error(messageSource.getMessage(
							"error.analysis.not_found", null,
							"Analysis cannot be found", locale));

				serviceParameter.saveOrUpdate(parameter);

				List<ExtendedParameter> parameters = serviceParameter
						.findExtendedByAnalysisAndType(id, parameter.getType());

				ParameterManager.ComputeImpactValue(parameters);

				serviceParameter.saveOrUpdate(parameters);

				return JsonMessage.Success(messageSource.getMessage(
						"success.extendedParameter.update", null,
						"Parameter was successfully update", locale));
			} else
				return JsonMessage.Error(messageSource.getMessage(
						"error.edit.type.field", null,
						"Data cannot be updated", locale));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			return JsonMessage.Error(messageSource.getMessage(
					"error.format.number", null, "Number expected", locale));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(),
					null, e.getMessage(), locale));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(
					"error.format.date", null, "Date expected", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(),
					null, e.getMessage(), locale));
		}
		return JsonMessage.Error(messageSource.getMessage(
				"error.edit.save.field", null, "Data cannot be saved", locale));
	}

	@RequestMapping(value = "/Assessment", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String assessment(@RequestBody FieldEditor fieldEditor,
			HttpSession session, Locale locale) {

		try {
			Assessment assessment = serviceAssessment.get(fieldEditor.getId());
			if (assessment == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.assessment.not_found", null,
						"Assessment cannot be found", locale));

			Integer id = (Integer) session.getAttribute("selectedAnalysis");

			if (id == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.analysis.no_selected", null,
						"No selected analysis", locale));

			if (!serviceDataValidation.isRegistred(assessment.getClass()))
				serviceDataValidation.register(new AssessmentValidator());

			Object value = value(fieldEditor, null);

			List<String> chooses = null;

			if ("impactRep,impactOp,impactLeg,impactFin".contains(fieldEditor
					.getFieldName()))
				chooses = serviceParameter.findAcronymByAnalysisAndType(id,
						Constant.PARAMETERTYPE_TYPE_IMPACT_NAME);
			else if ("likelihood".equals(fieldEditor.getFieldName()))
				chooses = serviceParameter.findAcronymByAnalysisAndType(id,
						Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME);

			String error = serviceDataValidation.validate(assessment,
					fieldEditor.getFieldName(), value, chooses.toArray());
			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(
						error, messageSource, locale));

			Field field = assessment.getClass().getDeclaredField(
					fieldEditor.getFieldName());

			field.setAccessible(true);
			if (!setFieldData(field, assessment, fieldEditor, null))
				return JsonMessage.Error(messageSource.getMessage(
						"error.edit.type.field", null,
						"Data cannot be updated", locale));

			Map<String, ExtendedParameter> parameters = new LinkedHashMap<>();

			for (ExtendedParameter parameter : serviceParameter
					.findExtendedByAnalysis(id))
				parameters.put(parameter.getAcronym(), parameter);

			AssessmentManager.ComputeAlE(assessment, parameters);

			serviceAssessment.saveOrUpdate(assessment);
			return JsonMessage.Success(messageSource.getMessage(
					"success.assessment.updated", null,
					"Assessment was successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JsonMessage.Error(messageSource.getMessage(
				"error.edit.save.field", null, "Data cannot be saved", locale));
	}

	@RequestMapping(value = "/History", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String history(@RequestBody FieldEditor fieldEditor, Locale locale) {
		try {
			History history = serviceHistory.get(fieldEditor.getId());

			if (history == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.history.not_found", null,
						"History cannot be found", locale));
			if (!serviceDataValidation.isRegistred(history.getClass()))
				serviceDataValidation.register(new HistoryValidator());

			Object value = value(fieldEditor, null);

			String error = serviceDataValidation.validate(history,
					fieldEditor.getFieldName(), value);

			if (error != null)
				return JsonMessage.Error(serviceDataValidation.ParseError(
						error, messageSource, locale));

			Field field = history.getClass().getDeclaredField(
					fieldEditor.getFieldName());
			field.setAccessible(true);

			if (setFieldData(field, history, fieldEditor, null)) {
				serviceHistory.saveOrUpdate(history);
				return JsonMessage.Success(messageSource.getMessage(
						"success.history.updated", null,
						"History was successfully updated", locale));
			} else
				return JsonMessage.Error(messageSource.getMessage(
						"error.edit.type.field", null,
						"Data cannot be updated", locale));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			return JsonMessage.Error(messageSource.getMessage(
					"error.format.number", null, "Number expected", locale));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(),
					null, e.getMessage(), locale));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(
					"error.format.date", null, "Date expected", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(),
					null, e.getMessage(), locale));
		}
		return JsonMessage.Error(messageSource.getMessage(
				"error.edit.save.field", null, "Data cannot be saved", locale));
	}

	@RequestMapping(value = "/Measure", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String measure(@RequestBody FieldEditor fieldEditor, HttpSession session,
			Locale locale) {
		try {
			Integer idAnalysis = (Integer) session
					.getAttribute("selectedAnalysis");
			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.analysis.not_found", null,
						"Analysis cannot be found", locale));

			Measure measure = serviceMeasure.findByIdAndAnalysis(
					fieldEditor.getId(), idAnalysis);
			if (measure == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.measure.not_found", null,
						"Measure cannot be found", locale));
			Field field = measure.getClass().getSuperclass()
					.getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);
			if (fieldEditor.getFieldName().equals("phase")) {
				Integer number = 0;
				if (!fieldEditor.getValue().equalsIgnoreCase("NA"))
					number = (Integer) value(fieldEditor, null);
				if (number == null)
					return JsonMessage.Error(messageSource.getMessage(
							"error.edit.type.field", null,
							"Data cannot be updated", locale));
				Phase phase = servicePhase.loadFromPhaseNumberAnalysis(number,
						idAnalysis);
				if (phase == null)
					return JsonMessage.Error(messageSource.getMessage(
							"error.phase.not_found", null,
							"Phase cannot be found", locale));
				measure.setPhase(phase);

			} else if (!setFieldData(field, measure, fieldEditor, null))
				return JsonMessage.Error(messageSource.getMessage(
						"error.edit.type.field", null,
						"Data cannot be updated", locale));
			serviceMeasure.saveOrUpdate(measure);
			return JsonMessage.Success(messageSource.getMessage(
					"success.measure.updated", null,
					"Measure was successfully updated", locale));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return JsonMessage.Error(messageSource.getMessage(
				"error.edit.save.field", null, "Data cannot be saved", locale));

	}

	@RequestMapping(value = "/MaturityMeasure", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String maturityMeasure(@RequestBody FieldEditor fieldEditor,
			HttpSession session, Locale locale) {
		try {
			Measure measure = serviceMeasure.findOne(fieldEditor.getId());
			if (measure == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.measure.not_found", null,
						"Measure cannot be found", locale));

			if (fieldEditor.getFieldName().equalsIgnoreCase(
					"implementationRate")) {
				Integer idAnalysis = (Integer) session
						.getAttribute("selectedAnalysis");
				if (idAnalysis == null)
					return JsonMessage.Error(messageSource.getMessage(
							"error.analysis.not_found", null,
							"Analysis cannot be found", locale));

				List<Parameter> parameters = serviceParameter
						.findByAnalysisAndType(idAnalysis,
								Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME);

				double value = Double.parseDouble(fieldEditor.getValue());

				for (Parameter parameter : parameters) {
					if (Math.abs(parameter.getValue() - value) < 1e-5) {
						measure.setImplementationRate(parameter);
						serviceMeasure.saveOrUpdate(measure);
						return JsonMessage.Success(messageSource.getMessage(
								"success.measure.updated", null,
								"Measure was successfully updated", locale));
					}
				}
				return JsonMessage.Error(messageSource.getMessage(
						"error.edit.type.field", null,
						"Data cannot be updated", locale));
			} else
				measure(fieldEditor, session, locale);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JsonMessage.Error(messageSource.getMessage(
				"error.edit.save.field", null, "Data cannot be saved", locale));
	}

	@RequestMapping(value = "/Phase", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String phase(@RequestBody FieldEditor fieldEditor, HttpSession session,
			Locale locale) {
		try {
			Phase phase = servicePhase.get(fieldEditor.getId());
			if (phase == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.phase.not_found", null, "Phase cannot be found",
						locale));
			Field field = phase.getClass().getDeclaredField(
					fieldEditor.getFieldName());
			field.setAccessible(true);

			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			field.set(phase, new Date(format.parse(fieldEditor.getValue())
					.getTime()));
			servicePhase.saveOrUpdate(phase);
			return JsonMessage.Success(messageSource.getMessage(
					"success.phase.updated", null,
					"Phase was successfully updated", locale));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return JsonMessage.Error(messageSource.getMessage(
				"error.edit.save.field", null, "Data cannot be saved", locale));
	}

}

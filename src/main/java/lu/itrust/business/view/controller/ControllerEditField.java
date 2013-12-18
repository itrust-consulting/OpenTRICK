/**
 * 
 */
package lu.itrust.business.view.controller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.JsonMessage;
import lu.itrust.business.component.ParameterManager;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAssessment;
import lu.itrust.business.service.ServiceHistory;
import lu.itrust.business.service.ServiceItemInformation;
import lu.itrust.business.service.ServiceMeasure;
import lu.itrust.business.service.ServiceParameter;
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
	private ServiceMeasure serviceMeasure;

	protected boolean setFieldData(Field field, Object object,
			FieldEditor fieldEditor) throws IllegalArgumentException,
			IllegalAccessException, ParseException, NumberFormatException {
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
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			field.set(object, format.parse(fieldEditor.getValue()));
		} else
			return false;
		return true;
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

			if (setFieldData(field, itemInformation, fieldEditor)) {
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
			Field field = parameter.getClass().getDeclaredField(
					fieldEditor.getFieldName());
			field.setAccessible(true);
			if (setFieldData(field, parameter, fieldEditor)) {
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
			Field field = null;
			if ("value id type description"
					.contains(fieldEditor.getFieldName()))
				field = parameter.getClass().getSuperclass()
						.getDeclaredField(fieldEditor.getFieldName());
			else
				field = parameter.getClass().getDeclaredField(
						fieldEditor.getFieldName());
			field.setAccessible(true);
			if (setFieldData(field, parameter, fieldEditor)) {

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

			Field field = assessment.getClass().getDeclaredField(
					fieldEditor.getFieldName());
			field.setAccessible(true);
			if (!setFieldData(field, assessment, fieldEditor))
				return JsonMessage.Error(messageSource.getMessage(
						"error.edit.type.field", null,
						"Data cannot be updated", locale));
			Integer id = (Integer) session.getAttribute("selectedAnalysis");

			if (id == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.analysis.no_selected", null,
						"No selected analysis", locale));

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

			Field field = history.getClass().getDeclaredField(
					fieldEditor.getFieldName());
			field.setAccessible(true);

			if (setFieldData(field, history, fieldEditor)) {
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
	String measure(@RequestBody FieldEditor fieldEditor, Locale locale) {
		try {
			Measure measure = serviceMeasure.findOne(fieldEditor.getId());
			if (measure == null)
				return JsonMessage.Error(messageSource.getMessage(
						"error.measure.not_found", null,
						"Measure cannot be found", locale));
			Field field = measure.getClass().getSuperclass()
					.getDeclaredField(fieldEditor.getFieldName());
			field.setAccessible(true);
			if (setFieldData(field, measure, fieldEditor)) {
				serviceMeasure.saveOrUpdate(measure);
				return JsonMessage.Success(messageSource.getMessage(
						"success.measure.updated", null,
						"Measure was successfully updated", locale));
			} else
				return JsonMessage.Error(messageSource.getMessage(
						"error.edit.type.field", null,
						"Data cannot be updated", locale));

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
						"error.edit.type.field", null, "Data cannot be updated",
						locale));
			}else measure(fieldEditor, locale);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JsonMessage.Error(messageSource.getMessage(
				"error.edit.save.field", null, "Data cannot be saved", locale));
	}

}

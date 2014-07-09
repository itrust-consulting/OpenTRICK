package lu.itrust.business.view.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.MaturityParameter;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.TrickService;
import lu.itrust.business.TS.actionplan.SummaryStage;
import lu.itrust.business.TS.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.GeneralComperator;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.service.ServiceActionPlanSummary;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceMeasure;
import lu.itrust.business.service.ServiceParameter;
import lu.itrust.business.service.ServiceScenario;
import lu.itrust.business.service.ServiceTrickService;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ControllerPatch.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl.
 * @version
 * @since May 9, 2014
 */
@Controller
@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
@RequestMapping("/Patch")
public class ControllerPatch {

	@Autowired
	private ServiceTrickService serviceTrickService;

	@Autowired
	private ServiceScenario serviceScenario;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceParameter serviceParameter;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceActionPlanSummary serviceActionPlanSummary;

	@Autowired
	private ServiceMeasure serviceMeasure;

	@Autowired
	private AssessmentManager assessmentManager;

	@RequestMapping(value = "/Update/ScenarioCategoryValue", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody
	String updateAllScenario(Locale locale) {

		try {

			List<Scenario> scenarios = serviceScenario.getAll();
			for (Scenario scenario : scenarios) {
				for (String key : CategoryConverter.JAVAKEYS)
					scenario.setCategoryValue(key, 0);
				scenario.setCategoryValue(CategoryConverter.getTypeFromScenario(scenario), 1);
				serviceScenario.saveOrUpdate(scenario);
			}

			System.out.println("Done...");

			return JsonMessage.Success(messageSource.getMessage("success.scenario.update.all", null, "Scenarios were successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(e.getMessage());
		}
	}

	@RequestMapping(value = "/Update/Assessments", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody
	Map<String, String> updateAssessments(Locale locale) {

		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {

			List<Analysis> analyses = serviceAnalysis.getAll();

			for (Analysis analysis : analyses) {
				Hibernate.initialize(analysis.getAssets());
				Hibernate.initialize(analysis.getScenarios());
				Hibernate.initialize(analysis.getAssessments());
				Hibernate.initialize(analysis.getParameters());
				assessmentManager.UpdateAssessment(analysis);
			}

			System.out.println("Done...");

			errors.put("success", messageSource.getMessage("success.assessments.update.all", null, "All assessments were successfully updated", locale));

			return errors;
		} catch (Exception e) {
			errors.put("error", e.getMessage());
			e.printStackTrace();
			return errors;
		}
	}

	@RequestMapping(value = "/Update/ParameterImplementationScale", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody
	Map<String, String> updateImplementationScaleNames(Locale locale) {

		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {

			List<Parameter> parameters = serviceParameter.getAll();

			for (Parameter parameter : parameters) {

				if (!parameter.getDescription().startsWith("ImpScale"))
					continue;
				else {

					Integer line = Integer.valueOf(parameter.getDescription().substring(8));
					String desc = "";
					switch (line) {
						case 1:
							desc = Constant.IS_NOT_ACHIEVED;
							break;
						case 2:
							desc = Constant.IS_RUDIMENTARY_ACHIEVED;
							break;
						case 3:
							desc = Constant.IS_PARTIALLY_ACHIEVED;
							break;
						case 4:
							desc = Constant.IS_LARGELY_ACHIEVED;
							break;
						case 5:
							desc = Constant.IS_FULLY_ACHIEVED;
							break;
						default:
							desc = "ImpScale" + String.valueOf(line);
							break;
					}

					parameter.setDescription(desc);
					serviceParameter.saveOrUpdate(parameter);
				}
			}

			System.out.println("Done...");

			errors.put("success", messageSource.getMessage("success.impscale.update.all", null, "Implmentation Scale Parameter descriptions were successfully updated", locale));

			return errors;
		} catch (Exception e) {
			errors.put("error", e.getMessage());
			e.printStackTrace();
			return errors;
		}
	}

	// @RequestMapping(value = "/Update/MeasureMaintenance", method = RequestMethod.GET, headers =
	// "Accept=application/json; charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody
	String updateMaintenance(Locale locale) {

		String patchversion = "0.0.2";

		try {

			List<Integer> analyses = serviceAnalysis.getAllAnalysisIDs();

			for (Integer idAnalysis : analyses) {

				System.out.println("analysis " + (analyses.indexOf(idAnalysis) + 1) + " of " + analyses.size());

				Parameter internalSetupRate = serviceAnalysis.getParameterFromAnalysis(idAnalysis, Constant.PARAMETER_INTERNAL_SETUP_RATE);

				Parameter externalSetupRate = serviceAnalysis.getParameterFromAnalysis(idAnalysis, Constant.PARAMETER_EXTERNAL_SETUP_RATE);

				Parameter defaultLifetime = serviceAnalysis.getParameterFromAnalysis(idAnalysis, Constant.PARAMETER_LIFETIME_DEFAULT);

				Parameter defaultMaintenance = serviceAnalysis.getParameterFromAnalysis(idAnalysis, Constant.PARAMETER_MAINTENANCE_DEFAULT);

				List<Measure> measures = serviceMeasure.getAllComputableFromAnalysis(idAnalysis);

				if (measures == null || measures.isEmpty())
					continue;

				for (Measure measure : measures) {

					double maintenance = measure.getMaintenance();

					double internalmaintenance = 0;

					double externalmaintenance = 0;

					double recurrentInvestment = 0;

					double investment = 0;

					investment = measure.getInvestment();

					internalmaintenance = (measure.getInternalWL() * (maintenance / 100.));

					externalmaintenance = (measure.getExternalWL() * (maintenance / 100.));

					recurrentInvestment = (investment * (maintenance / 100.));

					measure.setInternalMaintenance(internalmaintenance);

					measure.setExternalMaintenance(externalmaintenance);

					measure.setRecurrentInvestment(recurrentInvestment);

					measure.setCost(Analysis.computeCost(internalSetupRate.getValue(), externalSetupRate.getValue(), defaultLifetime.getValue(), measure.getInternalMaintenance(), measure
							.getExternalMaintenance(), measure.getRecurrentInvestment(), measure.getInternalWL(), measure.getExternalWL(), measure.getInvestment(), measure.getLifetime()));

					serviceMeasure.saveOrUpdate(measure);

				}

				for (SummaryStage summaryStage : serviceActionPlanSummary.getAllFromAnalysis(idAnalysis)) {
					summaryStage.setRecurrentInvestment(0);
					serviceActionPlanSummary.saveOrUpdate(summaryStage);
				}

				if (defaultMaintenance != null)
					serviceParameter.delete(defaultMaintenance);

			}

			TrickService ts = serviceTrickService.getStatus();
			if (GeneralComperator.VersionComparator(ts.getVersion(), patchversion) == -1) {

				ts.setVersion(patchversion);
				serviceTrickService.saveOrUpdate(ts);

			}
			System.out.println("Done...");

			return JsonMessage.Success(messageSource.getMessage("success.maintenance.update.all", null, "Measures were successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(e.getMessage());
		}
	}

	@RequestMapping(value = "/Update/ParameterMaturityILPS", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody
	Map<String, String> updateILPS(Locale locale) {

		String patchversion = "0.0.3";

		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {

			List<Integer> analyses = serviceAnalysis.getAllAnalysisIDs();

			for (int idAnalysis : analyses) {

				List<Parameter> parameters = serviceParameter.getAllFromAnalysisByType(idAnalysis, Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_LEVEL_PER_SML_NAME);

				Map<String, MaturityParameter> maturitylvls = new LinkedHashMap<String, MaturityParameter>();

				for (Parameter parameter : parameters) {

					boolean candelete = true;

					if (parameter.getValue() != -1) {

						MaturityParameter param = maturitylvls.get(((MaturityParameter) parameter).getCategory() + "_" + ((MaturityParameter) parameter).getDescription());

						MaturityParameter matparam = (MaturityParameter) parameter;

						if (param == null) {
							param = (MaturityParameter) parameter;
							maturitylvls.put(param.getCategory() + "_" + param.getDescription(), param);
							candelete = false;
						}

						switch (matparam.getSMLLevel()) {
							case 0: {
								param.setSMLLevel0(matparam.getValue());
								param.setValue(-1);
								break;
							}
							case 1: {
								param.setSMLLevel1(matparam.getValue());
								param.setValue(-1);
								break;
							}
							case 2: {
								param.setSMLLevel2(matparam.getValue());
								param.setValue(-1);
								break;
							}
							case 3: {
								param.setSMLLevel3(matparam.getValue());
								param.setValue(-1);
								break;
							}
							case 4: {
								param.setSMLLevel4(matparam.getValue());
								param.setValue(-1);
								break;
							}
							case 5: {
								param.setSMLLevel5(matparam.getValue());
								param.setValue(-1);
								break;
							}
						}

						if (candelete)
							serviceParameter.delete(parameter);
					}
				}

			}

			TrickService ts = serviceTrickService.getStatus();
			if (GeneralComperator.VersionComparator(ts.getVersion(), patchversion) == -1) {

				ts.setVersion(patchversion);
				serviceTrickService.saveOrUpdate(ts);

			}
			System.out.println("Done...");

			errors.put("success", messageSource.getMessage("success.impscale.update.all", null, "Implmentation Scale Parameter descriptions were successfully updated", locale));

			return errors;
		} catch (Exception e) {
			errors.put("error", e.getMessage());
			e.printStackTrace();
			return errors;
		}
	}
}
package lu.itrust.business.TS.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.data.TrickService;
import lu.itrust.business.TS.data.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.data.actionplan.summary.SummaryStandardConformance;
import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.assessment.helper.AssessmentManager;
import lu.itrust.business.TS.data.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.data.parameter.Parameter;
import lu.itrust.business.TS.data.scenario.Scenario;
import lu.itrust.business.TS.data.scenario.ScenarioType;
import lu.itrust.business.TS.data.standard.AnalysisStandard;
import lu.itrust.business.TS.data.standard.measure.helper.MeasureManager;
import lu.itrust.business.TS.database.service.ServiceActionPlan;
import lu.itrust.business.TS.database.service.ServiceActionPlanSummary;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceAppSettingEntry;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServiceParameter;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.database.service.ServiceTrickService;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.settings.AppSettingEntry;

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

	@Autowired
	private MeasureManager measureManager;

	@Autowired
	private ServiceActionPlan serviceActionPlan;

	@Autowired
	private ServiceAnalysisStandard serviceAnalysisStandard;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceAppSettingEntry serviceAppSettingEntry;

	@RequestMapping(value = "/Update/ScenarioCategoryValue", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody String updateAllScenario(Locale locale) {
		try {

			List<Scenario> scenarios = serviceScenario.getAll();
			for (Scenario scenario : scenarios) {
				for (String key : CategoryConverter.JAVAKEYS)
					scenario.setCategoryValue(key, 0);
				scenario.setCategoryValue(CategoryConverter.getTypeFromScenario(scenario), 4);
				serviceScenario.saveOrUpdate(scenario);
			}

			System.out.println("Done...");

			return JsonMessage.Success(messageSource.getMessage("success.scenario.update.all", null, "Scenarios were successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(e.getMessage());
		}
	}

	@RequestMapping(value = "/Update/Measure/AssettypeValue", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody String removeMeasureAssetTypeValueDuplicate(Locale locale) throws Exception {
		try {
			measureManager.patchMeasureAssetypeValueDuplicated();
		} catch (TrickException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		}
		return JsonMessage.Success(messageSource.getMessage("success.remove.measure.asset_type_value.duplication", null,
				"The duplications of measure characteristics for the assets were successfully removed", locale));
	}

	@RequestMapping(value = "/Update/Assessments", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody Map<String, String> updateAssessments(Locale locale) {

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
	public @ResponseBody Map<String, String> updateImplementationScaleNames(Locale locale) {

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

	@RequestMapping(value = "/Update/UpdateCompliances", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody String updateCompliances(Locale locale) {

		String patchversion = "0.6.3b";

		try {

			System.out.println("Patching compliance (version 0.6.3b)");

			if (serviceTrickService.getStatus().getVersion().equals(patchversion)) {
				System.out.println("Patch already installed!");
				return JsonMessage.Error(messageSource.getMessage("error.patch.installed", null, "The Patch is already installed!", locale));
			}

			List<Integer> analyses = serviceAnalysis.getAllAnalysisIDs();

			for (Integer idAnalysis : analyses) {

				System.out.println("analysis " + (analyses.indexOf(idAnalysis) + 1) + " of " + analyses.size());

				List<AnalysisStandard> standards = serviceAnalysisStandard.getAllFromAnalysis(idAnalysis);

				for (AnalysisStandard standard : standards) {

					List<SummaryStage> summaries = serviceActionPlanSummary.getAllFromAnalysis(idAnalysis);

					for (SummaryStage summary : summaries) {

						if (standard.getStandard().getLabel().equals(Constant.STANDARD_27001)) {

							boolean found = false;

							for (SummaryStandardConformance conformance : summary.getConformances())
								if (conformance.getAnalysisStandard().getStandard().getLabel().equals(standard.getStandard().getLabel())) {
									found = true;
									break;
								}

							if (!found)
								summary.addConformance(standard, summary.getConformance27001());
						} else if (standard.getStandard().getLabel().equals(Constant.STANDARD_27002)) {

							boolean found = false;

							for (SummaryStandardConformance conformance : summary.getConformances())
								if (conformance.getAnalysisStandard().getStandard().getLabel().equals(standard.getStandard().getLabel())) {
									found = true;
									break;
								}

							if (!found)
								summary.addConformance(standard, summary.getConformance27002());
						} else {

							boolean found = false;

							for (SummaryStandardConformance conformance : summary.getConformances())
								if (conformance.getAnalysisStandard().getStandard().getLabel().equals(standard.getStandard().getLabel())) {
									found = true;
									break;
								}

							if (!found) {

								Map<String, Object[]> previouscompliances = ChartGenerator.ComputeComplianceBefore(standard.getMeasures());

								int compliancevalue = 0;

								for (String key : previouscompliances.keySet()) {
									Object[] compliance = previouscompliances.get(key);
									compliancevalue += (int) Math.floor(((Double) compliance[1]) / (Integer) compliance[0]);
								}

								compliancevalue = compliancevalue / previouscompliances.size();

								summary.addConformance(standard, compliancevalue * 0.01);

							}

						}

					}

					for (SummaryStage summary : summaries)
						serviceActionPlanSummary.saveOrUpdate(summary);
				}
			}

			TrickService ts = serviceTrickService.getStatus();

			ts.setVersion(patchversion);
			serviceTrickService.saveOrUpdate(ts);

			System.out.println("Done...");

			return JsonMessage.Success(messageSource.getMessage("success.compliance.update", null, "Compliances were successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(e.getMessage());
		}
	}

	@RequestMapping(value = "/Update/ApplicationSettings", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody String updateAnalysisSettings(Locale locale) {

		String patchversion = "0.6.3d";

		try {

			System.out.println("Patching settings (version 0.6.3d)");

			if (serviceTrickService.getStatus().getVersion().equals(patchversion)) {
				System.out.println("Patch already installed!");
				return JsonMessage.Error(messageSource.getMessage("error.patch.installed", null, "The Patch is already installed!", locale));
			}

			List<Integer> analyses = serviceAnalysis.getAllAnalysisIDs();

			for (Integer idAnalysis : analyses) {

				System.out.println("analysis " + (analyses.indexOf(idAnalysis) + 1) + " of " + analyses.size());

				Analysis analysis = serviceAnalysis.get(idAnalysis);

				AppSettingEntry settings = serviceAppSettingEntry.getByUsernameAndGroupAndName("bfetler", "analysis", String.valueOf(idAnalysis));

				if (settings != null) {
					if (settings.findByKey("show_uncertainty") == "true")
						analysis.setUncertainty(true);
					else
						analysis.setUncertainty(false);
					if (settings.findByKey("show_cssf") == "true")
						analysis.setCssf(true);
					else
						analysis.setCssf(false);
				} else {
					analysis.setUncertainty(true);
					analysis.setCssf(true);
				}

				serviceAnalysis.saveOrUpdate(analysis);
				
			}

			TrickService ts = serviceTrickService.getStatus();

			ts.setVersion(patchversion);
			serviceTrickService.saveOrUpdate(ts);

			System.out.println("Done...");

			return JsonMessage.Success(messageSource.getMessage("success.settings.update", null, "Settings were successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(e.getMessage());
		}
	}

	@RequestMapping(value = "/Update/ScenarioTypes", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody String updateScenarioTypes(Locale locale) {

		String patchversion = "0.6.4";

		try {

			System.out.println("Patching settings (version 0.6.4)");

			/*if (serviceTrickService.getStatus().getVersion().equals(patchversion)) {
				System.out.println("Patch already installed!");
				return JsonMessage.Error(messageSource.getMessage("error.patch.installed", null, "The Patch is already installed!", locale));
			}*/

			List<Integer> analyses = serviceAnalysis.getAllAnalysisIDs();

			for (Integer idAnalysis : analyses) {

				System.out.println("analysis " + (analyses.indexOf(idAnalysis) + 1) + " of " + analyses.size());

				Analysis analysis = serviceAnalysis.get(idAnalysis);

				List<Scenario> scenarios = new ArrayList<Scenario>();
				
				ScenarioType t = ScenarioType.valueOf("Integrity");
				
				List<Scenario> altScenarios = analysis.getScenarios();
						
				for(Scenario scneario : altScenarios) {
					
					ScenarioType type = ScenarioType.getByName(scneario.getScenarioType().getName());
					
					scneario.setType(type);
					
					scenarios.add(scneario);
					
				}
				
				analysis.setScenarios(scenarios);

				serviceAnalysis.saveOrUpdate(analysis);
				
			}

			TrickService ts = serviceTrickService.getStatus();

			ts.setVersion(patchversion);
			serviceTrickService.saveOrUpdate(ts);

			System.out.println("Done...");

			return JsonMessage.Success(messageSource.getMessage("success.settings.update", null, "Settings were successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(e.getMessage());
		}
	}
	
}
package lu.itrust.business.view.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.TrickService;
import lu.itrust.business.TS.actionplan.SummaryStage;
import lu.itrust.business.TS.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.GeneralComperator;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.service.ServiceActionPlanSummary;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceMeasure;
import lu.itrust.business.service.ServiceParameter;
import lu.itrust.business.service.ServiceScenario;
import lu.itrust.business.service.ServiceTrickService;

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

	@RequestMapping(value = "/Update/ScenarioCategoryValue", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody
	String updateAllScenario(Locale locale) {

		String patchversion = "0.0.2";

		try {

			List<Scenario> scenarios = serviceScenario.loadAll();
			for (Scenario scenario : scenarios) {
				for (String key : CategoryConverter.JAVAKEYS)
					scenario.setCategoryValue(key, 0);
				scenario.setCategoryValue(CategoryConverter.getTypeFromScenario(scenario), 1);
				serviceScenario.saveOrUpdate(scenario);
			}

			TrickService ts = serviceTrickService.getStatus();

			if (GeneralComperator.VersionComparator(ts.getVersion(), patchversion) == -1) {

				ts.setVersion(patchversion);
				serviceTrickService.saveOrUpdate(ts);

			}

			System.out.println("Done...");

			return JsonMessage.Success(messageSource.getMessage("success.scenario.update.all", null, "Scenarios were successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(e.getMessage());
		}
	}

	@RequestMapping(value = "/Update/MeasureMaintenance", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody
	String updateMaintenance(Locale locale) {

		String patchversion = "0.0.3";

		try {

			List<Integer> analyses = serviceAnalysis.loadAllAnalysisIDs();

			for (Integer idAnalysis: analyses) {

				System.out.println("analysis " + (analyses.indexOf(idAnalysis)+1) + " of " + analyses.size());
				
				Parameter internalSetupRate = serviceAnalysis.getParameterFromAnalysis(idAnalysis, Constant.PARAMETER_INTERNAL_SETUP_RATE);

				Parameter externalSetupRate = serviceAnalysis.getParameterFromAnalysis(idAnalysis, Constant.PARAMETER_EXTERNAL_SETUP_RATE);

				Parameter defaultLifetime = serviceAnalysis.getParameterFromAnalysis(idAnalysis, Constant.PARAMETER_LIFETIME_DEFAULT);

				Parameter defaultMaintenance = serviceAnalysis.getParameterFromAnalysis(idAnalysis, Constant.PARAMETER_MAINTENANCE_DEFAULT);

				List<Measure> measures = serviceMeasure.findMeasureByAnalysisAndComputable(idAnalysis);

				if(measures == null || measures.isEmpty())
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

				for (SummaryStage summaryStage : serviceActionPlanSummary.findByAnalysis(idAnalysis)) {
					summaryStage.setRecurrentInvestment(0);
					serviceActionPlanSummary.saveOrUpdate(summaryStage);
				}

				if(defaultMaintenance != null)
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

	@RequestMapping(value = "/Update/ParameterImplementationScale", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody
	Map<String, String> updateImplementationScaleNames(Locale locale) {

		String patchversion = "0.0.4";

		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {

			List<Parameter> parameters = serviceParameter.findAll();

			for (Parameter parameter : parameters) {
								
				if (!parameter.getDescription().startsWith("ImpScale"))
					continue;
				else {
					
					System.out.println("Parameter " + (parameters.indexOf(parameter) + 1) + " of " + parameters.size());
					
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
package lu.itrust.business.TS.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.assessment.helper.AssessmentManager;
import lu.itrust.business.TS.data.asset.AssetType;
import lu.itrust.business.TS.data.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.data.general.AssetTypeValue;
import lu.itrust.business.TS.data.scenario.Scenario;
import lu.itrust.business.TS.data.standard.NormalStandard;
import lu.itrust.business.TS.data.standard.measure.Measure;
import lu.itrust.business.TS.data.standard.measure.NormalMeasure;
import lu.itrust.business.TS.data.standard.measure.helper.MeasureManager;
import lu.itrust.business.TS.database.service.ServiceActionPlan;
import lu.itrust.business.TS.database.service.ServiceActionPlanSummary;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAnalysisStandard;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.database.service.ServiceParameter;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.database.service.ServiceTrickService;
import lu.itrust.business.TS.database.service.ServiceUser;

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
	private ServiceAssetType serviceAssetType;

	@RequestMapping(value = "/Update/ScenarioCategoryValue", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody String updateAllScenario(Locale locale) {
		try {

			System.out.println("Reset scenario category value");

			List<Integer> analyses = serviceAnalysis.getAllAnalysisIDs();

			for (Integer idAnalysis : analyses) {

				System.out.println("analysis " + (analyses.indexOf(idAnalysis) + 1) + " of " + analyses.size());

				List<Scenario> scenarios = serviceScenario.getAllFromAnalysis(idAnalysis);
				for (Scenario scenario : scenarios) {
					for (String key : CategoryConverter.JAVAKEYS)
						scenario.setCategoryValue(key, 0);
					scenario.setCategoryValue(CategoryConverter.getTypeFromScenario(scenario), 4);
					serviceScenario.saveOrUpdate(scenario);
				}

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
	public @ResponseBody Map<String, String> updateAssessments(Locale locale) {

		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {

			System.out.println("Update Assessments");

			List<Analysis> analyses = serviceAnalysis.getAll();

			for (Analysis analysis : analyses) {
				System.out.println("analysis " + (analyses.indexOf(analysis) + 1) + " of " + analyses.size());

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

	
	@RequestMapping(value = "/Update/Measure/MeasureAssetTypeValues", method = RequestMethod.GET, headers = "Accept=application/json; charset=UTF-8")
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	public @ResponseBody String updateMeasureAssetTypes(Locale locale) {

		try {

			System.out.println("Update Measure Asset Types");

			List<Integer> analyses = serviceAnalysis.getAllAnalysisIDs();

			for (Integer idAnalysis : analyses) {

				System.out.println("analysis " + (analyses.indexOf(idAnalysis) + 1) + " of " + analyses.size());

				Analysis analysis = serviceAnalysis.get(idAnalysis);

				List<NormalStandard> normalStandards = analysis.getAllNormalStandards();

				List<AssetType> assetTypes = serviceAssetType.getAll();

				for (NormalStandard normalStandard : normalStandards) {

					List<Measure> measures = normalStandard.getMeasures();

					for (Measure measure : measures) {

						NormalMeasure normalMeasure = (NormalMeasure) measure;
						for (AssetType at : assetTypes) {
							AssetTypeValue atv = normalMeasure.getAssetTypeValueByAssetType(at);
							if (atv == null) {
								atv = new AssetTypeValue(at, 0);
								normalMeasure.addAnAssetTypeValue(atv);
							}

						}
						//serviceMeasure.saveOrUpdate(normalMeasure);
					}

				}

				serviceAnalysis.saveOrUpdate(analysis);

			}

			System.out.println("Done...");

			return JsonMessage.Success(messageSource.getMessage("success.matv.update", null, "MeasureAssetTypeValues successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(e.getMessage());
		}
	}

}
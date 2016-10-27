package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerRestoreAnalyisRight;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.database.service.ServiceParameter;
import lu.itrust.business.TS.database.service.ServiceParameterType;
import lu.itrust.business.TS.database.service.ServiceScenario;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.helper.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.model.iteminformation.ItemInformation;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.impl.SimpleParameter;
import lu.itrust.business.TS.model.parameter.type.impl.ParameterType;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.NormalStandard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;

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
	private ServiceScenario serviceScenario;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private ServiceParameterType serviceParameterType;

	@Autowired
	private ServiceParameter serviceParameter;

	@Autowired
	private TaskExecutor executor;

	@Autowired
	private SessionFactory sessionFactory;

	@RequestMapping(value = "/Update/ScenarioCategoryValue", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String updateAllScenario(Principal principal, Locale locale) {
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
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		} finally {
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.patch.apply", String.format("Runtime: %s", "Scenario-category-value"), principal.getName(),
					LogAction.APPLY, "Scenario-category-value");
		}
	}

	@RequestMapping(value = "/Update/Assessments", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Map<String, String> updateAssessments(Principal principal, Locale locale) {

		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {
			assessmentAndRiskProfileManager.UpdateAssessment();
			errors.put("success", messageSource.getMessage("success.assessments.update.all", null, "All assessments were successfully updated", locale));
			return errors;
		} catch (Exception e) {
			errors.put("error", messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
			TrickLogManager.Persist(e);
			return errors;
		} finally {
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.patch.apply", String.format("Runtime: %s", "Update-assessment"), principal.getName(), LogAction.APPLY,
					"Update-assessment");
		}
	}

	@RequestMapping(value = "/Restore/Analysis/Right", method = RequestMethod.POST, headers = "Accept=application/json; charset=UTF-8")
	public @ResponseBody String RestoreAnalysisRights(Principal principal, Locale locale) {
		try {
			Worker worker = new WorkerRestoreAnalyisRight(principal.getName(), workersPoolManager, sessionFactory, serviceTaskFeedback);
			worker.setPoolManager(workersPoolManager);
			// register worker to tasklist
			if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId()))
				return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
			// execute task
			executor.execute(worker);
			return JsonMessage.Success(messageSource.getMessage("success.start.restore.analysis.right", null, "Restoring analysis rights", locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		} finally {
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.patch.apply", String.format("Runtime: %s", "Restore-analysis-Right"), principal.getName(),
					LogAction.APPLY, "Restore-analysis-Right");
		}
	}

	@RequestMapping(value = "/Update/Analyses/Risk-item-information", method = RequestMethod.POST, headers = "Accept=application/json; charset=UTF-8")
	public @ResponseBody String updateRiskInformationAndRiskItem(Principal principal, Locale locale) {
		try {
			Analysis profile = serviceAnalysis.getDefaultProfile();
			if (profile == null)
				return JsonMessage.Error(messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
			if(profile.getItemInformations().isEmpty())
				return JsonMessage.Error(messageSource.getMessage("error.default.profile.no_item_information", null, "Default profile does not contain item information", locale));
			List<Analysis> analyses = serviceAnalysis.getAllNotEmptyNoItemInformationAndRiskInformation(1, 30);
			while (!analyses.isEmpty()) {
				Analysis analysis = analyses.remove(0);
				if (analysis.getRiskInformations().isEmpty()) {
					for (RiskInformation riskInformation : profile.getRiskInformations())
						analysis.addARiskInformation(riskInformation.duplicate());
				}

				if (analysis.getItemInformations().isEmpty()) {
					for (ItemInformation itemInformation : profile.getItemInformations())
						analysis.addAnItemInformation(itemInformation.duplicate());
				}

				serviceAnalysis.saveOrUpdate(analysis);

				TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.analysis.copy.risk_item.information",
						String.format("Analysis: %s, version: %s; Copy risk and item information from default profile", analysis.getIdentifier(), analysis.getVersion()),
						principal.getName(), LogAction.UPDATE, analysis.getIdentifier(), analysis.getVersion());
				if (analyses.isEmpty())
					analyses = serviceAnalysis.getAllNotEmptyNoItemInformationAndRiskInformation(1, 30);
			}
			return JsonMessage
					.Success(messageSource.getMessage("success.update.risk_item.information", null, "Risk and item information were imported from the default profile", locale));
		} catch (CloneNotSupportedException e) {
			return JsonMessage.Error(messageSource.getMessage("error.clone.object", null, "An error occurred while copy data", locale));
		} catch (TrickException e) {
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		} finally {
			TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.patch.apply", String.format("Runtime: %s", "Copy-risk-item-information-from-default-profile"),
					principal.getName(), LogAction.APPLY, "Copy-risk-item-information-from-default-profile");
		}
	}

	@RequestMapping(value = "/Update/Analyses/Scopes", method = RequestMethod.POST, headers = "Accept=application/json; charset=UTF-8")
	public @ResponseBody String updateScope(Principal principal, Locale locale) {
		try {
			int size = serviceAnalysis.countNotEmpty(), pageSize = 30;
			String[] extendedScopes = new String[] { "financialParameters", "riskEvaluationCriteria", "impactCriteria", "riskAcceptanceCriteria" };
			boolean saveRequired = false;
			for (int pageIndex = 1, pageCount = (size / pageSize) + 1; pageIndex <= pageCount; pageIndex++) {
				for (Analysis analysis : serviceAnalysis.getAllNotEmpty(pageIndex, pageSize)) {
					// Add missing scope
					saveRequired = false;
					for (String scopeName : extendedScopes) {
						if (!analysis.getItemInformations().stream().anyMatch(itemInformation -> itemInformation.getDescription().equals(scopeName))) {
							analysis.addAnItemInformation(new ItemInformation(scopeName, Constant.ITEMINFORMATION_SCOPE, ""));
							saveRequired = true;
						}
					}
					if (saveRequired) {
						serviceAnalysis.saveOrUpdate(analysis);
						/**
						 * log
						 */
						TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.analysis.add.scope",
								String.format("Analysis: %s, version: %s; Add missing scopes", analysis.getIdentifier(), analysis.getVersion()), principal.getName(),
								LogAction.UPDATE, analysis.getIdentifier(), analysis.getVersion());
					}
				}
			}
			return JsonMessage.Success(messageSource.getMessage("success.update.analyses.scopes", null, "Scopes of analyses were successfully updated", locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		} finally {
			TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.patch.apply", String.format("Runtime: %s", "Update-scopes-of-analyses"), principal.getName(),
					LogAction.APPLY, "Update-scopes-of-analyses");
		}
	}

	// public
	@RequestMapping(value = "/Update/Measure/MeasureAssetTypeValues", method = RequestMethod.POST, headers = "Accept=application/json; charset=UTF-8")
	public @ResponseBody String updateMeasureAssetTypes(Principal principal, Locale locale) {

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
						// serviceMeasure.saveOrUpdate(normalMeasure);
					}

				}

				serviceAnalysis.saveOrUpdate(analysis);

			}

			System.out.println("Done...");

			return JsonMessage.Success(messageSource.getMessage("success.matv.update", null, "MeasureAssetTypeValues successfully updated", locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
		} finally {
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.patch.apply", String.format("Runtime: %s", "Update-measure-asset-types"), principal.getName(),
					LogAction.APPLY, "Update-measure-asset-types");
		}
	}

	@RequestMapping(value = "/Add-CSSF-Parameters", method = RequestMethod.POST, headers = "Accept=application/json; charset=UTF-8")
	public @ResponseBody String AddCSSFParameters(Principal principal, Locale locale) {
		try {
			int size = serviceAnalysis.countNotEmpty(), pageSize = 30;
			ParameterType parameterType = serviceParameterType.get(Constant.PARAMETERTYPE_TYPE_CSSF);
			if (parameterType == null)
				serviceParameterType.save(parameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_CSSF_NAME));
			for (int pageIndex = 1, pageCount = (size / pageSize) + 1; pageIndex <= pageCount; pageIndex++) {
				for (Analysis analysis : serviceAnalysis.getAllNotEmpty(pageIndex, pageSize)) {
					if (!analysis.hasParameterType(Constant.PARAMETERTYPE_TYPE_CSSF_NAME)) {
						analysis.add(new SimpleParameter(parameterType, Constant.CSSF_IMPACT_THRESHOLD, (double) Constant.CSSF_IMPACT_THRESHOLD_VALUE));
						analysis.add(new SimpleParameter(parameterType, Constant.CSSF_PROBABILITY_THRESHOLD, (double) Constant.CSSF_PROBABILITY_THRESHOLD_VALUE));
						analysis.add(new SimpleParameter(parameterType, Constant.CSSF_DIRECT_SIZE, 20D));
						analysis.add(new SimpleParameter(parameterType, Constant.CSSF_INDIRECT_SIZE, 5D));
						analysis.add(new SimpleParameter(parameterType, Constant.CSSF_CIA_SIZE, -1D));
					}
					IParameter parameter = analysis.findParameter(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, Constant.IMPORTANCE_THRESHOLD);
					if (parameter != null && analysis.getParameters().remove(parameter))
						serviceParameter.delete(parameter);
					serviceAnalysis.saveOrUpdate(analysis);
				}
			}
			return JsonMessage.Success(messageSource.getMessage("success.add.css_parameter", null, "CSSF parameters were successfully added", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Success(messageSource.getMessage(e.getMessage(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
		} finally {
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.patch.apply", String.format("Runtime: %s", "Add-CSSF-parameters"), principal.getName(),
					LogAction.APPLY, "Add-CSSF-parameters");
		}
	}

}
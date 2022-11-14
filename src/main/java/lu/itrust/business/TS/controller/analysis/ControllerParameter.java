package lu.itrust.business.TS.controller.analysis;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.constants.Constant.OPEN_MODE;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerScaleLevelMigrator;
import lu.itrust.business.TS.component.AnalysisImpactManager;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceDynamicParameter;
import lu.itrust.business.TS.database.service.ServiceImpactParameter;
import lu.itrust.business.TS.database.service.ServiceLikelihoodParameter;
import lu.itrust.business.TS.database.service.ServiceRiskAcceptanceParameter;
import lu.itrust.business.TS.database.service.ServiceScaleType;
import lu.itrust.business.TS.database.service.ServiceSimpleParameter;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisSetting;
import lu.itrust.business.TS.model.analysis.helper.AnalysisUtils;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.general.OpenMode;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.impl.DynamicParameter;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.RiskAcceptanceParameter;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.parameter.value.impl.FormulaValue;
import lu.itrust.business.TS.model.scale.ScaleType;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.MaturityStandard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.expressions.TokenType;
import lu.itrust.business.expressions.TokenizerToString;

/**
 * @author eom
 * 
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@Controller
@RequestMapping("/Analysis/Parameter")
public class ControllerParameter extends AbstractController {

	@Autowired
	private ServiceSimpleParameter serviceSimpleParameter;

	@Autowired
	private ServiceDynamicParameter serviceDynamicParameter;

	@Autowired
	private ServiceImpactParameter serviceImpactParameter;

	@Autowired
	private ServiceLikelihoodParameter serviceLikelihoodParameter;

	@Autowired
	private ServiceRiskAcceptanceParameter serviceRiskAcceptanceParameter;

	@Autowired
	private AnalysisImpactManager analysisImpactManager;

	@Autowired
	private ServiceScaleType serviceScaleType;

	@RequestMapping(value = "/Impact-scale/Manage", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String manageImpactScale(Model model, HttpSession session, Principal principal, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Map<ScaleType, Boolean> impacts = new LinkedHashMap<>();
		serviceScaleType.findFromAnalysis(idAnalysis).forEach(scale -> impacts.put(scale, true));
		serviceScaleType.findAll().stream().filter(scale -> !impacts.containsKey(scale)).forEach(scale -> impacts.put(scale, false));
		model.addAttribute("quantitativeImpact", impacts.keySet().stream().filter(impact -> impact.getName().equals(Constant.DEFAULT_IMPACT_NAME)).findAny().orElse(null));
		model.addAttribute("impacts", impacts);
		model.addAttribute("langue", locale.getLanguage().toUpperCase());
		return "analyses/single/components/parameters/form/mange-impact";
	}

	@RequestMapping(value = "/Impact-scale/Manage/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String manageImpactScaleSave(@RequestBody Map<Integer, Boolean> impacts, HttpSession session, Principal principal, Locale locale) {
		try {
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			return analysisImpactManager.manageImpactScaleSave(idAnalysis, impacts)
					? JsonMessage.Success(messageSource.getMessage("success.analysis.update.impact_scale", null, "Impacts scales have been updated", locale))
					: JsonMessage.Warning(messageSource.getMessage("warning.analysis.update.impact_scale", null, "Your analysis has not be updated", locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	@RequestMapping(value = "/Scale-level/Manage", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String manageScaleLevel(Model model, HttpSession session, Principal principal, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		model.addAttribute("maxLevel", serviceLikelihoodParameter.findMaxLevelByIdAnalysis(idAnalysis));
		return "analyses/single/components/parameters/form/mange-scale-level";
	}

	@RequestMapping(value = "/Scale-level/Manage/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String manageScaleLevelSave(@RequestBody Map<Integer, List<Integer>> levels, HttpSession session, Principal principal, Locale locale) {
		try {
			final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			final Worker worker = new WorkerScaleLevelMigrator(idAnalysis, levels);
			if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale)) {
				executor.execute(worker);
				return JsonMessage.Success(messageSource.getMessage("success.analysis.scale.level.migrating.start", null, "Please wait while migrating scale level.", locale));
			}
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String section(Model model, HttpSession session, Principal principal) throws Exception {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		if (analysis.isQuantitative())
			setupQualitativeParameterUI(model, analysis);
		model.addAttribute("type", analysis.getType());
		model.addAttribute("reportSettings", loadReportSettings(analysis));
		model.addAttribute("exportFilenames", loadExportFileNames(analysis));
		model.addAttribute("isILR", analysis.findSetting(AnalysisSetting.ALLOW_ILR_ANALYSIS));
		model.addAttribute("isEditable", !OpenMode.isReadOnly((OpenMode) session.getAttribute(OPEN_MODE)));
		model.addAttribute("mappedParameters", AnalysisUtils.SplitParameters(analysis.getParameters()));
		return "analyses/single/components/parameters/other";
	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Impact-probability/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String impactSection(Model model, HttpSession session, Principal principal) throws Exception {
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final List<ImpactParameter> impactParameters = serviceImpactParameter.findByAnalysisId(idAnalysis);
		final List<IParameter> parameters = new LinkedList<>(impactParameters);
		final AnalysisSetting dynamicAnalysis = AnalysisSetting.ALLOW_DYNAMIC_ANALYSIS;
		final Map<String, String> settings = serviceAnalysis.getSettingsByIdAnalysis(idAnalysis);
		model.addAttribute("impactTypes", impactParameters.parallelStream().map(ImpactParameter::getType).distinct().collect(Collectors.toList()));
		parameters.addAll(serviceLikelihoodParameter.findByAnalysisId(idAnalysis));
		parameters.addAll(serviceDynamicParameter.findByAnalysisId(idAnalysis));
		parameters.addAll(serviceSimpleParameter.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, idAnalysis));
		model.addAttribute("mappedParameters", AnalysisUtils.SplitParameters(parameters));
		model.addAttribute("type", serviceAnalysis.getAnalysisTypeById(idAnalysis));
		model.addAttribute("showDynamicAnalysis", Analysis.findSetting(dynamicAnalysis, settings.get(dynamicAnalysis.name())));
		return "analyses/single/components/parameters/impact_probability";
	}

	/**
	 * maturityImplementationRate: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Maturity/ImplementationRate", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object maturityImplementationRate(Model model, HttpSession session, Principal principal) throws Exception {
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		// load parameters of analysis
		return serviceSimpleParameter.findByTypeAndAnalysisId(Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME, idAnalysis);
	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Risk-acceptance/form", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String riskAcceptanceForm(Model model, HttpSession session, Principal principal) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS), level = serviceLikelihoodParameter.findMaxLevelByIdAnalysis(idAnalysis);
		model.addAttribute("maxImportance", level * level);
		model.addAttribute("parameters", serviceRiskAcceptanceParameter.findByAnalysisId(idAnalysis));
		return "analyses/single/components/parameters/form/riskAcceptance";
	}

	@DeleteMapping(value = "/Dynamic/Delete/{id}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session,#id, 'DynamicParameter', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String deleteDynamicParameter(@PathVariable int id, HttpSession session, Principal principal, Locale locale) {
		try {
			final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			final Analysis analysis = serviceAnalysis.get(idAnalysis);
			final DynamicParameter parameter = analysis.getDynamicParameters().stream().filter(p -> p.getId() == id).findAny().orElse(null);
			final boolean deleteable[] = { true };
			for (Assessment e : analysis.getAssessments()) {
				if (e.getLikelihood() != null && e.getLikelihood() instanceof FormulaValue && hasVariable(parameter.getAcronym(), e.getLikelihood().getVariable())) {
					deleteable[0] = false;
					break;
				} else {
					for (IValue b : e.getImpacts()) {
						if (b instanceof FormulaValue && hasVariable(parameter.getAcronym(), b.getVariable())) {
							deleteable[0] = false;
							break;
						}
					}
					if (!deleteable[0])
						break;
				}
			}

			if (deleteable[0]) {
				for (AnalysisStandard standard : analysis.getAnalysisStandards().values()) {
					if (standard instanceof MaturityStandard)
						continue;
					for (Measure measure : standard.getMeasures()) {
						if (measure.getImplementationRate() instanceof String && hasVariable(parameter.getAcronym(), measure.getImplementationRate().toString())) {
							deleteable[0] = false;
							break;
						}
					}
					if (!deleteable[0])
						break;
				}
			}

			if (deleteable[0]) {
				analysis.getExcludeAcronyms().add(parameter.getAcronym());
				analysis.getDynamicParameters().remove(parameter);
				serviceAnalysis.saveOrUpdate(analysis);
				serviceDynamicParameter.delete(parameter);
				return JsonMessage.Success(messageSource.getMessage("success.delete.parameter", null, "Parameter has been successfully deleted", locale));
			}
			return JsonMessage.Error(messageSource.getMessage("error.parameter.in_used", null, "Parameter cannot be deleted as it still in used", locale));

		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	private boolean hasVariable(final String acronym, final String formular) {
		return new TokenizerToString(formular).getTokens().parallelStream().anyMatch(v -> v.getType().equals(TokenType.Variable) && v.getParameter().equals(acronym));
	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Risk-acceptance/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String riskAcceptanceSave(@RequestBody List<RiskAcceptanceParameter> parameters, HttpSession session, Principal principal, Locale locale)
			throws Exception {
		try {
			Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
			Map<Integer, RiskAcceptanceParameter> riskAcceptanceParameters = analysis.getRiskAcceptanceParameters().stream()
					.collect(Collectors.toMap(RiskAcceptanceParameter::getId, Function.identity()));
			for (RiskAcceptanceParameter riskAcceptanceParameter : parameters) {
				RiskAcceptanceParameter parameter = riskAcceptanceParameters.remove(riskAcceptanceParameter.getId());
				if (parameter == null)
					analysis.add(riskAcceptanceParameter);
				else {
					parameter.setLabel(riskAcceptanceParameter.getLabel());
					parameter.setColor(riskAcceptanceParameter.getColor());
					parameter.setDescription(riskAcceptanceParameter.getDescription());
					parameter.setValue(riskAcceptanceParameter.getValue());
				}
			}

			if (!riskAcceptanceParameters.isEmpty()) {
				analysis.getParameters().get(Constant.PARAMETER_CATEGORY_RISK_ACCEPTANCE).removeIf(parameter -> riskAcceptanceParameters.containsKey(parameter.getId()));
				serviceRiskAcceptanceParameter.delete(riskAcceptanceParameters.values());
			}
			serviceAnalysis.saveOrUpdate(analysis);
			return JsonMessage.Success(messageSource.getMessage("success.update.risk_acceptance", null, "Risk acceptance has been successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
			TrickLogManager.Persist(e);
			if (e instanceof TrickException)
				return JsonMessage.Error(messageSource.getMessage(((TrickException) e).getCode(), ((TrickException) e).getParameters(), e.getMessage(), locale));
			return JsonMessage.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}
}

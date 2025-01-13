package lu.itrust.business.ts.controller.analysis;

import static lu.itrust.business.ts.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.ts.constants.Constant.OPEN_MODE;

import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lu.itrust.business.expressions.TokenType;
import lu.itrust.business.expressions.TokenizerToString;
import lu.itrust.business.ts.asynchronousWorkers.Worker;
import lu.itrust.business.ts.asynchronousWorkers.WorkerScaleLevelMigrator;
import lu.itrust.business.ts.component.AnalysisImpactManager;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceDynamicParameter;
import lu.itrust.business.ts.database.service.ServiceIlrSoaScaleParameter;
import lu.itrust.business.ts.database.service.ServiceImpactParameter;
import lu.itrust.business.ts.database.service.ServiceLikelihoodParameter;
import lu.itrust.business.ts.database.service.ServiceParameterType;
import lu.itrust.business.ts.database.service.ServiceRiskAcceptanceParameter;
import lu.itrust.business.ts.database.service.ServiceScaleType;
import lu.itrust.business.ts.database.service.ServiceSimpleParameter;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.JsonMessage;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisSetting;
import lu.itrust.business.ts.model.analysis.helper.AnalysisUtils;
import lu.itrust.business.ts.model.assessment.Assessment;
import lu.itrust.business.ts.model.general.OpenMode;
import lu.itrust.business.ts.model.parameter.IParameter;
import lu.itrust.business.ts.model.parameter.impl.DynamicParameter;
import lu.itrust.business.ts.model.parameter.impl.IlrSoaScaleParameter;
import lu.itrust.business.ts.model.parameter.impl.ImpactParameter;
import lu.itrust.business.ts.model.parameter.impl.RiskAcceptanceParameter;
import lu.itrust.business.ts.model.parameter.impl.SimpleParameter;
import lu.itrust.business.ts.model.parameter.type.impl.ParameterType;
import lu.itrust.business.ts.model.parameter.value.IValue;
import lu.itrust.business.ts.model.parameter.value.impl.FormulaValue;
import lu.itrust.business.ts.model.scale.ScaleType;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.MaturityStandard;
import lu.itrust.business.ts.model.standard.measure.Measure;

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
	private ServiceIlrSoaScaleParameter serviceIlrSoaScaleParameter;

	@Autowired
	private AnalysisImpactManager analysisImpactManager;

	@Autowired
	private ServiceParameterType serviceParameterType;

	@Autowired
	private ServiceScaleType serviceScaleType;

	@RequestMapping(value = "/Impact-scale/Manage", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public String manageImpactScale(Model model, HttpSession session, Principal principal, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		Map<ScaleType, Boolean> impacts = new LinkedHashMap<>();
		serviceScaleType.findFromAnalysis(idAnalysis).forEach(scale -> impacts.put(scale, true));
		serviceScaleType.findAll().stream().filter(scale -> !impacts.containsKey(scale))
				.forEach(scale -> impacts.put(scale, false));
		model.addAttribute("quantitativeImpact", impacts.keySet().stream()
				.filter(impact -> impact.getName().equals(Constant.DEFAULT_IMPACT_NAME)).findAny().orElse(null));
		model.addAttribute("impacts", impacts);
		model.addAttribute("langue", locale.getLanguage().toUpperCase());
		return "jsp/analyses/single/components/parameters/form/mange-impact";
	}

	@RequestMapping(value = "/Impact-scale/Manage/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String manageImpactScaleSave(@RequestBody Map<Integer, Boolean> impacts, HttpSession session,
			Principal principal, Locale locale) {
		try {
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			return analysisImpactManager.manageImpactScaleSave(idAnalysis, impacts)
					? JsonMessage.Success(messageSource.getMessage("success.analysis.update.impact_scale", null,
							"Impacts scales have been updated", locale))
					: JsonMessage.Warning(messageSource.getMessage("warning.analysis.update.impact_scale", null,
							"Your analysis has not be updated", locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage
					.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	@RequestMapping(value = "/Scale-level/Manage", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public String manageScaleLevel(Model model, HttpSession session, Principal principal, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		model.addAttribute("maxLevel", serviceLikelihoodParameter.findMaxLevelByIdAnalysis(idAnalysis));
		return "jsp/analyses/single/components/parameters/form/mange-scale-level";
	}

	@RequestMapping(value = "/Scale-level/Manage/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String manageScaleLevelSave(@RequestBody Map<Integer, List<Integer>> levels,
			HttpSession session, Principal principal, Locale locale) {
		try {
			final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			final Worker worker = new WorkerScaleLevelMigrator(idAnalysis, levels);
			if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale)) {
				executor.execute(worker);
				return JsonMessage.Success(messageSource.getMessage("success.analysis.scale.level.migrating.start",
						null, "Please wait while migrating scale level.", locale));
			}
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null,
					"Too many tasks running in background", locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage
					.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public String section(Model model, HttpSession session, Principal principal) throws Exception {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		if (analysis.isQuantitative())
			setupQualitativeParameterUI(model, analysis);
		model.addAttribute("type", analysis.getType());
		model.addAttribute("reportSettings", loadReportSettings(analysis));
		model.addAttribute("exportFilenames", loadExportFileNames(analysis));
		model.addAttribute("isILR", Analysis.isILR(analysis));
		model.addAttribute("isEditable", !OpenMode.isReadOnly((OpenMode) session.getAttribute(OPEN_MODE)));
		model.addAttribute("mappedParameters", AnalysisUtils.SplitParameters(analysis.getParameters()));

		return "jsp/analyses/single/components/parameters/other";
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public String impactSection(Model model, HttpSession session, Principal principal) {
		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));

		if (analysis.getType().isQuantitative()) {
			final boolean showDynamicAnalysis = analysis.findSetting(AnalysisSetting.ALLOW_DYNAMIC_ANALYSIS);
			if (showDynamicAnalysis) {
				boolean showExcludeDynamic = analysis.findSetting(AnalysisSetting.ALLOW_EXCLUDE_DYNAMIC_ANALYSIS);
				model.addAttribute("showExcludeDynamic",
						showExcludeDynamic);
				if (showExcludeDynamic) {
					model.addAttribute("excludeAcronyms",
							analysis.getExcludeAcronyms().stream().sorted(NaturalOrderComparator::compareTo)
									.toList());
				}
			}
			model.addAttribute("showDynamicAnalysis", showDynamicAnalysis);
		}
		model.addAttribute("impactTypes",
				analysis.getImpactParameters().parallelStream().map(ImpactParameter::getType).distinct()
						.toList());
		model.addAttribute("isEditable", !OpenMode.isReadOnly((OpenMode) session.getAttribute(OPEN_MODE)));
		model.addAttribute("mappedParameters", AnalysisUtils.SplitParameters(analysis.getParameters()));
		model.addAttribute("type", analysis.getType());
		return "jsp/analyses/single/components/parameters/impact_probability";
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object maturityImplementationRate(Model model, HttpSession session, Principal principal)
			throws Exception {
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public String riskAcceptanceForm(Model model, HttpSession session, Principal principal) {
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Integer level = serviceLikelihoodParameter.findMaxLevelByIdAnalysis(idAnalysis);
		model.addAttribute("maxImportance", level * level);
		model.addAttribute("parameters", serviceRiskAcceptanceParameter.findByAnalysisId(idAnalysis));
		return "jsp/analyses/single/components/parameters/form/riskAcceptance";
	}

	@DeleteMapping(value = "/Dynamic/Delete/{id}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session,#id, 'DynamicParameter', #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String deleteDynamicParameter(@PathVariable int id, HttpSession session, Principal principal,
			Locale locale) {
		try {
			final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			final Analysis analysis = serviceAnalysis.get(idAnalysis);
			final DynamicParameter parameter = analysis.getDynamicParameters().stream().filter(p -> p.getId() == id)
					.findAny().orElse(null);
			final boolean[] deleteable = { true };
			for (Assessment e : analysis.getAssessments()) {
				if (e.getLikelihood() instanceof FormulaValue
						&& hasVariable(parameter.getAcronym(), e.getLikelihood().getVariable())) {
					deleteable[0] = false;
				} else {
					for (IValue b : e.getImpacts()) {
						if (b instanceof FormulaValue && hasVariable(parameter.getAcronym(), b.getVariable())) {
							deleteable[0] = false;
							break;
						}
					}
				}
				if (!deleteable[0])
					break;
			}

			if (deleteable[0]) {
				for (AnalysisStandard standard : analysis.getAnalysisStandards().values()) {
					if (!(standard instanceof MaturityStandard)) {
						for (Measure measure : standard.getMeasures()) {
							if (measure.getImplementationRate() instanceof String
									&& hasVariable(parameter.getAcronym(),
											measure.getImplementationRate().toString())) {
								deleteable[0] = false;
								break;
							}
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
				return JsonMessage.Success(messageSource.getMessage("success.delete.parameter", null,
						"Parameter has been successfully deleted", locale));
			}
			return JsonMessage.Error(messageSource.getMessage("error.parameter.in_used", null,
					"Parameter cannot be deleted as it still in used", locale));

		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage
					.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	@DeleteMapping(value = "/Dynamic/Restore/{acronym}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String restoreDynamicParameter(@PathVariable String acronym, HttpSession session,
			Principal principal,
			Locale locale) {
		try {
			final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
			if (analysis.getExcludeAcronyms().remove(acronym)) {
				analysis.getDynamicParameters()
						.add(new DynamicParameter(acronym, String.format("dynamic:%s", acronym), 0.0));
				serviceAnalysis.saveOrUpdate(analysis);
				return JsonMessage.Success(messageSource.getMessage("success.restore.parameter", null,
						"Parameter has been successfully restored", locale));
			}
			return JsonMessage.Error(messageSource.getMessage("error.acronym.not_found", null,
					"Acronym does not exist!", locale));

		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage
					.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
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
	@PostMapping(value = "/Risk-acceptance/Save", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String riskAcceptanceSave(@RequestBody List<RiskAcceptanceParameter> parameters,
			HttpSession session, Principal principal, Locale locale) {
		try {
			Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
			Map<Integer, RiskAcceptanceParameter> riskAcceptanceParameters = analysis.getRiskAcceptanceParameters()
					.stream()
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
				analysis.getRiskAcceptanceParameters()
						.removeIf(parameter -> riskAcceptanceParameters.containsKey(parameter.getId()));
				serviceRiskAcceptanceParameter.delete(riskAcceptanceParameters.values());
			}
			serviceAnalysis.saveOrUpdate(analysis);
			return JsonMessage.Success(messageSource.getMessage("success.update.risk_acceptance", null,
					"Risk acceptance has been successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
			TrickLogManager.Persist(e);
			if (e instanceof TrickException e1)
				return JsonMessage.Error(messageSource.getMessage(e1.getCode(),
						e1.getParameters(), e.getMessage(), locale));
			return JsonMessage
					.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
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
	@GetMapping(value = "/Ilr-soa-scale/form", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public String ilrSoaScaleForm(Model model, HttpSession session, Principal principal) {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		model.addAttribute("parameters", serviceIlrSoaScaleParameter.findByAnalysisId(idAnalysis));
		return "jsp/analyses/single/components/parameters/form/ilr-soa-scale";
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
	@PostMapping(value = "/Ilr-soa-scale/Save", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String ilrSoaScaleSave(@RequestBody List<IlrSoaScaleParameter> parameters,
			HttpSession session, Principal principal, Locale locale) {
		try {
			Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
			Map<Integer, IlrSoaScaleParameter> ilrSoaScaleParameters = analysis.getIlrSoaScaleParameters()
					.stream()
					.collect(Collectors.toMap(IlrSoaScaleParameter::getId, Function.identity()));
			for (IlrSoaScaleParameter ilrSoaScaleParameter : parameters) {
				IlrSoaScaleParameter parameter = ilrSoaScaleParameters.remove(ilrSoaScaleParameter.getId());
				if (parameter == null)
					analysis.add(ilrSoaScaleParameter);
				else {
					parameter.setColor(ilrSoaScaleParameter.getColor());
					parameter.setDescription(ilrSoaScaleParameter.getDescription());
					parameter.setValue(ilrSoaScaleParameter.getValue());
				}
			}

			if (!ilrSoaScaleParameters.isEmpty()) {
				analysis.getIlrSoaScaleParameters()
						.removeIf(parameter -> ilrSoaScaleParameters.containsKey(parameter.getId()));
				serviceIlrSoaScaleParameter.delete(ilrSoaScaleParameters.values());
			}
			serviceAnalysis.saveOrUpdate(analysis);
			return JsonMessage.Success(messageSource.getMessage("success.update.ilr-soa-scale", null,
					"ILR SOA Scales had been successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
			TrickLogManager.Persist(e);
			if (e instanceof TrickException)
				return JsonMessage.Error(messageSource.getMessage(((TrickException) e).getCode(),
						((TrickException) e).getParameters(), e.getMessage(), locale));
			return JsonMessage
					.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	@PostMapping(value = "/IlrVulnerability/Add", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object addIlrVulnerabilityParameter(@RequestBody SimpleParameter data, HttpSession session,
			Principal principal,
			Locale locale) {

		if (!StringUtils.hasText(data.getDescription()))
			return JsonMessage
					.Error(messageSource.getMessage("error.parameter.invalid", null, "Invalid parameter", locale));

		final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		final List<SimpleParameter> parameters = analysis.getSimpleParameters().stream()
				.filter(p -> p.getType().getName().equals(Constant.PARAMETERTYPE_TYPE_ILR_VULNERABILITY_SCALE_NAME))
				.collect(Collectors.toList());
		ParameterType type = parameters.stream().map(SimpleParameter::getType).findAny().orElseGet(() -> {
			ParameterType t = serviceParameterType.getByName(Constant.PARAMETERTYPE_TYPE_ILR_VULNERABILITY_SCALE_NAME);
			return t == null ? new ParameterType(Constant.PARAMETERTYPE_TYPE_ILR_VULNERABILITY_SCALE_NAME) : t;
		});

		SimpleParameter parameter = new SimpleParameter(type, data.getDescription().trim(),
				parameters.stream().mapToDouble(p -> p.getValue()).max().orElse(-1) + 1);

		analysis.add(parameter);

		serviceAnalysis.saveOrUpdate(analysis);

		final Map<String, Object> result = new HashMap<>();
		result.put("id", parameter.getId());
		result.put("reload", Math.abs(parameter.getValue() - data.getValue()) > 1e-6);
		result.put("success",
				messageSource.getMessage("success.add.parameter", null, "Parameter has been added", locale));

		return result;
	}

	/**
	 * Deletes an ILR vulnerability parameter.
	 *
	 * @param id        The ID of the parameter to be deleted.
	 * @param session   The HttpSession object.
	 * @param principal The Principal object.
	 * @param locale    The Locale object.
	 * @return An Object representing the result of the deletion operation.
	 */
	@DeleteMapping(value = "/IlrVulnerability/Delete/{id}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object deleteIlrVulnerabilityParameter(@PathVariable int id, HttpSession session,
			Principal principal,
			Locale locale) {
		try {
			final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));

			final SimpleParameter parameter = analysis.getSimpleParameters().stream()
					.filter(p -> p.getId() == id
							&& p.getType().getName().equals(Constant.PARAMETERTYPE_TYPE_ILR_VULNERABILITY_SCALE_NAME))
					.findAny().orElse(null);
			if (parameter == null)
				return JsonMessage.Error(
						messageSource.getMessage("error.parameter.not_found", null, "Parameter not found", locale));

			analysis.getSimpleParameters().remove(parameter);
			final List<SimpleParameter> parameters = analysis.getSimpleParameters().stream()
					.filter(p -> p.getType().getName().equals(Constant.PARAMETERTYPE_TYPE_ILR_VULNERABILITY_SCALE_NAME))
					.sorted((e1, e2) -> Double.compare(e1.getValue(), e2.getValue())).collect(Collectors.toList());

			final Map<String, Object> result = new HashMap<>();
			final SimpleParameter last = parameters.isEmpty() ? null : parameters.get(parameters.size() - 1);
			if (!(last == null || parameter.getValue() >= last.getValue())) {
				for (int i = parameter.getValue().intValue(); i < parameters.size(); i++)
					parameters.get(i).setValue(i);
				result.put("reload", true);
			}

			serviceAnalysis.saveOrUpdate(analysis);
			serviceSimpleParameter.delete(parameter);
			result.put("success",
					messageSource.getMessage("success.delete.parameter", null, "Parameter has been deleted", locale));
			return result;

		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage
					.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	private boolean hasVariable(final String acronym, final String formular) {
		return new TokenizerToString(formular).getTokens().parallelStream()
				.anyMatch(v -> v.getType().equals(TokenType.Variable) && v.getParameter().equals(acronym));
	}

}

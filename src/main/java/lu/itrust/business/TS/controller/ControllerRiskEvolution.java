/**
 * 
 */
package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.component.ALEChart;
import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.ComplianceChartData;
import lu.itrust.business.TS.component.Distribution;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.NaturalOrderComparator;
import lu.itrust.business.TS.component.chartJS.Chart;
import lu.itrust.business.TS.component.chartJS.helper.ColorBound;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.analysis.helper.AnalysisBaseInfo;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.assessment.helper.ALE;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.RiskAcceptanceParameter;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.scenario.ScenarioType;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
@Controller
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/Risk-evolution")
public class ControllerRiskEvolution {

	@Value("${app.settings.ale.chart.content.max.size}")
	private int aleChartMaxSize;

	@Value("${app.settings.ale.chart.single.content.max.size}")
	private int aleChartSingleMaxSize;

	@Value("${app.settings.ale.chart.content.size}")
	private int aleChartSize;

	@Autowired
	private ChartGenerator chartGenerator;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceUser serviceUser;

	@RequestMapping(value = "/Save-settings", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object saveSettings(@RequestBody String value, Principal principal, Locale locale) {
		User user = serviceUser.get(principal.getName());
		boolean isFirst = user.getUserSettings().containsKey("risk-evolution-data");
		user.setSetting("risk-evolution-data", value);
		serviceUser.saveOrUpdate(user);
		return isFirst ? isFirst : JsonMessage.Success(messageSource.getMessage("success.risk_evolution.setting.saved", null, "Your configuration was been updated", locale));
	}

	@RequestMapping(value = "/Chart/Compliance", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody List<Chart> compliance(Principal principal, @RequestParam(name = "customerId") int customerId, @RequestParam(name = "analyses") List<Integer> analysisIds,
			Locale locale) {
		List<Analysis> analyses = loadAnalyses(principal, customerId, analysisIds);
		List<Chart> charts = new LinkedList<>();
		if (analyses.isEmpty())
			charts.add(chartGenerator.compliance(locale, new ComplianceChartData("27001", null)));
		else {
			Map<String, List<ComplianceChartData>> complianceCharts = new LinkedHashMap<>(analyses.get(0).getAnalysisStandards().size());
			String analysisName = analyses.get(0).getLabel() + " " + analyses.get(0).getVersion();
			analyses.get(0).getAnalysisStandards().forEach(analysisStandard -> {
				List<ComplianceChartData> data = new LinkedList<>();
				data.add(new ComplianceChartData(analysisStandard.getStandard().getLabel(), analysisName, analysisStandard.getMeasures(),
						analyses.get(0).getExpressionParameters()));
				complianceCharts.put(analysisStandard.getStandard().getLabel(), data);
			});

			for (int i = 1; i < analyses.size(); i++) {
				Analysis analysis = analyses.get(i);
				String name = analysis.getLabel() + " " + analysis.getVersion();
				for (AnalysisStandard analysisStandard : analysis.getAnalysisStandards()) {
					List<ComplianceChartData> data = complianceCharts.get(analysisStandard.getStandard().getLabel());
					if (data != null)
						data.add(new ComplianceChartData(analysisStandard.getStandard().getLabel(), name, analysisStandard.getMeasures(), analysis.getExpressionParameters()));
				}
			}

			for (List<ComplianceChartData> chartDatas : complianceCharts.values())
				charts.add(chartGenerator.compliance(locale, chartDatas.toArray(new ComplianceChartData[chartDatas.size()])));
		}
		return charts;
	}

	@RequestMapping(value = "/Type/{type}/Customer/{id}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody List<AnalysisBaseInfo> findByCustomer(@PathVariable AnalysisType type, @PathVariable Integer id, Principal principal) {
		List<AnalysisBaseInfo> collection1 = serviceAnalysis.getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(id, principal.getName(), type, AnalysisRight.highRightFrom(AnalysisRight.READ));
		List<AnalysisBaseInfo> collection2 = serviceAnalysis.getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(id, principal.getName(), AnalysisType.HYBRID, AnalysisRight.highRightFrom(AnalysisRight.READ));
		return Stream.concat(collection1.stream(), collection2.stream()).collect(Collectors.toList());
	}

	@RequestMapping(value = "/Type/{type}/Customer/{id}/Identifier/{identifier}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody List<AnalysisBaseInfo> findByCustomerAndIdentifier(@PathVariable AnalysisType type, @PathVariable Integer id, @PathVariable String identifier, Principal principal) {
		List<AnalysisBaseInfo> collection1 = serviceAnalysis.getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(id, principal.getName(), identifier, type, AnalysisRight.highRightFrom(AnalysisRight.READ));
		List<AnalysisBaseInfo> collection2 = serviceAnalysis.getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(id, principal.getName(), identifier, AnalysisType.HYBRID, AnalysisRight.highRightFrom(AnalysisRight.READ));
		return Stream.concat(collection1.stream(), collection2.stream()).collect(Collectors.toList());
	}

	@RequestMapping
	public String home(Principal principal, HttpSession session, Model model) throws Exception {
		LoadUserAnalyses(session, principal, model);
		model.addAttribute("types", Arrays.stream(AnalysisType.values()).filter(type -> type != AnalysisType.HYBRID).toArray());
		return "analyses/risk-evolution/home";
	}

	@RequestMapping(value = "/Chart/ALE-by-asset", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody List<Chart> loadALEByAsset(Principal principal, @RequestParam(name = "customerId") int customerId,
			@RequestParam(name = "analyses") List<Integer> analysisIds, Locale locale) {
		int index = 0;
		List<Analysis> analyses = loadAnalyses(principal, customerId, analysisIds);
		ALEChart[] chartData = new ALEChart[analyses.size()];
		for (Analysis analysis : analyses) {
			List<Assessment> assessments = analysis.getSelectedAssessments();
			Map<Integer, ALE> mappedALEs = new LinkedHashMap<Integer, ALE>();
			List<ALE> ales = new LinkedList<ALE>();
			for (Assessment assessment : assessments) {
				ALE ale = mappedALEs.get(assessment.getAsset().getId());
				if (ale == null) {
					mappedALEs.put(assessment.getAsset().getId(), ale = new ALE(assessment.getAsset().getName(), 0));
					ales.add(ale);
				}
				ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
			}

			ales.sort(ALE.Comparator().reversed());
			chartData[index++] = new ALEChart(analysis.getLabel() + " - " + analysis.getVersion(), ales);
		}

		List<Chart> charts = new LinkedList<>();
		if (chartData.length == 0 || chartData[0].getAles().size() <= aleChartSingleMaxSize)
			charts.add(chartGenerator.generateALEJSChart(locale, messageSource.getMessage("label.title.chart.ale_by_asset", null, "ALE by Asset", locale), chartData));
		else {
			List<ALE> ales = chartData[0].getAles();
			Distribution distribution = Distribution.Distribut(ales.size(), aleChartSize, aleChartMaxSize);
			int multiplicator = ales.size() / distribution.getDivisor();
			for (int i = 0; i < multiplicator; i++) {
				chartData[0].setAles(ales.subList(i * distribution.getDivisor(), i == (multiplicator - 1) ? ales.size() : (i + 1) * distribution.getDivisor()));
				charts.add(chartGenerator.generateALEJSChart(locale, messageSource.getMessage("label.title.chart.part.ale_by_asset", new Integer[] { i + 1, multiplicator },
						String.format("ALE by Asset %d/%d", i + 1, multiplicator), locale), chartData));
			}
		}
		return charts;
	}

	@RequestMapping(value = "/Chart/ALE-by-asset-type", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Chart loadALEByAssetType(Principal principal, @RequestParam(name = "customerId") int customerId,
			@RequestParam(name = "analyses") List<Integer> analysisIds, Locale locale) {
		int index = 0;
		List<Analysis> analyses = loadAnalyses(principal, customerId, analysisIds);
		ALEChart[] charts = new ALEChart[analyses.size()];
		for (Analysis analysis : analyses) {
			List<Assessment> assessments = analysis.getSelectedAssessments();
			Map<Integer, ALE> mappedALEs = new LinkedHashMap<Integer, ALE>();
			List<ALE> ales = new LinkedList<ALE>();
			for (Assessment assessment : assessments) {
				ALE ale = mappedALEs.get(assessment.getAsset().getAssetType().getId());
				if (ale == null) {
					mappedALEs.put(assessment.getAsset().getAssetType().getId(), ale = new ALE(assessment.getAsset().getAssetType().getName(), 0));
					ales.add(ale);
				}
				ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
			}

			ales.sort(ALE.Comparator().reversed());
			charts[index++] = new ALEChart(analysis.getLabel() + " - " + analysis.getVersion(), ales);
		}
		return chartGenerator.generateALEJSChart(locale, messageSource.getMessage("label.title.chart.ale_by_asset_type", null, "ALE by Asset Type", locale), charts);
	}

	@RequestMapping(value = "/Chart/ALE-by-scenario", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody List<Chart> loadALEByScenario(Principal principal, @RequestParam(name = "customerId") int customerId,
			@RequestParam(name = "analyses") List<Integer> analysisIds, Locale locale) {
		int index = 0;
		List<Analysis> analyses = loadAnalyses(principal, customerId, analysisIds);
		ALEChart[] chartData = new ALEChart[analyses.size()];
		for (Analysis analysis : analyses) {
			List<Assessment> assessments = analysis.getSelectedAssessments();
			Map<Integer, ALE> mappedALEs = new LinkedHashMap<Integer, ALE>();
			List<ALE> ales = new LinkedList<ALE>();
			for (Assessment assessment : assessments) {
				ALE ale = mappedALEs.get(assessment.getScenario().getId());
				if (ale == null) {
					mappedALEs.put(assessment.getScenario().getId(), ale = new ALE(assessment.getScenario().getName(), 0));
					ales.add(ale);
				}
				ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
			}

			ales.sort(ALE.Comparator().reversed());
			chartData[index++] = new ALEChart(analysis.getLabel() + " - " + analysis.getVersion(), ales);
		}

		List<Chart> charts = new LinkedList<>();
		if (chartData.length == 0 || chartData[0].getAles().size() <= aleChartSingleMaxSize)
			charts.add(chartGenerator.generateALEJSChart(locale, messageSource.getMessage("label.title.chart.ale_by_scenario", null, "ALE by Scenario", locale), chartData));
		else {
			List<ALE> ales = chartData[0].getAles();
			Distribution distribution = Distribution.Distribut(ales.size(), aleChartSize, aleChartMaxSize);
			int multiplicator = ales.size() / distribution.getDivisor();
			for (int i = 0; i < multiplicator; i++) {
				chartData[0].setAles(ales.subList(i * distribution.getDivisor(), i == (multiplicator - 1) ? ales.size() : (i + 1) * distribution.getDivisor()));
				charts.add(chartGenerator.generateALEJSChart(locale, messageSource.getMessage("label.title.chart.part.ale_by_scenario", new Integer[] { i + 1, multiplicator },
						String.format("ALE by Scenario %d/%d", i + 1, multiplicator), locale), chartData));
			}
		}
		return charts;
	}

	@RequestMapping(value = "/Chart/ALE-by-scenario-type", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Chart loadALEByScenarioType(Principal principal, @RequestParam(name = "customerId") int customerId,
			@RequestParam(name = "analyses") List<Integer> analysisIds, Locale locale) {
		int index = 0;
		List<Analysis> analyses = loadAnalyses(principal, customerId, analysisIds);
		ALEChart[] charts = new ALEChart[analyses.size()];
		for (Analysis analysis : analyses) {
			List<Assessment> assessments = analysis.getSelectedAssessments();
			Map<Integer, ALE> mappedALEs = new LinkedHashMap<Integer, ALE>();
			List<ALE> ales = new LinkedList<ALE>();
			for (Assessment assessment : assessments) {
				ALE ale = mappedALEs.get(assessment.getScenario().getType().getValue());
				if (ale == null) {
					mappedALEs.put(assessment.getScenario().getType().getValue(), ale = new ALE(assessment.getScenario().getType().getName(), 0));
					ales.add(ale);
				}
				ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
			}

			ales.sort(ALE.Comparator().reversed());
			charts[index++] = new ALEChart(analysis.getLabel() + " - " + analysis.getVersion(), ales);
		}
		return chartGenerator.generateALEJSChart(locale, messageSource.getMessage("label.title.chart.ale_by_scenario_type", null, "ALE by Scenario Type", locale), charts);
	}

	@RequestMapping(value = "/Chart/Risk-by-asset", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody List<Chart> loadRiskByAsset(Principal principal, @RequestParam(name = "customerId") int customerId,
			@RequestParam(name = "analyses") List<Integer> analysisIds, Locale locale) {
		List<Chart> charts = new LinkedList<>();
		List<Analysis> analyses = loadAnalyses(principal, customerId, analysisIds);
		if (!analyses.isEmpty()) {
			List<RiskAcceptanceParameter> riskAcceptanceParameters = analyses.get(0).getRiskAcceptanceParameters();
			List<ColorBound> colorBounds = new ArrayList<>(riskAcceptanceParameters.size());
			for (int i = 0; i < riskAcceptanceParameters.size(); i++) {
				RiskAcceptanceParameter parameter = riskAcceptanceParameters.get(i);
				if (colorBounds.isEmpty())
					colorBounds.add(new ColorBound(parameter.getColor(), parameter.getLabel(), 0, parameter.getValue().intValue()));
				else if (riskAcceptanceParameters.size() == (i + 1))
					colorBounds.add(new ColorBound(parameter.getColor(), parameter.getLabel(), riskAcceptanceParameters.get(i - 1).getValue().intValue(), Integer.MAX_VALUE));
				else
					colorBounds.add(
							new ColorBound(parameter.getColor(), parameter.getLabel(), riskAcceptanceParameters.get(i - 1).getValue().intValue(), parameter.getValue().intValue()));
			}
			Map<String, Boolean> assetNames = analyses.get(0).getAssets().stream().collect(Collectors.toMap(Asset::getName, asset -> true));
			for (Analysis analysis : analyses) {
				Map<String, List<Assessment>> assessments = analysis.getAssessments().stream()
						.filter(assessment -> assessment.isSelected() && assetNames.containsKey(assessment.getAsset().getName()))
						.sorted((a1, a2) -> NaturalOrderComparator.compareTo(a1.getAsset().getName(), a2.getAsset().getName()))
						.collect(Collectors.groupingBy(assessment -> assessment.getAsset().getName()));
				List<Chart> analysisCharts = chartGenerator.generateAssessmentRiskChart(new ValueFactory(analysis.getParameters()), assessments, colorBounds);
				analysisCharts.stream().findFirst().ifPresent(chart -> chart.setTitle(analysis.getLabel() + " " + analysis.getVersion()));
				charts.addAll(analysisCharts);
			}
			charts.stream().findFirst().ifPresent(chart -> chart.setSettings(riskAcceptanceParameters));
		}
		return charts;

	}

	@RequestMapping(value = "/Chart/Risk-by-asset-type", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody List<Chart> loadRiskByAssetType(Principal principal, @RequestParam(name = "customerId") int customerId,
			@RequestParam(name = "analyses") List<Integer> analysisIds, Locale locale) {
		List<Chart> charts = new LinkedList<>();
		List<Analysis> analyses = loadAnalyses(principal, customerId, analysisIds);
		if (!analyses.isEmpty()) {
			List<RiskAcceptanceParameter> riskAcceptanceParameters = analyses.get(0).getRiskAcceptanceParameters();
			List<ColorBound> colorBounds = new ArrayList<>(riskAcceptanceParameters.size());
			for (int i = 0; i < riskAcceptanceParameters.size(); i++) {
				RiskAcceptanceParameter parameter = riskAcceptanceParameters.get(i);
				if (colorBounds.isEmpty())
					colorBounds.add(new ColorBound(parameter.getColor(), parameter.getLabel(), 0, parameter.getValue().intValue()));
				else if (riskAcceptanceParameters.size() == (i + 1))
					colorBounds.add(new ColorBound(parameter.getColor(), parameter.getLabel(), riskAcceptanceParameters.get(i - 1).getValue().intValue(), Integer.MAX_VALUE));
				else
					colorBounds.add(
							new ColorBound(parameter.getColor(), parameter.getLabel(), riskAcceptanceParameters.get(i - 1).getValue().intValue(), parameter.getValue().intValue()));
			}
			Map<String, Boolean> names = analyses.get(0).getAssets().stream().map(Asset::getAssetType).distinct().collect(Collectors.toMap(AssetType::getName, asset -> true));
			for (Analysis analysis : analyses) {
				Map<String, List<Assessment>> assessments = analysis.getAssessments().stream()
						.filter(assessment -> assessment.isSelected() && names.containsKey(assessment.getAsset().getAssetType().getName()))
						.sorted((a1, a2) -> NaturalOrderComparator.compareTo(
								messageSource.getMessage("label.asset_type." + a1.getAsset().getAssetType().getName().toLowerCase(), null, locale),
								messageSource.getMessage("label.asset_type." + a2.getAsset().getAssetType().getName().toLowerCase(), null, locale)))
						.collect(Collectors.groupingBy(
								assessment -> messageSource.getMessage("label.asset_type." + assessment.getAsset().getAssetType().getName().toLowerCase(), null, locale)));
				List<Chart> analysisCharts = chartGenerator.generateAssessmentRiskChart(new ValueFactory(analysis.getParameters()), assessments, colorBounds);
				analysisCharts.stream().findFirst().ifPresent(chart -> chart.setTitle(analysis.getLabel() + " " + analysis.getVersion()));
				charts.addAll(analysisCharts);
			}
			charts.stream().findFirst().ifPresent(chart -> chart.setSettings(riskAcceptanceParameters));
		}
		return charts;
	}

	@RequestMapping(value = "/Chart/Risk-by-scenario", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody List<Chart> loadRiskByScenario(Principal principal, @RequestParam(name = "customerId") int customerId,
			@RequestParam(name = "analyses") List<Integer> analysisIds, Locale locale) {
		List<Chart> charts = new LinkedList<>();
		List<Analysis> analyses = loadAnalyses(principal, customerId, analysisIds);
		if (!analyses.isEmpty()) {
			List<RiskAcceptanceParameter> riskAcceptanceParameters = analyses.get(0).getRiskAcceptanceParameters();
			List<ColorBound> colorBounds = new ArrayList<>(riskAcceptanceParameters.size());
			for (int i = 0; i < riskAcceptanceParameters.size(); i++) {
				RiskAcceptanceParameter parameter = riskAcceptanceParameters.get(i);
				if (colorBounds.isEmpty())
					colorBounds.add(new ColorBound(parameter.getColor(), parameter.getLabel(), 0, parameter.getValue().intValue()));
				else if (riskAcceptanceParameters.size() == (i + 1))
					colorBounds.add(new ColorBound(parameter.getColor(), parameter.getLabel(), riskAcceptanceParameters.get(i - 1).getValue().intValue(), Integer.MAX_VALUE));
				else
					colorBounds.add(
							new ColorBound(parameter.getColor(), parameter.getLabel(), riskAcceptanceParameters.get(i - 1).getValue().intValue(), parameter.getValue().intValue()));
			}
			Map<String, Boolean> names = analyses.get(0).getScenarios().stream().collect(Collectors.toMap(Scenario::getName, asset -> true));
			for (Analysis analysis : analyses) {
				Map<String, List<Assessment>> assessments = analysis.getAssessments().stream()
						.filter(assessment -> assessment.isSelected() && names.containsKey(assessment.getScenario().getName()))
						.sorted((a1, a2) -> NaturalOrderComparator.compareTo(a1.getScenario().getName(), a2.getScenario().getName()))
						.collect(Collectors.groupingBy(assessment -> assessment.getScenario().getName()));
				List<Chart> analysisCharts = chartGenerator.generateAssessmentRiskChart(new ValueFactory(analysis.getParameters()), assessments, colorBounds);
				analysisCharts.stream().findFirst().ifPresent(chart -> chart.setTitle(analysis.getLabel() + " " + analysis.getVersion()));
				charts.addAll(analysisCharts);
			}
			charts.stream().findFirst().ifPresent(chart -> chart.setSettings(riskAcceptanceParameters));
		}
		return charts;
	}

	@RequestMapping(value = "/Chart/Risk-by-scenario-type", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody List<Chart> loadRiskByScenarioType(Principal principal, @RequestParam(name = "customerId") int customerId,
			@RequestParam(name = "analyses") List<Integer> analysisIds, Locale locale) {
		List<Chart> charts = new LinkedList<>();
		List<Analysis> analyses = loadAnalyses(principal, customerId, analysisIds);
		if (!analyses.isEmpty()) {
			List<RiskAcceptanceParameter> riskAcceptanceParameters = analyses.get(0).getRiskAcceptanceParameters();
			List<ColorBound> colorBounds = new ArrayList<>(riskAcceptanceParameters.size());
			for (int i = 0; i < riskAcceptanceParameters.size(); i++) {
				RiskAcceptanceParameter parameter = riskAcceptanceParameters.get(i);
				if (colorBounds.isEmpty())
					colorBounds.add(new ColorBound(parameter.getColor(), parameter.getLabel(), 0, parameter.getValue().intValue()));
				else if (riskAcceptanceParameters.size() == (i + 1))
					colorBounds.add(new ColorBound(parameter.getColor(), parameter.getLabel(), riskAcceptanceParameters.get(i - 1).getValue().intValue(), Integer.MAX_VALUE));
				else
					colorBounds.add(
							new ColorBound(parameter.getColor(), parameter.getLabel(), riskAcceptanceParameters.get(i - 1).getValue().intValue(), parameter.getValue().intValue()));
			}
			Map<String, Boolean> names = analyses.get(0).getScenarios().stream().map(Scenario::getType).distinct().collect(Collectors.toMap(ScenarioType::getName, asset -> true));
			for (Analysis analysis : analyses) {
				Map<String, List<Assessment>> assessments = analysis.getAssessments().stream()
						.filter(assessment -> assessment.isSelected() && names.containsKey(assessment.getScenario().getType().getName()))
						.sorted((a1, a2) -> NaturalOrderComparator.compareTo(
								messageSource.getMessage("label.scenario.type." + a1.getScenario().getType().getName().replace("-", "_").toLowerCase(), null, locale), messageSource
										.getMessage("label.scenario.type." + a2.getScenario().getType().getName().replace("-", "_").toLowerCase(), null, locale)))
						.collect(Collectors.groupingBy(assessment -> messageSource
								.getMessage("label.scenario.type." + assessment.getScenario().getType().getName().replace("-", "_").toLowerCase(), null, locale)));
				List<Chart> analysisCharts = chartGenerator.generateAssessmentRiskChart(new ValueFactory(analysis.getParameters()), assessments, colorBounds);
				analysisCharts.stream().findFirst().ifPresent(chart -> chart.setTitle(analysis.getLabel() + " " + analysis.getVersion()));
				charts.addAll(analysisCharts);
			}
			charts.stream().findFirst().ifPresent(chart -> chart.setSettings(riskAcceptanceParameters));
		}
		return charts;
	}

	@RequestMapping(value = "/Chart/Total-ALE", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Chart loadTotalALE(Principal principal, @RequestParam(name = "customerId") int customerId, @RequestParam(name = "analyses") List<Integer> analysisIds,
			Locale locale) {
		return computeTotalALE(loadAnalyses(principal, customerId, analysisIds), locale);
	}

	@RequestMapping(value = "/Chart/Total-Risk", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Chart loadTotalRisk(Principal principal, @RequestParam(name = "customerId") int customerId, @RequestParam(name = "analyses") List<Integer> analysisIds,
			HttpServletRequest request, Locale locale) throws Exception {
		return chartGenerator.generateTotalRiskJSChart(loadAnalyses(principal, customerId, analysisIds), locale);
	}

	private Chart computeTotalALE(List<Analysis> analyses, Locale locale) {
		List<ALE> ales = new ArrayList<>(analyses.size());
		analyses.forEach(analysis -> ales.add(new ALE(analysis.getLabel() + " " + analysis.getVersion(),
				analysis.getAssessments().stream().filter(Assessment::isSelected).mapToDouble(Assessment::getALE).sum())));
		ales.forEach(ale -> ale.setValue(ale.getValue() * 0.001));
		return chartGenerator.generateALEJSChart(locale, messageSource.getMessage("label.title.chart.total_ale", null, "Total ALE", locale), ales);
	}

	private List<Analysis> loadAnalyses(Principal principal, int customerId, List<Integer> analysisIds) {
		Customer customer = serviceCustomer.getFromUsernameAndId(principal.getName(), customerId);
		List<Analysis> analyses = new ArrayList<>(analysisIds.size());
		for (Integer analysisId : analysisIds) {
			Analysis analysis = serviceAnalysis.getByUsernameAndId(principal.getName(), analysisId);
			if (analysis == null || !analysis.hasData() || !analysis.getCustomer().equals(customer))
				continue;
			analyses.add(analysis);
		}
		return analyses;
	}

	private void LoadUserAnalyses(HttpSession session, Principal principal, Model model) throws Exception {
		User user = serviceUser.get(principal.getName());
		model.addAttribute("customers", user.getCustomers().stream().filter(Customer::isCanBeUsed).collect(Collectors.toList()));
		model.addAttribute("riskEvolutionSettings", user.getUserSettings().get("risk-evolution-data"));
	}

}

/**
 * 
 */
package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.component.ALEChart;
import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.ComplianceChartData;
import lu.itrust.business.TS.component.Distribution;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.helper.AnalysisBaseInfo;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.assessment.helper.ALE;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.standard.AnalysisStandard;

/**
 * @author eomar
 *
 */
@Controller
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/Risk-evolution")
public class ControllerRiskEvolution {

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ChartGenerator chartGenerator;

	@Autowired
	private MessageSource messageSource;

	@Value("${app.settings.ale.chart.content.max.size}")
	private int aleChartMaxSize;

	@Value("${app.settings.ale.chart.content.size}")
	private int aleChartSize;

	@Value("${app.settings.ale.chart.single.content.max.size}")
	private int aleChartSingleMaxSize;

	@RequestMapping
	public String home(Principal principal, HttpSession session, Model model) throws Exception {
		LoadUserAnalyses(session, principal, model);
		return "analyses/risk-evolution/home";
	}

	private void LoadUserAnalyses(HttpSession session, Principal principal, Model model) throws Exception {
		model.addAttribute("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));
	}

	@RequestMapping(value = "/Chart/Total-ALE", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String loadTotalALE(Principal principal, @RequestParam(name = "customerId") int customerId, @RequestParam(name = "analyses") List<Integer> analysisIds,
			Locale locale) {
		return computeTotalALE(loadAnalyses(principal, customerId, analysisIds), locale);
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

	@RequestMapping(value = "/Chart/ALE-by-scenario-type", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String loadALEByScenarioType(Principal principal, @RequestParam(name = "customerId") int customerId,
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
		return chartGenerator.generateALEChart(locale, messageSource.getMessage("label.title.chart.ale_by_scenario_type", null, "ALE by Scenario Type", locale), charts);
	}

	@RequestMapping(value = "/Chart/ALE-by-asset-type", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String loadALEByAssetType(Principal principal, @RequestParam(name = "customerId") int customerId,
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
		return chartGenerator.generateALEChart(locale, messageSource.getMessage("label.title.chart.ale_by_asset_type", null, "ALE by Asset Type", locale), charts);
	}

	@RequestMapping(value = "/Chart/ALE-by-asset", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String loadALEByAsset(Principal principal, @RequestParam(name = "customerId") int customerId, @RequestParam(name = "analyses") List<Integer> analysisIds,
			Locale locale) {
		int index = 0;
		List<Analysis> analyses = loadAnalyses(principal, customerId, analysisIds);
		ALEChart[] charts = new ALEChart[analyses.size()];
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
			charts[index++] = new ALEChart(analysis.getLabel() + " - " + analysis.getVersion(), ales);
		}

		if (charts.length == 0 || charts[0].getAles().size() <= aleChartSingleMaxSize)
			return chartGenerator.generateALEChart(locale, messageSource.getMessage("label.title.chart.ale_by_asset", null, "ALE by Asset", locale), charts);
		else {
			String assetCharts = "";
			List<ALE> ales = charts[0].getAles();
			Distribution distribution = Distribution.Distribut(ales.size(), aleChartSize, aleChartMaxSize);
			int multiplicator = ales.size() / distribution.getDivisor();
			for (int i = 0; i < multiplicator; i++) {
				charts[0].setAles(ales.subList(i * distribution.getDivisor(), i == (multiplicator - 1) ? ales.size() : (i + 1) * distribution.getDivisor()));
				assetCharts += String.format("%s%s", assetCharts.isEmpty() ? "" : ",",
						chartGenerator.generateALEChart(locale, messageSource.getMessage("label.title.chart.part.ale_by_asset", new Integer[] { i + 1, multiplicator },
								String.format("ALE by Asset %d/%d", i + 1, multiplicator), locale), charts));
			}
			return "[" + assetCharts + "]";
		}
	}

	@RequestMapping(value = "/Chart/ALE-by-scenario", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String loadALEByScenario(Principal principal, @RequestParam(name = "customerId") int customerId,
			@RequestParam(name = "analyses") List<Integer> analysisIds, Locale locale) {
		int index = 0;
		List<Analysis> analyses = loadAnalyses(principal, customerId, analysisIds);
		ALEChart[] charts = new ALEChart[analyses.size()];
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
			charts[index++] = new ALEChart(analysis.getLabel() + " - " + analysis.getVersion(), ales);
		}

		if (charts.length == 0 || charts[0].getAles().size() <= aleChartSingleMaxSize)
			return chartGenerator.generateALEChart(locale, messageSource.getMessage("label.title.chart.ale_by_scenario", null, "ALE by Scenario", locale), charts);
		else {
			String assetCharts = "";
			List<ALE> ales = charts[0].getAles();
			Distribution distribution = Distribution.Distribut(ales.size(), aleChartSize, aleChartMaxSize);
			int multiplicator = ales.size() / distribution.getDivisor();
			for (int i = 0; i < multiplicator; i++) {
				charts[0].setAles(ales.subList(i * distribution.getDivisor(), i == (multiplicator - 1) ? ales.size() : (i + 1) * distribution.getDivisor()));
				assetCharts += String.format("%s%s", assetCharts.isEmpty() ? "" : ",",
						chartGenerator.generateALEChart(locale, messageSource.getMessage("label.title.chart.part.ale_by_scenario", new Integer[] { i + 1, multiplicator },
								String.format("ALE by Scenario %d/%d", i + 1, multiplicator), locale), charts));
			}
			return "[" + assetCharts + "]";
		}
	}

	@RequestMapping(value = "/Chart/Compliance", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object compliance(Principal principal, @RequestParam(name = "customerId") int customerId, @RequestParam(name = "analyses") List<Integer> analysisIds,
			Locale locale) {
		List<Analysis> analyses = loadAnalyses(principal, customerId, analysisIds);
		if (analyses.isEmpty())
			return chartGenerator.compliance(locale, new ComplianceChartData("27001", ""));
		Map<String, List<ComplianceChartData>> complianceCharts = new LinkedHashMap<>(analyses.get(0).getAnalysisStandards().size());
		String analysisName = analyses.get(0).getLabel() + " " + analyses.get(0).getVersion();
		analyses.get(0).getAnalysisStandards().forEach(analysisStandard -> {
			List<ComplianceChartData> charts = new LinkedList<>();
			charts.add(new ComplianceChartData(analysisStandard.getStandard().getLabel(), analysisName, analysisStandard.getMeasures(), analyses.get(0).getExpressionParameters()));
			complianceCharts.put(analysisStandard.getStandard().getLabel(), charts);

		});

		for (int i = 1; i < analyses.size(); i++) {
			Analysis analysis = analyses.get(i);
			String name = analysis.getLabel() + " " + analysis.getVersion();
			for (AnalysisStandard analysisStandard : analysis.getAnalysisStandards()) {
				List<ComplianceChartData> charts = complianceCharts.get(analysisStandard.getStandard().getLabel());
				if (charts != null)
					charts.add(new ComplianceChartData(analysisStandard.getStandard().getLabel(),name, analysisStandard.getMeasures(), analysis.getExpressionParameters()));
			}
		}
		String charts = "";
		for (List<ComplianceChartData> chartDatas : complianceCharts.values())
			charts += (charts.isEmpty() ? "" : ",") + chartGenerator.compliance(locale, chartDatas.toArray(new ComplianceChartData[chartDatas.size()]) );
		return complianceCharts.size() > 1 ? "[" + charts + "]" : charts;
	}

	@RequestMapping(value = "/Customer/{id}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody List<AnalysisBaseInfo> findByCustomer(@PathVariable Integer id, Principal principal) {
		return serviceAnalysis.getGroupByIdentifierAndFilterByCustmerIdAndUsernamerAndNotEmpty(id, principal.getName(), AnalysisRight.highRightFrom(AnalysisRight.READ));
	}

	@RequestMapping(value = "/Customer/{id}/Identifier/{identifier}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody List<AnalysisBaseInfo> findByCustomerAndIdentifier(@PathVariable Integer id, @PathVariable String identifier, Principal principal) {
		return serviceAnalysis.getBaseInfoByCustmerIdAndUsernamerAndIdentifierAndNotEmpty(id, principal.getName(), identifier, AnalysisRight.highRightFrom(AnalysisRight.READ));
	}
	
	private String computeTotalALE(List<Analysis> analyses, Locale locale) {
		List<ALE> ales = new ArrayList<>(analyses.size());
		analyses.forEach(analysis -> ales.add(new ALE(analysis.getLabel() + "<br />" + analysis.getVersion(),
				analysis.getAssessments().stream().filter(Assessment::isSelected).mapToDouble(Assessment::getALE).sum())));
		return chartGenerator.generateALEChart(locale, messageSource.getMessage("label.title.chart.total_ale", null, "Total ALE", locale) , ales);
	}

}

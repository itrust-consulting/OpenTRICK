package lu.itrust.business.TS.component;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lu.itrust.business.TS.component.chartJS.Chart;
import lu.itrust.business.TS.component.chartJS.ColorBound;
import lu.itrust.business.TS.component.chartJS.Dataset;
import lu.itrust.business.TS.component.chartJS.Legend;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOAnalysisStandard;
import lu.itrust.business.TS.database.dao.DAOAssessment;
import lu.itrust.business.TS.database.dao.DAOAssetType;
import lu.itrust.business.TS.database.dao.DAODynamicParameter;
import lu.itrust.business.TS.database.dao.DAOImpactParameter;
import lu.itrust.business.TS.database.dao.DAOLikelihoodParameter;
import lu.itrust.business.TS.database.dao.DAOMeasure;
import lu.itrust.business.TS.database.dao.DAOPhase;
import lu.itrust.business.TS.database.dao.DAORiskAcceptanceParameter;
import lu.itrust.business.TS.database.dao.DAOScenario;
import lu.itrust.business.TS.database.dao.DAOSimpleParameter;
import lu.itrust.business.TS.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.helper.ActionPlanComputation;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.assessment.helper.ALE;
import lu.itrust.business.TS.model.assessment.helper.AssetComparatorByALE;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.RiskAcceptanceParameter;
import lu.itrust.business.TS.model.rrf.RRF;
import lu.itrust.business.TS.model.rrf.RRFAsset;
import lu.itrust.business.TS.model.rrf.RRFAssetType;
import lu.itrust.business.TS.model.rrf.RRFMeasure;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measure.AssetMeasure;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.MeasureAssetValue;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;
import lu.itrust.business.TS.usermanagement.RoleType;
import net.minidev.json.JSONObject;

/**
 * ChartGenerator.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl. :
 * @version
 * @since Jan 30, 2014
 */
@Component
public class ChartGenerator {

	@Value("${app.settings.ale.chart.content.max.size}")
	private int aleChartMaxSize;

	@Value("${app.settings.ale.chart.single.content.max.size}")
	private int aleChartSingleMaxSize;

	@Value("${app.settings.ale.chart.content.size}")
	private int aleChartSize;

	@Value("#{'${app.settings.default.chart.colors}'.split(',')}")
	private List<String> defaultColors;

	@Autowired
	private DAOAnalysis daoAnalysis;

	@Autowired
	private DAOAnalysisStandard daoAnalysisStandard;

	@Autowired
	private DAOAssessment daoAssessment;

	@Autowired
	private DAOAssetType daoAssetType;

	@Autowired
	private DAODynamicParameter daoDynamicParameter;

	@Autowired
	private DAOImpactParameter daoImpactParameter;

	@Autowired
	private DAOLikelihoodParameter daoLikelihoodParameter;

	@Autowired
	private DAOMeasure daoMeasure;

	@Autowired
	private DAOPhase daoPhase;

	@Autowired
	private DAORiskAcceptanceParameter daoRiskAcceptanceParameter;

	@Autowired
	private DAOScenario daoScenario;

	@Autowired
	private DAOSimpleParameter daoSimpleParameter;

	@Autowired
	private DAOUserAnalysisRight daoUserAnalysisRight;

	@Autowired
	private DynamicRiskComputer dynamicRiskComputer;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceExternalNotification serviceExternalNotification;

	/**
	 * aleByAsset: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	public Object aleByAsset(int idAnalysis, Locale locale) throws Exception {
		List<Assessment> assessments = daoAssessment.getAllFromAnalysisAndSelected(idAnalysis);
		Map<Integer, ALE> mappedALEs = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales = new ArrayList<ALE>();
		for (Assessment assessment : assessments) {
			ALE ale = mappedALEs.get(assessment.getAsset().getId());
			if (ale == null) {
				mappedALEs.put(assessment.getAsset().getId(), ale = new ALE(assessment.getAsset().getName(), 0));
				ales.add(ale);
			}
			ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
		}
		Collections.sort(ales, new AssetComparatorByALE());
		try {
			if (ales.size() <= aleChartSingleMaxSize)
				return generateALEJSChart(locale, messageSource.getMessage("label.title.chart.ale_by_asset", null, "ALE by Asset", locale), ales);
			Distribution distribution = Distribution.Distribut(ales.size(), aleChartSize, aleChartMaxSize);
			int multiplicator = Math.floorDiv(ales.size(), distribution.getDivisor());
			List<Chart> charts = new ArrayList<>(multiplicator);
			for (int i = 0; i < multiplicator; i++) {
				List<ALE> aleSubList = ales.subList(i * distribution.getDivisor(), i == (multiplicator - 1) ? ales.size() : (i + 1) * distribution.getDivisor());
				if (aleSubList.get(0).getValue() == 0)
					break;
				charts.add(generateALEJSChart(locale, messageSource.getMessage("label.title.chart.part.ale_by_asset", new Integer[] { i + 1, multiplicator },
						String.format("ALE by Asset %d/%d", i + 1, multiplicator), locale), aleSubList));
			}
			return charts;
		} finally {
			assessments.clear();
			mappedALEs.clear();
			ales.clear();
		}
	}

	public Chart generateALEJSChart(Locale locale, String title, List<ALE> ales) {
		return generateALEJSChart(locale, title, new ALEChart(ales));
	}

	/**
	 * aleByAssetType: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param locale
	 * @return
	 */
	public Object aleByAssetType(int idAnalysis, Locale locale) throws Exception {
		List<Assessment> assessments = daoAssessment.getAllFromAnalysisAndSelected(idAnalysis);
		Map<Integer, ALE> mappedALEs = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales = new LinkedList<ALE>();
		try {
			for (Assessment assessment : assessments) {
				ALE ale = mappedALEs.get(assessment.getAsset().getAssetType().getId());
				if (ale == null) {
					mappedALEs.put(assessment.getAsset().getAssetType().getId(),
							ale = new ALE(messageSource.getMessage("label.asset_type." + assessment.getAsset().getAssetType().getName().toLowerCase(), null,
									assessment.getAsset().getAssetType().getName(), locale), 0));
					ales.add(ale);
				}
				ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
			}
			Collections.sort(ales, new AssetComparatorByALE());
			if (ales.size() <= aleChartSingleMaxSize)
				return generateALEJSChart(locale, messageSource.getMessage("label.title.chart.ale_by_asset_type", null, "ALE by Asset Type", locale), ales);
			Distribution distribution = Distribution.Distribut(ales.size(), aleChartSize, aleChartMaxSize);
			int multiplicator = Math.floorDiv(ales.size(), distribution.getDivisor());
			List<Chart> charts = new ArrayList<>(multiplicator);
			for (int i = 0; i < multiplicator; i++) {
				List<ALE> aleSubList = ales.subList(i * distribution.getDivisor(), i == (multiplicator - 1) ? ales.size() : (i + 1) * distribution.getDivisor());
				if (aleSubList.get(0).getValue() == 0)
					break;
				charts.add(generateALEJSChart(locale, messageSource.getMessage("label.title.chart.part.ale_by_asset_type", new Integer[] { i + 1, multiplicator },
						String.format("ALE by Asset Type %d/%d", i + 1, multiplicator), locale), aleSubList));
			}
			return charts;
		} finally {
			assessments.clear();
			mappedALEs.clear();
			ales.clear();
		}
	}

	/**
	 * aleByScenario: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	public Object aleByScenario(Integer idAnalysis, Locale locale) throws Exception {
		List<Assessment> assessments = daoAssessment.getAllFromAnalysisAndSelected(idAnalysis);
		Map<Integer, ALE> mappedALEs = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales = new LinkedList<ALE>();
		try {
			for (Assessment assessment : assessments) {
				ALE ale = mappedALEs.get(assessment.getScenario().getId());
				if (ale == null) {
					mappedALEs.put(assessment.getScenario().getId(), ale = new ALE(assessment.getScenario().getName(), 0));
					ales.add(ale);
				}
				ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
			}
			Collections.sort(ales, new AssetComparatorByALE());
			if (ales.size() <= aleChartSingleMaxSize)
				return generateALEJSChart(locale, messageSource.getMessage("label.title.chart.ale_by_scenario", null, "ALE by Scenario", locale), ales);
			Distribution distribution = Distribution.Distribut(ales.size(), aleChartSize, aleChartMaxSize);
			int multiplicator = Math.floorDiv(ales.size(), distribution.getDivisor());
			List<Chart> charts = new ArrayList<>(multiplicator);
			for (int i = 0; i < multiplicator; i++) {
				List<ALE> aleSubList = ales.subList(i * distribution.getDivisor(), i == (multiplicator - 1) ? ales.size() : (i + 1) * distribution.getDivisor());
				if (aleSubList.get(0).getValue() == 0)
					break;
				charts.add(generateALEJSChart(locale, messageSource.getMessage("label.title.chart.part.ale_by_scenario", new Integer[] { i + 1, multiplicator },
						String.format("ALE by Scenario %d/%d", i + 1, multiplicator), locale), aleSubList));
			}
			return charts;
		} finally {
			assessments.clear();
			mappedALEs.clear();
			ales.clear();
		}
	}

	/**
	 * aleByScenarioType: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	public Object aleByScenarioType(Integer idAnalysis, Locale locale) throws Exception {
		List<Assessment> assessments = daoAssessment.getAllFromAnalysisAndSelected(idAnalysis);
		Map<Integer, ALE> mappedALEs = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales = new LinkedList<ALE>();
		try {
			for (Assessment assessment : assessments) {
				ALE ale = mappedALEs.get(assessment.getScenario().getType().getValue());
				if (ale == null) {
					mappedALEs.put(assessment.getScenario().getType().getValue(),
							ale = new ALE(messageSource.getMessage("label.scenario.type." + assessment.getScenario().getType().getName().replace("-", "_").toLowerCase(), null,
									assessment.getScenario().getType().getName(), locale), 0));
					ales.add(ale);
				}
				ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
			}
			Collections.sort(ales, new AssetComparatorByALE());

			if (ales.size() <= aleChartSingleMaxSize)
				return generateALEJSChart(locale, messageSource.getMessage("label.title.chart.ale_by_scenario_type", null, "ALE by Scenario Type", locale), ales);
			Distribution distribution = Distribution.Distribut(ales.size(), aleChartSize, aleChartMaxSize);
			int multiplicator = Math.floorDiv(ales.size(), distribution.getDivisor());
			List<Chart> charts = new ArrayList<>(multiplicator);
			for (int i = 0; i < multiplicator; i++) {
				List<ALE> aleSubList = ales.subList(i * distribution.getDivisor(), i == (multiplicator - 1) ? ales.size() : (i + 1) * distribution.getDivisor());
				if (aleSubList.get(0).getValue() == 0)
					break;
				charts.add(generateALEJSChart(locale, messageSource.getMessage("label.title.chart.part.ale_by_scenario_type", new Integer[] { i + 1, multiplicator },
						String.format("ALE by Scenario Type %d/%d", i + 1, multiplicator), locale), aleSubList));
				;
			}
			return charts;
		} finally {
			assessments.clear();
			mappedALEs.clear();
			ales.clear();
		}
	}

	/**
	 * Generates the JSON data configuring a "Highcharts" chart which displays
	 * the ALE evolution of all asset types of an analysis.
	 * 
	 * @param idAnalysis
	 *            The ID of the analysis to generate the graph for.
	 */
	public String aleEvolutionOfAllAssetTypes(int idAnalysis, Locale locale) throws Exception {
		final Analysis analysis = daoAnalysis.get(idAnalysis);
		final List<Assessment> assessments = analysis.getAssessments();
		return aleEvolution(analysis, assessments, locale, a -> a.getAsset().getAssetType(), t -> t.getName(),
				messageSource.getMessage("label.title.chart.aleevolution", null, "ALE Evolution", locale));
	}

	/**
	 * Generates the JSON data configuring a "Highcharts" chart which displays
	 * the ALE evolution of all scenarios of a specific asset type of an
	 * analysis.
	 * 
	 * @param idAnalysis
	 *            The ID of the analysis to generate the graph for.
	 * @param assetType
	 *            The asset type to generate the graph for.
	 */
	public String aleEvolutionofAllScenarios(int idAnalysis, String assetType, Locale locale) throws Exception {
		final Analysis analysis = daoAnalysis.get(idAnalysis);
		final List<Assessment> assessments = analysis.getAssessments().stream().filter(a -> a.getAsset().getAssetType().getName().equals(assetType)).collect(Collectors.toList());
		return aleEvolution(analysis, assessments, locale, a -> a.getScenario(), s -> s.getName(),
				messageSource.getMessage("label.title.chart.aleevolution_of_asset_type", new Object[] { assetType }, "ALE Evolution of '{0}' assets", locale));
	}

	/**
	 * Generates the JSON data configuring a "Highcharts" chart which displays
	 * the ALE evolution of all scenarios of a specific asset type of an
	 * analysis.
	 * 
	 * @param idAnalysis
	 *            The ID of the analysis to generate the graph for.
	 * @param assetType
	 *            The asset type to generate the graph for.
	 */
	public String allAleEvolutionsofAllScenarios(int idAnalysis, Locale locale) throws Exception {
		final Analysis analysis = daoAnalysis.get(idAnalysis);
		final List<Assessment> assessments = analysis.getAssessments();
		final Map<AssetType, List<Assessment>> assessmentsByAssetType = new HashMap<>();

		// Split assessments by the type of their asset
		for (Assessment assessment : assessments) {
			final AssetType type = assessment.getAsset().getAssetType();
			List<Assessment> list = assessmentsByAssetType.get(type);
			if (list == null)
				assessmentsByAssetType.put(type, list = new ArrayList<>());
			list.add(assessment);
		}

		// Create individual graphs
		final List<String> graphs = new ArrayList<>();
		for (AssetType assetType : assessmentsByAssetType.keySet())
			graphs.add(aleEvolution(analysis, assessmentsByAssetType.get(assetType), locale, a -> a.getScenario(), s -> s.getName(), messageSource
					.getMessage("label.title.chart.aleevolution_of_asset_type", new Object[] { assetType.getName() }, "ALE Evolution of all {0}-type assets", locale)));
		return "[" + String.join(", ", graphs) + "]";
	}

	/**
	 * budget: <br>
	 * Description
	 * 
	 * @param summaryStages
	 * @param phases
	 * @param actionPlanType
	 * @param locale
	 * @return
	 */
	public Chart[] budget(List<SummaryStage> summaryStages, List<Phase> phases, String actionPlanType, Locale locale) {
		Map<String, List<String>> summaries = ActionPlanSummaryManager.buildTable(summaryStages, phases);
		Chart[] charts = {
				new Chart("chart_budget_cost_" + actionPlanType,
						messageSource.getMessage("label.title.chart.budget.cost." + actionPlanType.toLowerCase(), null, "Cost for " + actionPlanType, locale)),
				new Chart("chart_budget_workload_" + actionPlanType,
						messageSource.getMessage("label.title.chart.budget.workload." + actionPlanType.toLowerCase(), null, "Workload for " + actionPlanType, locale)) };

		if (summaries.isEmpty())
			return charts;

		String[] workloadNames = { ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD, ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD,
				ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE, ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE },
				costNames = { ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INVESTMENT, ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_IMPLEMENT_PHASE_COST,
						ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_RECURRENT_COST, ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST };

		Map<String, Dataset<String>> costDatasets = new LinkedHashMap<>(costNames.length), workloadDatasets = new LinkedHashMap<>(workloadNames.length);

		for (String name : costNames)
			costDatasets.put(name, new Dataset<String>(messageSource.getMessage(name, null, locale), getColor(costDatasets.size())));

		for (String name : workloadNames)
			workloadDatasets.put(name, new Dataset<String>(messageSource.getMessage(name, null, locale), getColor(workloadDatasets.size())));

		Map<String, Phase> usesPhases = ActionPlanSummaryManager.buildPhase(phases, ActionPlanSummaryManager.extractPhaseRow(summaryStages));

		for (Phase phase : usesPhases.values()) {
			for (Chart chart : charts)
				chart.getLabels().add("P" + phase.getNumber());
		}

		for (int i = 0, length = charts[0].getLabels().size(); i < length; i++) {
			for (String name : costNames)
				costDatasets.get(name).getData().add(summaries.get(name).get(i));
			for (String name : workloadNames)
				workloadDatasets.get(name).getData().add(summaries.get(name).get(i));
		}

		charts[0].getDatasets().addAll(costDatasets.values());

		charts[1].getDatasets().addAll(workloadDatasets.values());

		return charts;
	}

	/**
	 * compliance: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param standard
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	public Chart compliance(int idAnalysis, AnalysisStandard analysisStandard, Locale locale) {

		ValueFactory factory = new ValueFactory(daoDynamicParameter.findByAnalysisId(idAnalysis));

		Standard standard = analysisStandard.getStandard();

		List<Measure> measures = analysisStandard.getMeasures().stream()
				.filter(measure -> measure.getMeasureDescription().isComputable() && !measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE))
				.collect(Collectors.toList());

		Chart chart = new Chart(standard.getId(),
				messageSource.getMessage("label.title.chart.measure.compliance", new Object[] { standard.getLabel() }, standard.getLabel() + " measure compliance", locale));

		Map<String, Object[]> previouscompliances = ComputeComplianceBefore(measures, factory);

		chart.setLabels(analysisStandard.getMeasures().stream().map(measure -> ActionPlanComputation.extractMainChapter(measure.getMeasureDescription().getReference())).distinct()
				.collect(Collectors.toList()));

		if (previouscompliances.isEmpty())
			return chart;

		Dataset<String> dataset = new Dataset<String>(messageSource.getMessage("label.chart.series.current_level", null, "Current Level", locale), getColor(0));

		for (String key : chart.getLabels()) {
			Object[] compliance = previouscompliances.get(key);
			dataset.getData().add(compliance == null ? 0 : Math.floor(((Double) compliance[1]) / (Integer) compliance[0]));
		}

		if (!dataset.getData().isEmpty())
			chart.getDatasets().add(dataset);

		List<Integer> idMeasureInActionPlans = daoMeasure.getIdMeasuresImplementedByActionPlanTypeFromIdAnalysisAndStandard(idAnalysis, standard.getLabel(), ActionPlanMode.APPN);

		Map<Integer, Boolean> actionPlanMeasures = new LinkedHashMap<Integer, Boolean>(idMeasureInActionPlans.size());

		for (Integer integer : idMeasureInActionPlans)
			actionPlanMeasures.put(integer, true);

		idMeasureInActionPlans.clear();

		List<Phase> phases = daoPhase.getAllFromAnalysisActionPlan(idAnalysis);

		if (!actionPlanMeasures.isEmpty()) {
			for (Phase phase : phases) {
				if (phase.getNumber() == Constant.PHASE_NOT_USABLE)
					continue;
				Map<String, Object[]> compliances = ComputeCompliance(measures, phase, actionPlanMeasures, previouscompliances, factory);
				if (compliances.size() == 0)
					continue;
				chart.getDatasets().add(dataset = new Dataset<String>(messageSource.getMessage("label.chart.phase", new Object[] { phase.getNumber() }, "Phase", locale),
						getColor(chart.getDatasets().size())));
				for (String key : compliances.keySet()) {
					Object[] compliance = compliances.get(key);
					dataset.getData().add(compliance == null ? 0 : Math.floor(((Double) compliance[1]) / (Integer) compliance[0]));
				}
				previouscompliances = compliances;
			}
		}
		return chart;
	}

	/**
	 * compliance: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param standard
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	public Chart compliance(Locale locale, ComplianceChartData... measureChartDatas) {
		ComplianceChartData reference = measureChartDatas[0];
		Map<String, Object[]> compliances = ComputeComplianceBefore(reference.getMeasures(), reference.getFactory());
		Chart chart = new Chart(reference.getStandard(),
				messageSource.getMessage("label.title.chart.measure.compliance", new Object[] { reference.getStandard() }, reference + " measure compliance", locale));
		if (measureChartDatas.length > 0) {
			Dataset<String> dataset = new Dataset<String>(reference.getAnalysisKey(), getColor(chart.getDatasets().size()));
			for (String key : compliances.keySet()) {
				Object[] compliance = compliances.get(key);
				dataset.getData().add((int) Math.floor(((Double) compliance[1]) / (Integer) compliance[0]));
				chart.getLabels().add(key);
			}
			chart.getDatasets().add(dataset);
			if (measureChartDatas.length > 1) {
				Collection<String> keys = compliances.keySet();
				for (int i = 1; i < measureChartDatas.length; i++) {
					compliances = ComputeComplianceBefore(measureChartDatas[i].getMeasures(), measureChartDatas[i].getFactory());
					dataset = new Dataset<String>(measureChartDatas[i].getAnalysisKey(), getColor(chart.getDatasets().size()));
					for (String key : keys) {
						Object[] compliance = compliances.get(key);
						if (compliance == null)
							dataset.getData().add(0);
						else
							dataset.getData().add((int) Math.floor(((Double) compliance[1]) / (Integer) compliance[0]));
					}
					chart.getDatasets().add(dataset);
				}
			}
		}
		return chart;
	}

	/**
	 * Generates the JSON data configuring a "Highcharts" chart which displays
	 * the evolution of the dynamic parameters.
	 * 
	 * @param idAnalysis
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	public String dynamicParameterEvolution(int idAnalysis, Locale locale) throws Exception {
		// Find the user names of all sources involved
		List<String> sourceUserNames = daoUserAnalysisRight.getAllFromAnalysis(idAnalysis).stream().map(userRight -> userRight.getUser())
				.filter(user -> user.hasRole(RoleType.ROLE_IDS)).map(user -> user.getLogin()).collect(Collectors.toList());

		final Analysis analysis = daoAnalysis.get(idAnalysis);
		final double minimumProbability = Math.max(0.0, analysis.getParameter("p0")); // getParameter
																						// returns
																						// -1
																						// in
																						// case
																						// of
																						// a
																						// failure

		// Determine time-related stuff
		final long timeUpperBound = Instant.now().getEpochSecond();
		final long timeLowerBound = timeUpperBound - Constant.CHART_DYNAMIC_PARAMETER_EVOLUTION_HISTORY_IN_SECONDS;
		long nextTimeIntervalSize = 60; // in seconds

		String jsonXAxisValues = "";
		List<Long> xAxisValues = new ArrayList<>();

		// For each dynamic parameter, construct a series of values
		Map<String, Map<Long, Double>> data = new HashMap<>();
		for (long timeEnd = timeUpperBound - nextTimeIntervalSize; timeEnd - nextTimeIntervalSize >= timeLowerBound; timeEnd -= nextTimeIntervalSize) {
			// Add x-axis values to a list in reverse order (we use
			// Collections.reverse() later on)
			xAxisValues.add(timeEnd);
			if (!jsonXAxisValues.isEmpty())
				jsonXAxisValues = "," + jsonXAxisValues;
			jsonXAxisValues = "\"" + deltaTimeToString(timeUpperBound - timeEnd) + "\"" + jsonXAxisValues;

			// Fetch data
			for (String sourceUserName : sourceUserNames) {
				Map<String, Double> likelihoods = serviceExternalNotification.computeProbabilitiesInInterval(timeEnd - nextTimeIntervalSize, timeEnd, sourceUserName,
						minimumProbability);
				for (String parameterName : likelihoods.keySet()) {
					// Store data
					data.putIfAbsent(parameterName, new HashMap<Long, Double>());
					data.get(parameterName).put(timeEnd, likelihoods.get(parameterName));
				}
			}

			// Modify interval size
			if (nextTimeIntervalSize < Constant.CHART_DYNAMIC_PARAMETER_MAX_SIZE_OF_LOGARITHMIC_SCALE)
				nextTimeIntervalSize = (int) (nextTimeIntervalSize * Constant.CHART_DYNAMIC_PARAMETER_LOGARITHMIC_FACTOR);
		}
		Collections.reverse(xAxisValues);

		// Collect data
		String jsonSeries = "\"series\":[ "; // need space at the end if 'data'
												// is empty map
		for (String parameterName : data.keySet()) {
			String jsonSingleSeries = "[ "; // need space at the end if
											// 'xAxisValues' is empty list
			for (long timeEnd : xAxisValues) {
				jsonSingleSeries += data.get(parameterName).getOrDefault(timeEnd, 0.0) + ",";
			}
			jsonSingleSeries = jsonSingleSeries.substring(0, jsonSingleSeries.length() - 1) + "]";
			jsonSeries += "{\"name\":\"" + JSONObject.escape(parameterName) + "\", \"data\":" + jsonSingleSeries + ",\"valueDecimals\": 3, \"type\": \"line\",\"yAxis\": 0},";
		}
		jsonSeries = jsonSeries.substring(0, jsonSeries.length() - 1) + "]";

		// Build JSON data
		// final String unitPerYear =
		// messageSource.getMessage("label.assessment.likelihood.unit", null,
		// "/y", locale); // use with JSONObject.escape(unitPerYear)
		final String jsonChart = "\"chart\": {\"type\": \"column\", \"zoomType\": \"xy\", \"marginTop\": 50}, \"scrollbar\": {\"enabled\": false}";
		final String jsonTitle = "\"title\": {\"text\":\""
				+ JSONObject.escape(messageSource.getMessage("label.title.chart.dynamic", null, "Evolution of dynamic parameters", locale)) + "\"}";
		final String jsonPane = "\"pane\": {\"size\": \"100%\"}";
		final String jsonLegend = "\"legend\": {\"align\": \"right\", \"verticalAlign\": \"top\", \"y\": 70, \"layout\": \"vertical\"}";
		final String jsonPlotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0}}";
		final String jsonYAxis = "\"yAxis\": [{\"min\": 0, \"labels\":{\"format\": \"{value}\",\"useHTML\": true}, \"title\": {\"text\":\""
				+ JSONObject.escape(messageSource.getMessage("label.parameter.value", null, "Value", locale)) + "\"}}]";
		final String jsonXAxis = "\"xAxis\":{\"categories\":[" + jsonXAxisValues + "], \"labels\":{\"rotation\":-90}}";

		return ("{" + jsonChart + "," + jsonTitle + "," + jsonLegend + "," + jsonPane + "," + jsonPlotOptions + "," + jsonXAxis + "," + jsonYAxis + "," + jsonSeries + "}")
				.replaceAll("\r|\n", " ");
	}

	/**
	 * evolutionProfitabilityCompliance: <br>
	 * Description
	 * 
	 * @param summaryStages
	 * @param phases
	 * @param actionPlanType
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	public Chart[] evolutionProfitabilityCompliance(Integer idAnalysis, List<SummaryStage> summaryStages, List<Phase> phases, String actionPlanType, Locale locale)
			throws Exception {
		Chart[] charts = {
				new Chart("chart_evolution_profitability_" + actionPlanType,
						messageSource.getMessage("label.title.chart.evolution_profitability." + actionPlanType.toLowerCase(), null,
								"Evolution of profitability for " + actionPlanType, locale)),
				new Chart("chart_compliance_" + actionPlanType,
						messageSource.getMessage("label.title.chart.compliance." + actionPlanType.toLowerCase(), null, "ISO compliance for " + actionPlanType, locale)) };
		Map<String, List<String>> summaries = ActionPlanSummaryManager.buildTable(summaryStages, phases);
		if (summaries.isEmpty())
			return charts;
		Map<String, Phase> usesPhases = ActionPlanSummaryManager.buildPhase(phases, ActionPlanSummaryManager.extractPhaseRow(summaryStages));
		for (Phase phase : usesPhases.values()) {
			for (Chart chart : charts)
				chart.getLabels().add("P" + phase.getNumber());
		}

		Map<String, List<String>> standardcompliances = new LinkedHashMap<String, List<String>>();

		Map<String, Dataset<String>> complianceDatasets = new LinkedHashMap<>();

		List<AnalysisStandard> analysisStandards = daoAnalysisStandard.getAllFromAnalysis(idAnalysis);

		for (AnalysisStandard analysisStandard : analysisStandards) {
			if (summaries.get(ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COMPLIANCE + analysisStandard.getStandard().getLabel()) != null)
				if (standardcompliances.get(analysisStandard.getStandard().getLabel()) == null)
					standardcompliances.put(analysisStandard.getStandard().getLabel(),
							summaries.get(ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COMPLIANCE + analysisStandard.getStandard().getLabel()));
				else
					standardcompliances.put(analysisStandard.getStandard().getLabel(),
							summaries.get(ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COMPLIANCE + analysisStandard.getStandard().getLabel()));
		}
		String[] dataName = { ActionPlanSummaryManager.LABEL_PROFITABILITY_ALE_UNTIL_END, ActionPlanSummaryManager.LABEL_PROFITABILITY_RISK_REDUCTION,
				ActionPlanSummaryManager.LABEL_PROFITABILITY_AVERAGE_YEARLY_COST_OF_PHASE, ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI/*
																																			 * ,
																																			 * ActionPlanSummaryManager
																																			 * .
																																			 * LABEL_PROFITABILITY_ROSI_RELATIF
																																			 */ };
		Map<String, Dataset<String>> profiltabilityDatasets = new LinkedHashMap<>(dataName.length);

		for (String name : dataName)
			profiltabilityDatasets.put(name, new Dataset<String>(messageSource.getMessage(name, null, locale), getColor(profiltabilityDatasets.size())));
		for (String name : standardcompliances.keySet())
			complianceDatasets.put(name, new Dataset<String>(name, getColor(complianceDatasets.size())));

		for (int i = 0; i < usesPhases.size(); i++) {
			for (String name : dataName)
				profiltabilityDatasets.get(name).getData().add(summaries.get(name).get(i));
			for (String key : standardcompliances.keySet())
				complianceDatasets.get(key).getData().add(standardcompliances.get(key).get(i));

		}

		charts[0].getDatasets().addAll(profiltabilityDatasets.values());
		charts[1].getDatasets().addAll(complianceDatasets.values());
		return charts;
	}

	public Chart generateALEJSChart(Locale locale, String title, ALEChart... aleCharts) {
		Chart chart = new Chart(title);
		if (aleCharts.length == 1)
			buildSingleALESerie(chart, aleCharts[0]);
		else if (aleCharts.length > 0)
			buildMulitALESeries(chart, aleCharts);
		return chart;
	}

	public Chart generateRiskHeatMap(Integer idAnalysis) {
		return generateRiskHeatMap(daoAnalysis.get(idAnalysis), null);
	}

	public Object riskByAsset(Integer idAnalysis, Locale locale) {
		Map<String, List<Assessment>> assessmentByAssets = daoAssessment.getAllFromAnalysisAndSelected(idAnalysis).stream()
				.collect(Collectors.groupingBy(assessment -> assessment.getAsset().getName()));
		return assessmentByAssets.isEmpty() ? new Chart() : generateAssessmentRisk(idAnalysis, assessmentByAssets);
	}

	public Object riskByAssetType(Integer idAnalysis, Locale locale) {
		Map<String, List<Assessment>> assessmentByAssetTypes = daoAssessment.getAllFromAnalysisAndSelected(idAnalysis).stream()
				.collect(Collectors.groupingBy(assessment -> messageSource.getMessage("label.asset_type." + assessment.getAsset().getAssetType().getName().toLowerCase(), null,
						assessment.getAsset().getAssetType().getName(), locale)));
		return assessmentByAssetTypes.isEmpty() ? new Chart() : generateAssessmentRisk(idAnalysis, assessmentByAssetTypes);
	}

	public Object riskByScenario(Integer idAnalysis, Locale locale) {
		Map<String, List<Assessment>> assessmentByScenarios = daoAssessment.getAllFromAnalysisAndSelected(idAnalysis).stream()
				.collect(Collectors.groupingBy(assessment -> assessment.getScenario().getName()));
		return assessmentByScenarios.isEmpty() ? new Chart() : generateAssessmentRisk(idAnalysis, assessmentByScenarios);
	}

	public Object riskByScenarioType(Integer idAnalysis, Locale locale) {
		Map<String, List<Assessment>> assessmentByScenarioTypes = daoAssessment.getAllFromAnalysisAndSelected(idAnalysis).stream().collect(
				Collectors.groupingBy(assessment -> messageSource.getMessage("label.scenario.type." + assessment.getScenario().getType().getName().replace("-", "_").toLowerCase(),
						null, assessment.getAsset().getAssetType().getName(), locale)));
		return assessmentByScenarioTypes.isEmpty() ? new Chart() : generateAssessmentRisk(idAnalysis, assessmentByScenarioTypes);
	}

	public String rrfByMeasure(int idMeasure, Integer idAnalysis, List<Scenario> scenarios, Locale locale) throws Exception {
		Locale customLocale = new Locale(daoAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
		NormalMeasure normalMeasure = (NormalMeasure) daoMeasure.getFromAnalysisById(idMeasure, idAnalysis);
		if (normalMeasure == null)
			return null;
		return rrfByMeasure(normalMeasure, idAnalysis, scenarios, customLocale != null ? customLocale : locale);
	}

	public String rrfByMeasure(Measure measure, Integer idAnalysis, List<Scenario> scenarios, Locale locale) throws Exception {

		try {

			String title = "\"title\": {\"text\":\"" + messageSource.getMessage("label.title.chart.rff.measure", new String[] { measure.getMeasureDescription().getReference() },
					"RRF by measure (" + measure.getMeasureDescription().getReference() + ")", locale) + "\"}";

			String pane = "\"pane\": {\"size\": \"100%\"}";

			String legend = "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\"}";

			String plotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0}, \"series\":{\"minPointLength\" : 1.6}}";

			String series = "";

			double biggestrrf = 0.;

			if (measure instanceof NormalMeasure) {

				List<AssetType> assetTypes = daoAssetType.getAll();

				Map<String, RRFAssetType> rrfs = computeRRFByNormalMeasure((NormalMeasure) measure, assetTypes, scenarios, idAnalysis, locale);

				for (String key : rrfs.keySet())
					for (RRFMeasure mes : rrfs.get(key).getRrfMeasures())
						if (mes.getValue() > biggestrrf)
							biggestrrf = mes.getValue();

				series = generateNormalMeasureSeries(rrfs);
			} else if (measure instanceof AssetMeasure) {
				Map<String, RRFAsset> rrfs = computeRRFByAssetMeasure((AssetMeasure) measure, scenarios, idAnalysis);
				for (String key : rrfs.keySet())
					for (RRFMeasure mes : rrfs.get(key).getRrfMeasures())
						if (mes.getValue() > biggestrrf)
							biggestrrf = mes.getValue();
				series = generateAssetMeasureSeries(rrfs);
			}

			String chart = "\"chart\":{ \"type\":\"" + (scenarios.size() == 1 ? "column" : "spline")
					+ "\",  \"zoomType\": \"xy\", \"marginTop\": 50, \"renderTo\": \"chart-container\", \"height\":340},  \"scrollbar\": {\"enabled\": " + (scenarios.size() > 9)
					+ "}";

			String scenarioData = "[";

			for (Scenario scenario : scenarios)
				scenarioData += "\"" + scenario.getName() + "\",";

			if (scenarioData.endsWith(","))
				scenarioData = scenarioData.substring(0, scenarioData.length() - 1);
			scenarioData += "]";

			String xAxis = null;

			biggestrrf += 0.25;

			String yAxis = "\"yAxis\": {\"min\": 0 , \"max\": " + String.valueOf(biggestrrf) + ", \"title\": {\"text\": \"RRF\"}}";

			if (scenarios.size() >= 10)
				xAxis = "\"xAxis\":{\"categories\":" + scenarioData + ", \"min\":\"0\", \"max\":\"9\" ,  \"title\": {\"text\": \""
						+ messageSource.getMessage("label.measures", null, "Measures", locale) + "\"}}";
			else
				xAxis = "\"xAxis\":{\"categories\":" + scenarioData + ", \"min\":\"0\", \"max\":\"" + (scenarios.size() - 1) + "\", \"title\": {\"text\": \""
						+ messageSource.getMessage("label.scenarios", null, "Scenario", locale) + "\"}}";

			return ("{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + xAxis + "," + yAxis + "," + series + "}").replaceAll("\r|\n", " ");
		}

		catch (TrickException e) {
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
		return null;
	}

	public String rrfByScenario(int idScenario, int idAnalysis, List<Measure> measures, Locale locale) throws Exception {
		Scenario scenario = daoScenario.getFromAnalysisById(idAnalysis, idScenario);
		if (scenario == null)
			return null;
		return rrfByScenario(scenario, idAnalysis, measures, locale);
	}

	public String rrfByScenario(Scenario scenario, int idAnalysis, List<Measure> measures, Locale locale) throws Exception {
		try {

			String title = "\"title\": {\"text\":\""
					+ messageSource.getMessage("label.title.chart.rff.scenario", new String[] { scenario.getName() }, "RRF by scenario (" + scenario.getName() + ")", locale)
					+ "\"}";

			String pane = "\"pane\": {\"size\": \"100%\"}";

			String legend = "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\"}";

			String plotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0}, \"series\":{\"minPointLength\" : 1.6}}";

			String series = "\"series\":[";

			Map<String, Object> rrfs = computeRRFByScenario(scenario, measures, idAnalysis, locale);

			double biggestrrf = 0.;

			for (String key : rrfs.keySet()) {
				String rrf = "[";
				if (rrfs.get(key) instanceof RRFAssetType) {
					RRFAssetType rrfAssetType = (RRFAssetType) rrfs.get(key);
					for (RRFMeasure rrfMeasure : rrfAssetType.getRrfMeasures()) {
						rrf += rrfMeasure.getValue() + ",";
						if (biggestrrf < rrfMeasure.getValue())
							biggestrrf = rrfMeasure.getValue();
					}
					if (rrf.endsWith(","))
						rrf = rrf.substring(0, rrf.length() - 1);
					rrf += "]";
					series += "{\"name\":\"" + key + "\", \"data\":" + rrf + ",\"valueDecimals\": 0, \"visible\": true},";
				} else if (rrfs.get(key) instanceof RRFAsset) {
					RRFAsset rrfAsset = (RRFAsset) rrfs.get(key);
					for (RRFMeasure rrfMeasure : rrfAsset.getRrfMeasures()) {
						rrf += rrfMeasure.getValue() + ",";
						if (biggestrrf < rrfMeasure.getValue())
							biggestrrf = rrfMeasure.getValue();
					}
					if (rrf.endsWith(","))
						rrf = rrf.substring(0, rrf.length() - 1);
					rrf += "]";
					series += "{\"name\":\"" + key + "\", \"data\":" + rrf + ",\"valueDecimals\": 2, \"visible\": true},";
				}
			}

			if (series.endsWith(","))
				series = series.substring(0, series.length() - 1);
			series += "]";

			String chart = "\"chart\":{ \"type\":\"" + (measures.size() == 1 ? "column" : "spline")
					+ "\",  \"zoomType\": \"xy\", \"marginTop\": 50, \"renderTo\": \"chart_rrf\", \"height\":340},  \"scrollbar\": {\"enabled\": " + (measures.size() > 9) + "}";

			String measuresData = "[";

			for (Measure measure : measures)
				measuresData += "\"" + measure.getMeasureDescription().getReference() + "\",";

			if (measuresData.endsWith(","))
				measuresData = measuresData.substring(0, measuresData.length() - 1);
			measuresData += "]";

			String xAxis = null;

			biggestrrf += 0.25;

			String yAxis = "\"yAxis\": {\"min\": 0 , \"max\": " + String.valueOf(biggestrrf) + ", \"title\": {\"text\": \"RRF\"}}";

			if (measures.size() >= 10)
				xAxis = "\"xAxis\":{\"categories\":" + measuresData + ", \"min\":\"0\", \"max\":\"9\" ,  \"title\": {\"text\": \""
						+ messageSource.getMessage("label.measures", null, "Measures", locale) + "\"}}";
			else
				xAxis = "\"xAxis\":{\"categories\":" + measuresData + ", \"min\":\"0\", \"max\":\"" + (measures.size() - 1) + "\", \"title\": {\"text\": \""
						+ messageSource.getMessage("label.measure", null, "Measure", locale) + "\"}}";

			return ("{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + xAxis + "," + yAxis + "," + series + "}").replaceAll("\r|\n", " ");
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		}
		return null;
	}

	/**
	 * Generates the JSON data configuring a "Highcharts" chart which displays
	 * the ALE evolution of all scenarios of a specific asset type of an
	 * analysis.
	 * 
	 * @param idAnalysis
	 *            The ID of the analysis to generate the graph for.
	 * @param assetType
	 *            The asset type to generate the graph for.
	 */
	private <TAggregator> String aleEvolution(Analysis analysis, List<Assessment> assessments, Locale locale, Function<Assessment, TAggregator> aggregator,
			Function<TAggregator, String> axisLabelProvider, String chartTitle) throws Exception {
		final List<AnalysisStandard> standards = analysis.getAnalysisStandards();
		final List<IParameter> allParameters = analysis.findByGroup(Constant.PARAMETER_CATEGORY_SIMPLE, Constant.PARAMETER_CATEGORY_SIMPLE);
		final long now = Instant.now().getEpochSecond();

		// Find the user names of all sources involved
		final List<String> sourceUserNames = daoUserAnalysisRight.getAllFromAnalysis(analysis.getId()).stream().map(userRight -> userRight.getUser())
				.filter(user -> user.hasRole(RoleType.ROLE_IDS)).map(user -> user.getLogin()).collect(Collectors.toList());

		// Fetch ALE evolution data grouped by scenario and time
		final List<Long> xAxisValues = new ArrayList<>(); // populated within
															// dynamicRiskComputer.generateAleEvolutionData()
		final Map<Long, Map<Assessment, Set<String>>> involvedVariables = new HashMap<>(); // dito
		final Map<Long, Map<String, Double>> expressionParameters = new HashMap<>(); // dito
		final Map<TAggregator, Map<Long, Double>> data = dynamicRiskComputer.generateAleEvolutionData(assessments, standards, sourceUserNames, allParameters, aggregator,
				xAxisValues, involvedVariables, expressionParameters);

		// Output data
		final List<String> jsonSeriesList = new ArrayList<>();
		for (TAggregator key : data.keySet()) {
			final List<String> jsonDataList = new ArrayList<>();
			final List<List<String>> jsonMetaDataList = new ArrayList<>();

			// Collect data/metadata
			Long lastTimeEnd = null;
			for (long timeEnd : xAxisValues) {
				// Store value
				final double currentAle = data.get(key).get(timeEnd);
				jsonDataList.add(Double.toString(Math.round(currentAle / 10.) / 100.));

				if (lastTimeEnd != null) {
					// Find and store explanations of behaviour
					final Map<String, Double> currentExpressionParameters = expressionParameters.get(timeEnd);
					final Map<String, Double> lastExpressionParameters = expressionParameters.get(lastTimeEnd);
					final double lastAle = data.get(key).get(lastTimeEnd);
					jsonMetaDataList.add(generateNotableEventsJson(aggregator, key, lastTimeEnd, lastAle, currentAle, involvedVariables.get(lastTimeEnd), lastExpressionParameters,
							currentExpressionParameters));
				}

				// Update references
				lastTimeEnd = timeEnd;
			}

			// Add empty meta data array for last time point (it has none, since
			// there are no future time points to compare with)
			jsonMetaDataList.add(new ArrayList<>());

			// Build JSON object
			final String jsonData = "[" + String.join(",", jsonDataList) + "]";
			final String jsonMetaData = "[" + String.join(",", jsonMetaDataList.stream().map(x -> "[" + String.join(",", x) + "]").collect(Collectors.toList())) + "]";
			jsonSeriesList.add("{\"name\":\"" + JSONObject.escape(axisLabelProvider.apply(key)) + "\", \"data\":" + jsonData + ", \"metadata\":" + jsonMetaData
					+ ", \"valueDecimals\": 2, \"type\": \"line\",\"yAxis\": 0}");
		}
		final String jsonSeries = "\"series\":[" + String.join(",", jsonSeriesList) + "]";

		// Build JSON data
		final String jsonXAxisValues = String.join(",", xAxisValues.stream().map(x -> "\"" + deltaTimeToString(now - x) + "\"").collect(Collectors.toList()));
		final String unit = messageSource.getMessage("label.metric.keuro_by_year", null, "k\u20AC/y", locale);
		final String jsonChart = "\"chart\": {\"type\": \"column\", \"zoomType\": \"xy\", \"marginTop\": 50}, \"scrollbar\": {\"enabled\": false}";
		final String jsonTitle = "\"title\": {\"text\":\"" + JSONObject.escape(chartTitle) + "\"}";
		final String jsonPane = "\"pane\": {\"size\": \"100%\"}";
		final String jsonLegend = "\"legend\": {\"align\": \"right\", \"verticalAlign\": \"top\", \"y\": 70, \"layout\": \"vertical\"}";
		final String jsonPlotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0}}";
		final String jsonYAxis = "\"yAxis\": [{\"min\": 0, \"labels\":{\"format\": \"{value} " + JSONObject.escape(unit) + "\",\"useHTML\": true}, \"title\": {\"text\":\""
				+ JSONObject.escape(messageSource.getMessage("report.assessment.ale", null, "ALE (k\u20AC/y)", locale)) + "\"}}]";
		final String jsonXAxis = "\"xAxis\":{\"categories\":[" + jsonXAxisValues + "], \"labels\":{\"rotation\":-90}}";

		return ("{" + jsonChart + "," + jsonTitle + "," + jsonLegend + "," + jsonPane + "," + jsonPlotOptions + "," + jsonXAxis + "," + jsonYAxis + "," + jsonSeries + "}")
				.replaceAll("\r|\n", " ");
	}

	private void buildMulitALESeries(Chart chart, ALEChart... aleCharts) {

		Map<String, Map<String, ALE>> aleChartMapper = new LinkedHashMap<>();

		Map<String, ALE> references = new LinkedHashMap<>(aleCharts[0].getAles().size());

		aleCharts[0].getAles().forEach(ale -> references.put(ale.getAssetName(), ale));

		Map<String, Dataset<String>> datasets = new LinkedHashMap<>(references.size());

		Dataset<String> dataset = new Dataset<String>(aleCharts[0].getName(), getColor(chart.getDatasets().size()));

		chart.getDatasets().add(dataset);
		datasets.put(dataset.getLabel(), dataset);
		aleChartMapper.put(dataset.getLabel(), references);

		for (String category : references.keySet())
			chart.getLabels().add(category);

		for (int i = 1; i < aleCharts.length; i++) {
			Map<String, ALE> aleMapper = aleCharts[i].getAles().stream().filter(ale -> references.containsKey(ale.getAssetName()))
					.collect(Collectors.toMap(ALE::getAssetName, Function.identity()));
			if (!aleMapper.isEmpty()) {
				dataset = new Dataset<String>(aleCharts[i].getName(), getColor(chart.getDatasets().size()));
				chart.getDatasets().add(dataset);
				aleChartMapper.put(dataset.getLabel(), aleMapper);
				datasets.put(dataset.getLabel(), dataset);
			}
		}

		for (Entry<String, Map<String, ALE>> entry : aleChartMapper.entrySet()) {
			for (String category : references.keySet()) {
				ALE ale = entry.getValue().get(category);
				datasets.get(entry.getKey()).getData().add(ale == null ? 0d : ale.getValue());
			}
		}

	}

	private String getColor(int i, String defaultValue) {
		return defaultColors == null ? defaultValue : i < 0 ? defaultColors.get(0) : i >= defaultColors.size() ? defaultColors.get(i % defaultColors.size()) : defaultColors.get(i);
	}

	private String getColor(int i) {
		return getColor(i, null);
	}

	private void buildSingleALESerie(Chart chart, ALEChart data) {
		Dataset<String> dataset = new Dataset<String>(data.getName(), getColor(0));
		for (ALE ale : data.getAles()) {
			chart.getLabels().add(ale.getAssetName());
			dataset.getData().add(ale.getValue());
		}
		chart.getDatasets().add(dataset);
	}

	private void computeRRFAssetMeasure(Scenario scenario, IParameter parameter, NumberFormat numberFormat, Asset asset, RRFAssetType rrfAssetType, RRFMeasure rrfMeasure,
			AssetMeasure measure) throws TrickException, ParseException {
		rrfMeasure.setValue(numberFormat.parse(numberFormat.format(RRF.calculateAssetMeasureRRF(scenario, asset, parameter, measure))).doubleValue());
		rrfAssetType.getRrfMeasures().add(rrfMeasure);
	}

	/**
	 * computeRRFByAssetMeasure: <br>
	 * Description
	 * 
	 * @param measure
	 * @param scenarios
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 */
	private Map<String, RRFAsset> computeRRFByAssetMeasure(AssetMeasure measure, List<Scenario> scenarios, int idAnalysis) throws Exception {
		IParameter parameter = daoSimpleParameter.findByAnalysisIdAndTypeAndDescription(idAnalysis, Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, Constant.PARAMETER_MAX_RRF);
		Map<String, RRFAsset> rrfs = new LinkedHashMap<String, RRFAsset>(measure.getMeasureAssetValues().size());
		if (measure.getMeasureAssetValues().size() == 0)
			throw new TrickException("error.rrf.measure.no_assets", "The measure " + measure.getMeasureDescription().getReference() + " does not have any assets attributed!",
					measure.getMeasureDescription().getReference());
		for (MeasureAssetValue assetValue : measure.getMeasureAssetValues()) {
			RRFAsset rrfAsset = new RRFAsset(assetValue.getAsset().getName());
			for (Scenario scenario : scenarios) {
				RRFMeasure rrfMeasure = new RRFMeasure(measure.getId(), measure.getMeasureDescription().getReference());

				double val = RRF.calculateAssetMeasureRRF(scenario, assetValue.getAsset(), parameter, measure);

				NumberFormat nf = new DecimalFormat();

				nf.setMaximumFractionDigits(2);

				val = nf.parse(nf.format(val)).doubleValue();

				rrfMeasure.setValue(val);

				rrfAsset.getRrfMeasures().add(rrfMeasure);
			}
			rrfs.put(rrfAsset.getLabel(), rrfAsset);
		}
		return rrfs;
	}

	/**
	 * computeRRFByNormalMeasure: <br>
	 * Description
	 * 
	 * @param measure
	 * @param assetTypes
	 * @param scenarios
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 */
	private Map<String, RRFAssetType> computeRRFByNormalMeasure(NormalMeasure measure, List<AssetType> assetTypes, List<Scenario> scenarios, int idAnalysis, Locale locale)
			throws Exception {
		IParameter parameter = daoSimpleParameter.findByAnalysisIdAndTypeAndDescription(idAnalysis, Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, Constant.PARAMETER_MAX_RRF);
		Map<String, RRFAssetType> rrfs = new LinkedHashMap<String, RRFAssetType>(assetTypes.size());
		for (AssetType assetType : assetTypes) {
			RRFAssetType rrfAssetType = new RRFAssetType(messageSource.getMessage("label.asset_type." + assetType.getName().toLowerCase(), null, assetType.getName(), locale));
			for (Scenario scenario : scenarios) {
				RRFMeasure rrfMeasure = new RRFMeasure(measure.getId(), measure.getMeasureDescription().getReference());

				double val = RRF.calculateNormalMeasureRRF(scenario, assetType, parameter, measure);

				NumberFormat nf = new DecimalFormat();

				nf.setMaximumFractionDigits(2);

				val = nf.parse(nf.format(val)).doubleValue();

				rrfMeasure.setValue(val);

				rrfAssetType.getRrfMeasures().add(rrfMeasure);
			}
			rrfs.put(rrfAssetType.getLabel(), rrfAssetType);
		}
		return rrfs;
	}

	private Map<String, Object> computeRRFByScenario(Scenario scenario, List<Measure> measures, int idAnalysis, Locale locale) throws Exception {
		IParameter parameter = daoSimpleParameter.findByAnalysisIdAndTypeAndDescription(idAnalysis, Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, Constant.PARAMETER_MAX_RRF);
		Map<String, Object> rrfs = new LinkedHashMap<String, Object>();
		List<AssetType> assetTypes = scenario.getAssetTypes();
		if (assetTypes.isEmpty())
			throw new TrickException("error.rrf.scneario.no_assettypevalues", "The scenario " + scenario.getName() + " does not have any asset types attributed!",
					scenario.getName());
		NumberFormat numberFormat = new DecimalFormat();
		numberFormat.setMaximumFractionDigits(2);
		for (Measure measure : measures) {
			if (measure instanceof NormalMeasure) {
				if (scenario.isAssetLinked()) {
					for (Asset asset : scenario.getLinkedAssets())
						computeRRFNormalMeasure(scenario, parameter, numberFormat, asset.getAssetType(), findRRFAssetType(asset.getName(), rrfs),
								new RRFMeasure(measure.getId(), measure.getMeasureDescription().getReference()), (NormalMeasure) measure);
				} else {
					for (AssetType assetType : scenario.getAssetTypes())
						computeRRFNormalMeasure(scenario, parameter, numberFormat, assetType,
								findRRFAssetType(messageSource.getMessage("label.asset_type." + assetType.getName().toLowerCase(), null, assetType.getName(), locale), rrfs),
								new RRFMeasure(measure.getId(), measure.getMeasureDescription().getReference()), (NormalMeasure) measure);
				}

			} else if (measure instanceof AssetMeasure) {
				if (scenario.isAssetLinked()) {
					for (Asset asset : scenario.getLinkedAssets())
						computeRRFAssetMeasure(scenario, parameter, numberFormat, asset, findRRFAssetType(asset.getName(), rrfs),
								new RRFMeasure(measure.getId(), measure.getMeasureDescription().getReference()), (AssetMeasure) measure);
				} else {
					AssetMeasure assetMeasure = (AssetMeasure) measure;
					for (MeasureAssetValue measureAssetValue : assetMeasure.getMeasureAssetValues()) {
						if (scenario.hasInfluenceOnAsset(measureAssetValue.getAsset()))
							computeRRFAssetMeasure(scenario, parameter, numberFormat, measureAssetValue.getAsset(), findRRFAssetType(measureAssetValue.getAsset().getName(), rrfs),
									new RRFMeasure(measure.getId(), measure.getMeasureDescription().getReference()), assetMeasure);
					}
				}
			}
		}
		return rrfs;
	}

	private void computeRRFNormalMeasure(Scenario scenario, IParameter parameter, NumberFormat numberFormat, AssetType assetType, RRFAssetType rrfAssetType, RRFMeasure rrfMeasure,
			NormalMeasure normalMeasure) throws ParseException {
		rrfMeasure.setValue(numberFormat.parse(numberFormat.format(RRF.calculateNormalMeasureRRF(scenario, assetType, parameter, normalMeasure))).doubleValue());
		rrfAssetType.getRrfMeasures().add(rrfMeasure);
	}

	private RRFAssetType findRRFAssetType(String key, Map<String, Object> rrfs) {
		RRFAssetType rrfAssetType = (RRFAssetType) rrfs.get(key);
		if (rrfAssetType == null) {
			rrfAssetType = new RRFAssetType(key);
			rrfs.put(rrfAssetType.getLabel(), rrfAssetType);
		}
		return rrfAssetType;
	}

	private List<Chart> generateAssessmentRisk(Integer idAnalysis, Map<String, List<Assessment>> assessmentByAssetType) {
		ValueFactory valueFactory = new ValueFactory(daoLikelihoodParameter.findByAnalysisId(idAnalysis));
		valueFactory.add(daoImpactParameter.findByAnalysisId(idAnalysis));
		List<RiskAcceptanceParameter> riskAcceptanceParameters = daoRiskAcceptanceParameter.findByAnalysisId(idAnalysis);
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

		Distribution distribution = Distribution.Distribut(assessmentByAssetType.size(), aleChartSize, aleChartMaxSize);
		int multiplicator = Math.floorDiv(assessmentByAssetType.size(), distribution.getDivisor()), index = 1;
		List<Chart> charts = new ArrayList<>(multiplicator);
		Map<String, Dataset<String>> datasets = new LinkedHashMap<>();
		for (Entry<String, List<Assessment>> entry : assessmentByAssetType.entrySet()) {
			colorBounds.parallelStream().forEach(color -> color.setCount(0));
			entry.getValue().forEach(assessment -> {
				int importance = valueFactory.findImportance(assessment);
				colorBounds.stream().filter(colorBound -> colorBound.isAccepted(importance)).findAny().ifPresent(colorBound -> colorBound.setCount(colorBound.getCount() + 1));
			});
			if (charts.isEmpty() || (index++ % multiplicator) == 0) {
				charts.add(new Chart());
				datasets.clear();
			}
			Chart chart = charts.get(charts.size() - 1);
			if (colorBounds.parallelStream().anyMatch(colorBound -> colorBound.getCount() > 0)) {
				chart.getLabels().add(entry.getKey());
				colorBounds.forEach(colorBound -> {
					Dataset<String> dataset = datasets.get(colorBound.getLabel());
					if (dataset == null) {
						datasets.put(colorBound.getLabel(), dataset = new Dataset<String>(colorBound.getLabel(), colorBound.getColor()));
						chart.getDatasets().add(dataset);
					}
					while (dataset.getData().size() < chart.getLabels().size())
						dataset.getData().add(0);
					dataset.getData().set(chart.getLabels().size() - 1, colorBound.getCount());
				});
			}
		}
		return charts;
	}

	private String generateAssetMeasureSeries(Map<String, RRFAsset> rrfs) throws Exception {
		String series = "\"series\":[";

		for (String key : rrfs.keySet()) {
			String rrf = "[";
			RRFAsset rrfAsset = rrfs.get(key);
			for (RRFMeasure rrfMeasure : rrfAsset.getRrfMeasures())
				rrf += rrfMeasure.getValue() + ",";
			if (rrf.endsWith(","))
				rrf = rrf.substring(0, rrf.length() - 1);
			rrf += "]";
			series += "{\"name\":\"" + key + "\", \"data\":" + rrf + ",\"valueDecimals\": 0, \"visible\": true},";
		}

		if (series.endsWith(","))
			series = series.substring(0, series.length() - 1);
		series += "]";

		return series;
	}

	private String generateNormalMeasureSeries(Map<String, RRFAssetType> rrfs) throws Exception {
		String series = "\"series\":[";

		for (String key : rrfs.keySet()) {
			String rrf = "[";
			RRFAssetType rrfAssetType = rrfs.get(key);
			for (RRFMeasure rrfMeasure : rrfAssetType.getRrfMeasures())
				rrf += rrfMeasure.getValue() + ",";
			if (rrf.endsWith(","))
				rrf = rrf.substring(0, rrf.length() - 1);
			rrf += "]";
			series += "{\"name\":\"" + key + "\", \"data\":" + rrf + ",\"valueDecimals\": 0, \"visible\": true},";
		}

		if (series.endsWith(","))
			series = series.substring(0, series.length() - 1);
		series += "]";

		return series;
	}

	private <TAggregator> List<String> generateNotableEventsJson(Function<Assessment, TAggregator> aggregator, TAggregator key, long timeEnd, double currentAle, double nextAle,
			Map<Assessment, Set<String>> involvedVariables, Map<String, Double> currentExpressionParameters, Map<String, Double> nextExpressionParameters) {
		List<String> result = new ArrayList<>();

		// Check if the ALE in any scenario changes by any considerable amount
		if (Math.abs(nextAle - currentAle) > Constant.EVOLUTION_MIN_ALE_ABSOLUTE_DIFFERENCE
				&& Math.abs((nextAle - currentAle) / currentAle) >= Constant.EVOLUTION_MIN_ALE_RELATIVE_DIFFERENCE) {

			// Find parameter which changes most (which is responsible,
			// so-to-speak, for the drastic change in ALE)
			double maxRelativeDiff = 0.;
			String selectedDynamicParameterName = null;
			Double selectedDynamicParameterCurrentValue = null;
			Double selectedDynamicParameterNextValue = null;
			for (Assessment assessment : involvedVariables.keySet()) {
				if (!key.equals(aggregator.apply(assessment)))
					continue;
				for (String dynamicParameterName : involvedVariables.get(assessment)) {
					final double currentValue = currentExpressionParameters.getOrDefault(dynamicParameterName, 0.);
					final double nextValue = nextExpressionParameters.getOrDefault(dynamicParameterName, 0.);
					final double relativeDiff = Math.abs((nextValue - currentValue) / currentValue);
					if (relativeDiff > maxRelativeDiff) {
						maxRelativeDiff = relativeDiff;
						selectedDynamicParameterName = dynamicParameterName;
						selectedDynamicParameterCurrentValue = currentValue;
						selectedDynamicParameterNextValue = nextValue;
					}
				}
			}

			if (selectedDynamicParameterName != null) {
				result.add(String.format("{\"dynamicParameter\":\"%s\",\"aleOld\":%.2f,\"aleNew\":%.2f,\"valueOld\":%.5f,\"valueNew\":%.5f}",
						JSONObject.escape(selectedDynamicParameterName), currentAle, nextAle, selectedDynamicParameterCurrentValue, selectedDynamicParameterNextValue));
			}
		}
		return result;
	}

	/**
	 * ComputeCompliance: <br>
	 * Description
	 * 
	 * @param measures
	 * @param phase
	 * @param actionPlanMeasures
	 * @param previouscompliences
	 * @return
	 */
	public static Map<String, Object[]> ComputeCompliance(List<Measure> measures, Phase phase, Map<Integer, Boolean> actionPlanMeasures, Map<String, Object[]> previouscompliences,
			ValueFactory factory) {
		Map<String, Object[]> compliances = previouscompliences;
		for (Measure measure : measures) {
			if (measure.getPhase().getNumber() == phase.getNumber() && measure.getMeasureDescription().isComputable()) {
				String chapter = ActionPlanComputation.extractMainChapter(measure.getMeasureDescription().getReference());
				Object[] compliance = compliances.get(chapter);
				if (compliance == null)
					compliances.put(chapter, compliance = new Object[] { 0, 0.0 });
				if (actionPlanMeasures.containsKey(measure.getId()) && !measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE))
					compliance[1] = ((Double) compliance[1] + (Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE) - measure.getImplementationRateValue(factory));
			}
		}
		return compliances;
	}

	/**
	 * ComputeComplianceBefore: <br>
	 * Description
	 * 
	 * @param measures
	 * @return
	 */
	public static Map<String, Object[]> ComputeComplianceBefore(List<? extends Measure> measures, ValueFactory factory) {
		Map<String, Object[]> compliances = new LinkedHashMap<String, Object[]>();
		for (Measure measure : measures) {
			if (measure.getMeasureDescription().isComputable()) {
				String chapter = ActionPlanComputation.extractMainChapter(measure.getMeasureDescription().getReference());
				Object[] compliance = compliances.get(chapter);
				if (compliance == null)
					compliances.put(chapter, compliance = new Object[] { 0, 0.0 });

				if (!measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)) {
					compliance[1] = (Double) compliance[1] + measure.getImplementationRateValue(factory);
					compliance[0] = (Integer) compliance[0] + 1;
				}
			}
		}
		return compliances;
	}

	/**
	 * Converts a time difference (in seconds) into a human-readable string.
	 */
	public static String deltaTimeToString(long deltaTime) {
		if (deltaTime < 60)
			return String.format("%d s", deltaTime);
		else if (deltaTime < 3600)
			return String.format("%d m", Math.round(deltaTime / 60.0));
		else if (deltaTime < 86400)
			return String.format("%d h", Math.round(deltaTime / 3600.0));
		else if (deltaTime < 86400 * 7)
			return String.format("%d d", Math.round(deltaTime / 86400.0));
		else
			return String.format("%d w", Math.round(deltaTime / 86400.0 / 7));
	}

	public static Chart generateRiskHeatMap(Analysis analysis, ValueFactory factory) {
		if (factory == null)
			factory = new ValueFactory(analysis.getParameters());
		Map<String, Integer> importanceByCount = new LinkedHashMap<>();
		for (Assessment assessment : analysis.getSelectedAssessments()) {
			Integer impact = factory.findImpactLevel(assessment.getImpacts()), probability = factory.findProbLevel(assessment.getLikelihood());
			if (impact == 0 || probability == 0)
				continue;
			String key = String.format("%d-%d", impact, probability);
			Integer value = importanceByCount.get(key);
			if (value == null)
				value = 0;
			importanceByCount.put(key, ++value);
		}

		String type = factory.getImpacts().keySet().stream().findAny().orElse(null);

		List<? extends IBoundedParameter> probabilities = analysis.getLikelihoodParameters(), impacts = factory.getImpacts().get(type);

		List<RiskAcceptanceParameter> riskAcceptanceParameters = analysis.getRiskAcceptanceParameters();

		List<ColorBound> colorBounds = new ArrayList<>(riskAcceptanceParameters.size());

		Chart chart = new Chart();

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

		probabilities.stream().filter(probability -> probability.getLevel() > 0).sorted((p1, p2) -> Integer.compare(p1.getLevel(), p2.getLevel())).forEach(probability -> {
			chart.getLabels().add(probability.getLevel() + (StringUtils.isEmpty(probability.getLabel()) ? "" : "-" + probability.getLabel()));
		});

		impacts.stream().filter(impact -> impact.getLevel() > 0).sorted((p1, p2) -> Integer.compare(p2.getLevel(), p1.getLevel())).forEach(impact -> {
			Dataset<List<String>> dataset = new Dataset<List<String>>(new ArrayList<>());
			dataset.setLabel(impact.getLevel() + (StringUtils.isEmpty(impact.getLabel()) ? "" : "-" + impact.getLabel()));
			for (int i = 1; i < probabilities.size(); i++) {
				Integer importance = impact.getLevel() * i, count = importanceByCount.get(String.format("%d-%d", impact.getLevel(), i));
				ColorBound colorBound = colorBounds.stream().filter(color -> color.isAccepted(importance)).findAny().orElse(null);
				if (colorBound != null) {
					if (count != null)
						colorBound.setCount(colorBound.getCount() + count);
					dataset.getBackgroundColor().add(colorBound.getColor());
				} else
					dataset.getBackgroundColor().add("#000000");
				dataset.getData().add(count == null ? "" : count);
			}
			chart.getDatasets().add(dataset);
		});

		colorBounds.forEach(colorBound -> chart.getLegends().add(new Legend(colorBound.getCount() + " " + colorBound.getLabel(), colorBound.getColor())));

		return chart;
	}

	public Chart generateTotalRiskJSChart(List<Analysis> analyses, Locale locale) {
		Chart chart = new Chart(messageSource.getMessage("label.title.chart.total_risk", null, "Total Risk", locale));
		if (analyses.isEmpty())
			return chart;
		Map<Integer, ValueFactory> valueFactories = analyses.stream().collect(Collectors.toMap(Analysis::getId, analysis -> new ValueFactory(analysis.getParameters())));
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
		Map<String, Dataset<String>> datasets = new LinkedHashMap<>();
		for (Analysis analysis : analyses) {
			ValueFactory valueFactory = valueFactories.get(analysis.getId());
			analysis.getAssessments().stream().filter(Assessment::isSelected).forEach(assessment -> {
				int importance = valueFactory.findImportance(assessment);
				colorBounds.stream().filter(colorBound -> colorBound.isAccepted(importance)).findAny().ifPresent(colorBound -> colorBound.setCount(colorBound.getCount() + 1));
			});
			if (colorBounds.parallelStream().anyMatch(colorBound -> colorBound.getCount() > 0)) {
				chart.getLabels().add(analysis.getLabel() + " " + analysis.getVersion());
				colorBounds.forEach(colorBound -> {
					Dataset<String> dataset = datasets.get(colorBound.getLabel());
					if (dataset == null) {
						datasets.put(colorBound.getLabel(), dataset = new Dataset<String>(colorBound.getLabel(), colorBound.getColor()));
						chart.getDatasets().add(dataset);
					}
					while (dataset.getData().size() < chart.getLabels().size())
						dataset.getData().add(0);
					dataset.getData().set(chart.getLabels().size() - 1, colorBound.getCount());
				});
			}
			colorBounds.parallelStream().forEach(color -> color.setCount(0));
		}
		chart.setSettings(riskAcceptanceParameters);
		return chart;
	}

}

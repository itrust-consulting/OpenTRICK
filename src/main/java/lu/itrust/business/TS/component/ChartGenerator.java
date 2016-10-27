package lu.itrust.business.TS.component;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOActionPlan;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOAnalysisStandard;
import lu.itrust.business.TS.database.dao.DAOAssessment;
import lu.itrust.business.TS.database.dao.DAOAssetType;
import lu.itrust.business.TS.database.dao.DAOMeasure;
import lu.itrust.business.TS.database.dao.DAOParameter;
import lu.itrust.business.TS.database.dao.DAOPhase;
import lu.itrust.business.TS.database.dao.DAOScenario;
import lu.itrust.business.TS.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.helper.ActionPlanComputation;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.actionplan.summary.helper.ActionPlanSummaryManager;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.assessment.helper.ALE;
import lu.itrust.business.TS.model.assessment.helper.AssetComparatorByALE;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.rrf.RRF;
import lu.itrust.business.TS.model.rrf.RRFAsset;
import lu.itrust.business.TS.model.rrf.RRFAssetType;
import lu.itrust.business.TS.model.rrf.RRFMeasure;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
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

	@Autowired
	private DAOActionPlan daoActionPlan;

	@Autowired
	private DAOPhase daoPhase;

	@Autowired
	private DAOMeasure daoMeasure;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private DAOAssessment daoAssessment;

	@Autowired
	private DAOScenario daoScenario;

	@Autowired
	private DAOAssetType daoAssetType;

	@Autowired
	private DAOParameter daoParameter;

	@Autowired
	private DAOAnalysisStandard daoAnalysisStandard;

	@Autowired
	private DAOAnalysis daoAnalysis;

	@Autowired
	private ServiceExternalNotification serviceExternalNotification;

	@Autowired
	private DAOUserAnalysisRight daoUserAnalysisRight;

	@Autowired
	private DynamicRiskComputer dynamicRiskComputer;

	@Value("${app.settings.ale.chart.content.size}")
	private int aleChartSize;

	@Value("${app.settings.ale.chart.content.max.size}")
	private int aleChartMaxSize;

	@Value("${app.settings.ale.chart.single.content.max.size}")
	private int aleChartSingleMaxSize;

	private String exporting = "\"exporting\":{\"sourceWidth\":1500,\"sourceHeight\": 600,\"chartOptions\": {\"legend\": {\"enabled\": true,\"title\": { \"text\": \"\"  }, \"itemHiddenStyle\": { \"display\": \"none\" } }, \"rangeSelector\": {\"enabled\": false },\"navigator\": {\"enabled\": false},\"scrollbar\": {\"enabled\": false}}}";

	/**
	 * aleByAsset: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	public String aleByAsset(int idAnalysis, Locale locale) throws Exception {

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
				return generateALEChart(locale, messageSource.getMessage("label.title.chart.ale_by_asset", null, "ALE by Asset", locale), ales);
			Distribution distribution = Distribution.Distribut(ales.size(), aleChartSize, aleChartMaxSize);
			String result = "";
			int multiplicator = ales.size() / distribution.getDivisor();
			for (int i = 0; i < multiplicator; i++) {
				List<ALE> aleSubList = ales.subList(i * distribution.getDivisor(), i == (multiplicator - 1) ? ales.size() : (i + 1) * distribution.getDivisor());
				if (aleSubList.get(0).getValue() == 0)
					break;
				result += (result.isEmpty() ? "" : ",") + generateALEChart(locale, messageSource.getMessage("label.title.chart.part.ale_by_asset",
						new Integer[] { i + 1, multiplicator }, String.format("ALE by Asset %d/%d", i + 1, multiplicator), locale), aleSubList);
			}
			return String.format("[%s]", result);
		} finally {
			assessments.clear();
			mappedALEs.clear();
			ales.clear();
		}
	}

	/**
	 * aleByAssetType: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param locale
	 * @return
	 */
	public String aleByAssetType(int idAnalysis, Locale locale) throws Exception {
		List<Assessment> assessments = daoAssessment.getAllFromAnalysisAndSelected(idAnalysis);
		Map<Integer, ALE> mappedALEs = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales = new LinkedList<ALE>();
		try {
			for (Assessment assessment : assessments) {
				ALE ale = mappedALEs.get(assessment.getAsset().getAssetType().getId());
				if (ale == null) {
					mappedALEs.put(assessment.getAsset().getAssetType().getId(), ale = new ALE(assessment.getAsset().getAssetType().getType(), 0));
					ales.add(ale);
				}
				ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
			}
			Collections.sort(ales, new AssetComparatorByALE());

			if (ales.size() <= aleChartSingleMaxSize)
				return generateALEChart(locale, messageSource.getMessage("label.title.chart.ale_by_asset_type", null, "ALE by Asset Type", locale), ales);
			Distribution distribution = Distribution.Distribut(ales.size(), aleChartSize, aleChartMaxSize);
			String result = "";
			int multiplicator = ales.size() / distribution.getDivisor();
			for (int i = 0; i < multiplicator; i++) {
				List<ALE> aleSubList = ales.subList(i * distribution.getDivisor(), i == (multiplicator - 1) ? ales.size() : (i + 1) * distribution.getDivisor());
				if (aleSubList.get(0).getValue() == 0)
					break;
				result += (result.isEmpty() ? "" : ",") + generateALEChart(locale, messageSource.getMessage("label.title.chart.part.ale_by_asset_type",
						new Integer[] { i + 1, multiplicator }, String.format("ALE by Asset Type %d/%d", i + 1, multiplicator), locale), aleSubList);
			}
			return String.format("[%s]", result);
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
	public String aleByScenarioType(Integer idAnalysis, Locale locale) throws Exception {
		List<Assessment> assessments = daoAssessment.getAllFromAnalysisAndSelected(idAnalysis);
		Map<Integer, ALE> mappedALEs = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales = new LinkedList<ALE>();
		try {
			for (Assessment assessment : assessments) {
				ALE ale = mappedALEs.get(assessment.getScenario().getType().getValue());
				if (ale == null) {
					mappedALEs.put(assessment.getScenario().getType().getValue(), ale = new ALE(assessment.getScenario().getType().getName(), 0));
					ales.add(ale);
				}
				ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
			}
			Collections.sort(ales, new AssetComparatorByALE());

			if (ales.size() <= aleChartSingleMaxSize)
				return generateALEChart(locale, messageSource.getMessage("label.title.chart.ale_by_scenario_type", null, "ALE by Scenario Type", locale), ales);
			Distribution distribution = Distribution.Distribut(ales.size(), aleChartSize, aleChartMaxSize);
			String result = "";
			int multiplicator = ales.size() / distribution.getDivisor();
			for (int i = 0; i < multiplicator; i++) {
				List<ALE> aleSubList = ales.subList(i * distribution.getDivisor(), i == (multiplicator - 1) ? ales.size() : (i + 1) * distribution.getDivisor());
				if (aleSubList.get(0).getValue() == 0)
					break;
				result += (result.isEmpty() ? "" : ",") + generateALEChart(locale, messageSource.getMessage("label.title.chart.part.ale_by_scenario_type",
						new Integer[] { i + 1, multiplicator }, String.format("ALE by Scenario Type %d/%d", i + 1, multiplicator), locale), aleSubList);
			}
			return String.format("[%s]", result);
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
	public String aleByScenario(Integer idAnalysis, Locale locale) throws Exception {
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
				return generateALEChart(locale, messageSource.getMessage("label.title.chart.ale_by_scenario", null, "ALE by Scenario", locale), ales);
			Distribution distribution = Distribution.Distribut(ales.size(), aleChartSize, aleChartMaxSize);
			String result = "";
			int multiplicator = ales.size() / distribution.getDivisor();
			for (int i = 0; i < multiplicator; i++) {
				List<ALE> aleSubList = ales.subList(i * distribution.getDivisor(), i == (multiplicator - 1) ? ales.size() : (i + 1) * distribution.getDivisor());
				if (aleSubList.get(0).getValue() == 0)
					break;
				result += (result.isEmpty() ? "" : ",") + generateALEChart(locale, messageSource.getMessage("label.title.chart.part.ale_by_scenario",
						new Integer[] { i + 1, multiplicator }, String.format("ALE by Scenario %d/%d", i + 1, multiplicator), locale), aleSubList);
			}
			return String.format("[%s]", result);
		} finally {
			assessments.clear();
			mappedALEs.clear();
			ales.clear();
		}
	}

	public String generateALEChart(Locale locale, String chartitle, List<ALE> ales) {
		return generateALEChart(locale, chartitle, new ALEChart(ales));
	}

	public String generateALEChart(Locale locale, String chartitle, ALEChart... aleCharts) {
		JsonChart chart = new JsonChart("\"chart\":{ \"type\":\"column\",  \"zoomType\": \"y\", \"marginTop\": 50},  \"scrollbar\": {\"enabled\": true}",
				"\"title\": {\"text\":\"" + chartitle + "\"}", "\"pane\": {\"size\": \"100%\"}",
				"\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\"}");

		chart.setPlotOptions("\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0 }}");
		chart.setTooltip("\"tooltip\": { \"valueDecimals\": 2, \"valueSuffix\": \"k€\",\"useHTML\": true }");
		if (aleCharts.length == 1)
			buildSingleALESerie(chart, aleCharts[0]);
		else if (aleCharts.length > 0)
			buildMulitALESeries(chart, aleCharts);
		return chart.toString();

	}

	private void buildMulitALESeries(JsonChart chart, ALEChart... aleCharts) {

		Map<String, Map<String, ALE>> aleChartMapper = new LinkedHashMap<>();

		Map<String, ALE> references = new LinkedHashMap<>(aleCharts[0].getAles().size());

		aleCharts[0].getAles().forEach(ale -> references.put(ale.getAssetName(), ale));

		aleChartMapper.put(aleCharts[0].getName(), references);

		for (int i = 1; i < aleCharts.length; i++) {
			Map<String, ALE> aleMapper = aleCharts[i].getAles().stream().filter(ale -> references.containsKey(ale.getAssetName()))
					.collect(Collectors.toMap(ALE::getAssetName, Function.identity()));
			if (!aleMapper.isEmpty())
				aleChartMapper.put(aleCharts[i].getName(), aleMapper);
		}

		double max = aleChartMapper.values().stream().flatMap(aleMapper -> aleMapper.values().stream()).mapToDouble(ALE::getValue).max().orElse(0d);

		int count = references.size();

		String categories = "", series = "";

		for (String category : references.keySet())
			categories += String.format("%s\"%s\"", categories.isEmpty() ? "" : ",", category);

		for (Entry<String, Map<String, ALE>> entry : aleChartMapper.entrySet()) {
			String dataALEs = "";
			for (String category : references.keySet()) {
				ALE ale = entry.getValue().get(category);
				dataALEs += String.format("%s%f", dataALEs.isEmpty() ? "" : ",", ale == null ? 0d : ale.getValue());
			}
			series += String.format("%s{ \"name\":\"%s\",\"data\":[%s]}", series.isEmpty() ? "" : ",", entry.getKey(), dataALEs);
		}

		chart.setSeries("\"series\":[" + series + "]");

		chart.setxAxis("\"xAxis\":{\"categories\":[" + categories + "], \"min\":\"0\", \"max\":\"" + (count - 1) + "\"}");

		chart.setyAxis("\"yAxis\": {\"min\": 0 , \"max\":" + max * 1.1 + ", \"title\": {\"text\": \"ALE\"},\"labels\":{\"format\": \"{value} k&euro;\",\"useHTML\": true}}");
	}

	private void buildSingleALESerie(JsonChart chart, ALEChart data) {

		double max = data.getAles().stream().mapToDouble(ALE::getValue).max().orElse(0d);

		int count = data.getAles().size();

		String categories = "[";

		String dataALEs = "[";

		for (ALE ale : data.getAles()) {
			categories += "\"" + ale.getAssetName() + "\",";
			dataALEs += ale.getValue() + ",";
		}

		if (categories.endsWith(",")) {
			categories = categories.substring(0, categories.length() - 1);
			dataALEs = dataALEs.substring(0, dataALEs.length() - 1);
		}
		categories += "]";
		dataALEs += "]";

		chart.setxAxis("\"xAxis\":{\"categories\":" + categories + ", \"min\":\"0\", \"max\":\"" + (count - 1) + "\"}");

		chart.setSeries("\"series\":[{ \"name\":\"" + data.getName() + "\",\"data\":" + dataALEs + "}]");

		chart.setyAxis("\"yAxis\": {\"min\": 0 , \"max\":" + max * 1.1 + ", \"title\": {\"text\": \"ALE\"},\"labels\":{\"format\": \"{value} k&euro;\",\"useHTML\": true}}");

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
	 * compliance: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param standard
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	public String compliance(Locale locale, ComplianceChartData... measureChartDatas) {

		ComplianceChartData reference = measureChartDatas[0];

		Map<String, Object[]> compliances = ComputeComplianceBefore(reference.getMeasures(), reference.getFactory());

		JsonChart chart = new JsonChart("\"chart\":{ \"polar\":true, \"type\":\"line\",\"marginBottom\": 30, \"marginTop\": 50},  \"scrollbar\": {\"enabled\": false}",
				"\"title\": { \"marginLeft\": -50, \"text\":\""
						+ messageSource.getMessage("label.title.chart.measure.compliance", new Object[] { reference.getStandard() }, reference + " measure compliance", locale)
						+ "\"}",
				"\"pane\": {\"size\": \"100%\"}", "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\",\"layout\": \"vertical\",  \"y\": 70 }");

		chart.setPlotOptions("\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0 }}");

		if (measureChartDatas.length > 0) {

			chart.setyAxis(
					"\"yAxis\": {\"gridLineInterpolation\": \"polygon\" , \"lineWidth\":0,\"min\":0,\"max\":100, \"tickInterval\": 20, \"labels\":{ \"format\": \"{value}%\"} }");

			String series = "";
			String categories = "";
			String data = "";
			for (String key : compliances.keySet()) {
				Object[] compliance = compliances.get(key);
				data += (data.isEmpty() ? "" : ",") + (int) Math.floor(((Double) compliance[1]) / (Integer) compliance[0]);
				categories += (categories.isEmpty() ? "" : ",") + "\"" + key + "\"";
			}

			series += (series.isEmpty() ? "" : ",") + "{\"name\":\"" + reference.getAnalysisKey() + "\", \"data\":[" + data + "],\"valueDecimals\": 0}";

			if (measureChartDatas.length > 1) {
				Collection<String> keys = compliances.keySet();
				for (int i = 1; i < measureChartDatas.length; i++) {
					compliances = ComputeComplianceBefore(measureChartDatas[i].getMeasures(), measureChartDatas[i].getFactory());
					data = "";
					for (String key : keys) {
						Object[] compliance = compliances.get(key);
						if (compliance == null)
							data += (data.isEmpty() ? "0" : ",0");
						else
							data += (data.isEmpty() ? "" : ",") + (int) Math.floor(((Double) compliance[1]) / (Integer) compliance[0]);
					}
					series += (series.isEmpty() ? "" : ",") + "{\"name\":\"" + measureChartDatas[i].getAnalysisKey() + "\", \"data\":[" + data + "],\"valueDecimals\": 0}";
				}
			}
			chart.setxAxis(String.format("\"xAxis\":{\"categories\":[%s]}", categories));
			chart.setSeries(String.format("\"series\":[%s]", series));
		}

		return chart.toString();
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
	 * compliance: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param standard
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	public String compliance(int idAnalysis, String standard, Locale locale) throws Exception {

		List<Measure> measures = daoMeasure.getAllFromAnalysisAndStandard(idAnalysis, standard);

		ValueFactory factory = new ValueFactory(daoParameter.findAllDynamicByAnalysisId(idAnalysis));

		Map<String, Object[]> previouscompliances = ComputeComplianceBefore(measures, factory);

		String chart = "\"chart\":{ \"polar\":true, \"type\":\"line\",\"marginBottom\": 30, \"marginTop\": 50},  \"scrollbar\": {\"enabled\": false}";

		String title = "\"title\": {\"text\":\""
				+ messageSource.getMessage("label.title.chart.measure.compliance", new Object[] { standard }, standard + " measure compliance", locale) + "\"}";

		String pane = "\"pane\": {\"size\": \"100%\"}";

		String legend = "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\"}";

		String plotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0 }}";

		if (previouscompliances.isEmpty())
			return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "}";

		String series = "\"series\":[";

		String xAxis = "";

		String yAxis = "\"yAxis\": {\"gridLineInterpolation\": \"polygon\" , \"lineWidth\":0,\"min\":0,\"max\":100, \"tickInterval\": 20, \"labels\":{ \"format\": \"{value}%\"} }";

		String categories = "[";

		String data = "[";

		for (String key : previouscompliances.keySet()) {
			Object[] compliance = previouscompliances.get(key);
			categories += "\"" + key + "\",";
			data += (int) Math.floor(((Double) compliance[1]) / (Integer) compliance[0]) + ",";
		}

		if (categories.endsWith(",")) {
			categories = categories.substring(0, categories.length() - 1);
			data = data.substring(0, data.length() - 1);
		}

		categories += "]";

		data += "]";

		xAxis = "\"xAxis\":{\"categories\":" + categories + "}";

		String serie = "";

		serie = "{\"name\":\"" + messageSource.getMessage("label.chart.series.current_level", null, "Current Level", locale) + "\", \"data\":" + data + ",\"valueDecimals\": 0}";

		series += serie;

		List<Integer> idMeasureInActionPlans = daoMeasure.getIdMeasuresImplementedByActionPlanTypeFromIdAnalysisAndStandard(idAnalysis, standard, ActionPlanMode.APPN);

		Map<Integer, Boolean> actionPlanMeasures = new LinkedHashMap<Integer, Boolean>(idMeasureInActionPlans.size());

		for (Integer integer : idMeasureInActionPlans)
			actionPlanMeasures.put(integer, true);

		idMeasureInActionPlans.clear();

		List<Phase> phases = daoPhase.getAllFromAnalysisActionPlan(idAnalysis);

		if (!actionPlanMeasures.isEmpty()) {

			for (Phase phase : phases) {

				if (phase.getNumber() == Constant.PHASE_NOT_USABLE)
					continue;

				Map<String, Object[]> compliances = null;

				compliances = ComputeCompliance(measures, phase, actionPlanMeasures, previouscompliances, factory);

				previouscompliances = compliances;

				if (compliances.size() == 0)
					continue;

				data = "[";

				for (String key : compliances.keySet()) {
					Object[] compliance = compliances.get(key);
					data += (int) Math.floor(((Double) compliance[1]) / (Integer) compliance[0]) + ",";
				}

				data = data.substring(0, data.length() - 1);

				data += "]";

				serie = "";

				serie = "{\"name\":\"" + messageSource.getMessage("label.chart.phase", null, "Phase", locale) + " " + phase.getNumber() + "\", \"data\":" + data
						+ ",\"valueDecimals\": 0}";

				series += "," + serie;

			}

		}

		series += "]";

		return ("{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + xAxis + "," + yAxis + "," + series + "," + exporting + "}").replaceAll("\r|\n",
				" ");
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
	public String evolutionProfitabilityCompliance(Integer idAnalysis, List<SummaryStage> summaryStages, List<Phase> phases, String actionPlanType, Locale locale)
			throws Exception {

		Map<String, List<String>> summaries = ActionPlanSummaryManager.buildTable(summaryStages, phases);

		String chart = "\"chart\":{ \"type\":\"column\",  \"zoomType\": \"xy\", \"marginTop\": 50},  \"scrollbar\": {\"enabled\": false}";

		String title = "\"title\": {\"text\":\"" + messageSource.getMessage("label.title.chart.evolution_profitability_compliance." + actionPlanType.toLowerCase(), null,
				"Evolution of profitability and ISO compliance for " + actionPlanType, locale) + "\"}";

		String pane = "\"pane\": {\"size\": \"100%\"}";

		String legend = "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\",\"useHTML\":true}";

		String plotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0 }}";

		if (summaries.isEmpty())
			return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "}";

		String xAxis = "";

		String series = "";

		String categories = "[";

		String ale = "[";

		String riskReduction = "[";

		String costOfMeasures = "[";

		String rosi = "[";

		String relatifRosi = "[";

		Map<String, Phase> usesPhases = ActionPlanSummaryManager.buildPhase(phases, ActionPlanSummaryManager.extractPhaseRow(summaryStages));

		for (Phase phase : usesPhases.values())
			categories += "\"P" + phase.getNumber() + "\",";

		Map<String, List<String>> standardcompliances = new LinkedHashMap<String, List<String>>();

		Map<String, String> compliancedata = new LinkedHashMap<String, String>();

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

		List<String> dataALEs = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ALE_UNTIL_END);

		List<String> dataRiskReductions = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_RISK_REDUCTION);

		List<String> dataCostOfMeasures = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_AVERAGE_YEARLY_COST_OF_PHASE);

		List<String> dataROSIs = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI);

		List<String> dataRelatifROSIs = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI_RELATIF);

		int size = dataALEs.size() - 1;

		for (int i = 0; i < usesPhases.size(); i++) {
			ale += dataALEs.get(i) + (size != i ? "," : "]");
			riskReduction += dataRiskReductions.get(i) + (size != i ? "," : "]");
			costOfMeasures += dataCostOfMeasures.get(i) + (size != i ? "," : "]");
			rosi += dataROSIs.get(i) + (size != i ? "," : "]");
			relatifRosi += dataRelatifROSIs.get(i) + (size != i ? "," : "]");

			for (String key : standardcompliances.keySet()) {

				if (compliancedata.get(key) == null) {
					String data = "[";
					data += standardcompliances.get(key).get(i) + (size != i ? "," : "]");
					compliancedata.put(key, data);
				} else {
					String data = compliancedata.get(key);
					data += standardcompliances.get(key).get(i) + (size != i ? "," : "]");
					compliancedata.put(key, data);
				}

			}

		}

		if (categories.endsWith(","))
			categories = categories.substring(0, categories.length() - 1);
		categories += "]";

		String keuroByYear = messageSource.getMessage("label.metric.keuro_by_year", null, "k€/y", locale);

		String yAxis = "\"yAxis\": [{ \"labels\":{\"format\": \"{value} " + keuroByYear + "\",\"useHTML\": true}, \"title\": {\"text\":\""
				+ messageSource.getMessage("label.summary.cost", null, "Cost", locale)
				+ "\"}},{\"min\": 0,\"max\": 100, \"labels\":{ \"format\": \"{value}%\"}, \"title\":{\"text\":\""
				+ messageSource.getMessage("label.summary.compliance", null, "Compliance", locale) + "\"}, \"opposite\": true} ]";

		xAxis = "\"xAxis\":{\"categories\":" + categories + "}";

		series += "\"series\":[";

		for (String key : compliancedata.keySet()) {

			if (isStandardInActionPlan(key, daoActionPlan.getAllFromAnalysis(idAnalysis)))
				series += "{\"name\":\"" + messageSource.getMessage(ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COMPLIANCE, null, "Compliance", locale) + " " + key + "\","
						+ " \"data\":" + compliancedata.get(key) + ", \"valueDecimals\": 0, \"type\": \"column\", \"yAxis\": 1, \"tooltip\": {\"valueSuffix\": \"%\"}},";
		}

		series += "{\"name\":\"" + messageSource.getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_ALE_UNTIL_END, null, "ALE (k€)... at end", locale) + "\", \"data\":" + ale
				+ ",\"valueDecimals\": 0,\"type\": \"line\"},  {\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_RISK_REDUCTION, null, "Risk reduction", locale) + "\", \"data\":" + riskReduction
				+ ",\"valueDecimals\": 0,\"type\": \"line\"},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_AVERAGE_YEARLY_COST_OF_PHASE, null, "Average yearly cost of phase (k€/y)", locale)
				+ "\", \"data\":" + costOfMeasures + ",\"valueDecimals\": 0,\"type\": \"line\"},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI, null, "ROSI", locale) + "\", \"data\":" + rosi
				+ ",\"valueDecimals\": 0,\"type\": \"line\"},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI_RELATIF, null, "ROSI relatif", locale) + "\", \"data\":" + relatifRosi
				+ ",\"valueDecimals\": 0,\"type\": \"line\"}]";

		return ("{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + xAxis + "," + yAxis + "," + series + "," + exporting + "}").replaceAll("\r|\n",
				" ");
	}

	private boolean isStandardInActionPlan(String standard, List<ActionPlanEntry> actionplans) {
		boolean result = false;

		for (ActionPlanEntry entry : actionplans)
			if (entry.getMeasure().getAnalysisStandard().getStandard().getLabel().equals(standard)) {
				result = true;
				break;
			}

		return result;
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
	public String budget(List<SummaryStage> summaryStages, List<Phase> phases, String actionPlanType, Locale locale) {

		Map<String, List<String>> summaries = ActionPlanSummaryManager.buildTable(summaryStages, phases);

		String chart = "\"chart\":{ \"type\":\"column\",  \"zoomType\": \"xy\", \"marginTop\": 50},  \"scrollbar\": {\"enabled\": false}";

		String title = "\"title\": {\"text\":\""
				+ messageSource.getMessage("label.title.chart.budget." + actionPlanType.toLowerCase(), null, "Budget for " + actionPlanType, locale) + "\"}";

		String pane = "\"pane\": {\"size\": \"100%\"}";

		String legend = "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\"}";

		String plotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0 }}";

		if (summaries.isEmpty())
			return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "}";

		String xAxis = "";

		String series = "";

		String categories = "[";

		String internalWorkload = "[";

		String externalWorkload = "[";

		String internalMaintenace = "[";

		String externalMaintenance = "[";

		String investment = "[";

		String implementPhaseCost = "[";

		String currentCost = "[";

		String totalPhaseCost = "[";

		Map<String, Phase> usesPhases = ActionPlanSummaryManager.buildPhase(phases, ActionPlanSummaryManager.extractPhaseRow(summaryStages));

		for (Phase phase : usesPhases.values())
			categories += "\"P" + phase.getNumber() + "\",";

		List<String> dataInternalWorkload = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD);

		List<String> dataExternalWorkload = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD);

		List<String> dataInternalMaintenace = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE);

		List<String> dataExternalMaintenance = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE);

		List<String> dataInvestment = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INVESTMENT);

		List<String> dataImplementPhaseCost = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_IMPLEMENT_PHASE_COST);

		List<String> dataCurrentCost = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_RECURRENT_COST);

		List<String> dataTotalPhaseCost = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST);

		int size = dataInternalMaintenace.size() - 1;

		for (int i = 0; i < dataInternalWorkload.size(); i++) {
			internalWorkload += dataInternalWorkload.get(i) + (size != i ? "," : "]");
			externalWorkload += dataExternalWorkload.get(i) + (size != i ? "," : "]");
			internalMaintenace += dataInternalMaintenace.get(i) + (size != i ? "," : "]");
			externalMaintenance += dataExternalMaintenance.get(i) + (size != i ? "," : "]");
			investment += dataInvestment.get(i) + (size != i ? "," : "]");
			implementPhaseCost += dataImplementPhaseCost.get(i) + (size != i ? "," : "]");
			currentCost += dataCurrentCost.get(i) + (size != i ? "," : "]");
			totalPhaseCost += dataTotalPhaseCost.get(i) + (size != i ? "," : "]");
		}

		if (!internalWorkload.endsWith("]")) {
			internalWorkload += "]";
			externalWorkload += "]";
			internalMaintenace += "]";
			externalMaintenance += "]";
			investment += "]";
			implementPhaseCost += "]";
			currentCost += "]";
			totalPhaseCost += "]";
		}

		if (categories.endsWith(","))
			categories = categories.substring(0, categories.length() - 1);
		categories += "]";

		String manDay = messageSource.getMessage("label.metric.man_day", null, "md", locale);

		String yAxis = "\"yAxis\": [{\"min\": 0, \"labels\":{\"format\": \"{value} k€\",\"useHTML\": true}, \"title\": {\"text\":\""
				+ messageSource.getMessage("label.summary.cost", null, "Cost", locale) + "\"}},{\"min\": 0,\"max\": 100, \"labels\":{ \"format\": \"{value}" + manDay
				+ "\"}, \"title\":{\"text\":\"" + messageSource.getMessage("label.summary.workload", null, "Workload", locale) + "\"}, \"opposite\": true} ]";
		xAxis = "\"xAxis\":{\"categories\":" + categories + "}";
		series += "\"series\":[{\"name\":\"" + messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD, null, "Internal workload", locale)
				+ "\", \"data\":" + internalWorkload + ",\"valueDecimals\": 0,  \"type\": \"column\",\"yAxis\": 1}, {\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD, null, "External workload", locale) + "\", \"data\":"
				+ externalWorkload + ",\"valueDecimals\": 0,  \"type\": \"column\",\"yAxis\": 1},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE, null, "Internal maintenance", locale) + "\", \"data\":"
				+ internalMaintenace + ",\"valueDecimals\": 0,\"type\": \"line\"},  {\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE, null, "External maintenance", locale) + "\", \"data\":"
				+ externalMaintenance + ",\"valueDecimals\": 0,\"type\": \"line\"},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INVESTMENT, null, "Investment", locale) + "\", \"data\":" + investment
				+ ",\"valueDecimals\": 0,\"type\": \"line\"},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_IMPLEMENT_PHASE_COST, null, "Total implement cost of phase", locale) + "\", \"data\":"
				+ implementPhaseCost + ",\"valueDecimals\": 0,\"type\": \"line\"},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_RECURRENT_COST, null, "Current cost", locale) + "\", \"data\":" + currentCost
				+ ",\"valueDecimals\": 0,\"type\": \"line\"},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST, null, "Total phase cost", locale) + "\", \"data\":" + totalPhaseCost
				+ ",\"valueDecimals\": 0,\"type\": \"line\"}]";

		return ("{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + xAxis + "," + yAxis + "," + series + ", " + exporting + "}").replaceAll("\r|\n",
				" ");
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

			Map<String, Object> rrfs = computeRRFByScenario(scenario, measures, idAnalysis);

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

				Map<String, RRFAssetType> rrfs = computeRRFByNormalMeasure((NormalMeasure) measure, assetTypes, scenarios, idAnalysis);

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
	private Map<String, RRFAssetType> computeRRFByNormalMeasure(NormalMeasure measure, List<AssetType> assetTypes, List<Scenario> scenarios, int idAnalysis) throws Exception {
		IParameter parameter = daoParameter.getFromAnalysisByTypeAndDescription(idAnalysis, Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, Constant.PARAMETER_MAX_RRF);
		Map<String, RRFAssetType> rrfs = new LinkedHashMap<String, RRFAssetType>(assetTypes.size());
		for (AssetType assetType : assetTypes) {
			RRFAssetType rrfAssetType = new RRFAssetType(assetType.getType());
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
		IParameter parameter = daoParameter.getFromAnalysisByTypeAndDescription(idAnalysis, Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, Constant.PARAMETER_MAX_RRF);
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

	private Map<String, Object> computeRRFByScenario(Scenario scenario, List<Measure> measures, int idAnalysis) throws Exception {
		IParameter parameter = daoParameter.getFromAnalysisByTypeAndDescription(idAnalysis, Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, Constant.PARAMETER_MAX_RRF);
		Map<String, Object> rrfs = new LinkedHashMap<String, Object>();
		if (scenario.getAssetTypeValues().size() == 0)
			throw new TrickException("error.rrf.scneario.no_assettypevalues", "The scenario " + scenario.getName() + " does not have any asset types attributed!",
					scenario.getName());

		for (Measure measure : measures) {

			for (AssetTypeValue atv : scenario.getAssetTypeValues()) {

				RRFAssetType rrfAssetType = (RRFAssetType) rrfs.get(atv.getAssetType().getType());
				if (rrfAssetType == null) {
					rrfAssetType = new RRFAssetType(atv.getAssetType().getType());
					rrfs.put(rrfAssetType.getLabel(), rrfAssetType);
				}

				RRFMeasure rrfMeasure = new RRFMeasure(measure.getId(), measure.getMeasureDescription().getReference());
				if (measure instanceof NormalMeasure) {

					NormalMeasure normalMeasure = (NormalMeasure) measure;

					double val = RRF.calculateNormalMeasureRRF(scenario, atv.getAssetType(), parameter, normalMeasure);

					NumberFormat nf = new DecimalFormat();

					nf.setMaximumFractionDigits(2);

					val = nf.parse(nf.format(val)).doubleValue();

					rrfMeasure.setValue(val);

					rrfAssetType.getRrfMeasures().add(rrfMeasure);

				} else if (measure instanceof AssetMeasure) {

					AssetMeasure assetMeasure = (AssetMeasure) measure;

					List<MeasureAssetValue> mavs = assetMeasure.getMeasureAssetValueByAssetType(atv.getAssetType());

					if (!mavs.isEmpty()) {

						for (MeasureAssetValue mav : mavs) {

							double val = RRF.calculateAssetMeasureRRF(scenario, mav.getAsset(), parameter, (AssetMeasure) measure);

							NumberFormat nf = new DecimalFormat();
							nf.setMaximumFractionDigits(2);

							val = Double.valueOf(nf.format(val));

							rrfMeasure.setValue(rrfMeasure.getValue() + val);
						}
						rrfAssetType.getRrfMeasures().add(rrfMeasure);
					} else
						rrfs.remove(atv.getAssetType().getType());
				}
			}
		}

		return rrfs;
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

		return ("{" + jsonChart + "," + jsonTitle + "," + jsonLegend + "," + jsonPane + "," + jsonPlotOptions + "," + jsonXAxis + "," + jsonYAxis + "," + jsonSeries + ", "
				+ exporting + "}").replaceAll("\r|\n", " ");
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
		return aleEvolution(analysis, assessments, locale, a -> a.getAsset().getAssetType(), t -> t.getType(),
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
		final List<Assessment> assessments = analysis.getAssessments().stream().filter(a -> a.getAsset().getAssetType().getType().equals(assetType)).collect(Collectors.toList());
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
					.getMessage("label.title.chart.aleevolution_of_asset_type", new Object[] { assetType.getType() }, "ALE Evolution of all {0}-type assets", locale)));
		return "[" + String.join(", ", graphs) + "]";
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

		return ("{" + jsonChart + "," + jsonTitle + "," + jsonLegend + "," + jsonPane + "," + jsonPlotOptions + "," + jsonXAxis + "," + jsonYAxis + "," + jsonSeries + ", "
				+ exporting + "}").replaceAll("\r|\n", " ");
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
}

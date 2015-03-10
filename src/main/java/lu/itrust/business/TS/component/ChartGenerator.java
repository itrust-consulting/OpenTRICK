package lu.itrust.business.TS.component;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.data.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.data.actionplan.ActionPlanMode;
import lu.itrust.business.TS.data.actionplan.helper.ActionPlanComputation;
import lu.itrust.business.TS.data.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.data.actionplan.summary.helper.ActionPlanSummaryManager;
import lu.itrust.business.TS.data.assessment.Assessment;
import lu.itrust.business.TS.data.assessment.helper.ALE;
import lu.itrust.business.TS.data.assessment.helper.AssetComparatorByALE;
import lu.itrust.business.TS.data.asset.AssetType;
import lu.itrust.business.TS.data.general.AssetTypeValue;
import lu.itrust.business.TS.data.general.Phase;
import lu.itrust.business.TS.data.parameter.Parameter;
import lu.itrust.business.TS.data.rrf.RRF;
import lu.itrust.business.TS.data.rrf.RRFAsset;
import lu.itrust.business.TS.data.rrf.RRFAssetType;
import lu.itrust.business.TS.data.rrf.RRFMeasure;
import lu.itrust.business.TS.data.scenario.Scenario;
import lu.itrust.business.TS.data.standard.AnalysisStandard;
import lu.itrust.business.TS.data.standard.measure.AssetMeasure;
import lu.itrust.business.TS.data.standard.measure.Measure;
import lu.itrust.business.TS.data.standard.measure.MeasureAssetValue;
import lu.itrust.business.TS.data.standard.measure.NormalMeasure;
import lu.itrust.business.TS.database.dao.DAOActionPlan;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOAnalysisStandard;
import lu.itrust.business.TS.database.dao.DAOAssessment;
import lu.itrust.business.TS.database.dao.DAOAsset;
import lu.itrust.business.TS.database.dao.DAOAssetType;
import lu.itrust.business.TS.database.dao.DAOMeasure;
import lu.itrust.business.TS.database.dao.DAOParameter;
import lu.itrust.business.TS.database.dao.DAOPhase;
import lu.itrust.business.TS.database.dao.DAOScenario;
import lu.itrust.business.TS.exception.TrickException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

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
	private DAOAsset daoAsset;

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
		Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales2 = new LinkedList<ALE>();
		for (Assessment assessment : assessments) {
			ALE ale = ales.get(assessment.getAsset().getId());
			if (ale == null) {
				ales.put(assessment.getAsset().getId(), ale = new ALE(assessment.getAsset().getName(), 0));
				ales2.add(ale);
			}
			ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
		}
		Collections.sort(ales2, new AssetComparatorByALE());

		String chart = "\"chart\":{ \"type\":\"column\",  \"zoomType\": \"y\", \"marginTop\": 50},  \"scrollbar\": {\"enabled\": true}";

		String title = "\"title\": {\"text\":\"" + messageSource.getMessage("label.title.chart.ale_by_asset", null, "ALE by Asset", locale) + "\"}";

		String pane = "\"pane\": {\"size\": \"100%\"}";

		String legend = "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\"}";

		String plotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0 }}";

		String tooltip = "\"tooltip\": { \"valueDecimals\": 2, \"valueSuffix\": \"k&euro;\",\"useHTML\": true }";

		if (ales2.isEmpty())
			return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "}";

		ALE assetMax = ales2.get(0);

		double max = assetMax.getValue();

		String xAxis = "";

		String series = "";

		String categories = "[";

		String dataALEs = "[";

		String yAxis = "\"yAxis\": {\"min\": 0 , \"max\":" + max * 1.1 + ", \"title\": {\"text\": \"ALE\"}, \"labels\":{\"format\": \"{value} k&euro;\",\"useHTML\": true}}";

		for (ALE ale : ales2) {
			categories += "\"" + ale.getAssetName() + "\",";
			dataALEs += ale.getValue() + ",";
		}

		if (categories.endsWith(",")) {
			categories = categories.substring(0, categories.length() - 1);
			dataALEs = dataALEs.substring(0, dataALEs.length() - 1);
		}
		categories += "]";
		dataALEs += "]";

		if (ales2.size() >= 10)
			xAxis = "\"xAxis\":{\"categories\":" + categories + ", \"min\":\"0\", \"max\":\"9\"}";
		else
			xAxis = "\"xAxis\":{\"categories\":" + categories + ", \"min\":\"0\", \"max\":\"" + (ales2.size() - 1) + "\"}";

		series += "\"series\":[{\"name\":\"ALE\", \"data\":" + dataALEs + ",\"valueDecimals\": 0}]";

		ales.clear();

		ales2.clear();

		assessments.clear();

		return ("{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "," + xAxis + "," + yAxis + "," + series + "," + exporting + "}")
				.replaceAll("\r|\n", " ");

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
		Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales2 = new LinkedList<ALE>();
		for (Assessment assessment : assessments) {
			ALE ale = ales.get(assessment.getAsset().getAssetType().getId());
			if (ale == null) {
				ales.put(assessment.getAsset().getAssetType().getId(), ale = new ALE(assessment.getAsset().getAssetType().getType(), 0));
				ales2.add(ale);
			}
			ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
		}
		Collections.sort(ales2, new AssetComparatorByALE());

		String chart = "\"chart\":{ \"type\":\"column\",  \"zoomType\": \"y\", \"marginTop\": 50},  \"scrollbar\": {\"enabled\": true}";

		String title = "\"title\": {\"text\":\"" + messageSource.getMessage("label.title.chart.ale_by_asset_type", null, "ALE by Asset Type", locale) + "\"}";

		String pane = "\"pane\": {\"size\": \"100%\"}";

		String legend = "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\"}";

		String plotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0 }}";

		String tooltip = "\"tooltip\": { \"valueDecimals\": 2, \"valueSuffix\": \"k&euro;\",\"useHTML\": true }";

		if (ales2.isEmpty())
			return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "}";

		ALE assetMax = ales2.get(0);

		double max = assetMax.getValue();

		String xAxis = "";

		String series = "";

		String categories = "[";

		String dataALEs = "[";

		String yAxis = "\"yAxis\": {\"min\": 0 , \"max\":" + max * 1.1 + ", \"title\": {\"text\": \"ALE\"}, \"labels\":{\"format\": \"{value} k&euro;\",\"useHTML\": true}}";

		for (ALE ale : ales2) {
			categories += "\"" + ale.getAssetName() + "\",";
			dataALEs += ale.getValue() + ",";
		}

		if (categories.endsWith(",")) {
			categories = categories.substring(0, categories.length() - 1);
			dataALEs = dataALEs.substring(0, dataALEs.length() - 1);
		}
		categories += "]";
		dataALEs += "]";

		if (ales2.size() >= 10)
			xAxis = "\"xAxis\":{\"categories\":" + categories + ", \"min\":\"0\", \"max\":\"9\"}";
		else
			xAxis = "\"xAxis\":{\"categories\":" + categories + ", \"min\":\"0\", \"max\":\"" + (ales2.size() - 1) + "\"}";

		series += "\"series\":[{\"name\":\"ALE\", \"data\":" + dataALEs + "}]";

		ales.clear();

		ales2.clear();

		assessments.clear();

		return ("{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "," + xAxis + "," + yAxis + "," + series + "," + exporting + "}")
				.replaceAll("\r|\n", " ");
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
		Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales2 = new LinkedList<ALE>();
		for (Assessment assessment : assessments) {
			ALE ale = ales.get(assessment.getScenario().getType().getValue());
			if (ale == null) {
				ales.put(assessment.getScenario().getType().getValue(), ale = new ALE(assessment.getScenario().getType().getName(), 0));
				ales2.add(ale);
			}
			ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
		}
		Collections.sort(ales2, new AssetComparatorByALE());

		String chart = "\"chart\":{ \"type\":\"column\",  \"zoomType\": \"y\", \"marginTop\": 50},  \"scrollbar\": {\"enabled\": true}";

		String title = "\"title\": {\"text\":\"" + messageSource.getMessage("label.title.chart.ale_by_scenario_type", null, "ALE by Scenario Type", locale) + "\"}";

		String pane = "\"pane\": {\"size\": \"100%\"}";

		String legend = "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\"}";

		String plotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0 }}";

		String tooltip = "\"tooltip\": { \"valueDecimals\": 2, \"valueSuffix\": \"k&euro;\",\"useHTML\": true }";

		if (ales2.isEmpty())
			return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "}";

		ALE assetMax = ales2.get(0);

		double max = assetMax.getValue();

		String xAxis = "";

		String series = "";

		String categories = "[";

		String dataALEs = "[";

		String yAxis = "\"yAxis\": {\"min\": 0 , \"max\":" + max * 1.1 + ", \"title\": {\"text\": \"ALE\"},\"labels\":{\"format\": \"{value} k&euro;\",\"useHTML\": true}}";

		for (ALE ale : ales2) {
			categories += "\"" + ale.getAssetName() + "\",";
			dataALEs += ale.getValue() + ",";
		}

		if (categories.endsWith(",")) {
			categories = categories.substring(0, categories.length() - 1);
			dataALEs = dataALEs.substring(0, dataALEs.length() - 1);
		}
		categories += "]";
		dataALEs += "]";

		if (ales2.size() >= 10)
			xAxis = "\"xAxis\":{\"categories\":" + categories + ", \"min\":\"0\", \"max\":\"9\"}";
		else
			xAxis = "\"xAxis\":{\"categories\":" + categories + ", \"min\":\"0\", \"max\":\"" + (ales2.size() - 1) + "\"}";

		series += "\"series\":[{\"name\":\"ALE\", \"data\":" + dataALEs + "}]";

		ales.clear();

		ales2.clear();

		assessments.clear();

		return ("{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "," + xAxis + "," + yAxis + "," + series + "," + exporting + "}")
				.replaceAll("\r|\n", " ");
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
		Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales2 = new LinkedList<ALE>();
		for (Assessment assessment : assessments) {
			ALE ale = ales.get(assessment.getScenario().getId());
			if (ale == null) {
				ales.put(assessment.getScenario().getId(), ale = new ALE(assessment.getScenario().getName(), 0));
				ales2.add(ale);
			}
			ale.setValue(assessment.getALE() * 0.001 + ale.getValue());
		}
		Collections.sort(ales2, new AssetComparatorByALE());

		String chart = "\"chart\":{ \"type\":\"column\",  \"zoomType\": \"y\", \"marginTop\": 50},  \"scrollbar\": {\"enabled\": true}";

		String title = "\"title\": {\"text\":\"" + messageSource.getMessage("label.title.chart.ale_by_scenario", null, "ALE by Scenario", locale) + "\"}";

		String pane = "\"pane\": {\"size\": \"100%\"}";

		String legend = "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\"}";

		String plotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0 }}";

		String tooltip = "\"tooltip\": { \"valueDecimals\": 2, \"valueSuffix\": \"k€\",\"useHTML\": true }";

		if (ales2.isEmpty())
			return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "}";

		ALE assetMax = ales2.get(0);

		double max = assetMax.getValue();

		String xAxis = "";

		String series = "";

		String categories = "[";

		String dataALEs = "[";

		String yAxis = "\"yAxis\": {\"min\": 0 , \"max\":" + max * 1.1 + ", \"title\": {\"text\": \"ALE\"},\"labels\":{\"format\": \"{value} k&euro;\",\"useHTML\": true}}";

		for (ALE ale : ales2) {
			categories += "\"" + ale.getAssetName() + "\",";
			dataALEs += ale.getValue() + ",";
		}

		if (categories.endsWith(",")) {
			categories = categories.substring(0, categories.length() - 1);
			dataALEs = dataALEs.substring(0, dataALEs.length() - 1);
		}
		categories += "]";
		dataALEs += "]";

		if (ales2.size() >= 10)
			xAxis = "\"xAxis\":{\"categories\":" + categories + ", \"min\":\"0\", \"max\":\"9\"}";
		else
			xAxis = "\"xAxis\":{\"categories\":" + categories + ", \"min\":\"0\", \"max\":\"" + (ales2.size() - 1) + "\"}";

		series += "\"series\":[{\"name\":\"ALE\", \"data\":" + dataALEs + "}]";

		ales.clear();

		ales2.clear();

		assessments.clear();

		return ("{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "," + xAxis + "," + yAxis + "," + series + "," + exporting + "}")
				.replaceAll("\r|\n", " ");
	}

	/**
	 * ComputeComplianceBefore: <br>
	 * Description
	 * 
	 * @param measures
	 * @return
	 */
	public static Map<String, Object[]> ComputeComplianceBefore(List<? extends Measure> measures) {
		Map<String, Object[]> compliances = new LinkedHashMap<String, Object[]>();
		for (Measure measure : measures) {
			if (measure.getMeasureDescription().isComputable()) {
				String chapter = ActionPlanComputation.extractMainChapter(measure.getMeasureDescription().getReference());
				Object[] compliance = compliances.get(chapter);
				if (compliance == null)
					compliances.put(chapter, compliance = new Object[] { 0, 0.0 });

				if (!measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)) {
					compliance[1] = (Double) compliance[1] + measure.getImplementationRateValue();
					compliance[0] = (Integer) compliance[0] + 1;
				}
				
				/*
				 * else compliance[1] = (Double) compliance[1] +
				 * Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE;
				 */
			}
		}
		return compliances;
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
	public static Map<String, Object[]> ComputeCompliance(List<Measure> measures, Phase phase, Map<Integer, Boolean> actionPlanMeasures, Map<String, Object[]> previouscompliences) {
		Map<String, Object[]> compliances = previouscompliences;

		for (Measure measure : measures) {

			if (measure.getPhase().getNumber() == phase.getNumber() && measure.getMeasureDescription().isComputable()
					&& !measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)) {
				String chapter = ActionPlanComputation.extractMainChapter(measure.getMeasureDescription().getReference());
				Object[] compliance = compliances.get(chapter);
				if (actionPlanMeasures.containsKey(measure.getId()))
					compliance[1] = ((Double) compliance[1] + (Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE) - measure.getImplementationRateValue());
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

		Map<String, Object[]> previouscompliances = ComputeComplianceBefore(measures);

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

		// Hibernate.initialize(phases);

		if (!actionPlanMeasures.isEmpty()) {

			for (Phase phase : phases) {

				// Hibernate.initialize(phase);

				if (phase.getNumber() == Constant.PHASE_NOT_USABLE)
					continue;

				Map<String, Object[]> compliances = null;

				compliances = ComputeCompliance(measures, phase, actionPlanMeasures, previouscompliances);

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
	public String evolutionProfitabilityCompliance(Integer idAnalysis, List<SummaryStage> summaryStages, List<Phase> phases, String actionPlanType, Locale locale) throws Exception {

		Map<String, List<String>> summaries = ActionPlanSummaryManager.buildTable(summaryStages, phases);

		String chart = "\"chart\":{ \"type\":\"column\",  \"zoomType\": \"xy\", \"marginTop\": 50},  \"scrollbar\": {\"enabled\": false}";

		String title = "\"title\": {\"text\":\""
				+ messageSource.getMessage("label.title.chart.evolution_profitability_compliance." + actionPlanType.toLowerCase(), null,
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

		List<String> dataROSIs = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI);

		List<String> dataRelatifROSIs = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI_RELATIF);

		int size = dataALEs.size() - 1;

		for (int i = 0; i < usesPhases.size(); i++) {
			ale += dataALEs.get(i) + (size != i ? "," : "]");
			riskReduction += dataRiskReductions.get(i) + (size != i ? "," : "]");
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

		String yAxis = "\"yAxis\": [{\"min\": 0, \"labels\":{\"format\": \"{value} " + keuroByYear + "\",\"useHTML\": true}, \"title\": {\"text\":\""
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
				+ ",\"valueDecimals\": 0,\"type\": \"line\"},{\"name\":\"" + messageSource.getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI, null, "ROSI", locale)
				+ "\", \"data\":" + rosi + ",\"valueDecimals\": 0,\"type\": \"line\"},{\"name\":\""
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

		List<String> dataCurrentCost = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_RECURRENT_COST);

		List<String> dataTotalPhaseCost = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST);

		int size = dataInternalMaintenace.size() - 1;

		for (int i = 0; i < dataInternalWorkload.size(); i++) {
			internalWorkload += dataInternalWorkload.get(i) + (size != i ? "," : "]");
			externalWorkload += dataExternalWorkload.get(i) + (size != i ? "," : "]");
			internalMaintenace += dataInternalMaintenace.get(i) + (size != i ? "," : "]");
			externalMaintenance += dataExternalMaintenance.get(i) + (size != i ? "," : "]");
			investment += dataInvestment.get(i) + (size != i ? "," : "]");
			currentCost += dataCurrentCost.get(i) + (size != i ? "," : "]");
			totalPhaseCost += dataTotalPhaseCost.get(i) + (size != i ? "," : "]");
		}

		if (!internalWorkload.endsWith("]")) {
			internalWorkload += "]";
			externalWorkload += "]";
			internalMaintenace += "]";
			externalMaintenance += "]";
			investment += "]";
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
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST, null, "Total phase cost", locale) + "\", \"data\":" + totalPhaseCost
				+ ",\"valueDecimals\": 0,\"type\": \"line\"},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INVESTMENT, null, "Investment", locale) + "\", \"data\":" + investment
				+ ",\"valueDecimals\": 0,\"type\": \"line\"},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_RECURRENT_COST, null, "Current cost", locale) + "\", \"data\":" + currentCost
				+ ",\"valueDecimals\": 0,\"type\": \"line\"}]";

		return ("{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + xAxis + "," + yAxis + "," + series + ", " + exporting + "}").replaceAll("\r|\n",
				" ");
	}

	public String rrfByScenario(int idScenario, int idAnalysis, List<Measure> measures, Locale locale) throws Exception {
		Scenario scenario = daoScenario.getFromAnalysisById(idAnalysis, idScenario);
		if (scenario == null)
			return null;
		Locale customLocale = new Locale(daoAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
		return rrfByScenario(scenario, idAnalysis, measures, customLocale != null ? customLocale : locale);
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
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			e.printStackTrace();
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

			String title = "\"title\": {\"text\":\""
					+ messageSource.getMessage("label.title.chart.rff.measure", new String[] { measure.getMeasureDescription().getReference() }, "RRF by measure ("
							+ measure.getMeasureDescription().getReference() + ")", locale) + "\"}";

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
			e.printStackTrace();
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
		Parameter parameter = daoParameter.getFromAnalysisByTypeAndDescription(idAnalysis, Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, Constant.PARAMETER_MAX_RRF);
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
		Parameter parameter = daoParameter.getFromAnalysisByTypeAndDescription(idAnalysis, Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, Constant.PARAMETER_MAX_RRF);
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
		Parameter parameter = daoParameter.getFromAnalysisByTypeAndDescription(idAnalysis, Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, Constant.PARAMETER_MAX_RRF);
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

					AssetTypeValue matv = normalMeasure.getAssetTypeValueByAssetType(atv.getAssetType());

					double val = RRF.calculateNormalMeasureRRF(scenario, matv.getAssetType(), parameter, normalMeasure);

					NumberFormat nf = new DecimalFormat();

					nf.setMaximumFractionDigits(2);

					val = nf.parse(nf.format(val)).doubleValue();

					rrfMeasure.setValue(val);

					rrfAssetType.getRrfMeasures().add(rrfMeasure);

				} else if (measure instanceof AssetMeasure) {

					AssetMeasure assetMeasure = (AssetMeasure) measure;

					List<MeasureAssetValue> mavs = assetMeasure.getMeasureAssetValueByAssetType(atv.getAssetType());

					if (mavs.size() == 0)
						throw new TrickException("error.rrf.assetmeasure.no_assets", "Measure '" + assetMeasure.getMeasureDescription().getReference()
								+ "' does not have any assets of this asset type!", assetMeasure.getMeasureDescription().getReference());

					for (MeasureAssetValue mav : mavs) {

						double val = RRF.calculateAssetMeasureRRF(scenario, mav.getAsset(), parameter, (AssetMeasure) measure);

						NumberFormat nf = new DecimalFormat();
						nf.setMaximumFractionDigits(2);

						val = Double.valueOf(nf.format(val));

						rrfMeasure.setValue(rrfMeasure.getValue() + val);
					}
					rrfAssetType.getRrfMeasures().add(rrfMeasure);
				}
			}
		}

		return rrfs;
	}

}

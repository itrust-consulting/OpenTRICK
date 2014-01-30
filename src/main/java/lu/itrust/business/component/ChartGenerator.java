/**
 * 
 */
package lu.itrust.business.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Phase;
import lu.itrust.business.TS.actionplan.ActionPlanComputation;
import lu.itrust.business.TS.actionplan.ActionPlanMode;
import lu.itrust.business.TS.actionplan.SummaryStage;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.dao.DAOActionPlan;
import lu.itrust.business.dao.DAOAssessment;
import lu.itrust.business.dao.DAOAsset;
import lu.itrust.business.dao.DAOMeasure;
import lu.itrust.business.dao.DAOPhase;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * @author eomar
 * 
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

	public String aleByAsset(int idAnalysis, Locale locale) {

		List<Asset> assets = daoAsset.findByAnalysisAndSelectedOderByALE(idAnalysis);

		String chart = "\"chart\":{ \"type\":\"column\",  \"zoomType\": \"y\"},  \"scrollbar\": {\"enabled\": true}";

		String title = "\"title\": {\"text\":\"" + messageSource.getMessage("label.title.chart.ale_by_asset", null, "ALE by Asset", locale) + "\"}";

		String pane = "\"pane\": {\"size\": \"100%\"}";

		String legend = "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\"}";

		String plotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0 }}";

		String tooltip =
			"\"tooltip\": {\"headerFormat\": \"<span style='font-size:10px'>{point.key}</span><table>\", \"pointFormat\": \"<tr><td style='color:{series.color};padding:0;'>{series.name}: </td><td style='padding:0;min-width:120px;'><b>{point.y:.1f} k&euro;</b></td></tr>\",\"footerFormat\": \"</table>\", \"shared\": true, \"useHTML\": true }";

		if (assets.isEmpty())
			return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "}";

		Asset assetMax = assets.get(assets.size() - 1);

		double max = Math.max(assetMax.getALEO(), Math.max(assetMax.getALE(), assetMax.getALEP()));

		String xAxis = "";

		String series = "";

		String categories = "[";

		String dataALEs = "[";

		String dataALEOs = "[";

		String dataALEPs = "[";

		String yAxis = "\"yAxis\": {\"min\": 0 , \"max\":" + max * 1.1 + ", \"title\": {\"text\": \"ALE\"}}";

		Collections.reverse(assets);

		for (Asset asset : assets) {
			categories += "\"" + asset.getName() + "\",";
			dataALEs += asset.getALE() + ",";
			dataALEOs += asset.getALEO() + ",";
			dataALEPs += asset.getALEP() + ",";
		}

		if (categories.endsWith(",")) {
			categories = categories.substring(0, categories.length() - 1);
			dataALEs = dataALEs.substring(0, dataALEs.length() - 1);
			dataALEOs = dataALEOs.substring(0, dataALEOs.length() - 1);
			dataALEPs = dataALEPs.substring(0, dataALEPs.length() - 1);
		}
		categories += "]";
		dataALEs += "]";
		dataALEOs += "]";
		dataALEPs += "]";

		xAxis = "\"xAxis\":{\"categories\":" + categories + ", \"min\": " + assets.size() % 10 + "}";
		series += "\"series\":[{\"name\":\"ALEO\", \"data\":" + dataALEOs + ",\"valueDecimals\": 0},{\"name\":\"ALE\", \"data\":" + dataALEs
				+ ",\"valueDecimals\": 0},{\"name\":\"ALEP\", \"data\":" + dataALEPs + ",\"valueDecimals\": 0}]";
		return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "," + xAxis + "," + yAxis + "," + series + "}";
	}

	private List<Asset> assetByType(List<Asset> assets) {
		Map<Integer, Asset> assetbytypes = new LinkedHashMap<Integer, Asset>();
		for (Asset asset : assets) {
			Asset asset2 = assetbytypes.get(asset.getAssetType().getId());
			if (asset2 == null) {
				assetbytypes.put(asset.getAssetType().getId(), asset2 = new Asset());
				asset2.setAssetType(asset.getAssetType());
				asset2.setName(asset.getAssetType().getType());
			}
			asset2.setALE(asset2.getALE() + asset.getALE());
			asset2.setALEO(asset2.getALEO() + asset.getALEO());
			asset2.setALEP(asset2.getALEP() + asset.getALEP());
			asset2.setValue(asset2.getValue() + asset.getValue());
		}

		List<Asset> assets2 = new ArrayList<Asset>(assetbytypes.size());

		for (Asset asset : assetbytypes.values())
			assets2.add(asset);

		return assets2;
	}

	public String aleByAssetType(int idAnalysis, Locale locale) {

		List<Asset> assets = assetByType(daoAsset.findByAnalysisAndSelectedOderByALE(idAnalysis));

		String chart = "\"chart\":{ \"type\":\"column\",  \"zoomType\": \"y\"},  \"scrollbar\": {\"enabled\": false}";

		String title = "\"title\": {\"text\":\"" + messageSource.getMessage("label.title.chart.ale_by_asset_type", null, "ALE by Asset Type", locale) + "\"}";

		String pane = "\"pane\": {\"size\": \"100%\"}";

		String legend = "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\"}";

		String plotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0 }}";

		String tooltip =
			"\"tooltip\": {\"headerFormat\": \"<span style='font-size:10px'>{point.key}</span><table>\", \"pointFormat\": \"<tr><td style='color:{series.color};padding:0;'>{series.name}: </td><td style='padding:0;min-width:120px;'><b>{point.y:.1f} k&euro;</b></td></tr>\",\"footerFormat\": \"</table>\", \"shared\": true, \"useHTML\": true }";

		if (assets.isEmpty())
			return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "}";

		double max = 0;

		String xAxis = "";

		String series = "";

		String categories = "[";

		String dataALEs = "[";

		String dataALEOs = "[";

		String dataALEPs = "[";

		for (Asset asset : assets) {
			categories += "\"" + asset.getName() + "\",";
			dataALEs += asset.getALE() + ",";
			dataALEOs += asset.getALEO() + ",";
			dataALEPs += asset.getALEP() + ",";
			max = Math.max(asset.getALEO(), Math.max(asset.getALE(), Math.max(asset.getALEP(), max)));
		}

		if (categories.endsWith(",")) {
			categories = categories.substring(0, categories.length() - 1);
			dataALEs = dataALEs.substring(0, dataALEs.length() - 1);
			dataALEOs = dataALEOs.substring(0, dataALEOs.length() - 1);
			dataALEPs = dataALEPs.substring(0, dataALEPs.length() - 1);
		}
		categories += "]";
		dataALEs += "]";
		dataALEOs += "]";
		dataALEPs += "]";

		String yAxis = "\"yAxis\": {\"min\": 0 , \"max\":" + max * 1.1 + ", \"title\": {\"text\": \"ALE\"}}";

		xAxis = "\"xAxis\":{\"categories\":" + categories + " ,\"min\":0}";
		series += "\"series\":[{\"name\":\"ALEO\", \"data\":" + dataALEOs + ",\"valueDecimals\": 0 },{\"name\":\"ALE\", \"data\":" + dataALEs

				+ ",\"valueDecimals\": 0 },{\"name\":\"ALEP\", \"data\":" + dataALEPs + ",\"valueDecimals\": 0}]";
		return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "," + xAxis + "," + yAxis + "," + series + "}";
	}

	public static Map<String, Object[]> ComputeComplianceBefore(List<Measure> measures, String norm) {
		Map<String, Object[]> compliances = new LinkedHashMap<String, Object[]>();
		for (Measure measure : measures) {
			if (measure.getAnalysisNorm().getNorm().getLabel().equals(norm) && measure.getMeasureDescription().getLevel() >= 3
				&& !measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)) {
				String chapter = ActionPlanComputation.extractMainChapter(measure.getMeasureDescription().getReference());
				Object[] compliance = compliances.get(chapter);
				if (compliance == null)
					compliances.put(chapter, compliance = new Object[] { 0, 0.0 });
				compliance[0] = (Integer) compliance[0] + 1;
				compliance[1] = (Double) compliance[1] + measure.getImplementationRateValue();
			}
		}
		return compliances;
	}
	
	public static Map<String, Object[]> ComputeCompliance(List<Measure> measures, String norm, List<Measure> actionplanmeasures, List<Measure> actionplanmeasuresnottoimpl, Phase phase, Map<String, Object[]> previouscompliences) {
		Map<String, Object[]> compliances = previouscompliences;
		
		
		for (Measure measure : measures) {
						
			//System.out.println(measure.getPhase().getNumber() + "::" + phase.getNumber());
			
			String normname = measure.getAnalysisNorm().getNorm().getLabel();
			
			Integer measureLevel = measure.getMeasureDescription().getLevel();
			
			String status = measure.getStatus();
							
			Boolean goodPhase = measure.getPhase().getNumber() == phase.getNumber();
			
			if (normname.equals(norm) && measureLevel >= Constant.MEASURE_LEVEL_3 && !status.equals(Constant.MEASURE_STATUS_NOT_APPLICABLE) && goodPhase) {
				String chapter = ActionPlanComputation.extractMainChapter(measure.getMeasureDescription().getReference());
				Object[] compliance = compliances.get(chapter);
				if (compliance == null)
					compliances.put(chapter, compliance = new Object[] { 0, 0.0 });
				//compliance[0] = (Integer) compliance[0] + 1;

				Boolean onActionPlan = actionplanmeasures.contains(measure) && !actionplanmeasuresnottoimpl.contains(measure);
				
				if (onActionPlan) {
					compliance[1] = (Double) compliance[1] + Constant.MEASURE_IMPLEMENTATIONRATE_COMPLETE;
					compliance[1] = (Double) compliance[1] - measure.getImplementationRateValue();
				}
				
			}
		}
		return compliances;
	}
	
	public String compliance(int idAnalysis, String norm, Locale locale) throws Exception {
		List<Measure> measures = daoMeasure.findByAnalysis(idAnalysis);
		
		Map<String, Object[]> previouscompliances = ComputeComplianceBefore(measures, norm);

		String chart = "\"chart\":{ \"polar\":true, \"type\":\"line\",\"marginBottom\": 30},  \"scrollbar\": {\"enabled\": false}";

		String title = "\"title\": {\"text\":\"" + messageSource.getMessage("label.title.chart.measure.compliance", new Object[] { norm }, norm + " measure compliance", locale) + "\"}";

		String pane = "\"pane\": {\"size\": \"100%\"}";

		String legend = "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\"}";

		String plotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0 }}";

		String tooltip =
			"\"tooltip\": {\"headerFormat\": \"<span style='font-size:10px'>{point.key}</span><table>\", \"pointFormat\": \"<tr><td style='color:{series.color};padding:0;'>{series.name}: </td><td style='padding:0;min-width:70px;'><b>{point.y:.1f} %</b></td></tr>\",\"footerFormat\": \"</table>\", \"shared\": true, \"useHTML\": true }";


		if (previouscompliances.isEmpty())
			return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "}";

		String series = "\"series\":[";

		String xAxis = "";

		String yAxis = "\"yAxis\": {\"gridLineInterpolation\": \"polygon\" , \"lineWidth\":0,\"min\":0,\"max\":100}";

		String categories = "[";

		String data = "[";
		
		for (String key : previouscompliances.keySet()) {
			Object[] compliance = previouscompliances.get(key);
			categories += "\"" + key + "\",";
			data += ((Double) compliance[1]) / (Integer) compliance[0] + ",";
		}

		if (categories.endsWith(",")) {
			categories = categories.substring(0, categories.length() - 1);
			data = data.substring(0, data.length() - 1);
		}

		categories += "]";

		data += "]";

		xAxis = "\"xAxis\":{\"categories\":" + categories + "}";

		String serie = "";
		
		serie = "{\"name\":\"" + messageSource.getMessage("label.chart.series.currentlevel", null, "Current Level", locale) + "\", \"data\":" + data + ",\"valueDecimals\": 0}";
		
		series += serie;
	
		List<Measure> actionplanmeasures = daoActionPlan.loadMeasuresFromAnalysisActionPlan(idAnalysis, ActionPlanMode.PHASE_NORMAL);

		List<Measure> actionplanmeasuresnottoimplement = daoActionPlan.loadMeasuresFromAnalysisActionPlanNotToImplement(idAnalysis, ActionPlanMode.PHASE_NORMAL);

		
		List<Phase> phases = daoPhase.loadAllFromAnalysis(idAnalysis);
		
		Hibernate.initialize(phases);
		
		if (actionplanmeasures != null && actionplanmeasures.size()>0) {
				
			for (Phase phase : phases) {
										
				Hibernate.initialize(phase);
				
				if (phase.getNumber() == Constant.PHASE_NOT_USABLE)
					continue;
				
				Map<String, Object[]> compliances = null; 
				
				/*for (String key : previouscompliances.keySet()) {
					Object[] compliance = previouscompliances.get(key);
					compliance[0] = 0;
				}*/
				
				compliances = ComputeCompliance(measures, norm, actionplanmeasures, actionplanmeasuresnottoimplement, phase, previouscompliances);
								
				previouscompliances = compliances;
								
				if (compliances.size() == 0) 
					continue;

				data = "[";
				
				for (String key : compliances.keySet()) {
					Object[] compliance = compliances.get(key);
					data += ((Double) compliance[1]) / (Integer) compliance[0] + ",";
				}
		
				data = data.substring(0, data.length() - 1);
				
				data += "]";
				
				serie = "";
				
				serie = "{\"name\":\"" + messageSource.getMessage("label.phase", null, "Phase", locale) + " "+ phase.getNumber() +"\", \"data\":" + data + ",\"valueDecimals\": 0}";

				series += ","+serie;
			
			}
					
		}
		
		series += "]";
		
		return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "," + xAxis + "," + yAxis + "," + series + "}";
	}

	public String aleByScenarioType(Integer idAnalysis, Locale locale) {
		List<Assessment> assessments = daoAssessment.findByAnalysisAndSelectedScenario(idAnalysis);
		Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales2 = new LinkedList<ALE>();
		for (Assessment assessment : assessments) {
			ALE ale = ales.get(assessment.getScenario().getType().getId());
			if (ale == null) {
				ales.put(assessment.getScenario().getType().getId(), ale = new ALE(assessment.getScenario().getType().getTypeName(), 0));
				ales2.add(ale);
			}
			ale.setValue(assessment.getALE() + ale.getValue());
		}
		Collections.sort(ales2, new AssetComparatorByALE());

		String chart = "\"chart\":{ \"type\":\"column\",  \"zoomType\": \"y\"},  \"scrollbar\": {\"enabled\": true}";

		String title = "\"title\": {\"text\":\"" + messageSource.getMessage("label.title.chart.ale_by_scenario_type", null, "ALE by Scenario Type", locale) + "\"}";

		String pane = "\"pane\": {\"size\": \"100%\"}";

		String legend = "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\"}";

		String plotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0 }}";

		String tooltip =
			"\"tooltip\": {\"headerFormat\": \"<span style='font-size:10px'>{point.key}</span><table>\", \"pointFormat\": \"<tr><td style='color:{series.color};padding:0;'>{series.name}: </td><td style='padding:0;min-width:120px;'><b>{point.y:.1f} k&euro;</b></td></tr>\",\"footerFormat\": \"</table>\", \"shared\": true, \"useHTML\": true }";

		if (ales2.isEmpty())
			return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "}";

		ALE assetMax = ales2.get(0);

		double max = assetMax.getValue();

		String xAxis = "";

		String series = "";

		String categories = "[";

		String dataALEs = "[";

		String yAxis = "\"yAxis\": {\"min\": 0 , \"max\":" + max * 1.1 + ", \"title\": {\"text\": \"ALE\"}}";

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
		xAxis = "\"xAxis\":{\"categories\":" + categories + ", \"min\":" + ales2.size() % 3 + "}";
		series += "\"series\":[{\"name\":\"ALE\", \"data\":" + dataALEs + ",\"valueDecimals\": 0}]";

		ales.clear();

		ales2.clear();

		assessments.clear();

		return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "," + xAxis + "," + yAxis + "," + series + "}";
	}

	public String aleByScenario(Integer idAnalysis, Locale locale) {
		List<Assessment> assessments = daoAssessment.findByAnalysisAndSelectedScenario(idAnalysis);
		Map<Integer, ALE> ales = new LinkedHashMap<Integer, ALE>();
		List<ALE> ales2 = new LinkedList<ALE>();
		for (Assessment assessment : assessments) {
			ALE ale = ales.get(assessment.getScenario().getId());
			if (ale == null) {
				ales.put(assessment.getScenario().getId(), ale = new ALE(assessment.getScenario().getName(), 0));
				ales2.add(ale);
			}
			ale.setValue(assessment.getALE() + ale.getValue());
		}
		Collections.sort(ales2, new AssetComparatorByALE());

		String chart = "\"chart\":{ \"type\":\"column\",  \"zoomType\": \"y\"},  \"scrollbar\": {\"enabled\": true}";

		String title = "\"title\": {\"text\":\"" + messageSource.getMessage("label.title.chart.ale_by_scenario", null, "ALE by Scenario", locale) + "\"}";

		String pane = "\"pane\": {\"size\": \"100%\"}";

		String legend = "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\"}";

		String plotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0 }}";

		String tooltip =
			"\"tooltip\": {\"headerFormat\": \"<span style='font-size:10px'>{point.key}</span><table>\", \"pointFormat\": \"<tr><td style='color:{series.color};padding:0;'>{series.name}: </td><td style='padding:0;min-width:120px;'><b>{point.y:.1f} k&euro;</b></td></tr>\",\"footerFormat\": \"</table>\", \"shared\": true, \"useHTML\": true }";

		if (ales2.isEmpty())
			return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "}";

		ALE assetMax = ales2.get(0);

		double max = assetMax.getValue();

		String xAxis = "";

		String series = "";

		String categories = "[";

		String dataALEs = "[";

		String yAxis = "\"yAxis\": {\"min\": 0 , \"max\":" + max * 1.1 + ", \"title\": {\"text\": \"ALE\"}}";

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
		xAxis = "\"xAxis\":{\"categories\":" + categories + ", \"min\":" + ales2.size() % 10 + "}";
		series += "\"series\":[{\"name\":\"ALE\", \"data\":" + dataALEs + ",\"valueDecimals\": 0}]";

		ales.clear();

		ales2.clear();

		assessments.clear();

		return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + tooltip + "," + xAxis + "," + yAxis + "," + series + "}";
	}

	public String evolutionProfitabilityCompliance(List<SummaryStage> summaryStages, List<Phase> phases, String actionPlanType, Locale locale) {

		Map<String, List<String>> summaries = ActionPlanSummaryManager.buildTable(summaryStages, phases);

		String chart = "\"chart\":{ \"type\":\"column\",  \"zoomType\": \"xy\"},  \"scrollbar\": {\"enabled\": false}";

		String title = "\"title\": {\"text\":\""
				+ messageSource.getMessage("label.title.chart.evolution_profitability_compliance."+actionPlanType, null, "Evolution of profitability and ISO compliance for "+actionPlanType, locale) + "\"}";

		String pane = "\"pane\": {\"size\": \"100%\"}";
 
		String legend = "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\"}";

		String plotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0 }}";

		if (summaries.isEmpty())
			return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "}";

		String xAxis = "";

		String series = "";

		String categories = "[";

		String compliance27001 = "[";

		String compliance27002 = "[";

		String ale = "[";

		String riskReduction = "[";

		String rosi = "[";

		String relatifRosi = "[";

		String phaseAnnualCost = "[";

		Map<String, Phase> usesPhases = ActionPlanSummaryManager.buildPhase(phases, ActionPlanSummaryManager.extractPhaseRow(summaryStages));

		for (Phase phase : usesPhases.values())
			categories += "\"P" + phase.getNumber() + "\",";

		List<String> dataCompliance27001s = summaries.get(ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COMPLIANCE_27001);

		List<String> dataCompliance27002s = summaries.get(ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COMPLIANCE_27001);

		List<String> dataALEs = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ALE_UNTIL_END);

		List<String> dataRiskReductions = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_RISK_REDUCTION);

		List<String> dataROSIs = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI);

		List<String> dataRelatifROSIs = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI_RELATIF);

		List<String> dataPhaseAnnualCosts = summaries.get(ActionPlanSummaryManager.LABEL_PROFITABILITY_PHASE_ANNUAL_COST);

		int size = dataALEs.size() - 1;

		for (int i = 0; i < dataCompliance27001s.size(); i++) {
			compliance27001 += dataCompliance27001s.get(i) + (size != i ? "," : "]");
			compliance27002 += dataCompliance27002s.get(i) + (size != i ? "," : "]");
			ale += dataALEs.get(i) + (size != i ? "," : "]");
			riskReduction += dataRiskReductions.get(i) + (size != i ? "," : "]");
			rosi += dataROSIs.get(i) + (size != i ? "," : "]");
			relatifRosi += dataRelatifROSIs.get(i) + (size != i ? "," : "]");
			phaseAnnualCost += dataPhaseAnnualCosts + (size != i ? "," : "]");
		}

		if (!compliance27001.endsWith("]")) {
			compliance27001 += "]";
			compliance27002 += "]";
			ale += "]";
			riskReduction += "]";
			rosi += "]";
			relatifRosi += "]";
			phaseAnnualCost += "]";
		}

		if (categories.endsWith(","))
			categories = categories.substring(0, categories.length() - 1);
		categories += "]";
		
		String keuroByYear = messageSource.getMessage("label.metric.keuro_by_year", null, "k&euro;/y", locale);

		String tooltip = "\"tooltip\": {\"headerFormat\": \"<span style='font-size:10px'>{point.key}</span><table>\", \"pointFormat\": \"<tr><td style='color:{series.color};padding:0;'>{series.name}: </td><td style='padding:0;min-width:120px;'><b>{point.y:.1f}"+keuroByYear+" </b></td></tr>\",\"footerFormat\": \"</table>\", \"useHTML\": true }";

		String yAxis = "\"yAxis\": [{\"labels\":{\"format\": \"{value} "+keuroByYear+"\",\"useHTML\": true}, \"title\": {\"title\":\""
				+ messageSource.getMessage("label.summary.cost", null, "Cost", locale)
				+ "\"}},{\"min\": 0,\"max\": 100, \"labels\":{ \"format\": \"{value}%\"}, \"title\":{\"text\":\""
				+ messageSource.getMessage("label.summary.compliance", null, "Compliance", locale) + "\"}, \"opposite\": true} ]";

		xAxis = "\"xAxis\":{\"categories\":" + categories + "}";
		series += "\"series\":[{\"name\":\"" + messageSource.getMessage(ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COMPLIANCE_27001, null, "Compliance 27001", locale)
				+ "\", \"data\":" + compliance27001 + ",\"valueDecimals\": 0,  \"type\": \"column\",\"yAxis\": 1, \"tooltip\": {\"valueSuffix\": \"%\"}}, {\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_CHARACTERISTIC_COMPLIANCE_27002, null, "Compliance 27002", locale) + "\", \"data\":" + compliance27002
				+ ",\"valueDecimals\": 0,  \"type\": \"column\",\"yAxis\": 1 ,  \"tooltip\": {\"valueSuffix\": \"%\"}},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_ALE_UNTIL_END, null, "ALE", locale) + "\", \"data\":" + ale
				+ ",\"valueDecimals\": 0,\"type\": \"line\" , " + tooltip + "},  {\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_RISK_REDUCTION, null, "Risk reduction", locale) + "\", \"data\":" + riskReduction
				+ ",\"valueDecimals\": 0,\"type\": \"line\" ,  " + tooltip + "},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_PHASE_ANNUAL_COST, null, "Phase annual cost", locale) + "\", \"data\":" + phaseAnnualCost
				+ ",\"valueDecimals\": 0,\"type\": \"line\" ,  " + tooltip + "},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI, null, "ROSI", locale) + "\", \"data\":" + rosi
				+ ",\"valueDecimals\": 0,\"type\": \"line\" , " + tooltip + "},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_PROFITABILITY_ROSI_RELATIF, null, "ROSI relatif", locale) + "\", \"data\":" + relatifRosi
				+ ",\"valueDecimals\": 0,\"type\": \"line\", " + tooltip + "}]";
		return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + xAxis + "," + yAxis + "," + series + "}";
	}
	
	public String budget(List<SummaryStage> summaryStages, List<Phase> phases,String actionPlanType , Locale locale) {

		Map<String, List<String>> summaries = ActionPlanSummaryManager.buildTable(summaryStages, phases);

		String chart = "\"chart\":{ \"type\":\"column\",  \"zoomType\": \"xy\"},  \"scrollbar\": {\"enabled\": false}";

		String title = "\"title\": {\"text\":\""
				+ messageSource.getMessage("label.title.chart.budget."+actionPlanType, null, "Budget for "+actionPlanType, locale) + "\"}";

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

		List<String> dataCurrentCost = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_CURRENT_COST);

		List<String> dataTotalPhaseCost = summaries.get(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST);

		int size = dataInternalMaintenace.size() - 1;

		for (int i = 0; i < dataInternalWorkload.size(); i++) {
			internalWorkload += dataInternalWorkload.get(i) + (size != i ? "," : "]");
			externalWorkload += dataExternalWorkload.get(i) + (size != i ? "," : "]");
			internalMaintenace += dataInternalMaintenace.get(i) + (size != i ? "," : "]");
			externalMaintenance += dataExternalMaintenance.get(i) + (size != i ? "," : "]");
			investment += dataInvestment.get(i) + (size != i ? "," : "]");
			currentCost += dataCurrentCost.get(i) + (size != i ? "," : "]");
			totalPhaseCost += dataTotalPhaseCost + (size != i ? "," : "]");
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

		String tooltip = "\"tooltip\": {\"headerFormat\": \"<span style='font-size:10px'>{point.key}</span><table>\", \"pointFormat\": \"<tr><td style='color:{series.color};padding:0;'>{series.name}: </td><td style='padding:0;min-width:120px;'><b>{point.y:.1f} k&euro;</b></td></tr>\",\"footerFormat\": \"</table>\", \"useHTML\": true }";

		String manDay = messageSource.getMessage("label.metric.man_day", null, "md", locale);
		
		String yAxis = "\"yAxis\": [{\"min\": 0, \"labels\":{\"format\": \"{value} k&euro;\",\"useHTML\": true}, \"title\": {\"title\":\""
				+ messageSource.getMessage("label.summary.cost", null, "Cost", locale)
				+ "\"}},{\"min\": 0,\"max\": 100, \"labels\":{ \"format\": \"{value}"+manDay+"\"}, \"title\":{\"text\":\""
				+ messageSource.getMessage("label.summary.compliance", null, "Compliance", locale) + "\"}, \"opposite\": true} ]";
		

		xAxis = "\"xAxis\":{\"categories\":" + categories + "}";
		series += "\"series\":[{\"name\":\"" + messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INTERNAL_WORKLOAD, null, "Internal workload", locale)
				+ "\", \"data\":" + internalWorkload + ",\"valueDecimals\": 0,  \"type\": \"column\",\"yAxis\": 1, \"tooltip\": {\"valueSuffix\": \""+manDay+"\"}}, {\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_EXTERNAL_WORKLOAD, null, "External workload", locale) + "\", \"data\":" + externalWorkload
				+ ",\"valueDecimals\": 0,  \"type\": \"column\",\"yAxis\": 1 ,  \"tooltip\": {\"valueSuffix\": \""+manDay+"\"}},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INTERNAL_MAINTENANCE, null, "Internal maintenance", locale) + "\", \"data\":" + internalMaintenace
				+ ",\"valueDecimals\": 0,\"type\": \"line\" , " + tooltip + "},  {\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_EXTERNAL_MAINTENANCE, null, "External maintenance", locale) + "\", \"data\":" + externalMaintenance
				+ ",\"valueDecimals\": 0,\"type\": \"line\" ,  " + tooltip + "},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_TOTAL_PHASE_COST, null, "Total phase cost", locale) + "\", \"data\":" + totalPhaseCost
				+ ",\"valueDecimals\": 0,\"type\": \"line\" ,  " + tooltip + "},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_INVESTMENT, null, "Investment", locale) + "\", \"data\":" + investment
				+ ",\"valueDecimals\": 0,\"type\": \"line\" , " + tooltip + "},{\"name\":\""
				+ messageSource.getMessage(ActionPlanSummaryManager.LABEL_RESOURCE_PLANNING_CURRENT_COST, null, "Current cost", locale) + "\", \"data\":" + currentCost
				+ ",\"valueDecimals\": 0,\"type\": \"line\", " + tooltip + "}]";
		return "{" + chart + "," + title + "," + legend + "," + pane + "," + plotOptions + "," + xAxis + "," + yAxis + "," + series + "}";
	}

}

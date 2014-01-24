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
import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.actionplan.ActionPlanMode;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.dao.DAOActionPlan;
import lu.itrust.business.dao.DAOAssessment;
import lu.itrust.business.dao.DAOAsset;
import lu.itrust.business.dao.DAOMeasure;
import lu.itrust.business.dao.DAOPhase;
import lu.itrust.business.service.ServicePhase;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
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
		series +=
			"\"series\":[{\"name\":\"ALEO\", \"data\":" + dataALEOs + ",\"valueDecimals\": 0},{\"name\":\"ALE\", \"data\":" + dataALEs
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
		series +=
			"\"series\":[{\"name\":\"ALEO\", \"data\":" + dataALEOs + ",\"valueDecimals\": 0 },{\"name\":\"ALE\", \"data\":" + dataALEs
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
}

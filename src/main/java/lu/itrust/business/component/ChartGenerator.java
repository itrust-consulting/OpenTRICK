/**
 * 
 */
package lu.itrust.business.component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.Asset;
import lu.itrust.business.dao.DAOAsset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author eomar
 * 
 */
@Component
public class ChartGenerator {

	@Autowired
	private DAOAsset daoAsset;

	public String aleByAsset(int idAnalysis) {

		List<Asset> assets = daoAsset
				.findByAnalysisAndSelectedOderByALE(idAnalysis);

		String chart = "\"chart\":{ \"type\":\"column\",  \"zoomType\": \"y\"},  \"scrollbar\": {\"enabled\": true}";

		String title = "\"title\": {\"text\":\"" + "ALE by Asset" + "\"}";

		String pane = "\"pane\": {\"size\": \"100%\"}";

		String legend = "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\"}";

		String plotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0 }}";

		String tooltip = "\"tooltip\": {\"headerFormat\": \"<span style='font-size:10px'>{point.key}</span><table>\", \"pointFormat\": \"<tr><td style='color:{series.color};padding:0;'>{series.name}: </td><td style='padding:0;min-width:120px;'><b>{point.y:.1f} k&euro;</b></td></tr>\",\"footerFormat\": \"</table>\", \"shared\": true, \"useHTML\": true }";

		if (assets.isEmpty())
			return "{" + chart + "," + title + "," + legend + "," + pane + ","
					+ plotOptions + "," + tooltip + "}";

		Asset assetMax = assets.get(assets.size() - 1);

		double max = Math.max(assetMax.getALEO(),
				Math.max(assetMax.getALE(), assetMax.getALEP()));

		String xAxis = "";

		String series = "";

		String categories = "[";

		String dataALEs = "[";

		String dataALEOs = "[";

		String dataALEPs = "[";

		String yAxis = "\"yAxis\": {\"min\": 0 , \"max\":" + max * 1.1
				+ ", \"title\": {\"text\": \"ALE\"}}";

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

		xAxis = "\"xAxis\":{\"categories\":" + categories + ", \"min\": 10}";
		series += "\"series\":[{\"name\":\"ALEO\", \"data\":" + dataALEOs
				+ "},{\"name\":\"ALE\", \"data\":" + dataALEs
				+ "},{\"name\":\"ALEP\", \"data\":" + dataALEPs + "}]";
		return "{" + chart + "," + title + "," + legend + "," + pane + ","
				+ plotOptions + "," + tooltip + "," + xAxis + "," + yAxis + ","
				+ series + "}";
	}

	private List<Asset> assetByType(List<Asset> assets) {
		Map<Integer, Asset> assetbytypes = new LinkedHashMap<Integer, Asset>();
		for (Asset asset : assets) {
			Asset asset2 = assetbytypes.get(asset.getAssetType().getId());
			if (asset2 == null) {
				assetbytypes.put(asset.getAssetType().getId(),
						asset2 = new Asset());
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

	public String aleByAssetType(int idAnalysis) {

		List<Asset> assets = assetByType(daoAsset
				.findByAnalysisAndSelectedOderByALE(idAnalysis));

		String chart = "\"chart\":{ \"type\":\"column\",  \"zoomType\": \"y\"},  \"scrollbar\": {\"enabled\": true}";

		String title = "\"title\": {\"text\":\"" + "ALE by Asset" + "\"}";

		String pane = "\"pane\": {\"size\": \"100%\"}";

		String legend = "\"legend\": {\"align\": \"right\",\"verticalAlign\": \"top\", \"y\": 70,\"layout\": \"vertical\"}";

		String plotOptions = "\"plotOptions\": {\"column\": {\"pointPadding\": 0.2, \"borderWidth\": 0 }}";

		String tooltip = "\"tooltip\": {\"headerFormat\": \"<span style='font-size:10px'>{point.key}</span><table>\", \"pointFormat\": \"<tr><td style='color:{series.color};padding:0;'>{series.name}: </td><td style='padding:0;min-width:120px;'><b>{point.y:.1f} k&euro;</b></td></tr>\",\"footerFormat\": \"</table>\", \"shared\": true, \"useHTML\": true }";

		if (assets.isEmpty())
			return "{" + chart + "," + title + "," + legend + "," + pane + ","
					+ plotOptions + "," + tooltip + "}";

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
			max = Math.max(
					asset.getALEO(),
					Math.max(asset.getALE(),
							Math.max(asset.getALEP(), max)));
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
		
		String yAxis = "\"yAxis\": {\"min\": 0 , \"max\":" + max * 1.1
				+ ", \"title\": {\"text\": \"ALE\"}}";

		xAxis = "\"xAxis\":{\"categories\":" + categories + "}";
		series += "\"series\":[{\"name\":\"ALEO\", \"data\":" + dataALEOs
				+ "},{\"name\":\"ALE\", \"data\":" + dataALEs
				+ "},{\"name\":\"ALEP\", \"data\":" + dataALEPs + "}]";
		return "{" + chart + "," + title + "," + legend + "," + pane + ","
				+ plotOptions + "," + tooltip + "," + xAxis + "," + yAxis + ","
				+ series + "}";
	}
}

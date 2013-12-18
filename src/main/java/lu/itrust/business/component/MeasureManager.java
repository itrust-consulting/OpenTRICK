/**
 * 
 */
package lu.itrust.business.component;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Norm;

/**
 * @author eomar
 * 
 */
public class MeasureManager {

	public static Map<String, List<Measure>> SplitByNorm(List<Measure> measures) {
		Map<String, List<Measure>> mappingMeasures = new LinkedHashMap<>();
		for (Measure measure : measures) {
			Norm norm = measure.getAnalysisNorm().getNorm();
			List<Measure> measures2 = mappingMeasures.get(norm.getLabel());
			if (measures2 == null) {
				measures2 = new LinkedList<>();
				mappingMeasures.put(norm.getLabel(), measures2);
			}
			measures2.add(measure);
		}
		return mappingMeasures;
	}
	
	public static List<Measure> ConcatMeasure(List<AnalysisNorm> analysisNorms){
		List<Measure> measures = new LinkedList<>();
		for (AnalysisNorm analysisNorm : analysisNorms)
			measures.addAll(analysisNorm.getMeasures());
		return measures;
	}

}

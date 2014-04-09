/**
 * 
 */
package lu.itrust.business.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.component.helper.Chapter;

/**
 * @author eomar
 * 
 */
public class MeasureManager {

	public static Map<String, List<Measure>> SplitByNorm(List<Measure> measures) {
		Map<String, List<Measure>> mappingMeasures = new LinkedHashMap<>();
		Collections.sort(measures, new ComparatorMeasureReferance());
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

	public static List<Measure> ConcatMeasure(List<AnalysisNorm> analysisNorms) {
		List<Measure> measures = new LinkedList<>();
		for (AnalysisNorm analysisNorm : analysisNorms)
			measures.addAll(analysisNorm.getMeasures());
		return measures;
	}

	/**
	 * extractMainChapter: <br>
	 * extract the main chapter
	 * 
	 * @param chapter
	 * @return
	 */
	public static String extractMainChapter(String chapter) {

		if ((chapter.toUpperCase().startsWith("A.")) || (chapter.toUpperCase().startsWith("M."))) {
			String[] chapters = chapter.split("[.]");
			return chapters[0] + "." + chapters[1];
		} else {
			return (chapter.contains(".") ? chapter.split("[.]")[0] : chapter);
		}

	}

	public static Map<Chapter, List<Measure>> SplitByChapter(List<? extends Measure> measures) {
		Map<Chapter, List<Measure>> chapters = new LinkedHashMap<Chapter, List<Measure>>();
		Map<String, Chapter> chapterMapping = new LinkedHashMap<String, Chapter>();
		for (Measure measure : measures) {
			String reference = extractMainChapter(measure.getMeasureDescription().getReference());
			Norm norm = measure.getMeasureDescription().getNorm();
			Chapter chapter = chapterMapping.get(norm.getLabel() + "|-|" + reference);
			if (chapter == null)
				chapterMapping.put(norm.getLabel() + "|-|" + reference, chapter = new Chapter(norm, reference));
			List<Measure> measures2 = chapters.get(chapter);
			if (measures2 == null)
				chapters.put(chapter, measures2 = new ArrayList<Measure>());
			measures2.add(measure);
		}
		return chapters;
	}

}

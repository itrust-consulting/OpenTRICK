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
import lu.itrust.business.TS.AssetType;
import lu.itrust.business.TS.AssetTypeValue;
import lu.itrust.business.TS.MaturityMeasure;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureNorm;
import lu.itrust.business.TS.MeasureProperties;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.NormMeasure;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.Phase;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.helper.Chapter;
import lu.itrust.business.component.helper.ImportRRFForm;
import lu.itrust.business.dao.DAOAnalysis;
import lu.itrust.business.dao.DAOAnalysisNorm;
import lu.itrust.business.dao.DAOAssetType;
import lu.itrust.business.dao.DAOMeasure;
import lu.itrust.business.dao.hbm.DAOHibernate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * MeasureManager.java: <br>
 * Detailed description...
 * 
 * @author eomar itrust consulting s.a.rl.:
 * @version
 * @since May 26, 2013
 */
@Component
public class MeasureManager {

	@Autowired
	private DAOAnalysis daoAnalysis;

	@Autowired
	private DAOAnalysisNorm daoAnalysisNorm;

	@Autowired
	private DAOMeasure daoMeasure;

	@Autowired
	private DAOAssetType daoAssetType;

	/**
	 * SplitByNorm: <br>
	 * Description
	 * 
	 * @param measures
	 * @return
	 */
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

	/**
	 * SplitByChapter: <br>
	 * Description
	 * 
	 * @param measures
	 * @return
	 */
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

	/**
	 * createNewMeasureForAllAnalyses: <br>
	 * Description
	 * 
	 * @param measureDescription
	 */
	@Transactional
	public void createNewMeasureForAllAnalyses(MeasureDescription measureDescription) throws Exception {
		List<AnalysisNorm> analysisNorms = daoAnalysisNorm.getAllFromNorm(measureDescription.getNorm());
		for (AnalysisNorm anorm : analysisNorms) {
			boolean found = false;
			for (Measure measure : anorm.getMeasures()) {
				if (measure.getMeasureDescription().equals(measureDescription)) {
					found = true;
					break;
				}
			}
			if (found == false) {
				Measure measure;
				Object implementationRate = null;
				if (anorm instanceof MeasureNorm) {
					measure = new NormMeasure();
					List<AssetType> assetTypes = daoAssetType.getAll();
					List<AssetTypeValue> assetTypeValues = ((NormMeasure) measure).getAssetTypeValues();
					for (AssetType assetType : assetTypes)
						assetTypeValues.add(new AssetTypeValue(assetType, 0));
					((NormMeasure) measure).setMeasurePropertyList(new MeasureProperties());
					implementationRate = new Double(0);
				} else {
					measure = new MaturityMeasure();
					for (Parameter parameter : anorm.getAnalysis().getParameters()) {
						if (parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME) && parameter.getValue() == 0) {
							implementationRate = parameter;
							break;
						}
					}
				}
				Phase phase = anorm.getAnalysis().getPhaseByNumber(Constant.PHASE_DEFAULT);
				if (phase == null)
					anorm.getAnalysis().addUsedPhase(phase = new Phase(Constant.PHASE_DEFAULT));
				measure.setPhase(phase);
				measure.setImplementationRate(implementationRate);
				measure.setAnalysisNorm(anorm);
				measure.setMeasureDescription(measureDescription);
				measure.setStatus("AP");
				anorm.getMeasures().add(measure);
				daoAnalysisNorm.saveOrUpdate(anorm);
				daoAnalysis.saveOrUpdate(anorm.getAnalysis());
			}
		}
	}

	@Transactional
	public void importNorm(Integer idAnalysis, ImportRRFForm rrfForm) throws Exception {
		for (Integer idNorm : rrfForm.getNorms()) {
			
			Map<String, Measure> profileMeasures = daoMeasure.mappingAllFromAnalysisAndNorm(rrfForm.getProfile(), idNorm);
			List<Measure> measures = daoMeasure.getAllFromAnalysisAndNorm(idAnalysis, idNorm);
			for (Measure measure : measures) {
				NormMeasure normMeasure = (NormMeasure) profileMeasures.get(measure.getMeasureDescription().getReference());
				if(normMeasure == null)
					continue;
				((NormMeasure) measure).setMeasurePropertyList(DAOHibernate.Initialise(((NormMeasure) measure).getMeasurePropertyList()));//Force hibernate to initialise data
				normMeasure.setMeasurePropertyList(DAOHibernate.Initialise(normMeasure.getMeasurePropertyList()));//Force hibernate to initialise data
				normMeasure.copyMeasureCharacteristicsTo(((NormMeasure)measure));
				daoMeasure.saveOrUpdate(measure);
			}
		}
	}

}
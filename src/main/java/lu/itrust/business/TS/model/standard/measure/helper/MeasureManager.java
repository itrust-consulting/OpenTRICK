package lu.itrust.business.TS.model.standard.measure.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOActionPlan;
import lu.itrust.business.TS.database.dao.DAOActionPlanSummary;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOAnalysisStandard;
import lu.itrust.business.TS.database.dao.DAOAssetType;
import lu.itrust.business.TS.database.dao.DAOAssetTypeValue;
import lu.itrust.business.TS.database.dao.DAOMeasure;
import lu.itrust.business.TS.database.dao.DAOMeasureDescription;
import lu.itrust.business.TS.database.dao.DAOStandard;
import lu.itrust.business.TS.database.dao.hbm.DAOHibernate;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.parameter.MaturityParameter;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.rrf.ImportRRFForm;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.AssetStandard;
import lu.itrust.business.TS.model.standard.MaturityStandard;
import lu.itrust.business.TS.model.standard.NormalStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.StandardType;
import lu.itrust.business.TS.model.standard.measure.AssetMeasure;
import lu.itrust.business.TS.model.standard.measure.MaturityMeasure;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.MeasureAssetValue;
import lu.itrust.business.TS.model.standard.measure.MeasureProperties;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;

import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;


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
	private DAOStandard daoStandard;

	@Autowired
	private DAOAnalysisStandard daoAnalysisStandard;

	@Autowired
	private DAOMeasure daoMeasure;

	@Autowired
	private DAOAssetType daoAssetType;

	@Autowired
	private DAOAssetTypeValue daoAssetTypeValue;

	@Autowired
	private DAOActionPlanSummary daoActionPlanSummary;

	@Autowired
	private DAOActionPlan daoActionPlan;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private DAOMeasureDescription daoMeasureDescription;

	public static Standard getStandard(List<Standard> standards, String standardname) {
		for (Standard standard : standards)
			if (standard.getLabel().equals(standardname))
				return standard;
		return null;
	}

	public static Integer getStandardId(List<Standard> standards, String standardname) {
		for (Standard standard : standards)
			if (standard.getLabel().equals(standardname))
				return standard.getId();
		return null;
	}

	public static StandardType getStandardType(List<Standard> standards, String standardname) {
		for (Standard standard : standards)
			if (standard.getLabel().equals(standardname))
				return standard.getType();
		return null;
	}

	public static boolean isAnalysisOnlyStandard(List<Standard> standards, String standardname) {
		for (Standard standard : standards)
			if (standard.getLabel().equals(standardname))
				return standard.isAnalysisOnly();
		return false;
	}

	/**
	 * SplitByStandard: <br>
	 * Description
	 * 
	 * @param measures
	 * @return
	 */
	public static Map<String, List<Measure>> SplitByStandard(List<Measure> measures) {
		Map<String, List<Measure>> mappingMeasures = new LinkedHashMap<>();
		Collections.sort(measures, new MeasureComparator());
		for (Measure measure : measures) {
			Standard standard = measure.getAnalysisStandard().getStandard();
			List<Measure> measures2 = mappingMeasures.get(standard.getLabel());
			if (measures2 == null) {
				measures2 = new LinkedList<>();
				mappingMeasures.put(standard.getLabel(), measures2);
			}
			measures2.add(measure);
		}

		return mappingMeasures;
	}

	public static List<Measure> ConcatMeasure(List<AnalysisStandard> analysisStandards) {
		List<Measure> measures = new LinkedList<>();
		for (AnalysisStandard analysisStandard : analysisStandards)
			measures.addAll(analysisStandard.getMeasures());

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
			String[] chapters = chapter.split("[.]", 3);
			return chapters[0] + "." + chapters[1];
		} else {
			return (chapter.contains(".") ? chapter.split("[.]", 2)[0] : chapter);
		}

	}

	/**
	 * SplitByChapter: <br>
	 * Description
	 * 
	 * @param measures
	 * @return
	 */
	public static Map<Chapter, List<Measure>> SplitByChapter(List<Measure> measures) {
		Map<Chapter, List<Measure>> chapters = new LinkedHashMap<Chapter, List<Measure>>();
		Map<String, Chapter> chapterMapping = new LinkedHashMap<String, Chapter>();

		Comparator<Measure> cmp = new MeasureComparator();

		Map<String, List<Measure>> splittedmeasures = MeasureManager.SplitByStandard(measures);

		List<Measure> allmeasures = new ArrayList<Measure>();

		for (String key : splittedmeasures.keySet()) {
			List<Measure> tmpMeasures = splittedmeasures.get(key);
			Collections.sort(tmpMeasures, cmp);
			allmeasures.addAll(tmpMeasures);
		}

		for (Measure measure : allmeasures) {
			String reference = extractMainChapter(measure.getMeasureDescription().getReference());
			Standard standard = measure.getMeasureDescription().getStandard();
			Chapter chapter = chapterMapping.get(standard.getLabel() + "|-|" + reference);
			if (chapter == null)
				chapterMapping.put(standard.getLabel() + "|-|" + reference, chapter = new Chapter(standard, reference));
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
		List<AnalysisStandard> analysisStandards = daoAnalysisStandard.getAllFromStandard(measureDescription.getStandard());
		for (AnalysisStandard astandard : analysisStandards) {
			Analysis analysis = daoAnalysis.getByAnalysisStandardId(astandard.getId());
			Measure measure = null;
			Object implementationRate = null;
			if (astandard instanceof NormalStandard) {
				measure = new NormalMeasure();
				List<AssetType> assetTypes = daoAssetType.getAll();
				List<AssetTypeValue> assetTypeValues = ((NormalMeasure) measure).getAssetTypeValues();
				for (AssetType assetType : assetTypes)
					assetTypeValues.add(new AssetTypeValue(assetType, 0));
				((NormalMeasure) measure).setMeasurePropertyList(new MeasureProperties());
				implementationRate = new Double(0);
			} else if (astandard instanceof MaturityStandard) {
				measure = new MaturityMeasure();
				for (Parameter parameter : analysis.getParameters()) {
					if (parameter.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME) && parameter.getValue() == 0) {
						implementationRate = parameter;
						break;
					}
				}
			} else if (astandard instanceof AssetStandard) {
				measure = new AssetMeasure();
				((AssetMeasure) measure).setMeasurePropertyList(new MeasureProperties());
				implementationRate = new Double(0);
				((AssetMeasure) measure).setMeasureAssetValues(new ArrayList<MeasureAssetValue>());
			}
			Phase phase = analysis.getPhaseByNumber(Constant.PHASE_DEFAULT);
			if (phase == null) {
				phase = new Phase(Constant.PHASE_DEFAULT);
				phase.setAnalysis(analysis);
			}
			measure.setPhase(phase);
			measure.setImplementationRate(implementationRate);
			measure.setAnalysisStandard(astandard);
			measure.setMeasureDescription(measureDescription);
			measure.setStatus("AP");
			astandard.getMeasures().add(measure);
			daoAnalysisStandard.saveOrUpdate(astandard);
		}

		if (measureDescription.getId() < 1)
			daoMeasureDescription.saveOrUpdate(measureDescription);
	}

	@Transactional
	public void importStandard(Integer idAnalysis, ImportRRFForm rrfForm) throws Exception {
		for (Integer idStandard : rrfForm.getStandards()) {
			Map<String, Measure> profileMeasures = daoMeasure.mappingAllFromAnalysisAndStandard(rrfForm.getAnalysis(), idStandard);
			List<Measure> measures = daoMeasure.getAllFromAnalysisAndStandard(idAnalysis, idStandard);
			for (Measure measure : measures) {
				NormalMeasure normalMeasure = (NormalMeasure) profileMeasures.get(measure.getMeasureDescription().getReference());
				if (normalMeasure == null)
					continue;
				// Force hibernate to initialise data
				((NormalMeasure) measure).setMeasurePropertyList(DAOHibernate.Initialise(((NormalMeasure) measure).getMeasurePropertyList()));
				normalMeasure.setMeasurePropertyList(DAOHibernate.Initialise(normalMeasure.getMeasurePropertyList()));
				normalMeasure.copyMeasureCharacteristicsTo(((NormalMeasure) measure));
				daoMeasure.saveOrUpdate(measure);
			}
		}
	}

	@Transactional
	public void patchMeasureAssetypeValueDuplicated() throws Exception {
		int count = daoMeasure.countNormalMeasure(), pageSize = 30;
		if (count == 0)
			return;
		else if (count < pageSize)
			count = pageSize;
		int pageCount = (int) Math.ceil(count / (double) pageSize) + 1;
		for (int pageIndex = 1; pageIndex < pageCount; pageIndex++)
			for (NormalMeasure normalMeasure : daoMeasure.getAllNormalMeasure(pageIndex, pageSize))
				removeDuplicationAssetypeValue(normalMeasure);
	}

	@Transactional
	public void removeDuplicationAssetypeValue(NormalMeasure measure) throws Exception {
		if (measure.getAssetTypeValues() == null || measure.getAssetTypeValues().isEmpty())
			return;
		Map<Integer, AssetTypeValue> indexAsssetType = new LinkedHashMap<Integer, AssetTypeValue>();
		Iterator<AssetTypeValue> iterator = measure.getAssetTypeValues().iterator();
		boolean saveRequired = false;
		while (iterator.hasNext()) {
			AssetTypeValue typeValue = iterator.next();
			if (indexAsssetType.containsKey(typeValue.getAssetType().getId())) {
				AssetTypeValue typeValue2 = indexAsssetType.get(typeValue.getAssetType().getId());
				if (typeValue.getValue() > typeValue2.getValue())
					typeValue2.setValue(typeValue.getValue());
				iterator.remove();
				daoAssetTypeValue.delete(typeValue);
				saveRequired = true;
			} else
				indexAsssetType.put(typeValue.getAssetType().getId(), typeValue);
		}
		indexAsssetType.clear();
		if (saveRequired)
			daoMeasure.saveOrUpdate(measure);
	}

	@Transactional
	public void removeStandardFromAnalysis(Integer idAnalysis, int idStandard) throws Exception {
		AnalysisStandard analysisStandard = daoAnalysisStandard.getFromAnalysisIdAndStandardId(idAnalysis, idStandard);
		List<SummaryStage> summaryStages = daoActionPlanSummary.getAllFromAnalysis(idAnalysis);
		for (SummaryStage summaryStage : summaryStages)
			daoActionPlanSummary.delete(summaryStage);
		List<ActionPlanEntry> actionPlanEntries = daoActionPlan.getAllFromAnalysis(idAnalysis);
		for (ActionPlanEntry actionPlanEntry : actionPlanEntries)
			daoActionPlan.delete(actionPlanEntry);

		Standard standard = daoStandard.get(idStandard);

		daoAnalysisStandard.delete(analysisStandard);
		List<AnalysisStandard> astandards = daoAnalysisStandard.getAllFromStandard(standard);

		if (standard.isAnalysisOnly() && (astandards == null || astandards.isEmpty()))
			customDelete.deleteStandard(standard);
	}

	public static Measure Create(AnalysisStandard analysisStandard) throws TrickException {
		if (analysisStandard == null || analysisStandard.getStandard() == null || analysisStandard.getStandard().getType() == null)
			throw new TrickException("error.create.measure.invalid.standard", "Measure cannot be created: invalid standard");
		Measure measure = null;
		switch (analysisStandard.getStandard().getType()) {
		case ASSET:
			measure = new AssetMeasure();
			break;
		case NORMAL:
			measure = new NormalMeasure();
			break;
		case MATURITY:
			measure = new MaturityMeasure();
			break;
		default:
			throw new TrickException("error.create.measure.invalid.standard.type", "Measure cannot be created: invalid standard type");
		}
		measure.setAnalysisStandard(analysisStandard);
		return measure;
	}

	/**
	 * Compute Maturity-based Effectiveness Rate for 27002
	 * 
	 * @param measures27002
	 * @param maturityMeasures
	 * @param parameters
	 *            : require Maturity parameters + {@link Constant#PARAMETERTYPE_TYPE_MAX_EFF_NAME}
	 * @param reference:
	 *            mapped by reference
	 * @return Map<27002 Reference or id, efficiency implementation Rate>
	 */
	public static Map<Object, Double> ComputeMaturiyEfficiencyRate(List<Measure> measures27002, List<Measure> maturityMeasures, List<Parameter> parameters, boolean reference) {
		Map<Object, Double> effectiveImpelementationRate = new HashMap<>();
		if (measures27002 == null || maturityMeasures == null || parameters == null)
			return effectiveImpelementationRate;
		Map<String, Double> maturities = ComputeMaturityByChapter(maturityMeasures, parameters);
		if (maturities.isEmpty())
			return effectiveImpelementationRate;
		for (Measure measure : measures27002) {
			if (measure.getMeasureDescription().isComputable()) {
				Double maturity = maturities.get(measure.getMeasureDescription().getReference().split("[.]", 2)[0]);
				if (maturity != null)
					effectiveImpelementationRate.put(reference ? measure.getMeasureDescription().getReference() : measure.getId(),
							measure.getImplementationRateValue() * maturity * 0.01);
			}
		}
		return effectiveImpelementationRate;
	}

	/**
	 * Compute Maturity by Chapter
	 * @param maturityMeasures
	 * @param parameters :  MaturityParameter + {@link Constant#PARAMETERTYPE_TYPE_MAX_EFF_NAME}
	 * @return Map<Chapter, Maturity rate>
	 */
	public static Map<String, Double> ComputeMaturityByChapter(List<Measure> maturityMeasures, List<Parameter> parameters) {
		Map<String, Double> maturities = new HashMap<>();
		Map<String, MaturityParameter> mappedMaturityParameters = new HashMap<>(23);
		Map<String, Parameter> efficiencyRates = new HashMap<>(6);
		parameters.forEach(parameter -> {
			if (parameter instanceof MaturityParameter)
				mappedMaturityParameters.put(parameter.getDescription(), (MaturityParameter) parameter);
			else if (parameter.isMatch(Constant.PARAMETERTYPE_TYPE_MAX_EFF_NAME))
				efficiencyRates.put(parameter.getDescription(), parameter);
		});

		Map<String, Boolean[]> smls = new HashMap<>();
		for (Measure measure : maturityMeasures) {
			if (!(measure instanceof MaturityMeasure))
				continue;
			String[] chapters = measure.getMeasureDescription().getReference().replace("M.", "").split("[.]");
			Boolean[] complainces = smls.get(chapters[0]);
			if (complainces == null)
				smls.put(chapters[0], complainces = new Boolean[] { true, true, true, true, true });
			if (!measure.getMeasureDescription().isComputable() || chapters.length != 3)
				continue;
			MeasureDescriptionText descriptionText = measure.getMeasureDescription().findByAlph3("eng");
			if (descriptionText == null || !mappedMaturityParameters.containsKey(descriptionText.getDomain()))
				continue;
			MaturityParameter maturityMeasure = mappedMaturityParameters.get(descriptionText.getDomain());
			double implementation = measure.getImplementationRateValue() * 0.01;
			switch (chapters[1]) {
			case "1":
				complainces[0] &= implementation >= maturityMeasure.getSMLLevel1();
				break;
			case "2":
				complainces[1] &= implementation >= maturityMeasure.getSMLLevel2();
				break;
			case "3":
				complainces[2] &= implementation >= maturityMeasure.getSMLLevel3();
				break;
			case "4":
				complainces[3] &= implementation >= maturityMeasure.getSMLLevel5();
				break;
			case "5":
				complainces[4] &= implementation >= maturityMeasure.getSMLLevel5();
				break;
			}
		}

		smls.forEach((chapter, complainces) -> {
			String sml = "SML0";
			for (int i = 0; i < complainces.length; i++) {
				if (complainces[i])
					sml = "SML" + (i + 1);
				else
					break;
			}
			Parameter parameter = efficiencyRates.get(sml);
			if (parameter != null)
				maturities.put(chapter, parameter.getValue());
		});
		return maturities;
	}

}
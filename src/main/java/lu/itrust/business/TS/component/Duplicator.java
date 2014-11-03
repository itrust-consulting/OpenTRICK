package lu.itrust.business.TS.component;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.data.basic.Analysis;
import lu.itrust.business.TS.data.basic.AnalysisStandard;
import lu.itrust.business.TS.data.basic.Assessment;
import lu.itrust.business.TS.data.basic.Asset;
import lu.itrust.business.TS.data.basic.AssetMeasure;
import lu.itrust.business.TS.data.basic.AssetStandard;
import lu.itrust.business.TS.data.basic.AssetTypeValue;
import lu.itrust.business.TS.data.basic.History;
import lu.itrust.business.TS.data.basic.ItemInformation;
import lu.itrust.business.TS.data.basic.MaturityMeasure;
import lu.itrust.business.TS.data.basic.MaturityParameter;
import lu.itrust.business.TS.data.basic.MaturityStandard;
import lu.itrust.business.TS.data.basic.Measure;
import lu.itrust.business.TS.data.basic.MeasureAssetValue;
import lu.itrust.business.TS.data.basic.MeasureDescription;
import lu.itrust.business.TS.data.basic.MeasureProperties;
import lu.itrust.business.TS.data.basic.NormalMeasure;
import lu.itrust.business.TS.data.basic.NormalStandard;
import lu.itrust.business.TS.data.basic.Parameter;
import lu.itrust.business.TS.data.basic.Phase;
import lu.itrust.business.TS.data.basic.RiskInformation;
import lu.itrust.business.TS.data.basic.Scenario;
import lu.itrust.business.TS.data.basic.Standard;
import lu.itrust.business.TS.data.basic.UserAnalysisRight;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOAnalysisStandard;
import lu.itrust.business.TS.database.dao.DAOMeasureDescription;
import lu.itrust.business.TS.database.dao.DAOMeasureDescriptionText;
import lu.itrust.business.TS.database.dao.DAOStandard;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisStandardHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOMeasureDescriptionHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOMeasureDescriptionTextHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOStandardHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.tsconstant.Constant;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Duplicator.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Feb 12, 2014
 */
@Component
public class Duplicator {

	public static final String KEY_PARAMETER_FORMAT = "%s-#-_##_-#-%s";

	@Autowired
	private DAOAnalysisStandard daoAnalysisStandard;

	@Autowired
	private DAOStandard daoStandard;

	@Autowired
	private DAOMeasureDescription daoMeasureDescription;

	@Autowired
	private DAOMeasureDescriptionText daoMeasureDescriptionText;

	@Autowired
	private DAOAnalysis daoAnalysis;

	public Duplicator() {
	};

	public Duplicator(Session session) {
		this.daoAnalysis = new DAOAnalysisHBM(session);
		this.daoAnalysisStandard = new DAOAnalysisStandardHBM(session);
		this.daoStandard = new DAOStandardHBM(session);
		this.daoMeasureDescription = new DAOMeasureDescriptionHBM(session);
		this.daoMeasureDescriptionText = new DAOMeasureDescriptionTextHBM(session);
	}

	/**
	 * duplicateAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param copy
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public Analysis duplicateAnalysis(Analysis analysis, Analysis newAnalysis) throws Exception {

		Map<Integer, Phase> phases = new LinkedHashMap<>();

		Map<Integer, Scenario> scenarios = new LinkedHashMap<Integer, Scenario>(analysis.getScenarios().size());

		Map<Integer, Asset> assets = new LinkedHashMap<Integer, Asset>(analysis.getAssets().size());

		Map<String, Parameter> parameters = new LinkedHashMap<>(analysis.getParameters().size());

		try {
			Analysis copy = analysis.duplicateTo(newAnalysis);

			copy.setUserRights(new ArrayList<UserAnalysisRight>(analysis.getUserRights().size()));
			for (UserAnalysisRight uar : analysis.getUserRights()) {
				UserAnalysisRight uarcopy = uar.duplicate();
				copy.addUserRight(uarcopy);
			}

			copy.setHistories(new ArrayList<History>(analysis.getHistories().size()));
			for (History history : analysis.getHistories())
				copy.getHistories().add(history.duplicate());

			copy.setItemInformations(new ArrayList<ItemInformation>(analysis.getItemInformations().size()));
			for (ItemInformation itemInformation : analysis.getItemInformations())
				copy.getItemInformations().add(itemInformation.duplicate());

			copy.setParameters(new ArrayList<Parameter>(analysis.getParameters().size()));
			for (Parameter parameter : analysis.getParameters()) {
				Parameter parameter2 = parameter.duplicate();
				parameters.put(String.format(KEY_PARAMETER_FORMAT, parameter.getType().getLabel(), parameter.getDescription()), parameter2);
				copy.getParameters().add(parameter2);
			}

			copy.setRiskInformations(new ArrayList<RiskInformation>(analysis.getRiskInformations().size()));
			for (RiskInformation riskInformation : analysis.getRiskInformations())
				copy.getRiskInformations().add(riskInformation.duplicate());

			copy.setScenarios(new ArrayList<Scenario>(analysis.getScenarios().size()));
			for (Scenario scenario : analysis.getScenarios()) {
				scenarios.put(scenario.getId(), scenario.duplicate());
				copy.getScenarios().add(scenarios.get(scenario.getId()));
			}

			copy.setAssets(new ArrayList<Asset>(analysis.getAssets().size()));
			for (Asset asset : analysis.getAssets()) {
				assets.put(asset.getId(), asset.duplicate());
				copy.getAssets().add(assets.get(asset.getId()));
			}

			copy.setAssessments(new ArrayList<Assessment>(analysis.getAssessments().size()));

			for (Assessment assessment : analysis.getAssessments()) {
				Assessment clone = assessment.duplicate();
				clone.setScenario(scenarios.get(assessment.getScenario().getId()));
				clone.setAsset(assets.get(assessment.getAsset().getId()));
				copy.getAssessments().add(clone);
			}

			copy.setPhases(new ArrayList<Phase>(analysis.getPhases().size()));

			for (Phase phase : analysis.getPhases()) {
				phases.put(phase.getNumber(), phase.duplicate(copy));
				copy.addPhase(phases.get(phase.getNumber()));
			}

			copy.setAnalysisStandards(new ArrayList<AnalysisStandard>());

			for (AnalysisStandard analysisStandard : analysis.getAnalysisStandards())
				copy.addAnalysisStandard(duplicateAnalysisStandard(analysisStandard, phases, parameters, false));

			return copy;
		} finally {
			scenarios.clear();
			assets.clear();
			phases.clear();
			parameters.clear();
		}
	}

	/**
	 * duplicateAnalysisStandard: <br>
	 * Description
	 * 
	 * @param analysisStandard
	 * @param phases
	 * @param parameters
	 * @param anonymize
	 * @return
	 * @throws Exception
	 */
	public AnalysisStandard duplicateAnalysisStandard(AnalysisStandard analysisStandard, Map<Integer, Phase> phases, Map<String, Parameter> parameters, boolean anonymize) throws Exception {

		if (analysisStandard.getStandard().isAnalysisOnly() == false) {

			AnalysisStandard astandard = analysisStandard.duplicate();

			List<Measure> measures = new ArrayList<>(analysisStandard.getMeasures().size());
			for (Measure measure : analysisStandard.getMeasures())
				if (anonymize)
					measures.add(duplicateMeasure(measure, phases.get(Constant.PHASE_DEFAULT), astandard, parameters, anonymize));
				else
					measures.add(duplicateMeasure(measure, phases.containsKey(measure.getPhase().getNumber()) ? phases.get(measure.getPhase().getNumber()) : phases.get(Constant.PHASE_DEFAULT),
							astandard, parameters, anonymize));

			astandard.setMeasures(measures);
			return astandard;
		} else {
			Standard standard = analysisStandard.getStandard().duplicate();

			standard.setVersion(daoStandard.getBiggestVersionFromStandardByNameAndType(standard.getLabel(), standard.getType()) + 1);

			// daoStandard.save(standard);

			List<MeasureDescription> mesDescs = daoMeasureDescription.getAllByStandard(analysisStandard.getStandard());

			Map<String, MeasureDescription> newMesDescs = new LinkedHashMap<String, MeasureDescription>();

			for (MeasureDescription mesDesc : mesDescs) {

				MeasureDescription desc = mesDesc.duplicate(standard);

				// daoMeasureDescription.save(desc);

				newMesDescs.put(desc.getReference(), desc);

			}

			AnalysisStandard tmpAnalysisStandard = null;

			if (analysisStandard instanceof NormalStandard)
				tmpAnalysisStandard = new NormalStandard(standard);

			if (analysisStandard instanceof MaturityStandard)
				tmpAnalysisStandard = new MaturityStandard(standard);

			if (analysisStandard instanceof AssetStandard)
				tmpAnalysisStandard = new AssetStandard(standard);

			List<Measure> measures = new ArrayList<>(analysisStandard.getMeasures().size());
			for (Measure measure : analysisStandard.getMeasures()) {

				Measure tmpmeasure = null;

				tmpmeasure = duplicateMeasure(measure, anonymize ? phases.get(Constant.PHASE_DEFAULT) : phases.get(measure.getPhase().getNumber()), tmpAnalysisStandard, parameters, anonymize);

				MeasureDescription mesDesc = newMesDescs.get(tmpmeasure.getMeasureDescription().getReference());

				tmpmeasure.setMeasureDescription(mesDesc);

				// tmpmeasure.getAnalysisStandard().getMeasures().add(tmpmeasure);

				measures.add(tmpmeasure);

			}

			tmpAnalysisStandard.setMeasures(measures);

			return tmpAnalysisStandard;
		}
	}

	/**
	 * duplicateMeasure: <br>
	 * Description
	 * 
	 * @param measure
	 * @param phase
	 * @param standard
	 * @param parameters
	 * @param anonymize
	 * @return
	 * @throws CloneNotSupportedException
	 * @throws TrickException
	 */
	public Measure duplicateMeasure(Measure measure, Phase phase, AnalysisStandard standard, Map<String, Parameter> parameters, boolean anonymize) throws CloneNotSupportedException, TrickException {
		Measure copy = measure.duplicate(standard, phase);

		if (anonymize) {
			copy.setComment(Constant.EMPTY_STRING);
			copy.setToDo(Constant.EMPTY_STRING);
			copy.setCost(0);
			copy.setExternalWL(0);
			copy.setInternalWL(0);
			copy.setInvestment(0);
			copy.setLifetime(0);
			copy.setMaintenance(0);
		}
		if (copy instanceof MaturityMeasure) {
			MaturityMeasure matmeasure = (MaturityMeasure) copy;
			Parameter parameter =
				parameters.get(String.format(KEY_PARAMETER_FORMAT, Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME, anonymize ? Constant.IS_NOT_ACHIEVED : ((MaturityMeasure) measure)
						.getImplementationRate().getDescription()));
			if (parameter == null) {
				for (Parameter param : parameters.values()) {
					if (param instanceof MaturityParameter && param.getValue() == 0) {
						parameter = param;
						break;
					}
				}
			}
			matmeasure.setImplementationRate(parameter);
			if (anonymize) {
				matmeasure.setReachedLevel(1);
				matmeasure.setSML1Cost(0);
				matmeasure.setSML2Cost(0);
				matmeasure.setSML3Cost(0);
				matmeasure.setSML4Cost(0);
				matmeasure.setSML5Cost(0);
			}
		} else if (copy instanceof NormalMeasure) {
			NormalMeasure normalMeasure = (NormalMeasure) copy;
			if (anonymize) {
				normalMeasure.setToCheck(Constant.EMPTY_STRING);
				normalMeasure.setImplementationRate(0);
				normalMeasure.setMeasurePropertyList((MeasureProperties) normalMeasure.getMeasurePropertyList().duplicate());
				normalMeasure.getMeasurePropertyList().setSoaComment(Constant.EMPTY_STRING);
				normalMeasure.getMeasurePropertyList().setSoaReference(Constant.EMPTY_STRING);
				normalMeasure.getMeasurePropertyList().setSoaRisk(Constant.EMPTY_STRING);
				for (AssetTypeValue assetTypeValue : normalMeasure.getAssetTypeValues())
					assetTypeValue.setValue(0);
			}
		} else if (copy instanceof AssetMeasure) {
			AssetMeasure assetMeasure = (AssetMeasure) copy;
			if (anonymize) {
				assetMeasure.setToCheck(Constant.EMPTY_STRING);
				assetMeasure.setImplementationRate(0);
				assetMeasure.setMeasurePropertyList((MeasureProperties) assetMeasure.getMeasurePropertyList().duplicate());
				assetMeasure.getMeasurePropertyList().setSoaComment(Constant.EMPTY_STRING);
				assetMeasure.getMeasurePropertyList().setSoaReference(Constant.EMPTY_STRING);
				assetMeasure.getMeasurePropertyList().setSoaRisk(Constant.EMPTY_STRING);
				for (MeasureAssetValue assetValue : assetMeasure.getMeasureAssetValues())
					assetValue.setValue(0);
			}
		}
		return copy;
	}

	/**
	 * createProfile: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param analysisProfile
	 * @param serviceTaskFeedback
	 * @param idTask
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Analysis createProfile(Analysis analysis, String name, List<Integer> standards, ServiceTaskFeedback serviceTaskFeedback, long idTask) {

		try {

			Map<String, Parameter> parameters = new LinkedHashMap<>();

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.start", "Duplicate analysis base information", null, 2));

			// duplicate the analysis
			Analysis copy = analysis.duplicate();

			Timestamp ts = new Timestamp(System.currentTimeMillis());

			SimpleDateFormat outDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String tsstring = outDateFormat.format(ts);

			tsstring = copy.getLanguage().getAlpha3() + "_" + tsstring;

			// set basic values
			copy.setVersion("0.0.1");
			copy.setBasedOnAnalysis(null);
			// language 3char code + creation date and time
			copy.setIdentifier(tsstring);
			copy.setCreationDate(ts);
			copy.setLabel(name);
			copy.setProfile(true);
			copy.setData(false);

			// do not set analysis specific data which are unused for profile

			// history
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.delete.history", "Delete analysis histories", null, 3));
			copy.setHistories(null);

			// analysis rights
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.delete.right", "Delete analysis rights", null, 4));
			copy.setUserRights(null);

			// assets
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.asset", "empty assets", null, 5));
			copy.setAssets(null);

			// assessments
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.assessment", "empty assessments", null, 10));
			copy.setAssessments(null);

			// item information
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.itemInformation", "Empty item information", null, 20));
			copy.setItemInformations(null);

			// risk information
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.riskInformation", "empty risk information", null, 30));
			copy.setRiskInformations(null);

			// actionplans
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.actionplan", "empty actionplans and summaries", null, 35));
			copy.setActionPlans(null);
			copy.setSummaries(null);

			// risk register
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.riskregister", "empty risk register", null, 40));
			copy.setRiskRegisters(null);

			// copy nessesary data to profile

			// parameters
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.parameter", "Copy parameters", null, 45));
			copy.setParameters(new ArrayList<Parameter>(analysis.getParameters().size()));
			for (Parameter parameter : analysis.getParameters()) {
				Parameter parameter2 = parameter.duplicate();
				parameters.put(String.format(KEY_PARAMETER_FORMAT, parameter.getType().getLabel(), parameter.getDescription()), parameter2);
				copy.getParameters().add(parameter2);
			}

			// phases
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.phase", "empty phases", null, 50));

			copy.setPhases(new ArrayList<Phase>());

			Map<Integer, Phase> phases = new LinkedHashMap<Integer, Phase>();

			Phase tmpPhase = analysis.findPhaseByNumber(Constant.PHASE_DEFAULT);
			if (tmpPhase == null) {
				tmpPhase = new Phase(Constant.PHASE_DEFAULT);
				tmpPhase.setBeginDate(new Date(System.currentTimeMillis()));
				tmpPhase.setEndDate(new Date(System.currentTimeMillis()));
				tmpPhase.setAnalysis(copy);
			} else
				tmpPhase = tmpPhase.duplicate(copy);
			phases.put(Constant.PHASE_DEFAULT, tmpPhase);
			copy.addPhase(tmpPhase);

			// scenarios
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.scenario", "Copy scenarios", null, 55));
			copy.setScenarios(new ArrayList<Scenario>(analysis.getScenarios().size()));
			for (Scenario scenario : analysis.getScenarios())
				copy.getScenarios().add(scenario.duplicate());

			// standards
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.measure", "Copy standards", null, 60));

			copy.setAnalysisStandards(new ArrayList<AnalysisStandard>());
			Integer percentageperstandard = (int) 40 / standards.size();

			int copycounter = 0;
			for (Integer standardID : standards) {
				copycounter++;
				AnalysisStandard standard = analysis.getAnalysisStandardByStandardId(standardID);
				if (standard != null) {
					copy.addAnalysisStandard(duplicateAnalysisStandard(standard, phases, parameters, true));
					serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.measure", "Copy standards", null, 60 + (percentageperstandard * copycounter)));
				}
			}

			return copy;
		} catch (CloneNotSupportedException cex) {
			cex.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}

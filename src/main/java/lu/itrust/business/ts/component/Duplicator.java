package lu.itrust.business.ts.component;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAOAnalysisStandard;
import lu.itrust.business.ts.database.dao.DAOMeasureDescription;
import lu.itrust.business.ts.database.dao.DAOMeasureDescriptionText;
import lu.itrust.business.ts.database.dao.DAOStandard;
import lu.itrust.business.ts.database.dao.impl.DAOAnalysisImpl;
import lu.itrust.business.ts.database.dao.impl.DAOAnalysisStandardImpl;
import lu.itrust.business.ts.database.dao.impl.DAOMeasureDescriptionImpl;
import lu.itrust.business.ts.database.dao.impl.DAOMeasureDescriptionTextImpl;
import lu.itrust.business.ts.database.dao.impl.DAOStandardImpl;
import lu.itrust.business.ts.database.service.ServiceTaskFeedback;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.ts.model.assessment.Assessment;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.cssf.RiskProfile;
import lu.itrust.business.ts.model.general.AssetTypeValue;
import lu.itrust.business.ts.model.general.Phase;
import lu.itrust.business.ts.model.general.document.impl.SimpleDocument;
import lu.itrust.business.ts.model.history.History;
import lu.itrust.business.ts.model.ilr.AssetEdge;
import lu.itrust.business.ts.model.ilr.AssetImpact;
import lu.itrust.business.ts.model.ilr.AssetNode;
import lu.itrust.business.ts.model.iteminformation.ItemInformation;
import lu.itrust.business.ts.model.parameter.ILevelParameter;
import lu.itrust.business.ts.model.parameter.IParameter;
import lu.itrust.business.ts.model.parameter.impl.SimpleParameter;
import lu.itrust.business.ts.model.parameter.value.AbstractValue;
import lu.itrust.business.ts.model.parameter.value.IValue;
import lu.itrust.business.ts.model.riskinformation.RiskInformation;
import lu.itrust.business.ts.model.scenario.Scenario;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.AssetStandard;
import lu.itrust.business.ts.model.standard.MaturityStandard;
import lu.itrust.business.ts.model.standard.NormalStandard;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measure.impl.AssetMeasure;
import lu.itrust.business.ts.model.standard.measure.impl.MaturityMeasure;
import lu.itrust.business.ts.model.standard.measure.impl.MeasureAssetValue;
import lu.itrust.business.ts.model.standard.measure.impl.NormalMeasure;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;

/**
 * Duplicator.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Feb 12, 2014
 */
@Component
public class Duplicator {

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
	}

	public Duplicator(Session session) {
		this.daoAnalysis = new DAOAnalysisImpl(session);
		this.daoAnalysisStandard = new DAOAnalysisStandardImpl(session);
		this.daoStandard = new DAOStandardImpl(session);
		this.daoMeasureDescription = new DAOMeasureDescriptionImpl(session);
		this.daoMeasureDescriptionText = new DAOMeasureDescriptionTextImpl(session);

	}

	/**
	 * duplicateAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param serviceTaskFeedback
	 * @param idTask
	 * @param minProgress
	 * @param maxProgress
	 * @param copy
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public Analysis duplicateAnalysis(Analysis analysis, Analysis newAnalysis, ServiceTaskFeedback serviceTaskFeedback,
			String idTask, int minProgress, int maxProgress)
			throws Exception {

		final Map<Integer, Phase> phases = new LinkedHashMap<>();

		final Map<Integer, Scenario> scenarios = new LinkedHashMap<>(analysis.getScenarios().size());

		final Map<Integer, Asset> assets = new LinkedHashMap<>(analysis.getAssets().size());

		final Map<Long, AssetNode> nodes = new LinkedHashMap<>(analysis.getAssetNodes().size());

		final Map<Long, AssetImpact> assetImpacts = new LinkedHashMap<>(
				Math.min(analysis.getAssets().size(), analysis.getAssetNodes().size()));

		final Map<String, IParameter> parameters = new LinkedHashMap<>(analysis.getParameters().size());

		double bound = ((double) (maxProgress - minProgress)) / 100.0;

		try {

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.start",
					"Copy analysis base information", (int) (minProgress + bound * 2)));

			Analysis copy = analysis.duplicateTo(newAnalysis);

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.right", "Copy rights",
					(int) (minProgress + bound * 4)));

			copy.setUserRights(new ArrayList<>(analysis.getUserRights().size()));
			for (UserAnalysisRight uar : analysis.getUserRights())
				copy.addUserRight(uar.duplicate());

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.history", "Copy history",
					(int) (minProgress + bound * 7)));

			copy.setHistories(new ArrayList<>(analysis.getHistories().size()));
			for (History history : analysis.getHistories())
				copy.getHistories().add(history.duplicate());

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.itemInformation",
					"Copy item information", (int) (minProgress + bound * 10)));

			copy.setItemInformations(new ArrayList<>(analysis.getItemInformations().size()));
			for (ItemInformation itemInformation : analysis.getItemInformations())
				copy.getItemInformations().add(itemInformation.duplicate());

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.parameter",
					"Copy parameters", (int) (minProgress + bound * 12)));

			copy.setParameters(new LinkedHashMap<>(analysis.getParameters().size()));

			analysis.getParameters().forEach((group, parameterList) -> parameterList.stream().forEach(parameter -> {
				IParameter duplicate = parameter.duplicate();
				copy.add(duplicate);
				parameters.put(parameter.getKey(), duplicate);
			}));

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.riskInformation",
					"Copy risk information", (int) (minProgress + bound * 15)));

			copy.setRiskInformations(new ArrayList<>(analysis.getRiskInformations().size()));
			for (RiskInformation riskInformation : analysis.getRiskInformations())
				copy.getRiskInformations().add(riskInformation.duplicate());

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.asset", "Copy assets",
					(int) (minProgress + bound * 18)));

			copy.setAssets(new ArrayList<>(analysis.getAssets().size()));
			for (Asset asset : analysis.getAssets()) {
				assets.put(asset.getId(), asset.duplicate());
				copy.getAssets().add(assets.get(asset.getId()));
			}

			serviceTaskFeedback.send(idTask,
					new MessageHandler("info.analysis.duplication.asset_dependancy", "Copy assets dependancies",
							(int) (minProgress + bound * 18)));

			copy.setAssetNodes(new ArrayList<>(analysis.getAssetNodes().size()));
			for (AssetNode node : analysis.getAssetNodes()) {
				final AssetImpact impact = assetImpacts.computeIfAbsent(node.getImpact().getId(),
						k -> node.getImpact().duplicate(assets.get(node.getAsset().getId())));

				final AssetNode myNode = node.duplicate(impact);
				copy.getAssetNodes().add(myNode);
				nodes.put(node.getId(), myNode);
			}

			copy.getAssetNodes()
					.forEach(e -> e.setEdges(e.getEdges().values().stream()
							.map(b -> b.duplicate(e, nodes.get(b.getChild().getId())))
							.collect(Collectors.toMap(AssetEdge::getChild, Function.identity())))

					);

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.scenario", "Copy scenarios",
					(int) (minProgress + bound * 30)));

			copy.setScenarios(new ArrayList<>(analysis.getScenarios().size()));
			for (Scenario scenario : analysis.getScenarios()) {
				scenarios.put(scenario.getId(), scenario.duplicate(assets));
				copy.getScenarios().add(scenarios.get(scenario.getId()));
			}

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.assessment",
					"Copy estimations", (int) (minProgress + bound * 35)));

			copy.setAssessments(new ArrayList<>(analysis.getAssessments().size()));

			for (Assessment assessment : analysis.getAssessments()) {
				Assessment clone = assessment.duplicate();
				clone.setScenario(scenarios.get(assessment.getScenario().getId()));
				clone.setAsset(assets.get(assessment.getAsset().getId()));
				clone.setImpacts(new LinkedList<>());
				assessment.getImpacts().forEach(impact -> {
					IValue value = impact.duplicate();
					if (value instanceof AbstractValue)
						((AbstractValue) value).setParameter(
								(ILevelParameter) parameters.get(((AbstractValue) value).getParameter().getKey()));
					clone.setImpact(value);
				});

				if (assessment.getLikelihood() != null) {
					final IValue value = assessment.getLikelihood().duplicate();
					if (value instanceof AbstractValue)
						((AbstractValue) value).setParameter(
								(ILevelParameter) parameters.get(((AbstractValue) value).getParameter().getKey()));
					clone.setLikelihood(value);
				}

				copy.getAssessments().add(clone);
			}

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.phase", "Copy phases",
					(int) (minProgress + bound * 40)));

			copy.setPhases(new ArrayList<>(analysis.getPhases().size()));

			for (Phase phase : analysis.getPhases()) {
				phases.put(phase.getNumber(), phase.duplicate(copy));
				copy.add(phases.get(phase.getNumber()));
			}

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.measure", "Copy standards",
					(int) (minProgress + bound * 42)));

			copy.setAnalysisStandards(new ArrayList<>());

			copy.setDocuments(analysis.getDocuments().values().stream().map(SimpleDocument::new)
					.collect(Collectors.toMap(SimpleDocument::getType, Function.identity())));

			if (!analysis.getAnalysisStandards().isEmpty()) {

				Integer percentageperstandard = (int) analysis.getAnalysisStandards().size() / 90 * 100;

				int copycounter = 0;

				for (AnalysisStandard analysisStandard : analysis.getAnalysisStandards().values()) {
					copycounter++;
					serviceTaskFeedback.send(idTask,
							new MessageHandler("info.analysis.duplication.measure", "Copy standards",
									(int) (minProgress + bound * (42 + (percentageperstandard * copycounter)))));
					copy.add(duplicateAnalysisStandard(analysisStandard, phases, parameters, assets, false));
				}
			}

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.risk_profile",
					"Copy risk profiles", (int) (minProgress + bound * 90)));

			copy.setRiskProfiles(new ArrayList<>(analysis.getRiskProfiles().size()));

			if (!analysis.getRiskProfiles().isEmpty()) {
				Map<String, Measure> measures = copy.getAnalysisStandards().values().stream()
						.flatMap(analysisStandard -> analysisStandard.getMeasures().stream())
						.collect(Collectors.toMap(Measure::getKeyName, Function.identity()));
				for (RiskProfile riskProfile : analysis.getRiskProfiles())
					copy.getRiskProfiles().add(riskProfile.duplicate(assets, scenarios, parameters, measures));
			}

			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.update.risk_dependencies",
					"Update risk dependencies", (int) (minProgress + bound * 95)));

			AssessmentAndRiskProfileManager.updateRiskDendencies(copy, null);

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
	 * @param assets
	 * @param anonymize
	 * @return
	 * @throws Exception
	 */
	public AnalysisStandard duplicateAnalysisStandard(AnalysisStandard analysisStandard, Map<Integer, Phase> phases,
			Map<String, IParameter> parameters, Map<Integer, Asset> assets,
			boolean anonymize) throws Exception {

		if (analysisStandard.getStandard().isAnalysisOnly()) {
			final Standard standard = analysisStandard.getStandard().duplicate();

			standard.setVersion(daoStandard.getNextVersionByLabelAndType(standard.getLabel(), standard.getType()));

			final List<MeasureDescription> mesDescs = daoMeasureDescription
					.getAllByStandard(analysisStandard.getStandard());

			final Map<String, MeasureDescription> newMesDescs = new LinkedHashMap<>();

			for (MeasureDescription mesDesc : mesDescs) {

				final MeasureDescription desc = mesDesc.duplicate(standard);

				newMesDescs.put(desc.getReference(), desc);

			}

			AnalysisStandard tmpAnalysisStandard = null;

			if (analysisStandard instanceof NormalStandard)
				tmpAnalysisStandard = new NormalStandard(standard);
			else if (analysisStandard instanceof MaturityStandard)
				tmpAnalysisStandard = new MaturityStandard(standard);
			else if (analysisStandard instanceof AssetStandard)
				tmpAnalysisStandard = new AssetStandard(standard);
			else
				throw new TrickException("error.unkown.standard", "Unkown standard type");

			final List<Measure> measures = new ArrayList<>(analysisStandard.getMeasures().size());
			for (Measure measure : analysisStandard.getMeasures()) {

				Measure tmpmeasure = null;

				tmpmeasure = duplicateMeasure(measure,
						anonymize ? phases.get(Constant.PHASE_DEFAULT) : phases.get(measure.getPhase().getNumber()),
						tmpAnalysisStandard, assets,
						parameters, anonymize);

				final MeasureDescription mesDesc = newMesDescs.get(tmpmeasure.getMeasureDescription().getReference());

				tmpmeasure.setMeasureDescription(mesDesc);

				measures.add(tmpmeasure);

			}

			tmpAnalysisStandard.setMeasures(measures);

			return tmpAnalysisStandard;
		} else {
			AnalysisStandard astandard = analysisStandard.duplicate();
			List<Measure> measures = new ArrayList<>(analysisStandard.getMeasures().size());
			for (Measure measure : analysisStandard.getMeasures())
				if (anonymize)
					measures.add(duplicateMeasure(measure, phases.get(Constant.PHASE_DEFAULT), astandard, assets,
							parameters, anonymize));
				else
					measures.add(duplicateMeasure(measure,
							phases.containsKey(measure.getPhase().getNumber())
									? phases.get(measure.getPhase().getNumber())
									: phases.get(Constant.PHASE_DEFAULT),
							astandard, assets,
							parameters, anonymize));

			astandard.setMeasures(measures);
			return astandard;
		}
	}

	/**
	 * duplicateMeasure: <br>
	 * Description
	 * 
	 * @param measure
	 * @param phase
	 * @param standard
	 * @param assets
	 * @param parameters
	 * @param anonymize
	 * @return
	 * @throws CloneNotSupportedException
	 * @throws TrickException
	 */
	public Measure duplicateMeasure(Measure measure, Phase phase, AnalysisStandard standard, Map<Integer, Asset> assets,
			Map<String, IParameter> parameters, boolean anonymize)
			throws CloneNotSupportedException, TrickException {
		Measure copy = measure.duplicate(standard, phase);

		if (anonymize) {
			copy.setComment(Constant.EMPTY_STRING);
			copy.setToDo(Constant.EMPTY_STRING);
			copy.setResponsible(Constant.EMPTY_STRING);
			copy.setTicket(Constant.EMPTY_STRING);
			copy.setRecurrentInvestment(0);
			copy.setInternalMaintenance(0);
			copy.setExternalMaintenance(0);
			copy.setImportance(2);
			copy.setExternalWL(0);
			copy.setInternalWL(0);
			copy.setInvestment(0);
			copy.setLifetime(0);
			copy.setCost(0);
		}

		if (copy instanceof MaturityMeasure) {
			final MaturityMeasure matmeasure = (MaturityMeasure) copy;
			IParameter parameter = parameters.get(anonymize
					? SimpleParameter.key(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME,
							Constant.IS_NOT_ACHIEVED)
					: ((MaturityMeasure) measure).getImplementationRate().getKey());
			if (parameter == null)
				parameter = parameters.values().stream()
						.filter(param -> (param.isMatch(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME)))
						.min((p1, p2) -> {
							return Double.compare(p1.getValue().doubleValue(), p1.getValue().doubleValue());
						}).orElse(null);

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
			final NormalMeasure normalMeasure = (NormalMeasure) copy;
			if (anonymize) {
				normalMeasure.setToCheck(Constant.EMPTY_STRING);
				normalMeasure.setImplementationRate(0.0);
				normalMeasure.getMeasurePropertyList().setSoaComment(Constant.EMPTY_STRING);
				normalMeasure.getMeasurePropertyList().setSoaReference(Constant.EMPTY_STRING);
				normalMeasure.getMeasurePropertyList().setSoaRisk(Constant.EMPTY_STRING);
				for (AssetTypeValue assetTypeValue : normalMeasure.getAssetTypeValues())
					assetTypeValue.setValue(0);
			}
		} else if (copy instanceof AssetMeasure) {
			final AssetMeasure assetMeasure = (AssetMeasure) copy;
			if (assets == null || assets.isEmpty())
				assetMeasure.getMeasureAssetValues().clear();
			else {
				Iterator<MeasureAssetValue> iterator = assetMeasure.getMeasureAssetValues().iterator();
				while (iterator.hasNext()) {
					MeasureAssetValue assetValue = iterator.next();
					if (!assets.containsKey(assetValue.getAsset().getId()))
						iterator.remove();
					else {
						assetValue.setAsset(assets.get(assetValue.getAsset().getId()));
						if (anonymize)
							assetValue.setValue(0);
					}
				}
			}
			if (anonymize) {
				assetMeasure.setToCheck(Constant.EMPTY_STRING);
				assetMeasure.setImplementationRate(0.0);
				assetMeasure.getMeasurePropertyList().setSoaComment(Constant.EMPTY_STRING);
				assetMeasure.getMeasurePropertyList().setSoaReference(Constant.EMPTY_STRING);
				assetMeasure.getMeasurePropertyList().setSoaRisk(Constant.EMPTY_STRING);
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
	public Analysis createProfile(Analysis analysis, String name, List<Integer> standards,
			ServiceTaskFeedback serviceTaskFeedback, String idTask) {

		try {

			final Map<String, IParameter> parameters = new LinkedHashMap<>();

			serviceTaskFeedback.send(idTask,
					new MessageHandler("info.analysis.duplication.start", "Copy analysis base information", 2));

			// duplicate the analysis
			final Analysis copy = analysis.duplicate();

			final Timestamp ts = new Timestamp(System.currentTimeMillis());

			final SimpleDateFormat outDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
			copy.setArchived(false);
			copy.setData(false);
			copy.setProject(Constant.EMPTY_STRING);
			// do not set analysis specific data which are unused for profile
			// history
			serviceTaskFeedback.send(idTask,
					new MessageHandler("info.analysis.delete.history", "Delete analysis histories", 3));
			copy.setHistories(null);

			// analysis rights
			serviceTaskFeedback.send(idTask,
					new MessageHandler("info.analysis.delete.right", "Delete analysis rights", 4));
			copy.setUserRights(null);

			// assets
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.delete.asset", "Clear assets", 5));
			copy.setAssets(null);

			serviceTaskFeedback.send(idTask,
					new MessageHandler("info.analysis.delete.asset_dependancy", "Clear assets dependancy", 8));
			copy.setAssetNodes(null);

			// assessments
			serviceTaskFeedback.send(idTask,
					new MessageHandler("info.analysis.delete.assessment", "empty assessments", 10));
			copy.setAssessments(null);

			// assessments
			serviceTaskFeedback.send(idTask,
					new MessageHandler("info.analysis.delete.risk_profile", "empty risk profile", 15));
			copy.setRiskProfiles(null);

			// item information
			serviceTaskFeedback.send(idTask,
					new MessageHandler("info.analysis.delete.itemInformation", "Clear item information", 20));
			copy.setItemInformations(new ArrayList<>());
			for (ItemInformation itemInformation : analysis.getItemInformations())
				copy.getItemInformations().add(itemInformation.anonymise());

			// risk information
			serviceTaskFeedback.send(idTask,
					new MessageHandler("info.analysis.delete.riskInformation", "Clear risk information", 30));
			copy.setRiskInformations(new ArrayList<>());
			for (RiskInformation riskInformation : analysis.getRiskInformations())
				copy.getRiskInformations().add(riskInformation.anonymise());

			// actionplans
			serviceTaskFeedback.send(idTask,
					new MessageHandler("info.analysis.delete.actionplan", "Clear actionplans and summaries", 35));
			copy.setActionPlans(null);

			copy.setSummaries(null);

			// risk register
			serviceTaskFeedback.send(idTask,
					new MessageHandler("info.analysis.delete.riskregister", "Clear risk register", 40));
			copy.setRiskRegisters(null);

			// copy nessesary data to profile

			// parameters
			serviceTaskFeedback.send(idTask,
					new MessageHandler("info.analysis.duplication.parameter", "Copy parameters", 45));

			copy.setParameters(new LinkedHashMap<>(analysis.getParameters().size()));

			analysis.getParameters().forEach((group, parameterList) -> parameterList.stream().forEach(parameter -> {
				IParameter duplicate = parameter.duplicate();
				copy.add(duplicate);
				parameters.put(parameter.getKey(), duplicate);
			}));
			// phases
			serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.delete.phase", "Clear phases", 50));

			copy.setPhases(new ArrayList<>());

			Map<Integer, Phase> phases = new LinkedHashMap<>();

			Phase tmpPhase = analysis.findPhaseByNumber(Constant.PHASE_DEFAULT);
			if (tmpPhase == null) {
				tmpPhase = new Phase(Constant.PHASE_DEFAULT);
				tmpPhase.setBeginDate(new Date(System.currentTimeMillis()));
				tmpPhase.setEndDate(new Date(System.currentTimeMillis()));
				tmpPhase.setAnalysis(copy);
			} else
				tmpPhase = tmpPhase.duplicate(copy);
			phases.put(Constant.PHASE_DEFAULT, tmpPhase);
			copy.add(tmpPhase);

			// scenarios
			serviceTaskFeedback.send(idTask,
					new MessageHandler("info.analysis.duplication.scenario", "Copy scenarios", 55));
			copy.setScenarios(new ArrayList<>(analysis.getScenarios().size()));
			for (Scenario scenario : analysis.getScenarios())
				copy.getScenarios().add(scenario.duplicate(null));

			// standards
			serviceTaskFeedback.send(idTask,
					new MessageHandler("info.analysis.duplication.measure", "Copy standards", 60));

			copy.setDocuments(Collections.emptyMap());

			copy.setAnalysisStandards(new ArrayList<>());
			if (!standards.isEmpty()) {
				Integer percentageperstandard = 40 / standards.size();

				int copycounter = 0;
				for (Integer standardID : standards) {
					copycounter++;
					AnalysisStandard standard = analysis.findAnalysisStandardByStandardId(standardID);
					if (standard != null) {
						copy.add(duplicateAnalysisStandard(standard, phases, parameters, null, true));
						serviceTaskFeedback.send(idTask, new MessageHandler("info.analysis.duplication.measure",
								"Copy standards", 60 + (percentageperstandard * copycounter)));
					}
				}
			}
			return copy;
		} catch (CloneNotSupportedException e) {
			TrickLogManager.persist(e);
			return null;
		} catch (Exception e) {
			TrickLogManager.persist(e);
			return null;
		}

	}

	public DAOAnalysisStandard getDaoAnalysisStandard() {
		return daoAnalysisStandard;
	}

	public DAOStandard getDaoStandard() {
		return daoStandard;
	}

	public DAOMeasureDescription getDaoMeasureDescription() {
		return daoMeasureDescription;
	}

	public DAOMeasureDescriptionText getDaoMeasureDescriptionText() {
		return daoMeasureDescriptionText;
	}

	public DAOAnalysis getDaoAnalysis() {
		return daoAnalysis;
	}

}

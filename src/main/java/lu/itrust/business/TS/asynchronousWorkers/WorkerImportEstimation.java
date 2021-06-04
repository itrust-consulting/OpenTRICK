/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.findSheet;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.findTable;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getBoolean;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getDouble;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getString;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.isEmpty;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.TablePart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.hibernate.Session;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.Sheet;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.TS.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.TS.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOAssessment;
import lu.itrust.business.TS.database.dao.DAOAsset;
import lu.itrust.business.TS.database.dao.DAOAssetType;
import lu.itrust.business.TS.database.dao.DAOAssetTypeValue;
import lu.itrust.business.TS.database.dao.DAORiskProfile;
import lu.itrust.business.TS.database.dao.DAOScenario;
import lu.itrust.business.TS.database.dao.DAOUser;
import lu.itrust.business.TS.database.dao.DAOWordReport;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOAssessmentHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOAssetHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOAssetTypeHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOAssetTypeValueHBM;
import lu.itrust.business.TS.database.dao.hbm.DAORiskProfileHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOScenarioHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOWordReportHBM;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.AddressRef;
import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.CellRef;
import lu.itrust.business.TS.helper.Column;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.messagehandler.TaskName;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.scale.ScaleType;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.scenario.ScenarioType;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.impl.MaturityMeasure;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
public class WorkerImportEstimation extends WorkerImpl {

	private int idAnalysis;

	private String username;

	private String filename;

	private DAOUser daoUser;

	private boolean assetOnly;

	private DAOAsset daoAsset;

	private boolean scenarioOnly;

	private DAOAnalysis daoAnalysis;

	private DAOScenario daoScenario;

	private DAOAssetType daoAssetType;

	private DAOWordReport daoWordReport;

	private DAOAssessment daoAssessment;

	private DAORiskProfile daoRiskProfile;

	private DAOAssetTypeValue daoAssetTypeValue;

	private final Pattern impactPattern = Pattern.compile("^i\\d+$");

	public WorkerImportEstimation(int idAnalysis, String username, String filename, boolean assetOnly, boolean scenarioOnly) {
		setUsername(username);
		setFilename(filename);
		setAssetOnly(assetOnly);
		setIdAnalysis(idAnalysis);
		setScenarioOnly(scenarioOnly);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.asynchronousWorkers.Worker#cancel()
	 */
	@Override
	public void cancel() {
		try {
			if (isWorking() && !isCanceled()) {
				synchronized (this) {
					if (isWorking() && !isCanceled()) {
						if (getCurrent() == null)
							Thread.currentThread().interrupt();
						else
							getCurrent().interrupt();
						setCanceled(true);
					}
				}
			}
		} catch (Exception e) {
			setError(e);
			TrickLogManager.Persist(e);
		} finally {
			cleanUp();
		}
	}

	public DAOAnalysis getDaoAnalysis() {
		return daoAnalysis;
	}

	public DAOAssessment getDaoAssessment() {
		return daoAssessment;
	}

	public DAOAsset getDaoAsset() {
		return daoAsset;
	}

	public DAORiskProfile getDaoRiskProfile() {
		return daoRiskProfile;
	}

	public DAOScenario getDaoScenario() {
		return daoScenario;
	}

	public DAOUser getDaoUser() {
		return daoUser;
	}

	public DAOWordReport getDaoWordReport() {
		return daoWordReport;
	}

	public int getIdAnalysis() {
		return idAnalysis;
	}

	public String getUsername() {
		return username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Session session = null;
		try {
			synchronized (this) {
				if (getWorkersPoolManager() != null && !getWorkersPoolManager().exist(getId()))
					if (!getWorkersPoolManager().add(this))
						return;
				if (isCanceled() || isWorking())
					return;
				setWorking(true);
				setStarted(new Timestamp(System.currentTimeMillis()));
				if (isAssetOnly())
					setName(TaskName.IMPORT_ASSET);
				else if (isScenarioOnly())
					setName(TaskName.IMPORT_SCENARIO);
				else
					setName(TaskName.IMPORT_RISK_ESTIMATION);
				setCurrent(Thread.currentThread());
			}
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.initialise.data", null, "Initialising risk analysis data", 5));
			session = getSessionFactory().openSession();
			initialiseDAO(session);
			session.beginTransaction();
			processing();
			session.getTransaction().commit();
			MessageHandler messageHandler = isAssetOnly() ? new MessageHandler("success.import.asset", "Assets had been successfully imported", 100)
					: isScenarioOnly() ? new MessageHandler("success.import.scenario", "Risk scenarios had been successfully imported", 100)
							: new MessageHandler("success.import.risk.estimation", "Risk estimations had been successfully imported", 100);
			messageHandler.setAsyncCallbacks(new AsyncCallback("reloadAssetScenario"), new AsyncCallback("reloadAssetScenarioChart"),
					new AsyncCallback("reloadSection", "section_riskregister"), new AsyncCallback("riskEstimationUpdate", true));
			getServiceTaskFeedback().send(getId(), messageHandler);
		} catch (Exception e) {
			if (session != null) {
				try {
					if (session.getTransaction().getStatus().canRollback()) {
						session.getTransaction().rollback();
						System.out.println("here");
					}
				} catch (Exception e1) {
				}
			}
			MessageHandler messageHandler = null;
			if (e instanceof TrickException)
				messageHandler = new MessageHandler(((TrickException) e).getCode(), ((TrickException) e).getParameters(), e.getMessage(), e);
			else
				messageHandler = new MessageHandler("error.500.message", "Internal error", e);
			getServiceTaskFeedback().send(getId(), messageHandler);
			TrickLogManager.Persist(e);
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (Exception e) {
				}
			}
			cleanUp();
		}
	}

	public void setDaoAnalysis(DAOAnalysis daoAnalysis) {
		this.daoAnalysis = daoAnalysis;
	}

	public void setDaoAssessment(DAOAssessment daoAssessment) {
		this.daoAssessment = daoAssessment;
	}

	public void setDaoAsset(DAOAsset daoAsset) {
		this.daoAsset = daoAsset;
	}

	public void setDaoRiskProfile(DAORiskProfile daoRiskProfile) {
		this.daoRiskProfile = daoRiskProfile;
	}

	public void setDaoScenario(DAOScenario daoScenario) {
		this.daoScenario = daoScenario;
	}

	public void setDaoUser(DAOUser daoUser) {
		this.daoUser = daoUser;
	}

	public void setDaoWordReport(DAOWordReport daoWordReport) {
		this.daoWordReport = daoWordReport;
	}

	public void setIdAnalysis(int idAnalysis) {
		this.idAnalysis = idAnalysis;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.asynchronousWorkers.Worker#start()
	 */
	@Override
	public synchronized void start() {
		run();
	}

	private void cleanUp() {
		if (isWorking()) {
			synchronized (this) {
				if (isWorking()) {
					setWorking(false);
					setFinished(new Timestamp(System.currentTimeMillis()));
				}
			}
		}

		getServiceStorage().delete(getFilename());

		if (getWorkersPoolManager() != null)
			getWorkersPoolManager().remove(this);
	}

	private void importAsset(Analysis analysis, WorkbookPart workbook, Map<String, Sheet> sheets, DataFormatter formatter, int min, int max) throws Exception {
		final Sheet sheet = sheets.get("Assets");
		if (sheet == null)
			return;
		final SheetData sheetData = findSheet(workbook, sheet);
		if (sheetData == null)
			return;
		final TablePart table = findTable(sheetData, "Assets");
		if (table == null)
			throw new TrickException("error.import.data.table.not.found", "Table named `Assets` cannot be found!", "Assets");

		final MessageHandler handler = new MessageHandler("info.updating.asset", null, "Updating assets", min);
		getServiceTaskFeedback().send(getId(), handler);

		final Map<String, String> names = loadTypeNames(workbook, sheets, formatter, "AssetTypes");
		final AddressRef address = AddressRef.parse(table.getContents().getRef());
		final int size = (int) Math.min(address.getEnd().getRow() + 1, sheetData.getRow().size()), maxProgress = (max - min);
		if (size < 2 || table.getContents().getTableColumns().getTableColumn().size() < 2)
			throw new TrickException("error.import.data.table.no.data", "Table named `Asset` has not enough data!", "Asset");
		final List<String> columns = table.getContents().getTableColumns().getTableColumn().stream().map(c -> c.getName().toLowerCase()).collect(Collectors.toList());
		final int nameIndex = columns.indexOf("name"), odlNameIndex = columns.indexOf("old name");
		if (nameIndex == -1)
			throw new TrickException("error.import.data.no.column", "Name column cannot be found!", "Name");
		final Map<String, Asset> assets = analysis.getAssets().stream().collect(Collectors.toMap(e -> e.getName().trim(), Function.identity()));
		final Map<String, AssetType> assetTypes = daoAssetType.getAll().stream().collect(Collectors.toMap(e -> e.getName().trim(), Function.identity()));
		for (int i = 1; i < size; i++) {
			Row row = sheetData.getRow().get(i);
			String name = getString(row, nameIndex, formatter), oldName = getString(row, odlNameIndex, formatter);
			if (isEmpty(name))
				continue;
			Asset asset = assets.get(name.trim());
			if (asset == null) {
				if (isEmpty(oldName))
					asset = assets.get(oldName);
				if (asset == null) {
					analysis.getAssets().add(asset = new Asset(name.trim()));
					assets.put(asset.getName(), asset);
				}
			}

			for (int j = 0; j < columns.size(); j++) {
				if (j == nameIndex || j == odlNameIndex)
					continue;
				switch (columns.get(j)) {
				case "type":
					AssetType assetType = asset.getAssetType();
					String type = getString(row, j, formatter);
					if (!isEmpty(type)) {
						String typeName = names.get(type.trim());
						if (!isEmpty(typeName))
							assetType = assetTypes.get(typeName.trim());
						if (assetType == null)
							assetType = assetTypes.get(type.trim());
					}

					if (assetType == null) {
						if (asset.getAssetType() == null)
							throw new TrickException("error.import.data.asset.bad.type", String.format("Asset type cannot be found for asset `%s`!", name), name,
									new CellRef(i, j).toString());
					} else
						asset.setAssetType(assetType);
					break;
				case "selected":
					asset.setSelected(getBoolean(row, j));
					break;
				case "value":
					asset.setValue(getDouble(row, j, formatter) * 1000);
					break;
				case "hidden comment":
					asset.setHiddenComment(getString(row, j, formatter));
					break;
				case "comment":
					asset.setComment(getString(row, j, formatter));
					break;
				}
			}
			handler.setProgress((int) (min + ((double) i / (double) size) * maxProgress));
			getServiceTaskFeedback().send(getId(), handler);
		}

		TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.import.asset",
				String.format("Analysis: %s, version: %s, type: Asset", analysis.getIdentifier(), analysis.getVersion()), getUsername(), LogAction.IMPORT, analysis.getIdentifier(),
				analysis.getVersion());

	}

	private void importRiskEstimation(final Analysis analysis, ValueFactory factory, AssessmentAndRiskProfileManager riskProfileManager, final WorkbookPart workbook,
			final Map<String, Sheet> sheets, DataFormatter formatter, final int min, final int max) throws Exception, Docx4JException {
		final Sheet sheet = sheets.get("Risk estimation");
		if (sheet == null)
			return;
		SheetData sheetData = findSheet(workbook, sheet);
		if (sheetData == null)
			return;
		TablePart table = findTable(sheetData, "Risk_estimation");
		if (table == null)
			throw new TrickException("error.import.data.table.not.found", "Table named `Risk_estimation` cannot be found!", "Risk_estimation");
		final AddressRef address = AddressRef.parse(table.getContents().getRef());
		final int size = (int) Math.min(address.getEnd().getRow() + 1, sheetData.getRow().size()), maxProgress = max - min;
		if (size < 2 || table.getContents().getTableColumns().getTableColumn().size() < 3)
			return;
		final List<String> columns = table.getContents().getTableColumns().getTableColumn().stream().map(c -> c.getName().toLowerCase()).collect(Collectors.toList());
		final int assetIndex = columns.indexOf("asset"), scenarioIndex = columns.indexOf("scenario");
		if (assetIndex == -1)
			throw new TrickException("error.import.data.no.column", "Asset column cannot be found!", "Asset");
		if (scenarioIndex == -1)
			throw new TrickException("error.import.data.no.column", "Scenario column cannot be found!", "Scenario");

		MessageHandler handler = new MessageHandler("info.updating.risk.estimation", null, "Update risks estimation", min);
		getServiceTaskFeedback().send(getId(), handler);
		riskProfileManager.updateAssessment(analysis, factory);
		final boolean qualitative = analysis.isQualitative();
		final List<IValue> valuesToDelete = new LinkedList<>();
		final List<ScaleType> scaleTypes = analysis.findImpacts();
		final Map<String, String> columnsMapper = generateColumns(scaleTypes, qualitative, true, true, true).stream()
				.collect(Collectors.toMap(s -> s.getName().toLowerCase(), Column::getName));
		final Map<String, Assessment> assessments = analysis.getAssessments().stream().collect(Collectors.toMap(Assessment::getKeyName, Function.identity()));
		final Map<String, RiskProfile> riskProfiles = qualitative ? analysis.getRiskProfiles().stream().collect(Collectors.toMap(RiskProfile::getKeyName, Function.identity()))
				: Collections.emptyMap();
		final Map<String, RiskProfile> riskIDs = riskProfiles.values().stream().collect(Collectors.toMap(RiskProfile::getIdentifier, Function.identity()));
		final Map<String, Map<String, Measure>> measuresMapper = qualitative || !riskProfiles.isEmpty()
				? analysis.getAnalysisStandards().values().stream().flatMap(m -> m.getMeasures().stream())
						.collect(Collectors.groupingBy(m -> m.getMeasureDescription().getStandard().getName(),
								Collectors.mapping(Function.identity(), Collectors.toMap(m -> m.getMeasureDescription().getReference(), Function.identity()))))
				: Collections.emptyMap();
		final Map<String, Scenario> scenarios = analysis.getScenarios().stream().collect(Collectors.toMap(e -> e.getName().trim(), Function.identity()));
		final Map<String, ScaleType> scalesMapper = scaleTypes.stream().collect(Collectors.toMap(e -> e.getDisplayName().trim(), Function.identity()));
		final Map<String, Asset> assets = analysis.getAssets().stream().collect(Collectors.toMap(e -> e.getName().trim(), Function.identity()));
		riskProfiles.values().forEach(r -> r.setIdentifier(null));
		daoRiskProfile.resetRiskIdByIds(riskIDs.values().stream().filter(r -> r.getId() > 0).map(RiskProfile::getId).collect(Collectors.toList()));
		for (int i = 1; i < size; i++) {
			Row row = sheetData.getRow().get(i);
			String assetName = getString(row, assetIndex, formatter), scenarioName = getString(row, scenarioIndex, formatter);
			if (isEmpty(assetName) || isEmpty(scenarioName))
				continue;
			Assessment assessment = findOrCreateAssessment(analysis, assets.get(assetName), scenarios.get(scenarioName), factory, assessments, riskProfiles);
			if (assessment == null)
				continue;
			RiskProfile riskProfile = riskProfiles.isEmpty() ? null : riskProfiles.get(RiskProfile.keyName(assetName, scenarioName));
			for (int j = 0; j < columns.size(); j++) {
				String name = columnsMapper.get(columns.get(j));
				if (name == null || j == assetIndex || j == scenarioIndex)
					continue;
				String value = getString(row, j, formatter);
				switch (name) {
				case "Risk ID":
					if (riskProfile == null || isEmpty(value))
						continue;
					riskProfile.setIdentifier(value);
					riskIDs.remove(value);
					break;
				case "Response":
					if (riskProfile == null)
						continue;
					riskProfile.setRiskStrategy(parseResponse(value, riskProfile.getRiskStrategy()));
					break;
				case "Probability":
					final IValue probability = factory.findProb(value);
					if (assessment.getLikelihood() == null)
						assessment.setLikelihood(probability);
					else if (!(assessment.getLikelihood().merge(probability) || probability == null)) {
						valuesToDelete.add(assessment.getLikelihood());
						assessment.setLikelihood(probability);
					}
					break;
				case "Impact":
					if (!analysis.isQuantitative())
						continue;
					if (loadImpact(assessment, scalesMapper.get(name), value, 0d, factory, valuesToDelete))
						throw new TrickException("error.analysis.parameter.no.impact", "Internal error: something wrong with impacts");
					break;
				case "Uncertainty":
					assessment.setUncertainty(ValueFactory.ToDouble(value, assessment.getUncertainty()));
					break;
				case "Owner":
					assessment.setOwner(value);
					break;
				case "Comment":
					assessment.setComment(value);
					break;
				case "Hidden comment":
					assessment.setHiddenComment(value);
					break;
				case "Security measures":
					if (riskProfile == null)
						continue;
					riskProfile.setRiskTreatment(value);
					break;
				case "Measures":
					loadMeasures(riskProfile, value, measuresMapper);
					break;
				case "Action plan":
					if (riskProfile == null)
						continue;
					riskProfile.setActionPlan(value);
					break;
				default:
					ScaleType type = scalesMapper.get(name);
					if (type != null)
						loadImpact(assessment, type, value, 0, factory, valuesToDelete);
					else if (riskProfile != null) {
						RiskProbaImpact probaImpact = null;
						String subName = null;
						if (name.startsWith("RAW")) {
							if (riskProfile.getRawProbaImpact() == null)
								riskProfile.setRawProbaImpact(new RiskProbaImpact());
							probaImpact = riskProfile.getRawProbaImpact();
							subName = name.replace("RAW", "").trim();
						} else if (name.startsWith("EXP")) {
							if (riskProfile.getExpProbaImpact() == null)
								riskProfile.setExpProbaImpact(new RiskProbaImpact());
							probaImpact = riskProfile.getExpProbaImpact();
							subName = name.replace("EXP", "").trim();
						}
						if (subName == null || probaImpact == null || Constant.DEFAULT_IMPACT_NAME.equalsIgnoreCase(subName))
							break;
						if (subName.equals("Probability"))
							probaImpact.setProbability((LikelihoodParameter) factory.findProbParameter(value));
						else {
							type = scalesMapper.get(subName);
							if (type != null)
								probaImpact.add((ImpactParameter) factory.findParameter(parseValue(type, value), type.getName()));
						}
					}
				}
				handler.setProgress((int) (min + ((double) i / (double) size) * maxProgress));
				getServiceTaskFeedback().send(getId(), handler);
			}
		}

		riskIDs.entrySet().stream().filter(e -> e.getValue().getIdentifier() == null).forEach(e -> e.getValue().setIdentifier(e.getKey()));

		valuesToDelete.forEach(v -> daoAssessment.delete(v));

		TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.import.risk.estimation",
				String.format("Analysis: %s, version: %s, type: Risk estimation", analysis.getIdentifier(), analysis.getVersion()), getUsername(), LogAction.IMPORT,
				analysis.getIdentifier(), analysis.getVersion());
	}

	private Assessment findOrCreateAssessment(Analysis analysis, Asset asset, Scenario scenario, ValueFactory factory, Map<String, Assessment> assessments,
			Map<String, RiskProfile> riskProfiles) {
		if (asset == null || scenario == null)
			return null;
		Assessment assessment = assessments.get(Assessment.keyName(asset, scenario));
		if (assessment != null)
			return assessment;
		scenario.addApplicable(asset);
		assessment = AssessmentAndRiskProfileManager.GenerateAssessment(analysis.getAssessments(), factory, asset, scenario);
		if (analysis.isQualitative() || !riskProfiles.isEmpty()) {
			RiskProfile riskProfile = riskProfiles.get(RiskProfile.keyName(asset, scenario));
			if (riskProfile == null)
				riskProfiles.put(RiskProfile.keyName(asset, scenario), AssessmentAndRiskProfileManager.GenerateRiskProfile(analysis.getRiskProfiles(), asset, scenario));
		}
		return assessment;
	}

	private void importScenario(Analysis analysis, WorkbookPart workbook, Map<String, Sheet> sheets, DataFormatter formatter, int min, int max) throws Exception {
		final Sheet sheet = sheets.get("Scenarios");
		if (sheet == null)
			return;
		SheetData sheetData = findSheet(workbook, sheet);
		if (sheetData == null)
			return;
		TablePart table = findTable(sheetData, "Scenarios");
		if (table == null)
			throw new TrickException("error.import.data.table.not.found", "Table named `Scenarios` cannot be found!", "Scenarios");
		final MessageHandler handler = new MessageHandler("info.updating.scenario", null, "Updating scenarios", min);
		getServiceTaskFeedback().send(getId(), handler);
		final AddressRef address = AddressRef.parse(table.getContents().getRef());
		final int size = (int) Math.min(address.getEnd().getRow() + 1, sheetData.getRow().size()), maxProgress = max - min;
		if (size < 2 || table.getContents().getTableColumns().getTableColumn().size() < 2)
			throw new TrickException("error.import.data.table.no.data", "Table named `Scenario` has not enough data!", "Scenario");
		final List<String> columns = table.getContents().getTableColumns().getTableColumn().stream().map(c -> c.getName().toLowerCase()).collect(Collectors.toList());
		final int nameIndex = columns.indexOf("name"), odlNameIndex = columns.indexOf("old name");
		if (nameIndex == -1)
			throw new TrickException("error.import.data.no.column", "Name column cannot be found!", "Name");

		final Map<String, String> names = loadTypeNames(workbook, sheets, formatter, "ScenarioTypes");
		final Map<String, ScenarioType> scenarioTypes = new LinkedHashMap<>();
		final List<AssetType> assetTypes = daoAssetType.getAll();
		for (ScenarioType scenarioType : ScenarioType.values())
			scenarioTypes.put(scenarioType.getName(), scenarioType);

		final Map<String, Scenario> scenarios = analysis.getScenarios().stream().collect(Collectors.toMap(e -> e.getName().trim(), Function.identity()));
		for (int i = 1; i < size; i++) {
			Row row = sheetData.getRow().get(i);
			String name = getString(row, nameIndex, formatter), oldName = getString(row, odlNameIndex, formatter);
			if (isEmpty(name))
				continue;
			Scenario scenario = scenarios.get(name.trim());
			
			if (scenario == null) {
				if (isEmpty(oldName))
					scenario = scenarios.get(oldName);
				if (scenario == null) {
					analysis.getScenarios().add(scenario = new Scenario(name));
					scenarios.put(name, scenario);
				}
			}
			
			for (int j = 0; j < columns.size(); j++) {
				if (j == nameIndex || j == odlNameIndex)
					continue;
				switch (columns.get(j)) {
				case "type":
					ScenarioType scenarioType = scenario.getType();
					String type = getString(row, j, formatter);
					if (!isEmpty(type)) {
						String typeName = names.get(type);
						if (!isEmpty(typeName))
							scenarioType = scenarioTypes.get(typeName);
						if (scenarioType == null)
							scenarioType = scenarioTypes.get(type);
					}

					if (scenarioType == null) {
						if (scenario.getType() == null)
							throw new TrickException("error.import.data.scenario.bad.type", String.format("Scenario type cannot be found for scenario `%s`!", name), name,
									new CellRef(i, j).toString());
					} else if (scenario.getType() != scenarioType) {
						scenario.setType(scenarioType);
						for (String category : ScenarioType.JAVAKEYS)
							scenario.setCategoryValue(category, 0);
					}

					break;
				case "selected":
					scenario.setSelected(getBoolean(row, j));
					break;
				case "description":
					scenario.setDescription(getString(row, j, formatter));
					break;
				case "apply to":
					String value = getString(row, j, formatter);
					if (!isEmpty(value)) {
						if (value.equalsIgnoreCase("asset")) {
							while (!scenario.getAssetTypeValues().isEmpty())
								daoAssetTypeValue.delete(scenario.getAssetTypeValues().remove(0));
							scenario.setAssetLinked(true);
							break;
						} else if (value.equalsIgnoreCase("asset type")) {
							if (scenario.isAssetLinked())
								scenario.getLinkedAssets().clear();
							scenario.setAssetLinked(false);
						}
					}
					updateAssetTypeValue(assetTypes, scenario);
					break;
				}
			}
			handler.setProgress((int) (min + ((double) i / (double) size) * maxProgress));
			getServiceTaskFeedback().send(getId(), handler);

		}

		TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.import.scenario",
				String.format("Analysis: %s, version: %s, type: Scenario", analysis.getIdentifier(), analysis.getVersion()), getUsername(), LogAction.IMPORT,
				analysis.getIdentifier(), analysis.getVersion());
	}

	private void updateAssetTypeValue(final List<AssetType> assetTypes, final Scenario scenario) {
		if (scenario.isAssetLinked())
			return;
		final Map<AssetType, AssetTypeValue> values = scenario.getAssetTypeValues().stream().collect(Collectors.toMap(AssetTypeValue::getAssetType, Function.identity()));
		assetTypes.stream().filter(v -> !values.containsKey(v)).forEach(v -> scenario.add(new AssetTypeValue(v, 0)));
	}

	private void initialiseDAO(Session session) {
		setDaoAnalysis(new DAOAnalysisHBM(session));
		setDaoWordReport(new DAOWordReportHBM(session));
		setDaoAssessment(new DAOAssessmentHBM(session));
		setDaoAsset(new DAOAssetHBM(session));
		setDaoScenario(new DAOScenarioHBM(session));
		setDaoRiskProfile(new DAORiskProfileHBM(session));
		setDaoUser(new DAOUserHBM(session));
		daoAssetType = new DAOAssetTypeHBM(session);
		daoAssetTypeValue = new DAOAssetTypeValueHBM(session);
	}

	private boolean loadImpact(Assessment assessment, ScaleType type, String value, Number defaultValue, ValueFactory factory, List<IValue> valuesToDelete) {
		if (type == null)
			return false;
		IValue impact = assessment.getImpact(type.getName()), iValue = loadImpactValue(type, value, factory, defaultValue);
		if (impact == null) {
			if (iValue == null)
				return false;
			assessment.setImpact(iValue);
		} else {
			if (iValue == null)
				iValue = factory.findValue(defaultValue, type.getName());
			if (iValue == null)
				return true;
			if (!impact.merge(iValue)) {
				assessment.setImpact(iValue);
				valuesToDelete.add(impact);
			}
		}
		return false;
	}

	private IValue loadImpactValue(ScaleType type, String value, ValueFactory factory, Number defaultValue) {
		if (defaultValue instanceof Double) {
			Double tempValue = ValueFactory.ToDouble(value, null);
			if (tempValue != null)
				return factory.findValue(tempValue * 1000, type.getName());
		}
		return factory.findValue(parseValue(type, value), type.getName());
	}

	private boolean loadMeasures(RiskProfile riskProfile, String value, Map<String, Map<String, Measure>> measuresMapper) {
		if (riskProfile == null || value == null)
			return true;
		riskProfile.getMeasures().clear();
		String[] lines = value.trim().split("\n");
		Map<Integer, Boolean> contains = new HashMap<>();
		for (String line : lines) {
			String[] data = line.trim().split(":");
			if (data.length != 2)
				continue;
			Map<String, Measure> measures = measuresMapper.get(data[0].trim());
			if (measures == null)
				continue;
			String[] references = data[1].trim().split(";");
			for (String reference : references) {
				Measure measure = measures.get(reference.trim());
				if (measure == null || contains.containsKey(measure.getId()) || !measure.getMeasureDescription().isComputable() || measure instanceof MaturityMeasure)
					continue;
				contains.put(measure.getId(), riskProfile.getMeasures().add(measure));
			}
		}
		return false;
	}

	private Map<String, String> loadTypeNames(WorkbookPart workbook, Map<String, Sheet> sheets, DataFormatter formatter, String value) throws Exception {
		final Sheet sheet = sheets.get(value);
		if (sheet == null)
			throw new TrickException("error.import.data.sheet.not.found", "Worksheet named `" + value + "` cannot be found!", value);
		SheetData sheetData = findSheet(workbook, sheet);
		if (sheetData == null)
			throw new TrickException("error.import.data.sheet.no.data", "No data for worksheet named `" + value + "`", value);
		TablePart table = findTable(sheetData, value);
		if (table == null)
			throw new TrickException("error.import.data.table.not.found", "Table named `" + value + "` cannot be found!", value);
		final AddressRef address = AddressRef.parse(table.getContents().getRef());
		final int size = (int) Math.min(address.getEnd().getRow() + 1, sheetData.getRow().size());
		if (size < 2 || table.getContents().getTableColumns().getTableColumn().size() < 2)
			throw new TrickException("error.import.data.table.no.data", "Table named `" + value + "` has not enough data!", value);
		final List<String> columns = table.getContents().getTableColumns().getTableColumn().stream().map(c -> c.getName().toLowerCase()).collect(Collectors.toList());
		final int nameIndex = columns.indexOf("name"), displayNameIndex = columns.indexOf("display name");
		if (nameIndex == -1)
			throw new TrickException("error.import.data.no.column", "Name column cannot be found!", "Name");
		if (displayNameIndex == -1)
			throw new TrickException("error.import.data.no.column", "Display name column cannot be found!", "Display name");
		Map<String, String> names = new LinkedHashMap<>(size - 1);
		for (int i = 1; i < size; i++) {
			Row row = sheetData.getRow().get(i);
			String name = getString(row, nameIndex, formatter), displayName = getString(row, displayNameIndex, formatter);
			if (isEmpty(name) || isEmpty(displayName))
				continue;
			names.put(displayName, name);
		}
		return names;
	}

	private RiskStrategy parseResponse(String value, RiskStrategy defaultValue) {
		try {
			return value == null || value.length() == 0 ? defaultValue : RiskStrategy.valueOf(value.toUpperCase());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private String parseValue(ScaleType type, String value) {
		if (value == null)
			return null;
		Matcher matcher = impactPattern.matcher(value);
		return matcher.matches() ? value.replace("i", type.getAcronym()) : value;
	}

	private void processing() throws Exception {
		final User user = daoUser.get(username);
		final Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		if (user == null)
			throw new TrickException("error.user.not_found", "User cannot be found");
		final DataFormatter formatter = new DataFormatter();
		final SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(getServiceStorage().loadAsFile(getFilename()));
		final WorkbookPart workbook = mlPackage.getWorkbookPart();
		final Map<String, Sheet> sheets = workbook.getContents().getSheets().getSheet().parallelStream().collect(Collectors.toMap(Sheet::getName, Function.identity()));
		final ValueFactory factory = new ValueFactory(analysis.getParameters());
		final AssessmentAndRiskProfileManager riskProfileManager = new AssessmentAndRiskProfileManager().initialise(daoAnalysis, daoAsset, daoAssessment, daoRiskProfile,
				daoScenario);
		if (isAssetOnly())
			importAsset(analysis, workbook, sheets, formatter, 6, 90);
		else if (isScenarioOnly())
			importScenario(analysis, workbook, sheets, formatter, 6, 90);
		else {
			importAsset(analysis, workbook, sheets, formatter, 6, 35);
			importScenario(analysis, workbook, sheets, formatter, 35, 60);
			importRiskEstimation(analysis, factory, riskProfileManager, workbook, sheets, formatter, 60, 90);
		}
		getServiceTaskFeedback().send(getId(), new MessageHandler("info.compute.risk.esitmation", null, "Computing risk estimation", 91));
		riskProfileManager.updateAssessment(analysis, factory);
		getServiceTaskFeedback().send(getId(), new MessageHandler("info.saving.analysis", null, "Saving risk analysis", 93));
		daoAnalysis.saveOrUpdate(analysis);
		getServiceTaskFeedback().send(getId(), new MessageHandler("info.commit.transcation", "Commit transaction", 96));
	}

	public final static List<Column> generateColumns(List<ScaleType> scales, boolean qualitative, boolean hiddenComment, boolean rowColumn, boolean uncertainty) {
		List<Column> columns = new ArrayList<>();
		if (qualitative)
			columns.add(new Column("Risk ID"));
		columns.add(new Column("Asset"));
		columns.add(new Column("Scenario"));
		if (qualitative) {
			columns.add(new Column("Response"));
			if (rowColumn) {
				columns.add(new Column("RAW Probability"));
				for (ScaleType type : scales) {
					if (!type.getName().equals(Constant.DEFAULT_IMPACT_NAME))
						columns.add(new Column("RAW " + type.getDisplayName()));
				}
			}
			columns.add(new Column("Probability"));
			for (ScaleType type : scales)
				columns.add(new Column(type.getDisplayName()));
			columns.add(new Column("EXP Probability"));
			for (ScaleType type : scales) {
				if (!type.getName().equals(Constant.DEFAULT_IMPACT_NAME))
					columns.add(new Column("EXP " + type.getDisplayName()));
			}
		} else {
			columns.add(new Column("Probability"));
			columns.add(new Column("Impact"));
		}
		if (uncertainty)
			columns.add(new Column("Uncertainty"));
		columns.add(new Column("Owner"));
		columns.add(new Column("Comment"));
		if (hiddenComment)
			columns.add(new Column("Hidden comment"));
		if (qualitative) {
			columns.add(new Column("Security measures"));
			columns.add(new Column("Measures"));
			columns.add(new Column("Action plan"));
		}

		return columns;
	}

	public boolean isAssetOnly() {
		return assetOnly;
	}

	public void setAssetOnly(boolean assetOnly) {
		this.assetOnly = assetOnly;
	}

	public boolean isScenarioOnly() {
		return scenarioOnly;
	}

	public void setScenarioOnly(boolean scenarioOnly) {
		this.scenarioOnly = scenarioOnly;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}

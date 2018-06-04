/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.findSheet;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.findTable;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getString;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.isEmpty;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.TablePart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.STSheetState;
import org.xlsx4j.sml.Sheet;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.TS.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.TS.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOAssessment;
import lu.itrust.business.TS.database.dao.DAOAsset;
import lu.itrust.business.TS.database.dao.DAORiskProfile;
import lu.itrust.business.TS.database.dao.DAOScenario;
import lu.itrust.business.TS.database.dao.DAOUser;
import lu.itrust.business.TS.database.dao.DAOWordReport;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOAssessmentHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOAssetHBM;
import lu.itrust.business.TS.database.dao.hbm.DAORiskProfileHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOScenarioHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOWordReportHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.AddressRef;
import lu.itrust.business.TS.helper.Column;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.messagehandler.TaskName;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.scale.ScaleType;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.impl.MaturityMeasure;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
public class WorkerImportEstimation extends WorkerImpl {

	private File workFile;

	private int idAnalysis;

	private String username;

	private ServiceTaskFeedback serviceTaskFeedback;

	private DAOUser daoUser;

	private DAOAsset daoAsset;

	private DAOAnalysis daoAnalysis;

	private DAOScenario daoScenario;

	private DAOWordReport daoWordReport;

	private DAOAssessment daoAssessment;

	private DAORiskProfile daoRiskProfile;

	private final Pattern impactPattern = Pattern.compile("^i\\d+$");

	public WorkerImportEstimation(int idAnalysis, String username, File workFile, ServiceTaskFeedback feedback, WorkersPoolManager poolManager, SessionFactory sessionFactory) {
		super(poolManager, sessionFactory);
		setIdAnalysis(idAnalysis);
		setUsername(username);
		setWorkFile(workFile);
		setServiceTaskFeedback(feedback);
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

	private void cleanUp() {
		if (isWorking()) {
			synchronized (this) {
				if (isWorking()) {
					setWorking(false);
					setFinished(new Timestamp(System.currentTimeMillis()));
				}
			}
		}
		if (workFile != null && workFile.exists()) {
			if (!workFile.delete())
				workFile.deleteOnExit();
		}
		if (getPoolManager() != null)
			getPoolManager().remove(this);
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
				if (getPoolManager() != null && !getPoolManager().exist(getId()))
					if (!getPoolManager().add(this))
						return;
				if (isCanceled() || isWorking())
					return;
				setWorking(true);
				setStarted(new Timestamp(System.currentTimeMillis()));
				setName(TaskName.EXPORT_RISK_REGISTER);
				setCurrent(Thread.currentThread());
			}
			serviceTaskFeedback.send(getId(), new MessageHandler("info.initialise.data", null, "Initialising risk analysis data", 5));
			session = getSessionFactory().openSession();
			initialiseDAO(session);
			session.beginTransaction();
			processing();
			session.getTransaction().commit();
			MessageHandler messageHandler = new MessageHandler("success.import.risk.estimation", "Risk estimation has been successfully imported", 100);
			messageHandler.setAsyncCallbacks(new AsyncCallback("reloadAssetScenario"), new AsyncCallback("reloadAssetScenarioChart"),
					new AsyncCallback("reloadSection", "section_riskregister"), new AsyncCallback("riskEstimationUpdate", true));
			serviceTaskFeedback.send(getId(), messageHandler);
		} catch (Exception e) {
			if (session != null) {
				try {
					if (session.beginTransaction().getStatus().canRollback())
						session.beginTransaction().rollback();
				} catch (Exception e1) {
				}
			}
			MessageHandler messageHandler = null;
			if (e instanceof TrickException)
				messageHandler = new MessageHandler(((TrickException) e).getCode(), ((TrickException) e).getParameters(), e.getMessage(), e);
			else
				messageHandler = new MessageHandler("error.500.message", "Internal error", e);
			serviceTaskFeedback.send(getId(), messageHandler);
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

	private void initialiseDAO(Session session) {
		setDaoAnalysis(new DAOAnalysisHBM(session));
		setDaoWordReport(new DAOWordReportHBM(session));
		setDaoAssessment(new DAOAssessmentHBM(session));
		setDaoAsset(new DAOAssetHBM(session));
		setDaoScenario(new DAOScenarioHBM(session));
		setDaoRiskProfile(new DAORiskProfileHBM(session));
		setDaoUser(new DAOUserHBM(session));
	}

	private void processing() throws Exception {
		final User user = daoUser.get(username);
		final Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		if (user == null)
			throw new TrickException("error.user.not_found", "User cannot be found");
		final SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(workFile);
		final WorkbookPart workbook = mlPackage.getWorkbookPart();
		final Map<String, Sheet> sheets = workbook.getContents().getSheets().getSheet().parallelStream().filter(s -> s.getState() == STSheetState.VISIBLE)
				.collect(Collectors.toMap(Sheet::getName, Function.identity()));
		Sheet sheet = sheets.get("Risk estimation");
		if (sheet == null)
			return;
		SheetData sheetData = findSheet(workbook, sheet);
		if (sheetData == null)
			return;
		TablePart table = findTable(sheetData.getWorksheetPart(), "Risk_estimation");
		if (table == null)
			throw new TrickException("error.import.data.table.not.found", "Table named `Risk_estimation` cannot be found!", "Risk_estimation");
		final AddressRef address = AddressRef.parse(table.getContents().getRef());
		final int size = (int) Math.min(address.getEnd().getRow() + 1, sheetData.getRow().size());
		if (size < 2 || table.getContents().getTableColumns().getTableColumn().size() < 3)
			return;
		final List<String> columns = table.getContents().getTableColumns().getTableColumn().stream().map(c -> c.getName().toLowerCase()).collect(Collectors.toList());
		final int assetIndex = columns.indexOf("asset"), scenarioIndex = columns.indexOf("scenario"), min = 6, max = 90;
		if (assetIndex == -1)
			throw new TrickException("error.import.data.no.column", "Asset column cannot be found!", "Asset");
		if (scenarioIndex == -1)
			throw new TrickException("error.import.data.no.column", "Scenario column cannot be found!", "Scenario");

		MessageHandler handler = new MessageHandler("info.updating.risk.estimation", null, "Update risks estimation", min);
		serviceTaskFeedback.send(getId(), handler);

		final ValueFactory factory = new ValueFactory(analysis.getParameters());
		final AssessmentAndRiskProfileManager riskProfileManager = new AssessmentAndRiskProfileManager();
		riskProfileManager.initialise(daoAnalysis, daoAsset, daoAssessment, daoRiskProfile, daoScenario);
		riskProfileManager.updateAssessment(analysis, factory);
		final DataFormatter formatter = new DataFormatter();
		final boolean qualitative = analysis.isHybrid() || analysis.isQualitative();
		final List<ScaleType> scaleTypes = analysis.getImpacts();
		final Map<String, String> columnsMapper = generateColumns(scaleTypes, qualitative, true, true, true).stream()
				.collect(Collectors.toMap(s -> s.getName().toLowerCase(), Column::getName));
		final Map<String, Assessment> assessments = analysis.getAssessments().stream().collect(Collectors.toMap(Assessment::getKeyName, Function.identity()));
		final Map<String, RiskProfile> riskProfiles = qualitative ? analysis.getRiskProfiles().stream().collect(Collectors.toMap(RiskProfile::getKeyName, Function.identity()))
				: Collections.emptyMap();
		final Map<String, RiskProfile> riskIDs = riskProfiles.isEmpty() ? Collections.emptyMap()
				: analysis.getRiskProfiles().stream().collect(Collectors.toMap(RiskProfile::getIdentifier, Function.identity()));
		final Map<String, Map<String, Measure>> measuresMapper = riskProfiles.isEmpty() ? Collections.emptyMap()
				: analysis.getAnalysisStandards().stream().flatMap(m -> m.getMeasures().stream())
						.collect(Collectors.groupingBy(m -> m.getMeasureDescription().getStandard().getLabel(),
								Collectors.mapping(Function.identity(), Collectors.toMap(m -> m.getMeasureDescription().getReference(), Function.identity()))));
		final List<IValue> valuesToDelete = new LinkedList<>();
		final Map<String, ScaleType> scalesMapper = scaleTypes.stream().collect(Collectors.toMap(ScaleType::getDisplayName, Function.identity()));
		for (int i = 1; i < size; i++) {
			Row row = sheetData.getRow().get(i);
			String assetName = getString(row, assetIndex, formatter), scenarioName = getString(row, scenarioIndex, formatter);
			if (isEmpty(assetName) || isEmpty(scenarioName))
				continue;
			Assessment assessment = assessments.get(Assessment.keyName(assetName, scenarioName));
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
					if (riskProfile == null)
						continue;
					if (isEmpty(value))
						riskProfile.setIdentifier(null);
					else {
						riskProfile.setIdentifier(value);
						RiskProfile profile = riskIDs.get(value);
						if (!(profile == null || riskProfile.equals(profile)))
							riskProfile.setIdentifier(null);
					}
					break;
				case "Response":
					if (riskProfile == null)
						continue;

					riskProfile.setRiskStrategy(parseResponse(value, riskProfile.getRiskStrategy()));
					break;
				case "Probability":
					assessment.setLikelihood(value);
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
				handler.setProgress((int) (min + (i / (double) size) * max));
				serviceTaskFeedback.send(getId(), handler);
			}
		}
		valuesToDelete.forEach(v -> daoAssessment.delete(v));
		riskProfileManager.updateAssessment(analysis, factory);
		daoAnalysis.saveOrUpdate(analysis);
		TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.import.risk.estimation",
				String.format("Analysis: %s, version: %s, type: Risk estimation", analysis.getIdentifier(), analysis.getVersion()), getUsername(), LogAction.IMPORT,
				analysis.getIdentifier(), analysis.getVersion());
	}

	private RiskStrategy parseResponse(String value, RiskStrategy defaultValue) {
		try {
			return RiskStrategy.valueOf(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private boolean loadMeasures(RiskProfile riskProfile, String value, Map<String, Map<String, Measure>> measuresMapper) {
		if (riskProfile == null)
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

	private String parseValue(ScaleType type, String value) {
		if (value == null)
			return null;
		Matcher matcher = impactPattern.matcher(value);
		return matcher.matches() ? value.replace("i", type.getAcronym()) : value;
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

	public File getWorkFile() {
		return workFile;
	}

	public void setWorkFile(File workFile) {
		this.workFile = workFile;
	}

	public int getIdAnalysis() {
		return idAnalysis;
	}

	public void setIdAnalysis(int idAnalysis) {
		this.idAnalysis = idAnalysis;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ServiceTaskFeedback getServiceTaskFeedback() {
		return serviceTaskFeedback;
	}

	public void setServiceTaskFeedback(ServiceTaskFeedback serviceTaskFeedback) {
		this.serviceTaskFeedback = serviceTaskFeedback;
	}

	public DAOUser getDaoUser() {
		return daoUser;
	}

	public void setDaoUser(DAOUser daoUser) {
		this.daoUser = daoUser;
	}

	public DAOAnalysis getDaoAnalysis() {
		return daoAnalysis;
	}

	public void setDaoAnalysis(DAOAnalysis daoAnalysis) {
		this.daoAnalysis = daoAnalysis;
	}

	public DAOWordReport getDaoWordReport() {
		return daoWordReport;
	}

	public void setDaoWordReport(DAOWordReport daoWordReport) {
		this.daoWordReport = daoWordReport;
	}

	public DAOAssessment getDaoAssessment() {
		return daoAssessment;
	}

	public void setDaoAssessment(DAOAssessment daoAssessment) {
		this.daoAssessment = daoAssessment;
	}

	public DAOAsset getDaoAsset() {
		return daoAsset;
	}

	public void setDaoAsset(DAOAsset daoAsset) {
		this.daoAsset = daoAsset;
	}

	public DAOScenario getDaoScenario() {
		return daoScenario;
	}

	public void setDaoScenario(DAOScenario daoScenario) {
		this.daoScenario = daoScenario;
	}

	public DAORiskProfile getDaoRiskProfile() {
		return daoRiskProfile;
	}

	public void setDaoRiskProfile(DAORiskProfile daoRiskProfile) {
		this.daoRiskProfile = daoRiskProfile;
	}
}

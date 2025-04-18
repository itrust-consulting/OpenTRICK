package lu.itrust.business.ts.asynchronousWorkers;

import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.findSheet;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.findTable;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getBoolean;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getDouble;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getInt;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getRow;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getString;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.isEmpty;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.TablePart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.hibernate.Session;
import org.springframework.util.StringUtils;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.Sheet;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.ts.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.ts.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAOAssessment;
import lu.itrust.business.ts.database.dao.DAOAsset;
import lu.itrust.business.ts.database.dao.DAOAssetType;
import lu.itrust.business.ts.database.dao.DAOAssetTypeValue;
import lu.itrust.business.ts.database.dao.DAORiskProfile;
import lu.itrust.business.ts.database.dao.DAOScaleType;
import lu.itrust.business.ts.database.dao.DAOScenario;
import lu.itrust.business.ts.database.dao.DAOUser;
import lu.itrust.business.ts.database.dao.DAOWordReport;
import lu.itrust.business.ts.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOAssessmentHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOAssetHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOAssetTypeHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOAssetTypeValueHBM;
import lu.itrust.business.ts.database.dao.hbm.DAORiskProfileHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOScaleTypeHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOScenarioHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOWordReportHBM;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.AddressRef;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.CellRef;
import lu.itrust.business.ts.helper.Column;
import lu.itrust.business.ts.helper.DependencyGraphManager;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.messagehandler.TaskName;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.assessment.Assessment;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.asset.AssetType;
import lu.itrust.business.ts.model.cssf.RiskProbaImpact;
import lu.itrust.business.ts.model.cssf.RiskProfile;
import lu.itrust.business.ts.model.cssf.RiskStrategy;
import lu.itrust.business.ts.model.general.AssetTypeValue;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.ilr.AssetEdge;
import lu.itrust.business.ts.model.ilr.AssetImpact;
import lu.itrust.business.ts.model.ilr.AssetNode;
import lu.itrust.business.ts.model.ilr.ILRImpact;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.parameter.impl.ImpactParameter;
import lu.itrust.business.ts.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.ts.model.parameter.value.IValue;
import lu.itrust.business.ts.model.scale.ScaleType;
import lu.itrust.business.ts.model.scenario.Scenario;
import lu.itrust.business.ts.model.scenario.ScenarioType;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measure.impl.MaturityMeasure;
import lu.itrust.business.ts.usermanagement.User;

/**
 * This class represents a worker for importing risk estimations, assets, or
 * scenarios.
 * It extends the WorkerImpl class and implements the Runnable interface.
 * The worker is responsible for importing data from a file and updating the
 * risk analysis accordingly.
 * The import can be performed for assets only, scenarios only, or both assets
 * and scenarios.
 * The worker runs in a separate thread and provides feedback on the progress of
 * the import task.
 * The worker uses various DAOs (Data Access Objects) to interact with the
 * database and perform the necessary operations.
 * The import process involves reading data from a file, parsing it, and
 * updating the corresponding entities in the database.
 * The worker also handles exceptions and provides error messages in case of any
 * issues during the import process.
 */
public class WorkerImportEstimation extends WorkerImpl {

	/**
	 *
	 */
	private static final String PROBABILITY = "Probability";

	/**
	 *
	 */
	private static final String VULNERABILITY = "Vulnerability";

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

	private DAOScaleType daoScaleType;

	private final Pattern impactPattern = Pattern.compile("^i\\d+$");

	public WorkerImportEstimation(int idAnalysis, String username, String filename, boolean assetOnly,
			boolean scenarioOnly) {
		setUsername(username);
		setFilename(filename);
		setAssetOnly(assetOnly);
		setIdAnalysis(idAnalysis);
		setScenarioOnly(scenarioOnly);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.asynchronousWorkers.Worker#cancel()
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
			TrickLogManager.persist(e);
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
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.initialise.data", null, "Initialising risk analysis data", 5));
			session = getSessionFactory().openSession();
			initialiseDAO(session);
			session.beginTransaction();
			processing();
			session.getTransaction().commit();
			MessageHandler messageHandler = isAssetOnly()
					? new MessageHandler("success.import.asset", "Assets had been successfully imported", 100)
					: isScenarioOnly()
							? new MessageHandler("success.import.scenario",
									"Risk scenarios had been successfully imported", 100)
							: new MessageHandler("success.import.risk.estimation",
									"Risk estimations had been successfully imported", 100);
			messageHandler.setAsyncCallbacks(new AsyncCallback("reloadAssetScenario"),
					new AsyncCallback("reloadAssetScenarioChart"),
					new AsyncCallback("reloadSection", "section_riskregister"),
					new AsyncCallback("riskEstimationUpdate", true));
			getServiceTaskFeedback().send(getId(), messageHandler);
		} catch (Exception e) {
			if (session != null) {
				try {
					if (session.getTransaction().getStatus().canRollback()) {
						session.getTransaction().rollback();
					}
				} catch (Exception e1) {
				}
			}
			MessageHandler messageHandler = null;
			if (e instanceof TrickException)
				messageHandler = new MessageHandler(((TrickException) e).getCode(),
						((TrickException) e).getParameters(), e.getMessage(), e);
			else
				messageHandler = new MessageHandler("error.500.message", "Internal error", e);
			getServiceTaskFeedback().send(getId(), messageHandler);
			TrickLogManager.persist(e);
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
	 * @see lu.itrust.business.ts.asynchronousWorkers.Worker#start()
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

	private void importAsset(Analysis analysis, WorkbookPart workbook, Map<String, Sheet> sheets,
			DataFormatter formatter, int min, int max) throws Exception {
		final Sheet sheet = sheets.get("Assets");
		if (sheet == null)
			return;
		final SheetData sheetData = findSheet(workbook, sheet);
		if (sheetData == null)
			return;
		final TablePart table = findTable(sheetData, "Assets");
		if (table == null)
			throw new TrickException("error.import.data.table.not.found", "Table named `Assets` cannot be found!",
					"Assets");

		final MessageHandler handler = new MessageHandler("info.updating.asset", null, "Updating assets", min);
		getServiceTaskFeedback().send(getId(), handler);

		final Map<String, String> names = loadTypeNames(workbook, sheets, formatter, "AssetTypes");
		final AddressRef address = AddressRef.parse(table.getContents().getRef());
		final int size = (int) Math.min(address.getEnd().getRow() + 1, sheetData.getRow().size()),
				maxProgress = (max - min);
		if (size < 2 || table.getContents().getTableColumns().getTableColumn().size() < 2)
			throw new TrickException("error.import.data.table.no.data", "Table named `Asset` has not enough data!",
					"Asset");
		final List<String> columns = table.getContents().getTableColumns().getTableColumn().stream()
				.map(c -> c.getName().toLowerCase()).collect(Collectors.toList());
		final int nameIndex = columns.indexOf("name"), odlNameIndex = columns.indexOf("old name");
		if (nameIndex == -1)
			throw new TrickException("error.import.data.no.column", "Name column cannot be found!", "Name");

		final boolean isILR = Analysis.isILR(analysis);

		final Map<String, AssetNode> nodeByAssetNames = isILR
				? analysis.getAssetNodes().stream()
						.collect(
								Collectors.toMap(e -> e.getAsset().getName().toLowerCase().trim(), Function.identity()))
				: Collections.emptyMap();

		final Map<String, ScaleType> scaleTypes = daoScaleType.findAll().stream()
				.collect(Collectors.toMap(e -> e.getShortName().replace(".", "").toLowerCase(), Function.identity(),
						(e1, e2) -> e1));

		final Map<String, Asset> assets = analysis.getAssets().stream()
				.collect(Collectors.toMap(e -> e.getName().toLowerCase(), Function.identity()));

		final Map<String, AssetType> assetTypes = daoAssetType.getAll().stream()
				.collect(Collectors.toMap(e -> e.getName().trim(), Function.identity()));

		final Set<ScaleType> usedScales = new HashSet<>(analysis.getIlrImpactTypes());

		final Set<String> seenAssets = new HashSet<>();

		final int beginColumn = address.getBegin().getCol();

		final int endColumn = Math.max(address.getEnd().getCol(), columns.size());

		final int namePos = nameIndex + beginColumn;
		final int oldNamePos = odlNameIndex == -1 ? -1 : odlNameIndex + beginColumn;

		for (int i = address.getBegin().getRow() + 1; i < size; i++) {
			Row row = sheetData.getRow().get(i);
			String name = getString(row, namePos, formatter);
			if (isEmpty(name))
				continue;

			String oldName = getString(row, oldNamePos, formatter);

			Asset asset = assets.get(name.trim().toLowerCase());

			if (asset == null) {
				if (!isEmpty(oldName))
					asset = assets.get(oldName.trim().toLowerCase());
				if (asset == null) {
					asset = assets.computeIfAbsent(name.trim().toLowerCase(), k -> new Asset(name));
				} else
					asset.setName(name.trim());

			}

			if (seenAssets.contains(asset.getName().toLowerCase())) {
				final String cellString = new CellRef(i, 0).toString();
				throw new TrickException("error.import.data.asset.duplicated",
						String.format("Duplicated asset `%s`, see: %s", name, cellString), name, cellString);
			} else
				seenAssets.add(asset.getName().toLowerCase());

			final AssetNode node;
			final Asset myAsset = asset;

			if (isILR) {
				if (isEmpty(oldName) || !nodeByAssetNames.containsKey(oldName.trim().toLowerCase()))
					node = nodeByAssetNames.computeIfAbsent(asset.getName().trim().toLowerCase(),
							e -> new AssetNode(myAsset));
				else
					node = nodeByAssetNames.computeIfAbsent(oldName.trim().toLowerCase(), e -> new AssetNode());
			} else {
				node = null;
			}

			for (int j = beginColumn; j < endColumn; j++) {
				if (j == namePos || j == oldNamePos)
					continue;
				final String column = columns.get(j - beginColumn);
				switch (column) {
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
								throw new TrickException("error.import.data.asset.bad.type",
										String.format("Asset type cannot be found for asset `%s`!", name), name,
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
					case "related":
					case "Related":
					case "related name":
					case "Related name":
						asset.setRelatedName(getString(row, j, formatter));
						break;
					case "comment":
						asset.setComment(getString(row, j, formatter));
						break;
					default:
						if (isILR && (column.startsWith("c-") || column.startsWith("i-") || column.startsWith("a-"))) {
							final String scaleAcronym = column.substring(2);
							final ScaleType scaleType = scaleTypes.get(scaleAcronym);

							if (scaleType == null) {
								handler.update("error.import.asset.scale.not_found",
										String.format("The impact scale the with acronym '%s' cannot be found!",
												scaleAcronym),
										handler.getProgress());
								getServiceTaskFeedback().send(getId(), handler);
							} else {

								final int impact = getInt(row, j, -2, formatter);

								final AssetImpact assetImpact = node.getImpact();

								usedScales.add(scaleType);

								if (impact > -2) {
									switch (column.charAt(0)) {
										case 'c': {
											final ILRImpact ilrImpact = assetImpact.getConfidentialityImpacts()
													.computeIfAbsent(scaleType, k -> new ILRImpact(scaleType));
											ilrImpact.setValue(impact);
											break;
										}
										case 'i': {
											final ILRImpact ilrImpact = assetImpact.getIntegrityImpacts()
													.computeIfAbsent(scaleType, k -> new ILRImpact(scaleType));
											ilrImpact.setValue(impact);

											break;
										}
										default: {
											final ILRImpact ilrImpact = assetImpact.getAvailabilityImpacts()
													.computeIfAbsent(scaleType, k -> new ILRImpact(scaleType));
											ilrImpact.setValue(impact);

										}
									}

								}
							}

						}
				}
			}

			handler.setProgress((int) (min + ((double) i / (double) size) * maxProgress));
			getServiceTaskFeedback().send(getId(), handler);
		}

		assets.values().stream().filter(e -> e.getId() < 1).forEach(e -> {
			if (e.getAssetType() == null)
				nodeByAssetNames.values().removeIf(j -> j.getAsset() == null || j.getAsset().equals(e));
			else
				analysis.add(e);

		});

		nodeByAssetNames.values().stream().filter(n -> n.getId() < 1).forEach(e -> analysis.getAssetNodes().add(e));

		usedScales.stream().filter(e -> !analysis.getIlrImpactTypes().contains(e))
				.forEach(e -> analysis.getIlrImpactTypes().add(e));

		TrickLogManager.persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.import.asset",
				String.format("Analysis: %s, version: %s, type: Asset", analysis.getIdentifier(),
						analysis.getVersion()),
				getUsername(), LogAction.IMPORT, analysis.getIdentifier(),
				analysis.getVersion());

	}

	/**
	 * Imports asset dependencies from the specified sheet in the workbook.
	 * 
	 * @param analysis  The analysis object.
	 * @param workbook  The workbook containing the data.
	 * @param sheets    The map of sheet names to sheet objects.
	 * @param formatter The data formatter for parsing cell values.
	 * @param min       The minimum progress value for the message handler.
	 * @param max       The maximum progress value for the message handler.
	 * @throws Exception If an error occurs during the import process.
	 */
	private void importAssetDependancy(Analysis analysis, WorkbookPart workbook, Map<String, Sheet> sheets,
			DataFormatter formatter, int min, int max) throws Exception {
		final Sheet sheet = sheets.get("Dependency");
		if (sheet == null)
			return;
		final SheetData sheetData = findSheet(workbook, sheet);
		if (sheetData == null)
			return;
		final TablePart table = findTable(sheetData, "Table_dep");
		if (table == null)
			throw new TrickException("error.import.data.table.not.found", "Table named `Table_dep` cannot be found!",
					"Table_dep");

		final MessageHandler handler = new MessageHandler("info.updating.asset_dependancy", null,
				"Updating asset dependancies", min);
		getServiceTaskFeedback().send(getId(), handler);

		final AddressRef address = AddressRef.parse(table.getContents().getRef());

		final int size = Math.max(address.getEnd().getRow() + 1, sheetData.getRow().size());

		final Map<String, Asset> assets = analysis.getAssets().stream()
				.collect(Collectors.toMap(e -> e.getName().toLowerCase(), Function.identity()));

		final Map<String, AssetNode> nodes = analysis.getAssetNodes().stream()
				.collect(Collectors.toMap(e -> e.getAsset().getName().toLowerCase(), Function.identity()));

		final Map<AssetNode, Map<AssetNode, AssetEdge>> oldDependancies = analysis.getAssetNodes().stream()
				.collect(Collectors.toMap(Function.identity(),
						e -> e.getEdges().values().stream()
								.collect(Collectors.toMap(AssetEdge::getChild, Function.identity()))));

		if (size < 2 || table.getContents().getTableColumns().getTableColumn().size() < 2)
			throw new TrickException("error.import.data.table.no.data", "Table named `Table_dep` has not enough data!",
					"Asset");
		final List<String> columns = table.getContents().getTableColumns().getTableColumn().stream()
				.map(c -> c.getName().trim().toLowerCase()).collect(Collectors.toList());

		final int nameIndex = columns.indexOf("assetlist");
		final int assetTypeIndex = columns.indexOf("assettype");
		final int assetComment  = columns.indexOf("assetcomment");
		if (nameIndex == -1)
			throw new TrickException("error.import.data.no.column", "AssetList column cannot be found!", "AssetList");

		final int headerRowIndex = address.getBegin().getRow();

		for (int i = headerRowIndex + 1; i < size; i++) {
			final Row row = getRow(sheetData, i);
			final String name = getString(row, nameIndex, formatter);
			if (isEmpty(name))
				continue;
			final Asset asset = assets.get(name.trim().toLowerCase());
			if (asset == null) {
				final String myCell = new CellRef(i, nameIndex).toString();
				throw new TrickException("error.import.data.asset.not_found",
						String.format("Asset `%s` cannot be found! See cell `%s`", name, myCell), name,
						myCell);
			}

			final AssetNode node = nodes.computeIfAbsent(asset.getName().toLowerCase(), k -> new AssetNode(asset));
			final Map<AssetNode, AssetEdge> oldEdges = oldDependancies.get(node);
			for (int j = 0; j < columns.size(); j++) {
				if (nameIndex == j || assetTypeIndex == j || assetComment == j)
					continue;
				final String childName = columns.get(j);
				final Asset childAsset = assets.get(childName);
				if (childAsset == null) {
					final String myCell = new CellRef(headerRowIndex, j).toString();
					throw new TrickException("error.import.data.asset.not_found",
							String.format("Asset `%s` cannot be found! See cell `%s`", childName, myCell),
							childName,
							myCell);
				} else if (!childAsset.equals(asset)) {

					final double weight = getDouble(row, j, 0d, formatter);

					final AssetNode childNode = nodes.computeIfAbsent(childName, k -> new AssetNode(childAsset));

					if (Math.abs(weight - 0d) > 1e-9) {
						AssetEdge assetEdge = oldEdges == null ? null : oldEdges.remove(childNode);
						if (assetEdge == null)
							node.getEdges().put(childNode, new AssetEdge(node, childNode, weight));
						else
							assetEdge.setWeight(weight);
					}
				}

			}
		}

		nodes.values().stream().filter(e -> e.getId() < 1).forEach(e -> analysis.getAssetNodes().add(e));

		oldDependancies.values().stream().flatMap(e -> e.values().stream())
				.forEach(e -> e.getParent().getEdges().remove(e.getChild()));

	}

	/**
	 * Imports risk estimation data from a specified sheet in a workbook.
	 *
	 * @param analysis           The analysis object.
	 * @param factory            The value factory.
	 * @param riskProfileManager The assessment and risk profile manager.
	 * @param workbook           The workbook containing the data.
	 * @param sheets             The map of sheet names to sheet objects.
	 * @param formatter          The data formatter.
	 * @param min                The minimum progress value.
	 * @param max                The maximum progress value.
	 * @throws Exception       If an error occurs during the import process.
	 * @throws Docx4JException If an error occurs while working with the workbook.
	 */
	private void importRiskEstimation(final Analysis analysis, ValueFactory factory,
			AssessmentAndRiskProfileManager riskProfileManager, final WorkbookPart workbook,
			final Map<String, Sheet> sheets, DataFormatter formatter, final int min, final int max)
			throws Exception, Docx4JException {
		final Sheet sheet = sheets.get("Risk estimation");
		if (sheet == null)
			return;
		SheetData sheetData = findSheet(workbook, sheet);
		if (sheetData == null)
			return;
		TablePart table = findTable(sheetData, "Risk_estimation");
		if (table == null)
			throw new TrickException("error.import.data.table.not.found",
					"Table named `Risk_estimation` cannot be found!", "Risk_estimation");
		final AddressRef address = AddressRef.parse(table.getContents().getRef());
		final int size = (int) Math.min(address.getEnd().getRow() + 1, sheetData.getRow().size()),
				maxProgress = max - min;
		if (size < 2 || table.getContents().getTableColumns().getTableColumn().size() < 3)
			return;
		final List<String> columns = table.getContents().getTableColumns().getTableColumn().stream()
				.map(c -> c.getName().toLowerCase()).collect(Collectors.toList());
		final int assetIndex = columns.indexOf("asset"), scenarioIndex = columns.indexOf("scenario");
		if (assetIndex == -1)
			throw new TrickException("error.import.data.no.column", "Asset column cannot be found!", "Asset");
		if (scenarioIndex == -1)
			throw new TrickException("error.import.data.no.column", "Scenario column cannot be found!", "Scenario");

		MessageHandler handler = new MessageHandler("info.updating.risk.estimation", null, "Update risks estimation",
				min);
		getServiceTaskFeedback().send(getId(), handler);
		riskProfileManager.updateAssessment(analysis, factory);
		final boolean qualitative = analysis.isQualitative();
		final List<IValue> valuesToDelete = new LinkedList<>();
		final List<ScaleType> scaleTypes = analysis.findImpacts();
		final Map<String, String> columnsMapper = generateColumns(scaleTypes, qualitative, true, true, true, true)
				.stream()
				.collect(Collectors.toMap(s -> s.getName().toLowerCase(), Column::getName));
		final Map<String, Assessment> assessments = analysis.getAssessments().stream()
				.collect(Collectors.toMap(Assessment::getKeyName, Function.identity()));
		final Map<String, RiskProfile> riskProfiles = qualitative
				? analysis.getRiskProfiles().stream()
						.collect(Collectors.toMap(RiskProfile::getKeyName, Function.identity()))
				: Collections.emptyMap();
		final Map<String, RiskProfile> riskIDs = riskProfiles.values().stream()
				.collect(Collectors.toMap(RiskProfile::getIdentifier, Function.identity()));
		final Map<String, Map<String, Measure>> measuresMapper = qualitative || !riskProfiles.isEmpty()
				? analysis.getAnalysisStandards().values().stream().flatMap(m -> m.getMeasures().stream())
						.collect(Collectors.groupingBy(m -> m.getMeasureDescription().getStandard().getName(),
								Collectors.mapping(Function.identity(),
										Collectors.toMap(m -> m.getMeasureDescription().getReference(),
												Function.identity()))))
				: Collections.emptyMap();
		final Map<String, Scenario> scenarios = analysis.getScenarios().stream()
				.collect(Collectors.toMap(e -> e.getName().trim(), Function.identity()));
		final Map<String, ScaleType> scalesMapper = scaleTypes.stream()
				.collect(Collectors.toMap(e -> e.getDisplayName().trim(), Function.identity()));
		final Map<String, Asset> assets = analysis.getAssets().stream()
				.collect(Collectors.toMap(e -> e.getName().trim(), Function.identity()));
		riskProfiles.values().forEach(r -> r.setIdentifier(null));
		daoRiskProfile.resetRiskIdByIds(riskIDs.values().stream().filter(r -> r.getId() > 0).map(RiskProfile::getId)
				.collect(Collectors.toList()));
		for (int i = 1; i < size; i++) {
			Row row = sheetData.getRow().get(i);
			String assetName = getString(row, assetIndex, formatter),
					scenarioName = getString(row, scenarioIndex, formatter);
			if (isEmpty(assetName) || isEmpty(scenarioName))
				continue;
			final Assessment assessment = findOrCreateAssessment(analysis, assets.get(assetName), scenarios.get(scenarioName),
					factory, assessments, riskProfiles);
			if (assessment == null)
				continue;
			final RiskProfile riskProfile = riskProfiles.isEmpty() ? null
					: riskProfiles.get(RiskProfile.keyName(assetName, scenarioName));
	
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
					case PROBABILITY:
						final IValue probability = factory.findProb(value);
						if (assessment.getLikelihood() == null)
							assessment.setLikelihood(probability);
						else if (!(assessment.getLikelihood().merge(probability) || probability == null)) {
							valuesToDelete.add(assessment.getLikelihood());
							assessment.setLikelihood(probability);
						}
						break;
					case VULNERABILITY:
						assessment.setVulnerability(ValueFactory.toInt(value.toLowerCase().replace("v", "").trim(),
								assessment.getVulnerability()));
						break;
					case "Impact":
						if (!analysis.isQuantitative())
							continue;
						if (loadImpact(assessment, scalesMapper.get(name), value, 0d, factory, valuesToDelete))
							throw new TrickException("error.analysis.parameter.no.impact",
									"Internal error: something wrong with impacts");
						break;
					case "Uncertainty":
						assessment.setUncertainty(ValueFactory.toDouble(value, assessment.getUncertainty()));
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
					case "Cockpit":
						assessment.setCockpit(value);
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
							if (subName == null || probaImpact == null
									|| Constant.DEFAULT_IMPACT_NAME.equalsIgnoreCase(subName))
								break;
							if (subName.equals(PROBABILITY))
								probaImpact.setProbability((LikelihoodParameter) factory.findProbParameter(value));
							else if (subName.equals(VULNERABILITY))
								probaImpact.setVulnerability(ValueFactory.toInt(
										value.toLowerCase().replace("v", "").trim(), probaImpact.getVulnerability()));
							else {
								type = scalesMapper.get(subName);
								if (type != null)
									probaImpact.add((ImpactParameter) factory.findParameter(parseValue(type, value),
											type.getName()));
							}
						}
				}
				handler.setProgress((int) (min + ((double) i / (double) size) * maxProgress));
				getServiceTaskFeedback().send(getId(), handler);
			}
		}

		riskIDs.entrySet().stream().filter(e -> e.getValue().getIdentifier() == null)
				.forEach(e -> e.getValue().setIdentifier(e.getKey()));

		valuesToDelete.forEach(v -> daoAssessment.delete(v));

		TrickLogManager.persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.import.risk.estimation",
				String.format("Analysis: %s, version: %s, type: Risk estimation", analysis.getIdentifier(),
						analysis.getVersion()),
				getUsername(), LogAction.IMPORT,
				analysis.getIdentifier(), analysis.getVersion());
	}

	/**
	 * Represents an assessment for a specific asset and scenario.
	 */
	private Assessment findOrCreateAssessment(Analysis analysis, Asset asset, Scenario scenario, ValueFactory factory,
			Map<String, Assessment> assessments,
			Map<String, RiskProfile> riskProfiles) {
		if (asset == null || scenario == null)
			return null;
		Assessment assessment = assessments.get(Assessment.keyName(asset, scenario));
		if (assessment != null)
			return assessment;
		scenario.addApplicable(asset);
		assessment = AssessmentAndRiskProfileManager.GenerateAssessment(analysis.getAssessments(), factory, asset,
				scenario);
		if (analysis.isQualitative() || !riskProfiles.isEmpty()) {
			RiskProfile riskProfile = riskProfiles.get(RiskProfile.keyName(asset, scenario));
			if (riskProfile == null)
				riskProfiles.put(RiskProfile.keyName(asset, scenario), AssessmentAndRiskProfileManager
						.GenerateRiskProfile(analysis.getRiskProfiles(), asset, scenario));
		}
		return assessment;
	}

	/**
	 * Imports scenarios from a workbook into the analysis.
	 *
	 * @param analysis  The analysis to import the scenarios into.
	 * @param workbook  The workbook containing the scenarios.
	 * @param sheets    A map of sheet names to sheet objects.
	 * @param formatter The data formatter to use for formatting cell values.
	 * @param min       The minimum progress value for the progress handler.
	 * @param max       The maximum progress value for the progress handler.
	 * @throws Exception If an error occurs during the import process.
	 */
	private void importScenario(Analysis analysis, WorkbookPart workbook, Map<String, Sheet> sheets,
			DataFormatter formatter, int min, int max) throws Exception {
		final Sheet sheet = sheets.get("Scenarios");
		if (sheet == null)
			return;
		SheetData sheetData = findSheet(workbook, sheet);
		if (sheetData == null)
			return;
		TablePart table = findTable(sheetData, "Scenarios");
		if (table == null)
			throw new TrickException("error.import.data.table.not.found", "Table named `Scenarios` cannot be found!",
					"Scenarios");
		final MessageHandler handler = new MessageHandler("info.updating.scenario", null, "Updating scenarios", min);
		getServiceTaskFeedback().send(getId(), handler);
		final AddressRef address = AddressRef.parse(table.getContents().getRef());
		final int size = (int) Math.min(address.getEnd().getRow() + 1, sheetData.getRow().size()),
				maxProgress = max - min;
		if (size < 2 || table.getContents().getTableColumns().getTableColumn().size() < 2)
			throw new TrickException("error.import.data.table.no.data", "Table named `Scenario` has not enough data!",
					"Scenario");
		final List<String> columns = table.getContents().getTableColumns().getTableColumn().stream()
				.map(c -> c.getName().toLowerCase()).collect(Collectors.toList());
		final int nameIndex = columns.indexOf("name"), odlNameIndex = columns.indexOf("old name");
		if (nameIndex == -1)
			throw new TrickException("error.import.data.no.column", "Name column cannot be found!", "Name");

		final boolean isILR = Analysis.isILR(analysis);

		final Map<String, String> names = loadTypeNames(workbook, sheets, formatter, "ScenarioTypes");
		final Map<String, ScenarioType> scenarioTypes = new LinkedHashMap<>();
		final List<AssetType> assetTypes = daoAssetType.getAll();
		for (ScenarioType scenarioType : ScenarioType.values())
			scenarioTypes.put(scenarioType.getName(), scenarioType);

		final Map<String, Scenario> scenarios = analysis.getScenarios().stream()
				.collect(Collectors.toMap(e -> e.getName().trim(), Function.identity()));
		for (int i = 1; i < size; i++) {
			Row row = sheetData.getRow().get(i);
			String name = getString(row, nameIndex, formatter), oldName = getString(row, odlNameIndex, formatter);
			if (isEmpty(name))
				continue;
			Scenario scenario = scenarios.get(name.trim());

			if (scenario == null) {
				if (!isEmpty(oldName))
					scenario = scenarios.get(oldName);
				if (scenario == null) {
					analysis.getScenarios().add(scenario = new Scenario(name));
					scenarios.put(name, scenario);
				} else
					scenario.setName(name);
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
								throw new TrickException("error.import.data.scenario.bad.type",
										String.format("Scenario type cannot be found for scenario `%s`!", name), name,
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
					case "threat":
						if (isILR)
							scenario.setThreat(getString(row, j, formatter));
						else
							handler.update("error.import.ilr.no_ilr_analysis",
									"Trying to import ILR data into No ILR Analysis", handler.getProgress());
						break;
					case "vulnerability":
						if (isILR)
							scenario.setVulnerability(getString(row, j, formatter));
						else
							handler.update("error.import.ilr.no_ilr_analysis",
									"Trying to import ILR data into No ILR Analysis", handler.getProgress());
						break;

					default:
						// ignore for now
				}
			}
			handler.setProgress((int) (min + ((double) i / (double) size) * maxProgress));
			getServiceTaskFeedback().send(getId(), handler);

		}

		if (isILR) {
			// check if Threat and Vulnerability are duplicated
			analysis.getScenarios().stream()
					.filter(e -> StringUtils.hasText(e.getVulnerability()) && StringUtils.hasText(e.getThreat()))
					.collect(Collectors.toMap(Scenario::getILRKey, Function.identity(), (e1, e2) -> {
						throw new TrickException("error.import.scenraio.threat.vulnerability.duplicate",
								String.format("Threat and vulnerability are duplicated in scenario: %s and %s",
										e1.getName(), e2.getName()),
								e1.getName(), e2.getName());
					}));
		}

		TrickLogManager.persist(LogLevel.INFO, LogType.ANALYSIS, "log.analysis.import.scenario",
				String.format("Analysis: %s, version: %s, type: Scenario", analysis.getIdentifier(),
						analysis.getVersion()),
				getUsername(), LogAction.IMPORT,
				analysis.getIdentifier(), analysis.getVersion());
	}

	/**
	 * Updates the asset type value for a given list of asset types and scenario.
	 * If the scenario is asset linked, the method returns without making any
	 * updates.
	 * Otherwise, it checks the existing asset type values in the scenario and adds
	 * any missing asset types with a value of 0.
	 *
	 * @param assetTypes the list of asset types to update
	 * @param scenario   the scenario to update the asset type values for
	 */
	private void updateAssetTypeValue(final List<AssetType> assetTypes, final Scenario scenario) {
		if (scenario.isAssetLinked())
			return;
		final Map<AssetType, AssetTypeValue> values = scenario.getAssetTypeValues().stream()
				.collect(Collectors.toMap(AssetTypeValue::getAssetType, Function.identity()));
		assetTypes.stream().filter(v -> !values.containsKey(v)).forEach(v -> scenario.add(new AssetTypeValue(v, 0)));
	}

	/**
	 * Initializes the Data Access Objects (DAOs) used by the WorkerImportEstimation
	 * class.
	 * 
	 * @param session the Hibernate session used for database operations
	 */
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
		daoScaleType = new DAOScaleTypeHBM(session);
	}

	/**
	 * Loads the impact of an assessment based on the given parameters.
	 *
	 * @param assessment     The assessment object to load the impact for.
	 * @param type           The scale type of the impact.
	 * @param value          The value of the impact.
	 * @param defaultValue   The default value for the impact.
	 * @param factory        The value factory used to create new values.
	 * @param valuesToDelete The list of values to delete.
	 * @return {@code true} if the impact was successfully loaded, {@code false}
	 *         otherwise.
	 */
	private boolean loadImpact(Assessment assessment, ScaleType type, String value, Number defaultValue,
			ValueFactory factory, List<IValue> valuesToDelete) {
		if (type == null)
			return false;
		IValue impact = assessment.getImpact(type.getName()),
				iValue = loadImpactValue(type, value, factory, defaultValue);
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

	/**
	 * Represents a value in the system.
	 * This interface provides methods to manipulate and retrieve values.
	 */
	private IValue loadImpactValue(ScaleType type, String value, ValueFactory factory, Number defaultValue) {
		if (defaultValue instanceof Double) {
			Double tempValue = ValueFactory.toDouble(value, null);
			if (tempValue != null)
				return factory.findValue(tempValue * 1000, type.getName());
		}
		return factory.findValue(parseValue(type, value), type.getName());
	}

	/**
	 * Loads measures into the given risk profile based on the provided value.
	 * 
	 * @param riskProfile    the risk profile to load measures into
	 * @param value          the value containing measure data
	 * @param measuresMapper a map of measure categories to their corresponding
	 *                       measures
	 * @return true if the loading was successful, false otherwise
	 */
	private boolean loadMeasures(RiskProfile riskProfile, String value,
			Map<String, Map<String, Measure>> measuresMapper) {
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
				if (measure == null || contains.containsKey(measure.getId())
						|| !measure.getMeasureDescription().isComputable() || measure instanceof MaturityMeasure)
					continue;
				contains.put(measure.getId(), riskProfile.getMeasures().add(measure));
			}
		}
		return false;
	}

	/**
	 * Loads the type names from the specified worksheet in the given workbook.
	 * 
	 * @param workbook  the workbook containing the worksheet
	 * @param sheets    a map of sheet names to sheet objects
	 * @param formatter the data formatter used to format cell values
	 * @param value     the name of the worksheet to load type names from
	 * @return a map of display names to type names
	 * @throws Exception if the worksheet or table cannot be found, or if there is
	 *                   not enough data in the table
	 */
	private Map<String, String> loadTypeNames(WorkbookPart workbook, Map<String, Sheet> sheets, DataFormatter formatter,
			String value) throws Exception {
		final Sheet sheet = sheets.get(value);
		if (sheet == null)
			throw new TrickException("error.import.data.sheet.not.found",
					"Worksheet named `" + value + "` cannot be found!", value);
		SheetData sheetData = findSheet(workbook, sheet);
		if (sheetData == null)
			throw new TrickException("error.import.data.sheet.no.data", "No data for worksheet named `" + value + "`",
					value);
		TablePart table = findTable(sheetData, value);
		if (table == null)
			throw new TrickException("error.import.data.table.not.found",
					"Table named `" + value + "` cannot be found!", value);
		final AddressRef address = AddressRef.parse(table.getContents().getRef());
		final int size = (int) Math.min(address.getEnd().getRow() + 1, sheetData.getRow().size());
		if (size < 2 || table.getContents().getTableColumns().getTableColumn().size() < 2)
			throw new TrickException("error.import.data.table.no.data",
					"Table named `" + value + "` has not enough data!", value);
		final List<String> columns = table.getContents().getTableColumns().getTableColumn().stream()
				.map(c -> c.getName().toLowerCase()).collect(Collectors.toList());
		final int nameIndex = columns.indexOf("name"), displayNameIndex = columns.indexOf("display name");
		if (nameIndex == -1)
			throw new TrickException("error.import.data.no.column", "Name column cannot be found!", "Name");
		if (displayNameIndex == -1)
			throw new TrickException("error.import.data.no.column", "Display name column cannot be found!",
					"Display name");
		Map<String, String> names = new LinkedHashMap<>(size - 1);
		for (int i = 1; i < size; i++) {
			Row row = sheetData.getRow().get(i);
			String name = getString(row, nameIndex, formatter),
					displayName = getString(row, displayNameIndex, formatter);
			if (isEmpty(name) || isEmpty(displayName))
				continue;
			names.put(displayName, name);
		}
		return names;
	}

	/**
	 * Represents a strategy for handling risk in a certain context.
	 */
	private RiskStrategy parseResponse(String value, RiskStrategy defaultValue) {
		try {
			return value == null || value.length() == 0 ? defaultValue
					: RiskStrategy.valueOf(value.trim().toUpperCase());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Parses the given value based on the specified scale type.
	 *
	 * @param type  the scale type
	 * @param value the value to parse
	 * @return the parsed value as a string, or null if the input value is null
	 */
	private String parseValue(ScaleType type, String value) {
		if (value == null)
			return null;
		Matcher matcher = impactPattern.matcher(value);
		return matcher.matches() ? value.replace("i", type.getAcronym()) : value;
	}

	/**
	 * Performs the processing of the data for risk estimation.
	 * This method retrieves the user and analysis objects from the database,
	 * loads the spreadsheet file, and performs the necessary imports and risk
	 * estimations
	 * based on the analysis settings and parameters.
	 * Finally, it updates the assessment and saves the analysis.
	 *
	 * @throws Exception if there is an error during the processing.
	 */
	private void processing() throws Exception {
		final User user = daoUser.get(username);
		final Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		if (user == null)
			throw new TrickException("error.user.not_found", "User cannot be found");
		final DataFormatter formatter = new DataFormatter();
		final boolean isILR = Analysis.isILR(analysis);
		final SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(getServiceStorage().loadAsFile(getFilename()));
		final WorkbookPart workbook = mlPackage.getWorkbookPart();
		final Map<String, Sheet> sheets = workbook.getContents().getSheets().getSheet().parallelStream()
				.collect(Collectors.toMap(Sheet::getName, Function.identity()));
		final ValueFactory factory = new ValueFactory(analysis.getParameters());
		final AssessmentAndRiskProfileManager riskProfileManager = new AssessmentAndRiskProfileManager().initialise(
				daoAnalysis, daoAsset, daoAssessment, daoRiskProfile,
				daoScenario);
		if (isAssetOnly())
			importAsset(analysis, workbook, sheets, formatter, 6, 90);
		else if (isScenarioOnly())
			importScenario(analysis, workbook, sheets, formatter, 6, 90);
		else if (isAssetDependancyOnly()) {
			importAssetDependancy(analysis, workbook, sheets, formatter, 6, 90);
		} else if (isILR) {
			importAssetDependancy(analysis, workbook, sheets, formatter, 6, 60);
			importRiskEstimation(analysis, factory, riskProfileManager, workbook, sheets, formatter, 60, 90);
			DependencyGraphManager.computeImpact(analysis.getAssetNodes());
		} else {
			importRiskEstimation(analysis, factory, riskProfileManager, workbook, sheets, formatter, 6, 90);
		}
		getServiceTaskFeedback().send(getId(),
				new MessageHandler("info.compute.risk.esitmation", null, "Computing risk estimation", 91));
		riskProfileManager.updateAssessment(analysis, factory);
		getServiceTaskFeedback().send(getId(),
				new MessageHandler("info.saving.analysis", null, "Saving risk analysis", 93));
		daoAnalysis.saveOrUpdate(analysis);
		getServiceTaskFeedback().send(getId(), new MessageHandler("info.commit.transcation", "Commit transaction", 96));
	}

	/**
	 * Checks if the asset has dependency only.
	 * 
	 * @return true if the asset has dependency only, false otherwise.
	 */
	private boolean isAssetDependancyOnly() {
		return false;
	}

	/**
	 * Generates a list of columns based on the provided parameters.
	 *
	 * @param scales        the list of scale types
	 * @param qualitative   a boolean indicating whether the columns should include
	 *                      qualitative information
	 * @param hiddenComment a boolean indicating whether the columns should include
	 *                      a hidden comment
	 * @param rowColumn     a boolean indicating whether the columns should include
	 *                      a row column
	 * @param uncertainty   a boolean indicating whether the columns should include
	 *                      uncertainty information
	 * @return the list of generated columns
	 */
	public final static List<Column> generateColumns(List<ScaleType> scales, boolean qualitative, boolean isILR,
			boolean hiddenComment,
			boolean rowColumn, boolean uncertainty) {
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
			columns.add(new Column(PROBABILITY));

			if (isILR)
				columns.add(new Column(VULNERABILITY));

			for (ScaleType type : scales)
				columns.add(new Column(type.getDisplayName()));

			columns.add(new Column("EXP Probability"));

			if (isILR)
				columns.add(new Column("EXP Vulnerability"));

			for (ScaleType type : scales) {
				if (!type.getName().equals(Constant.DEFAULT_IMPACT_NAME))
					columns.add(new Column("EXP " + type.getDisplayName()));
			}
		} else {
			columns.add(new Column(PROBABILITY));
			columns.add(new Column("Impact"));
		}
		if (uncertainty)
			columns.add(new Column("Uncertainty"));
		if (isILR) {
			columns.add(new Column("Risk"));
			columns.add(new Column("Residual risk"));
		}
		columns.add(new Column("Owner"));
		columns.add(new Column("Comment"));
		if (hiddenComment)
			columns.add(new Column("Hidden comment"));
		columns.add(new Column("Cockpit"));
		if (qualitative) {
			columns.add(new Column("Security measures"));
			columns.add(new Column("Measures"));
			columns.add(new Column("Action plan"));
		}

		return columns;
	}

	/**
	 * Returns whether the worker is configured to import only assets.
	 *
	 * @return true if the worker is configured to import only assets, false
	 *         otherwise.
	 */
	public boolean isAssetOnly() {
		return assetOnly;
	}

	/**
	 * Sets the flag indicating whether only assets should be imported.
	 *
	 * @param assetOnly true if only assets should be imported, false otherwise
	 */
	public void setAssetOnly(boolean assetOnly) {
		this.assetOnly = assetOnly;
	}

	/**
	 * Returns a boolean value indicating whether the worker is configured to import
	 * scenarios only.
	 *
	 * @return true if the worker is configured to import scenarios only, false
	 *         otherwise.
	 */
	public boolean isScenarioOnly() {
		return scenarioOnly;
	}

	/**
	 * Sets the flag indicating whether only the scenario should be imported.
	 *
	 * @param scenarioOnly true if only the scenario should be imported, false
	 *                     otherwise
	 */
	public void setScenarioOnly(boolean scenarioOnly) {
		this.scenarioOnly = scenarioOnly;
	}

	/**
	 * Returns the filename associated with this worker.
	 *
	 * @return the filename as a String
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Sets the filename for the import estimation worker.
	 *
	 * @param filename the name of the file to be imported
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

}

/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.findSheet;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.findTableNameStartWith;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getDouble;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getInt;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getString;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getWorksheetPart;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.isEmpty;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.TablePart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.hibernate.Session;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.STSheetState;
import org.xlsx4j.sml.Sheet;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.TS.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.AddressRef;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.messagehandler.TaskName;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.parameter.impl.SimpleParameter;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.MaturityStandard;
import lu.itrust.business.TS.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.impl.MaturityMeasure;

/**
 * @author eomar
 *
 */
public class WorkerImportMeasureData extends WorkerImpl {

	private String username;

	private String filename;

	private Integer idAnalysis;

	public WorkerImportMeasureData(String username, Integer idAnalysis, String filename) {
		setUsername(username);
		setFilename(filename);
		setIdAnalysis(idAnalysis);
		setName(TaskName.IMPORT_MEASURE_DATA);
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
		setWorking(false);
		getServiceStorage().delete(getFilename());
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
				setCurrent(Thread.currentThread());
			}
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.initialise.data", null, "Initialising risk analysis data", 5));
			session = getSessionFactory().openSession();
			session.beginTransaction();
			DAOAnalysis daoAnalysis = new DAOAnalysisHBM(session);
			Analysis analysis = daoAnalysis.get(idAnalysis);
			loadMeasures(analysis);
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.saving.analysis", null, "Saving risk analysis", 90));
			daoAnalysis.saveOrUpdate(analysis);
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.commit.transcation", "Commit transaction", 95));
			session.getTransaction().commit();

			MessageHandler handler = new MessageHandler("success.import.measure.data", "Measures data has been successfully imported", 100);

			handler.setAsyncCallbacks(new AsyncCallback("reload"));

			getServiceTaskFeedback().send(getId(), handler);

			TrickLogManager.Persist(LogLevel.INFO, LogType.ANALYSIS, "log.import.measure.data",
					String.format("Analysis: %s, version: %s, Type: Measure data", analysis.getIdentifier(), analysis.getVersion()), username, LogAction.IMPORT,
					analysis.getIdentifier(), analysis.getVersion());

		} catch (Exception e) {
			if (session != null) {
				try {
					if (session.getTransaction().getStatus().canRollback())
						session.getTransaction().rollback();
				} catch (Exception e1) {
					TrickLogManager.Persist(e1);
				}
			}
			if (e instanceof TrickException) {
				getServiceTaskFeedback().send(getId(), new MessageHandler(((TrickException) e).getCode(), ((TrickException) e).getParameters(), e.getMessage(), e));
			} else {
				TrickLogManager.Persist(e);
				getServiceTaskFeedback().send(getId(), new MessageHandler("error.500.message", "Internal error occurred", e));
			}
		} finally {
			try {
				if (session != null && session.isOpen())
					session.close();
			} catch (Exception e) {
				TrickLogManager.Persist(e);
			}
			cleanUp();
		}

	}

	private void loadMeasures(Analysis analysis) throws Exception {
		final SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(getServiceStorage().loadAsFile(getFilename()));
		final WorkbookPart workbook = mlPackage.getWorkbookPart();
		final DataFormatter formatter = new DataFormatter();
		final Map<String, Sheet> sheets = workbook.getContents().getSheets().getSheet().parallelStream().filter(s -> s.getState() == STSheetState.VISIBLE)
				.collect(Collectors.toMap(Sheet::getName, Function.identity()));
		final Map<Integer, Phase> phases = analysis.getPhases().stream().collect(Collectors.toMap(Phase::getNumber, Function.identity()));
		final int maxProgress = 90 / Math.max(analysis.getAnalysisStandards().size(), 1);
		int index = 1, minProgress = 6;
		for (AnalysisStandard analysisStandard : analysis.getAnalysisStandards().values())
			minProgress = loadData(analysis, workbook, formatter, sheets, phases, analysisStandard, minProgress, maxProgress * index++);
	}

	private int loadData(Analysis analysis, final WorkbookPart workbook, final DataFormatter formatter, final Map<String, Sheet> sheets, final Map<Integer, Phase> phases,
			AnalysisStandard analysisStandard, final int minProgress, final int maxProgress) throws Exception, Docx4JException {
		final Sheet sheet = sheets.get(analysisStandard.getStandard().getName());
		if (sheet == null)
			return minProgress;
		final SheetData sheetData = findSheet(workbook, sheet);
		if (sheetData == null)
			return minProgress;
		final TablePart table = findTableNameStartWith(getWorksheetPart(sheetData), "Measures");
		if (table == null)
			throw new TrickException("error.import.data.table.not.found", "Table named `Measures` cannot be found!", "Measures");

		final AddressRef address = AddressRef.parse(table.getContents().getRef());

		int size = (int) Math.min(address.getEnd().getRow() + 1, sheetData.getRow().size());

		if (size < 2 || table.getContents().getTableColumns().getTableColumn().size() < 2)
			return minProgress;
		final List<SimpleParameter> parameters = findMaturityImplementationRates(analysis, analysisStandard);
		final List<String> columns = table.getContents().getTableColumns().getTableColumn().stream().map(c -> c.getName().toLowerCase()).collect(Collectors.toList());
		final Map<String, Measure> measures = analysisStandard.getMeasures().stream().collect(Collectors.toMap(m -> m.getMeasureDescription().getReference(), Function.identity()));
		final Map<String, String> columnsMapper = new LinkedHashMap<>(Constant.NORMAL_MEASURE_COLUMNS.length);
		for (String column : Constant.NORMAL_MEASURE_COLUMNS)
			columnsMapper.put(column.toLowerCase(), column);
		final int refIndex = columns.indexOf("reference");
		if (refIndex == -1)
			throw new TrickException("error.import.measure.data.no.reference", "Reference column cannot be found!");
		final MessageHandler handler = new MessageHandler("info.updating.measure", null, "Update security measures", minProgress);
		getServiceTaskFeedback().send(getId(), handler);
		for (int i = 1; i < size; i++) {
			final Row row = sheetData.getRow().get(i);
			final String reference = getString(row, refIndex, formatter);
			if (isEmpty(reference))
				continue;
			final Measure measure = measures.get(reference);
			if (measure == null)
				continue;
			boolean updateCost = false;
			for (int j = 0; j < columns.size(); j++) {
				final String name = columnsMapper.get(columns.get(j));
				if (name == null || j == refIndex)
					continue;
				switch (name) {
				case "Status":
					measure.setStatus(getString(row, j, formatter));
					break;
				case "Phase":
					measure.setPhase(phases.getOrDefault(getInt(row, j, formatter), measure.getPhase()));
					break;
				case "Responsible":
					measure.setResponsible(getString(row, j, formatter));
					break;
				case "To check":
					if (measure instanceof AbstractNormalMeasure)
						((AbstractNormalMeasure) measure).setToCheck(getString(row, j, formatter));
					break;
				case "Comment":
					measure.setComment(getString(row, j, formatter));
					break;
				case "To do":
					measure.setToDo(getString(row, j, formatter));
					break;
				default:
					boolean tmpUpdateCost = true;
					switch (name) {
					case "Implemention":
						double value = getDouble(row, j, formatter) * 100;
						if (value > 100)
							value = 100;
						else if (value < 0)
							value = 0;
						if (measure instanceof MaturityMeasure)
							measure.setImplementationRate(findParameter(value, parameters));
						else
							measure.setImplementationRate(value);
						break;
					case "Internal Workload":
						measure.setInternalWL(getDouble(row, j, formatter));
						break;
					case "External Workload":
						measure.setExternalWL(getDouble(row, j, formatter));
						break;
					case "Investment":
						measure.setInvestment(getDouble(row, j, formatter) * 1000);
						break;
					case "Life time":
						measure.setLifetime(getDouble(row, j, formatter));
						break;
					case "Internal Maintenance":
						measure.setInternalMaintenance(getDouble(row, j, formatter));
						break;
					case "External Maintenance":
						measure.setExternalMaintenance(getDouble(row, j, formatter));
						break;
					case "Recurrent Maintenance":
						measure.setRecurrentInvestment(getDouble(row, j, formatter) * 1000);
						break;
					default:
						tmpUpdateCost = false;
						break;
					}
					updateCost |= tmpUpdateCost;
				}

				if (updateCost)
					Measure.ComputeCost(measure, analysis);

				handler.setProgress((int) (minProgress + (i / (double) size) * maxProgress));
				getServiceTaskFeedback().send(getId(), handler);
			}

		}
		return handler.getProgress();
	}

	private List<SimpleParameter> findMaturityImplementationRates(Analysis analysis, AnalysisStandard analysisStandard) {
		if (analysisStandard instanceof MaturityStandard)
			return analysis.getSimpleParameters().stream().filter(p -> p.isMatch(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME))
					.sorted((p0, p1) -> Double.compare(p0.getValue(), p1.getValue())).collect(Collectors.toList());
		else
			return Collections.emptyList();
	}

	private SimpleParameter findParameter(double value, List<SimpleParameter> parameters) {
		int mid = parameters.size() / 2;
		double pValue = parameters.get(mid).getValue();
		if (Math.abs(pValue - value) < 0.1 || mid == 0)
			return parameters.get(mid);
		else if (pValue > value)
			return findParameter(value, parameters.subList(mid, parameters.size()));
		else
			return findParameter(value, parameters.subList(0, mid));
	}

	public Integer getIdAnalysis() {
		return idAnalysis;
	}

	public void setIdAnalysis(Integer idAnalysis) {
		this.idAnalysis = idAnalysis;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}

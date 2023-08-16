/**
 * 
 */
package lu.itrust.business.ts.asynchronousWorkers;

import static lu.itrust.business.ts.constants.Constant.REGEXP_VALID_RISKINFORMATION_EXPOSED;
import static lu.itrust.business.ts.constants.Constant.RI_SHEET_MAPPERS;
import static lu.itrust.business.ts.constants.Constant.RI_TYPE_RISK;
import static lu.itrust.business.ts.constants.Constant.RI_TYPE_RISK_TBA;
import static lu.itrust.business.ts.constants.Constant.RI_TYPE_RISK_TBS;
import static lu.itrust.business.ts.constants.Constant.RI_TYPE_THREAT;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.findSheet;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.findTable;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getString;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.numToColString;
import static lu.itrust.business.ts.helper.NaturalOrderComparator.compareTo;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.TablePart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.util.StringUtils;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.ts.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAORiskInformation;
import lu.itrust.business.ts.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.ts.database.dao.hbm.DAORiskInformationHBM;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.AddressRef;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.messagehandler.TaskName;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.riskinformation.RiskInformation;

/**
 * @author eomar
 *
 */
public class WorkerImportRiskInformation extends WorkerImpl {

	private int analysisId;

	private String username;

	private String filename;

	private boolean overwrite;

	private DAOAnalysis daoAnalysis;

	private DAORiskInformation daoRiskInformation;

	public WorkerImportRiskInformation(int analysisId, String username, String filename, boolean overwrite) {
		setName(TaskName.IMPORT_RISK_INFORMATION);
		setAnalysisId(analysisId);
		setUsername(username);
		setFilename(filename);
		setOverwrite(overwrite);
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
			TrickLogManager.Persist(e);
		} finally {
			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						setWorking(false);
						setFinished(new Timestamp(System.currentTimeMillis()));
					}
				}
			}
			getServiceStorage().delete(getFilename());

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Session session = null;
		Transaction transaction = null;
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

			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.risk.information.initialise", "Initialise data", 2));

			session = getSessionFactory().openSession();

			initialiseDAO(session);

			transaction = session.beginTransaction();

			final Analysis analysis = daoAnalysis.get(analysisId);

			importRiskInformation(analysis);

			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.commit.transcation", "Commit transaction", 95));

			transaction.commit();

			final MessageHandler messageHandler = new MessageHandler("success.import.risk.information",
					"Brainstorming was successfully imported", 100);
			messageHandler.setAsyncCallbacks(new AsyncCallback("reloadSection", "section_risk-information_risk"),
					new AsyncCallback("reloadSection", "section_risk-information_vul"),
					new AsyncCallback("reloadSection", "section_risk-information_threat"));

			getServiceTaskFeedback().send(getId(), messageHandler);
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogType.ANALYSIS, "log.import.risk.information",
					String.format("Brainstorming data has been overwritten, Analysis: %s, version: %s",
							analysis.getIdentifier(), analysis.getVersion()),
					username,
					LogAction.IMPORT, analysis.getIdentifier(), analysis.getVersion());
		} catch (TrickException e) {
			setError(e);
			getServiceTaskFeedback().send(getId(),
					new MessageHandler(e.getCode(), e.getParameters(), e.getMessage(), e));
			TrickLogManager.Persist(e);
			if (!(session == null || transaction == null) && transaction.getStatus().canRollback())
				session.getTransaction().rollback();
		} catch (Exception e) {
			setError(e);
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("error.import.risk.information", "Import of risk information failed!", e));
			TrickLogManager.Persist(e);
			if (transaction != null && transaction.getStatus().canRollback())
				transaction.rollback();
		} finally {
			try {
				if (session != null && !session.isOpen())
					session.close();
			} catch (HibernateException e) {
				TrickLogManager.Persist(e);
			}
			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						setWorking(false);
						setFinished(new Timestamp(System.currentTimeMillis()));
					}
				}
			}
			getServiceStorage().delete(getFilename());

		}

	}

	private void importRiskInformation(Analysis analysis) throws Exception {
		final Map<String, RiskInformation> riskInformations = analysis.getRiskInformations().stream()
				.collect(Collectors.toMap(RiskInformation::getKey, Function.identity()));
		final SpreadsheetMLPackage mlPackage = SpreadsheetMLPackage.load(getServiceStorage().loadAsFile(getFilename()));
		final WorkbookPart workbook = mlPackage.getWorkbookPart();
		final DataFormatter formatter = new DataFormatter();
		final Locale locale = new Locale(analysis.getLanguage().getAlpha2());
		final Pattern exposurePattern = Pattern.compile(REGEXP_VALID_RISKINFORMATION_EXPOSED);
		final int min = 2;
		final int max = 90;
		int multi = (max - min) / 3;
		int progress = min;
		int indexProgress = 1;

		for (Object[] mapper : RI_SHEET_MAPPERS) {

			final String category = mapper[0].toString().toLowerCase();

			final MessageHandler messageHandler = new MessageHandler("info.risk.information.process.sheet." + category,
					String.format("Processing of %s in progress", category),
					progress);

			getServiceTaskFeedback().send(getId(), messageHandler);

			final SheetData sheet = findSheet(workbook, mapper[1].toString());
			if (sheet == null)
				throw new TrickException("error.risk.information.sheet.not.found",
						String.format("Something wrong with file: Sheet `%s` cannot be found", mapper[1].toString()),
						mapper[1].toString());
			final TablePart tablePart = findTable(sheet, mapper[0] + "Table");
			if (tablePart == null)
				throw new TrickException("error.risk.information.table.not.found",
						String.format("Something wrong with sheet `%s` : Table `%s` cannot be found",
								mapper[1].toString(), mapper[0] + "Table"),
						mapper[1].toString(),
						mapper[0] + "Table");
			final AddressRef address = AddressRef.parse(tablePart.getContents().getRef());
			final int maxProgress = progress + (multi * indexProgress);
			final int minProgress = progress;
			final int size = Math.min(address.getEnd().getRow() + 1, sheet.getRow().size());
			final Map<String, Boolean> chapterIndexer = new HashMap<>(size);

			for (int i = address.getBegin().getRow() + 1; i < size; i++) {

				int colIndex = 0;

				final Row row = sheet.getRow().get(i);

				final String chapter = getString(row, colIndex++, formatter);

				if (!StringUtils.hasText(chapter))
					emptyCellError(mapper[1].toString(), i, colIndex);
				else if (chapterIndexer.containsKey(chapter))
					duplicateCellError(mapper[1].toString(), i, colIndex);
				else
					chapterIndexer.put(chapter, true);

				RiskInformation riskInformation = riskInformations
						.remove(RiskInformation.key(mapper[0].toString(), chapter));
				if (riskInformation == null)
					analysis.getRiskInformations().add(riskInformation = new RiskInformation(chapter));

				if (mapper[0].equals(RI_TYPE_RISK)) {
					if (compareTo(chapter, "7") < 0)
						riskInformation.setCategory(RI_TYPE_RISK_TBS);
					else
						riskInformation.setCategory(RI_TYPE_RISK_TBA);
				} else if (riskInformation.getId() < 1)
					riskInformation.setCategory(mapper[0].toString());

				final String name = getString(row, colIndex++, formatter);

				if (!StringUtils.hasText(name))
					emptyCellError(mapper[1].toString(), i, colIndex);

				final String cleanName = name.trim();

				if (!riskInformation.isCustom() && riskInformation.getId() > 0) {
					final String label = getOrignalLabel(locale, riskInformation);
					if ((label == null || label.trim().equals(cleanName))) {
						riskInformation.setLabel(cleanName);
						riskInformation.setCustom(true);
					}
				} else {
					riskInformation.setLabel(cleanName);
					riskInformation.setCustom(true);
				}

				if (mapper[0].equals(RI_TYPE_THREAT))
					riskInformation.setAcronym(getString(row, colIndex++, formatter));

				final String exposed = getString(row, colIndex++, formatter);
				if (!StringUtils.hasText(exposed) || exposurePattern.matcher(exposed.trim()).matches())
					riskInformation.setExposed(exposed);
				else
					errorInvalidValue(mapper[0].toString(), i, colIndex);

				riskInformation.setOwner(getString(row, colIndex++, formatter));

				riskInformation.setComment(getString(row, colIndex++, formatter));

				riskInformation.setHiddenComment(getString(row, colIndex, formatter));

				messageHandler.setProgress((int) (minProgress + ((i / (double) size) * maxProgress)));
			}

		}

		getServiceTaskFeedback().send(getId(), new MessageHandler("info.save.analysis", "Saving analysis", max));

		if (isOverwrite()) {
			analysis.getRiskInformations().removeAll(riskInformations.values());
			daoRiskInformation.delete(riskInformations.values());
		}

		riskInformations.clear();

		daoAnalysis.saveOrUpdate(analysis);
		getServiceTaskFeedback().send(getId(),
				new MessageHandler("info.delete.removed.entry", "Delete removed entries", max + 3));
	}

	private void duplicateCellError(String sheet, int i, int colIndex) {
		final String colValue = numToColString(colIndex - 1);
		final String rowValue = (i + 1) + "";
		throw new TrickException("error.import.risk.information.duplicate",
				String.format("An entry is duplicated in sheet: %s, row: %s, column: %s", sheet, rowValue, colValue),
				sheet, rowValue, colValue);

	}

	private void errorInvalidValue(String sheet, int i, int colIndex) {
		final String colValue = numToColString(colIndex - 1);
		final String rowValue = (i + 1) + "";
		throw new TrickException("error.import.risk.information.cell",
				String.format("Invalid value in sheet: %s, row: %s, column: %s", sheet, rowValue, colValue), sheet,
				rowValue,
				colValue);
	}

	private String getOrignalLabel(final Locale locale, RiskInformation information) {
		return getOrignalLabel(locale, information.getChapter(), information.getCategory(), information.getLabel());
	}

	private String getOrignalLabel(final Locale locale, String chapter, String category, String label) {
		switch (category) {
			case RI_TYPE_RISK_TBA:
				return getMessageSource().getMessage(
						String.format("label.risk_information.risk_tba.%s", chapter.replace(".", "_")), null, label,
						locale);
			case RI_TYPE_RISK_TBS:
				return getMessageSource().getMessage(
						String.format("label.risk_information.risk_tbs.%s", chapter.replace(".", "_")), null, label,
						locale);
			default:
				return getMessageSource().getMessage(
						String.format("label.risk_information.%s.%s", category.toLowerCase(),
								chapter.replace(".", "_")),
						null, label, locale);
		}
	}

	/**
	 * Throw Error.
	 * 
	 * @param sheet
	 * @param row
	 * @param col
	 * @throws TrickException
	 */
	private void emptyCellError(String sheet, int row, int col) {
		String colValue = numToColString(col - 1), rowValue = (row + 1) + "";
		throw new TrickException("error.import.risk.information.cell.empty",
				String.format("Cell cannot be empty. Sheet: %s, row: %s, column: %s", sheet, rowValue, colValue),
				sheet, rowValue, colValue);
	}

	private void initialiseDAO(Session session) {
		setDaoAnalysis(new DAOAnalysisHBM(session));
		setDaoRiskInformation(new DAORiskInformationHBM(session));
	}

	public DAOAnalysis getDaoAnalysis() {
		return daoAnalysis;
	}

	public void setDaoAnalysis(DAOAnalysis daoAnalysis) {
		this.daoAnalysis = daoAnalysis;
	}

	public DAORiskInformation getDaoRiskInformation() {
		return daoRiskInformation;
	}

	public void setDaoRiskInformation(DAORiskInformation daoRiskInformation) {
		this.daoRiskInformation = daoRiskInformation;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(int analysisId) {
		this.analysisId = analysisId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

}
